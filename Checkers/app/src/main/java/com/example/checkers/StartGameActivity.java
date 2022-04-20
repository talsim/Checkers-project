package com.example.checkers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


/*
 * RULES
 * >>>>>>>>>>>>>
 * 1.	Checkers can only move diagonally, on darkwood tiles.
 *
 * 2.	Normal checkers can only move forward diagonally (for black checkers,
 * 		this is down and for red checkers, this is up).
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

    protected ImageView[][] imageViewsTiles = new ImageView[Board.SIZE][Board.SIZE]; // all the squares which contain the actual pieces (it will be drawn over them)
    protected Board board;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);

        Piece[][] boardArray = new Piece[Board.SIZE][Board.SIZE];
        board = new Board(boardArray);

        initImageViews(imageViewsTiles);
        initBoardAndDrawPieces(); // init board




//        for (int x = 0; x < Board.SIZE; x++) {
//            for (int y = 0; y < Board.SIZE; y++) {
//                if (board.isTileForChecker(x, y)) {
//                    /*
//                    1. implement onClick movement for black
//                    NOT USEFUL! 1.1. check with isTileForChecker if the x and y axis represent a possible checker tile (darkwood colored)
//                    1.2. if piece is NOT on edge (y > 0  && y < 7), then it can move upwards with making sure that moving diagonally by doing the following: going upwards -> (endX == startX+1) AND left or right -> (endY == startY-1 || endY-1 == startY)
//                    1.3. if piece is on edge, then find out what edge (left or right) by doing (y == 0) for left OR (y == 7) for right
//                    1.4. if piece is on left edge, then check that it moves to a higher row ofc (endX == startX+1), and also to (endY == 1)
//                    1.5. if piece is on right edge, then check that it moves also to higher row, and also to (endY == 6)
//                    1.6. DONE! :]
//                     */
//
//                    int X = x;
//                    int Y = y;
//                    // for black only
//                    if (x >= 5) {
//                        board.getImageViewsTiles()[x][y].setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                                if (Y > 0 && Y < 7) {
//                                    // left side
//                                    Move left = new Move(board, X, Y, X - 1, Y - 1);
//                                    board.getImageViewsTiles()[X - 1][Y - 1].setImageResource(R.drawable.possible_location_marker);
//                                    board.getImageViewsTiles()[X - 1][Y - 1].setClickable(true);
//                                    board.getImageViewsTiles()[X - 1][Y - 1].setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            // now just perform the move to the left side
//                                            left.performMove();
//                                        }
//                                    });
//                                }
//                            }
//                        });
//                    }
//                }
//            }
//        }

    }



    // Responsible for drawing the pieces on the board
    public void initBoardAndDrawPieces() {
        for (int x = 0; x < Board.SIZE; x++) {
            for (int y = 0; y < Board.SIZE; y++) {
                // red pieces
                if (x <= 2 && board.isTileForChecker(x, y)) {
                    this.imageViewsTiles[x][y].setImageResource(R.drawable.red_piece);
                    board.getBoardArray()[x][y] = new Piece(this.imageViewsTiles[x][y], x, y, false);
                }


                // black pieces
                if (x >= 5 && board.isTileForChecker(x, y)) {
                    this.imageViewsTiles[x][y].setImageResource(R.drawable.black_piece);
                    board.getBoardArray()[x][y] = new Piece(this.imageViewsTiles[x][y], x, y, true);
                }

            }
        }
    }


    private void initImageViews(ImageView[][] tiles) {
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