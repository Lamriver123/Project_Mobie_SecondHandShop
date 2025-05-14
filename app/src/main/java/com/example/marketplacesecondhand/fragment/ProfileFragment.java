package com.example.marketplacesecondhand.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.API.DatabaseHandler;
import com.example.marketplacesecondhand.LoginActivity;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.RetrofitClient;
import com.example.marketplacesecondhand.databinding.FragmentProfileBinding;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.UserResponse;
import com.example.marketplacesecondhand.fragment.follow.FollowersFollowingFragment;
import com.example.marketplacesecondhand.models.UserLoginInfo;
import com.example.marketplacesecondhand.viewModel.ShopViewModel;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private APIService apiService;
    private TextView tvLogout, editProfileOption, termsConditions, tvUsername, tvPhoneNumber, tvEmail;
    private TextView tvFollowers, tvFollowing;
    private ImageView imgAvatar;
    private LinearLayout profileInfoContainer, layoutRating, layoutFollowers;
    private RatingBar ratingBar;
    private UserLoginInfo userInfo;
    private ShopViewModel shopViewModel;

    public ProfileFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        mapping();

        DatabaseHandler dbHandler = new DatabaseHandler(getContext());
        userInfo = dbHandler.getLoginInfoSQLite();
        if (userInfo == null) {
            imgAvatar.setImageResource(R.drawable.user);
            tvUsername.setText("Đăng nhập / Đăng ký");
            tvPhoneNumber.setText("");
            tvEmail.setText("");

            tvLogout.setVisibility(View.GONE);
            editProfileOption.setVisibility(View.GONE);
            layoutRating.setVisibility(View.GONE);
            layoutFollowers.setVisibility(View.GONE);

            profileInfoContainer.setClickable(true);
            profileInfoContainer.setOnClickListener(v2 -> {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            });
        }
        else {
            loadInfo();
            setupShopViewModel();
            tvLogout.setVisibility(View.VISIBLE);
            editProfileOption.setVisibility(View.VISIBLE);
            layoutRating.setVisibility(View.VISIBLE);
            layoutFollowers.setVisibility(View.VISIBLE);

            tvLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dbHandler.clearAllLoginData();

                    imgAvatar.setImageResource(R.drawable.user);
                    tvUsername.setText("Đăng nhập / Đăng ký");
                    tvPhoneNumber.setText("");
                    tvEmail.setText("");

                    tvLogout.setVisibility(View.GONE);
                    editProfileOption.setVisibility(View.GONE);
                    layoutRating.setVisibility(View.GONE);
                    layoutFollowers.setVisibility(View.GONE);

                    profileInfoContainer.setClickable(true);
                    profileInfoContainer.setOnClickListener(v2 -> {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    });
                    Toast.makeText(requireContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                }
            });

            editProfileOption.setOnClickListener(v -> {
                requireActivity().findViewById(R.id.content_frame).setVisibility(View.GONE);
                requireActivity().findViewById(R.id.header_fragment).setVisibility(View.GONE);
                requireActivity().findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);

                EditProfileFragment editProfileFragment = new EditProfileFragment();

                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, editProfileFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            });
        }

        termsConditions.setOnClickListener(v -> {
            requireActivity().findViewById(R.id.content_frame).setVisibility(View.GONE);
            requireActivity().findViewById(R.id.header_fragment).setVisibility(View.GONE);
            requireActivity().findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);

            TermsConditionsFragment termsConditionsFragment = new TermsConditionsFragment();

            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, termsConditionsFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return binding.getRoot();
    }

    private void mapping() {
        tvLogout = binding.optionsContainer.findViewById(R.id.logout);
        editProfileOption = binding.optionsContainer.findViewById(R.id.editProfileOption);
        termsConditions = binding.optionsContainer.findViewById(R.id.termConditionsOption);
        imgAvatar = binding.getRoot().findViewById(R.id.imgAvt);
        tvUsername = binding.getRoot().findViewById(R.id.username);
        tvPhoneNumber = binding.getRoot().findViewById(R.id.tvPhoneNumber);
        tvEmail = binding.getRoot().findViewById(R.id.tvEmail);
        profileInfoContainer = binding.getRoot().findViewById(R.id.profileInfoContainer);
        layoutRating = binding.getRoot().findViewById(R.id.layoutRating);
        layoutFollowers = binding.getRoot().findViewById(R.id.layoutFollowers);
        
        tvFollowers = binding.getRoot().findViewById(R.id.tvFollowers);
        tvFollowing = binding.getRoot().findViewById(R.id.tvFollowing);
        ratingBar = binding.getRoot().findViewById(R.id.ratingBar);

        // Add click listeners for followers and following
        tvFollowers.setOnClickListener(v -> navigateToFollowersFollowing(0));
        tvFollowing.setOnClickListener(v -> navigateToFollowersFollowing(1));
    }

    private void navigateToFollowersFollowing(int tabPosition) {
        requireActivity().findViewById(R.id.content_frame).setVisibility(View.GONE);
        requireActivity().findViewById(R.id.header_fragment).setVisibility(View.GONE);
        requireActivity().findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);

        FollowersFollowingFragment fragment = new FollowersFollowingFragment();
        Bundle args = new Bundle();
        args.putInt("tab_position", tabPosition);
        fragment.setArguments(args);

        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setupShopViewModel() {
        if (userInfo == null || userInfo.getUserId() == 0) return;

        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        shopViewModel = new ViewModelProvider(requireActivity(), new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ShopViewModel(apiService);
            }
        }).get(ShopViewModel.class);

        // Observe current shop first
        shopViewModel.getCurrentShop().observe(getViewLifecycleOwner(), shop -> {
            if (shop != null) {
                // Update rating
                ratingBar.setRating((float) shop.getAverageRating());

                // Update followers count
                int followersCount = shop.getFollowerIds() != null ? shop.getFollowerIds().size() : 0;
                tvFollowers.setText(String.valueOf(followersCount) + " Người theo dõi");

                // Update following count (if available in your API)
                tvFollowing.setText(String.valueOf(shop.getFollowingIds().size()) + " Người đang theo dõi");
            }
        });

        // Observe error
        shopViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        // Load current user's shop directly
        shopViewModel.loadCurrentUserShop();
    }

    void loadInfo() {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<ApiResponse<UserResponse>> call = apiService.getMyInfo();

        call.enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse user = response.body().getData();

                    tvUsername.setText(user.getFullName());
                    tvPhoneNumber.setText(user.getPhoneNumber());
                    tvEmail.setText(user.getEmail());

                    if (user.getAvt() != null && !user.getAvt().isEmpty()) {
                        Glide.with(requireContext())
                                .load(user.getAvt())
                                .placeholder(R.drawable.user)
                                .error(R.drawable.user)
                                .into(imgAvatar);
                    }
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            ApiResponse<?> errorResponse = new Gson().fromJson(errorJson, ApiResponse.class);
                            Toast.makeText(getContext(), errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Lỗi không xác định!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Lỗi hệ thống!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                Log.e("API", "Failed to connect: " + t.getMessage());
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getParentFragmentManager().setFragmentResultListener(
                "profile_update_key",
                getViewLifecycleOwner(),
                (requestKey, result) -> {
                    boolean updated = result.getBoolean("profile_updated", false);
                    if (updated) {
                        loadInfo();
                        if (shopViewModel != null) {
                            shopViewModel.loadCurrentUserShop();
                        }
                    }
                }
        );
    }
}
