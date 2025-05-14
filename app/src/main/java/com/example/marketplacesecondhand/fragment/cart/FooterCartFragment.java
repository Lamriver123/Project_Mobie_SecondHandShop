package com.example.marketplacesecondhand.fragment.cart;

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

import com.example.marketplacesecondhand.activity.PaymentActivity;
import com.example.marketplacesecondhand.databinding.FragmentFooterCartBinding;
import com.example.marketplacesecondhand.models.CartProduct;
import com.example.marketplacesecondhand.models.CartShop;
import com.example.marketplacesecondhand.viewModel.PaymentViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FooterCartFragment extends Fragment {
    private static final String TAG = "FooterCartFragment";
    private FragmentFooterCartBinding binding;
    private PaymentViewModel paymentViewModel;

    public FooterCartFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo PaymentViewModel, scope với Activity chứa Fragment này (CartActivity hoặc HomeActivity)
        if (getActivity() != null) {
            paymentViewModel = new ViewModelProvider(requireActivity()).get(PaymentViewModel.class);
        } else {
            Log.e(TAG, "getActivity() is null, không thể khởi tạo PaymentViewModel.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle saveInstanceState) {
        binding = FragmentFooterCartBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonCheckout.setOnClickListener(v -> {
            if (!isAdded() || getContext() == null)
                return;

            List<CartShop> selectedShopsToCheckout = new ArrayList<>();
            int totalSelectedItems = 0;


            for (CartShop originalShop : CartDetailFragment.cartShopList) {
                List<CartProduct> selectedProductsInShop = new ArrayList<>();
                for (CartProduct product : originalShop.getProducts()) {
                    if (product != null && product.isChecked()) {
                        selectedProductsInShop.add(product);
                        totalSelectedItems++;
                    }
                }

                // Chỉ thêm shop vào danh sách thanh toán nếu shop đó có sản phẩm được chọn
                if (!selectedProductsInShop.isEmpty()) {
                    // Tạo một CartShop mới chỉ chứa các sản phẩm đã chọn
                    CartShop shopToCheckout = new CartShop();
                    shopToCheckout.setUser(originalShop.getUser()); // Giữ lại thông tin người bán
                    shopToCheckout.setChecked(originalShop.isChecked()); // Trạng thái chọn của cả shop (nếu có)
                    shopToCheckout.setProducts(selectedProductsInShop);
                    selectedShopsToCheckout.add(shopToCheckout);
                }
            }

            if (selectedShopsToCheckout.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng chọn ít nhất một sản phẩm để thanh toán.", Toast.LENGTH_SHORT).show();
                return;
            }

            paymentViewModel.setCartShopsToCheckout(selectedShopsToCheckout);

            // Chuyển sang PaymentActivity
            Intent intent = new Intent(getActivity(), PaymentActivity.class);
            intent.putExtra("SELECTED_CART_SHOPS", (Serializable) selectedShopsToCheckout);
            intent.putExtra(PaymentActivity.EXTRA_SOURCE, PaymentActivity.SOURCE_CART);
            startActivity(intent);

        });

        // Logic cập nhật tổng tiền (textViewTotalPrice) nên được đặt trong CartDetailFragment
        // hoặc thông qua một ViewModel chung mà cả CartDetailFragment và FooterCartFragment cùng lắng nghe.
        // Nếu textViewTotalPrice nằm trong layout của Activity, CartDetailFragment có thể cập nhật nó trực tiếp.
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
