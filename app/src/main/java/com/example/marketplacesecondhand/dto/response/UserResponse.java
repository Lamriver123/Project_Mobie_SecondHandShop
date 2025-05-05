package com.example.marketplacesecondhand.dto.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

public class UserResponse implements Serializable {
    @SerializedName("fullName")
    private String fullName;
    @SerializedName("phoneNumber")
    private String phoneNumber;
    @SerializedName("gender")
    private String gender;
    @SerializedName("dateOfBirth")
    private Date dateOfBirth;
    @SerializedName("email")
    private String email;
    @SerializedName("username")
    private String username;
    @SerializedName("isActive")
    private Boolean isActive;
    @SerializedName("otp")
    private String otp;
    @SerializedName("otpGenaratedTime")
    private String otpGenaratedTime;
    @SerializedName("roles")
    private Set<String> roles;

    public UserResponse() {}

    public UserResponse(String fullName, String phoneNumber, String gender, Date dateOfBirth, String email, String username, Boolean isActive, String otp, String otpGenaratedTime, Set<String> roles) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.username = username;
        this.isActive = isActive;
        this.otp = otp;
        this.otpGenaratedTime = otpGenaratedTime;
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getOtpGenaratedTime() {
        return otpGenaratedTime;
    }

    public void setOtpGenaratedTime(String otpGenaratedTime) {
        this.otpGenaratedTime = otpGenaratedTime;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
