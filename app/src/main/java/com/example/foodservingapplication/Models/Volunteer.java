package com.example.foodservingapplication.Models;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Volunteer {

    private String username;
    private String email;
    private String  phoneNumber;
    private String volunteerId;
    private String VolunteerProfileImage;
    private String  Type_of_user;
    private String token_id;
    private GeoPoint geoPoint;
    private  @ServerTimestamp Date timestamp;



    public Volunteer() {
    }

    public Volunteer(String username, String email, String phoneNumber, String volunteerId, String volunteerProfileImage, String type_of_user, String token_id, GeoPoint geoPoint, Date timestamp) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.volunteerId = volunteerId;
        VolunteerProfileImage = volunteerProfileImage;
        Type_of_user = type_of_user;
        this.token_id = token_id;
        this.geoPoint = geoPoint;
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(String volunteerId) {
        this.volunteerId = volunteerId;
    }

    public String getVolunteerProfileImage() {
        return VolunteerProfileImage;
    }

    public void setVolunteerProfileImage(String volunteerProfileImage) {
        VolunteerProfileImage = volunteerProfileImage;
    }

    public String getType_of_user() {
        return Type_of_user;
    }

    public void setType_of_user(String type_of_user) {
        Type_of_user = type_of_user;
    }

    public String getToken_id() {
        return token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Volunteer{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", volunteerId='" + volunteerId + '\'' +
                ", VolunteerProfileImage='" + VolunteerProfileImage + '\'' +
                ", Type_of_user='" + Type_of_user + '\'' +
                ", token_id='" + token_id + '\'' +
                ", geoPoint=" + geoPoint +
                ", timestamp=" + timestamp +
                '}';
    }
}
