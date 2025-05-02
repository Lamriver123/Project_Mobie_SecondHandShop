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
import com.example.marketplacesecondhand.dto.response.ProductResponse;
import com.example.marketplacesecondhand.models.Order;

import java.util.ArrayList;
import java.util.Arrays;
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

        // Gọi hàm lấy dữ liệu
        orderList.clear();
        orderList.addAll(getFakeOrders(status)); // Quan trọng!

        // Gán adapter
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
    }


    private List<Order> getFakeOrders(String status) {
        List<Order> orders = new ArrayList<>();

        List<ProductResponse> productList1 = Arrays.asList(
                new ProductResponse(1, "Áo thun Unisex", "100000", "100000"),
                new ProductResponse(2, "Quần jeans nam", "100000", "100000"),
                new ProductResponse(3, "Giày thể thao", "100000", "100000")
        );

        List<ProductResponse> productList2 = Arrays.asList(
                new ProductResponse(4, "Túi xách nữ", "100000", "100000"),
                new ProductResponse(5, "Đồng hồ thời trang", "100000", "100000")
        );

        List<ProductResponse> productList3 = Arrays.asList(
                new ProductResponse(6, "Balo laptop", "100000", "100000")
        );

        switch (status) {
            case "CHO_XAC_NHAN":
                orders.add(new Order("MHD123456", "CHO_XAC_NHAN", "Shop ABC", productList1));
                orders.add(new Order("MHD123457", "CHO_XAC_NHAN", "Shop XYZ", productList2));
                break;
            case "DANG_GIAO":
                orders.add(new Order("MHD123458", "DANG_GIAO", "Shop ABC", productList3));
                break;
            case "DA_GIAO":
                orders.add(new Order("MHD123459", "DA_GIAO", "Shop DEF", productList1));
                break;
            case "DA_HUY":
                orders.add(new Order("MHD123460", "DA_HUY", "Shop GHI", productList2));
                break;
        }

        return orders;
    }
}
