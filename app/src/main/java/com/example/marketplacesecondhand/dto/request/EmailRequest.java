package com.example.marketplacesecondhand.dto.request;

public class EmailRequest {
    private String email;

    public EmailRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
