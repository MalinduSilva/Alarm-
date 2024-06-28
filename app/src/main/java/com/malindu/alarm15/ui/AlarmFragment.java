package com.malindu.alarm15.ui;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.malindu.alarm15.R;
import com.malindu.alarm15.adapters.AlarmRecyclerViewAdapter;
import com.malindu.alarm15.models.Alarm;
import com.malindu.alarm15.utils.AlarmUtils;
import com.malindu.alarm15.utils.Constants;
import com.malindu.alarm15.utils.PermissionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AlarmFragment extends Fragment implements AlarmAddNewClockDialog.OnAlarmAddedListener,
        AlarmRecyclerViewAdapter.OnAlarmClickListener{

    private static final String TAG = "AlarmFragment";
    private FloatingActionButton fabAddAlarm;
    private RecyclerView alarmListRecyclerView;
    private Button deletePreferences;
    private AlarmRecyclerViewAdapter adapter;
    private int permissionRequestTimes = 0;
    private boolean isPermissionsGranted = true;

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

        if (!PermissionUtils.checkPermissionsForAlarm(getContext())) {
            isPermissionsGranted = false;
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.dialog_permissions_title_first)
                    .setMessage(R.string.dialog_permissions_message)
                    .setPositiveButton(R.string.dialog_permissions_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PermissionUtils.requestPermissionsForAlarm(AlarmFragment.this);
                        }
                    })
                    .setNegativeButton(R.string.dialog_permissions_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(view.getContext(), "Keep your permissions. BYE!", Toast.LENGTH_LONG).show();
                            requireActivity().finish();
                        }
                    })
                    //.setOnDismissListener(dialog -> PermissionUtils.requestPermissionsForAlarm(AlarmFragment.this))
                    .setCancelable(false)
                    .show();
        }


        alarmListRecyclerView = view.findViewById(R.id.alarm_list_recview);
        fabAddAlarm = view.findViewById(R.id.fab_add);
        fabAddAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPermissionsGranted) {
                    AlarmAddNewClockDialog dialog = new AlarmAddNewClockDialog();
                    dialog.setTargetFragment(AlarmFragment.this, 1);
                    dialog.setOnAlarmAddedListener(AlarmFragment.this);
                    dialog.show(requireFragmentManager(), AlarmAddNewClockDialog.TAG);
                } else {
                    Toast.makeText(requireContext(), "Required permissions are not granted!", Toast.LENGTH_LONG).show();
                }
            }
        });

        adapter = new AlarmRecyclerViewAdapter(requireContext());
        adapter.setOnAlarmClickListener(this);
        alarmListRecyclerView.setAdapter(adapter);
        alarmListRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()) {
            @Override
            public void onLayoutCompleted(RecyclerView.State state) {
                super.onLayoutCompleted(state);
                int itemCount = getItemCount();
                if (itemCount > 0) {
                    View lastItem = findViewByPosition(itemCount - 1);
                    if (lastItem != null) {
                        lastItem.setPadding(0, 0, 0, fabAddAlarm.getHeight());
                    }
                }
            }
        });

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: " + requestCode + " " + Arrays.toString(grantResults));
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                isPermissionsGranted = false;
                return;
            }
        }
        isPermissionsGranted = true;
    }
    @Override
    public void onAlarmDeleted() {
        adapter.updateData();
    }
}