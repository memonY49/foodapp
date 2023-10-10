package com.example.foodservingapplication.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.foodservingapplication.Models.TrackLocationItem;
import com.example.foodservingapplication.Models.VolunteerPostShow;
import com.example.foodservingapplication.R;
import com.example.foodservingapplication.UI.Volunteer.Post_Showed;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HomePagePostAdapter extends ArrayAdapter<VolunteerPostShow> {
    private Context mContext;
    private LayoutInflater mInflater;
    private int layoutResource;
    private FirebaseFirestore mDb;


    public HomePagePostAdapter(@NonNull Context context, int resource, @NonNull List<VolunteerPostShow> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutResource = resource;
    }


    private static class ViewHolder {
        TextView username,itemname,typeofselling,discountedprice,uploadedtime,description;
        SquareImageViewWideget postedimage;
        ProgressBar mProgressBar;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent ) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();
            holder.username = convertView.findViewById(R.id.username);
            holder.itemname = convertView.findViewById(R.id.item_name_ticket);
            holder.typeofselling = convertView.findViewById(R.id.typeofsell);
            holder.discountedprice = convertView.findViewById(R.id.discounted_ticket_show_post);
            holder.description = convertView.findViewById(R.id.post_text_description);
            holder.postedimage = convertView.findViewById(R.id.post_showed_mainfeed_image);
            holder.mProgressBar = (ProgressBar) convertView.findViewById(R.id.ticket_mainfeed_post_show_progressbar) ;
            holder.uploadedtime = convertView.findViewById(R.id.uploaded_time);
            convertView.setTag(holder);
        }else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        try {

                setUsername(position,holder);
                holder.itemname.setText(getItem(position).getItemName());
                holder.description.setText(getItem(position).getDescriptionOfItem());

                holder.typeofselling.setText(getItem(position).getSellingMethod());


                if (getItem(position).getSellingMethod()=="sell") {
                    holder.discountedprice.setText(getItem(position).getDiscountedprice() + "RS");
                }else {
                    holder.discountedprice.setVisibility(View.GONE);
                }

                //for the images in the post here universal imageloader is used
            String imgUrl = getItem(position).getItemImage();
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage("" + imgUrl, holder.postedimage, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    if (holder.mProgressBar!=null){
                        holder.mProgressBar.setVisibility(View.VISIBLE);

                    }
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    if (holder.mProgressBar!=null){
                        holder.mProgressBar.setVisibility(View.GONE);

                    }
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if (holder.mProgressBar!=null){
                        holder.mProgressBar.setVisibility(View.GONE);

                    }
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    if (holder.mProgressBar!=null){
                        holder.mProgressBar.setVisibility(View.GONE);

                    }
                }
            });

            //for the date
            Date time =  getItem(position).getTimestamp() ;
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy h:mm a");
            String dateString = formatter.format(time);
            Log.d("THE TAG", dateString);
            holder.uploadedtime.setText(dateString);

            //for username



        } catch (Exception e) {
                e.printStackTrace();
            }


        return convertView;
    }










     void setUsername(int position, final ViewHolder holder){


        mDb = FirebaseFirestore.getInstance();
        mDb.collection(getContext().getString(R.string.users_collection))
                .document(getItem(position).getUserId()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
                        String username = (String) snapshot.get("username");
                        holder.username.setText(username);


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();

            }
        });

    }





}
