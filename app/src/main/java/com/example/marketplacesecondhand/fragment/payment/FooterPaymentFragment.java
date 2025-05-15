package com.example.marketplacesecondhand.fragment.payment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.API.DatabaseHandler;
import com.example.marketplacesecondhand.service.RetrofitClient;
import com.example.marketplacesecondhand.activity.PaymentActivity;
import com.example.marketplacesecondhand.activity.CheckoutSuccessActivity;
import com.example.marketplacesecondhand.ZaloPay.Api.CreateOrder;
import com.example.marketplacesecondhand.databinding.FragmentFooterPaymentBinding;
import com.example.marketplacesecondhand.dto.request.CartRequest;
import com.example.marketplacesecondhand.dto.request.OrderDetailRequest;
import com.example.marketplacesecondhand.dto.request.OrderRequest;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.DeliveryAddressResponse;
import com.example.marketplacesecondhand.dto.response.VoucherResponse;
import com.example.marketplacesecondhand.models.CartProduct;
import com.example.marketplacesecondhand.models.CartShop;
import com.example.marketplacesecondhand.models.UserLoginInfo;
import com.example.marketplacesecondhand.viewModel.LocationViewModel;
import com.example.marketplacesecondhand.viewModel.PaymentViewModel;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

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

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ZaloPay SDK Init
        ZaloPaySDK.init(553, Environment.SANDBOX);

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
                binding.textViewTotalPrice.setText("Tổng: " + formatCurrency(totalAmount) + " ₫");
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
        // Extract category IDs from cart items
        List<Integer> categoryIds = new ArrayList<>();
        List<CartShop> selectedShops = paymentViewModel.getCartShopsToCheckout().getValue();
        
        if (selectedShops != null) {
            for (CartShop shop : selectedShops) {
                for (CartProduct product : shop.getProducts()) {
                    int categoryId = product.getProductResponse().getCategoryId();
                    if (!categoryIds.contains(categoryId)) {
                        categoryIds.add(categoryId);
                    }
                }
            }
        }

        Intent intent = new Intent(requireContext(), CheckoutSuccessActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putIntegerArrayListExtra("categoryIds", new ArrayList<>(categoryIds));
        startActivity(intent);
        requireActivity().finish();
    }

    private void setupOrderButton() {
        binding.buttonCheckout.setOnClickListener(v -> {
            String paymentMethod = paymentViewModel.getSelectedPaymentMethod().getValue();
            if (paymentMethod != null && paymentMethod.equals("COD")) {
                processOrder();
            }
            else if (paymentMethod != null && paymentMethod.equals("ZALOPAY")) {
                ZaloPayPaymentProcess();
            }
        });
    }

    private void ZaloPayPaymentProcess() {
        CreateOrder orderApi = new CreateOrder();
        paymentViewModel.getTotalAmount().observe(getViewLifecycleOwner(), totalAmount -> {
            if (totalAmount != null) {
                try {
                    JSONObject data = orderApi.createOrder(totalAmount.toString());
                    String code = data.getString("returncode");

                    if (code.equals("1")) {
                        String token = data.getString("zptranstoken");

                        ZaloPaySDK.getInstance().payOrder(getActivity(), token, "demozpdk://payment", new PayOrderListener() {
                            @Override
                            public void onPaymentSucceeded(String s, String s1, String s2) {
                                // Xử lý khi thanh toán thành công
                                String paymentMethod = paymentViewModel.getSelectedPaymentMethod().getValue();
                                if (paymentMethod != null && !paymentMethod.isEmpty()) {
                                    processOrder();
                                }
                            }

                            @Override
                            public void onPaymentCanceled(String s, String s1) {
                                AlertDialog dialog = new AlertDialog.Builder(getContext())
                                        .setTitle("Thanh toán bị hủy")
                                        .setMessage("Bạn đã hủy thanh toán")
                                        .setPositiveButton("OK", null)
                                        .show();
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#FF7622"));
                            }

                            @Override
                            public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                                new AlertDialog.Builder(getContext())
                                        .setTitle("Thanh toán thất bại")
                                        .setMessage("Có lỗi xảy ra trong quá trình thanh toán")
                                        .setPositiveButton("OK", null)
                                        .setNegativeButton("Thử lại", (dialog, which) -> {
                                            // Có thể thêm logic để thử lại thanh toán ở đây
                                        }).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Có lỗi xảy ra khi tạo đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void processOrder() {
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
        VoucherResponse selectedVoucher = paymentViewModel.getSelectedVoucher().getValue();

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
                    selectedVoucher != null ? selectedVoucher.getCode() : null,
                    paymentMethod,
                    orderDetails

            );
            paymentViewModel.createOrder(orderRequest);
        }
    }

    private String formatCurrency(int number) {
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(number);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
