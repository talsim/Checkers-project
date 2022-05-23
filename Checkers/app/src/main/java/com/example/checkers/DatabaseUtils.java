package com.example.checkers;



import static com.example.checkers.MyOnClickListenerForPieceMoves.gameplayRef;
import static com.example.checkers.WaitingRoomActivity.playerName;
import static com.example.checkers.WaitingRoomActivity.roomName;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class DatabaseUtils {
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

    public static boolean isHost(String playerName, String roomName) {
        return playerName.equals(roomName);
    }

    public static String getGuestUsername(DocumentReference roomRef) {
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

    public static void updateBlackTurnInDb(boolean blackTurn, CollectionReference gameplayRef) {
        Map<String, Object> gameUpdates = new HashMap<>();
        gameUpdates.put("isBlackTurn", blackTurn);
        addDataToDatabase(gameUpdates, gameplayRef.document("gameUpdates"));
    }

    public static void uploadPieceLocationToDb(Move move, boolean isJump, int jumpX, int jumpY, boolean isKing) {
        DocumentReference documentReference;
        if (isHost(playerName, roomName))
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
}
