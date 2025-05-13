package com.example.marketplacesecondhand.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.flexbox.FlexboxLayout;
import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.RetrofitClient;
import com.example.marketplacesecondhand.models.Category;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.R;
import android.util.Log;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryBottomSheetFragment extends BottomSheetDialogFragment {
    private FlexboxLayout flexboxCategory;
    private APIService apiService;
    private OnCategorySelectedListener listener;
    private int selectedCategoryId = -1;

    public interface OnCategorySelectedListener {
        void onCategorySelected(Category category);
    }

    public void setOnCategorySelectedListener(OnCategorySelectedListener listener) {
        this.listener = listener;
    }

    public void setSelectedCategoryId(int categoryId) {
        this.selectedCategoryId = categoryId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_category, container, false);
        flexboxCategory = view.findViewById(R.id.flexboxCategory);
        fetchCategories();
        return view;
    }

    private void fetchCategories() {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<ApiResponse<List<Category>>> call = apiService.getCategories();

        call.enqueue(new Callback<ApiResponse<List<Category>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Category>>> call, Response<ApiResponse<List<Category>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body().getData();
                    categories.add(new Category(-1, "Tất cả",""));
                    displayCategories(categories);
                } else {
                    Log.e("API", "Lỗi dữ liệu hoặc response body null");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Category>>> call, Throwable t) {
                Log.e("API", "Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void displayCategories(List<Category> categories) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        flexboxCategory.removeAllViews();

        for (Category category : categories) {
            TextView categoryView = (TextView) inflater.inflate(R.layout.item_category_chip, flexboxCategory, false);
            categoryView.setText(category.getCategoryName());

            // Kiểm tra nếu là category đang được chọn
            if (category.getCategoryId() == selectedCategoryId) {
                categoryView.setBackgroundResource(R.drawable.bg_category_chip_selected);
                categoryView.setTextColor(getResources().getColor(R.color.yellow));
            }

            categoryView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCategorySelected(category);
                }
                dismiss();
            });

            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                    FlexboxLayout.LayoutParams.WRAP_CONTENT,
                    FlexboxLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 8, 8, 8);
            categoryView.setLayoutParams(params);

            flexboxCategory.addView(categoryView);
        }
    }
}