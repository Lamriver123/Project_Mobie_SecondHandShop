package com.example.marketplacesecondhand;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.marketplacesecondhand.fragment.FilterFragment;
import com.example.marketplacesecondhand.fragment.HeaderWithBackFragment;
import com.example.marketplacesecondhand.fragment.ProductCategoryFragment;

public class ActivityCategory extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category);

        String categoryName = getIntent().getStringExtra("category_name");
        int categoryId = getIntent().getIntExtra("category_id", -1);

        // Setup header fragment
        HeaderWithBackFragment headerFragment = new HeaderWithBackFragment();
        Bundle headerBundle = new Bundle();
        headerBundle.putString("category_name", categoryName);
        headerFragment.setArguments(headerBundle);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.header_with_back, headerFragment)
                .commit();

        // Setup filter fragment
        FilterFragment filterFragment = new FilterFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.filter_container, filterFragment)
                .commit();

        // Setup product fragment
        ProductCategoryFragment productFragment = new ProductCategoryFragment();
        Bundle productBundle = new Bundle();
        productBundle.putInt("category_id", categoryId);
        productFragment.setArguments(productBundle);
        
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.category_content, productFragment)
                .commit();
    }
}
