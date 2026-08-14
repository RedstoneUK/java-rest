package com.example.api.handler;

import com.example.api.model.Product;
import com.example.api.service.ProductService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

public class ProductHandler implements HttpHandler {

    private final Gson gson = new Gson();
    private final ProductService service;

    public ProductHandler(ProductService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path   = exchange.getRequestURI().getPath();

        if ("GET".equals(method) && path.equals("/api/products")) {
            sendResponse(exchange, 200, gson.toJson(service.getAllProducts()));
        } else if ("GET".equals(method) && path.matches("/api/products/\\d+")) {
            int id = extractId(path);
            service.getProduct(id)
                .ifPresentOrElse(
                    p -> sendResponseUnchecked(exchange, 200, gson.toJson(p)),
                    () -> sendResponseUnchecked(exchange, 404, "{\"error\":\"Not found\"}")
                );
        } else if ("POST".equals(method) && path.equals("/api/products")) {
            Product incoming = readBody(exchange, Product.class);
            sendResponse(exchange, 201, gson.toJson(service.createProduct(incoming)));
        } else if ("PUT".equals(method) && path.matches("/api/products/\\d+")) {
            int id = extractId(path);
            Product incoming = readBody(exchange, Product.class);
            Optional<Product> updated = service.updateProduct(id, incoming);
            updated.ifPresentOrElse(
                p -> sendResponseUnchecked(exchange, 200, gson.toJson(p)),
                () -> sendResponseUnchecked(exchange, 404, "{\"error\":\"Not found\"}")
            );
        } else if ("DELETE".equals(method) && path.matches("/api/products/\\d+")) {
            int id = extractId(path);
            if (service.deleteProduct(id)) {
                sendResponse(exchange, 204, "");
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Not found\"}");
            }
        } else {
            sendResponse(exchange, 404, "{\"error\":\"Not found\"}");
        }
    }

    private <T> T readBody(HttpExchange exchange, Class<T> type) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes());
        return gson.fromJson(body, type);
    }

    private int extractId(String path) {
        String[] parts = path.split("/");
        return Integer.parseInt(parts[parts.length - 1]);
    }

    private void sendResponseUnchecked(HttpExchange exchange, int status, String body) {
        try { sendResponse(exchange, status, body); } catch (IOException e) { throw new RuntimeException(e); }
    }

    private void sendResponse(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes();
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length == 0 ? -1 : bytes.length);
        if (bytes.length > 0) {
            try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
        } else {
            exchange.getResponseBody().close();
        }
    }
}
