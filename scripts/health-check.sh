#!/bin/bash

# Agent de vérification de santé pour FindMe Backend
# Ce script vérifie que l'application est fonctionnelle après déploiement

set -e

HEALTH_URL="${HEALTH_URL:-http://localhost:8080/actuator/health}"
MAX_RETRIES="${MAX_RETRIES:-30}"
RETRY_INTERVAL="${RETRY_INTERVAL:-5}"
TIMEOUT="${TIMEOUT:-10}"

echo "🔍 Agent de vérification de santé démarré"
echo "📍 URL de santé: $HEALTH_URL"
echo "⏱️  Tentatives max: $MAX_RETRIES"
echo "⏱️  Intervalle: ${RETRY_INTERVAL}s"
echo "⏱️  Timeout: ${TIMEOUT}s"
echo ""

retry_count=0

while [ $retry_count -lt $MAX_RETRIES ]; do
    retry_count=$((retry_count + 1))
    
    echo "🔄 Tentative $retry_count/$MAX_RETRIES..."
    
    if curl -f -s -S --max-time $TIMEOUT "$HEALTH_URL" > /dev/null 2>&1; then
        echo "✅ Application saine et fonctionnelle"
        
        # Vérifications supplémentaires
        echo "🔍 Vérifications supplémentaires..."
        
        # Vérifier l'endpoint info
        if curl -f -s -S --max-time $TIMEOUT "${HEALTH_URL/health/info}" > /dev/null 2>&1; then
            echo "✅ Endpoint /actuator/info accessible"
        else
            echo "⚠️  Endpoint /actuator/info non accessible"
        fi
        
        # Vérifier l'endpoint metrics
        if curl -f -s -S --max-time $TIMEOUT "${HEALTH_URL/health/metrics}" > /dev/null 2>&1; then
            echo "✅ Endpoint /actuator/metrics accessible"
        else
            echo "⚠️  Endpoint /actuator/metrics non accessible"
        fi
        
        echo "🎉 Vérification de santé réussie"
        exit 0
    else
        echo "❌ Application non prête"
        if [ $retry_count -lt $MAX_RETRIES ]; then
            echo "⏳ Attente de ${RETRY_INTERVAL}s avant la prochaine tentative..."
            sleep $RETRY_INTERVAL
        fi
    fi
done

echo "❌ Échec de la vérification de santé après $MAX_RETRIES tentatives"
exit 1
