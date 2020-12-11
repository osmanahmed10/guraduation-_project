package com.example.graduation_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class home extends AppCompatActivity {

    private miniUser miniUserInfo;
    private String userId;
    DatabaseReference myRef;

    private ProgressBar progressBar;
    private ViewPager Pager;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        progressBar = findViewById(R.id.loading1);
        miniUserInfo = new miniUser();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myRef = FirebaseDatabase.getInstance().getReference("All Persons").child(userId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                miniUserInfo = dataSnapshot.getValue(miniUser.class);
                buildView();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(home.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buildView() {
        Pager = findViewById(R.id.view_pager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (miniUserInfo.getRole() == 0) {
            bottomNavigationView.inflateMenu(R.menu.bottom_navigation_bar_menu_customer);
        } else if (miniUserInfo.getRole() == 1){
            bottomNavigationView.inflateMenu(R.menu.bottom_navigation_bar_menu_worker);
        }

        setupViewPager(Pager);

        setBottomNavigationView();

        setPager();

        bottomNavigationView.setSelectedItemId(R.id.home);
        progressBar.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    private void setupViewPager(ViewPager viewPager) {
        List<Fragment> list = new ArrayList<>();
        if (miniUserInfo.getRole() == 0) {
            list.add(new profileFragment());
            list.add(new chatListFragment());
            list.add(new homeCustomerFragment());
            list.add(new ordersFragment());
            list.add(new settingsFragment());
        } else if (miniUserInfo.getRole() == 1){
            list.add(new profileFragment());
            list.add(new chatListFragment());
            list.add(new homeWorkerFragment());
            list.add(new settingsFragment());
        }
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), list);
        viewPager.setAdapter(adapter);
    }

    private void setBottomNavigationView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        if (miniUserInfo.getRole() == 0) {
                            switch (item.getItemId()) {
                                case R.id.profile:
                                    Pager.setCurrentItem(0);
                                    break;
                                case R.id.chat:
                                    Pager.setCurrentItem(1);
                                    break;
                                case R.id.home:
                                    Pager.setCurrentItem(2);
                                    break;
                                case R.id.orders:
                                    Pager.setCurrentItem(3);
                                    break;
                                case R.id.settings:
                                    Pager.setCurrentItem(4);
                                    break;
                            }
                        } else if (miniUserInfo.getRole() == 1){
                            switch (item.getItemId()) {
                                case R.id.profile:
                                    Pager.setCurrentItem(0);
                                    break;
                                case R.id.chat:
                                    Pager.setCurrentItem(1);
                                    break;
                                case R.id.home:
                                    Pager.setCurrentItem(2);
                                    break;
                                case R.id.settings:
                                    Pager.setCurrentItem(3);
                                    break;
                            }
                        }
                        return false;
                    }
                }
        );
    }

    private void setPager() {
        Pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private MenuItem prevMenuItem;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null)
                    prevMenuItem.setChecked(false);
                else
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);

                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
