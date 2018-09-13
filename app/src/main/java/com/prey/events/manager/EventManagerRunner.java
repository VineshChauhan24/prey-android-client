/*******************************************************************************
 * Created by Orlando Aliaga
 * Copyright 2015 Prey Inc. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.events.manager;

import com.prey.PreyLogger;
import com.prey.events.Event;

import android.content.Context;

public class EventManagerRunner implements Runnable {

    private Context ctx = null;
    private Event event;

    public EventManagerRunner(Context ctx, Event event) {
        this.ctx = ctx;
        this.event = event;
    }

    public void run() {
        if (event != null) {
            PreyLogger.d("EVENT CheckInReceiver IN:" + event.getName());
            new EventManager(ctx).execute(event);
            PreyLogger.d("EVENT CheckInReceiver OUT:" + event.getName());
        }
    }
}

