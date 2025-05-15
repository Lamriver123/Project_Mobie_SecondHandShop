package com.example.marketplacesecondhand.adapter.order;

import android.content.Context;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketplacesecondhand.databinding.ItemOrderBinding;
import com.example.marketplacesecondhand.dto.response.OrderDetailResponse;
import com.example.marketplacesecondhand.dto.response.OrderResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class OrderProductAdapter extends RecyclerView.Adapter<OrderProductAdapter.OrderViewHolder> {

    private final Context context;
    private final List<OrderResponse> orderList;
    private final OrderActionListener listener;
    private final Set<Integer> expandedOrderIds = new HashSet<>();


    public interface OrderActionListener {
        void onCancel(OrderResponse order);
        void onConfirmReceived(OrderResponse order);
        void onReview(OrderResponse order);
    }

    public OrderProductAdapter(Context context, List<OrderResponse> orderList, OrderActionListener listener) {
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
        OrderResponse order = orderList.get(position);
        holder.binding.txtShopName.setText(order.getOwnerName());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = sdf.format(order.getCreatedAt());
        holder.binding.txtTime.setText(formattedDate);
        holder.binding.txtOrderStatus.setText(order.getStatus());
        holder.binding.txtTotalPrice.setText("Tổng cộng: ₫" + calculateTotal(order.getTotalAmount()));

        List<OrderDetailResponse> fullProductList = order.getOrderDetails();
        List<OrderDetailResponse> productsToDisplay;
        boolean isExpanded = expandedOrderIds.contains(order.getOrderId());

        if (fullProductList == null || fullProductList.isEmpty()) {
            productsToDisplay = new ArrayList<>();
            holder.binding.recyclerViewProductsOrder.setVisibility(View.GONE);
            holder.binding.btnViewMoreProducts.setVisibility(View.GONE);
        }
        else if (fullProductList.size() <= 1) {
            productsToDisplay = fullProductList;
            holder.binding.recyclerViewProductsOrder.setVisibility(View.VISIBLE);
            holder.binding.btnViewMoreProducts.setVisibility(View.GONE);
        }
        else {
            if (isExpanded) {
                productsToDisplay = fullProductList;
                holder.binding.recyclerViewProductsOrder.setVisibility(View.VISIBLE);
                holder.binding.btnViewMoreProducts.setVisibility(View.VISIBLE);
                holder.binding.btnViewMoreProducts.setText("Ẩn bớt");
            } else {
                productsToDisplay = fullProductList.subList(0, 1);
                holder.binding.recyclerViewProductsOrder.setVisibility(View.VISIBLE);
                holder.binding.btnViewMoreProducts.setVisibility(View.VISIBLE);
                holder.binding.btnViewMoreProducts.setText("Xem thêm " + (fullProductList.size() - 1) + " sản phẩm");
            }
        }

        ProductInOrderAdapter productAdapter = new ProductInOrderAdapter(context, productsToDisplay,order.getStatus(), order.getOrderId());
        if (holder.binding.recyclerViewProductsOrder.getLayoutManager() == null) {
            holder.binding.recyclerViewProductsOrder.setLayoutManager(new LinearLayoutManager(context));
        }
        holder.binding.recyclerViewProductsOrder.setAdapter(productAdapter);

        if (fullProductList != null && fullProductList.size() > 1) {
            holder.binding.btnViewMoreProducts.setOnClickListener(v -> {
                ViewGroup layoutToAnimate = (ViewGroup) holder.binding.recyclerViewProductsOrder.getParent();
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

        switch (order.getStatus()) {
            case "Chờ xác nhận":
                holder.binding.btnCancel.setVisibility(View.VISIBLE);
                break;
            case "Đang giao":
                holder.binding.btnConfirmReceived.setVisibility(View.VISIBLE);
                break;
//            case "Đã giao":
//                holder.binding.btnReview.setVisibility(View.VISIBLE);
//                break;
//            case "Đã đánh giá":
//                holder.binding.btnReview.setVisibility(View.GONE);
//                break;
        }

        // Set listeners cho các nút action
        holder.binding.btnCancel.setOnClickListener(v -> listener.onCancel(order));
        holder.binding.btnConfirmReceived.setOnClickListener(v -> listener.onConfirmReceived(order));
//        holder.binding.btnReview.setOnClickListener(v -> listener.onReview(order));
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

    private String calculateTotal(String total) {
        double doubleValue = Double.parseDouble(total);
        long intTotal = (long) doubleValue;
        return String.format("%,d", intTotal).replace(',', '.');
    }
}