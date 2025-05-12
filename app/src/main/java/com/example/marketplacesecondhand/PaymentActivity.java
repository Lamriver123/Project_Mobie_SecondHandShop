package com.example.marketplacesecondhand;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.marketplacesecondhand.databinding.ActivityPaymentBinding;
import com.example.marketplacesecondhand.fragment.payment.BodyPaymentFragment;
import com.example.marketplacesecondhand.models.CartShop;
import com.example.marketplacesecondhand.viewModel.PaymentViewModel;

import java.util.List;

public class PaymentActivity extends AppCompatActivity {
    private ActivityPaymentBinding binding;
    private PaymentViewModel paymentViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ViewModel
        paymentViewModel = new ViewModelProvider(this).get(PaymentViewModel.class);
        List<CartShop> selectedShops =
                (List<CartShop>) getIntent().getSerializableExtra("SELECTED_CART_SHOPS");

        paymentViewModel.setCartShopsToCheckout(selectedShops);
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
}