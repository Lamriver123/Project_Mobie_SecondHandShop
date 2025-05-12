package com.example.marketplacesecondhand.adapter.payment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.marketplacesecondhand.dto.response.OrderDetailResponse;
import com.example.marketplacesecondhand.databinding.ItemProductPaymentBinding;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductPaymentAdapter extends RecyclerView.Adapter<ProductPaymentAdapter.ViewHolder> {
    private List<OrderDetailResponse> orderDetailList;
    private Context context;

    public ProductPaymentAdapter(Context context, List<OrderDetailResponse> orderDetailList) {
        this.context = context;
        this.orderDetailList = orderDetailList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductPaymentBinding binding = ItemProductPaymentBinding.inflate(
            LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderDetailResponse orderDetail = orderDetailList.get(position);
        
        holder.binding.txtProductName.setText(orderDetail.getProductName());
        holder.binding.txtProductQuantity.setText("x" + orderDetail.getQuantity());
        holder.binding.txtProductPrice.setText(formatCurrency(orderDetail.getPrice()) + " VND");

        // Load image using Glide
        if (orderDetail.getCurrentImages() != null && !orderDetail.getCurrentImages().isEmpty()) {
            Glide.with(context)
                .load(orderDetail.getCurrentImages())
                .into(holder.binding.imgProduct);
        }
    }

    @Override
    public int getItemCount() {
        return orderDetailList.size();
    }

    private String formatCurrency(String number) {
        int num = Integer.parseInt(number);
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(num);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemProductPaymentBinding binding;

        public ViewHolder(@NonNull ItemProductPaymentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

} 