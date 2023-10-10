package com.example.foodservingapplication.Models;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class UserPost {
   private String ItemImage;
   private String ItemName;
   private String QuantityOfItem;
   private String DescriptionOfItem;
   private String SellingMethod;
   private String SellingPoint;
   private GeoPoint geoPoint;
   private String ExtraAddressInformation;
    private  @ServerTimestamp Date timestamp;
    private String postID;
    private String realprice;
    private String discountedprice;


    public UserPost(String itemImage, String itemName, String quantityOfItem, String descriptionOfItem, String sellingMethod, String sellingPoint, GeoPoint geoPoint, String extraAddressInformation, Date timestamp, String postID, String realprice, String discountedprice) {
        ItemImage = itemImage;
        ItemName = itemName;
        QuantityOfItem = quantityOfItem;
        DescriptionOfItem = descriptionOfItem;
        SellingMethod = sellingMethod;
        SellingPoint = sellingPoint;
        this.geoPoint = geoPoint;
        ExtraAddressInformation = extraAddressInformation;
        this.timestamp = timestamp;
        this.postID = postID;
        this.realprice = realprice;
        this.discountedprice = discountedprice;
    }

    public String getItemImage() {
        return ItemImage;
    }

    public void setItemImage(String itemImage) {
        ItemImage = itemImage;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public String getQuantityOfItem() {
        return QuantityOfItem;
    }

    public void setQuantityOfItem(String quantityOfItem) {
        QuantityOfItem = quantityOfItem;
    }

    public String getDescriptionOfItem() {
        return DescriptionOfItem;
    }

    public void setDescriptionOfItem(String descriptionOfItem) {
        DescriptionOfItem = descriptionOfItem;
    }

    public String getSellingMethod() {
        return SellingMethod;
    }

    public void setSellingMethod(String sellingMethod) {
        SellingMethod = sellingMethod;
    }

    public String getSellingPoint() {
        return SellingPoint;
    }

    public void setSellingPoint(String sellingPoint) {
        SellingPoint = sellingPoint;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public String getExtraAddressInformation() {
        return ExtraAddressInformation;
    }

    public void setExtraAddressInformation(String extraAddressInformation) {
        ExtraAddressInformation = extraAddressInformation;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getRealprice() {
        return realprice;
    }

    public void setRealprice(String realprice) {
        this.realprice = realprice;
    }

    public String getDiscountedprice() {
        return discountedprice;
    }

    public void setDiscountedprice(String discountedprice) {
        this.discountedprice = discountedprice;
    }

    @Override
    public String toString() {
        return "UserPost{" +
                "ItemImage='" + ItemImage + '\'' +
                ", ItemName='" + ItemName + '\'' +
                ", QuantityOfItem='" + QuantityOfItem + '\'' +
                ", DescriptionOfItem='" + DescriptionOfItem + '\'' +
                ", SellingMethod='" + SellingMethod + '\'' +
                ", SellingPoint='" + SellingPoint + '\'' +
                ", geoPoint=" + geoPoint +
                ", ExtraAddressInformation='" + ExtraAddressInformation + '\'' +
                ", timestamp=" + timestamp +
                ", postID='" + postID + '\'' +
                ", realprice='" + realprice + '\'' +
                ", discountedprice='" + discountedprice + '\'' +
                '}';
    }

    public UserPost() {
    }
}
