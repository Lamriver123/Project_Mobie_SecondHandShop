package com.example.marketplacesecondhand.adapter.order;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.marketplacesecondhand.fragment.order.OrderStatusFragment;

public class OrderPagerAdapter extends FragmentStateAdapter {

    public OrderPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return OrderStatusFragment.newInstance("Chờ xác nhận"); // Chờ xác nhận
            case 1:
                return OrderStatusFragment.newInstance("Đang giao"); // Đang giao
            case 2:
                return OrderStatusFragment.newInstance("Đã giao"); // Đã giao
            case 3:
                return OrderStatusFragment.newInstance("Đã hủy"); // Đã hủy
            default:
                return OrderStatusFragment.newInstance("Chờ xác nhận");
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}




