package com.example.foodservingapplication.UI.Volunteer

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import com.example.foodservingapplication.R
import com.example.foodservingapplication.utils.Constants.Companion.ERROR_DIALOG_REQUEST
import com.example.foodservingapplication.utils.Constants.Companion.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
import com.example.foodservingapplication.utils.Constants.Companion.PERMISSIONS_REQUEST_ENABLE_GPS
import com.example.foodservingapplication.utils.SectionPagerAdapter
import com.example.foodservingapplication.utils.UniversalImageLoader
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.iid.FirebaseInstanceId
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.snippet_main_activity_volunteer.*

class MainActivityVolunteer : AppCompatActivity(){
    private var mDb: FirebaseFirestore? = null
    val TAG = "MainActivityVolunteer"
    var tokenId: String = ""

    //LOCATION varaiables
    private var mFusedLocationCilent: FusedLocationProviderClient? = null
    var geoPoint: GeoPoint? = null

    //location permission
    var mLocationPermissionGranted: Boolean = false

    //SECTION PAGER adapter
    var sectionPagerAdapter: SectionPagerAdapter? = null
    var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null

    //navigation view
    var drawerLayout: DrawerLayout? = null
    lateinit var navigationView: NavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_volunteer)
        viewPager = viewpager_container_volunteer
        tabLayout = tab_layout_volunteer
        mDb = FirebaseFirestore.getInstance()
        mFusedLocationCilent = LocationServices.getFusedLocationProviderClient(applicationContext)


        drawerLayout = findViewById(R.id.drawer_layout_volunteer);
        navigationView = findViewById(R.id.nav_view_volunteer)

        initImageLoader()
        setupFragment()
        navigation_image_view_icon.setOnClickListener({
            drawerLayout!!.openDrawer(GravityCompat.START);
        })

        navigationView.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener {
            Log.d("Things", "onNavigationItemSelected: -----------------------------------------------------------")

            when (it.itemId){
                R.id.nav_Track ->{
                    var intent:Intent = Intent(applicationContext,TrackLocationVolunteer::class.java)
                    startActivity(intent)
                    drawerLayout!!.closeDrawer(GravityCompat.START);
                    finish()
                }

                }






            true
        })





    }

    fun initImageLoader() {
        var universalImageLoader: UniversalImageLoader = UniversalImageLoader(applicationContext)
        ImageLoader.getInstance().init(universalImageLoader.config)
    }


    override fun onBackPressed() {
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            drawerLayout!!.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    //set up tab fragament
    private fun setupFragment() {
        sectionPagerAdapter = SectionPagerAdapter(supportFragmentManager)
        sectionPagerAdapter!!.addFragment(Fragment_Volunteer_Items())
        sectionPagerAdapter!!.addFragment(Fragment_Volunteer_Profile())
        viewPager!!.setAdapter(sectionPagerAdapter)
        tabLayout!!.setupWithViewPager(viewPager)
        tabLayout!!.getTabAt(0)!!.setText("ITEMS")
        tabLayout!!.getTabAt(1)!!.setText("PROFILE")
    }




























































    override fun onResume() {
        super.onResume()
        if (checkMapServices()) {
            if (mLocationPermissionGranted) {

                updateToken()
                gettingVolunteerDetails()
            } else {
                getLocationPermission()
            }
        }
    }

    private fun checkMapServices(): Boolean {
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                return true
            }
        }
        return false
    }

    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                val enableGpsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS)
            }
        val alert = builder.create()
        alert.show()
    }

    fun isMapsEnabled(): Boolean {
        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
            return false
        }
        return true
    }

    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mLocationPermissionGranted = true
            gettingVolunteerDetails()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    fun isServicesOK(): Boolean {
        Log.d(TAG, "isServicesOK: checking google services version")
        val available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working")
            return true
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it")
            val dialog = GoogleApiAvailability.getInstance()
                .getErrorDialog(this, available, ERROR_DIALOG_REQUEST)
            dialog.show()
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show()
        }
        return false
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        mLocationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: called.")
        when (requestCode) {
            PERMISSIONS_REQUEST_ENABLE_GPS -> {
                if (mLocationPermissionGranted) {
                    gettingVolunteerDetails()
                } else {
                    getLocationPermission()
                }
            }
        }
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


    fun gettingVolunteerDetails() {
        mDb!!.collection(getString(R.string.users_collection))
            .document(FirebaseAuth.getInstance().currentUser.toString()).get()
            .addOnSuccessListener { result ->
                // getLastKnownLocation()
            }.addOnFailureListener { exception ->
                Log.d(TAG, "onComplete: the exception occurs " + exception.printStackTrace())
            }
    }




    /*//step 1  geetting the user
      fun gettingVolunteerDetails(){
        mDb!!.collection(getString(R.string.users_collection)).
        document(FirebaseAuth.getInstance().currentUser.toString()).get()
            .addOnSuccessListener { result ->
           // getLastKnownLocation()
            }.addOnFailureListener { exception ->
                Log.d(TAG, "onComplete: the exception occurs "+ exception.printStackTrace())
            }
    }



      //step 3 saving the corodinates into database
      fun savingThecordinates(geoPoint: GeoPoint){
          mDb!!.collection(getString(R.string.users_collection))
              .document(FirebaseAuth.getInstance().currentUser.toString())
              .update("geoPoint",geoPoint)
              .addOnCompleteListener() { result ->
                  if (result.isSuccessful){
                      Log.d(TAG, " savingThecordinates onComplete: The result is sucessful")
                  }else {
                      Log.d(TAG, " savingThecordinates onComplete: The result is UnSuceessfull")
                  }



              }


      }

      private fun getLastKnownLocation() {
          mFusedLocationCilent!!.lastLocation.addOnCompleteListener { task ->
                  if (task.isSuccessful) {
                      if (task.result != null) {
                          val location = task.result
                          var geocoder: Geocoder = Geocoder(applicationContext, Locale.getDefault())
                          geoPoint = GeoPoint(location!!.latitude, location.longitude)
                          Log.d(TAG, "onComplete: on complete listerner " + geoPoint!!.latitude)
                          Log.d(TAG, "onComplete: on complete longitude " + geoPoint!!.longitude)
                          savingThecordinates(geoPoint!!)
                          startLocationService()
                      }
                  }else {
                      Toast.makeText(applicationContext,"We Can'nt get your location plz turn it on",Toast.LENGTH_SHORT ).show()
                  }
              }
      }






      //starting the service code

      //starting the service code
      // checeking that is location service is running or not
      private fun isLocationServiceRunning(): Boolean {
          val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
          for (service in manager.getRunningServices(Int.MAX_VALUE)) {
              if ("com.codingwithmitch.googlemaps2018.Services.LocationService" == service.service.className) {
                  Log.d(
                      TAG,
                      "isLocationServiceRunning: location service is already running "
                  )
                  return true
              }
          }
          Log.d(
              TAG,
              "isLocationServiceRunning: location service is not running"
          )
          return false
      }

      private fun startLocationService() {
          if (!isLocationServiceRunning()) {
              val serviceIntent = Intent(this, LocationService::class.java)
              // this.startService(serviceIntent);
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                  // this check is neccessary in order to run the service  in foreground otherwise if its goes in background it will destroy
                  this.startForegroundService(serviceIntent)
              } else {
                  startService(serviceIntent)
              }
          }
      }*/


}
