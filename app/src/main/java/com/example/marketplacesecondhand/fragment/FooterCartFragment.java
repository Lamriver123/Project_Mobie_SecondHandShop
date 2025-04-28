package com.example.marketplacesecondhand.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.marketplacesecondhand.databinding.FragmentFooterCartBinding;
import com.example.marketplacesecondhand.databinding.FragmentFooterProductDetailBinding;

public class FooterCartFragment extends Fragment {
    private FragmentFooterCartBinding binding;

    public FooterCartFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle saveInstanceState) {
        binding = FragmentFooterCartBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }
}
