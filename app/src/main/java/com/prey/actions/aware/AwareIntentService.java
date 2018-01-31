package com.prey.actions.aware;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.prey.PreyConfig;
import com.prey.PreyLogger;
import com.prey.actions.geofences.GeofenceDataSource;
import com.prey.actions.geofences.GeofenceDto;
import com.prey.actions.location.LocationUtil;
import com.prey.actions.location.PreyLocation;
import com.prey.events.Event;
import com.prey.events.manager.EventThread;
import com.prey.net.PreyHttpResponse;
import com.prey.net.PreyWebServices;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by oso on 26-12-17.
 */

public class AwareIntentService  extends IntentService {

    public AwareIntentService() {
        super(PreyConfig.TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event != null) {
            if (event.hasError()) {
                onError(event.getErrorCode());
            } else {
                int transition = event.getGeofenceTransition();
                event.getTriggeringLocation().getLongitude();
                event.getTriggeringLocation().getLatitude();
                event.getTriggeringLocation().getAccuracy();



                if (transition == Geofence.GEOFENCE_TRANSITION_ENTER || transition == Geofence.GEOFENCE_TRANSITION_DWELL || transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                    List<String> geofenceIds = new ArrayList<>();
                    List<Geofence> triggeringGeofences = event.getTriggeringGeofences();
                    notifyGeofenceTransition(getApplicationContext(), transition, triggeringGeofences, event.getTriggeringLocation());
                }
            }
        }
    }



    private void notifyGeofenceTransition(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences, Location location) {
        String transition="";
        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
            transition="ENTER";
        }else{
            if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL){
                transition="DWELL";
            }else{
                transition="EXIT";
            }
        }
        PreyLocation preyLocation=new PreyLocation(location);
        PreyLogger.d("notifyGeofenceTransition["+transition+"]  lat:"+preyLocation.getLat()+" lng:"+preyLocation.getLng()+" acc:"+preyLocation.getAccuracy()+" mth:"+preyLocation.getMethod());
        HashMap<String, String> parametersMap = new HashMap<String, String>();
        parametersMap.put(LocationUtil.LAT, Double.toString(preyLocation.getLat()));
        parametersMap.put(LocationUtil.LNG, Double.toString(preyLocation.getLng()));
        parametersMap.put(LocationUtil.ACC, Float.toString(Math.round(preyLocation.getAccuracy())));
        parametersMap.put(LocationUtil.METHOD, preyLocation.getMethod() );
        PreyHttpResponse preyResponse=PreyWebServices.getInstance().sendLocation(context,parametersMap);
        if(preyResponse!=null){
            if (preyResponse.getStatusCode()==201){
                PreyLogger.i("getStatusCode 201");
                AwareController.getInstance().removeAware(context);
            }
            if (preyResponse.getStatusCode()==200){
                PreyLogger.i("getStatusCode 200");
            }
            AwareController.getInstance().run(context);
        }
        new AwareController().run(context);
    }

    private void onError(int i) {
        PreyLogger.d("***************AwareIntentService Error: " + i);
    }
}
