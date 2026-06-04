package br.com.linkflow.service;

import br.com.linkflow.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProductServiceTest {

    @Autowired ProductService productService;
    @Autowired ProductRepository productRepository;

    @Test
    @DisplayName("Deve carregar produtos mock na inicialização")
    void deveCarregarProdutosMock() {
        productService.sincronizarProdutos();
        assertThat(productRepository.count()).isEqualTo(20);
    }

    @Test
    @DisplayName("Deve buscar produtos com filtro de categoria")
    void deveBuscarPorCategoria() {
        productService.sincronizarProdutos();
        var result = productService.buscar("eletronicos", null, null, 0);
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent()).allMatch(p -> p.category().equals("eletronicos"));
    }

    @Test
    @DisplayName("Deve buscar produtos em alta ordenados por score")
    void deveBuscarEmAlta() {
        productService.sincronizarProdutos();
        var trending = productService.buscarEmAlta();
        assertThat(trending).isNotEmpty();
        assertThat(trending).allMatch(p -> p.trend().equals("RISING"));
    }

    @Test
    @DisplayName("Deve calcular comissão estimada corretamente")
    void deveCalcularComissao() {
        productService.sincronizarProdutos();
        var produtos = productService.buscar(null, null, "Airfryer Philips", 0);
        assertThat(produtos.getContent()).isNotEmpty();
        var airfryer = produtos.getContent().get(0);
        // Preço 399.90 * 8.5% = 33.99
        assertThat(airfryer.estimatedCommission()).isEqualByComparingTo("33.99");
    }

    @Test
    @DisplayName("Deve retornar lista de categorias disponíveis")
    void deveListarCategorias() {
        var categorias = productService.listarCategorias();
        assertThat(categorias).contains("eletrodomesticos", "eletronicos", "beleza", "fitness");
    }
}
