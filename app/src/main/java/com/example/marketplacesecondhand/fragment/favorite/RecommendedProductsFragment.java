package com.example.marketplacesecondhand.fragment.favorite;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.marketplacesecondhand.databinding.FragmentRecommendedProductsBinding;

public class RecommendedProductsFragment extends Fragment {
    private FragmentRecommendedProductsBinding binding;


    public RecommendedProductsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRecommendedProductsBinding.inflate(inflater, container, false);



        return binding.getRoot();
    }
}
