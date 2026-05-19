#!/bin/bash

# =============================================================
#  Script de lancement — Gestion Commerciale (Module Produits)
#  Usage : ./start.sh
#  Base H2 en mémoire — aucune dépendance Docker requise
# =============================================================

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
BACKEND_DIR="$PROJECT_DIR/backend"
FRONTEND_DIR="$PROJECT_DIR/frontend"

echo ""
echo -e "${CYAN}=================================================${NC}"
echo -e "${CYAN}   Gestion Commerciale — Module Produits          ${NC}"
echo -e "${CYAN}=================================================${NC}"
echo ""

# ---- Fonction cleanup : tuer les processus au Ctrl+C ----
cleanup() {
    echo ""
    echo -e "${YELLOW}Arrêt des services...${NC}"
    [ -n "$BACKEND_PID" ] && kill "$BACKEND_PID" 2>/dev/null
    [ -n "$FRONTEND_PID" ] && kill "$FRONTEND_PID" 2>/dev/null
    echo -e "${GREEN}Tous les services sont arrêtés.${NC}"
    exit 0
}
trap cleanup INT TERM

# ---- 1. Vérification des prérequis ----
echo -e "${YELLOW}[1/4] Vérification des prérequis...${NC}"

# Node.js
if ! command -v node &>/dev/null; then
    echo -e "${YELLOW}  Node.js non trouvé, installation via Homebrew...${NC}"
    brew install node
fi
echo "  ✓ Node.js $(node -v)"

# Java 17
if ! command -v java &>/dev/null || ! java -version 2>&1 | grep -q "version"; then
    echo -e "${YELLOW}  Java non trouvé, installation de OpenJDK 17 via Homebrew...${NC}"
    brew install openjdk@17
    export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"
    export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
fi

# Si Java est installé via Homebrew mais pas dans le PATH
if ! java -version 2>&1 | grep -q "version"; then
    if [ -d "/opt/homebrew/opt/openjdk@17" ]; then
        export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"
        export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
    elif [ -d "/opt/homebrew/opt/openjdk" ]; then
        export PATH="/opt/homebrew/opt/openjdk/bin:$PATH"
        export JAVA_HOME="/opt/homebrew/opt/openjdk"
    fi
fi
echo "  ✓ Java $(java -version 2>&1 | head -1)"

# Maven
if ! command -v mvn &>/dev/null; then
    echo -e "${YELLOW}  Maven non trouvé, installation via Homebrew...${NC}"
    brew install maven
fi
echo "  ✓ Maven OK"

echo ""

# ---- 2. Installation des dépendances frontend ----
echo -e "${YELLOW}[2/4] Installation des dépendances frontend...${NC}"
cd "$FRONTEND_DIR"
if [ ! -d "node_modules" ]; then
    npm install
else
    echo "  ✓ node_modules déjà présent"
fi

echo ""

# ---- 3. Démarrage du backend Spring Boot (H2 en mémoire) ----
echo -e "${YELLOW}[3/4] Démarrage du backend Spring Boot (port 8080)...${NC}"
echo "  Base de données H2 en mémoire (aucune config externe requise)"
cd "$BACKEND_DIR"

mvn spring-boot:run -q &
BACKEND_PID=$!

echo "  Compilation et démarrage en cours..."
for i in $(seq 1 120); do
    if curl -s http://localhost:8080/api/produits &>/dev/null; then
        echo -e "  ${GREEN}✓ Backend prêt sur http://localhost:8080${NC}"
        break
    fi
    if [ $i -eq 120 ]; then
        echo -e "  ${YELLOW}Le backend met du temps, on continue...${NC}"
    fi
    sleep 2
done

echo ""

# ---- 4. Démarrage du frontend Angular ----
echo -e "${YELLOW}[4/4] Démarrage du frontend Angular (port 4200)...${NC}"
cd "$FRONTEND_DIR"
npx ng serve --open &
FRONTEND_PID=$!

echo ""
echo -e "${GREEN}=================================================${NC}"
echo -e "${GREEN}   Tout est lancé !                               ${NC}"
echo -e "${GREEN}=================================================${NC}"
echo ""
echo -e "  Frontend  :  ${CYAN}http://localhost:4200${NC}"
echo -e "  API REST  :  ${CYAN}http://localhost:8080/api/produits${NC}"
echo -e "  Console H2:  ${CYAN}http://localhost:8080/h2-console${NC}"
echo ""
echo -e "  ${YELLOW}Appuie sur Ctrl+C pour tout arrêter${NC}"
echo ""

wait
