package com.example.checkers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.Map;
import java.util.Objects;

public class WaitingRoomActivity extends AppCompatActivity {
    public static final String TAG = "WaitingRoom";
    public static final String ROOMSPATH = "rooms";
    protected Toolbar toolbar;
    protected DrawerLayout drawer;
    protected TextView mUsername;
    protected TextView mEmail;

    protected DocumentReference roomRef;
    private FirebaseFirestore fStore;
    public ListView listView;
    public String playerName;
    public String roomName;
    public AlertDialog gameRequestDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        listView = findViewById(R.id.listViewPlayers);
        ArrayList<String> roomsList = new ArrayList<>();
        fStore = FirebaseFirestore.getInstance();

        initNavHeader();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // join an existing room and add yourself as guest
                roomName = roomsList.get(position);
                roomRef = fStore.collection(ROOMSPATH).document(roomName);
                Map<String, Object> userData = new HashMap<>();
                userData.put("guest", playerName);
                userData.put("isInGame", true);
                addUserdataToDatabase(userData);
                listenForRoomUpdates();
                Log.d(TAG, "SOMEONE JOINED !!!!!");
            }
        });

        updateListview(roomsList);
    }

    private boolean getIsInGame() {
        Task<DocumentSnapshot> getInGame = fStore.collection(ROOMSPATH).document(roomName).get();
        while (!getInGame.isComplete()) {
            System.out.println("waiting for isInGame value");
        }

        if (getInGame.isSuccessful()) {
            DocumentSnapshot isInGameVal = getInGame.getResult();
            return (boolean) isInGameVal.get("isInGame");
        } else
            Log.d(TAG, "Error getting document: ", getInGame.getException());

        return false;

    }

    private boolean getIsHost() {
        Log.d(TAG, "playerName = " +playerName + ";; roomName = " + roomName);
        return playerName.equals(roomName);
    }

    private void updateListview(ArrayList<String> roomsList) {
        CollectionReference roomsRef = fStore.collection(ROOMSPATH);
        roomsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, roomsList);
                listView.setAdapter(adapter);
            }
        });
    }

    // when a room gets updated, check what got updated/added and react accordingly
    public void listenForRoomUpdates() {
        roomRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                /*
                 if (only host in room)
                      just ignore it
                 else    // guest and host is in room now
                      send_game_request_to_host()
                      if (confirmed)
                         start_game_for_host_and_guest()
                      else // host declined
                          send_to_guest_that_host_declined()

                */

                // sum up:
                // take the needed fields from the database, and change isHost and isInGame accordingly to the local values (playerName, roomName...)

                Log.d(TAG, "ROOM UPDATED!");
                if (getIsInGame()) // if a guest joined
                {
                    Log.d(TAG, "a guest joined! :D");
                    sendGameRequestToHost();
                    // show_for_guest_challenging_state
                }
            }
        });
    }

    // display the alertDialog on the host's (only!) phone
    private void sendGameRequestToHost() {
        // *** check if this is the host's phone by comparing the roomName (which is the host's username) to playerName
        String guestUsername = playerName; // playerName is the name of the current phone's user, and that user wants to play with the host, which makes him a guest in the room.
        Log.d(TAG, "isHost: " + getIsHost());
        if (getIsHost()) // if it's not the guest' phone
        {
            AlertDialog.Builder gameRequestBuilder = new AlertDialog.Builder(WaitingRoomActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            gameRequestBuilder.setTitle("Challenge Request");
            gameRequestBuilder.setMessage("You've been challenged by " + guestUsername + "!");
            gameRequestBuilder.setCancelable(false);
            gameRequestBuilder.setPositiveButton("ACCEPT", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d(TAG, "START GAME FOR HOST AND GUEST!");
                    gameRequestDialog.dismiss();
                }
            });
            gameRequestBuilder.setNegativeButton("DECLINE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d(TAG, "HOST DECLINED: SEND 'DECLINED' MESSAGE TO GUEST");
                    gameRequestDialog.dismiss();
                }
            });
            gameRequestDialog = gameRequestBuilder.create();
            gameRequestDialog.show();
        }
    }

    private void addUserdataToDatabase(Map<String, Object> userData) {
        roomRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        roomRef.update(userData);
                    } else {
                        roomRef.set(userData);
                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                }
            }
        });
    }

    public void initNavHeader() {
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.nav_open, R.string.nav_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawer.closeDrawer(GravityCompat.START); // close the menu
                switch (item.getItemId()) {
                    case R.id.nav_settings:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        break;
                    case R.id.nav_scores:
                        startActivity(new Intent(getApplicationContext(), ScoresActivity.class));
                        break;
                    case R.id.nav_logout:
                        FirebaseAuth.getInstance().signOut();
                        listView.setAdapter(null);
                        disconnectUser();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + item.getItemId());
                }
                return false; // return false means unchecked state
            }
        });

        View headerView = nav_view.getHeaderView(0);
        mUsername = headerView.findViewById(R.id.textviewUsername); // referring to the header style
        mEmail = headerView.findViewById(R.id.textviewEmail); // referring to the header style

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();

        String uid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid(); // can't be null cuz we're already in WaitingRoom...
        DocumentReference userRef = fStore.collection("users").document(uid);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    playerName = task.getResult().getString("username");
                    mUsername.setText(playerName);
                    Log.d(TAG, "set username field in the navigation header to: " + playerName);

                    connectUser();

                } else
                    Log.d(TAG, "get() failed with: " + task.getException());
            }
        });
        mEmail.setText(Objects.requireNonNull(fAuth.getCurrentUser()).getEmail()); // again, can't be null...
    }

    public void disconnectUser() {
        if (getIsHost())
            if (!getIsInGame())
                roomRef.delete();

    }

    // create a room with your name (as host)
    public void connectUser() {
        roomName = playerName;
        roomRef = fStore.collection(ROOMSPATH).document(roomName);
        Map<String, Object> userData = new HashMap<>();
        userData.put("host", playerName);
        userData.put("isInGame", false);
        addUserdataToDatabase(userData);
        listenForRoomUpdates();
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        startActivity(new Intent(getApplicationContext(), StartGameActivity.class));
        //super.onBackPressed(); // this disables back button by not calling the super function
    }

//    @Override
//    protected void onPause() {
//        // remove player from database because he is no longer online.
//        Log.d(TAG, "ONSTOP: USER DISCONNECTED");
//        disconnectUser();
//
//        super.onPause();
//    }

    //@Override
//    protected void onResume() {
//        // rejoin player to database because he came back online.
//        Log.d(TAG, "ONRESUME: USER IS ONLINE AGAIN");
//        connectUser();
//
//
//        super.onResume();
//    }
}