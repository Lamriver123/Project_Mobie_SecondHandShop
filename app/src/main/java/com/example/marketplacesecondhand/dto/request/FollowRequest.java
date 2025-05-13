package com.example.marketplacesecondhand.dto.request;

import java.io.Serializable;

public class FollowRequest implements Serializable {
    private int shopId;

    public FollowRequest(int shopId) {
        this.shopId = shopId;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }
}
