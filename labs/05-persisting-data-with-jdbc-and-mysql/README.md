# Lab 05 — Persisting Data with JDBC and MySQL

**Module:** Persisting Data with JDBC and MySQL  
**Duration:** 75 minutes  
**Tools:** Java 17, Gradle, MySQL, JDBC, HikariCP, IDE

---

## Objectives

By the end of this lab you will be able to:

- Create a MySQL database schema for the products table
- Configure HikariCP to manage a pool of JDBC connections
- Implement all CRUD operations using PreparedStatement
- Map ResultSet rows to Java objects
- Replace the in-memory repository with the JDBC implementation without changing the service or handler layers

---

## Prerequisites

MySQL must be running. Start it with Docker if you do not have a local install:

```bash
docker run --name mysql-lab \
  -e MYSQL_ROOT_PASSWORD=password \
  -e MYSQL_DATABASE=products_db \
  -p 3306:3306 \
  -d mysql:8.3
```

Wait a few seconds for MySQL to initialise, then verify the connection:

```bash
mysql -u root -ppassword -h 127.0.0.1 -e "SHOW DATABASES;"
```

---

## Setup

Start from your Module 04 project. Add the two new dependencies to `build.gradle`:

```groovy
implementation 'com.zaxxer:HikariCP:5.1.0'
implementation 'com.mysql:mysql-connector-j:8.3.0'
```

---

## Exercise 1 — Create the Database Schema (10 min)

Create a file called `schema.sql` at the root of your project:

```sql
CREATE DATABASE IF NOT EXISTS products_db;
USE products_db;

CREATE TABLE IF NOT EXISTS products (
    id    INT          AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(255) NOT NULL,
    price DOUBLE       NOT NULL
);

INSERT INTO products (name, price) VALUES ('Laptop', 999.99);
INSERT INTO products (name, price) VALUES ('Mouse',   29.99);
```

Run it against your MySQL instance:

```bash
mysql -u root -ppassword -h 127.0.0.1 < schema.sql
```

Verify the rows exist:

```bash
mysql -u root -ppassword -h 127.0.0.1 products_db -e "SELECT * FROM products;"
```

---

## Exercise 2 — Create DatabaseConfig (10 min)

Create `src/main/java/com/example/api/config/DatabaseConfig.java`.

The class must expose one static method `createDataSource()` that returns a `javax.sql.DataSource`.

Requirements:
- Read `DB_URL`, `DB_USER`, and `DB_PASSWORD` from environment variables, with sensible defaults
- Use `HikariConfig` and `HikariDataSource`
- Set `maximumPoolSize` to 10

Default values for local development:
- URL: `jdbc:mysql://localhost:3306/products_db`
- User: `root`
- Password: `password`

---

## Exercise 3 — Replace ProductRepository with a JDBC Implementation (40 min)

Replace the contents of `src/main/java/com/example/api/repository/ProductRepository.java` with a JDBC-backed implementation. The constructor must accept a `DataSource`.

Implement each method using `try-with-resources` to ensure connections are always returned to the pool.

### findAll

```sql
SELECT id, name, price FROM products
```

Return a `List<Product>` built by iterating the `ResultSet`.

### findById

```sql
SELECT id, name, price FROM products WHERE id = ?
```

Use `stmt.setInt(1, id)` to bind the parameter. Return `Optional.of(product)` if a row is found, `Optional.empty()` otherwise.

### insert (called from save when id == 0)

```sql
INSERT INTO products (name, price) VALUES (?, ?)
```

Use `Statement.RETURN_GENERATED_KEYS` and retrieve the generated id with `stmt.getGeneratedKeys()`. Set the id on the product object and return it.

### update (called from save when id != 0)

```sql
UPDATE products SET name = ?, price = ? WHERE id = ?
```

### deleteById

```sql
DELETE FROM products WHERE id = ?
```

Return `stmt.executeUpdate() > 0`.

### save method

```java
public Product save(Product product) {
    return product.getId() == 0 ? insert(product) : update(product);
}
```

### mapRow helper

Extract the row-mapping logic into a private `mapRow(ResultSet rs)` method to avoid repeating column names in multiple places.

### SQLException handling

Wrap all `SQLException` instances in `RuntimeException` before re-throwing:

```java
} catch (SQLException e) {
    throw new RuntimeException("Failed to fetch products", e);
}
```

---

## Exercise 4 — Update App.java (10 min)

Update `src/main/java/com/example/api/App.java` to wire a `DataSource` into the repository:

```java
DataSource        dataSource = DatabaseConfig.createDataSource();
ProductRepository repository = new ProductRepository(dataSource);
ProductService    service    = new ProductService(repository);
ProductHandler    handler    = new ProductHandler(service);
```

The `ProductService` and `ProductHandler` classes do not need any changes.

---

## Exercise 5 — Test All Endpoints (5 min)

Start the application and verify all five endpoints against the database:

```bash
# List all products (should return the seeded rows)
curl http://localhost:8080/api/products

# Get a single product
curl http://localhost:8080/api/products/1

# Create a new product
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Monitor","price":399.99}'

# Update a product
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Laptop Pro","price":1299.99}'

# Delete a product
curl -X DELETE http://localhost:8080/api/products/2

# Confirm it is gone
curl http://localhost:8080/api/products/2
```

Restart the server and repeat `GET /api/products` — the data should persist between restarts.

---

## Acceptance Criteria

- [ ] `schema.sql` creates the `products_db` database and `products` table
- [ ] `DatabaseConfig.createDataSource()` returns a HikariCP-backed `DataSource`
- [ ] `ProductRepository` constructor accepts a `DataSource`; no `ConcurrentHashMap` or `AtomicInteger`
- [ ] All four CRUD methods use `PreparedStatement` with `?` placeholders; no string concatenation in SQL
- [ ] All `Connection`, `PreparedStatement`, and `ResultSet` objects are closed via `try-with-resources`
- [ ] `ProductService` and `ProductHandler` are unchanged
- [ ] Data persists across server restarts
- [ ] All five curl endpoints return correct responses
