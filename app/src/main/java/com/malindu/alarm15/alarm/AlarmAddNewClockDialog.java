package com.malindu.alarm15.alarm;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.malindu.alarm15.R;

import java.util.Calendar;
import java.util.List;

public class AlarmAddNewClockDialog extends DialogFragment {
    public static final String TAG = "AlarmAddNewClockDialog";
    private Button btnSave, btnDiscard, btnCalendar;
    private TimePicker timePicker;
    private ChipGroup chipGroupWeek;
    private EditText alarmLabel;
    private MaterialSwitch switch_sound, switch_vibration, switch_snooze;
    private Alarm newAlarm = new Alarm();
    private boolean set_for_date = false; private boolean set_for_weekdays = false;
    public interface OnAlarmAddedListener { void onAlarmAdded(); }
    private OnAlarmAddedListener listener;

    public void setOnAlarmAddedListener(OnAlarmAddedListener listener) {
        this.listener = listener;
    }

    public static AlarmAddNewClockDialog newInstance(Alarm alarm) {
        AlarmAddNewClockDialog dialog = new AlarmAddNewClockDialog();
        Bundle args = new Bundle();
        args.putSerializable("alarm", alarm);
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_add_clock_alarm, container, false);
        btnDiscard = view.findViewById(R.id.btn_discard);
        btnSave = view.findViewById(R.id.btn_save);
        chipGroupWeek = view.findViewById(R.id.chipGroup_week);
        timePicker = view.findViewById(R.id.time_picker);
        btnCalendar = view.findViewById(R.id.btn_calendar);
        alarmLabel = view.findViewById(R.id.txt_alarm_label);
        switch_sound = view.findViewById(R.id.switch_sound);
        switch_vibration = view.findViewById(R.id.switch_vibration);
        switch_snooze = view.findViewById(R.id.switch_snooze);

        // Check if an existing Alarm object is provided
        if (getArguments() != null && getArguments().containsKey("alarm")) {
            newAlarm = (Alarm) getArguments().getSerializable("alarm");
            populateFieldsWithExistingData();
        } else {
            newAlarm = new Alarm();
        }

        btnDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
                newAlarm.setAlarmLabel(alarmLabel.getText().toString());
                newAlarm.setSound(switch_sound.isChecked());
                newAlarm.setVibration(switch_vibration.isChecked());
                newAlarm.setSnooze(switch_snooze.isChecked());
                newAlarm.setTurnedOn(true);
                AlarmUtils.setAlarm(requireContext(), newAlarm);
                if (listener != null) {
                    listener.onAlarmAdded();
                }
            }
        });
        chipGroupWeek.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup chipGroup, @NonNull List<Integer> list) {
                for (int i = 0; i < 7; i++) {
                    Chip chip = (Chip) chipGroup.getChildAt(i);
                    if (chip.isChecked()) {
                        newAlarm.setWeekdays(i,true);
                        chip.setChipStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.md_theme_primary)));
                    } else {
                        newAlarm.setWeekdays(i,false);
                        chip.setChipStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.ash_border)));
                    }
                }
                set_for_date = false; set_for_weekdays = true;
                Calendar c = Calendar.getInstance();
                newAlarm.setDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            }
        });
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                newAlarm.setTime(hourOfDay, minute);
            }
        });
        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker<Long> datePicker = MaterialDatePicker
                        .Builder.datePicker()
                        .setTitleText("Select date")
                        .setSelection(Calendar.getInstance().getTimeInMillis())
                        .build();
                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long aLong) {
                        for (int i = 0; i < 7; i++) {
                            newAlarm.setWeekdays(i,false); //weekdays[i] = false;
                            Chip chip = (Chip) chipGroupWeek.getChildAt(i);
                            chip.setChipStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.ash_border)));
                        }
                        Calendar c = Calendar.getInstance();
                        c.setTimeInMillis(aLong);
                        newAlarm.setDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                    }
                });
                datePicker.show(getChildFragmentManager(), TAG);

            }
        });
        return view;
    }

    private void populateFieldsWithExistingData() {
        alarmLabel.setText(newAlarm.getAlarmLabel());
        switch_sound.setChecked(newAlarm.isSound());
        switch_vibration.setChecked(newAlarm.isVibration());
        switch_snooze.setChecked(newAlarm.isSnooze());
        timePicker.setHour(newAlarm.getAlarmTime().get(Calendar.HOUR_OF_DAY));
        timePicker.setMinute(newAlarm.getAlarmTime().get(Calendar.MINUTE));
        for (int i = 0; i < 7; i++) {
            Chip chip = (Chip) chipGroupWeek.getChildAt(i);
            chip.setChecked(newAlarm.getWeekdays(i));
            if (newAlarm.getWeekdays(i)) {
                chip.setChipStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.md_theme_primary)));
            } else {
                chip.setChipStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.ash_border)));
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
    }

}
