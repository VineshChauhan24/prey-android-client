/*******************************************************************************
 * Created by Orlando Aliaga
 * Copyright 2015 Prey Inc. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.actions.picture;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.prey.PreyLogger;
import com.prey.actions.HttpDataService;
import com.prey.actions.camera.CameraAction;

import com.prey.activities.ScreenActivity;
import com.prey.activities.SimpleCamera2Activity;
import com.prey.activities.SimpleCamera3Activity;
import com.prey.activities.SimpleCameraActivity;
import com.prey.net.http.EntityFile;

public class PictureUtil {

    public static HttpDataService getPicture(Context ctx) {
        PreyLogger.i("PictureUtil getPicture");
        HttpDataService data = null;
        try {
            SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmZ");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                    || ActivityCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    ) {
                data = new HttpDataService(CameraAction.DATA_ID);
                data.setList(true);


                try{
                    Integer numberOfCameras = SimpleCameraActivity.getNumberOfCameras();
                    if (numberOfCameras != null && numberOfCameras > 1) {
                        Thread.sleep(6000);
                        byte[] backPicture = getPicture0(ctx, "back");
                        if (backPicture != null) {
                            PreyLogger.d("back data length=" + backPicture.length);
                            InputStream file = new ByteArrayInputStream(backPicture);
                            EntityFile entityFile = new EntityFile();
                            entityFile.setFile(file);
                            entityFile.setMimeType("image/png");
                            entityFile.setName("screenshot.jpg");
                            entityFile.setType("screenshot");
                            entityFile.setIdFile(sdf.format(new Date()) + "_" + entityFile.getType());
                            entityFile.setLength(backPicture.length);
                            data.addEntityFile(entityFile);
                        }
                    }
                } catch (Exception e1){
                    PreyLogger.e("CAMERA error:"+e1.getMessage(),e1);
                }

                try{
                    byte[] frontPicture = getPicture0(ctx, "front");

                    if (frontPicture != null) {
                        PreyLogger.d("front data length=" + frontPicture.length);
                        InputStream file = new ByteArrayInputStream(frontPicture);
                        EntityFile entityFile = new EntityFile();
                        entityFile.setFile(file);
                        entityFile.setMimeType("image/png");
                        entityFile.setName("picture.jpg");
                        entityFile.setType("picture");
                        entityFile.setIdFile(sdf.format(new Date()) + "_" + entityFile.getType());
                        entityFile.setLength(frontPicture.length);
                        data.addEntityFile(entityFile);
                    }
                } catch (Exception e1){
                    PreyLogger.e("CAMERA error:"+e1.getMessage(),e1);
                }

                Intent intent2 = new Intent(ctx, SimpleCameraActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle myKillerBundle = new Bundle();
                myKillerBundle.putInt("kill",1);
                intent2.putExtras(myKillerBundle);
                ctx.startActivity(intent2);
            }



        } catch (Exception e) {
            PreyLogger.e("Error:" + e.getMessage() + e.getMessage(), e);
        }
        return data;
    }
    static SurfaceTexture texture =null;


    private static byte[] getPicture2(Context ctx, String focus) {
        AudioManager mgr = null;
        SimpleCamera3Activity.dataImagen = null;

        SimpleCamera3Activity.texture2=texture;


        int streamType = AudioManager.STREAM_SYSTEM;
        SimpleCamera3Activity.activity = null;
        Intent intent = new Intent(ctx, SimpleCamera3Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("focus", focus);
        ctx.startActivity(intent);
        int i = 0;
        mgr = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        //mgr.setStreamSolo(streamType, true);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mgr.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            mgr.setStreamMute(streamType, true);
        }else{
            final int setVolFlags = AudioManager.FLAG_PLAY_SOUND;
            mgr.setStreamVolume(AudioManager.STREAM_MUSIC, 0, setVolFlags);
        }

        while (SimpleCamera3Activity.activity == null&& i < 10) {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
            }
            i++;
        }
        if (SimpleCamera3Activity.activity != null) {
            SimpleCamera3Activity.activity.takePicture(ctx,focus);
        }
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
        }
        //mgr.setStreamSolo(streamType, false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mgr.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            mgr.setStreamMute(streamType, false);
        }
        try {
            i = 0;
            while (SimpleCamera3Activity.activity != null && SimpleCamera3Activity.dataImagen == null && i < 5) {
                Thread.sleep(2000);
                i++;
            }
        } catch (InterruptedException e) {
            PreyLogger.e("Error:" + e.getMessage(),e);
        }
        byte[] out=null;
        if (SimpleCamera3Activity.activity != null) {
            texture=SimpleCamera3Activity.texture2;

            out=SimpleCamera3Activity.dataImagen;

            SimpleCamera3Activity.mPreviewRequestBuilder=null;

            SimpleCamera3Activity.mCaptureSession=null;

            SimpleCamera3Activity.mPreviewRequest=null;
            SimpleCamera3Activity.activity.finish();
            SimpleCamera3Activity.activity=null;
            SimpleCamera3Activity.dataImagen=null;




        }


        return out;
    }


    private static byte[] getPicture3(Context ctx, String focus) {
        AudioManager mgr = null;
        ScreenActivity.dataImagen = null;



        PreyLogger.i("SCREEN getPicture3");
        int streamType = AudioManager.STREAM_SYSTEM;
        ScreenActivity.activity = null;
        Intent intent = new Intent(ctx, ScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("focus", focus);
        ctx.startActivity(intent);

        int i=0;
        while (ScreenActivity.activity == null&& i < 10) {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
            }
            i++;
        }

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
        }

        try {
            i = 0;
            while (ScreenActivity.activity != null && ScreenActivity.dataImagen == null && i < 5) {
                Thread.sleep(2000);
                i++;
            }
        } catch (InterruptedException e) {
            PreyLogger.e("Error:" + e.getMessage(),e);
        }
        byte[] out=null;
        if (ScreenActivity.activity != null) {


            out=ScreenActivity.dataImagen;


            ScreenActivity.activity.finish();
            ScreenActivity.activity=null;
            ScreenActivity.dataImagen=null;




        }


        return out;
    }


    private static byte[] getPicture(Context ctx, String focus) {
        AudioManager mgr = null;
        SimpleCamera2Activity.dataImagen = null;

        SimpleCamera2Activity.texture2=texture;


        int streamType = AudioManager.STREAM_SYSTEM;
        SimpleCamera2Activity.activity = null;
        Intent intent = new Intent(ctx, SimpleCamera2Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("focus", focus);
        ctx.startActivity(intent);
        int i = 0;
        mgr = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        //mgr.setStreamSolo(streamType, true);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mgr.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            mgr.setStreamMute(streamType, true);
        }else{
            final int setVolFlags = AudioManager.FLAG_PLAY_SOUND;
            mgr.setStreamVolume(AudioManager.STREAM_MUSIC, 0, setVolFlags);
        }

        while (SimpleCamera2Activity.activity == null&& i < 10) {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
            }
            i++;
        }
        if (SimpleCamera2Activity.activity != null) {
            SimpleCamera2Activity.activity.takePicture(ctx,focus);
        }
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
        }
        //mgr.setStreamSolo(streamType, false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mgr.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            mgr.setStreamMute(streamType, false);
        }
        try {
            i = 0;
            while (SimpleCamera2Activity.activity != null && SimpleCamera2Activity.dataImagen == null && i < 5) {
                Thread.sleep(2000);
                i++;
            }
        } catch (InterruptedException e) {
            PreyLogger.e("Error:" + e.getMessage(),e);
        }
        byte[] out=null;
        if (SimpleCamera2Activity.activity != null) {
            texture=SimpleCamera2Activity.texture2;

            out=SimpleCamera2Activity.dataImagen;

            SimpleCamera2Activity.activity.unlockFocus();

            SimpleCamera2Activity.mPreviewRequestBuilder=null;

            SimpleCamera2Activity.mCaptureSession=null;

            SimpleCamera2Activity.mPreviewRequest=null;
            SimpleCamera2Activity.activity.finish();
            SimpleCamera2Activity.activity=null;
            SimpleCamera2Activity.dataImagen=null;




        }


        return out;
    }


    private static byte[] getPicture0(Context ctx, String focus) {
        AudioManager mgr = null;
        SimpleCameraActivity.dataImagen = null;




        int streamType = AudioManager.STREAM_SYSTEM;
        SimpleCameraActivity.activity = null;
        Intent intent = new Intent(ctx, SimpleCameraActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("focus", focus);
        ctx.startActivity(intent);
        int i = 0;
        mgr = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        //mgr.setStreamSolo(streamType, true);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mgr.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            mgr.setStreamMute(streamType, true);
        }else{
            final int setVolFlags = AudioManager.FLAG_PLAY_SOUND;
            mgr.setStreamVolume(AudioManager.STREAM_MUSIC, 0, setVolFlags);
        }

        while (SimpleCameraActivity.activity == null&& i < 10) {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
            }
            i++;
        }
        if (SimpleCameraActivity.activity != null) {
            SimpleCameraActivity.activity.takePicture(ctx,focus);
        }
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
        }
        //mgr.setStreamSolo(streamType, false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mgr.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            mgr.setStreamMute(streamType, false);
        }
        try {
            i = 0;
            while (SimpleCameraActivity.activity != null && SimpleCameraActivity.dataImagen == null && i < 5) {
                Thread.sleep(2000);
                i++;
            }
        } catch (InterruptedException e) {
            PreyLogger.e("Error:" + e.getMessage(),e);
        }
        byte[] out=null;
        if (SimpleCameraActivity.activity != null) {


            out=SimpleCameraActivity.dataImagen;

            SimpleCameraActivity.activity.finish();
            SimpleCameraActivity.activity=null;
            SimpleCameraActivity.dataImagen=null;




        }


        return out;
    }

}