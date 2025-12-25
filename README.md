Explanation: Replace empty README.md with a concise, correct minimal README for the LiveMenu project. The README includes project overview, tech stack, prerequisites, quick start for backend and frontend, Docker-compose instructions, build/test commands, configuration pointers, and where to find main entrypoints.

# LiveMenu

Minimal notes to build, run and contribute to LiveMenu — a small menu-management app (backend: Java + Spring Boot, frontend: Angular).

## Tech stack
- Backend: Java 17+, Spring Boot, Maven
- Frontend: Angular, TypeScript, npm
- Database: configurable via Spring Datasource (in `application.yml`)
- Optional: Docker / Docker Compose

## Prerequisites
- Java 17 or newer
- Maven 3.6+ (the backend includes the Maven wrapper `mvnw`)
- Node 18+ and npm
- (Optional) Docker & Docker Compose

## Quick start

1) Run backend (from project root):

```bash
cd backend/server
# using the included Maven wrapper
./mvnw spring-boot:run
# or with a locally installed maven
mvn spring-boot:run
```

- Default backend port: 8080
- Main Spring Boot application: `backend/server/src/main/java/com/flykraft/livemenu/LiveMenuApplication.java`

2) Run frontend (from project root):

```bash
cd frontend
npm install
npm start
```

- Default frontend port: 4200
- The frontend expects the backend API; change the API base URL in the Angular environment or proxy settings if necessary.

3) Run both with Docker Compose (from project root):

```bash
docker-compose up --build
```

This will build and start the services defined in `docker-compose.yml` (if configured).

## Build
- Build backend jar:

```bash
cd backend/server
./mvnw clean package
```

- Build frontend for production:

```bash
cd frontend
npm install
npm run build
```

## Tests
- Backend tests:

```bash
cd backend/server
./mvnw test
```

- Frontend tests:

```bash
cd frontend
npm test
```

## Configuration
- Backend Spring configuration: `backend/server/src/main/resources/application.yml` (also `data.sql` is present for sample data).
- Configure the database and other properties via `application.yml` or environment variables (common Spring keys: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`).

## Project layout (important paths)
- backend/server - Java/Spring Boot app (Maven project)
- backend/server/src/main/java/com/flykraft/livemenu - Java source, main app class
- backend/server/src/main/resources - `application.yml`, `data.sql`, static/templates
- frontend - Angular app (npm)

## Contributing
- Use feature branches and open PRs against `main`.
- Run backend and frontend tests locally before submitting a PR.

## License
- Add a `LICENSE` file to this repository if you want to specify a license.


