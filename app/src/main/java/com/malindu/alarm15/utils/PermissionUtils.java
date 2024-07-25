package com.malindu.alarm15.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.malindu.alarm15.R;
import com.malindu.alarm15.ui.AlarmFragment;
import com.malindu.alarm15.ui.LocationFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PermissionUtils {
    private static final String TAG = "PermissionUtils";

    // Method to check if all required permissions are granted
    public static boolean hasPermissions(Context context, String... permissions) {
        if (permissions != null) { //context != null &&
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    // Method to request permissions
    public static void requestPermissions(Activity activity, String... permissions) {
        if (permissions != null) { //Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity != null &&
            ActivityCompat.requestPermissions(activity, permissions, Constants.PERMISSION_REQUEST_CODE);
        }
    }

//    // Method to handle permission result
//    public static boolean handlePermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == Constants.PERMISSION_REQUEST_CODE) {
//            for (int result : grantResults) {
//                if (result != PackageManager.PERMISSION_GRANTED) {
//                    return false;
//                }
//            }
//            return true;
//        }
//        return false;
//    }

    public static boolean checkPermissionsForAlarm(Context context) {
        if (ContextCompat.checkSelfPermission(context, "android.permission.POST_NOTIFICATIONS") != PackageManager.PERMISSION_GRANTED
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return false;
        } else if (ContextCompat.checkSelfPermission(context, "android.permission.USE_FULL_SCREEN_INTENT") != PackageManager.PERMISSION_GRANTED
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return false;
        } else {
            return true;
        }
    }

    public static void requestPermissionsForAlarm(AlarmFragment fragment) {
        List<String> permissionsToRequest = new ArrayList<>();

        // API level 33 (Android 13) needs permissions to post notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(fragment.requireContext(), "android.permission.POST_NOTIFICATIONS") != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add("android.permission.POST_NOTIFICATIONS");
            }
        }

        // API level 31 (Android 12) needs permissions to use full-screen intents, which is used for alarm ringing screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {  // API level 31 (Android 12)
            if (ContextCompat.checkSelfPermission(fragment.requireContext(), "android.permission.USE_FULL_SCREEN_INTENT") != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add("android.permission.USE_FULL_SCREEN_INTENT");
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            fragment.requestPermissions(permissionsToRequest.toArray(new String[0]), Constants.PERMISSION_REQUEST_CODE);
        }
    }

    public static void requestLocationPermissions(Context context) {
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION); }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) { permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION); }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) { permissionList.add(Manifest.permission.INTERNET); }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) { permissionList.add(Manifest.permission.ACCESS_NETWORK_STATE); }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && ContextCompat.checkSelfPermission(context, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) { permissionList.add(Manifest.permission.FOREGROUND_SERVICE); }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && ContextCompat.checkSelfPermission(context, Manifest.permission.FOREGROUND_SERVICE_LOCATION) != PackageManager.PERMISSION_GRANTED) { permissionList.add(Manifest.permission.FOREGROUND_SERVICE_LOCATION); }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) { permissionList.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION); }
        if (!permissionList.isEmpty()) {
            new MaterialAlertDialogBuilder(context)
                    .setTitle(R.string.dialog_permissions_title_first)
                    .setMessage(R.string.dialog_permissions_message_location)
                    .setPositiveButton(R.string.dialog_permissions_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LocationFragment fragment = LocationFragment.newInstance();
                            fragment.requestPermissions(permissionList.toArray(new String[0]), Constants.PERMISSION_REQUEST_CODE_LOCATION);
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    public static boolean checkLocationPermissions(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { return false; }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) { return false; }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) { return false; }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) { return false; }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && ContextCompat.checkSelfPermission(context, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) { return false; }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && ContextCompat.checkSelfPermission(context, Manifest.permission.FOREGROUND_SERVICE_LOCATION) != PackageManager.PERMISSION_GRANTED) { return false; }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) { return false; }
        return true;
    }
}
