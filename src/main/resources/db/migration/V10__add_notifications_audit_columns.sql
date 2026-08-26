-- Add missing audit columns to notifications table
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS created_by VARCHAR(100);
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100);
