/*******************************************************************************
 * Created by Orlando Aliaga
 * Copyright 2018 Prey Inc. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.actions.aware;

import android.content.Context;

import com.prey.PreyConfig;
import com.prey.PreyLogger;
import com.prey.net.PreyWebServices;

import org.json.JSONObject;

public class AwareConfig {

    private static AwareConfig cachedInstance = null;
    private Context ctx;

    private AwareConfig(Context ctx) {
        this.ctx = ctx;
    }

    public static synchronized AwareConfig getAwareConfig(Context ctx) {
        if (cachedInstance == null) {
            synchronized (AwareConfig.class) {
                if (cachedInstance == null)
                    cachedInstance = new AwareConfig(ctx);
            }
        }
        return cachedInstance;
    }

    public void init() {
        boolean locationAware = PreyConfig.getPreyConfig(ctx).getAware();
        if (locationAware) {
            startAware();
        }
    }

    public void startAware() {
        PreyConfig.getPreyConfig(ctx).setIntervalAware("20");
        AwareScheduled.getInstance(ctx).run();
    }
}