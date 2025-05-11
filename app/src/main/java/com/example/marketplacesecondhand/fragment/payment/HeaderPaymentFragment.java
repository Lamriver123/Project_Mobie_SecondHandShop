package com.example.marketplacesecondhand.fragment.payment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.marketplacesecondhand.databinding.FragmentHeaderPaymentBinding;

public class HeaderPaymentFragment extends Fragment {
    private FragmentHeaderPaymentBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHeaderPaymentBinding.inflate(inflater, container, false);

        binding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().onBackPressed();
            }
        });
        return binding.getRoot();
    }
}
