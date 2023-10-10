package com.example.foodservingapplication.UI.Volunteer

import android.Manifest
import android.app.ActivityManager
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.*
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.foodservingapplication.Models.VolunteerPostShow
import com.example.foodservingapplication.R
import com.example.foodservingapplication.utils.Constants.Companion.ERROR_DIALOG_REQUEST
import com.example.foodservingapplication.utils.Constants.Companion.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
import com.example.foodservingapplication.utils.Constants.Companion.PERMISSIONS_REQUEST_ENABLE_GPS
import com.example.foodservingapplication.utils.GoogleMapDTO
import com.example.foodservingapplication.utils.LocationService
import com.example.foodservingapplication.utils.Notifications.NotificationData
import com.example.foodservingapplication.utils.Notifications.PushNotification
import com.example.foodservingapplication.utils.Notifications.RetrofitInstance
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.toObject
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_add_item.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class VolunteerMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    var TAG = "VolunteerMapsActivity"
    //widgets

    //to get last known location
    lateinit  var fusedLocationProviderClient:FusedLocationProviderClient

    //LOcation request
    lateinit var locationRequest:LocationRequest

    //permissions
     var mLocationPermissionGranted = false;

    //maps
    lateinit var mMap:GoogleMap


    //project varaiables
    lateinit var geocoder: Geocoder
     var userLatitute :Double =0.0
      var userLongitude:Double=0.0
   var userLocationMarker:Marker ?= null
    var userLocationAccuracyCircle:Circle?=null

    lateinit var geopoint:GeoPoint

   lateinit var firebaseFirestore:FirebaseFirestore

    //required in start location updates location callback
   private var locationCallback: LocationCallback = locationCallBackfunction()

    private var postId:String?=null

    private var volunteerPostShow:VolunteerPostShow?=null

    var checkTheNotificationisSend = true
     var tokenId:String?=null





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_volunteer_maps)
        firebaseFirestore = FirebaseFirestore.getInstance()
        geetingTheExtras()
        if (getString(R.string.google_maps_api_key).isEmpty()) {
           logs("NOT GEETTTTINGG THEHEE API KEYYY --------------- WE ARE SORRRYYYY")
        }
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        geocoder= Geocoder(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()
        locationRequest.setInterval(4000)
        locationRequest.setFastestInterval(2000)
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)




    }


  fun gettingThetoken():String{
      firebaseFirestore.collection(getString(R.string.users_collection))
          .document(postId!!).get().addOnSuccessListener { document->
              logs("geetttting the user dataa" + postId!!)
              tokenId = document.get("token_id") as String
              logs("assigning the tokenId"+tokenId)

          }.addOnFailureListener { exception ->  }

      return tokenId!!

  }


    fun gettingThedistance(lat1:Double ,long1:Double,lat2:Double,long2:Double):String{
        val mylocation = Location("")
        val dest_location = Location("")
        dest_location.setLatitude(lat1.toDouble())
        dest_location.setLongitude(long1.toDouble())

        mylocation.setLatitude(lat2)
        mylocation.setLongitude(long2)
        val distance  = mylocation.distanceTo(dest_location) //in meters
       /* val distanceInKilometer = distance * 0.001
        val solution:Double = String.format("%.1f", distanceInKilometer).toDouble()
*/
        Toast.makeText(
            this, "Distance"+distance, Toast.LENGTH_LONG).show()

        Log.d(TAG,"the  volunteer Location is " + "Latititude is :  ----" +lat1+
                "  longitutude is --- " + long1 +
                "The user location is  "+lat2 + "THE longitude is ++ === "+long2)

        return distance.toString()
    }

    fun geetingTheExtras(){
        try {
            if (intent!=null){
                var bundle = intent.extras!!
                userLatitute = bundle.getDouble(getString(R.string.post_geopoint_latitude))
                userLongitude = bundle.getDouble(getString(R.string.post_geopoint_longitude))
                postId = bundle.getString(getString(R.string.user_id))

                Toastcreate("postId"+postId)
                gettingThetoken()
            }else {
                Toastcreate("SORRRRYY MISSING THE LOCATION AVOID TO PROCEED THIS ORDER")
            }
        }catch (ex:Exception){
            logs("CANNTTTT GETTT ")
        }

    }


    override fun onMapReady(googleMap: GoogleMap?) {
     mMap = googleMap!!;
        var user_location = LatLng(userLatitute, userLongitude)
        CreateMarker(user_location,16f)
        Log.d("GoogleMap", "before URL")

        if (checkMapServices()) {
            if (mLocationPermissionGranted) {
               // enableUserLocation()
                //zoomToUsersLocation()

            } else {
                getLocationPermission()
            }
        }
    }

    fun CreateMarker(latLng: LatLng,zoomLevel:Float){
        logs("CreateMarker geetttting the positions"+latLng)

        try {
            var addresses:List<Address> = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1)
            if (addresses.size>0){
                var address:Address = addresses.get(0)
                var streetAddress: String? = address.getAddressLine(0)
                mMap.addMarker(MarkerOptions().position(latLng).title(streetAddress))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel))
            }
        }catch (ex:Exception){
            Toastcreate("Failed to get adrresss in it")
            logs("FAILEDD TO GET THE ADDRRESSS FROM LATITUDE AND LONGITUDE"+ex.printStackTrace())
        }

    }

    fun zoomToUsersLocation(){
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location->
            if (location!=null){
                Log.d(TAG,"getLastLocation addOnSuccessListener "+location.toString() )
                Log.d(TAG,"getLastLocation addOnSuccessListener "+location.latitude )
                Log.d(TAG,"getLastLocation addOnSuccessListener "+location.longitude )
                var geocoder: Geocoder = Geocoder(applicationContext, Locale.getDefault())
                geopoint = GeoPoint(location!!.latitude, location.longitude)
                Log.d(TAG, "onComplete: on complete listerner " + geopoint!!.latitude)
                Log.d(TAG, "onComplete: on complete longitude " + geopoint!!.longitude)
                savingThecordinates(geopoint!!)
                startLocationService()
                CreateMarker(LatLng(location.latitude,location.longitude),20f)


            }else {
                logs( " fusedLocationProviderClient addOnSuccessListener sorrryy can't get the location loccation is null")
            }


        }.addOnFailureListener { exception ->
            logs("exception occured addOnFailureListener"+exception.printStackTrace())
        }

    }

    //send notification method this method will make request calll
    private fun CreateNotificationServiceCall(notification: PushNotification) = CoroutineScope(
        Dispatchers.IO)
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
        val message = "Rider is nearby to you get Ready your food"
        val tile= "GET READY YOUR FOOD"

        if (title.isNotEmpty() && message.isNotEmpty()){
            PushNotification(
                NotificationData(tile,message,volunteerPostShow!!.postId),recipentToken

            ).also {
                CreateNotificationServiceCall(it)
            }
        }
    }




    fun locationCallBackfunction():LocationCallback{
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
             super.onLocationResult(locationResult)
               var distance = gettingThedistance(userLatitute,userLongitude,
                   locationResult!!.lastLocation.latitude,locationResult.lastLocation.longitude)
                var distanceindouble:Double = distance.toDouble()
                if (distanceindouble<= 180.0){
                    Toastcreate("ABOUT REACHED TO THE USER SUCCESFULLL REACHED ")
                    if(checkTheNotificationisSend){
                       sendnotification(tokenId!!)
                        checkTheNotificationisSend = false
                    }
                   //send notification to the user

                }

                logs("onLocationResult --------------"+locationResult!!.lastLocation)
                
                if (mMap!=null){
                  setUserLocationMarker(locationResult.lastLocation)

              }

            }

        }
        return locationCallback
    }
    fun startLocationUpdates(){
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper())
    }
    fun stopLocationUpdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)

    }

    override fun onStop() {
        super.onStop()
        stopLocationUpdates()
    }

    override fun onStart() {
        super.onStart()
        geetingTheExtras()

        startLocationUpdates()


    }


    fun setUserLocationMarker(location:Location){
        logs("setUserLocationMarker  SETTTING THE MARKER")
        var latLng:LatLng = LatLng(location.latitude,location.longitude)
        if (userLocationMarker == null){
            //Create a new marker
            var markerOption:MarkerOptions = MarkerOptions()
            markerOption.position(latLng)
            markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.food))
            markerOption.rotation(location.getBearing())
            markerOption.anchor(0.5f,0.5f)

            userLocationMarker = mMap.addMarker(markerOption)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17f))
        }else {
            userLocationMarker!!.setPosition(latLng)
            userLocationMarker!!.rotation=location.bearing
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17f))
        }

        if(userLocationAccuracyCircle == null){
            var circleOptions:CircleOptions = CircleOptions()
            circleOptions.center(latLng)
            circleOptions.strokeWidth(4f)
            circleOptions.strokeColor(Color.argb(255,255,0,0))
            circleOptions.fillColor(Color.argb(32,255,0,0))
            circleOptions.radius(location.getAccuracy().toDouble())
            userLocationAccuracyCircle = mMap.addCircle((circleOptions))
        }else {
            userLocationAccuracyCircle!!.setCenter(latLng)
            userLocationAccuracyCircle!!.setRadius(location.getAccuracy().toDouble())
        }

    }



    fun logs(string: String){
        Log.d(TAG,string )
    }
    fun Toastcreate(string: String){
        Toast.makeText(applicationContext,string,Toast.LENGTH_SHORT).show()

    }



    ///---------------------------------------LOCATION PERMISSIONS ---------------------------------------------//

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
            .setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, id: Int) {
                    val enableGpsIntent =
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS)
                }
            })
        val alert = builder.create()
        alert.show()
    }

    fun isMapsEnabled(): Boolean {
        val manager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
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
        if (ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true

            //enableUserLocation()
            zoomToUsersLocation()

        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    fun isServicesOK(): Boolean {
        Log.d(TAG, "isServicesOK: checking google services version")
        val available: Int = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working")
            return true
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it")
            val dialog: Dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available, ERROR_DIALOG_REQUEST)
            dialog.show()
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        mLocationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                { mLocationPermissionGranted = true }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: called.")
        when (requestCode) {
            PERMISSIONS_REQUEST_ENABLE_GPS -> {
                if (mLocationPermissionGranted) {

                    //enableUserLocation()
                    zoomToUsersLocation()
                } else {
                    getLocationPermission()
                }
            }
        }
    }





    //step 3 saving the corodinates into database
    fun savingThecordinates(geoPoint: GeoPoint){
        firebaseFirestore!!.collection(getString(R.string.users_collection))
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








    //starting the service code

    //starting the service code
    // checeking that is location service is running or not
    private fun isLocationServiceRunning(): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if ("com.example.foodservingapplication.utils.LocationService" == service.service.className) {
                Log.d(TAG, "isLocationServiceRunning: location service is already running ")
                return true
            }
        }
        Log.d(TAG, "isLocationServiceRunning: location service is not running")
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
    }


    private fun stopLocationSerive() {
        if (isLocationServiceRunning()) {
            val serviceIntent = Intent(this, LocationService::class.java)
            // this.startService(serviceIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // this check is neccessary in order to run the service  in foreground otherwise if its goes in background it will destroy
               stopService(serviceIntent)
            }
        }
    }






































































    //-----------------------GETTTTING THE DIRECTION FROM URL ---------------------------------/////




    fun getDirectionURL(origin:LatLng,dest:LatLng) : String{
        return "https://maps.googleapis.com/maps/api/directions/json?key=AIzaSyAsFlWqtkBtGsmoLd3IH1r11Mac606Pdgo&origin=${origin.latitude},${origin.longitude}&destination=${dest.latitude},${dest.longitude}&sensor=false&mode=driving"
    }

    private inner class GetDirection(val url : String) : AsyncTask<Void, Void, List<List<LatLng>>>(){
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body()!!.string()
            Log.d("GoogleMap" , " data : $data")
            val result =  ArrayList<List<LatLng>>()
            try{
                val respObj = Gson().fromJson(data, GoogleMapDTO::class.java)

                val path =  ArrayList<LatLng>()

                for (i in 0..(respObj.routes[0].legs[0].steps.size-1)){
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            }catch (e:Exception){
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>) {
            val lineoption = PolylineOptions()
            for (i in result.indices){
                lineoption.addAll(result[i])
                lineoption.width(10f)
                lineoption.color(Color.BLUE)
                lineoption.geodesic(true)
            }
            mMap.addPolyline(lineoption)
        }
    }

    public fun decodePolyline(encoded: String): List<LatLng> {

        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latLng = LatLng((lat.toDouble() / 1E5),(lng.toDouble() / 1E5))
            poly.add(latLng)
        }

        return poly
    }





}
