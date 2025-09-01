package com.example.fashionstore;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private GridView vehiclesGridView;
    private VehicleGridAdapter vehicleAdapter;
    private List<Item> itemList = new ArrayList<>();
    private List<Item> filteredList = new ArrayList<>();
    private EditText searchBar;
    private TextView seeAllText;
    private ImageView toyotaBrand, nissanBrand, bydBrand, bmwBrand, benzBrand;
    private String currentUserId;
    private String currentFilterBrand = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        // Get current user ID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        }

        // Initialize views
        searchBar = findViewById(R.id.searchBar);
        seeAllText = findViewById(R.id.seeAllText);
        vehiclesGridView = findViewById(R.id.vehiclesGridView);

        // Initialize brand image views
        toyotaBrand = findViewById(R.id.toyotaBrand);
        nissanBrand = findViewById(R.id.nissanBrand);
        bydBrand = findViewById(R.id.bydBrand);
        bmwBrand = findViewById(R.id.bmwBrand);
        benzBrand = findViewById(R.id.benzBrand);

        // Setup GridView adapter
        vehicleAdapter = new VehicleGridAdapter(this, filteredList, currentUserId);
        vehiclesGridView.setAdapter(vehicleAdapter);

        // Load items from Firebase
        loadItems();

        // Setup search functionality
        setupSearch();

        // Setup brand click listeners
        setupBrandClickListeners();

        // See All click listener
        seeAllText.setOnClickListener(v -> {
            currentFilterBrand = "All";
            filteredList.clear();
            filteredList.addAll(itemList);
            vehicleAdapter.notifyDataSetChanged();
            Toast.makeText(HomeActivity.this, "Showing all vehicles", Toast.LENGTH_SHORT).show();
        });

        // Item click listener
        vehiclesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item item = filteredList.get(position);
                Toast.makeText(HomeActivity.this, "Selected: " + item.getName(), Toast.LENGTH_SHORT).show();
                // You can open item details activity here
            }
        });
    }

    private void setupBrandClickListeners() {
        toyotaBrand.setOnClickListener(v -> filterItemsByBrand("Toyota"));
        nissanBrand.setOnClickListener(v -> filterItemsByBrand("Nissan"));
        bydBrand.setOnClickListener(v -> filterItemsByBrand("BYD"));
        bmwBrand.setOnClickListener(v -> filterItemsByBrand("BMW"));
        benzBrand.setOnClickListener(v -> filterItemsByBrand("Mercedes"));
    }

    private void loadItems() {
        DatabaseReference itemsRef = FirebaseDatabase.getInstance().getReference("items");

        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();
                filteredList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Item item = dataSnapshot.getValue(Item.class);
                    if (item != null) {
                        item.setItemId(dataSnapshot.getKey());
                        itemList.add(item);
                    }
                }

                filteredList.addAll(itemList);
                vehicleAdapter.notifyDataSetChanged();

                if (itemList.isEmpty()) {
                    Toast.makeText(HomeActivity.this, "No vehicles found", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HomeActivity.this, itemList.size() + " vehicles found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Failed to load items: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSearch() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterItems(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterItems(String searchText) {
        filteredList.clear();

        if (searchText.isEmpty() && currentFilterBrand.equals("All")) {
            filteredList.addAll(itemList);
        } else if (searchText.isEmpty()) {
            filterItemsByBrand(currentFilterBrand);
        } else {
            for (Item item : itemList) {
                boolean matchesSearch = item.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(searchText.toLowerCase());

                boolean matchesBrand = currentFilterBrand.equals("All") ||
                        (item.getBrand() != null && item.getBrand().equalsIgnoreCase(currentFilterBrand));

                if (matchesSearch && matchesBrand) {
                    filteredList.add(item);
                }
            }
        }

        vehicleAdapter.notifyDataSetChanged();

        if (filteredList.isEmpty()) {
            Toast.makeText(HomeActivity.this, "No matching vehicles found", Toast.LENGTH_SHORT).show();
        }
    }

    private void filterItemsByBrand(String brandName) {
        currentFilterBrand = brandName;
        filteredList.clear();

        if (brandName.equals("All")) {
            filteredList.addAll(itemList);
            Toast.makeText(HomeActivity.this, "Showing all vehicles", Toast.LENGTH_SHORT).show();
        } else {
            for (Item item : itemList) {
                if (item.getBrand() != null && item.getBrand().equalsIgnoreCase(brandName)) {
                    filteredList.add(item);
                }
            }
            Toast.makeText(HomeActivity.this, "Showing " + brandName + " vehicles", Toast.LENGTH_SHORT).show();
        }

        vehicleAdapter.notifyDataSetChanged();

        if (filteredList.isEmpty()) {
            Toast.makeText(HomeActivity.this, "No " + brandName + " vehicles found", Toast.LENGTH_SHORT).show();
        }
    }
}