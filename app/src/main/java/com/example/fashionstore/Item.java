package com.example.fashionstore;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import com.google.firebase.database.PropertyName;

public class Item implements Parcelable {
    private String itemId;
    private String name;
    private String description;
    private double price;
    private String sellerId;
    private String imageUrl;
    private boolean approved;
    private boolean visible;
    private long timestamp;
    private String brand;  // Added brand field

    public Item() {
        // Required empty constructor for Firebase
    }

    public Item(String name, String description, double price, String sellerId, String imageUrl, String brand) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.sellerId = sellerId;
        this.imageUrl = imageUrl;
        this.brand = brand;
        this.approved = false;
        this.visible = false;
        this.timestamp = System.currentTimeMillis();
    }

    protected Item(Parcel in) {
        itemId = in.readString();
        name = in.readString();
        description = in.readString();
        price = in.readDouble();
        sellerId = in.readString();
        imageUrl = in.readString();
        approved = in.readByte() != 0;
        visible = in.readByte() != 0;
        timestamp = in.readLong();
        brand = in.readString();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(itemId);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeDouble(price);
        dest.writeString(sellerId);
        dest.writeString(imageUrl);
        dest.writeByte((byte) (approved ? 1 : 0));
        dest.writeByte((byte) (visible ? 1 : 0));
        dest.writeLong(timestamp);
        dest.writeString(brand);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    // Getters and setters
    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() {
        return price;
    }

    @PropertyName("price")
    public void setPrice(Object priceObj) {
        if (priceObj == null) {
            this.price = 0.0;
            return;
        }

        if (priceObj instanceof Double) {
            this.price = (Double) priceObj;
        } else if (priceObj instanceof Long) {
            this.price = ((Long) priceObj).doubleValue();
        } else if (priceObj instanceof Integer) {
            this.price = ((Integer) priceObj).doubleValue();
        } else if (priceObj instanceof String) {
            try {
                // Remove any non-numeric characters except decimal point
                String cleanPrice = ((String) priceObj).replaceAll("[^\\d.]", "");
                this.price = Double.parseDouble(cleanPrice);
            } catch (NumberFormatException e) {
                this.price = 0.0;
            }
        } else {
            this.price = 0.0;
        }
    }

    public String getSellerId() { return sellerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
}
