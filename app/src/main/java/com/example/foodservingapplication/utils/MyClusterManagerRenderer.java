package com.example.foodservingapplication.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.example.foodservingapplication.Models.CustomMarker;
import com.example.foodservingapplication.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyClusterManagerRenderer extends DefaultClusterRenderer<CustomMarker> {
    private final IconGenerator iconGenerator;
    private final ImageView imageView;
    private final  int markerWidth;
    private final  int markerheight;
    private DisplayImageOptions options;



    public MyClusterManagerRenderer(Context context, GoogleMap map, ClusterManager<CustomMarker> clusterManager) {
        super(context, map, clusterManager);
        iconGenerator = new IconGenerator(context.getApplicationContext());
        imageView = new ImageView(context.getApplicationContext());
        markerWidth = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        markerheight = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(markerWidth,markerheight));
        int padding = (int) context.getResources().getDimension(R.dimen.custom_marker_padding);
        imageView.setPadding(padding,padding,padding,padding);
        iconGenerator.setContentView(imageView);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_android_black_24dp)
                .showImageForEmptyUri(R.drawable.ic_android_black_24dp)
                .showImageOnFail(R.drawable.ic_android_black_24dp)
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();



    }



    @Override
    protected void onBeforeClusterItemRendered(CustomMarker item, MarkerOptions markerOptions) {
       // UniversalImageLoader.setImage(item.getIconPicture(),imageView,null,"");
        ImageLoader.getInstance().displayImage(item.getIconPicture(), imageView, options);
        Bitmap icon = iconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getTitle());



    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<CustomMarker> cluster) {
        //set to be false later
        return false;
    }
    public  void  setUpdateMarker(CustomMarker customMarker){
        Marker marker = getMarker(customMarker);
        if (marker!=null){
            marker.setPosition(customMarker.getPosition());
        }
    }


    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }








}
