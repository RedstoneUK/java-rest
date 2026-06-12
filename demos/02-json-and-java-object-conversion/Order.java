package com.example.demo;

public class Order {

    private int orderId;
    private Product product;
    private String status;

    public Order(int orderId, Product product, String status) {
        this.orderId = orderId;
        this.product = product;
        this.status = status;
    }

    public int getOrderId()     { return orderId; }
    public Product getProduct() { return product; }
    public String getStatus()   { return status; }
}
