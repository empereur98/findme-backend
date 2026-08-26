-- Migration pour créer des tickets de support de test
-- Associés à l'utilisateur admin par défaut

INSERT INTO support_tickets (code, user_name, user_email, subject, message, type, status, user_id, created_at, created_by, version)
SELECT 
    'MSG-001',
    'Admin User',
    'admin@findme.com',
    'Problème de connexion',
    'Je ne parviens pas à me connecter à mon compte depuis hier.',
    'technique',
    'En cours',
    id,
    CURRENT_TIMESTAMP,
    'system',
    0
FROM users 
WHERE email = 'admin@findme.com';

INSERT INTO support_tickets (code, user_name, user_email, subject, message, type, status, user_id, created_at, created_by, version)
SELECT 
    'MSG-002',
    'Admin User',
    'admin@findme.com',
    'Demande de fonctionnalité',
    'Serait-il possible d''ajouter une fonctionnalité d''export PDF ?',
    'fonctionnalité',
    'Non traité',
    id,
    CURRENT_TIMESTAMP,
    'system',
    0
FROM users 
WHERE email = 'admin@findme.com';
