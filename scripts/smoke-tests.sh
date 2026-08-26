#!/bin/bash

# Agent de tests de fumée pour FindMe Backend
# Ce script exécute des tests de base après déploiement

set -e

API_URL="${API_URL:-http://localhost:8080}"
API_VERSION="${API_VERSION:-v1}"

echo "🧪 Agent de tests de fumée démarré"
echo "🌐 URL de l'API: $API_URL"
echo "📌 Version de l'API: $API_VERSION"
echo ""

FAILED_TESTS=0
TOTAL_TESTS=0

# Fonction de test
run_test() {
    local test_name="$1"
    local endpoint="$2"
    local expected_status="$3"
    
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    echo "🔍 Test: $test_name"
    
    response=$(curl -s -o /dev/null -w "%{http_code}" "${API_URL}${endpoint}")
    
    if [ "$response" = "$expected_status" ]; then
        echo "✅ $test_name - Status: $response"
    else
        echo "❌ $test_name - Échec (attendu: $expected_status, reçu: $response)"
        FAILED_TESTS=$((FAILED_TESTS + 1))
    fi
}

# Tests de base
run_test "Health Check" "/actuator/health" "200"
run_test "API Info" "/actuator/info" "200"
run_test "API Metrics" "/actuator/metrics" "200"

# Tests d'API (adapter selon vos endpoints)
# run_test "List Users" "/api/${API_VERSION}/users" "200"
# run_test "OpenAPI Docs" "/swagger-ui.html" "200"

echo ""
echo "📊 Résultats des tests:"
echo "   Total: $TOTAL_TESTS"
echo "   Réussis: $((TOTAL_TESTS - FAILED_TESTS))"
echo "   Échoués: $FAILED_TESTS"

if [ $FAILED_TESTS -eq 0 ]; then
    echo "🎉 Tous les tests de fumée ont réussi"
    exit 0
else
    echo "❌ Certains tests de fumée ont échoué"
    exit 1
fi
