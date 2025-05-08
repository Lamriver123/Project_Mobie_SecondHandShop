package com.example.marketplacesecondhand.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.API.DatabaseHandler;
import com.example.marketplacesecondhand.RetrofitClient;
import com.example.marketplacesecondhand.adapter.order.OrderProductAdapter;
import com.example.marketplacesecondhand.databinding.FragmentOrderStatusBinding;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.OrderResponse;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderStatusFragment extends Fragment {
    private static final String ARG_STATUS = "status";
    private FragmentOrderStatusBinding binding;
    private OrderProductAdapter adapter;
    private List<OrderResponse> orderList = new ArrayList<>();
    private APIService apiService;

    public static OrderStatusFragment newInstance(String status) {
        OrderStatusFragment fragment = new OrderStatusFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
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

        String status = getArguments() != null ? getArguments().getString(ARG_STATUS) : "Trạng thái không xác định";

        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        
        // Gọi API lấy danh sách đơn hàng
        loadOrders(status);

        // Gán adapter
        adapter = new OrderProductAdapter(requireContext(), orderList, new OrderProductAdapter.OrderActionListener() {
            @Override
            public void onCancel(OrderResponse order) {
                // Xử lý khi bấm Hủy đơn
                cancelOrder(order.getOrderId());
            }

            @Override
            public void onConfirmReceived(OrderResponse order) {
                // Xử lý khi bấm Xác nhận đã nhận hàng
                confirmReceivedOrder(order.getOrderId());
            }

            @Override
            public void onReview(OrderResponse order) {
                // Xử lý khi bấm Đánh giá
                // TODO: Implement review functionality
            }
        });

        binding.recyclerViewOrderStatus.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewOrderStatus.setAdapter(adapter);
    }

    private void loadOrders(String status) {
        // Kiểm tra đăng nhập
        DatabaseHandler dbHandler = new DatabaseHandler(requireContext());
        if (dbHandler.getLoginInfoSQLite() == null) {
            Toast.makeText(requireContext(), "Vui lòng đăng nhập để xem đơn hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gọi API
        Call<ApiResponse<List<OrderResponse>>> call = apiService.getMyOrders(status);
        call.enqueue(new Callback<ApiResponse<List<OrderResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<OrderResponse>>> call, Response<ApiResponse<List<OrderResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orderList.clear();
                    orderList.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
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
                Log.e("API", "Failed to load orders: " + t.getMessage());
                Toast.makeText(requireContext(), "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelOrder(int orderId) {
//        Call<ApiResponse<Void>> call = apiService.cancelOrder(orderId);
//        call.enqueue(new Callback<ApiResponse<Void>>() {
//            @Override
//            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
//                if (response.isSuccessful()) {
//                    Toast.makeText(requireContext(), "Hủy đơn hàng thành công", Toast.LENGTH_SHORT).show();
//                    // Reload orders
//                    loadOrders(getArguments().getString(ARG_STATUS));
//                } else {
//                    try {
//                        if (response.errorBody() != null) {
//                            String errorJson = response.errorBody().string();
//                            ApiResponse<?> errorResponse = new Gson().fromJson(errorJson, ApiResponse.class);
//                            Toast.makeText(requireContext(), errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    } catch (Exception e) {
//                        Toast.makeText(requireContext(), "Lỗi hệ thống!", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
//                Toast.makeText(requireContext(), "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void confirmReceivedOrder(int orderId) {
//        Call<ApiResponse<Void>> call = apiService.confirmReceivedOrder(orderId);
//        call.enqueue(new Callback<ApiResponse<Void>>() {
//            @Override
//            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
//                if (response.isSuccessful()) {
//                    Toast.makeText(requireContext(), "Xác nhận nhận hàng thành công", Toast.LENGTH_SHORT).show();
//                    // Reload orders
//                    loadOrders(getArguments().getString(ARG_STATUS));
//                } else {
//                    try {
//                        if (response.errorBody() != null) {
//                            String errorJson = response.errorBody().string();
//                            ApiResponse<?> errorResponse = new Gson().fromJson(errorJson, ApiResponse.class);
//                            Toast.makeText(requireContext(), errorResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    } catch (Exception e) {
//                        Toast.makeText(requireContext(), "Lỗi hệ thống!", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
//                Toast.makeText(requireContext(), "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}
