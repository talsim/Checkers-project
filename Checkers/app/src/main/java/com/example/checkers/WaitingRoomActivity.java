package com.example.checkers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WaitingRoomActivity extends AppCompatActivity {
    public static final String TAG = "WaitingRoom";
    protected Toolbar toolbar;
    protected DrawerLayout drawer;
    protected TextView mUsername;
    protected TextView mEmail;

    protected DocumentReference roomRef;
    private FirebaseFirestore fStore;
    public ListView listView;
    public String playerName;
    public String roomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        initNavHeader();

        listView = (ListView) findViewById(R.id.listViewPlayers);
        ArrayList<String> roomsList = new ArrayList<>();
        fStore = FirebaseFirestore.getInstance();
        roomName = "";

        //
        Log.d(TAG, "playname is : " + playerName);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // join an existing room and add yourself as player2
                roomName = roomsList.get(position);
                roomRef = fStore.collection("rooms").document(roomName);
                Map<String, Object> userData = new HashMap<>();
                userData.put("player2", playerName);
                addRoomEventListener();
                roomRef.set(userData);
            }
        });

        addRoomsEventListener(roomsList);
    }

    private void addRoomsEventListener(ArrayList<String> roomsList) {
        CollectionReference roomsRef = fStore.collection("rooms");
        roomsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }
                roomsList.clear();
                for (QueryDocumentSnapshot doc : value){
                    if (!doc.getId().equals(playerName))
                        roomsList.add(doc.getId());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, roomsList);
                listView.setAdapter(adapter);
            }
        });
    }

    public void addRoomEventListener(){
        roomRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Log.d(TAG,"START GAME!!! :)");
            }
        });
    }

    public void initNavHeader()
    {
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
        DocumentReference documentReference = fStore.collection("users").document(uid);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    playerName = task.getResult().getString("username");
                    mUsername.setText(playerName);
                    Log.d(TAG, "set username field in the navigation header to: " + playerName);

                    //
                    roomName = playerName;
                    roomRef = fStore.collection("rooms").document(roomName);
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("player1", playerName);
                    addRoomEventListener();
                    roomRef.set(userData);
                }
                else
                    Log.d(TAG, "get() failed with: " + task.getException());
            }
        });
        mEmail.setText(Objects.requireNonNull(fAuth.getCurrentUser()).getEmail()); // again, can't be null...
    }


    @Override
    public void onBackPressed()
    {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        startActivity(new Intent(getApplicationContext(), StartGameActivity.class));
        //super.onBackPressed(); // this disables back button by not calling the super function
    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}