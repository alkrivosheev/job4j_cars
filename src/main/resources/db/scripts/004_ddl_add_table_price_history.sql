CREATE TABLE PRICE_HISTORY(
                              id SERIAL PRIMARY KEY,
                              before BIGINT NOT NULL,
                              after BIGINT NULL,
                              created TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
                              post_id BIGINT NOT NULL,
                              CONSTRAINT fk_price_history_post FOREIGN KEY (post_id) REFERENCES auto_post(id) ON DELETE CASCADE
);