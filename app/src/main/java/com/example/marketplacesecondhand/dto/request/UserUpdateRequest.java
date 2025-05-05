package com.example.marketplacesecondhand.dto.request;

public class UserUpdateRequest {
    private String fullName;
    private String phoneNumber;
    private String gender;
    private String dateOfBirth;

    public UserUpdateRequest() {
    }

    public UserUpdateRequest(String fullName, String phoneNumber, String gender, String dateOfBirth) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
