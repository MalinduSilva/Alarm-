package com.malindu.alarm15.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.malindu.alarm15.R;
import com.malindu.alarm15.models.Alarm;
import com.malindu.alarm15.ui.AlarmRingFullscreenActivity;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");

        Log.d(TAG, "onReceive: " + intent.getStringExtra("AlarmStr"));
        Alarm alarm = new Alarm();
        alarm = Alarm.getAlarmObj(intent.getStringExtra("AlarmStr"));
        Intent fullscreenIntent = new Intent(context, AlarmRingFullscreenActivity.class);
        fullscreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent fullscreenPendingIntent = PendingIntent.getActivity(context, 0, fullscreenIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    Constants.CHANNEL_ID_ALARM,
                    "Alarm Notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setBypassDnd(true);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.CHANNEL_ID_ALARM)
                .setSmallIcon(R.drawable.icon_alarm_outlined)
                .setContentTitle(alarm.getAlarmLabel())
                .setContentText("Wake up! Your alarm is ringing.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setFullScreenIntent(fullscreenPendingIntent, true)
                .setAutoCancel(true);

        notificationManager.notify(Constants.NOTIFICATION_ID, builder.build());
    }
}