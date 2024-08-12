package com.malindu.alarm15.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;
import com.malindu.alarm15.R;
import com.malindu.alarm15.models.AlarmSoundItem;
import com.malindu.alarm15.models.VibratePattern;
import com.malindu.alarm15.utils.AlarmUtils;
import com.malindu.alarm15.models.Alarm;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class AlarmAddNewClockDialog extends DialogFragment {
    public static final String TAG = "AlarmAddNewClockDialog";

    // Widgets
    private Button btnSave, btnDiscard, btnCalendar;
    private TimePicker timePicker;
    private ChipGroup chipGroupWeek;
    private EditText alarmLabel;
    private MaterialSwitch switch_sound, switch_vibration, switch_snooze;
    private LinearLayout layout_sound_selection, layout_vibrate_selection, layout_snooze_selection;
    private TextView txt_alarm_sound, txt_vibrate_pattern, txt_snooze_pattern;
    private TextView txt_alarm_frequency;
    private Alarm newAlarm = new Alarm();
    //private boolean set_for_date = false; private boolean set_for_weekdays = false;

    // Communication interfaces
    public interface OnAlarmAddedListener { void onAlarmAdded(); }
    private OnAlarmAddedListener listener;
    public void setOnAlarmAddedListener(OnAlarmAddedListener listener) { this.listener = listener; }

    // Fields
    private List<AlarmSoundItem> alarmSoundList;
    private String[] alarmSoundsChoices;
    private int selectedChoiceIndex = 0;
    private int selectedChoiceIndex_vibrate = 0;
    private MediaPlayer mediaPlayer;

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
        txt_alarm_frequency = view.findViewById(R.id.txt_alarm_frequency);
        layout_sound_selection = view.findViewById(R.id.layout_sound_selection);
        layout_vibrate_selection = view.findViewById(R.id.layout_vibrate_selection);
        layout_snooze_selection = view.findViewById(R.id.layout_snooze_selection);
        txt_alarm_sound = view.findViewById(R.id.txt_alarm_sound);
        txt_vibrate_pattern = view.findViewById(R.id.txt_vibrate_pattern);
        txt_snooze_pattern = view.findViewById(R.id.txt_snooze_pattern);

        // Check if an existing Alarm object is provided
        if (getArguments() != null && getArguments().containsKey("alarm")) {
            newAlarm = (Alarm) getArguments().getSerializable("alarm");
            populateFieldsWithExistingData();
        } else {
            newAlarm = new Alarm();
            newAlarm.setSet_for_tomorrow(true);
            newAlarm.setAlarmSound(AlarmUtils.getDefaultAlarmSound(requireContext()));
            newAlarm.setVibratePattern(VibratePattern.PATTERN_ONE);
        }
        txt_alarm_frequency.setText(newAlarm.getAlarmDateAsText());

        btnDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newAlarm.setAlarmLabel(alarmLabel.getText().toString());
                newAlarm.setSound(switch_sound.isChecked());
                newAlarm.setVibration(switch_vibration.isChecked());
                newAlarm.setSnooze(switch_snooze.isChecked());
                newAlarm.setTurnedOn(true);
                if (!newAlarm.isSet_for_date() && !newAlarm.isSet_for_weekdays()) {
                    Calendar currentTime = Calendar.getInstance();
                    if (newAlarm.getAlarmTime().after(currentTime)) {
                        Log.d(TAG, "Time(save): " + newAlarm.getAlarmTime().get(Calendar.DAY_OF_MONTH));
                        newAlarm.setSet_for_today(true);
                        newAlarm.setDate(currentTime.get(Calendar.YEAR), currentTime.get(Calendar.MONTH), currentTime.get(Calendar.DATE));
                    } else {
                        Log.d(TAG, "Time(save): " + newAlarm.getAlarmTime().get(Calendar.DAY_OF_MONTH));
                        newAlarm.setSet_for_tomorrow(true);
                        newAlarm.setDate(currentTime.get(Calendar.YEAR), currentTime.get(Calendar.MONTH), currentTime.get(Calendar.DATE) + 1);
                    }
                }
                //AlarmUtils.setAlarmm(requireContext(), newAlarm, true);
                AlarmUtils.setAlarm(requireContext(), newAlarm);
                if (listener != null) {
                    listener.onAlarmAdded();
                }
                Log.d(TAG, "onClick: " + newAlarm.getAlarmSound().getTitle() + "---" + newAlarm.getAlarmSound().getUri());
                getDialog().dismiss();
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
                //set_for_date = false; set_for_weekdays = true;
                newAlarm.setSet_for_weekdays(true); newAlarm.setSet_for_date(false);
                newAlarm.setSet_for_today(false); newAlarm.setSet_for_tomorrow(false);
                //Calendar c = Calendar.getInstance();
                //newAlarm.setDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                txt_alarm_frequency.setText(newAlarm.getAlarmDateAsText());
            }
        });
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                newAlarm.setTime(hourOfDay, minute);
                Calendar currentTime = Calendar.getInstance();
                if (!newAlarm.isSet_for_weekdays() && !newAlarm.isSet_for_date()) {
                    if (newAlarm.getAlarmTime().get(Calendar.HOUR_OF_DAY) > currentTime.get(Calendar.HOUR_OF_DAY)
                        || (newAlarm.getAlarmTime().get(Calendar.HOUR_OF_DAY) == currentTime.get(Calendar.HOUR_OF_DAY))
                            && newAlarm.getAlarmTime().get(Calendar.MINUTE) > currentTime.get(Calendar.MINUTE)) {
                        newAlarm.setSet_for_today(true);
                        newAlarm.setSet_for_tomorrow(false);
                        newAlarm.setDate(currentTime.get(Calendar.YEAR), currentTime.get(Calendar.MONTH), currentTime.get(Calendar.DAY_OF_MONTH));
                    } else {
                        newAlarm.setSet_for_tomorrow(true);
                        newAlarm.setSet_for_today(false);
                        newAlarm.setDate(currentTime.get(Calendar.YEAR), currentTime.get(Calendar.MONTH), currentTime.get(Calendar.DAY_OF_MONTH)+1);
                    }
                }
                txt_alarm_frequency.setText(newAlarm.getAlarmDateAsText());
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
                        newAlarm.setSet_for_weekdays(false); newAlarm.setSet_for_date(true);
                        newAlarm.setSet_for_today(false); newAlarm.setSet_for_tomorrow(false);
                        Calendar c = Calendar.getInstance();
                        c.setTimeInMillis(aLong);
                        newAlarm.setDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                        txt_alarm_frequency.setText(newAlarm.getAlarmDateAsText());
                    }
                });
                datePicker.show(getChildFragmentManager(), TAG);
            }
        });

        alarmSoundList = AlarmUtils.getAvailableAlarmSounds(requireContext());
        alarmSoundsChoices = new String[alarmSoundList.size()];
        for (int i = 0; i < alarmSoundList.size(); i++) {
            alarmSoundsChoices[i] = alarmSoundList.get(i).getTitle();
        }
        txt_alarm_sound.setText(newAlarm.getAlarmSound().getTitle());
        mediaPlayer = new MediaPlayer();
        layout_sound_selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(requireContext());
                View dialogView = inflater.inflate(R.layout.dialog_volume_slider, null);
                Slider volumeSlider = dialogView.findViewById(R.id.volume_slider);
                ListView soundListView = dialogView.findViewById(R.id.sound_list_view);
                volumeSlider.setValue(newAlarm.getAlarmVolume());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_single_choice, alarmSoundsChoices);
                soundListView.setAdapter(adapter);
                soundListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                soundListView.setItemChecked(selectedChoiceIndex, true);
                volumeSlider.setLabelBehavior(LabelFormatter.LABEL_GONE);

                volumeSlider.addOnChangeListener(new Slider.OnChangeListener() {
                    @Override
                    public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            mediaPlayer.setVolume(value, value);
                        } else {
                            playAlarmSound(requireContext(), alarmSoundList.get(selectedChoiceIndex), value);
                        }
                    }
                });

                soundListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectedChoiceIndex = position;
                        stopAlarmSound();
                        playAlarmSound(requireContext(), alarmSoundList.get(selectedChoiceIndex), volumeSlider.getValue());
                    }
                });

                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.sound_select_title)
                        .setView(dialogView)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                newAlarm.setAlarmSound(alarmSoundList.get(selectedChoiceIndex));
                                txt_alarm_sound.setText(newAlarm.getAlarmSound().getTitle());
                                newAlarm.setAlarmVolume(volumeSlider.getValue());
                                stopAlarmSound();
                                //Uri selectedUri = alarmSoundList.get(selectedChoiceIndex).getUri();
                            }
                        })
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                stopAlarmSound();
                            }
                        })
