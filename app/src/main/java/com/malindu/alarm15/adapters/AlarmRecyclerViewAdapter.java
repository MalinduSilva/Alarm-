package com.malindu.alarm15.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.malindu.alarm15.R;
import com.malindu.alarm15.utils.AlarmUtils;
import com.malindu.alarm15.models.Alarm;
import com.malindu.alarm15.utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class AlarmRecyclerViewAdapter extends RecyclerView.Adapter<AlarmRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "AlarmRecyclerViewAdapte";

    //private Alarm alarm = new Alarm();
    private Map<String, ?> allAlarms;
    private ArrayList<Alarm> alarmList;
    private Context context;
    public interface OnAlarmClickListener { void onAlarmClick(Alarm alarm); void onAlarmDeleted(); }
    private OnAlarmClickListener alarmClickListener;
    public void setOnAlarmClickListener(OnAlarmClickListener listener) { this.alarmClickListener = listener; }

    public AlarmRecyclerViewAdapter(Context context) {
        this.context = context;
        alarmList = new ArrayList<>();
        loadAlarms();
    }
    private void loadAlarms() {
        SharedPreferences sp = context.getSharedPreferences(Constants.ALARM_PREFERENCES_FILE, Context.MODE_PRIVATE);
        allAlarms = sp.getAll();
        alarmList.clear();
        for (Map.Entry<String, ?> entry : allAlarms.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            //Log.d(TAG, "AlarmList { key: " + key + ", value: " + value.toString() + "}");
            if (key.startsWith(Constants.ALARM_KEY)) {
                Alarm alarm = Alarm.getAlarmObj(value.toString());
                alarmList.add(alarm);
            }
        }
        Collections.sort(alarmList);
    }
    public void updateData() {
        //loadAlarmsFromPreferences();
        loadAlarms();
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview_alarm, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Alarm alarm = alarmList.get(position);
        if (alarm.getAlarmLabel().isEmpty()) {
            //holder.cardview_alarm_label_layout.setVisibility(View.GONE); //TODO: change this
            holder.cardview_alarm_label_layout.setVisibility(View.VISIBLE);
            holder.alarmLabel.setText(alarm.getAlarmID());
        } else {
            holder.cardview_alarm_label_layout.setVisibility(View.VISIBLE);
            holder.alarmLabel.setText(alarm.getAlarmLabel());
        }
        holder.alarmTime.setText(Alarm.getAlarmTimeAsText(context, alarm));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alarmClickListener != null) {
                    alarmClickListener.onAlarmClick(alarm);
                }
                //Toast.makeText(context, "card clicked", Toast.LENGTH_SHORT).show();
            }
        });
        holder.turnOnSwitch.setOnCheckedChangeListener(null); // To fix random alarms getting turned on
        holder.turnOnSwitch.setChecked(alarm.getTurnedOn());
        holder.turnOnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                alarm.setTurnedOn(isChecked);
                if (!isChecked) {
                    AlarmUtils.cancelAlarm(context, alarm);
                } else {
                    AlarmUtils.setAlarm(context, alarm);
                }
            }
        });
        holder.cardView.setLongClickable(true);
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder.btnDeleteAlarm.setVisibility(View.VISIBLE);
                return true;
            }
        });
        holder.btnDeleteAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(context)
                        .setTitle(R.string.dialog_alarm_delete_title)
                        .setMessage(R.string.dialog_alarm_delete_message)
                        .setPositiveButton(R.string.dialog_alarm_delete_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AlarmUtils.deleteAlarm(context, alarm);
                                if (alarmClickListener != null) {
                                    alarmClickListener.onAlarmDeleted();
                                }
                            }
                        })
                        .setNegativeButton(R.string.dialog_alarm_delete_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                holder.btnDeleteAlarm.setVisibility(View.GONE);
                                //requireActivity().finish();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });
        holder.cardview_alarm_frequency_weekdays_layout.setVisibility(alarm.isSet_for_weekdays() ? View.VISIBLE : View.GONE);
        holder.cardview_alarm_frequency_date.setVisibility(alarm.isSet_for_date() ? View.VISIBLE : View.GONE);
        holder.cardview_txt_monday.setTextColor(alarm.getWeekdays(0) ? context.getColor(R.color.black) : context.getColor(R.color.ash_border));
        holder.cardview_txt_tuesday.setTextColor(alarm.getWeekdays(1) ? context.getColor(R.color.black) : context.getColor(R.color.ash_border));
        holder.cardview_txt_wednesday.setTextColor(alarm.getWeekdays(2) ? context.getColor(R.color.black) : context.getColor(R.color.ash_border));
        holder.cardview_txt_thursday.setTextColor(alarm.getWeekdays(3) ? context.getColor(R.color.black) : context.getColor(R.color.ash_border));
        holder.cardview_txt_friday.setTextColor(alarm.getWeekdays(4) ? context.getColor(R.color.black) : context.getColor(R.color.ash_border));
        holder.cardview_txt_saturday.setTextColor(alarm.getWeekdays(5) ? context.getColor(R.color.black) : context.getColor(R.color.ash_border));
        holder.cardview_txt_sunday.setTextColor(alarm.getWeekdays(6) ? context.getColor(R.color.black) : context.getColor(R.color.ash_border));
        holder.cardview_alarm_frequency_date.setText(alarm.getAlarmDateAsText());
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView alarmLabel, alarmTime;
        private LinearLayout cardview_alarm_label_layout;
        private MaterialSwitch turnOnSwitch;
        private MaterialCardView cardView;
        private Button btnDeleteAlarm;
        private LinearLayout cardview_alarm_frequency_weekdays_layout;
        private TextView cardview_alarm_frequency_date;
        private TextView cardview_txt_monday, cardview_txt_tuesday, cardview_txt_wednesday, cardview_txt_thursday, cardview_txt_friday,
                cardview_txt_saturday, cardview_txt_sunday;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            alarmLabel = itemView.findViewById(R.id.cardview_alarm_label);
            cardview_alarm_label_layout = itemView.findViewById(R.id.cardview_alarm_label_layout);
            turnOnSwitch = itemView.findViewById(R.id.cardview_alarm_switch);
            alarmTime = itemView.findViewById(R.id.cardview_alarm_time);
            cardView = itemView.findViewById(R.id.cardview_alarm_item_parent);
            btnDeleteAlarm = itemView.findViewById(R.id.btnDeleteAlarm);
            cardview_alarm_frequency_weekdays_layout = itemView.findViewById(R.id.cardview_alarm_frequency_weekdays_layout);
            cardview_alarm_frequency_date = itemView.findViewById(R.id.cardview_alarm_frequency_date);
            cardview_txt_monday = itemView.findViewById(R.id.cardview_txt_monday);
            cardview_txt_tuesday = itemView.findViewById(R.id.cardview_txt_tuesday);
            cardview_txt_wednesday = itemView.findViewById(R.id.cardview_txt_wednesday);
            cardview_txt_thursday = itemView.findViewById(R.id.cardview_txt_thursday);
            cardview_txt_friday = itemView.findViewById(R.id.cardview_txt_friday);
            cardview_txt_saturday = itemView.findViewById(R.id.cardview_txt_saturday);
            cardview_txt_sunday = itemView.findViewById(R.id.cardview_txt_sunday);
        }
    }
}
