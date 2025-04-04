package com.example.marketplacesecondhand.dto.response;

public class AuthResponse {
    private String token;
    private Boolean authenticated;
    public AuthResponse() {}
    public AuthResponse(String token, Boolean authenticated) {
        this.token = token;
        this.authenticated = authenticated;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(Boolean authenticated) {
        this.authenticated = authenticated;
    }
}
