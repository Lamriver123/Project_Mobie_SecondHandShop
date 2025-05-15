package com.example.marketplacesecondhand.fragment.cart;

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

import com.example.marketplacesecondhand.databinding.FragmentHeaderCartBinding;
import com.example.marketplacesecondhand.models.CartShop;
import com.example.marketplacesecondhand.viewModel.LocationViewModel;

public class HeaderCartFragment extends Fragment {
    private FragmentHeaderCartBinding binding;
    private static final String TAG = "HeaderCartFragment";
    private LocationViewModel locationViewModel;

    public HeaderCartFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHeaderCartBinding.inflate(inflater, container, false);

        int count = 0;
        for (CartShop cartShop : CartDetailFragment.cartShopList) {
            count += cartShop.getProducts().size();
        }

        binding.textViewCartTitle.setText("Giỏ hàng (" + count + ")");
        setupObservers();
        loadDefaultAddress();

        binding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().onBackPressed();
            }
        });

        return binding.getRoot();
    }

    private void setupObservers() {
        locationViewModel.getSelectedAddress().observe(getViewLifecycleOwner(), address -> {
            if (address != null) {
                binding.textViewAddress.setText(address.getAddressName());
            } else {
                binding.textViewAddress.setText("Địa chỉ giao hàng: Chưa chọn");
            }
        });

        locationViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDefaultAddress() {
        locationViewModel.loadAddresses();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
