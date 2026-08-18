package com.example.api;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class App {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        // TODO: create an HttpServer on port 8080

        server.createContext("/api/products", new ProductHandler());
        // TODO: register ProductHandler at /api/products


        server.setExecutor(null);
        // TODO: set executor to null and start the server

        server.start();

        System.out.println("Server running on port 8080");
        // TODO: print a startup message
    }
}
