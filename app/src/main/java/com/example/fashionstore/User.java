package com.example.fashionstore;

import android.os.Build;

import java.time.LocalDate;

public class User {
    private String userId;
    private String name;
    private String userName;
    private String email;
    private String password;
    private String phone;
    private boolean admin;
    private int itemCount;
    private String joinDate;

    public User() {
        // Required empty constructor for Firebase
    }

    public User(String name, String userName, String email, String password, String phone) {
        this.name = name;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.admin = false;
        this.itemCount = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.joinDate = LocalDate.now().toString();
        }
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public boolean isAdmin() { return admin; }
    public void setAdmin(boolean admin) { this.admin = admin; }

    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }

    public String getJoinDate() { return joinDate; }
    public void setJoinDate(String joinDate) { this.joinDate = joinDate; }
}
