package com.prey.services;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.prey.PreyConfig;
import com.prey.PreyLogger;
import com.prey.net.PreyHttpResponse;
import com.prey.net.PreyWebServices;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {



    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        PreyLogger.d("Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        PreyLogger.d("sendRegistrationToServer:"+token);
        String registration="FCM__"+token;

        PreyHttpResponse response = PreyWebServices.getInstance().setPushRegistrationId(this, registration);
        PreyConfig.getPreyConfig(this).setNotificationId(registration);
        if (response != null) {
            PreyLogger.d("response:" + response.toString());
        }
        PreyConfig.getPreyConfig(this).setRegisterC2dm(true);
    }
}
