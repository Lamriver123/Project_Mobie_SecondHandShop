package com.example.marketplacesecondhand.repository;

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.dto.request.FollowRequest;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
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
        void onSuccess(List<ShopResponse> shopList);
        void onError(String errorMessage);
    }

    public interface SingleShopCallback {
        void onSuccess(ShopResponse shop);
        void onError(String errorMessage);
    }

    public interface FollowCallback {
        void onSuccess(boolean isFollowing);
        void onError(String error);
    }

    public void getAllShops(ShopCallback callback) {
        apiService.getAllShops().enqueue(new Callback<ApiResponse<List<ShopResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ShopResponse>>> call, 
                                 Response<ApiResponse<List<ShopResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError("Failed to load shops");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ShopResponse>>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getCurrentUserShop(SingleShopCallback callback) {
        apiService.getCurrentUserShop().enqueue(new Callback<ApiResponse<ShopResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ShopResponse>> call, Response<ApiResponse<ShopResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError("Failed to load current user's shop");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ShopResponse>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void toggleFollow(FollowRequest request, FollowCallback callback) {
        apiService.toggleFollow(request).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(true);
                } else {
                    callback.onError("Failed to update follow status");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
} 