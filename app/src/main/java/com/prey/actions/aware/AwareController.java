package com.prey.actions.aware;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.prey.FileConfigReader;
import com.prey.PreyConfig;
import com.prey.PreyLogger;
import com.prey.actions.geofences.GeofenceController;
import com.prey.actions.geofences.GeofenceIntentService;
import com.prey.actions.location.LocationUtil;
import com.prey.actions.location.PreyLocation;
import com.prey.json.UtilJson;
import com.prey.net.PreyHttpResponse;
import com.prey.net.PreyWebServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by oso on 26-12-17.
 */

public class AwareController {
    private static AwareController INSTANCE;

    private GoogleApiClient mGoogleApiClient = null;

    String AWARE_ID = "Aware";


    public static AwareController getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AwareController();
        }
        return INSTANCE;
    }

    public void addAware(Context context){
        PreyConfig.getPreyConfig(context).setAware(true);
    }

    public void removeAware(Context context){
        PreyConfig.getPreyConfig(context).setAware(false);
    }

    public void run(Context context){

        mGoogleApiClient = connectGoogleApiClient(context);

        boolean aware=PreyConfig.getPreyConfig(context).getAware();
        if(true) {
            PreyLogger.i("removeList.add(AWARE_ID)");
            List<String> removeList = new ArrayList<String>();
            removeList.add(AWARE_ID);
            LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, removeList);
        }
        if (aware){
            PreyLogger.i("addList.add(AWARE_ID)");
            int i = 0;
            try {
                int geofenceMaximumAccuracy = PreyConfig.getPreyConfig(context).getGeofenceMaximumAccuracy();
                PreyLocation locationNow = null;
                do {
                    locationNow = LocationUtil.getLocation(context, null, false);
                    PreyLogger.d("locationNow lat:" + locationNow.getLat() + " lng:" + locationNow.getLng() + " acc:" + locationNow.getAccuracy());
                    Thread.sleep(1000);
                    i = i + 1;
                } while (i < 10 && locationNow.getAccuracy() > geofenceMaximumAccuracy);
                if (locationNow.getAccuracy() > geofenceMaximumAccuracy) {
                    locationNow = null;
                }
                if (locationNow != null) {


                    HashMap<String, String> parametersMap = new HashMap<String, String>();
                    parametersMap.put(LocationUtil.LAT, Double.toString(locationNow.getLat()));
                    parametersMap.put(LocationUtil.LNG, Double.toString(locationNow.getLng()));
                    parametersMap.put(LocationUtil.ACC, Float.toString(Math.round(locationNow.getAccuracy())));
                    parametersMap.put(LocationUtil.METHOD, locationNow.getMethod() );

                    //PreyHttpResponse preyResponse= PreyWebServices.getInstance().sendLocation(context,parametersMap);



                    int transitionTypes = com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_DWELL |
                            com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER |
                            com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT;

                    int radius = 2000;
                    List<Geofence> mGeofenceList = new ArrayList<Geofence>();
                    mGeofenceList.add(new com.google.android.gms.location.Geofence.Builder()
                            .setRequestId(AWARE_ID)
                            .setCircularRegion(locationNow.getLat(), locationNow.getLng(), radius)
                            .setExpirationDuration(Geofence.NEVER_EXPIRE)
                            .setTransitionTypes(transitionTypes)
                            .setLoiteringDelay(FileConfigReader.getInstance(context).getGeofenceLoiteringDelay())
                            .setNotificationResponsiveness(FileConfigReader.getInstance(context).getGeofenceNotificationResponsiveness())
                            .build());

                    GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
                    builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
                    builder.addGeofences(mGeofenceList);
                    GeofencingRequest geofencingRequest = builder.build();
                    if (mGoogleApiClient.isConnected()) {
                        PreyLogger.d("---->isConnected");

                        try {
                            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                Intent intent = new Intent(context, AwareIntentService.class);
                                PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                PreyLogger.d("---->addGeofences");
                                PendingResult<Status> result = LocationServices.GeofencingApi.addGeofences(
                                        mGoogleApiClient,
                                        geofencingRequest,
                                        pendingIntent
                                );
                                result.setResultCallback(new ResultCallback<Status>() {
                                    @Override
                                    public void onResult(Status status) {
                                        PreyLogger.d("*********************connectionAddListener  status :" + status);
                                        if (status.isSuccess()) {
                                            PreyLogger.d("********saveAwareGeofence");
                                        } else {
                                            PreyLogger.d("*********************Registering geofence failed: " + status.getStatusMessage() + " : " + status.getStatusCode());

                                        }
                                    }
                                });
                            }
                        } catch (Exception e) {
                            PreyLogger.e("error ---->isConnected:" + e.getMessage(), e);

                        }
                    }
                }
            } catch (Exception e) {
            }
        }
    }


    private GoogleApiClient connectGoogleApiClient(Context ctx) {
            GoogleApiClient mGoogleApiClient = null;
            try {
                mGoogleApiClient = buildGoogleApiClient(ctx);
                int i = 0;
                while (i < 50 && !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                    i++;
                    Thread.sleep(1000);
                    if (i % 10 == 0) {
                        buildGoogleApiClient(ctx);
                    }
                    PreyLogger.d("___[" + i + "] sleep");
                }
            } catch (Exception e) {
                PreyLogger.e("error:" + e.getMessage(), e);
            }
            return mGoogleApiClient;
    }

    private synchronized GoogleApiClient buildGoogleApiClient(Context ctx) {
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(ctx)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        PreyLogger.d("________________Connected to GoogleApiClient");
                    }
                    @Override
                    public void onConnectionSuspended(int i) {
                        PreyLogger.d("________________Connection suspended");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        PreyLogger.d("________________Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
                    }
                })
                .addApi(LocationServices.API)
                .build();
        return mGoogleApiClient;
    }
}
