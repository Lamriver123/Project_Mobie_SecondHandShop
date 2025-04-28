package com.example.marketplacesecondhand.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketplacesecondhand.databinding.ItemCartShopBinding;
import com.example.marketplacesecondhand.models.CartShop;
import com.example.marketplacesecondhand.models.User;

import java.util.List;

public class CartShopAdapter extends RecyclerView.Adapter<CartShopAdapter.CartShopViewHolder> {

    private Context context;

    private List<CartShop> shopList;

    public CartShopAdapter(Context context, List<CartShop> shopList) {
        this.context = context;
        this.shopList = shopList;
    }

    @NonNull
    @Override
    public CartShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCartShopBinding binding = ItemCartShopBinding.inflate(LayoutInflater.from(context), parent, false);
        return new CartShopViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartShopViewHolder holder, int position) {
        CartShop cartShop = shopList.get(position);
        User shop = cartShop.getUser();
        if (shop == null) return;

        holder.binding.textViewShopName.setText(shop.getFullName());
        holder.binding.checkboxShop.setChecked(cartShop.isChecked());

        holder.binding.recyclerViewProducts.setLayoutManager(new LinearLayoutManager(context));

        // Setup inner RecyclerView
        CartProductAdapter itemAdapter = new CartProductAdapter(context, cartShop.getProducts());
        holder.binding.recyclerViewProducts.setAdapter(itemAdapter);

        // Sự kiện click checkbox Shop
        holder.binding.checkboxShop.setOnCheckedChangeListener((buttonView, isChecked) -> {
            cartShop.setChecked(isChecked);
            for (int i = 0; i < cartShop.getProducts().size(); i++) {
                cartShop.getProducts().get(i).setChecked(isChecked);
            }
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return shopList != null ? shopList.size() : 0;
    }

    public static class CartShopViewHolder extends RecyclerView.ViewHolder {
        private final ItemCartShopBinding binding;

        public CartShopViewHolder(ItemCartShopBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
