package com.example.marketplacesecondhand.fragment.productDetail;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.API.DatabaseHandler;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.service.RetrofitClient;
import com.example.marketplacesecondhand.databinding.BottomSheetAddToCartBinding;
import com.example.marketplacesecondhand.dto.request.CartRequest;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.models.UserLoginInfo;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottomSheetAddToCartFragment extends BottomSheetDialogFragment {
    private BottomSheetAddToCartBinding binding;
    // Các biến lưu trữ thông tin sản phẩm và số lượng
    private int quantity = 1;           // Số lượng mặc định
    private int unitPrice = 0;          // Đơn giá, sẽ được cập nhật từ arguments
    private int maxQuantity = 1;        // Số lượng tối đa trong kho, sẽ được cập nhật
    private String productName = "";    // Tên sản phẩm
    private String productImageUrl = "";// URL hình ảnh sản phẩm
    private int productId = -1;         // ID sản phẩm

    private String currentPrice = "";
    private String originalPrice = "";

    // Keys cho Bundle arguments
    private static final String ARG_PRODUCT_ID = "product_id";
    private static final String ARG_PRODUCT_NAME = "product_name";
    private static final String ARG_UNIT_PRICE = "unit_price";
    private static final String ARG_MAX_QUANTITY = "max_quantity";
    private static final String ARG_IMAGE_URL = "image_url";
//    private static final String ARG_PRODUCT_ID = "product_id";
//    private static final String ARG_PRODUCT_NAME = "product_name";
    private static final String TAG = "BottomSheetBuyNow";

    public static BottomSheetAddToCartFragment newInstance(int productId, String productName, int unitPrice, int maxQuantity, String imageUrl) {
        BottomSheetAddToCartFragment fragment = new BottomSheetAddToCartFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PRODUCT_ID, productId);
        args.putString(ARG_PRODUCT_NAME, productName);
        args.putInt(ARG_UNIT_PRICE, unitPrice);
        args.putInt(ARG_MAX_QUANTITY, maxQuantity);
        args.putString(ARG_IMAGE_URL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Lấy dữ liệu sản phẩm từ arguments
        if (getArguments() != null) {
            productId = getArguments().getInt(ARG_PRODUCT_ID, -1);
            productName = getArguments().getString(ARG_PRODUCT_NAME, "Sản phẩm");
            unitPrice = getArguments().getInt(ARG_UNIT_PRICE, 0);
            maxQuantity = getArguments().getInt(ARG_MAX_QUANTITY, 1);
            productImageUrl = getArguments().getString(ARG_IMAGE_URL, "");

            // Đảm bảo maxQuantity hợp lệ
            if (maxQuantity <= 0 && unitPrice > 0) {
                maxQuantity = 1;
                Log.w(TAG, "maxQuantity không hợp lệ (" + getArguments().getInt(ARG_MAX_QUANTITY) + "), đặt lại thành 1 cho sản phẩm: " + productName);
            }
            else if (maxQuantity <= 0) {
                maxQuantity = 0; // Nếu không có hàng
            }
            Log.d(TAG, "onCreate: ProductID=" + productId + ", Name=" + productName + ", Price=" + unitPrice + ", MaxQty=" + maxQuantity + ", Image=" + productImageUrl);
        } else {
            Log.e(TAG, "onCreate: Arguments is null. BottomSheet có thể không hiển thị đúng thông tin.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetAddToCartBinding.inflate(inflater, container, false);

        // Hiển thị thông tin sản phẩm lên UI
        binding.tvProductNameBottomSheet.setText(productName);
        if (getContext() != null && productImageUrl != null && !productImageUrl.isEmpty()) {
            Glide.with(requireContext())
                    .load(productImageUrl)
                    .placeholder(R.drawable.img)
                    .error(R.drawable.img)
                    .into(binding.ivProductImageBottomSheet);
        }
        else {
            binding.ivProductImageBottomSheet.setImageResource(R.drawable.img);
        }

        // Nếu không có hàng, vô hiệu hóa các nút và hiển thị thông báo
        if (maxQuantity <= 0) {
            quantity = 0;
            Toast.makeText(getContext(), "Sản phẩm hiện đã hết hàng.", Toast.LENGTH_LONG).show();
        }
        else {
            quantity = 1;
        }

        updateUI();
        setupButtonClickListeners();

        return binding.getRoot();
    }

    private void setupButtonClickListeners() {
        binding.buttonIncrease.setOnClickListener(v -> {
            if (maxQuantity <= 0) {
                if(getContext()!=null) Toast.makeText(getContext(), "Sản phẩm đã hết hàng.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (quantity < maxQuantity) {
                quantity++;
                updateUI();
            } else {
                if(getContext()!=null) {
                    Toast.makeText(getContext(), "Bạn đã chọn số lượng tối đa.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.buttonDecrease.setOnClickListener(v -> {
            if (maxQuantity <= 0) {
                if(getContext()!=null) Toast.makeText(getContext(), "Sản phẩm đã hết hàng.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (quantity > 1) {
                quantity--;
                updateUI();
            }
        });

        binding.btnApply.setOnClickListener(v -> {
            if (quantity <= 0) {
                if(getContext()!=null) {
                    Toast.makeText(getContext(), "Vui lòng chọn số lượng hợp lệ.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            if (productId == -1) {
                if(getContext()!=null) {
                    Toast.makeText(getContext(), "Lỗi: Không xác định được sản phẩm.", Toast.LENGTH_SHORT).show();
                }
                return;
            }


            DatabaseHandler db = new DatabaseHandler(requireContext());
            UserLoginInfo userLoginInfo = db.getLoginInfoSQLite();

            if (userLoginInfo == null || userLoginInfo.getUserId() == 0) {
                Toast.makeText(requireContext(), "Bạn cần đăng nhập để thêm sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                return; // Không thực hiện gọi API nữa
            }

            APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);

            CartRequest request = new CartRequest(userLoginInfo.getUserId(), productId, quantity);

            Call<ApiResponse<Void>> call = apiService.addToCart(request);
            call.enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(requireContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        dismiss();
                    } else {
                        Toast.makeText(requireContext(), "Có lỗi xảy ra, thử lại sau!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                    Toast.makeText(requireContext(), "Không thể kết nối server!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Quan trọng: giải phóng binding
    }

    private void updateUI() {
        if (binding == null) return; // Đảm bảo binding không null

        binding.textViewQuantity.setText(String.valueOf(quantity));
        int total = quantity * unitPrice;
        binding.txtTotalPrice.setText(formatCurrency(total) + " VND"); // Hiển thị tổng tiền

        if (maxQuantity <= 0) { // Hết hàng
            binding.btnApply.setEnabled(false);
            binding.buttonIncrease.setEnabled(false);
            binding.buttonDecrease.setEnabled(false);
            binding.textViewQuantity.setText("0");
        }
        else {
            binding.btnApply.setEnabled(quantity > 0);
            binding.buttonIncrease.setEnabled(quantity < maxQuantity);
            binding.buttonDecrease.setEnabled(quantity > 1);
        }
    }

    private String formatCurrency(int number) {
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(number);
    }
}
