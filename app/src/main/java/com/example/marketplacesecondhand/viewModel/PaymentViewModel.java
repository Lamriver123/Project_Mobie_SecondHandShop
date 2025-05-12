package com.example.marketplacesecondhand.viewModel; // Hoặc package viewModel của bạn

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.marketplacesecondhand.models.CartShop; // Import model CartShop của bạn

import java.util.List;

public class PaymentViewModel extends ViewModel {
    private final MutableLiveData<List<CartShop>> cartShopsToCheckoutLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    // LiveData cho danh sách các shop và sản phẩm của họ cần thanh toán
    public LiveData<List<CartShop>> getCartShopsToCheckout() {
        return cartShopsToCheckoutLiveData;
    }

    public void setCartShopsToCheckout(List<CartShop> cartShops) {
        cartShopsToCheckoutLiveData.setValue(cartShops);
    }

    // LiveData cho trạng thái tải (nếu cần tải thêm gì đó trong PaymentViewModel)
    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }

    public void setIsLoading(boolean isLoading) {
        isLoadingLiveData.setValue(isLoading);
    }

    // LiveData cho thông báo lỗi (nếu có)
    public LiveData<String> getError() {
        return errorLiveData;
    }

    public void setError(String error) {
        errorLiveData.setValue(error);
    }
}
