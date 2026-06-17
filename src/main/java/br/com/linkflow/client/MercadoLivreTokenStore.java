package br.com.linkflow.client;

import java.util.Optional;

/**
 * Armazena o refresh token do Mercado Livre de forma durável. O ML rotaciona o
 * refresh token a cada renovação do access token; sem persistir o valor novo, o
 * token configurado (env) fica obsoleto após a primeira renovação.
 *
 * <p>Implementações devem ser resilientes: uma falha de leitura/escrita não pode
 * derrubar o sync (retornar {@link Optional#empty()} / não propagar exceção).</p>
 */
public interface MercadoLivreTokenStore {

    /** Refresh token persistido, se houver. */
    Optional<String> read();

    /** Persiste o refresh token rotacionado (upsert). */
    void save(String refreshToken);
}