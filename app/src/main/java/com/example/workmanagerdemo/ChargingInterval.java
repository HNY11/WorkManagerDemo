package com.example.workmanagerdemo;

import android.os.Handler;
import android.os.SystemClock;

public class ChargingInterval {
    private long startTime = 0L,timeInMilliSeconds = 0L,swapBuffer = 0L,updateTime = 0L;
    private Handler mtimeHandler;
    private Runnable mRunnable;
    private Boolean isRunning = false;
    private int hours,minutes,seconds;
    String timeZone;
    ChargingInterval(){

        // initialize the handler and runnable
        mtimeHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                timeInMilliSeconds = SystemClock.uptimeMillis() - startTime;
                updateTime = swapBuffer + timeInMilliSeconds;
                seconds = (int) updateTime/1000;
                minutes = seconds/60;
                hours = minutes/60;
                seconds %= 60;
                minutes %= 60;
               // set the time wherever you want.
                // mTextViewStopwatch.setText("" + hours + ":" + String.format("%02d",minutes) +":" + String.format("%02d",seconds));
                mtimeHandler.postDelayed(this,1000);
            }
        };
    }

    public void start_stopwatch() {
        if (!isRunning) {
            startTime = SystemClock.uptimeMillis();
            mtimeHandler.postDelayed(mRunnable, 1000);
           // mTextViewStopwatch.setText("");
            isRunning = true;
        }
    }

    public String stop_stopwatch() {
        if (isRunning) {
            swapBuffer += timeInMilliSeconds;
            mtimeHandler.removeCallbacks(mRunnable);
            isRunning = false;

            timeZone = "" + hours + ":" + String.format("%02d",minutes) +":" + String.format("%02d",seconds);
            return timeZone;
        }
        return null;
    }

    public void reset_stopwatch() {
        mtimeHandler.removeCallbacks(mRunnable);
        startTime=swapBuffer=timeInMilliSeconds=updateTime=0L;
        isRunning=false;
    }

}
