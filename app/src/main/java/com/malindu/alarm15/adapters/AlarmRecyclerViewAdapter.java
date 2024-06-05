package com.malindu.alarm15.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.malindu.alarm15.R;
import com.malindu.alarm15.utils.AlarmUtils;
import com.malindu.alarm15.models.Alarm;

import java.util.ArrayList;
import java.util.Map;

public class AlarmRecyclerViewAdapter extends RecyclerView.Adapter<AlarmRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "AlarmRecyclerViewAdapte";

    //private Alarm alarm = new Alarm();
    private Map<String, ?> allAlarms;
    private ArrayList<Alarm> alarmList;
    private Context context;
    private static final String ALARM_PREFERENCES_FILE = "ALARM_PREFERENCES_FILE";
    private static final String ALARM_KEY  = "ALARM_";
    private static final String ALARM_COUNT_KEY  = "ALARM_COUNT";
    public interface OnAlarmClickListener { void onAlarmClick(Alarm alarm); }
    private OnAlarmClickListener alarmClickListener;
    public void setOnAlarmClickListener(OnAlarmClickListener listener) { this.alarmClickListener = listener; }

    public AlarmRecyclerViewAdapter(Context context) {
        this.context = context;
        alarmList = new ArrayList<>();
        loadAlarms();

    }
    private void loadAlarms() {
        SharedPreferences sp = context.getSharedPreferences(ALARM_PREFERENCES_FILE, Context.MODE_PRIVATE);
        allAlarms = sp.getAll();
        alarmList.clear();
        for (Map.Entry<String, ?> entry : allAlarms.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            //Log.d(TAG, "AlarmList { key: " + key + ", value: " + value.toString() + "}");
            if (!key.equals(ALARM_COUNT_KEY)) {
                Alarm alarm = Alarm.getAlarmObj(value.toString());
                alarmList.add(alarm);
            }
        }
    }
    public void updateData() {
        //loadAlarmsFromPreferences();
        loadAlarms();
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_alarm_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Alarm alarm = alarmList.get(position);
        if (alarm.getAlarmLabel().isEmpty()) {
            holder.cardview_alarm_label_layout.setVisibility(View.GONE);
        } else {
            holder.cardview_alarm_label_layout.setVisibility(View.VISIBLE);
            holder.alarmLabel.setText(alarm.getAlarmLabel());
        }
        holder.turnOnSwitch.setChecked(alarm.getTurnedOn());
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
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            alarmLabel = itemView.findViewById(R.id.cardview_alarm_label);
            cardview_alarm_label_layout = itemView.findViewById(R.id.cardview_alarm_label_layout);
            turnOnSwitch = itemView.findViewById(R.id.cardview_alarm_switch);
            alarmTime = itemView.findViewById(R.id.cardview_alarm_time);
            cardView = itemView.findViewById(R.id.cardview_alarm_item_parent);
        }
    }
}
