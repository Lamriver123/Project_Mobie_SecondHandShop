package com.example.marketplacesecondhand.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.API.DatabaseHandler;
import com.example.marketplacesecondhand.LoginActivity;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.RetrofitClient;
import com.example.marketplacesecondhand.adapter.order.OrderProductAdapter;
import com.example.marketplacesecondhand.databinding.DialogCancelReasonBinding;
import com.example.marketplacesecondhand.databinding.FragmentOrderStatusBinding;
import com.example.marketplacesecondhand.dto.request.CancelOrderRequest;
import com.example.marketplacesecondhand.dto.request.UpdateOrderStatusRequest;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.CancelledOrderResponse;
import com.example.marketplacesecondhand.dto.response.OrderResponse;
import com.example.marketplacesecondhand.models.UserLoginInfo;
import com.example.marketplacesecondhand.viewModel.OrderViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderStatusFragment extends Fragment {
    private static final String TAG = "OrderStatusFragment";
    private static final String ARG_STATUS = "status";
    private FragmentOrderStatusBinding binding;
    private OrderProductAdapter adapter;
    private List<OrderResponse> orderList = new ArrayList<>();
    private APIService apiService;
    private OrderViewModel orderViewModel;
    private String currentStatusTab;

    public static OrderStatusFragment newInstance(String status) {
        OrderStatusFragment fragment = new OrderStatusFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo ViewModel, scope với Fragment cha
        if (getParentFragment() != null) {
            orderViewModel = new ViewModelProvider(requireParentFragment()).get(OrderViewModel.class);
        } else {
            orderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
            Log.e(TAG, "Không tìm thấy ParentFragment, ViewModel có thể không được chia sẻ đúng cách giữa các tab.");
        }

        // Lấy trạng thái của tab này từ arguments
        if (getArguments() != null) {
            currentStatusTab = getArguments().getString(ARG_STATUS, "Trạng thái không xác định");
        } else {
            currentStatusTab = "Trạng thái không xác định";
        }
        Log.d(TAG, "onCreate: currentStatusTab = " + currentStatusTab);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentOrderStatusBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       // String status = getArguments() != null ? getArguments().getString(ARG_STATUS) : "Trạng thái không xác định";

        apiService = RetrofitClient.getRetrofit().create(APIService.class);

        setupRecyclerView();

        // Gọi API lấy danh sách đơn hàng
      //  loadOrders(status);

        // Lắng nghe sự kiện làm mới từ ViewModel
        if (orderViewModel != null) {
            orderViewModel.getRefreshTrigger().observe(getViewLifecycleOwner(), triggered -> {
                if (triggered != null && triggered) {
                    Log.d(TAG, "RefreshTrigger observed for status: " + currentStatusTab + ". isResumed: " + isResumed());
                    // Chỉ tải lại nếu Fragment đang hiển thị (resumed) HOẶC nếu bạn muốn tất cả các tab đều làm mới
                    // Để đảm bảo dữ liệu nhất quán, chúng ta sẽ tải lại.
                    // Bạn có thể thêm điều kiện isResumed() nếu muốn tối ưu hơn.
                    if (isResumed()) { // Đảm bảo chỉ tải lại nếu tab đang hiển thị
                        Log.d(TAG, "ViewModel trigger: Đang tải lại đơn hàng cho tab " + currentStatusTab);
                        loadOrders(currentStatusTab);
                    }
                    // orderViewModel.onRefreshTriggered(); // Reset cờ nếu nó là sự kiện một lần
                }
            });
        }


    }

    private void setupRecyclerView() {
        // Gán adapter
        adapter = new OrderProductAdapter(requireContext(), orderList, new OrderProductAdapter.OrderActionListener() {
            @Override
            public void onCancel(OrderResponse order) {
                showCancelReasonDialog(order);
            }

            @Override
            public void onConfirmReceived(OrderResponse order) {
                // Xử lý khi bấm Xác nhận đã nhận hàng
                UpdateOrderStatusRequest updateOrderStatusRequest = new UpdateOrderStatusRequest("Đã giao");
                confirmReceivedOrder(order.getOrderId(), updateOrderStatusRequest);
            }

            @Override
            public void onReview(OrderResponse order) {
                // Xử lý khi bấm Đánh giá
                Toast.makeText(getContext(), "Chức năng đánh giá cho đơn hàng: " + order.getOrderId(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.recyclerViewOrderStatus.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewOrderStatus.setAdapter(adapter);
    }

    private void showCancelReasonDialog(OrderResponse order) {
        if (getContext() == null || !isAdded()) {
            Log.w(TAG, "showCancelReasonDialog: Fragment not attached or context is null.");
            return;
        }

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext()); // Sử dụng style nếu có
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        DialogCancelReasonBinding dialogBinding = DialogCancelReasonBinding.inflate(inflater, null, false);
        builder.setView(dialogBinding.getRoot());

        final AlertDialog dialog = builder.create();

        // Yêu cầu focus và hiển thị bàn phím cho EditText
        dialogBinding.etCancelReason.requestFocus();
        dialog.setOnShowListener(dialogInterface -> {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(dialogBinding.etCancelReason, InputMethodManager.SHOW_IMPLICIT);
            }
        });


        dialogBinding.btnDialogConfirm.setOnClickListener(v -> {
            String reason = "";
            if (dialogBinding.etCancelReason.getText() != null) {
                reason = dialogBinding.etCancelReason.getText().toString().trim();
            }

            // Bạn có thể bắt buộc nhập lý do ở đây nếu muốn
             if (TextUtils.isEmpty(reason)) {
                 dialogBinding.tilCancelReason.setError("Vui lòng nhập lý do hủy");
                 return;
             }
             dialogBinding.tilCancelReason.setError(null); // Xóa lỗi nếu có

             CancelOrderRequest request = new CancelOrderRequest(order.getOrderId(), reason);
             cancelOrderAPI(request);
             dialog.dismiss();



        });

        dialogBinding.btnDialogDismiss.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void loadOrders(String status) {
        if (!isAdded() || getContext() == null || apiService == null) {
            Log.w(TAG, "loadOrders: Cannot load. Fragment not ready or APIService is null. Status: " + status);
            if (binding != null && binding.progressBar != null) { // Kiểm tra binding trước khi truy cập
                binding.progressBar.setVisibility(View.GONE);
            }
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.recyclerViewOrderStatus.setVisibility(View.GONE);
        binding.tvNoOrders.setVisibility(View.GONE);

        Call<ApiResponse<List<OrderResponse>>> call = apiService.getMyOrders(status);
        call.enqueue(new Callback<ApiResponse<List<OrderResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<OrderResponse>>> call, Response<ApiResponse<List<OrderResponse>>> response) {
                if (!isAdded() || getContext() == null) return;
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    orderList.clear();
                    orderList.addAll(response.body().getData());
                    if (adapter != null) { // Kiểm tra adapter trước khi gọi notify
                        adapter.notifyDataSetChanged();
                    }


                    if (orderList.isEmpty()) {
                        binding.tvNoOrders.setText("Không có đơn hàng nào");
                        binding.tvNoOrders.setVisibility(View.VISIBLE);
                        binding.recyclerViewOrderStatus.setVisibility(View.GONE);
                    } else {
                        binding.tvNoOrders.setVisibility(View.GONE);
                        binding.recyclerViewOrderStatus.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            ApiResponse<?> errorResponse = new Gson().fromJson(errorJson, ApiResponse.class);
                            Toast.makeText(requireContext(), errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Lỗi không xác định!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(requireContext(), "Lỗi hệ thống!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<OrderResponse>>> call, Throwable t) {
                if (!isAdded() || getContext() == null || binding == null) return;
                binding.progressBar.setVisibility(View.GONE);
                binding.tvNoOrders.setText("Lỗi kết nối. Vui lòng kiểm tra mạng.");
                binding.tvNoOrders.setVisibility(View.VISIBLE);
                binding.recyclerViewOrderStatus.setVisibility(View.GONE);
                Log.e("API", "Failed to load orders: " + t.getMessage());
                Toast.makeText(requireContext(), "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelOrderAPI(CancelOrderRequest request) {
        if (apiService == null || getContext() == null || !isAdded()) return;
     //   Toast.makeText(getContext(), "Đang xử lý hủy đơn...", Toast.LENGTH_SHORT).show();

        Call<ApiResponse<CancelledOrderResponse>> call = apiService.cancelOrder(request);
        call.enqueue(new Callback<ApiResponse<CancelledOrderResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CancelledOrderResponse>> call, Response<ApiResponse<CancelledOrderResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(requireContext(), "Hủy đơn hàng thành công", Toast.LENGTH_SHORT).show();
                    // Reload orders
                 //   loadOrders(getArguments().getString(ARG_STATUS));
                    if (orderViewModel != null) {
                        orderViewModel.triggerOrderRefresh();
                    }
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            ApiResponse<?> errorResponse = new Gson().fromJson(errorJson, ApiResponse.class);
                            Toast.makeText(requireContext(), errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(requireContext(), "Lỗi hệ thống!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CancelledOrderResponse>> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmReceivedOrder(int orderId, UpdateOrderStatusRequest request) {
        if (apiService == null || getContext() == null || !isAdded()) return;
     //   Toast.makeText(getContext(), "Đang xác nhận đã nhận hàng...", Toast.LENGTH_SHORT).show();

        Call<ApiResponse<Void>> call = apiService.updateOrderStatus(orderId, request);
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(requireContext(), "Xác nhận nhận hàng thành công", Toast.LENGTH_SHORT).show();
                    // Reload orders
              //      loadOrders(getArguments().getString(ARG_STATUS));
                    if (orderViewModel != null) {
                        orderViewModel.triggerOrderRefresh();
                    }
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            ApiResponse<?> errorResponse = new Gson().fromJson(errorJson, ApiResponse.class);
                            Toast.makeText(requireContext(), errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(requireContext(), "Lỗi hệ thống!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Đang tải lại đơn hàng cho tab " + currentStatusTab);
        // Tải lại dữ liệu mỗi khi tab này được hiển thị (trở thành active)
        loadOrders(currentStatusTab);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
