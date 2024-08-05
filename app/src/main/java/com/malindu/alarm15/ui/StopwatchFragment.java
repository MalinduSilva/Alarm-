package com.malindu.alarm15.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.malindu.alarm15.R;

public class StopwatchFragment extends Fragment {

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
        return view;
    }

}