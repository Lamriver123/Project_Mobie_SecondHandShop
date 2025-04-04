package com.example.marketplacesecondhand;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.dto.request.EmailRequest;
import com.example.marketplacesecondhand.dto.request.VerifyAccountRequest;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpActivity extends AppCompatActivity {
    Button btnOtp;
    PinView pinView;
    TextView tvResendOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp);

        btnOtp = findViewById(R.id.btnOtp);
        tvResendOtp = findViewById(R.id.tvResendOtp);
        pinView = findViewById(R.id.pinView);

        pinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 6) {
                    btnOtp.performClick(); // tự động gọi sự kiện bấm nút "Verify"
                }
            }
        });

        btnOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String otp = pinView.getText().toString().trim();


                String email = getIntent().getStringExtra("email");
                VerifyAccountRequest verifyAccountRequest = new VerifyAccountRequest(email, otp);
                activateAccount(verifyAccountRequest);
            }

        });

        tvResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pinView.setText("");

                String email = getIntent().getStringExtra("email");
                EmailRequest emailRequest = new EmailRequest(email);
                regenerateOtp(emailRequest);
            }
        });


//        pinView = findViewById(R.id.pinView);
//        pinView.setOnClickListener(str -> {
//            Toast.makeText(this, "OTP Entered: " + str, Toast.LENGTH_SHORT).show();
//        });

    }

    private void activateAccount(VerifyAccountRequest verifyAccountRequest) {
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<ApiResponse<Void>> call = apiService.verifyAccount(verifyAccountRequest);
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(OtpActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    openLoginActivity();
                }
                else {
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            ApiResponse<?> errorResponse = new Gson().fromJson(errorJson, ApiResponse.class);
                            Toast.makeText(OtpActivity.this, errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(OtpActivity.this, "Lỗi không xác định!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(OtpActivity.this, "Lỗi hệ thống!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Log.e("Register", "Error: " + t.getMessage());
                Toast.makeText(OtpActivity.this, "Lỗi hệ thống!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void regenerateOtp(EmailRequest emailRequest) {
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<ApiResponse<Void>> call = apiService.regenerateOtp(emailRequest);
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(OtpActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            ApiResponse<?> errorResponse = new Gson().fromJson(errorJson, ApiResponse.class);
                            Toast.makeText(OtpActivity.this, errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(OtpActivity.this, "Lỗi không xác định!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(OtpActivity.this, "Lỗi hệ thống!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Log.e("Register", "Error: " + t.getMessage());
                Toast.makeText(OtpActivity.this, "Lỗi hệ thống!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void openLoginActivity() {
        String source = getIntent().getStringExtra("source");
        Log.e("source", source);
        if (source.equals("register")) {
            Intent intent = new Intent(OtpActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        else if (source.equals("forgotPassword")){
            Intent intent = new Intent(OtpActivity.this, ResetPasswordActivity.class);
            String email = getIntent().getStringExtra("email");
            intent.putExtra("email", email);
            startActivity(intent);
        }
    }
}
