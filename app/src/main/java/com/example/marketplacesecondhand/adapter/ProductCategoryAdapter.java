package com.example.marketplacesecondhand.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
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
import com.example.marketplacesecondhand.ProductDetailActivity;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.RetrofitClient;
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

public class ProductCategoryAdapter extends RecyclerView.Adapter<ProductCategoryAdapter.ProductCategoryViewHolder>{
    private Context context;
    private List<ProductResponse> productList;
    private ProductAdapter.OnItemClickListener listener;
    private final Set<Integer> favoriteProductIds;
    public interface OnItemClickListener {
        void onItemClick(ProductResponse product);
    }

    public ProductCategoryAdapter(Context context, List<ProductResponse> productList, ProductAdapter.OnItemClickListener listener, List<Integer> favoriteProductIds) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
        this.favoriteProductIds = new HashSet<>(favoriteProductIds);;
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
        holder.tvPrice.setText(formatCurrency(Integer.parseInt(product.getCurrentPrice())) + " VND");
        holder.tvPricePre.setText(formatCurrency(Integer.parseInt(product.getCurrentPrice())) + " VND");
        holder.tvPricePre.setPaintFlags(holder.tvPricePre.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("product_id", product.getProductId());
                context.startActivity(intent);
            }
        });
    }

    private String formatCurrency(int number) {
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(number);
    }
    private void toggleFavorite(int productId, int position, ProductCategoryViewHolder holder) {
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

    public static class ProductCategoryViewHolder extends RecyclerView.ViewHolder {

        ImageView imageProduct, iconFavorite;
        TextView tvTitle, tvSpec, tvPrice, tvTimeLocation, tvShop, tvRate, tvSold, tvPricePre;

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
            tvPricePre = itemView.findViewById(R.id.tvPricePre);
        }
    }

}
