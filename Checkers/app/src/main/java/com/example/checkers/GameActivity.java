package com.example.checkers;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import static com.example.checkers.DBUtils.addDataToDatabase;
import static com.example.checkers.DBUtils.isGameOver;
import static com.example.checkers.DBUtils.isHost;
import static com.example.checkers.LobbyActivity.playerName;
import static com.example.checkers.LobbyActivity.roomName;
import static com.example.checkers.LobbyActivity.roomRef;

import java.util.HashMap;
import java.util.Map;

/** This class is responsible to handle game events, such as piece moves, game ending and more.
 * @author Tal Simhayev
 * @version 1.0
 */
public class GameActivity extends AppCompatActivity {

    /**
     * all the squares which contain the actual pieces (reference from the xml).
     */
    public static final ImageView[][] imageViewsTiles = new ImageView[Board.SIZE][Board.SIZE];
    public static final String TAG = "GameActivity";
    public static ListenerRegistration guestMovesUpdatesListener;
    public static ListenerRegistration hostMovesUpdatesListener;
    public static ListenerRegistration gameOverListener;
    public TextView currentTurn;
    public Board board;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        currentTurn = findViewById(R.id.currentTurn);

        initImageViews();

        board = new Board();
        initBoardAndDrawPieces(); // init board as well as drawing the black and red pieces on it

        // set initial value for isBlackTurn (host starts as black)
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        DocumentReference gameUpdatesRef = fStore.collection(LobbyActivity.ROOMSPATH).document(roomName).collection("gameplay").document("gameUpdates");
        Map<String, Object> data = new HashMap<>();
        data.put("isBlackTurn", true); // sets isBlackTurn to true, so that Black player starts first.
        addDataToDatabase(data, gameUpdatesRef);

        if (isHost()) {
            currentTurn.setText(R.string.your_turn);

            // set a listener for red's moves (guest moves) and move the red pieces accordingly
            DocumentReference guestMovesUpdatesRef = roomRef.collection("gameplay").document("guestMovesUpdates");
            guestMovesUpdatesListener = listenDBForPieceMoves(guestMovesUpdatesRef, false);
        } else {
            currentTurn.setText(R.string.not_your_turn);

            // set a listener for black's moves (host pieces) and move the black pieces accordingly
            DocumentReference hostMovesUpdatesRef = roomRef.collection("gameplay").document("hostMovesUpdates");
            hostMovesUpdatesListener = listenDBForPieceMoves(hostMovesUpdatesRef, true);
        }

        setOnClickForPieces();


    }

    // set onClick listeners on all pieces
    public void setOnClickForPieces() {
        for (int x = 0; x < Board.SIZE; x++) {
            for (int y = 0; y < Board.SIZE; y++) {
                Piece currPiece = board.getBoardArray()[x][y];
                if (currPiece != null) {
                    imageViewsTiles[x][y].setOnClickListener(new OnClickListenerForPieceMoves(currPiece, board, currentTurn));
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
                    board.getBoardArray()[x][y] = new RedPiece(x, y, currentTurn);
                }


                // black pieces
                if (x >= 5 && Logic.isTileForChecker(x, y)) {
                    imageViewsTiles[x][y].setImageResource(R.drawable.black_piece);
                    board.getBoardArray()[x][y] = new BlackPiece(x, y, currentTurn);
                }

            }
        }
    }

    // listen to playerMovesUpdatesRef in the host and in the guest
    private ListenerRegistration listenDBForPieceMoves(DocumentReference playerMovesUpdatesRef, boolean isPieceBlack) {

        return playerMovesUpdatesRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    String endAxis = (String) snapshot.get("endAxis"); // parsing the axis in the format: "X-Y"
                    String startAxis = (String) snapshot.get("startAxis"); // parsing the axis in the format: "X-Y"
                    Boolean isJump = (Boolean) snapshot.get("isJump");
                    Boolean isKingDb = (Boolean) snapshot.get("isKing");
                    if (endAxis != null && startAxis != null && isKingDb != null) {
                        // parser
                        int startX = Integer.parseInt(startAxis.split("-")[0]);
                        int startY = Integer.parseInt(startAxis.split("-")[1]);
                        int endX = Integer.parseInt(endAxis.split("-")[0]);
                        int endY = Integer.parseInt(endAxis.split("-")[1]);
                        Move move = new Move(startX, startY, endX, endY);
                        move.perform(isPieceBlack, isKingDb);

                        // updating boardArray
                        board.getBoardArray()[endX][endY] = createAddedPiece(isPieceBlack, isKingDb, endX, endY);
                        board.getBoardArray()[startX][startY] = null; // remove old piece

                        // marking the start position (for the user)
                        imageViewsTiles[startX][startY].setImageResource(R.drawable.possible_location_marker);

                        if (isJump != null) {
                            if (isJump) { // if true: there was a jump, remove the jumped piece
                                String jumpedAxis = (String) snapshot.get("jumpedAxis"); // // parsing the axis in the format: "X-Y"
                                if (jumpedAxis != null) {
                                    int jumpedX = Integer.parseInt(jumpedAxis.split("-")[0]);
                                    int jumpedY = Integer.parseInt(jumpedAxis.split("-")[1]);

                                    GameActivity.imageViewsTiles[jumpedX][jumpedY].setImageResource(android.R.color.transparent);
                                    GameActivity.imageViewsTiles[jumpedX][jumpedY].setClickable(false);
                                    board.getBoardArray()[jumpedX][jumpedY] = null;
                                } else
                                    Log.d(TAG, "Couldn't get jumpedAxis");
                            }
                        }
                        currentTurn.setText(R.string.your_turn);
                        isGameOver(board, !isPieceBlack);
                    }
                }
            }
        });
    }

    private Piece createAddedPiece(boolean isPieceBlack, boolean isKingDb, int endX, int endY) {
        Piece addedPiece;
        if (isKingDb)
            addedPiece = new KingPiece(endX, endY, isPieceBlack, currentTurn);
        else if (isPieceBlack)
            addedPiece = new BlackPiece(endX, endY, currentTurn);
        else
            addedPiece = new RedPiece(endX, endY, currentTurn);
        return addedPiece;
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
    }

    @Override
    protected void onStop() {
        if (gameOverListener != null)
            gameOverListener.remove();
        if (hostMovesUpdatesListener != null)
            hostMovesUpdatesListener.remove();
        if (guestMovesUpdatesListener != null)
            guestMovesUpdatesListener.remove();
        super.onStop();
    }
}