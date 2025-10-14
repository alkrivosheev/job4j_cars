create table car(
                    id SERIAL PRIMARY KEY,
                    engine_id INT NOT NULL UNIQUE REFERENCES engine(id)
);