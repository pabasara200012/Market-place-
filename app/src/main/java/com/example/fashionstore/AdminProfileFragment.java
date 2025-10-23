package com.example.fashionstore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class AdminProfileFragment extends Fragment {
    private TextView adminNameText;
    private TextView adminEmailText;
    private TextView adminStatsText;
    private DatabaseReference adminRef;
    private DatabaseReference itemsRef;
    private DatabaseReference usersRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_profile, container, false);

        // Initialize views
        adminNameText = view.findViewById(R.id.adminNameText);
        adminEmailText = view.findViewById(R.id.adminEmailText);
        adminStatsText = view.findViewById(R.id.adminStatsText);

        // Initialize Firebase references
        adminRef = FirebaseDatabase.getInstance().getReference("admin");
        itemsRef = FirebaseDatabase.getInstance().getReference("items");
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Load admin profile data
        loadAdminProfile();
        loadAdminStats();

        return view;
    }

    public void loadAdminProfile() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            adminNameText.setText("Not signed in");
            adminEmailText.setText("");
            return;
        }

        String adminId = auth.getCurrentUser().getUid();
        adminRef.child(adminId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) return;  // Check if fragment is still attached

                if (snapshot.exists()) {
                    Admin admin = snapshot.getValue(Admin.class);
                    if (admin != null) {
                        adminNameText.setText(getString(R.string.admin_name_format, admin.getName()));
                        adminEmailText.setText(getString(R.string.admin_email_format, admin.getEmail()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!isAdded()) return;
                // Handle error case
                adminNameText.setText(R.string.error_loading_profile);
                adminEmailText.setText("");
            }
        });
    }

    private void loadAdminStats() {
        itemsRef.get().addOnSuccessListener(itemsSnapshot -> {
            usersRef.get().addOnSuccessListener(usersSnapshot -> {
                int totalUsers = (int) usersSnapshot.getChildrenCount();
                int pendingItems = 0;
                int totalItems = 0;

                for (DataSnapshot itemSnapshot : itemsSnapshot.getChildren()) {
                    Item item = itemSnapshot.getValue(Item.class);
                    if (item != null) {
                        totalItems++;
                        if (!item.isApproved()) {
                            pendingItems++;
                        }
                    }
                }

                String stats = String.format("Total Users: %d\nTotal Items: %d\nPending Items: %d",
                    totalUsers, totalItems, pendingItems);
                adminStatsText.setText(stats);
            });
        });
    }
}
