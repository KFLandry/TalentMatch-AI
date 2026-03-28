# TalentMatch AI

API Spring Boot pour gerer des candidats, des offres, et un matching asynchrone (Kafka + Ollama).

## Prerequis

- Java 21
- Maven local (ou lancement depuis l'IDE)
- Docker compose pour Kafka et Ollama (fichier `docker-compose.yml` fourni)
- Kafka et Ollama accessibles

## Lancer l'application

Depuis l'IDE: lancer `TalentMatchAiApplication`.

Ou en terminal:

```powershell
java -version
mvn spring-boot:run
```

## Auth (Basic)

Routes protegees:

- `POST /api/candidate/import/**`
- `POST /api/matching/analyze`

Comptes par defaut:

- `recruiter` / `recruiter123`
- `admin` / `admin123`

Exemple:

```powershell
curl.exe -u recruiter:recruiter123 -X POST "http://localhost:8080/api/matching/analyze" -H "Content-Type: application/json" -d "{\"candidateId\":\"<UUID>\",\"jobOfferId\":\"<UUID>\"}"
```

## Actuator (palier 3)

- `/actuator/health`
- `/actuator/info`
- `/actuator/metrics`
- `/actuator/matchingstats`

## Tests

```powershell
mvn test
```
