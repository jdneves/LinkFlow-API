package br.com.linkflow.client;

import br.com.linkflow.entity.IntegrationToken;
import br.com.linkflow.repository.IntegrationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Persiste o refresh token do Mercado Livre na tabela {@code integration_tokens}.
 * Resiliente: falhas de banco são logadas com {@code log.warn} e não propagam,
 * para não interromper a sincronização.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JpaMercadoLivreTokenStore implements MercadoLivreTokenStore {

    private static final String PROVIDER = "MERCADO_LIVRE";

    private final IntegrationTokenRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Optional<String> read() {
        try {
            return repository.findById(PROVIDER).map(IntegrationToken::getRefreshToken);
        } catch (Exception e) {
            log.warn("Falha ao ler refresh token do Mercado Livre no banco: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public void save(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) return;
        try {
            IntegrationToken token = repository.findById(PROVIDER)
                .orElseGet(() -> IntegrationToken.builder().provider(PROVIDER).build());
            token.setRefreshToken(refreshToken);
            token.setUpdatedAt(LocalDateTime.now());
            repository.save(token);
            log.debug("Refresh token do Mercado Livre persistido.");
        } catch (Exception e) {
            log.warn("Falha ao persistir refresh token do Mercado Livre: {}", e.getMessage());
        }
    }
}