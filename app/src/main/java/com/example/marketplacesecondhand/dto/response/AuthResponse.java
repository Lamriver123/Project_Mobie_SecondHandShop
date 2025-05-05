package com.example.marketplacesecondhand.dto.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AuthResponse implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("token")
    private String token;

    @SerializedName("authenticated")
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
