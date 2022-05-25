package com.example.checkers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import static com.example.checkers.DatabaseUtils.addDataToDatabase;

import java.util.HashMap;
import java.util.Map;



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
 * 6.	The game is over if a player either has no more checkers or cannot make
 * 		a move on their turn.
 *
 * 7.	The player with the black checkers moves first.
 */

public class GameActivity extends AppCompatActivity {

    public static final ImageView[][] imageViewsTiles = new ImageView[Board.SIZE][Board.SIZE]; // all the squares which contain the actual pieces (reference from the xml)
    public static final String TAG = "GameActivity";
    protected Board board;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        String roomName = null;
        String playerName = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            roomName = extras.getString("roomName");
            playerName = extras.getString("playerName");
        }

        initImageViews();

        board = new Board();
        initBoardAndDrawPieces(); // init board as well as drawing the black and red pieces on it

        // set initial value for isBlackTurn (host starts as black)
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        DocumentReference gameUpdatesRef = fStore.collection(WaitingRoomActivity.ROOMSPATH).document(roomName).collection("gameplay").document("gameUpdates");
        Map<String, Object> data = new HashMap<>();
        data.put("isBlackTurn", true); //  - we can also randomize this init value to get the result of a random player to start the game (a possible feature)
        addDataToDatabase(data, gameUpdatesRef);

        setOnClickForPieces(roomName, playerName);


    }

    public void setOnClickForPieces(String roomName, String playerName) {
        for (int x = 0; x < Board.SIZE; x++) {
            for (int y = 0; y < Board.SIZE; y++) {
                Piece currPiece = board.getBoardArray()[x][y];
                if (currPiece != null) {
                    imageViewsTiles[x][y].setOnClickListener(new MyOnClickListenerForPieceMoves(currPiece, board));
                }
            }
        }
    }

    // Responsible for drawing the pieces on the board
    public void initBoardAndDrawPieces() {
        for (int x = 0; x < Board.SIZE; x++) {
            for (int y = 0; y < Board.SIZE; y++) {
                // red pieces
                if (x <= 2 && Logic.isTileForChecker(x, y)) {
                    imageViewsTiles[x][y].setImageResource(R.drawable.red_piece);
                    board.getBoardArray()[x][y] = new Piece(x, y, false);
                }


                // black pieces
                if (x >= 5 && Logic.isTileForChecker(x, y)) {
                    imageViewsTiles[x][y].setImageResource(R.drawable.black_piece);
                    board.getBoardArray()[x][y] = new Piece(x, y, true);
                }

            }
        }
    }

    private void initImageViews() {

        imageViewsTiles[0][1] = findViewById(R.id.circle01);
        imageViewsTiles[0][3] = findViewById(R.id.circle03);
        imageViewsTiles[0][5] = findViewById(R.id.circle05);
        imageViewsTiles[0][7] = findViewById(R.id.circle07);
        imageViewsTiles[1][0] = findViewById(R.id.circle10);
        imageViewsTiles[1][2] = findViewById(R.id.circle12);
        imageViewsTiles[1][4] = findViewById(R.id.circle14);
        imageViewsTiles[1][6] = findViewById(R.id.circle16);
        imageViewsTiles[2][1] = findViewById(R.id.circle21);
        imageViewsTiles[2][3] = findViewById(R.id.circle23);
        imageViewsTiles[2][5] = findViewById(R.id.circle25);
        imageViewsTiles[2][7] = findViewById(R.id.circle27);
        imageViewsTiles[3][0] = findViewById(R.id.circle30);
        imageViewsTiles[3][2] = findViewById(R.id.circle32);
        imageViewsTiles[3][4] = findViewById(R.id.circle34);
        imageViewsTiles[3][6] = findViewById(R.id.circle36);
        imageViewsTiles[4][1] = findViewById(R.id.circle41);
        imageViewsTiles[4][3] = findViewById(R.id.circle43);
        imageViewsTiles[4][5] = findViewById(R.id.circle45);
        imageViewsTiles[4][7] = findViewById(R.id.circle47);
        imageViewsTiles[5][0] = findViewById(R.id.circle50);
        imageViewsTiles[5][2] = findViewById(R.id.circle52);
        imageViewsTiles[5][4] = findViewById(R.id.circle54);
        imageViewsTiles[5][6] = findViewById(R.id.circle56);
        imageViewsTiles[6][1] = findViewById(R.id.circle61);
        imageViewsTiles[6][3] = findViewById(R.id.circle63);
        imageViewsTiles[6][5] = findViewById(R.id.circle65);
        imageViewsTiles[6][7] = findViewById(R.id.circle67);
        imageViewsTiles[7][0] = findViewById(R.id.circle70);
        imageViewsTiles[7][2] = findViewById(R.id.circle72);
        imageViewsTiles[7][4] = findViewById(R.id.circle74);
        imageViewsTiles[7][6] = findViewById(R.id.circle76);
    }

    @Override
    public void onBackPressed() {
        // back button is not allowed here.
        super.onBackPressed();
    }
}