-- Migration pour créer un utilisateur admin par défaut
-- Mot de passe: Admin@1234 (hashé avec BCrypt)
-- Email: admin@findme.com
-- Rôle: ADMIN
-- Plan: premium

INSERT INTO users (first_name, last_name, email, password, verified, role, plan, addresses_created_count, max_addresses, registration_date, created_at, created_by, version)
VALUES (
    'Admin',
    'User',
    'admin@findme.com',
    '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', -- BCrypt hash de "Admin@1234"
    true,
    'ADMIN',
    'premium',
    0,
    100,
    CURRENT_DATE,
    CURRENT_TIMESTAMP,
    'system',
    0
);
