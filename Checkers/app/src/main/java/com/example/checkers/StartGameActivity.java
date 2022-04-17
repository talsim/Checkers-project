package com.example.checkers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;


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

    protected ImageView[][] tiles; // all the squares which contains the actual pieces (it will be drawn over them)
    protected Board board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);

        initImageViews();

        board = new Board(tiles); // sets the board

    }

    private void initImageViews() {
        tiles = new ImageView[Board.SIZE][Board.SIZE];

        tiles[0][1] = findViewById(R.id.circle01);
        tiles[0][3] = findViewById(R.id.circle03);
        tiles[0][5] = findViewById(R.id.circle05);
        tiles[0][7] = findViewById(R.id.circle07);
        tiles[1][0] = findViewById(R.id.circle10);
        tiles[1][2] = findViewById(R.id.circle12);
        tiles[1][4] = findViewById(R.id.circle14);
        tiles[1][6] = findViewById(R.id.circle16);
        tiles[2][1] = findViewById(R.id.circle21);
        tiles[2][3] = findViewById(R.id.circle23);
        tiles[2][5] = findViewById(R.id.circle25);
        tiles[2][7] = findViewById(R.id.circle27);
        tiles[3][0] = findViewById(R.id.circle30);
        tiles[3][2] = findViewById(R.id.circle32);
        tiles[3][4] = findViewById(R.id.circle34);
        tiles[3][6] = findViewById(R.id.circle36);
        tiles[4][1] = findViewById(R.id.circle41);
        tiles[4][3] = findViewById(R.id.circle43);
        tiles[4][5] = findViewById(R.id.circle45);
        tiles[4][7] = findViewById(R.id.circle47);
        tiles[5][0] = findViewById(R.id.circle50);
        tiles[5][2] = findViewById(R.id.circle52);
        tiles[5][4] = findViewById(R.id.circle54);
        tiles[5][6] = findViewById(R.id.circle56);
        tiles[6][1] = findViewById(R.id.circle61);
        tiles[6][3] = findViewById(R.id.circle63);
        tiles[6][5] = findViewById(R.id.circle65);
        tiles[6][7] = findViewById(R.id.circle67);
        tiles[7][0] = findViewById(R.id.circle70);
        tiles[7][2] = findViewById(R.id.circle72);
        tiles[7][4] = findViewById(R.id.circle74);
        tiles[7][6] = findViewById(R.id.circle76);
    }
}