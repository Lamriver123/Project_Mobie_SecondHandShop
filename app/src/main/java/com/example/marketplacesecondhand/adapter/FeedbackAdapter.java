package com.example.marketplacesecondhand.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.databinding.ItemFeedbackBinding;
import com.example.marketplacesecondhand.dto.response.FeedbackResponse;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {

    private final Context context;
    private final List<FeedbackResponse> feedbackList;

    public FeedbackAdapter(Context context, List<FeedbackResponse> feedbackList) {
        this.context = context;
        this.feedbackList = feedbackList;
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ItemFeedbackBinding binding = ItemFeedbackBinding.inflate(inflater, parent, false);
        return new FeedbackViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        FeedbackResponse feedback = feedbackList.get(position);
        holder.bind(feedback);
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    public class FeedbackViewHolder extends RecyclerView.ViewHolder {
        private final ItemFeedbackBinding binding;

        public FeedbackViewHolder(ItemFeedbackBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(FeedbackResponse feedback) {
            binding.txtShopName.setText(feedback.getFeedbackerName());
            binding.txtProductName.setText("Mặt hàng: " + feedback.getProductName());
            binding.txtFeedback.setText(String.valueOf(feedback.getFeedback()));
            binding.ratingBar.setRating(feedback.getStar());

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            binding.txtDate.setText(sdf.format(feedback.getCreatedAt()));

            Glide.with(context)
                    .load(feedback.getImageFeedbacker())
                    .placeholder(R.drawable.img)
                    .error(R.drawable.img)
                    .into(binding.imgShop);
        }
    }
}
