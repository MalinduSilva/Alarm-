package com.malindu.alarm15.utils;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.malindu.alarm15.R;
import com.malindu.alarm15.models.Alarm;
import com.malindu.alarm15.models.VibratePattern;
import com.malindu.alarm15.ui.AlarmRingFullscreenActivity;

public class AlarmService extends Service {
    private static final String TAG = "AlarmService";
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private MediaSession mediaSession;
    public AlarmService() { }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AlarmUtils.createFGSNotificationChannel(getApplicationContext());
//        mediaSession = new MediaSession(getApplicationContext(), "AlarmService");
//        mediaSession.setCallback(new MediaSession.Callback() {
//            @Override
//            public boolean onMediaButtonEvent(@NonNull Intent mediaButtonIntent) {
//                Log.d(TAG, "onMediaButtonEvent: ");
//                KeyEvent keyEvent = mediaButtonIntent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
//                if (keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP || keyEvent.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)) {
//                    if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
//                        // Handle the volume button press to silence the alarm
//                        stopAlarmSound();
//                        return true;
//                    }
//                }
//                return super.onMediaButtonEvent(mediaButtonIntent);
//            }
//        });
//        mediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS | MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
//        mediaSession.setActive(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Alarm alarm = new Alarm();
        Context context = getApplicationContext();
        String action = intent.getAction();

        if (action != null) {
            switch (action) {
                case Constants.ACTION_START_ALARM_SERVICE:
                    Log.d(TAG, "onStartCommand: ACTION_START_ALARM_SERVICE");
                    alarm = Alarm.getAlarmObj(intent.getStringExtra("AlarmStr"));
                    startForeground(Constants.NOTIFICATION_ID, createNotification(context, alarm));
                    startAlarmSound(context, alarm);
                    startAlarmVibrate(context, alarm);
                    saveAlarm(context, alarm);
                    break;
                case Constants.ACTION_DISMISS:
                    Log.d(TAG, "onStartCommand: ACTION_DISMISS");
                    Intent dismissIntent = new Intent(Constants.ACTION_DISMISS);
                    stopAlarmSound();
                    stopAlarmVibrate();
                    context.sendBroadcast(dismissIntent);
                    stopSelf();
                    break;
                case Constants.ACTION_SNOOZE:
                    Log.d(TAG, "onStartCommand: SNOOZE");
                    alarm = Alarm.getAlarmObj(intent.getStringExtra("AlarmStr"));
                    snoozeAlarm(context, alarm);
                    stopAlarmSound();
                    stopAlarmVibrate();
                    stopSelf();
                    break;
                case Constants.ACTION_SNOOZED_ALARM:
                    Log.d(TAG, "onStartCommand: ACTION_SNOOZED_ALARM");
                    alarm = Alarm.getAlarmObj(intent.getStringExtra("AlarmStr"));
                    startForeground(Constants.NOTIFICATION_ID, createNotification(context, alarm));
                    startAlarmSound(context, alarm);
                    startAlarmVibrate(context, alarm);
                    break;
                case Constants.ACTION_SILENCE_ALARM:
                    Log.d(TAG, "onStartCommand: ACTION_SILENCE_ALARM");
                    stopAlarmSound();
                    stopAlarmVibrate();
                default:
                    Log.d(TAG, "onStartCommand: " + action);
                    break;
            }
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
//        if (mediaSession != null) {
//            mediaSession.release();
//        }
        super.onDestroy();
    }

    private void saveAlarm(Context context, Alarm alarm) {
        alarm.setTurnedOn(false);
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.ALARM_PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(alarm.getAlarmID(), alarm.getStringObj());
        editor.apply();
    }


    private Notification createNotification(Context context, Alarm alarm) {
        // Intent for full-screen ringing activity
        Intent fullscreenIntent = new Intent(context, AlarmRingFullscreenActivity.class);
        fullscreenIntent.putExtra("AlarmStr", alarm.getStringObj());
        fullscreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent fullscreenPendingIntent = PendingIntent.getActivity(context, 0, fullscreenIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Intent for dismiss action
        Intent dismissIntent = new Intent(context, AlarmService.class);
        dismissIntent.putExtra("AlarmStr", alarm.getStringObj());
        dismissIntent.setAction(Constants.ACTION_DISMISS);
        PendingIntent dismissPendingIntent = PendingIntent.getService(context, 1, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Intent for dismiss action
        Intent snoozeIntent = new Intent(context, AlarmService.class);
        snoozeIntent.putExtra("AlarmStr", alarm.getStringObj());
        snoozeIntent.setAction(Constants.ACTION_SNOOZE);
        PendingIntent snoozePendingIntent = PendingIntent.getService(context, 2, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        String label = alarm.getAlarmLabel();
        String text = Alarm.getAlarmTimeAsText(context, alarm);
        if (label == null) {label = getString(R.string.label_alarm); }
        if (text == null) {text = "not set"; }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.CHANNEL_ID_ALARM)
                .setSmallIcon(R.drawable.icon_alarm_outlined)
                .setContentTitle(label)
                .setContentText(alarm.getAlarmID() + " - " + text)//"Wake up! Your alarm is ringing.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setFullScreenIntent(fullscreenPendingIntent, true)
                .setAutoCancel(true)
                .setVibrate(null) //new long []{ 1000 , 1000 , 1000 , 1000 , 1000 }
                .setSound(null)
                .setOngoing(true)
                .addAction(R.drawable.icon_alarm_off, getString(R.string.dismiss), dismissPendingIntent)
                .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE);
        if (alarm.isSnooze()) {
            builder.addAction(R.drawable.icon_snooze, getString(R.string.snooze), snoozePendingIntent);
        }
        //notificationManager.notify(Constants.NOTIFICATION_ID, builder.build());
        return builder.build();
    }

    private void snoozeAlarm(Context context, Alarm alarm) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        Alarm snoozedAlarm = Alarm.getSnoozedAlarm(context, alarm);
        intent.putExtra("AlarmStr", alarm.getStringObj());
        intent.setAction(Constants.ACTION_SNOOZED_ALARM);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, snoozedAlarm.getAlarmID().substring(Constants.ALARM_KEY.length()).hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, snoozedAlarm.getAlarmTime().getTimeInMillis(), pendingIntent);
                }
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, snoozedAlarm.getAlarmTime().getTimeInMillis(), pendingIntent);
            }
        }
    }

    private void startAlarmSound(Context context, Alarm alarm) {
        if (alarm.isSound()) {
            try {
                //Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(context.getApplicationContext(), alarm.getAlarmSound().getUri());
                mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build());
                mediaPlayer.setLooping(true);
                mediaPlayer.prepare();
                mediaPlayer.setVolume(alarm.getAlarmVolume(), alarm.getAlarmVolume());
                mediaPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void stopAlarmSound() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    private void startAlarmVibrate(Context context, Alarm alarm) {
        if (alarm.isVibration()) {
            vibrator = context.getSystemService(Vibrator.class);
            VibratePattern vibratePattern = alarm.getVibratePattern();
            //Log.d(TAG, "startAlarmVibrate: " + Build.VERSION.SDK_INT + " " + vibrator.hasAmplitudeControl());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    vibrator.vibrate(VibrationEffect.createWaveform(vibratePattern.getTimings(), vibratePattern.getAmplitudes(), vibratePattern.getRepeat()));
                }
            } else {
                vibrator.vibrate(new long []{ 1000 , 1000 , 1000 , 1000 , 1000 }, 1);
            }
        }
    }
    private void stopAlarmVibrate() {
        if (vibrator != null) { vibrator.cancel(); }
    }
}