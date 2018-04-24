package com.prey.actions.call;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.prey.PreyLogger;

public class EndCallListener extends PhoneStateListener {
    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        if(TelephonyManager.CALL_STATE_RINGING == state) {
            PreyLogger.i(  "RINGING, number: " + incomingNumber);
        }
        if(TelephonyManager.CALL_STATE_OFFHOOK == state) {
            //wait for phone to go offhook (probably set a boolean flag) so you know your app initiated the call.
            PreyLogger.i(  "OFFHOOK");
        }
        if(TelephonyManager.CALL_STATE_IDLE == state) {
            //when this state occurs, and your flag is set, restart your app
            PreyLogger.i( "IDLE");
        }
    }
}
