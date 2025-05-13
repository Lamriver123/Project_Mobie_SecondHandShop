package com.example.marketplacesecondhand.fragment.payment;

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
import com.example.marketplacesecondhand.RetrofitClient;
import com.example.marketplacesecondhand.PaymentActivity;
import com.example.marketplacesecondhand.CheckoutSuccessActivity;
import com.example.marketplacesecondhand.databinding.FragmentFooterPaymentBinding;
import com.example.marketplacesecondhand.dto.request.CartRequest;
import com.example.marketplacesecondhand.dto.request.OrderDetailRequest;
import com.example.marketplacesecondhand.dto.request.OrderRequest;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.DeliveryAddressResponse;
import com.example.marketplacesecondhand.models.CartProduct;
import com.example.marketplacesecondhand.models.CartShop;
import com.example.marketplacesecondhand.models.UserLoginInfo;
import com.example.marketplacesecondhand.viewModel.LocationViewModel;
import com.example.marketplacesecondhand.viewModel.PaymentViewModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FooterPaymentFragment extends Fragment {
    private static final String TAG = "FooterPaymentFragment";
    private FragmentFooterPaymentBinding binding;
    private PaymentViewModel paymentViewModel;
    private LocationViewModel locationViewModel;
    private APIService apiService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        paymentViewModel = new ViewModelProvider(requireActivity()).get(PaymentViewModel.class);
        locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFooterPaymentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObservers();
        setupOrderButton();
    }

    private void setupObservers() {
        paymentViewModel.getOrderSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                String source = paymentViewModel.getPaymentSource().getValue();
                if (PaymentActivity.SOURCE_CART.equals(source)) {
                    removeOrderedItemsFromCart();
                } else {
                    navigateToSuccessScreen();
                }
            }
        });

        paymentViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        paymentViewModel.getTotalAmount().observe(getViewLifecycleOwner(), totalAmount -> {
            if (totalAmount != null) {
                binding.textViewTotalPrice.setText("Tổng: " + totalAmount);
            }
        });
    }

    private void removeOrderedItemsFromCart() {
        List<CartShop> selectedShops = paymentViewModel.getCartShopsToCheckout().getValue();
        if (selectedShops == null || selectedShops.isEmpty()) {
            navigateToSuccessScreen();
            return;
        }

        DatabaseHandler dbHandler = new DatabaseHandler(getContext());
        UserLoginInfo userInfo = dbHandler.getLoginInfoSQLite();
        AtomicInteger removedCount = new AtomicInteger(0);
        AtomicInteger totalItems = new AtomicInteger(0);

        // Đếm tổng số sản phẩm cần xóa
        for (CartShop shop : selectedShops) {
            totalItems.addAndGet(shop.getProducts().size());
        }

        for (CartShop shop : selectedShops) {
            for (CartProduct product : shop.getProducts()) {
                CartRequest cartRequest = new CartRequest(
                    userInfo.getUserId(),
                    product.getProductResponse().getProductId(),
                    product.getQuantityCart()
                );

                apiService.deleteCart(cartRequest).enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                        int currentCount = removedCount.incrementAndGet();
                        if (currentCount == totalItems.get()) {
                            navigateToSuccessScreen();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                        Log.e(TAG, "Error removing item from cart", t);
                        int currentCount = removedCount.incrementAndGet();
                        if (currentCount == totalItems.get()) {
                            navigateToSuccessScreen();
                        }
                    }
                });
            }
        }
    }

    private void navigateToSuccessScreen() {
        Intent intent = new Intent(requireContext(), CheckoutSuccessActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void setupOrderButton() {
        binding.buttonCheckout.setOnClickListener(v -> {
            String paymentMethod = paymentViewModel.getSelectedPaymentMethod().getValue();
            if (paymentMethod == null || paymentMethod.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
                return;
            }

            DeliveryAddressResponse address = locationViewModel.getSelectedAddress().getValue();
            if (address == null) {
                Toast.makeText(requireContext(), "Vui lòng chọn địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
                return;
            }

            List<CartShop> selectedShops = paymentViewModel.getCartShopsToCheckout().getValue();
            if (selectedShops == null || selectedShops.isEmpty()) {
                Toast.makeText(requireContext(), "Không có sản phẩm nào được chọn", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseHandler dbHandler = new DatabaseHandler(getContext());
            UserLoginInfo userInfo = dbHandler.getLoginInfoSQLite();

            List<OrderDetailRequest> orderDetails;
            OrderRequest orderRequest;
            for (CartShop shop : selectedShops) {
                orderDetails = new ArrayList<>();
                for (CartProduct product : shop.getProducts()) {
                    orderDetails.add(new OrderDetailRequest(
                            product.getProductResponse().getProductId(),
                            product.getQuantityCart(),
                            product.getProductResponse().getCurrentPrice()
                    ));
                }

                orderRequest = new OrderRequest(
                        userInfo.getUserId(),
                        address.getAddressName(),
                        paymentMethod,
                        orderDetails
                );
                paymentViewModel.createOrder(orderRequest);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
