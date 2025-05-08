package com.example.marketplacesecondhand.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.AutoCompleteTextView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.API.DatabaseHandler;
import com.example.marketplacesecondhand.LoginActivity;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.RetrofitClient;
import com.example.marketplacesecondhand.VerifyPasswordActivity;
import com.example.marketplacesecondhand.databinding.FragmentEditProfileBinding;
import com.example.marketplacesecondhand.dto.request.UserUpdateRequest;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.UserResponse;
import com.example.marketplacesecondhand.models.UserLoginInfo;
import com.example.marketplacesecondhand.service.CloudinaryService;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileFragment extends Fragment {
    private APIService apiService;
    private FragmentEditProfileBinding binding;
    private Date selectedDateOfBirth;
    private Uri selectedImageUri;
    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    selectedImageUri = result.getData().getData();
                    // Hiển thị ảnh đã chọn bằng
                    if (isAdded() && getContext() != null) {
                        Glide.with(requireContext())
                                .load(selectedImageUri)
                                .placeholder(R.drawable.user)
                                .error(R.drawable.user)
                                .into(binding.avatarImage);
                    }
                }
            });

    interface OnImageUploadListener {
        void onUploadSuccess(String imageUrl);
        void onUploadFailure(String errorMessage);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapping();
        loadInfo();
    }

    private void mapping() {
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ẩn header
                requireActivity().findViewById(R.id.header_fragment).setVisibility(View.GONE);
                // Hiện lại ViewPager
                requireActivity().findViewById(R.id.content_frame).setVisibility(View.VISIBLE);
                // Ẩn container fragment
                requireActivity().findViewById(R.id.fragment_container).setVisibility(View.GONE);
                // Xóa fragment hiện tại
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .remove(EditProfileFragment.this)
                        .commit();
            }
        });

        // Xử lý chọn ảnh
        binding.avatarImage.setOnClickListener(v -> openImageChooser());


        // Xử lý chọn giới tính
        binding.editGender.setOnClickListener(v -> showGenderBottomSheet());

        // Xử lý chọn ngày sinh
        binding.editDateOfBirth.setOnClickListener(v -> showDatePickerBottomSheet());

        binding.editTextTextPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), VerifyPasswordActivity.class);
                intent.putExtra("username", binding.editTextTextUsername.getText().toString());
                intent.putExtra("email", binding.editEmail.getText().toString());
                startActivity(intent);
            }
        });

        binding.btnSave.setOnClickListener(v -> prepareUpdateUserProfile());
    }

    private void loadInfo() {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<ApiResponse<UserResponse>> call = apiService.getMyInfo();

        call.enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse user = response.body().getData();

                    // Truyền dữ liệu vào view
                    binding.editFullName.setText(user.getFullName());
                    binding.editPhoneNumber.setText(user.getPhoneNumber());
                    binding.editGender.setText(user.getGender());

                    Date dateOfBirth = user.getDateOfBirth();
                    if (dateOfBirth != null) {
                        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        binding.editDateOfBirth.setText(displayFormat.format(dateOfBirth));
                        selectedDateOfBirth = dateOfBirth;
                    }

                    binding.editEmail.setText(user.getEmail());
                    binding.editTextTextUsername.setText(user.getUsername());
//                    binding.editTextTextPassword.setText(user.getEmail());

                    if (user.getAvt() != null && !user.getAvt().isEmpty()) {
                        Glide.with(requireContext())
                                .load(user.getAvt())
                                .placeholder(R.drawable.user) // ảnh mặc định nếu chưa có
                                .error(R.drawable.user)       // nếu load thất bại
                                .into(binding.avatarImage);
                    }

                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            ApiResponse<?> errorResponse = new Gson().fromJson(errorJson, ApiResponse.class);
                            Toast.makeText(getContext(), errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Lỗi không xác định!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Lỗi hệ thống!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                Log.e("API", "Failed to connect: " + t.getMessage());
            }
        });
    }

    private void prepareUpdateUserProfile() {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setFullName(binding.editFullName.getText().toString());
        request.setPhoneNumber(binding.editPhoneNumber.getText().toString());
        request.setGender(binding.editGender.getText().toString());

        if (selectedDateOfBirth != null) {
            SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            request.setDateOfBirth(serverFormat.format(selectedDateOfBirth));
        }

        if (selectedImageUri != null) {
            if (isAdded() && getContext() != null) {
                Toast.makeText(getContext(), "Đang tải ảnh lên...", Toast.LENGTH_SHORT).show();
            }
            uploadImageToCloudinary(selectedImageUri, new OnImageUploadListener() {
                @Override
                public void onUploadSuccess(String imageUrl) {
                    request.setAvt(imageUrl);
                    callUpdateUserApi(request);
                }

                @Override
                public void onUploadFailure(String errorMessage) {
                    if (isAdded() && getContext() != null) {
                        Toast.makeText(getContext(), "Lỗi tải ảnh: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else {
            callUpdateUserApi(request);
        }
    }

    private void callUpdateUserApi(UserUpdateRequest request) {
        DatabaseHandler dbHandler = new DatabaseHandler(getContext());
        UserLoginInfo userInfo = dbHandler.getLoginInfoSQLite();
        if (userInfo != null) {
            int userId = userInfo.getUserId();

            apiService.updateUser(userId, request).enqueue(new Callback<ApiResponse<UserResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<UserResponse>> call, Response<ApiResponse<UserResponse>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();

                        // Gửi kết quả cập nhật về ProfileFragment
                        Bundle result = new Bundle();
                        result.putBoolean("profile_updated", true);

                        getParentFragmentManager().setFragmentResult("profile_update_key", result);

                        // Ẩn EditProfileFragment và hiện lại content_frame + header
                        requireActivity().getSupportFragmentManager().popBackStack();
                        requireActivity().findViewById(R.id.fragment_container).setVisibility(View.GONE);
                        requireActivity().findViewById(R.id.content_frame).setVisibility(View.VISIBLE);
                        requireActivity().findViewById(R.id.header_fragment).setVisibility(View.GONE);
                    }
                    else {
                        try {
                            if (response.errorBody() != null) {
                                String errorJson = response.errorBody().string();
                                ApiResponse<?> errorResponse = new Gson().fromJson(errorJson, ApiResponse.class);
                                Toast.makeText(getContext(), errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Lỗi không xác định!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("LOGIN_BUG", "Exception: " + e);

                            Toast.makeText(getContext(), "Lỗi hệ thống!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<UserResponse>> call, Throwable t) {
                    Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void uploadImageToCloudinary(Uri imageUri, OnImageUploadListener listener) {
        // Lấy instance Cloudinary từ service
        Cloudinary cloudinaryInstance = CloudinaryService.getCloudinary();
        if (cloudinaryInstance == null) {
            listener.onUploadFailure("Cloudinary chưa được cấu hình.");
            Log.e("Cloudinary", "Cloudinary instance from service is null.");
            return;
        }

        new Thread(() -> {
            try {
                if (getContext() == null || !isAdded()) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> listener.onUploadFailure("Context không hợp lệ."));
                    } else {
                        // Không có activity, không thể chạy runOnUiThread
                        Log.e("CloudinaryUpload", "Activity is null, cannot post to UI thread for failure.");
                    }
                    return;
                }
                InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
                if (inputStream == null) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> listener.onUploadFailure("Không thể mở ảnh đã chọn."));
                    } else {
                        Log.e("CloudinaryUpload", "Activity is null, cannot post to UI thread for failure.");
                    }
                    return;
                }

                // Sử dụng cloudinaryInstance lấy từ service
                Map uploadResult = cloudinaryInstance.uploader().upload(inputStream, ObjectUtils.emptyMap());
                inputStream.close();

                String imageUrl = (String) uploadResult.get("secure_url");
                if (imageUrl == null) {
                    imageUrl = (String) uploadResult.get("url");
                }

                final String finalImageUrl = imageUrl;
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (finalImageUrl != null && !finalImageUrl.isEmpty()) {
                            listener.onUploadSuccess(finalImageUrl);
                        } else {
                            Log.e("CloudinaryUpload", "Upload result: " + new Gson().toJson(uploadResult));
                            listener.onUploadFailure("Không nhận được URL ảnh từ Cloudinary.");
                        }
                    });
                } else {
                    Log.e("CloudinaryUpload", "Activity is null after upload, cannot post to UI thread for success/failure.");
                }
            } catch (Exception e) {
                Log.e("CloudinaryUpload", "Lỗi tải ảnh lên Cloudinary", e);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> listener.onUploadFailure(e.getMessage() != null ? e.getMessage() : "Lỗi không xác định khi tải ảnh"));
                } else {
                    Log.e("CloudinaryUpload", "Activity is null, cannot post exception to UI thread.");
                }
            }
        }).start();
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT); // Hoặc Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        intent.setType("image/*");
        // Không dùng startActivityForResult nữa, mà dùng launcher
        pickImageLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    private void showGenderBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_gender, null, false);

        bottomSheetDialog.setContentView(sheetView);

        TextView optionMale = sheetView.findViewById(R.id.option_male);
        TextView optionFemale = sheetView.findViewById(R.id.option_female);
        TextView optionOther = sheetView.findViewById(R.id.option_other);
        TextView optionCancel = sheetView.findViewById(R.id.option_cancel);

        optionMale.setOnClickListener(v -> {
            binding.editGender.setText("Male");
            bottomSheetDialog.dismiss();
        });

        optionFemale.setOnClickListener(v -> {
            binding.editGender.setText("Female");
            bottomSheetDialog.dismiss();
        });

        optionOther.setOnClickListener(v -> {
            binding.editGender.setText("Other");
            bottomSheetDialog.dismiss();
        });

        optionCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }

    private void showDatePickerBottomSheet() {
        if (getContext() == null) return;

        View sheetView = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_date_picker, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(sheetView);

        NumberPicker npDay = sheetView.findViewById(R.id.npDay);
        NumberPicker npMonth = sheetView.findViewById(R.id.npMonth);
        NumberPicker npYear = sheetView.findViewById(R.id.npYear);
        Button btnDone = sheetView.findViewById(R.id.btnDone);

        String[] months = new String[]{"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};

        // Năm hiện tại
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);

        // Cấu hình NumberPicker
        npMonth.setMinValue(1);
        npMonth.setMaxValue(12);
        npMonth.setDisplayedValues(months);

        npYear.setMinValue(1900);
        npYear.setMaxValue(currentYear);

        // Khởi tạo mặc định tháng và năm hiện tại
        int initialMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1;
        int initialYear = currentYear;

        npMonth.setValue(initialMonth);
        npYear.setValue(initialYear);

        // Hàm cập nhật ngày tối đa dựa trên tháng và năm
        Runnable updateMaxDay = () -> {
            int month = npMonth.getValue();
            int year = npYear.getValue();
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.set(java.util.Calendar.YEAR, year);
            calendar.set(java.util.Calendar.MONTH, month - 1); // Calendar.MONTH từ 0-11
            int maxDay = calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
            int currentDay = npDay.getValue();
            npDay.setMaxValue(maxDay);
            if (currentDay > maxDay) {
                npDay.setValue(maxDay); // giữ cho ngày không vượt quá max
            }
        };

        // Lắng nghe khi tháng hoặc năm thay đổi
        npMonth.setOnValueChangedListener((picker, oldVal, newVal) -> updateMaxDay.run());
        npYear.setOnValueChangedListener((picker, oldVal, newVal) -> updateMaxDay.run());

        // Cấu hình ngày lần đầu
        npDay.setMinValue(1);
        updateMaxDay.run(); // để set đúng số ngày ngay từ đầu

        btnDone.setOnClickListener(view1 -> {
            int day = npDay.getValue();
            int month = npMonth.getValue();
            int year = npYear.getValue();

            // Tạo lại Date object từ picker
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month - 1, day); // Lưu ý: month - 1 vì Calendar.MONTH bắt đầu từ 0
            Date selectedDate = calendar.getTime();

            // Gán text cho người xem
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            binding.editDateOfBirth.setText(displayFormat.format(selectedDate));

            // Lưu tạm Date để gửi lại khi cập nhật
            selectedDateOfBirth = selectedDate;

            bottomSheetDialog.dismiss();
        });


        bottomSheetDialog.show();
    }
}

