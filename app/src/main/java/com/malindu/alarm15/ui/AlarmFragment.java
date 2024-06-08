package com.malindu.alarm15.ui;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.malindu.alarm15.R;
import com.malindu.alarm15.adapters.AlarmRecyclerViewAdapter;
import com.malindu.alarm15.models.Alarm;
import com.malindu.alarm15.utils.Constants;
import com.malindu.alarm15.utils.PermissionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AlarmFragment extends Fragment implements AlarmAddNewClockDialog.OnAlarmAddedListener, AlarmRecyclerViewAdapter.OnAlarmClickListener {

    private static final String TAG = "AlarmFragment";
    private FloatingActionButton fabAddAlarm;
    private RecyclerView alarmListRecyclerView;
    private Button deletePreferences;
    private AlarmRecyclerViewAdapter adapter;

    public AlarmFragment() {
        // Required empty public constructor
    }
    public static AlarmFragment newInstance() { return new AlarmFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);
        requestPermissions();

        alarmListRecyclerView = view.findViewById(R.id.alarm_list_recview);
        fabAddAlarm = view.findViewById(R.id.fab_add);
        fabAddAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmAddNewClockDialog dialog = new AlarmAddNewClockDialog();
                dialog.setTargetFragment(AlarmFragment.this, 1);
                dialog.setOnAlarmAddedListener(AlarmFragment.this);
                dialog.show(requireFragmentManager(), AlarmAddNewClockDialog.TAG);
            }
        });

        adapter = new AlarmRecyclerViewAdapter(requireContext());
        adapter.setOnAlarmClickListener(this);
        alarmListRecyclerView.setAdapter(adapter);
        alarmListRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        deletePreferences = view.findViewById(R.id.btn_test_delete_sharedpreferences);
        deletePreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = v.getContext().getSharedPreferences(Constants.ALARM_PREFERENCES_FILE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.apply();
            }
        });
        return view;
    }

    @Override
    public void onAlarmAdded() {
        adapter.updateData();
    }

    @Override
    public void onAlarmClick(Alarm alarm) {
        AlarmAddNewClockDialog dialog = AlarmAddNewClockDialog.newInstance(alarm);
        dialog.setOnAlarmAddedListener(this);
        dialog.show(getParentFragmentManager(), AlarmAddNewClockDialog.TAG);
    }

    private void requestPermissions() {
        List<String> permissionsToRequest = new ArrayList<>();

        // API level 33 (Android 13) needs permissions to post notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), "android.permission.POST_NOTIFICATIONS") != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add("android.permission.POST_NOTIFICATIONS");
            }
        }

        // API level 31 (Android 12) needs permissions to use full-screen intents, which is used for alarm ringing screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {  // API level 31 (Android 12)
            if (ContextCompat.checkSelfPermission(requireContext(), "android.permission.USE_FULL_SCREEN_INTENT") != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add("android.permission.USE_FULL_SCREEN_INTENT");
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            PermissionUtils.requestPermissions(requireActivity(), permissionsToRequest.toArray(new String[0]));
            //ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), 1);
        }
    }
}