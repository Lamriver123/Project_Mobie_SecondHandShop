package com.example.marketplacesecondhand.fragment.review;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ReviewPagerAdapter extends FragmentStateAdapter {
    public ReviewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // 0: tất cả, 1: mình đánh giá, 2: người khác đánh giá mình
        return ReviewListFragment.newInstance(position);
    }

    @Override
    public int getItemCount() {
        return 3;
    }
} 