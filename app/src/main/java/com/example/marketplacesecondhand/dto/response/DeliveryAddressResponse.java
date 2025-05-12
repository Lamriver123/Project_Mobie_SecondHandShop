package com.example.marketplacesecondhand.dto.response;

public class DeliveryAddressResponse {
	private Long addressId;
	private String nameBuyer;
	private String phoneNumber;
	private String addressName;
	private int defaultAddress;

	public DeliveryAddressResponse() {}
	public DeliveryAddressResponse(Long addressId, String nameBuyer, String phoneNumber, String addressName, int defaultAddress) {
		this.addressId = addressId;
		this.nameBuyer = nameBuyer;
		this.phoneNumber = phoneNumber;
		this.addressName = addressName;
		this.defaultAddress = defaultAddress;
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

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAddressName() {
		return addressName;
	}

	public void setAddressName(String addressName) {
		this.addressName = addressName;
	}

	public int getDefaultAddress() {
		return defaultAddress;
	}

	public void setDefaultAddress(int defaultAddress) {
		this.defaultAddress = defaultAddress;
	}
}
