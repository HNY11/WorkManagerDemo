package com.example.workmanagerdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import androidx.work.Constraints;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class MainActivity extends AppCompatActivity {

    WorkManager mWorkManager;
    public static final String TAG="MainActivityTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWorkManager = WorkManager.getInstance();
        setupWorkRequest();
    }

    private void setupWorkRequest() {
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
}
