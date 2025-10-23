package com.example.fashionstore;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ConstraintLayout mainLayours;
    private ImageView homeButton, addItemButton, cartButton, userButton;
    private String loggedInUserName;
    private String loggedInUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLayours = findViewById(R.id.mainLayer);
        homeButton = findViewById(R.id.homeButton);
        addItemButton = findViewById(R.id.addItemButton);
        cartButton = findViewById(R.id.nextAiButton);
        userButton = findViewById(R.id.userButton);

        loggedInUserName = getIntent().getStringExtra("username");
        loggedInUserId = getIntent().getStringExtra("user_id");

        loadHomeLayout();

        homeButton.setOnClickListener(v -> loadHomeLayout());
        addItemButton.setOnClickListener(v -> loadAddItemLayout());
        cartButton.setOnClickListener(v -> loadCartLayout());
        userButton.setOnClickListener(v -> loadUserLayout());
    }

    private void loadHomeLayout() {
        mainLayours.removeAllViews();
        View view = LayoutInflater.from(this).inflate(R.layout.home_page, mainLayours, false);
        mainLayours.addView(view);

        // Find GridView in home_page.xml
        GridView vehiclesGridView = view.findViewById(R.id.vehiclesGridView);

        // Use logic from HomeActivity
        List<Item> itemList = new ArrayList<>();
        List<Item> filteredList = new ArrayList<>();

        VehicleGridAdapter vehicleAdapter = new VehicleGridAdapter(this, filteredList, loggedInUserId);
        vehiclesGridView.setAdapter(vehicleAdapter);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("items");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();
                filteredList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    try {
                        Item item = new Item();
                        item.setItemId(ds.getKey());

                        // Get each field with null checks
                        String name = ds.child("name").getValue(String.class);
                        String description = ds.child("description").getValue(String.class);
                        String sellerId = ds.child("sellerId").getValue(String.class);
                        String imageUrl = ds.child("imageUrl").getValue(String.class);
                        String brand = ds.child("brand").getValue(String.class);
                        Boolean approved = ds.child("approved").getValue(Boolean.class);
                        Boolean visible = ds.child("visible").getValue(Boolean.class);
                        Long timestamp = ds.child("timestamp").getValue(Long.class);

                        item.setName(name != null ? name : "");
                        item.setDescription(description != null ? description : "");
                        item.setSellerId(sellerId != null ? sellerId : "");
                        item.setImageUrl(imageUrl != null ? imageUrl : "");
                        item.setBrand(brand != null ? brand : "");
                        item.setApproved(approved != null ? approved : false);
                        item.setVisible(visible != null ? visible : false);
                        item.setTimestamp(timestamp != null ? timestamp : 0L);

                        // Handle price carefully
                        Object priceObj = ds.child("price").getValue();
                        if (priceObj instanceof Double) {
                            item.setPrice((Double) priceObj);
                        } else if (priceObj instanceof Long) {
                            item.setPrice(((Long) priceObj).doubleValue());
                        } else if (priceObj instanceof String) {
                            try {
                                item.setPrice(Double.parseDouble((String) priceObj));
                            } catch (NumberFormatException e) {
                                item.setPrice(0.0);
                            }
                        } else {
                            item.setPrice(0.0);
                        }

                        itemList.add(item);
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                }

                filteredList.addAll(itemList);
                vehicleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadAddItemLayout() {
        Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
        intent.putExtra("username", loggedInUserName);
        intent.putExtra("user_id", loggedInUserId);
        startActivity(intent);
    }

    private void loadCartLayout() {
        inflateLayout(R.layout.cart_view);
    }

    private void loadUserLayout() {
        mainLayours.removeAllViews();
        View view = LayoutInflater.from(this).inflate(R.layout.testuserview, mainLayours, false);
        mainLayours.addView(view);
        UserViewHandler.setup(view, loggedInUserName, this); // Pass activity as third argument
    }

    private void inflateLayout(int layoutResId) {
        mainLayours.removeAllViews();
        View view = LayoutInflater.from(this).inflate(layoutResId, mainLayours, false);
        mainLayours.addView(view);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mainLayours.getChildCount() > 0) {
            View userView = mainLayours.getChildAt(0);
            UserViewHandler.handleImageResult(this, requestCode, resultCode, data, loggedInUserName, userView);
        }
    }

    public void openEditItemActivity(Item item) {
        Intent intent = new Intent(this, EditItemActivity.class);
        intent.putExtra(EditItemActivity.EXTRA_ITEM, item);
        startActivity(intent);
    }
}