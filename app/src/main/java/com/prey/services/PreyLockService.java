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
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.lock_android7, null);
            Typeface regularMedium = Typeface.createFromAsset(getAssets(), "fonts/Regular/regular-medium.ttf");
            TextView textView1 = (TextView) view.findViewById(R.id.TextView_Lock_AccessDenied);
            textView1.setTypeface(regularMedium);
            Typeface regularBold = Typeface.createFromAsset(getAssets(), "fonts/Regular/regular-bold.ttf");
            EditText editText1 = (EditText) view.findViewById(R.id.EditText_Lock_Password);
            editText1.setTypeface(regularMedium);

            final EditText editText = (EditText) this.view.findViewById(R.id.EditText_Lock_Password);
            final Button btn_unlock = (Button) this.view.findViewById(R.id.Button_Lock_Unlock);
            btn_unlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PreyLogger.i("btn_stopcall");

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


                    /*
                    try {
                        String key = editText.getText().toString().trim();
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
                            editText.setText("");
                        }
                    } catch (Exception e) {
                    }*/
                }
            });

            final Button btn_call = (Button) this.view.findViewById(R.id.Button_Call);
            btn_call.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  PreyLogger.i("btn_call");
                                                  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                                      if(getApplicationContext().checkSelfPermission( Manifest.permission.CALL_PHONE ) == PackageManager.PERMISSION_GRANTED) {
                                                          Intent callIntent = new Intent(Intent.ACTION_CALL);
                                                          callIntent.setData(Uri.parse("tel:"+PreyConfig.getPreyConfig(getApplicationContext()).getDestinationSmsNumber()));
                                                          startActivity(callIntent);


                                                          EndCallListener callListener = new EndCallListener();
                                                          TelephonyManager mTM = (TelephonyManager) getApplication().getSystemService(Context.TELEPHONY_SERVICE);
                                                          mTM.listen(callListener, PhoneStateListener.LISTEN_CALL_STATE);




                                                      }
                                                  }
                                              }
            });

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.format = PixelFormat.TRANSLUCENT;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_FULLSCREEN;
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
            wm.addView(view, layoutParams);
        }else{
            if(view != null){
                WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
                wm.removeView(view);
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
            wm.removeView(view);
            view = null;
        }
    }

}
