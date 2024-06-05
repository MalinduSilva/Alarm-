package com.malindu.alarm15.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.malindu.alarm15.R;

public class LocationFragment extends Fragment {
    private FloatingActionButton fab_loc;

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
//        TextClock clock = view.findViewById(R.id.clock);
//        clock.setFormat12Hour("hh:mm:ss a");
        fab_loc = view.findViewById(R.id.fab_add_location_alarm);
        fab_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationAddNewDialog dialog = new LocationAddNewDialog();
                dialog.setTargetFragment(LocationFragment.this, 1);
                dialog.show(getFragmentManager(), LocationAddNewDialog.TAG);
            }
        });
        return view;
    }
}