package com.example.marketplacesecondhand.adapter.order;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.API.DatabaseHandler;
import com.example.marketplacesecondhand.activity.ProductDetailActivity;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.service.RetrofitClient;
import com.example.marketplacesecondhand.databinding.ItemProductOrderBinding;
import com.example.marketplacesecondhand.dto.response.OrderDetailResponse;
import com.example.marketplacesecondhand.models.UserLoginInfo;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductInOrderAdapter extends RecyclerView.Adapter<ProductInOrderAdapter.ProductViewHolder> {

    private final Context context;
    private final List<OrderDetailResponse> productList;
    private final String orderStatus; // Thêm trường để lưu trạng thái đơn hàng
    private final int orderId; // Thêm trường để lưu ID đơn hàng

    private APIService apiService;
    public ProductInOrderAdapter(Context context, List<OrderDetailResponse> productList, String orderStatus, int orderId) {
        this.context = context;
        this.productList = productList;
        this.orderStatus = orderStatus;
        this.orderId = orderId;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductOrderBinding binding = ItemProductOrderBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ProductViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        OrderDetailResponse product = productList.get(position);
        holder.binding.txtProductName.setText(product.getProductName());
        holder.binding.txtProductQuantity.setText("x" + product.getQuantity());
//        holder.binding.imgProduct
        int radiusInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 16f, holder.binding.getRoot().getContext().getResources().getDisplayMetrics());

        Glide.with(holder.binding.getRoot().getContext())
                .load(product.getCurrentImages())
                .transform(new RoundedCornersTransformation(radiusInPx, 0))
                .error(R.drawable.img)
                .placeholder(R.drawable.bg_shape)
                .into(holder.binding.imgProduct);

        int price = Integer.parseInt(product.getPrice());
        holder.binding.txtProductPrice.setText("₫" + String.format("%,d", price).replace(',', '.'));


        // Placeholder data
       // holder.binding.txtProductVariation.setText("Màu ngẫu nhiên, Size tự chọn");


        holder.binding.btnReview.setVisibility(View.GONE);

        DatabaseHandler db = new DatabaseHandler(context);
        UserLoginInfo userLoginInfo = db.getLoginInfoSQLite();
        int userId = userLoginInfo.getUserId();

        if ("Đã giao".equals(orderStatus)) {


            apiService = RetrofitClient.getRetrofit().create(APIService.class);
            Log.d("API_CALL", "User ID: " + userId + ", Order ID: " + orderId + ", Product ID: " + product.getProductId() + ", Status: " + orderStatus);
            Call<Boolean> call = apiService.checkFeedbackExists(orderId, product.getProductId(),userId); // Ví dụ: orderId = 123, productId = 456, buyerId = 1
            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.isSuccessful()) {
                        Boolean exists = response.body();
                        if (!exists) {
                            holder.binding.btnReview.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.e("API_CALL", "Error response code: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    Log.e("API_CALL", "API call failed: " + t.getMessage());
                }
            });
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("product_id", product.getProductId());
                context.startActivity(intent);
            }
        });

        holder.binding.btnReview.setOnClickListener(v -> {
            FeedbackDialog dialog = new FeedbackDialog(context,
                    product.getProductId(),
                    orderId,
                    userId
            );

            dialog.setOnFeedbackSubmittedListener(success -> {
                if (success) {
                    holder.binding.btnReview.setVisibility(View.GONE); // Ẩn nút nếu đánh giá thành công
                }
            });

            dialog.show();
        });




    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ItemProductOrderBinding binding;

        public ProductViewHolder(@NonNull ItemProductOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}