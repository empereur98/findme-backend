#!/bin/bash

# Agent de sauvegarde pour FindMe Backend
# Ce script crée des sauvegardes avant déploiement

set -e

BACKUP_DIR="${BACKUP_DIR:-/opt/findme-backups}"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_NAME="findme-backup-${TIMESTAMP}"

echo "💾 Agent de sauvegarde démarré"
echo "📁 Répertoire de sauvegarde: $BACKUP_DIR"
echo "📦 Nom de sauvegarde: $BACKUP_NAME"
echo ""

# Créer le répertoire de sauvegarde
mkdir -p "$BACKUP_DIR"

# Sauvegarde de la base de données
echo "🗄️  Sauvegarde de la base de données..."
docker exec findme-postgres pg_dump -U findme findme > "${BACKUP_DIR}/${BACKUP_NAME}-database.sql"
echo "✅ Base de données sauvegardée"

# Sauvegarde des fichiers de configuration
echo "⚙️  Sauvegarde des fichiers de configuration..."
tar -czf "${BACKUP_DIR}/${BACKUP_NAME}-config.tar.gz" \
    docker-compose.yml \
    .env \
    prometheus/ \
    grafana/ \
    2>/dev/null || echo "⚠️  Certains fichiers de configuration n'ont pas pu être sauvegardés"
echo "✅ Configuration sauvegardée"

# Sauvegarde des logs
echo "📝 Sauvegarde des logs..."
tar -czf "${BACKUP_DIR}/${BACKUP_NAME}-logs.tar.gz" logs/ 2>/dev/null || echo "⚠️  Les logs n'ont pas pu être sauvegardés"
echo "✅ Logs sauvegardés"

# Nettoyage des anciennes sauvegardes (garder les 10 dernières)
echo "🧹 Nettoyage des anciennes sauvegardes..."
cd "$BACKUP_DIR"
ls -t findme-backup-* | tail -n +11 | xargs -r rm -rf
echo "✅ Nettoyage terminé"

echo "🎉 Sauvegarde terminée avec succès"
echo "📦 Sauvegarde disponible: ${BACKUP_DIR}/${BACKUP_NAME}"
