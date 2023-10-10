package com.example.foodservingapplication.Models;

import com.google.firebase.firestore.GeoPoint;

public class VolunteerPickedFoods {
   private String UserId;
    private  String VolunteerId;
    private String Status;
    private GeoPoint UserGeopoint;
    private GeoPoint VolunteerGeopoint;
    private VolunteerPostShow postShow;
    private String token_Id_volunteer;


    public VolunteerPickedFoods() {
    }

    public VolunteerPickedFoods(String userId, String volunteerId, String status, GeoPoint userGeopoint, GeoPoint volunteerGeopoint, VolunteerPostShow postShow, String token_Id_volunteer) {
        UserId = userId;
        VolunteerId = volunteerId;
        Status = status;
        UserGeopoint = userGeopoint;
        VolunteerGeopoint = volunteerGeopoint;
        this.postShow = postShow;
        this.token_Id_volunteer = token_Id_volunteer;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getVolunteerId() {
        return VolunteerId;
    }

    public void setVolunteerId(String volunteerId) {
        VolunteerId = volunteerId;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public GeoPoint getUserGeopoint() {
        return UserGeopoint;
    }

    public void setUserGeopoint(GeoPoint userGeopoint) {
        UserGeopoint = userGeopoint;
    }

    public GeoPoint getVolunteerGeopoint() {
        return VolunteerGeopoint;
    }

    public void setVolunteerGeopoint(GeoPoint volunteerGeopoint) {
        VolunteerGeopoint = volunteerGeopoint;
    }

    public VolunteerPostShow getPostShow() {
        return postShow;
    }

    public void setPostShow(VolunteerPostShow postShow) {
        this.postShow = postShow;
    }

    public String getToken_Id_volunteer() {
        return token_Id_volunteer;
    }

    public void setToken_Id_volunteer(String token_Id_volunteer) {
        this.token_Id_volunteer = token_Id_volunteer;
    }

    @Override
    public String toString() {
        return "VolunteerPickedFoods{" +
                "UserId='" + UserId + '\'' +
                ", VolunteerId='" + VolunteerId + '\'' +
                ", Status='" + Status + '\'' +
                ", UserGeopoint=" + UserGeopoint +
                ", VolunteerGeopoint=" + VolunteerGeopoint +
                ", postShow=" + postShow +
                ", token_Id_volunteer='" + token_Id_volunteer + '\'' +
                '}';
    }
}
