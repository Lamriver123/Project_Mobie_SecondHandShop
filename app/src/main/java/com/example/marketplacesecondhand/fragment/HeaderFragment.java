package com.example.marketplacesecondhand.fragment;

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

import com.example.marketplacesecondhand.activity.ActivityCategory;
import com.example.marketplacesecondhand.activity.CartActivity;
import com.example.marketplacesecondhand.activity.FavoritesActivity;
import com.example.marketplacesecondhand.databinding.FragmentHeaderBinding;

public class HeaderFragment extends Fragment {
    private FragmentHeaderBinding binding;
    private OnSearchListener searchListener;

    private boolean isSubmitted = false;

    public interface OnSearchListener {
        void onSearch(String query);
    }

    public void setOnSearchListener(OnSearchListener listener) {
        this.searchListener = listener;
    }

    public HeaderFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHeaderBinding.inflate(inflater, container, false);
        setupSearch();
        setupClickListener();

        return binding.getRoot();
    }

    private void setupClickListener(){
        binding.btnCart.setOnClickListener(v -> {
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

        binding.editTextSearch.requestFocus();

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
