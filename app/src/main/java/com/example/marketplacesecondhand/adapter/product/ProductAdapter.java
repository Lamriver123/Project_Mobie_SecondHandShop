package com.example.marketplacesecondhand.adapter.product;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Paint;
import android.util.TypedValue;
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
import com.example.marketplacesecondhand.activity.ProductDetailActivity;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.service.RetrofitClient;
import com.example.marketplacesecondhand.dto.request.FavoriteRequest;
import com.example.marketplacesecondhand.dto.response.ApiResponse;
import com.example.marketplacesecondhand.dto.response.ProductResponse;
import com.example.marketplacesecondhand.models.UserLoginInfo;

import java.text.NumberFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder>{
    private final Context context;
    private final List<ProductResponse> productList;
    private final Set<Integer> favoriteProductIds;
    public interface OnItemClickListener {
        void onItemClick(ProductResponse product);
    }

    public ProductAdapter(Context context, List<ProductResponse> productList, List<Integer> favoriteProductIds) {
        this.context = context;
        this.productList = productList;
        this.favoriteProductIds = new HashSet<>(favoriteProductIds);
    }
    public ProductAdapter(Context context, List<ProductResponse> productList) {
        this.context = context;
        this.productList = productList;
        this.favoriteProductIds = new HashSet<>();
    }


    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_products, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductResponse product = productList.get(position);

        holder.tvTitle.setText(product.getProductName());
        holder.tvPrice.setText(formatCurrency(Integer.parseInt(product.getCurrentPrice())) + " VND");
        holder.tvTimeLocation.setText(product.getTimeAgoText());
        holder.tvPricePre.setText(formatCurrency(Integer.parseInt(product.getOriginalPrice())) + " VND");
        holder.tvPricePre.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.tvSold.setText(product.getSoldText());
        // Badge (số lượng đã bán hoặc thứ hạng)
        holder.tvBadge.setText(String.valueOf(product.getSold()));

        // Load ảnh
//        Glide.with(context)
//                .load(product.getImageUrl()) // đảm bảo Product có hàm getImageUrl()
//                .placeholder(R.drawable.img)
//                .into(holder.imageProduct);

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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("product_id", product.getProductId());
                context.startActivity(intent);
            }
        });
        int marginInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 8, Resources.getSystem().getDisplayMetrics()); // 8dp

        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int itemWidth = (screenWidth / 2) - (marginInPx * 2); // Trừ 2 bên
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        layoutParams.width = itemWidth;
        holder.itemView.setLayoutParams(layoutParams);

// Đặt margin cho item
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            marginLayoutParams.setMargins(marginInPx, marginInPx, marginInPx, marginInPx);
            holder.itemView.setLayoutParams(marginLayoutParams);
        }

    }

    private String formatCurrency(int number) {
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(number);
    }
    private void toggleFavorite(int productId, int position, ProductViewHolder holder) {
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
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView imageProduct, iconFavorite;
        TextView tvBadge, tvTitle, tvPrice, tvTimeLocation, tvPricePre, tvSold;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.imageProduct);
            iconFavorite = itemView.findViewById(R.id.iconFavorite);
            tvBadge = itemView.findViewById(R.id.tvBadge);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvTimeLocation = itemView.findViewById(R.id.tvTimeLocation);
            tvPricePre = itemView.findViewById(R.id.tvPricePre);
            tvSold = itemView.findViewById(R.id.tvSold);
        }
    }
}
