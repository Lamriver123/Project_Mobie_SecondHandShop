package com.example.marketplacesecondhand.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.service.RetrofitClient;
import com.example.marketplacesecondhand.dto.request.EmailRequest;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {
    TextView btnResetPassword;
    EditText editTextEmailAddress;
    ProgressBar progressBarLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgotpassword);

        progressBarLogin = findViewById(R.id.progressBarForgotPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarLogin.setVisibility(View.VISIBLE);
                btnResetPassword.setEnabled(false);
                editTextEmailAddress = findViewById(R.id.editTextEmailAddress);
                String email = editTextEmailAddress.getText().toString().trim();

                EmailRequest emailRequest = new EmailRequest(email);
                sendOtp(emailRequest);
            }

        });
    }
    private void sendOtp(EmailRequest emailRequest) {

        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<ApiResponse<Void>> call = apiService.forgotPassword(emailRequest);
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                progressBarLogin.setVisibility(View.GONE);
                btnResetPassword.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ForgotPasswordActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    openOtpRegisterActivity(emailRequest.getEmail());
                }
                else {
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            ApiResponse<String> errorResponse = new Gson().fromJson(errorJson, ApiResponse.class);
                            Toast.makeText(ForgotPasswordActivity.this, errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this, "Lỗi không xác định!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(ForgotPasswordActivity.this, "Lỗi hệ thống!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                progressBarLogin.setVisibility(View.GONE);
                btnResetPassword.setEnabled(true);
                Log.e("Register", "Error: " + t.getMessage());
                Toast.makeText(ForgotPasswordActivity.this, "Lỗi hệ thống!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void openOtpRegisterActivity(String email) {
        Intent intent = new Intent(ForgotPasswordActivity.this, OtpActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("source", "forgotPassword");
        startActivity(intent);
    }
}
