package com.example.marketplacesecondhand.fragment.favorite;

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
import com.example.marketplacesecondhand.API.DatabaseHandler;
import com.example.marketplacesecondhand.RetrofitClient;
import com.example.marketplacesecondhand.adapter.ProductAdapter;
import com.example.marketplacesecondhand.databinding.FragmentRecommendedProductsBinding;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.ProductResponse;
import com.example.marketplacesecondhand.fragment.SimilarProductsFragment;
import com.example.marketplacesecondhand.models.UserLoginInfo;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecommendedProductsFragment extends Fragment {
    private FragmentRecommendedProductsBinding binding;
    private APIService apiService;
    private List<Integer> categoryIds;

    public RecommendedProductsFragment() {}

    public static RecommendedProductsFragment newInstance(List<Integer> categoryIds) {
        RecommendedProductsFragment fragment = new RecommendedProductsFragment();
        Bundle args = new Bundle();
        args.putIntegerArrayList("categoryIds", new ArrayList<>(categoryIds));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryIds = getArguments().getIntegerArrayList("categoryIds");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRecommendedProductsBinding.inflate(inflater, container, false);

        if (categoryIds != null && !categoryIds.isEmpty()) {
            fetchRecommendedProducts(categoryIds);
        }

        return binding.getRoot();
    }

    private void fetchRecommendedProducts(List<Integer> categoryIds) {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<ApiResponse<List<ProductResponse>>> call = apiService.getRecommendedProducts(categoryIds);

        call.enqueue(new Callback<ApiResponse<List<ProductResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ProductResponse>>> call, Response<ApiResponse<List<ProductResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductResponse> products = response.body().getData();

                    fetchFavoriteProductIds(products);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ProductResponse>>> call, Throwable t) {
                Log.e("Recommended", "Error: " + t.getMessage());
            }
        });
    }

    private void fetchFavoriteProductIds(List<ProductResponse> products) {
        if (binding.recyclerViewRecommendations == null) {
            Log.e("BINDING", "recyclerViewRecommendations is null - check fragment_category.xml");
        }

        DatabaseHandler db = new DatabaseHandler(getContext());
        UserLoginInfo userLoginInfo = db.getLoginInfoSQLite();

        if (userLoginInfo != null && userLoginInfo.getUserId() != 0) {
            Call<ApiResponse<List<Integer>>> call = apiService.getFavoriteProductIds(userLoginInfo.getUserId());

            call.enqueue(new Callback<ApiResponse<List<Integer>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<Integer>>> call, Response<ApiResponse<List<Integer>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Integer> favoriteProductIds = response.body().getData();

                        ProductAdapter adapter = new ProductAdapter(getContext(), products, favoriteProductIds);
                        binding.recyclerViewRecommendations.setAdapter(adapter);
                        binding.recyclerViewRecommendations.setLayoutManager(new GridLayoutManager(getContext(), 2));
                    } else {
                        Log.e("API", "Lỗi dữ liệu hoặc response body null");
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<Integer>>> call, Throwable t) {
                    Log.e("API", "Lỗi kết nối: " + t.getMessage());
                }
            });
        }
        else {
            // Nếu user chưa login thì không cần load favorite
            ProductAdapter adapter = new ProductAdapter(getContext(), products);
            binding.recyclerViewRecommendations.setAdapter(adapter);
            binding.recyclerViewRecommendations.setLayoutManager(new GridLayoutManager(getContext(), 2));
        }
    }
}
