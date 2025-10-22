package com.example.fashionstore;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.bumptech.glide.Glide;

public class VehicleDetailsActivity extends AppCompatActivity {
    private ImageView vehicleImage;
    private TextView brandText, modelText, priceText, descriptionText;
    private TextView sellerNameText, sellerPhoneText, sellerLocationText;
    private Button chatButton;
    private DatabaseReference usersRef;
    private String sellerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_details);

        usersRef = FirebaseDatabase.getInstance().getReference("users");
        initializeViews();
        loadVehicleDetails();
    }

    private void initializeViews() {
        vehicleImage = findViewById(R.id.detailVehicleImage);
        brandText = findViewById(R.id.detailVehicleBrand);
        modelText = findViewById(R.id.detailVehicleModel);
        priceText = findViewById(R.id.detailVehiclePrice);
        descriptionText = findViewById(R.id.detailVehicleDescription);
        sellerNameText = findViewById(R.id.sellerName);
        sellerPhoneText = findViewById(R.id.sellerPhone);
        sellerLocationText = findViewById(R.id.sellerLocation);
        chatButton = findViewById(R.id.chatButton);

        chatButton.setOnClickListener(v -> openChat());
    }

    private void loadVehicleDetails() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String brand = extras.getString("brand", "");
            String model = extras.getString("model", "");
            String price = extras.getString("price", "");
            String description = extras.getString("description", "");
            String imageUrl = extras.getString("imageUrl", "");
            sellerId = extras.getString("sellerId", "");

            brandText.setText(brand);
            modelText.setText(model);
            priceText.setText(price);
            descriptionText.setText(description);

            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this)
                    .load(imageUrl)
                    .into(vehicleImage);
            }

            loadSellerDetails();
        }
    }

    private void loadSellerDetails() {
        if (sellerId != null && !sellerId.isEmpty()) {
            usersRef.child(sellerId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String name = dataSnapshot.child("name").getValue(String.class);
                        String phone = dataSnapshot.child("phone").getValue(String.class);
                        String location = dataSnapshot.child("location").getValue(String.class);

                        sellerNameText.setText("Seller: " + (name != null ? name : "N/A"));
                        sellerPhoneText.setText("Phone: " + (phone != null ? phone : "N/A"));
                        sellerLocationText.setText("Location: " + (location != null ? location : "N/A"));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(VehicleDetailsActivity.this,
                        "Error loading seller details", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void openChat() {
        if (sellerId != null && !sellerId.isEmpty()) {
            // TODO: Implement chat functionality with sellerId
            Toast.makeText(this, "Chat feature coming soon", Toast.LENGTH_SHORT).show();
        }
    }
}
