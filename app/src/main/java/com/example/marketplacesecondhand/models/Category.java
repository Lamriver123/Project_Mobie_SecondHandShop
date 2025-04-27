package com.example.marketplacesecondhand.models;

import com.google.gson.annotations.SerializedName;

public class Category {
    @SerializedName("categoryId")
    private int categoryId;

    @SerializedName("categoryName")
    private String categoryName;

    @SerializedName("categoryImg")
    private String categoryImg;


    public Category(int categoryId, String categoryName, String categoryImg) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryImg = categoryImg;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryImg() {
        return categoryImg;
    }

    public void setCategoryImg(String categoryImg) {
        this.categoryImg = categoryImg;
    }
}
