CREATE TABLE post_photos (
                             id BIGSERIAL PRIMARY KEY,
                             post_id BIGINT NOT NULL REFERENCES auto_post(id) ON DELETE CASCADE,
                             photo_path VARCHAR(255) NOT NULL
);