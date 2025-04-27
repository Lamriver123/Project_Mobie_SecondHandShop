package com.example.marketplacesecondhand.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.RetrofitClient;
import com.example.marketplacesecondhand.adapter.ProductCategoryAdapter;
import com.example.marketplacesecondhand.databinding.FragmentProductCategoryBinding;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.ProductResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductCategoryFragment extends Fragment {
    private FragmentProductCategoryBinding binding;
    private APIService apiService;

    public ProductCategoryFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProductCategoryBinding.inflate(inflater, container, false);
        setupRecyclerView();
        return binding.getRoot();
    }

    private void setupRecyclerView() {
        // Tạo GridLayoutManager với 1 cột
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        binding.rvProductCategory.setLayoutManager(layoutManager);

        // Thêm ItemDecoration để tạo khoảng cách giữa các items
        binding.rvProductCategory.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, 
                                     @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int spacing = 16; // Khoảng cách 16dp giữa các items
                outRect.left = spacing;
                outRect.right = spacing;
                outRect.top = spacing / 2;
                outRect.bottom = spacing / 2;
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int categoryId = getArguments() != null ? getArguments().getInt("category_id", -1) : -1;
        int minPrice = getArguments() != null ? getArguments().getInt("min_price", -1) : -1;
        int maxPrice = getArguments() != null ? getArguments().getInt("max_price", -1) : -1;

        if (categoryId != -1) {
            if (minPrice != -1 && maxPrice != -1) {
                fetchProductByPrice(categoryId, minPrice, maxPrice);
            } else {
                fetchProductByCategory(categoryId);
            }
        } else {
            Log.e("Fragment", "categoryId không tồn tại trong Bundle");
        }
    }

    private void fetchProductByPrice(int categoryId, int minPrice, int maxPrice) {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<ApiResponse<List<ProductResponse>>> call = apiService.filterProductsByPrice(categoryId, minPrice, maxPrice);

        call.enqueue(new Callback<ApiResponse<List<ProductResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ProductResponse>>> call, Response<ApiResponse<List<ProductResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductResponse> products = response.body().getData();
                    ProductCategoryAdapter adapter = new ProductCategoryAdapter(getContext(), products, product -> {
                        // Xử lý khi người dùng click vào product
                        Toast.makeText(getContext(),"Sản phẩm được chọn: " + product.getProductName(), Toast.LENGTH_SHORT).show();
                    });

                    binding.rvProductCategory.setAdapter(adapter);
                } else {
                    Log.e("API", "Lỗi dữ liệu lọc giá");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ProductResponse>>> call, Throwable t) {
                Log.e("API", "Lỗi kết nối lọc giá: " + t.getMessage());
            }
        });
    }

    private void fetchProductByCategory(int categoryId) {
        if (binding.rvProductCategory == null) {
            Log.e("BINDING", "categoryRecycler is null - check fragment_category.xml");
            return;
        }
        
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<ApiResponse<List<ProductResponse>>> call = apiService.getProductsByCategory(categoryId);

        call.enqueue(new Callback<ApiResponse<List<ProductResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ProductResponse>>> call, Response<ApiResponse<List<ProductResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductResponse> products = response.body().getData();
                    ProductCategoryAdapter adapter = new ProductCategoryAdapter(getContext(), products, product -> {
                        // Xử lý khi người dùng click vào product
                        Toast.makeText(getContext(),"Sản phẩm được chọn: " + product.getProductName(), Toast.LENGTH_SHORT).show();
                    });

                    binding.rvProductCategory.setAdapter(adapter);
                    Log.d("API", "Số lượng sản phẩm: " + products.size());
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
