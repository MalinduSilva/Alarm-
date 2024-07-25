package com.malindu.alarm15.utils;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.malindu.alarm15.models.LocationAlarm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LocationUtils {
    private static final String TAG = "LocationUtils";

    public static void setLocationAlarm(Context context, LocationAlarm locationAlarm) {
        Intent intent = new Intent(context, LocationService.class);
        if (isLocationServiceRunning(context)) {
            intent.setAction(Constants.ACTION_NEW_LOCATION_ALARM);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
        saveLocationAlarm(context, locationAlarm);
    }

    private static void saveLocationAlarm(Context context, LocationAlarm locationAlarm) {
        SharedPreferences pref = context.getSharedPreferences(Constants.ALARM_PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(locationAlarm.getLocationAlarmID(), locationAlarm.toString());
        editor.apply();
    }

    private static boolean isLocationServiceRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo serviceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (LocationService.class.getName().equals(serviceInfo.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static LocationAlarm parseLocationAlarm(String value) {
        String[] parts = value.split("\\|", -1);
        LocationAlarm alarm = new LocationAlarm();
        Log.d(TAG, "parseLocationAlarm: " + value);
        alarm.setLocationAlarmID(parts[0]);
        alarm.setTurnedOn(Boolean.parseBoolean(parts[1]));
        String[] latLng = parts[2].split(":");
        alarm.setLatLng(new LatLng(Double.parseDouble(latLng[0]), Double.parseDouble(latLng[1])));
        alarm.setExact(Boolean.parseBoolean(parts[3]));
        alarm.setRange(Integer.parseInt(parts[4]));
        alarm.setTitle(parts[5]);
        alarm.setAddress(parts[6]);
        alarm.setNote_title(parts[7]);
        alarm.setNote(parts[8]);
        //alarm.setDateCreated(Long.parseLong(parts[9]));

        return alarm;
    }

    public static void cancelLocationAlarm(Context context, LocationAlarm locationAlarm) {
        locationAlarm.setTurnedOn(false);
        saveLocationAlarm(context, locationAlarm);
        notifyLocationService(context, Constants.ACTION_CANCEL_LOCATION_ALARM, locationAlarm);
    }

    public static void deleteLocationAlarm(Context context, LocationAlarm locationAlarm) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.ALARM_PREFERENCES_FILE, Context.MODE_PRIVATE).edit();
        editor.remove(locationAlarm.getLocationAlarmID());
        editor.apply();
    }

    private static void notifyLocationService(Context context, String action, LocationAlarm alarm) {
        Intent intent = new Intent(context, LocationService.class);
        intent.putExtra(alarm.getLocationAlarmID(), alarm.toString());
        intent.setAction(action);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static List<LocationAlarm> getAllLocationAlarms(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.ALARM_PREFERENCES_FILE, Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();
        List<LocationAlarm> alarms = new ArrayList<>();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().startsWith(Constants.LOCATION_ALARM_KEY)){
                String value = entry.getValue().toString();
                LocationAlarm alarm = parseLocationAlarm(value);
                alarms.add(alarm);
                Log.d(TAG, "Saved alarm: " + alarm);
            }
        }
        return alarms;
    }

    public static int getActiveAlarmCount(Context context) {
        List<LocationAlarm> alarms = getAllLocationAlarms(context);
        int count = 0;
        if (alarms != null) {
            for (LocationAlarm alarm : alarms) {
                if (alarm.isTurnedOn())
                    count++;
            }
        }
        return count;
    }

    public static void startLocationService(Context context) {
        Intent intent = new Intent(context, LocationService.class);
        intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static void createFGSNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    Constants.CHANNEL_ID_LOCATION_FGS,
                    "Location Alarm Service Channel",
                    NotificationManager.IMPORTANCE_MIN
            );
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
    public static void createLocationNotificationChannel(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    Constants.CHANNEL_ID_LOCATION_ALARM,
                    "LocationAlarm Notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
