# Demo: Module 03 — Building a Basic CRUD API with Java SE

**Duration:** 12 minutes  
**Prerequisite:** IDE open with a Gradle project containing the Gson dependency

---

## Part 1: Starting the HttpServer (3 min)

Show the class how few lines it takes to get an HTTP server running.

```java
HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
server.createContext("/api/products", new ProductHandler());
server.setExecutor(null);
server.start();
System.out.println("Server running on port 8080");
```

**Narration:** "This is the entire server bootstrap. `HttpServer` is in the JDK — no
framework, no dependency. `createContext` maps a URI path to a handler class.
`setExecutor(null)` uses the default single-threaded executor — fine for learning,
and we can swap it later."

Run the server and hit it with curl:

```bash
curl -s http://localhost:8080/api/products
```

---

## Part 2: Routing Inside a Handler (4 min)

Open `ProductHandler.java`. Walk through the `handle` method.

```java
@Override
public void handle(HttpExchange exchange) throws IOException {
    String method = exchange.getRequestMethod();
    String path   = exchange.getRequestURI().getPath();

    if ("GET".equals(method) && path.equals("/api/products")) {
        handleGetAll(exchange);
    } else if ("GET".equals(method) && path.matches("/api/products/\\d+")) {
        int id = extractId(path);
        handleGetOne(exchange, id);
    } else if ("POST".equals(method)) {
        handleCreate(exchange);
    } else if ("PUT".equals(method) && path.matches("/api/products/\\d+")) {
        handleUpdate(exchange, extractId(path));
    } else if ("DELETE".equals(method) && path.matches("/api/products/\\d+")) {
        handleDelete(exchange, extractId(path));
    } else {
        sendResponse(exchange, 404, "{\"error\":\"Not found\"}");
    }
}
```

**Narration:** "We switch on both method and path. This is the routing layer — in a
framework like Spring this would be annotations, but here it's explicit Java.
It's more verbose but entirely transparent."

---

## Part 3: Implementing GET All and GET by ID (3 min)

```java
private void handleGetAll(HttpExchange exchange) throws IOException {
    List<Product> products = new ArrayList<>(store.values());
    sendResponse(exchange, 200, gson.toJson(products));
}

private void handleGetOne(HttpExchange exchange, int id) throws IOException {
    Product product = store.get(id);
    if (product == null) {
        sendResponse(exchange, 404, "{\"error\":\"Product not found\"}");
    } else {
        sendResponse(exchange, 200, gson.toJson(product));
    }
}
```

Demonstrate with curl:

```bash
curl -s http://localhost:8080/api/products
curl -s http://localhost:8080/api/products/1
curl -s http://localhost:8080/api/products/99
```

---

## Part 4: Creating a Resource with POST (2 min)

```java
private void handleCreate(HttpExchange exchange) throws IOException {
    String body    = new String(exchange.getRequestBody().readAllBytes());
    Product product = gson.fromJson(body, Product.class);
    product.setId(nextId++);
    store.put(product.getId(), product);
    sendResponse(exchange, 201, gson.toJson(product));
}
```

```bash
curl -s -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Keyboard","price":79.99}'
```

**Narration:** "We read the request body as a string, deserialise with Gson, assign the
next ID, store it, and return 201 Created with the saved resource."

---

## Key message

A REST API in plain Java SE is just an HttpServer, a handler that switches on method
and path, and Gson for JSON. Every framework you'll ever use is built on exactly these
primitives.
