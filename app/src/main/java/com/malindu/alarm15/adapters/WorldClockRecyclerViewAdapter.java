package com.malindu.alarm15.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.search.SearchBar;
import com.malindu.alarm15.R;
import com.malindu.alarm15.models.WorldClockItem;
import com.malindu.alarm15.utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class WorldClockRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//public class WorldClockRecyclerViewAdapter extends RecyclerView.Adapter<WorldClockRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "WorldClockRecViewAdapte";
    private static final int TYPE_FIRST = 0;
    private static final int TYPE_NORMAL = 1;
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;
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
        } else if (holder instanceof FirstViewHolder) {
            ((FirstViewHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater inflater = LayoutInflater.from(context);
                    View dialogView = inflater.inflate(R.layout.dialog_worldclock_select, null);
                    EditText search = dialogView.findViewById(R.id.search_bar);
                    ImageView clear = dialogView.findViewById(R.id.icon_close);
                    ListView listView = dialogView.findViewById(R.id.listview_wc);
                    List<String> cityList = WorldClockItem.getSearchCityList("");
                    ArrayAdapter<String> city_adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_single_choice, cityList);
                    listView.setAdapter(city_adapter);
                    listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                    clear.setVisibility(View.GONE);
                    search.addTextChangedListener(new android.text.TextWatcher() {

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            //
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            city_adapter.getFilter().filter(s.toString());
                            if (count == 0) {
                                clear.setVisibility(View.GONE);
                            } else {
                                clear.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            //
                        }
                    });

                    new MaterialAlertDialogBuilder(context)
                            .setTitle(R.string.worldclock_add_title)
                            .setView(dialogView)
                            .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int selectedPosition = listView.getCheckedItemPosition();
                                    if (selectedPosition != ListView.INVALID_POSITION) {
                                        String selectedItem = city_adapter.getItem(selectedPosition);
                                        WorldClockItem worldClockItem = new WorldClockItem(selectedItem, WorldClockItem.getWCItemFromCity(selectedItem).getTimeZoneID());
                                        sharedPreferences = context.getSharedPreferences(Constants.ALARM_PREFERENCES_FILE, Context.MODE_PRIVATE);
                                        if (sharedPreferences.getString(Constants.WORLD_CLOCK_ITEM_KEY + worldClockItem.getCity(), "").isEmpty()) {
                                            sharedPreferencesEditor = sharedPreferences.edit();
                                            sharedPreferencesEditor.putString(Constants.WORLD_CLOCK_ITEM_KEY + worldClockItem.getCity(), worldClockItem.toString());
                                            sharedPreferencesEditor.apply();
                                            updateData();
                                        } else {
                                            Toast.makeText(context, context.getString(R.string.toast_city_exists), Toast.LENGTH_SHORT).show();
                                        }
                                        Log.d(TAG, "Selected item: " + selectedItem);
                                    } else {
                                        Log.d(TAG, "No item selected");
                                    }
                                }
                            })
                            .setNeutralButton(R.string.cancel, null)
                            .show();
                }
            });
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
