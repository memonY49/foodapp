package com.example.foodservingapplication.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import java.util.ArrayList

class SectionPagerAdapter  : FragmentPagerAdapter {
    var fragmentList: MutableList<Fragment> =
        ArrayList()

    constructor(fm: FragmentManager) : super(fm)


    fun addFragment(fragment: Fragment) {
        fragmentList.add(fragment)
    }



    override fun getItem(position: Int): Fragment {
        return fragmentList.get(position)
    }

    override fun getCount(): Int {
        return fragmentList.size
    }


}