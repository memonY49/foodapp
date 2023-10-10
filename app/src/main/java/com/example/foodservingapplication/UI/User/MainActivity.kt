package com.example.foodservingapplication.UI.User

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.example.foodservingapplication.R
import com.example.foodservingapplication.utils.SectionPagerAdapter
import com.example.foodservingapplication.utils.UniversalImageLoader
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    //widegets varaiables from UI
    var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null
    var sectionPagerAdapter: SectionPagerAdapter? = null
    val TAG= "MAIN ACTIVITY"
    var tokenId:String ?=null
    var mDb:FirebaseFirestore?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewPager = viewpager_container
        tabLayout = tab_layout
        mDb = FirebaseFirestore.getInstance()
        initImageLoader()
        verifyPermissions()
        updateToken()
    }




    fun updateToken() {

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            tokenId = it.token
            Log.d(TAG, "GETTTTING THE TOKENNN ID " + it.token)

            val washingtonRef = mDb!!.collection(getString(R.string.users_collection))
                .document(FirebaseAuth.getInstance().currentUser!!.uid)
            washingtonRef
                .update("token_id", tokenId)
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully updated!")
                    Toast.makeText(this, "TokenUpdated", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error updating document", e)
                    Toast.makeText(this, "Token Failed", Toast.LENGTH_SHORT).show()

                }
        }
    }

    override fun onResume() {
        super.onResume()
        updateToken()
    }

    fun initImageLoader(){
            var universalImageLoader: UniversalImageLoader = UniversalImageLoader(applicationContext)
            ImageLoader.getInstance().init(universalImageLoader.config)
        }

    private fun setupFragment() {
        sectionPagerAdapter = SectionPagerAdapter(supportFragmentManager)
        sectionPagerAdapter!!.addFragment(Fragment_home())
        sectionPagerAdapter!!.addFragment(Fragment_profile())
        sectionPagerAdapter!!.addFragment(Fragment_add_item())
        sectionPagerAdapter!!.addFragment(Fragment_Search())


        viewPager!!.setAdapter(sectionPagerAdapter)
        tabLayout!!.setupWithViewPager(viewPager)
        tabLayout!!.getTabAt(0)!!.setText("HOME")
        tabLayout!!.getTabAt(1)!!.setText("PROFILE")
        tabLayout!!.getTabAt(2)!!.setText("ADD ITEM")
        tabLayout!!.getTabAt(3)!!.setText("SEARCH")

    }

    private fun verifyPermissions() {
        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                permissions[0]
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this.applicationContext,
                permissions[1]
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this.applicationContext,
                permissions[2]
            )==PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this.applicationContext,
                permissions[3]
            ) == PackageManager.PERMISSION_GRANTED) {
            setupFragment()
        } else {
            ActivityCompat.requestPermissions(this@MainActivity, permissions, 1)

        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        verifyPermissions()
    }


}
