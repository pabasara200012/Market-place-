package com.example.fashionstore;

public class Vehicle {
    private String brand;
    private String model;
    private String price;
    private String description;
    private String imageUrl;
    private String sellerName;
    private String sellerPhone;
    private String sellerLocation;

    public Vehicle(String brand, String model, String price, String description,
                  String imageUrl, String sellerName, String sellerPhone, String sellerLocation) {
        this.brand = brand;
        this.model = model;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.sellerName = sellerName;
        this.sellerPhone = sellerPhone;
        this.sellerLocation = sellerLocation;
    }

    // Getters
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public String getPrice() { return price; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getSellerName() { return sellerName; }
    public String getSellerPhone() { return sellerPhone; }
    public String getSellerLocation() { return sellerLocation; }

    // Setters
    public void setBrand(String brand) { this.brand = brand; }
    public void setModel(String model) { this.model = model; }
    public void setPrice(String price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }
    public void setSellerPhone(String sellerPhone) { this.sellerPhone = sellerPhone; }
    public void setSellerLocation(String sellerLocation) { this.sellerLocation = sellerLocation; }
}
