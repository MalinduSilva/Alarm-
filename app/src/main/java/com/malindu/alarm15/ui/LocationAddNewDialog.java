package com.malindu.alarm15.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.malindu.alarm15.R;
import com.malindu.alarm15.utils.Constants;
import com.malindu.alarm15.utils.PermissionUtils;

public class LocationAddNewDialog extends DialogFragment implements OnMapReadyCallback {
    public static final String TAG = "LocationAddNewDialog";
    private GoogleMap mMap;
    private SharedPreferences sharedPreferences;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_add_location_alarm, container, false);

        sharedPreferences = view.getContext().getSharedPreferences(Constants.ALARM_PREFERENCES_FILE, Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean(Constants.ALARM_PREFERENCES_KEY_FIRST_LAUNCH_LOCATION, true)) {
            firstLaunchTourLocation();
        }
        if (PermissionUtils.hasPermissions(getContext(), Constants.REQUIRED_PERMISSIONS_LOCATION)) {
            Log.d(TAG, "firstLaunchTourLocation: Permissions granted already");
        } else {
            PermissionUtils.requestPermissions(getActivity(), Constants.REQUIRED_PERMISSIONS_LOCATION);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void firstLaunchTourLocation() {
        // TODO implement guide

        if (PermissionUtils.hasPermissions(getContext(), Constants.REQUIRED_PERMISSIONS_LOCATION)) {
            //Toast.makeText(getContext(), "Permissions ok", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "firstLaunchTourLocation: Permissions granted already");
        } else {
            PermissionUtils.requestPermissions(getActivity(), Constants.REQUIRED_PERMISSIONS_LOCATION);
        }
        sharedPreferences.edit().putBoolean(Constants.ALARM_PREFERENCES_KEY_FIRST_LAUNCH_LOCATION, false).apply();
    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (PermissionUtils.handlePermissionsResult(requestCode, permissions, grantResults)) {
//            // Permissions are granted
//            Log.d(TAG, "onRequestPermissionsResult: permissions granted");
//        } else {
//            // Permissions are denied
//            Log.d(TAG, "onRequestPermissionsResult: permissions denied");
//            Toast.makeText(getContext(), "Permissions denied", Toast.LENGTH_SHORT).show();
//        }
//    }
}
