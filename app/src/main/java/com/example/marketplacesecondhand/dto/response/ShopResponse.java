package com.example.marketplacesecondhand.dto.response;

import java.io.Serializable;
import java.util.List;

public class ShopResponse implements Serializable {
    private int id;
    private String avt;
    private String username;
    private int totalReviews;
    private double averageRating;
    private List<Integer> followerIds;
    private List<Integer> followingIds;
    private List<ProductResponse> products;

    public ShopResponse(int id, String avt, String username, int totalReviews, double averageRating, List<Integer> followerIds, List<Integer> followingIds, List<ProductResponse> products) {
        this.id = id;
        this.avt = avt;
        this.username = username;
        this.totalReviews = totalReviews;
        this.averageRating = averageRating;
        this.followerIds = followerIds;
        this.followingIds = followingIds;
        this.products = products;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public List<Integer> getFollowingIds() {
        return followingIds;
    }

    public void setFollowingIds(List<Integer> followingIds) {
        this.followingIds = followingIds;
    }

    public List<ProductResponse> getProducts() {
        return products;
    }

    public void setProducts(List<ProductResponse> products) {
        this.products = products;
    }
}
