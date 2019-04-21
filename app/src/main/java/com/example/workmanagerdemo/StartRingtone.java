package com.example.workmanagerdemo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

public class StartRingtone extends Service {

    // extend the service for play ringtone even if application closed.

    private MediaPlayer playingTone;
    AudioManager mAudioManager;


    @Override
    public void onCreate() {
        super.onCreate();
        playingTone = new MediaPlayer();
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //set volume to max
        if (mAudioManager!=null) {
            Log.i(AlarmService.TAG,"audiomanager not null");
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        }
        //start the ringtone
        start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    public void start()
    {
        Log.i(AlarmService.TAG,"Ringtone started");
        if (!playingTone.isPlaying()){


            switch ("Default1")
            {
                case "Default1":
                    playingTone= MediaPlayer.create(this,R.raw.low1);
                    playingTone.start();
                    playingTone.setLooping(true);
                    break;

                case "Default2":
                    playingTone= MediaPlayer.create(this,R.raw.low2);
                    playingTone.start();
                    playingTone.setLooping(true);
                    break;
                case "Default3":
                    playingTone = MediaPlayer.create(this, R.raw.low3);
                    playingTone.start();
                    playingTone.setLooping(true);
                    break;
                case "Default4":
                    playingTone = MediaPlayer.create(this, R.raw.ring_s);
                    playingTone.start();
                    playingTone.setLooping(true);
                    break;
//                case "custom":
//                    playingTone=new MediaPlayer();
//                    playingTone.setAudioStreamType(AudioManager.STREAM_MUSIC);
//
//                    try {
//                        playingTone.setDataSource(this,mAlert_dialog.audiofile);
//                        playingTone.prepare();
//                        playingTone.start();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    break;

                default:
                {
                    playingTone= MediaPlayer.create(this,R.raw.low1);
                    playingTone.start();
                    playingTone.setLooping(true);
                }
            }
        }
    }

    public void stop(){
        if (playingTone.isPlaying()){
            playingTone.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stop();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
