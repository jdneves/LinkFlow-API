package br.com.linkflow.service;

import br.com.linkflow.dto.response.ProductResponse;
import br.com.linkflow.dto.response.RestPage;
import br.com.linkflow.entity.Product;
import br.com.linkflow.entity.Product.Platform;
import br.com.linkflow.mock.ProductMockData;
import br.com.linkflow.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // ── Busca com filtros ──────────────────────────────────────────────────

    @Cacheable(value = "products", key = "#category + '_' + #platform + '_' + #search + '_' + #page")
    public Page<ProductResponse> buscar(String category, String platform, String search, int page) {
        Pageable pageable = PageRequest.of(page, 12);
        Platform plat = platform != null ? Platform.valueOf(platform.toUpperCase()) : null;
        String searchPattern = search != null ? "%" + search.toLowerCase() + "%" : null;
        return new RestPage<>(productRepository.findWithFilters(category, plat, searchPattern, pageable)
            .map(ProductResponse::from));
    }

    // ── Produtos em alta ───────────────────────────────────────────────────

    @Cacheable(value = "trending-products")
    public List<ProductResponse> buscarEmAlta() {
        return productRepository.findTrending(PageRequest.of(0, 6))
            .map(ProductResponse::from)
            .toList();
    }

    // ── Busca produto por ID ───────────────────────────────────────────────

    public ProductResponse buscarPorId(String id) {
        return productRepository.findById(java.util.UUID.fromString(id))
            .map(ProductResponse::from)
            .orElseThrow(() -> new br.com.linkflow.exception.BusinessException("Produto não encontrado."));
    }

    // ── Categorias disponíveis ─────────────────────────────────────────────

    public List<String> listarCategorias() {
        return List.of(
            "eletrodomesticos",
            "eletronicos",
            "beleza",
            "fitness",
            "casa",
            "games",
            "moda"
        );
    }

    // ── Sincronização com mock (substitua pela API real depois) ───────────

    @Scheduled(cron = "0 0 */6 * * *") // a cada 6 horas
    @Transactional
    @CacheEvict(value = {"products", "trending-products"}, allEntries = true)
    public void sincronizarProdutos() {
        log.info("Sincronizando produtos do radar...");
        List<Product> produtos = ProductMockData.getProdutos();

        for (Product produto : produtos) {
            productRepository.findByExternalIdAndPlatform(produto.getExternalId(), produto.getPlatform())
                .ifPresentOrElse(
                    existente -> {
                        // Atualiza dados do produto existente
                        existente.setPrice(produto.getPrice());
                        existente.setOriginalPrice(produto.getOriginalPrice());
                        existente.setCommissionPct(produto.getCommissionPct());
                        existente.setScore(produto.getScore());
                        existente.setTrend(produto.getTrend());
                        productRepository.save(existente);
                    },
                    () -> productRepository.save(produto)
                );
        }

        log.info("Sincronização concluída: {} produtos processados.", produtos.size());
    }

    // Roda na inicialização da aplicação (1 vez, 5s após startup)
    @Scheduled(initialDelay = 5000, fixedDelay = Long.MAX_VALUE)
    public void sincronizarNaInicializacao() {
        if (productRepository.count() == 0) {
            log.info("Banco vazio — carregando produtos mock iniciais...");
            sincronizarProdutos();
        }
    }
}
