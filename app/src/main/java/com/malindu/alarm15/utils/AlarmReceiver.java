package com.malindu.alarm15.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.malindu.alarm15.R;
import com.malindu.alarm15.models.Alarm;
import com.malindu.alarm15.ui.AlarmRingFullscreenActivity;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    private PowerManager.WakeLock wakeLock;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");

        Log.d(TAG, "onReceive: " + intent.getStringExtra("AlarmStr"));
//        Alarm alarm = new Alarm();
//        alarm = Alarm.getAlarmObj(intent.getStringExtra("AlarmStr"));
//        alarm.setTurnedOn(false);
//        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.ALARM_PREFERENCES_FILE, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString(alarm.getAlarmID(), alarm.getStringObj());
//        editor.apply();
//        Intent fullscreenIntent = new Intent(context, AlarmRingFullscreenActivity.class);
//        fullscreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent fullscreenPendingIntent = PendingIntent.getActivity(context, 0, fullscreenIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(
//                    Constants.CHANNEL_ID_ALARM,
//                    "Alarm Notifications",
//                    NotificationManager.IMPORTANCE_HIGH);
//            channel.setBypassDnd(true); //TODO this is not working, has to guide the user to manually enable it
//            channel.setSound(null, null);
//            channel.enableVibration(true);
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        Intent dismissIntent = new Intent(context, AlarmReceiverDismiss.class);
//        dismissIntent.setAction(Constants.ACTION_DISMISS);
//        dismissIntent.putExtra("AlarmStr", alarm.getStringObj());
//        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(context, 12345, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//        Intent snoozeIntent = new Intent(context, AlarmReceiverSnooze.class);
//        snoozeIntent.setAction(Constants.ACTION_SNOOZE);
//        snoozeIntent.putExtra("AlarmStr", alarm.getStringObj());
//        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(context, 12346, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.CHANNEL_ID_ALARM)
//                .setSmallIcon(R.drawable.icon_alarm_outlined)
//                .setContentTitle(alarm.getAlarmLabel().isEmpty() ? "Alarm" : alarm.getAlarmLabel())
//                .setContentText(alarm.getAlarmID() + " - " + Alarm.getAlarmTimeAsText(context, alarm))//"Wake up! Your alarm is ringing.")
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setCategory(NotificationCompat.CATEGORY_ALARM)
//                .setFullScreenIntent(fullscreenPendingIntent, true)
//                .setAutoCancel(true)
//                .setSound(null);
//                //.addAction(R.drawable.icon_alarm_off, "Dismiss", dismissPendingIntent)
//                //.addAction(R.drawable.icon_snooze, "Snooze", snoozePendingIntent);
//
//        notificationManager.notify(Constants.NOTIFICATION_ID, builder.build());

//        Intent serviceIntent = new Intent(context, AlarmSoundService.class);
//        serviceIntent.setAction(Constants.ACTION_START_ALARM);
//        context.startService(serviceIntent);
//        Intent alarmServiceIntent = new Intent(context, AlarmService.class);
//        alarmServiceIntent.putExtra("AlarmStr", alarm.getStringObj());
//        alarmServiceIntent.setAction()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.startForegroundService(alarmServiceIntent);
//        }

//        // Acquire WakeLock
//        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "AlarmReceiver::WakeLock");
//        wakeLock.acquire(10*60*1000L /*10 minutes*/);

        Intent i = new Intent(context, AlarmService.class);
        i.setAction(Constants.ACTION_START_ALARM_SERVICE);
        i.putExtra("AlarmStr", intent.getStringExtra("AlarmStr"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(i);
        } else {
            context.startService(i);
        }
    }
}