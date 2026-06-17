package br.com.linkflow.client;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class JpaMercadoLivreTokenStoreTest {

    @Autowired
    JpaMercadoLivreTokenStore store;

    @Test
    @DisplayName("Sem token persistido → read retorna vazio")
    void deveRetornarVazioSemToken() {
        assertThat(store.read()).isEmpty();
    }

    @Test
    @DisplayName("Deve persistir e ler o refresh token (upsert mantém único registro)")
    void devePersistirEAtualizar() {
        store.save("TG-token-1");
        assertThat(store.read()).contains("TG-token-1");

        // Nova rotação sobrescreve o valor anterior.
        store.save("TG-token-2");
        assertThat(store.read()).contains("TG-token-2");
    }

    @Test
    @DisplayName("save ignora token nulo ou em branco")
    void deveIgnorarTokenInvalido() {
        store.save("TG-valido");
        store.save(null);
        store.save("   ");
        assertThat(store.read()).contains("TG-valido");
    }
}