package com.example.checkers;



import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

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
}
