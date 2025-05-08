package com.example.marketplacesecondhand.dto.response;

public class OrderDetailResponse {
    private int productId;
    private String productName;
    private int quantity;
    private String price;
    private String currentImages;

    public OrderDetailResponse(int productId, String productName, int quantity, String price, String currentImages) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.currentImages = currentImages;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCurrentImages() {
        return currentImages;
    }

    public void setCurrentImages(String currentImages) {
        this.currentImages = currentImages;
    }
}
