package com.example.marketplacesecondhand.adapter.cart;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.API.DatabaseHandler;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.service.RetrofitClient;
import com.example.marketplacesecondhand.databinding.ItemProductCartBinding;
import com.example.marketplacesecondhand.dto.request.CartRequest;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.ProductResponse;
import com.example.marketplacesecondhand.models.CartProduct;
import com.example.marketplacesecondhand.models.UserLoginInfo;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartProductAdapter extends RecyclerView.Adapter<CartProductAdapter.CartItemViewHolder> {

    private static final String TAG = "CartProductAdapter";
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
                .load(cartProduct.getProductResponse().getCurrentImages().get(0))
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
            if (qty < product.getQuantity()){
                cartProduct.setQuantityCart(qty + 1);
                updateCartQuantity(qty + 1,product.getProductId());
                notifyItemChanged(position);
                if (cartProduct.isChecked() && onCartItemCheckListener != null) {
                    onCartItemCheckListener.onItemChecked();
                }
            }
        });

        // Nút giảm số lượng
        holder.binding.buttonDecrease.setOnClickListener(v -> {
            int qty = cartProduct.getQuantityCart();
            if (qty > 1) {
                cartProduct.setQuantityCart(qty - 1);
                notifyItemChanged(position);
                updateCartQuantity(qty - 1,product.getProductId());
                if (cartProduct.isChecked() && onCartItemCheckListener != null) {
                    onCartItemCheckListener.onItemChecked();
                }
            } else {
                Toast.makeText(context, "Không thể giảm thêm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void removeItem(int position) {
        if (position >= 0 && position < productList.size()) {
            DatabaseHandler db = new DatabaseHandler(context);
            UserLoginInfo userLoginInfo = db.getLoginInfoSQLite();

            if (userLoginInfo == null || userLoginInfo.getUserId() == 0) {
                Toast.makeText(context, "Bạn cần đăng nhập để thêm sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                return; // Không thực hiện gọi API nữa
            }

            APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);

            CartProduct cartProduct = productList.get(position);

            CartRequest request = new CartRequest(userLoginInfo.getUserId(),cartProduct.getProductResponse().getProductId() , 0);

            Call<ApiResponse<Void>> call = apiService.deleteCart(request);
            call.enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Có lỗi xảy ra, thử lại sau!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                    Toast.makeText(context, "Không thể kết nối server!", Toast.LENGTH_SHORT).show();
                }
            });
            productList.remove(position);
            notifyItemRemoved(position);
            if (onCartItemCheckListener != null) {
                onCartItemCheckListener.onItemChecked();
            }
        }
    }

    private void updateCartQuantity(int quantity,int productId){
        DatabaseHandler db = new DatabaseHandler(context);
        UserLoginInfo userLoginInfo = db.getLoginInfoSQLite();

        if (userLoginInfo == null || userLoginInfo.getUserId() == 0) {
            Toast.makeText(context, "Bạn cần đăng nhập để thêm sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            return; // Không thực hiện gọi API nữa
        }

        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);

        CartRequest request = new CartRequest(userLoginInfo.getUserId(), productId, quantity);

        Call<ApiResponse<Void>> call = apiService.updateToCart(request);
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Có lỗi xảy ra, thử lại sau!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(context, "Không thể kết nối server!", Toast.LENGTH_SHORT).show();
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
