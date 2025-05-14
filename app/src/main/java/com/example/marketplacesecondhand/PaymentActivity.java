package com.example.marketplacesecondhand;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.marketplacesecondhand.databinding.ActivityPaymentBinding;
import com.example.marketplacesecondhand.fragment.payment.BodyPaymentFragment;
import com.example.marketplacesecondhand.models.CartShop;
import com.example.marketplacesecondhand.viewModel.PaymentViewModel;
import vn.zalopay.sdk.ZaloPaySDK;

import java.util.List;

public class PaymentActivity extends AppCompatActivity {
    private ActivityPaymentBinding binding;
    private PaymentViewModel paymentViewModel;
    public static final String EXTRA_SOURCE = "source";
    public static final String SOURCE_CART = "cart";
    public static final String SOURCE_PRODUCT_DETAIL = "product_detail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ViewModel
        paymentViewModel = new ViewModelProvider(this).get(PaymentViewModel.class);
        List<CartShop> selectedShops =
                (List<CartShop>) getIntent().getSerializableExtra("SELECTED_CART_SHOPS");
        String source = getIntent().getStringExtra(EXTRA_SOURCE);

        paymentViewModel.setCartShopsToCheckout(selectedShops);
        paymentViewModel.setPaymentSource(source);

        // Load BodyPaymentFragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.payment_detail, new BodyPaymentFragment())
                .commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("ZaloPay", "onNewIntent triggered");
        Log.d("ZaloPay", "Intent Data: " + intent.getDataString());
        // Kiểm tra dữ liệu có về hay không
        if (intent != null && intent.getData() != null) {
            Log.d("ZaloPay", "Deeplink data: " + intent.getData().toString());
        } else {
            Log.d("ZaloPay", "No Deeplink Data Received");
        }
        ZaloPaySDK.getInstance().onResult(intent);
    }
}