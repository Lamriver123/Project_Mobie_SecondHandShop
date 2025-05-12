package com.example.marketplacesecondhand.dto.request;

public class UpdateDeliveryAddressRequest {
    private Long addressId;
    private String nameBuyer;
    private String addressName;
    private String phoneNumber;
    private Integer defaultFlag;

    public UpdateDeliveryAddressRequest(Long addressId, String nameBuyer, String addressName, String phoneNumber, Integer defaultFlag) {
        this.addressId = addressId;
        this.nameBuyer = nameBuyer;
        this.addressName = addressName;
        this.phoneNumber = phoneNumber;
        this.defaultFlag = defaultFlag;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public String getNameBuyer() {
        return nameBuyer;
    }

    public void setNameBuyer(String nameBuyer) {
        this.nameBuyer = nameBuyer;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getDefaultFlag() {
        return defaultFlag;
    }

    public void setDefaultFlag(Integer defaultFlag) {
        this.defaultFlag = defaultFlag;
    }
}
