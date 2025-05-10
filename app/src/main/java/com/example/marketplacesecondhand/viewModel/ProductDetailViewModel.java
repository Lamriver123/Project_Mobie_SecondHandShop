package com.example.marketplacesecondhand.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.marketplacesecondhand.dto.response.ProductResponse;

public class ProductDetailViewModel extends ViewModel {
    private final MutableLiveData<ProductResponse> productDetailLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();


    public void setProductDetail(ProductResponse product) {
        productDetailLiveData.setValue(product);
    }

    public LiveData<ProductResponse> getProductDetail() {
        return productDetailLiveData;
    }

    public void setLoading(boolean isLoading) {
        isLoadingLiveData.setValue(isLoading);
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }

    public void setErrorMessage(String message) {
        errorMessageLiveData.setValue(message);
    }

    public LiveData<String> getErrorMessage() {
        return errorMessageLiveData;
    }

    // Xóa dữ liệu khi ViewModel bị hủy (tùy chọn, nhưng tốt cho việc dọn dẹp)
    @Override
    protected void onCleared() {
        super.onCleared();
        productDetailLiveData.setValue(null);
        errorMessageLiveData.setValue(null);
    }
}
