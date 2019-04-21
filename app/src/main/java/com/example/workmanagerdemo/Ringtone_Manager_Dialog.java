package com.example.workmanagerdemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class Ringtone_Manager_Dialog extends Activity {
    TextView chargingInfo;
    Button cancel;
    Intent getIntervalIntent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE); //for disable the title
        this.setFinishOnTouchOutside(false); // activity not closes if user click outside the activity

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_ringtone_manager);

        cancel = findViewById(R.id.buttoncanceldialog);
        chargingInfo = findViewById(R.id.text_percentage_interval);

        getIntervalIntent = getIntent();
        String timeInterval = getIntervalIntent.getStringExtra("timeinterval");
        int startLevel = getIntervalIntent.getIntExtra("startPercentageLevel",0);
        int stopLevel = getIntervalIntent.getIntExtra("stopPercentageLevel",100);
        Log.i(AlarmService.TAG, "intent time: " + timeInterval);

        if (timeInterval!=null) {
            String displayedText = "Charging Interval " + String.valueOf(startLevel) + " % to " + String.valueOf(stopLevel) + " % in " + timeInterval;
            chargingInfo.setText(displayedText);
            Log.i(AlarmService.TAG, "onCreateDialog: Interval " + displayedText);
        }
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(Ringtone_Manager_Dialog.this,StartRingtone.class));
                Ringtone_Manager_Dialog.this.finish();
            }
        });
        // this should be the last line of method
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    public void Close(View view) {
        stopService(new Intent(Ringtone_Manager_Dialog.this,StartRingtone.class));
        this.finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
