package com.example.marketplacesecondhand.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.marketplacesecondhand.adapter.OrderProductAdapter;
import com.example.marketplacesecondhand.databinding.FragmentOrderStatusBinding;
import com.example.marketplacesecondhand.models.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderStatusFragment extends Fragment {
    private static final String ARG_STATUS = "status";
    private FragmentOrderStatusBinding binding;
    private OrderProductAdapter adapter;
    private List<Order> orderList = new ArrayList<>();

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

        adapter = new OrderProductAdapter(requireContext(), orderList, new OrderProductAdapter.OrderActionListener() {
            @Override
            public void onCancel(Order order) {
                // Xử lý khi bấm Hủy đơn
            }

            @Override
            public void onConfirmReceived(Order order) {
                // Xử lý khi bấm Xác nhận đã nhận hàng
            }

            @Override
            public void onReview(Order order) {
                // Xử lý khi bấm Đánh giá
            }
        });

        binding.recyclerViewOrderStatus.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewOrderStatus.setAdapter(adapter);

        loadFakeData(status);
    }

    private void loadFakeData(String status) {
        // Dữ liệu ảo
        for (int i = 1; i <= 5; i++) {
            Order order = new Order(
                    "Shop " + i,
                    "Sản phẩm " + i,
                    "Phân loại A" + i,
                    status,
                    "", // Image URL (chưa có)
                    i,
                    100000 * i,
                    100000 * i
            );
            orderList.add(order);
        }
        adapter.notifyDataSetChanged();
    }
}
