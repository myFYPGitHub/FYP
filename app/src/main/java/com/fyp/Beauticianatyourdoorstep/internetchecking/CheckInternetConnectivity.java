package com.fyp.Beauticianatyourdoorstep.internetchecking;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public final class CheckInternetConnectivity {

    public static boolean isInternetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
