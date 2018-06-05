package com.prey;

import android.content.Context;

import com.prey.actions.aware.AwareConfig;
import com.prey.actions.aware.AwareScheduled;
import com.prey.net.PreyWebServices;

import org.json.JSONObject;

public class PreySetting {

    boolean autoConnect;
    boolean locationAware;
    boolean missing;
    int delay;
    PreySetting preySetting;

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

    public PreySetting getPreySetting() {
        return preySetting;
    }

    public void setPreySetting(PreySetting preySetting) {
        this.preySetting = preySetting;
    }

    public boolean updateSetting() {
        locationAware = false;
        autoConnect = false;
        missing = false;
        delay=10;
        try {
            JSONObject jsnobject = PreyWebServices.getInstance().getStatus(ctx);
            if (jsnobject != null) {
                PreyLogger.d("jsnobject :" + jsnobject);


                JSONObject jsnobjectStatus = jsnobject.getJSONObject("status");
                PreyLogger.d("jsnobjectStatus :" +jsnobjectStatus);
                missing = jsnobjectStatus.getBoolean("missing");
                delay =jsnobjectStatus.getInt("delay");

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


                return true;
            } else {
                PreyLogger.d("getLocationAware null");
                return false;
            }
        } catch (Exception e) {
            PreyLogger.e("Error:" + e.getMessage(), e);
            return false;
        }

    }

    public boolean isAutoConnect() {
        return autoConnect;
    }



    public boolean isLocationAware() {
        return locationAware;
    }

    public boolean isMissing() {
        return missing;
    }

    public int getDelay() {
        return delay;
    }

    public void setAutoConnect(boolean autoConnect) {
        this.autoConnect = autoConnect;
    }

    public void setLocationAware(boolean locationAware) {
        this.locationAware = locationAware;
    }

    public void setMissing(boolean missing) {
        this.missing = missing;
    }
}
