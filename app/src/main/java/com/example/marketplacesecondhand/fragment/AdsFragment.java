package com.example.marketplacesecondhand.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.adapter.SliderAdapter;
import com.example.marketplacesecondhand.databinding.FragmentAdsBinding;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

public class AdsFragment extends Fragment {
    private FragmentAdsBinding binding;
    private SliderView sliderView;
    private ArrayList<Integer> arrayList;
    private SliderAdapter sliderAdapter;

    public AdsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAdsBinding.inflate(inflater, container, false);
        arrayList = new ArrayList<>();
        arrayList.add(R.drawable.bn1);
        arrayList.add(R.drawable.bn2);
        arrayList.add(R.drawable.bn3);
        arrayList.add(R.drawable.bn4);

        sliderAdapter = new SliderAdapter(getContext(), arrayList);
        binding.imageSlider.setSliderAdapter(sliderAdapter);

        binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM);
        binding.imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        binding.imageSlider.setIndicatorSelectedColor(getResources().getColor(R.color.red));
        binding.imageSlider.setIndicatorUnselectedColor(Color.GRAY);
        binding.imageSlider.setScrollTimeInSec(5);
        binding.imageSlider.startAutoCycle();
        return binding.getRoot();
    }
}
