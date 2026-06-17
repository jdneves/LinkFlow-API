package br.com.linkflow.provider;

import br.com.linkflow.entity.Product.Platform;
import br.com.linkflow.mock.ProductMockData;

import java.util.List;

/**
 * Provider de fallback que embrulha o catálogo {@link ProductMockData},
 * filtrando por plataforma.
 *
 * <p>Usado quando o provider real de uma plataforma está desabilitado ou
 * falha durante o sync. É instanciado por plataforma (não é um bean Spring),
 * de modo que {@link #platform()} sempre identifica a fonte mock servida.</p>
 */
public class MockProductProvider implements ProductProvider {

    private final Platform platform;

    public MockProductProvider(Platform platform) {
        this.platform = platform;
    }

    @Override
    public Platform platform() {
        return platform;
    }

    /** O mock está sempre disponível como fallback. */
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public List<RawProduct> fetchCatalog() {
        return ProductMockData.getProdutos().stream()
            .filter(p -> p.getPlatform() == platform)
            .map(RawProduct::fromEntity)
            .toList();
    }
}
