package com.example.marketplacesecondhand.adapter.follow;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.ShopDetailActivity;
import com.example.marketplacesecondhand.databinding.ItemFollowBinding;
import com.example.marketplacesecondhand.dto.response.UserResponse;

import java.util.ArrayList;
import java.util.List;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.FollowViewHolder> {
    private List<UserResponse> users = new ArrayList<>();
    private OnFollowClickListener listener;

    public interface OnFollowClickListener {
        void onFollowClick(UserResponse user, boolean isFollowing);
    }

    public void setOnFollowClickListener(OnFollowClickListener listener) {
        this.listener = listener;
    }

    public void updateUsers(List<UserResponse> newUsers) {
        this.users = newUsers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FollowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFollowBinding binding = ItemFollowBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new FollowViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowViewHolder holder, int position) {
        UserResponse user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class FollowViewHolder extends RecyclerView.ViewHolder {
        private final ItemFollowBinding binding;

        FollowViewHolder(ItemFollowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(UserResponse user) {
            binding.tvName.setText(user.getFullName());

            // Load avatar using Glide
            Glide.with(binding.getRoot().getContext())
                    .load(user.getAvt())
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .into(binding.imgAvatar);

            // Optional: Add click listener for the entire item
            binding.getRoot().setOnClickListener(v -> {
                Intent intent = new Intent(binding.getRoot().getContext(), ShopDetailActivity.class);
                intent.putExtra("shop_id", user.getId());
                binding.getRoot().getContext().startActivity(intent);
            });
        }
    }
} 