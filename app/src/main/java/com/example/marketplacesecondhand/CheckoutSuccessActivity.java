package com.example.marketplacesecondhand;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_success);

        toolbar = findViewById(R.id.toolbarCheckoutSuccess);
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
        btnGoToHome = findViewById(R.id.btnGoToHome);
        btnViewOrders = findViewById(R.id.btnViewOrders);
        recyclerRecommendedProducts = findViewById(R.id.recyclerRecommendedProducts);

        // Setup Toolbar
        // setSupportActionBar(toolbar); // Nếu bạn muốn dùng nó như ActionBar chính
        if (tvToolbarTitle != null) {
            tvToolbarTitle.setText("Đặt Hàng Thành Công"); // Hoặc "Đang chờ thanh toán" tùy theo trạng thái
        }
        // Không hiển thị tiêu đề mặc định của Toolbar nếu đã có TextView tùy chỉnh
        // if (getSupportActionBar() != null) {
        //     getSupportActionBar().setDisplayShowTitleEnabled(false);
        // }


        btnGoToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckoutSuccessActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish(); // Đóng Activity hiện tại
            }
        });

        btnViewOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Điều hướng đến màn hình xem danh sách đơn hàng (OrderActivity hoặc một tab trong HomeActivity)
                // Ví dụ:
                // Intent intent = new Intent(CheckoutSuccessActivity.this, OrderHistoryActivity.class);
                // startActivity(intent);
                // finish(); // Tùy chọn, có thể muốn giữ lại màn hình này hoặc không

                // Hoặc nếu bạn muốn mở một tab cụ thể trong HomeActivity
                Intent intent = new Intent(CheckoutSuccessActivity.this, HomeActivity.class);
                intent.putExtra("NAVIGATE_TO_ORDERS", true); // Thêm cờ để HomeActivity biết cần chuyển tab
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

                Toast.makeText(CheckoutSuccessActivity.this, "Chuyển đến xem đơn mua", Toast.LENGTH_SHORT).show();
            }
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
