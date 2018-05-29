package com.prey.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.prey.PreyLogger;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {


        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            PreyLogger.d("NetworkCheckReceiver NetworkCheckReceiver invoked...");


            boolean noConnectivity = intent.getBooleanExtra(
                    ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

            if (!noConnectivity) {
                PreyLogger.d("NetworkCheckReceiver connected");
            }
            else
            {
                PreyLogger.d("NetworkCheckReceiver disconnected");
            }
        }


        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isAvailable() || mobile.isAvailable()) {
            // Do something

            PreyLogger.d("Network Available  Flag No 1");
        }else{
            PreyLogger.d("Network Available   Flag No 2");
        }
    }
}