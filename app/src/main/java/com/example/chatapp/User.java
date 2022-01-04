package com.example.chatapp;

public class User {
    private String uid, imageUrl, name, email;

    public User(String uid, String imageUrl, String name, String email) {
        this.uid = uid;
        this.imageUrl = imageUrl;
        this.name = name;
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

