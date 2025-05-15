package com.example.marketplacesecondhand.dto.request;

import java.util.List;

public class OrderRequest {
    int buyerId;
    String address;
    String voucherCode;
    String paymentMethod;
    List<OrderDetailRequest> orderDetails;

    public OrderRequest(int buyerId, String address, String voucherCode, String paymentMethod, List<OrderDetailRequest> orderDetails) {
        this.buyerId = buyerId;
        this.address = address;
        this.voucherCode = voucherCode;
        this.paymentMethod = paymentMethod;
        this.orderDetails = orderDetails;
    }
    public OrderRequest(int buyerId, String address, String paymentMethod, List<OrderDetailRequest> orderDetails) {
        this.buyerId = buyerId;
        this.address = address;
        this.paymentMethod = paymentMethod;
        this.orderDetails = orderDetails;
    }
    public int getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(int buyerId) {
        this.buyerId = buyerId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }
    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<OrderDetailRequest> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetailRequest> orderDetails) {
        this.orderDetails = orderDetails;
    }
}
