package com.example.marketplacesecondhand;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.marketplacesecondhand.databinding.ActivityPaymentBinding;

public class PaymentActivity extends AppCompatActivity {
    private ActivityPaymentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}