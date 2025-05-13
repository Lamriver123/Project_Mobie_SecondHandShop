package com.example.marketplacesecondhand.repository;

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.dto.response.ShopResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopRepository {
    private final APIService apiService;

    public ShopRepository(APIService apiService) {
        this.apiService = apiService;
    }

    public interface ShopCallback {
        void onSuccess(List<ShopResponse> shops);
        void onError(String error);
    }

    public void getAllShops(ShopCallback callback) {
        apiService.getAllShops().enqueue(new Callback<com.example.marketplacesecondhand.dto.response.ApiResponse<List<ShopResponse>>>() {
            @Override
            public void onResponse(Call<com.example.marketplacesecondhand.dto.response.ApiResponse<List<ShopResponse>>> call, 
                                 Response<com.example.marketplacesecondhand.dto.response.ApiResponse<List<ShopResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError("Failed to load shops");
                }
            }

            @Override
            public void onFailure(Call<com.example.marketplacesecondhand.dto.response.ApiResponse<List<ShopResponse>>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
} 