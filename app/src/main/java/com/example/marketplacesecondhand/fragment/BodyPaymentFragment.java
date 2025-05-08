package com.example.marketplacesecondhand.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.marketplacesecondhand.databinding.BottomSheetAddLocationBinding;
import com.example.marketplacesecondhand.databinding.FragmentBodyPaymentBinding;

public class BodyPaymentFragment extends Fragment {
    private FragmentBodyPaymentBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBodyPaymentBinding.inflate(inflater, container, false);

        binding.btnChooseLocation.setOnClickListener(v -> {
            BottomSheetAddLocationFragment bottomSheet = new BottomSheetAddLocationFragment();
            bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
        });
        return binding.getRoot();
    }
}
