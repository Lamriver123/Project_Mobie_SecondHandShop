package com.example.marketplacesecondhand.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.service.RetrofitClient;
import com.example.marketplacesecondhand.dto.request.RegisterRequest;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etBirthday, etPhoneNumber, etEmail, etUsername, etPassword;
    private RadioButton rbMale, rbFemale;
    private ImageButton btnSignUp;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        mapping();

        etBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void mapping() {
        etFullName = findViewById(R.id.editTextFullName);
        etBirthday = findViewById(R.id.etBirthday);
        etPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        etEmail = findViewById(R.id.editTextEmailAddress);
        etUsername = findViewById(R.id.editTextUname);
        etPassword = findViewById(R.id.editTextTextPassword);
        rbMale = findViewById(R.id.radioButton);
        rbFemale = findViewById(R.id.radioButton2);
        btnSignUp = findViewById(R.id.btnSignUp);
        progressBar = findViewById(R.id.progressBar);
    }

    private void registerUser() {
        String fullName = etFullName.getText().toString().trim();
        String dateOfBirth = etBirthday.getText().toString().trim();
        String phone = etPhoneNumber.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        String gender = rbMale.isChecked() ? "Male" : rbFemale.isChecked() ? "Female" : "";

        if (fullName.isEmpty() || dateOfBirth.isEmpty() || phone.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() || gender.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hiển thị ProgressBar và vô hiệu hóa nút
        progressBar.setVisibility(View.VISIBLE);
        btnSignUp.setEnabled(false);
        // Bạn cũng có thể vô hiệu hóa các trường nhập liệu khác nếu muốn
        // setInputFieldsEnabled(false);

        RegisterRequest request = new RegisterRequest(fullName, phone, gender, dateOfBirth, email, username, password);

        sendRegisterRequest(request);
    }

    private void sendRegisterRequest(RegisterRequest request) {
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<ApiResponse<Void>> call = apiService.registerUser(request);
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                // Ẩn ProgressBar và kích hoạt lại nút
                progressBar.setVisibility(View.GONE);
                btnSignUp.setEnabled(true);
                // setInputFieldsEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegisterActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        openOtpOtpActivity(request.getEmail());
                }
                else {
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            ApiResponse<?> errorResponse = new Gson().fromJson(errorJson, ApiResponse.class);
                            Toast.makeText(RegisterActivity.this, errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Lỗi không xác định!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(RegisterActivity.this, "Lỗi hệ thống!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                // Ẩn ProgressBar và kích hoạt lại nút
                progressBar.setVisibility(View.GONE);
                btnSignUp.setEnabled(true);
                // setInputFieldsEnabled(true);
                Log.e("Register", "Error: " + t.getMessage());
                Toast.makeText(RegisterActivity.this, "Lỗi hệ thống!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openOtpOtpActivity(String email) {
        Intent intent = new Intent(RegisterActivity.this, OtpActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("source", "register");
        startActivity(intent);
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        // Định dạng ngày thành DD/MM/YYYY
                        String formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                        //   String formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                        etBirthday.setText(formattedDate);
                    }
                },
                year, month, day
        );

        datePickerDialog.show();
    }
}