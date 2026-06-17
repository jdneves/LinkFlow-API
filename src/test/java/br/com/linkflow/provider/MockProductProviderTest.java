package br.com.linkflow.provider;

import br.com.linkflow.entity.Product.Platform;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MockProductProviderTest {

    @Test
    @DisplayName("Deve retornar apenas produtos da plataforma Mercado Livre")
    void deveFiltrarPorMercadoLivre() {
        List<RawProduct> produtos = new MockProductProvider(Platform.MERCADO_LIVRE).fetchCatalog();

        assertThat(produtos).isNotEmpty();
        assertThat(produtos).allMatch(p -> p.platform() == Platform.MERCADO_LIVRE);
        assertThat(produtos).allMatch(p -> p.externalId().startsWith("ML-"));
    }

    @Test
    @DisplayName("Deve retornar apenas produtos da plataforma Shopee")
    void deveFiltrarPorShopee() {
        List<RawProduct> produtos = new MockProductProvider(Platform.SHOPEE).fetchCatalog();

        assertThat(produtos).isNotEmpty();
        assertThat(produtos).allMatch(p -> p.platform() == Platform.SHOPEE);
    }

    @Test
    @DisplayName("Plataforma sem produtos mock retorna lista vazia")
    void deveRetornarVazioParaAmazon() {
        List<RawProduct> produtos = new MockProductProvider(Platform.AMAZON).fetchCatalog();

        assertThat(produtos).isEmpty();
    }

    @Test
    @DisplayName("Mock está sempre habilitado (fallback) e reporta sua plataforma")
    void deveSerHabilitadoEReportarPlataforma() {
        MockProductProvider provider = new MockProductProvider(Platform.SHOPEE);

        assertThat(provider.isEnabled()).isTrue();
        assertThat(provider.platform()).isEqualTo(Platform.SHOPEE);
    }

    @Test
    @DisplayName("RawProduct do mock não carrega score/trend (calculados pelo LinkFlow)")
    void rawProductNaoExpoeScore() {
        RawProduct produto = new MockProductProvider(Platform.MERCADO_LIVRE).fetchCatalog().get(0);

        // RawProduct preserva os dados da fonte (incl. comissão), mas não score/trend.
        assertThat(produto.commissionPct()).isNotNull();
        assertThat(produto.name()).isNotBlank();
    }
}
