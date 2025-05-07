package com.example.marketplacesecondhand.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.marketplacesecondhand.PaymentActivity;
import com.example.marketplacesecondhand.databinding.BottomSheetAddToCartBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.NumberFormat;
import java.util.Locale;

public class BottomSheetAddToCartFragment extends BottomSheetDialogFragment {
    private BottomSheetAddToCartBinding binding;
    private int quantity = 1;
    private int unitPrice = 200000;
    private int maxQuantity = 10;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetAddToCartBinding.inflate(inflater, container, false);

        binding.btnApply.setEnabled(quantity > 0);
        binding.buttonIncrease.setOnClickListener(v -> {
            if(quantity<maxQuantity){
                quantity++;
                updateUI();
            }
        });

        binding.buttonDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                updateUI();
            }
        });

        binding.btnApply.setOnClickListener(v -> {
//            Intent intent = new Intent(requireContext(), PaymentActivity.class);
////            intent.putExtra("quantity", quantity);
////            intent.putExtra("total", quantity * unitPrice);
//            startActivity(intent);
            dismiss();
        });
        return binding.getRoot();
    }

    private void updateUI() {
        binding.textViewQuantity.setText(String.valueOf(quantity));
        int total = quantity * unitPrice;
        binding.txtTotalPrice.setText("Tổng tiền: " + formatCurrency(total) + " VND");

        // Bật nút mua ngay khi số lượng > 0
        binding.btnApply.setEnabled(quantity > 0);
    }

    private String formatCurrency(int number) {
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(number);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
