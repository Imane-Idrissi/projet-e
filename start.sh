#!/bin/bash

# =============================================================
#  Script de lancement — Gestion Commerciale (Module Produits)
#  Usage : ./start.sh
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
    docker stop gestion-commerciale-db 2>/dev/null
    echo -e "${GREEN}Tous les services sont arrêtés.${NC}"
    exit 0
}
trap cleanup INT TERM

# ---- 1. Vérification des prérequis ----
echo -e "${YELLOW}[1/5] Vérification des prérequis...${NC}"

# Docker
if ! command -v docker &>/dev/null; then
    echo -e "${RED}Docker n'est pas installé. Installe Docker Desktop : https://docker.com${NC}"
    exit 1
fi
if ! docker info &>/dev/null; then
    echo -e "${RED}Docker n'est pas démarré. Lance Docker Desktop d'abord.${NC}"
    exit 1
fi
echo "  ✓ Docker OK"

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
    echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
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

# Maven (via wrapper ou installation)
if [ ! -f "$BACKEND_DIR/mvnw" ] || [ ! -f "$BACKEND_DIR/.mvn/wrapper/maven-wrapper.jar" ]; then
    if ! command -v mvn &>/dev/null; then
        echo -e "${YELLOW}  Maven non trouvé, installation via Homebrew...${NC}"
        brew install maven
    fi
fi
echo "  ✓ Maven OK"

echo ""

# ---- 2. Démarrage PostgreSQL ----
echo -e "${YELLOW}[2/5] Démarrage de PostgreSQL...${NC}"
cd "$PROJECT_DIR"

if docker ps --format '{{.Names}}' | grep -q gestion-commerciale-db; then
    echo "  ✓ PostgreSQL déjà en cours d'exécution"
else
    docker-compose up -d db
    echo "  Attente du démarrage de PostgreSQL..."
    for i in $(seq 1 30); do
        if docker exec gestion-commerciale-db pg_isready -U app_user &>/dev/null; then
            break
        fi
        sleep 1
    done
    echo "  ✓ PostgreSQL démarré sur le port 5432"
fi

echo ""

# ---- 3. Installation des dépendances frontend ----
echo -e "${YELLOW}[3/5] Installation des dépendances frontend...${NC}"
cd "$FRONTEND_DIR"
if [ ! -d "node_modules" ]; then
    npm install
else
    echo "  ✓ node_modules déjà présent"
fi

echo ""

# ---- 4. Démarrage du backend Spring Boot ----
echo -e "${YELLOW}[4/5] Démarrage du backend Spring Boot (port 8080)...${NC}"
cd "$BACKEND_DIR"

if command -v mvn &>/dev/null; then
    mvn spring-boot:run -q &
else
    chmod +x mvnw 2>/dev/null
    ./mvnw spring-boot:run -q &
fi
BACKEND_PID=$!

# Attendre que le backend soit prêt
echo "  Compilation et démarrage en cours..."
for i in $(seq 1 120); do
    if curl -s http://localhost:8080/api/produits &>/dev/null; then
        echo -e "  ${GREEN}✓ Backend prêt sur http://localhost:8080${NC}"
        break
    fi
    if [ $i -eq 120 ]; then
        echo -e "  ${YELLOW}⏳ Le backend met du temps à démarrer, on continue...${NC}"
    fi
    sleep 2
done

echo ""

# ---- 5. Démarrage du frontend Angular ----
echo -e "${YELLOW}[5/5] Démarrage du frontend Angular (port 4200)...${NC}"
cd "$FRONTEND_DIR"
npx ng serve --open &
FRONTEND_PID=$!

echo ""
echo -e "${GREEN}=================================================${NC}"
echo -e "${GREEN}   Tout est lancé !                               ${NC}"
echo -e "${GREEN}=================================================${NC}"
echo ""
echo -e "  Frontend :  ${CYAN}http://localhost:4200${NC}"
echo -e "  Backend  :  ${CYAN}http://localhost:8080/api/produits${NC}"
echo -e "  Base DB  :  ${CYAN}localhost:5432 (gestion_commerciale)${NC}"
echo ""
echo -e "  ${YELLOW}Appuie sur Ctrl+C pour tout arrêter${NC}"
echo ""

# Attendre (garde le script en vie)
wait
