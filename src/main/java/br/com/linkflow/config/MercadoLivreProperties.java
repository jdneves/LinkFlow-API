package br.com.linkflow.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Configuração da integração com o Mercado Livre (env vars — só backend).
 *
 * <pre>
 * ml.enabled=true
 * ml.client-id=...
 * ml.client-secret=...
 * ml.refresh-token=...
 * ml.site-id=MLB
 * ml.affiliate-tag=...
 * ml.commission-by-category.eletronicos=6.0
 * </pre>
 */
@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "ml")
public class MercadoLivreProperties {

    /** Liga/desliga o provider real do Mercado Livre. */
    private boolean enabled = false;

    /** Credenciais OAuth 2.0 da aplicação no Mercado Livre. */
    private String clientId;
    private String clientSecret;

    /** Refresh token de longa duração usado para renovar o access token. */
    private String refreshToken;

    /** Site do Mercado Livre (Brasil = MLB). */
    private String siteId = "MLB";

    /** Tag de afiliado adicionada aos links de produto. */
    private String affiliateTag;

    /** Itens buscados por categoria a cada ciclo de sync. */
    private int limitPerCategory = 20;

    /** Pausa (ms) entre páginas/categorias para respeitar o rate limit do ML. */
    private long throttleMs = 300;

    /**
     * Base URL da API do Mercado Livre. Configurável para testes
     * (MockWebServer). Em produção usa o endpoint oficial.
     */
    private String apiBaseUrl = "https://api.mercadolibre.com";

    /**
     * Comissão (percentual) por categoria do LinkFlow, usada quando a API não
     * retorna a comissão real do produto.
     */
    private Map<String, BigDecimal> commissionByCategory = new LinkedHashMap<>();

    /**
     * Comissão (percentual) padrão usada quando a categoria não está em
     * {@link #commissionByCategory}. Evita que produtos de categorias não
     * mapeadas fiquem com comissão {@code null} → score zerado no peso da
     * comissão (45%).
     */
    private BigDecimal defaultCommissionPct = new BigDecimal("5.0");
}