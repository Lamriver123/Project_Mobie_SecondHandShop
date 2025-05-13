package com.example.marketplacesecondhand.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.marketplacesecondhand.fragment.HomeFragment;
import com.example.marketplacesecondhand.fragment.OrderFragment;
import com.example.marketplacesecondhand.fragment.ProfileFragment;
import com.example.marketplacesecondhand.fragment.SearchFragment;
import com.example.marketplacesecondhand.fragment.store.StoreFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new SearchFragment();
            case 2:
                return new StoreFragment();
            case 3:
                return new OrderFragment();
            case 4:
                return new ProfileFragment();
//            case 5:
//                return new EditProfileFragment();
//            case 6:
//                return new TermsConditionsFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getCount() {
        return 5;
    }
}
