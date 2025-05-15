package com.example.marketplacesecondhand.fragment.productDetail;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.marketplacesecondhand.activity.PaymentActivity;
import com.example.marketplacesecondhand.R; // Đảm bảo import R đúng
import com.example.marketplacesecondhand.databinding.BottomSheetBuyNowBinding;
import com.example.marketplacesecondhand.dto.response.ProductResponse;
import com.example.marketplacesecondhand.models.CartProduct;
import com.example.marketplacesecondhand.models.CartShop;
import com.example.marketplacesecondhand.models.User;
import com.example.marketplacesecondhand.viewModel.PaymentViewModel;
import com.example.marketplacesecondhand.viewModel.ProductDetailViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BottomSheetBuyNowFragment extends BottomSheetDialogFragment {

    private BottomSheetBuyNowBinding binding;
    private ProductDetailViewModel productDetailViewModel;
    private ProductResponse productResponse;
    private PaymentViewModel paymentViewModel;
    private int quantity = 1;

    public static BottomSheetBuyNowFragment newInstance() {
        return new BottomSheetBuyNowFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            productDetailViewModel = new ViewModelProvider(requireActivity()).get(ProductDetailViewModel.class);
            paymentViewModel = new ViewModelProvider(requireActivity()).get(PaymentViewModel.class);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetBuyNowBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productDetailViewModel.getProductDetail().observe(getViewLifecycleOwner(), product -> {
            if (product != null && binding != null) {
                productResponse = product;

                // Hiển thị thông tin sản phẩm lên UI
                binding.tvProductNameBottomSheet.setText(productResponse.getProductName());
                if (getContext() != null && productResponse.getCurrentImages() != null && !productResponse.getCurrentImages().isEmpty()) {
                    Glide.with(requireContext())
                            .load(productResponse.getCurrentImages().get(0))
                            .placeholder(R.drawable.img)
                            .error(R.drawable.img)
                            .into(binding.ivProductImageBottomSheet);
                }
                else {
                    binding.ivProductImageBottomSheet.setImageResource(R.drawable.img);
                }

                // Nếu không có hàng, vô hiệu hóa các nút và hiển thị thông báo
                if (productResponse.getQuantity() <= 0) {
                    quantity = 0;
                    Toast.makeText(getContext(), "Sản phẩm hiện đã hết hàng.", Toast.LENGTH_LONG).show();
                }
                else {
                    quantity = 1;
                }

                updateUI();
                setupButtonClickListeners();
            }
        });


    }
    private void setupButtonClickListeners() {
        binding.buttonIncrease.setOnClickListener(v -> {
            if (productResponse.getQuantity() <= 0) {
                if(getContext()!=null) Toast.makeText(getContext(), "Sản phẩm đã hết hàng.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (quantity < productResponse.getQuantity()) {
                quantity++;
                updateUI();
            } else {
                if(getContext()!=null) {
                    Toast.makeText(getContext(), "Bạn đã chọn số lượng tối đa.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.buttonDecrease.setOnClickListener(v -> {
            if (productResponse.getQuantity() <= 0) {
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
            if (productResponse.getProductId() == -1) {
                if(getContext()!=null) {
                    Toast.makeText(getContext(), "Lỗi: Không xác định được sản phẩm.", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            List<CartShop> selectedShopsToCheckout = new ArrayList<>();
            productDetailViewModel.getProductDetail().observe(getViewLifecycleOwner(), product -> {
                if (product != null && binding != null) {
                    CartProduct productCart = new CartProduct(product, quantity, true);
                    List<CartProduct> products = new ArrayList<>();
                    products.add(productCart);
                    
                    // Tạo user với đầy đủ thông tin
                    User user = new User();
                    user.setUsername(ProductDetailFragment.SHOP_NAME);
                    user.setId(product.getOwnerId()); // Set shop ID từ product
                    
                    CartShop cartShop = new CartShop(user, true, products);
                    selectedShopsToCheckout.add(cartShop);
                    
                    // Set cart shops và chuyển sang màn hình thanh toán
                    paymentViewModel.setCartShopsToCheckout(selectedShopsToCheckout);

                    Intent intent = new Intent(requireContext(), PaymentActivity.class);
                    intent.putExtra("SELECTED_CART_SHOPS", (Serializable) selectedShopsToCheckout);
                    startActivity(intent);
                    dismiss();
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
        int total = quantity * Integer.parseInt(productResponse.getCurrentPrice());
        binding.txtTotalPrice.setText(formatCurrency(total) + " VND"); // Hiển thị tổng tiền

        if (productResponse.getQuantity() <= 0) { // Hết hàng
            binding.btnApply.setEnabled(false);
            binding.buttonIncrease.setEnabled(false);
            binding.buttonDecrease.setEnabled(false);
            binding.textViewQuantity.setText("0");
        }
        else {
            binding.btnApply.setEnabled(quantity > 0);
            binding.buttonIncrease.setEnabled(quantity < productResponse.getQuantity());
            binding.buttonDecrease.setEnabled(quantity > 1);
        }
    }

    private String formatCurrency(int number) {
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(number);
    }
}
