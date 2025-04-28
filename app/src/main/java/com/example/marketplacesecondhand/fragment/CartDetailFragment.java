package com.example.marketplacesecondhand.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.adapter.CartShopAdapter;
import com.example.marketplacesecondhand.databinding.FragmentCartDetailBinding;
import com.example.marketplacesecondhand.dto.response.ProductResponse;
import com.example.marketplacesecondhand.models.CartProduct;
import com.example.marketplacesecondhand.models.CartShop;
import com.example.marketplacesecondhand.models.Product;
import com.example.marketplacesecondhand.models.User;

import java.util.ArrayList;
import java.util.List;

public class CartDetailFragment extends Fragment {
    private FragmentCartDetailBinding binding;
    private CartShopAdapter cartShopAdapter;
    private List<CartShop> cartShopList;

    public CartDetailFragment() {
        super(R.layout.fragment_cart_detail);
    }

    @Override
    public void onViewCreated(@NonNull android.view.View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentCartDetailBinding.bind(view);

        setupRecyclerView();
        loadSampleData();
    }

    private void setupRecyclerView() {
        cartShopList = new ArrayList<>();
        cartShopAdapter = new CartShopAdapter(requireContext(), cartShopList);
        binding.recyclerViewCart.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewCart.setAdapter(cartShopAdapter);
    }

    private void loadSampleData() {
        // Dữ liệu mẫu
        List<CartProduct> productList1 = new ArrayList<>();

        ProductResponse product1 = new ProductResponse(1,"Áo Thun Basic1","230000","160000");
        ProductResponse product2 = new ProductResponse(1,"Áo Thun Basic2","240000","170000");
        ProductResponse product3 = new ProductResponse(1,"Áo Thun Basic3","250000","180000");


        productList1.add(new CartProduct(product1,  1, false));
        productList1.add(new CartProduct(product2, 2, false));

        List<CartProduct> productList2 = new ArrayList<>();
        productList2.add(new CartProduct(product3, 1, false));

        User user1 = new User();
        user1.setFullName("Shop Hipmade");

        User user2 = new User();
        user2.setFullName("Shop Delifitness");
        cartShopList.add(new CartShop(user1, false, productList1));
        cartShopList.add(new CartShop(user2, false, productList2));

        cartShopAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
