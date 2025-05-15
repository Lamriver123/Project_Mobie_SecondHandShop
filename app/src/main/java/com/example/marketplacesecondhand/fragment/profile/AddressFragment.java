package com.example.marketplacesecondhand.fragment.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.adapter.deliveryAddress.DeliveryAddressAdapter;
import com.example.marketplacesecondhand.databinding.FragmentAddressBinding;
import com.example.marketplacesecondhand.dto.response.DeliveryAddressResponse;
import com.example.marketplacesecondhand.fragment.payment.BottomSheetLocationDetailFragment;
import com.example.marketplacesecondhand.viewModel.LocationViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class AddressFragment extends Fragment implements DeliveryAddressAdapter.OnAddressClickListener {
    private static final String TAG = "AddressFragment";
    private FragmentAddressBinding binding;
    private DeliveryAddressAdapter addressAdapter;
    private LocationViewModel locationViewModel;
    private List<DeliveryAddressResponse> addressList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() != null) {
            locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);
        }
        else {
            Log.e(TAG, "getActivity() is null, không thể khởi tạo LocationViewModel.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddressBinding.inflate(inflater, container, false);

        // Hide bottom navigation
        if (getActivity() != null) {
            getActivity().findViewById(R.id.bottom_nav_view).setVisibility(View.GONE);
        }

        // Setup header
        setupHeader();

//        addressAdapter = new DeliveryAddressAdapter(requireContext(), addressList, this);
//        binding.recyclerViewAddress.setLayoutManager(new LinearLayoutManager(getContext()));
//        binding.recyclerViewAddress.setAdapter(addressAdapter);
//
//        binding.icBack.setOnClickListener(v -> requireActivity().onBackPressed());
//
//        binding.btnAddAddress.setOnClickListener(v -> {
//            Toast.makeText(getContext(), "Thêm địa chỉ mới", Toast.LENGTH_SHORT).show();
//            // TODO: Mở dialog hoặc fragment thêm địa chỉ mới
//        });
//
//        observeAddressList();
//        locationViewModel.loadAddresses();

        return binding.getRoot();
    }
    private void setupHeader() {
        binding.icBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().findViewById(R.id.bottom_nav_view).setVisibility(View.VISIBLE);
                getActivity().onBackPressed();
            }
        });
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupObservers();

        if (locationViewModel != null) {
            // Lấy địa chỉ hiện tại đang được chọn từ ViewModel
            DeliveryAddressResponse currentSelected = locationViewModel.getSelectedAddress().getValue();
            if (currentSelected != null && addressAdapter != null) {
                addressAdapter.setSelectedAddress(currentSelected);
            }
            // Load danh sách địa chỉ sau khi đã set địa chỉ được chọn
            locationViewModel.loadAddresses();
        }

        binding.btnAddAddress.setOnClickListener(v -> {
            if (!isAdded()) return;
            BottomSheetLocationDetailFragment bottomSheetDetail = BottomSheetLocationDetailFragment.newInstance(null);
            bottomSheetDetail.show(getParentFragmentManager(), bottomSheetDetail.getTag());
        });
    }

    private void setupRecyclerView() {
        if (getContext() != null) {
            binding.recyclerViewAddress.setLayoutManager(new LinearLayoutManager(getContext()));
            addressAdapter = new DeliveryAddressAdapter(getContext(), addressList, this);
            binding.recyclerViewAddress.setAdapter(addressAdapter);
        }
        else {
            Log.e(TAG, "RecyclerView (recyclerViewAddresses) hoặc Context là null.");
        }
    }

    private void setupObservers() {
        if (locationViewModel == null) {
            Log.e(TAG, "LocationViewModel is null in setupObservers.");
            if (binding != null) {
                binding.tvNoAddresses.setText("Lỗi tải ViewModel.");
                binding.tvNoAddresses.setVisibility(View.VISIBLE);
            }
            return;
        }

        // Observe danh sách địa chỉ
        locationViewModel.getAddresses().observe(getViewLifecycleOwner(), addresses -> {
            if (!isAdded() || binding == null)
                return;

            if (addresses != null) {
                Log.d(TAG, "Observer: Nhận được danh sách địa chỉ, số lượng: " + addresses.size());
                if (addressAdapter != null) {
                    // Lưu lại địa chỉ đang được chọn trước khi cập nhật danh sách
                    DeliveryAddressResponse currentSelected = addressAdapter.getSelectedAddress();
                    addressAdapter.setAddressList(addresses);
                    // Khôi phục lại địa chỉ đã chọn sau khi cập nhật danh sách
                    if (currentSelected != null) {
                        addressAdapter.setSelectedAddress(currentSelected);
                    }
                }
                updateUIVisibility(addresses.isEmpty());
            } else {
                // Trường hợp list là null (ví dụ: sau khi ViewModel onCleared)
                Log.d(TAG, "Observer: Danh sách địa chỉ là null.");
                if (addressAdapter != null) {
                    addressAdapter.setAddressList(new ArrayList<>()); // Đặt danh sách rỗng
                }
                updateUIVisibility(true); // Hiển thị thông báo không có địa chỉ
            }
        });

        // Observe lỗi
        locationViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (!isAdded() || binding == null) return;

            if (error != null && !error.isEmpty()) {
                Log.e(TAG, "Observer: Lỗi từ ViewModel: " + error);
                if (getContext() != null) Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                binding.progressBarAddresses.setVisibility(View.GONE);
                if (binding.recyclerViewAddress != null) binding.recyclerViewAddress.setVisibility(View.GONE);
                if (binding.recyclerViewAddress != null) binding.recyclerViewAddress.setVisibility(View.GONE);
                if (binding.tvNoAddresses != null) {
                    binding.tvNoAddresses.setText(error);
                    binding.tvNoAddresses.setVisibility(View.VISIBLE);
                }
            }
        });

        // Observe trạng thái loading
        locationViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && binding != null && isAdded()) {
                binding.progressBarAddresses.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                if (isLoading) {
                    // Khi đang tải, ẩn các view khác
                    binding.recyclerViewAddress.setVisibility(View.GONE);
                    binding.tvNoAddresses.setVisibility(View.GONE);
                    binding.recyclerViewAddress.setVisibility(View.GONE);
                }

            }
        });

        // Observe địa chỉ được chọn/mặc định để cập nhật RadioButton trong adapter
        locationViewModel.getSelectedAddress().observe(getViewLifecycleOwner(), selectedAddress -> {
//            if (addressAdapter != null && selectedAddress != null && isAdded()) {
//                int position = -1;
//                List<DeliveryAddressResponse> currentList = addressAdapter.getAddressList(); // Cần thêm getter này vào Adapter
//                if (currentList != null) {
//                    for (int i = 0; i < currentList.size(); i++) {
//                        if (currentList.get(i).getAddressId().equals(selectedAddress.getAddressId())) {
//                            position = i;
//                            break;
//                        }
//                    }
//                }
//                if (position != -1) {
//                    addressAdapter.setSelectedPosition(position);
//                }
//            }
        });
    }

    private void updateUIVisibility(boolean isListEmpty) {
        if (binding == null || !isAdded()) return;

        binding.recyclerViewAddress.setVisibility(isListEmpty ? View.GONE : View.VISIBLE);
        binding.recyclerViewAddress.setVisibility(isListEmpty ? View.GONE : View.VISIBLE);
        if (isListEmpty) {
            binding.tvNoAddresses.setText("Bạn chưa có địa chỉ nào. Hãy thêm địa chỉ mới.");
            binding.tvNoAddresses.setVisibility(View.VISIBLE);
        } else {
            binding.tvNoAddresses.setVisibility(View.GONE);
        }
    }


    @Override
    public void onAddressClick(DeliveryAddressResponse address, int position) {
        if (!isAdded() || address == null || locationViewModel == null) return;
        Log.d(TAG, "Địa chỉ được chọn: " + address.getAddressName() + " tại vị trí " + position);

        if (address.getDefaultAddress() == 0) {
            // Show confirmation dialog for setting default address
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Đặt địa chỉ mặc định")
                    .setMessage("Bạn có muốn đặt địa chỉ này làm mặc định không?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        // Update default address
                        locationViewModel.updateDefaultAddress(address.getAddressId());
                        locationViewModel.setSelectedAddress(address);
                        if (addressAdapter != null) {
                            addressAdapter.setSelectedAddress(address);
                        }
                 //       dismiss();
                    })
                    .setNegativeButton("Không", (dialog, which) -> {
                        // Just set as selected address without making it default
                        locationViewModel.setSelectedAddress(address);
                        if (addressAdapter != null) {
                            addressAdapter.setSelectedAddress(address);
                        }
                  //      dismiss();
                    })
                    //      .setNeutralButton("Hủy", null)
                    .show();

        } else {
            // Nếu địa chỉ đã là mặc định, chỉ cần set làm địa chỉ được chọn
            locationViewModel.setSelectedAddress(address);
            if (addressAdapter != null) {
                addressAdapter.setSelectedAddress(address);
            }
         //   dismiss();
        }
    }

    @Override
    public void onEditAddressClick(DeliveryAddressResponse address, int position) {
        if (!isAdded() || address == null) return;
        Log.d(TAG, "Chỉnh sửa địa chỉ: " + address.getAddressName());
        BottomSheetLocationDetailFragment editSheet = BottomSheetLocationDetailFragment.newInstance(address);
        editSheet.show(getParentFragmentManager(), editSheet.getTag());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Show bottom navigation when leaving the fragment
        if (getActivity() != null) {
            getActivity().findViewById(R.id.bottom_nav_view).setVisibility(View.VISIBLE);
        }
        binding = null;
    }
} 