package com.example.foodservingapplication.UI.Volunteer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.foodservingapplication.R
import kotlinx.android.synthetic.main.fragment_volunteer_items.view.*

class Fragment_Volunteer_Items : Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_volunteer_items, container, false)
        Toast.makeText(context!!, "Items", Toast.LENGTH_SHORT).show()
        view.btn.setOnClickListener({
            Toast.makeText(context!!, "Items CLICKEDDDDDDDDD", Toast.LENGTH_SHORT).show()

        })

        return view

    }

}