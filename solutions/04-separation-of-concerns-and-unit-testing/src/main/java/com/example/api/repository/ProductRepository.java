package com.example.api.repository;

import com.example.api.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductRepository {

    private final Map<Integer, Product> store = new ConcurrentHashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    public ProductRepository() {
        store.put(1, new Product(1, "Laptop", 999.99));
        store.put(2, new Product(2, "Mouse", 29.99));
        nextId.set(3);
    }

    public List<Product> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<Product> findById(int id) {
        return Optional.ofNullable(store.get(id));
    }

    public Product save(Product product) {
        if (product.getId() == 0) {
            product.setId(nextId.getAndIncrement());
        }
        store.put(product.getId(), product);
        return product;
    }

    public boolean deleteById(int id) {
        return store.remove(id) != null;
    }
}
