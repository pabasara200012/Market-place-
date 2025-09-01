
package com.example.fashionstore;

public class User {
    private String userName;
    private String email;
    private String profileImageUrl;

    public User() {}

    public User(String userName, String email, String profileImageUrl) {
        this.userName = userName;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
    }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
}