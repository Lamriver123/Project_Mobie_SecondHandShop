package com.example.marketplacesecondhand.models;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

public class User {
    private int id;
    private String fullName;
    private String phoneNumber;
    private String gender;
    private Date dateOfBirth;
    private String avt;
    private String email;
    private String username;
    private String password;
    private Boolean isActive;
    private String otp;
    private LocalDateTime otpGeneratedTime;
    private Set<String> roles;
    public User() {
    }
    public User(int id, String fullName, String phoneNumber, String gender, Date dateOfBirth, String avt, String email, String username, String password, Boolean isActive, String otp, LocalDateTime otpGeneratedTime, Set<String> roles) {
        this.id = id;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.avt = avt;
        this.email = email;
        this.username = username;
        this.password = password;
        this.isActive = isActive;
        this.otp = otp;
        this.otpGeneratedTime = otpGeneratedTime;
        this.roles = roles;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
    public String getAvt() {
        return avt;
    }

    public void setAvt(String avt) {
        this.avt = avt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public LocalDateTime getOtpGeneratedTime() {
        return otpGeneratedTime;
    }

    public void setOtpGeneratedTime(LocalDateTime otpGeneratedTime) {
        this.otpGeneratedTime = otpGeneratedTime;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
