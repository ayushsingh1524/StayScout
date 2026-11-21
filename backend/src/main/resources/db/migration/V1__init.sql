CREATE EXTENSION IF NOT EXISTS btree_gist;

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE hotels (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    city VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    rating NUMERIC(2,1) NOT NULL CHECK (rating BETWEEN 0 AND 5),
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE rooms (
    id BIGSERIAL PRIMARY KEY,
    hotel_id BIGINT NOT NULL REFERENCES hotels(id) ON DELETE CASCADE,
    room_type VARCHAR(50) NOT NULL,
    price_per_night NUMERIC(10,2) NOT NULL CHECK (price_per_night > 0),
    capacity INT NOT NULL CHECK (capacity > 0)
);

CREATE TABLE bookings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    room_id BIGINT NOT NULL REFERENCES rooms(id),
    check_in DATE NOT NULL,
    check_out DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
    total_price NUMERIC(10,2) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CHECK (check_out > check_in),
    EXCLUDE USING gist (room_id WITH =, daterange(check_in, check_out) WITH &&)
        WHERE (status = 'CONFIRMED')
);

CREATE INDEX idx_hotels_city_rating ON hotels(city, rating);
CREATE INDEX idx_rooms_hotel_price ON rooms(hotel_id, price_per_night);
CREATE INDEX idx_bookings_user ON bookings(user_id);
