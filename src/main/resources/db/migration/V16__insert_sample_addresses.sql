-- Migration pour insérer des adresses de test
-- Ces adresses seront associées à l'utilisateur admin par défaut

-- Insérer les adresses de test en utilisant l'ID de l'admin
INSERT INTO addresses (code, name, country, city, district, street, landmark, gps_lat, gps_lng, image_facade, code_plus, owner_name, owner_email, status, address_date, type, user_id, created_at, created_by, updated_at, updated_by, version)
SELECT 
    'ADR-1234-XX',
    'Maison Principale',
    'sn',
    'Dakar',
    'Plateau',
    'Rue de la République',
    'Près de la place de l''Indépendance',
    14.7167,
    -17.4677,
    NULL,
    '8F4G22+22',
    'Admin User',
    'admin@findme.com',
    'Vérifié',
    CURRENT_DATE,
    'Personnel',
    id,
    CURRENT_TIMESTAMP,
    'system',
    CURRENT_TIMESTAMP,
    'system',
    0
FROM users 
WHERE email = 'admin@findme.com'
LIMIT 1;

INSERT INTO addresses (code, name, country, city, district, street, landmark, gps_lat, gps_lng, image_facade, code_plus, owner_name, owner_email, status, address_date, type, user_id, created_at, created_by, updated_at, updated_by, version)
SELECT 
    'ADR-5678-XX',
    'Bureau',
    'sn',
    'Dakar',
    'Sacré-Cœur',
    'Avenue Cheikh Anta Diop',
    'Immeuble ABC',
    14.6928,
    -17.4467,
    NULL,
    '8F4G23+33',
    'Admin User',
    'admin@findme.com',
    'En cours',
    CURRENT_DATE,
    'Professionnel',
    id,
    CURRENT_TIMESTAMP,
    'system',
    CURRENT_TIMESTAMP,
    'system',
    0
FROM users 
WHERE email = 'admin@findme.com'
LIMIT 1;

INSERT INTO addresses (code, name, country, city, district, street, landmark, gps_lat, gps_lng, image_facade, code_plus, owner_name, owner_email, status, address_date, type, user_id, created_at, created_by, updated_at, updated_by, version)
SELECT 
    'ADR-9012-YY',
    'Villa',
    'sn',
    'Saly',
    'Saly Portudal',
    'Boulevard des Pêcheurs',
    'Face à la plage',
    14.3833,
    -16.8167,
    NULL,
    '8F4G24+44',
    'Admin User',
    'admin@findme.com',
    'En cours',
    CURRENT_DATE,
    'Personnel',
    id,
    CURRENT_TIMESTAMP,
    'system',
    CURRENT_TIMESTAMP,
    'system',
    0
FROM users 
WHERE email = 'admin@findme.com'
LIMIT 1;

-- Mettre à jour le compteur d'adresses de l'admin
UPDATE users 
SET addresses_created_count = addresses_created_count + 3 
WHERE email = 'admin@findme.com';
