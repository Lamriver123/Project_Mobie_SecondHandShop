package com.example.marketplacesecondhand.models;

import java.util.List;

public class Shop {
    private int shopId;

    private String avt;
    private String name;
    private float rating;
    private String reviewCount;
    private List<String> productImagesAds;

    public Shop(int shopId, String avt, String name, float rating, String reviewCount, String followText, List<String> productImagesAds) {
        this.shopId = shopId;
        this.avt = avt;
        this.name = name;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.productImagesAds = productImagesAds;
    }


    public String getAvt() {
        return avt;
    }

    public void setAvt(String avt) {
        this.avt = avt;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(String reviewCount) {
        this.reviewCount = reviewCount;
    }

    public List<String> getProductImagesAds() {
        return productImagesAds;
    }

    public void setProductImagesAds(List<String> productImagesAds) {
        this.productImagesAds = productImagesAds;
    }
}
