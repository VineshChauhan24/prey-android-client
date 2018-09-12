package com.prey.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.prey.PreyLogger;
import com.prey.R;

public class PreyLockAdminService extends Service {

    private WindowManager windowManager;
    private View view;


    public PreyLockAdminService(){

    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        PreyLogger.d("PreyLockAdminService onCreate");
    }

    public void onStart(Intent intent, int startId) {
        super.onStart(intent,startId);
        final Context ctx=this;
        PreyLogger.d("PreyLockAdminService onStart");


            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.lock_admin, null);

        if(view!=null){
            final Button btn_unlock = (Button) view.findViewById(R.id.Button_Lock_Unlock);
            final Button btn_cancel = (Button) view.findViewById(R.id.Button_Cancel);

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    PreyLogger.d("PreyLockAdminService btn_cancel");
                    try {
                        Intent intent3 = new Intent(ctx, PreyLockAdminService.class);
                        ctx.stopService(intent3);
                    } catch (Exception e) {
                        PreyLogger.e("error:" + e.getMessage(), e);
                    }
                }
            });

            btn_unlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    PreyLogger.d("PreyLockAdminService btn_unlock");
                    try {
                        Intent intent3 = new Intent(ctx, PreyLockAdminService.class);
                        ctx.stopService(intent3);
                    } catch (Exception e) {
                        PreyLogger.e("error:" + e.getMessage(), e);
                    }
                }
            });
        }





            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.format = PixelFormat.TRANSLUCENT;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_FULLSCREEN;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }

        PreyLogger.d("PreyLockAdminService onStart 1");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PreyLogger.d("PreyLockAdminService onStart 2");

            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
                if (true){//{Settings.canDrawOverlays(this)) {
                    PreyLogger.d("PreyLockAdminService onStart 3");



                    if(wm != null) {
                        try{
                            wm.addView(view, layoutParams);
                        }catch (Exception e){
                            PreyLogger.e(e.getMessage(),e);
                        }
                    }
                }
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

