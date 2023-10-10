package com.example.foodservingapplication.UI.Volunteer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.foodservingapplication.Models.TrackLocationItem
import com.example.foodservingapplication.Models.VolunteerPostShow
import com.example.foodservingapplication.R
import com.example.foodservingapplication.UI.User.Fragment_View_Post
import com.example.foodservingapplication.utils.ListViewAdapter
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.FadingCircle
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.toolbar_track_locations.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TrackLocationVolunteer : AppCompatActivity() {
    lateinit var track_orders:ArrayList<TrackLocationItem>
    lateinit var listView: ListView
    lateinit var mDb:FirebaseFirestore
    lateinit var trackLocationItem: TrackLocationItem
    lateinit var post_showed:VolunteerPostShow
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_location_volunteer)
        track_orders = ArrayList()
        listView = findViewById(R.id.listview_track_orders_volunteer)
        mDb = FirebaseFirestore.getInstance()
        trackLocationItem = TrackLocationItem()
        post_showed = VolunteerPostShow()
        loadListView()
        backarrow_track_location.setOnClickListener({
            var intent: Intent = Intent(applicationContext,MainActivityVolunteer::class.java)
            startActivity(intent)
            finish()

        })
        refresh_btn_track_location.setOnClickListener({
            track_orders.clear()
            loadListView()
        })

        listView.setOnItemClickListener(AdapterView.OnItemClickListener { adapterView, view, i, l ->

        })




    }

    fun loadListView(){
        ShowProgressBar()
        mDb.collection(getString(R.string.VolunteerPicked_Foods)).
        document(FirebaseAuth.getInstance().currentUser!!.uid.toString())
            .collection(getString(R.string.picked_post_node)).orderBy("status")
            .get().addOnSuccessListener {querySnapshot ->
                for (document in querySnapshot.documents){
                    var post: Map<String, String>
                    var dateString: String = ""
                    try {
                        post = document.get("postShow") as Map<String,String>
                        var time:Timestamp = post.get("timestamp") as Timestamp
                        var formatter:SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy h:mm a");
                        dateString = formatter.format(time.toDate());
                        Log.d("THE TAG", dateString)
                        addItems(post.get("address")!!,dateString,document.get("status") as String , document.id)
                    } catch (e: Exception) {
                       // Toast.makeText(applicationContext,"ERRROR OCCURED",Toast.LENGTH_LONG).show()
                    }


                }
                Log.d("TAG","THE POST SHOWED" +querySnapshot.size())
            }.addOnFailureListener {exception ->
                Log.d("TAG","THE EXCEPTION OF"+exception)

            }




    }

    fun addItems(address:String, date: String, status:String,postId:String){

        track_orders.add(TrackLocationItem(address,date,status,postId))
        hideProgressBar()
        var adapter: ListViewAdapter = ListViewAdapter(applicationContext,R.layout.ticket_volunteer_location,track_orders)
        listView.adapter = adapter



    }



    fun hideProgressBar(){
        val progressBar = findViewById(R.id.progress_bar_track_location) as ProgressBar
        val doubleBounce: Sprite = FadingCircle()
        progressBar.indeterminateDrawable = doubleBounce
        progressBar.visibility = View.GONE
    }
    fun ShowProgressBar(){
        val progressBar = findViewById(R.id.progress_bar_track_location) as ProgressBar
        val doubleBounce: Sprite = FadingCircle()
        progressBar.indeterminateDrawable = doubleBounce
        progressBar.visibility = View.VISIBLE
    }





}
