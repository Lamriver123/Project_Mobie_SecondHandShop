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
import com.example.marketplacesecondhand.databinding.FragmentBodyPaymentBinding;
import com.example.marketplacesecondhand.dto.response.DeliveryAddressResponse;
import com.example.marketplacesecondhand.dto.response.OrderDetailResponse;
import com.example.marketplacesecondhand.dto.response.OrderResponse;
import com.example.marketplacesecondhand.models.CartProduct;
import com.example.marketplacesecondhand.models.CartShop;
import com.example.marketplacesecondhand.viewModel.LocationViewModel;
import com.example.marketplacesecondhand.viewModel.PaymentViewModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BodyPaymentFragment extends Fragment {
    private static final String TAG = "BodyPaymentFragment";
    private FragmentBodyPaymentBinding binding;
    private LocationViewModel locationViewModel;
    private PaymentViewModel paymentViewModel;
    private OrderPaymentAdapter orderAdapter;
    private List<OrderResponse> orderList;

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

    private void processSelectedShops(List<CartShop> selectedShops) {
        orderList = new ArrayList<>();
        
        for (CartShop shop : selectedShops) {
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
                    
                    // Calculate total amount
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
                    0, // temporary orderId
                    "", // buyerName will be set later
                    shop.getUser().getUsername(),
                    new Date(),
                    String.format("₫%,d", totalAmount),
                    "", // address will be set later
                    "Chờ xác nhận",
                    "", // paymentMethod will be set later
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
        
        binding.txtTotalProduct.setText("Tổng tiền sản phẩm: " + formatCurrency(totalAmount) + " ₫");
        binding.txtShipping.setText("Phí vận chuyển: " + formatCurrency(totalShippingFee) + " ₫");
        binding.txtOrderTotal.setText("Tổng tiền đơn hàng: " + formatCurrency(totalAmount + totalShippingFee) + " ₫");
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

    private String formatCurrency(int number) {
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(number);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
