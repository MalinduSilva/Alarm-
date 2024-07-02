package com.malindu.alarm15.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.malindu.alarm15.R;
import com.malindu.alarm15.models.Alarm;
import com.malindu.alarm15.ui.AlarmRingFullscreenActivity;

public class AlarmService extends Service {
    private static final String TAG = "AlarmService";
    private Handler handler;
    public static final String ACTION_DISMISS = "com.malindu.alarm15.ACTION_DISMISS";
    public static final String ACTION_SNOOZE = "com.malindu.alarm15.ACTION_SNOOZE";
    public AlarmService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Alarm alarm = new Alarm();
        alarm = Alarm.getAlarmObj(intent.getStringExtra("AlarmStr"));
        Context context = getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        saveAlarm(context, alarm);
        createNotificationChannel(notificationManager);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Log.d(TAG, "run: Alarm ringing");
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }).start();
        startForeground(Constants.NOTIFICATION_ID, createNotification(context, notificationManager, alarm));
//        return super.onStartCommand(intent, flags, startId);
        handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Alarm ringing");
                // Handle alarm ringing logic here (e.g., play sound)
            }
        });
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void saveAlarm(Context context, Alarm alarm) {
        alarm.setTurnedOn(false);
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.ALARM_PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(alarm.getAlarmID(), alarm.getStringObj());
        editor.apply();
    }

    private void createNotificationChannel(NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    Constants.CHANNEL_ID_ALARM,
                    "Alarm Notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            //channel.setBypassDnd(true); //TODO this is not working, has to guide the user to manually enable it
            //channel.setSound(null, null);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private Notification createNotification(Context context, NotificationManager notificationManager, Alarm alarm) {
        Intent fullscreenIntent = new Intent(context, AlarmRingFullscreenActivity.class);
        fullscreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent fullscreenPendingIntent = PendingIntent.getActivity(context, 0, fullscreenIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.CHANNEL_ID_ALARM)
                .setSmallIcon(R.drawable.icon_alarm_outlined)
                .setContentTitle(alarm.getAlarmLabel().isEmpty() ? "Alarm" : alarm.getAlarmLabel())
                .setContentText(alarm.getAlarmID() + " - " + Alarm.getAlarmTimeAsText(context, alarm))//"Wake up! Your alarm is ringing.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setFullScreenIntent(fullscreenPendingIntent, true)
                .setAutoCancel(true);
                //.setSound(null);
        //notificationManager.notify(Constants.NOTIFICATION_ID, builder.build());
        return builder.build();
    }
}