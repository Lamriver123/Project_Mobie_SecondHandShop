package com.example.marketplacesecondhand.adapter.order;

import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.marketplacesecondhand.ProductDetailActivity;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.databinding.ItemProductOrderBinding;
import com.example.marketplacesecondhand.dto.response.OrderDetailResponse;
import com.example.marketplacesecondhand.dto.response.ProductResponse;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ProductInOrderAdapter extends RecyclerView.Adapter<ProductInOrderAdapter.ProductViewHolder> {

    private final Context context;
    private final List<OrderDetailResponse> productList;

    public ProductInOrderAdapter(Context context, List<OrderDetailResponse> productList) {
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
        OrderDetailResponse product = productList.get(position);
        holder.binding.txtProductName.setText(product.getProductName());
        holder.binding.txtProductQuantity.setText("x" + product.getQuantity());
//        holder.binding.imgProduct
        int radiusInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 16f, holder.binding.getRoot().getContext().getResources().getDisplayMetrics());

        Glide.with(holder.binding.getRoot().getContext())
                .load(product.getCurrentImages())
                .transform(new RoundedCornersTransformation(radiusInPx, 0))
                .error(R.drawable.img)
                .placeholder(R.drawable.bg_shape)
                .into(holder.binding.imgProduct);

        int price = Integer.parseInt(product.getPrice());
        holder.binding.txtProductPrice.setText("₫" + String.format("%,d", price).replace(',', '.'));


        // Placeholder data
        holder.binding.txtProductVariation.setText("Màu ngẫu nhiên, Size tự chọn");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("product_id", product.getProductId());
                context.startActivity(intent);
            }
        });
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