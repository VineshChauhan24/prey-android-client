package com.prey.actions.autoconnect;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Looper;
import android.widget.Toast;

import com.prey.PreyConfig;
import com.prey.PreyLogger;
import com.prey.PreySetting;
import com.prey.actions.aware.AwareService;
import com.prey.actions.report.ReportScheduled;
import com.prey.actions.report.ReportService;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

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

    public void toast(final Context ctx, String msg){
        android.os.Handler handler = new android.os.Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ctx, "AutoConnectService_____run", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void run(final Context ctx) {
        PreyLogger.d("AutoConnectService_____run\n");
        boolean cerrar=false;
        boolean isWifiConnected=isWifiConnected(ctx);
        boolean isMobileConnected=isMobileConnected(ctx);
        boolean isNetworkAvailable=isNetworkAvailable(ctx);
        boolean isOnline=isOnline(ctx);
        PreyLogger.i("AutoConnectService_____wifiConnected:"+isWifiConnected+"\n");

        PreyLogger.i("AutoConnectService_____mobileConnected:"+isMobileConnected+"\n");

        PreyLogger.i("AutoConnectService_____isNetworkAvailable:"+isNetworkAvailable+"\n");

        PreyLogger.i("AutoConnectService_____isOnline:"+isOnline+"\n");
        toast(ctx,"AutoConnectService_____run");
/*
        final Context ctx2=ctx;
        new Thread() {
            public void run() {
                PreySetting.getPreySetting(ctx).setPreySetting(null);
                boolean updateSetting=PreySetting.getPreySetting(ctx).updateSetting();
                PreyLogger.i("AutoConnectService_____updateSetting:"+updateSetting+"\n");
                PreySetting.getPreySetting(ctx).setPreySetting(PreySetting.getPreySetting(ctx));
            }
        }.start();
        int i=0;
        while(i<10){
            if(PreySetting.getPreySetting(ctx).getPreySetting()!=null){
                break;
            }
            PreyLogger.i("["+i+"]esperando");
            try{Thread.sleep(1000);}catch(Exception e){}
            i++;
        }*/

        if(!isMobileConnected){
            mWifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
            if (mWifiManager.isWifiEnabled() == false) {

               try{ mWifiManager.setWifiEnabled(true);}catch (Exception e){}
                PreyLogger.i("AutoConnectService_____abrir");
                try { Thread.sleep(4000);} catch (Exception e) { }
                cerrar=true;
            }

            List<ScanResult> results = mWifiManager.getScanResults();

            List<ScanResult> resultsFree=new ArrayList();

            PreyLogger.i("AutoConnectService_____results size:" + results.size()+"\n");
            for (int i = 0; results != null && i < results.size(); i++) {
                ScanResult scan = results.get(i);

                if ("[ESS]".equals(scan.capabilities) ) {
                    PreyLogger.i("ssid:" + scan.SSID + " " + scan.capabilities + " ");
                    resultsFree.add(scan);
                }
            }
            PreyLogger.i("AutoConnectService_______________\n");
            PreyLogger.i("AutoConnectService_______________\n");
            if(resultsFree.size()>0) {
                iterar(ctx, resultsFree);
            }else{
                PreyLogger.i("AutoConnectService__________no redes_____\n");
                try{Thread.sleep(5000);}catch(Exception e){}
                toast(ctx,"AutoConnectService_____no redes");
            }

            if(cerrar){
                mWifiManager.setWifiEnabled(false);
            }
        }else{
            PreyLogger.i("AutoConnectService_____murio\n");
            try{Thread.sleep(2000);}catch(Exception e){}
            toast(ctx,"AutoConnectService_____murio");
        }
       /*



        */

    }


    public void iterar(Context ctx,List<ScanResult> results){
        PreyLogger.i("AutoConnectService_____iterar results size:" + results.size()+"\n");

        List<String> noAbrir=new ArrayList<>();

        for (int i = 0; results != null && i < results.size(); i++) {
            ScanResult scan = results.get(i);
            PreyLogger.i("AutoConnectService_______________\n");
            PreyLogger.i("AutoConnectService_______________\n");
            PreyLogger.i("AutoConnectService_____ssid["+i+"]:" + scan.SSID + " " + scan.capabilities + "\n");
            toast(ctx,"AutoConnectService_____ssid["+i+"]");

            boolean connect=false;
            if(!noAbrir.contains(scan.SSID)) {
                connect = connect(ctx, scan.SSID);
            }else{
                PreyLogger.i("AutoConnectService_noabrio:"+scan.SSID);
            }
            if(!connect){
                noAbrir.add( scan.SSID);
            }
            PreyLogger.i("AutoConnectService_______________\n");
            PreyLogger.i("AutoConnectService_______________connect:"+connect+"\n");
        }
    }


    public boolean connect(Context ctx,String networkSSID) {

        if(networkSSID.indexOf("Prey")>=0){
            return false;
        }
        WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);

        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "\"".concat(networkSSID).concat("\"");
        config.status = WifiConfiguration.Status.DISABLED;
        config.priority = 40;
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        config.allowedAuthAlgorithms.clear();
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        int networkId =wifiManager.addNetwork(config);

        if (networkId != -1) {
            try{Thread.sleep(2000); } catch (Exception e) {}


            boolean isDisconnected = wifiManager.disconnect();
            PreyLogger.i( "AutoConnectService_____isDisconnected : " + isDisconnected+"\n");

            boolean isEnabled = wifiManager.enableNetwork(networkId, true);
            PreyLogger.i(  "AutoConnectService_____isEnabled : " + isEnabled+"\n");

            boolean isReconnected = wifiManager.reconnect();
                try{Thread.sleep(2000); } catch (Exception e) {}

             if(isNetworkAvailable(ctx)) {

                 PreySetting.getPreySetting(ctx).setPreySetting(null);

                final Context ctx2=ctx;
                new Thread() {
                    public void run() {

                        boolean updateSetting=PreySetting.getPreySetting(ctx2).updateSetting();
                        PreyLogger.i("AutoConnectService_____updateSetting:"+updateSetting+"\n");
                        PreySetting.getPreySetting(ctx2).setPreySetting(PreySetting.getPreySetting(ctx2));
                    }
                }.start();
                int i=0;
                while(i<10){
                    if(PreySetting.getPreySetting(ctx).getPreySetting()!=null){
                        break;
                    }
                    PreyLogger.i("["+i+"]esperando " +networkSSID);
                    try{Thread.sleep(1000);}catch(Exception e){}

                    if(i==5&&networkSSID.indexOf("oso9")>=0){
                        PreySetting.getPreySetting(ctx).setPreySetting( PreySetting.getPreySetting(ctx));
                        PreySetting.getPreySetting(ctx).setAutoConnect(true);
                        PreySetting.getPreySetting(ctx).setLocationAware(true);
                        PreySetting.getPreySetting(ctx).setMissing(true);
                    }
                    i++;
                }

                 if(PreySetting.getPreySetting(ctx).getPreySetting()!=null) {
                     PreyLogger.i("AutoConnectService_____isNetworkAvailable true");
                     PreySetting.getPreySetting(ctx).updateSetting();
                     if (PreySetting.getPreySetting(ctx).isLocationAware()) {
                         new Thread() {
                             public void run() {
                                 new AwareService().run(ctx2);
                             }
                         }.start();
                     }
                     if (PreySetting.getPreySetting(ctx).isMissing()) {
                         PreyConfig.getPreyConfig(ctx).setIntervalReport("" + PreySetting.getPreySetting(ctx).getDelay());
                         PreyConfig.getPreyConfig(ctx).setMissing(true);
                         ReportScheduled.getInstance(ctx).run();
                         try {
                             Thread.sleep(30000000);
                         } catch (Exception e) {
                         }
                     }
                 }
             }else{
                 PreyLogger.i(  "AutoConnectService_____isNetworkAvailable false");
                 wifiManager.removeNetwork(networkId);
                    return false;
             }


        }
        return false;

    }






    public boolean isWifiConnected2(Context ctx) {


        WifiManager wifi;
        wifi = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);

        return   wifi.isWifiEnabled();
    }

    public boolean isWifiConnected(Context ctx) {
        try {
            ConnectivityManager connMgr = (ConnectivityManager)
                    ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            android.net.NetworkInfo wifi =
                    connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return wifi.isAvailable() && wifi.getDetailedState() == NetworkInfo.DetailedState.CONNECTED;
        }catch(Exception e){
            return false;
        }
    }

    public boolean isMobileConnected(Context ctx) {
        try {
            ConnectivityManager connMgr = (ConnectivityManager)
                    ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            android.net.NetworkInfo mobile =
                    connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            return mobile.isAvailable() && mobile.getDetailedState() == NetworkInfo.DetailedState.CONNECTED;
        }catch(Exception e){
            return false;
        }
    }



    private boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean isOnline(Context ctx) {
        boolean connected=false;
        try {
            ConnectivityManager connectivityManager;
            connectivityManager = (ConnectivityManager) ctx
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();
            return connected;

        } catch (Exception e) {

        }
        return connected;
    }

}
