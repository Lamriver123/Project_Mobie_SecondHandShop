package com.example.marketplacesecondhand.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.RetrofitClient;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PriceFilterBottomSheetFragment extends BottomSheetDialogFragment {
    public static final String PREFS_NAME = "PriceFilterPrefs";
    public static final String KEY_MIN_PRICE = "min_price";
    public static final String KEY_MAX_PRICE = "max_price";

    private TextInputEditText edtMinPrice, edtMaxPrice;
    private RangeSlider priceRangeSlider;
    private Button btnApply;
    private TextView btnUnder2M, btn2To5M, btn5To10M, btn10To20M;
    private TextView btnClearFilter;
    private ImageButton btnClose;
    private OnPriceFilterListener listener;
    private APIService apiService;
    private boolean isUpdatingManually = false;
    private int selectedCategoryId = -1;
    private int maxPrice = 10000000;

    public interface OnPriceFilterListener {
        void onPriceFilterApplied(int minPrice, int maxPrice);
    }

    public void setOnPriceFilterListener(OnPriceFilterListener listener) {
        this.listener = listener;
    }

    public void setSelectedCategoryId(int categoryId) {
        this.selectedCategoryId = categoryId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_price_filter, container, false);
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        // Init views
        edtMinPrice = view.findViewById(R.id.edtMinPrice);
        edtMaxPrice = view.findViewById(R.id.edtMaxPrice);
        priceRangeSlider = view.findViewById(R.id.priceRangeSlider);
        btnApply = view.findViewById(R.id.btnApply);
        btnUnder2M = view.findViewById(R.id.btnUnder2M);
        btn2To5M = view.findViewById(R.id.btn2To5M);
        btn5To10M = view.findViewById(R.id.btn5To10M);
        btn10To20M = view.findViewById(R.id.btn10To20M);
        btnClearFilter = view.findViewById(R.id.btnClearFilter);
        btnClose = view.findViewById(R.id.btnClose);

        // Mai sửa API này !!
       // getMaxPriceWithCategoryId(selectedCategoryId);

        setupPriceRangeSlider();
        setupQuickSelectButtons();
        setupApplyButton();
        setupEditTextListeners();
        setupClearAndClose();
     //   loadPreviousValues();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //    init(view);
        getMaxPriceWithCategoryId(selectedCategoryId);
    }

    private void getMaxPriceWithCategoryId(int categoryId) {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<ApiResponse<Integer>> call = apiService.getMaxPrice(categoryId);

        call.enqueue(new Callback<ApiResponse<Integer>>() {
            @Override
            public void onResponse(Call<ApiResponse<Integer>> call, Response<ApiResponse<Integer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    maxPrice = response.body().getData();

                    // Cập nhật max cho slider
                    priceRangeSlider.setValueTo((float) maxPrice);
                    priceRangeSlider.setValues(0.0f, (float) maxPrice);

                    edtMaxPrice.setText(String.valueOf(maxPrice));

                    loadPreviousValues();

                } else {
                    Log.e("API", "Lỗi dữ liệu hoặc response body null");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Integer>> call, Throwable t) {
                Log.e("API", "Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void loadPreviousValues() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int savedMinPrice = prefs.getInt(KEY_MIN_PRICE, -1);
        int savedMaxPrice = prefs.getInt(KEY_MAX_PRICE, -1);

        if (savedMinPrice != -1 && savedMaxPrice != -1) {
            // Nếu có giá trị lưu thì load lại
            edtMinPrice.setText(String.valueOf(savedMinPrice));
            edtMaxPrice.setText(String.valueOf(savedMaxPrice));
            priceRangeSlider.setValues((float) savedMinPrice, (float) savedMaxPrice);
        } else {
            // Nếu chưa có thì dùng mặc định
            edtMinPrice.setText(String.valueOf(0));
            edtMaxPrice.setText(String.valueOf(maxPrice));
            priceRangeSlider.setValues(0f, (float) maxPrice);
        }
        updateApplyButtonState();
    }

    private void saveValues(int minPrice, int maxPrice) {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_MIN_PRICE, minPrice);
        editor.putInt(KEY_MAX_PRICE, maxPrice);
        editor.apply();
    }

    private void setupPriceRangeSlider() {
        priceRangeSlider.setValueFrom(0);
        priceRangeSlider.setValueTo(maxPrice);
        priceRangeSlider.setStepSize((int)(maxPrice * 0.01));

        priceRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            edtMinPrice.setText(String.valueOf(values.get(0).intValue()));
            edtMaxPrice.setText(String.valueOf(values.get(1).intValue()));
            updateApplyButtonState();
        });
    }

    private void setupQuickSelectButtons() {
        btnUnder2M.setOnClickListener(v -> setPriceRange(0, 2000000));
        btn2To5M.setOnClickListener(v -> setPriceRange(2000000, 5000000));
        btn5To10M.setOnClickListener(v -> setPriceRange(5000000, 10000000));
        btn10To20M.setOnClickListener(v -> setPriceRange(10000000, 20000000));
    }

    private void setPriceRange(int min, int max) {
        isUpdatingManually = true;
        edtMinPrice.setText(String.valueOf(min));
        edtMaxPrice.setText(String.valueOf(max));
        priceRangeSlider.setValues((float) min, (float) max);
        isUpdatingManually = false;
        updateApplyButtonState();
    }

    private void setupApplyButton() {
        btnApply.setOnClickListener(v -> {
            try {
                int minPrice = edtMinPrice.getText().toString().isEmpty() ? 0 :
                        Integer.parseInt(edtMinPrice.getText().toString());
                int maxPrice = edtMaxPrice.getText().toString().isEmpty() ? 50000000 :
                        Integer.parseInt(edtMaxPrice.getText().toString());

                if (minPrice <= maxPrice && listener != null) {
                    saveValues(minPrice, maxPrice);
                    listener.onPriceFilterApplied(minPrice, maxPrice);
                    dismiss();
                }
            } catch (NumberFormatException e) {
                // Có thể hiện thông báo lỗi ở đây nếu muốn
            }
        });
    }

    private void setupEditTextListeners() {
        TextWatcher priceTextWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isUpdatingManually) {
                    updateSliderFromText();
                    updateApplyButtonState();
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        };
        edtMinPrice.addTextChangedListener(priceTextWatcher);
        edtMaxPrice.addTextChangedListener(priceTextWatcher);
    }

    private void updateSliderFromText() {
        String minText = edtMinPrice.getText().toString();
        String maxText = edtMaxPrice.getText().toString();
        if (!minText.isEmpty() && !maxText.isEmpty()) {
            try {
                float min = Float.parseFloat(minText);
                float max = Float.parseFloat(maxText);
                if (min <= max) {
                    priceRangeSlider.setValues(min, max);
                }
            } catch (NumberFormatException ignored) {}
        }
    }

    private void updateApplyButtonState() {
        String minStr = edtMinPrice.getText().toString();
        String maxStr = edtMaxPrice.getText().toString();

        boolean isValid = false;
        try {
            if (!minStr.isEmpty() && !maxStr.isEmpty()) {
                int min = Integer.parseInt(minStr);
                int max = Integer.parseInt(maxStr);
                isValid = min <= max;
            }
        } catch (NumberFormatException ignored) {}

        btnApply.setEnabled(isValid);
    }

    private void setupClearAndClose() {
        btnClearFilter.setOnClickListener(v -> {
            edtMinPrice.setText("");
            edtMaxPrice.setText("");
            priceRangeSlider.setValues(0f, 50000000f);
            btnApply.setEnabled(false);
            saveValues(0, 50000000);
        });

        btnClose.setOnClickListener(v -> dismiss());
    }
}
