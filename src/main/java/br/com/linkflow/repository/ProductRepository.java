package br.com.linkflow.repository;

import br.com.linkflow.entity.Product;
import br.com.linkflow.entity.Product.Platform;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findByExternalIdAndPlatform(String externalId, Platform platform);

    @Query("""
        SELECT p FROM Product p
        WHERE (:category IS NULL OR p.category = :category)
          AND (:platform IS NULL OR p.platform = :platform)
          AND (:search   IS NULL OR LOWER(p.name) LIKE :search)
        ORDER BY p.score DESC, p.updatedAt DESC
    """)
    Page<Product> findWithFilters(
        @Param("category") String category,
        @Param("platform") Platform platform,
        @Param("search")   String search,
        Pageable pageable
    );

    // Top produtos em alta (trend = RISING)
    @Query("SELECT p FROM Product p WHERE p.trend = 'RISING' ORDER BY p.score DESC")
    Page<Product> findTrending(Pageable pageable);
}
