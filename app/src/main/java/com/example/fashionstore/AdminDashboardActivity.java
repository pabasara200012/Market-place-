package com.example.fashionstore;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.*;

public class AdminDashboardActivity extends AppCompatActivity {
    private TextView adminStatsText;
    private DatabaseReference itemsRef, usersRef;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dashboard);

        // Initialize Firebase references
        itemsRef = FirebaseDatabase.getInstance().getReference("items");
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Setup logout button
        findViewById(R.id.logoutButton).setOnClickListener(v -> {
            // Clear any stored admin session/credentials
            getSharedPreferences("AdminPrefs", MODE_PRIVATE)
                .edit()
                .clear()
                .apply();

            // Navigate back to login screen
            finish();
        });

        // Initialize views
        TabLayout tabLayout = findViewById(R.id.adminTabLayout);
        viewPager = findViewById(R.id.adminViewPager);
        adminStatsText = findViewById(R.id.adminStatsText);

        // Setup ViewPager with fragments
        AdminPagerAdapter pagerAdapter = new AdminPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Connect TabLayout with ViewPager2
        viewPager.setOffscreenPageLimit(3); // Keep all fragments in memory
        new TabLayoutMediator(tabLayout, viewPager,
            (tab, position) -> {
                switch (position) {
                    case 0:
                        tab.setText(R.string.tab_pending_items);
                        break;
                    case 1:
                        tab.setText(R.string.tab_all_items);
                        break;
                    case 2:
                        tab.setText(R.string.tab_users);
                        break;
                    case 3:
                        tab.setText(R.string.admin_profile_title);
                        break;
                }
            }
        ).attach();

        // Load initial statistics
        loadAdminStats();

        // Setup tab selection listener for refresh
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                refreshCurrentTab();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                refreshCurrentTab();
            }
        });
    }

    private void refreshCurrentTab() {
        Fragment currentFragment = getSupportFragmentManager()
            .findFragmentByTag("f" + viewPager.getCurrentItem());

        if (currentFragment instanceof PendingItemsFragment) {
            ((PendingItemsFragment) currentFragment).loadItems();
        } else if (currentFragment instanceof AllItemsFragment) {
            ((AllItemsFragment) currentFragment).loadItems();
        } else if (currentFragment instanceof UsersFragment) {
            ((UsersFragment) currentFragment).refreshUsers();
        } else if (currentFragment instanceof AdminProfileFragment) {
            ((AdminProfileFragment) currentFragment).loadAdminProfile();
        }
    }

    private void loadAdminStats() {
        usersRef.get().addOnSuccessListener(usersSnapshot ->
            itemsRef.get().addOnSuccessListener(itemsSnapshot -> {
                int totalUsers = (int) usersSnapshot.getChildrenCount();
                int pendingItems = 0;
                int visibleItems = 0;

                for (DataSnapshot itemSnapshot : itemsSnapshot.getChildren()) {
                    Item item = itemSnapshot.getValue(Item.class);
                    if (item != null) {
                        if (!item.isApproved()) {
                            pendingItems++;
                        }
                        if (item.isVisible()) {
                            visibleItems++;
                        }
                    }
                }

                if (adminStatsText != null) {
                    adminStatsText.setText(getString(R.string.admin_stats_format,
                        totalUsers, pendingItems, visibleItems));
                }
            })
        );
    }

    // Method to approve an item
    public void approveItem(String itemId) {
        itemsRef.child(itemId).child("approved").setValue(true)
            .addOnSuccessListener(aVoid -> {
                loadAdminStats();
                refreshCurrentTab();
            });
    }

    // Method to toggle item visibility
    public void toggleItemVisibility(String itemId, boolean visible) {
        itemsRef.child(itemId).child("visible").setValue(visible)
            .addOnSuccessListener(aVoid -> {
                loadAdminStats();
                refreshCurrentTab();
            });
    }
}
