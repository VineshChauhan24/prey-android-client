package com.prey.activities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;


import com.prey.PreyLogger;
import com.prey.R;

import java.util.HashMap;
import java.util.Map;

public class LockActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        setContentView(R.layout.super_lock2);

        PreyLogger.i("onCreate btn_lock");

        ImageView  btn_lock=(ImageView)findViewById(R.id.btn_lock);
        btn_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreyLogger.i("click btn_lock2");
                Map<String, Object> eventValue = new HashMap<String, Object>();
               // eventValue.put(AFInAppEventParameterName.REGSITRATION_METHOD, "email");

               // AppsFlyerLib.getInstance().trackEvent(getApplicationContext(), AFInAppEventType.COMPLETE_REGISTRATION, eventValue);



            }
        });
        ImageView  recovery_call=(ImageView)findViewById(R.id.recovery_call);
        recovery_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreyLogger.i("click recovery_call2");
                Map<String, Object> eventValue = new HashMap<String, Object>();

               // eventValue.put(AFInAppEventParameterName.SUCCESS, true);
               // AppsFlyerLib.getInstance().trackEvent(getApplicationContext(), AFInAppEventType.LOGIN, eventValue);





            }
        });

    }
}