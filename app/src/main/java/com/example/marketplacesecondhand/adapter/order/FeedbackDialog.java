package com.example.marketplacesecondhand.adapter.order;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.service.RetrofitClient;
import com.example.marketplacesecondhand.dto.request.FeedbackRequest;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackDialog extends Dialog {
    private final int productId;
    private final int orderId;
    private final int userId;
    private OnFeedbackSubmittedListener listener;

    public interface OnFeedbackSubmittedListener {
        void onSubmitted(boolean success);
    }

    public void setOnFeedbackSubmittedListener(OnFeedbackSubmittedListener listener) {
        this.listener = listener;
    }

    public FeedbackDialog(@NonNull Context context, int productId, int orderId, int userId) {
        super(context);
        this.productId = productId;
        this.orderId = orderId;
        this.userId = userId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_feedback);

        TextInputEditText etFeedback = findViewById(R.id.etFeedback);
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        Button btnConfirm = findViewById(R.id.btnDialogConfirm);
        Button btnDismiss = findViewById(R.id.btnDialogDismiss);

        btnDismiss.setOnClickListener(v -> dismiss());

        btnConfirm.setOnClickListener(v -> {
            String feedbackText = etFeedback.getText().toString().trim();
            int starRating = (int) ratingBar.getRating();

            if (feedbackText.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập nội dung đánh giá", Toast.LENGTH_SHORT).show();
                return;
            }

            FeedbackRequest request = new FeedbackRequest(productId, userId, starRating, feedbackText, orderId);

            APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
            Call<ApiResponse<String>> call = apiService.saveFeedback(request);
            call.enqueue(new Callback<ApiResponse<String>>() {
                @Override
                public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getCode() == 1000) {
                        Toast.makeText(getContext(), "Đánh giá thành công!", Toast.LENGTH_SHORT).show();
                        if (listener != null) listener.onSubmitted(true);
                        dismiss();
                    } else {
                        Toast.makeText(getContext(), "Gửi đánh giá thất bại", Toast.LENGTH_SHORT).show();
                        if (listener != null) listener.onSubmitted(false);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                    Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    if (listener != null) listener.onSubmitted(false);
                }
            });
        });
    }
}
