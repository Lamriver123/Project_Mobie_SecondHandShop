package com.example.marketplacesecondhand.fragment.productDetail;

import android.content.Intent;
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

import com.example.marketplacesecondhand.API.DatabaseHandler;
import com.example.marketplacesecondhand.LoginActivity;
import com.example.marketplacesecondhand.databinding.FragmentFooterProductDetailBinding;
import com.example.marketplacesecondhand.dto.response.ProductResponse;
import com.example.marketplacesecondhand.models.UserLoginInfo;
import com.example.marketplacesecondhand.viewModel.ProductDetailViewModel;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class FooterProductDetailFragment extends Fragment {
    private static final String TAG = "FooterProductDetail";
    private FragmentFooterProductDetailBinding binding;
    private ProductDetailViewModel productDetailViewModel;

    public FooterProductDetailFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() != null) {
            productDetailViewModel = new ViewModelProvider(requireActivity()).get(ProductDetailViewModel.class);
        }
        else {
            Log.e(TAG, "Không thể lấy ProductDetailViewModel vì getActivity() là null.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFooterProductDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (productDetailViewModel == null) {
            binding.buttonBuyNow.setEnabled(false);
            binding.buttonAddToCart.setEnabled(false);
            Log.e(TAG, "ProductDetailViewModel is null in onViewCreated. Buttons disabled.");
            return;
        }

        productDetailViewModel.getProductDetail().observe(getViewLifecycleOwner(), product -> {
            if (binding == null)
                return;

            if (product != null && product.getQuantity() > 0) {
                binding.buttonBuyNow.setEnabled(true);
                binding.buttonAddToCart.setEnabled(true);
            }
            else {
                binding.buttonBuyNow.setEnabled(false);
                binding.buttonAddToCart.setEnabled(false);
                if (product != null && product.getQuantity() <= 0) {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Sản phẩm đã hết hàng", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (product != null) {
                    if (getContext() != null) Toast.makeText(getContext(), "Thông tin số lượng sản phẩm không có sẵn", Toast.LENGTH_SHORT).show();
                }
            }
        });



        binding.buttonBuyNow.setOnClickListener(v -> {
            DatabaseHandler db = new DatabaseHandler(getContext());
            UserLoginInfo userLoginInfo = db.getLoginInfoSQLite();
            if (userLoginInfo != null) {
                ProductResponse product = productDetailViewModel.getProductDetail().getValue();
                if (product != null) {
                    if (product.getQuantity() <= 0) {
                        if(getContext() != null) Toast.makeText(getContext(), "Sản phẩm đã hết hàng.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (product.getCurrentPrice() == null) {
                        if (getContext()!=null) Toast.makeText(getContext(), "Thông tin sản phẩm không đầy đủ.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String firstImageUrl = (product.getCurrentImages() != null && !product.getCurrentImages().isEmpty()) ? product.getCurrentImages().get(0) : "";
                    int price = 0;
                    try {
                        String cleanPriceString = product.getCurrentPrice().replaceAll("[^\\d]", "");
                        if (!cleanPriceString.isEmpty()) {
                            price = Integer.parseInt(cleanPriceString);
                        } else {
                            if(getContext()!=null) Toast.makeText(getContext(), "Lỗi định dạng giá sản phẩm.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Lỗi parse giá sản phẩm khi mua ngay: " + product.getCurrentPrice(), e);
                        if(getContext()!=null) Toast.makeText(getContext(), "Lỗi định dạng giá sản phẩm.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    BottomSheetBuyNowFragment bottomSheet = BottomSheetBuyNowFragment.newInstance(
                            product.getProductId(),
                            product.getProductName(),
                            price,
                            product.getQuantity(),
                            firstImageUrl
                    );
                    bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());

                }
                else {
                    if(getContext() != null){
                        Toast.makeText(getContext(), "Dữ liệu chi tiết sản phẩm chưa sẵn sàng.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        binding.buttonAddToCart.setOnClickListener(v -> {
            DatabaseHandler db = new DatabaseHandler(getContext());
            UserLoginInfo userLoginInfo = db.getLoginInfoSQLite();
            if (userLoginInfo != null) {
                ProductResponse product = productDetailViewModel.getProductDetail().getValue();
                if (product != null) {
                    if (product.getQuantity() <= 0) {
                        if(getContext()!=null) {
                            Toast.makeText(getContext(), "Sản phẩm đã hết hàng.", Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }
                    if (product.getCurrentPrice() == null) {
                        if (getContext()!=null) Toast.makeText(getContext(), "Thông tin sản phẩm không đầy đủ.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String firstImageUrl = (product.getCurrentImages() != null && !product.getCurrentImages().isEmpty()) ? product.getCurrentImages().get(0) : "";
                    int price = 0;
                    try {
                        String cleanPriceString = product.getCurrentPrice().replaceAll("[^\\d]", "");
                        if (!cleanPriceString.isEmpty()) {
                            price = Integer.parseInt(cleanPriceString);
                        } else {
                            if(getContext()!=null) Toast.makeText(getContext(), "Lỗi định dạng giá sản phẩm.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Lỗi parse giá sản phẩm khi mua ngay: " + product.getCurrentPrice(), e);
                        if(getContext()!=null) Toast.makeText(getContext(), "Lỗi định dạng giá sản phẩm.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    BottomSheetAddToCartFragment bottomSheet = BottomSheetAddToCartFragment.newInstance(
                            product.getProductId(),
                            product.getProductName(),
                            price,
                            product.getQuantity(),
                            firstImageUrl
                    );
                    bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());

                }
                else {
                    if(getContext() != null){
                        Toast.makeText(getContext(), "Dữ liệu chi tiết sản phẩm chưa sẵn sàng.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
