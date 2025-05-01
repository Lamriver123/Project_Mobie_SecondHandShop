package com.example.marketplacesecondhand.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.marketplacesecondhand.fragment.OrderStatusFragment;

public class OrderPagerAdapter extends FragmentStateAdapter {
    public OrderPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return OrderStatusFragment.newInstance("Chờ xác nhận");
            case 1: return OrderStatusFragment.newInstance("Đang giao");
            case 2: return OrderStatusFragment.newInstance("Đã giao");
            case 3: return OrderStatusFragment.newInstance("Đã hủy");
            default: return OrderStatusFragment.newInstance("Chờ giao hàng");
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}



