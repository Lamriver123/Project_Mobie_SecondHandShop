package com.example.marketplacesecondhand.repository;

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.service.RetrofitClient;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.VoucherResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VoucherRepository {
    private final APIService apiService;

    public interface VoucherCallback {
        void onSuccess(List<VoucherResponse> vouchers);
        void onError(String error);
    }

    public VoucherRepository() {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
    }

    public void getShopActiveVouchers(int shopId, VoucherCallback callback) {
        apiService.getShopActiveVouchers(shopId).enqueue(new Callback<ApiResponse<List<VoucherResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<VoucherResponse>>> call, Response<ApiResponse<List<VoucherResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<VoucherResponse>> apiResponse = response.body();
                    if (apiResponse.getCode() == 1000 ) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage() != null ? apiResponse.getMessage() : "Không thể tải danh sách voucher");
                    }
                } else {
                    callback.onError("Lỗi server: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<VoucherResponse>>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
} 