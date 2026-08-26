-- Migration pour la table settings
-- Généré automatiquement pour FindMe Backend
-- Date: 2024-07-20

CREATE TABLE settings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    strict_formatting BOOLEAN NOT NULL DEFAULT true,
    auto_correct BOOLEAN NOT NULL DEFAULT true,
    validation_threshold INTEGER NOT NULL DEFAULT 85,
    rate_limit INTEGER NOT NULL DEFAULT 1000,
    webhook_timeout INTEGER NOT NULL DEFAULT 5000,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT NOT NULL DEFAULT 0
);
