package com.example.foodservingapplication.utils.Notifications;

import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.foodservingapplication.R;

public class displayNotificationHelperUser {

    public static void  displayNotification(Context context, String title , String body){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context,"channelIdDisplayUser")
                .setSmallIcon(R.drawable.ic_menu_black_24dp)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        managerCompat.notify(1,mBuilder.build());
    }

}
