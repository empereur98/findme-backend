-- Migration pour la table exports
-- Généré automatiquement pour FindMe Backend
-- Date: 2024-07-20

CREATE TABLE exports (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(20) NOT NULL UNIQUE,
    filename VARCHAR(255) NOT NULL,
    download_url VARCHAR(500) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    size VARCHAR(50) NOT NULL,
    user_id UUID NOT NULL REFERENCES users(id),
    address_id UUID NOT NULL REFERENCES addresses(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_exports_code ON exports(code);
CREATE INDEX idx_exports_user_id ON exports(user_id);
CREATE INDEX idx_exports_address_id ON exports(address_id);
