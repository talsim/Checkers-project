package com.example.checkers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.ListView;
import android.widget.Toast;

import static com.example.checkers.DatabaseUtils.updateListview;
import static com.example.checkers.WaitingRoomActivity.roomsUpdaterViewListener;

import java.util.ArrayList;

public class MyBroadcastReceiver extends BroadcastReceiver {

    protected ArrayList<String> roomsList;
    protected ListView listView;
    protected Context appContext;

    public MyBroadcastReceiver(ArrayList<String> roomsList, ListView listView, Context appContext)
    {
        this.roomsList = roomsList;
        this.listView = listView;
        this.appContext = appContext;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // an Intent broadcast.
        if (isOnline(context))
        {
            updateListview(this.roomsList, this.listView, this.appContext);
        }
        else
        {
            Toast.makeText(context, "Lost Internet Connection.", Toast.LENGTH_SHORT).show();
            roomsUpdaterViewListener.remove(); // removing the listener to avoid network issues
        }

    }

    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }
}