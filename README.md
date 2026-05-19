# Gestion Commerciale — Module Produits & Catalogue

Application de gestion commerciale développée avec Spring Boot 3 et Angular 17.

## Module EP03 : Gestion des Produits & Catalogue

Ce module permet de :
- Gérer le catalogue de produits (CRUD complet)
- Organiser les produits par catégories hiérarchiques
- Suivre les niveaux de stock avec alertes automatiques
- Rechercher et filtrer les produits par critères multiples
- Archiver les produits au lieu de les supprimer

## Stack Technique

| Composant | Technologie |
|-----------|-------------|
| Backend | Spring Boot 3 (Java 17) |
| Frontend | Angular 17 |
| Base de données | PostgreSQL 15 |
| Conteneurisation | Docker Compose |

## Lancement rapide

```bash
# Démarrer la base de données
docker-compose up -d db

# Backend
cd backend
./mvnw spring-boot:run

# Frontend
cd frontend
npm install
ng serve
```

L'application sera accessible sur `http://localhost:4200`

## Structure du projet

```
projet-e/
├── backend/          # API REST Spring Boot
├── frontend/         # Application Angular
├── docker-compose.yml
└── README.md
```
