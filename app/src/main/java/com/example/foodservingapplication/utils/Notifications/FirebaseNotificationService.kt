package com.example.foodservingapplication.utils.Notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.nfc.Tag
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.foodservingapplication.R
import com.example.foodservingapplication.UI.User.MainActivity
import com.example.foodservingapplication.UI.Volunteer.Post_Showed
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class FirebaseNotificationService : FirebaseMessagingService(){
    val CHANNEL_ID:String = "My_CHANNEL"
    val TAG :String = "Firebase"
     var intent: Intent?=null;


    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        if(p0.data["title"].equals("GET READY YOUR FOOD")){
            intent  = Intent(this,MainActivity::class.java)
            Log.d("Firebase","putttinggg the extrasss" )

        }else {
             intent  = Intent(this,Post_Showed::class.java)

        }
        intent!!.putExtra(getString(R.string.title_notification),p0.data["title"])
        intent!!.putExtra(getString(R.string.message_notification),p0.data["message"])
        intent!!.putExtra(getString(R.string.post_id_notification),p0.data["postId"])

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)

        }
       // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        Log.d("Firebase","putttinggg the extrasss" )

        val pendingIntent = PendingIntent.getActivity(this,0,intent,FLAG_ONE_SHOT)
        val notification = NotificationCompat.Builder(this,CHANNEL_ID)
            .setContentTitle(p0.data["title"])
            .setContentText(p0.data["message"])
            .setSmallIcon(R.drawable.ic_android_black_24dp)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationID,notification)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channelName = "channelName"
        val channel = NotificationChannel(CHANNEL_ID,channelName,IMPORTANCE_HIGH).apply {
        description = "My channel description"
            enableLights(true)
            lightColor = Color.GREEN

        }
        notificationManager.createNotificationChannel(channel)


    }
}