package com.example.marketplacesecondhand;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.marketplacesecondhand.fragment.FilterFragment;
import com.example.marketplacesecondhand.fragment.HeaderWithBackFragment;
import com.example.marketplacesecondhand.fragment.ProductCategoryFragment;
import com.example.marketplacesecondhand.models.Category;

public class ActivityCategory extends AppCompatActivity implements FilterFragment.OnFilterChangeListener {
    private ProductCategoryFragment productFragment;
    private HeaderWithBackFragment headerFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category);

        String categoryName = getIntent().getStringExtra("category_name");
        int categoryId = getIntent().getIntExtra("category_id", -1);

        // Setup header fragment
        headerFragment = new HeaderWithBackFragment();
        Bundle headerBundle = new Bundle();
        headerBundle.putString("category_name", categoryName);
        headerFragment.setArguments(headerBundle);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.header_with_back, headerFragment)
                .commit();

        // Setup filter fragment
        FilterFragment filterFragment = new FilterFragment();
        Bundle filterBundle = new Bundle();
        filterBundle.putInt("category_id", categoryId);
        filterFragment.setArguments(filterBundle);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.filter_container, filterFragment)
                .commit();

        // Setup product fragment
        productFragment = new ProductCategoryFragment();
        Bundle productBundle = new Bundle();
        productBundle.putInt("category_id", categoryId);
        productFragment.setArguments(productBundle);
        
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.category_content, productFragment)
                .commit();
    }

    @Override
    public void onCategoryChanged(Category category) {
        if (productFragment != null) {
            // Tạo bundle mới với category id mới
            Bundle args = new Bundle();
            args.putInt("category_id", category.getCategoryId());
            args.putString("category_name", category.getCategoryName());

            // Tạo fragment header mới để load search
            headerFragment = new HeaderWithBackFragment();
            headerFragment.setArguments(args);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.header_with_back, headerFragment)
                    .commit();

            // Tạo fragment mới
            productFragment = new ProductCategoryFragment();
            productFragment.setArguments(args);
            
            // Replace fragment cũ bằng fragment mới
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.category_content, productFragment)
                    .commit();
        }
    }
}
