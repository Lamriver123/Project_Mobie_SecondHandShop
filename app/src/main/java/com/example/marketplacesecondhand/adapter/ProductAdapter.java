package com.example.marketplacesecondhand.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.models.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder>{
    private final Context context;
    private final List<Product> productList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }


    public ProductAdapter(Context context, List<Product> productList, OnItemClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_products, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.tvTitle.setText(product.getProductName());
        holder.tvPrice.setText(product.getCurrentPrice() );
        holder.tvTimeLocation.setText(product.getTimeAgoText() + " · Tp Hồ Chí Minh");

        // Badge (số lượng đã bán hoặc thứ hạng)
        holder.tvBadge.setText(String.valueOf(product.getSold()));

        // Load ảnh
//        Glide.with(context)
//                .load(product.getImageUrl()) // đảm bảo Product có hàm getImageUrl()
//                .placeholder(R.drawable.img)
//                .into(holder.imageProduct);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(product));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView imageProduct, iconFavorite;
        TextView tvBadge, tvTitle, tvPrice, tvTimeLocation;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.imageProduct);
            iconFavorite = itemView.findViewById(R.id.iconFavorite);
            tvBadge = itemView.findViewById(R.id.tvBadge);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvTimeLocation = itemView.findViewById(R.id.tvTimeLocation);
        }
    }
}
