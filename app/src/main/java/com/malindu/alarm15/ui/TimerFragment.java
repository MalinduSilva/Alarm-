package com.malindu.alarm15.ui;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.malindu.alarm15.R;

import java.util.ArrayList;
import java.util.List;

public class TimerFragment extends Fragment {
    private static final String TAG = "TimerFragment";
    private List<Integer> digitList;
    private NumberPicker pickerHours, pickerMinutes, pickerSeconds;
    private NumberPicker.Formatter formatter;
    private FloatingActionButton btnStartStop, btnReset;
    private int timeInSeconds, hours, minutes, seconds;
    private boolean isRunning = false;
    private CountDownTimer timer;

    public TimerFragment() {
        // Required empty public constructor
    }

    public static TimerFragment newInstance() {return new TimerFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timer, container, false);
        digitList = new ArrayList<>();
        for (int i = 0; i <= 99; i++) { digitList.add(i); }


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnStartStop = view.findViewById(R.id.btnStartStop);
        btnReset = view.findViewById(R.id.btnReset);
        pickerHours = view.findViewById(R.id.pickerHour);
        pickerMinutes = view.findViewById(R.id.pickerMinute);
        pickerSeconds = view.findViewById(R.id.pickerSeconds);
        formatter = value -> value < 10 ? "0" + value : String.valueOf(value);
        String[] digits99 = new String[100];
        String[] digits59 = new String[60];
        for (int i = 0; i < 100; i++) { digits99[i] = String.valueOf(i); }
        for (int i = 0; i < 60; i++) { digits59[i] = String.valueOf(i); }

        pickerHours.setMaxValue(99);
        pickerHours.setMinValue(0);
        pickerHours.setDisplayedValues(digits99);
        pickerHours.setWrapSelectorWheel(true); // infinite scrolling
        pickerHours.setFormatter(formatter);

        pickerMinutes.setMaxValue(59);
        pickerMinutes.setMinValue(0);
        pickerMinutes.setWrapSelectorWheel(true);
        pickerMinutes.setFormatter(formatter);

        pickerSeconds.setMaxValue(59);
        pickerSeconds.setMinValue(0);
        pickerSeconds.setWrapSelectorWheel(true);
        pickerSeconds.setFormatter(formatter);

        setDividerHeight(1);

        btnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRunning) {
                    long t = System.currentTimeMillis();
                    hours = pickerHours.getValue();
                    minutes = pickerMinutes.getValue();
                    seconds = pickerSeconds.getValue();
                    timeInSeconds = ((hours * 60) + minutes)  * 60 + seconds;
                    pickerHours.setMinValue(hours); pickerHours.setMaxValue(hours); pickerHours.setDisplayedValues(new String[]{String.valueOf(hours)});
                    pickerMinutes.setMinValue(minutes); pickerMinutes.setMaxValue(minutes); pickerMinutes.setDisplayedValues(new String[]{String.valueOf(minutes)});
                    pickerSeconds.setMinValue(seconds); pickerSeconds.setMaxValue(seconds); pickerSeconds.setDisplayedValues(new String[]{String.valueOf(seconds)});
                    setDividerHeight(0);
                    timer = new CountDownTimer(timeInSeconds * 1000L + 99, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            long secondsRemaining = millisUntilFinished / 1000;
                            int h = (int) (secondsRemaining / 3600);
                            int m = (int) ((secondsRemaining % 3600) / 60);
                            int s = (int) (secondsRemaining % 60);
                            //s = Math.min(s, 59);
                            Log.d(TAG, "onTick: " + h +":"+ m +":"+ s + "..." + ((System.currentTimeMillis() - t) % 1000));
                            pickerHours.setDisplayedValues(new String[]{String.valueOf(h)});
                            pickerMinutes.setDisplayedValues(new String[]{String.valueOf(m)});
                            pickerSeconds.setDisplayedValues(new String[]{String.valueOf(s)});
                        }

                        @Override
                        public void onFinish() {
                            Log.d(TAG, "onFinish: ");
                            pickerSeconds.setDisplayedValues(new String[]{String.valueOf(0)});
                            isRunning = false;
                            btnStartStop.setImageResource(R.drawable.icon_play_arrow);
                            btnStartStop.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_theme_primaryFixed)));
                            btnReset.setVisibility(View.VISIBLE);
                            Toast.makeText(requireContext(), "Time is up", Toast.LENGTH_SHORT).show();
                        }
                    }.start();

                    isRunning = true;
                    btnStartStop.setImageResource(R.drawable.icon_pause);
                    btnStartStop.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_theme_errorContainer)));
                    btnReset.setVisibility(View.GONE);
                    Log.d(TAG, "onClick: time: " + timeInSeconds);
                } else {
                    pickerHours.setDisplayedValues(digits99); pickerHours.setMinValue(0); pickerHours.setMaxValue(99);
                    pickerMinutes.setDisplayedValues(digits59); pickerMinutes.setMinValue(0); pickerMinutes.setMaxValue(59);
                    pickerSeconds.setDisplayedValues(digits59); pickerSeconds.setMinValue(0); pickerSeconds.setMaxValue(59);
                    setDividerHeight(1);
                    if (timer != null) {
                        timer.cancel();
                    }

                    isRunning = false;
                    btnStartStop.setImageResource(R.drawable.icon_play_arrow);
                    btnStartStop.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_theme_primaryFixed)));
                    btnReset.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setDividerHeight(int size) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            pickerHours.setSelectionDividerHeight(size);
            pickerMinutes.setSelectionDividerHeight(size);
            pickerSeconds.setSelectionDividerHeight(size);
            if (!isRunning) {
                pickerHours.setTextSize(100); pickerMinutes.setTextSize(100); pickerSeconds.setTextSize(100 );
            }
        }
    }

//    private void setupRecyclerView(View view, int recyclerViewId) {
//        RecyclerView recyclerView = view.findViewById(recyclerViewId);
//        TimerDigitAdapter adapter = new TimerDigitAdapter(digitList, this);
//
//        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setAdapter(adapter);
//
//        SnapHelper snapHelper = new LinearSnapHelper();
//        snapHelper.attachToRecyclerView(recyclerView);
//
//        RecyclerView.ItemDecoration decoration = new RecyclerView.ItemDecoration() {
//            @Override
//            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
//                super.getItemOffsets(outRect, view, parent, state);
//                int spaceInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
//                outRect.left = spaceInDp;
//                outRect.right = spaceInDp;
//                outRect.top = spaceInDp;
//                outRect.bottom = spaceInDp;
//                // If you want to avoid double spacing between items (not needed on first item), adjust accordingly:
//                if (parent.getChildAdapterPosition(view) == 0) {
//                    outRect.top = spaceInDp;
//                } else {
//                    outRect.top = 0;
//                }
//            }
//        };
//        recyclerView.addItemDecoration(decoration);
//        recyclerView.scrollToPosition(startPositionHour);
//        focusedHour = startPositionHour;
//
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    View centerView = snapHelper.findSnapView(layoutManager);
//                    if (centerView != null) {
//                        int position = layoutManager.getPosition(centerView);
//                        int focusedDigit = digitList.get(position % digitList.size());
//                        Log.d("Focused Digit", "Focused digit is: " + focusedDigit);
//                        focusedHour = focusedDigit;
//                    }
//                }
//            }
//        });
////        recyclerView.getChildAt(focusedHour+1).setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                recyclerView.scrollToPosition(focusedHour+1);
////            }
////        });
////        recyclerView.getChildAt(focusedHour-1).setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                recyclerView.scrollToPosition(focusedHour-1);
////            }
////        });
//    }
//
//    @Override
//    public void onDigitClick(int digit) {
//        Log.d(TAG, "onDigitClick: " + digit);
//        recHours.scrollToPosition(startPositionHour + digit + 2);
//    }
}