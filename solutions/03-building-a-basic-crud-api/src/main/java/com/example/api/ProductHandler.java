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

        if ("GET".equals(method) && path.equals("/api/products")) {
            handleGetAll(exchange);
        } else if ("GET".equals(method) && path.matches("/api/products/\\d+")) {
            handleGetOne(exchange, extractId(path));
        } else if ("POST".equals(method) && path.equals("/api/products")) {
            handleCreate(exchange);
        } else if ("PUT".equals(method) && path.matches("/api/products/\\d+")) {
            handleUpdate(exchange, extractId(path));
        } else if ("DELETE".equals(method) && path.matches("/api/products/\\d+")) {
            handleDelete(exchange, extractId(path));
        } else {
            sendResponse(exchange, 404, "{\"error\":\"Not found\"}");
        }
    }

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

    private void handleCreate(HttpExchange exchange) throws IOException {
        String body     = new String(exchange.getRequestBody().readAllBytes());
        Product product = gson.fromJson(body, Product.class);
        product.setId(nextId.getAndIncrement());
        store.put(product.getId(), product);
        sendResponse(exchange, 201, gson.toJson(product));
    }

    private void handleUpdate(HttpExchange exchange, int id) throws IOException {
        if (!store.containsKey(id)) {
            sendResponse(exchange, 404, "{\"error\":\"Product not found\"}");
            return;
        }
        String body     = new String(exchange.getRequestBody().readAllBytes());
        Product updated = gson.fromJson(body, Product.class);
        updated.setId(id);
        store.put(id, updated);
        sendResponse(exchange, 200, gson.toJson(updated));
    }

    private void handleDelete(HttpExchange exchange, int id) throws IOException {
        if (store.remove(id) == null) {
            sendResponse(exchange, 404, "{\"error\":\"Product not found\"}");
        } else {
            sendResponse(exchange, 204, "");
        }
    }

    private int extractId(String path) {
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
