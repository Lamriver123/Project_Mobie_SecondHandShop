package com.example.marketplacesecondhand.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketplacesecondhand.databinding.ItemVoucherBinding;
import com.example.marketplacesecondhand.dto.response.VoucherResponse;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder> {
    private List<VoucherResponse> vouchers;
    private int selectedPosition = -1;
    private OnVoucherSelectedListener listener;
    private DecimalFormat currencyFormat = new DecimalFormat("###,###,###đ");
    private static final String TAG = "VoucherAdapter";
    public interface OnVoucherSelectedListener {
        void onVoucherSelected(VoucherResponse voucher);
    }

    public VoucherAdapter(List<VoucherResponse> vouchers, OnVoucherSelectedListener listener) {
        this.vouchers = (vouchers != null) ? new ArrayList<>(vouchers) : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemVoucherBinding binding = ItemVoucherBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new VoucherViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        VoucherResponse voucher = vouchers.get(position);
        holder.bind(voucher, position);
    }

    @Override
    public int getItemCount() {
        return vouchers != null ? vouchers.size() : 0;
    }

    public void updateVouchers(List<VoucherResponse> newVouchers) {
        this.vouchers.clear();
        if (newVouchers != null) {
            this.vouchers.addAll(newVouchers);
        }
        selectedPosition = -1;
        notifyDataSetChanged();
        Log.d(TAG, "Vouchers updated. Size: " + this.vouchers.size());
    }

    public VoucherResponse getSelectedVoucher() {
        if (selectedPosition != -1 && selectedPosition < vouchers.size()) {
            return vouchers.get(selectedPosition);
        }
        return null;
    }

    public void clearSelection() {
        if (selectedPosition != -1) {
            int previouslySelected = selectedPosition;
            selectedPosition = -1;
            if (previouslySelected < getItemCount()) {
                notifyItemChanged(previouslySelected);
            }
        }
    }


    class VoucherViewHolder extends RecyclerView.ViewHolder {
        ItemVoucherBinding binding;

        public VoucherViewHolder(@NonNull ItemVoucherBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(final VoucherResponse voucher, final int position) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            binding.txtVoucherCode.setText(voucher.getCode());
            binding.txtVoucherDescription.setText(voucher.getDescription());
            binding.txtVoucherStatus.setText(voucher.getStatus());
            if (voucher.getEndDate() != null) {
                binding.txtVoucherValidity.setText("HSD: " + dateFormat.format(voucher.getEndDate()));
            } else {
                binding.txtVoucherValidity.setText("HSD: Không xác định");
            }
            binding.txtVoucherQuantity.setText("Còn lại: " + (voucher.getQuantity()));

            String discountInfo;
            if ("percentage".equalsIgnoreCase(voucher.getDiscountType())) {
                discountInfo = String.format(Locale.getDefault(),"Giảm %d%%, tối đa %s",
                        voucher.getDiscountValue() != null ? voucher.getDiscountValue().intValue() : 0,
                        currencyFormat.format(voucher.getMaximumDiscountAmount() != null ? voucher.getMaximumDiscountAmount() : 0));
            } else {
                discountInfo = String.format(Locale.getDefault(),"Giảm %s",
                        currencyFormat.format(voucher.getDiscountValue() != null ? voucher.getDiscountValue() : 0));
            }
            binding.txtDiscountInfo.setText(discountInfo);

            binding.txtMinimumOrder.setText("Đơn tối thiểu: " +
                    currencyFormat.format(voucher.getMinimumOrderAmount() != null ? voucher.getMinimumOrderAmount() : 0));

            binding.radioVoucher.setChecked(position == selectedPosition);
            Log.d(TAG, "Bind ViewHolder at position " + position + ", selectedPosition: " + selectedPosition + ", isChecked: " + binding.radioVoucher.isChecked());

            binding.getRoot().setOnClickListener(v -> {
                int clickedPosition = getAdapterPosition();
                if (clickedPosition == RecyclerView.NO_POSITION) {
                    return;
                }

                int previouslySelected = selectedPosition;

                if (selectedPosition == clickedPosition) {
                    // Nếu click vào item đã chọn, thì bỏ chọn nó
                    selectedPosition = -1;
                    if (listener != null) {
                        listener.onVoucherSelected(null);
                    }
                    Log.d(TAG, "Item at position " + clickedPosition + " unselected.");
                } else {
                    // Nếu click vào item mới, chọn nó
                    selectedPosition = clickedPosition;
                    if (listener != null) {
                        listener.onVoucherSelected(vouchers.get(selectedPosition));
                    }
                    Log.d(TAG, "Item at position " + selectedPosition + " selected. Previous: " + previouslySelected);
                }

                if (previouslySelected != -1 && previouslySelected < getItemCount()) {
                    notifyItemChanged(previouslySelected);
                }

                if (clickedPosition < getItemCount()){
                    notifyItemChanged(clickedPosition);
                }
            });

            binding.radioVoucher.setClickable(false);
            binding.radioVoucher.setFocusable(false);
        }
    }
}
