package com.malindu.alarm15.utils;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.malindu.alarm15.MainActivity;
import com.malindu.alarm15.R;
import com.malindu.alarm15.models.Alarm;
import com.malindu.alarm15.models.LocationAlarm;
import com.malindu.alarm15.ui.AlarmRingFullscreenActivity;

import java.util.List;

public class LocationService extends Service {
    private static final String TAG = "LocationService";
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private List<LocationAlarm> locationAlarms;
    private Location userLocation;
    private int activeCount;
    private Context context;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //locationAlarms = loadLocationAlarms(getApplicationContext());
        LocationUtils.createFGSNotificationChannel(getApplicationContext());
        getLocation();
        startForeground(1, getFGSNotification("Tracking location..."));
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        context = getApplicationContext();
        locationAlarms = LocationUtils.getAllLocationAlarms(context);
//        LocationAlarm locationAlarm;
//        String action = intent.getAction();
//        if (action != null) {
//            switch (action) {
//                case Constants.ACTION_NEW_LOCATION_ALARM:
//                    locationAlarm = LocationUtils.parseLocationAlarm(intent.getStringExtra("LocationAlarmStr"));
//                    startAlarmSound(context, locationAlarm);
//                    break;
//                case Constants.ACTION_LOCATION_ALARM_DISMISS:
//                    stopAlarmSound();
//                    break;
//                case Constants.ACTION_LOCATION_ALARM_DISMISS_AND_OFF:
//                    locationAlarm = LocationUtils.parseLocationAlarm(intent.getStringExtra("LocationAlarmStr"));
//                    stopAlarmSound();
//                    locationAlarm.setTurnedOn(false);
//                    LocationUtils.saveLocationAlarm(context, locationAlarm);
//                    break;
//            }
//        }
        return START_NOT_STICKY;
    }

    private void getLocation() {
        LocationRequest locationRequest = new LocationRequest
                .Builder(5000)
                .setMinUpdateIntervalMillis(2000)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLocation: no permissions");
            Toast.makeText(this, "Location permissions are not granted", Toast.LENGTH_LONG).show();
            return;
        }
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                //super.onLocationResult(locationResult);
                userLocation = locationResult.getLastLocation();
                if (userLocation != null) {
                    Log.d(TAG, "Count: " + activeCount + " -- " + "UserLocation - " + userLocation.getLatitude()+":"+userLocation.getLongitude());
                    checkLocationAlarms(userLocation);
                }
            }
        };
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
//        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
//            @Override
//            public void onLocationResult(@NonNull LocationResult locationResult) {
//                //super.onLocationResult(locationResult);
//                userLocation = locationResult.getLastLocation();
//                if (userLocation != null) {
//                    Log.d(TAG, "Count: " + " -- " + "UserLocation - " + userLocation.getLatitude()+":"+userLocation.getLongitude());
//                    checkLocationAlarms(userLocation);
//                }
//
//            }
//        }, Looper.myLooper());
    }

    private void checkLocationAlarms(Location location) {
        activeCount = 0;
        for (LocationAlarm alarm : locationAlarms) {
            if (alarm.isTurnedOn() && isWithinRange(location, alarm)) {
                showNotification(alarm,"You are within range of: " + alarm.getTitle());
                // Optionally: Turn off the alarm if it should only trigger once
                //TODO allow user to set trigger once or trigger everytime
                alarm.setTurnedOn(false); // has to write in shared preferences
                if (LocationUtils.getActiveAlarmCount(getApplicationContext()) == 0) {
                    Log.d(TAG, "Service stopped");
                    stopSelf();
                }
            }
            if (alarm.isTurnedOn()) { activeCount++; }
        }
        if (activeCount == 0) { stopSelf(); }
    }

    private boolean isWithinRange(Location location, LocationAlarm alarm) {
        float[] results = new float[1];
        Location.distanceBetween(location.getLatitude(), location.getLongitude(), alarm.getLatLng().latitude, alarm.getLatLng().longitude, results);
        return results[0] <= alarm.getRange();
    }

    private Notification getFGSNotification(String text) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, Constants.CHANNEL_ID_LOCATION_FGS)
                .setContentTitle("Location Alarm Service")
                .setContentText(text)
                .setSmallIcon(R.drawable.icon_pin_drop)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
    }

    private void showNotification(LocationAlarm locationAlarm, String message) {
        // Intent for full-screen ringing activity
        Intent fullscreenIntent = new Intent(context, AlarmRingFullscreenActivity.class);
        fullscreenIntent.putExtra("LocationAlarmStr", locationAlarm.toString());
        fullscreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent fullscreenPendingIntent = PendingIntent.getActivity(context, 0, fullscreenIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Intent for dismiss action
        Intent dismissIntent = new Intent(context, LocationService.class);
        dismissIntent.putExtra("LocationAlarmStr", locationAlarm.toString());
        dismissIntent.setAction(Constants.ACTION_LOCATION_ALARM_DISMISS_AND_OFF);
        PendingIntent dismissPendingIntent = PendingIntent.getService(context, 1, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Intent for dismiss & turn off action
        Intent dismissOffIntent = new Intent(context, LocationService.class);
        dismissOffIntent.putExtra("LocationAlarmStr", locationAlarm.toString());
        dismissOffIntent.setAction(Constants.ACTION_LOCATION_ALARM_DISMISS_AND_OFF);
        PendingIntent dismissOffPendingIntent = PendingIntent.getService(context, 2, dismissOffIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Constants.CHANNEL_ID_LOCATION_ALARM)
                .setSmallIcon(R.drawable.icon_pin_drop)
                .setContentTitle("Location Alarm")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_MAX);
//                .addAction(R.drawable.icon_alarm_off, getString(R.string.dismiss), dismissPendingIntent)
//                .addAction(R.drawable.icon_off_200, getString(R.string.turn_off), dismissOffPendingIntent);
//        if (locationAlarm.getAlertType().equals(LocationAlarm.ALERT_TYPE_ALARM)) {
//            builder.setOngoing(true)
//                    .setCategory(NotificationCompat.CATEGORY_ALARM)
//                    .setFullScreenIntent(fullscreenPendingIntent, true);
//        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void startAlarmSound(Context context, LocationAlarm locationAlarm) {
        if (locationAlarm.getAlertType().equals(LocationAlarm.ALERT_TYPE_ALARM)) {
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
                //mediaPlayer.setVolume(alarm.getAlarmVolume(), alarm.getAlarmVolume());
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

    @Override
    public void onDestroy() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
        super.onDestroy();
    }
}