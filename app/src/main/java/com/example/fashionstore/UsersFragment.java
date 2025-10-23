package com.example.fashionstore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView emptyView;
    private DatabaseReference usersRef;
    private AdminUserAdapter adapter;
    private ValueEventListener valueEventListener;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_list, container, false);

        recyclerView = view.findViewById(R.id.adminRecyclerView);
        emptyView = view.findViewById(R.id.emptyView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        emptyView.setText(R.string.no_users_found);
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        List<User> users = new ArrayList<>();
        adapter = new AdminUserAdapter(getContext(), users);
        recyclerView.setAdapter(adapter);

        // Setup pull to refresh
        swipeRefreshLayout.setOnRefreshListener(this::refreshUsers);

        loadUsers();

        return view;
    }

    private void loadUsers() {
        if (getContext() == null) return;

        if (valueEventListener != null) {
            usersRef.removeEventListener(valueEventListener);
        }

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!isAdded()) return;  // Check if fragment is still attached

                List<User> userList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null && !user.isAdmin()) {
                        user.setUserId(snapshot.getKey());
                        userList.add(user);
                    }
                }

                if (adapter != null) {
                    adapter.clear();
                    adapter.addAll(userList);
                    adapter.notifyDataSetChanged();

                    updateEmptyView(userList.isEmpty());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (!isAdded()) return;  // Check if fragment is still attached
                Toast.makeText(requireContext(),
                    getString(R.string.error_loading_users, databaseError.getMessage()),
                    Toast.LENGTH_SHORT).show();
            }
        };

        // Attach the listener
        usersRef.addValueEventListener(valueEventListener);
    }

    private void updateEmptyView(boolean isEmpty) {
        if (emptyView != null && recyclerView != null) {
            emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (valueEventListener != null) {
            usersRef.removeEventListener(valueEventListener);
            valueEventListener = null;
        }
        adapter = null;
        recyclerView = null;
        emptyView = null;
    }

    public void refreshUsers() {
        if (isAdded()) {
            loadUsers();
        }
    }
}
