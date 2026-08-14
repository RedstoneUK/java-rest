package com.example.api;

import com.example.api.config.DatabaseConfig;
import com.example.api.handler.ProductHandler;
import com.example.api.repository.ProductRepository;
import com.example.api.service.ProductService;
import com.sun.net.httpserver.HttpServer;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.InetSocketAddress;

public class App {

    public static void main(String[] args) throws IOException {
        DataSource        dataSource = DatabaseConfig.createDataSource();
        ProductRepository repository = new ProductRepository(dataSource);
        ProductService    service    = new ProductService(repository);
        ProductHandler    handler    = new ProductHandler(service);

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/products", handler);
        server.start();
        System.out.println("Server started on port 8080");
    }
}
