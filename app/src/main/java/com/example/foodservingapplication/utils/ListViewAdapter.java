package com.example.foodservingapplication.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.foodservingapplication.Models.TrackLocationItem;
import com.example.foodservingapplication.R;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.RotatingPlane;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListViewAdapter  extends ArrayAdapter<TrackLocationItem> {
    private Context mContext;
    private LayoutInflater mInflater;
    private int layoutResource;
    private FirebaseFirestore mDb;

    public ListViewAdapter(@NonNull Context context, int resource, @NonNull List<TrackLocationItem> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutResource = resource;
    }


    private static class ViewHolder {
        TextView picklocation, date, status;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();
            holder.picklocation = convertView.findViewById(R.id.pickup_location_address);
            holder.date = convertView.findViewById(R.id.date_location);
            holder.status = convertView.findViewById(R.id.status_track_location);
            convertView.setTag(holder);


        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        try {

            holder.picklocation.setText(getItem(position).getAddress());
            holder.date.setText(getItem(position).getDate());
            holder.status.setText(getItem(position).getStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }



        return convertView;
    }

}
