package com.example.checkers;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.ListView;
import android.widget.Toast;

import static com.example.checkers.DBUtils.updateListview;
import static com.example.checkers.LobbyActivity.roomsUpdaterViewListener;

import java.util.ArrayList;

/**
 * This class handles network changes on the user's phone.
 *
 * @author Tal Simhayev
 * @version 1.0
 */
public class MyBroadcastReceiver extends android.content.BroadcastReceiver {

    private final ArrayList<String> roomsList;
    private final ListView listView;
    private final Context appContext;

    public MyBroadcastReceiver(ArrayList<String> roomsList, ListView listView, Context appContext) {
        // for updateListView when no internet.
        this.roomsList = roomsList;
        this.listView = listView;
        this.appContext = appContext;
    }

    /**
     * A callback function for network changes.
     * Check if the phone is connected to the internet, and if it's not connected, then show a Toast and stop listening to new players joining the server.
     *
     * @param context The context of the activity.
     * @param intent  The intent object used.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // an Intent broadcast.
        if (isOnline(context)) {
            updateListview(this.roomsList, this.listView, this.appContext);
        } else {
            Toast.makeText(context, "Lost Internet Connection.", Toast.LENGTH_SHORT).show();
            roomsUpdaterViewListener.remove(); // removing the listener to avoid network issues
        }

    }

    /**
     * Check if there is an internet connection or not.
     *
     * @param context The context of the activity.
     * @return true if there is an internet connection, false otherwise.
     */
    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }
}