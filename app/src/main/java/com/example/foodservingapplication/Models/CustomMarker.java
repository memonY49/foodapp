package com.example.foodservingapplication.Models;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class CustomMarker implements ClusterItem {
    private LatLng position;
    private  String title;
    private  String snippet;
    private  String iconPicture;

    public CustomMarker(LatLng position, String title, String snippet, String iconPicture){
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.iconPicture = iconPicture;
       }

    public CustomMarker() {
    }
    @Override
    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getIconPicture() {
        return iconPicture;
    }

    public void setIconPicture(String iconPicture) {
        this.iconPicture = iconPicture;
    }

}
