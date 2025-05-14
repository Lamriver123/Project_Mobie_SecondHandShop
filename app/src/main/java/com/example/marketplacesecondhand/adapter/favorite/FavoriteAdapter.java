package com.example.marketplacesecondhand.adapter.favorite;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.API.DatabaseHandler;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.service.RetrofitClient;
import com.example.marketplacesecondhand.dto.request.FavoriteRequest;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.ProductResponse;
import com.example.marketplacesecondhand.models.UserLoginInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {
    private List<ProductResponse> favoriteProducts;
    private Set<Integer> favoriteProductIds;
    private OnFavoriteItemClickListener listener;
    private Context context;

    public interface OnFavoriteItemClickListener {
        void onItemClick(ProductResponse product);
    }

    public FavoriteAdapter(Context context, List<ProductResponse> favoriteProducts, List<Integer> favoriteProductIds, OnFavoriteItemClickListener listener) {
        this.context = context;
        this.favoriteProducts = favoriteProducts;
        this.favoriteProductIds = new HashSet<>(favoriteProductIds);
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        ProductResponse product = favoriteProducts.get(position);

        holder.tvProductName.setText(product.getProductName());
        holder.tvProductPrice.setText(product.getCurrentPrice() + " đ");
        holder.tvProductTime.setText(product.getTimeAgoText());

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

        // Set iconFavorite ban đầu theo danh sách favoriteProductIds
        if (favoriteProductIds.contains(product.getProductId())) {
            holder.iconFavorite.setImageResource(R.drawable.ic_heart_selected);
        } else {
            holder.iconFavorite.setImageResource(R.drawable.ic_heart_border_red);
        }

        // Xử lý sự kiện nhấn vào iconFavorite
        holder.iconFavorite.setOnClickListener(v -> {
            toggleFavorite(product.getProductId(), position, holder);
        });

        holder.itemView.setOnClickListener(v -> listener.onItemClick(product));
    }

    private void toggleFavorite(int productId, int position, FavoriteAdapter.FavoriteViewHolder holder) {
        DatabaseHandler db = new DatabaseHandler(context);
        UserLoginInfo userLoginInfo = db.getLoginInfoSQLite();

        if (userLoginInfo == null || userLoginInfo.getUserId() == 0) {
            Toast.makeText(context, "Bạn cần đăng nhập để sử dụng tính năng yêu thích!", Toast.LENGTH_SHORT).show();
            return; // Không thực hiện gọi API nữa
        }

        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);

        FavoriteRequest request = new FavoriteRequest(userLoginInfo.getUserId(), productId);

        Call<ApiResponse<String>> call = apiService.toggleFavorite(request);
        call.enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                    // Toggle favorite ID trong danh sách
                    if (favoriteProductIds.contains(productId)) {
                        favoriteProductIds.remove(productId);
                        holder.iconFavorite.setImageResource(R.drawable.ic_heart_border_red);
                    } else {
                        favoriteProductIds.add(productId);
                        holder.iconFavorite.setImageResource(R.drawable.ic_heart_selected);
                    }
                } else {
                    Toast.makeText(context, "Có lỗi xảy ra, thử lại sau!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                Toast.makeText(context, "Không thể kết nối server!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d("getItemCount", "getItemCount: " + favoriteProducts.size());
        return favoriteProducts.size();
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageProduct;
        private TextView tvProductName;
        private TextView tvProductPrice;
        private TextView tvProductTime;
        private ImageView iconFavorite;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.imageProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductTime = itemView.findViewById(R.id.tvProductTime);
            iconFavorite = itemView.findViewById(R.id.iconFavorite);
        }
    }
} 