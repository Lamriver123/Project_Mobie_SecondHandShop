package com.example.marketplacesecondhand.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.databinding.ItemProductCartBinding;
import com.example.marketplacesecondhand.dto.response.ProductResponse;
import com.example.marketplacesecondhand.models.CartProduct;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartProductAdapter extends RecyclerView.Adapter<CartProductAdapter.CartItemViewHolder> {

    private Context context;
    private List<CartProduct> productList;
    private CartShopAdapter.OnCartItemCheckListener onCartItemCheckListener;

    public void setOnCartItemCheckListener(CartShopAdapter.OnCartItemCheckListener listener) {
        this.onCartItemCheckListener = listener;
    }

    public CartProductAdapter(Context context, List<CartProduct> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductCartBinding binding = ItemProductCartBinding.inflate(LayoutInflater.from(context), parent, false);
        return new CartItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        CartProduct cartProduct = productList.get(position);
        ProductResponse product = cartProduct.getProductResponse();
        if (product == null) return;

        holder.binding.textViewProductName.setText(product.getProductName());
        holder.binding.textViewOriginalPrice.setText(formatCurrency(Integer.parseInt(product.getOriginalPrice())) + " VND");
        holder.binding.textViewOriginalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.binding.textViewDiscountPrice.setText(formatCurrency(Integer.parseInt(product.getCurrentPrice())) + " VND");
        holder.binding.textViewQuantity.setText(String.valueOf(cartProduct.getQuantityCart()));
        holder.binding.checkboxSelectItem.setChecked(cartProduct.isChecked());

        Glide.with(context)
                .load(R.drawable.img)
                .placeholder(R.drawable.img)
                .error(R.drawable.img)
                .into(holder.binding.imageViewProduct);

        // Checkbox chọn sản phẩm
        holder.binding.checkboxSelectItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            cartProduct.setChecked(isChecked);
            if (onCartItemCheckListener != null) {
                onCartItemCheckListener.onItemChecked();
            }
        });

        // Nút tăng số lượng
        holder.binding.buttonIncrease.setOnClickListener(v -> {
            int qty = cartProduct.getQuantityCart();
            cartProduct.setQuantityCart(qty + 1);
            notifyItemChanged(position);
            if (cartProduct.isChecked() && onCartItemCheckListener != null) {
                onCartItemCheckListener.onItemChecked();
            }
        });

        // Nút giảm số lượng
        holder.binding.buttonDecrease.setOnClickListener(v -> {
            int qty = cartProduct.getQuantityCart();
            if (qty > 1) {
                cartProduct.setQuantityCart(qty - 1);
                notifyItemChanged(position);
                if (cartProduct.isChecked() && onCartItemCheckListener != null) {
                    onCartItemCheckListener.onItemChecked();
                }
            } else {
                Toast.makeText(context, "Không thể giảm thêm", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String formatCurrency(int number) {
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(number);
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class CartItemViewHolder extends RecyclerView.ViewHolder {
        private final ItemProductCartBinding binding;

        public CartItemViewHolder(ItemProductCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public List<CartProduct> getProductList() {
        return productList;
    }
}
