package com.example.marketplacesecondhand.models;

import java.io.Serializable;

public class Follow implements Serializable {
    private int followId;
    private User follower;
    private User shop;

    public Follow(int followId, User follower, User shop) {
        this.followId = followId;
        this.follower = follower;
        this.shop = shop;
    }

    public int getFollowId() {
        return followId;
    }

    public void setFollowId(int followId) {
        this.followId = followId;
    }

    public User getFollower() {
        return follower;
    }

    public void setFollower(User follower) {
        this.follower = follower;
    }

    public User getShop() {
        return shop;
    }

    public void setShop(User shop) {
        this.shop = shop;
    }
}
