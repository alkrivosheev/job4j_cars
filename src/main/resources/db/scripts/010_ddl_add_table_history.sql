CREATE TABLE history(
                              id SERIAL PRIMARY KEY,
                              startAt TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
                              endAt  TIMESTAMP WITHOUT TIME ZONE
);