/*******************************************************************************
 * Created by Carlos Yaconi
 * Copyright 2015 Prey Inc. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey;

import android.app.Application;
import android.content.Context;

import com.prey.actions.aware.AwareController;
import com.prey.actions.aware.AwareScheduled;
import com.prey.actions.fileretrieval.FileretrievalController;
import com.prey.actions.geofences.GeofenceController;
import com.prey.actions.report.ReportScheduled;
import com.prey.backwardcompatibility.FroyoSupport;
import com.prey.net.PreyWebServices;

import java.util.Date;

public class PreyApp extends Application {

    public long mLastPause;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            mLastPause = 0;
            PreyLogger.d("__________________");
            PreyLogger.i("Application launched!");
            PreyLogger.d("__________________");

           // PreyConfig.getPreyConfig(this).setDeviceId("b48d38");

            boolean chromium=getPackageManager().hasSystemFeature("org.chromium.arc.device_management");
            PreyLogger.d("chromium:"+chromium);

            String deviceKey = PreyConfig.getPreyConfig(this).getDeviceId();

            PreyLogger.d("InstallationDate:" + PreyConfig.getPreyConfig(this).getInstallationDate());
            if (PreyConfig.getPreyConfig(this).getInstallationDate() == 0) {
                PreyConfig.getPreyConfig(this).setInstallationDate(new Date().getTime());
                PreyWebServices.getInstance().sendEvent(this, PreyConfig.ANDROID_INIT);
            }
            String sessionId = PreyUtils.randomAlphaNumeric(16);
            PreyLogger.d("#######sessionId:" + sessionId);
            PreyConfig.getPreyConfig(this).setSessionId(sessionId);
            String PreyVersion = PreyConfig.getPreyConfig(this).getPreyVersion();
            String preferencePreyVersion = PreyConfig.getPreyConfig(this).getPreferencePreyVersion();
            PreyLogger.d("PreyVersion:" + PreyVersion+" preferencePreyVersion:"+preferencePreyVersion);
            boolean missing=PreyConfig.getPreyConfig(this).isMissing();
            if (PreyVersion.equals(preferencePreyVersion)) {
                PreyConfig.getPreyConfig(this).setPreferencePreyVersion(PreyVersion);
                PreyWebServices.getInstance().sendEvent(this, PreyConfig.ANDROID_VERSION_UPDATED);
            }
            if (deviceKey != null && deviceKey != "") {
                PreyConfig.getPreyConfig(this).registerC2dm();
                new Thread() {
                    public void run() {
                        /*
                        String error="";
                        Context ctx=getApplicationContext();
                        try {
                            PreyConfig.getPreyConfig(ctx).unregisterC2dm(false); } catch (Exception e) { error = e.getMessage();}
                        try {   PreyConfig.getPreyConfig(ctx).setSecurityPrivilegesAlreadyPrompted(false);} catch (Exception e) {}


                        try {   PreyConfig.getPreyConfig(ctx).setProtectAccount(false);} catch (Exception e) {error = e.getMessage();}
                        try {   PreyConfig.getPreyConfig(ctx).setProtectPrivileges(false);} catch (Exception e) {error = e.getMessage();}
                        try {   PreyConfig.getPreyConfig(ctx).setProtectTour(false);} catch (Exception e) {error = e.getMessage();}
                        try {   PreyConfig.getPreyConfig(ctx).setProtectReady(false);} catch (Exception e) {error = e.getMessage();}
                        try {   PreyConfig.getPreyConfig(ctx).setEmail("");} catch (Exception e) {error = e.getMessage();}

                        try {
                            FroyoSupport fSupport = FroyoSupport.getInstance(ctx);
                            if (fSupport.isAdminActive()) {
                                fSupport.removeAdminPrivileges();
                            }
                        } catch (Exception e) {}

                        try {
                            GeofenceController.getInstance().deleteAllZones(ctx);
                        } catch (Exception e) {}

                        try {
                            FileretrievalController.getInstance().deleteAll(ctx);
                        } catch (Exception e) {}

                        try {  PreyWebServices.getInstance().deleteDevice(ctx);} catch (Exception e) {error = e.getMessage();}
                        try {    PreyConfig.getPreyConfig(ctx).wipeData();} catch (Exception e) {error = e.getMessage();}
*/

                        //GeofenceController.getInstance().init(getApplicationContext());
                    }
                }.start();
                new Thread() {
                    public void run() {
                        FileretrievalController.getInstance().run(getApplicationContext());
                    }
                }.start();
                /*
                if (missing) {
                    if (PreyConfig.getPreyConfig(this).getIntervalReport() != null && !"".equals(PreyConfig.getPreyConfig(this).getIntervalReport())) {
                        ReportScheduled.getInstance(this).run();
                    }
                }*/
                new Thread() {
                    public void run() {
                        if (PreyConfig.getPreyConfig(getApplicationContext()).getAware()) {
                            AwareScheduled.getInstance(getApplicationContext()).run();

                        }
                    }
                }.start();
            }
        } catch (Exception e) {
            PreyLogger.e("Error PreyApp:" + e.getMessage(), e);
        }
    }

}
