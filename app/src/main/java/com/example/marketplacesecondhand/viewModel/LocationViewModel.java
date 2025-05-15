package com.example.marketplacesecondhand.viewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.service.RetrofitClient;
import com.example.marketplacesecondhand.dto.request.UpdateDefaultAddressRequest;
import com.example.marketplacesecondhand.dto.request.DeliveryAddressRequest;
import com.example.marketplacesecondhand.dto.request.UpdateDeliveryAddressRequest;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.DeliveryAddressResponse;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationViewModel extends ViewModel {
    private static final String TAG = "LocationViewModel";

    private final MutableLiveData<List<DeliveryAddressResponse>> addressesLiveData = new MutableLiveData<>();
    private final MutableLiveData<DeliveryAddressResponse> selectedAddressLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> addressOperationComplete = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isUpdatingAddress = new MutableLiveData<>(false);
    private final MutableLiveData<String> addressOperationMessage = new MutableLiveData<>();

    private APIService apiService;
    private boolean hasLoadedOnce = false; // Cờ để chỉ tải lần đầu hoặc khi có yêu cầu rõ ràng

    public LocationViewModel() {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
    }

    public LiveData<List<DeliveryAddressResponse>> getAddresses() {
        return addressesLiveData;
    }

    public LiveData<DeliveryAddressResponse> getSelectedAddress() {
        return selectedAddressLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public LiveData<Boolean> getAddressOperationComplete() {
        return addressOperationComplete;
    }

    public LiveData<Boolean> getIsUpdatingAddress() {
        return isUpdatingAddress;
    }

    public LiveData<String> getAddressOperationMessage() {
        return addressOperationMessage;
    }

    public void resetAddressOperationMessage() {
        addressOperationMessage.setValue(null);
    }

    public void loadAddresses() {
        isLoadingLiveData.setValue(true);
        errorLiveData.setValue(null);

        if (apiService == null) {
            errorLiveData.setValue("Lỗi: APIService chưa được khởi tạo.");
            isLoadingLiveData.setValue(false);
            Log.e(TAG, "APIService is null in loadAddresses.");
            return;
        }

        apiService.getUserAddresses().enqueue(new Callback<ApiResponse<List<DeliveryAddressResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<DeliveryAddressResponse>>> call, Response<ApiResponse<List<DeliveryAddressResponse>>> response) {
                isLoadingLiveData.setValue(false);
                hasLoadedOnce = true;
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<DeliveryAddressResponse> fetchedAddresses = response.body().getData();
                    addressesLiveData.setValue(fetchedAddresses);
                    Log.d(TAG, "Tải địa chỉ thành công, số lượng: " + fetchedAddresses.size());

                    DeliveryAddressResponse currentSelected = selectedAddressLiveData.getValue();

                    if (currentSelected != null) {
                        // Kiểm tra xem địa chỉ đang chọn có còn trong danh sách mới không
                        boolean stillExists = false;
                        for (DeliveryAddressResponse addr : fetchedAddresses) {
                            if (addr.getAddressId().equals(currentSelected.getAddressId())) {
                                stillExists = true;
                                if (!currentSelected.equals(addr)) {
                                    selectedAddressLiveData.setValue(addr);
                                    Log.d(TAG, "Địa chỉ đã chọn được cập nhật với thông tin mới từ server.");
                                }
                                break;
                            }
                        }
                        if (!stillExists) {
                            Log.d(TAG, "Địa chỉ đã chọn trước đó không còn tồn tại, thử tìm mặc định.");
                            findAndSetDefaultAddress(fetchedAddresses); // Nếu không còn, thử tìm mặc định
                        } else {
                            Log.d(TAG, "Giữ lại địa chỉ đã chọn trước đó: " + currentSelected.getAddressName());
                        }
                    } else {
                        // Nếu chưa có địa chỉ nào được chọn, tìm và đặt địa chỉ mặc định
                        Log.d(TAG, "Chưa có địa chỉ nào được chọn, đang tìm mặc định...");
                        findAndSetDefaultAddress(fetchedAddresses);
                    }
                } else {
                    String errorMsg = "Không thể tải danh sách địa chỉ (Code: " + response.code() + ")";
                    errorLiveData.setValue(errorMsg);
                    Log.e(TAG, "Lỗi tải địa chỉ: " + errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<DeliveryAddressResponse>>> call, Throwable t) {
                isLoadingLiveData.setValue(false);
                hasLoadedOnce = true; // Coi như đã thử tải
                String failError = "Lỗi kết nối: " + t.getMessage();
                errorLiveData.setValue(failError);
                Log.e(TAG, "Lỗi kết nối khi tải địa chỉ: " + t.getMessage(), t);
            }
        });
    }

    private void findAndSetDefaultAddress(List<DeliveryAddressResponse> addresses) {
        boolean defaultFound = false;
        if (addresses != null && !addresses.isEmpty()) {
            for (DeliveryAddressResponse address : addresses) {
                if (address.getDefaultAddress() != 0) { // Giả sử 0 là không mặc định
                    selectedAddressLiveData.setValue(address);
                    defaultFound = true;
                    Log.d(TAG, "Địa chỉ mặc định được tìm thấy và đặt: " + address.getAddressName());
                    break;
                }
            }
            if (!defaultFound) {
                selectedAddressLiveData.setValue(null);
                Log.d(TAG, "Không tìm thấy địa chỉ mặc định trong danh sách.");
            }
        } else {
            selectedAddressLiveData.setValue(null); // Không có địa chỉ nào
            Log.d(TAG, "Danh sách địa chỉ trống, không thể tìm mặc định.");
        }
    }


    public void setSelectedAddress(DeliveryAddressResponse address) {
        Log.d(TAG, "setSelectedAddress được gọi với: " + (address != null ? address.getAddressName() : "null"));
        selectedAddressLiveData.setValue(address);
    }

    public void triggerAddressOperationComplete() {
        addressOperationComplete.setValue(true);
    }

    public void onAddressOperationCompleteHandled() {
        if (Boolean.TRUE.equals(addressOperationComplete.getValue())) {
            addressOperationComplete.setValue(false);
        }
    }

    public boolean hasLoadedAddressesOnce() {
        return hasLoadedOnce;
    }

    public void updateDefaultAddress(long addressId) {
        isLoadingLiveData.setValue(true);
        errorLiveData.setValue(null);
        UpdateDefaultAddressRequest request = new UpdateDefaultAddressRequest(addressId);
        apiService.updateDefaultAddress(request).enqueue(new Callback<ApiResponse<DeliveryAddressResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<DeliveryAddressResponse>> call, Response<ApiResponse<DeliveryAddressResponse>> response) {
                isLoadingLiveData.setValue(false);
                if (response.isSuccessful()) {
                    // Reload addresses to get updated default status
                    loadAddresses();
                } else {
                    String errorMsg = "Không thể cập nhật địa chỉ mặc định (Code: " + response.code() + ")";
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            ApiResponse<?> apiError = new Gson().fromJson(errorJson, ApiResponse.class);
                            if (apiError != null && apiError.getMessage() != null) {
                                errorMsg = apiError.getMessage();
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi parse error body: " + e.getMessage());
                    }
                    errorLiveData.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<DeliveryAddressResponse>> call, Throwable t) {
                isLoadingLiveData.setValue(false);
                String failError = "Lỗi kết nối: " + t.getMessage();
                errorLiveData.setValue(failError);
                Log.e(TAG, "Lỗi kết nối khi cập nhật địa chỉ mặc định: " + t.getMessage(), t);
            }
        });
    }

    public void addDeliveryAddress(DeliveryAddressRequest request) {
        isUpdatingAddress.setValue(true);
        addressOperationMessage.setValue(null);

        apiService.createDeliveryAddress(request).enqueue(new Callback<ApiResponse<DeliveryAddressResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<DeliveryAddressResponse>> call, Response<ApiResponse<DeliveryAddressResponse>> response) {
                isUpdatingAddress.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    addressOperationMessage.setValue(response.body().getMessage());
                    loadAddresses(); // Reload the address list
                } else {
                    String errorMsg = "Thêm địa chỉ thất bại";
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            ApiResponse<?> apiError = new Gson().fromJson(errorJson, ApiResponse.class);
                            if (apiError != null && apiError.getMessage() != null) {
                                errorMsg = apiError.getMessage();
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi parse error body: " + e.getMessage());
                    }
                    addressOperationMessage.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<DeliveryAddressResponse>> call, Throwable t) {
                isUpdatingAddress.setValue(false);
                addressOperationMessage.setValue("Lỗi kết nối: " + t.getMessage());
                Log.e(TAG, "Lỗi kết nối khi thêm địa chỉ: " + t.getMessage(), t);
            }
        });
    }

    public void updateDeliveryAddress(UpdateDeliveryAddressRequest request) {
        isUpdatingAddress.setValue(true);
        addressOperationMessage.setValue(null);

        apiService.updateDeliveryAddress(request).enqueue(new Callback<ApiResponse<DeliveryAddressResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<DeliveryAddressResponse>> call, Response<ApiResponse<DeliveryAddressResponse>> response) {
                isUpdatingAddress.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    addressOperationMessage.setValue(response.body().getMessage());
                    loadAddresses(); // Reload the address list
                } else {
                    String errorMsg = "Cập nhật địa chỉ thất bại";
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            ApiResponse<?> apiError = new Gson().fromJson(errorJson, ApiResponse.class);
                            if (apiError != null && apiError.getMessage() != null) {
                                errorMsg = apiError.getMessage();
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi parse error body: " + e.getMessage());
                    }
                    addressOperationMessage.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<DeliveryAddressResponse>> call, Throwable t) {
                isUpdatingAddress.setValue(false);
                addressOperationMessage.setValue("Lỗi kết nối: " + t.getMessage());
                Log.e(TAG, "Lỗi kết nối khi cập nhật địa chỉ: " + t.getMessage(), t);
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "LocationViewModel onCleared");
    }
}
