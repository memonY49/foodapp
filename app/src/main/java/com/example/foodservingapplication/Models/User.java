package com.example.foodservingapplication.Models;

public class User {
    private String username;
    private  String email;
    private String phoneNumber;
    private  String TypeOfDonor;
    private  String userid;
    private String  Type_of_user;
    private String  profile_Image;
    private  String description;
    private String Number_of_items;
    private String Number_of_items_by_volunteer;
    private String privacy_type;
    private String password;
    private String token_id;

    public User(String username, String email, String phoneNumber, String typeOfDonor, String userid, String type_of_user, String profile_Image, String description, String number_of_items, String number_of_items_by_volunteer, String privacy_type, String password,String token_id) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        TypeOfDonor = typeOfDonor;
        this.userid = userid;
        Type_of_user = type_of_user;
        this.profile_Image = profile_Image;
        this.description = description;
        Number_of_items = number_of_items;
        Number_of_items_by_volunteer = number_of_items_by_volunteer;
        this.privacy_type = privacy_type;
        this.password = password;
        this.token_id = token_id;
    }

    public String getToken_id() {
        return token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPrivacy_type() {
        return privacy_type;
    }

    public void setPrivacy_type(String privacy_type) {
        this.privacy_type = privacy_type;
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

    public String getTypeOfDonor() {
        return TypeOfDonor;
    }

    public void setTypeOfDonor(String typeOfDonor) {
        TypeOfDonor = typeOfDonor;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getType_of_user() {
        return Type_of_user;
    }

    public void setType_of_user(String type_of_user) {
        Type_of_user = type_of_user;
    }

    public String getProfile_Image() {
        return profile_Image;
    }

    public void setProfile_Image(String profile_Image) {
        this.profile_Image = profile_Image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNumber_of_items() {
        return Number_of_items;
    }

    public void setNumber_of_items(String number_of_items) {
        Number_of_items = number_of_items;
    }

    public String getNumber_of_items_by_volunteer() {
        return Number_of_items_by_volunteer;
    }

    public void setNumber_of_items_by_volunteer(String number_of_items_by_volunteer) {
        Number_of_items_by_volunteer = number_of_items_by_volunteer;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", TypeOfDonor='" + TypeOfDonor + '\'' +
                ", userid='" + userid + '\'' +
                ", Type_of_user='" + Type_of_user + '\'' +
                ", profile_Image='" + profile_Image + '\'' +
                ", description='" + description + '\'' +
                ", Number_of_items='" + Number_of_items + '\'' +
                ", Number_of_items_by_volunteer='" + Number_of_items_by_volunteer + '\'' +
                '}';
    }

    public User() {
    }

}
