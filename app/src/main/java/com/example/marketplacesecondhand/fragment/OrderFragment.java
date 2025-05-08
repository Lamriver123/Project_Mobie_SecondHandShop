package com.example.marketplacesecondhand.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.marketplacesecondhand.adapter.order.OrderPagerAdapter;
import com.example.marketplacesecondhand.databinding.FragmentOrderBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class OrderFragment extends Fragment {
    private FragmentOrderBinding binding;


    public OrderFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentOrderBinding.inflate(inflater, container, false);

        // Gắn adapter
        OrderPagerAdapter adapter = new OrderPagerAdapter(requireActivity());
        binding.viewPagerOrder.setAdapter(adapter);

        // Gắn ViewPager2 vào TabLayout
        new TabLayoutMediator(binding.tabLayoutOrder, binding.viewPagerOrder,
                (tab, position) -> {
                    switch (position) {
                        case 0: tab.setText("Chờ xác nhận"); break;
                        case 1: tab.setText("Đang giao"); break;
                        case 2: tab.setText("Đã giao"); break;
                        case 3: tab.setText("Đã hủy"); break;
                    }
                }).attach();

        return binding.getRoot();
    }

}
