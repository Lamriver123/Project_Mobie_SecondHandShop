package com.example.marketplacesecondhand.fragment.productDetail;

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
import androidx.lifecycle.ViewModelProvider;

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.API.DatabaseHandler;
import com.example.marketplacesecondhand.activity.LoginActivity;
import com.example.marketplacesecondhand.databinding.FragmentFooterProductDetailBinding;
import com.example.marketplacesecondhand.dto.request.FavoriteRequest;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.ProductResponse;
import com.example.marketplacesecondhand.models.UserLoginInfo;
import com.example.marketplacesecondhand.service.RetrofitClient;
import com.example.marketplacesecondhand.viewModel.ProductDetailViewModel;
import com.example.marketplacesecondhand.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FooterProductDetailFragment extends Fragment {
    private static final String TAG = "FooterProductDetail";
    private FragmentFooterProductDetailBinding binding;
    private ProductDetailViewModel productDetailViewModel;
    private Set<Integer> favoriteProductIds = new HashSet<>();
    private APIService apiService;

    public FooterProductDetailFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = RetrofitClient.getRetrofit().create(APIService.class);

        if (getActivity() != null) {
            productDetailViewModel = new ViewModelProvider(requireActivity()).get(ProductDetailViewModel.class);
        } else {
            Log.e(TAG, "Không thể lấy ProductDetailViewModel vì getActivity() là null.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFooterProductDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (productDetailViewModel == null) {
            binding.buttonBuyNow.setEnabled(false);
            binding.buttonAddToCart.setEnabled(false);
            Log.e(TAG, "ProductDetailViewModel is null in onViewCreated. Buttons disabled.");
            return;
        }

        // Load favorite status when product is available
        productDetailViewModel.getProductDetail().observe(getViewLifecycleOwner(), product -> {
            if (binding == null) return;

            if (product != null && product.getQuantity() > 0) {
                binding.buttonBuyNow.setEnabled(true);
                binding.buttonAddToCart.setEnabled(true);
            } else {
                binding.buttonBuyNow.setEnabled(false);
                binding.buttonAddToCart.setEnabled(false);
                if (product != null && product.getQuantity() <= 0) {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Sản phẩm đã hết hàng", Toast.LENGTH_SHORT).show();
                    }
                } else if (product != null) {
                    if (getContext() != null) Toast.makeText(getContext(), "Thông tin số lượng sản phẩm không có sẵn", Toast.LENGTH_SHORT).show();
                }
            }

            // Check favorite status when product is loaded
            if (product != null) {
                checkFavoriteStatus(product.getProductId());
            }
        });

        // Setup favorite button click listener
        binding.ivFavorite.setOnClickListener(v -> {
            ProductResponse product = productDetailViewModel.getProductDetail().getValue();
            if (product != null) {
                toggleFavorite(product.getProductId());
            }
        });

        binding.buttonBuyNow.setOnClickListener(v -> {
            DatabaseHandler db = new DatabaseHandler(getContext());
            UserLoginInfo userLoginInfo = db.getLoginInfoSQLite();
            if (userLoginInfo != null) {
                ProductResponse product = productDetailViewModel.getProductDetail().getValue();
                if (product != null) {
                    if (product.getQuantity() <= 0) {
                        if(getContext() != null) Toast.makeText(getContext(), "Sản phẩm đã hết hàng.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (product.getCurrentPrice() == null) {
                        if (getContext()!=null) Toast.makeText(getContext(), "Thông tin sản phẩm không đầy đủ.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String firstImageUrl = (product.getCurrentImages() != null && !product.getCurrentImages().isEmpty()) ? product.getCurrentImages().get(0) : "";
                    int price = 0;
                    try {
                        String cleanPriceString = product.getCurrentPrice().replaceAll("[^\\d]", "");
                        if (!cleanPriceString.isEmpty()) {
                            price = Integer.parseInt(cleanPriceString);
                        } else {
                            if(getContext()!=null) Toast.makeText(getContext(), "Lỗi định dạng giá sản phẩm.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Lỗi parse giá sản phẩm khi mua ngay: " + product.getCurrentPrice(), e);
                        if(getContext()!=null) Toast.makeText(getContext(), "Lỗi định dạng giá sản phẩm.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    BottomSheetBuyNowFragment bottomSheet = BottomSheetBuyNowFragment.newInstance();
                    bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());

                }
                else {
                    if(getContext() != null){
                        Toast.makeText(getContext(), "Dữ liệu chi tiết sản phẩm chưa sẵn sàng.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        binding.buttonAddToCart.setOnClickListener(v -> {
            DatabaseHandler db = new DatabaseHandler(getContext());
            UserLoginInfo userLoginInfo = db.getLoginInfoSQLite();
            if (userLoginInfo != null) {
                ProductResponse product = productDetailViewModel.getProductDetail().getValue();
                if (product != null) {
                    if (product.getQuantity() <= 0) {
                        if(getContext()!=null) {
                            Toast.makeText(getContext(), "Sản phẩm đã hết hàng.", Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }
                    if (product.getCurrentPrice() == null) {
                        if (getContext()!=null) Toast.makeText(getContext(), "Thông tin sản phẩm không đầy đủ.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String firstImageUrl = (product.getCurrentImages() != null && !product.getCurrentImages().isEmpty()) ? product.getCurrentImages().get(0) : "";
                    int price = 0;
                    try {
                        String cleanPriceString = product.getCurrentPrice().replaceAll("[^\\d]", "");
                        if (!cleanPriceString.isEmpty()) {
                            price = Integer.parseInt(cleanPriceString);
                        } else {
                            if(getContext()!=null) Toast.makeText(getContext(), "Lỗi định dạng giá sản phẩm.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Lỗi parse giá sản phẩm khi mua ngay: " + product.getCurrentPrice(), e);
                        if(getContext()!=null) Toast.makeText(getContext(), "Lỗi định dạng giá sản phẩm.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    BottomSheetAddToCartFragment bottomSheet = BottomSheetAddToCartFragment.newInstance(
                            product.getProductId(),
                            product.getProductName(),
                            price,
                            product.getQuantity(),
                            firstImageUrl
                    );
                    bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());

                }
                else {
                    if(getContext() != null){
                        Toast.makeText(getContext(), "Dữ liệu chi tiết sản phẩm chưa sẵn sàng.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void checkFavoriteStatus(int productId) {
        DatabaseHandler db = new DatabaseHandler(getContext());
        UserLoginInfo userLoginInfo = db.getLoginInfoSQLite();

        if (userLoginInfo != null && userLoginInfo.getUserId() != 0) {
            Call<ApiResponse<List<Integer>>> call = apiService.getFavoriteProductIds(userLoginInfo.getUserId());
            call.enqueue(new Callback<ApiResponse<List<Integer>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<Integer>>> call, Response<ApiResponse<List<Integer>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Integer> favoriteIds = response.body().getData();
                        favoriteProductIds.clear();
                        favoriteProductIds.addAll(favoriteIds);
                        updateFavoriteIcon(productId);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<Integer>>> call, Throwable t) {
                    Log.e(TAG, "Failed to fetch favorite status", t);
                }
            });
        } else {
            // If user is not logged in, show unfilled heart
            binding.ivFavorite.setImageResource(R.drawable.ic_heart_border_red);
        }
    }

    private void updateFavoriteIcon(int productId) {
        if (favoriteProductIds.contains(productId)) {
            binding.ivFavorite.setImageResource(R.drawable.ic_heart_selected);
        } else {
            binding.ivFavorite.setImageResource(R.drawable.ic_heart_border_red);
        }
    }

    private void toggleFavorite(int productId) {
        DatabaseHandler db = new DatabaseHandler(getContext());
        UserLoginInfo userLoginInfo = db.getLoginInfoSQLite();

        if (userLoginInfo == null || userLoginInfo.getUserId() == 0) {
            Toast.makeText(getContext(), "Bạn cần đăng nhập để sử dụng tính năng yêu thích!", Toast.LENGTH_SHORT).show();
            return;
        }

        FavoriteRequest request = new FavoriteRequest(userLoginInfo.getUserId(), productId);
        Call<ApiResponse<String>> call = apiService.toggleFavorite(request);
        call.enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    
                    // Toggle favorite status
                    if (favoriteProductIds.contains(productId)) {
                        favoriteProductIds.remove(productId);
                    } else {
                        favoriteProductIds.add(productId);
                    }
                    updateFavoriteIcon(productId);
                } else {
                    Toast.makeText(getContext(), "Có lỗi xảy ra, thử lại sau!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                Toast.makeText(getContext(), "Không thể kết nối server!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
