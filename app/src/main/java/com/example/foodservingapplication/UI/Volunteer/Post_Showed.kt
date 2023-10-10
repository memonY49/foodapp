package com.example.foodservingapplication.UI.Volunteer

import android.animation.ObjectAnimator
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.foodservingapplication.Models.Volunteer
import com.example.foodservingapplication.Models.VolunteerPickedFoods
import com.example.foodservingapplication.Models.VolunteerPostShow
import com.example.foodservingapplication.R
import com.example.foodservingapplication.utils.UniversalImageLoader
import com.example.foodservingapplication.utils.ViewWeightAnimationWrapper
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.FoldingCube
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.post_show.*

class Post_Showed : AppCompatActivity(),OnMapReadyCallback {
    var content: String? = null
    var mDb: FirebaseFirestore? = null
    var UsergeoPoint: GeoPoint? = null

    val TAG = "Post_Showed"
    var totalDistance: String = ""
    var status: String = ""
    var UserId:String = ""

    //class Objects
    lateinit var VolunteerPosition:Volunteer
    lateinit var Post:VolunteerPostShow
    lateinit var volunteerPickedFood:VolunteerPickedFoods

    //to set the camera view we have varaiables
    private var googleMap: GoogleMap? = null
    private var mMapBoundary: LatLngBounds? = null
    private lateinit var mapView: MapView


