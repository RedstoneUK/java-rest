package com.example.api.handler;

import com.example.api.exception.ApiException;
import com.example.api.model.ErrorResponse;
import com.example.api.model.Product;
import com.example.api.service.ProductService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

public class ProductHandler implements HttpHandler {

    private static final Logger LOGGER = Logger.getLogger(ProductHandler.class.getName());

    private final Gson           gson = new Gson();
    private final ProductService service;

    public ProductHandler(ProductService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path   = exchange.getRequestURI().getPath();
        LOGGER.info(() -> method + " " + path);

        try {
            route(exchange, method, path);
        } catch (ApiException e) {
            LOGGER.warning(() -> "API error " + e.getStatusCode() + ": " + e.getMessage());
            sendError(exchange, e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            LOGGER.severe(() -> "Unexpected error: " + e.getMessage());
            sendError(exchange, 500, "Internal server error");
        }
    }

    private void route(HttpExchange exchange, String method, String path) throws IOException {
        if ("GET".equals(method) && path.equals("/api/products")) {
            sendResponse(exchange, 200, gson.toJson(service.getAllProducts()));

        } else if ("GET".equals(method) && path.matches("/api/products/\\d+")) {
            int     id      = extractId(path);
            Product product = service.getProduct(id)
                .orElseThrow(() -> new ApiException(404, "Product " + id + " not found"));
            sendResponse(exchange, 200, gson.toJson(product));

        } else if ("POST".equals(method) && path.equals("/api/products")) {
            Product incoming = readBody(exchange, Product.class);
            if (incoming.getName() == null || incoming.getName().isBlank()) {
                throw new ApiException(400, "Product name is required");
            }
            if (incoming.getPrice() < 0) {
                throw new ApiException(400, "Price must not be negative");
            }
            Product created = service.createProduct(incoming);
            LOGGER.info(() -> "Created product id=" + created.getId());
            sendResponse(exchange, 201, gson.toJson(created));

        } else if ("PUT".equals(method) && path.matches("/api/products/\\d+")) {
            int     id      = extractId(path);
            Product incoming = readBody(exchange, Product.class);
            Product updated = service.updateProduct(id, incoming)
                .orElseThrow(() -> new ApiException(404, "Product " + id + " not found"));
            sendResponse(exchange, 200, gson.toJson(updated));

        } else if ("DELETE".equals(method) && path.matches("/api/products/\\d+")) {
            int id = extractId(path);
            if (!service.deleteProduct(id)) {
                throw new ApiException(404, "Product " + id + " not found");
            }
            LOGGER.info(() -> "Deleted product id=" + id);
            sendResponse(exchange, 204, "");

        } else {
            throw new ApiException(404, "No route: " + method + " " + path);
        }
    }

    private void sendError(HttpExchange exchange, int status, String message) throws IOException {
        sendResponse(exchange, status, gson.toJson(new ErrorResponse(status, message)));
    }

    private <T> T readBody(HttpExchange exchange, Class<T> type) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes());
        return gson.fromJson(body, type);
    }

    private int extractId(String path) {
        String[] parts = path.split("/");
        return Integer.parseInt(parts[parts.length - 1]);
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
