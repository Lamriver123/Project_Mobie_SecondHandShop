package com.example.marketplacesecondhand.fragment.payment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.RetrofitClient;
import com.example.marketplacesecondhand.databinding.BottomSheetLocationDetailBinding;
import com.example.marketplacesecondhand.dto.request.DeliveryAddressRequest;
import com.example.marketplacesecondhand.dto.request.UpdateDefaultAddressRequest;
import com.example.marketplacesecondhand.dto.request.UpdateDeliveryAddressRequest;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.DeliveryAddressResponse;
import com.example.marketplacesecondhand.viewModel.LocationViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottomSheetLocationDetailFragment extends BottomSheetDialogFragment {
    private static final String TAG = "BottomSheetLocationDetail";
    private BottomSheetLocationDetailBinding binding;
    private LocationViewModel locationViewModel;
    private DeliveryAddressResponse addressToEdit;
    private APIService apiService;

    public static BottomSheetLocationDetailFragment newInstance(DeliveryAddressResponse address) {
        BottomSheetLocationDetailFragment fragment = new BottomSheetLocationDetailFragment();
        if (address != null) {
            Bundle args = new Bundle();
            args.putLong("addressId", address.getAddressId());
            args.putString("nameBuyer", address.getNameBuyer());
            args.putString("phoneNumber", address.getPhoneNumber());
            args.putString("addressName", address.getAddressName());
            args.putInt("defaultAddress", address.getDefaultAddress());
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            addressToEdit = new DeliveryAddressResponse();
            addressToEdit.setAddressId(getArguments().getLong("addressId"));
            addressToEdit.setNameBuyer(getArguments().getString("nameBuyer"));
            addressToEdit.setPhoneNumber(getArguments().getString("phoneNumber"));
            addressToEdit.setAddressName(getArguments().getString("addressName"));
            addressToEdit.setDefaultAddress(getArguments().getInt("defaultAddress"));
        }
        locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetLocationDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Reset operation message when opening the bottom sheet
        locationViewModel.resetAddressOperationMessage();

        // Set title based on whether we're editing or adding
        binding.tvTitle.setText(addressToEdit != null ? "Sửa địa chỉ" : "Thêm địa chỉ mới");

        // If editing, populate the fields
        if (addressToEdit != null) {
            binding.etName.setText(addressToEdit.getNameBuyer());
            binding.etPhone.setText(addressToEdit.getPhoneNumber());
            binding.etAddress.setText(addressToEdit.getAddressName());
            binding.switchDefault.setChecked(addressToEdit.getDefaultAddress() != 0);
        }

        // Observe loading state
        locationViewModel.getIsUpdatingAddress().observe(getViewLifecycleOwner(), isLoading -> {
            // binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnSave.setEnabled(!isLoading);
        });

        // Observe operation result
        locationViewModel.getAddressOperationMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                if (!message.contains("thất bại") && !message.contains("Lỗi")) {
                    dismiss();
                }
            }
        });

        binding.btnSave.setOnClickListener(v -> saveAddress());
    }

    private void saveAddress() {
        String name = binding.etName.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String address = binding.etAddress.getText().toString().trim();
        boolean isDefault = binding.switchDefault.isChecked();

        // Validate input
        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (addressToEdit != null) {
            // Update existing address
            UpdateDeliveryAddressRequest updateRequest = new UpdateDeliveryAddressRequest(
                    addressToEdit.getAddressId(),
                    name,
                    address,
                    phone,
                    isDefault ? 1 : 0
            );
            locationViewModel.updateDeliveryAddress(updateRequest);
        } else {
            // Add new address
            DeliveryAddressRequest request = new DeliveryAddressRequest();
            request.setNameBuyer(name);
            request.setPhoneNumber(phone);
            request.setAddressName(address);
            request.setDefaultFlag(isDefault ? 1 : 0);
            locationViewModel.addDeliveryAddress(request);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
