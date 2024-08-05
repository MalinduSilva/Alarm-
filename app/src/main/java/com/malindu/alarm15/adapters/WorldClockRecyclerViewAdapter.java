package com.malindu.alarm15.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.malindu.alarm15.R;
import com.malindu.alarm15.models.WorldClockItem;
import com.malindu.alarm15.utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class WorldClockRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//public class WorldClockRecyclerViewAdapter extends RecyclerView.Adapter<WorldClockRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "WorldClockRecViewAdapte";
    private static final int TYPE_FIRST = 0;
    private static final int TYPE_NORMAL = 1;
    private Context context;
    private List<WorldClockItem> worldClockItemList;

    public WorldClockRecyclerViewAdapter(Context context) {
        this.context = context;
        updateData();
    }

    public void updateData() {
        SharedPreferences prefs = context.getSharedPreferences(Constants.ALARM_PREFERENCES_FILE, Context.MODE_PRIVATE);
        Map<String, ?> allEntries = prefs.getAll();
        worldClockItemList = new ArrayList<>();
        worldClockItemList.add(null);
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().startsWith(Constants.WORLD_CLOCK_ITEM_KEY)) {
                String cityString = (String) entry.getValue();
                WorldClockItem city = WorldClockItem.parseWCItem(cityString);
                worldClockItemList.add(city);
            }
        }
        //worldClockItemList.add(null);
        rearrangeData();
        //worldClockItemList = WorldClockItem.getAllTimeZones();
        notifyDataSetChanged();
    }

    private void rearrangeData() {
        Collections.sort(worldClockItemList);
        if(worldClockItemList.size() > 1) {
            WorldClockItem temp = worldClockItemList.get(0);
            worldClockItemList.set(0, worldClockItemList.get(1));
            worldClockItemList.set(1, temp);
        }
        //Collections.reverse(worldClockItemList);
    }

    @Override
    public int getItemViewType(int position) {
        if (worldClockItemList.size() == 1) {
            return TYPE_FIRST;
        }
        return position == 1 ? TYPE_FIRST : TYPE_NORMAL;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview_worldclock, parent, false);
//        return new ViewHolder(view);
        if (viewType == TYPE_FIRST) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview_worldclock_add, parent, false);
            return new FirstViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview_worldclock, parent, false);
            return new NormalViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorldClockItem item = worldClockItemList.get(position);
        if (holder instanceof NormalViewHolder && item != null) {
            ((NormalViewHolder) holder).city.setText(item.getCity());
            ((NormalViewHolder) holder).country.setText(item.getTimeZoneID().substring(0, item.getTimeZoneID().lastIndexOf('/')));
            ((NormalViewHolder) holder).date.setTimeZone(item.getTimeZoneID());
            ((NormalViewHolder) holder).time.setTimeZone(item.getTimeZoneID());
        } else if (item == null) {
            Log.d(TAG, "onBindViewHolder: null");
        }
    }

    @Override
    public int getItemCount() {
        return worldClockItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView city, country;
        private TextClock time, date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            city = itemView.findViewById(R.id.city);
            country = itemView.findViewById(R.id.country);
            time = itemView.findViewById(R.id.time);
            date = itemView.findViewById(R.id.date);
        }
    }

    public static class FirstViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardView;

        public FirstViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.wc_add);
        }
    }

    public static class NormalViewHolder extends RecyclerView.ViewHolder {
        private TextView city, country;
        private TextClock time, date;

        public NormalViewHolder(@NonNull View itemView) {
            super(itemView);
            city = itemView.findViewById(R.id.city);
            country = itemView.findViewById(R.id.country);
            time = itemView.findViewById(R.id.time);
            date = itemView.findViewById(R.id.date);
        }
    }
}
