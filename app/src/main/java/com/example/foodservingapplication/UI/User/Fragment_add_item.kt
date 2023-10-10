package com.example.foodservingapplication.UI.User

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils.isEmpty
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.foodservingapplication.Models.UserPost
import com.example.foodservingapplication.Models.Volunteer
import com.example.foodservingapplication.Models.VolunteerPostShow
import com.example.foodservingapplication.R
import com.example.foodservingapplication.utils.Notifications.NotificationData
import com.example.foodservingapplication.utils.Notifications.PushNotification
import com.example.foodservingapplication.utils.Notifications.RetrofitInstance

import com.example.foodservingapplication.utils.SelectPhotoDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_add_item.*
import kotlinx.android.synthetic.main.fragment_add_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import worker8.com.github.radiogroupplus.RadioGroupPlus
import java.io.ByteArrayOutputStream
import java.util.*

class Fragment_add_item : Fragment(),
    SelectPhotoDialog.OnPhotoSelectedListerners{

   var TAG:String = "Fragment_add_item"

    //Location varaiables
    private var mFusedLocationCilent: FusedLocationProviderClient? = null
    var geoPoint:GeoPoint?=null

    //image variables
    var mSelectedBitmap: Bitmap? = null
    var mSelectedUri: Uri? = null

    //background class varaibles
    var uploadBtyes: ByteArray? = null
    var mProgress: Long = 0
    var finalVariable:Long = 100

    //Firebase varaiables
    private var mDb: FirebaseFirestore? = null
    var uri:String?=null
    var post: UserPost?=null

    //fragment widegets
    var customview:View?=null

    var parentLayout: View?=null

    //SharedPerfernces
    var sp:SharedPreferences?=null




//android.graphics.drawable.BitmapDrawable@10fe8e0




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_add_item, container, false)
        post = UserPost()
        customview = view
        HIDESHOWPRICEVISIBILTY()
        mDb = FirebaseFirestore.getInstance()
        sp = activity?.getSharedPreferences(getString(R.string.items_references), Context.MODE_PRIVATE)
        parentLayout=view.findViewById(android.R.id.content)


        view.radio_group.setOnCheckedChangeListener( { group, checkedId ->
            if (checkedId.equals(R.id.sell_add_post)){
                VISIBITYSHOWPRICE()
            } else if (checkedId.equals(R.id.donate_add_post)){
                HIDESHOWPRICEVISIBILTY()

            }
            Toast.makeText(context, " On checked change :",
                Toast.LENGTH_SHORT
            ).show()
        })



        //Location variable declaration
        mFusedLocationCilent = LocationServices.getFusedLocationProviderClient(activity!!.applicationContext)

       //location method
        view.location_btn.setOnClickListener({
        getLastKnownLocation()
            showProgressbar()


        })

        //post picture
        view.post_picture.setOnClickListener({
            Log.d("setOnClickListener","-------------------------------------")
            val dialog = SelectPhotoDialog()
            dialog.show(fragmentManager!!, "SelectPhotoDialog")
            dialog.setTargetFragment(this, 1)


        })


        //post button
        view.post_button.setOnClickListener({
            if (!AreWiegetsEmpty()) {
                if (mSelectedBitmap == null && mSelectedUri != null) {
                    //from memory
                    UploadPhoto(mSelectedUri!!)
                }
                // the case where there is the bitmap
                else if (mSelectedBitmap != null && mSelectedUri == null) {
                    //camera image
                    UploadPhoto(mSelectedBitmap!!)
                }


            }else {
                Toast.makeText(activity!!.applicationContext,"Please Fill all the fields Properly",Toast.LENGTH_SHORT).show()
            }
        })

        return view
    }




    fun clearAllWidegets(){
        item_name_add_post.setText("")
        decription_add_post.setText("")
        quantity_add_post.setText("")
        item_name_add_post.setText("")
        spinner.setSelection(0)
        Extra_location_info.setText("")
        radio_group.clearCheck()
        real_price_post.setText("")
        discount_price.setText("")
        post_picture.setImageResource(R.drawable.foodserve)



    }








    fun AreWiegetsEmpty():Boolean{
        if (item_name_add_post.text.toString().equals("") ||
            decription_add_post.text.toString().equals("") ||
            quantity_add_post.text.toString().equals("") ||
            getRadiobuttonSelection().equals("") ||
            spinner.selectedItemPosition == 0 ||
            Extra_location_info.text.toString().equals("")) {
            Log.d(TAG, "getttingg the drawable " + post_picture.drawable)

            return true
        }
        else {
            return  false
        }

    }

    fun getRadiobuttonSelection():String{
        var selection:String

        var id = radio_group.checkedRadioButtonId
        when(id){
            R.id.donate_add_post ->
                selection = "donate"
            R.id.sell_add_post -> {
                selection = "sell"
            }

            else ->
                selection= ""

        }

        return selection
    }

    fun VISIBITYSHOWPRICE(){

        real_price_post.visibility = View.VISIBLE
        discount_price.visibility = View.VISIBLE
        to_price.visibility= View.VISIBLE
    }
    fun HIDESHOWPRICEVISIBILTY(){

        customview!!.real_price_post.visibility = View.GONE
        customview!!.discount_price.visibility = View.GONE
        customview!!.to_price.visibility = View.GONE
    }



    //Background class call

    fun UploadPhoto(uri: Uri) {
        Log.d("UPLOAD PHOTO", "UI IS WORKING --------------------------------------------------")
        var resize = BackgroundImageResize()
        resize.execute(uri)

    }
    fun UploadPhoto(bitmap: Bitmap){
        Log.d("UPLOAD PHOTO", "BITMAP  IS WORKING --------------------------------------------------")
        var resize = BackgroundImageResize(bitmap)
        var uri:Uri ?=null
        resize.execute(uri)

    }



    //get photo on the opening of the image
    override fun getImagePath(imagePath: Uri?) {
        Log.d("getImagePath", "getImagePath: setting the image to imageview")
        post_picture.setImageURI(imagePath)
        //assign to global variable
        mSelectedBitmap = null
        mSelectedUri = imagePath

    }

    override fun getImageBitmap(bitmap: Bitmap) {
        Log.d("getImageBitmap", "getImageBitmap: setting the image to imageview")
        post_picture.setImageBitmap(bitmap)
        //assign to a global variable
        mSelectedUri = null
        mSelectedBitmap = bitmap
    }


    //background class in order to compress the picture
    inner class BackgroundImageResize(): AsyncTask<Uri, Int, ByteArray>()
    {
        var mbitmap:Bitmap?=null
        constructor(bitmap: Bitmap) : this(){
            this.mbitmap = bitmap
        }
        override fun onPreExecute() {
            super.onPreExecute()
            Log.d("PRE EXCUTE ", "the working is on the wayy --------------------------------------------------")
        }


        @RequiresApi(Build.VERSION_CODES.P)
        override fun doInBackground(vararg p0: Uri?): ByteArray? {
            if (mbitmap==null){
                try {
                    //settting uri in bitmap
                    mbitmap = MediaStore.Images.Media.getBitmap(context!!.getContentResolver(), p0[0]!!)
                }catch (ex:Exception){
                    Log.d("doInBackground","tHE EXCeption in occurr in do in background *********************************************")
                }

            }
            var arraybtyes:ByteArray?=null
            Log.d("BeforeCompression", " The before comprression the size of btye array will be "+mbitmap!!.byteCount/1000000)
            arraybtyes = getByteFromBitmap(mbitmap!!,100)
            Log.d("After Compression", " The after  comprression the size of btye array will be "+arraybtyes!!.size)
            return arraybtyes
        }
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onPostExecute(result: ByteArray?) {
            super.onPostExecute(result)
            Log.d("Post EXCUTE ", "the working is on the wayy --------------------------------------------------")
            uploadBtyes=result
            //upload it to the firebase
            ExceutUploadTask()
        }
    }

    fun  getByteFromBitmap(bitmap:Bitmap, quality:Int):ByteArray{
        var stream: ByteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,quality,stream)
        return stream.toByteArray()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun ExceutUploadTask(){
        var uid = FirebaseAuth.getInstance().currentUser!!.uid.toString()
        Toast.makeText(context,"Uploading The image", Toast.LENGTH_SHORT).show()
        var postId = FirebaseDatabase.getInstance().getReference().push().key
        var storage = FirebaseStorage.getInstance().getReference().
        child("postpictures/users/"+ uid+"/"+postId+"/post_image")
        post!!.postID = postId
        var upload: UploadTask = storage.putBytes(uploadBtyes!!)
        upload.addOnSuccessListener {taskSnapshot: UploadTask.TaskSnapshot ->
            val FirebaseUri: Task<Uri> =
                storage.getDownloadUrl()
                    .addOnSuccessListener(OnSuccessListener<Uri> { uri ->
                        Log.d(
                            "ProfileUri",
                            "onSuccess:  the getttting the downloaded Url"+uri)
                        //store data in datbase
                        post!!.itemImage = uri.toString()


                        StoreImageInFirebaseUsersNode()

                    })
        }.addOnFailureListener(){exception ->
            Toast.makeText(context,"Could Not Upload Details", Toast.LENGTH_SHORT).show()
        }.addOnProgressListener(){taskSnapshot: UploadTask.TaskSnapshot ->
            var currentProgress = ((100*taskSnapshot.bytesTransferred)/taskSnapshot.totalByteCount)
            if (currentProgress > (mProgress +15)){
                mProgress = ((100*taskSnapshot.bytesTransferred)/taskSnapshot.totalByteCount)
                Toast.makeText(context,mProgress.toString()+"%", Toast.LENGTH_SHORT).show()
            }
        }

    }



    //send notification method this method will make request calll
    private fun CreateNotificationServiceCall(notification:PushNotification) = CoroutineScope(Dispatchers.IO)
        .launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
                if (response.isSuccessful){
                    Log.d(TAG,"Response getttting the responseeeee from the server: ${Gson().toJson(response)}")
                } else {
                    Log.e(TAG,response.errorBody().toString())
                }

            }catch (ex:java.lang.Exception){

                Log.e(TAG,"getttting thee error" + ex.printStackTrace())
            }
        }


      fun sendnotification(recipentToken:String) {
          val title = item_name_add_post.text.toString()
          val message = adress_tv.text.toString()+"\n"+Extra_location_info.text.toString()

          if (title.isNotEmpty() && message.isNotEmpty()){
              PushNotification(
                  NotificationData(title,message,post!!.postID),recipentToken

              ).also {
                  CreateNotificationServiceCall(it)
              }
          }
      }



    fun traversingthevolunteers(){
        mDb!!.collection(getString(R.string.users_collection)).whereEqualTo("type_of_user", "Volunteer")
            .get().addOnCompleteListener({task ->
                if (task.isSuccessful){
                    for (result in task.result!!.documents){
                        var recipentToken= result.get("token_id") as String
                        sendnotification(recipentToken)
                        Toast.makeText(activity!!.applicationContext,"Successfully the notification is send ",Toast.LENGTH_LONG).show()

                    }

                }else {
                    Log.d(TAG, "getting the exception"+ task.exception)
                    Toast.makeText(activity!!.applicationContext
                    ,"Something went wrong Could not notify the volunteer .Try Again"
                    ,Toast.LENGTH_LONG).show()


                }

            })




    }










    // store all things in the Firebase UsersPost node

    fun StoreImageInFirebaseUsersNode(){
        post!!.itemImage
        post!!.itemName =item_name_add_post.text.toString()
        post!!.descriptionOfItem = decription_add_post.text.toString()
        post!!.quantityOfItem =quantity_add_post.text.toString()
        post!!.geoPoint
        post!!.extraAddressInformation=Extra_location_info.text.toString()
        post!!.sellingMethod = getRadiobuttonSelection()
        post!!.sellingPoint = spinner.selectedItem.toString()
        post!!.postID
        post!!.timestamp = null
        post!!.realprice = real_price_post.text.toString()
        post!!.discountedprice = discount_price.text.toString()

        var postshowed:VolunteerPostShow = VolunteerPostShow()
        postshowed.itemImage = post!!.itemImage
        postshowed.itemName = post!!.itemName
        postshowed.quantityOfItem= post!!.quantityOfItem
        postshowed.descriptionOfItem = post!!.descriptionOfItem
        postshowed.sellingMethod = post!!.sellingMethod
        postshowed.sellingPoint = post!!.sellingPoint
        postshowed.discountedprice = post!!.discountedprice
        postshowed.address = adress_tv.text.toString()
        postshowed.extraAddressInformation = post!!.extraAddressInformation
        postshowed.userId = FirebaseAuth.getInstance().currentUser!!.uid
        postshowed.geoPoint = post!!.geoPoint
        postshowed.timestamp = null
        postshowed.pickedStatus=getString(R.string.status_false)
        postshowed.postId = post!!.postID


        mDb!!.collection(getString(R.string.posts_node)).document(post!!.postID).
        set(postshowed).addOnSuccessListener { res ->
            Log.d(TAG,"Saving Things"+ post!!.sellingPoint)
        }
        Log.d(TAG,"GETTTING THE THINGS " + post!!.sellingPoint)

        mDb!!.collection(getString(R.string.node_userpost)).document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection(getString(R.string.post)).document(post!!.postID).set(post!!)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully written!")
                Toast.makeText(context,"SucessFully Uploaded", Toast.LENGTH_SHORT).show()
                // send the notification here
                traversingthevolunteers()
               // clearAllWidegets()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error writing document", e)
            }
    }


    //get location method
    private fun getLastKnownLocation() {
        mFusedLocationCilent!!.lastLocation
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result != null) {
                        val location = task.result
                        var address: String = ""
                        try {
                            var geocoder:Geocoder = Geocoder(activity!!.applicationContext, Locale.getDefault())
                            geoPoint = GeoPoint(location!!.latitude, location.longitude)
                            var addresses:List<Address> = geocoder.getFromLocation(geoPoint!!.latitude,geoPoint!!.longitude,1)
                            address = addresses.get(0).getAddressLine(0)
                            Log.d(TAG, "onComplete: on complete listerner " + geoPoint!!.latitude)
                            Log.d(TAG, "onComplete: on complete longitude " + geoPoint!!.longitude)
                            hideProgressBar()
                            post!!.geoPoint = geoPoint
                        } catch (e: Exception) {
                            Log.d(TAG,"ERROR GRPC FAILED ")
                        }

                        adress_tv.setText(address)
                    }
                }else {
                    Snackbar.make(view!!, "Something went wrong.", Snackbar.LENGTH_SHORT).show()
                }
            }
    }


    fun hideProgressBar(){
        progress_bar_add_items_post.visibility = View.GONE
    }
    fun showProgressbar(){
        progress_bar_add_items_post.visibility = View.VISIBLE
    }




}

