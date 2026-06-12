# Demo: Module 06 — HTTP Status Codes, Exception Handling, and Logging

**Duration:** 12 minutes  
**Prerequisite:** Module 05 project running with MySQL

---

## Part 1: HTTP Status Code Review (2 min)

Show the current handler and point out inconsistencies:

- POST returns 201, but only when it succeeds — an invalid body causes a 500 or NullPointerException
- A missing product returns a hardcoded `{"error":"Not found"}` string — not machine-readable with a status field
- Unhandled exceptions reach the client as empty 500 responses with no body

**Narration:** HTTP status codes are part of the API contract. Clients rely on them to decide what to do next. If your API returns 200 for an error, you force every client to parse the body to find out what happened. A 404 means "not there"; a 400 means "your request was wrong"; a 500 means "we broke something."

---

## Part 2: Consistent Error Response Format (2 min)

Show `ErrorResponse.java`:

```java
public class ErrorResponse {
    private final int    status;
    private final String error;
}
```

**Narration:** Every error response will now have the same shape — a status code and a human-readable message. Clients can deserialise this reliably rather than trying to parse free-form text.

---

## Part 3: ApiException (2 min)

Show `ApiException.java`. Explain:

- Extends RuntimeException — no checked exception boilerplate in calling code
- Carries an HTTP status code alongside the message
- Thrown anywhere in the route logic when a client error occurs

**Narration:** ApiException is our way of signalling a client error from deep inside the routing logic. Because it is a RuntimeException, we do not need try/catch everywhere — it bubbles up to the single handler in `handle()`.

---

## Part 4: Centralised Exception Handling (3 min)

Show the `handle()` method:

```java
try {
    route(exchange, method, path);
} catch (ApiException e) {
    LOGGER.warning("API error " + e.getStatusCode() + ": " + e.getMessage());
    sendError(exchange, e.getStatusCode(), e.getMessage());
} catch (Exception e) {
    LOGGER.severe("Unexpected error: " + e.getMessage());
    sendError(exchange, 500, "Internal server error");
}
```

**Narration:** One try/catch block handles everything. ApiException becomes a structured error response with the correct status. Any other exception becomes a 500 with a generic message — we never expose internal error details to clients.

Show the route method — `orElseThrow(() -> new ApiException(404, ...))` replaces the old `ifPresentOrElse`.

Show the POST validation:

```java
if (incoming.getName() == null || incoming.getName().isBlank()) {
    throw new ApiException(400, "Product name is required");
}
```

---

## Part 5: Logging with java.util.logging (3 min)

Show the Logger declaration:

```java
private static final Logger LOGGER = Logger.getLogger(ProductHandler.class.getName());
```

Show each log level in context:

- `LOGGER.info(...)` — normal request logging
- `LOGGER.warning(...)` — expected client errors (404, 400)
- `LOGGER.severe(...)` — unexpected server errors (500)

Point out the lambda form `() -> "message"` — the string is only built if the level is enabled.

**Narration:** Logging is not about printing everything; it is about leaving a trail that helps you diagnose problems. INFO for normal traffic, WARNING for client mistakes, SEVERE for things that need immediate attention. We never log the request body because it may contain sensitive data like passwords or card numbers.

---

## Part 6: Test with curl (2 min)

```bash
# Valid request
curl http://localhost:8080/api/products/1

# 404
curl http://localhost:8080/api/products/999

# 400 — missing name
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"price":9.99}'

# 400 — negative price
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Widget","price":-1}'
```

Show the server console — each request produces a log line. Errors produce WARNING or SEVERE lines.

---

## Key message

Centralised exception handling means one place controls every error response. ApiException lets any layer signal a client error with the correct status code. java.util.logging is sufficient for structured, level-based logging without adding a dependency.
