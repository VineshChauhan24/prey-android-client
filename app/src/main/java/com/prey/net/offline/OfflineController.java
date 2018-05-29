/*******************************************************************************
 * Created by Orlando Aliaga
 * Copyright 2017 Prey Inc. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.net.offline;

import android.content.Context;

import com.prey.PreyConfig;
import com.prey.PreyLogger;
import com.prey.managers.PreyWifiManager;
import com.prey.net.PreyHttpResponse;
import com.prey.net.UtilConnection;
import com.prey.net.http.EntityFile;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class OfflineController {

    private static OfflineController instance= null;
    private static Object mutex= new Object();
    private OfflineController(){
    }

    public static OfflineController getInstance(){
        if(instance==null){
            synchronized (mutex){
                if(instance==null) instance= new OfflineController();
            }
        }
        return instance;
    }

}
