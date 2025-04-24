package com.example.marketplacesecondhand.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.RetrofitClient;
import com.example.marketplacesecondhand.adapter.ImageSliderAdapter;
import com.example.marketplacesecondhand.adapter.SliderAdapter;
import com.example.marketplacesecondhand.databinding.FragmentProductDetailBinding;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.ProductResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailFragment extends Fragment {
    private static final String ARG_PRODUCT_ID = "product_id";
    private APIService apiService;
    private FragmentProductDetailBinding binding;

    public static ProductDetailFragment newInstance(int productId) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PRODUCT_ID, productId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProductDetailBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Bundle args = getArguments();
        if (args != null) {
            int productId = args.getInt(ARG_PRODUCT_ID, -1);
            if (productId != -1) {
                fetchProductDetail(productId);
            } else {
                Log.e("ProductDetailFragment", "productId không hợp lệ");
            }
        } else {
            Log.e("ProductDetailFragment", "Không nhận được arguments");
        }

        return view;
    }

    private void fetchProductDetail(int productId) {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getProductDetail(productId).enqueue(new Callback<ApiResponse<ProductResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProductResponse>> call, Response<ApiResponse<ProductResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProductResponse product = response.body().getData();
                    Log.d("ProductDetailFragment", "Tên sản phẩm: " + product.getProductName());

                    // Binding dữ liệu lên UI
                    binding.textProductName.setText(product.getProductName());
                    binding.textProductPrice.setText(product.getCurrentPrice());

                    ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(requireContext(), product.getCurrentImages());
                    binding.viewPagerImages.setAdapter(sliderAdapter);

                    binding.radioCurrent.setOnClickListener(v -> {
                        binding.viewPagerImages.setAdapter(new ImageSliderAdapter(requireContext(), product.getCurrentImages()));
                    });

                    binding.radioOriginal.setOnClickListener(v -> {
                        binding.viewPagerImages.setAdapter(new ImageSliderAdapter(requireContext(), product.getInitialImages()));
                    });




                } else {
                    Log.e("API", "Lỗi khi load chi tiết sản phẩm");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ProductResponse>> call, Throwable t) {
                Log.e("API", "Lỗi kết nối API: " + t.getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
