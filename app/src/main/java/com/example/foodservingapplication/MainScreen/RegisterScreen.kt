package com.example.foodservingapplication.MainScreen

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.foodservingapplication.Models.User
import com.example.foodservingapplication.Models.Volunteer
import com.example.foodservingapplication.R
import com.example.foodservingapplication.utils.Check.isEmpty
import com.example.foodservingapplication.utils.SelectPhotoDialog
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.CubeGrid
import com.github.ybq.android.spinkit.style.WanderingCubes
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_register_screen.*
import kotlinx.android.synthetic.main.activity_register_screen.radio_group
import kotlinx.android.synthetic.main.fragment_add_item.*
import java.io.ByteArrayOutputStream
import java.util.*

class RegisterScreen : AppCompatActivity(),
    View.OnClickListener {

    var SELECT_USER_TYPE:String?=null;
    val TAG:String = "RegisterScreen"
    var fullNumber:String?=null


    //Firebase varaiables
    private var mDb: FirebaseFirestore? = null
    var volunteer:Volunteer?=null

    //shared perfences varaibales
    var sp:SharedPreferences?=null
    var tokenId: String =""
    var tokenIdUser:String = ""

    //Location varaiables



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_screen)
        layout_volunteer.visibility = View.GONE;
        layout_User.visibility = View.GONE;
        volunteer = Volunteer()
        RadioButtonSelection()
        mDb = FirebaseFirestore.getInstance()
        btn_reg_volunteer.setOnClickListener(this);
        btn_reg.setOnClickListener(this);

        hideSoftKeyboard()
        sp = getSharedPreferences(getString(R.string.User_Ref_Perefernces),Context.MODE_PRIVATE);






    }

    //*******Radio button selection code
    fun RadioButtonSelection(){
        radio_group_panelSelection.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId.equals(R.id.user_radio_btn)){
                layout_volunteer.visibility = View.GONE;
                layout_User.visibility = View.VISIBLE;
                SELECT_USER_TYPE = getString(R.string.donor)

            }else if (checkedId.equals(R.id.volunteer_radio_btn)){
                layout_volunteer.visibility = View.VISIBLE;
                layout_User.visibility = View.GONE;
                SELECT_USER_TYPE = getString(R.string.volunteer)
            }
        }

    }


    //***Functional click call
    fun RegisterNewEmail(email:String , password:String) {
        var editor:SharedPreferences.Editor  = sp!!.edit()
        if (SELECT_USER_TYPE.equals(getString(R.string.donor))){
            ShowProgressBar()
            REgisterUserInFirebase(EmailTextField.text.toString(),passwordTextField.text.toString())

        }else if (SELECT_USER_TYPE.equals(getString(R.string.volunteer))){
            ShowProgressBar()

            REgisterVolunteerInFirebase(EmailTextField_volunteer.text.toString(),passwordTextField_volunteer.text.toString())
        }
        editor.putString(getString(R.string.type_of_user),SELECT_USER_TYPE)
        editor.commit()
    }

   //** actually register the user with  help of the firebase method in the firebase
    private fun REgisterUserInFirebase(email:String , password:String) {
       FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
           .addOnCompleteListener { task ->
               Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful)
               if (task.isSuccessful) {
                   Log.d(TAG, "onComplete: AuthState: " + FirebaseAuth.getInstance().currentUser!!.getUid())
                   //insert some default data TO USER
                   val user = User()

                   FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                       tokenIdUser = it.token
                   }
                   user.token_id = tokenIdUser
                   user.setEmail(email)
                   user.setUsername(UsernameTextField.text.toString())
                   user.password = passwordTextField.text.toString()
                   user.userid = FirebaseAuth.getInstance().uid
                   user.phoneNumber = getPhoneNumber()
                   user.typeOfDonor = getRadiobuttonSelection()
                   user.description = ""
                   user.profile_Image = ""
                   user.number_of_items = ""
                   user.number_of_items_by_volunteer = ""
                   user.privacy_type = getString(R.string.privacy_public)
                   user.type_of_user = getString(R.string.donor)
                   mDb!!.collection(getString(R.string.users_collection)).document(user.userid)
                       .set(user).addOnCompleteListener { task ->
                           if (task.isSuccessful) {
                               hideProgressBar()
                               val parentLayout: View = findViewById(android.R.id.content)
                               Snackbar.make(
                                   parentLayout,
                                   "Succesfully Registered",
                                   Snackbar.LENGTH_SHORT
                               ).show()
                               Log.d(TAG, "createUserWithEmail:USER:" + task.isSuccessful)
                               redirectLoginScreen()
                           } else {
                               val parentLayout: View = findViewById(android.R.id.content)
                               Snackbar.make(
                                   parentLayout,
                                   "Something went wrong.",
                                   Snackbar.LENGTH_SHORT
                               ).show()
                           }
                       }
               }
           }
   }



    private fun REgisterVolunteerInFirebase(email:String , password:String){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful)
                if (task.isSuccessful) {
                    Log.d(TAG, "onComplete: AuthState: " + FirebaseAuth.getInstance().currentUser!!.getUid())
                    //insert some default data to Volunteer
                    FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                        tokenId = it.token
                    }
                    volunteer!!.token_id = tokenId
                    volunteer!!.email = email
                    volunteer!!.username = UsernameTextField_volunteer.text.toString()
                    volunteer!!.volunteerId = FirebaseAuth.getInstance().uid
                    volunteer!!.phoneNumber = getPhoneNumber()
                    volunteer!!.geoPoint
                    volunteer!!.timestamp = null
                    volunteer!!.type_of_user = getString(R.string.volunteer)
                    //insert value volunteer into  firebase
                        mDb!!.collection(getString(R.string.users_collection))
                            .document(FirebaseAuth.getInstance().uid!!).set(volunteer!!)
                            .addOnCompleteListener { task1 ->
                                if (task1.isSuccessful) {
                                    val parentLayout: View = findViewById(android.R.id.content)
                                    Snackbar.make(parentLayout, "SuccessFully Registered", Snackbar.LENGTH_SHORT).show()
                                    redirectLoginScreen()
                                    Log.d(TAG, "createUserWithEmail:Volunteer:" + task.isSuccessful)
                                } else {
                                    val parentLayout: View = findViewById(android.R.id.content)
                                    Snackbar.make(parentLayout, "Something went wrong ."+task1.exception, Snackbar.LENGTH_SHORT).show()
                                }
                            }
                }else {
                    val parentLayout: View = findViewById(android.R.id.content)
                    Snackbar.make(parentLayout, "Something went wrong.", Snackbar.LENGTH_SHORT).show()
                }

            }
    }


    fun hideProgressBar(){
        val progressBar = findViewById(R.id.register_progressbar) as ProgressBar
        val doubleBounce: Sprite = CubeGrid()
        progressBar.indeterminateDrawable = doubleBounce
        progressBar.visibility = View.GONE
    }
    fun ShowProgressBar(){
        val progressBar = findViewById(R.id.register_progressbar) as ProgressBar
        val doubleBounce: Sprite = CubeGrid()
        progressBar.indeterminateDrawable = doubleBounce
        progressBar.visibility = View.VISIBLE
    }



    // ** get the strings from the ui methods
    fun getRadiobuttonSelection():String{
        var selection:String
        var id = radio_group.checkedRadioButtonId
        when(id){
            R.id.individual_select ->
                selection = "Individual"
            R.id.organization_select ->
                selection = "Organization"
            else ->
                selection= ""

        }

        return selection
    }
    fun  getPhoneNumber():String{
        if (SELECT_USER_TYPE == getString(R.string.donor)){
             fullNumber = country_codepicker.getFullNumber()+phone_number.getText().toString()
        }else
        {
            fullNumber = country_codepicker_volunteer.getFullNumber()+phone_number_volunteer.getText().toString()
        }

        return fullNumber as String
    }

    // on click in order to sett on click on the different widgets
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_reg_volunteer -> {
                RegisterNewEmail(EmailTextField_volunteer.text.toString()
                ,passwordTextField_volunteer.text.toString())
            }
            R.id.btn_reg -> {
                RegisterNewEmail(EmailTextField.text.toString()
                    ,passwordTextField.text.toString())

            }

        }
    }

    /**
     * Redirects the user to the login screen
     */
    private fun redirectLoginScreen() {
        Log.d(TAG, "redirectLoginScreen: redirecting to login screen.")
        val intent = Intent(applicationContext, LoginScreen::class.java)
        intent.putExtra(getString(R.string.select_user_type),SELECT_USER_TYPE)
        startActivity(intent)
        finish()
    }

    //hide the keyboard
    private fun hideSoftKeyboard() {
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }


}
