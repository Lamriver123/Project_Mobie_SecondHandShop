package com.example.marketplacesecondhand.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.marketplacesecondhand.PaymentActivity;
import com.example.marketplacesecondhand.databinding.BottomSheetAddLocationBinding;
import com.example.marketplacesecondhand.databinding.BottomSheetAddToCartBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.NumberFormat;
import java.util.Locale;

public class BottomSheetAddLocationFragment extends BottomSheetDialogFragment {
    private BottomSheetAddLocationBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetAddLocationBinding.inflate(inflater, container, false);

        binding.btnAddLocation.setOnClickListener(v -> {
            BottomSheetLocationDetailFragment bottomSheet = new BottomSheetLocationDetailFragment();
            bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
            dismiss();
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
