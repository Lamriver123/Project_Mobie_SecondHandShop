package com.example.marketplacesecondhand.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.API.DatabaseHandler;
import com.example.marketplacesecondhand.service.RetrofitClient;
import com.example.marketplacesecondhand.adapter.category.ProductCategoryAdapter;
import com.example.marketplacesecondhand.databinding.FragmentProductCategoryBinding;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.ProductResponse;
import com.example.marketplacesecondhand.models.UserLoginInfo;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductCategoryFragment extends Fragment {
    private FragmentProductCategoryBinding binding;
    private APIService apiService;
    private ProductCategoryAdapter adapter;
    private List<ProductResponse> productList = new ArrayList<>();
    private List<Integer> favoriteProductIds = new ArrayList<>();

    private static final String TAG = "ProductCategoryFrag";

    public ProductCategoryFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProductCategoryBinding.inflate(inflater, container, false);
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Log.d(TAG, "onCreateView - Initializing...");
        setupRecyclerView();
        return binding.getRoot();
    }

    private void setupRecyclerView() {
        // Kiểm tra context trước khi sử dụng
        if (getContext() == null || !isAdded()) {
            Log.e(TAG, "setupRecyclerView: Context is null or Fragment not added!");
            return;
        }
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        binding.rvProductCategory.setLayoutManager(layoutManager);

        adapter = new ProductCategoryAdapter(getContext(), productList, product -> {
//            if (getContext() != null && isAdded()) {
//                Toast.makeText(getContext(), "Sản phẩm: " + product.getProductName(), Toast.LENGTH_SHORT).show();
//            }
        }, favoriteProductIds);
        binding.rvProductCategory.setAdapter(adapter);

        binding.rvProductCategory.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                       @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                if (getContext() == null || !isAdded()) return; // Kiểm tra context
                int spacing = 16;
                outRect.left = spacing;
                outRect.right = spacing;
                outRect.top = spacing / 2;
                outRect.bottom = spacing / 2;
            }
        });
        Log.d(TAG, "RecyclerView setup complete.");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated - View is ready.");
        Bundle args = getArguments();
        if (args != null) {
            String keyword = args.getString("keyword", "");
            int categoryId = args.getInt("category_id", -1);
            int minPrice = args.getInt("min_price", -1);
            int maxPrice = args.getInt("max_price", -1);
            fetchProductsWithFilters(keyword, categoryId, minPrice, maxPrice);
        } else {
            fetchProductsWithFilters("", -1, -1, -1);
        }
    }

    private void fetchProductsWithFilters(String keyword, int categoryId, int minPrice, int maxPrice) {
        if (binding == null) {
            Log.e(TAG, "fetchProductsWithFilters: Binding is null.");
            return;
        }
        if (!isAdded()) {
            Log.w(TAG, "fetchProductsWithFilters: Fragment not added. Aborting UI updates.");
            return;
        }

        binding.rvProductCategory.setVisibility(View.GONE);

        Integer apiCategoryId = categoryId;
        Integer apiMinPrice = minPrice;
        Integer apiMaxPrice = maxPrice;
        String apiKeyword = (keyword == null || keyword.trim().isEmpty()) ? "nullNull1511" : keyword.trim();


        Call<ApiResponse<List<ProductResponse>>> call = apiService.filterProducts(apiCategoryId, apiMinPrice, apiMaxPrice, apiKeyword);

        call.enqueue(new Callback<ApiResponse<List<ProductResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ProductResponse>>> call, Response<ApiResponse<List<ProductResponse>>> response) {
                if (!isAdded() || getContext() == null || binding == null) {
                    Log.w(TAG, "onResponse (fetchProducts): Fragment not valid (not added, null context, or null binding). Aborting.");
                    return;
                }

                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<ProductResponse> fetchedProducts = response.body().getData();
                    Log.i(TAG, "API Response (fetchProducts): " + fetchedProducts.size() + " products fetched successfully.");
                    fetchFavoriteProductIdsAndUpdateAdapter(fetchedProducts);
                } else {
                    String errorBodyString = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBodyString = response.errorBody().string();
                        }
                    } catch (Exception e) { Log.e(TAG, "Error reading error body: " + e.getMessage()); }
                    Log.e(TAG, "API Error (fetchProducts): Code=" + response.code() + ", Message=" + response.message() + ", ErrorBody=" + errorBodyString);

                    binding.rvProductCategory.setVisibility(View.GONE);
                    updateAdapterData(new ArrayList<>(), new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ProductResponse>>> call, Throwable t) {
                if (!isAdded() || getContext() == null || binding == null) {
                    Log.w(TAG, "onFailure (fetchProducts): Fragment not valid. Aborting.");
                    return;
                }
                Log.e(TAG, "API Failure (fetchProducts): " + t.getMessage(), t);

                binding.rvProductCategory.setVisibility(View.GONE);
                updateAdapterData(new ArrayList<>(), new ArrayList<>());
            }
        });
    }

    private void fetchFavoriteProductIdsAndUpdateAdapter(List<ProductResponse> newProducts) {
        // **Kiểm tra Fragment có còn hợp lệ không NGAY KHI BẮT ĐẦU PHƯƠNG THỨC NÀY**
        if (!isAdded() || getContext() == null) {
            Log.w(TAG, "fetchFavoriteProductIdsAndUpdateAdapter: Fragment not valid (not added or null context). Updating adapter without favorites.");
            updateAdapterData(newProducts, new ArrayList<>()); // Cập nhật UI với sản phẩm nhưng không có favs
            return;
        }

        DatabaseHandler db = new DatabaseHandler(getContext()); // getContext() nên an toàn hơn ở đây
        UserLoginInfo userLoginInfo = db.getLoginInfoSQLite(); // Đây là nơi NPE xảy ra

        if (userLoginInfo != null && userLoginInfo.getUserId() != 0) {
            Log.d(TAG, "Fetching favorite IDs for UserID: " + userLoginInfo.getUserId());
            Call<ApiResponse<List<Integer>>> favCall = apiService.getFavoriteProductIds(userLoginInfo.getUserId());
            favCall.enqueue(new Callback<ApiResponse<List<Integer>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<Integer>>> call, Response<ApiResponse<List<Integer>>> response) {
                    if (!isAdded() || getContext() == null) { // Kiểm tra lại trong callback lồng nhau
                        Log.w(TAG, "onResponse (fetchFavorites): Fragment not valid. Aborting.");
                        // Có thể vẫn cập nhật sản phẩm nếu newProducts không null
                        updateAdapterData(newProducts, new ArrayList<>());
                        return;
                    }
                    List<Integer> fetchedFavoriteIds = new ArrayList<>();
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        fetchedFavoriteIds.addAll(response.body().getData());
                        Log.i(TAG, "Favorite IDs fetched: " + fetchedFavoriteIds.size());
                    } else {
                        Log.w(TAG, "Failed to fetch favorite IDs. Code: " + response.code() + ", Message: " + response.message());
                    }
                    updateAdapterData(newProducts, fetchedFavoriteIds);
                }

                @Override
                public void onFailure(Call<ApiResponse<List<Integer>>> call, Throwable t) {
                    if (!isAdded() || getContext() == null) {
                        Log.w(TAG, "onFailure (fetchFavorites): Fragment not valid. Aborting.");
                        updateAdapterData(newProducts, new ArrayList<>());
                        return;
                    }
                    Log.e(TAG, "API Failure while fetching favorite IDs: " + t.getMessage());
                    updateAdapterData(newProducts, new ArrayList<>());
                }
            });
        } else {
            Log.d(TAG, "User not logged in or UserID is 0. No favorites to fetch.");
            updateAdapterData(newProducts, new ArrayList<>());
        }
    }

    private void updateAdapterData(List<ProductResponse> newProductsToShow, List<Integer> currentFavoriteIds) {
        if (binding == null || adapter == null || !isAdded()) {
            Log.w(TAG, "updateAdapterData: Binding, Adapter is null or Fragment not added. Cannot update UI.");
            return;
        }

        productList.clear();
        if (newProductsToShow != null) {
            productList.addAll(newProductsToShow);
        }

        favoriteProductIds.clear();
        if (currentFavoriteIds != null) {
            favoriteProductIds.addAll(currentFavoriteIds);
            // Update adapter's favorites
            adapter.updateFavorites(favoriteProductIds);
        }
        
        adapter.notifyDataSetChanged();
        Log.d(TAG, "Adapter data updated. Product count: " + productList.size() + ", Favorite count: " + favoriteProductIds.size());

        if (productList.isEmpty()) {
            binding.rvProductCategory.setVisibility(View.GONE);
            Log.d(TAG, "No products to display. tvNoProducts is visible.");
        } else {
            binding.rvProductCategory.setVisibility(View.VISIBLE);
            Log.d(TAG, "Products available. rvProductCategory is visible.");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        Log.d(TAG, "onDestroyView - Binding set to null.");
    }
}
