package com.example.marketplacesecondhand.API;

import com.example.marketplacesecondhand.dto.request.EmailRequest;
import com.example.marketplacesecondhand.dto.request.LoginRequest;
import com.example.marketplacesecondhand.dto.request.RegisterRequest;
import com.example.marketplacesecondhand.dto.request.ResetPasswordRequest;
import com.example.marketplacesecondhand.dto.request.VerifyAccountRequest;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.AuthResponse;
import com.example.marketplacesecondhand.models.Category;
import com.example.marketplacesecondhand.models.Product;
import com.example.marketplacesecondhand.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface APIService {
    @POST("auth/register")
    Call<ApiResponse<Void>> registerUser(@Body RegisterRequest request);
    @POST("auth/login")
    Call<ApiResponse<AuthResponse>> login(@Body LoginRequest request);

    @PUT("auth/verify-account")
    Call<ApiResponse<Void>> verifyAccount(@Body VerifyAccountRequest request);

    @PUT("auth/regenerate-otp")
    Call<ApiResponse<Void>> regenerateOtp(@Body EmailRequest request);

    @PUT("auth/forgot-password")
    Call<ApiResponse<Void>> forgotPassword(@Body EmailRequest request);

    @PUT("auth/reset-password")
    Call<ApiResponse<Void>> resetPassword(@Body ResetPasswordRequest request);

    @GET("categories")
    Call<ApiResponse<List<Category>>> getCategories();

    @GET("products/last-7-days")
    Call<ApiResponse<List<Product>>> getProductLast7Days();
}
