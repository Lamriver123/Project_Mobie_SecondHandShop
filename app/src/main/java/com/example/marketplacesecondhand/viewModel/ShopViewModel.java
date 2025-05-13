package com.example.marketplacesecondhand.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.dto.response.ShopResponse;
import com.example.marketplacesecondhand.repository.ShopRepository;

import java.util.List;

public class ShopViewModel extends ViewModel {
    private final ShopRepository shopRepository;
    private final MutableLiveData<List<ShopResponse>> shops = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public ShopViewModel(APIService apiService) {
        shopRepository = new ShopRepository(apiService);
    }

    public LiveData<List<ShopResponse>> getShops() {
        return shops;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadShops() {
        shopRepository.getAllShops(new ShopRepository.ShopCallback() {
            @Override
            public void onSuccess(List<ShopResponse> shopList) {
                shops.postValue(shopList);
            }

            @Override
            public void onError(String errorMessage) {
                error.postValue(errorMessage);
            }
        });
    }
} 