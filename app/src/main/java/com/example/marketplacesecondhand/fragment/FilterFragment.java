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

import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.databinding.FragmentFilterBinding;
import com.example.marketplacesecondhand.models.Category;

public class FilterFragment extends Fragment {
    private FragmentFilterBinding binding;
    private OnFilterChangeListener filterChangeListener;
    private int currentCategoryId = -1;

    public interface OnFilterChangeListener {
        void onCategoryChanged(Category category);
       // void onPriceChanged(Category category);
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
            binding.btnCategory.setText(getArguments().getString("category_name", ""));
        }
        
        setupClickListeners();
        return binding.getRoot();
    }

    private void setupClickListeners() {
        binding.btnPrice.setOnClickListener(v -> {
            PriceFilterBottomSheetFragment bottomSheet = new PriceFilterBottomSheetFragment();
            bottomSheet.setSelectedCategoryId(currentCategoryId);
            bottomSheet.setOnPriceFilterListener(new PriceFilterBottomSheetFragment.OnPriceFilterListener() {
                @Override
                public void onPriceFilterApplied(int minPrice, int maxPrice) {
                    binding.btnPrice.setText(minPrice + " - " + maxPrice);
                    binding.btnPrice.setBackgroundResource(R.drawable.bg_category_chip_selected);
                    binding.btnPrice.setTextColor(getResources().getColor(R.color.yellow));
                    // Lưu giá trị minPrice và maxPrice vào SharedPreferences
//                    if (filterChangeListener != null) {
//                        filterChangeListener.onPriceChanged(currentCategoryId);
//                    }

                }
            });
            bottomSheet.show(getParentFragmentManager(), "PriceFilterBottomSheet");
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