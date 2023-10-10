package com.example.foodservingapplication.UI.Volunteer

import android.graphics.Color
import android.os.AsyncTask
import android.util.Log
import com.example.foodservingapplication.utils.GoogleMapDTO
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request

class Polylincode {

    var googleMap:GoogleMap?=null

    fun setPolyLines(volunteerLocation: LatLng, UserLocation: LatLng){
        //this is for polyline drawing
        val URL = getDirectionURL(volunteerLocation,UserLocation)
        Log.d("GoogleMap", "URL : $URL")
        GetDirection(URL).execute()


    }

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
            }catch (e: java.lang.Exception){
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
            googleMap!!.addPolyline(lineoption)
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
    /*
    fun gettingThedistance(lat1: Double, long1: Double, lat2: Double, long2: Double): String {

        //calculate the longitude difference
        var longDiff: Double = long1 - long2

        //calculate the distance
        var distance: Double =
            Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(
                deg2rad(lat2)
            ) * Math.cos(deg2rad(longDiff))
        distance = Math.acos(distance)
        //convert randian to degree
        distance = rad2degree(distance)
        //Distance in miles
        distance = distance * 60 * 1.1515
        //Distance in kilometeres
        distance = distance * 1.609344
        return String.format(Locale.getDefault(), "%2f Kilometers", distance)
    }

    fun rad2degree(distance: Double): Double {
        return (distance * 180.0 / Math.PI)
    }


    fun deg2rad(lat1: Double): Double {
        return (lat1 * Math.PI / 180.0)
    }*/



}