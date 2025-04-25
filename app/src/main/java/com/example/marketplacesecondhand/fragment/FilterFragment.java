package com.example.marketplacesecondhand.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.marketplacesecondhand.databinding.FragmentFilterBinding;
import com.example.marketplacesecondhand.models.Category;

public class FilterFragment extends Fragment {
    private FragmentFilterBinding binding;
    private OnFilterChangeListener filterChangeListener;
    private int currentCategoryId = -1;

    public interface OnFilterChangeListener {
        void onCategoryChanged(Category category);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            filterChangeListener = (OnFilterChangeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnFilterChangeListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                            @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        binding = FragmentFilterBinding.inflate(inflater, container, false);
        
        // Lấy category id từ arguments nếu có
        if (getArguments() != null) {
            currentCategoryId = getArguments().getInt("category_id", -1);
        }
        
        setupClickListeners();
        return binding.getRoot();
    }

    private void setupClickListeners() {
        binding.btnPrice.setOnClickListener(v -> {
            // Xử lý chọn khoảng giá ở đây
            Toast.makeText(getContext(), "Chọn khoảng giá", Toast.LENGTH_SHORT).show();
        });

        binding.btnCategory.setOnClickListener(v -> {
            CategoryBottomSheetFragment bottomSheet = new CategoryBottomSheetFragment();
            bottomSheet.setSelectedCategoryId(currentCategoryId);
            bottomSheet.setOnCategorySelectedListener(new CategoryBottomSheetFragment.OnCategorySelectedListener() {
                @Override
                public void onCategorySelected(Category category) {
                    binding.btnCategory.setText(category.getCategoryName());
                    currentCategoryId = category.getCategoryId();
                    if (filterChangeListener != null) {
                        filterChangeListener.onCategoryChanged(category);
                    }
                }
            });
            bottomSheet.show(getParentFragmentManager(), "CategoryBottomSheet");
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 