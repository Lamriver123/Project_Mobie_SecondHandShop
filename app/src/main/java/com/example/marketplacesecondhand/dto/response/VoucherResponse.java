package com.example.marketplacesecondhand.dto.response;

import java.util.Date;

public class VoucherResponse {
    private int voucherId;
    private String code;
    private String description;
    private String discountType;
    private Double discountValue;
    private Double minimumOrderAmount;
    private Double maximumDiscountAmount;
    private Date startDate;
    private Date endDate;
    private String status;
    private Integer quantity;
    private Integer usedCount;
    private UserResponse shopOwner;

    public VoucherResponse(int voucherId, String code, String description, String discountType, Double discountValue, Double minimumOrderAmount, Double maximumDiscountAmount, Date startDate, Date endDate, String status, Integer quantity, Integer usedCount, UserResponse shopOwner) {
        this.voucherId = voucherId;
        this.code = code;
        this.description = description;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minimumOrderAmount = minimumOrderAmount;
        this.maximumDiscountAmount = maximumDiscountAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.quantity = quantity;
        this.usedCount = usedCount;
        this.shopOwner = shopOwner;
    }

    public int getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(int voucherId) {
        this.voucherId = voucherId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public Double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(Double discountValue) {
        this.discountValue = discountValue;
    }

    public Double getMinimumOrderAmount() {
        return minimumOrderAmount;
    }

    public void setMinimumOrderAmount(Double minimumOrderAmount) {
        this.minimumOrderAmount = minimumOrderAmount;
    }

    public Double getMaximumDiscountAmount() {
        return maximumDiscountAmount;
    }

    public void setMaximumDiscountAmount(Double maximumDiscountAmount) {
        this.maximumDiscountAmount = maximumDiscountAmount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(Integer usedCount) {
        this.usedCount = usedCount;
    }

    public UserResponse getShopOwner() {
        return shopOwner;
    }

    public void setShopOwner(UserResponse shopOwner) {
        this.shopOwner = shopOwner;
    }
}
