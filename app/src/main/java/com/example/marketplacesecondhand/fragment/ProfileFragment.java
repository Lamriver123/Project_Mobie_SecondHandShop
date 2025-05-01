package com.example.marketplacesecondhand.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.marketplacesecondhand.API.DatabaseHandler;
import com.example.marketplacesecondhand.R;
import com.example.marketplacesecondhand.databinding.FragmentProfileBinding;
import com.example.marketplacesecondhand.models.UserLoginInfo;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;

    public ProfileFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        TextView tvLogout = binding.optionsContainer.findViewById(R.id.logout);

        DatabaseHandler dbHandler = new DatabaseHandler(getContext());
        UserLoginInfo userInfo = dbHandler.getLoginInfoSQLite();
        if (userInfo == null) {
            tvLogout.setVisibility(View.GONE);
        }
        else {
            tvLogout.setVisibility(View.VISIBLE);
            tvLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dbHandler.clearAllLoginData();
                    tvLogout.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                    //binding.username.setText("Duy");
                    // Load lại chính Fragment này
//                    requireActivity().getSupportFragmentManager()
//                            .beginTransaction()
//                            .replace(R.id.content_frame, new ProfileFragment()) // Thay thế fragment
//                            .commit();
                }
            });
        }

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
