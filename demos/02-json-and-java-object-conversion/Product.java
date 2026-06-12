package com.example.demo;

public class Product {

    private int id;
    private String name;
    private double price;
    private boolean inStock;

    public Product(int id, String name, double price, boolean inStock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.inStock = inStock;
    }

    public int getId()       { return id; }
    public String getName()  { return name; }
    public double getPrice() { return price; }
    public boolean isInStock() { return inStock; }
}
