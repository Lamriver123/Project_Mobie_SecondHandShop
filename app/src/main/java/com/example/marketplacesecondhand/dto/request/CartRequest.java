package com.example.marketplacesecondhand.dto.request;

public class CartRequest {
    int buyerId;
    int productId;
    int quantity;

    public CartRequest(int buyerId, int productId, int quantity) {
        this.buyerId = buyerId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public int getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(int buyerId) {
        this.buyerId = buyerId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
