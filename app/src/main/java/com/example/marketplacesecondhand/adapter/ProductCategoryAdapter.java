package com.example.marketplacesecondhand.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.dto.response.ProductResponse;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductCategoryAdapter extends RecyclerView.Adapter<ProductCategoryAdapter.ProductCategoryViewHolder>{
    private Context context;
    private List<ProductResponse> productList;
    private ProductAdapter.OnItemClickListener listener;
    private final Set<Integer> favoritePositions = new HashSet<>();
    public interface OnItemClickListener {
        void onItemClick(ProductResponse product);
    }

    public ProductCategoryAdapter(Context context, List<ProductResponse> productList, ProductAdapter.OnItemClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_category, parent, false);
        return new ProductCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductCategoryViewHolder holder, int position) {
        ProductResponse product = productList.get(position);

        holder.tvTitle.setText(product.getProductName());
        holder.tvPrice.setText(product.getCurrentPrice() + " đ");
        holder.tvTimeLocation.setText(product.getTimeAgoText() + " · Tp Hồ Chí Minh");
        holder.tvSold.setText(product.getSold() + " Đã bán");
        holder.tvSpec.setText(product.getProductDescription());


        // Load ảnh từ currentImages (dùng ảnh đầu tiên làm đại diện)
        List<String> imageUrls = product.getCurrentImages();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            Glide.with(context)
                    .load(imageUrls.get(0)) // load ảnh đầu tiên
                    .placeholder(R.drawable.img) // ảnh mặc định khi đang load
                    .error(R.drawable.img) // ảnh hiển thị nếu lỗi
                    .into(holder.imageProduct);
        } else {
            // Nếu không có ảnh, hiển thị ảnh mặc định
            holder.imageProduct.setImageResource(R.drawable.img);
        }

        // Set iconFavorite ban đầu
        if (favoritePositions.contains(position)) {
            holder.iconFavorite.setImageResource(R.drawable.ic_heart_selected);
        } else {
            holder.iconFavorite.setImageResource(R.drawable.ic_heart_border_red);
        }

        // Xử lý sự kiện nhấn vào iconFavorite
        holder.iconFavorite.setOnClickListener(v -> {
            if (favoritePositions.contains(position)) {
                favoritePositions.remove(position);
                holder.iconFavorite.setImageResource(R.drawable.ic_heart_border_red);
                Toast.makeText(context, "Đã hủy yêu thích sản phầm này", Toast.LENGTH_SHORT).show();
            } else {
                favoritePositions.add(position);
                holder.iconFavorite.setImageResource(R.drawable.ic_heart_selected);
                Toast.makeText(context, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnClickListener(v -> listener.onItemClick(product));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductCategoryViewHolder extends RecyclerView.ViewHolder {

        ImageView imageProduct, iconFavorite;
        TextView tvTitle, tvSpec, tvPrice, tvTimeLocation, tvShop, tvRate, tvSold;

        public ProductCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.imgProduct);
            iconFavorite = itemView.findViewById(R.id.ivFavorite);
            tvTitle = itemView.findViewById(R.id.textTitle);
            tvSpec = itemView.findViewById(R.id.textSpec);
            tvPrice = itemView.findViewById(R.id.textPrice);
            tvTimeLocation = itemView.findViewById(R.id.textLocation);
            tvShop = itemView.findViewById(R.id.textShop);
            tvRate = itemView.findViewById(R.id.textRating);
            tvSold = itemView.findViewById(R.id.textSold);
        }
    }

}
