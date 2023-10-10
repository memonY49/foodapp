package com.example.foodservingapplication.MainScreen

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foodservingapplication.R
import com.example.foodservingapplication.UI.User.MainActivity
import com.example.foodservingapplication.UI.Volunteer.MainActivityVolunteer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login_screen.*
import java.lang.Exception

class LoginScreen : AppCompatActivity(),View.OnClickListener{
    //variables
    val TAG = "LoginScreen"
    var SelectUserType:String?=null
    private var mDb: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var firebaseAuth:FirebaseAuth = FirebaseAuth.getInstance()
    var result2:String?=null

    var sp:SharedPreferences?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)
        register_click_volunteer.setOnClickListener(this);
        btn_user_login.setOnClickListener(this);
        sp = getSharedPreferences(getString(R.string.User_Ref_Perefernces),Context.MODE_PRIVATE);

       // to extract the bundle from register activity
        try {
            var bundle:Bundle = intent.extras!!
            SelectUserType = bundle.getString(getString(R.string.select_user_type))

        }catch (ex:Exception){
            Log.d(TAG,"THE exception occured")
        }

    }


    // sigin in the firebase  with the help of firebase methods
    private fun signIn() {
        showProgressbar()
        //check if the fields are filled out
        if (!TextUtils.isEmpty(EmailTextField_login.getText().toString())
            && !TextUtils.isEmpty(passwordTextField_login.getText().toString())) {
            Log.d(TAG, "onClick: attempting to authenticate.")

            FirebaseAuth.getInstance().signInWithEmailAndPassword(
                EmailTextField_login.getText().toString(),
                passwordTextField_login.getText().toString()
            ).addOnCompleteListener {
                ChooseMainScreenDisplay()
            }.addOnFailureListener {
                hideProgressbar()
                    Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show() }
        } else {
            Toast.makeText(this, "You didn't fill in all the fields.", Toast.LENGTH_SHORT).show()
        }
    }

    //***
    // choose the screen according such as if
    // donor then open one and if volunteer then login into the volunteer screen
    fun ChooseMainScreenDisplay(){


        if (SelectUserType == getString(R.string.volunteer)){
            Toast.makeText(
                this, "Authentication Successful", Toast.LENGTH_SHORT).show()
            hideProgressbar()
            val intent = Intent(applicationContext, MainActivityVolunteer::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }else if(SelectUserType == getString(R.string.donor)){
            Toast.makeText(
                this, "Authentication Successful", Toast.LENGTH_SHORT).show()
            hideProgressbar()
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }else {

            ///In case of direct login
            mDb.collection(getString(R.string.users_collection))
                .get()
                .addOnSuccessListener { result ->
                    for (documents in result) {
                        if (EmailTextField_login.text.toString().equals(documents.get("email"))){
                          var resut2:String  = documents.get("type_of_user") as String
                            if (resut2 == "Volunteer"){
                                storeIntoSharedPerfernces("Volunteer")
                                Toast.makeText(this, "Authentication Successful", Toast.LENGTH_SHORT).show()
                                hideProgressbar()
                                val intent = Intent(applicationContext, MainActivityVolunteer::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()

                            }else if (resut2 == "Donor"){
                                storeIntoSharedPerfernces("Donor")
                                Toast.makeText(
                                    this, "Authentication Successful", Toast.LENGTH_SHORT).show()
                                hideProgressbar()
                                val intent = Intent(applicationContext, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            }
                        }

                      Log.d(TAG,result.toString())
                    }
                }.addOnFailureListener { exception ->
                    Log.d(TAG, "Error getting documents: ", exception)
                    Toast.makeText(
                        this, "Authentication UnSucessful \n Could not find documents ", Toast.LENGTH_SHORT).show()
                    hideProgressbar()
                }
        }

        }





    //****
    // to set the clicks on the multiple widegets
    override fun onClick(v: View?) {
        when(v!!.id){
            R.id. register_click_volunteer ->{
                val intent = Intent(this, RegisterScreen::class.java)
                startActivity(intent)

            }
            R.id.btn_user_login ->{
                signIn()

            }
        }
    }


    fun hideProgressbar(){
        progress_bar_login.visibility = View.GONE


    }
    fun showProgressbar(){
        progress_bar_login.visibility = View.VISIBLE

    }

    fun storeIntoSharedPerfernces(SELECT_USER_TYPE:String){

        var editor:SharedPreferences.Editor  = sp!!.edit()
        editor.putString(getString(R.string.type_of_user),SELECT_USER_TYPE)
        editor.commit()

    }






}


