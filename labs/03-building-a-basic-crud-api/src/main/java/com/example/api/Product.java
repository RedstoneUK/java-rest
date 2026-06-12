package com.example.api;

public class Product {

    private int id;
    private String name;
    private double price;

    public Product() {}

    public Product(int id, String name, double price) {
        this.id    = id;
        this.name  = name;
        this.price = price;
    }

    public int getId()       { return id; }
    public String getName()  { return name; }
    public double getPrice() { return price; }

    public void setId(int id)      { this.id = id; }
    public void setName(String n)  { this.name = n; }
    public void setPrice(double p) { this.price = p; }
}
