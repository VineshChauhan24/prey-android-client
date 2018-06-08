/*******************************************************************************
 * Created by Orlando Aliaga
 * Copyright 2017 Prey Inc. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.prey.PreyConfig;
import com.prey.PreyLogger;
import com.prey.R;
import com.prey.actions.call.EndCallListener;
import com.prey.json.UtilJson;
import com.prey.net.PreyWebServices;

import java.lang.reflect.Method;

public class PreyLockService extends Service{

    private WindowManager windowManager;
    private View view;

    private int estado=0;
    private ImageView recovery_call;

    public PreyLockService(){

    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        PreyLogger.d("PreyLockService onCreate");
    }

    public void onStart(Intent intent, int startId) {
        super.onStart(intent,startId);
        final Context ctx=this;
        PreyLogger.d("PreyLockService onStart");

        final String unlock= PreyConfig.getPreyConfig(ctx).getUnlockPass();

        if(unlock!=null&&!"".equals(unlock)) {

            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.super_lock2, null);
            Typeface regularBold = Typeface.createFromAsset(getAssets(), "fonts/Regular/regular-bold.ttf");
            Typeface regularMedium = Typeface.createFromAsset(getAssets(), "fonts/Regular/regular-medium.ttf");


            TextView textView_title = (TextView) view.findViewById(R.id.textView_title);
            textView_title.setTypeface(regularBold);
            TextView textView_body = (TextView) view.findViewById(R.id.textView_body);
            textView_body.setTypeface(regularMedium);
            final EditText editTextEnterPassword = (EditText) view.findViewById(R.id.editTextEnterPassword);
            editTextEnterPassword.setTypeface(regularMedium);
            editTextEnterPassword.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.setFocusable(true);
                    v.setFocusableInTouchMode(true);
                    return false;
                }
            });

            final ImageView btn_lock = (ImageView) this.view.findViewById(R.id.btn_lock);
            btn_lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PreyLogger.i("btn_lock");

                    try {
                        String key = editTextEnterPassword.getText().toString().trim();
                        PreyLogger.i("unlock key:"+key+" unlock:"+unlock);
                        if (unlock.equals(key)) {
                            String jobIdLock=PreyConfig.getPreyConfig(ctx).getJobIdLock();
                            String reason=null;
                            if(jobIdLock!=null&&!"".equals(jobIdLock)){
                                reason="{\"device_job_id\":\""+jobIdLock+"\"}";
                                PreyConfig.getPreyConfig(ctx).setJobIdLock("");
                            }
                            final String reasonFinal=reason;
                            PreyConfig.getPreyConfig(ctx).setLock(false);
                            PreyConfig.getPreyConfig(ctx).deleteUnlockPass();
                            new Thread(){
                                public void run() {
                                    PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(ctx, UtilJson.makeMapParam("start", "lock", "stopped",reasonFinal));


                                    int pid = android.os.Process.myPid();
                                    android.os.Process.killProcess(pid);
                                }
                            }.start();
                        } else {
                            editTextEnterPassword.setText("");
                            editTextEnterPassword.setBackgroundColor(Color.RED);


                        }
                    } catch (Exception e) {
                    }


                }
            });

            recovery_call = (ImageView) this.view.findViewById(R.id.recovery_call);

            String number=PreyConfig.getPreyConfig(getApplicationContext()).getDestinationHeroNumber();
            if(number==null||"".equals(number)){
                recovery_call.setVisibility(View.INVISIBLE);
            }else {

                recovery_call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PreyLogger.i("btn_call state:" + estado);
                        if (estado == 1 || estado == 2) {
                            TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                            try {
                                Class c = Class.forName(tm.getClass().getName());
                                Method m = c.getDeclaredMethod("getITelephony");
                                m.setAccessible(true);
                                Object telephonyService = m.invoke(tm);

                                c = Class.forName(telephonyService.getClass().getName());
                                m = c.getDeclaredMethod("endCall");
                                m.setAccessible(true);
                                m.invoke(telephonyService);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            PreyLogger.i("stop call");
                            Bitmap bImage = BitmapFactory.decodeResource(getResources(), R.drawable.recovery_call_en);
                            recovery_call.setImageBitmap(bImage);
                        } else {
                            estado = 0;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (getApplicationContext().checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                                    callIntent.setData(Uri.parse("tel:" + PreyConfig.getPreyConfig(getApplicationContext()).getDestinationHeroNumber()));
                                    startActivity(callIntent);


                                    EndCallListener callListener = new EndCallListener();
                                    TelephonyManager mTM = (TelephonyManager) getApplication().getSystemService(Context.TELEPHONY_SERVICE);
                                    mTM.listen(new PreyPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);


                                }
                            }
                        }
                    }
                });
            }

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.format = PixelFormat.TRANSLUCENT;

           layoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_FULLSCREEN;


            //layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_FULLSCREEN;
           // layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;



            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;



            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
                PreyLogger.i("antes");
                if (Settings.canDrawOverlays(this)) {
                    PreyLogger.i("durante");
                    if(wm != null) {
                        wm.addView(view, layoutParams);
                    }else{
                        PreyLogger.i("vacio");
                    }
                }else{
                    PreyLogger.i("no permiso");
                }
            }

        }else{
            if(view != null){
                WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
                if(wm != null) {
                    wm.removeView(view);
                }
                view = null;
            }
            stopSelf();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        PreyLogger.d("PreyLockService onDestroy");
        if(view != null){
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            if(wm != null) {
                wm.removeView(view);
            }
            view = null;
        }
    }

    class PreyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if(TelephonyManager.CALL_STATE_RINGING == state) {
                PreyLogger.i(  "RINGING2, number: " + incomingNumber);

                Bitmap bImage = BitmapFactory.decodeResource(getResources(), R.drawable.recovery_call_end_en);
                recovery_call.setImageBitmap(bImage);
                estado=1;

            }
            if(TelephonyManager.CALL_STATE_OFFHOOK == state) {
                //wait for phone to go offhook (probably set a boolean flag) so you know your app initiated the call.
                PreyLogger.i(  "OFFHOOK2");


                Bitmap bImage = BitmapFactory.decodeResource(getResources(), R.drawable.recovery_call_end_en);
                recovery_call.setImageBitmap(bImage);
                estado=2;
            }
            if(TelephonyManager.CALL_STATE_IDLE == state) {
                //when this state occurs, and your flag is set, restart your app
                PreyLogger.i( "IDLE2");
                Bitmap bImage = BitmapFactory.decodeResource(getResources(), R.drawable.recovery_call_en);
                recovery_call.setImageBitmap(bImage);
                estado=3;
            }
        }
    }
}

