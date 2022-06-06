package com.example.checkers;

import static com.example.checkers.DBUtils.addDataToDatabase;
import static com.example.checkers.DBUtils.isHost;
import static com.example.checkers.DBUtils.getGuestUsername;
import static com.example.checkers.DBUtils.updateListview;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class handles the interaction in the Lobby activity, such as sending a game invite.
 *
 * @author Tal Simhayev
 * @version 1.0
 */
public class LobbyActivity extends AppCompatActivity {
    public static final String TAG = "WaitingRoom";
    public static final String ROOMSPATH = "rooms";
    public static final int PhoneNumOfDeveloper = 99999999;
    public Toolbar toolbar;
    public DrawerLayout drawer;
    public TextView mUsername;
    public TextView mEmail;

    public BroadcastReceiver broadcastReceiver;
    public static DocumentReference roomRef;
    public static ListenerRegistration roomListener;
    public ListenerRegistration hostUpdatesListener;
    public ListenerRegistration guestUpdatesListener;
    public static ListenerRegistration roomsUpdaterViewListener;
    public ListView listView;
    public static String playerName;
    public static String roomName;

    private FirebaseFirestore fStore;

    public DocumentReference hostUpdatesRef;
    public DocumentReference guestUpdatesRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        listView = findViewById(R.id.listViewPlayers);
        ArrayList<String> roomsList = new ArrayList<>();
        fStore = FirebaseFirestore.getInstance();
        broadcastReceiver = new MyBroadcastReceiver(roomsList, listView, getApplicationContext());
        registerBroadcastListener();
        removeFirestorePersistence();

