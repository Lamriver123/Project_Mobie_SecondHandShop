package com.example.marketplacesecondhand;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.marketplacesecondhand.fragment.ProductDetailFragment;

public class ProductDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        int productId = getIntent().getIntExtra("product_id", -1);

        Bundle bundle = new Bundle();
        bundle.putInt("product_id", productId);

        ProductDetailFragment fragment = new ProductDetailFragment();
        fragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.product_detail, fragment)
                .commit();

    }


}

