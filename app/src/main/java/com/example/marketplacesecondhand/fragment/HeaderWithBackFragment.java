package com.example.marketplacesecondhand.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.databinding.FragmentHeaderWithBackBinding;

public class HeaderWithBackFragment extends Fragment {
    private FragmentHeaderWithBackBinding binding;


    public HeaderWithBackFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHeaderWithBackBinding.inflate(inflater, container, false);

        binding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().onBackPressed();
            }
        });

        // Lấy categoryName từ arguments
        if (getArguments() != null) {
            String categoryName = getArguments().getString("category_name", "");
            binding.editTextSearch.setHint(categoryName);
        }
        return binding.getRoot();
    }

}
