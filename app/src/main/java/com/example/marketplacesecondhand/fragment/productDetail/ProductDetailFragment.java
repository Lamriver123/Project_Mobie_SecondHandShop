package com.example.marketplacesecondhand.fragment.productDetail;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.RetrofitClient;
import com.example.marketplacesecondhand.adapter.ImageSliderAdapter;
import com.example.marketplacesecondhand.databinding.FragmentProductDetailBinding;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.ProductResponse;
import com.example.marketplacesecondhand.dto.response.UserResponse;
import com.example.marketplacesecondhand.fragment.SimilarProductsFragment;
import com.example.marketplacesecondhand.viewModel.ProductDetailViewModel;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailFragment extends Fragment {
    private static final String TAG = "ProductDetailFragment";
    private static final String ARG_PRODUCT_ID = "product_id";
    private APIService apiService;
    private FragmentProductDetailBinding binding;
    private ProductDetailViewModel productDetailViewModel;
    private int currentProductId = -1;

    public static ProductDetailFragment newInstance(int productId) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PRODUCT_ID, productId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        productDetailViewModel = new ViewModelProvider(requireActivity()).get(ProductDetailViewModel.class);

        if (getArguments() != null) {
            currentProductId = getArguments().getInt(ARG_PRODUCT_ID, -1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProductDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiService = RetrofitClient.getRetrofit().create(APIService.class);

        setupObservers();

        if (currentProductId != -1) {
            fetchProductDetail(currentProductId);
        }
        else {
            String errorMessage = "Lỗi: ID sản phẩm không hợp lệ.";
            Log.e(TAG, errorMessage);
            productDetailViewModel.setErrorMessage(errorMessage);
        }
    }

    private void setupObservers() {
        productDetailViewModel.getProductDetail().observe(getViewLifecycleOwner(), product -> {
            if (product != null && binding != null) {
                updateUIWithProduct(product);
                fetchShopInfo(product.getOwnerId());
            }
        });

        productDetailViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && binding != null) {
                binding.progressBarProductDetail.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                binding.contentScrollView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
                binding.textErrorMessage.setVisibility(View.GONE);
            }
        });

        productDetailViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (binding == null) return;
            if (errorMessage != null && !errorMessage.isEmpty()) {
                binding.textErrorMessage.setText(errorMessage);
                binding.textErrorMessage.setVisibility(View.VISIBLE);
                binding.contentScrollView.setVisibility(View.GONE);
                if (getContext() != null) {
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            } else {
                binding.textErrorMessage.setVisibility(View.GONE);
            }
        });
    }


    private void fetchProductDetail(int productId) {
        productDetailViewModel.setLoading(true);
        productDetailViewModel.setErrorMessage(null);

        apiService.getProductDetail(productId).enqueue(new Callback<ApiResponse<ProductResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<ProductResponse>> call, @NonNull Response<ApiResponse<ProductResponse>> response) {
                if (!isAdded()) return;
                productDetailViewModel.setLoading(false);

                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    ProductResponse product = response.body().getData();
                    productDetailViewModel.setProductDetail(product);
                } else {
                    String errorMsg = "Không thể tải chi tiết sản phẩm (Code: " + response.code() + ")";
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            ApiResponse<?> apiError = new Gson().fromJson(errorJson, ApiResponse.class);
                            if (apiError != null && apiError.getMessage() != null) {
                                errorMsg = apiError.getMessage();
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi parse error body: " + e.getMessage());
                    }
                    productDetailViewModel.setErrorMessage(errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<ProductResponse>> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                productDetailViewModel.setLoading(false);
                Log.e(TAG, "Lỗi kết nối API: " + t.getMessage(), t);
                productDetailViewModel.setErrorMessage("Lỗi kết nối. Vui lòng kiểm tra mạng.");
            }
        });
    }

    private void updateUIWithProduct(ProductResponse product) {
        Log.d(TAG, "Đang cập nhật UI với sản phẩm: " + product.getProductName());

        binding.textProductName.setText(product.getProductName());

        if (product.getCurrentPrice() != null && !product.getCurrentPrice().isEmpty()) {
            try {
                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                String cleanPrice = product.getCurrentPrice().replaceAll("[^\\d]", "");
                if (!cleanPrice.isEmpty()) {
                    Number priceNum = currencyFormat.parse(cleanPrice);
                    binding.textProductPrice.setText(currencyFormat.format(priceNum.doubleValue()));
                } else {
                    binding.textProductPrice.setText("N/A");
                }
            } catch (ParseException | NumberFormatException e) {
                Log.e(TAG, "Lỗi parse giá tiền: " + product.getCurrentPrice(), e);
                binding.textProductPrice.setText(product.getCurrentPrice());
            }
        } else {
            binding.textProductPrice.setText("Liên hệ");
        }

        showProductDetailsTable(product);

        if (product.getCurrentImages() != null && !product.getCurrentImages().isEmpty()) {
            ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(requireContext(), product.getCurrentImages());
            binding.viewPagerImages.setAdapter(sliderAdapter);
            binding.dotsIndicator.attachTo(binding.viewPagerImages);
            binding.radioCurrent.setChecked(true);
            binding.viewPagerImages.setVisibility(View.VISIBLE);
            binding.dotsIndicator.setVisibility(View.VISIBLE);
        } else {
            binding.viewPagerImages.setVisibility(View.GONE);
            binding.dotsIndicator.setVisibility(View.GONE);
        }

        binding.radioCurrent.setOnClickListener(v -> {
            if (product.getCurrentImages() != null && !product.getCurrentImages().isEmpty()) {
                binding.viewPagerImages.setVisibility(View.VISIBLE);
                binding.dotsIndicator.setVisibility(View.VISIBLE);
                binding.viewPagerImages.setAdapter(new ImageSliderAdapter(requireContext(), product.getCurrentImages()));
            } else {
                if(getContext()!=null) Toast.makeText(getContext(), "Không có ảnh hiện tại.", Toast.LENGTH_SHORT).show();
                binding.viewPagerImages.setVisibility(View.GONE);
                binding.dotsIndicator.setVisibility(View.GONE);
            }
        });

        binding.radioOriginal.setOnClickListener(v -> {
            if (product.getInitialImages() != null && !product.getInitialImages().isEmpty()) {
                binding.viewPagerImages.setVisibility(View.VISIBLE);
                binding.dotsIndicator.setVisibility(View.VISIBLE);
                binding.viewPagerImages.setAdapter(new ImageSliderAdapter(requireContext(), product.getInitialImages()));
            } else {
                if(getContext()!=null) Toast.makeText(getContext(), "Không có ảnh gốc.", Toast.LENGTH_SHORT).show();
                binding.viewPagerImages.setVisibility(View.GONE);
                binding.dotsIndicator.setVisibility(View.GONE);
            }
        });

        if (product.getCategoryName() != null && !product.getCategoryName().isEmpty()) {
            binding.similarProducts.setVisibility(View.VISIBLE);
            SimilarProductsFragment fragment = SimilarProductsFragment.newInstance(product.getCategoryName());
            // Sử dụng getChildFragmentManager vì similar_products là con của ProductDetailFragment
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.similar_products, fragment)
                    .commitAllowingStateLoss();
        } else {
            Log.w(TAG, "Tên danh mục rỗng, không tải sản phẩm tương tự.");
            binding.similarProducts.setVisibility(View.GONE);
        }
    }

    private void showProductDetailsTable(ProductResponse product) {
        if (binding == null || getContext() == null) return;
        binding.tableProductDetails.removeAllViews();
        NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

        addTableRow("Danh mục", product.getCategoryName());

        try {
            if (product.getOriginalPrice() != null && !product.getOriginalPrice().isEmpty()) {
                String cleanPrice = product.getOriginalPrice().replaceAll("[^\\d]", "");
                if (!cleanPrice.isEmpty()) {
                    Number priceNum = currencyFormat.parse(cleanPrice);
                    addTableRow("Giá gốc", currencyFormat.format(priceNum.doubleValue()));
                } else { addTableRow("Giá gốc", "-"); }
            } else { addTableRow("Giá gốc", "-");}

            if (product.getCurrentPrice() != null && !product.getCurrentPrice().isEmpty()) {
                String cleanPrice = product.getCurrentPrice().replaceAll("[^\\d]", "");
                if (!cleanPrice.isEmpty()) {
                    Number priceNum = currencyFormat.parse(cleanPrice);
                    addTableRow("Giá hiện tại", currencyFormat.format(priceNum.doubleValue()));
                } else { addTableRow("Giá hiện tại", "-");}
            } else { addTableRow("Giá hiện tại", "-");}
        } catch (ParseException | NumberFormatException e) {
            Log.e(TAG, "Lỗi parse giá trong bảng: " + e.getMessage());
            addTableRow("Giá gốc", product.getOriginalPrice() != null ? product.getOriginalPrice() : "-");
            addTableRow("Giá hiện tại", product.getCurrentPrice() != null ? product.getCurrentPrice() : "-");
        }

        addTableRow("Số lượng", (product.getQuantity() > 0) ? String.valueOf(product.getQuantity()) : "-");
        addTableRow("Xuất xứ", product.getOrigin());
        addTableRow("Bảo hành", product.getWarranty());
        addTableRow("Tình trạng", product.getProductCondition());
        addTableRow("Mô tả tình trạng", product.getConditionDescription());
        addTableRow("Đã bán", (product.getSold() > 0) ? String.valueOf(product.getSold()) : "0");

        if (product.getCreatedAt() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            addTableRow("Ngày đăng", sdf.format(product.getCreatedAt()));
        } else {
            addTableRow("Ngày đăng", "-");
        }
    }

    private void addTableRow(String title, String value) {
        if (getContext() == null || binding == null) return;

        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rowParams.topMargin = getResources().getDimensionPixelSize(R.dimen.table_row_vertical_margin);
        rowParams.bottomMargin = getResources().getDimensionPixelSize(R.dimen.table_row_vertical_margin);
        row.setLayoutParams(rowParams);
        row.setPadding(getResources().getDimensionPixelSize(R.dimen.table_row_horizontal_padding), 0,
                getResources().getDimensionPixelSize(R.dimen.table_row_horizontal_padding), 0);

        TextView titleView = new TextView(requireContext());
        titleView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        titleView.setText(title);
        titleView.setTextSize(15);
        titleView.setTextColor(Color.parseColor("#555555"));

        TextView valueView = new TextView(requireContext());
        LinearLayout.LayoutParams valueParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.5f);
        valueParams.setMarginStart(getResources().getDimensionPixelSize(R.dimen.table_value_margin_start));
        valueView.setLayoutParams(valueParams);
        valueView.setText(value != null && !value.trim().isEmpty() ? value.trim() : "-");
        valueView.setTextSize(15);
        valueView.setTextColor(Color.parseColor("#212121"));
        valueView.setTypeface(null, Typeface.BOLD);

        row.addView(titleView);
        row.addView(valueView);
        binding.tableProductDetails.addView(row);

        View divider = new View(requireContext());
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.divider_height));
        dividerParams.setMargins(getResources().getDimensionPixelSize(R.dimen.table_row_horizontal_padding), 0,
                getResources().getDimensionPixelSize(R.dimen.table_row_horizontal_padding), 0);
        divider.setLayoutParams(dividerParams);
        divider.setBackgroundColor(Color.parseColor("#E0E0E0"));
        binding.tableProductDetails.addView(divider);
    }

    private void fetchShopInfo(Integer shopId) {
        if (shopId == null || shopId <= 0) {
            Log.e(TAG, "shopId không hợp lệ: " + shopId);
            if (binding != null) {
                binding.tvSellerName.setText("Không có thông tin người bán");
                binding.tvSellerLastActive.setVisibility(View.VISIBLE);
                binding.ivSellerAvatar.setImageResource(R.drawable.icon_user);
            }
            return;
        }

        apiService.getShopInfo(shopId).enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<UserResponse>> call, @NonNull Response<ApiResponse<UserResponse>> response) {
                if (!isAdded() || getContext() == null || binding == null) return;

                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    UserResponse userResponse = response.body().getData();
                    binding.tvSellerName.setText(userResponse.getUsername());

                    Glide.with(requireContext())
                            .load(userResponse.getAvt())
                            .circleCrop()
                            .placeholder(R.drawable.icon_user)
                            .error(R.drawable.icon_user)
                            .into(binding.ivSellerAvatar);
                } else {
                    Log.e(TAG, "Lỗi khi tải thông tin shop: Code " + response.code());
                    binding.tvSellerName.setText("Không tải được thông tin shop");
                    binding.ivSellerAvatar.setImageResource(R.drawable.icon_user);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<UserResponse>> call, @NonNull Throwable t) {
                if (!isAdded() || getContext() == null || binding == null) return;
                Log.e(TAG, "Lỗi kết nối API khi lấy thông tin shop: " + t.getMessage(), t);
                binding.tvSellerName.setText("Lỗi kết nối");
                binding.ivSellerAvatar.setImageResource(R.drawable.icon_user);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
