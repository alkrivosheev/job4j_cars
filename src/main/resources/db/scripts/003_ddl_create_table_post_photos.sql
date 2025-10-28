CREATE TABLE IF NOT EXISTS post_photos (
                             id BIGSERIAL PRIMARY KEY,
                             post_id BIGINT NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
                             photo_path VARCHAR(255) NOT NULL
);