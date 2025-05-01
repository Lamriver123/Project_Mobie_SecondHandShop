package com.example.marketplacesecondhand.models;

public class Order {
    private String shopName, productName, variation, status, imageUrl;
    private int quantity;
    private double price, total;

    public Order(String shopName, String productName, String variation, String status, String imageUrl, int quantity, double price, double total) {
        this.shopName = shopName;
        this.productName = productName;
        this.variation = variation;
        this.status = status;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getVariation() {
        return variation;
    }

    public void setVariation(String variation) {
        this.variation = variation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    // Constructor + Getters + Setters
}

