# Lab 06 — HTTP Status Codes, Exception Handling, and Logging

**Module:** HTTP Status Codes, Exception Handling, and Logging  
**Duration:** 60 minutes  
**Tools:** Java 17, Gradle, curl, IDE

---

## Objectives

By the end of this lab you will be able to:

- Apply correct HTTP status codes for each CRUD operation and error condition
- Design a consistent JSON error response format
- Create a custom exception that carries an HTTP status code
- Implement centralised exception handling in a single try/catch block
- Add structured logging with java.util.logging at appropriate levels

---

## Setup

Continue from your Module 05 project. No new dependencies are required — `java.util.logging` is part of the JDK.

---

## Exercise 1 — Create a Consistent Error Response (10 min)

Create `src/main/java/com/example/api/model/ErrorResponse.java`.

The class needs two fields:

| Field | Type | Description |
|-------|------|-------------|
| `status` | `int` | The HTTP status code |
| `error` | `String` | A human-readable message |

Both fields should be `final`, set in the constructor, with getters so Gson can serialise them.

---

## Exercise 2 — Create ApiException (10 min)

Create `src/main/java/com/example/api/exception/ApiException.java`.

Requirements:
- Extends `RuntimeException`
- Constructor accepts `int statusCode` and `String message`
- Exposes `getStatusCode()` method

This exception will be thrown anywhere in the routing logic when a client error occurs (400, 404, etc.).

---

## Exercise 3 — Centralised Exception Handling (20 min)

Refactor `ProductHandler` so that:

1. The `handle()` method contains a single `try/catch` block:
   ```java
   try {
       route(exchange, method, path);
   } catch (ApiException e) {
       sendError(exchange, e.getStatusCode(), e.getMessage());
   } catch (Exception e) {
       sendError(exchange, 500, "Internal server error");
   }
   ```

2. Extract routing logic into a private `route(HttpExchange, String, String)` method.

3. Replace `ifPresentOrElse` with `orElseThrow` for the 404 cases:
   ```java
   Product product = service.getProduct(id)
       .orElseThrow(() -> new ApiException(404, "Product " + id + " not found"));
   ```

4. Add input validation to the POST handler — throw `ApiException(400, ...)` if:
   - `name` is null or blank
   - `price` is negative

5. Add a `sendError` helper:
   ```java
   private void sendError(HttpExchange exchange, int status, String message) throws IOException {
       sendResponse(exchange, status, gson.toJson(new ErrorResponse(status, message)));
   }
   ```

6. Return the correct status codes:
   - `POST` success: **201**
   - `DELETE` success: **204** (no body)
   - `GET`/`PUT` success: **200**
   - Not found: **404**
   - Invalid input: **400**
   - Unexpected exception: **500**

---

## Exercise 4 — Add Logging (20 min)

Add a logger to `ProductHandler`:

```java
private static final Logger LOGGER = Logger.getLogger(ProductHandler.class.getName());
```

Add log statements at appropriate levels:

| Where | Level | What to log |
|-------|-------|-------------|
| Start of `handle()` | `INFO` | HTTP method and path |
| After successful POST | `INFO` | Created product id |
| After successful DELETE | `INFO` | Deleted product id |
| In ApiException catch | `WARNING` | Status code and message |
| In generic Exception catch | `SEVERE` | Exception message |

Use the lambda form for all log statements so the string is only built if the level is enabled:

```java
LOGGER.info(() -> method + " " + path);
```

Do NOT log request bodies — they may contain sensitive data.

---

## Acceptance Criteria

- [ ] `ErrorResponse` has `status` and `error` fields serialisable by Gson
- [ ] `ApiException` extends `RuntimeException` and carries a status code
- [ ] `handle()` contains a single `try/catch` — no other try/catch in the handler
- [ ] `GET /api/products/999` returns `404` with JSON error body
- [ ] `POST` with missing name returns `400` with JSON error body
- [ ] `POST` with negative price returns `400` with JSON error body
- [ ] Successful `POST` returns `201`
- [ ] Successful `DELETE` returns `204`
- [ ] Server console shows `INFO` log for every request
- [ ] Server console shows `WARNING` log for 404 and 400 responses

### Test with curl

```bash
# 200
curl -i http://localhost:8080/api/products/1

# 404
curl -i http://localhost:8080/api/products/999

# 400 missing name
curl -i -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"price":9.99}'

# 400 negative price
curl -i -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Widget","price":-5}'

# 201
curl -i -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Monitor","price":399.99}'

# 204
curl -i -X DELETE http://localhost:8080/api/products/1
```
