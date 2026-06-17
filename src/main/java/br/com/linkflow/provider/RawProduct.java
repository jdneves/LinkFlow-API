package br.com.linkflow.provider;

import br.com.linkflow.entity.Product;
import br.com.linkflow.entity.Product.Platform;

import java.math.BigDecimal;

/**
 * Produto bruto, já normalizado, como um ÚNICO provedor (Mercado Livre, mock,
 * etc.) consegue fornecê-lo.
 *
 * <p>Contém apenas os campos que a fonte externa entrega. Métricas calculadas
 * pelo LinkFlow — {@code score}, {@code trend}, {@code scoreDetail} e
 * {@code estimatedCommission} — NÃO fazem parte deste DTO: elas são derivadas
 * pela régua de scoring ({@code ProductScoringService}) na etapa de sync.</p>
 *
 * @param commissionPct pode ser {@code null} quando a fonte não informa a
 *                      comissão; nesse caso o provider deriva do mapa de
 *                      comissão por categoria (config).
 */
public record RawProduct(
    String externalId,
    Platform platform,
    String name,
    String description,
    BigDecimal price,
    BigDecimal originalPrice,
    BigDecimal commissionPct,
    String imageUrl,
    String productUrl,
    String category
) {

    /**
     * Constrói um {@link RawProduct} a partir de uma entidade {@link Product}.
     * Usado pelo {@code MockProductProvider} para reaproveitar o catálogo mock
     * existente sem carregar score/trend (recalculados pelo LinkFlow).
     */
    public static RawProduct fromEntity(Product p) {
        return new RawProduct(
            p.getExternalId(),
            p.getPlatform(),
            p.getName(),
            p.getDescription(),
            p.getPrice(),
            p.getOriginalPrice(),
            p.getCommissionPct(),
            p.getImageUrl(),
            p.getProductUrl(),
            p.getCategory()
        );
    }
}
