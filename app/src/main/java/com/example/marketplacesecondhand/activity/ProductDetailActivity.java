package com.example.marketplacesecondhand.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.fragment.productDetail.ProductDetailFragment;

public class ProductDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
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

