package com.example.marketplacesecondhand.dto.response;

import java.io.Serializable;
import java.util.List;

public class ShopResponse implements Serializable {
    private String avt;
    private String username;
    private int totalReviews;
    private double averageRating;
    private List<Integer> followerIds;
    private List<ProductResponse> products;

    public ShopResponse(String avt, String username, int totalReviews, double averageRating, List<Integer> followerIds, List<ProductResponse> products) {
        this.avt = avt;
        this.username = username;
        this.totalReviews = totalReviews;
        this.averageRating = averageRating;
        this.followerIds = followerIds;
        this.products = products;
    }

    public String getAvt() {
        return avt;
    }

    public void setAvt(String avt) {
        this.avt = avt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(int totalReviews) {
        this.totalReviews = totalReviews;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public List<Integer> getFollowerIds() {
        return followerIds;
    }

    public void setFollowerIds(List<Integer> followerIds) {
        this.followerIds = followerIds;
    }

    public List<ProductResponse> getProducts() {
        return products;
    }

    public void setProducts(List<ProductResponse> products) {
        this.products = products;
    }
}
