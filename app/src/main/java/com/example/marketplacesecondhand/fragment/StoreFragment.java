package com.example.marketplacesecondhand.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager; // Import LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView; // Import RecyclerView

import com.example.marketplacesecondhand.R; // Import R class for resources
import com.example.marketplacesecondhand.adapter.ShopAdapter; // Import ShopAdapter
import com.example.marketplacesecondhand.databinding.FragmentStoreBinding; // Import FragmentStoreBinding
import com.example.marketplacesecondhand.models.Shop; // Import Shop model

import java.util.ArrayList; // Import ArrayList
import java.util.Arrays; // Import Arrays
import java.util.List; // Import List

public class StoreFragment extends Fragment {

    private FragmentStoreBinding binding; // Biến binding cho layout của Fragment

    public StoreFragment() {
        // Constructor rỗng là bắt buộc đối với Fragment
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Sử dụng View Binding để inflate layout
        binding = FragmentStoreBinding.inflate(inflater, container, false);
        return binding.getRoot(); // Trả về root view từ binding
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Chuẩn bị dữ liệu mẫu cho danh sách Shop
        // Trong ứng dụng thực tế, bạn sẽ lấy dữ liệu này từ API, database, v.v.
        List<Shop> shopList = new ArrayList<>();
        shopList.add(new Shop(
                1,
                "https://res.cloudinary.com/dk7ypst5k/image/upload/v1744876837/uhczotgu6u9e4sttvsqq.jpg",
                "Top Điện Gia Dụng",
                4.9f,
                "5 đánh giá",
                "Theo dõi ngay để nhận hàng ngàn ưu đãi",
                Arrays.asList(
                        "https://giaiphaphutam.com/upload/product/dieu-hoa-di-dong-fujie-mpac921102.jpg", // URL ảnh sản phẩm 1
                        "https://dienmaynhapkhau.net/vnt_upload/product/07_2020/May-lanh-daikin-ftf25uv1v.jpg" // URL ảnh sản phẩm 2
                )
        ));
        shopList.add(new Shop(
                2,
                "https://res.cloudinary.com/dk7ypst5k/image/upload/v1744790076/kx42tkck3gw1fts7qtal.jpg",
                "Quần áo",
                4.9f,
                "8k đánh giá",
                "Theo dõi ngay để nhận hàng ngàn ưu đãi",
                Arrays.asList(
                        "https://res.cloudinary.com/dk7ypst5k/image/upload/v1744336770/cld-sample-3.jpg" // URL ảnh sản phẩm 1

                )
        ));
        shopList.add(new Shop(
                3,
                "https://res.cloudinary.com/dk7ypst5k/image/upload/v1744528622/axec7ap4amatxrulnvho.jpg",
                "Top Điện Gia Dụng",
                4f,
                "545,8k đánh giá",
                "Theo dõi ngay để nhận hàng ngàn ưu đãi",
                Arrays.asList(
                        "https://res.cloudinary.com/dk7ypst5k/image/upload/v1744336770/cld-sample-3.jpg" // URL ảnh sản phẩm 1
                        // Thêm các URL ảnh khác nếu cần
                )
        ));

        RecyclerView recyclerViewShops = binding.recyclerViewShops;
        recyclerViewShops.setLayoutManager(new LinearLayoutManager(getContext()));
        ShopAdapter shopAdapter = new ShopAdapter(shopList);
        recyclerViewShops.setAdapter(shopAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
