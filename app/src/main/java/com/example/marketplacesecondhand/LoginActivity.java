package com.example.marketplacesecondhand;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.dto.request.LoginRequest;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.AuthResponse;
import com.example.marketplacesecondhand.models.User;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    TextView etUsername;
    TextView etPassword;
    CheckBox cbRememberMe;
    TextView tvRegister, tvForgotPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        // Kiểm tra xem đã lưu username trong SharedPreferences chưa
//        SharedPreferences preferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
//        String savedUsername = preferences.getString("USERNAME", null);
//        if (savedUsername != null) {
//            // Nếu đã lưu, chuyển sang MainActivity
//            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//            return;
//        }

        mapping();

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the sign-up screen
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
                // Navigate to the sign-up screen
//                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//                startActivity(intent);
            }
        });
    }

    private void mapping() {
        tvRegister = findViewById(R.id.signUp);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnLogin = findViewById(R.id.btnLogin);
        etUsername = findViewById(R.id.editTextTextUsername);
        etPassword = findViewById(R.id.editTextTextPassword);
        cbRememberMe = findViewById(R.id.checkBox);
    }
    private void loginUser() {
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);

        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        LoginRequest loginRequest = new LoginRequest(username, password);

        // Gọi API đăng nhập
        Call<ApiResponse<AuthResponse>> call = apiService.login(loginRequest);
        call.enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<AuthResponse> apiResponse = response.body();
                    AuthResponse authResponse = apiResponse.getData(); // Lấy AuthResponse từ ApiResponse

                    if (authResponse != null) {
                        String token = authResponse.getToken(); // Lấy token từ AuthResponse

                        // Bạn có thể lưu token vào SharedPreferences nếu cần
                        // SharedPreferences preferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
                        // SharedPreferences.Editor editor = preferences.edit();
                        // editor.putString("TOKEN", token);
                        // editor.apply();

                        Toast.makeText(LoginActivity.this, "Login successfull!", Toast.LENGTH_SHORT).show();

                        // Chuyển sang MainActivity
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        finish();

                    }
                    else {
                        Toast.makeText(LoginActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            ApiResponse<?> errorResponse = new Gson().fromJson(errorJson, ApiResponse.class);
                            Toast.makeText(LoginActivity.this, errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Lỗi không xác định!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "Lỗi hệ thống!", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
