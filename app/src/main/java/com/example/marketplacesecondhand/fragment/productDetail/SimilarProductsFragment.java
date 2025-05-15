package com.example.marketplacesecondhand.fragment.productDetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.service.RetrofitClient;
import com.example.marketplacesecondhand.adapter.product.ProductAdapter;
import com.example.marketplacesecondhand.databinding.FragmentSimilarProductsBinding;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.ProductResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SimilarProductsFragment extends Fragment {
    private FragmentSimilarProductsBinding binding;
    private ProductAdapter productAdapter;
    private APIService apiService;
    private List<ProductResponse> productList = new ArrayList<>();
    private String categoryName;
    private int currentProductId;

    public SimilarProductsFragment() {
        // Required empty public constructor
    }

    public static SimilarProductsFragment newInstance(String categoryName, int currentProductId) {
        SimilarProductsFragment fragment = new SimilarProductsFragment();
        Bundle args = new Bundle();
        args.putString("categoryName", categoryName);
        args.putInt("currentProductId", currentProductId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryName = getArguments().getString("categoryName");
            currentProductId = getArguments().getInt("currentProductId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSimilarProductsBinding.inflate(inflater, container, false);
        fetchSimilarProducts();
        return binding.getRoot();
    }

    private void fetchSimilarProducts() {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<ApiResponse<List<ProductResponse>>> call = apiService.getAllProducts(); // hoặc API riêng lấy theo categoryId

        call.enqueue(new Callback<ApiResponse<List<ProductResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ProductResponse>>> call, Response<ApiResponse<List<ProductResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductResponse> allProducts = response.body().getData();
                    List<ProductResponse> similarProducts = new ArrayList<>();

                    for (ProductResponse product : allProducts) {
                        if (product.getCategoryName().equals(categoryName) && product.getProductId() != currentProductId) {
                            similarProducts.add(product);
                        }
                    }

                    ProductAdapter adapter = new ProductAdapter(getContext(), similarProducts);

                    binding.productRecycler.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.HORIZONTAL, false));
                    binding.productRecycler.setAdapter(adapter);

                } else {
                    Toast.makeText(getContext(), "Lỗi tải sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ProductResponse>>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}