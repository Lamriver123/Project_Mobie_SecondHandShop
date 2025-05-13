package com.example.marketplacesecondhand.viewModel; // Hoặc package viewModel của bạn

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.marketplacesecondhand.models.CartShop; // Import model CartShop của bạn
import com.example.marketplacesecondhand.dto.request.OrderRequest;
import com.example.marketplacesecondhand.repository.OrderRepository;

import java.util.List;

public class PaymentViewModel extends ViewModel {
    private final MutableLiveData<List<CartShop>> cartShopsToCheckoutLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> orderSuccessLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> selectedPaymentMethodLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> totalAmountLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> paymentSourceLiveData = new MutableLiveData<>();
    private final OrderRepository orderRepository;

    public PaymentViewModel() {
        orderRepository = new OrderRepository();
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
    public LiveData<String> getTotalAmount() {
        return totalAmountLiveData;
    }

    public void setTotalAmount(String totalAmount) {
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
}
