package com.example.marketplacesecondhand.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.RetrofitClient;
import com.example.marketplacesecondhand.adapter.ProductCategoryAdapter;
import com.example.marketplacesecondhand.databinding.FragmentFilterBinding;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.ProductResponse;
import com.example.marketplacesecondhand.models.Category;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilterFragment extends Fragment {
    private FragmentFilterBinding binding;
    private OnFilterChangeListener filterChangeListener;
    private int currentCategoryId = -1;
    private static final String TAG = "FilterFragment";

    public interface OnFilterChangeListener {
        void onCategoryChanged(Category category);
        void onPriceChanged(int categoryId, int minPrice, int maxPrice);

        int getCurrentCategoryIdForSearch();

        int getCurrentMinPriceForSearch();

        int getCurrentMaxPriceForSearch();
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentCategoryId = getArguments().getInt("category_id", -1);
            Log.d(TAG, "onCreate - Initial CategoryID: " + currentCategoryId );
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
                public void onPriceFilterApplied(int categoryId, int minPrice, int maxPrice) {
                    binding.btnPrice.setText(minPrice + " - " + maxPrice);
                    binding.btnPrice.setBackgroundResource(R.drawable.bg_category_chip_selected);
                    binding.btnPrice.setTextColor(getResources().getColor(R.color.yellow));

                    if (filterChangeListener != null) {
                        filterChangeListener.onPriceChanged(categoryId, minPrice, maxPrice);
                    }

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

                    // Reset lọc theo giá về mặc định
                    binding.btnPrice.setText("Giá");
                    binding.btnPrice.setBackgroundResource(R.drawable.bg_filter_button); // Background mặc định
                    binding.btnPrice.setTextColor(getResources().getColor(R.color.text_filter)); // Màu chữ mặc định

                    if (filterChangeListener != null) {
                        filterChangeListener.onCategoryChanged(category);
                    }
                }
            });
            bottomSheet.show(getParentFragmentManager(), "CategoryBottomSheet");
        });
    }
    public void resetPriceButton() {
        if (binding != null) {
            binding.btnPrice.setText("Giá");
            binding.btnPrice.setBackgroundResource(R.drawable.bg_filter_button);
            binding.btnPrice.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_filter));
            Log.d(TAG, "Price button reset.");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 