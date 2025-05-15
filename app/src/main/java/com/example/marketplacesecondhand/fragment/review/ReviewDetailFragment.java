package com.example.marketplacesecondhand.fragment.review;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import com.bumptech.glide.Glide;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.databinding.FragmentReviewDetailBinding;
import com.example.marketplacesecondhand.service.RetrofitClient;
import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.dto.response.ShopResponse;
import com.example.marketplacesecondhand.viewModel.ShopViewModel;

public class ReviewDetailFragment extends Fragment {
    private FragmentReviewDetailBinding binding;
    private ShopViewModel shopViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReviewDetailBinding.inflate(inflater, container, false);

        // Hide bottom navigation
        if (getActivity() != null) {
            getActivity().findViewById(R.id.bottom_nav_view).setVisibility(View.GONE);
        }

        // Setup header
        setupHeader();

        // Khởi tạo ShopViewModel
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        shopViewModel = new ViewModelProvider(requireActivity(), new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ShopViewModel(apiService);
            }
        }).get(ShopViewModel.class);

        // Observe ShopResponse
        shopViewModel.getCurrentShop().observe(getViewLifecycleOwner(), shop -> {
            if (shop != null) {
                binding.tvUserName.setText(shop.getUsername());
                binding.ratingBar.setRating((float) shop.getAverageRating());
                binding.tvRatingScore.setText(String.valueOf(shop.getAverageRating()));
                binding.tvReviewCount.setText(shop.getTotalReviews() + " đánh giá");

                // Load shop avatar using Glide
                Glide.with(binding.getRoot().getContext())
                        .load(shop.getAvt())
                        .circleCrop()
                        .error(R.drawable.bg1)
                        .placeholder(R.drawable.bg_shape)
                        .into(binding.imgShop);
            }
        });
        shopViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
        shopViewModel.loadCurrentUserShop();

        // Setup TabLayout + ViewPager2
        ReviewPagerAdapter pagerAdapter = new ReviewPagerAdapter(this);
        binding.viewPager.setAdapter(pagerAdapter);
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("TẤT CẢ"); break;
                case 1: tab.setText("NGƯỜI BÁN"); break;
                case 2: tab.setText("NGƯỜI MUA"); break;
            }
        }).attach();

        // Xử lý nút back
        binding.icBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
                // Hiện lại footer nếu cần
                View bottomNav = getActivity().findViewById(R.id.bottom_nav_view);
                if (bottomNav != null) bottomNav.setVisibility(View.VISIBLE);
            }
        });

        return binding.getRoot();
    }
    private void setupHeader() {
        binding.icBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().findViewById(R.id.bottom_nav_view).setVisibility(View.VISIBLE);
                getActivity().onBackPressed();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Show bottom navigation when leaving the fragment
        if (getActivity() != null) {
            getActivity().findViewById(R.id.bottom_nav_view).setVisibility(View.VISIBLE);
        }
        binding = null;
    }
} 