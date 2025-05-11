package com.example.marketplacesecondhand;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.marketplacesecondhand.databinding.ActivityPaymentBinding;
import com.example.marketplacesecondhand.fragment.payment.BodyPaymentFragment;

public class PaymentActivity extends AppCompatActivity {
    private ActivityPaymentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Lấy dữ liệu từ Intent được gửi từ BottomSheetBuyNowFragment
        Intent intent = getIntent();
        int productId = intent.getIntExtra("PRODUCT_ID", -1);
        String productName = intent.getStringExtra("PRODUCT_NAME");
        int quantity = intent.getIntExtra("QUANTITY", 0);
        int unitPrice = intent.getIntExtra("UNIT_PRICE", 0);
        int totalPrice = intent.getIntExtra("TOTAL_PRICE", 0);
        String imageUrl = intent.getStringExtra("PRODUCT_IMAGE_URL");
        String shopName = intent.getStringExtra("SHOP_NAME");

        if (productId != -1 && quantity > 0) {
            // Tạo và hiển thị BodyPaymentFragment với dữ liệu đã nhận
            BodyPaymentFragment bodyPaymentFragment = BodyPaymentFragment.newInstance(
                    productId,
                    productName,
                    quantity,
                    unitPrice,
                    totalPrice,
                    imageUrl,
                    shopName
            );

            // Giả sử bạn có một FragmentContainerView với ID là R.id.cart_detail trong activity_payment.xml
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.cart_detail, bodyPaymentFragment) // Sử dụng đúng ID container
                    .commit();
        }
        else {
            // Xử lý lỗi nếu không có dữ liệu hợp lệ
                Toast.makeText(this, "Lỗi: Không có thông tin sản phẩm để thanh toán.", Toast.LENGTH_LONG).show();
                finish(); // Đóng Activity nếu không có gì để hiển thị
        }
    }
}