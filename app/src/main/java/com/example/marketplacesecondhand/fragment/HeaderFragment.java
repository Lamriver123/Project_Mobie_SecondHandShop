package com.example.marketplacesecondhand.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.marketplacesecondhand.CartActivity;
import com.example.marketplacesecondhand.FavoritesActivity;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.databinding.FragmentHeaderBinding;

public class HeaderFragment extends Fragment {
    private FragmentHeaderBinding binding;

    public HeaderFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHeaderBinding.inflate(inflater, container, false);

        binding.btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), CartActivity.class);
            startActivity(intent);
        });

        binding.ivFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), FavoritesActivity.class);
            startActivity(intent);
        });

        return binding.getRoot();
    }
}
