package com.example.checkers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import java.util.Objects;

public class StartGameActivity extends AppCompatActivity {

    protected ImageView[][] imageViews;
    protected Board board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);

    }
}