package com.prey.actions.aware;

import android.app.IntentService;
import android.content.Intent;

import com.prey.PreyConfig;
import com.prey.PreyLogger;
import com.prey.actions.location.LocationUtil;
import com.prey.actions.location.PreyLocation;
import com.prey.net.PreyHttpResponse;
import com.prey.net.PreyWebServices;

import java.util.HashMap;

/**
 * Created by oso on 03-01-18.
 */

public class AwareService extends IntentService {

    public AwareService() {
        super("awareService");
    }

    public AwareService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int interval=-1;
        int i=0;
        try{
            int geofenceMaximumAccuracy = PreyConfig.getPreyConfig(getApplicationContext()).getGeofenceMaximumAccuracy();
            PreyLocation locationNow = null;
            do {
                locationNow = LocationUtil.getLocation(getApplicationContext(), null, false);
                PreyLogger.d("locationNow lat:" + locationNow.getLat() + " lng:" + locationNow.getLng() + " acc:" + locationNow.getAccuracy());
                Thread.sleep(1000);
                i = i + 1;
            } while (i < 5 && locationNow.getAccuracy() > geofenceMaximumAccuracy);
            if (locationNow.getAccuracy() > geofenceMaximumAccuracy) {
                locationNow = null;
            }
            HashMap<String, String> parametersMap = new HashMap<String, String>();
            parametersMap.put(LocationUtil.LAT, Double.toString(locationNow.getLat()));
            parametersMap.put(LocationUtil.LNG, Double.toString(locationNow.getLng()));
            parametersMap.put(LocationUtil.ACC, Float.toString(Math.round(locationNow.getAccuracy())));
            parametersMap.put(LocationUtil.METHOD, locationNow.getMethod() );
            PreyHttpResponse preyResponse= PreyWebServices.getInstance().sendLocation(getApplicationContext(),parametersMap);
            if(preyResponse!=null){
                if (preyResponse.getStatusCode()==201){
                    PreyLogger.i("getStatusCode 201");
                    AwareScheduled.getInstance(getApplicationContext()).reset();
                }
                if (preyResponse.getStatusCode()==200){
                    PreyLogger.i("getStatusCode 200");
                }

            }
        } catch (Exception e) {
        }

        stopSelf();

    }
}
