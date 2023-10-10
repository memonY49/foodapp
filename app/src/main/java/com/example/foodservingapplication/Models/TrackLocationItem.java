package com.example.foodservingapplication.Models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class TrackLocationItem {
    String address;
    String Date;
    String status;
    String postId;

    public TrackLocationItem(String address, String date, String status, String postId) {
        this.address = address;
        Date = date;
        this.status = status;
        this.postId = postId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    @Override
    public String toString() {
        return "TrackLocationItem{" +
                "address='" + address + '\'' +
                ", Date='" + Date + '\'' +
                ", status='" + status + '\'' +
                ", postId='" + postId + '\'' +
                '}';
    }

    public TrackLocationItem() {
    }
}
