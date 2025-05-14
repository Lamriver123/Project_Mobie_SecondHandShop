package com.example.marketplacesecondhand;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class IntroActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intro);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(IntroActivity.this, HomeActivity.class));
            finish(); // Đóng SplashActivity
        }, 2000); // Hiển thị trong 2 giây
    }
}