    private val MAP_LAYOUT_STATE_CONTRACTED = 0
    private val MAP_LAYOUT_STATE_EXPANDED = 1
    private var mMapLayoutState = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post_show)
        mDb = FirebaseFirestore.getInstance()


        mapView = findViewById(R.id.map_polyline)
        VolunteerPosition = Volunteer()
        Post = VolunteerPostShow()

        hideProgressBar()
        initImageLoader()

        inintGoogleMaps(savedInstanceState)



        if (intent != null) {
            content = intent.getExtras()!!.getString(getString(R.string.post_id_notification))
            content1.text = content
            gettingthewidegets()
        }


    }

    fun inintGoogleMaps(savedInstanceState: Bundle?){

        try {
            val mapViewBundle = savedInstanceState?.getBundle(MAPVIEW_BUNDLE_KEY)

            mapView.onCreate(mapViewBundle)
            mapView.getMapAsync(this)
        } catch (e: Exception) {
            Log.d(TAG,"Geeetting the error in map view")
        }

    }


    private fun setCameraView() {

        //overall map view window : 0.2 * 0.2 = 0.04
     val bottomBoundary = VolunteerPosition.geoPoint.getLatitude() - .1
      val leftBoundary = VolunteerPosition.geoPoint.getLongitude() - .1
     val RightBoundary = VolunteerPosition.geoPoint.getLongitude() + .1
    val topBoundary = VolunteerPosition.geoPoint.getLatitude() + .1
      mMapBoundary = LatLngBounds(
          LatLng(bottomBoundary, leftBoundary),
            LatLng(topBoundary, RightBoundary)
        )
        googleMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 0))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY) ?: Bundle().also {
            outState.putBundle(MAPVIEW_BUNDLE_KEY, it)
        }
        mapView.onSaveInstanceState(mapViewBundle)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onMapReady(map: GoogleMap) {
        map.addMarker(MarkerOptions().position(LatLng(0.0, 0.0)).title("Marker"))
        map.isMyLocationEnabled = true
        googleMap = map
        gettingTheVolunteerLocation()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    companion object {
        private const val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
    }

    fun gettingTheVolunteerLocation(){
        //ShowProgressBar()
        Log.d(TAG,"the auntheciated user sus"+FirebaseAuth.getInstance().currentUser!!.uid.toString())
        mDb!!.collection(getString(R.string.users_collection))
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .get().addOnSuccessListener { documentSnapshot ->
                Log.d(TAG, "gettingTheVolunteerLocation the succes listerner ")
                 VolunteerPosition = documentSnapshot.toObject(Volunteer::class.java)!!
                Log.d(TAG,"GETTTING VOLUNTEER POSITION  "+VolunteerPosition.toString())
                setCameraView()
            }.addOnFailureListener { exception ->
                Log.d(TAG, "GETTING the exception here " + exception.printStackTrace())
            }


}

    // *********************************************************************************** contracting and expanding the mapp position
    private fun expandMapAnimation() {

        //linear layout
        val viewWeightAnimationWrapper =
            ViewWeightAnimationWrapper(map_container)
        val mapAnimation: ObjectAnimator = ObjectAnimator.ofFloat(viewWeightAnimationWrapper, "weight", 30f, 100f)
        mapAnimation.duration = 800

        //relative layout
        val recylerAnimationWrapper = ViewWeightAnimationWrapper(post_details_parent_layout)
        val recylerAnimation: ObjectAnimator = ObjectAnimator.ofFloat(recylerAnimationWrapper, "weight", 60f, 0f)
        recylerAnimation.duration = 800
        recylerAnimation.start()
        mapAnimation.start()
    }

    private fun contractMapAnimation() {
        //linear layout
        val viewWeightAnimationWrapper = ViewWeightAnimationWrapper(map_container)
        val mapAnimation: ObjectAnimator = ObjectAnimator.ofFloat(viewWeightAnimationWrapper, "weight", 100f, 30f)
        mapAnimation.duration = 800

        //relative layout
        val recylerAnimationWrapper =ViewWeightAnimationWrapper(post_details_parent_layout)
        val recylerAnimation: ObjectAnimator = ObjectAnimator.ofFloat(recylerAnimationWrapper, "weight", 0f, 60f)
        recylerAnimation.start()
        mapAnimation.start()
    }


    fun Icon_on_map_function(view: View) {
        Log.d(TAG, "BUTTTTONNN CLICKEDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD")

        if (mMapLayoutState == MAP_LAYOUT_STATE_CONTRACTED) {
            mMapLayoutState = MAP_LAYOUT_STATE_EXPANDED
            expandMapAnimation()
        } else if (mMapLayoutState == MAP_LAYOUT_STATE_EXPANDED) {
            mMapLayoutState = MAP_LAYOUT_STATE_CONTRACTED
            contractMapAnimation()
        }
    }


    fun initImageLoader() {
        var universalImageLoader: UniversalImageLoader = UniversalImageLoader(applicationContext)
        ImageLoader.getInstance().init(universalImageLoader.config)
    }


    fun gettingthewidegets() {
        ShowProgressBar()
        mDb!!.collection(getString(R.string.posts_node)).document(content!!).get()
            .addOnSuccessListener { document ->
                Post = document.toObject(VolunteerPostShow::class.java)!!
                settingthewidegets(
                    Post.itemImage,
                    Post.itemName,
                    Post.quantityOfItem,
                    Post.descriptionOfItem,
                    Post.sellingMethod,
                    Post.sellingPoint,
                    Post.address,
                    Post.extraAddressInformation,
                    Post.discountedprice
                )
                try {
                    UsergeoPoint = Post.geoPoint
                    UserId = Post.userId
                }catch (ex:Exception){
                    Log.d(TAG, "GETTING the exception here " + ex.printStackTrace())

                }






                hideProgressBar()
            }
    }

    fun settingthewidegets(item_image: String, item_name: String, item_quantity: String, item_description: String, item_type_of_selling: String, item_type_receiveing: String, address: String, extra_information: String, discountPrice: String) {
        UniversalImageLoader.setImage(item_image, post_showed, null, "")
        textview_itemname_post.text = item_name
        textview_quantity_show_post.text = item_quantity
        textview_description_show_post.text = item_description
        textview_type_of_selling_show_post.text = item_type_of_selling
        textview_type_of_receiver_show_post.text = item_type_receiveing
        textview_address_show_post.text = address
        textview_extr_info_show_post.text = extra_information
        textview_discount_price_show_post.text = discountPrice
    }
    fun hideProgressBar() {
        val progressBar = findViewById(R.id.post_showed_progressbar) as ProgressBar
        val doubleBounce: Sprite = FoldingCube()
        progressBar.indeterminateDrawable = doubleBounce
        progressBar.visibility = View.GONE
    }

    fun ShowProgressBar() {
        val progressBar = findViewById(R.id.post_showed_progressbar) as ProgressBar
        val doubleBounce: Sprite = FoldingCube()
        progressBar.indeterminateDrawable = doubleBounce
        progressBar.visibility = View.VISIBLE
    }


    //to check the distance
    fun Track_Order(view: View) {
        checkingThestatus()

    }

    fun checkingThestatus() {
        creatingDialog()

    }


    fun puttingEntriesOfTrackOrderInDatabase(){
        volunteerPickedFood = VolunteerPickedFoods()
        volunteerPickedFood.userId = Post.userId
        volunteerPickedFood.volunteerId = VolunteerPosition.volunteerId
        volunteerPickedFood.status =getString(R.string.inProcessing)
        volunteerPickedFood.userGeopoint=Post.geoPoint
        volunteerPickedFood.volunteerGeopoint=VolunteerPosition.geoPoint
        volunteerPickedFood.token_Id_volunteer=VolunteerPosition.token_id
        volunteerPickedFood.postShow = Post

        mDb!!.collection(getString(R.string.posts_node)).document(content!!)
            .update("pickedStatus", getString(R.string.inProcessing))
            .addOnSuccessListener {
                Log.d(TAG,"UPDATED INTO THE DATABASEE")
            }.addOnFailureListener {exception ->
                Log.d(TAG,"FAILED TO UPDATED  INTO THE DATABASEE")
            }

        mDb!!.collection(getString(R.string.VolunteerPicked_Foods)).
        document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection(getString(R.string.picked_post_node)).document(Post.postId).
        set(volunteerPickedFood).addOnSuccessListener {it
            Log.d(TAG,"ADDDED INTO THE DATABASEE")


        }.addOnFailureListener { exception ->
            Log.d(TAG,"FAILED TO ADDED DATABASE INTO THE DATABASEE")
        }


    }

    fun creatingDialog() {
        totalDistance = "You are " + gettingThedistance(VolunteerPosition.geoPoint.latitude,
            VolunteerPosition.geoPoint.longitude,Post.geoPoint.latitude,Post.geoPoint.longitude)+
                "  Away from Your order" + "\n" + "Are your sure you want to Track it"
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Your Estimation")
        builder.setMessage(totalDistance)
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            Toast.makeText(applicationContext, "clicked yes", Toast.LENGTH_LONG).show()
            //ENTRY TO THE DATABASE  --->volunteerPICKEDFOODS -->Volunteer-->POst -->Processing
            /// post has three status Inprocessing ,Received , UNPICKED
            puttingEntriesOfTrackOrderInDatabase()
            var intent: Intent = Intent(applicationContext, VolunteerMapsActivity::class.java)
            intent.putExtra(getString(R.string.post_geopoint_latitude), UsergeoPoint!!.latitude)
            intent.putExtra(getString(R.string.post_geopoint_longitude), UsergeoPoint!!.longitude)
            intent.putExtra(getString(R.string.post_id_by_volunteer),Post.postId)
            intent.putExtra(getString(R.string.user_id),Post.userId)
            startActivity(intent)
        }
        builder.setNegativeButton("No") { dialogInterface, which ->
            Toast.makeText(applicationContext, "clicked no", Toast.LENGTH_LONG).show()
        }.show()
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




}



