package com.example.jomride;

// User class
public class User {

    private String name;
    private boolean membership;

    private String phone;
    private String email;
    private String imageUrl; // New field for the image URL

    // Default constructor (required for Firebase)
    public User() {
    }

    public User(String name, String phone, String email, String imageUrl, boolean membership) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.imageUrl = imageUrl;
        this.membership = membership;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public boolean getMembership() {
        return membership;
    }

    public void setMembership(boolean membership){
        this.membership = membership;
    }

}
