package com.example.marketplacesecondhand.dto.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class CancelledOrderResponse implements Serializable {
    @SerializedName("cancelId")
    private int cancelId;
    @SerializedName("buyerId")
    private int buyerId;
    @SerializedName("buyerName")
    private String buyerName;
    @SerializedName("orderId")
    private int orderId;
    @SerializedName("reason")
    private String reason;
    @SerializedName("cancelledAt")
    private String cancelledAt;

    public CancelledOrderResponse(int cancelId, int buyerId, String buyerName, int orderId, String reason, String cancelledAt) {
        this.cancelId = cancelId;
        this.buyerId = buyerId;
        this.buyerName = buyerName;
        this.orderId = orderId;
        this.reason = reason;
        this.cancelledAt = cancelledAt;
    }

    public int getCancelId() {
        return cancelId;
    }

    public void setCancelId(int cancelId) {
        this.cancelId = cancelId;
    }

    public int getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(int buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
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

    public String getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(String cancelledAt) {
        this.cancelledAt = cancelledAt;
    }
}
