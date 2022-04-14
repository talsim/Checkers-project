package com.example.checkers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import java.util.Objects;


/*
 * RULES
 * >>>>>>>>>>>>>
 * 1.	Checkers can only move diagonally, on darkwood tiles.
 *
 * 2.	Normal checkers can only move forward diagonally (for black checkers,
 * 		this is down and for white checkers, this is up).
 *
 * 3.	A checker becomes a king when it reaches the opponents end and cannot
 * 		move forward anymore.
 *
 * 4.	Once a checker becomes a king, the player's turn is over.
 *
 * 5.	After a checker/king moves one space diagonally, the player's turn is
 * 		over.
 *
 * 6.	If an opponent's checker/king can be skipped, it must be skipped.
 *
 * 7.	If after a skip, the same checker can skip again, it must. Otherwise,
 * 		the turn is over.
 *
 * 8.	The game is over if a player either has no more checkers or cannot make
 * 		a move on their turn.
 *
 * 9.	The player with the black checkers moves first.
 */

public class StartGameActivity extends AppCompatActivity {

    protected ImageView[][] imageViews; // all the squares which will contain the actual pieces (it will be drawn over them)
    protected Board board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);

        initImageViews();
    }

    private void initImageViews() {
        imageViews = new ImageView[8][8];

        imageViews[0][0] = findViewById(R.id.circle00);
        imageViews[0][2] = findViewById(R.id.circle02);
        imageViews[0][4] = findViewById(R.id.circle04);
        imageViews[0][6] = findViewById(R.id.circle06);
        imageViews[1][1] = findViewById(R.id.circle11);
        imageViews[1][3] = findViewById(R.id.circle13);
        imageViews[1][5] = findViewById(R.id.circle15);
        imageViews[1][7] = findViewById(R.id.circle17);
        imageViews[2][0] = findViewById(R.id.circle20);
        imageViews[2][2] = findViewById(R.id.circle22);
        imageViews[2][4] = findViewById(R.id.circle24);
        imageViews[2][6] = findViewById(R.id.circle26);
        imageViews[3][1] = findViewById(R.id.circle31);
        imageViews[3][3] = findViewById(R.id.circle33);
        imageViews[3][5] = findViewById(R.id.circle35);
        imageViews[3][7] = findViewById(R.id.circle37);
        imageViews[4][0] = findViewById(R.id.circle40);
        imageViews[4][2] = findViewById(R.id.circle42);
        imageViews[4][4] = findViewById(R.id.circle44);
        imageViews[4][6] = findViewById(R.id.circle46);
        imageViews[5][1] = findViewById(R.id.circle51);
        imageViews[5][3] = findViewById(R.id.circle53);
        imageViews[5][5] = findViewById(R.id.circle55);
        imageViews[5][7] = findViewById(R.id.circle57);
        imageViews[6][0] = findViewById(R.id.circle60);
        imageViews[6][2] = findViewById(R.id.circle62);
        imageViews[6][4] = findViewById(R.id.circle64);
        imageViews[6][6] = findViewById(R.id.circle66);
        imageViews[7][1] = findViewById(R.id.circle71);
        imageViews[7][3] = findViewById(R.id.circle73);
        imageViews[7][5] = findViewById(R.id.circle75);
        imageViews[7][7] = findViewById(R.id.circle77);
    }
}