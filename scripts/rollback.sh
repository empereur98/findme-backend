#!/bin/bash

# Agent de rollback pour FindMe Backend
# Ce script restaure la version précédente en cas d'échec de déploiement

set -e

BACKUP_DIR="${BACKUP_DIR:-/opt/findme-backups}"
LATEST_BACKUP=$(ls -t ${BACKUP_DIR}/findme-backup-* 2>/dev/null | head -n 1)

echo "🔄 Agent de rollback démarré"
echo "📁 Répertoire de sauvegarde: $BACKUP_DIR"
echo "📦 Dernière sauvegarde: $LATEST_BACKUP"
echo ""

if [ -z "$LATEST_BACKUP" ]; then
    echo "❌ Aucune sauvegarde trouvée"
    exit 1
fi

# Confirmation
read -p "⚠️  Êtes-vous sûr de vouloir effectuer un rollback? (y/N) " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "❌ Rollback annulé"
    exit 1
fi

# Arrêter les services
echo "🛑 Arrêt des services..."
docker-compose down
echo "✅ Services arrêtés"

# Restauration de la base de données
echo "🗄️  Restauration de la base de données..."
if [ -f "${LATEST_BACKUP}-database.sql" ]; then
    docker-compose up -d postgres
    sleep 10
    docker exec -i findme-postgres psql -U findme findme < "${LATEST_BACKUP}-database.sql"
    echo "✅ Base de données restaurée"
else
    echo "⚠️  Sauvegarde de base de données non trouvée"
fi

# Restauration de la configuration
echo "⚙️  Restauration de la configuration..."
if [ -f "${LATEST_BACKUP}-config.tar.gz" ]; then
    tar -xzf "${LATEST_BACKUP}-config.tar.gz"
    echo "✅ Configuration restaurée"
else
    echo "⚠️  Sauvegarde de configuration non trouvée"
fi

# Redémarrer les services
echo "🚀 Redémarrage des services..."
docker-compose up -d
echo "✅ Services redémarrés"

# Attendre que les services soient prêts
echo "⏳ Attente que les services soient prêts..."
sleep 30

# Vérification de santé
echo "🔍 Vérification de santé..."
./scripts/health-check.sh

echo "🎉 Rollback terminé avec succès"
