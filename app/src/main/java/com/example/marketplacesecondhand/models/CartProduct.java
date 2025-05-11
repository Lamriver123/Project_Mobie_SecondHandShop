package com.example.marketplacesecondhand.models;

import com.example.marketplacesecondhand.dto.response.ProductResponse;

import java.util.List;

public class CartProduct {



    private ProductResponse productResponse;

    private int quantityCart;
    private boolean isChecked;

    public CartProduct() {
    }

    public CartProduct(ProductResponse productResponse, int quantityCart, boolean isChecked) {
        this.productResponse = productResponse;
        this.quantityCart = quantityCart;
        this.isChecked = isChecked;
    }

    public ProductResponse getProductResponse() {
        return productResponse;
    }

    public void setProductResponse(ProductResponse productResponse) {
        this.productResponse = productResponse;
    }

    public int getQuantityCart() {
        return quantityCart;
    }

    public void setQuantityCart(int quantityCart) {
        this.quantityCart = quantityCart;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
