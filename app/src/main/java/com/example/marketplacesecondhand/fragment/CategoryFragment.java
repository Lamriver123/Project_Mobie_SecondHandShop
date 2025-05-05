package com.example.marketplacesecondhand.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.RetrofitClient;
import com.example.marketplacesecondhand.adapter.CategoryAdapter;
import com.example.marketplacesecondhand.databinding.FragmentCategoryBinding;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.models.Category;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryFragment extends Fragment {
    private FragmentCategoryBinding binding;
    private APIService apiService;

    public CategoryFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fetchCategory();
    }

    private void fetchCategory() {
        if (binding.categoryRecycler == null) {
            Log.e("BINDING", "categoryRecycler is null - check fragment_category.xml");
        }
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<ApiResponse<List<Category>>> call = apiService.getCategories();

        call.enqueue(new Callback<ApiResponse<List<Category>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Category>>> call, Response<ApiResponse<List<Category>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body().getData();
                    CategoryAdapter adapter = new CategoryAdapter(getContext(), categories);

                    binding.categoryRecycler.setLayoutManager(
                            new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
                    );

                    binding.categoryRecycler.setAdapter(adapter);
                    // TODO: Hiển thị categories lên UI (ví dụ RecyclerView)
                    Log.d("API", "Số lượng category: " + categories.size());
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

}
