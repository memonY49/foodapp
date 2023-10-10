package com.example.foodservingapplication.UI.User

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.GridView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.foodservingapplication.R
import com.example.foodservingapplication.utils.GridImageAdapter
import com.example.foodservingapplication.utils.UniversalImageLoader
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.WanderingCubes
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.snippet_center_profile.view.*
import kotlinx.android.synthetic.main.snippet_top_profile.view.*


class Fragment_profile : Fragment(){
    var view2:View?=null
    val NUM_GRID_COLUMNS = 3

    //Firebase Variables
    private var mDb: FirebaseFirestore? = null
     var profileimageurl:String?=null
    var items:String?=null
    var volunteer_items:String?=null
    var username:String?=null
    var description:String?=null
    lateinit var itemId:ArrayList<String>




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view: View = inflater.inflate(R.layout.fragment_profile, container, false)
        mDb = FirebaseFirestore.getInstance()
        itemId = ArrayList()
        Toast.makeText(context!!, "The profile activity", Toast.LENGTH_SHORT).show()
        tempGridSetup(view)
        ShowProgressBar(view)
        getProfileImage(view)
        getPost(view)
        view.edit_profile_btn.setOnClickListener({
            val fragment: Fragment = Fragment_settings()

            val fragmentManager: FragmentManager = activity!!.supportFragmentManager
            val fragmentTransaction: FragmentTransaction =
                fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container_main, fragment)
            fragmentTransaction.addToBackStack(getString(R.string.fragment_search))
            fragmentTransaction.commit()



        })

        view.refresh_fragment_profile.setOnClickListener({
            tempGridSetup(view)
            getProfileImage(view)
            getPost(view)
        })


        return view
    }






















    fun tempGridSetup(view: View){
        var imgUrls:ArrayList<String> = ArrayList<String>()

        mDb!!.collection(getString(R.string.node_userpost)).
        document(FirebaseAuth.getInstance().currentUser!!.uid).
        collection("Posts").get().addOnSuccessListener { result ->
            for (documents in result){
                imgUrls.add(documents.get("itemImage") as String)
                itemId.add(documents.id)
            }
        }.addOnFailureListener { exception ->
            val parentLayout: View = view.findViewById(android.R.id.content)
            Snackbar.make(parentLayout, "Something went wrong.", Snackbar.LENGTH_SHORT).show()
        }

        setupImageGrid(imgUrls,view)

    }
    fun setupImageGrid(imgUrls:ArrayList<String>,view: View){
        var gridview:GridView = view.gridview
        var gridWidth = resources.displayMetrics.widthPixels;
         var imageWidth = gridWidth/NUM_GRID_COLUMNS;
        gridview.columnWidth = imageWidth
        var adapter:GridImageAdapter = GridImageAdapter(activity!!.applicationContext,R.layout.ticket_grid_images_profile,"",imgUrls)
        gridview.adapter = adapter

       gridview.setOnItemClickListener(OnItemClickListener { adapterView, view, i, l ->

           val fragment: Fragment = Fragment_View_Post()
           //line of code to pass data along with fragment
           val args = Bundle()
           args.putString(getString(R.string.intent_post_id_grid_view), itemId[i])
           fragment.setArguments(args)
           //newIntent(this , where to go )
           val fragmentManager: FragmentManager = activity!!.supportFragmentManager
           val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
           fragmentTransaction.replace(R.id.container_main, fragment)

           //fragmentTransaction.addToBackStack(getString(R.string.fragment_search))
           fragmentTransaction.commit()


           Log.d("GETTTING","getting the itemm position here" + itemId[i])


        })

        gridview.setOnItemLongClickListener(OnItemLongClickListener { arg0, arg1, i, arg3 ->
           //remove the item from the images array list and delete it from the firebase
            creatingDialog(i)
            false
        })


    }





    fun creatingDialog(position: Int) {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("Your Estimation")
        builder.setMessage("Are you sure you want to delete?")
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            Toast.makeText(context, "clicked yes", Toast.LENGTH_LONG).show()
            deletedocuments(position)

        }
        builder.setNegativeButton("No") { dialogInterface, which ->
            Toast.makeText(context, "clicked no", Toast.LENGTH_LONG).show()
        }.show()
    }





    fun deletedocuments(position:Int){

        Toast.makeText(activity!!.applicationContext,"PROCESSING TO DELETE DOCUMENT",Toast.LENGTH_SHORT).show()

        mDb!!.collection(getString(R.string.posts_node)).document(itemId[position])
            .delete()
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!")

            }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
        mDb!!.collection(getString(R.string.node_userpost))
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection("Posts").document(itemId[position])
            .delete().addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }

        itemId.remove(itemId[position])
        Toast.makeText(activity!!.applicationContext,"SUCCESFULLY DELETED",Toast.LENGTH_SHORT).show()
        tempGridSetup(view!!)





    }
    fun getProfileImage(view: View){
        mDb!!.collection(getString(R.string.users_collection)).
        document(FirebaseAuth.getInstance().currentUser!!.uid)
            .get().addOnSuccessListener({result ->
                profileimageurl = result.get("profile_Image") as String
                username = result.get("username") as String
                description = result.get("description") as String
                UniversalImageLoader.setImage(profileimageurl,view.profile_image,null,"")
                view.tv_number_volunteer.setText(volunteer_items)
                view.display_name.setText(username)
                view.description_profile.setText(description)
                hideProgressBar(view)
            }).addOnFailureListener({


            })
    }

    fun getPost(view: View){
        var noOfitems:Int=0
        mDb!!.collection(getString(R.string.node_userpost)).
        document(FirebaseAuth.getInstance().currentUser!!.uid)
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







    fun hideProgressBar(view: View){
        val progressBar = view.findViewById(R.id.profile_progressbar) as ProgressBar
        val doubleBounce: Sprite = WanderingCubes()
        progressBar.indeterminateDrawable = doubleBounce
        progressBar.visibility = View.GONE
    }
    fun ShowProgressBar(view: View){
        val progressBar = view.findViewById(R.id.profile_progressbar) as ProgressBar
        val doubleBounce: Sprite = WanderingCubes()
        progressBar.indeterminateDrawable = doubleBounce
        progressBar.visibility = View.VISIBLE
    }






}