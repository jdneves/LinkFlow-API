-- Persistência de tokens de integração por provedor externo.
-- O refresh token do Mercado Livre rotaciona a cada renovação do access token;
-- precisa sobreviver a restart, então deixa de viver apenas em memória/env.
CREATE TABLE integration_tokens (
    provider      VARCHAR(50)   PRIMARY KEY,
    refresh_token VARCHAR(1024) NOT NULL,
    updated_at    TIMESTAMP     NOT NULL
);