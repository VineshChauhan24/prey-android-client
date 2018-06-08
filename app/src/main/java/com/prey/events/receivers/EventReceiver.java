/*******************************************************************************
 * Created by Orlando Aliaga
 * Copyright 2015 Prey Inc. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.events.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.prey.PreyLogger;
import com.prey.events.Event;
import com.prey.events.factories.EventFactory;
import com.prey.events.manager.EventManagerRunner;

public class EventReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        PreyLogger.i("EventReceiver "+intent.getAction());

        Event event = EventFactory.getEvent(context, intent);
        new Thread(new EventManagerRunner(context, event)).start();

    }

}

