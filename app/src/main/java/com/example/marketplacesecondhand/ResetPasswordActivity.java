package com.example.marketplacesecondhand;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.dto.request.ResetPasswordRequest;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {
    EditText editTextPassword, editTextConfirmPassword;
    Button btnResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resetpassword);

        mapping();

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = getIntent().getStringExtra("email");
                String newPassword = editTextPassword.getText().toString().trim();
                String confirmPassword = editTextConfirmPassword.getText().toString().trim();
                ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(email, newPassword, confirmPassword);
                resetPassword(resetPasswordRequest);
            }
        });
    }
    private void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<ApiResponse<Void>> call = apiService.resetPassword(resetPasswordRequest);
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ResetPasswordActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                    String continueActivity = getIntent().getStringExtra("continue");
                    if (continueActivity != null && continueActivity.equals("editProfile")) {
                        finish();
                    }
                    else {
                        openLoginActivity();
                    }
                }
                else {
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            ApiResponse<?> errorResponse = new Gson().fromJson(errorJson, ApiResponse.class);
                            Toast.makeText(ResetPasswordActivity.this, errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ResetPasswordActivity.this, "Lỗi không xác định!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(ResetPasswordActivity.this, "Lỗi hệ thống!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Log.e("Register", "Error: " + t.getMessage());
                Toast.makeText(ResetPasswordActivity.this, "Lỗi hệ thống!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void mapping() {
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
    }
}
