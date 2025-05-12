package com.example.marketplacesecondhand.adapter.payment;

import android.content.Context;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketplacesecondhand.dto.response.OrderDetailResponse;
import com.example.marketplacesecondhand.dto.response.OrderResponse;
import com.example.marketplacesecondhand.databinding.ItemOrderPaymentBinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrderPaymentAdapter extends RecyclerView.Adapter<OrderPaymentAdapter.ViewHolder> {
    private List<OrderResponse> orderList;
    private Context context;
    private final Set<Integer> expandedOrderIds = new HashSet<>();

    public OrderPaymentAdapter(Context context, List<OrderResponse> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderPaymentBinding binding = ItemOrderPaymentBinding.inflate(
            LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderResponse order = orderList.get(position);

        List<OrderDetailResponse> fullProductList = order.getOrderDetails();
        List<OrderDetailResponse> productsToDisplay;
        boolean isExpanded = expandedOrderIds.contains(order.getOrderId());

        holder.binding.txtShopName.setText(order.getOwnerName());
        holder.binding.txtOrderStatus.setText(order.getStatus());
        holder.binding.txtTotalPrice.setText("Tổng cộng: " + order.getTotalAmount());



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

        // Setup product recycler view
        ProductPaymentAdapter productAdapter = new ProductPaymentAdapter(context, productsToDisplay);
        holder.binding.recyclerViewProductsOrder.setLayoutManager(new LinearLayoutManager(context));
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
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemOrderPaymentBinding binding;

        public ViewHolder(@NonNull ItemOrderPaymentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
} 