package com.example.marketplacesecondhand.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;


    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // Gắn từng fragment con vào layout
//        requireActivity().getSupportFragmentManager().beginTransaction()
//                .replace(R.id.banner_fragment, new AdsFragment())
//                .replace(R.id.category_fragment, new CategoryFragment())
//                .replace(R.id.new_product_fragment, new NewProductFragment())
//                .commit();

        return binding.getRoot();
    }
}
