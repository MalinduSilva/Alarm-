package com.malindu.alarm15.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.malindu.alarm15.R;
import com.malindu.alarm15.adapters.AlarmRecyclerViewAdapter;
import com.malindu.alarm15.models.Alarm;

public class AlarmFragment extends Fragment implements AlarmAddNewClockDialog.OnAlarmAddedListener, AlarmRecyclerViewAdapter.OnAlarmClickListener {

    private static final String TAG = "AlarmFragment";
    private FloatingActionButton fabAddAlarm;
    private RecyclerView alarmListRecyclerView;
    private Button deletePreferences;
    private AlarmRecyclerViewAdapter adapter;
    private static final String ALARM_PREFERENCES_FILE = "ALARM_PREFERENCES_FILE";
    private static final String ALARM_KEY  = "ALARM_";
    private static final String ALARM_COUNT_KEY  = "ALARM_COUNT";

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
                SharedPreferences prefs = v.getContext().getSharedPreferences(ALARM_PREFERENCES_FILE, Context.MODE_PRIVATE);
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
}