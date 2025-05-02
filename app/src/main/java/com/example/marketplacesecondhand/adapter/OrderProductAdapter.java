package com.example.marketplacesecondhand.adapter;

import android.content.Context;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketplacesecondhand.databinding.ItemOrderBinding;
import com.example.marketplacesecondhand.dto.response.ProductResponse;
import com.example.marketplacesecondhand.models.Order;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrderProductAdapter extends RecyclerView.Adapter<OrderProductAdapter.OrderViewHolder> {

    private final Context context;
    private final List<Order> orderList;
    private final OrderActionListener listener;
    // Set để lưu trữ ID của các đơn hàng đã được mở rộng (expanded)
    private final Set<String> expandedOrderIds = new HashSet<>();

    public interface OrderActionListener {
        void onCancel(Order order);
        void onConfirmReceived(Order order);
        void onReview(Order order);
    }

    public OrderProductAdapter(Context context, List<Order> orderList, OrderActionListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderBinding binding = ItemOrderBinding.inflate(LayoutInflater.from(context), parent, false);
        return new OrderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.binding.txtShopName.setText(order.getShopName());
        holder.binding.txtOrderStatus.setText(convertStatus(order.getStatus()));
        holder.binding.txtTotalPrice.setText("Tổng cộng: ₫" + calculateTotal(order.getProductList()));

        List<ProductResponse> fullProductList = order.getProductList();
        List<ProductResponse> productsToDisplay;
        // Kiểm tra xem đơn hàng này có trong danh sách đã mở rộng hay không
        boolean isExpanded = expandedOrderIds.contains(order.getOrderId());

        // --- Logic hiển thị sản phẩm và nút "Xem thêm/Ẩn bớt" ---
        if (fullProductList == null || fullProductList.isEmpty()) {
            // Không có sản phẩm nào
            productsToDisplay = new ArrayList<>();
            holder.binding.recyclerViewProductsOrder.setVisibility(View.GONE); // Ẩn RecyclerView
            holder.binding.btnViewMoreProducts.setVisibility(View.GONE); // Ẩn nút "Xem thêm"
        } else if (fullProductList.size() <= 1) {
            // Có 1 sản phẩm: hiển thị nó, ẩn nút "Xem thêm"
            productsToDisplay = fullProductList;
            holder.binding.recyclerViewProductsOrder.setVisibility(View.VISIBLE);
            holder.binding.btnViewMoreProducts.setVisibility(View.GONE);
        } else {
            // Có nhiều hơn 1 sản phẩm: hiển thị 1 hoặc tất cả tùy trạng thái mở rộng
            if (isExpanded) {
                productsToDisplay = fullProductList; // Show all products
                holder.binding.recyclerViewProductsOrder.setVisibility(View.VISIBLE);
                holder.binding.btnViewMoreProducts.setVisibility(View.VISIBLE);
                holder.binding.btnViewMoreProducts.setText("Ẩn bớt");
            } else {
                productsToDisplay = fullProductList.subList(0, 1);
                holder.binding.recyclerViewProductsOrder.setVisibility(View.VISIBLE);
                holder.binding.btnViewMoreProducts.setVisibility(View.VISIBLE);
                holder.binding.btnViewMoreProducts.setText("Xem thêm " + (fullProductList.size() - 1) + " sản phẩm"); // Text "Xem thêm X sản phẩm"
            }
        }
        // --- Kết thúc Logic hiển thị ---


        // Setup nested product RecyclerView
        ProductInOrderAdapter productAdapter = new ProductInOrderAdapter(context, productsToDisplay);
        // Chỉ tạo LayoutManager nếu chưa có (để tái sử dụng ViewHolder hiệu quả hơn)
        if (holder.binding.recyclerViewProductsOrder.getLayoutManager() == null) {
            holder.binding.recyclerViewProductsOrder.setLayoutManager(new LinearLayoutManager(context));

        }
        holder.binding.recyclerViewProductsOrder.setAdapter(productAdapter);


        // Set click listener cho nút "Xem thêm/Ẩn bớt"
        // Chỉ set listener nếu nút này có khả năng hiển thị (khi danh sách sản phẩm > 1)
        if (fullProductList != null && fullProductList.size() > 1) {
            holder.binding.btnViewMoreProducts.setOnClickListener(v -> {
                // Lấy layout cha cần animate
                ViewGroup layoutToAnimate = (ViewGroup) holder.binding.recyclerViewProductsOrder.getParent();

                // Bắt đầu transition animation trước khi thay đổi layout
                TransitionManager.beginDelayedTransition(layoutToAnimate);

                if (expandedOrderIds.contains(order.getOrderId())) {
                    expandedOrderIds.remove(order.getOrderId());
                } else {
                    expandedOrderIds.add(order.getOrderId());
                }
                notifyItemChanged(position);

            });
        } else {

            holder.binding.btnViewMoreProducts.setOnClickListener(null);
        }


        // Show/hide other buttons based on order status
        holder.binding.btnCancel.setVisibility(View.GONE);
        holder.binding.btnConfirmReceived.setVisibility(View.GONE);
        holder.binding.btnReview.setVisibility(View.GONE);

        switch (order.getStatus()) {
            case "CHO_XAC_NHAN":
                holder.binding.btnCancel.setVisibility(View.VISIBLE);
                break;
            case "DANG_GIAO":
                holder.binding.btnConfirmReceived.setVisibility(View.VISIBLE);
                break;
            case "DA_GIAO":
                holder.binding.btnReview.setVisibility(View.VISIBLE);
                break;
        }

        // Set listeners cho các nút action
        holder.binding.btnCancel.setOnClickListener(v -> listener.onCancel(order));
        holder.binding.btnConfirmReceived.setOnClickListener(v -> listener.onConfirmReceived(order));
        holder.binding.btnReview.setOnClickListener(v -> listener.onReview(order));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        ItemOrderBinding binding;

        public OrderViewHolder(@NonNull ItemOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


    private String convertStatus(String statusCode) {
        switch (statusCode) {
            case "CHO_XAC_NHAN": return "Chờ xác nhận";
            case "DANG_GIAO": return "Đang giao";
            case "DA_GIAO": return "Đã giao";
            case "DA_HUY": return "Đã hủy";
            default: return "Không xác định";
        }
    }

    private String calculateTotal(List<ProductResponse> products) {
        if (products == null) return "0";
        int total = 0;
        for (ProductResponse product : products) {
            try {
                total += Integer.parseInt(product.getCurrentPrice());
            } catch (NumberFormatException e) {
                // Xử lý lỗi nếu giá không phải số
                e.printStackTrace(); // Hoặc log lỗi
            }
        }
        return String.format("%,d", total).replace(',', '.');
    }
}