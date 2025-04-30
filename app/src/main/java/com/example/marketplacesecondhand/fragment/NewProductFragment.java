package com.example.marketplacesecondhand.fragment;

import android.content.Intent;
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

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.API.DatabaseHandler;
import com.example.marketplacesecondhand.ProductDetailActivity;
import com.example.marketplacesecondhand.RetrofitClient;
import com.example.marketplacesecondhand.adapter.CategoryAdapter;
import com.example.marketplacesecondhand.adapter.ProductAdapter;
import com.example.marketplacesecondhand.databinding.FragmentCategoryBinding;
import com.example.marketplacesecondhand.databinding.FragmentNewProductBinding;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.ProductResponse;
import com.example.marketplacesecondhand.models.Category;
import com.example.marketplacesecondhand.models.Product;
import com.example.marketplacesecondhand.models.UserLoginInfo;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewProductFragment extends Fragment {
    private FragmentNewProductBinding binding;
    private APIService apiService;

    public NewProductFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNewProductBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchNewProduct();
    }

    private void fetchNewProduct() {
        if (binding.newProductRecycler == null) {
            Log.e("BINDING", "categoryRecycler is null - check fragment_category.xml");
        }
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<ApiResponse<List<ProductResponse>>> call = apiService.getNewestProducts();

        call.enqueue(new Callback<ApiResponse<List<ProductResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ProductResponse>>> call, Response<ApiResponse<List<ProductResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductResponse> products = response.body().getData();

                    fetchFavoriteProductIds(products);

//                    ProductAdapter adapter = new ProductAdapter(getContext(), products, favoriteProductIds);
//
//                    binding.newProductRecycler.setAdapter(adapter);
//                    binding.newProductRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
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


    private void fetchFavoriteProductIds(List<ProductResponse> products) {
        if (binding.newProductRecycler == null) {
            Log.e("BINDING", "categoryRecycler is null - check fragment_category.xml");
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
                        binding.newProductRecycler.setAdapter(adapter);
                        binding.newProductRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
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
            binding.newProductRecycler.setAdapter(adapter);
            binding.newProductRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        }
    }
    @Override
    public void onResume() {
        super.onResume();

        fetchNewProduct();
    }
}
