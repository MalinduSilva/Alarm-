package com.malindu.alarm15.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.malindu.alarm15.models.Alarm;

public class AlarmUtils {
    private static final String TAG = "AlarmUtils";
    private Alarm newAlarm = new Alarm();
    private static int alarmNo = 0;
    private static final String ALARM_PREFERENCES_FILE = "ALARM_PREFERENCES_FILE";
    private static final String ALARM_KEY  = "ALARM_";
    private static final String ALARM_COUNT_KEY  = "ALARM_COUNT";

    public static void setAlarm(Context context, Alarm alarm) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ALARM_PREFERENCES_FILE, Context.MODE_PRIVATE);
        alarmNo = sharedPreferences.getInt(ALARM_COUNT_KEY, 0);
        alarmNo++;
        //alarm.setAlarmID(ALARM_KEY + alarmNo);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmNo, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    Log.d(TAG, "setAlarm: permission ok");
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm.getAlarmTime().getTimeInMillis(), pendingIntent);
                }
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm.getAlarmTime().getTimeInMillis(), pendingIntent);
            }
            saveAlarm(context, alarm);
            Log.d(TAG, "setAlarm: " + alarm.toString());
            //Log.d(TAG, "setAlarm: " + alarm.getStringObj());
        }
    }

    private static void saveAlarm(Context context, Alarm alarm) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ALARM_PREFERENCES_FILE, Context.MODE_PRIVATE);
        alarmNo = sharedPreferences.getInt(ALARM_COUNT_KEY, 0);
        alarmNo++;
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (alarm.getAlarmID().isEmpty()) { // means this alarm is new
            alarm.setAlarmID(ALARM_KEY + alarmNo);
            editor.putString(alarm.getAlarmID(), alarm.getStringObj());
            editor.putInt(ALARM_COUNT_KEY, alarmNo);
            Log.d(TAG, "New alarmNo: " + alarmNo + " - AlarmID: " + alarm.getAlarmID());
        } else {
            editor.putString(alarm.getAlarmID(), alarm.getStringObj());
            Log.d(TAG, "Existing alarmNo: " + alarm.getAlarmID().substring(ALARM_KEY.length()) + " - AlarmID: " + alarm.getAlarmID());
        }
        editor.apply();
    }

    public static void cancelAlarm(Context context, Alarm alarm) {
        alarm.setTurnedOn(false);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(alarm.getAlarmID().substring(ALARM_KEY.length())), intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
            saveAlarm(context, alarm);
            Log.d(TAG, "cancelAlarm: Alarm cancelled with ID: " + alarm.getAlarmID());
        }
    }
}
