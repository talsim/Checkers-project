package com.example.checkers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import static com.example.checkers.DatabaseUtils.addDataToDatabase;
import static com.example.checkers.DatabaseUtils.isHost;
import static com.example.checkers.DatabaseUtils.getGuestUsername;
import static com.example.checkers.DatabaseUtils.updateBlackTurnInDb;

import java.util.HashMap;
import java.util.Map;

public class MyOnClickListenerForPieceMoves implements View.OnClickListener {

    public static final String TAG = "MyListenerForPieceMoves";
    public static ImageView[] lastUsedImageViews; // for removing the setOnClickListeners that we set and the player did not choose, so there will not be hanging listeners.
    public static Context appContext; // for showing dialogs
    private Piece piece;
    private final Board board;
    private final String roomName;
    private final String playerName;
    public static CollectionReference gameplayRef;
    public static DocumentReference roomRef;
    public static ListenerRegistration guestMovesUpdatesListener;
    public static ListenerRegistration hostMovesUpdatesListener;


    public MyOnClickListenerForPieceMoves(Piece piece, Board board) {
        this.piece = piece;
        this.board = board;
        this.roomName = WaitingRoomActivity.roomName;
        this.playerName = WaitingRoomActivity.playerName;
        roomRef = FirebaseFirestore.getInstance().collection(WaitingRoomActivity.ROOMSPATH).document(roomName);
        gameplayRef = roomRef.collection("gameplay");
        hostMovesUpdatesListener = null;
        guestMovesUpdatesListener = null;
        appContext = null;
        lastUsedImageViews = new ImageView[10];
    }

    @Override
    public void onClick(View v) {
        displayMoveOptionsAndMove(this.piece.getX(), this.piece.getY(), this.piece.isBlack(), this.piece.isKing(), (ImageView) v);
    }

    public void displayMoveOptionsAndMove(int x, int y, boolean isBlack, boolean isKing, ImageView pieceImage) {

        appContext = pieceImage.getContext();
        this.piece.clearPossibleLocationMarkers(board);
        this.piece.unsetOnClickLastImageViews(board);

        if (isHost(playerName, roomName)) // for the host (for black)
        {
            if (isBlack && getIsBlackTurn()) {
                highlightPiece(true, isKing, pieceImage);
                if (!isKing) {
                    // move black
                    this.piece = new BlackPiece(x, y, true, false);
                    ((BlackPiece) this.piece).move(board);
                } else
                {
                    this.piece = new KingPiece(x, y, true);
                    ((KingPiece) this.piece).move(board);
                }

            } else {
                // it is red's turn now.
                // set a listener for red's moves (guest moves) and move the red pieces accordingly
                DocumentReference guestMovesUpdatesRef = gameplayRef.document("guestMovesUpdates");
                guestMovesUpdatesListener = guestMovesUpdatesRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
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
                            Boolean isGameEnd = (Boolean) snapshot.get("isGameOver");
                            if (endAxis != null && startAxis != null && isKingDb != null) {
                                int startX = Integer.parseInt(startAxis.split("-")[0]);
                                int startY = Integer.parseInt(startAxis.split("-")[1]);
                                int endX = Integer.parseInt(endAxis.split("-")[0]);
                                int endY = Integer.parseInt(endAxis.split("-")[1]);
                                Move move = new Move(startX, startY, endX, endY);
                                move.perform(false, isKingDb);

                                // updating boardArray
                                board.getBoardArray()[endX][endY] = new Piece(endX, endY, false, isKingDb);
                                board.getBoardArray()[startX][startY] = null; // remove old piece

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

                                if (isGameEnd != null) { // the players only update when they won, so when isGameEnd is not null, it means it must be true (so someone won)
                                    piece.gameOver(false); // red won the game
                                }
                            }

                        }
                    }
                });

            }

        } else // for the guest (for red)
        {
            if (!isBlack && !getIsBlackTurn()) {
                highlightPiece(false, isKing, pieceImage);
                if (!isKing) {
                    this.piece = new RedPiece(x, y, false, false);
                    ((RedPiece) this.piece).move(board);
                } else
                {
                    this.piece = new KingPiece(x, y, false);
                    ((KingPiece) this.piece).move(board);
                }

            } else {
                // it is black's turn now.
                // set a listener for black's moves (host pieces) and move the black pieces accordingly
                DocumentReference hostMovesUpdatesRef = gameplayRef.document("hostMovesUpdates");
                hostMovesUpdatesListener = hostMovesUpdatesRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error);
                            return;
                        }
                        if (snapshot != null && snapshot.exists()) {
                            String endAxis = (String) snapshot.get("endAxis"); // parsing the axis by the format: "X-Y"
                            String startAxis = (String) snapshot.get("startAxis"); // parsing the axis by the format: "X-Y"
                            Boolean isJump = (Boolean) snapshot.get("isJump");
                            Boolean isKingDb = (Boolean) snapshot.get("isKing");
                            Boolean isGameEnd = (Boolean) snapshot.get("isGameOver");
                            if (endAxis != null && startAxis != null && isKingDb != null) {
                                int startX = Integer.parseInt(startAxis.split("-")[0]);
                                int startY = Integer.parseInt(startAxis.split("-")[1]);
                                int endX = Integer.parseInt(endAxis.split("-")[0]);
                                int endY = Integer.parseInt(endAxis.split("-")[1]);
                                Move move = new Move(startX, startY, endX, endY);
                                move.perform(true, isKingDb);

                                // updating boardArray
                                board.getBoardArray()[endX][endY] = new Piece(endX, endY, true, isKingDb); // ***** change isKing here
                                board.getBoardArray()[startX][startY] = null; // remove old piece

                                if (isJump != null) {
                                    if (isJump) {
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

                                if (isGameEnd != null) // the players only update when they won, so when isGameEnd is not null, it means it must be true (so someone won)
                                    piece.gameOver(true);
                            }
                        }
                    }
                });

            }
        }

