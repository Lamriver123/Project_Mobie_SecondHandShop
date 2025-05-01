package com.example.marketplacesecondhand.API;

import com.example.marketplacesecondhand.dto.request.EmailRequest;
import com.example.marketplacesecondhand.dto.request.FavoriteRequest;
import com.example.marketplacesecondhand.dto.request.LoginRequest;
import com.example.marketplacesecondhand.dto.request.RegisterRequest;
import com.example.marketplacesecondhand.dto.request.ResetPasswordRequest;
import com.example.marketplacesecondhand.dto.request.VerifyAccountRequest;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.AuthResponse;
import com.example.marketplacesecondhand.dto.response.ProductResponse;
import com.example.marketplacesecondhand.models.Category;
import com.example.marketplacesecondhand.models.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

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

    @GET("products")
    Call<ApiResponse<List<ProductResponse>>> getAllProducts();

    @GET("products/newest")
    Call<ApiResponse<List<ProductResponse>>> getNewestProducts();

    @GET("products/last-7-days")
    Call<ApiResponse<List<Product>>> getProductLast7Days();

    @GET("category/{categoryId}")
    Call<ApiResponse<List<ProductResponse>>> getProductsByCategory(@Path("categoryId") int categoryId);

    @GET("products/{productId}")
    Call<ApiResponse<ProductResponse>> getProductDetail(@Path("productId") int productId);

    @GET("category/{categoryId}/max-price")
    Call<ApiResponse<Integer>> getMaxPrice(@Path("categoryId") int categoryId);

    @GET("filter")
    Call<ApiResponse<List<ProductResponse>>> filterProductsByPrice(
            @Query("categoryId") int categoryId,
            @Query("minPrice") int minPrice,
            @Query("maxPrice") int maxPrice
    );

    @POST("favorites/toggle")
    Call<ApiResponse<String>> toggleFavorite(@Body FavoriteRequest request);

    @GET("favorites/{userId}")
    Call<ApiResponse<List<Integer>>> getFavoriteProductIds(@Path("userId") int userId);

    @POST("products/by-ids")
    Call<ApiResponse<List<ProductResponse>>> getProductsByProductIds(@Body List<Integer> productIds);
}
