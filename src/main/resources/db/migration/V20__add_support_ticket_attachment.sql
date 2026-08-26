-- Migration pour ajouter le champ attachment_url à support_tickets
-- Permet aux utilisateurs d'envoyer des fichiers avec leurs tickets de support
-- Date: 2026-08-26

ALTER TABLE support_tickets ADD COLUMN attachment_url VARCHAR(500);

CREATE INDEX idx_support_tickets_attachment_url ON support_tickets(attachment_url);
