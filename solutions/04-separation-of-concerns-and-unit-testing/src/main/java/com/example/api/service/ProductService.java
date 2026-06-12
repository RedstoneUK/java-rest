package com.example.api.service;

import com.example.api.model.Product;
import com.example.api.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    public Optional<Product> getProduct(int id) {
        return repository.findById(id);
    }

    public Product createProduct(Product product) {
        return repository.save(product);
    }

    public Optional<Product> updateProduct(int id, Product product) {
        return repository.findById(id).map(existing -> {
            product.setId(id);
            return repository.save(product);
        });
    }

    public boolean deleteProduct(int id) {
        return repository.deleteById(id);
    }
}
