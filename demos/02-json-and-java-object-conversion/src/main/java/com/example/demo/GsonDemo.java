package com.example.demo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.List;

public class GsonDemo {

    public static void main(String[] args) {
        Gson gson = new Gson();

        // --- Serialisation ---
        Product product = new Product(1, "Laptop", 999.99, true);
        String json = gson.toJson(product);
        System.out.println("Serialised: " + json);

        // --- Deserialisation ---
        String incoming = "{\"id\":2,\"name\":\"Mouse\",\"price\":29.99,\"inStock\":false}";
        Product parsed = gson.fromJson(incoming, Product.class);
        System.out.println("Parsed: " + parsed.getName() + " costs " + parsed.getPrice());

        // --- List serialisation ---
        List<Product> products = List.of(
            new Product(1, "Laptop", 999.99, true),
            new Product(2, "Mouse", 29.99, true)
        );
        System.out.println("List: " + gson.toJson(products));

        // --- Nested object ---
        Order order = new Order(101, products.get(0), "PENDING");
        System.out.println("Order: " + gson.toJson(order));

        // --- Null field omission ---
        Product noName = new Product(3, null, 0.0, false);
        System.out.println("Null field: " + gson.toJson(noName));

        // --- Pretty printing ---
        Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println("Pretty:\n" + prettyGson.toJson(product));
    }
}

