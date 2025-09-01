package com.example.fashionstore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private Context context;
    private List<Item> itemList;
    private String currentUserId;

    public ItemAdapter(Context context, List<Item> itemList, String currentUserId) {
        this.context = context;
        this.itemList = itemList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_row, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemImage;
        private TextView itemName, itemBrand, itemPrice, itemDescription; // Brand field එක එකතු කරන්න
        private Button editButton, deleteButton;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemBrand = itemView.findViewById(R.id.itemBrand); // Brand field එක initialize කරන්න
            itemPrice = itemView.findViewById(R.id.itemPrice);
            itemDescription = itemView.findViewById(R.id.itemDescription);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(Item item) {
            itemName.setText(item.getName());
            itemBrand.setText(item.getBrand()); // Brand එක set කරන්න
            itemPrice.setText("Rs. " + item.getPrice());
            itemDescription.setText(item.getDescription());

            // Load image using Glide - FIXED placeholder issue
            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(item.getImageUrl())
                        .placeholder(android.R.drawable.ic_menu_gallery) // Using Android default icon
                        .error(android.R.drawable.ic_delete) // Error placeholder
                        .into(itemImage);
            } else {
                // If no image URL, set a default image
                itemImage.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            // Edit button click listener
            editButton.setOnClickListener(v -> {
                openEditDialog(item);
            });

            // Delete button click listener
            deleteButton.setOnClickListener(v -> {
                deleteItem(item);
            });
        }

        private void openEditDialog(Item item) {
            // Implement your edit dialog here
            Toast.makeText(context, "Edit: " + item.getName(), Toast.LENGTH_SHORT).show();

            // You can open a dialog or new activity for editing
            /*
            Intent intent = new Intent(context, EditItemActivity.class);
            intent.putExtra("item_id", item.getItemId());
            context.startActivity(intent);
            */
        }

        private void deleteItem(Item item) {
            DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference("items").child(item.getItemId());
            itemRef.removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Item deleted successfully", Toast.LENGTH_SHORT).show();

                        // Remove item from the list and update RecyclerView
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            itemList.remove(position);
                            notifyItemRemoved(position);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Failed to delete item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}