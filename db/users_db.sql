CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE SCHEMA IF NOT EXISTS users_db;

CREATE TABLE users_db.roles (
    id    SERIAL PRIMARY KEY,
    name  VARCHAR(100) NOT NULL UNIQUE,
    level INTEGER NOT NULL UNIQUE
);

CREATE TABLE users_db.users (
    id            SERIAL PRIMARY KEY,
    first_name    VARCHAR(255) NOT NULL,
    last_name     VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    phone_number  VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at    TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE users_db.user_role (
    user_id INTEGER NOT NULL REFERENCES users_db.users(id),
    role_id INTEGER NOT NULL REFERENCES users_db.roles(id),
    PRIMARY KEY (user_id, role_id)
);

INSERT INTO users_db.roles (name, level) VALUES
('subscriber', 1), ('producer', 2), ('administrator', 3);

INSERT INTO users_db.users (id, first_name, last_name, email, phone_number, password_hash) VALUES
(1, 'Валерий', 'Игнатов', 'valera@mail.ru', '+79001112233', crypt('1234', gen_salt('bf'))),
(2, 'Игнат', 'Григорьев', 'ignat@mail.ru', NULL, crypt('5678', gen_salt('bf'))),
(3, 'Олег', 'Степанов', 'oleg@mail.ru', '+79005556677', crypt('admin', gen_salt('bf'))),
(4, 'Анна', 'Козлова', 'anna@mail.ru', NULL, crypt('1111', gen_salt('bf'))),
(5, 'Пётр', 'Смирнов', 'petr@mail.ru', NULL, crypt('2222', gen_salt('bf'))),
(6, 'Мария', 'Орлова', 'maria@mail.ru', NULL, crypt('3333', gen_salt('bf'))),
(7, 'Денис', 'Волков', 'denis@mail.ru', NULL, crypt('4444', gen_salt('bf')));

SELECT setval('users_db.users_id_seq', 7);

INSERT INTO users_db.user_role (user_id, role_id)
SELECT v.user_id, r.id
FROM (VALUES (1, 'producer'), (4, 'producer'), (5, 'producer'),
             (2, 'subscriber'), (6, 'subscriber'), (7, 'subscriber'),
             (3, 'administrator')) v(user_id, role)
JOIN users_db.roles r ON r.name = v.role;
