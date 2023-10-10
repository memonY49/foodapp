package com.example.foodservingapplication.utils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.foodservingapplication.MainScreen.FragmentOnBoarding2;
import com.example.foodservingapplication.MainScreen.FragmentOnboarding1;
import com.example.foodservingapplication.MainScreen.FragmentOnboarding3;

public class OnBoardingViewpager extends FragmentPagerAdapter {


    public OnBoardingViewpager(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new FragmentOnboarding1();
            case 1: return new FragmentOnBoarding2();
            case 2: return new FragmentOnboarding3();

        }




        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
