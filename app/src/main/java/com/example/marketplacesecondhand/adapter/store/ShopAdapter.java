package com.example.marketplacesecondhand.adapter.store;

import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.activity.LoginActivity;
import com.example.marketplacesecondhand.activity.ShopDetailActivity;
import com.example.marketplacesecondhand.databinding.ItemShopBinding;
import com.example.marketplacesecondhand.dto.response.ShopResponse;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder> {

    private List<ShopResponse> shops;
    private int currentUserId;
    private OnFollowListener onFollowListener;

    public interface OnFollowListener {
        void onFollowClick(ShopResponse shop, boolean isFollowing);
    }

    public void setOnFollowListener(OnFollowListener listener) {
        this.onFollowListener = listener;
    }

    public ShopAdapter(List<ShopResponse> shops, int currentUserId) {
        this.shops = shops;
        this.currentUserId = currentUserId;
    }

    public void updateShops(List<ShopResponse> newShops) {
        this.shops = newShops;
        notifyDataSetChanged();
    }

    public static class ShopViewHolder extends RecyclerView.ViewHolder {
        private final ItemShopBinding binding;

        public ShopViewHolder(@NonNull ItemShopBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ShopResponse shop, int currentUserId, OnFollowListener listener) {
            binding.txtShopName.setText(shop.getUsername());
            binding.ratingBar.setRating((float) shop.getAverageRating());
            binding.txtReviewCount.setText(shop.getTotalReviews() + " đánh giá");

            // Load shop avatar using Glide
            Glide.with(binding.getRoot().getContext())
                    .load(shop.getAvt())
                    .circleCrop()
                    .error(R.drawable.bg1)
                    .placeholder(R.drawable.bg_shape)
                    .into(binding.imgShop);

            // Load product images if available
            if (shop.getProducts() != null && !shop.getProducts().isEmpty()) {
                int radiusInPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 16f, binding.getRoot().getContext().getResources().getDisplayMetrics());

                // Load first product image
                if (shop.getProducts().size() >= 1) {
                    Glide.with(binding.getRoot().getContext())
                            .load(shop.getProducts().get(0).getCurrentImages().get(0))
                            .transform(new RoundedCornersTransformation(radiusInPx, 0))
                            .error(R.drawable.bg1)
                            .placeholder(R.drawable.bg_shape)
                            .into(binding.imgProduct1);
                    binding.imgProduct1.setVisibility(View.VISIBLE);
                } else {
                    binding.imgProduct1.setVisibility(View.INVISIBLE);
                }

                // Load second product image
                if (shop.getProducts().size() >= 2) {
                    Glide.with(binding.getRoot().getContext())
                            .load(shop.getProducts().get(1).getCurrentImages().get(1))
                            .transform(new RoundedCornersTransformation(radiusInPx, 0))
                            .error(R.drawable.bg1)
                            .placeholder(R.drawable.bg_shape)
                            .into(binding.imgProduct2);
                    binding.imgProduct2.setVisibility(View.VISIBLE);
                } else {
                    binding.imgProduct2.setVisibility(View.INVISIBLE);
                }
            }

            // Update follow button state
           // boolean isFollowing = shop.getFollowerIds() != null && shop.getFollowerIds().contains(currentUserId);
          //  updateFollowButtonState(isFollowing);
            boolean isFollowing;
            if (currentUserId > 0 && shop.getFollowerIds() != null) {
                isFollowing = shop.getFollowerIds().contains(currentUserId);
            } else {
                isFollowing = false;
            }
            updateFollowButtonState(isFollowing);
            
            binding.btnViewShop.setOnClickListener(v -> {
                Intent intent = new Intent(binding.getRoot().getContext(), ShopDetailActivity.class);
                intent.putExtra("shop_id", shop.getId());
                binding.getRoot().getContext().startActivity(intent);
            });

            binding.btnFollow.setOnClickListener(v -> {
                if (currentUserId > 0 && shop.getFollowerIds() != null) {
                    if (listener != null) {
                        listener.onFollowClick(shop, isFollowing);
                    }
                }
                else {
                    Intent intent = new Intent(binding.getRoot().getContext(), LoginActivity.class);
                    binding.getRoot().getContext().startActivity(intent);
                }
            });
        }

        private void updateFollowButtonState(boolean isFollowing) {
            if (isFollowing) {
                binding.btnFollow.setText("Đang theo dõi");
              //  binding.btnFollow.setBackgroundResource(R.drawable.bg_button_following);
                binding.btnFollow.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.grey_dark));
            } else {
                binding.btnFollow.setText("Theo dõi");
               // binding.btnFollow.setBackgroundResource(R.drawable.bg_button_follow);
                binding.btnFollow.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.white));
            }
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
        ShopResponse shop = shops.get(position);
        holder.bind(shop, currentUserId, onFollowListener);
    }

    @Override
    public int getItemCount() {
        return shops.size();
    }
}
