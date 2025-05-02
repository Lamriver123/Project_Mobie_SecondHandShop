package com.example.marketplacesecondhand.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.databinding.ItemProductOrderBinding;
import com.example.marketplacesecondhand.dto.response.ProductResponse;

import java.util.List;

public class ProductInOrderAdapter extends RecyclerView.Adapter<ProductInOrderAdapter.ProductViewHolder> {

    private final Context context;
    private final List<ProductResponse> productList;

    public ProductInOrderAdapter(Context context, List<ProductResponse> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductOrderBinding binding = ItemProductOrderBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ProductViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductResponse product = productList.get(position);
        holder.binding.txtProductName.setText(product.getProductName());
        holder.binding.txtProductQuantity.setText("x1");
        int price = Integer.parseInt(product.getCurrentPrice());
        holder.binding.txtProductPrice.setText("₫" + String.format("%,d", price).replace(',', '.'));


        // Placeholder data
        holder.binding.txtProductVariation.setText("Màu ngẫu nhiên, Size tự chọn");

        Glide.with(context)
                .load(R.drawable.img)
                .placeholder(R.drawable.img)
                .error(R.drawable.img)
                .into(holder.binding.imgProduct);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ItemProductOrderBinding binding;

        public ProductViewHolder(@NonNull ItemProductOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}