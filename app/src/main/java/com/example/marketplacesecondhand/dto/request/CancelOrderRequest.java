package com.example.marketplacesecondhand.dto.request;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CancelOrderRequest implements Serializable {
    @SerializedName("orderId")
    private int orderId;
    @SerializedName("reason")
    private String reason;

    public CancelOrderRequest(int orderId, String reason) {
        this.orderId = orderId;
        this.reason = reason;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
