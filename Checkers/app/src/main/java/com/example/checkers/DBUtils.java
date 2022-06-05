package com.example.checkers;


import static com.example.checkers.GameActivity.gameOverListener;
import static com.example.checkers.GameActivity.guestMovesUpdatesListener;
import static com.example.checkers.GameActivity.hostMovesUpdatesListener;
import static com.example.checkers.LobbyActivity.roomListener;
import static com.example.checkers.LobbyActivity.roomRef;
import static com.example.checkers.OnClickListenerForPieceMoves.TAG;
import static com.example.checkers.OnClickListenerForPieceMoves.appContext;
import static com.example.checkers.OnClickListenerForPieceMoves.gameplayRef;
import static com.example.checkers.LobbyActivity.ROOMSPATH;
import static com.example.checkers.LobbyActivity.playerName;
import static com.example.checkers.LobbyActivity.roomName;
import static com.example.checkers.LobbyActivity.roomsUpdaterViewListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBUtils {
    public static final String TAG = "DatabaseUtils";

    // wrapper function for update and set functions in the Firebase API
    public static void addDataToDatabase(Map<String, Object> data, DocumentReference docRef) {
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        docRef.update(data);
                    } else {
                        docRef.set(data);
                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                }
            }
        });
    }

    // checks if local playerName is equal to host name (roomName)
    public static boolean isHost() {
        return playerName.equals(roomName);
    }

    // returns true if playerName is the winner of the game, else otherwise.
    public static boolean isWinner(String nameOfWinner) {
        return playerName.equals(nameOfWinner);
    }


    // get from db
    public static String getGuestUsername() {
        Task<DocumentSnapshot> getGuest = roomRef.get();
        while (!getGuest.isComplete()) {
            System.out.println("waiting for guestUsername");
        }
        if (getGuest.isSuccessful()) {
            DocumentSnapshot guestUsernameDoc = getGuest.getResult();
            return (String) guestUsernameDoc.get("guest");
        } else
            Log.d(TAG, "Error getting document: ", getGuest.getException());

        return "*GUEST*"; // couldn't get the guest username
    }

    // get blackTurn and upload it to db
    public static void updateBlackTurnInDb(boolean blackTurn, CollectionReference gameplayRef) {
        Map<String, Object> gameUpdates = new HashMap<>();
        gameUpdates.put("isBlackTurn", blackTurn);
        addDataToDatabase(gameUpdates, gameplayRef.document("gameUpdates"));
    }

    // upload Piece locations by a format
    public static void uploadPieceLocationToDb(Move move, boolean isJump, int jumpX, int jumpY, boolean isKing) {
        DocumentReference documentReference;
        if (isHost())
            documentReference = gameplayRef.document("hostMovesUpdates"); // for host updates
        else
            documentReference = gameplayRef.document("guestMovesUpdates"); // for guest updates
        Map<String, Object> updates = new HashMap<>();
        String startAxis = move.getStartX() + "-" + move.getStartY();
        String endAxis = move.getEndX() + "-" + move.getEndY();
        updates.put("startAxis", startAxis);
        updates.put("endAxis", endAxis);
        updates.put("isKing", isKing);
        updates.put("isJump", isJump);
        if (isJump)
            updates.put("jumpedAxis", jumpX + "-" + jumpY);
        addDataToDatabase(updates, documentReference);
    }

    // update list view when player joins to server
    public static void updateListview(ArrayList<String> roomsList, ListView listView, Context appContext) {
        CollectionReference roomsRef = FirebaseFirestore.getInstance().collection(ROOMSPATH);
        roomsUpdaterViewListener = roomsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }
                roomsList.clear();
                for (QueryDocumentSnapshot doc : value) {
                    if (!doc.getId().equals(playerName)) {
                        roomsList.add(doc.getId());
                    }

                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(appContext, android.R.layout.simple_list_item_1, roomsList);
                listView.setAdapter(adapter);
            }
        });
    }

    // delete all documents in a given collectionReference
    public static void deleteAllDocumentsInCollection(CollectionReference collectionReference) {
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> myListOfDocuments = task.getResult().getDocuments();
                for (DocumentSnapshot doc : myListOfDocuments) {
                    doc.getReference().delete().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "ERROR!!! Failed to delete gameplay documents!", e);
                        }
                    });
                }

            }
        });
    }

    public static void isGameOver(Board board, boolean isBlackTurn) {
        int redPieces = 0;
        int blackPieces = 0;
        boolean canBlackMove = false;
        boolean canRedMove = false;

        for (int i = 0; i < Board.SIZE; i++)
            for (int j = 0; j < Board.SIZE; j++) {
                if (board.getBoardArray()[i][j] != null) {
                    if (board.getBoardArray()[i][j].isBlack()) {
                        blackPieces++;
                        if (board.getBoardArray()[i][j].canMove(board)) {
                            canBlackMove = true;
                        }
                    } else {
                        redPieces++;
                        if (board.getBoardArray()[i][j].canMove(board)) {
                            canRedMove = true;
                        }
                    }

                }
            }

        Log.d(TAG, "CAN RED MOVE: " + canRedMove);
        Log.d(TAG, "CAN BLACK MOVE: " + canBlackMove);
        // black won
        if (redPieces == 0 || (!canRedMove && !isBlackTurn)) {
            // show locally on black's phone that he won
            gameOver(true);
        }

        // red won
        else if (blackPieces == 0 || (!canBlackMove && isBlackTurn)) {
            // show locally on red's phone that he won
            gameOver(false);
        }

    }

    public static void gameOver(boolean isBlack) {

        boolean host = isHost();
        String roomNameBak = roomName;
        String winner;
        DocumentReference gameUpdates = FirebaseFirestore.getInstance().collection(LobbyActivity.ROOMSPATH).document(roomName).collection("gameplay").document("gameUpdates");

        AlertDialog.Builder builder = new AlertDialog.Builder(appContext, AlertDialog.THEME_HOLO_LIGHT);
        builder.setCancelable(false);
        builder.setTitle("Game is Over!");
        builder.setPositiveButton("Return Back To The Lobby", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                appContext.startActivity(new Intent(appContext, LobbyActivity.class));
                ((Activity) appContext).finish(); // finish GameActivity
            }
        });

        // the winner starts to listen for "finish" in gameUpdates.
        // the loser finishes everything he needs, and then uploads "finish" to gameUpdates location.
        // the winner gets the "finish" from the loser and then removes the room completely.

        if (isBlack) {
            // show popup that the host won (roomName = hostname)
            String hostUsername = roomName;
            winner = hostUsername; // winner is host
            builder.setMessage(hostUsername + " has won the game! he is probably better.");
        } else {
            // show popup that the guest won (getGuestUsername())
            String guestUsername;
            if (host) // on the host phone (he doesn't have the guest's username, so he has to get it from db
                guestUsername = getGuestUsername();
            else // on the guest phone (the local username is stored in playerName)
                guestUsername = playerName;

            winner = guestUsername; // winner is guest
            builder.setMessage(guestUsername + " has won the game! he is probably better.");
        }
        AlertDialog gameFinishedDialog;
        gameFinishedDialog = builder.create();
        gameFinishedDialog.show();

        if (isWinner(winner)) {
            // start listening for "finish" message from the loser.
            gameOverListener = gameUpdates.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.w(TAG, "Listen failed.", error);
                        return;
                    }
                    if (snapshot != null && snapshot.exists()) {
                        removeRoom(roomNameBak, snapshot);
                    }
                }
            });

        }


        if (!host) // for the guest
        {

            // change roomName back to guest's name
            roomName = playerName;
            roomRef = FirebaseFirestore.getInstance().collection(ROOMSPATH).document(roomName);

            // remove room listener for guest
            roomListener.remove();
        }

        if (!isWinner(winner)) // if i'm the loser
        {
            // upload "finish" message
            Map<String, Object> updates = new HashMap<>();
            updates.put("finish", true);
            addDataToDatabase(updates, gameUpdates);
        }

        // clean-up stuff
        if (hostMovesUpdatesListener != null)
            hostMovesUpdatesListener.remove();
        if (guestMovesUpdatesListener != null)
            guestMovesUpdatesListener.remove();
    }


    private static void removeRoom(String roomNameBak, DocumentSnapshot snapshot) {
        Boolean isFinish = (Boolean) snapshot.get("finish");
        if (isFinish != null) // loser is finished, remove the room.
        {
            Log.d(TAG, "GOT finish MESSAGE, REMOVING ROOM");
            Map<String, Object> updates = new HashMap<>();
            updates.put("guest", FieldValue.delete()); // mark "guest" field as deletable on the database (remove it)
            updates.put("isInGame", false); // update isInGame to false
            addDataToDatabase(updates, FirebaseFirestore.getInstance().collection(ROOMSPATH).document(roomNameBak));

            deleteAllDocumentsInCollection(gameplayRef); // remove all gameplay documents that the host and guest created (cleaning-up)
        }
    }


    public static boolean getIsBlackTurn() {
        Task<DocumentSnapshot> getTurn = gameplayRef.document("gameUpdates").get();
        while (!getTurn.isComplete()) {
            System.out.println("waiting for getIsBlackTurn");
        }
        if (getTurn.isSuccessful()) {
            DocumentSnapshot isBlackTurnResult = getTurn.getResult();
            Boolean val = (Boolean) isBlackTurnResult.get("isBlackTurn");
            if (val != null)
                return val;
        }
        Log.d(TAG, "Error getting document: ", getTurn.getException());
        throw new IllegalStateException("couldn't get isBlackTurn from db");
    }
}

