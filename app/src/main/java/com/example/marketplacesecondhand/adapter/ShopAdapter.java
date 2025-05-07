package com.example.marketplacesecondhand.adapter;

import static androidx.core.content.ContentProviderCompat.requireContext;
import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.marketplacesecondhand.CartActivity;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.ShopDetailActivity;
import com.example.marketplacesecondhand.databinding.ItemShopBinding;
import com.example.marketplacesecondhand.models.Shop;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder> {

    private List<Shop> shops;

    public ShopAdapter(List<Shop> shops) {
        this.shops = shops;
    }

    public static class ShopViewHolder extends RecyclerView.ViewHolder {
        private final ItemShopBinding binding;

        public ShopViewHolder(@NonNull ItemShopBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Shop shop) {

            binding.txtShopName.setText(shop.getName());
            binding.ratingBar.setRating(shop.getRating());
            binding.txtReviewCount.setText(shop.getReviewCount());

            // Load shop avatar using Glide
            Glide.with(binding.getRoot().getContext())
                    .load(shop.getAvt())
                    .circleCrop()
                    .error(R.drawable.bg1)
                    .placeholder(R.drawable.bg_shape)
                    .into(binding.imgShop);


            List<String> productImages = shop.getProductImagesAds();

            int radiusInPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 16f, binding.getRoot().getContext().getResources().getDisplayMetrics());

            // Load ảnh thứ nhất
            if (productImages != null && productImages.size() >= 1) {
                Glide.with(binding.getRoot().getContext())
                        .load(productImages.get(0))
                        .transform(new RoundedCornersTransformation(radiusInPx, 0))
                        .error(R.drawable.bg1)
                        .placeholder(R.drawable.bg_shape)
                        .into(binding.imgProduct1);
                binding.imgProduct1.setVisibility(View.VISIBLE);
            } else {
                binding.imgProduct1.setVisibility(View.INVISIBLE);
            }

            // Load ảnh thứ hai
            if (productImages != null && productImages.size() >= 2) {
                Glide.with(binding.getRoot().getContext())
                        .load(productImages.get(1))
                        .transform(new RoundedCornersTransformation(radiusInPx, 0))
                        .error(R.drawable.bg1)
                        .placeholder(R.drawable.bg_shape)
                        .into(binding.imgProduct2);
                binding.imgProduct2.setVisibility(View.VISIBLE);
            } else {
                binding.imgProduct2.setVisibility(View.INVISIBLE);
            }


            // Optional: Add click listeners
            binding.btnViewShop.setOnClickListener(v -> {
                Intent intent = new Intent(binding.getRoot().getContext(), ShopDetailActivity.class);
                intent.putExtra("shop_id", shop.getShopId());
                binding.getRoot().getContext().startActivity(intent);
                Toast.makeText(binding.getRoot().getContext(), "Xem shop: " + shop.getName(), Toast.LENGTH_SHORT).show();
            });

            binding.btnFollow.setOnClickListener(v -> {
                // Xử lý khi nhấn nút "Theo dõi"
                Toast.makeText(binding.getRoot().getContext(), "Theo dõi shop: " + shop.getName(), Toast.LENGTH_SHORT).show();
                // TODO: Implement follow logic (update UI, call API, etc.)
            });


        }
    }

    @NonNull
    @Override
    public ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemShopBinding binding = ItemShopBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ShopViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopViewHolder holder, int position) {
        Shop shop = shops.get(position);
        holder.bind(shop);
    }

    @Override
    public int getItemCount() {
        return shops.size();
    }
}
