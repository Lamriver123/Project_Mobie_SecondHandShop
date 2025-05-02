package com.example.marketplacesecondhand.fragment.favorite;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.API.DatabaseHandler;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.RetrofitClient;
import com.example.marketplacesecondhand.adapter.ProductAdapter;
import com.example.marketplacesecondhand.adapter.favorite.FavoriteAdapter;
import com.example.marketplacesecondhand.databinding.FragmentFavoriteContainerBinding;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.ProductResponse;
import com.example.marketplacesecondhand.fragment.SimilarProductsFragment;
import com.example.marketplacesecondhand.models.UserLoginInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteContainerFragment extends Fragment {
    private FragmentFavoriteContainerBinding binding;
    private APIService apiService;

    public FavoriteContainerFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFavoriteContainerBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("FavoriteFragment", "Fragment loaded");
        
        // Thêm LayoutManager cho RecyclerView
        binding.recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(getContext()));
        
        int userId = getUserId();
        if (userId != 0) {
            fetchFavoriteByUserId(userId);
        }
    }

    private int getUserId() {
        DatabaseHandler db = new DatabaseHandler(getContext());
        UserLoginInfo userLoginInfo = db.getLoginInfoSQLite();

        if (userLoginInfo == null || userLoginInfo.getUserId() == 0) {
            Toast.makeText(getContext(), "Bạn cần đăng nhập để xem sản phẩm yêu thích!", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
            return 0;
        }

        return userLoginInfo.getUserId();
    }

    private void fetchFavoriteByUserId(int userId) {
        Log.d("FavoriteFragment", "Fetching favorites for userId: " + userId);
        apiService = RetrofitClient.getRetrofit().create(APIService.class);

        Call<ApiResponse<List<Integer>>> call = apiService.getFavoriteProductIds(userId);
        call.enqueue(new Callback<ApiResponse<List<Integer>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Integer>>> call, Response<ApiResponse<List<Integer>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<Integer> favoriteProductIds = response.body().getData();
                    Log.d("FavoriteFragment", "Got favorite product IDs: " + favoriteProductIds.size());

                    if (favoriteProductIds.isEmpty()) {
                        Log.d("FavoriteFragment", "No favorite products found");
                        binding.tvNoFavorites.setVisibility(View.VISIBLE);
                        binding.recyclerViewFavorites.setVisibility(View.GONE);
                        return;
                    }

                    binding.tvNoFavorites.setVisibility(View.GONE);
                    binding.recyclerViewFavorites.setVisibility(View.VISIBLE);
                    fetchProductDetails(favoriteProductIds);
                } else {
                    Log.e("FavoriteFragment", "Error response: " + (response.body() != null ? response.body().getMessage() : "null response"));
                    Toast.makeText(getContext(), "Không thể lấy danh sách sản phẩm yêu thích", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Integer>>> call, Throwable t) {
                Log.e("FavoriteFragment", "Network error: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchProductDetails(List<Integer> productIds) {
        Log.d("FavoriteFragment", "Fetching product details for " + productIds.size() + " products");

        Call<ApiResponse<List<ProductResponse>>> call = apiService.getProductsByProductIds(productIds);
        call.enqueue(new Callback<ApiResponse<List<ProductResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ProductResponse>>> call, Response<ApiResponse<List<ProductResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<ProductResponse> favoriteProducts = response.body().getData();
                    Log.d("FavoriteFragment", "Got " + favoriteProducts.size() + " product details");

                    if (favoriteProducts.isEmpty()) {
                        Log.d("FavoriteFragment", "No product details found");
                        binding.tvNoFavorites.setVisibility(View.VISIBLE);
                        binding.recyclerViewFavorites.setVisibility(View.GONE);
                        return;
                    }

                    binding.tvNoFavorites.setVisibility(View.GONE);
                    binding.recyclerViewFavorites.setVisibility(View.VISIBLE);

                    FavoriteAdapter adapter = new FavoriteAdapter(
                            getContext(),
                            favoriteProducts,
                            productIds,
                            product -> {
                                Toast.makeText(getContext(),"Sản phẩm được chọn: " + product.getProductName(), Toast.LENGTH_SHORT).show();
                            }
                    );

                    binding.recyclerViewFavorites.setAdapter(adapter);
                    Log.d("FavoriteFragment", "Adapter set with " + favoriteProducts.size() + " items");

                    // Lấy danh sách categoryId từ danh sách sản phẩm yêu thích
                    List<Integer> categoryIds = favoriteProducts.stream()
                            .map(ProductResponse::getCategoryId)
                            .distinct()
                            .collect(Collectors.toList());

                    if (isAdded()) {
                        RecommendedProductsFragment recommendedFragment = RecommendedProductsFragment.newInstance(categoryIds);
                        getParentFragmentManager()
                                .beginTransaction()
                                .replace(R.id.recommended_products, recommendedFragment)
                                .commit();
                    }

                } else {
                    Log.e("FavoriteFragment", "Error getting product details: " + (response.body() != null ? response.body().getMessage() : "null response"));
                    Toast.makeText(getContext(), "Không thể lấy thông tin sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ProductResponse>>> call, Throwable t) {
                Log.e("FavoriteFragment", "Network error getting product details: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi kết nối khi lấy thông tin sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
