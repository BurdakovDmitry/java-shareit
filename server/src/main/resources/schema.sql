CREATE TABLE IF NOT EXISTS users (
    user_id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_email VARCHAR(512) NOT NULL UNIQUE,
    user_name  VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS requests (
    request_id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    request_description VARCHAR(5000) NOT NULL,
    requestor_id        BIGINT NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    created             TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS items (
    item_id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    item_name        VARCHAR(255) NOT NULL,
    item_description VARCHAR(5000) NOT NULL,
    available        BOOLEAN NOT NULL,
    owner_id         BIGINT NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    request_id       BIGINT REFERENCES requests (request_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings (
    booking_id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    booking_start  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    booking_end    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id        BIGINT NOT NULL REFERENCES items (item_id) ON DELETE CASCADE,
    booker_id      BIGINT NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    booking_status VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS comments (
    comment_id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    comment_text    VARCHAR(5000) NOT NULL,
    comment_created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    author_name     VARCHAR(255) NOT NULL,
    item_id         BIGINT NOT NULL REFERENCES items (item_id) ON DELETE CASCADE
);