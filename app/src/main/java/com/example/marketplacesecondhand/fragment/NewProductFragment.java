package com.example.marketplacesecondhand.fragment;

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
import com.example.marketplacesecondhand.RetrofitClient;
import com.example.marketplacesecondhand.adapter.CategoryAdapter;
import com.example.marketplacesecondhand.adapter.ProductAdapter;
import com.example.marketplacesecondhand.databinding.FragmentCategoryBinding;
import com.example.marketplacesecondhand.databinding.FragmentNewProductBinding;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.models.Category;
import com.example.marketplacesecondhand.models.Product;

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
        //    init(view);
        fetchNewProduct();
    }

    private void fetchNewProduct() {
        if (binding.newProductRecycler == null) {
            Log.e("BINDING", "categoryRecycler is null - check fragment_category.xml");
        }
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<ApiResponse<List<Product>>> call = apiService.getProductLast7Days();

        call.enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body().getData();
                    ProductAdapter adapter = new ProductAdapter(getContext(), products, product -> {
                        // Xử lý khi người dùng click vào product
                        Toast.makeText(getContext(),"Sản phẩm được chọn: " + product.getProductName(), Toast.LENGTH_SHORT).show();
                    });

                    binding.newProductRecycler.setAdapter(adapter);
                    binding.newProductRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
                    // TODO: Hiển thị categories lên UI (ví dụ RecyclerView)
                    Log.d("API", "Số lượng category: " + products.size());
                } else {
                    Log.e("API", "Lỗi dữ liệu hoặc response body null");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                Log.e("API", "Lỗi kết nối: " + t.getMessage());
            }
        });


    }
}
