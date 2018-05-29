package com.prey.actions.autoconnect;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.prey.PreyLogger;

import java.util.List;

public class AutoConnectService extends IntentService {
    private WifiManager mWifiManager;
    public AutoConnectService() {
        super("autoConnectService");
    }

    public AutoConnectService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        run(getApplicationContext());
        stopSelf();
    }

    public void run(Context ctx) {
        PreyLogger.d("AutoConnectService run");
        boolean cerrar=false;
        boolean wifiConnected=false;
        boolean mobileConnected=false;
        updateConnectedFlags();
        PreyLogger.i("wifiConnected:"+wifiConnected);
        PreyLogger.i("mobileConnected:"+mobileConnected);


        mWifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        if (mWifiManager.isWifiEnabled() == false) {

            mWifiManager.setWifiEnabled(true);
            try { Thread.sleep(4000);} catch (Exception e) { }
            cerrar=true;
        }
        List<ScanResult> results = mWifiManager.getScanResults();
        PreyLogger.i("results size:" + results.size());
        for (int i = 0; results != null && i < results.size(); i++) {
            ScanResult scan = results.get(i);
            PreyLogger.i("ssid:" + scan.SSID + " " + scan.capabilities + " ");
            if ("[ESS]".equals(scan.capabilities)&&scan.SSID.indexOf("Prey")<0) {
                PreyLogger.i("ssid:" + scan.SSID + " " + scan.capabilities + " ");

                //WifiConfiguration conf = new WifiConfiguration();
                String ssid =   scan.SSID  ;
                //wifiManager.addNetwork(conf);
                //connect(ctx,ssid);
                // break;
            }
        }
        if(cerrar){
            mWifiManager.setWifiEnabled(false);
        }

    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    boolean wifiConnected=false;
    boolean mobileConnected=false;

    public void updateConnectedFlags() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            wifiConnected = false;
            mobileConnected = false;
        }
    }


}
