package com.example.marketplacesecondhand.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.marketplacesecondhand.API.DatabaseHandler;
import com.example.marketplacesecondhand.LoginActivity;
import com.example.marketplacesecondhand.adapter.order.OrderPagerAdapter;
import com.example.marketplacesecondhand.databinding.FragmentOrderBinding;
import com.example.marketplacesecondhand.viewModel.OrderViewModel;
import com.google.android.material.tabs.TabLayoutMediator;

public class OrderFragment extends Fragment {
    private FragmentOrderBinding binding;
    private boolean uiInitialized = false;
    private OrderViewModel orderViewModel;

    public OrderFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentOrderBinding.inflate(inflater, container, false);
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        // Gắn adapter
//        OrderPagerAdapter adapter = new OrderPagerAdapter(requireActivity());
//        binding.viewPagerOrder.setAdapter(adapter);
//
//        // Gắn ViewPager2 vào TabLayout
//        new TabLayoutMediator(binding.tabLayoutOrder, binding.viewPagerOrder,
//                (tab, position) -> {
//                    switch (position) {
//                        case 0: tab.setText("Chờ xác nhận"); break;
//                        case 1: tab.setText("Đang giao"); break;
//                        case 2: tab.setText("Đã giao"); break;
//                        case 3: tab.setText("Đã hủy"); break;
//                    }
//                }).attach();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getContext() == null) {
            return;
        }

        DatabaseHandler dbHandler = new DatabaseHandler(requireContext());
        if (dbHandler.getLoginInfoSQLite() == null) {
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            uiInitialized = false;
            return;
        }

        if (!uiInitialized && binding != null) {
            setupUI();
            uiInitialized = true;
        }
    }
    private void setupUI() {
        // Đảm bảo activity có sẵn cho adapter
        if (getActivity() == null || binding == null) {
            return;
        }

        OrderPagerAdapter adapter = new OrderPagerAdapter(requireActivity());
        binding.viewPagerOrder.setAdapter(adapter);

        new TabLayoutMediator(binding.tabLayoutOrder, binding.viewPagerOrder,
                (tab, position) -> {
                    switch (position) {
                        case 0: tab.setText("Chờ xác nhận"); break;
                        case 1: tab.setText("Đang giao"); break;
                        case 2: tab.setText("Đã giao"); break;
                        case 3: tab.setText("Đã hủy"); break;
                        default: tab.setText("Tab " + (position + 1)); break;
                    }
                }).attach();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        uiInitialized = false;
    }

}
