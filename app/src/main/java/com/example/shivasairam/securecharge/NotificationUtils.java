package com.example.shivasairam.securecharge;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.Resources;;
import android.content.Intent;
import android.os.Build;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.example.shivasairam.securecharge.MainActivity;
import com.example.shivasairam.securecharge.R;

import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
//import android.support.v7.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.app.Notification;

/**
 * Created by Shiva Sai Ram on 04-10-2017.
 */

public class NotificationUtils {
    public static void remindUserBecauseChargerUnplugged(Context context,String title,String text){
//        this methid will create a notification for charging.
//        initializinngthe nnotification whch will be shwed
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context,R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_drink_notification)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(title)
                .setContentText(text)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                  .setAutoCancel(true);
//            set top priority but it only works on jellybean
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            notificationBuilder.setPriority(Notification.PRIORITY_MAX);
        }
//getting the notification manager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
//  trigger the notification by calling notify
        notificationManager.notify(0,notificationBuilder.build());
    }

    private static PendingIntent contentIntent(Context context){
//                create intent which oppens up the main activity
        Intent startActivityIntent = new Intent(context,MainActivity.class);
//        create a pending intent whch gets the intent as the parameter
        return PendingIntent.getActivity(context,0,startActivityIntent,0);
    }

    private static Bitmap largeIcon(Context context){
        Resources res = context.getResources();

        Bitmap largeIcon = BitmapFactory.decodeResource(res,R.drawable.ic_local_drink_black_24px);
        return largeIcon;
    }
}
