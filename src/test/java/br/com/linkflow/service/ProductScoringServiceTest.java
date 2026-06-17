package br.com.linkflow.service;

import br.com.linkflow.entity.Product;
import br.com.linkflow.entity.Product.Platform;
import br.com.linkflow.entity.Product.Trend;
import br.com.linkflow.provider.RawProduct;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ProductScoringService - régua de scoring")
class ProductScoringServiceTest {

    private final ProductScoringService service = new ProductScoringService();

    @Test
    @DisplayName("Sem desconto nem comissão o produto recebe o piso de score (40) e tendência FALLING")
    void deveAplicarPisoSemDescontoNemComissao() {
        RawProduct raw = raw(null, null, null);

        int score = service.calcularScore(raw);

        assertThat(score).isEqualTo(40);
        assertThat(service.calcularTrend(score)).isEqualTo(Trend.FALLING);
    }

    @Test
    @DisplayName("Desconto e comissão máximos saturam no teto e limitam o score a 99")
    void deveSaturarScoreEm99() {
        // Desconto de 90% (acima do teto de 60%) e comissão de 20% (acima de 15%).
        RawProduct raw = raw(new BigDecimal("10"), new BigDecimal("100"), new BigDecimal("20"));

        int score = service.calcularScore(raw);

        assertThat(score).isEqualTo(99);
        assertThat(service.calcularTrend(score)).isEqualTo(Trend.RISING);
    }

    @Test
    @DisplayName("Score pondera desconto (55%) e comissão (45%) normalizados pelos tetos")
    void devePonderarDescontoEComissao() {
        // Desconto de 40% → 66,67 normalizado; comissão de 10% → 66,67 normalizado.
        // 0,55*66,67 + 0,45*66,67 = 66,67 → arredonda para 67.
        RawProduct raw = raw(new BigDecimal("60"), new BigDecimal("100"), new BigDecimal("10"));

        int score = service.calcularScore(raw);

        assertThat(score).isEqualTo(67);
        assertThat(service.calcularTrend(score)).isEqualTo(Trend.STABLE);
    }

    @Test
    @DisplayName("Comissão nula é tratada como zero, contando apenas o desconto")
    void deveTratarComissaoNulaComoZero() {
        // Desconto de 50% → 83,33 normalizado; comissão nula → 0.
        // 0,55*83,33 = 45,83 → arredonda para 46.
        RawProduct raw = raw(new BigDecimal("100"), new BigDecimal("200"), null);

        int score = service.calcularScore(raw);

        assertThat(score).isEqualTo(46);
        assertThat(service.calcularTrend(score)).isEqualTo(Trend.FALLING);
    }

    @Test
    @DisplayName("Preço maior que o original não gera desconto negativo (desconto = 0)")
    void deveIgnorarDescontoNegativo() {
        // price > originalPrice → desconto negativo deve ser tratado como 0.
        RawProduct raw = raw(new BigDecimal("150"), new BigDecimal("100"), null);

        assertThat(service.calcularScore(raw)).isEqualTo(40);
    }

    @Test
    @DisplayName("calcularTrend respeita os limiares 78 (RISING) e 55 (STABLE)")
    void deveDerivarTrendPorLimiar() {
        assertThat(service.calcularTrend(99)).isEqualTo(Trend.RISING);
        assertThat(service.calcularTrend(78)).isEqualTo(Trend.RISING);
        assertThat(service.calcularTrend(77)).isEqualTo(Trend.STABLE);
        assertThat(service.calcularTrend(55)).isEqualTo(Trend.STABLE);
        assertThat(service.calcularTrend(54)).isEqualTo(Trend.FALLING);
        assertThat(service.calcularTrend(40)).isEqualTo(Trend.FALLING);
    }

    @Test
    @DisplayName("toScoredProduct preserva os campos do RawProduct e deriva score e trend")
    void devePreservarCamposEDerivarScoreETrend() {
        RawProduct raw = new RawProduct(
            "MLB123",
            Platform.MERCADO_LIVRE,
            "Fone Bluetooth",
            "Fone sem fio com cancelamento de ruído",
            new BigDecimal("60"),
            new BigDecimal("100"),
            new BigDecimal("10"),
            "https://img/f.jpg",
            "https://ml/fone",
            "eletronicos"
        );

        Product product = service.toScoredProduct(raw);

        assertThat(product.getExternalId()).isEqualTo("MLB123");
        assertThat(product.getPlatform()).isEqualTo(Platform.MERCADO_LIVRE);
        assertThat(product.getName()).isEqualTo("Fone Bluetooth");
        assertThat(product.getDescription()).isEqualTo("Fone sem fio com cancelamento de ruído");
        assertThat(product.getPrice()).isEqualByComparingTo("60");
        assertThat(product.getOriginalPrice()).isEqualByComparingTo("100");
        assertThat(product.getCommissionPct()).isEqualByComparingTo("10");
        assertThat(product.getImageUrl()).isEqualTo("https://img/f.jpg");
        assertThat(product.getProductUrl()).isEqualTo("https://ml/fone");
        assertThat(product.getCategory()).isEqualTo("eletronicos");
        // Score/trend derivados pela régua (mesmo caso de devePonderarDescontoEComissao).
        assertThat(product.getScore()).isEqualTo(67);
        assertThat(product.getTrend()).isEqualTo(Trend.STABLE);
    }

    /** Cria um RawProduct mínimo variando preço, preço original e comissão. */
    private RawProduct raw(BigDecimal price, BigDecimal originalPrice, BigDecimal commissionPct) {
        return new RawProduct(
            "EXT1",
            Platform.MERCADO_LIVRE,
            "Produto",
            "Descrição",
            price,
            originalPrice,
            commissionPct,
            "https://img/p.jpg",
            "https://ml/p",
            "eletronicos"
        );
    }
}