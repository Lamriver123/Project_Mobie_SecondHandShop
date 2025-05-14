package com.example.marketplacesecondhand.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.fragment.search.FilterFragment;
import com.example.marketplacesecondhand.fragment.HeaderWithBackFragment;
import com.example.marketplacesecondhand.fragment.search.PriceFilterBottomSheetFragment;
import com.example.marketplacesecondhand.fragment.ProductCategoryFragment;
import com.example.marketplacesecondhand.models.Category;

public class ActivityCategory extends AppCompatActivity implements FilterFragment.OnFilterChangeListener, HeaderWithBackFragment.SearchHandler {
    private ProductCategoryFragment productFragment;
    private HeaderWithBackFragment headerFragment;
    private FilterFragment filterFragment;

    private static final String TAG = "ActivityCategory";
    private String currentKeyword = "";
    private int currentCategoryId = -1;
    private String currentCategoryName = "Danh mục";
    private int currentMinPrice = -1;
    private int currentMaxPrice = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category);

        currentKeyword = getIntent().getStringExtra("keyword");
        if (currentKeyword == null) currentKeyword = "";
        currentCategoryName = getIntent().getStringExtra("category_name");
        if (currentCategoryName == null) currentCategoryName = "";
        currentCategoryId = getIntent().getIntExtra("category_id", -1);
        currentMinPrice = getIntent().getIntExtra("min_price", -1);
        currentMaxPrice = getIntent().getIntExtra("max_price", -1);

        Log.d(TAG, "onCreate - Initial filters: keyword=" + currentKeyword + ", catId=" + currentCategoryId +
                ", catName=" + currentCategoryName + ", minP=" + currentMinPrice + ", maxP=" + currentMaxPrice);
        setupHeaderFragment();
        setupFilterFragment();
        loadProductFragment();
    }

    private void setupHeaderFragment() {
        if (getSupportFragmentManager().findFragmentById(R.id.header_with_back) == null) {
            headerFragment = new HeaderWithBackFragment();
        } else {
            headerFragment = (HeaderWithBackFragment) getSupportFragmentManager().findFragmentById(R.id.header_with_back);
        }
        if (headerFragment != null) { // headerFragment có thể null nếu layout không có R.id.header_with_back
            headerFragment.setSearchHandler(this);
            Bundle headerArgs = new Bundle();
            headerArgs.putString("keyword", currentKeyword); // Truyền keyword ban đầu nếu muốn SearchView hiển thị
            headerFragment.setArguments(headerArgs);

            if (getSupportFragmentManager().findFragmentById(R.id.header_with_back) == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.header_with_back, headerFragment)
                        .commit();
            }
        } else {
            Log.e(TAG, "HeaderFragment container (R.id.header_with_back) not found or headerFragment is null.");
        }
    }

    private void setupFilterFragment() {
        if (getSupportFragmentManager().findFragmentById(R.id.filter_container) == null) {
            filterFragment = new FilterFragment();
            Bundle filterBundle = new Bundle();
            filterBundle.putInt("category_id", currentCategoryId);
            filterBundle.putString("category_name", currentCategoryName);

            filterFragment.setArguments(filterBundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.filter_container, filterFragment)
                    .commit();
        } else {
            filterFragment = (FilterFragment) getSupportFragmentManager().findFragmentById(R.id.filter_container);
        }
        if (filterFragment != null && filterFragment.isAdded()) {
            Bundle args = filterFragment.getArguments();
            if (args == null) args = new Bundle();
            args.putInt("category_id", currentCategoryId);
            args.putString("category_name", currentCategoryName);
            if(!filterFragment.isStateSaved()) {
                filterFragment.setArguments(args);
            }

        }
    }


    private void loadProductFragment() {
        Log.i(TAG, "Loading ProductFragment with: Keyword='" + currentKeyword +
                "', CategoryID=" + currentCategoryId +
                ", MinPrice=" + currentMinPrice +
                ", MaxPrice=" + currentMaxPrice);

        productFragment = new ProductCategoryFragment();
        Bundle productBundle = new Bundle();
        productBundle.putString("keyword", currentKeyword);
        productBundle.putInt("category_id", currentCategoryId);
        productBundle.putInt("min_price", currentMinPrice);
        productBundle.putInt("max_price", currentMaxPrice);
        productFragment.setArguments(productBundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.category_content, productFragment)
                .commit();
    }


    @Override
    public void onCategoryChanged(Category category) {
        Log.d(TAG, "onCategoryChanged: " + category.getCategoryName() + " (ID: " + category.getCategoryId() + ")");
        this.currentCategoryId = category.getCategoryId();
        this.currentCategoryName = category.getCategoryName();
        this.currentKeyword = ""; // Reset keyword
        this.currentMinPrice = -1;  // Reset giá
        this.currentMaxPrice = -1;

        if (headerFragment != null && headerFragment.isAdded()) {
            headerFragment.clearSearchQuery();
        }
        // Cập nhật lại FilterFragment nếu cần (ví dụ: nút giá về mặc định)
        if (filterFragment != null && filterFragment.isAdded()) {
            filterFragment.resetPriceButton();
        }

        loadProductFragment();
    }

    @Override
    public void onPriceChanged(int categoryIdFromFilter, int minPrice, int maxPrice) {
        // categoryIdFromFilter có thể dùng để xác nhận, nhưng chúng ta đã có currentCategoryId
        Log.d(TAG, "onPriceChanged: For CategoryID=" + this.currentCategoryId + // Sử dụng currentCategoryId của Activity
                ", MinPrice=" + minPrice + ", MaxPrice=" + maxPrice);
        // this.currentCategoryId KHÔNG nên thay đổi ở đây, nó chỉ đổi khi onCategoryChanged
        this.currentMinPrice = minPrice;
        this.currentMaxPrice = maxPrice;
        // Keyword giữ nguyên

        loadProductFragment();
    }

    @Override
    public void performSearch(String query) {
        Log.d(TAG, "performSearch called with query: " + query);
        this.currentKeyword = query != null ? query.trim() : "";
        loadProductFragment();
    }

    @Override
    public int getCurrentCategoryIdForSearch() { return currentCategoryId; }

    @Override
    public int getCurrentMinPriceForSearch() { return currentMinPrice; }

    @Override
    public int getCurrentMaxPriceForSearch() { return currentMaxPrice; }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Xóa SharedPreferences nếu cần
        SharedPreferences prefs = getSharedPreferences(PriceFilterBottomSheetFragment.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(PriceFilterBottomSheetFragment.KEY_MIN_PRICE);
        editor.remove(PriceFilterBottomSheetFragment.KEY_MAX_PRICE);
        editor.apply();
        Log.d(TAG, "onDestroy - Cleared price filter SharedPreferences");
    }
}
