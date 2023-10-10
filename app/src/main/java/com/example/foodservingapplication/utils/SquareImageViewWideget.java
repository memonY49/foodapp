package com.example.foodservingapplication.utils;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class SquareImageViewWideget extends AppCompatImageView {

    public SquareImageViewWideget(@NonNull Context context) {
        super(context);
    }

    public SquareImageViewWideget(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageViewWideget(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //this method will basically set the same
        // width and the height so that we pass the both parameters same (widthMeasureSpec)
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
