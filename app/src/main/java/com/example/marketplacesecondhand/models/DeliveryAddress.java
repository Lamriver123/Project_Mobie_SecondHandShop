package com.example.marketplacesecondhand.models;

public class DeliveryAddress {
    private Long addressId;
    private String addressName;
    private String phoneNumber;
    private User buyer;
    private String nameBuyer;
    private int defaultAddress;

    public DeliveryAddress(Long addressId, String addressName, String phoneNumber, User buyer, String nameBuyer, int defaultAddress) {
        this.addressId = addressId;
        this.addressName = addressName;
        this.phoneNumber = phoneNumber;
        this.buyer = buyer;
        this.nameBuyer = nameBuyer;
        this.defaultAddress = defaultAddress;
    }

    public String getNameBuyer() {
        return nameBuyer;
    }

    public void setNameBuyer(String nameBuyer) {
        this.nameBuyer = nameBuyer;
    }
    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
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

    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public int getDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(int defaultAddress) {
        this.defaultAddress = defaultAddress;
    }
}
