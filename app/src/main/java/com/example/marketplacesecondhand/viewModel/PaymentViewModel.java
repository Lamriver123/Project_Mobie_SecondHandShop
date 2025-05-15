package com.example.marketplacesecondhand.viewModel; // Hoặc package viewModel của bạn

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.marketplacesecondhand.models.CartShop; // Import model CartShop của bạn
import com.example.marketplacesecondhand.dto.request.OrderRequest;
import com.example.marketplacesecondhand.repository.OrderRepository;
import com.example.marketplacesecondhand.repository.VoucherRepository;
import com.example.marketplacesecondhand.dto.response.VoucherResponse;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class PaymentViewModel extends ViewModel {
    private final MutableLiveData<List<CartShop>> cartShopsToCheckoutLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> orderSuccessLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> selectedPaymentMethodLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalAmountLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> paymentSourceLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<VoucherResponse>> availableVouchers = new MutableLiveData<>();
    private final MutableLiveData<VoucherResponse> selectedVoucher = new MutableLiveData<>();
    private final MutableLiveData<Map<Integer, List<VoucherResponse>>> shopVouchersMap = new MutableLiveData<>(new HashMap<>());
    private int pendingVoucherLoads = 0;
    
    private final OrderRepository orderRepository;
    private final VoucherRepository voucherRepository;

    public PaymentViewModel() {
        orderRepository = new OrderRepository();
        voucherRepository = new VoucherRepository();
    }

    // LiveData cho danh sách các shop và sản phẩm của họ cần thanh toán
    public LiveData<List<CartShop>> getCartShopsToCheckout() {
        return cartShopsToCheckoutLiveData;
    }

    public void setCartShopsToCheckout(List<CartShop> cartShops) {
        cartShopsToCheckoutLiveData.setValue(cartShops);
    }

    // LiveData cho trạng thái tải
    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }

    public void setIsLoading(boolean isLoading) {
        isLoadingLiveData.setValue(isLoading);
    }

    // LiveData cho thông báo lỗi
    public LiveData<String> getError() {
        return errorLiveData;
    }

    public void setError(String error) {
        errorLiveData.setValue(error);
    }

    // LiveData cho trạng thái đặt hàng thành công
    public LiveData<Boolean> getOrderSuccess() {
        return orderSuccessLiveData;
    }

    // LiveData cho phương thức thanh toán được chọn
    public LiveData<String> getSelectedPaymentMethod() {
        return selectedPaymentMethodLiveData;
    }

    public void setSelectedPaymentMethod(String paymentMethod) {
        selectedPaymentMethodLiveData.setValue(paymentMethod);
    }

    // LiveData cho tổng tiền
    public LiveData<Integer> getTotalAmount() {
        return totalAmountLiveData;
    }

    public void setTotalAmount(Integer totalAmount) {
        totalAmountLiveData.setValue(totalAmount);
    }

    // LiveData cho nguồn gọi payment
    public LiveData<String> getPaymentSource() {
        return paymentSourceLiveData;
    }

    public void setPaymentSource(String source) {
        paymentSourceLiveData.setValue(source);
    }

    public void createOrder(OrderRequest orderRequest) {
        setIsLoading(true);
        orderRepository.createOrder(orderRequest, new OrderRepository.OrderCallback() {
            @Override
            public void onSuccess() {
                orderSuccessLiveData.postValue(true);
                setIsLoading(false);
            }

            @Override
            public void onError(String error) {
                setError(error);
                setIsLoading(false);
            }
        });
    }

    public void loadAvailableVouchers(int shopId) {
        setIsLoading(true);
        pendingVoucherLoads++;
        
        voucherRepository.getShopActiveVouchers(shopId, new VoucherRepository.VoucherCallback() {
            @Override
            public void onSuccess(List<VoucherResponse> vouchers) {
                Map<Integer, List<VoucherResponse>> currentMap = shopVouchersMap.getValue();
                if (currentMap != null) {
                    currentMap.put(shopId, vouchers);
                    shopVouchersMap.postValue(currentMap);
                }
                
                pendingVoucherLoads--;
                if (pendingVoucherLoads == 0) {
                    setIsLoading(false);
                }
            }

            @Override
            public void onError(String error) {
                errorLiveData.postValue(error);
                pendingVoucherLoads--;
                if (pendingVoucherLoads == 0) {
                    setIsLoading(false);
                }
            }
        });
    }

//    public VoucherResponse getSelectedVoucherForOrder() {
//        return selectedVoucher.getValue();
//    }

    public void setSelectedVoucher(VoucherResponse voucher) {
        selectedVoucher.setValue(voucher);
        recalculateTotalWithVoucher();
    }

    private void recalculateTotalWithVoucher() {
        Integer currentTotal = totalAmountLiveData.getValue();
        VoucherResponse voucher = selectedVoucher.getValue();
        
        if (currentTotal != null && voucher != null) {
            double discountAmount = 0;
            if ("PERCENTAGE".equals(voucher.getDiscountType())) {
                discountAmount = currentTotal * (voucher.getDiscountValue() / 100);
                if (voucher.getMaximumDiscountAmount() != null) {
                    discountAmount = Math.min(discountAmount, voucher.getMaximumDiscountAmount());
                }
            } else {
                discountAmount = voucher.getDiscountValue();
            }
            
            totalAmountLiveData.setValue((int) (currentTotal - discountAmount));
        }
    }

    public LiveData<List<VoucherResponse>> getAvailableVouchers() {
        return availableVouchers;
    }

    public LiveData<VoucherResponse> getSelectedVoucher() {
        return selectedVoucher;
    }

    public LiveData<Map<Integer, List<VoucherResponse>>> getShopVouchersMap() {
        return shopVouchersMap;
    }
}
