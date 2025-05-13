package com.example.marketplacesecondhand.fragment.productDetail;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.example.marketplacesecondhand.ActivityCategory;
import com.example.marketplacesecondhand.CartActivity;
import com.example.marketplacesecondhand.FavoritesActivity;
import com.example.marketplacesecondhand.databinding.FragmentHeaderBinding;
import com.example.marketplacesecondhand.databinding.FragmentHeaderProductDetailBinding;

public class HeaderProductDetailFragment extends Fragment {
    private FragmentHeaderProductDetailBinding binding;
    private boolean isSubmitted = false;

    public HeaderProductDetailFragment() {}
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHeaderProductDetailBinding.inflate(inflater, container, false);
        setupSearch();
        setupClickListener();

        return binding.getRoot();
    }

    private void setupClickListener(){
        binding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().onBackPressed();
            }
        });

        binding.ivCart.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), CartActivity.class);
            startActivity(intent);
        });

        binding.ivFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), FavoritesActivity.class);
            startActivity(intent);
        });
    }
    private void setupSearch() {
        binding.editTextSearch.setIconified(false);
        ImageView searchIcon = binding.editTextSearch.findViewById(androidx.appcompat.R.id.search_mag_icon);
        searchIcon.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        searchIcon.setVisibility(View.GONE);

        binding.editTextSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!isSubmitted) {
                    isSubmitted = true;
                    Intent intent = new Intent(getContext(), ActivityCategory.class);
                    intent.putExtra("keyword", query);
                    intent.putExtra("category_id", -1);
                    intent.putExtra("min_price", -1);
                    intent.putExtra("max_price", -1);
                    startActivity(intent);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }



    @Override
    public void onResume() {
        super.onResume();
        isSubmitted = false;
        if (binding != null) {
            binding.editTextSearch.clearFocus();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
