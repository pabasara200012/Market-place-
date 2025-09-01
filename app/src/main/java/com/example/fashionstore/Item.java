package com.example.fashionstore;

public class Item {
    private String itemId;
    private String name;
    private String price;
    private String description;
    private String imageUrl;
    private String sellerId;
    private long timestamp;
    private String brand; // Brand field එක එකතු කරන්න

    // Default constructor required for Firebase
    public Item() {}

    public Item(String name, String price, String description, String imageUrl, String sellerId, String brand) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.sellerId = sellerId;
        this.brand = brand;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and setters
    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getSellerId() { return sellerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getBrand() { return brand; } // නිවැරදි getBrand method එක
    public void setBrand(String brand) { this.brand = brand; }
}