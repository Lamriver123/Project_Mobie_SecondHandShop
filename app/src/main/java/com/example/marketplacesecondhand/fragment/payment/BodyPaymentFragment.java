package com.example.marketplacesecondhand.fragment.payment;

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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.adapter.payment.OrderPaymentAdapter;
import com.example.marketplacesecondhand.adapter.payment.ProductPaymentAdapter;
import com.example.marketplacesecondhand.adapter.VoucherAdapter;
import com.example.marketplacesecondhand.databinding.FragmentBodyPaymentBinding;
import com.example.marketplacesecondhand.dto.response.DeliveryAddressResponse;
import com.example.marketplacesecondhand.dto.response.OrderDetailResponse;
import com.example.marketplacesecondhand.dto.response.OrderResponse;
import com.example.marketplacesecondhand.dto.response.VoucherResponse;
import com.example.marketplacesecondhand.models.CartProduct;
import com.example.marketplacesecondhand.models.CartShop;
import com.example.marketplacesecondhand.viewModel.LocationViewModel;
import com.example.marketplacesecondhand.viewModel.PaymentViewModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

public class BodyPaymentFragment extends Fragment {
    private static final String TAG = "BodyPaymentFragment";
    private FragmentBodyPaymentBinding binding;
    private LocationViewModel locationViewModel;
    private PaymentViewModel paymentViewModel;
    private OrderPaymentAdapter orderAdapter;
    private List<OrderResponse> orderList;
    private VoucherAdapter voucherAdapter;
    private List<VoucherResponse> voucherList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);
        paymentViewModel = new ViewModelProvider(requireActivity()).get(PaymentViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBodyPaymentBinding.inflate(inflater, container, false);
        locationViewModel.loadAddresses();
        locationViewModel.getSelectedAddress();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.layoutPaymentBody.requestFocus();

        setupAddressObserver();
        setupPaymentObserver();
        setupLocationButton();
        setupPaymentMethodSelection();
        setupVoucherObserver();
    }

    private void setupPaymentObserver() {
        paymentViewModel.getCartShopsToCheckout().observe(getViewLifecycleOwner(), selectedShops -> {
            if (selectedShops != null && !selectedShops.isEmpty()) {
                processSelectedShops(selectedShops);
                setupOrderList();
            } else {
                Log.e(TAG, "No selected shops found in ViewModel");
                binding.recyclerOrder.setVisibility(View.GONE);
            }
        });

        paymentViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupPaymentMethodSelection() {
        // Mặc định chọn COD
        paymentViewModel.setSelectedPaymentMethod("COD");
        binding.radioCod.setChecked(true);

        binding.radioZaloPay.setOnClickListener(v -> {
            binding.radioZaloPay.setChecked(true);
            binding.radioCod.setChecked(false);
            paymentViewModel.setSelectedPaymentMethod("ZALOPAY");
        });

        binding.radioCod.setOnClickListener(v -> {
            binding.radioCod.setChecked(true);
            binding.radioZaloPay.setChecked(false);
            paymentViewModel.setSelectedPaymentMethod("COD");
        });
    }

    private void setupVoucherObserver() {
        // Observe loading state
        paymentViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                binding.recyclerVoucher.setVisibility(View.GONE);
            }
        });

        // Observe vouchers from all shops
        paymentViewModel.getShopVouchersMap().observe(getViewLifecycleOwner(), shopVouchersMap -> {
            if (shopVouchersMap != null && !shopVouchersMap.isEmpty()) {
                // Combine all vouchers from different shops
                List<VoucherResponse> allVouchers = new ArrayList<>();
                for (List<VoucherResponse> vouchers : shopVouchersMap.values()) {
                    if (vouchers != null) {
                        allVouchers.addAll(vouchers);
                    }
                }
                
                if (!allVouchers.isEmpty()) {
                    voucherList = allVouchers;
                    setupVoucherList();
                    binding.recyclerVoucher.setVisibility(View.VISIBLE);
                } else {
                    binding.recyclerVoucher.setVisibility(View.GONE);
                }
            } else {
                binding.recyclerVoucher.setVisibility(View.GONE);
            }
        });

        // Observe selected voucher
        paymentViewModel.getSelectedVoucher().observe(getViewLifecycleOwner(), selectedVoucher -> {
            if (selectedVoucher != null) {
                updateOrderSummary(selectedVoucher);
            }
        });

        // Observe errors
        paymentViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processSelectedShops(List<CartShop> selectedShops) {
        orderList = new ArrayList<>();
        
        // Clear previous vouchers
//        Map<Integer, List<VoucherResponse>> currentMap = paymentViewModel.getShopVouchersMap().getValue();
//        Map<Integer, List<VoucherResponse>> map = paymentViewModel.getShopVouchersMap().getValue();
//        if (map != null) {
//            map.clear();
//            shopVouchersMap.setValue(map); // notify observers
//        }
        for (CartShop shop : selectedShops) {
            // Load vouchers for each shop
            if (shop.getUser() != null && shop.getUser().getId() > 0) {
                paymentViewModel.loadAvailableVouchers(shop.getUser().getId());
            }

            List<OrderDetailResponse> orderDetails = new ArrayList<>();
            int totalAmount = 0;

            for (CartProduct cartProduct : shop.getProducts()) {
                if (cartProduct.isChecked()) {
                    OrderDetailResponse orderDetail = new OrderDetailResponse(
                        cartProduct.getProductResponse().getProductId(),
                        cartProduct.getProductResponse().getProductName(),
                        cartProduct.getQuantityCart(),
                        cartProduct.getProductResponse().getCurrentPrice(),
                        cartProduct.getProductResponse().getCurrentImages().get(0)
                    );
                    orderDetails.add(orderDetail);
                    
                    try {
                        String priceStr = cartProduct.getProductResponse().getCurrentPrice()
                            .replaceAll("[^\\d]", "");
                        totalAmount += Integer.parseInt(priceStr) * cartProduct.getQuantityCart();
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Error parsing price", e);
                    }
                }
            }

            if (!orderDetails.isEmpty()) {
                OrderResponse order = new OrderResponse(
                    0,
                    "", // buyerName will be set later
                    shop.getUser().getUsername(),
                    new Date(),
                    String.format("₫%,d", totalAmount),
                    "", // address will be set later
                    "Chờ xác nhận",
                    paymentViewModel.getSelectedPaymentMethod().getValue(), // Get selected payment method
                    orderDetails
                );
                orderList.add(order);
            }
        }
    }

    private void setupOrderList() {
        if (orderList == null || orderList.isEmpty()) {
            binding.recyclerOrder.setVisibility(View.GONE);
            return;
        }

        // Setup order adapter
        orderAdapter = new OrderPaymentAdapter(requireContext(), orderList);
        binding.recyclerOrder.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerOrder.setAdapter(orderAdapter);

        // Calculate total amount
        int totalAmount = 0;
        for (OrderResponse order : orderList) {
            try {
                String priceStr = order.getTotalAmount().replaceAll("[^\\d]", "");
                totalAmount += Integer.parseInt(priceStr);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error parsing total amount", e);
            }
        }

        // Update order summary
        int shippingFee = 30000; // Example shipping fee per order
        int totalShippingFee = shippingFee * orderList.size();
        int finalTotal = totalAmount + totalShippingFee;
        
        binding.txtTotalProduct.setText("Tổng tiền sản phẩm: " + formatCurrency(totalAmount) + " ₫");
        binding.txtShipping.setText("Phí vận chuyển: " + formatCurrency(totalShippingFee) + " ₫");
        binding.txtOrderTotal.setText("Tổng tiền đơn hàng: " + formatCurrency(finalTotal) + " ₫");

        // Save total amount to ViewModel
        paymentViewModel.setTotalAmount(finalTotal);
    }

    private void setupAddressObserver() {
        locationViewModel.getSelectedAddress().observe(getViewLifecycleOwner(), address -> {
            if (address != null) {
                binding.txtAddress.setText(address.getAddressName());
            } else {
                List<DeliveryAddressResponse> addresses = locationViewModel.getAddresses().getValue();
                if (addresses != null && !addresses.isEmpty()) {
                    for (DeliveryAddressResponse addr : addresses) {
                        if (addr.getDefaultAddress() != 0) {
                            binding.txtAddress.setText(addr.getAddressName());
                            locationViewModel.setSelectedAddress(addr);
                            break;
                        }
                    }
                } else {
                    binding.txtAddress.setText("Chọn địa chỉ giao hàng");
                }
            }
        });

        locationViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupLocationButton() {
        binding.btnChooseLocation.setOnClickListener(v -> {
            BottomSheetAddLocationFragment bottomSheet = new BottomSheetAddLocationFragment();
            bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
        });
    }

    private void setupVoucherList() {
        if (voucherList == null || voucherList.isEmpty()) {
            binding.recyclerVoucher.setVisibility(View.GONE);
            return;
        }

        if (voucherAdapter == null) {
            voucherAdapter = new VoucherAdapter(voucherList, voucher -> {
                // Khi voucher được chọn, cập nhật ViewModel và tổng tiền
                paymentViewModel.setSelectedVoucher(voucher);
                updateOrderSummary(voucher);
            });
            LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            layoutManager.setAutoMeasureEnabled(true);
            binding.recyclerVoucher.setLayoutManager(layoutManager);
            binding.recyclerVoucher.setAdapter(voucherAdapter);
        } else {
            voucherAdapter.updateVouchers(voucherList);
        }
    }

    private void updateOrderSummary(VoucherResponse voucher) {
        if (orderList == null || orderList.isEmpty()) {
            return;
        }

        // Calculate total amount
        int totalAmount = 0;
        for (OrderResponse order : orderList) {
            try {
                String priceStr = order.getTotalAmount().replaceAll("[^\\d]", "");
                totalAmount += Integer.parseInt(priceStr);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error parsing total amount", e);
            }
        }

        // Update order summary
        int shippingFee = 30000;
        int totalShippingFee = shippingFee * orderList.size();
        
        // Calculate discount amount
        double discountAmount = 0;
        if (voucher != null) {
            if ("percentage".equalsIgnoreCase(voucher.getDiscountType())) {
                discountAmount = totalAmount * (voucher.getDiscountValue() / 100.0);
                if (voucher.getMaximumDiscountAmount() != null) {
                    discountAmount = Math.min(discountAmount, voucher.getMaximumDiscountAmount());
                }
            } else {
                discountAmount = voucher.getDiscountValue();
            }
        }
        
        int finalTotal = (int) (totalAmount + totalShippingFee - discountAmount);
        
        // Update UI
        binding.txtTotalProduct.setText("Tổng tiền sản phẩm: " + formatCurrency(totalAmount) + " ₫");
        binding.txtShipping.setText("Phí vận chuyển: " + formatCurrency(totalShippingFee) + " ₫");
        
        if (discountAmount > 0) {
            binding.txtDiscount.setText("Giảm giá: -" + formatCurrency((int)discountAmount) + " ₫");
            binding.txtDiscount.setVisibility(View.VISIBLE);
        } else {
            binding.txtDiscount.setVisibility(View.GONE);
        }
        
        binding.txtOrderTotal.setText("Tổng tiền đơn hàng: " + formatCurrency(finalTotal) + " ₫");

        // Update total amount in ViewModel for footer
        paymentViewModel.setTotalAmount(finalTotal);
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
