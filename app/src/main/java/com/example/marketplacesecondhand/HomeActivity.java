package com.example.marketplacesecondhand;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.marketplacesecondhand.API.DatabaseHandler;
import com.example.marketplacesecondhand.adapter.ViewPagerAdapter;
import com.example.marketplacesecondhand.databinding.ActivityHomeBinding;
import com.example.marketplacesecondhand.models.UserLoginInfo;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private ViewPagerAdapter viewPagerAdapter;
    View fragmentHeader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//        );
        // Gắn HomeFragment vào container
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.content_frame, new HomeFragment())
//                .commit();

        DatabaseHandler dbHandler = new DatabaseHandler(this);
        UserLoginInfo userInfo = dbHandler.getLoginInfoSQLite();
        if (userInfo != null) {
            RetrofitClient.currentToken = userInfo.token;
        }

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        binding.contentFrame.setAdapter(viewPagerAdapter);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_view);

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                View view = bottomNav.findViewById(item.getItemId());

                int selectedColor = Color.parseColor("#fac63d");
                int defaultColor = Color.parseColor("#00FFFFFF");

                // Duyệt tất cả item và đặt màu mặc định
                for (int i = 0; i < bottomNav.getMenu().size(); i++) {
                    bottomNav.getMenu().getItem(i).getIcon().setTint(defaultColor);
                }

                // Đổi màu icon của item được chọn
                item.getIcon().setTint(selectedColor);
                onNavigationItemSelectedCustom(item);
        //        drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        fragmentHeader = findViewById(R.id.fragment_header);

        binding.contentFrame.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                // Hiện lại ViewPager và ẩn container fragment
                findViewById(R.id.content_frame).setVisibility(View.VISIBLE);
                findViewById(R.id.fragment_container).setVisibility(View.GONE);
                
                // Xóa tất cả fragment trong back stack
                getSupportFragmentManager().popBackStack(null, getSupportFragmentManager().POP_BACK_STACK_INCLUSIVE);

                // Reset fragment trong container
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (fragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .remove(fragment)
                            .commit();
                }

                if (position == 0) {
                    bottomNav.setSelectedItemId(R.id.nav_home);
                    findViewById(R.id.header_fragment).setVisibility(View.VISIBLE);
                }
                else if (position == 1) {
                    bottomNav.setSelectedItemId(R.id.nav_search);
                    findViewById(R.id.header_fragment).setVisibility(View.VISIBLE);
                }
                else if (position == 2) {
                    bottomNav.setSelectedItemId(R.id.nav_store);
                    findViewById(R.id.header_fragment).setVisibility(View.GONE);
                }
                else if (position == 3) {
                    bottomNav.setSelectedItemId(R.id.nav_order);
                    findViewById(R.id.header_fragment).setVisibility(View.GONE);
                }
                else if (position == 4) {
                    bottomNav.setSelectedItemId(R.id.nav_profile);
                    findViewById(R.id.header_fragment).setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        // Xử lý chuyển tab từ intent
        int selectedTab = getIntent().getIntExtra("SELECTED_TAB", 0);
        if (selectedTab >= 0 && selectedTab < 5) {
            binding.contentFrame.setCurrentItem(selectedTab, false);
            MenuItem item = bottomNav.getMenu().getItem(selectedTab);
            item.getIcon().setTint(Color.parseColor("#fac63d"));
            onNavigationItemSelectedCustom(item);
        }
    }

    @Override
    public void onBackPressed() {
        // Kiểm tra nếu đang ở trong fragment container
        if (findViewById(R.id.fragment_container).getVisibility() == View.VISIBLE) {
            // Ẩn header
            findViewById(R.id.header_fragment).setVisibility(View.GONE);
            // Hiện lại ViewPager
            findViewById(R.id.content_frame).setVisibility(View.VISIBLE);
            // Ẩn container fragment
            findViewById(R.id.fragment_container).setVisibility(View.GONE);
            // Xóa fragment trong container
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .remove(fragment)
                        .commit();
            }
        } else {
            super.onBackPressed();
        }
    }

    private void onNavigationItemSelectedCustom(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            binding.contentFrame.setCurrentItem(0, true);
        }
        else if (id == R.id.nav_search) {
            binding.contentFrame.setCurrentItem(1, true);
        }
        else if (id == R.id.nav_store) {
            binding.contentFrame.setCurrentItem(2, true);
        }
        else if (id == R.id.nav_order) {
            binding.contentFrame.setCurrentItem(3, true);
        }
        else if (id == R.id.nav_profile) {
            binding.contentFrame.setCurrentItem(4, true);
        }
    }

//    private void loadFragment(Fragment fragment) {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.content_frame, fragment);
//        transaction.commit();
//    }
}
