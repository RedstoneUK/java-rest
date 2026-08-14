# Lab 03 — Building a Basic CRUD API with Java SE

**Module:** Building a Basic CRUD API with Java SE  
**Duration:** 60 minutes  
**Tools:** Java 17, Gradle, curl or Postman

---

## Objectives

By the end of this lab you will be able to:

- Start an HTTP server using `com.sun.net.httpserver.HttpServer`
- Route requests by HTTP method and URI path inside an `HttpHandler`
- Parse path segments and read JSON request bodies
- Implement all five CRUD operations against an in-memory store
- Return correct HTTP status codes and JSON responses

---

## Project Setup

Use the provided starter project in this folder, or create a new Gradle project:

```bash
gradle init --type java-application --dsl groovy
```

Add the Gson dependency to `build.gradle`:

```groovy
dependencies {
    implementation 'com.google.code.gson:gson:2.10.1'
}
```

Set the main class to `com.example.api.App`.

---

## The Domain

You are building a REST API for a **product catalogue**. Each product has:

| Field | Type | Notes |
|-------|------|-------|
| `id` | `int` | Assigned by the server, not the client |
| `name` | `String` | Required |
| `price` | `double` | Required |

---

## Exercise 1 — Start the HTTP Server (10 min)

Open `src/main/java/com/example/api/App.java`. Complete the `main` method to:

1. Create an `HttpServer` bound to port 8080.
2. Register a context at `/api/products` with a `ProductHandler`.
3. Set the executor to `null` (uses the default).
4. Start the server and print a confirmation message.

```java
HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
// TODO: register context and start server
```

Run the application and verify it starts without errors:

```bash
curl -s http://localhost:8080/api/products
```

---

## Exercise 2 — Implement GET All Products (10 min)

Open `src/main/java/com/example/api/ProductHandler.java`. The handler already has a `ConcurrentHashMap` store and two
seed products.

Complete `handleGetAll`:

- Retrieve all values from the store as a list.
- Serialise the list to JSON using Gson.
- Send a `200` response with the JSON body.

Test:

```bash
curl -s http://localhost:8080/api/products
```

Expected: a JSON array containing the two seed products.

---

## Exercise 3 — Implement GET by ID (10 min)

Complete `handleGetOne(HttpExchange exchange, int id)`:

- Look up the product by ID in the store.
- If found, return `200` with the product as JSON.
- If not found, return `404` with `{"error":"Product not found"}`.

Also complete `extractId(String path)` which parses the numeric ID from the last path segment.

Test:

```bash
curl -s http://localhost:8080/api/products/1
curl -s http://localhost:8080/api/products/99
```

---

## Exercise 4 — Implement POST (Create) (10 min)

Complete `handleCreate`:

1. Read the request body as a `String` using `exchange.getRequestBody().readAllBytes()`.
2. Deserialise it into a `Product` using Gson.
3. Assign the next ID using the `AtomicInteger nextId`.
4. Store the product and return `201 Created` with the saved product as JSON.

Test:

```bash
curl -s -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Keyboard","price":79.99}'
```

Expected: the created product with its assigned ID.

---

## Exercise 5 — Implement PUT (Update) and DELETE (15 min)

### PUT

Complete `handleUpdate(HttpExchange exchange, int id)`:

1. Return `404` if the ID does not exist in the store.
2. Deserialise the request body into a `Product`.
3. Set the ID on the deserialised product (the client body may not include it).
4. Replace the existing entry and return `200` with the updated product.

```bash
curl -s -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Laptop Pro","price":1299.99}'
```

### DELETE

Complete `handleDelete(HttpExchange exchange, int id)`:

1. Remove the product from the store.
2. If it existed, return `204 No Content` (empty body).
3. If not found, return `404`.

```bash
curl -s -X DELETE http://localhost:8080/api/products/2
curl -si -X DELETE http://localhost:8080/api/products/99 | head -1
```

---

## Exercise 6 — Routing (5 min)

Complete the `handle` method to route each request to the correct handler method.
Use `exchange.getRequestMethod()` for the HTTP method and
`exchange.getRequestURI().getPath()` for the path.

Use `path.matches("/api/products/\\d+")` to detect paths with a numeric ID.

Return `404` for any unrecognised combination.

---

## Acceptance Criteria

- [ ] `GET /api/products` returns all products as a JSON array with status 200
- [ ] `GET /api/products/{id}` returns one product or 404
- [ ] `POST /api/products` creates a product, assigns an ID, returns 201
- [ ] `PUT /api/products/{id}` replaces the product, returns 200 or 404
- [ ] `DELETE /api/products/{id}` removes the product, returns 204 or 404
- [ ] All responses include `Content-Type: application/json`
