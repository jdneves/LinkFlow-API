package br.com.linkflow.service;

import br.com.linkflow.entity.Product;
import br.com.linkflow.entity.Product.Platform;
import br.com.linkflow.provider.ProductProvider;
import br.com.linkflow.provider.RawProduct;
import br.com.linkflow.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ProductSyncFallbackTest {

    private final ProductScoringService scoringService = new ProductScoringService();

    private ProductRepository repoQueAceitaTudo() {
        ProductRepository repo = mock(ProductRepository.class);
        when(repo.findByExternalIdAndPlatform(anyString(), any())).thenReturn(Optional.empty());
        when(repo.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));
        return repo;
    }

    private List<Product> capturarSalvos(ProductRepository repo) {
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(repo, atLeastOnce()).save(captor.capture());
        return captor.getAllValues();
    }

    @Test
    @DisplayName("Provider real habilitado → usa dados reais (não o mock)")
    void deveUsarProviderRealQuandoHabilitado() {
        ProductRepository repo = repoQueAceitaTudo();
        ProductProvider mlReal = new FakeProvider(Platform.MERCADO_LIVRE, true, List.of(
            new RawProduct("ML-REAL-1", Platform.MERCADO_LIVRE, "Produto Real", "desc",
                new BigDecimal("100.00"), new BigDecimal("200.00"),
                new BigDecimal("10.0"), "img", "url", "eletronicos")
        ));

        ProductService service = new ProductService(repo, scoringService, List.of(mlReal));
        service.sincronizarProdutos();

        List<Product> salvos = capturarSalvos(repo);
        // O produto real do ML foi persistido...
        assertThat(salvos).anyMatch(p -> "ML-REAL-1".equals(p.getExternalId()));
        // ...e nenhum produto mock do ML (ML-00x) foi usado para essa plataforma.
        assertThat(salvos)
            .filteredOn(p -> p.getPlatform() == Platform.MERCADO_LIVRE)
            .allMatch(p -> "ML-REAL-1".equals(p.getExternalId()));
    }

    @Test
    @DisplayName("Provider real desabilitado → fallback para o mock da plataforma")
    void deveCairNoMockQuandoDesabilitado() {
        ProductRepository repo = repoQueAceitaTudo();
        ProductProvider mlDesabilitado = new FakeProvider(Platform.MERCADO_LIVRE, false, List.of());

        ProductService service = new ProductService(repo, scoringService, List.of(mlDesabilitado));
        service.sincronizarProdutos();

        List<Product> salvos = capturarSalvos(repo);
        // Caiu no mock: produtos ML-00x do ProductMockData aparecem.
        assertThat(salvos).anyMatch(p -> p.getExternalId().startsWith("ML-0"));
    }

    @Test
    @DisplayName("Provider real que falha → fallback para o mock (sync não quebra)")
    void deveCairNoMockQuandoProviderFalha() {
        ProductRepository repo = repoQueAceitaTudo();
        ProductProvider mlQuebrado = new FakeProvider(Platform.MERCADO_LIVRE, true, null); // lança

        ProductService service = new ProductService(repo, scoringService, List.of(mlQuebrado));

        // Não deve lançar — cai no mock.
        service.sincronizarProdutos();

        List<Product> salvos = capturarSalvos(repo);
        assertThat(salvos).anyMatch(p -> p.getExternalId().startsWith("ML-0"));
    }

    @Test
    @DisplayName("Scoring é aplicado a todas as fontes (score e trend preenchidos)")
    void deveAplicarScoringEmTodasAsFontes() {
        ProductRepository repo = repoQueAceitaTudo();
        ProductService service = new ProductService(repo, scoringService, List.of());

        service.sincronizarProdutos();

        List<Product> salvos = capturarSalvos(repo);
        assertThat(salvos).isNotEmpty();
        assertThat(salvos).allMatch(p -> p.getScore() != null && p.getScore() >= 40 && p.getScore() <= 99);
        assertThat(salvos).allMatch(p -> p.getTrend() != null);
    }

    /** Provider de teste: lista nula faz fetchCatalog lançar (simula falha de I/O). */
    private record FakeProvider(Platform platform, boolean enabled, List<RawProduct> catalog)
        implements ProductProvider {

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public List<RawProduct> fetchCatalog() {
            if (catalog == null) throw new RuntimeException("falha simulada");
            return catalog;
        }
    }
}
