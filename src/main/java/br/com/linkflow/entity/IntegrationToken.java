package br.com.linkflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Token de integração persistido por provedor externo. Usado para guardar
 * credenciais que rotacionam e precisam sobreviver a restart — caso do
 * refresh token do Mercado Livre, que é trocado a cada renovação do access token.
 */
@Entity
@Table(name = "integration_tokens")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class IntegrationToken {

    /** Identificador do provedor (ex.: {@code MERCADO_LIVRE}). */
    @Id
    @Column(length = 50)
    private String provider;

    @Column(name = "refresh_token", nullable = false, length = 1024)
    private String refreshToken;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
