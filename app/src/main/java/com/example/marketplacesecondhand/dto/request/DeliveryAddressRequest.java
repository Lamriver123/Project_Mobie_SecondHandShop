package com.example.marketplacesecondhand.dto.request;

public class DeliveryAddressRequest {
    private String nameBuyer;
    private String addressName;
    private String phoneNumber;
    private int defaultFlag;

    public DeliveryAddressRequest() {}
    public DeliveryAddressRequest(String nameBuyer, String addressName, String phoneNumber, int defaultFlag) {
        this.nameBuyer = nameBuyer;
        this.addressName = addressName;
        this.phoneNumber = phoneNumber;
        this.defaultFlag = defaultFlag;
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

    public int getDefaultFlag() {
        return defaultFlag;
    }

    public void setDefaultFlag(int defaultFlag) {
        this.defaultFlag = defaultFlag;
    }
}
