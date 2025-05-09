package com.example.marketplacesecondhand;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.API.DatabaseHandler;
import com.example.marketplacesecondhand.databinding.DialogEnterEmailBinding;
import com.example.marketplacesecondhand.dto.request.EmailRequest;
import com.example.marketplacesecondhand.dto.request.LoginRequest;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.AuthResponse;
import com.example.marketplacesecondhand.models.User;
import com.example.marketplacesecondhand.models.UserLoginInfo;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int ERROR_CODE_ACCOUNT_NOT_ACTIVATED = 1009; // Mã lỗi từ backend
    Button btnLogin;
    TextInputEditText etUsername;
    TextInputEditText etPassword;
    CheckBox cbRememberMe;
    TextView tvRegister, tvForgotPassword, tvActivateAccount;
    ProgressBar progressBarLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        mapping();

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        tvActivateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEnterEmailDialogForActivation(); // Call the dialog method
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
        tvActivateAccount = findViewById(R.id.tvActivateAccount);
        progressBarLogin = findViewById(R.id.progressBarLogin);

        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        boolean isRemembered = sharedPreferences.getBoolean("rememberMe", false);
        DatabaseHandler db = new DatabaseHandler(this);
        UserLoginInfo userLoginInfo = db.getLoginInfoSQLite();

        if (isRemembered && userLoginInfo != null) {
            String savedUsername = userLoginInfo.username;
            String savedPassword = userLoginInfo.password;
            etUsername.setText(savedUsername);
            etPassword.setText(savedPassword);
            cbRememberMe.setChecked(true);
           // loginUser();
        }
    }
    private void showEnterEmailDialogForActivation() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        // Inflate using ViewBinding
        DialogEnterEmailBinding dialogBinding = DialogEnterEmailBinding.inflate(inflater);
        builder.setView(dialogBinding.getRoot());
        builder.setCancelable(false); // User must interact with buttons

        AlertDialog dialog = builder.create();

        // Request focus and show keyboard
        dialogBinding.etDialogEmail.requestFocus();
        dialog.setOnShowListener(dialogInterface -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(dialogBinding.etDialogEmail, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        dialogBinding.btnDialogEmailConfirm.setOnClickListener(v_confirm -> {
            String emailToActivate = "";
            if (dialogBinding.etDialogEmail.getText() != null) {
                emailToActivate = dialogBinding.etDialogEmail.getText().toString().trim();
            }

            if (TextUtils.isEmpty(emailToActivate) || !Patterns.EMAIL_ADDRESS.matcher(emailToActivate).matches()) {
                dialogBinding.tilDialogEmail.setError("Vui lòng nhập địa chỉ email hợp lệ.");
                return;
            }
            dialogBinding.tilDialogEmail.setError(null); // Clear error
            EmailRequest emailRequest = new EmailRequest(emailToActivate);
            sendOtp(emailRequest);
            dialog.dismiss();
        });

        dialogBinding.btnDialogEmailDismiss.setOnClickListener(v_dismiss -> dialog.dismiss());

        dialog.show();
    }
    private void sendOtp(EmailRequest emailRequest) {
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<ApiResponse<Void>> call = apiService.forgotPassword(emailRequest);
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    openOtpActivity(emailRequest.getEmail());
                }
                else {
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            ApiResponse<String> errorResponse = new Gson().fromJson(errorJson, ApiResponse.class);
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
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Log.e("Register", "Error: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Lỗi hệ thống!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openOtpActivity(String emailToActivate) {
        Intent intent = new Intent(LoginActivity.this, OtpActivity.class);
        intent.putExtra("email", emailToActivate);
        intent.putExtra("source", "login_activation"); // Source to indicate activation flow
        startActivity(intent);
    }

    private void loginUser() {
        String username = "";
        String password = "";

        if (etUsername.getText() != null) {
            username = etUsername.getText().toString().trim();
        }
        if (etPassword.getText() != null) {
            password = etPassword.getText().toString().trim();
        }

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên đăng nhập và mật khẩu.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hiển thị ProgressBar và vô hiệu hóa nút Login
        progressBarLogin.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);
        tvActivateAccount.setVisibility(View.GONE); // Ẩn link kích hoạt (nếu đang hiển thị)
        LoginRequest loginRequest = new LoginRequest(username, password);

        // Gọi API đăng nhập
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<ApiResponse<AuthResponse>> call = apiService.login(loginRequest);
        call.enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                progressBarLogin.setVisibility(View.GONE);
                btnLogin.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<AuthResponse> apiResponse = response.body();
                    AuthResponse authResponse = apiResponse.getData(); // Lấy AuthResponse từ ApiResponse

                    if (authResponse != null && apiResponse.getCode() == 1000) {
                        String token = authResponse.getToken(); // Lấy token từ AuthResponse

                        setRememberMe(cbRememberMe.isChecked());
                        saveUserInfo(authResponse.getId(), etUsername.getText().toString(), etPassword.getText().toString(), token);
                        RetrofitClient.currentToken = token;

                        Log.d("LoginActivity", "Token: " + RetrofitClient.currentToken);

                        Toast.makeText(LoginActivity.this, "Login successfull!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(LoginActivity.this, apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            ApiResponse<?> errorResponse = new Gson().fromJson(errorJson, ApiResponse.class);
                            String message = errorResponse != null ? errorResponse.getMessage() : "Lỗi không xác định từ server.";
                            int code = errorResponse != null ? errorResponse.getCode() : -1;

                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                            Log.e(TAG, "Lỗi đăng nhập: " + message + " (Code: " + code + ")");

                            if (code == ERROR_CODE_ACCOUNT_NOT_ACTIVATED && tvActivateAccount != null) {
                                tvActivateAccount.setVisibility(View.VISIBLE);
                            }
                         //   Toast.makeText(LoginActivity.this, errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Lỗi không xác định!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("LOGIN_BUG", "Exception: " + e);

                        Toast.makeText(LoginActivity.this, "Lỗi hệ thống!", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                progressBarLogin.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                Log.e(TAG, "Lỗi gọi API đăng nhập: " + t.getMessage(), t);
                Toast.makeText(LoginActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
        RetrofitClient.retrofit = null;
    }

    private void saveUserInfo(int userId, String username, String password, String token) {
        DatabaseHandler db = new DatabaseHandler(this);
        db.saveLoginInfoSQLite(userId, username, password, token);
        // Log thông tin user sau khi lưu vào SQLite
//        UserLoginInfo savedUserInfo = db.getLoginInfoSQLite();
//        if (savedUserInfo != null) {
//            Log.d("SQLiteUserInfo", "UserID: " + savedUserInfo.userId);
//            Log.d("SQLiteUserInfo", "Username: " + savedUserInfo.username);
//            Log.d("SQLiteUserInfo", "Password: " + savedUserInfo.password);
//            Log.d("SQLiteUserInfo", "Token: " + savedUserInfo.token);
//        } else {
//            Log.d("SQLiteUserInfo", "No user info saved!");
//        }
    }
    private void setRememberMe(boolean value) {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("rememberMe", value);
        editor.apply();
    }
}
