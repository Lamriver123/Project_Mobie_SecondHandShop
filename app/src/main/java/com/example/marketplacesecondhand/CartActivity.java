package com.example.marketplacesecondhand;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.marketplacesecondhand.fragment.cart.CartDetailFragment;

public class CartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        CartDetailFragment fragment = new CartDetailFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.cart_detail, fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
