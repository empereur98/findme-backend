-- Alter image_facade column type from varchar(1000) to TEXT to support larger base64 images
ALTER TABLE addresses ALTER COLUMN image_facade TYPE TEXT;
