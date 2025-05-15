package com.example.marketplacesecondhand.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.fragment.cart.CartDetailFragment;
import com.example.marketplacesecondhand.fragment.cart.HeaderCartFragment;

public class CartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        CartDetailFragment fragment1 = new CartDetailFragment();
        //HeaderCartFragment fragment2 = new HeaderCartFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.cart_detail, fragment1)
                .commit();

//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.header_cart, fragment2)
//                .commit();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
