package com.example.fashionstore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;
import java.util.Locale;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private final Context context;
    private final List<Item> itemList;
    private final String currentUserId;

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
        private final ImageView itemImage;
        private final TextView itemName, itemBrand, itemPrice, itemDescription;
        private final Button editButton, deleteButton;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemBrand = itemView.findViewById(R.id.itemBrand);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            itemDescription = itemView.findViewById(R.id.itemDescription);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(Item item) {
            // Set basic item information
            itemName.setText(item.getName());
            itemBrand.setText(item.getBrand());
            itemPrice.setText(String.format(Locale.US, "Rs. %.2f", item.getPrice()));
            itemDescription.setText(item.getDescription());

            // Load image
            loadItemImage(item);

            // Set visibility based on approval status
            updateItemVisibility(item);

            // Setup buttons only if user owns the item
            boolean isOwner = currentUserId != null && currentUserId.equals(item.getSellerId());
            setupButtons(item, isOwner);
        }

        private void loadItemImage(Item item) {
            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                Glide.with(context)
                    .load(item.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_delete)
                    .into(itemImage);
            } else {
                itemImage.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        }

        private void updateItemVisibility(Item item) {
            itemView.setAlpha(item.isVisible() ? 1.0f : 0.5f);

            // Show edit/delete buttons only for the item owner
            boolean isOwner = currentUserId != null && currentUserId.equals(item.getSellerId());
            editButton.setVisibility(isOwner ? View.VISIBLE : View.GONE);
            deleteButton.setVisibility(isOwner ? View.VISIBLE : View.GONE);
        }

        private void setupButtons(Item item, boolean isOwner) {
            editButton.setVisibility(isOwner ? View.VISIBLE : View.GONE);
            deleteButton.setVisibility(isOwner ? View.VISIBLE : View.GONE);

            if (isOwner) {
                editButton.setOnClickListener(v -> {
                    if (context instanceof MainActivity) {
                        ((MainActivity) context).openEditItemActivity(item);
                    }
                });

                deleteButton.setOnClickListener(v -> showDeleteConfirmation(item));
            }
        }

        private void showDeleteConfirmation(Item item) {
            new AlertDialog.Builder(context)
                .setTitle(R.string.delete_item_title)
                .setMessage(context.getString(R.string.delete_item_message, item.getName()))
                .setPositiveButton(R.string.delete_button_text, (dialog, which) -> deleteItem(item))
                .setNegativeButton(android.R.string.cancel, null)
                .show();
        }

        private void deleteItem(Item item) {
            if (item.getItemId() == null) {
                showError(context.getString(R.string.error_generic, "Invalid item ID"));
                return;
            }

            DatabaseReference itemRef = FirebaseDatabase.getInstance()
                .getReference("items")
                .child(item.getItemId());

            itemRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    int position = itemList.indexOf(item);
                    if (position != -1) {
                        itemList.remove(position);
                        notifyItemRemoved(position);
                    }
                })
                .addOnFailureListener(e ->
                    showError(context.getString(R.string.error_deleting_item, e.getMessage())));
        }

        private void showError(String message) {
            new AlertDialog.Builder(context)
                .setTitle(R.string.error_title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
        }
    }
}