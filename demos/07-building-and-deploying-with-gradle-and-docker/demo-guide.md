# Demo: Module 07 — Building and Deploying with Gradle and Docker

**Duration:** 12 minutes  
**Prerequisite:** Docker Desktop running, Module 06 project compiling

---

## Part 1: The Problem with a Standard JAR (1 min)

Run `./gradlew jar` and show the output JAR:

```bash
ls -lh build/libs/
java -jar build/libs/*.jar
```

It fails — the dependencies (Gson, HikariCP, MySQL connector) are not included. A standard JAR only contains the project's own compiled classes.

**Narration:** To run anywhere without a classpath setup, we need a fat JAR — one file that contains both our code and every dependency.

---

## Part 2: Shadow Plugin for a Fat JAR (3 min)

Open `build.gradle`. Show the additions:

```groovy
id 'com.github.johnrengelman.shadow' version '8.1.1'

shadowJar {
    archiveBaseName.set('products-api')
    archiveVersion.set('1.0')
    archiveClassifier.set('')
}
```

Build the fat JAR:

```bash
./gradlew shadowJar
ls -lh build/libs/
java -jar build/libs/products-api-1.0.jar
```

Show it starts and the endpoints respond. The JAR is fully self-contained.

**Narration:** The Shadow plugin merges all dependency JARs into one. We give it a fixed name so the Dockerfile can reference it predictably — no version wildcards.

---

## Part 3: The Dockerfile — Multi-Stage Build (4 min)

Open `Dockerfile` and walk through it section by section:

**Build stage:**
```dockerfile
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY . .
RUN ./gradlew shadowJar --no-daemon
```

- `eclipse-temurin:17-jdk-alpine` — JDK image on Alpine Linux; small footprint
- `COPY . .` — copies the project into the container
- `--no-daemon` — Gradle daemon is pointless in a container; saves memory

**Runtime stage:**
```dockerfile
FROM eclipse-temurin:17-jre-alpine
COPY --from=build /app/build/libs/products-api-1.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

- Second `FROM` starts a fresh layer — the JDK and Gradle are not in the final image
- Only the JAR is copied from the build stage
- `EXPOSE` is documentation; the actual port binding happens at `docker run`
- `ENTRYPOINT` as a JSON array avoids shell wrapping — signals reach the JVM directly

**Narration:** Multi-stage builds keep the final image small. The JDK plus Gradle is several hundred MB. The runtime image with just the JRE and our JAR is around 80-90 MB. Smaller images pull faster, scan faster, and have a smaller attack surface.

---

## Part 4: Build and Run the Container (2 min)

```bash
docker build -t products-api .
docker images products-api
docker run -p 8080:8080 \
  -e DB_URL=jdbc:mysql://host.docker.internal:3306/products_db \
  -e DB_USER=root \
  -e DB_PASSWORD=password \
  products-api
```

Test with curl from another terminal:

```bash
curl http://localhost:8080/api/products
```

**Narration:** `host.docker.internal` resolves to the Docker host's IP on Mac and Windows. On Linux, use `--network=host` or create a Docker network.

---

## Part 5: Docker Compose for the Full Stack (2 min)

Open `docker-compose.yml`. Show:

- `db` service: MySQL 8.3 with a healthcheck
- `api` service: builds from the Dockerfile; `depends_on` with `condition: service_healthy`
- Environment variables pass DB credentials to the container — no hard-coded values in code

```bash
docker compose up --build
```

Wait for both containers to start, then test:

```bash
curl http://localhost:8080/api/products
```

Bring it down:

```bash
docker compose down
```

**Narration:** `depends_on` with `service_healthy` ensures the API container only starts after MySQL is ready to accept connections. Without this, the API starts, tries to connect, fails, and crashes before MySQL finishes initialising.

---

## Key message

The Shadow plugin creates a single deployable JAR. A multi-stage Dockerfile keeps the runtime image small. Docker Compose wires the API and database together for consistent, repeatable deployments with no manual setup.
