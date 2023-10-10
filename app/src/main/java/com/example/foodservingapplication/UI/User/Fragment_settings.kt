package com.example.foodservingapplication.UI.User

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.foodservingapplication.MainScreen.LoginScreen
import com.example.foodservingapplication.Models.User
import com.example.foodservingapplication.R
import com.example.foodservingapplication.utils.ConfirmDialog
import com.example.foodservingapplication.utils.SelectPhotoDialog
import com.example.foodservingapplication.utils.UniversalImageLoader
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.FadingCircle
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import kotlinx.android.synthetic.main.snippet_toolbar_edit_profile.view.*
import java.io.ByteArrayOutputStream

class Fragment_settings : Fragment(),
    SelectPhotoDialog.OnPhotoSelectedListerners,ConfirmDialog.OnConfirmDialogListerner{
    val TAG = "Fragment_settings"
    var mDb:FirebaseFirestore?=null
    var user:User?=null

    //image variables
    var mSelectedBitmap: Bitmap? = null
    var mSelectedUri: Uri? = null

    //background class varaibles
    var uploadBtyes: ByteArray? = null
    var mProgress: Long = 0
    var finalVariable:Long = 100

    var IsPhotoChanged:Boolean =false;

    var password:String = ""




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_settings, container, false)
        Toast.makeText(context!!, "The Settings activity", Toast.LENGTH_SHORT).show()
        mDb = FirebaseFirestore.getInstance()
        user = User()
        gettingThedataFromFirebase(view)

        view.saving_changes_edit_profile.setOnClickListener({
            Saving_Changes_function()

        })
        view.changeProfilePhoto.setOnClickListener {
        //now dialog box will open
            val dialog = SelectPhotoDialog()
            dialog.show(fragmentManager!!, "SelectPhotoDialog")
            dialog.setTargetFragment(this, 1)
            IsPhotoChanged = true

        }

        view.backarrow_edit_profile.setOnClickListener({
           activity!!.onBackPressed()

        })
        view.edit_profile_logout_image.setOnClickListener({
         if (view.logout_btn.visibility == View.VISIBLE){
             view.logout_btn.visibility= View.GONE
         }else if(view.logout_btn.visibility == View.GONE){
             view.logout_btn.visibility= View.VISIBLE
         }


        })
        view.logout_btn.setOnClickListener({
            Logout()

        })




        return view
    }




    fun Logout(){
        var firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut()
        var intent = Intent(activity!!.applicationContext,LoginScreen::class.java)
        startActivity(intent)
        activity!!.finish()
    }


    fun Saving_Changes_function(){
        if (!checkingIfChangesMade()){
              Toast.makeText(activity!!.applicationContext,"YESS CHANGES ARE MADE",Toast.LENGTH_SHORT).show()
            val dialog = ConfirmDialog()
            dialog.show(fragmentManager!!, "Confirm Dialog")
            dialog.setTargetFragment(this, 1)

        }else {
              Toast.makeText(activity!!.applicationContext,"NO CHANGES ARE MADE",Toast.LENGTH_SHORT).show()
          }

    }
    override fun getPasswordFromDialog(password: String) {
          this.password = password
        mDb!!.collection(getString(R.string.users_collection)).
        document(FirebaseAuth.getInstance().currentUser!!.uid)
            .get().addOnSuccessListener { documentSnapshot: DocumentSnapshot? ->

               var getpassword = documentSnapshot!!.get("password") as String
                if (getpassword.equals(password)){

                    if (mSelectedBitmap == null && mSelectedUri != null) {
                        //from memory
                        UploadPhoto(mSelectedUri!!)
                    }
                    // the case where there is the bitmap
                    else if (mSelectedBitmap != null && mSelectedUri == null) {
                        //camera image
                        UploadPhoto(mSelectedBitmap!!)
                    }else {
                        updateWidegets(documentSnapshot.get("profile_Image") as String)



                    }




                }else{

                    Toast.makeText(activity!!.applicationContext,"Sorry password is not correct",Toast.LENGTH_SHORT).show()
                }



            }.addOnFailureListener { exception ->
                Toast.makeText(activity!!.applicationContext,"Sorry Can't check the password" +
                        "\n Update settings later ON",Toast.LENGTH_SHORT).show()
            }




    }

    /*
    * the widegets we want here are
    * profileImage
    * username
    * description
    * email
    * phonenumber
    * privacy type
    * password for verification
    * */
    fun gettingThedataFromFirebase(view: View){
        ShowProgressBar(view)
        mDb!!.collection(getString(R.string.users_collection)).
        document(FirebaseAuth.getInstance().currentUser!!.uid)
            .get().addOnSuccessListener { result->
                user!!.profile_Image = result.get("profile_Image") as String
                user!!.username = result.get("username") as String
                user!!.description = result.get("description") as String
                user!!.email = result.get("email") as String
                user!!.privacy_type = result.get("privacy_type") as String
                user!!.phoneNumber = result.get("phoneNumber") as String
                Log.d(TAG,"successfullly get the information------------------------------------------"
                + user!!.profile_Image +user!!.username + user!!.description+user!!.email+  user!!.privacy_type+ user!!.phoneNumber
                )
                settingThewidegets(user!!.username,user!!.description,user!!.email,user!!.privacy_type,user!!.phoneNumber,user!!.profile_Image,view)
               hideProgressBar(view)
            }.addOnFailureListener { exception ->
                Log.d(TAG,"exception occurs here "+ exception.printStackTrace())
                Toast.makeText(activity!!.applicationContext,"Sorry Can't get the information" +
                        "\n Check your internet connection",Toast.LENGTH_SHORT).show()
            }
    }

    fun checkingIfChangesMade():Boolean{
        if (user!!.username.equals(user_name_edit_donor.text.toString())
            &&(user!!.description.equals(description_edit_donor.text.toString()))
            &&(user!!.email.equals(email_edit_donor.text.toString()))
            &&(user!!.phoneNumber.equals(phone_number_edit_donor.text.toString()))
            &&(user!!.privacy_type.equals(getRadiobuttonSelection()))
                   ){
            return true
            ///go for process
        }else {
            return false//means any updation occur
        }

    }


    fun getRadiobuttonSelection():String{
        var selection:String
        var id = radio_group_edit_donor.checkedRadioButtonId
        when(id){
            R.id.public_edit_settings_donor ->
                selection = getString(R.string.privacy_public)
            R.id.private_edit_settings_donor ->
                selection = getString(R.string.privacy_private)
            else ->
                selection= ""
        }

        return selection
    }




    fun settingThewidegets(username:String,description:String,email:String,privacy_type:String,phoneNumber:String,profileImage: String,view: View){

        view.user_name_edit_donor.setText(username)
        view.description_edit_donor.setText(description)
        view.email_edit_donor.setText(email)
        view.phone_number_edit_donor.setText(phoneNumber)
         try {
             if (privacy_type.equals(getString(R.string.privacy_public))){
                 public_edit_settings_donor.isChecked = true


             }else if (privacy_type.equals(getString(R.string.privacy_private))) {
                 private_edit_settings_donor.isChecked = true

             }
         }catch (ex:Exception){
             Log.d(TAG,"EXCEPTION OCCURED")
         }

        UniversalImageLoader.setImage(profileImage,profile_photo_user_donor,null,"")
    }




    fun hideProgressBar(view: View){
        val progressBar = view.findViewById(R.id.progress_bar_edit_profile_donor) as ProgressBar
        val doubleBounce: Sprite = FadingCircle()
        progressBar.indeterminateDrawable = doubleBounce
        progressBar.visibility = View.GONE
    }
    fun ShowProgressBar(view: View){
        val progressBar = view.findViewById(R.id.progress_bar_edit_profile_donor) as ProgressBar
        val doubleBounce: Sprite = FadingCircle()
        progressBar.indeterminateDrawable = doubleBounce
        progressBar.visibility = View.VISIBLE
    }



    fun updateWidegets(profileImage:String){
        mDb!!.collection(getString(R.string.users_collection)).
        document(FirebaseAuth.getInstance().currentUser!!.uid).update(mapOf(
            "username" to user_name_edit_donor.text.toString(),
            "description" to description_edit_donor.text.toString(),
            "privacy_type" to getRadiobuttonSelection(),
            "phoneNumber" to phone_number_edit_donor.text.toString(),
            "email" to email_edit_donor.text.toString(),
            "profile_Image" to  profileImage
        )).addOnSuccessListener { result->
                Toast.makeText(activity!!.applicationContext,"SUCCESSFULLY UPDATE",Toast.LENGTH_SHORT).show()
            settingThewidegets(user_name_edit_donor.text.toString(),
                description_edit_donor.text.toString(),
                email_edit_donor.text.toString(),
                getRadiobuttonSelection(),
                phone_number_edit_donor.text.toString(),profileImage,view!!)
        }.addOnFailureListener { exception ->
                Toast.makeText(activity!!.applicationContext,"FAILED TO UPDATE",Toast.LENGTH_SHORT).show()
            }

    }
    //Image upload work

    //get photo on the opening of the image
    override fun getImagePath(imagePath: Uri?) {
        Log.d("getImagePath", "getImagePath: setting the image to imageview")
        profile_photo_user_donor.setImageURI(imagePath)
        //assign to global variable
        mSelectedBitmap = null
        mSelectedUri = imagePath

    }

    override fun getImageBitmap(bitmap: Bitmap) {
        Log.d("getImageBitmap", "getImageBitmap: setting the image to imageview")
        profile_photo_user_donor.setImageBitmap(bitmap)
        //assign to a global variable
        mSelectedUri = null
        mSelectedBitmap = bitmap
    }



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
        child("postpictures/users/profilephoto/"+ uid+"/"+"/profile_photo")
        var upload: UploadTask = storage.putBytes(uploadBtyes!!)
        upload.addOnSuccessListener {taskSnapshot: UploadTask.TaskSnapshot ->
            val FirebaseUri: Task<Uri> =
                storage.getDownloadUrl()
                    .addOnSuccessListener(OnSuccessListener<Uri> { uri ->
                        Log.d(
                            "ProfileUri",
                            "onSuccess:  the getttting the downloaded Url"+uri)
                        //store data in datbase
                        user!!.profile_Image = uri.toString()

                        updateWidegets(uri.toString())
                        //StoreImageInFirebaseUsersNode()
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



}