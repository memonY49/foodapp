package com.example.foodservingapplication.UI.User

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.foodservingapplication.Models.User
import com.example.foodservingapplication.Models.VolunteerPostShow
import com.example.foodservingapplication.R
import com.example.foodservingapplication.utils.UniversalImageLoader
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.FadingCircle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.fragment_view_post.view.*
import kotlinx.android.synthetic.main.snippet_view_post.*
import kotlinx.android.synthetic.main.snippet_viewpost_toolbar.*
import kotlinx.android.synthetic.main.snippet_viewpost_toolbar.view.*
import java.text.SimpleDateFormat
import java.util.*


class Fragment_View_Post : Fragment(){
    lateinit var value:String
    val TAG = "Fragment_View_Post"
    lateinit var mDb:FirebaseFirestore
    lateinit var volunteerPostShow:VolunteerPostShow
    lateinit var user:User
    lateinit var progressBar:ProgressBar


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_view_post, container, false)
        Toast.makeText(context!!, "The Fragmennt activity", Toast.LENGTH_SHORT).show()
        mDb = FirebaseFirestore.getInstance()
        user = User()
        volunteerPostShow = VolunteerPostShow()
        progressBar = view.findViewById(R.id.view_post_progressbar)

        try {
            value = arguments!!.getString(getString(R.string.intent_post_id_grid_view))!!
            getTheUsername()
            getTheDataFromDatabase()
        } catch (e: Exception) {

        }
        view.backButton_viewpost.setOnClickListener({
            view.relativelayout_viewPost.visibility = View.GONE
        })
        return view
    }



    fun getTheDataFromDatabase(){
        ShowProgressBar()
        mDb.collection(getString(R.string.posts_node))
            .document(value).get().addOnSuccessListener{dataSnaphot ->
                volunteerPostShow = dataSnaphot.toObject<VolunteerPostShow>()!!
                logs(volunteerPostShow.toString())
                settingOnWidegets(volunteerPostShow.itemName,
                    volunteerPostShow.descriptionOfItem,volunteerPostShow.itemImage
                ,volunteerPostShow.timestamp)
               hideProgressBar()

            }.addOnFailureListener { exception ->
                toast("EXCEPTION OCCURED HERE")

            }



    }

    fun getTheUsername(){
        mDb.collection(getString(R.string.users_collection))
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .get().addOnSuccessListener {
                user = it.toObject<User>()!!
                username_post_item.setText(user.username)
                UniversalImageLoader.setImage(user.profile_Image,profile_post_user_image,null,"")
            }.addOnFailureListener { exception ->  }
    }

    fun settingOnWidegets(title:String,description:String,imagepath:String,date:Date){
        title_post.text = title
        post_text_description.text = description
        UniversalImageLoader.setImage(imagepath,image_view_post,null,"")

        var formatter: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy h:mm a");
       var Mydate = formatter.format(date);
        Log.d("THE TAG", Mydate)
        uploaded_time.setText(Mydate)






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








    fun logs(string: String){
        Log.d(TAG,string)

    }
    fun toast(string: String){
        Toast.makeText(activity,string,Toast.LENGTH_SHORT).show()
    }





}