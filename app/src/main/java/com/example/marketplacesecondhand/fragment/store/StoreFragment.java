package com.example.marketplacesecondhand.fragment.store;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketplacesecondhand.API.APIService;
import com.example.marketplacesecondhand.API.DatabaseHandler;
import com.example.marketplacesecondhand.service.RetrofitClient;
import com.example.marketplacesecondhand.adapter.store.ShopAdapter;
import com.example.marketplacesecondhand.databinding.FragmentStoreBinding;
import com.example.marketplacesecondhand.models.UserLoginInfo;
import com.example.marketplacesecondhand.viewModel.ShopViewModel;

import java.util.ArrayList;

public class StoreFragment extends Fragment {

    private FragmentStoreBinding binding;
    private ShopViewModel shopViewModel;
    private ShopAdapter shopAdapter;
    private APIService apiService;
    public StoreFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentStoreBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        shopViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ShopViewModel(apiService);
            }
        }).get(ShopViewModel.class);

        DatabaseHandler dbHandler = new DatabaseHandler(getContext());
        UserLoginInfo userInfo = dbHandler.getLoginInfoSQLite();

        // Setup RecyclerView
        RecyclerView recyclerViewShops = binding.recyclerViewShops;
        recyclerViewShops.setLayoutManager(new LinearLayoutManager(getContext()));

        if (userInfo == null) {
            shopAdapter = new ShopAdapter(new ArrayList<>(), 0);
        }
        else {
            shopAdapter = new ShopAdapter(new ArrayList<>(), userInfo.getUserId());
        }

        recyclerViewShops.setAdapter(shopAdapter);

        // Set up follow listener
        shopAdapter.setOnFollowListener((shop, isFollowing) -> {
            shopViewModel.toggleFollow(shop.getId());
        });

        // Observe LiveData
        shopViewModel.getShops().observe(getViewLifecycleOwner(), shops -> {
            shopAdapter.updateShops(shops);
        });

        shopViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        shopViewModel.getFollowStatus().observe(getViewLifecycleOwner(), isFollowing -> {
            // Handle follow status change if needed
        });

        // Load shops
        shopViewModel.loadShops();

    }

    @Override
    public void onResume() {
        super.onResume();
        shopViewModel.loadShops();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
