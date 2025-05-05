package com.example.marketplacesecondhand.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.API.DatabaseHandler;
import com.example.marketplacesecondhand.LoginActivity;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.RetrofitClient;
import com.example.marketplacesecondhand.databinding.FragmentProfileBinding;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.ProductResponse;
import com.example.marketplacesecondhand.dto.response.UserResponse;
import com.example.marketplacesecondhand.models.UserLoginInfo;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private APIService apiService;
    private TextView tvLogout, editProfileOption, termsConditions, tvUsername, tvPhoneNumber, tvEmail;;
    private ImageView imgAvatar;
    private LinearLayout profileInfoContainer, layoutRating, layoutFollowers;;
    private UserLoginInfo userInfo;

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
//                    requireActivity().getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.content_frame, new ProfileFragment())
//                        .commit();
                }
            });

            // Tìm TextView Edit Profile và thêm sự kiện click
            editProfileOption.setOnClickListener(v -> {
                // Ẩn ViewPager và header, hiện container fragment
                requireActivity().findViewById(R.id.content_frame).setVisibility(View.GONE);
                requireActivity().findViewById(R.id.header_fragment).setVisibility(View.GONE);
                requireActivity().findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);

                // Tạo instance mới của EditProfileFragment
                EditProfileFragment editProfileFragment = new EditProfileFragment();

                // Bắt đầu transaction chuyển fragment
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();

                // Thay thế fragment hiện tại bằng EditProfileFragment và thêm vào back stack
                transaction.replace(R.id.fragment_container, editProfileFragment);
                transaction.addToBackStack(null);

                // Thực hiện transaction
                transaction.commit();
            });


        }

        // Tìm TextView Terms and Conditions và thêm sự kiện click
        termsConditions.setOnClickListener(v -> {
            // Ẩn ViewPager và header, hiện container fragment
            requireActivity().findViewById(R.id.content_frame).setVisibility(View.GONE);
            requireActivity().findViewById(R.id.header_fragment).setVisibility(View.GONE);
            requireActivity().findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);

            // Tạo instance mới của TermsConditionsFragment
            TermsConditionsFragment termsConditionsFragment = new TermsConditionsFragment();

            // Bắt đầu transaction chuyển fragment
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();

            // Thay thế fragment hiện tại bằng TermsConditionsFragment và thêm vào back stack
            transaction.replace(R.id.fragment_container, termsConditionsFragment);
            transaction.addToBackStack(null);

            // Thực hiện transaction
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
    }

    void loadInfo() {
      //  Log.d("API", "Token: " + RetrofitClient.currentToken);
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<ApiResponse<UserResponse>> call = apiService.getMyInfo();

        call.enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse user = response.body().getData();

                    // Truyền dữ liệu vào view
                    tvUsername.setText(user.getFullName());
                    tvPhoneNumber.setText(user.getPhoneNumber());
                    tvEmail.setText(user.getEmail());

                    // Nếu có link avatar thì load bằng Glide
//                    if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
//                        Glide.with(requireContext())
//                                .load(user.getAvatarUrl())
//                                .placeholder(R.drawable.default_avatar) // ảnh mặc định nếu chưa có
//                                .error(R.drawable.default_avatar)       // nếu load thất bại
//                                .into(imgAvatar);
//                    }

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
                    }
                }
        );

    }
}
