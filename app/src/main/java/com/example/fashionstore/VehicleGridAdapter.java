package com.example.fashionstore;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.List;
import java.util.Locale;

public class VehicleGridAdapter extends BaseAdapter {
    private List<Item> items;
    private Context context;
    private String currentUserId;

    public VehicleGridAdapter(Context context, List<Item> items, String currentUserId) {
        this.context = context;
        this.items = items;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getCount() {
        return items != null ? items.size() : 0;
    }

    @Override
    public Item getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.vehicle_grid_item, parent, false);
            holder = new ViewHolder();
            holder.vehicleImage = convertView.findViewById(R.id.vehicleImage);
            holder.brandText = convertView.findViewById(R.id.vehicleBrand);
            holder.modelText = convertView.findViewById(R.id.vehicleModel);
            holder.priceText = convertView.findViewById(R.id.vehiclePrice);
            holder.detailsButton = convertView.findViewById(R.id.DetailsButton);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Item item = getItem(position);

        holder.brandText.setText(item.getBrand());
        holder.modelText.setText(item.getName());
        // Format price properly
        holder.priceText.setText(String.format(Locale.US, "Rs. %.2f", item.getPrice()));

        // Load image using Glide
        String imageUrl = item.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.vehicle_placeholder)
                .error(R.drawable.vehicle_placeholder)
                .centerCrop()
                .into(holder.vehicleImage);
        } else {
            holder.vehicleImage.setImageResource(R.drawable.vehicle_placeholder);
        }

        holder.detailsButton.setOnClickListener(v -> openItemDetails(item));

        return convertView;
    }

    private void openItemDetails(Item item) {
        Intent intent = new Intent(context, VehicleDetailsActivity.class);
        intent.putExtra("brand", item.getBrand());
        intent.putExtra("model", item.getName());
        intent.putExtra("price", item.getPrice());
        intent.putExtra("description", item.getDescription());
        intent.putExtra("sellerId", item.getSellerId());
        intent.putExtra("imageUrl", item.getImageUrl());
        context.startActivity(intent);
    }

    private static class ViewHolder {
        ImageView vehicleImage;
        TextView brandText, modelText, priceText;
        Button detailsButton;
    }
}
