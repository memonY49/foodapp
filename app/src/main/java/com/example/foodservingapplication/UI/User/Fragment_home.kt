package com.example.foodservingapplication.UI.User

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.foodservingapplication.Models.TrackLocationItem
import com.example.foodservingapplication.Models.VolunteerPostShow
import com.example.foodservingapplication.R
import com.example.foodservingapplication.utils.HomePagePostAdapter
import com.example.foodservingapplication.utils.ListViewAdapter
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.FadingCircle
import com.google.android.gms.dynamic.IFragmentWrapper
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.fragment_home.view.*
import java.util.*
import kotlin.collections.ArrayList

class Fragment_home : Fragment(){

    lateinit var listview:ListView
    lateinit var postShowed:VolunteerPostShow
     var  postloadedlist:ArrayList<VolunteerPostShow>?=null
    lateinit var firestore: FirebaseFirestore
    lateinit var adapter: HomePagePostAdapter
    val TAG = "Fragment_home"
    lateinit var progressBar:ProgressBar
    var userprivacy: String?=null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        Toast.makeText(context!!, "The home activity", Toast.LENGTH_SHORT).show()
        progressBar = view.findViewById(R.id.progress_bar_mainfeed_location)
        listview = view.findViewById(R.id.listview_home)
        postShowed = VolunteerPostShow()
        firestore = FirebaseFirestore.getInstance()
        postloadedlist = ArrayList()

        adapter = HomePagePostAdapter(activity!!.applicationContext,R.layout.ticket_show_post,postloadedlist!!)
        listview.adapter = adapter
        loadpost()

        view.refresh_fragment_home.setOnClickListener({
            adapter.notifyDataSetChanged()
            loadpost()

        })


        return view
    }



    fun loadpost(){
        ShowProgressBar()
        firestore.collection(getString(R.string.posts_node)).get().addOnSuccessListener {querysnaphot ->
            for (document in querysnaphot.documents){
                 postShowed = document.toObject<VolunteerPostShow>()!!
                 users(postShowed.userId)
                  if (userprivacy.equals(getString(R.string.privacy_public))){
                      addItems(postShowed.userId,postShowed.itemName,postShowed.sellingMethod,postShowed.discountedprice,
                          postShowed.timestamp,
                          postShowed.itemImage,
                          postShowed.descriptionOfItem,postShowed.postId)
                      adapter.notifyDataSetChanged()
                      hideProgressBar()
                  }else {
                      Log.d("tag","please dont add items ")
                  }

                hideProgressBar()
            }
        }.addOnFailureListener { exception ->
            Log.d(TAG,"GOT THE CRASH IN THE gettting data " + exception.printStackTrace())
        }
    }

    fun users(string:String){
        firestore.collection(getString(R.string.users_collection))
            .document(string).get().addOnSuccessListener {document ->
                if (document.get("privacy_type")as String == getString(R.string.privacy_public)){
                    userprivacy = getString(R.string.privacy_public)

                    Log.d(TAG,"THE PRIVACY IS PUBLIC")
                }else {
                   userprivacy = getString(R.string.privacy_private)
                    Log.d(TAG,"THE PRIVACY IS PRIVATE")
                }
            }. addOnFailureListener { exception ->
                Log.d(TAG,"GOT THE CRASH IN THE PRIVACY TYPE " + exception.printStackTrace())
                Toast.makeText(activity!!.applicationContext,"SOORRY FAILDED TO GET INFORMATION" +
                        " \n Check internet connection",Toast.LENGTH_LONG).show()
            }




    }




    fun addItems(usernames:String,itemname:String,typeofselling:String,discountedprice:String,date:Date,imagepath:String,description:String,postId:String){
        postloadedlist!!.add(
            VolunteerPostShow(imagepath,itemname,
            "",description,typeofselling,
            "",null,"",
             date,"",discountedprice,usernames,"","",postId
            )
        )
        adapter.notifyDataSetChanged()
         hideProgressBar()



    }

    fun hideProgressBar(){

        val doubleBounce: Sprite = FadingCircle()
        progressBar.indeterminateDrawable = doubleBounce
        progressBar.visibility = View.GONE
    }
    fun ShowProgressBar(){

        val doubleBounce: Sprite = FadingCircle()
        progressBar.indeterminateDrawable = doubleBounce
        progressBar.visibility = View.VISIBLE
    }






}