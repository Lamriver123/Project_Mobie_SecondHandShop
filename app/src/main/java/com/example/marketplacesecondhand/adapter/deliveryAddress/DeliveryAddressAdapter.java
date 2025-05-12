package com.example.marketplacesecondhand.adapter.deliveryAddress;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketplacesecondhand.databinding.ItemLocationBinding;
import com.example.marketplacesecondhand.dto.response.DeliveryAddressResponse;

import java.util.List;

public class DeliveryAddressAdapter extends RecyclerView.Adapter<DeliveryAddressAdapter.AddressViewHolder> {
    private List<DeliveryAddressResponse> addressList;
    private Context context;
    private OnAddressClickListener listener;
    private int selectedPosition = -1;
    private DeliveryAddressResponse selectedAddress;

    public List<DeliveryAddressResponse> getAddressList() {
        return addressList;
    }

    public interface OnAddressClickListener {
        void onAddressClick(DeliveryAddressResponse address, int position);
        void onEditAddressClick(DeliveryAddressResponse address, int position);
    }

    public DeliveryAddressAdapter(Context context, List<DeliveryAddressResponse> addressList, OnAddressClickListener listener) {
        this.context = context;
        this.addressList = addressList;
        this.listener = listener;
        this.selectedAddress = null;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemLocationBinding binding = ItemLocationBinding.inflate(inflater, parent, false);
        return new AddressViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        DeliveryAddressResponse address = addressList.get(position);
        holder.bind(address, position);
    }

    @Override
    public int getItemCount() {
        return addressList == null ? 0 : addressList.size();
    }

    public void setSelectedAddress(DeliveryAddressResponse address) {
        this.selectedAddress = address;
        if (address != null) {
            for (int i = 0; i < addressList.size(); i++) {
                if (addressList.get(i).getAddressId() == address.getAddressId()) {
                    selectedPosition = i;
                    break;
                }
            }
        }
        else {
            selectedPosition = -1;
        }
        notifyDataSetChanged();
    }

    public void setAddressList(List<DeliveryAddressResponse> newAddressList) {
        // Lưu lại địa chỉ đang được chọn trước khi cập nhật
        DeliveryAddressResponse currentSelected = this.selectedAddress;
        
        this.addressList.clear();
        this.addressList.addAll(newAddressList);

        if (currentSelected != null) {
            // Tìm vị trí của địa chỉ đã chọn trong danh sách mới
            for (int i = 0; i < this.addressList.size(); i++) {
                if (this.addressList.get(i).getAddressId() == currentSelected.getAddressId()) {
                    selectedPosition = i;
                    this.selectedAddress = currentSelected;
                    break;
                }
            }
        } else {
            // Nếu không có địa chỉ được chọn, tìm địa chỉ mặc định
            selectedPosition = -1;
            for (int i = 0; i < this.addressList.size(); i++) {
                if (this.addressList.get(i).getDefaultAddress() != 0) {
                    selectedPosition = i;
                    this.selectedAddress = this.addressList.get(i);
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }

    public DeliveryAddressResponse getSelectedAddress() {
        return selectedAddress;
    }

    public class AddressViewHolder extends RecyclerView.ViewHolder {
        private ItemLocationBinding binding;

        public AddressViewHolder(ItemLocationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(final DeliveryAddressResponse address, final int position) {
            binding.tvAddressUserName.setText(address.getNameBuyer()); // Giả sử addressName chứa tên + SĐT
            binding.tvAddressPhoneNumber.setText(address.getPhoneNumber());
            binding.tvAddressName.setText(address.getAddressName());

            // Hiển thị tag "Mặc định"
            if (address.getDefaultAddress() != 0) {
                binding.tvAddressDefaultTag.setVisibility(View.VISIBLE);
            } else {
                binding.tvAddressDefaultTag.setVisibility(View.GONE);
            }

            // Xử lý trạng thái của RadioButton
            binding.radioSelectAddress.setChecked(position == selectedPosition);

            // Sự kiện click cho toàn bộ item hoặc RadioButton
            View.OnClickListener clickListener = v -> {
                if (listener != null) {
                    int previousSelectedPosition = selectedPosition;
                    selectedPosition = getAdapterPosition();
                    listener.onAddressClick(address, selectedPosition);

                    // Cập nhật lại trạng thái của RadioButton
                    notifyItemChanged(previousSelectedPosition);
                    notifyItemChanged(selectedPosition);
                }
            };

            binding.getRoot().setOnClickListener(clickListener);
            binding.radioSelectAddress.setOnClickListener(clickListener);


            // Tùy chọn: Xử lý sự kiện click cho nút chỉnh sửa
            binding.ivEditAddress.setVisibility(View.VISIBLE); // Tạm ẩn, bạn có thể hiện lại
            binding.ivEditAddress.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditAddressClick(address, getAdapterPosition());
                }
            });
        }
    }
}
