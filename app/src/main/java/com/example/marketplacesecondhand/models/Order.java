package com.example.marketplacesecondhand.models;

import com.example.marketplacesecondhand.dto.response.ProductResponse;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Order implements Serializable {
    private int orderId;
    private User buyer;;
    private Date createdAt;
    private String totalAmount;
    private String address;
    private String status;
    String paymentMethod;
    private List<OrderDetail> orderDetails;

    public Order(int orderId, User buyer, Date createdAt, String totalAmount, String address, String status, String paymentMethod, List<OrderDetail> orderDetails) {
        this.orderId = orderId;
        this.buyer = buyer;
        this.createdAt = createdAt;
        this.totalAmount = totalAmount;
        this.address = address;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.orderDetails = orderDetails;
    }
    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
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

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }
}


