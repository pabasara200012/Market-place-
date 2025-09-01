package com.example.fashionstore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class VehicleGridAdapter extends ArrayAdapter<Item> {

    private Context context;
    private List<Item> itemList;
    private String currentUserId;

    public VehicleGridAdapter(Context context, List<Item> itemList, String currentUserId) {
        super(context, R.layout.vehicle_grid_item, itemList);
        this.context = context;
        this.itemList = itemList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.vehicle_grid_item, parent, false);
        }

        Item item = itemList.get(position);

        ImageView vehicleImage = convertView.findViewById(R.id.vehicleImage);
        TextView vehicleBrand = convertView.findViewById(R.id.vehicleBrand);
        TextView vehicleModel = convertView.findViewById(R.id.vehicleModel);
        TextView vehiclePrice = convertView.findViewById(R.id.vehiclePrice);
        Button addToCartButton = convertView.findViewById(R.id.addToCartButton);

        // Set item details
        if (item.getBrand() != null) {
            vehicleBrand.setText(item.getBrand());
        } else {
            vehicleBrand.setText("Brand");
        }

        vehicleModel.setText(item.getName());
        vehiclePrice.setText("Rs. " + item.getPrice());

        // Load image using Glide
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.vehicle_placeholder)
                    .into(vehicleImage);
        } else {
            vehicleImage.setImageResource(R.drawable.vehicle_placeholder);
        }

        // Add to cart button
        addToCartButton.setOnClickListener(v -> addToCart(item));

        return convertView;
    }

    private void addToCart(Item item) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(context, "Please login to add items to cart", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("carts")
                .child(userId)
                .child(item.getItemId());

        // Check if item already exists in cart
        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Item already in cart, update quantity
                    CartItem existingItem = snapshot.getValue(CartItem.class);
                    if (existingItem != null) {
                        existingItem.setQuantity(existingItem.getQuantity() + 1);
                        cartRef.setValue(existingItem)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(context, item.getName() + " quantity updated in cart!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Failed to update cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                } else {
                    // Item not in cart, add new item
                    CartItem cartItem = new CartItem(
                            item.getItemId(),
                            item.getName(),
                            item.getDescription(),
                            item.getPrice(),
                            item.getImageUrl(),
                            item.getBrand(),
                            userId,
                            1, // Quantity
                            System.currentTimeMillis() // Timestamp
                    );

                    cartRef.setValue(cartItem)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, item.getName() + " added to cart!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Failed to add to cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error checking cart: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}