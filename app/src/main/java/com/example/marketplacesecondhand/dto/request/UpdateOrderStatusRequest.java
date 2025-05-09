package com.example.marketplacesecondhand.dto.request;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UpdateOrderStatusRequest implements Serializable {
    @SerializedName("status")
    private String status;

    public UpdateOrderStatusRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
