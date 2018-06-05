package com.prey.actions.autoconnect;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.prey.PreyConfig;
import com.prey.PreyLogger;

public class AutoConnectScheduled {

    private static AutoConnectScheduled instance = null;
    private Context context = null;
    private AlarmManager alarmMgr = null;
    private PendingIntent pendingIntent = null;

    private AutoConnectScheduled(Context context) {
        this.context = context;
    }

    public synchronized static AutoConnectScheduled getInstance(Context context) {
        if (instance == null) {
            instance = new AutoConnectScheduled(context);
        }
        return instance;
    }

    public void run() {
        try {
            String minuteSt = "1";//PreyConfig.getPreyConfig(context).getIntervalAutoConnect();
            PreyLogger.d("----------AutoConnectScheduled start minute:" + minuteSt);
            if (true){//PreyConfig.getPreyConfig(context).getAutoConnect() && minuteSt != null && !"".equals(minuteSt)) {
                int minute = Integer.parseInt(minuteSt);
                Intent intent = new Intent(context, AutoConnectAlarmReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
                    PreyLogger.d("----------setRepeating AutoConnect: " + minute+"\n");
                    alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * minute, pendingIntent);
                } else {
                    PreyLogger.d("----------setInexactRepeating AutoConnect: " + minute);
                    alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * minute, pendingIntent);
                }
                PreyLogger.d("----------start AutoConnect [" + minute + "] AutoConnectScheduled\n");
            }
        } catch (Exception e) {
            PreyLogger.d("----------Error AutoConnectScheduled :" + e.getMessage());
        }
    }

    public void reset() {
        if (alarmMgr != null) {
            alarmMgr.cancel(pendingIntent);
        }
    }
}
