package br.com.linkflow.client;

import java.util.Optional;

/** Store em memória para testes do {@link MercadoLivreClient} (sem banco). */
public class InMemoryMercadoLivreTokenStore implements MercadoLivreTokenStore {

    private String refreshToken;

    public InMemoryMercadoLivreTokenStore() {
    }

    public InMemoryMercadoLivreTokenStore(String seed) {
        this.refreshToken = seed;
    }

    @Override
    public Optional<String> read() {
        return Optional.ofNullable(refreshToken);
    }

    @Override
    public void save(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
