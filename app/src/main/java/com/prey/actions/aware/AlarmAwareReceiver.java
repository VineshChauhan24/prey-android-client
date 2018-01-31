package com.prey.actions.aware;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.prey.PreyLogger;
import com.prey.actions.report.ReportService;

/**
 * Created by oso on 03-01-18.
 */

public class AlarmAwareReceiver  extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            PreyLogger.d("______________________________");
            PreyLogger.d("______________________________");
            PreyLogger.d("----------AlarmAwareReceiver onReceive");

            Intent intent2 = new Intent(context, AwareService.class);
            context.startService(intent2);
        }catch(Exception e){
            PreyLogger.d("_______AlarmAwareReceiver error:"+e.getMessage());
        }


    }
}
