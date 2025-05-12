package com.example.marketplacesecondhand.dto.request;

public class UpdateDefaultAddressRequest {
    private Long addressId;

    public UpdateDefaultAddressRequest(Long addressId) {
        this.addressId = addressId;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }
}
