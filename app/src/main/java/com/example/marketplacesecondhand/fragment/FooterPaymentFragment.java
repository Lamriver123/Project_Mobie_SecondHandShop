package com.example.marketplacesecondhand.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.marketplacesecondhand.databinding.FragmentFooterPaymentBinding;

public class FooterPaymentFragment extends Fragment {
    private FragmentFooterPaymentBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFooterPaymentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
