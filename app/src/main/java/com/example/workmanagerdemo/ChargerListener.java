package com.example.workmanagerdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class ChargerListener extends Worker {

    private static final String TAG = "ChangeListenerTag";
    private Context mContext;

    public ChargerListener(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork: "+"Charger Connected");
        Intent startIntent = new Intent(mContext, AlarmService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // for oreo and above we have to start foreground service
            Log.i(AlarmService.TAG,"startForegroundService called.");
            mContext.startForegroundService(startIntent);
        }else {
            mContext.startService(startIntent);
        }
       // sendNotification("Notify","Charger Connected");
        return Result.success();
    }
}
