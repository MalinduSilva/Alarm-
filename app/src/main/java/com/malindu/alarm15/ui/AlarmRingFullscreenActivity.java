package com.malindu.alarm15.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.malindu.alarm15.R;

public class AlarmRingFullscreenActivity extends AppCompatActivity {
    private static final String TAG = "AlarmRingFullscreenActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alarm_ring_fullscreen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fullscreenmain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button dismissButton = findViewById(R.id.dismissButton);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Log.d(TAG, "onCreate: ");
    }
}