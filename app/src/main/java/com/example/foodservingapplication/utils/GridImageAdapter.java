package com.example.foodservingapplication.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.foodservingapplication.R;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Pulse;
import com.github.ybq.android.spinkit.style.RotatingPlane;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

public class GridImageAdapter  extends ArrayAdapter<String>{
    private Context mContext;
    private LayoutInflater mInflater;
    private  int layoutResource;
    private  String mAppend;
    private ArrayList<String> imgURLS;


    public GridImageAdapter(Context mContext ,int layoutResource, String mAppend, ArrayList<String> imgURLS) {
        super(mContext,layoutResource,imgURLS);
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mContext = mContext;

        this.layoutResource = layoutResource;
        this.mAppend = mAppend;
        this.imgURLS = imgURLS;
    }

    private  static class  ViewHolder{
        SquareImageViewWideget grids_images;
        ProgressBar mProgressBar;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final  ViewHolder holder;
        if (convertView == null){
            convertView = mInflater.inflate(layoutResource,parent,false);
            holder = new ViewHolder();
            ProgressBar progressBar = (ProgressBar)convertView.findViewById(R.id.gridview_progressbar);
            Sprite doubleBounce = new RotatingPlane();
            progressBar.setIndeterminateDrawable(doubleBounce);
            holder.mProgressBar = progressBar;
            holder.grids_images = (SquareImageViewWideget) convertView.findViewById(R.id.gridImageView);

            convertView.setTag(holder);


        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        String imgUrl = getItem(position);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(mAppend + imgUrl, holder.grids_images, new ImageLoadingListener() {
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




        return convertView;
    }
}
