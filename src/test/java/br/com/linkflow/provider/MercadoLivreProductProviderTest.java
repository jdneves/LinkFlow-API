package br.com.linkflow.provider;

import br.com.linkflow.client.MercadoLivreClient;
import br.com.linkflow.config.MercadoLivreProperties;
import br.com.linkflow.entity.Product.Platform;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MercadoLivreProductProviderTest {

    private MockWebServer server;
    private MercadoLivreProductProvider provider;
    private MercadoLivreProperties props;

    @BeforeEach
    void setUp() throws Exception {
        server = new MockWebServer();
        server.start();

        props = new MercadoLivreProperties();
        props.setEnabled(true);
        props.setClientId("c");
        props.setClientSecret("s");
        props.setRefreshToken("r");
        props.setSiteId("MLB");
        props.setLimitPerCategory(10);
        props.setThrottleMs(0); // sem pausa nos testes
        props.setApiBaseUrl(server.url("/").toString().replaceAll("/$", ""));
        props.getCommissionByCategory().put("eletronicos", new BigDecimal("5.0"));
        props.setAffiliateTag("AFF123");

        MercadoLivreClient client = new MercadoLivreClient(props, new ObjectMapper(),
            new br.com.linkflow.client.InMemoryMercadoLivreTokenStore());
        provider = new MercadoLivreProductProvider(client, props, new MercadoLivreCategoryMapper());
    }

    @AfterEach
    void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    @DisplayName("isEnabled reflete o flag de config e a plataforma é MERCADO_LIVRE")
    void deveReportarEstado() {
        assertThat(provider.platform()).isEqualTo(Platform.MERCADO_LIVRE);
        assertThat(provider.isEnabled()).isTrue();
        props.setEnabled(false);
        assertThat(provider.isEnabled()).isFalse();
    }

    @Test
    @DisplayName("Deve mapear item do ML para RawProduct com categoria e comissão por config")
    void deveMapearItemParaRawProduct() {
        // Dispatcher: token + busca por categoria (só eletronicos traz item).
        enqueuePorCategoria();

        List<RawProduct> catalog = provider.fetchCatalog();

        assertThat(catalog).hasSize(1);
        RawProduct p = catalog.get(0);
        assertThat(p.platform()).isEqualTo(Platform.MERCADO_LIVRE);
        assertThat(p.externalId()).isEqualTo("MLB999");
        assertThat(p.name()).isEqualTo("Fone Bluetooth");
        assertThat(p.category()).isEqualTo("eletronicos");
        assertThat(p.price()).isEqualByComparingTo("199.90");
        assertThat(p.originalPrice()).isEqualByComparingTo("299.90");
        // Comissão derivada do mapa de config (a busca não retorna comissão).
        assertThat(p.commissionPct()).isEqualByComparingTo("5.0");
        // Link com a tag de afiliado.
        assertThat(p.productUrl()).contains("matt_tool=AFF123");
        // RawProduct não carrega score/trend.
    }

    @Test
    @DisplayName("Instrumenta a coleta: loga contagem por categoria e avisa categorias sem catálogo")
    void deveInstrumentarContagemPorCategoria() {
        enqueuePorCategoria();

        Logger logger = (Logger) LoggerFactory.getLogger(MercadoLivreProductProvider.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);

        provider.fetchCatalog();

        logger.detachAppender(appender);
        List<ILoggingEvent> logs = appender.list;

        // Resumo com a contagem por categoria (só eletronicos trouxe 1 item).
        assertThat(logs).anyMatch(e -> e.getFormattedMessage().contains("Itens por categoria")
            && e.getFormattedMessage().contains("eletronicos=1"));
        // Categoria sem catálogo gera WARN (ex.: beleza ficou vazia neste cenário).
        assertThat(logs).anyMatch(e -> e.getLevel() == Level.WARN
            && e.getFormattedMessage().contains("beleza")
            && e.getFormattedMessage().contains("não retornou catálogo"));
    }

    /**
     * Dispatcher dos endpoints reais: OAuth, destaques por categoria e detalhe de
     * produto/ofertas. Só a categoria "eletronicos" (MLB1000) traz um destaque
     * (produto MLB999); as demais categorias vêm sem conteúdo.
     */
    private void enqueuePorCategoria() {
        server.setDispatcher(new okhttp3.mockwebserver.Dispatcher() {
            @Override
            public MockResponse dispatch(okhttp3.mockwebserver.RecordedRequest request) {
                String path = request.getPath() == null ? "" : request.getPath();
                if (path.startsWith("/oauth/token")) {
                    return json("{\"access_token\":\"TK\",\"expires_in\":21600}");
                }
                if (path.equals("/highlights/MLB/category/MLB1000")) {
                    return json("{\"content\":[{\"id\":\"MLB999\",\"position\":1,\"type\":\"PRODUCT\"}]}");
                }
                if (path.startsWith("/highlights/")) {
                    return json("{\"content\":[]}");
                }
                if (path.equals("/products/MLB999")) {
                    return json("""
                        {"name":"Fone Bluetooth","permalink":"https://ml/fone",
                         "pictures":[{"url":"https://img/f.jpg"}]}
                        """);
                }
                if (path.startsWith("/products/MLB999/items")) {
                    return json("""
                        {"results":[{"price":199.90,"original_price":299.90,"category_id":"MLB1055"}]}
                        """);
                }
                return json("{\"content\":[]}");
            }
        });
    }

    private static MockResponse json(String body) {
        return new MockResponse().setHeader("Content-Type", "application/json").setBody(body);
    }
}
