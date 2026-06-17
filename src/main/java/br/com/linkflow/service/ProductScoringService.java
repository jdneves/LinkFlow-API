package br.com.linkflow.service;

import br.com.linkflow.entity.Product;
import br.com.linkflow.entity.Product.Trend;
import br.com.linkflow.provider.RawProduct;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Régua de scoring do LinkFlow, aplicada a TODAS as fontes (mock, Mercado
 * Livre, ...). Calcula {@code score} e {@code trend} a partir dos atributos
 * que um provider fornece (desconto e comissão) e converte o {@link RawProduct}
 * em entidade {@link Product} pronta para persistência.
 *
 * <p>As demais métricas derivadas — {@code scoreDetail} e
 * {@code estimatedCommission} — continuam sendo calculadas na borda, em
 * {@code ProductResponse.from(...)}, mantendo o contrato do Radar intacto.</p>
 */
@Service
public class ProductScoringService {

    /** Teto de desconto considerado para normalização (acima disso satura). */
    private static final BigDecimal MAX_DISCOUNT = BigDecimal.valueOf(60);
    /** Teto de comissão considerado para normalização. */
    private static final BigDecimal MAX_COMMISSION = BigDecimal.valueOf(15);

    private static final double WEIGHT_DISCOUNT = 0.55;
    private static final double WEIGHT_COMMISSION = 0.45;

    /** Converte um produto bruto em entidade pontuada do LinkFlow. */
    public Product toScoredProduct(RawProduct raw) {
        int score = calcularScore(raw);
        return Product.builder()
            .externalId(raw.externalId())
            .platform(raw.platform())
            .name(raw.name())
            .description(raw.description())
            .price(raw.price())
            .originalPrice(raw.originalPrice())
            .commissionPct(raw.commissionPct())
            .imageUrl(raw.imageUrl())
            .productUrl(raw.productUrl())
            .category(raw.category())
            .score(score)
            .trend(calcularTrend(score))
            .build();
    }

    /**
     * Score 0–99 ponderando desconto (55%) e comissão (45%), normalizados pelos
     * respectivos tetos. Sem desconto/comissão o produto ainda recebe um piso.
     */
    public int calcularScore(RawProduct raw) {
        double discountScore = normalizar(descontoPct(raw), MAX_DISCOUNT);
        double commissionScore = normalizar(raw.commissionPct(), MAX_COMMISSION);
        double score = WEIGHT_DISCOUNT * discountScore + WEIGHT_COMMISSION * commissionScore;
        return clamp((int) Math.round(score), 40, 99);
    }

    /** Tendência derivada do score: alta procura no topo, queda na base. */
    public Trend calcularTrend(int score) {
        if (score >= 78) return Trend.RISING;
        if (score >= 55) return Trend.STABLE;
        return Trend.FALLING;
    }

    private BigDecimal descontoPct(RawProduct raw) {
        BigDecimal original = raw.originalPrice();
        BigDecimal price = raw.price();
        if (original == null || price == null || original.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return original.subtract(price)
            .divide(original, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));
    }

    /** Normaliza um valor em 0–100 saturando no teto. {@code null} → 0. */
    private double normalizar(BigDecimal value, BigDecimal max) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) return 0.0;
        BigDecimal capped = value.min(max);
        return capped.divide(max, 4, RoundingMode.HALF_UP).doubleValue() * 100.0;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
