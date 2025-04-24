package com.example.marketplacesecondhand.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;

    public ProfileFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        
        // Tìm TextView Edit Profile và thêm sự kiện click
        TextView editProfileOption = binding.optionsContainer.findViewById(R.id.editProfileOption);
        editProfileOption.setOnClickListener(v -> {
            // Ẩn ViewPager và header, hiện container fragment
            requireActivity().findViewById(R.id.content_frame).setVisibility(View.GONE);
            requireActivity().findViewById(R.id.header_fragment).setVisibility(View.GONE);
            requireActivity().findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
            
            // Tạo instance mới của EditProfileFragment
            EditProfileFragment editProfileFragment = new EditProfileFragment();
            
            // Bắt đầu transaction chuyển fragment
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            
            // Thay thế fragment hiện tại bằng EditProfileFragment và thêm vào back stack
            transaction.replace(R.id.fragment_container, editProfileFragment);
            transaction.addToBackStack(null);
            
            // Thực hiện transaction
            transaction.commit();
        });

        // Tìm TextView Terms and Conditions và thêm sự kiện click
        TextView termsConditions = binding.optionsContainer.findViewById(R.id.termConditionsOption);
        termsConditions.setOnClickListener(v -> {
            // Ẩn ViewPager và header, hiện container fragment
            requireActivity().findViewById(R.id.content_frame).setVisibility(View.GONE);
            requireActivity().findViewById(R.id.header_fragment).setVisibility(View.GONE);
            requireActivity().findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
            
            // Tạo instance mới của TermsConditionsFragment
            TermsConditionsFragment termsConditionsFragment = new TermsConditionsFragment();
            
            // Bắt đầu transaction chuyển fragment
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            
            // Thay thế fragment hiện tại bằng TermsConditionsFragment và thêm vào back stack
            transaction.replace(R.id.fragment_container, termsConditionsFragment);
            transaction.addToBackStack(null);
            
            // Thực hiện transaction
            transaction.commit();
        });
        
        return binding.getRoot();
    }
}
