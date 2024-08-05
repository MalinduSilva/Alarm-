package com.malindu.alarm15.utils;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.malindu.alarm15.models.Alarm;
import com.malindu.alarm15.models.AlarmSoundItem;
import com.malindu.alarm15.models.VibratePattern;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AlarmUtils {
    private static final String TAG = "AlarmUtils";
    private Alarm newAlarm = new Alarm();
    private static int alarmNo = 0;

    public static void setAlarm(Context context, Alarm alarm) {
        boolean isNew = false;
        //SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.ALARM_PREFERENCES_FILE, Context.MODE_PRIVATE);
        if (alarm.getAlarmID().isEmpty()) {
            alarm.setAlarmID((Constants.ALARM_KEY + System.currentTimeMillis()));
            //int alarmNumber = sharedPreferences.getInt(Constants.ALARM_COUNT_KEY, 0) + 1;
            //alarm.setAlarmID(Constants.ALARM_KEY + alarmNumber);
            isNew = true;
        }
        if (alarm.getAlarmTime().before(Calendar.getInstance())) {
            alarm.setDate(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DATE) + 1);
        }

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("AlarmStr", alarm.getStringObj());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarm.getAlarmID().substring(Constants.ALARM_KEY.length()).hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        //PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(alarm.getAlarmID().substring(Constants.ALARM_KEY.length())), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
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
            saveAlarm(context, alarm, isNew);
            Log.d(TAG, "setAlarm: " + alarm);
            showToast(context, alarm);
        } else {
            Log.e(TAG, "setAlarm: Alarm Manager is null!!!", new Exception("Exception: Alarm Manager is null!!!")); Log.i(TAG, "setAlarm: Alarm Manager is null!!!");
        }
    }

    public static void saveAlarm(Context context, Alarm alarm, boolean isNew) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.ALARM_PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(alarm.getAlarmID(), alarm.getStringObj());

//        if (isNew) {
//            editor.putInt(Constants.ALARM_COUNT_KEY, Integer.parseInt(alarm.getAlarmID().substring(Constants.ALARM_KEY.length())));
//        }
        editor.apply();
        Log.d(TAG, "Alarm saved: " + alarm);
    }

    public static void cancelAlarm(Context context, Alarm alarm) {
        alarm.setTurnedOn(false);
        Intent intent = new Intent(context, AlarmReceiver.class);
        //PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(alarm.getAlarmID().substring(Constants.ALARM_KEY.length())), intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarm.getAlarmID().substring(Constants.ALARM_KEY.length()).hashCode(), intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
            saveAlarm(context, alarm, false);
            Log.d(TAG, "cancelAlarm: Alarm cancelled with ID: " + alarm.getAlarmID());
        }
    }

    public static void deleteAlarm(Context context, Alarm alarm) {
        //int alarmId = Integer.parseInt(alarm.getAlarmID().substring(Constants.ALARM_KEY.length()));
        Intent intent = new Intent(context, AlarmReceiver.class);
        //PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarm.getAlarmID().substring(Constants.ALARM_KEY.length()).hashCode(), intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
            Log.d(TAG, "deleteAlarm: Alarm cancelled with ID: " + alarm.getAlarmID());
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.ALARM_PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(alarm.getAlarmID());
        editor.apply();
        Log.d(TAG, "deleteAlarm: Alarm removed from SharedPreferences with ID: " + alarm.getAlarmID());
    }

    private static void showToast(Context context, Alarm alarm) {
        //TODO : implement
        Toast.makeText(context, alarm.getAlarmID(), Toast.LENGTH_SHORT).show();
        Log.d("showToast", "showToast: " + alarm.getAlarmID());
        Calendar c = Calendar.getInstance();
        long time = alarm.getAlarmTime().getTimeInMillis();
        long timeLeft = alarm.getAlarmTime().getTimeInMillis() - c.getTimeInMillis(); //System.currentTimeMillis();
        long days = TimeUnit.MILLISECONDS.toDays(timeLeft);
        timeLeft -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(timeLeft);
        timeLeft -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeft);
        timeLeft -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeLeft);
        
//        long years = timeLeft / (365L * 24 * 60 * 60 * 1000); timeLeft = timeLeft % (365L * 24 * 60 * 60 * 1000);
//        long days = timeLeft / 24 * 60 * 60 * 1000; timeLeft = timeLeft % (24 * 60 * 60 * 1000);
//        long seconds = timeLeft % (60 * 1000);
//        long minutes = (timeLeft - seconds * 60 * 1000) / 60;
//        long years = timeLeft / (365L * 24 * 60 * 60 * 1000);
//        int minutes = (int) (timeLeft % (1000 * 60 * 60));
//        long seconds = timeLeft / 1000;
//        long minutes = (seconds / 60) % 60;
//        long hours = (seconds / 3600) % 24;
//        long days = (seconds / 86400) % 365;
//        long years = seconds / (86400 * 365);
//        String timeLeftStr = years + " years, " + days + " days, " + hours + " hours, " + minutes + " minutes, " + seconds + " seconds left";
//        Toast.makeText(context, timeLeftStr, Toast.LENGTH_LONG).show();
//        Log.d(TAG, "showToast: " + timeLeftStr + ":" + time +":"+ System.currentTimeMillis());
    }

    public static void createFGSNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    Constants.CHANNEL_ID_ALARM,
                    Constants.CHANNEL_NAME_ALARM,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setVibrationPattern(new long []{ 1000 , 1000 , 1000 , 1000 , 1000 });
            channel.setSound(null, null);
            channel.enableVibration(false);
            channel.setBypassDnd(true);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static List<AlarmSoundItem> getAvailableAlarmSounds(Context context) {
        List<AlarmSoundItem> alarmSoundList = new ArrayList<>();

        RingtoneManager ringtoneManager = new RingtoneManager(context);
        ringtoneManager.setType(RingtoneManager.TYPE_ALARM);
        Cursor cursor = ringtoneManager.getCursor();

        while (cursor.moveToNext()) {
            String title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            Uri alarmSoundUri = ringtoneManager.getRingtoneUri(cursor.getPosition());

            alarmSoundList.add(new AlarmSoundItem(title, alarmSoundUri));
        }
        cursor.close();
        return alarmSoundList;
    }

    public static AlarmSoundItem getDefaultAlarmSound(Context context) {
        RingtoneManager ringtoneManager = new RingtoneManager(context);
        Uri defaultAlarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        if (defaultAlarmUri != null) {
            String title = RingtoneManager.getRingtone(context, defaultAlarmUri).getTitle(context);
            return new AlarmSoundItem(title, defaultAlarmUri);
        }
        return null;
    }

    public static String[] getVibratePatternNames() {
        VibratePattern[] patterns = VibratePattern.values();
        String[] patternNames = new String[patterns.length];
        for (int i = 0; i < patterns.length; i++) {
            patternNames[i] = patterns[i].name();
        }
        return patternNames;
    }
}
