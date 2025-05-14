package com.example.marketplacesecondhand.fragment.follow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.service.RetrofitClient;
import com.example.marketplacesecondhand.adapter.follow.FollowAdapter;
import com.example.marketplacesecondhand.databinding.FragmentFollowersListBinding;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.UserResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowersListFragment extends Fragment {
    private FragmentFollowersListBinding binding;
    private FollowAdapter followAdapter;
    private APIService apiService;
    private List<UserResponse> followersList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        binding = FragmentFollowersListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        binding.recyclerViewFollowers.setLayoutManager(new LinearLayoutManager(getContext()));
        followAdapter = new FollowAdapter();
        binding.recyclerViewFollowers.setAdapter(followAdapter);
    }

    public void updateFollowers(List<Integer> followerIds) {
        if (followerIds == null || followerIds.isEmpty()) {
            binding.emptyView.setVisibility(View.VISIBLE);
            binding.recyclerViewFollowers.setVisibility(View.GONE);
            followersList.clear();
            followAdapter.updateUsers(followersList);
        } else {
            binding.emptyView.setVisibility(View.GONE);
            binding.recyclerViewFollowers.setVisibility(View.VISIBLE);
            followersList.clear();
            loadFollowersDetails(followerIds);
        }
    }

    private void loadFollowersDetails(List<Integer> followerIds) {
        if (followerIds.isEmpty()) {
            followAdapter.updateUsers(followersList);
            return;
        }

        AtomicInteger completedRequests = new AtomicInteger(0);
        int totalRequests = followerIds.size();
        List<UserResponse> tempList = new ArrayList<>();

        for (Integer userId : followerIds) {
            apiService.getShopInfo(userId).enqueue(new Callback<ApiResponse<UserResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        UserResponse user = response.body().getData();
                        tempList.add(user);
                    }

                    // Check if all requests are completed
                    if (completedRequests.incrementAndGet() == totalRequests) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                followersList.clear();
                                followersList.addAll(tempList);
                                followAdapter.updateUsers(followersList);
                            });
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                    Toast.makeText(getContext(), "Lỗi khi tải thông tin người dùng", Toast.LENGTH_SHORT).show();
                    
                    // Check if all requests are completed even if there's an error
                    if (completedRequests.incrementAndGet() == totalRequests) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                followersList.clear();
                                followersList.addAll(tempList);
                                followAdapter.updateUsers(followersList);
                            });
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 