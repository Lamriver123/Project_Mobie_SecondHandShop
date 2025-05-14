package com.example.marketplacesecondhand.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.dto.request.FollowRequest;
import com.example.marketplacesecondhand.dto.response.ShopResponse;
import com.example.marketplacesecondhand.repository.ShopRepository;

import java.util.List;

public class ShopViewModel extends ViewModel {
    private final ShopRepository shopRepository;
    private final MutableLiveData<List<ShopResponse>> shops = new MutableLiveData<>();
    private final MutableLiveData<ShopResponse> currentShop = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> followStatus = new MutableLiveData<>();

    public ShopViewModel(APIService apiService) {
        shopRepository = new ShopRepository(apiService);
    }

    public LiveData<List<ShopResponse>> getShops() {
        return shops;
    }

    public LiveData<ShopResponse> getCurrentShop() {
        return currentShop;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getFollowStatus() {
        return followStatus;
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

    public void loadShopById(int shopId) {
        List<ShopResponse> shopList = shops.getValue();
        if (shopList != null) {
            for (ShopResponse shop : shopList) {
                if (shop.getId() == shopId) {
                    currentShop.postValue(shop);
                    return;
                }
            }
        }
        error.postValue("Không tìm thấy thông tin shop");
    }

    public void loadCurrentUserShop() {
        shopRepository.getCurrentUserShop(new ShopRepository.SingleShopCallback() {
            @Override
            public void onSuccess(ShopResponse shop) {
                currentShop.postValue(shop);
            }

            @Override
            public void onError(String errorMessage) {
                error.postValue(errorMessage);
            }
        });
    }

    public void toggleFollow(int shopId) {
        FollowRequest request = new FollowRequest(shopId);
        shopRepository.toggleFollow(request, new ShopRepository.FollowCallback() {
            @Override
            public void onSuccess(boolean isFollowing) {
                followStatus.postValue(isFollowing);
                loadShops();
                // Reload current shop to get updated follower list
//                if (currentShop.getValue() != null && currentShop.getValue().getId() == shopId) {
//                    loadCurrentUserShop();
//                }
            }

            @Override
            public void onError(String errorMessage) {
                error.postValue(errorMessage);
            }
        });
    }
}