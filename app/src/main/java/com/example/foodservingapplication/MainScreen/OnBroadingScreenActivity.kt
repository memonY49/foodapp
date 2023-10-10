package com.example.foodservingapplication.MainScreen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.cuberto.liquid_swipe.LiquidPager
import com.example.foodservingapplication.R
import com.example.foodservingapplication.utils.OnBoardingViewpager
import kotlinx.android.synthetic.main.activity_on_broading_screen.*

class OnBroadingScreenActivity : AppCompatActivity() {
    var pager:LiquidPager?=null
    var viewPager:OnBoardingViewpager?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_broading_screen)
        pager = pager_liquid
        viewPager = OnBoardingViewpager(supportFragmentManager,1)
        pager!!.adapter= viewPager
    }
}
