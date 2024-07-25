package com.malindu.alarm15.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.malindu.alarm15.R;
import com.malindu.alarm15.models.LocationAlarm;
import com.malindu.alarm15.utils.Constants;
import com.malindu.alarm15.utils.LocationUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

public class LocationRecyclerViewAdapter extends RecyclerView.Adapter<LocationRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "LocationRecViewAdapter";
    private Context context;
    private ArrayList<LocationAlarm> locationAlarmList;
    private Map<String, ?> allRecords;

    public interface OnLocationClickListener { void onLocationClick(LocationAlarm locationAlarm); }
    private OnLocationClickListener locationClickListener;
    public void setOnLocationClickListener(OnLocationClickListener listener) { this.locationClickListener = listener; }

    public LocationRecyclerViewAdapter(Context context) {
        this.context = context;
        locationAlarmList = new ArrayList<>();
        loadLocationAlarms();
    }

    private void loadLocationAlarms() {
        SharedPreferences sp = context.getSharedPreferences(Constants.ALARM_PREFERENCES_FILE, Context.MODE_PRIVATE);
        allRecords = sp.getAll();
        locationAlarmList.clear();
        for (Map.Entry<String, ?> entry : allRecords.entrySet()) {
            if (entry.getKey().startsWith(Constants.LOCATION_ALARM_KEY)) {
                LocationAlarm locationAlarm = LocationUtils.parseLocationAlarm(entry.getValue().toString());
                locationAlarmList.add(locationAlarm);
            }
        }
        //Collections.sort(locationAlarmList);
        // TODO sort by the distance to the location dynamically
    }

    public void updateData() {
        loadLocationAlarms();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview_alarm_location, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocationAlarm locationAlarm = locationAlarmList.get(position);
        holder.locationTitle.setText(locationAlarm.getTitle());
        holder.locationAddress.setText(locationAlarm.getAddress());
        holder.locationRange.setText(String.format(Locale.getDefault(), "%dm", locationAlarm.getRange()));
        holder.turnOnSwitch.setOnCheckedChangeListener(null);
        holder.turnOnSwitch.setChecked(locationAlarm.isTurnedOn());
        holder.turnOnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                locationAlarm.setTurnedOn(isChecked);
                if (!isChecked) {
                    LocationUtils.cancelLocationAlarm(context, locationAlarm);
                } else {
                    LocationUtils.setLocationAlarm(context, locationAlarm);
                }
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationClickListener != null) { locationClickListener.onLocationClick(locationAlarm); }
                else {
                    Toast.makeText(context, "asd", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.cardView.setLongClickable(true);
        holder.btnDelete.setVisibility(View.GONE);
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder.btnDelete.setVisibility(View.VISIBLE);
                return true;
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(context)
                        .setTitle(R.string.dialog_alarm_delete_title)
                        .setMessage(R.string.dialog_alarm_delete_message)
                        .setPositiveButton(R.string.dialog_alarm_delete_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LocationUtils.deleteLocationAlarm(context, locationAlarm);
                            }
                        })
                        .setNegativeButton(R.string.dialog_alarm_delete_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                holder.btnDelete.setVisibility(View.GONE);
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                holder.btnDelete.setVisibility(View.GONE);
                            }
                        })
                        //.setCancelable(false)
                        .show();
            }
        });
        if (!locationAlarm.getNote_title().isEmpty() || !locationAlarm.getNote().isEmpty()) {
            holder.layoutNote.setVisibility(View.VISIBLE);
            String note = "";

            if (!locationAlarm.getNote_title().isEmpty()) {
                if (locationAlarm.getNote().isEmpty()) {
                    note = locationAlarm.getNote_title();
                } else {
                    note = locationAlarm.getNote_title() + "\n" + locationAlarm.getNote();
                }
            } else {
                note = locationAlarm.getNote();
            }
            holder.locationNote.setText(note);
        } else {
            holder.layoutNote.setVisibility(View.GONE);
        }
        holder.btnExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.locationNote.getMaxLines() == 3) {
                    holder.locationNote.setMaxLines(Integer.MAX_VALUE);
                    holder.locationNote.setEllipsize(null);
                    holder.btnExpand.setImageResource(R.drawable.icon_arrow_up);
                } else {
                    holder.locationNote.setMaxLines(3);
                    holder.locationNote.setEllipsize(TextUtils.TruncateAt.END);
                    holder.btnExpand.setImageResource(R.drawable.icon_arrow_down);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + locationAlarmList.size());
        return locationAlarmList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView locationTitle, locationAddress, locationRange, locationNote;
        private ImageView iconAddress, iconRange, iconNote, btnExpand;
        private LinearLayout layoutNote;
        private MaterialSwitch turnOnSwitch;
        private Button btnDelete;
        private MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            locationTitle = itemView.findViewById(R.id.location_title);
            locationAddress = itemView.findViewById(R.id.txt_address);
            locationRange = itemView.findViewById(R.id.txt_range);
            locationNote = itemView.findViewById(R.id.txt_note);
            iconAddress = itemView.findViewById(R.id.icon_address);
            iconRange = itemView.findViewById(R.id.icon_range);
            iconNote = itemView.findViewById(R.id.icon_note);
            btnExpand = itemView.findViewById(R.id.icon_arrow_down);
            layoutNote = itemView.findViewById(R.id.layout_note);
            turnOnSwitch = itemView.findViewById(R.id.switch_on);
            btnDelete = itemView.findViewById(R.id.btnDeleteLocation);
            cardView = itemView.findViewById(R.id.cardview_location_alarm_item_parent);
        }
    }
}
