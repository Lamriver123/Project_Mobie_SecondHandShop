package com.example.marketplacesecondhand;

import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.marketplacesecondhand.adapter.SliderAdapter;
import com.example.marketplacesecondhand.databinding.ActivityHomeBinding;
import com.example.marketplacesecondhand.fragment.HomeFragment;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Gắn HomeFragment vào container
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new HomeFragment())
                .commit();
    }
}
