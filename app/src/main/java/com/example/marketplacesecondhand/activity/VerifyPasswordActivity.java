package com.example.marketplacesecondhand.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.service.RetrofitClient;
import com.example.marketplacesecondhand.dto.request.LoginRequest;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.AuthResponse;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyPasswordActivity extends AppCompatActivity {

    private TextInputEditText passwordEditText;
    private Button confirmButton;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_password);

        mapping();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Bật nút nếu có dữ liệu nhập
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                confirmButton.setEnabled(s.length() > 0);
                confirmButton.setBackgroundTintList(ColorStateList.valueOf(
                        s.length() > 0 ? Color.parseColor("#FF5722") : Color.parseColor("#e0e0e0")
                ));
                confirmButton.setTextColor(s.length() > 0 ? Color.WHITE : Color.GRAY);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        confirmButton.setOnClickListener(v -> {
            verifyPassword();
        });
    }

    private void mapping() {
        passwordEditText = findViewById(R.id.editTextPassword);
        confirmButton = findViewById(R.id.btnConfirm);
        ivBack = findViewById(R.id.ivBack);
    }

    private void verifyPassword() {
        String username = getIntent().getStringExtra("username");
        String password = passwordEditText.getText().toString();
        LoginRequest loginRequest = new LoginRequest(username, password);

        verify(loginRequest);
    }

    private void verify(LoginRequest loginRequest) {
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);

        Call<ApiResponse<AuthResponse>> call = apiService.login(loginRequest);
        call.enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<AuthResponse> apiResponse = response.body();
                    AuthResponse authResponse = apiResponse.getData(); // Lấy AuthResponse từ ApiResponse

                    if (authResponse != null) {

                        Toast.makeText(VerifyPasswordActivity.this, "Account Verification Successful!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(VerifyPasswordActivity.this, ResetPasswordActivity.class);
                        intent.putExtra("email", getIntent().getStringExtra("email"));
                        intent.putExtra("continue", "editProfile");
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(VerifyPasswordActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            ApiResponse<?> errorResponse = new Gson().fromJson(errorJson, ApiResponse.class);
                            Toast.makeText(VerifyPasswordActivity.this, errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(VerifyPasswordActivity.this, "Lỗi không xác định!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("LOGIN_BUG", "Exception: " + e);

                        Toast.makeText(VerifyPasswordActivity.this, "Lỗi hệ thống!", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                Toast.makeText(VerifyPasswordActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }
}
