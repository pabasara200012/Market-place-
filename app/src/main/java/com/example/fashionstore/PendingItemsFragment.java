package com.example.fashionstore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class PendingItemsFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView emptyView;
    private DatabaseReference itemsRef;
    private AdminItemAdapter adapter;
    private ValueEventListener valueEventListener;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_list, container, false);

        recyclerView = view.findViewById(R.id.adminRecyclerView);
        emptyView = view.findViewById(R.id.emptyView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        emptyView.setText(R.string.no_pending_items);
        itemsRef = FirebaseDatabase.getInstance().getReference("items");

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        List<Item> items = new ArrayList<>();
        adapter = new AdminItemAdapter(getContext(), items, true); // true for pending items view
        recyclerView.setAdapter(adapter);

        // Setup pull to refresh
        swipeRefreshLayout.setOnRefreshListener(this::loadItems);

        loadItems();

        return view;
    }

    public void loadItems() {
        if (getContext() == null) return;

        if (valueEventListener != null) {
            itemsRef.removeEventListener(valueEventListener);
        }

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!isAdded()) return;

                List<Item> itemList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Item item = snapshot.getValue(Item.class);
                    if (item != null && !item.isApproved()) {
                        item.setItemId(snapshot.getKey());
                        itemList.add(item);
                    }
                }

                if (adapter != null) {
                    adapter.clear();
                    adapter.addAll(itemList);
                    adapter.notifyDataSetChanged();
                    updateEmptyView(itemList.isEmpty());
                }

                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (!isAdded()) return;
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        };

        itemsRef.addValueEventListener(valueEventListener);
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
            itemsRef.removeEventListener(valueEventListener);
            valueEventListener = null;
        }
        adapter = null;
        recyclerView = null;
        emptyView = null;
        swipeRefreshLayout = null;
    }
}
