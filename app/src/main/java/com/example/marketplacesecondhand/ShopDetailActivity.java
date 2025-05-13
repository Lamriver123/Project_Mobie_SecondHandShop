package com.example.marketplacesecondhand;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.marketplacesecondhand.fragment.store.ProductShopFragment;

public class ShopDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
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
