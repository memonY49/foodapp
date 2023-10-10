package com.example.foodservingapplication.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.foodservingapplication.Models.User;
import com.example.foodservingapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserListAdapter extends ArrayAdapter<User> {
    private LayoutInflater inflater;
    private int layout_resource;
    private Context mContext;
    private List<User> mUserList;


    private static final String TAG = "UserListAdapter";
    public UserListAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
        super(context, resource, objects);
        mContext=context;
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         layout_resource= resource;
         mUserList=objects;



    }

    public  static class ViewHolder {
        TextView username,email;
        CircleImageView profileImage;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;

        if(convertView==null) {
            convertView = inflater.inflate(layout_resource, parent, false);
            holder = new ViewHolder();
            holder.username = convertView.findViewById(R.id.username_widget);
            holder.email = convertView.findViewById(R.id.email_widget);
            holder.profileImage = convertView.findViewById(R.id.profileImage_widget);

            convertView.setTag(holder);
        }
        else
        {
          holder=  (ViewHolder) convertView.getTag();
        }


        try {
            holder.username.setText(getItem(position).getUsername());
            holder.email.setText(getItem(position).getEmail());
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(getItem(position).getProfile_Image(), holder.profileImage);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return convertView;
    }
}
