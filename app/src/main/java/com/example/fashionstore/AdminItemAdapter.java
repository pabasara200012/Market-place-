package com.example.fashionstore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
import java.util.Locale;

public class AdminItemAdapter extends RecyclerView.Adapter<AdminItemAdapter.ViewHolder> {
    private final Context context;
    private final List<Item> items;
    private final boolean isPendingView;

    public AdminItemAdapter(Context context, List<Item> items, boolean isPendingView) {
        this.context = context;
        this.items = items;
        this.isPendingView = isPendingView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_item_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);

        holder.itemName.setText(item.getName());
        holder.itemPrice.setText(String.format(Locale.US, context.getString(R.string.price_format), item.getPrice()));
        holder.itemStatus.setText(item.isApproved() ? R.string.status_approved : R.string.status_pending);

        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.itemImage);
        }

        // Show/hide approve button based on view type and item status
        if (isPendingView && !item.isApproved()) {
            holder.approveButton.setVisibility(View.VISIBLE);
            holder.toggleVisibilityButton.setVisibility(View.GONE);
        } else {
            holder.approveButton.setVisibility(View.GONE);
            holder.toggleVisibilityButton.setVisibility(View.VISIBLE);
            holder.toggleVisibilityButton.setText(item.isVisible() ?
                R.string.hide_item_title : R.string.show_item_title);
        }

        // Setup button click listeners
        holder.approveButton.setOnClickListener(v -> {
            if (context instanceof AdminDashboardActivity) {
                ((AdminDashboardActivity) context).approveItem(item.getItemId());
            }
        });

        holder.toggleVisibilityButton.setOnClickListener(v -> {
            if (context instanceof AdminDashboardActivity) {
                ((AdminDashboardActivity) context).toggleItemVisibility(item.getItemId(), !item.isVisible());
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void clear() {
        int size = items.size();
        items.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void addAll(List<Item> itemList) {
        int startPosition = items.size();
        items.addAll(itemList);
        notifyItemRangeInserted(startPosition, itemList.size());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView itemImage;
        final TextView itemName;
        final TextView itemPrice;
        final TextView itemStatus;
        final Button approveButton;
        final Button toggleVisibilityButton;

        ViewHolder(View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            itemStatus = itemView.findViewById(R.id.itemStatus);
            approveButton = itemView.findViewById(R.id.approveButton);
            toggleVisibilityButton = itemView.findViewById(R.id.toggleVisibilityButton);
        }
    }
}
