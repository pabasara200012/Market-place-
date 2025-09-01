package com.example.fashionstore;

public class CartItem {
    private String itemId;
    private String name;
    private String description;
    private String price;
    private String imageUrl;
    private String brand;
    private String userId;
    private int quantity; // Add this field
    private long timestamp; // Add this field

    // Default constructor required for Firebase
    public CartItem() {
    }

    // Updated constructor with all fields including quantity and timestamp
    public CartItem(String itemId, String name, String description, String price,
                    String imageUrl, String brand, String userId, int quantity, long timestamp) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.brand = brand;
        this.userId = userId;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }

    // Getters and setters for all fields
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}