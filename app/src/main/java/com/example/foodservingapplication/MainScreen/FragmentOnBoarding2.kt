package com.example.foodservingapplication.MainScreen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.foodservingapplication.R
import kotlinx.android.synthetic.main.fragment_fragment_on_boarding2.view.*


class FragmentOnBoarding2 : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view:View= inflater.inflate(R.layout.fragment_fragment_on_boarding2, container, false)

        view.skip_on_boarding_2.setOnClickListener({
            var intent: Intent = Intent(activity!!,LoginScreen::class.java)
            startActivity(intent)



        })
        return view


    }


}
