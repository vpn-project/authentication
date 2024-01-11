CREATE TABLE IF NOT EXISTS users
(
    id       SERIAL PRIMARY KEY,
    username VARCHAR(32) UNIQUE  NOT NULL,
    password VARCHAR(60)         NOT NULL,
    email    VARCHAR(256) UNIQUE NOT NULL,
    role     VARCHAR(16) DEFAULT 'USER'
);

CREATE TABLE IF NOT EXISTS account
(
    id              BIGSERIAL PRIMARY KEY,
    username        VARCHAR(255),
    is_main_account BOOLEAN
);

CREATE TABLE IF NOT EXISTS account_linked_account
(
    id                BIGSERIAL PRIMARY KEY,
    main_account_id   BIGINT,
    linked_account_id BIGINT,
    FOREIGN KEY (main_account_id) REFERENCES account (id),
    FOREIGN KEY (linked_account_id) REFERENCES account (id)
);

CREATE TABLE IF NOT EXISTS token
(
    id    serial PRIMARY KEY,
    token VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS subscription
(
    id              BIGSERIAL PRIMARY KEY,
    expiration_date TIMESTAMP,
    is_active       BOOLEAN,
    token_id        BIGINT,
    FOREIGN KEY (token_id) REFERENCES token (id)
);
