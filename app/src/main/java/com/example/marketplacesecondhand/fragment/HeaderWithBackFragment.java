package com.example.marketplacesecondhand.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.example.marketplacesecondhand.CartActivity;
import com.example.marketplacesecondhand.FavoritesActivity;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.databinding.FragmentHeaderWithBackBinding;

public class HeaderWithBackFragment extends Fragment {
    private FragmentHeaderWithBackBinding binding;
    private SearchHandler searchHandler;
    private static final String TAG = "HeaderWithBackFragment";

    public interface SearchHandler {
        void performSearch(String query);
    }

    public void setSearchHandler(SearchHandler listener) {
        this.searchHandler = listener;
        Log.d(TAG, "SearchHandler explicitly set.");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof SearchHandler) {
            if (this.searchHandler == null) {
                this.searchHandler = (SearchHandler) context;
                Log.d(TAG, "SearchHandler được gắn thông qua onAttach vì chưa được đặt trước đó.");
            } else {
                Log.d(TAG, "SearchHandler đã được đặt, onAttach xác nhận Activity triển khai nó.");
            }
        } else {
            Log.e(TAG, context.toString() + " must implement SearchHandler");
            throw new RuntimeException(context.toString() + " must implement SearchHandler");
        }
    }

    public HeaderWithBackFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHeaderWithBackBinding.inflate(inflater, container, false);

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

        setupSearch();
        if (getArguments() != null) {
            String keyword = getArguments().getString("keyword", "");
            binding.editTextSearch.setQuery(keyword, false);
        }

        return binding.getRoot();
    }



    private void setupSearch() {
        binding.editTextSearch.setIconified(false);
        ImageView searchIcon = binding.editTextSearch.findViewById(androidx.appcompat.R.id.search_mag_icon);
        searchIcon.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        searchIcon.setVisibility(View.GONE);
        binding.editTextSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (searchHandler != null) {
                    Log.i(TAG, "Search submitted with query: '" + query + "'");
                    searchHandler.performSearch(query);
                } else {
                    Log.w(TAG, "SearchHandler is null, cannot perform search.");
                }
                binding.editTextSearch.clearFocus(); // Ẩn bàn phím
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Không làm gì khi text thay đổi, chỉ xử lý khi submit
                return false;
            }
        });
    }

    public void clearSearchQuery() {
        if (binding != null && binding.editTextSearch != null) {
            binding.editTextSearch.setQuery("", false); // false để không trigger onQueryTextSubmit
            Log.d(TAG, "Search query cleared.");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        searchHandler = null; // Quan trọng: tránh memory leak
        Log.d(TAG, "onDetach - SearchHandler detached.");
    }
}
