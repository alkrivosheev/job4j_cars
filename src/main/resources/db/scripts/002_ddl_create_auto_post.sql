CREATE TABLE auto_post (
                           id BIGSERIAL PRIMARY KEY,
                           description TEXT,
                           created TIMESTAMP WITH TIME ZONE NOT NULL,
                           auto_user_id BIGINT NOT NULL,
                           CONSTRAINT fk_auto_post_user FOREIGN KEY (auto_user_id)
                               REFERENCES auto_user(id) ON DELETE CASCADE
);
