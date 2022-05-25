package com.example.checkers;

import static com.example.checkers.DatabaseUtils.addDataToDatabase;
import static com.example.checkers.DatabaseUtils.isHost;
import static com.example.checkers.DatabaseUtils.getGuestUsername;
import static com.example.checkers.DatabaseUtils.updateListview;
import static com.example.checkers.SettingsActivity.SETTINGS_PREFS;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
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

    BroadcastReceiver broadcastReceiver;
    public DocumentReference roomRef;
    protected ListenerRegistration roomListener;
    protected ListenerRegistration hostUpdatesListener;
    protected ListenerRegistration guestUpdatesListener;
    protected static ListenerRegistration roomsUpdaterViewListener;
    private FirebaseFirestore fStore;
    public ListView listView;
    public static String playerName;
    public static String roomName;
    public DocumentReference hostUpdatesRef;
    public DocumentReference guestUpdatesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        listView = findViewById(R.id.listViewPlayers);
        ArrayList<String> roomsList = new ArrayList<>();
        fStore = FirebaseFirestore.getInstance();
        broadcastReceiver = new MyBroadcastReceiver(roomsList, listView, getApplicationContext());
        registerBroadcastListener();

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
                addDataToDatabase(userData, roomRef);
                listenForRoomUpdates();
            }
        });

        updateListview(roomsList, listView, getApplicationContext());
    }

    public void registerBroadcastListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public void unregisterBroadcastListener() {
        if (broadcastReceiver != null)
            unregisterReceiver(broadcastReceiver);
    }


    // Contact button handler function
    public void ContactHandler() {
        Uri uri = Uri.parse("smsto:" + 99999999);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", "Your feedback here");
        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "SMS FAILED, please try again later", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean getIsInGame() {
        Task<DocumentSnapshot> getInGame = fStore.collection(ROOMSPATH).document(roomName).get();
        while (!getInGame.isComplete()) {
            System.out.println("waiting for isInGame value");
        }

        if (getInGame.isSuccessful()) {
            DocumentSnapshot isInGameVal = getInGame.getResult();
            Boolean val = (Boolean) isInGameVal.get("isInGame");
            if (val != null)
                return val;
        } else
            Log.d(TAG, "Error getting document: ", getInGame.getException());

        return false;

    }


    // when a room gets updated, check what got updated/added and react accordingly
    public void listenForRoomUpdates() {
        roomListener = roomRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                /*
                            ** pseudo-code **

                 if (only host in room)
                      just ignore it
                 else    // guest and host is in room now
                      send_game_request_to_host()
                      if (confirmed)
                         start_game_for_host_and_guest()
                      else // host declined
                          send_to_guest_that_host_declined()

                */

                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }
                Log.d(TAG, "listenForRoomsUpdates");
                if (getIsInGame()) // if a guest joined
                {
                    Log.d(TAG, "sending request to host");
                    gameInvitationHandler();
                }
            }
        });
    }

    private void gameInvitationHandler() {
        // *** check if this is the host's phone by comparing the roomName (which is the host's username) to playerName
        String hostUsername = roomName;
        Map<String, Object> gameRequestData = new HashMap<>();
        AlertDialog gameRequestDialog;
        AlertDialog.Builder gameRequestDialogBuilder = new AlertDialog.Builder(WaitingRoomActivity.this, AlertDialog.THEME_HOLO_LIGHT);
        gameRequestDialogBuilder.setCancelable(false);

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        hostUpdatesRef = roomRef.collection("hostUpdates").document("gameStatus");
        guestUpdatesRef = roomRef.collection("guestUpdates").document("gameStatus");


        if (isHost(playerName, roomName)) // for the host
        {
            String guestUsername = getGuestUsername(roomRef);

            gameRequestDialogBuilder.setMessage(guestUsername + " has challenged you to a game!");
            gameRequestDialogBuilder.setTitle("You've Been Challenged");
            gameRequestDialogBuilder.setPositiveButton("ACCEPT", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d(TAG, "START GAME FOR HOST AND GUEST!");

                    gameRequestData.put("startGame", true);

                    addDataToDatabase(gameRequestData, hostUpdatesRef);

                    // LET'S PLAYYYYYY!!!!!!!!!
                    startGame(vibrator);
                }
            });
            gameRequestDialogBuilder.setNegativeButton("DECLINE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d(TAG, "HOST DECLINED: SEND 'DECLINED' MESSAGE TO GUEST");

                    gameRequestData.put("startGame", false);
                    addDataToDatabase(gameRequestData, hostUpdatesRef);

                    // remove guest now because host doesn't want to play with him
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("guest", FieldValue.delete()); // mark "guest" field as deletable on the database (remove it)
                    updates.put("isInGame", false); // update isInGame to false
                    addDataToDatabase(updates, roomRef);


                }
            });

            gameRequestDialog = gameRequestDialogBuilder.create();

            // listen for guest updates (e.g maybe he canceled the request)
            setListenerForGuestUpdates(guestUsername, gameRequestDialog, guestUpdatesRef);


        } else // for the guest
        {
            gameRequestDialogBuilder.setMessage("Challenging " + hostUsername + "...");
            gameRequestDialogBuilder.setTitle("Challenge Sent");
            gameRequestDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d(TAG, "GUEST CANCELED: SEND 'CANCELED' MESSAGE TO HOST");

                    Map<String, Object> cancelRequestData = new HashMap<>();
                    cancelRequestData.put("canceled", true);
                    addDataToDatabase(cancelRequestData, guestUpdatesRef);

                    // remove room listener for guest
                    roomListener.remove();

                    // change roomName back to guest's name because he canceled
                    roomName = playerName;
                    roomRef = fStore.collection(ROOMSPATH).document(roomName);

                    // remove the guest from the room because he canceled - this is managed in setListenerForGuestUpdates()

                }
            });

            gameRequestDialog = gameRequestDialogBuilder.create();


            // listen for host response
            setListenerForHostUpdates(hostUsername, gameRequestDialog, hostUpdatesRef, vibrator);
        }


        gameRequestDialog.show();

    }


    private void setListenerForGuestUpdates(String guestUsername, AlertDialog gameRequestDialog, DocumentReference guestUpdatesRef) {
        guestUpdatesListener = guestUpdatesRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    Boolean isCanceled = (Boolean) snapshot.get("canceled");
                    if (isCanceled != null)
                        if (isCanceled) { // guest canceled
                            gameRequestDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Sorry, " + guestUsername + " cancelled their invitation.", Toast.LENGTH_SHORT).show();

                            // remove gameStatus document in guestUpdates
                            guestUpdatesRef.delete();

                            // remove the guest from the room because he canceled
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("guest", FieldValue.delete()); // mark "guest" field as deletable on the database (removes it)
                            updates.put("isInGame", false); // update isInGame to false
                            addDataToDatabase(updates, roomRef);
                        }
                }
            }
        });
    }

    private void setListenerForHostUpdates(String hostUsername, AlertDialog gameRequestDialog, DocumentReference hostUpdatesRef, Vibrator vibrator) {
        hostUpdatesListener = hostUpdatesRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    Boolean startGame = (Boolean) snapshot.get("startGame");
                    if (startGame != null)
                        if (startGame) { // host confirmed = LET'S FUCKING PLAYYYY!!!!
                            gameRequestDialog.dismiss();
                            startGame(vibrator);
                        } else { // host declined
                            gameRequestDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "" + hostUsername + "" + " declined the game request", Toast.LENGTH_SHORT).show();

                            // remove gameStatus document in hostUpdates
                            hostUpdatesRef.delete();

                            // change roomName back to guest's name
                            roomName = playerName;
                            roomRef = fStore.collection(ROOMSPATH).document(roomName);

                            // remove room listener for guest
                            roomListener.remove();
                        }
                }
            }
        });
    }

    private void startGame(Vibrator v) {
        SharedPreferences settingsPrefs = getSharedPreferences(SETTINGS_PREFS, MODE_PRIVATE);
        boolean isVibrate = settingsPrefs.getBoolean("vibrate", true);

        if (isVibrate) {
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(500);
            }
        }

        /*if (!isHost(playerName, roomName)) // delete the guest's room when starting a game
        {
            DocumentReference guestRoomRef = fStore.collection(ROOMSPATH).document(playerName);
            guestRoomRef.delete();
        }*/

        Intent intent = new Intent(getApplicationContext(), GameActivity.class);
        intent.putExtra("roomName", roomName);
        startActivity(intent);
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
                    case R.id.nav_feedback:
                        ContactHandler();
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
        if (isHost(playerName, roomName))
            if (!getIsInGame())
                roomRef.delete();

    }

    // create a room with your name (as host)
    public void connectUser() {
        if (playerName != null) {
            Log.d(TAG, "from connectUser: playerName = " + playerName);
            roomName = playerName;
            roomRef = fStore.collection(ROOMSPATH).document(roomName);
            Map<String, Object> userData = new HashMap<>();
            userData.put("host", playerName);
            userData.put("isInGame", false);
            addDataToDatabase(userData, roomRef);
            listenForRoomUpdates();
        }

    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        startActivity(new Intent(getApplicationContext(), GameActivity.class));
        //super.onBackPressed(); // this disables back button by not calling the super function
    }

    @Override
    protected void onStop() {
        if (roomListener != null)
            roomListener.remove();
        if (guestUpdatesListener != null)
            guestUpdatesListener.remove();
        if (hostUpdatesListener != null)
            hostUpdatesListener.remove();
        if (roomsUpdaterViewListener != null)
            roomsUpdaterViewListener.remove();
        if (guestUpdatesRef != null)
            guestUpdatesRef.delete();
        if (hostUpdatesRef != null)
            hostUpdatesRef.delete();
        super.onStop();
    }

    //    @Override
//    protected void onPause() {
//        // remove player from database because he is no longer online.
//        Log.d(TAG, "ONSTOP: USER DISCONNECTED");
//        disconnectUser();
//
//        super.onPause();
//    }

    @Override
    protected void onResume() {
        // rejoin player to database because he came back online.
        Log.d(TAG, "ONRESUME: USER IS ONLINE AGAIN");
        connectUser();


        super.onResume();
    }

    @Override
    protected void onDestroy() {
        unregisterBroadcastListener();

        super.onDestroy();

    }
}