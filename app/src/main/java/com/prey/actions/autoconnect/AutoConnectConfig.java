package com.prey.actions.autoconnect;

import android.content.Context;

import com.prey.PreyConfig;
import com.prey.actions.aware.AwareConfig;
import com.prey.actions.aware.AwareScheduled;

public class AutoConnectConfig {

    private static AutoConnectConfig cachedInstance = null;
    private Context ctx;

    private AutoConnectConfig(Context ctx) {
        this.ctx = ctx;
    }

    public static synchronized AutoConnectConfig getAutoConnectConfig(Context ctx) {
        if (cachedInstance == null) {
            synchronized (AutoConnectConfig.class) {
                if (cachedInstance == null)
                    cachedInstance = new AutoConnectConfig(ctx);
            }
        }
        return cachedInstance;
    }

    public void init() {
        boolean autoConnect = PreyConfig.getPreyConfig(ctx).getAutoConnect();
        if (autoConnect) {
            startAutoConnect();
        }
    }

    public void startAutoConnect() {
        //PreyConfig.getPreyConfig(ctx).setIntervalAutoConnect("5");
        AutoConnectScheduled.getInstance(ctx).run();
    }
}
