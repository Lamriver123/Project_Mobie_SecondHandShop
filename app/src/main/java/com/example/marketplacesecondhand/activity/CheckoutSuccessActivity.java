package com.example.marketplacesecondhand.activity;

import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.API.DatabaseHandler;
import com.example.marketplacesecondhand.service.RetrofitClient;
import com.example.marketplacesecondhand.adapter.product.ProductAdapter;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.ProductResponse;
import com.example.marketplacesecondhand.models.UserLoginInfo;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutSuccessActivity extends AppCompatActivity {
    private static final String TAG = "CheckoutSuccessActivity";
    private Button btnGoToHome, btnViewOrders;
    private RecyclerView recyclerRecommendedProducts;
    private Toolbar toolbar;
    private TextView tvToolbarTitle;
    private ImageView ivSuccess, ivCartIconToolbar;
    private APIService apiService;
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout_success);

        initView();
        setupToolbar();
        setupRecyclerView();
        loadRecommendedProducts();

        setupClickListeners();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbarCheckoutSuccess);
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
        btnGoToHome = findViewById(R.id.btnGoToHome);
        btnViewOrders = findViewById(R.id.btnViewOrders);
        recyclerRecommendedProducts = findViewById(R.id.recyclerRecommendedProducts);
        ivCartIconToolbar = findViewById(R.id.ivCartIconToolbar);
        ivSuccess = findViewById(R.id.ivSuccessIcon);

        apiService = RetrofitClient.getRetrofit().create(APIService.class);

        Drawable drawable = ivSuccess.getDrawable();
        if (drawable instanceof AnimatedVectorDrawable) {
            ((AnimatedVectorDrawable) drawable).start();
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (tvToolbarTitle != null) {
            tvToolbarTitle.setText("Đặt Hàng Thành Công");
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setupRecyclerView() {
        recyclerRecommendedProducts.setLayoutManager(new GridLayoutManager(this, 2));
        productAdapter = new ProductAdapter(this, new ArrayList<>(), new ArrayList<>());
        recyclerRecommendedProducts.setAdapter(productAdapter);
    }

    private void loadRecommendedProducts() {
        ArrayList<Integer> categoryIds = getIntent().getIntegerArrayListExtra("categoryIds");
        if (categoryIds == null || categoryIds.isEmpty()) {
            Log.d(TAG, "No category IDs found in intent");
            return;
        }

        Call<ApiResponse<List<ProductResponse>>> call = apiService.getRecommendedProducts(categoryIds);
        call.enqueue(new Callback<ApiResponse<List<ProductResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ProductResponse>>> call, Response<ApiResponse<List<ProductResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductResponse> products = response.body().getData();
                    fetchFavoriteProductIds(products);
                } else {
                    Log.e(TAG, "Error loading recommended products: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ProductResponse>>> call, Throwable t) {
                Log.e(TAG, "Failed to load recommended products", t);
            }
        });
    }

    private void fetchFavoriteProductIds(List<ProductResponse> products) {
        DatabaseHandler db = new DatabaseHandler(this);
        UserLoginInfo userLoginInfo = db.getLoginInfoSQLite();

        if (userLoginInfo != null && userLoginInfo.getUserId() != 0) {
            Call<ApiResponse<List<Integer>>> call = apiService.getFavoriteProductIds(userLoginInfo.getUserId());
            call.enqueue(new Callback<ApiResponse<List<Integer>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<Integer>>> call, Response<ApiResponse<List<Integer>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Integer> favoriteProductIds = response.body().getData();
                        updateProductList(products, favoriteProductIds);
                    } else {
                        updateProductList(products, new ArrayList<>());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<Integer>>> call, Throwable t) {
                    Log.e(TAG, "Failed to fetch favorite product IDs", t);
                    updateProductList(products, new ArrayList<>());
                }
            });
        } else {
            updateProductList(products, new ArrayList<>());
        }
    }

    private void updateProductList(List<ProductResponse> products, List<Integer> favoriteProductIds) {
        productAdapter = new ProductAdapter(this, products, favoriteProductIds);
        recyclerRecommendedProducts.setAdapter(productAdapter);
    }

    private void setupClickListeners() {
        btnGoToHome.setOnClickListener(v -> {
            Intent intent = new Intent(CheckoutSuccessActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        btnViewOrders.setOnClickListener(v -> {
            Intent intent = new Intent(CheckoutSuccessActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("SELECTED_TAB", 3);
            startActivity(intent);
            finish();
        });

        ivCartIconToolbar.setOnClickListener(v -> {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CheckoutSuccessActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
