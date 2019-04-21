package com.example.workmanagerdemo;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.BatteryManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class AlarmService extends Service{

    private BroadcastReceiver mBatteryWatcher;
    public int level,startLevel,stopLevel;
    Intent startIntent;
    NotificationCompat.Builder mNotification;
    WorkManager mWorkManager;

    //start Alarm Requirements
    String containpercentage,batteryHealth = "N/A", concatenate = "35";
    int ChargerStatus,temperature; //for getting the discharging status
    MediaPlayer alarmtone;
    Boolean isAlarmStarted = true;

    //for charging interval
    ChargingInterval mInterval;
    String getInterval;
    // REMOVE TAG AFTER TESTING
    public static final String TAG = "AlarmServiceTag";

    @Override
    public void onCreate() {
        super.onCreate();
        mWorkManager = WorkManager.getInstance();

        final SharedPreferences shared1=getSharedPreferences("percentage",Context.MODE_PRIVATE);

        // change level from here
        containpercentage = shared1.getString("percentage","100");

        mBatteryWatcher = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                      level = intent.getIntExtra("level",0);
                      ChargerStatus = intent.getIntExtra("status",BatteryManager.BATTERY_STATUS_UNKNOWN);
                      batteryHealth = getHealth(intent);
                      temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
                      if (temperature > 0) {
                        float temp = ((float) temperature) / 10f;
                        concatenate = temp + "°C";
                        }

                if (containpercentage.equals(String.valueOf(level)))
                {
                    if (isAlarmStarted)
                    {
                        Log.i(TAG, "Level meet and alarm is running");
                        isAlarmStarted = false;
                        stopLevel = level;
                        getInterval = mInterval.stop_stopwatch();
                        Log.i(TAG,"stop level = " + stopLevel + "interval = " + getInterval);
                        startAlarm();
                    }
                }

                if (ChargerStatus == BatteryManager.BATTERY_STATUS_DISCHARGING){

                    Log.i(TAG,"Foreground Service Stopped and WorkManager started.");
                    stopSelf();
                }
                // need to put in Receiver because if user change the percentage after service is running
                containpercentage = shared1.getString("percentage","100");
                //inside the broadcast receiver for update the notification
                createNotification(batteryHealth, concatenate,2);



            }
        };

        // outside the Broadcast Receiver
        createNotification(batteryHealth, concatenate, 1);
        registerReceiver(mBatteryWatcher,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        //initialize
        alarmtone = new MediaPlayer();
        mInterval = new ChargingInterval();
        //for getting interval
        createIntervalZone();
    }

    private void createIntervalZone() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    startLevel = level;
                    mInterval.start_stopwatch();
                    Log.i(TAG,"start level = " + startLevel + "stopwatch started");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBatteryWatcher!=null){
            unregisterReceiver(mBatteryWatcher);
            mBatteryWatcher = null;
        }
        //start the Work Request again
        Log.i(TAG,"Service stopped and ondestroy called");
        createWorkRequest();
        stopService(new Intent(this,StartRingtone.class));

    }

    private void createNotification(String batteryHealth, String temperature, int counter){
        if (counter<=1) {
            Log.i(TAG, "Notification Created");
            startIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 10, startIntent, 0);

            mNotification = new NotificationCompat.Builder(this, Startup.CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("Battery target level is " + containpercentage)
                    .setContentText("Temp: " + temperature + " & Battery Health: " + batteryHealth)
                    .setShowWhen(false)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setOnlyAlertOnce(true);

            startForeground(1, mNotification.build());
        }
        else {
            mNotification.setContentTitle("Battery target level is " + containpercentage);
            mNotification.setContentText("Temp: " + temperature + " & Battery Health: " + batteryHealth);
            startForeground(1, mNotification.build());
        }
            // this notification will cancelled only when service is destroyed

    }

    public void createWorkRequest(){
        Log.d(TAG, "setupWorkRequest: " + "setting request");
        // Create charging constraint
        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(true)
                .build();
        OneTimeWorkRequest chargerListen = new OneTimeWorkRequest
                .Builder(ChargerListener.class)
                .setConstraints(constraints)
                .build();
        mWorkManager.enqueue(chargerListen);
    }

    private void startAlarm(){
        // start the service of alarm
        startService(new Intent(this,StartRingtone.class));

        //open dialog
        Intent startRingtoneIntent = new Intent(this,Ringtone_Manager_Dialog.class);
        startRingtoneIntent.putExtra("timeinterval",getInterval);
        startRingtoneIntent.putExtra("startPercentageLevel",startLevel);
        startRingtoneIntent.putExtra("stopPercentageLevel",stopLevel);
        startRingtoneIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try {
            startActivity(startRingtoneIntent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String getHealth(Intent intent){

        boolean present=intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);
        String healthlbl = "Good";
        if (present){
            int health=intent.getIntExtra(BatteryManager.EXTRA_HEALTH,0);

            switch (health){
                case BatteryManager.BATTERY_HEALTH_COLD:
                    healthlbl= "COLD";
                    break;
                case BatteryManager.BATTERY_HEALTH_DEAD:
                    healthlbl = "DEAD";
                    break;

                case BatteryManager.BATTERY_HEALTH_GOOD:
                    healthlbl = "GOOD";
                    break;

                case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                    healthlbl = "OVER VOLTAGE";
                    break;

                case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                    healthlbl = "OVER HEAT";
                    break;

                case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                    healthlbl = "UNKNOWN";
                    break;

                case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                default:
                    break;

            }
        }

        return healthlbl;
    }
}
