package com.example.foodservingapplication.MainScreen

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.daimajia.androidanimations.library.Techniques
import com.example.foodservingapplication.R
import com.example.foodservingapplication.UI.User.MainActivity
import com.example.foodservingapplication.UI.Volunteer.MainActivityVolunteer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseAuth.getInstance
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.viksaa.sssplash.lib.activity.AwesomeSplash
import com.viksaa.sssplash.lib.cnst.Flags
import com.viksaa.sssplash.lib.model.ConfigSplash

class SplashScreen : AwesomeSplash() {
    var hello: String? = null
    val TAG: String = "SplashScreen"
    var firebaseAuth: FirebaseAuth = getInstance()
    private var mDb: FirebaseFirestore = FirebaseFirestore.getInstance()
    var result2: String? = null


    override fun initSplash(configSplash: ConfigSplash?) {
        if (firebaseAuth.currentUser == null) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            //background color animation
            configSplash!!.backgroundColor = R.color.blueish
            configSplash.animCircularRevealDuration = 2500
            configSplash.revealFlagX = Flags.REVEAL_LEFT
            configSplash.revealFlagY = Flags.REVEAL_BOTTOM
            //For logo
            configSplash.setLogoSplash(R.drawable.fyplogo); //or any other drawable
            configSplash.setAnimLogoSplashDuration(3000); //int ms

            // for text
            configSplash.titleSplash = "SAVE n SHARE!"
            configSplash.titleTextColor = R.color.Black
            configSplash.titleTextSize = 50f
            configSplash.animTitleDuration = 3000
            configSplash.animTitleTechnique = Techniques.ZoomIn
            configSplash.setTitleFont("fonts/allura_regular.ttf")

        } else {
            animationsFinished()
        }

    }

    override fun animationsFinished() {
        var sp: SharedPreferences = applicationContext.getSharedPreferences(
            getString(R.string.User_Ref_Perefernces),
            Context.MODE_PRIVATE)
        result2 = sp.getString(getString(R.string.type_of_user), getString(R.string.type_of_user))
        Log.d(TAG, result2!!)
        if (firebaseAuth.currentUser != null) {
            if (result2 == "Volunteer") {
                val intent = Intent(applicationContext, MainActivityVolunteer::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()

            } else if (result2 == "Donor") {
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }


        }else {
            val intent:Intent = Intent(this,OnBroadingScreenActivity::class.java)
            startActivity(intent)
            finish()
        }


    }
}










