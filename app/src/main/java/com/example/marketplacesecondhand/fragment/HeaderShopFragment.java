package com.example.marketplacesecondhand.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.marketplacesecondhand.databinding.FragmentHeaderFavoriteBinding;
import com.example.marketplacesecondhand.databinding.FragmentHeaderShopBinding;

public class HeaderShopFragment extends Fragment {
    private FragmentHeaderShopBinding binding;


    public HeaderShopFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHeaderShopBinding.inflate(inflater, container, false);

        binding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().onBackPressed();
            }
        });

        return binding.getRoot();
    }
}
