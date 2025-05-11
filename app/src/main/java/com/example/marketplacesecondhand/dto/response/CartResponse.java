package com.example.marketplacesecondhand.dto.response;

import com.google.gson.annotations.SerializedName;


public class CartResponse {
    // Ánh xạ trường 'id' từ JSON (ID của mục giỏ hàng cụ thể này).
    @SerializedName("id")
    private int id;

    // Ánh xạ trường 'buyer' từ JSON. Chứa thông tin chi tiết về người mua.
    @SerializedName("buyer")
    private UserResponse buyer;

    // Ánh xạ trường 'product' từ JSON. Chứa thông tin chi tiết về sản phẩm.
    @SerializedName("product")
    private ProductResponse product;

    // Ánh xạ trường 'quantity' từ JSON (Số lượng của sản phẩm này trong giỏ hàng).
    @SerializedName("quantity")
    private int quantity;

    public CartResponse() {
    }

    public CartResponse(int id, UserResponse buyer, ProductResponse product, int quantity) {
        this.id = id;
        this.buyer = buyer;
        this.product = product;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserResponse getBuyer() {
        return buyer;
    }

    public void setBuyer(UserResponse buyer) {
        this.buyer = buyer;
    }

    public ProductResponse getProduct() {
        return product;
    }

    public void setProduct(ProductResponse product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
