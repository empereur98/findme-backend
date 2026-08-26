# Script d'analyse de code complet pour le projet findme-backend
# Ce script exécute tous les outils d'analyse de code statique

Write-Host "=== Analyse de Code Complète ===" -ForegroundColor Green
Write-Host "Projet: findme-backend" -ForegroundColor Yellow
Write-Host ""

# Vérifier si Maven est installé
try {
    $mvnVersion = ./mvnw --version | Select-Object -First 1
    Write-Host "Maven détecté: $mvnVersion" -ForegroundColor Green
} catch {
    Write-Host "Erreur: Maven n'est pas installé ou accessible" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "=== Étape 1: Nettoyage du projet ===" -ForegroundColor Cyan
./mvnw clean
if ($LASTEXITCODE -ne 0) {
    Write-Host "Erreur lors du nettoyage" -ForegroundColor Red
} else {
    Write-Host "Nettoyage terminé avec succès" -ForegroundColor Green
}

Write-Host ""
Write-Host "=== Étape 2: Analyse Checkstyle ===" -ForegroundColor Cyan
./mvnw checkstyle:check
if ($LASTEXITCODE -ne 0) {
    Write-Host "Attention: Checkstyle a détecté des violations" -ForegroundColor Yellow
} else {
    Write-Host "Checkstyle: Analyse terminée (violations possibles mais build non bloqué)" -ForegroundColor Green
}

Write-Host ""
Write-Host "=== Étape 3: Analyse SpotBugs (désactivé - problème Java 21) ===" -ForegroundColor Yellow
Write-Host "SpotBugs est désactivé temporairement en raison de problèmes de compatibilité avec Java 21" -ForegroundColor Yellow

Write-Host ""
Write-Host "=== Étape 4: Analyse PMD ===" -ForegroundColor Cyan
./mvnw pmd:check
if ($LASTEXITCODE -ne 0) {
    Write-Host "Attention: PMD a détecté des violations" -ForegroundColor Yellow
} else {
    Write-Host "PMD: Analyse terminée avec succès" -ForegroundColor Green
}

Write-Host ""
Write-Host "=== Étape 5: Compilation et tests ===" -ForegroundColor Cyan
./mvnw compile test
if ($LASTEXITCODE -ne 0) {
    Write-Host "Erreur lors de la compilation ou des tests" -ForegroundColor Red
} else {
    Write-Host "Compilation et tests réussis" -ForegroundColor Green
}

Write-Host ""
Write-Host "=== Étape 6: Rapport de couverture de code (JaCoCo) ===" -ForegroundColor Cyan
./mvnw jacoco:report
if ($LASTEXITCODE -ne 0) {
    Write-Host "Erreur lors de la génération du rapport JaCoCo" -ForegroundColor Red
} else {
    Write-Host "Rapport JaCoCo généré: target/site/jacoco/index.html" -ForegroundColor Green
}

Write-Host ""
Write-Host "=== Analyse terminée ===" -ForegroundColor Green
Write-Host ""
Write-Host "Rapports générés:" -ForegroundColor Yellow
Write-Host "  - Checkstyle: target/checkstyle-result.xml" -ForegroundColor White
Write-Host "  - PMD: target/pmd.xml" -ForegroundColor White
Write-Host "  - JaCoCo: target/site/jacoco/index.html" -ForegroundColor White
Write-Host ""
Write-Host "Note: SpotBugs est désactivé temporairement (problème Java 21)" -ForegroundColor Yellow
Write-Host "Pour visualiser les rapports, ouvrez les fichiers correspondants." -ForegroundColor Cyan
