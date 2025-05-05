package com.example.marketplacesecondhand;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.marketplacesecondhand.fragment.ProductDetailFragment;
import com.example.marketplacesecondhand.fragment.ProductShopFragment;

public class ShopDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        int productId = getIntent().getIntExtra("shop_id", -1);

        Bundle bundle = new Bundle();
        bundle.putInt("shop_id", productId);

        ProductShopFragment fragment = new ProductShopFragment();
        fragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.shop_detail, fragment)
                .commit();

    }
}
