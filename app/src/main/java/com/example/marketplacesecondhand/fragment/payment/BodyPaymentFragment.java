package com.example.marketplacesecondhand.fragment.payment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.marketplacesecondhand.R; // Đảm bảo import R đúng
import com.example.marketplacesecondhand.databinding.FragmentBodyPaymentBinding;
import com.example.marketplacesecondhand.dto.response.DeliveryAddressResponse;
import com.example.marketplacesecondhand.viewModel.LocationViewModel;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class BodyPaymentFragment extends Fragment {
    private static final String TAG = "BodyPaymentFragment";
    private FragmentBodyPaymentBinding binding;
    private LocationViewModel locationViewModel;

    // Keys cho arguments
    private static final String ARG_PRODUCT_ID = "arg_product_id";
    private static final String ARG_PRODUCT_NAME = "arg_product_name";
    private static final String ARG_QUANTITY = "arg_quantity";
    private static final String ARG_UNIT_PRICE = "arg_unit_price";
    private static final String ARG_TOTAL_PRICE = "arg_total_price";
    private static final String ARG_IMAGE_URL = "arg_image_url";
    private static final String ARG_SHOP_NAME = "arg_shop_name";

    private int productId;
    private String productName;
    private int quantity;
    private int unitPrice;
    private int totalPrice;
    private String imageUrl;
    private String shopName;

    public static BodyPaymentFragment newInstance(int productId, String productName, int quantity, int unitPrice, int totalPrice, String imageUrl, String shopName) {
        BodyPaymentFragment fragment = new BodyPaymentFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PRODUCT_ID, productId);
        args.putString(ARG_PRODUCT_NAME, productName);
        args.putInt(ARG_QUANTITY, quantity);
        args.putInt(ARG_UNIT_PRICE, unitPrice);
        args.putInt(ARG_TOTAL_PRICE, totalPrice);
        args.putString(ARG_IMAGE_URL, imageUrl);
        args.putString(ARG_SHOP_NAME, shopName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productId = getArguments().getInt(ARG_PRODUCT_ID, -1);
            productName = getArguments().getString(ARG_PRODUCT_NAME, "N/A");
            quantity = getArguments().getInt(ARG_QUANTITY, 0);
            unitPrice = getArguments().getInt(ARG_UNIT_PRICE, 0);
            totalPrice = getArguments().getInt(ARG_TOTAL_PRICE, 0);
            imageUrl = getArguments().getString(ARG_IMAGE_URL, "");
            shopName = getArguments().getString(ARG_SHOP_NAME, "lỗi rồi");
            Log.d(TAG, "onCreate: Dữ liệu nhận được - ID: " + productId + ", Tên: " + productName + ", SL: " + quantity + ", Đơn giá: " + unitPrice + ", Tổng: " + totalPrice);
        } else {
            Log.e(TAG, "onCreate: Không có arguments nào được truyền cho BodyPaymentFragment.");
        }
        locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBodyPaymentBinding.inflate(inflater, container, false);
        locationViewModel.loadAddresses();
        locationViewModel.getSelectedAddress();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.layoutPaymentBody.requestFocus();

        // Observe địa chỉ được chọn
        locationViewModel.getSelectedAddress().observe(getViewLifecycleOwner(), address -> {
            if (address != null) {
                binding.txtAddress.setText(address.getAddressName());
            } else {
                // Nếu không có địa chỉ được chọn, hiển thị địa chỉ mặc định
                List<DeliveryAddressResponse> addresses = locationViewModel.getAddresses().getValue();
                if (addresses != null && !addresses.isEmpty()) {
                    for (DeliveryAddressResponse addr : addresses) {
                        if (addr.getDefaultAddress() != 0) {
                            binding.txtAddress.setText(addr.getAddressName());
                            locationViewModel.setSelectedAddress(addr);
                            break;
                        }
                    }
                } else {
                    binding.txtAddress.setText("Chọn địa chỉ giao hàng");
                }
            }
        });

        // Observe lỗi
        locationViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        if (productId == -1 || quantity == 0) {
            // Xử lý trường hợp không có dữ liệu sản phẩm hợp lệ
            Log.e(TAG, "onViewCreated: Dữ liệu sản phẩm không hợp lệ để hiển thị.");

            // Có thể ẩn các view liên quan đến sản phẩm hoặc hiển thị thông báo lỗi
            binding.txtShopName.setText("Lỗi tải sản phẩm");
            binding.imgProduct1.setVisibility(View.GONE);
            binding.txtProductName1.setText("Không có sản phẩm");
            binding.txtProductQuantity1.setVisibility(View.GONE);
            binding.txtProductPrice1.setVisibility(View.GONE);
            // Ẩn luôn layout sản phẩm thứ 2 vì đây là "Mua ngay" cho 1 sản phẩm
            view.findViewById(R.id.imgProduct).setVisibility(View.GONE);
            view.findViewById(R.id.txtProductName).setVisibility(View.GONE);
            view.findViewById(R.id.txtProductVariation).setVisibility(View.GONE);
            view.findViewById(R.id.txtProductQuantity).setVisibility(View.GONE);
            view.findViewById(R.id.txtProductPrice).setVisibility(View.GONE);

        } else {
            // Hiển thị thông tin sản phẩm đã nhận được
            binding.txtShopName.setText(shopName);

            if (getContext() != null && imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(requireContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.img) // Thay bằng placeholder của bạn
                        .error(R.drawable.img)   // Thay bằng ảnh lỗi của bạn
                        .into(binding.imgProduct1);
            } else {
                binding.imgProduct1.setImageResource(R.drawable.img);
            }

            binding.txtProductName1.setText(productName);
            binding.txtProductQuantity1.setText("x" + quantity);
            binding.txtProductPrice1.setText(formatCurrency(unitPrice * quantity) + " ₫"); // Hiển thị tổng tiền của item này

            // Ẩn layout sản phẩm thứ 2 vì đây là "Mua ngay" cho 1 sản phẩm
            // (Các ID này là từ layout XML bạn cung cấp)
            view.findViewById(R.id.imgProduct).setVisibility(View.GONE);
            view.findViewById(R.id.txtProductName).setVisibility(View.GONE);
            view.findViewById(R.id.txtProductVariation).setVisibility(View.GONE);
            view.findViewById(R.id.txtProductQuantity).setVisibility(View.GONE);
            view.findViewById(R.id.txtProductPrice).setVisibility(View.GONE);
            if (view.findViewById(R.id.btnViewMoreProducts) != null) { // Kiểm tra null nếu ID này có thể không tồn tại
                view.findViewById(R.id.btnViewMoreProducts).setVisibility(View.GONE);
            }


            // Cập nhật tóm tắt đơn hàng
            // Giả sử phí vận chuyển là cố định hoặc sẽ được tính sau
            int shippingFee = 30000; // Ví dụ
            binding.txtTotalProduct.setText("Tổng tiền sản phẩm: " + formatCurrency(totalPrice) + " ₫");
            binding.txtShipping.setText("Phí vận chuyển: " + formatCurrency(shippingFee) + " ₫");
            binding.txtOrderTotal.setText("Tổng tiền đơn hàng: " + formatCurrency(totalPrice + shippingFee) + " ₫");
        }


        binding.btnChooseLocation.setOnClickListener(v -> {
            BottomSheetAddLocationFragment bottomSheet = new BottomSheetAddLocationFragment();
            bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
        });
    }

    private String formatCurrency(int number) {
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(number);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
