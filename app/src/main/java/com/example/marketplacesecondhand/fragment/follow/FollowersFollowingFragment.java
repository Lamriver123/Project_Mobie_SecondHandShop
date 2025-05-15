package com.example.marketplacesecondhand.fragment.follow;

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

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.API.DatabaseHandler;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.service.RetrofitClient;
import com.example.marketplacesecondhand.adapter.follow.FollowersFollowingPagerAdapter;
import com.example.marketplacesecondhand.databinding.FragmentFollowersFollowingBinding;
import com.example.marketplacesecondhand.dto.response.ShopResponse;
import com.example.marketplacesecondhand.models.UserLoginInfo;
import com.example.marketplacesecondhand.viewModel.ShopViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class FollowersFollowingFragment extends Fragment {
    private FragmentFollowersFollowingBinding binding;
    private ShopViewModel shopViewModel;
    private APIService apiService;
    private int currentTab = 0; // 0 for followers, 1 for following
    private ShopResponse currentShop;

    public FollowersFollowingFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        binding = FragmentFollowersFollowingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Hide bottom navigation
        if (getActivity() != null) {
            getActivity().findViewById(R.id.bottom_nav_view).setVisibility(View.GONE);
        }

        // Setup header
        setupHeader();

        // Get the tab position from arguments
        if (getArguments() != null) {
            currentTab = getArguments().getInt("tab_position", 0);
        }

        // Initialize ViewModel
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        shopViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ShopViewModel(apiService);
            }
        }).get(ShopViewModel.class);

        DatabaseHandler dbHandler = new DatabaseHandler(getContext());
        UserLoginInfo userInfo = dbHandler.getLoginInfoSQLite();
        if (userInfo != null) {
            setupViewPager();
            setupTabLayout();
            setupObservers();
            loadCurrentUserShop();
        }
    }

    private void setupHeader() {
        binding.icBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().findViewById(R.id.bottom_nav_view).setVisibility(View.VISIBLE);
                getActivity().onBackPressed();
            }
        });
    }

    private void setupViewPager() {
        // Create two fragments for followers and following
        FollowersListFragment followersFragment = new FollowersListFragment();
        FollowingListFragment followingFragment = new FollowingListFragment();

        // Create a ViewPager adapter
        FollowersFollowingPagerAdapter pagerAdapter = new FollowersFollowingPagerAdapter(this);
        pagerAdapter.addFragment(followersFragment, "Người theo dõi");
        pagerAdapter.addFragment(followingFragment, "Đang theo dõi");

        binding.viewPager.setAdapter(pagerAdapter);
        // Set the current item before attaching the TabLayoutMediator
        binding.viewPager.setCurrentItem(currentTab, false);

        // Add page change callback
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentTab = position;
                if (currentShop != null) {
                    updateCurrentTabData();
                }
            }
        });
    }

    private void setupTabLayout() {
        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText("Người theo dõi");
                    } else {
                        tab.setText("Đang theo dõi");
                    }
                }).attach();

        // Ensure the correct tab is selected
        binding.tabLayout.getTabAt(currentTab).select();

        // Add tab selected listener
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                if (currentShop != null) {
                    updateCurrentTabData();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (currentShop != null) {
                    updateCurrentTabData();
                }
            }
        });
    }

    private void updateCurrentTabData() {
        if (currentShop == null) return;

        FollowersListFragment followersFragment = (FollowersListFragment) 
            getChildFragmentManager().findFragmentByTag("f0");
        FollowingListFragment followingFragment = (FollowingListFragment) 
            getChildFragmentManager().findFragmentByTag("f1");

        if (currentTab == 0 && followersFragment != null) {
            followersFragment.updateFollowers(currentShop.getFollowerIds());
        } else if (currentTab == 1 && followingFragment != null) {
            followingFragment.updateFollowing(currentShop.getFollowingIds());
        }
    }

    private void setupObservers() {
        shopViewModel.getCurrentShop().observe(getViewLifecycleOwner(), shop -> {
            if (shop != null) {
                currentShop = shop;
                updateCurrentTabData();
            }
        });

        shopViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCurrentUserShop() {
        shopViewModel.loadCurrentUserShop();
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