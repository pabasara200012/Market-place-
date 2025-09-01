package com.example.fashionstore;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddItemActivity extends AppCompatActivity {

    private EditText itemName, itemPrice, itemDescription, itemBrand; // Brand field එක එකතු කරන්න
    private ImageView itemImagePreview;
    private ImageButton chooseImageBtn;
    private Button uploadItemBtn;
    private ProgressBar progressBar;
    private Uri imageUri;
    private static final String TAG = "AddItemActivity";

    // RecyclerView variables
    private RecyclerView itemsRecyclerView;
    private ItemAdapter itemAdapter;
    private List<Item> itemList = new ArrayList<>();
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item_activity);

        // Get current user ID
        currentUserId = getIntent().getStringExtra("user_id");
        if (currentUserId == null) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                currentUserId = currentUser.getUid();
            }
        }

        // Initialize UI components
        initializeViews();

        // Set up button click listeners
        setupButtonListeners();

        // Setup RecyclerView for user's items
        setupRecyclerView();

        // Load user's items
        loadUserItems();
    }

    private void initializeViews() {
        itemName = findViewById(R.id.itemName);
        itemBrand = findViewById(R.id.itemBrand); // Brand field එක initialize කරන්න
        itemPrice = findViewById(R.id.itemPrice);
        itemDescription = findViewById(R.id.itemDescription);
        chooseImageBtn = findViewById(R.id.chooseImageBtn);
        uploadItemBtn = findViewById(R.id.uploadItemBtn);
        itemImagePreview = findViewById(R.id.itemImagePreview);
        progressBar = findViewById(R.id.progressBar);
        itemsRecyclerView = findViewById(R.id.itemsRecyclerView);
    }

    private void setupButtonListeners() {
        chooseImageBtn.setOnClickListener(v -> openImagePicker());
        uploadItemBtn.setOnClickListener(v -> uploadItem());
    }

    private void setupRecyclerView() {
        itemAdapter = new ItemAdapter(this, itemList, currentUserId);
        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemsRecyclerView.setAdapter(itemAdapter);
    }

    private void loadUserItems() {
        if (currentUserId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("items");
        db.orderByChild("sellerId").equalTo(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        itemList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Item item = ds.getValue(Item.class);
                            if (item != null) {
                                item.setItemId(ds.getKey()); // Set the Firebase key as item ID
                                itemList.add(item);
                            }
                        }
                        itemAdapter.notifyDataSetChanged();

                        if (itemList.isEmpty()) {
                            Toast.makeText(AddItemActivity.this, "No items found. Add your first item!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AddItemActivity.this, "Failed to load items: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            itemImagePreview.setImageURI(imageUri);
        }
    }

    private void uploadItem() {
        String name = itemName.getText().toString().trim();
        String brand = itemBrand.getText().toString().trim(); // Brand එක ලබාගන්න
        String price = itemPrice.getText().toString().trim();
        String description = itemDescription.getText().toString().trim();

        if (name.isEmpty() || brand.isEmpty() || price.isEmpty() || description.isEmpty() || imageUri == null) {
            Toast.makeText(this, "All fields and image are required", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        uploadItemBtn.setEnabled(false);

        // Upload image to Cloudinary first
        uploadImageToCloudinary(name, brand, price, description);
    }

    private void uploadImageToCloudinary(String name, String brand, String price, String description) {
        MediaManager.get().upload(imageUri)
                .option("folder", "fashion_store_items")
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d(TAG, "Cloudinary upload started");
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        // Upload progress
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String imageUrl = (String) resultData.get("secure_url");
                        Log.d(TAG, "Cloudinary upload success: " + imageUrl);

                        // Save item details to Firebase with the image URL
                        saveItemToFirestore(name, brand, price, description, imageUrl);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.e(TAG, "Cloudinary upload error: " + error.getDescription());
                        progressBar.setVisibility(View.GONE);
                        uploadItemBtn.setEnabled(true);
                        Toast.makeText(AddItemActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        // Retry logic
                    }
                })
                .dispatch();
    }

    private void saveItemToFirestore(String name, String brand, String price, String description, String imageUrl) {
        if (currentUserId == null || currentUserId.isEmpty()) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            uploadItemBtn.setEnabled(true);
            return;
        }

        // Create item object
        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("brand", brand); // Brand එක ඇතුළත් කරන්න
        item.put("price", price);
        item.put("description", description);
        item.put("imageUrl", imageUrl);
        item.put("sellerId", currentUserId);
        item.put("timestamp", System.currentTimeMillis());

        // Save to Firebase Realtime Database
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        String itemId = db.child("items").push().getKey();

        db.child("items").child(itemId).setValue(item)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    uploadItemBtn.setEnabled(true);
                    Toast.makeText(AddItemActivity.this, "Item uploaded successfully", Toast.LENGTH_SHORT).show();

                    // Clear form
                    clearForm();

                    // Refresh the items list
                    loadUserItems();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    uploadItemBtn.setEnabled(true);
                    Toast.makeText(AddItemActivity.this, "Failed to upload item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearForm() {
        itemName.setText("");
        itemBrand.setText(""); // Brand field එක clear කරන්න
        itemPrice.setText("");
        itemDescription.setText("");
        itemImagePreview.setImageResource(android.R.color.transparent);
        imageUri = null;
    }
}