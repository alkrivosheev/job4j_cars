INSERT  INTO fuel_types (name) VALUES ('Бензин');
INSERT  INTO engines (name) VALUES ('V8');
INSERT  INTO car_colors (name) VALUES ('Красный');
INSERT  INTO bodies (name) VALUES ('Седан');
INSERT  INTO categories (name) VALUES ('Легковой');
INSERT  INTO brands (name) VALUES ('Toyota');
INSERT  INTO models (name) VALUES ('Camry');
INSERT  INTO drive_types (name) VALUES ('Передний');
INSERT  INTO transmission_types (name) VALUES ('Автомат');
INSERT  INTO wheel_sides (name) VALUES ('Левый');

INSERT INTO users (login, name, password) VALUES ('user', 'Тестовый Иван Петрович', '123');

INSERT INTO cars (
    vin, mileage, year_of_manufacture, count_owners,
    model_id, brand_id, category_id, body_id, engine_id,
    transmission_type_id, drive_type_id, wheel_side_id, car_color_id, fuel_type_id
) VALUES (
             '1HGBH41JXMN109186', 15000, 2020, 1,
             (SELECT id FROM models WHERE name = 'Camry' LIMIT 1),
         (SELECT id FROM brands WHERE name = 'Toyota' LIMIT 1),
         (SELECT id FROM categories WHERE name = 'Легковой' LIMIT 1),
         (SELECT id FROM bodies WHERE name = 'Седан' LIMIT 1),
         (SELECT id FROM engines WHERE name = 'V8' LIMIT 1),
         (SELECT id FROM transmission_types WHERE name = 'Автомат' LIMIT 1),
         (SELECT id FROM drive_types WHERE name = 'Передний' LIMIT 1),
         (SELECT id FROM wheel_sides WHERE name = 'Левый' LIMIT 1),
         (SELECT id FROM car_colors WHERE name = 'Красный' LIMIT 1),
         (SELECT id FROM fuel_types WHERE name = 'Бензин' LIMIT 1)
    );

INSERT INTO posts (status, description, created_at, price, car_id, user_id)
VALUES ('ACTIVE', 'Продам отличную машину!', NOW(), 2500000.00,
        (SELECT id FROM cars ORDER BY id DESC LIMIT 1),
       (SELECT id FROM users WHERE login = 'user' LIMIT 1));

INSERT INTO post_photos (photo_path, post_id)
VALUES ('image1.jpg',
        (SELECT id FROM posts ORDER BY id DESC LIMIT 1));
INSERT INTO post_photos (photo_path, post_id)
VALUES ('image2.jpg',
        (SELECT id FROM posts ORDER BY id DESC LIMIT 1));