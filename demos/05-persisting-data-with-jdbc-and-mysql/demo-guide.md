# Demo: Module 05 — Persisting Data with JDBC and MySQL

**Duration:** 12 minutes  
**Prerequisite:** MySQL running locally (or via Docker), Module 04 project compiling

---

## Part 1: Database Setup (3 min)

Start MySQL and run the schema:

```bash
mysql -u root -p < schema.sql
```

Verify the seed data:

```sql
USE products_db;
SELECT * FROM products;
```

**Narration:** The schema is simple — one table, three columns. We seed it with three products so we can immediately test GET endpoints without adding data first.

---

## Part 2: Add Dependencies (2 min)

Show `build.gradle`. Point out the two new lines:

```groovy
implementation 'com.zaxxer:HikariCP:5.1.0'
implementation 'com.mysql:mysql-connector-j:8.3.0'
```

**Narration:** The MySQL connector is the JDBC driver — it translates JDBC calls into MySQL's wire protocol. HikariCP is a connection pool; it keeps a set of open connections ready, so we do not pay the overhead of creating a new connection on every request.

---

## Part 3: DatabaseConfig and Connection Pooling (3 min)

Open `DatabaseConfig.java`. Walk through:

- Environment variables for URL, user, password (production-ready config)
- `HikariConfig` properties: `maximumPoolSize`, `connectionTimeout`
- Returns a `DataSource` — the rest of the code never knows it is a pool

**Narration:** We read credentials from environment variables rather than hard-coding them. That is standard practice; it keeps secrets out of source control and lets us change the database URL when deploying to Docker.

---

## Part 4: JDBC ProductRepository (4 min)

Open `ProductRepository.java`. Show each method in turn:

**findAll:**
- `try-with-resources` closes Connection, PreparedStatement, ResultSet automatically
- No string concatenation in SQL — the query has no parameters, so there is no injection risk

**findById:**
- `stmt.setInt(1, id)` — the `?` placeholder, not string interpolation
- Nested try-with-resources for the ResultSet inside the connection block

**insert:**
- `Statement.RETURN_GENERATED_KEYS` — asks MySQL to send back the auto-generated id
- `stmt.getGeneratedKeys()` retrieves it and sets it on the object

**mapRow:**
- Private helper keeps mapping in one place — change the column name once if the schema evolves

**Narration:** PreparedStatement does two things: it sends the query and its parameters separately, so the database treats the parameter as data, never as SQL. That is what prevents SQL injection. The `try-with-resources` block ensures every connection is returned to the pool, preventing connection leaks.

---

## Part 5: Wire and Test (2 min)

Open `App.java`. Show the wiring: `DataSource -> ProductRepository -> ProductService -> ProductHandler`.

Run the application and test with curl:

```bash
curl http://localhost:8080/api/products
curl http://localhost:8080/api/products/1
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Monitor","price":399.99}'
curl -X DELETE http://localhost:8080/api/products/1
curl http://localhost:8080/api/products/1   # expect 404
```

**Narration:** The handler and service layers are unchanged. Only the repository changed — the service still calls `findAll`, `findById`, `save`, and `deleteById`. That is the benefit of the layered architecture from Module 04; swapping the data source required no changes above the repository layer.

---

## Key message

PreparedStatement prevents SQL injection by sending SQL and parameters separately. HikariCP prevents connection leaks by pooling and recycling connections. The layered architecture means swapping in-memory storage for a real database touches only one class.
