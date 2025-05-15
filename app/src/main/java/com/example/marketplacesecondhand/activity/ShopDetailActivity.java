package com.example.marketplacesecondhand.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.service.RetrofitClient;
import com.example.marketplacesecondhand.fragment.store.HeaderShopFragment;
import com.example.marketplacesecondhand.fragment.store.ProductShopFragment;
import com.example.marketplacesecondhand.viewModel.ShopViewModel;

public class ShopDetailActivity extends AppCompatActivity {
    private ShopViewModel shopViewModel;
    private int shopId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shop);

        shopId = getIntent().getIntExtra("shop_id", -1);
        if (shopId == -1) {
            finish();
            return;
        }

        // Initialize ViewModel
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        shopViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(Class<T> modelClass) {
                return (T) new ShopViewModel(apiService);
            }
        }).get(ShopViewModel.class);

        // Add HeaderShopFragment
        HeaderShopFragment headerFragment = new HeaderShopFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.header_shop, headerFragment)
                .commit();

        // Add ProductShopFragment
        ProductShopFragment productFragment = new ProductShopFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.shop_detail, productFragment)
                .commit();

        // Load shops list first, then find specific shop
        shopViewModel.getShops().observe(this, shopList -> {
            if (shopList != null && !shopList.isEmpty()) {
                shopViewModel.loadShopById(shopId);
            }
        });
        shopViewModel.loadShops();
    }
}
