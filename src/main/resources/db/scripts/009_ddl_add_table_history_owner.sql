CREATE TABLE history_owner(
                              id BIGSERIAL PRIMARY KEY,
                              car_id BIGINT NOT NULL REFERENCES car(id),
                              owner_id BIGINT NOT NULL REFERENCES owners(id)
);