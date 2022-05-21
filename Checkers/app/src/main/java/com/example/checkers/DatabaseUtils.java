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

    // wrapper function for update and set functions in
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
}
