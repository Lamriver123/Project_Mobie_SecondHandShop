package com.example.marketplacesecondhand.dto.response;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ProductResponse {
    private int productId;
    private String productName;
    private String currentPrice;
    private String originalPrice;
    private String origin;
    private String warranty;
    private String productCondition;
    private String productDescription;
    private String conditionDescription;
    private Date createdAt;
    private int sold;
    private int quantity;
    private int categoryId;
    private String categoryName;
    private List<String> initialImages;
    private List<String> currentImages;

    public ProductResponse(int productId, String productName, String currentPrice, String originalPrice) {
        this.productId = productId;
        this.productName = productName;
        this.currentPrice = currentPrice;
        this.originalPrice = originalPrice;
    }

    public ProductResponse(int productId, String productName, String currentPrice, String originalPrice, String origin, String warranty, String productCondition, String productDescription, String conditionDescription, Date createdAt, int sold, int quantity, int categoryId, String categoryName, List<String> initialImages, List<String> currentImages) {
        this.productId = productId;
        this.productName = productName;
        this.currentPrice = currentPrice;
        this.originalPrice = originalPrice;
        this.origin = origin;
        this.warranty = warranty;
        this.productCondition = productCondition;
        this.productDescription = productDescription;
        this.conditionDescription = conditionDescription;
        this.createdAt = createdAt;
        this.sold = sold;
        this.quantity = quantity;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.initialImages = initialImages;
        this.currentImages = currentImages;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(String currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
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

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getConditionDescription() {
        return conditionDescription;
    }

    public void setConditionDescription(String conditionDescription) {
        this.conditionDescription = conditionDescription;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<String> getInitialImages() {
        return initialImages;
    }

    public void setInitialImages(List<String> initialImages) {
        this.initialImages = initialImages;
    }

    public List<String> getCurrentImages() {
        return currentImages;
    }

    public void setCurrentImages(List<String> currentImages) {
        this.currentImages = currentImages;
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
