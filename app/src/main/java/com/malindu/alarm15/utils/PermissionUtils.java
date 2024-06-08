package com.malindu.alarm15.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtils {

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
}
