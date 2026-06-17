package br.com.linkflow.provider;

import br.com.linkflow.entity.Product.Platform;

import java.util.List;

/**
 * Abstração de uma fonte de produtos para o Radar (Mercado Livre, Shopee,
 * Amazon, mock, ...).
 *
 * <p>Cada provider sabe buscar o catálogo já normalizado em {@link RawProduct}.
 * A chamada é <b>síncrona</b> e usada apenas pelo scheduler de sync — o Radar
 * NUNCA chama um provider por request (serve sempre do banco/cache).</p>
 */
public interface ProductProvider {

    /** Plataforma que este provider atende. */
    Platform platform();

    /** Lê um flag de config; quando {@code false}, o sync cai no mock da plataforma. */
    boolean isEnabled();

    /**
     * Busca o catálogo de produtos da fonte, já normalizado.
     * Pode lançar exceção em falha de I/O — o sync trata com fallback para o mock.
     */
    List<RawProduct> fetchCatalog();
}