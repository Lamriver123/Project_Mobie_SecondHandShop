package com.example.marketplacesecondhand.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.marketplacesecondhand.databinding.FragmentFilterBinding;

public class FilterFragment extends Fragment {
    private FragmentFilterBinding binding;

    public void setFilterListener() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                            @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        binding = FragmentFilterBinding.inflate(inflater, container, false);
        setupClickListeners();
        return binding.getRoot();
    }

    private void setupClickListeners() {
        binding.btnPrice.setOnClickListener(v -> {
            // Xử lý chọn khoảng giá ở đây
            Toast.makeText(getContext(), "Chọn khoảng giá", Toast.LENGTH_SHORT).show();
        });

        binding.btnCategory.setOnClickListener(v -> {
            // Xử lý chọn danh mục ở đây
            String selectedCategory = binding.btnCategory.getText().toString();
            Toast.makeText(getContext(), "Danh mục: " + selectedCategory, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 