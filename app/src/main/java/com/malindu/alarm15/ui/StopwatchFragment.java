package com.malindu.alarm15.ui;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.malindu.alarm15.R;

import java.util.ArrayList;
import java.util.List;

public class StopwatchFragment extends Fragment {
    private TextView tvTimer, tvTimerMilli;
    private FloatingActionButton btnStartStop, btnLap, btnReset;
    private Handler handler = new Handler();
    private long startTime, elapsedTime = 0;
    private List<String> laps;
    private boolean isRunning = false;
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime + elapsedTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            int hours = minutes / 60;
            seconds = seconds % 60;
            int hundredths = (int) (millis / 10 % 100);
            tvTimer.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
            tvTimerMilli.setText(String.format(":%02d", hundredths));
            handler.postDelayed(this, 1);
        }
    };
    private ListView listView;

    public StopwatchFragment() {
        // Required empty public constructor
    }
    public static StopwatchFragment newInstance() { return new StopwatchFragment();}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stopwatch, container, false);
        tvTimer = view.findViewById(R.id.tvTimer);
        tvTimerMilli = view.findViewById(R.id.tvTimerMilli);
        btnStartStop = view.findViewById(R.id.btnStartStop);
        btnLap = view.findViewById(R.id.btnLap);
        btnReset = view.findViewById(R.id.btnReset);
        listView = view.findViewById(R.id.listView);
        laps = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.test_list_item, laps);
        //listView.setDivider();
        listView.setAdapter(adapter);

        btnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRunning) {
                    startTime = System.currentTimeMillis();
                    handler.postDelayed(timerRunnable, 0);
                    isRunning = true;
                    //btnStartStop.setText("Stop");
                    btnStartStop.setImageResource(R.drawable.icon_pause);
                    btnStartStop.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_theme_errorContainer)));
                    btnLap.setVisibility(View.VISIBLE);
                    btnReset.setVisibility(View.GONE);
                } else {
                    elapsedTime += System.currentTimeMillis() - startTime;
                    handler.removeCallbacks(timerRunnable);
                    isRunning = false;
                    //btnStartStop.setText("Start");
                    btnStartStop.setImageResource(R.drawable.icon_play_arrow);
                    btnStartStop.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_theme_primaryFixed)));
                    btnLap.setVisibility(View.GONE);
                    btnReset.setVisibility(View.VISIBLE);
                }
            }
        });

        btnLap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = tvTimer.getText().toString() + tvTimerMilli.getText().toString();
                laps.add(time);
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(timerRunnable);
                elapsedTime = 0;
                tvTimer.setText("00:00:00");
                tvTimerMilli.setText(":00");
                isRunning = false;
                laps.clear();
                btnReset.setVisibility(View.GONE);
            }
        });
        return view;
    }

}