//                        .setSingleChoiceItems(alarmSoundsChoices, 0, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                selectedChoiceIndex = which;
//                                Log.d(TAG, "onClick: " + alarmSoundList.get(selectedChoiceIndex).getTitle());
//                                stopAlarmSound();
//                                playAlarmSound(requireContext(), alarmSoundList.get(selectedChoiceIndex));
//                            }
//                        })
                        .show();
            }
        });
        txt_vibrate_pattern.setText(newAlarm.getVibratePattern().toString());
        String[] vibratePatterns = AlarmUtils.getVibratePatternNames();
        layout_vibrate_selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.vibrate_select_title)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                newAlarm.setVibratePattern(VibratePattern.valueOf(vibratePatterns[selectedChoiceIndex_vibrate]));
                                txt_vibrate_pattern.setText(vibratePatterns[selectedChoiceIndex_vibrate]);
                            }
                        })
                        .setSingleChoiceItems(vibratePatterns, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selectedChoiceIndex_vibrate = which;
                                Log.d(TAG, "onClick: " + vibratePatterns[which]);
                            }
                        })
                        .show();
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
            //Log.d(TAG, "populateFieldsWithExistingData: " + i + newAlarm.getWeekdays(i));
        }
        txt_alarm_sound.setText(newAlarm.getAlarmSound().getTitle());
        txt_vibrate_pattern.setText(newAlarm.getVibratePattern().toString());
//        for (int i = 0; i < 7; i++) {
//            Log.d(TAG, "Weekday " + i + ": " + newAlarm.getWeekdays(i));
//        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
    }

    private void playAlarmSound(Context context, AlarmSoundItem alarmSoundItem, float volume) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(context.getApplicationContext(), alarmSoundItem.getUri());
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build());
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.setVolume(volume, volume);
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void stopAlarmSound() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}
