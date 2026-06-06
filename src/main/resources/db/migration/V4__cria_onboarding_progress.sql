CREATE TABLE onboarding_progress (
                                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                     user_id UUID NOT NULL UNIQUE REFERENCES users(id),
                                     steps JSONB NOT NULL DEFAULT '{
                                       "cadastro_concluido": true,
                                       "primeiro_produto": false,
                                       "primeiro_roteiro": false,
                                       "primeiro_link": false,
                                       "primeiro_video": false
                                     }',
                                     completed BOOLEAN NOT NULL DEFAULT false,
                                     completed_at TIMESTAMP,
                                     created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_onboarding_user_id ON onboarding_progress(user_id);