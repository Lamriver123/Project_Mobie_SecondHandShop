package com.example.marketplacesecondhand.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketplacesecondhand.databinding.ItemCartShopBinding;
import com.example.marketplacesecondhand.models.CartProduct;
import com.example.marketplacesecondhand.models.CartShop;
import com.example.marketplacesecondhand.models.User;

import java.util.List;

public class CartShopAdapter extends RecyclerView.Adapter<CartShopAdapter.CartShopViewHolder> {

    private Context context;
    private List<CartShop> shopList;
    private OnCartItemCheckListener onCartItemCheckListener;

    public interface OnCartItemCheckListener {
        void onItemChecked();
    }

    public void setOnCartItemCheckListener(OnCartItemCheckListener listener) {
        this.onCartItemCheckListener = listener;
    }

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

        holder.binding.textViewShopName.setText(shop.getUsername());
        holder.binding.checkboxShop.setChecked(cartShop.isChecked());

        holder.binding.recyclerViewProducts.setLayoutManager(new LinearLayoutManager(context));

        // Setup inner RecyclerView
        CartProductAdapter itemAdapter = new CartProductAdapter(context, cartShop.getProducts());
        itemAdapter.setOnCartItemCheckListener(() -> {
            if (onCartItemCheckListener != null) {
                onCartItemCheckListener.onItemChecked();
            }
        });
        holder.binding.recyclerViewProducts.setAdapter(itemAdapter);

        // Gắn ItemTouchHelper
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int swipedPosition = viewHolder.getAdapterPosition();

                new AlertDialog.Builder(context)
                        .setTitle("Xác nhận")
                        .setMessage("Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            itemAdapter.removeItem(swipedPosition);
                            if(cartShop.getProducts().size() == 0){
                                shopList.remove(cartShop);
                                notifyDataSetChanged();
                            }
                            if (onCartItemCheckListener != null) {
                                onCartItemCheckListener.onItemChecked();
                            }
                        })
                        .setNegativeButton("Hủy", (dialog, which) -> {
                            itemAdapter.notifyItemChanged(swipedPosition); // undo swipe
                        })
                        .setCancelable(false)
                        .show();
            }

        });
        itemTouchHelper.attachToRecyclerView(holder.binding.recyclerViewProducts);

        // Sự kiện click checkbox Shop
        holder.binding.checkboxShop.setOnCheckedChangeListener((buttonView, isChecked) -> {
            cartShop.setChecked(isChecked);
            for (CartProduct product : cartShop.getProducts()) {
                product.setChecked(isChecked);
            }
            itemAdapter.notifyDataSetChanged();
            if (onCartItemCheckListener != null) {
                onCartItemCheckListener.onItemChecked();
            }
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

    public List<CartShop> getShopList() {
        return shopList;
    }
}
