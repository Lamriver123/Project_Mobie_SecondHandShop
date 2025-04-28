package com.example.marketplacesecondhand.dto.response;

public class AuthResponse {
    private int id;
    private String token;
    private Boolean authenticated;
    public AuthResponse() {}
    public AuthResponse(int id, String token, Boolean authenticated) {
        this.id = id;
        this.token = token;
        this.authenticated = authenticated;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
