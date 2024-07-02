package com.malindu.alarm15.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;

public class AlarmSoundService extends Service {
    private MediaPlayer mediaPlayer;
    public AlarmSoundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case Constants.ACTION_START_ALARM:
                    startAlarmSound(this);
                    break;
                case Constants.ACTION_STOP_ALARM:
                    stopAlarmSound();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void startAlarmSound(Context context) {
        try {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(context.getApplicationContext(), alarmSound);
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build());
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopAlarmSound() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}