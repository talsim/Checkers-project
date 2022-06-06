package com.example.checkers;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

/**
 * This class contains helper functions for a database API.
 *
 * @author Tal Simhayev
 * @version 1.0
 */
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

    /**
     * This function returns to the caller activity, and finishes this activity.
     *
     * @param item The item in the menu (when pressing the back button).
     * @return true if returning to MainActivity, else the super function's value.
     */
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}