package com.example.checkers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;


public class SettingsActivity extends AppCompatActivity {

    public static final String SETTINGS_PREFS = "settings";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SwitchCompat vibrator = findViewById(R.id.vibrateID);

        SharedPreferences settingsPrefs = getSharedPreferences(SETTINGS_PREFS, MODE_PRIVATE);
        boolean isVibrate = settingsPrefs.getBoolean("vibrate", true);
        vibrator.setChecked(isVibrate);

        vibrator.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(SETTINGS_PREFS, MODE_PRIVATE).edit();
            editor.putBoolean("vibrate", isChecked);
            editor.apply();
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        else
            System.out.println("*********Error: actionBar is null! Can't set back arrow.");

    }

    // return to the caller activity (just finish)
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}