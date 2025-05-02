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
            case 0:
                return OrderStatusFragment.newInstance("CHO_XAC_NHAN");
            case 1:
                return OrderStatusFragment.newInstance("DANG_GIAO");
            case 2:
                return OrderStatusFragment.newInstance("DA_GIAO");
            case 3:
                return OrderStatusFragment.newInstance("DA_HUY");
            default:
                return OrderStatusFragment.newInstance("CHO_XAC_NHAN");
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}




