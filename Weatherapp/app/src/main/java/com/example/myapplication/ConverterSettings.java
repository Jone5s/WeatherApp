package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;

public class ConverterSettings extends AppCompatActivity {

    private Switch unitSwitch;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        unitSwitch = findViewById(R.id.unitSwitch);
        backButton = findViewById(R.id.backButton);

        // Load saved unit preference
        SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean isMetric = preferences.getBoolean("isMetric", true);
        unitSwitch.setChecked(isMetric);

        // Set initial text on the switch
        updateSwitchText(isMetric);

        // Listen for switch changes
        unitSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Save the unit preference
                SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
                editor.putBoolean("isMetric", isChecked);
                editor.apply();

                // Update the text on the switch
                updateSwitchText(isChecked);
            }
        });

        // Listen for back button click
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return to the main activity
                finish();
            }
        });
    }

    private void updateSwitchText(boolean isMetric) {
        // Update the text on the switch based on the selected unit
        unitSwitch.setText(isMetric ? getString(R.string.units) + "Metric" : getString(R.string.units) + "Imperial");
    }
}

