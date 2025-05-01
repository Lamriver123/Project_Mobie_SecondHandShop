package com.example.marketplacesecondhand.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketplacesecondhand.databinding.ItemProductOrderBinding;
import com.example.marketplacesecondhand.models.Order;

import java.util.List;

public class OrderProductAdapter extends RecyclerView.Adapter<OrderProductAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;

    public interface OrderActionListener {
        void onCancel(Order order);
        void onConfirmReceived(Order order);
        void onReview(Order order);
    }

    private OrderActionListener listener;

    public OrderProductAdapter(Context context, List<Order> orderList, OrderActionListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductOrderBinding binding = ItemProductOrderBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new OrderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {

        ItemProductOrderBinding binding;

        public OrderViewHolder(ItemProductOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Order order) {
            binding.txtShopName.setText(order.getShopName());
            binding.txtOrderStatus.setText(order.getStatus());
            binding.txtProductName.setText(order.getProductName());
            binding.txtProductVariation.setText(order.getVariation());
            binding.txtProductQuantity.setText("x" + order.getQuantity());
            binding.txtProductPrice.setText("₫" + order.getPrice());
            binding.txtTotalPrice.setText("Tổng số tiền: ₫" + order.getTotal());

            // Ẩn hết nút
            binding.btnCancel.setVisibility(View.GONE);
            binding.btnConfirmReceived.setVisibility(View.GONE);
            binding.btnReview.setVisibility(View.GONE);

            switch (order.getStatus()) {
                case "Chờ xác nhận":
                    binding.btnCancel.setVisibility(View.VISIBLE);
                    binding.btnCancel.setOnClickListener(v -> listener.onCancel(order));
                    break;
                case "Đang giao":
                    binding.btnConfirmReceived.setVisibility(View.VISIBLE);
                    binding.btnConfirmReceived.setOnClickListener(v -> listener.onConfirmReceived(order));
                    break;
                case "Đã giao":
                    binding.btnReview.setVisibility(View.VISIBLE);
                    binding.btnReview.setOnClickListener(v -> listener.onReview(order));
                    break;
            }

            // TODO: Load ảnh với Glide nếu có link ảnh
            // Glide.with(context).load(order.getImageUrl()).into(binding.imgProduct);
        }
    }
}

