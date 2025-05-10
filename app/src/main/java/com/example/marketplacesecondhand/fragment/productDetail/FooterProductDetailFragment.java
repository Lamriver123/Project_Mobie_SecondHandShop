package com.example.marketplacesecondhand.fragment.productDetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.marketplacesecondhand.databinding.FragmentFooterProductDetailBinding;

public class FooterProductDetailFragment extends Fragment {
    private FragmentFooterProductDetailBinding binding;

    public FooterProductDetailFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle saveInstanceState) {
        binding = FragmentFooterProductDetailBinding.inflate(inflater, container, false);

        binding.buttonBuyNow.setOnClickListener(v -> {
            BottomSheetBuyNowFragment bottomSheet = new BottomSheetBuyNowFragment();
            bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
        });

        binding.buttonAddToCart.setOnClickListener(v -> {
            BottomSheetAddToCartFragment bottomSheet = new BottomSheetAddToCartFragment();
            bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
        });
        return binding.getRoot();
    }
}
