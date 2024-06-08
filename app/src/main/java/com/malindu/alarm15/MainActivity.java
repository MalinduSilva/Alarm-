package com.malindu.alarm15;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationBarView;
import com.malindu.alarm15.ui.AlarmFragment;
import com.malindu.alarm15.ui.ClockFragment;
import com.malindu.alarm15.ui.LocationFragment;
import com.malindu.alarm15.ui.StopwatchFragment;
import com.malindu.alarm15.ui.TimerFragment;
import com.malindu.alarm15.utils.Constants;
import com.malindu.alarm15.utils.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private NavigationBarView navigationBarView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
        setTheme(R.style.AppTheme);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.ALARM_PREFERENCES_FILE, MODE_PRIVATE);
        if (sharedPreferences.getBoolean(Constants.ALARM_PREFERENCES_KEY_FIRST_LAUNCH_APP, true)) {
            firstLaunchTour();
            //sharedPreferences.edit().putBoolean(ALARM_PREFERENCES_FIRST_LAUNCH, false).apply();

        }

        toolbar = findViewById(R.id.toolbar);
        navigationBarView = findViewById(R.id.bottom_navbar);
        rearrangeMenuItems((BottomNavigationView) navigationBarView);

        Drawable three_dots = getDrawable(R.drawable.icon_three_dots);
        three_dots.setTint(getColor(R.color.md_theme_primary));
        toolbar.setOverflowIcon(three_dots);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        ClockFragment clockFragment = ClockFragment.newInstance();
        if (savedInstanceState != null) {
            // Restore the selected fragment based on the saved instance state
            int selectedFragmentId = savedInstanceState.getInt(Constants.SELECTED_FRAGMENT_ID_KEY, R.id.clockFragment);
            switchFragment(selectedFragmentId);
        } else {
            // Default fragment to open first
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, clockFragment).commit();
        }

        navigationBarView.setItemIconTintList(ColorStateList.valueOf(getColor(R.color.md_theme_primary)));
        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                switchFragment(itemId);
                return true;
            }
        });
        navigationBarView.setOnItemReselectedListener(new BottomNavigationView.OnItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                switchFragment(itemId);
            }
        });
//        TODO: create a fab and use it across all fragments {@link activity_main.fab}
//        fab = findViewById(R.id.fab);
//        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_theme_primaryContainer)));
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "fab", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void firstLaunchTour() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.welcome_title)
                .setMessage(R.string.welcome_message)
                .setPositiveButton(R.string.welcome_button_permissions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions();
                    }
                })
                .setNegativeButton(R.string.welcome_button_tour, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        initTour();
                    }
                })
                .setNeutralButton(R.string.welcome_button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getSharedPreferences(Constants.ALARM_PREFERENCES_FILE, MODE_PRIVATE)
                                .edit()
                                .putBoolean(Constants.ALARM_PREFERENCES_KEY_FIRST_LAUNCH_APP, true)
                                .apply();
                        finish();
                    }
                })
                .show();
    }

    private void initTour() {
        //TODO implement tour, at end, check permissions
        requestPermissions();
    }

    private void requestPermissions() {
        List<String> permissionsToRequest = new ArrayList<>();

        // API level 33 (Android 13) needs permissions to post notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS") != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add("android.permission.POST_NOTIFICATIONS");
            }
        }

        // API level 31 (Android 12) needs permissions to use full-screen intents, which is used for alarm ringing screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {  // API level 31 (Android 12)
            if (ContextCompat.checkSelfPermission(this, "android.permission.USE_FULL_SCREEN_INTENT") != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add("android.permission.USE_FULL_SCREEN_INTENT");
            }
        }

        // permission for approximate location
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add("android.permission.ACCESS_COARSE_LOCATION");
        }
        // permission for precise location
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add("android.permission.ACCESS_FINE_LOCATION");
        }
        // permission for using internet, to get api data
        if (ContextCompat.checkSelfPermission(this, "android.permission.INTERNET") != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add("android.permission.INTERNET");
        }
        // permission for network state
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_NETWORK_STATE") != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add("android.permission.ACCESS_NETWORK_STATE");
        }

        if (!permissionsToRequest.isEmpty()) {
            PermissionUtils.requestPermissions(this, permissionsToRequest.toArray(new String[0]));
            //ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), 1);
        }
    }

    private void rearrangeMenuItems(BottomNavigationView bottomNavigationView) {
        Menu menu = bottomNavigationView.getMenu();

        // Save the current menu items
        MenuItem clockItem = menu.findItem(R.id.clockFragment);
        MenuItem alarmItem = menu.findItem(R.id.alarmFragment);
        MenuItem locationItem = menu.findItem(R.id.locationFragment);
        MenuItem timerItem = menu.findItem(R.id.timerFragment);
        MenuItem stopwatchItem = menu.findItem(R.id.stopwatchFragment);

        // Clear the current menu
        menu.clear();

        // Add menu items in the new order
        menu.add(Menu.NONE, clockItem.getItemId(), Menu.NONE, clockItem.getTitle()).setIcon(clockItem.getIcon());
        menu.add(Menu.NONE, alarmItem.getItemId(), Menu.NONE, alarmItem.getTitle()).setIcon(alarmItem.getIcon());
        menu.add(Menu.NONE, locationItem.getItemId(), Menu.NONE, locationItem.getTitle()).setIcon(locationItem.getIcon());
        menu.add(Menu.NONE, timerItem.getItemId(), Menu.NONE, timerItem.getTitle()).setIcon(timerItem.getIcon());
        menu.add(Menu.NONE, stopwatchItem.getItemId(), Menu.NONE, stopwatchItem.getTitle()).setIcon(stopwatchItem.getIcon());
    }

    private void switchFragment(int fragmentID) {
        Fragment fragment = null;
        if (fragmentID == R.id.clockFragment) {
            fragment = ClockFragment.newInstance();
        } else if (fragmentID == R.id.alarmFragment) {
            fragment = AlarmFragment.newInstance();
        } else if (fragmentID == R.id.locationFragment) {
            fragment = LocationFragment.newInstance();
        } else if (fragmentID == R.id.timerFragment) {
            fragment = TimerFragment.newInstance();
        } else if (fragmentID == R.id.stopwatchFragment) {
            fragment = StopwatchFragment.newInstance();
        }

        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_top_dots, menu);
        MenuItem settingsItem = menu.findItem(R.id.settingsFragment);
        settingsItem.setIcon(R.drawable.icon_settings);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            settingsItem.setIconTintList(ColorStateList.valueOf(getColor(R.color.md_theme_primary)));
        }
        settingsItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        MenuItem helpItem = menu.findItem(R.id.helpFragment);
        helpItem.setIcon(R.drawable.icon_info);
        //return super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == R.id.settingsFragment) {
            Toast.makeText(this, "Settings - to be implemented...", Toast.LENGTH_SHORT).show();
        } else if (item_id == R.id.helpFragment) {
            Toast.makeText(this, "Help - to be implemented...", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // nav bar resets when the phone is rotated. this is the fix
        // cause - on config changes, activities are restarted
        int selectedItemId = navigationBarView.getSelectedItemId();
        outState.putInt(Constants.SELECTED_FRAGMENT_ID_KEY, selectedItemId);
    }
}