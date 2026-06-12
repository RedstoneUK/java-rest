# Lab 07 — Building and Deploying with Gradle and Docker

**Module:** Building and Deploying with Gradle and Docker  
**Duration:** 60 minutes  
**Tools:** Java 17, Gradle, Docker Desktop, curl

---

## Objectives

By the end of this lab you will be able to:

- Configure the Shadow plugin to build a self-contained fat JAR
- Write a multi-stage Dockerfile that separates build from runtime
- Build a Docker image and run the API as a container
- Write a Docker Compose file that starts the API and MySQL together
- Pass database credentials to a container via environment variables

---

## Prerequisites

Docker Desktop must be running. Verify with:

```bash
docker --version
docker compose version
```

---

## Exercise 1 — Build a Fat JAR with the Shadow Plugin (15 min)

### 1a. Add the Shadow plugin

Open `build.gradle` and add the Shadow plugin alongside the existing ones:

```groovy
plugins {
    id 'java'
    id 'application'
    id 'com.github.johnrengelman.shadow' version '8.1.1'
}
```

Add a `shadowJar` configuration block at the end of `build.gradle`:

```groovy
shadowJar {
    archiveBaseName.set('products-api')
    archiveVersion.set('1.0')
    archiveClassifier.set('')
}
```

### 1b. Build and test the fat JAR

```bash
./gradlew shadowJar
ls -lh build/libs/
```

You should see `products-api-1.0.jar`. Run it (with your local MySQL running):

```bash
java -jar build/libs/products-api-1.0.jar
curl http://localhost:8080/api/products
```

Confirm the server starts and the endpoint responds.

---

## Exercise 2 — Write the Dockerfile (20 min)

Create a file called `Dockerfile` at the root of your project.

The Dockerfile must use a **two-stage build**:

### Stage 1: Build

```dockerfile
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY . .
RUN ./gradlew shadowJar --no-daemon
```

### Stage 2: Runtime

```dockerfile
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/products-api-1.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Also create `.dockerignore` at the project root to prevent unnecessary files from being sent to the Docker build context:

```
.gradle/
build/
.git/
*.class
*.jar
```

### Build and run the image

```bash
docker build -t products-api .
docker images products-api
```

Run the container, connecting to your local MySQL. On Mac/Windows, use `host.docker.internal`:

```bash
docker run -p 8080:8080 \
  -e DB_URL=jdbc:mysql://host.docker.internal:3306/products_db \
  -e DB_USER=root \
  -e DB_PASSWORD=password \
  products-api
```

Test with curl:

```bash
curl http://localhost:8080/api/products
curl http://localhost:8080/api/products/1
```

Stop the container with `Ctrl+C` or `docker stop`.

---

## Exercise 3 — Write a Docker Compose File (25 min)

Create `docker-compose.yml` at the root of your project:

```yaml
services:
  db:
    image: mysql:8.3
    environment:
      MYSQL_ROOT_PASSWORD: password
      MYSQL_DATABASE: products_db
    ports:
      - "3306:3306"
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-ppassword"]
      interval: 10s
      timeout: 5s
      retries: 5

  api:
    build: .
    ports:
      - "8080:8080"
    environment:
      DB_URL: jdbc:mysql://db:3306/products_db
      DB_USER: root
      DB_PASSWORD: password
    depends_on:
      db:
        condition: service_healthy
```

Note that the `DB_URL` uses `db` as the hostname — Docker Compose puts both containers on the same network and resolves service names automatically.

The `depends_on` with `condition: service_healthy` ensures the API only starts after MySQL is ready to accept connections.

### Start the full stack

```bash
docker compose up --build
```

Wait for both containers to start (the `db` healthcheck runs a few times before it passes), then test:

```bash
curl http://localhost:8080/api/products

curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Webcam","price":89.99}'

curl http://localhost:8080/api/products
```

### Stop and clean up

```bash
docker compose down
```

---

## Acceptance Criteria

- [ ] `./gradlew shadowJar` produces `build/libs/products-api-1.0.jar`
- [ ] The fat JAR runs standalone with `java -jar` and responds to curl
- [ ] `Dockerfile` uses two `FROM` statements (build stage and runtime stage)
- [ ] Runtime image is based on `eclipse-temurin:17-jre-alpine` (not JDK)
- [ ] `docker build -t products-api .` succeeds
- [ ] Container starts and responds to `curl http://localhost:8080/api/products`
- [ ] `docker compose up --build` starts both the API and MySQL containers
- [ ] API container uses the `db` service name in `DB_URL`
- [ ] `depends_on` uses `condition: service_healthy`
- [ ] All five curl endpoints respond correctly via Docker Compose
