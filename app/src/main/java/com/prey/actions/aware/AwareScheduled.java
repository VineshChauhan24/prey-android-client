package com.prey.actions.aware;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.prey.PreyConfig;
import com.prey.PreyLogger;

/**
 * Created by oso on 03-01-18.
 */

public class AwareScheduled {

    private static AwareScheduled instance = null;
    private Context context = null;
    private AlarmManager alarmMgr = null;
    private PendingIntent pendingIntent = null;


    private AwareScheduled(Context context) {
        this.context = context;

    }

    public synchronized static AwareScheduled getInstance(Context context) {
        if (instance == null) {
            instance = new AwareScheduled(context);
        }
        return instance;
    }


    public void run() {

        try {
            String minuteSt=PreyConfig.getPreyConfig(context).getIntervalAware();
            if(minuteSt!=null&!"".equals(minuteSt)) {
                int minute = Integer.parseInt(minuteSt);


                PreyLogger.d("----------AwareScheduled start minute:" + minute);


                Intent intent = new Intent(context, AlarmAwareReceiver.class);

                pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);


                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
                    PreyLogger.d("----------setRepeating Aware: " + minute);
                    alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000 * 60 * minute), 1000 * 60 * minute, pendingIntent);
                } else {
                    PreyLogger.d("----------setInexactRepeating : " + minute);
                    alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000 * 60 * minute), 1000 * 60 * minute, pendingIntent);
                }


                PreyLogger.d("----------start aware [" + minute + "] AwareScheduled");
            }
        }catch(Exception e){
            PreyLogger.d("----------Error AwareScheduled :"+e.getMessage());
        }
    }

    public void reset() {
        if (alarmMgr != null) {
            String minuteSt=PreyConfig.getPreyConfig(context).getIntervalAware();
            if(minuteSt!=null&!"".equals(minuteSt)) {
                int minute = Integer.parseInt(minuteSt);

                PreyLogger.i("_________________shutdown aware [" + minute + "] alarmIntent");
                alarmMgr.cancel(pendingIntent);
                minute = 0;
            }
        }
    }

}
