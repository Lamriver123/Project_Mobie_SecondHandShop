package com.example.marketplacesecondhand.repository;

import android.util.Log;

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.service.RetrofitClient;
import com.example.marketplacesecondhand.dto.request.OrderRequest;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepository {
    private static final String TAG = "OrderRepository";
    private final APIService apiService;

    public OrderRepository() {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
    }

    public interface OrderCallback {
        void onSuccess();
        void onError(String error);
    }

    public void createOrder(OrderRequest orderRequest, OrderCallback callback) {
        Call<ApiResponse<Void>> call = apiService.createOrder(orderRequest);
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse.getCode() == 1000) {
                        callback.onSuccess();
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            ApiResponse<?> errorResponse = new Gson().fromJson(errorJson, ApiResponse.class);
                            callback.onError(errorResponse.getMessage());
                        } else {
                            callback.onError("Lỗi không xác định!");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response", e);
                        callback.onError("Lỗi hệ thống!");
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
} 