CREATE TABLE owners(
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       user_id BIGINT NOT NULL REFERENCES auto_user(id)
);