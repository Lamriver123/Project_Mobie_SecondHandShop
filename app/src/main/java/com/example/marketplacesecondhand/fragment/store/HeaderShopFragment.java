package com.example.marketplacesecondhand.fragment.store;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.API.DatabaseHandler;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.RetrofitClient;
import com.example.marketplacesecondhand.databinding.FragmentHeaderShopBinding;
import com.example.marketplacesecondhand.dto.response.ShopResponse;
import com.example.marketplacesecondhand.models.UserLoginInfo;
import com.example.marketplacesecondhand.viewModel.ShopViewModel;

public class HeaderShopFragment extends Fragment {
    private FragmentHeaderShopBinding binding;
    private ShopViewModel shopViewModel;
    private ShopResponse currentShop;

    public HeaderShopFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHeaderShopBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        shopViewModel = new ViewModelProvider(requireActivity(), new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ShopViewModel(apiService);
            }
        }).get(ShopViewModel.class);

        // Set up observers
        shopViewModel.getCurrentShop().observe(getViewLifecycleOwner(), shop -> {
            if (shop != null) {
                currentShop = shop;
                binding.txtShopName.setText(shop.getUsername());
                binding.ratingBar.setRating((float) shop.getAverageRating());
                binding.txtFollowerCount.setText(shop.getFollowerIds().size() + " người theo dõi");

                // Load shop avatar
                Glide.with(requireContext())
                        .load(shop.getAvt())
                        .circleCrop()
                        .error(R.drawable.bg1)
                        .placeholder(R.drawable.bg_shape)
                        .into(binding.imgShopAvatar);

                // Set up follow button state and click listener
                DatabaseHandler db = new DatabaseHandler(requireContext());
                UserLoginInfo userLoginInfo = db.getLoginInfoSQLite();
                if (userLoginInfo != null) {
                    boolean isFollowing = shop.getFollowerIds() != null && 
                                        shop.getFollowerIds().contains(userLoginInfo.getUserId());
                    updateFollowButtonState(isFollowing);
                }

                binding.btnFollow.setOnClickListener(v -> {
                    if (currentShop != null) {
                        shopViewModel.toggleFollow(currentShop.getId());
                    }
                });
            }
        });

        shopViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        // Set up back button
        binding.icBack.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void updateFollowButtonState(boolean isFollowing) {
        if (isFollowing) {
            binding.btnFollow.setText("Đang theo dõi");
            binding.btnFollow.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.grey_dark));
        } else {
            binding.btnFollow.setText("Theo dõi");
            binding.btnFollow.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.white));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
