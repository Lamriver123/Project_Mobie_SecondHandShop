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
import com.example.marketplacesecondhand.RetrofitClient;
import com.example.marketplacesecondhand.adapter.CategoryAdapter;
import com.example.marketplacesecondhand.adapter.ProductAdapter;
import com.example.marketplacesecondhand.databinding.FragmentCategoryBinding;
import com.example.marketplacesecondhand.databinding.FragmentShopProductBinding;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.ProductResponse;
import com.example.marketplacesecondhand.models.Category;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductShopFragment extends Fragment {
    private FragmentShopProductBinding binding;
    private APIService apiService;

    public ProductShopFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentShopProductBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //    init(view);
        fetchCategory();
    }


    private void fetchCategory() {
        if (binding.productRecycler == null) {
            Log.e("BINDING", "categoryRecycler is null - check fragment_category.xml");
        }
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<ApiResponse<List<ProductResponse>>> call = apiService.getAllProducts();

        call.enqueue(new Callback<ApiResponse<List<ProductResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ProductResponse>>> call, Response<ApiResponse<List<ProductResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductResponse> products = response.body().getData();
                    ProductAdapter adapter = new ProductAdapter(getContext(), products);

                    binding.productRecycler.setAdapter(adapter);
                    binding.productRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
                    // TODO: Hiển thị categories lên UI (ví dụ RecyclerView)
                    Log.d("API", "Số lượng category: " + products.size());
                } else {
                    Log.e("API", "Lỗi dữ liệu hoặc response body null");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ProductResponse>>> call, Throwable t) {
                Log.e("API", "Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}
