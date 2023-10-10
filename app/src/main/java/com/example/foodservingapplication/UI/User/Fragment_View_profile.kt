package com.example.foodservingapplication.UI.User

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.foodservingapplication.Models.UserPost
import com.example.foodservingapplication.R
import com.example.foodservingapplication.utils.GridImageAdapter
import com.example.foodservingapplication.utils.UniversalImageLoader
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.DoubleBounce
import com.github.ybq.android.spinkit.style.WanderingCubes
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.snippet_center_view_profile.view.*
import kotlinx.android.synthetic.main.snippet_top_view_profile.view.*
import java.util.*
import kotlin.collections.ArrayList


class Fragment_View_profile : Fragment(){
    val TAG = "Fragment_Search"

   lateinit var  value:String

    val NUM_GRID_COLUMNS = 3

    //Firebase Variables
    private var mDb: FirebaseFirestore? = null
    var profileimageurl:String?=null
    var items:String?=null
    var volunteer_items:String?=null
    var username:String?=null
    var description:String?=null
     var toolbar:androidx.appcompat.widget.Toolbar?=null
    var backarrow:ImageView?=null
    var profile_name:TextView?=null
    var postList:ArrayList<UserPost>?=null
    var progressBar:ProgressBar?=null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_view_profile, container, false)
        Toast.makeText(context!!, "The Fragment activity", Toast.LENGTH_SHORT).show()
        toolbar = view.findViewById(R.id.profile_Toolbar)
        backarrow = view.findViewById(R.id.backarrow_view_profile)
        profile_name = view.findViewById(R.id.view_profile_name)
         progressBar = view.findViewById(R.id.profile_progressbar) as ProgressBar

        postList = ArrayList()
        try {
             value = arguments!!.getString(getString(R.string.intent_user_id_search))!!
            mDb = FirebaseFirestore.getInstance()
            Toast.makeText(context!!, "The profile activity", Toast.LENGTH_SHORT).show()
            setupToolbar()

            getProfileImage(view)
            tempGridSetup(view)
            ShowProgressBar()
            getPost(view)

        } catch (e: Exception) {
            Log.d("THE THINGS","THE EEROROOROR OCCUUURED"+e.printStackTrace())
        }

        return view
    }



    private fun setupToolbar() {
        (Objects.requireNonNull(activity) as MainActivity).setSupportActionBar(toolbar)
        backarrow!!.setOnClickListener(View.OnClickListener {
           logs("ON CLICKK "+"NAVIGATINGG BACKK ")
            activity!!.onBackPressed()
        })

        mDb!!.collection(getString(R.string.users_collection)).document(value)
            .get().addOnSuccessListener {
                profile_name!!.setText(it.get("username")as String)
            }.addOnFailureListener {
            }


    }

    fun tempGridSetup(view: View){
        ShowProgressBar()
        logs("INNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN")
        var imgUrls:ArrayList<String> = ArrayList<String>()

        mDb!!.collection(getString(R.string.node_userpost)).
        document(value).
        collection("Posts").get().addOnSuccessListener { result ->
            for (documents in result){
                imgUrls.add(documents.get("itemImage") as String)
                postList!!.add(documents.toObject<UserPost>())

            }
        }.addOnFailureListener {
            val parentLayout: View = view.findViewById(android.R.id.content)
            Snackbar.make(parentLayout, "Something went wrong.", Snackbar.LENGTH_SHORT).show()
        }
        hideProgressBar()

        setupImageGrid(imgUrls,view)

    }
    fun setupImageGrid(imgUrls:ArrayList<String>,view: View){
        logs("ITEMSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS")
        ShowProgressBar()
        var gridview: GridView = view.gridview
        var gridWidth = resources.displayMetrics.widthPixels;
        var imageWidth = gridWidth/NUM_GRID_COLUMNS;
        gridview.columnWidth = imageWidth


        var adapter: GridImageAdapter = GridImageAdapter(activity!!.applicationContext,R.layout.ticket_grid_images_profile,"",imgUrls)
        gridview.adapter = adapter
        hideProgressBar()

        gridview.setOnItemClickListener(OnItemClickListener { adapterView, view, i, l ->

            val fragment: Fragment = Fragment_View_Post()
            val args = Bundle()
            args.putString(getString(R.string.intent_post_id_grid_view), postList!!.get(i).postID)
            logs(postList!!.get(i).postID+"----------------------------------------------------------")
            fragment.setArguments(args)
            val fragmentManager: FragmentManager = activity!!.supportFragmentManager
            val fragmentTransaction: FragmentTransaction =
                fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container_main, fragment)
            fragmentTransaction.addToBackStack(getString(R.string.fragment_search))
        fragmentTransaction.commit()

        })

    }
    fun getProfileImage(view: View){
        mDb!!.collection(getString(R.string.users_collection)).
        document(value)
            .get().addOnSuccessListener({result ->
                profileimageurl = result.get("profile_Image") as String
                username = result.get("username") as String
                description = result.get("description") as String
                UniversalImageLoader.setImage(profileimageurl,view.profile_image,null,"")
                view.tv_number_volunteer.setText(volunteer_items)
                view.display_name.setText(username)
                view.description_profile.setText(description)
                hideProgressBar()
            }).addOnFailureListener({


            })
    }

    fun getPost(view: View){
        var noOfitems:Int=0
        mDb!!.collection(getString(R.string.node_userpost)).
        document(value)
            .collection("Posts")
            .get().addOnSuccessListener({result ->
                for (documents in result){
                    noOfitems++
                }
                view.tv_number_items.text = noOfitems.toString()
            }).addOnFailureListener { exception ->
                val parentLayout: View = view.findViewById(android.R.id.content)
                Snackbar.make(parentLayout, "Something went wrong.", Snackbar.LENGTH_SHORT).show()

            }
    }







    fun hideProgressBar(){

        val doubleBounce: Sprite = DoubleBounce()
        progressBar!!.indeterminateDrawable = doubleBounce
        progressBar!!.visibility = View.GONE
    }
    fun ShowProgressBar(){

        val doubleBounce: Sprite = DoubleBounce()
        progressBar!!.indeterminateDrawable = doubleBounce
        progressBar!!.visibility = View.VISIBLE
    }


    fun logs(string: String){
        Log.d(TAG,string)

    }
    fun toast(string: String){
        Toast.makeText(activity,string,Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        logs("ON STARTTT IS WORKINGGGGGGGGGGG---------------------------------")
        tempGridSetup(view!!)
    }


}