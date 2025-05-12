package com.example.marketplacesecondhand.API;

import com.example.marketplacesecondhand.dto.request.CancelOrderRequest;
import com.example.marketplacesecondhand.dto.request.CartRequest;
import com.example.marketplacesecondhand.dto.request.DeliveryAddressRequest;
import com.example.marketplacesecondhand.dto.request.EmailRequest;
import com.example.marketplacesecondhand.dto.request.FavoriteRequest;
import com.example.marketplacesecondhand.dto.request.LoginRequest;
import com.example.marketplacesecondhand.dto.request.RegisterRequest;
import com.example.marketplacesecondhand.dto.request.ResetPasswordRequest;
import com.example.marketplacesecondhand.dto.request.UpdateDefaultAddressRequest;
import com.example.marketplacesecondhand.dto.request.UpdateDeliveryAddressRequest;
import com.example.marketplacesecondhand.dto.request.UpdateOrderStatusRequest;
import com.example.marketplacesecondhand.dto.request.UserUpdateRequest;
import com.example.marketplacesecondhand.dto.request.VerifyAccountRequest;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.AuthResponse;
import com.example.marketplacesecondhand.dto.response.CancelledOrderResponse;
import com.example.marketplacesecondhand.dto.response.CartResponse;
import com.example.marketplacesecondhand.dto.response.DeliveryAddressResponse;
import com.example.marketplacesecondhand.dto.response.OrderResponse;
import com.example.marketplacesecondhand.dto.response.ProductResponse;
import com.example.marketplacesecondhand.dto.response.UserResponse;
import com.example.marketplacesecondhand.models.CartShop;
import com.example.marketplacesecondhand.models.Category;
import com.example.marketplacesecondhand.models.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
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

    @POST("products/recommend")
    Call<ApiResponse<List<ProductResponse>>> getRecommendedProducts(@Body List<Integer> categoryIds);

    @GET("users/myInfo")
    Call<ApiResponse<UserResponse>> getMyInfo();
    @PUT("users/{userId}")
    Call<ApiResponse<UserResponse>> updateUser(@Path("userId") int userId, @Body UserUpdateRequest request);

    @GET("orders/my-orders")
    Call<ApiResponse<List<OrderResponse>>> getMyOrders(@Query("status") String status);
    @PUT("orders/{orderId}/status")
    Call<ApiResponse<Void>> updateOrderStatus(
            @Path("orderId") int orderId,
            @Body UpdateOrderStatusRequest request
    );

    @POST("orders/cancel")
    Call<ApiResponse<CancelledOrderResponse>> cancelOrder(@Body CancelOrderRequest request);

    @GET("users/{userId}")
    Call<ApiResponse<UserResponse>> getShopInfo(@Path("userId") int userId);

    @POST("cart/add")
    Call<ApiResponse<Void>> addToCart(@Body CartRequest request);

    @PUT("cart/update")
    Call<ApiResponse<Void>> updateToCart(@Body CartRequest request);

    @GET("cart")
    Call<ApiResponse<List<CartResponse>>> getCartItems();

    @GET("cart/{userId}")
    Call<ApiResponse<List<CartResponse>>> getCartItemsByUserId(@Path("userId") int userId);

    @HTTP(method = "DELETE", path = "cart/remove", hasBody = true)
    Call<ApiResponse<Void>> deleteCart(@Body CartRequest request);

    @GET("address")
    Call<ApiResponse<List<DeliveryAddressResponse>>> getUserAddresses();

    @POST("address")
    Call<ApiResponse<DeliveryAddressResponse>> createDeliveryAddress(
            @Body DeliveryAddressRequest request
    );

    @PUT("address")
    Call<ApiResponse<DeliveryAddressResponse>> updateDeliveryAddress(@Body UpdateDeliveryAddressRequest request);

    @PUT("address/default")
    Call<ApiResponse<DeliveryAddressResponse>> updateDefaultAddress(@Body UpdateDefaultAddressRequest request);

}
