# TalentMatch AI

API Spring Boot pour gerer des candidats, des offres d'emploi, et un matching asynchrone via Kafka + Ollama.

## 1) Instructions de demarrage (infrastructure + application)

### Prerequis
- Java 21
- Maven local
- Docker (pour l'infrastructure)

### 1.1 Demarrer l'infrastructure
```bash
docker compose up -d
```

### 1.2 Demarrer l'application
Option IDE: lancer `TalentMatchAiApplication`.

Option terminal:
```bash
java -version
mvn spring-boot:run
```

## 2) Liste des endpoints avec exemples de requetes curl

Base URL: `http://localhost:8080`

### Candidates
- `POST /api/candidate`
```bash
curl -X POST "http://localhost:8080/api/candidate" \
  -H "Content-Type: application/json" \
  -d '{"firstName":"John","lastName":"Doe","email":"john.doe@example.com","skills":["Java","Spring"],"yearsOfExperience":3,"bio":"Backend developer"}'
```

- `GET /api/candidate`
```bash
curl "http://localhost:8080/api/candidate"
```

- `POST /api/candidate/import/{username}` (Basic Auth)
```bash
curl -u recruiter:recruiter123 -X POST "http://localhost:8080/api/candidate/import/octocat"
```

### Job Offers
- `POST /api/job-offers`
```bash
curl -X POST "http://localhost:8080/api/job-offers" \
  -H "Content-Type: application/json" \
  -d '{"title":"Java Developer","company":"TechCorp","requiredSkills":["Java","Spring Boot"],"description":"Build REST APIs","location":"Paris","salaryRange":"45k-60k"}'
```

- `GET /api/job-offers`
```bash
curl "http://localhost:8080/api/job-offers"
```

### Matching
- `POST /api/matching/analyze` (Basic Auth)
```bash
curl -u recruiter:recruiter123 -X POST "http://localhost:8080/api/matching/analyze" \
  -H "Content-Type: application/json" \
  -d '{"candidateId":"<CANDIDATE_UUID>","jobOfferId":"<JOB_UUID>"}'
```

- `GET /api/matching/results`
```bash
curl "http://localhost:8080/api/matching/results"
```

### Actuator (palier 3)
```bash
curl "http://localhost:8080/actuator/health"
curl "http://localhost:8080/actuator/info"
curl "http://localhost:8080/actuator/metrics"
curl "http://localhost:8080/actuator/matchingstats"
```

## 3) Exemples de reponses attendues

### `POST /api/matching/analyze` (202)
```json
{
  "id": "0abc120c-9d34-19cd-819d-34d1092c0007",
  "status": "PENDING",
  "requestedAt": "2026-03-28T14:23:01.123",
  "message": "L'analyse a ete soumise et sera traitee prochainement"
}
```

### `GET /actuator/matchingstats` (200)
```json
{
  "total": 10,
  "pending": 2,
  "processing": 1,
  "completed": 6,
  "failed": 1,
  "completionRate": 0.6
}
```

### Erreur type (404)
```json
{
  "code": "NOT_FOUND",
  "message": "Resource not found",
  "timestamp": "2026-03-28T14:24:11.001",
  "details": null,
  "path": "/api/candidate/<id>"
}
```

## 4) Utilisateurs de test (username/password pour Basic Auth)

Comptes definis dans `src/main/resources/application.yml`:
- `recruiter` / `recruiter123`
- `admin` / `admin123`

Endpoints proteges:
- `POST /api/candidate/import/**`
- `POST /api/matching/analyze`

## 5) Choix techniques et difficultes rencontrees

### Choix techniques
- Architecture Spring Boot en couches (`controller`, `service`, `repository`)
- Traitement asynchrone du matching via Kafka (`PENDING -> PROCESSING -> COMPLETED/FAILED`)
- Securite Basic Auth ciblee sur endpoints sensibles
- Observabilite via Actuator + health custom (`kafka`, `ollama`) + endpoint custom `matchingstats`
- Gestion centralisee des erreurs via `GlobalExceptionHandler`

### Difficultes rencontrees
- Stabiliser les tests sur objets mutables (etat au moment du `save`)
- Aligner annotations/imports de test avec Spring Boot 4

## 6) Lancer les tests
```bash
mvn test
```
