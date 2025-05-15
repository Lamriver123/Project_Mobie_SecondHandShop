package com.example.marketplacesecondhand.fragment.review;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.adapter.feedback.FeedbackAdapter;
import com.example.marketplacesecondhand.databinding.FragmentReviewListBinding;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.FeedbackResponse;
import com.example.marketplacesecondhand.service.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewListFragment extends Fragment {
    private static final String ARG_TYPE = "type";
    private int type; // 0: tất cả, 1: mình đánh giá, 2: người khác đánh giá mình

    private FragmentReviewListBinding binding;
    private FeedbackAdapter adapter;
    private List<FeedbackResponse> feedbackList = new ArrayList<>();

    public static ReviewListFragment newInstance(int type) {
        ReviewListFragment fragment = new ReviewListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt(ARG_TYPE, 0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReviewListBinding.inflate(inflater, container, false);

        adapter = new FeedbackAdapter(getContext(), feedbackList);
        binding.recyclerViewReview.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewReview.setAdapter(adapter);

        loadFeedback();

        return binding.getRoot();
    }

    private void loadFeedback() {
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<ApiResponse<List<FeedbackResponse>>> call;
        if (type == 0) {
            call = apiService.getAllUserRelatedFeedback();
        } else if (type == 1) {
            call = apiService.getFeedbackGivenByCurrentUser();
        } else {
            call = apiService.getFeedbackReceivedByCurrentUser();
        }

        call.enqueue(new Callback<ApiResponse<List<FeedbackResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<FeedbackResponse>>> call, Response<ApiResponse<List<FeedbackResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<FeedbackResponse> data = response.body().getData();
                    feedbackList.clear();
                    feedbackList.addAll(data);
                    adapter.notifyDataSetChanged();

                    if (data.isEmpty()) {
                        binding.recyclerViewReview.setVisibility(View.GONE);
                        binding.tvEmpty.setVisibility(View.VISIBLE);
                    } else {
                        binding.recyclerViewReview.setVisibility(View.VISIBLE);
                        binding.tvEmpty.setVisibility(View.GONE);
                    }
                } else {
                    binding.recyclerViewReview.setVisibility(View.GONE);
                    binding.tvEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<FeedbackResponse>>> call, Throwable t) {
                binding.recyclerViewReview.setVisibility(View.GONE);
                binding.tvEmpty.setVisibility(View.VISIBLE);
            }
        });
    }
} 