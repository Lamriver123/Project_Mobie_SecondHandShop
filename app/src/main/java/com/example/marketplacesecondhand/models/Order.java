package com.example.marketplacesecondhand.models;

import com.example.marketplacesecondhand.dto.response.ProductResponse;

import java.util.List;

public class Order {
    private String orderId;
    private String status;
    private String shopName;
    private List<ProductResponse> productList;
    private boolean isExpanded;

    public Order(String orderId, String status, String shopName, List<ProductResponse> productList) {
        this.orderId = orderId;
        this.status = status;
        this.shopName = shopName;
        this.productList = productList;
        this.isExpanded = false;
    }

    // Getters và setters
    public String getOrderId() { return orderId; }
    public String getStatus() { return status; }
    public String getShopName() { return shopName; }
    public List<ProductResponse> getProductList() { return productList; }
    public boolean isExpanded() { return isExpanded; }
    public void setExpanded(boolean expanded) { isExpanded = expanded; }
}


