package com.malindu.alarm15.utils;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
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

public class MyService extends Service {
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private static final String TAG = "MyService";

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createNotificationChannel();
        startForeground(1, getNotification("Tracking location..."));
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        getLocation();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
////                while (true) {
////                    Log.d(TAG, "Service running...");
////                    try {
////                        Thread.sleep(1000);
////                    } catch (InterruptedException e) {
////                        Log.d(TAG, e.getMessage());
////                    }
////                }
////
////            }
////        }).start();
//        locationRequest.setInterval(5000);
//        locationRequest.setFastestInterval(1000);
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(@NonNull LocationResult locationResult) {
//                for (Location location : locationResult.getLocations()) {
//                    Log.d(TAG, "onLocationResult: " + location.getLatitude() + ":" + location.getLongitude());
//                    Toast.makeText(MyService.this, location.getLatitude()+":"+location.getLongitude(), Toast.LENGTH_SHORT).show();
//                }
//                super.onLocationResult(locationResult);
//            }
//        };
        return START_NOT_STICKY;
    }

    private void getLocation() {
        LocationRequest locationRequest = new LocationRequest
                .Builder(5000)
                .setMinUpdateIntervalMillis(1000)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build();
        Log.d(TAG, "getLocation: location request created");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLocation: no permissions");
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Log.d(TAG, "onLocationResult");
                //super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    Toast.makeText(MyService.this, location.getLatitude()+":"+location.getLongitude(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, location.getLatitude()+":"+location.getLongitude());
                } else {
                    Toast.makeText(MyService.this, "Unable to get location", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onLocationResult: null");
                }
            }
        }, Looper.myLooper());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    "LocationAlarmServiceChannel",
                    "Location Alarm Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private Notification getNotification(String text) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, "LocationAlarmServiceChannel")
                .setContentTitle("Location Alarm Service")
                .setContentText(text)
                .setSmallIcon(R.drawable.icon_pin_drop)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
    }
    @Override
    public void onDestroy() {
        // Stop location updates and remove location callback
        fusedLocationClient.removeLocationUpdates(locationCallback);
        super.onDestroy();
    }
}