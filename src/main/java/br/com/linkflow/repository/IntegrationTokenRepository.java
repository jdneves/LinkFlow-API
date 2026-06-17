package br.com.linkflow.repository;

import br.com.linkflow.entity.IntegrationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntegrationTokenRepository extends JpaRepository<IntegrationToken, String> {
}