CREATE TABLE auto_user (
                           id BIGSERIAL PRIMARY KEY,
                           login VARCHAR(255) NOT NULL UNIQUE,
                           password VARCHAR(255) NOT NULL
);