        initNavHeader();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // join an existing room and add yourself as guest
                String otherPlayerName = roomsList.get(position);
                if (!otherPlayerName.equals(playerName)) // if the player is not challenging himself
                {
                    roomName = otherPlayerName;
                    roomRef = fStore.collection(ROOMSPATH).document(roomName);
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("guest", playerName);
                    userData.put("isInGame", true);
                    addDataToDatabase(userData, roomRef);
                    listenForRoomUpdates();
                } else // if the player IS challenging himself, just tell him he can't.
                    Toast.makeText(getApplicationContext(), "Challenge... yourself? :)", Toast.LENGTH_LONG).show();
            }
        });

        updateListview(roomsList, listView, getApplicationContext());
    }

    /**
     * Remove the Firestore persistence, thus disabling getting data from cache (because we need realtime updates during a game).
     */
    public void removeFirestorePersistence() {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        fStore.setFirestoreSettings(settings);
    }


    /**
     * Registers the broadcast listener.
     */
    public void registerBroadcastListener() {
        // According to the
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    /**
     * Unregisters the broadcast listener.
     */
    public void unregisterBroadcastListener() {
        if (broadcastReceiver != null)
            unregisterReceiver(broadcastReceiver);
    }


    /**
     * Contact button handler function. Creates an Intent object with an ACTION_SENDTO (SMS app), to the developer's phone.
     * The user can send feedback to the developer and report issues and possible add-ons.
     */
    public void contactHandler() {
        Uri uri = Uri.parse("smsto:" + PhoneNumOfDeveloper);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", "Your feedback here");
        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "SMS FAILED, please try again later", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Gets the "isInGame" field in the room document, which can be accessed by using roomName.
     *
     * @return "isInGame" field value (Boolean) in the current room path. Throws an IllegalStateException if couldn't retrieve the field.
     */
    // get isInGame value from db
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
        }
        Log.d(TAG, "Error getting document: ", getInGame.getException());
        throw new IllegalStateException("couldn't get isBlackTurn from db");
    }

    /**
     * This function is called when a room gets updated, and checks if isInGame value on the database has changed (means a guest joined the room).
     * if it has then it redirects execution to gameInvitationHandler(), else does nothing.
     */
    public void listenForRoomUpdates() {
        roomListener = roomRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }
                Log.d(TAG, "listenForRoomsUpdates");
                if (snapshot != null && snapshot.exists()) {
                    Boolean isInGame = (Boolean) snapshot.get("isInGame");
                    if (isInGame != null && isInGame) // if a guest joined
                    {
                        gameInvitationHandler();
                    }
                }
            }
        });
    }

    /**
     * Handles a game invite (when a guest joins someone's room) by calling the handleHostInGameInvitation() and handleGuestInGameInvitation().
     */
    private void gameInvitationHandler() {
        String hostUsername = roomName;
        Map<String, Object> gameRequestData = new HashMap<>();
        AlertDialog.Builder gameRequestDialogBuilder = new AlertDialog.Builder(LobbyActivity.this, AlertDialog.THEME_HOLO_LIGHT);
        gameRequestDialogBuilder.setCancelable(false);

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        hostUpdatesRef = roomRef.collection("hostUpdates").document("gameStatus");
        guestUpdatesRef = roomRef.collection("guestUpdates").document("gameStatus");

        if (isHost()) // for the host
            handleHostInGameInvitation(gameRequestDialogBuilder, gameRequestData, vibrator);

        else // for the guest
            handleGuestInGameInvitation(gameRequestDialogBuilder, hostUsername, vibrator);
    }

    /**
     * Handle guest side when a game invite occurs, by showing the invite dialog and start listening to host response.
     *
     * @param gameRequestDialogBuilder The AlertDialog builder to build the "Challenging" dialog in the guest's phone.
     * @param hostUsername             The host username value (of type String)
     * @param vibrator                 The vibrator object to pass to startGame(), if the host accepts the invite.
     */
    public void handleGuestInGameInvitation(AlertDialog.Builder gameRequestDialogBuilder, String hostUsername, Vibrator vibrator) {
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

        AlertDialog gameRequestDialog = gameRequestDialogBuilder.create();

        gameRequestDialog.show();

        // listen for host response
        setListenerForHostUpdates(hostUsername, gameRequestDialog, hostUpdatesRef, vibrator);
    }

    /**
     * Handle host side when a game invite occurs, by showing the invite dialog and start listening for guest cancellation.
     *
     * @param gameRequestDialogBuilder The AlertDialog builder to build the "Challenging" dialog in the host's phone.
     * @param gameRequestData          The host username value (of type String)
     * @param vibrator                 The vibrator object to pass to startGame(), if the host accepts the invite.
     */
    public void handleHostInGameInvitation(AlertDialog.Builder gameRequestDialogBuilder, Map<String, Object> gameRequestData, Vibrator vibrator) {
        String guestUsername = getGuestUsername();

        gameRequestDialogBuilder.setMessage(guestUsername + " has challenged you to a game!");
        gameRequestDialogBuilder.setTitle("You've Been Challenged");
        gameRequestDialogBuilder.setPositiveButton("ACCEPT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "START GAME FOR HOST AND GUEST!");

                gameRequestData.put("startGame", true);

                addDataToDatabase(gameRequestData, hostUpdatesRef);

                // LET'S PLAY!
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

        AlertDialog gameRequestDialog = gameRequestDialogBuilder.create();

        gameRequestDialog.show();

        // listen for guest updates (e.g maybe he canceled the request)
        setListenerForGuestUpdates(guestUsername, gameRequestDialog, guestUpdatesRef);

    }


    /**
     * Host will call this function to listen for guest updates e.g if guest cancelled their invitation.
     *
     * @param guestUsername     The guest's username (String)
     * @param gameRequestDialog The AlertDialog object of the game request. used when host declines the request and then the dialog is dismissed.
     * @param guestUpdatesRef   The DocumentReference of the location to the guest updates.
     */
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

    /**
     * Guest will call this function to listen for host updates when inviting him to a game. The guest will start a game if the host accepts the invite.
     *
     * @param hostUsername      The host username (a String).
     * @param gameRequestDialog The AlertDialog object of the game request. used when host accepts the request and then the dialog is dismissed and a game is started.
     * @param hostUpdatesRef    The DocumentReference to the hostUpdates location in the database.
     * @param vibrator          The vibrator object, used when a game starts (if it is enabled in the Settings).
     */
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
                        if (startGame) { // host confirmed = LET'S PLAY!
                            gameRequestDialog.dismiss();
                            startGame(vibrator);
                        } else { // host declined
                            gameRequestDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Sorry, " + hostUsername + " declined your invitation", Toast.LENGTH_SHORT).show();

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

    /**
     * Start the game. this will be also a vibration if the user didn't disable the option in the settings.
     *
     * @param vibrator The vibrator object, used when a game is started.
     */
    private void startGame(Vibrator vibrator) {
        SharedPreferences settingsPrefs = getSharedPreferences(SETTINGS_PREFS, MODE_PRIVATE);
        boolean isVibrate = settingsPrefs.getBoolean("vibrate", true);

        if (isVibrate) {
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                vibrator.vibrate(500);
            }
        }

        if (!isHost()) // delete the guest's room when starting a game
        {
            DocumentReference guestRoomRef = fStore.collection(ROOMSPATH).document(playerName);
            guestRoomRef.delete();
        }

        Intent intent = new Intent(getApplicationContext(), GameActivity.class);
        startActivity(intent);
    }


    /**
     * init the menu and connect the user to the database when finished.
     */
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
                        contactHandler();
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

    /**
     * Disconnect the user from the database. This removes the room only if the user is not in a game.
     */
    public void disconnectUser() {
        if (isHost())
            if (!getIsInGame())
                roomRef.delete();

    }

    /**
     * Create a room in the database, named playerName.
     * This adds the fields "host" which saves the player's name, and "isInGame" which indicates if the room is taken (in a game)
     */
    public void connectUser() {
        if (playerName != null) {
            roomName = playerName;
            roomRef = fStore.collection(ROOMSPATH).document(roomName);
            Map<String, Object> userData = new HashMap<>();
            userData.put("host", playerName);
            userData.put("isInGame", false);
            addDataToDatabase(userData, roomRef);
            listenForRoomUpdates();
        }

    }

    /**
     * When pressing the back button in the phone, the menu will collapse.
     */
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    /**
     * When the activity is in the "STOP" state (onStop() is called), do clean-ups by removing the listeners, as well as guest and host updates.
     */
    @Override
    public void onStop() {
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

    /**
     * When a application is being destroyed, unregister the broadcast listener and disconnect the user from the database.
     * It is important to mention that this callback is not called always.
     *
     * @see <a href="https://stackoverflow.com/questions/4449955/activity-ondestroy-never-called"> onDestroy is never called </a>
     */
    @Override
    public void onDestroy() {
        unregisterBroadcastListener();
        disconnectUser();

        super.onDestroy();

    }
}