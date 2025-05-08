package com.example.marketplacesecondhand.dto.response;

import java.util.Date;
import java.util.List;

public class OrderResponse {
    int orderId;
    String buyerName;
    String ownerName;
    Date createdAt;
    String totalAmount;
    String address;
    String status;
    List<OrderDetailResponse> orderDetails;

    public OrderResponse(int orderId, String buyerName, String ownerName, Date createdAt, String totalAmount, String address, String status, List<OrderDetailResponse> orderDetails) {
        this.orderId = orderId;
        this.buyerName = buyerName;
        this.ownerName = ownerName;
        this.createdAt = createdAt;
        this.totalAmount = totalAmount;
        this.address = address;
        this.status = status;
        this.orderDetails = orderDetails;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderDetailResponse> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetailResponse> orderDetails) {
        this.orderDetails = orderDetails;
    }
}
