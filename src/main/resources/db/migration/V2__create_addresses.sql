-- Migration pour la table addresses
-- Généré automatiquement pour FindMe Backend
-- Date: 2024-07-20

CREATE TABLE addresses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    country VARCHAR(5) NOT NULL,
    city VARCHAR(100) NOT NULL,
    district VARCHAR(100) NOT NULL,
    street VARCHAR(255),
    landmark VARCHAR(500) NOT NULL,
    gps_lat DOUBLE PRECISION NOT NULL,
    gps_lng DOUBLE PRECISION NOT NULL,
    image_facade VARCHAR(1000),
    code_plus VARCHAR(20) NOT NULL,
    owner_name VARCHAR(100) NOT NULL,
    owner_email VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'En cours',
    address_date VARCHAR(20) NOT NULL,
    type VARCHAR(20) NOT NULL,
    user_id UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_addresses_code ON addresses(code);
CREATE INDEX idx_addresses_code_plus ON addresses(code_plus);
CREATE INDEX idx_addresses_user_id ON addresses(user_id);
CREATE INDEX idx_addresses_country ON addresses(country);
CREATE INDEX idx_addresses_city ON addresses(city);
CREATE INDEX idx_addresses_status ON addresses(status);
CREATE INDEX idx_addresses_type ON addresses(type);
