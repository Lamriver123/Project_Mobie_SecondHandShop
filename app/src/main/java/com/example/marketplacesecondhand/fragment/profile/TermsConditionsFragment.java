package com.example.marketplacesecondhand.fragment.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.marketplacesecondhand.R;

public class TermsConditionsFragment extends Fragment {
    public TermsConditionsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_terms_conditions, container, false);

        ImageView btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ẩn header
                requireActivity().findViewById(R.id.header_fragment).setVisibility(View.GONE);
                // Hiện lại ViewPager
                requireActivity().findViewById(R.id.content_frame).setVisibility(View.VISIBLE);
                // Ẩn container fragment
                requireActivity().findViewById(R.id.fragment_container).setVisibility(View.GONE);
                // Xóa fragment hiện tại
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .remove(TermsConditionsFragment.this)
                        .commit();
            }
        });

        return view;
    }
}
