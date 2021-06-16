package com.firestore.changelogmine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkChangeReceiver extends BroadcastReceiver {

    public static NetworkReceiverListener networkReceiverListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (networkReceiverListener != null) {
            networkReceiverListener.onNetworkConnectionChanged(isConnectedOrConnecting(context));
        }
    }

    private Boolean isConnectedOrConnecting(Context context){
        ConnectivityManager connectionManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    interface NetworkReceiverListener {
        void onNetworkConnectionChanged(Boolean isConnected);
    }
}