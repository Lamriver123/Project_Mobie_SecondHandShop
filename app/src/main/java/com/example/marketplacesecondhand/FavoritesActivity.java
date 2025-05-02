package com.example.marketplacesecondhand;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.marketplacesecondhand.fragment.favorite.FavoriteContainerFragment;
import com.example.marketplacesecondhand.fragment.favorite.HeaderFavoriteFragment;
import com.example.marketplacesecondhand.fragment.favorite.RecommendedProductsFragment;


public class FavoritesActivity extends AppCompatActivity {
    private HeaderFavoriteFragment headerFavoriteFragment;
    private FavoriteContainerFragment favoriteContainerFragment;
    private RecommendedProductsFragment recommendedProductsFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorites);


        // Add header fragment
        headerFavoriteFragment = new HeaderFavoriteFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.header_favorite, headerFavoriteFragment)
                .commit();

        // Add favorite container fragment
        favoriteContainerFragment = new FavoriteContainerFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.favorite_container, favoriteContainerFragment)
                .commit();

        // Add recommended products fragment
        recommendedProductsFragment = new RecommendedProductsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.recommended_products, recommendedProductsFragment)
                .commit();


    }
}
