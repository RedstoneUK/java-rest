package com.example.api;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class App {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/products", new ProductHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server running on http://localhost:8080");
    }
}
