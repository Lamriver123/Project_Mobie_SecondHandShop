package com.example.marketplacesecondhand.fragment.productDetail;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.service.RetrofitClient;
import com.example.marketplacesecondhand.adapter.feedback.FeedbackAdapter;
import com.example.marketplacesecondhand.databinding.FragmentFeedbackBinding;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.FeedbackResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackFragment extends Fragment {

    private FragmentFeedbackBinding binding;
    private FeedbackAdapter feedbackAdapter;

    private int productId;
    private APIService apiService;

    public static FeedbackFragment newInstance(int productId) {
        FeedbackFragment fragment = new FeedbackFragment();
        Bundle args = new Bundle();
        args.putInt("product_id", productId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.productId = getArguments().getInt("product_id");
        }
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFeedbackBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadFeedbacks();
    }

    private void loadFeedbacks() {
        binding.progressBarLoading.setVisibility(View.VISIBLE);
        binding.recyclerViewFeedback.setVisibility(View.GONE);
        binding.tvEmpty.setVisibility(View.GONE);

        Log.d("FeedbackFragment", "productId = " + productId);

        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<ApiResponse<List<FeedbackResponse>>> call = apiService.getFeedbackByProductId(productId);

        call.enqueue(new Callback<ApiResponse<List<FeedbackResponse>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<FeedbackResponse>>> call,
                                   @NonNull Response<ApiResponse<List<FeedbackResponse>>> response) {
                binding.progressBarLoading.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<FeedbackResponse> feedbacks = response.body().getData();

                    if (feedbacks == null || feedbacks.isEmpty()) {
                        binding.tvEmpty.setVisibility(View.VISIBLE);
                        binding.recyclerViewFeedback.setVisibility(View.GONE);
                    } else {
                        Log.d("FeedbackFragment", "so feedback = " + feedbacks.size());
                        binding.recyclerViewFeedback.setVisibility(View.VISIBLE);
                        binding.tvEmpty.setVisibility(View.GONE);
                        feedbackAdapter = new FeedbackAdapter(getContext(), feedbacks);
                        binding.recyclerViewFeedback.setLayoutManager(
                                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false)
                        );
                        binding.recyclerViewFeedback.setAdapter(feedbackAdapter);

                    }
                } else {
                    binding.tvEmpty.setText("Không thể tải đánh giá");
                    binding.tvEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<FeedbackResponse>>> call, @NonNull Throwable t) {
                binding.progressBarLoading.setVisibility(View.GONE);
                Log.e("FeedbackFragment", "Lỗi Retrofit: " + t.getMessage(), t);
                binding.tvEmpty.setText("Lỗi kết nối mạng");
                binding.tvEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
