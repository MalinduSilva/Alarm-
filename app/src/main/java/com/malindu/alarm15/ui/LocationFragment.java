package com.malindu.alarm15.ui;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.malindu.alarm15.R;
import com.malindu.alarm15.adapters.LocationRecyclerViewAdapter;
import com.malindu.alarm15.models.LocationAlarm;
import com.malindu.alarm15.utils.Constants;
import com.malindu.alarm15.utils.LocationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LocationFragment extends Fragment implements LocationAddNewDialog.OnLocationAddedListener,
LocationRecyclerViewAdapter.OnLocationClickListener {
    private static final String TAG = "LocationFragment";
    private FloatingActionButton fab_loc;
    private RecyclerView locationListRecyclerView;
    private LocationRecyclerViewAdapter adapter;
    private RelativeLayout layout;

    public LocationFragment() {
        // Required empty public constructor
    }
    public static LocationFragment newInstance() {
        return new LocationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location, container, false);
        LocationUtils.createLocationNotificationChannel(view.getContext());
//        TextClock clock = view.findViewById(R.id.clock);
//        clock.setFormat12Hour("hh:mm:ss a");
        fab_loc = view.findViewById(R.id.fab_add_location_alarm);
        fab_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationAddNewDialog dialog = new LocationAddNewDialog();
                dialog.setTargetFragment(LocationFragment.this, 1);
                dialog.setOnLocationAddedListener(LocationFragment.this);
                dialog.show(requireFragmentManager(), LocationAddNewDialog.TAG);
            }
        });

        adapter = new LocationRecyclerViewAdapter(requireContext());
        adapter.setOnLocationClickListener(this);
        locationListRecyclerView = view.findViewById(R.id.location_list_recview);
        locationListRecyclerView.setAdapter(adapter);
        locationListRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        layout = view.findViewById(R.id.layout_location_fragment);
        layout.setOnClickListener(v -> adapter.notifyDataSetChanged());
        locationListRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int paddingBottom = locationListRecyclerView.getPaddingBottom();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (y > locationListRecyclerView.getHeight() - paddingBottom) {
                        adapter.notifyDataSetChanged();
                        return true;
                    }
                }
                return false;
            }
        });

        view.findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = requireContext().getSharedPreferences(Constants.ALARM_PREFERENCES_FILE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Map<String, ?> allEntries = sharedPreferences.getAll();
                for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
//                    if (entry.getKey().startsWith(Constants.LOCATION_ALARM_KEY)){
                        editor.remove(entry.getKey());
//                    }
                }
                editor.apply();
            }
        });

        reqPermissions();
        return view;
    }

    private void reqPermissions() {
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION); }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) { permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION); }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) { permissionList.add(Manifest.permission.INTERNET); }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) { permissionList.add(Manifest.permission.ACCESS_NETWORK_STATE); }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) { permissionList.add(Manifest.permission.FOREGROUND_SERVICE); }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.FOREGROUND_SERVICE_LOCATION) != PackageManager.PERMISSION_GRANTED) { permissionList.add(Manifest.permission.FOREGROUND_SERVICE_LOCATION); }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) { permissionList.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION); }
        if (!permissionList.isEmpty()) {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.dialog_permissions_title_first)
                    .setMessage(R.string.dialog_permissions_message_location)
                    .setPositiveButton(R.string.dialog_permissions_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(permissionList.toArray(new String[0]), Constants.PERMISSION_REQUEST_CODE_LOCATION);
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    @Override
    public void onLocationAdded() {
        Log.d(TAG, "onLocationAdded: ");
        adapter.updateData();
    }

    @Override
    public void onLocationClick(LocationAlarm locationAlarm) {
        LocationAddNewDialog dialog = LocationAddNewDialog.newInstance(locationAlarm);
        dialog.setOnLocationAddedListener(this);
        dialog.show(getParentFragmentManager(), LocationAddNewDialog.TAG);
    }
}