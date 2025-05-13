package com.example.marketplacesecondhand;

import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// import com.example.marketplacesecondhand.adapter.RecommendedProductAdapter; // Bạn sẽ cần tạo adapter này
// import com.example.marketplacesecondhand.model.RecommendedProduct; // Và model này

import java.util.ArrayList;
import java.util.List;

public class CheckoutSuccessActivity extends AppCompatActivity {
    private Button btnGoToHome, btnViewOrders;
    private RecyclerView recyclerRecommendedProducts;
    // private RecommendedProductAdapter recommendedAdapter;
    // private List<RecommendedProduct> recommendedProductList;
    private Toolbar toolbar;
    private TextView tvToolbarTitle;
    private ImageView ivSuccess, ivCartIconToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_success);

        initView();

        // Setup Toolbar
        setSupportActionBar(toolbar); // Nếu bạn muốn dùng nó như ActionBar chính
        if (tvToolbarTitle != null) {
            tvToolbarTitle.setText("Đặt Hàng Thành Công");
        }
        // Không hiển thị tiêu đề mặc định của Toolbar nếu đã có TextView tùy chỉnh
         if (getSupportActionBar() != null) {
             getSupportActionBar().setDisplayShowTitleEnabled(false);
         }


        btnGoToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckoutSuccessActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        btnViewOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckoutSuccessActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        ivCartIconToolbar.setOnClickListener(v -> {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
        });
        // Setup RecyclerView cho sản phẩm gợi ý (phần này bạn cần tự triển khai Adapter và Model)
        // setupRecommendedProductsRecyclerView();
    }

    /*
    private void setupRecommendedProductsRecyclerView() {
        recommendedProductList = new ArrayList<>();
        // TODO: Thêm dữ liệu mẫu hoặc fetch dữ liệu sản phẩm gợi ý từ API
        // recommendedProductList.add(new RecommendedProduct("Sản phẩm gợi ý 1", "₫50.000", R.drawable.placeholder_image));
        // recommendedProductList.add(new RecommendedProduct("Sản phẩm gợi ý 2", "₫150.000", R.drawable.placeholder_image));
        // recommendedProductList.add(new RecommendedProduct("Sản phẩm gợi ý 3", "₫250.000", R.drawable.placeholder_image));
        // recommendedProductList.add(new RecommendedProduct("Sản phẩm gợi ý 4", "₫350.000", R.drawable.placeholder_image));


        recommendedAdapter = new RecommendedProductAdapter(this, recommendedProductList);
        recyclerRecommendedProducts.setLayoutManager(new GridLayoutManager(this, 2)); // Hiển thị 2 cột
        recyclerRecommendedProducts.setAdapter(recommendedAdapter);
        // Thêm ItemDecoration nếu cần (ví dụ: GridSpacingItemDecoration)
    }
    */

    private void initView() {
        toolbar = findViewById(R.id.toolbarCheckoutSuccess);
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
        btnGoToHome = findViewById(R.id.btnGoToHome);
        btnViewOrders = findViewById(R.id.btnViewOrders);
        recyclerRecommendedProducts = findViewById(R.id.recyclerRecommendedProducts);
        ivCartIconToolbar = findViewById(R.id.ivCartIconToolbar);
        ivSuccess = findViewById(R.id.ivSuccessIcon);

        Drawable drawable = ivSuccess.getDrawable();

        if (drawable instanceof AnimatedVectorDrawable) {
            ((AnimatedVectorDrawable) drawable).start();
        }
    }
    @Override
    public void onBackPressed() {
        // Khi người dùng nhấn nút back, chuyển về trang chủ thay vì quay lại màn hình thanh toán
        Intent intent = new Intent(CheckoutSuccessActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
