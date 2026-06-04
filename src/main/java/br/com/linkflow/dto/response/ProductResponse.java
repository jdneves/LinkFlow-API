package br.com.linkflow.dto.response;

import br.com.linkflow.entity.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public record ProductResponse(
    UUID id,
    String externalId,
    String platform,
    String name,
    String description,
    BigDecimal price,
    BigDecimal originalPrice,
    BigDecimal discountPct,
    BigDecimal commissionPct,
    BigDecimal estimatedCommission,
    String imageUrl,
    String productUrl,
    String category,
    Integer score,
    String trend,
    ScoreDetail scoreDetail
) {
    public record ScoreDetail(
        String commission,   // Baixa / Média / Alta
        String competition,  // Baixa / Média / Alta
        String demand        // Estável / Crescente / Alta
    ) {}

    public static ProductResponse from(Product p) {
        BigDecimal discount = BigDecimal.ZERO;
        if (p.getOriginalPrice() != null && p.getOriginalPrice().compareTo(BigDecimal.ZERO) > 0
                && p.getPrice() != null) {
            discount = p.getOriginalPrice().subtract(p.getPrice())
                .divide(p.getOriginalPrice(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(1, RoundingMode.HALF_UP);
        }

        BigDecimal commission = BigDecimal.ZERO;
        if (p.getPrice() != null && p.getCommissionPct() != null) {
            commission = p.getPrice()
                .multiply(p.getCommissionPct())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        return new ProductResponse(
            p.getId(),
            p.getExternalId(),
            p.getPlatform().name(),
            p.getName(),
            p.getDescription(),
            p.getPrice(),
            p.getOriginalPrice(),
            discount,
            p.getCommissionPct(),
            commission,
            p.getImageUrl(),
            p.getProductUrl(),
            p.getCategory(),
            p.getScore(),
            p.getTrend().name(),
            buildScoreDetail(p)
        );
    }

    private static ScoreDetail buildScoreDetail(Product p) {
        String commission = p.getCommissionPct() == null ? "Baixa"
            : p.getCommissionPct().compareTo(BigDecimal.valueOf(8)) >= 0 ? "Alta"
            : p.getCommissionPct().compareTo(BigDecimal.valueOf(4)) >= 0 ? "Média"
            : "Baixa";

        String demand = switch (p.getTrend()) {
            case RISING  -> "Crescente";
            case STABLE  -> "Estável";
            case FALLING -> "Caindo";
        };

        String competition = p.getScore() >= 75 ? "Baixa"
            : p.getScore() >= 45 ? "Média"
            : "Alta";

        return new ScoreDetail(commission, competition, demand);
    }
}
