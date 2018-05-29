package com.prey;

import android.content.Context;

import com.prey.actions.aware.AwareConfig;
import com.prey.actions.aware.AwareScheduled;
import com.prey.net.PreyWebServices;

import org.json.JSONObject;

public class PreySetting {

    boolean autoConnect;
    boolean locationAware;


    private static PreySetting cachedInstance = null;
    private Context ctx;

    private PreySetting(Context ctx) {
        this.ctx = ctx;
    }

    public static synchronized PreySetting getPreySetting(Context ctx) {
        if (cachedInstance == null) {
            synchronized (PreySetting.class) {
                if (cachedInstance == null)
                    cachedInstance = new PreySetting(ctx);
            }
        }
        return cachedInstance;
    }

    public void setSetting() {
        locationAware = false;
        autoConnect = false;
        try {
            JSONObject jsnobject = PreyWebServices.getInstance().getStatus(ctx);
            if (jsnobject != null) {
                PreyLogger.d("jsnobject :" + jsnobject);

                JSONObject jsnobjectSettings = jsnobject.getJSONObject("settings");
                PreyLogger.d("jsnobjectSettings :" +jsnobjectSettings);

                JSONObject jsnobjectGlobal = jsnobjectSettings.getJSONObject("global");
                PreyLogger.d("jsnobjectGlobal :" +jsnobjectGlobal);
                autoConnect = jsnobjectGlobal.getBoolean("auto_connect");
                PreyLogger.d("auto_connect :" +autoConnect);
                PreyConfig.getPreyConfig(ctx).setAutoConnect(autoConnect);

                JSONObject jsnobjectLocal = jsnobjectSettings.getJSONObject("local");
                PreyLogger.d("jsnobjectLocal :" +jsnobjectLocal);
                locationAware = jsnobjectLocal.getBoolean("location_aware");
                PreyLogger.d("locationAware :" + locationAware);
                PreyConfig.getPreyConfig(ctx).setAware(locationAware);
            } else {
                PreyLogger.d("getLocationAware null");
            }
        } catch (Exception e) {
            PreyLogger.e("Error:" + e.getMessage(), e);
        }

    }

    public boolean isAutoConnect() {
        return autoConnect;
    }



    public boolean isLocationAware() {
        return locationAware;
    }


}
