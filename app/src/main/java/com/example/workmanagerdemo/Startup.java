package com.example.workmanagerdemo;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;


public class Startup extends Application {

    public static final String CHANNEL_ID = "notification_alarm";
    NotificationManager mManager;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels() {
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
             NotificationChannel channel = new NotificationChannel(
                     CHANNEL_ID,"Charging alarm Status"
                     , NotificationManager.IMPORTANCE_HIGH
             );
             channel.setDescription("This channel is to show you that " +
                     "app will notify you when charging is completed");

             mManager = getSystemService(NotificationManager.class);
             mManager.createNotificationChannel(channel);
        }
    }
}
