package com.example.marketplacesecondhand.models;

import java.io.Serializable;
import java.util.List;

public class CartShop implements Serializable {
    private User user;
    private boolean isChecked;
    private List<CartProduct> products;


    public CartShop() {
    }

    public CartShop(User user, boolean isChecked, List<CartProduct> products) {
        this.user = user;
        this.isChecked = isChecked;
        this.products = products;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public List<CartProduct> getProducts() {
        return products;
    }

    public void setProducts(List<CartProduct> products) {
        this.products = products;
    }
}
