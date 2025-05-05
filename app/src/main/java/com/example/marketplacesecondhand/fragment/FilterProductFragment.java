package com.example.marketplacesecondhand.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.marketplacesecondhand.databinding.FragmentFilterProductBinding;


public class FilterProductFragment extends Fragment {
    private FragmentFilterProductBinding binding;

    public FilterProductFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle saveInstanceState) {
        binding = FragmentFilterProductBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }
}
