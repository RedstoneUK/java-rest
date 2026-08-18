package com.example.api;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductHandler implements HttpHandler {

    private final Gson gson = new Gson();
    private final Map<Integer, Product> store = new ConcurrentHashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    public ProductHandler() {
        store.put(1, new Product(1, "Laptop", 999.99));
        store.put(2, new Product(2, "Mouse", 29.99));
        nextId.set(3);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path   = exchange.getRequestURI().getPath();

        // TODO: route to the correct handler method based on method and path
        // Hint: use path.matches("/api/products/\\d+") to detect ID-based routes
        if (method.equals("GET") && path.equals("/api/products")) {
            handleGetAll(exchange);
        } else if (method.equals("GET") && path.matches("/api/products/\\d+")) {
            handleGetOne(exchange, extractId(path));
        } else if (method.equals("POST") && path.equals("/api/products")) {
            handleCreate(exchange);
        } else {
            sendResponse(exchange, 404, "{\"error\":\"Not found\"}");
        }

    }

    private void handleGetAll(HttpExchange exchange) throws IOException {
        // TODO: serialise all products and send a 200 response
        // 1. all the values in the map, as a list
        List<Product> products = new ArrayList<>(store.values());

        // 2. turn the list into a JSON string
        String json = gson.toJson(products);

        // 3. send 200 with that JSON as the body
        sendResponse(exchange, 200, json);
    }

    private void handleGetOne(HttpExchange exchange, int id) throws IOException {
        // TODO: look up by id; send 200 with product JSON, or 404 if not found
        Product product = store.get(id);
        if (product != null) {
            sendResponse(exchange, 200, gson.toJson(product));
        } else {
            sendResponse(exchange, 404, "{\"error\":\"Product not found\"}");
        }
    }

    private void handleCreate(HttpExchange exchange) throws IOException {
        // 1. read the raw request body bytes and turn them into a String
        byte[] bytes = exchange.getRequestBody().readAllBytes();
        String body = new String(bytes);

        // 2. deserialise the JSON body into a Product
        Product product = gson.fromJson(body, Product.class);

        // 3. assign the next server-generated id
        int id = nextId.getAndIncrement();
        product.setId(id);

        // 4. store it and send 201 Created with the saved product
        store.put(id, product);
        sendResponse(exchange, 201, gson.toJson(product));
    }

    private void handleUpdate(HttpExchange exchange, int id) throws IOException {
        // TODO: return 404 if id missing; otherwise deserialise body,
        //       set id, replace in store, and send 200
    }

    private void handleDelete(HttpExchange exchange, int id) throws IOException {
        // TODO: remove from store; send 204 if removed, 404 if not found
    }

    private int extractId(String path) {
        // TODO: split path on "/" and parse the last segment as an int
        String[] parts = path.split("/");
        return Integer.parseInt(parts[parts.length - 1]);
    }

    private void sendResponse(HttpExchange exchange, int status, String body)
            throws IOException {
        byte[] bytes = body.getBytes();
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length == 0 ? -1 : bytes.length);
        if (bytes.length > 0) {
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        } else {
            exchange.getResponseBody().close();
        }
    }
}
