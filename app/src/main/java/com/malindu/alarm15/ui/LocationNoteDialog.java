package com.malindu.alarm15.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.malindu.alarm15.R;
import com.malindu.alarm15.models.LocationAlarm;

public class LocationNoteDialog extends DialogFragment {
    public static final String TAG = "LocationNoteDialog";
    private ImageButton btnSave, btnDiscard, btnExpand;
    private EditText title, note;

    public interface OnNoteAddedListener { void onNoteAdded(String title, String note); }
    private OnNoteAddedListener listener;
    public void setOnNoteAddedListener(OnNoteAddedListener listener) { this.listener = listener; }

    public static LocationNoteDialog newInstance(String title, String desc) {
        LocationNoteDialog dialog = new LocationNoteDialog();
        Bundle args = new Bundle();
        args.putString("location_note_title", title);
        args.putString("location_note", desc);
        dialog.setArguments(args);
        Log.d(TAG, "newInstance: " + args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_location_note, container, false);
        btnSave = view.findViewById(R.id.btn_note_save);
        btnDiscard = view.findViewById(R.id.btn_note_discard);
        btnExpand = view.findViewById(R.id.btn_show_desc);
        title = view.findViewById(R.id.note_title);
        note = view.findViewById(R.id.note_desc);

        if (getArguments() != null && getArguments().containsKey("location_note")) {
            title.setText(getArguments().getString("location_note_title"));
            note.setText(getArguments().getString("location_note"));
        }

        btnDiscard.setOnClickListener(v -> getDialog().dismiss());
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String note_title = title.getText().toString();
                String note_desc = note.getText().toString();
                if (listener != null) {
                    listener.onNoteAdded(note_title, note_desc);
                }
                getDialog().dismiss();
            }
        });
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Translucent_NoTitleBar);
    }
}
