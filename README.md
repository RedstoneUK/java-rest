# Building REST APIs with Java SE — Course Labs

This repository contains the lab exercises, demos, and solutions for the
**Building REST APIs with Java SE** course.

---

## Repository Structure

```
demos/      Instructor demo guides and supporting files (per module)
labs/       Delegate lab exercises with README and starter files (per module)
solutions/  Reference implementations for each lab (per module)
```

---

## Modules

| # | Module | Lab |
|---|--------|-----|
| 01 | [Introduction to REST APIs](labs/01-introduction-to-rest-apis/README.md) | Explore REST APIs with curl |
| 02 | [JSON and Java Object Conversion](labs/02-json-and-java-object-conversion/README.md) | Gson serialisation exercises |
| 03 | [Building a Basic CRUD API with Java SE](labs/03-building-a-basic-crud-api-with-java-se/README.md) | HttpServer + in-memory store |
| 04 | [Separation of Concerns and Unit Testing](labs/04-separation-of-concerns-and-unit-testing/README.md) | Layered refactor + JUnit/Mockito |
| 05 | [Persisting Data with JDBC and MySQL](labs/05-persisting-data-with-jdbc-and-mysql/README.md) | JDBC + HikariCP data access layer |
| 06 | [HTTP Status Codes, Exception Handling, and Logging](labs/06-http-status-codes-exception-handling-and-logging/README.md) | Error handling + logging |
| 07 | [Building and Deploying with Gradle and Docker](labs/07-building-and-deploying-with-gradle-and-docker/README.md) | Dockerfile + container run |

---

## Prerequisites

- Java 17 or later
- Gradle 8.x
- Docker Desktop (for Module 07)
- MySQL 8.x (for Modules 05 onwards)
- curl or Postman

---

## Project Approach

The course builds a single REST API project incrementally across all modules.
Each lab adds to the same codebase:

- Module 03 introduces the in-memory API
- Module 04 refactors it with layered architecture
- Module 05 replaces the in-memory store with MySQL via JDBC
- Module 06 adds proper error handling and logging
- Module 07 packages and deploys the finished application

---

## Running the Labs

Each module's lab folder contains a `README.md` with setup steps and instructions.
Start with `labs/01-introduction-to-rest-apis/README.md` and work through in order.
