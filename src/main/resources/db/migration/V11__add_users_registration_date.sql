-- Add registration_date column to users table
ALTER TABLE users ADD COLUMN IF NOT EXISTS registration_date DATE;
