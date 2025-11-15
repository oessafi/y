# E-Voting System (Microservices)

Architecture microservices simple pour un système de vote électronique.

Services:
- `eureka-server` : Service de découverte (port 8761)
- `api-gateway` : Spring Cloud Gateway (port 8080)
- `voter-service` : Gestion des électeurs (port 9101) — exposé via Spring Data REST `/api/voters`
- `vote-service` : Enregistrement des votes (port 9102) — utilise Feign pour valider électeurs
- `result-service` : Agrégation des résultats (port 9103) — récupère les votes depuis `vote-service`

Stack technique
- Java 17
- Spring Boot 3.5.7
- Spring Cloud 2025.0.0
- Maven
- Eureka, Spring Cloud Gateway, OpenFeign, Spring Data REST, JPA, H2, Lombok

Démarrage (ordre recommandé)
1. Démarrer `eureka-server` (port 8761)
2. Démarrer `api-gateway` (port 8080)
3. Démarrer `voter-service` (port 9101)
4. Démarrer `vote-service` (port 9102)
5. Démarrer `result-service` (port 9103)

Exemples de commandes Maven (PowerShell):

```powershell
cd .\eureka-server
mvn spring-boot:run

cd ..\api-gateway
mvn spring-boot:run

cd ..\voter-service
mvn spring-boot:run

cd ..\vote-service
mvn spring-boot:run

cd ..\result-service
mvn spring-boot:run
```

Exemples REST
- Lister les électeurs (exposé via Spring Data REST):
  GET http://localhost:9101/api/voters

- Recherche par CIN (Spring Data REST search):
  GET http://localhost:9101/api/voters/search/findByCin?cin=CIN12345

- Soumettre un vote (vote-service) :
  POST http://localhost:9102/votes
  Body (JSON): { "cin": "CIN12345", "candidate": "Candidate A" }

  - Le `vote-service` vérifie que l'électeur existe et n'a pas déjà voté.
  - Si OK, il enregistre le vote et demande à `voter-service` de patcher `hasVoted=true`.

- Obtenir la liste des votes :
  GET http://localhost:9102/votes

- Obtenir les résultats agrégés :
  GET http://localhost:9103/results
  Response: [{ "candidate": "Candidate A", "totalVotes": 5 }, ...]

Via API Gateway (si démarré) :
- GET http://localhost:8080/voter-service/api/voters
- POST http://localhost:8080/vote-service/votes
- GET http://localhost:8080/result-service/results

Remarques & améliorations possibles
- Authentification / Authorisation (JWT, OAuth2)
- Persistance durable (MySQL/Postgres) et migration Flyway
- Gestion des erreurs réseau et retries pour Feign
- Tests d'intégration pour workflows (voter -> vote -> result)

Fichiers créés
- `eureka-server/` (pom, Application, application.yml)
- `api-gateway/` (pom, Application, application.yml)
- `voter-service/` (pom, Application, entity, repository, application.yml, data.sql)
- `vote-service/` (pom, Application, entity, repository, Feign client, controller, application.yml)
- `result-service/` (pom, Application, Feign client, controller, application.yml)

---
Si tu veux, je peux:
- Exécuter les services ici (si tu veux que je lance `mvn spring-boot:run` pour chaque module).
- Remplacer H2 par MySQL et ajouter `application-mysql.yml` et `README` mis à jour.
- Ajouter sécurité (JWT) ou un front minimal.
