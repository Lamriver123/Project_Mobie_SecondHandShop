package com.example.marketplacesecondhand.fragment.store;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.API.DatabaseHandler;
import com.example.marketplacesecondhand.RetrofitClient;
import com.example.marketplacesecondhand.adapter.ProductAdapter;
import com.example.marketplacesecondhand.databinding.FragmentShopProductBinding;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.ProductResponse;
import com.example.marketplacesecondhand.models.UserLoginInfo;
import com.example.marketplacesecondhand.viewModel.ShopViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductShopFragment extends Fragment {
    private FragmentShopProductBinding binding;
    private ShopViewModel shopViewModel;
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

        // Initialize ViewModel and API Service
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        shopViewModel = new ViewModelProvider(requireActivity(), new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ShopViewModel(apiService);
            }
        }).get(ShopViewModel.class);

        // Set up RecyclerView
        binding.productRecycler.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        // Observe shop data
        shopViewModel.getCurrentShop().observe(getViewLifecycleOwner(), shop -> {
            if (shop != null && shop.getProducts() != null) {
                List<ProductResponse> products = shop.getProducts();
                fetchFavoriteProductIds(products);
                
                // Show/hide empty state
                if (products.isEmpty()) {
                  //  binding.emptyState.setVisibility(View.VISIBLE);
                    binding.productRecycler.setVisibility(View.GONE);
                } else {
                 //   binding.emptyState.setVisibility(View.GONE);
                    binding.productRecycler.setVisibility(View.VISIBLE);
                }
            }
        });

        shopViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchFavoriteProductIds(List<ProductResponse> products) {
        if (binding.productRecycler == null) {
            Log.e("BINDING", "productRecycler is null - check fragment_shop_product.xml");
            return;
        }

        DatabaseHandler db = new DatabaseHandler(requireContext());
        UserLoginInfo userLoginInfo = db.getLoginInfoSQLite();

        if (userLoginInfo != null && userLoginInfo.getUserId() != 0) {
            Call<ApiResponse<List<Integer>>> call = apiService.getFavoriteProductIds(userLoginInfo.getUserId());

            call.enqueue(new Callback<ApiResponse<List<Integer>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<Integer>>> call, Response<ApiResponse<List<Integer>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Integer> favoriteProductIds = response.body().getData();

                        ProductAdapter adapter = new ProductAdapter(requireContext(), products, favoriteProductIds);
                        binding.productRecycler.setAdapter(adapter);
                    } else {
                        Log.e("API", "Lỗi dữ liệu hoặc response body null");
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<Integer>>> call, Throwable t) {
                    Log.e("API", "Lỗi kết nối: " + t.getMessage());
                }
            });
        } else {
            // Nếu user chưa login thì không cần load favorite
            ProductAdapter adapter = new ProductAdapter(requireContext(), products);
            binding.productRecycler.setAdapter(adapter);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
