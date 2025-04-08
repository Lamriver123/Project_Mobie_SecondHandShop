package com.example.marketplacesecondhand.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.adapter.SliderAdapter;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private SliderView sliderView;
    private ArrayList<Integer> arrayList;
    private SliderAdapter sliderAdapter;

    public HomeFragment() {
        super(R.layout.fragment_home); // gắn layout fragment_home.xml
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sliderView = view.findViewById(R.id.imageSlider);
        arrayList = new ArrayList<>();

        // Thêm ảnh vào danh sách
        arrayList.add(R.drawable.coffee);
        arrayList.add(R.drawable.companypizza);
        arrayList.add(R.drawable.quancao);

        // Gắn adapter
        sliderAdapter = new SliderAdapter(requireContext(), arrayList);
        sliderView.setSliderAdapter(sliderAdapter);

        // Cấu hình hiệu ứng
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        sliderView.setIndicatorSelectedColor(getResources().getColor(R.color.red));
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(5);
        sliderView.startAutoCycle();
    }
}
