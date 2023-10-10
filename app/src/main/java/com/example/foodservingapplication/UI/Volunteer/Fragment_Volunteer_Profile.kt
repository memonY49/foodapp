package com.example.foodservingapplication.UI.Volunteer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.foodservingapplication.R

class Fragment_Volunteer_Profile :Fragment(){


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_volunteer_profile, container, false)
        Toast.makeText(context!!, "Items", Toast.LENGTH_SHORT).show()
        return view
    }

}