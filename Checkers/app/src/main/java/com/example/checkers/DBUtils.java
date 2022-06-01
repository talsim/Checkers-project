package com.example.checkers;


import static com.example.checkers.OnClickListenerForPieceMoves.gameplayRef;
import static com.example.checkers.LobbyActivity.ROOMSPATH;
import static com.example.checkers.LobbyActivity.playerName;
import static com.example.checkers.LobbyActivity.roomName;
import static com.example.checkers.LobbyActivity.roomsUpdaterViewListener;

import android.content.Context;
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
    public static boolean isHost(String playerName, String roomName) {
        return playerName.equals(roomName);
    }

    // get from db
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

    // get blackTurn and upload it to db
    public static void updateBlackTurnInDb(boolean blackTurn, CollectionReference gameplayRef) {
        Map<String, Object> gameUpdates = new HashMap<>();
        gameUpdates.put("isBlackTurn", blackTurn);
        addDataToDatabase(gameUpdates, gameplayRef.document("gameUpdates"));
    }

    // upload Piece locations by the correct format
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


}