//        ****play locally****
//        if (isBlack) {
//            highlightPiece(true, isKing, pieceImage);
//            if (!isKing) {
//                this.piece = new BlackPiece(x, y, true, false);
//                ((BlackPiece) this.piece).move(board);
//            } else {
//                this.piece = new KingPiece(x, y, true);
//                ((KingPiece) this.piece).move(board);
//            }
//        } else if (!isBlack) {
//            highlightPiece(false, isKing, pieceImage);
//            if (!isKing) {
//
//                this.piece = new RedPiece(x, y, false, false);
//                ((RedPiece) this.piece).move(board);
//
//            } else {
//                this.piece = new KingPiece(x, y, true);
//                ((KingPiece) this.piece).move(board);
//            }
//        }
    }

    private void highlightPiece(boolean isBlack, boolean isKing, ImageView piece) {
        if (isBlack) {
            if (isKing) {
                piece.setImageResource(R.drawable.black_king_highlighted);
                piece.setTag(R.drawable.black_king_highlighted);
            } else {
                piece.setImageResource(R.drawable.black_piece_highlighted);
                piece.setTag(R.drawable.black_piece_highlighted);
            }

        } else {
            if (isKing) {
                piece.setImageResource(R.drawable.red_king_highlighted);
                piece.setTag(R.drawable.red_king_highlighted);
            } else {
                piece.setImageResource(R.drawable.red_piece_highlighted);
                piece.setTag(R.drawable.red_piece_highlighted);
            }

        }
    }


    private boolean getIsBlackTurn() {
        Task<DocumentSnapshot> getTurn = gameplayRef.document("gameUpdates").get();
        while (!getTurn.isComplete()) {
            System.out.println("waiting for getIsBlackTurn");
        }
        if (getTurn.isSuccessful()) {
            DocumentSnapshot isBlackTurnResult = getTurn.getResult();
            Boolean val = (Boolean) isBlackTurnResult.get("isBlackTurn");
            if (val != null)
                return (boolean) val;
        }
        Log.d(TAG, "Error getting document: ", getTurn.getException());
        throw new IllegalStateException("couldn't get isBlackTurn from db");
    }


}

