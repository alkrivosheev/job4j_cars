CREATE TABLE car(
                    id BIGSERIAL PRIMARY KEY,
                    name TEXT,
                    engine_id BIGINT NOT NULL UNIQUE REFERENCES engine(id)
);