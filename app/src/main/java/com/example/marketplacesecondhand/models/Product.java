package com.example.marketplacesecondhand.models;

import com.google.gson.annotations.SerializedName;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Product {
    @SerializedName("productId")
    private int productId;

    @SerializedName("ownerId")
    private int ownerId;

    @SerializedName("productName")
    private String productName;

    @SerializedName("category")
    private Category category;

    @SerializedName("originalPrice")
    private String originalPrice;

    @SerializedName("currentPrice")
    private String currentPrice;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("origin")
    private String origin;

    @SerializedName("warranty")
    private String warranty;

    @SerializedName("productCondition")
    private String productCondition;

    @SerializedName("conditionDescription")
    private String conditionDescription;

    @SerializedName("productDescription")
    private String productDescription;

    @SerializedName("createdAt")
    private Date createdAt;

    @SerializedName("sold")
    private int sold;

    public Product(int productId, int ownerId, String productName, Category category, String originalPrice, String currentPrice, int quantity, String origin, String warranty, String productCondition, String conditionDescription, String productDescription, Date createdAt, int sold) {
        this.productId = productId;
        this.ownerId = ownerId;
        this.productName = productName;
        this.category = category;
        this.originalPrice = originalPrice;
        this.currentPrice = currentPrice;
        this.quantity = quantity;
        this.origin = origin;
        this.warranty = warranty;
        this.productCondition = productCondition;
        this.conditionDescription = conditionDescription;
        this.productDescription = productDescription;
        this.createdAt = createdAt;
        this.sold = sold;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(String currentPrice) {
        this.currentPrice = currentPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getWarranty() {
        return warranty;
    }

    public void setWarranty(String warranty) {
        this.warranty = warranty;
    }

    public String getProductCondition() {
        return productCondition;
    }

    public void setProductCondition(String productCondition) {
        this.productCondition = productCondition;
    }

    public String getConditionDescription() {
        return conditionDescription;
    }

    public void setConditionDescription(String conditionDescription) {
        this.conditionDescription = conditionDescription;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getSold() {
        return sold;
    }

    public void setSold(int sold) {
        this.sold = sold;
    }

    public String getTimeAgoText() {
        if (createdAt == null) return "";

        Calendar createdCal = Calendar.getInstance();
        createdCal.setTime(createdAt);

        Calendar nowCal = Calendar.getInstance();

        boolean isSameDay =
                createdCal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR) &&
                        createdCal.get(Calendar.DAY_OF_YEAR) == nowCal.get(Calendar.DAY_OF_YEAR);

        if (isSameDay) {
            return "Hôm nay";
        }

        // Tính khoảng cách ngày
        long diffMillis = nowCal.getTimeInMillis() - createdCal.getTimeInMillis();
        long diffDays = diffMillis / (24 * 60 * 60 * 1000);

        if (diffDays == 1) return "Hôm qua";
        else return diffDays + " ngày trước";
    }
}
