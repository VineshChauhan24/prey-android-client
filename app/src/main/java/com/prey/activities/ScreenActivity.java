package com.prey.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaPlayer;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;

import com.prey.PreyLogger;
import com.prey.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Date;

public class ScreenActivity extends Activity implements MediaPlayer.OnPreparedListener, SurfaceHolder.Callback {
    public static byte[] dataImagen = null;

    private MediaProjectionManager mProjectionManager;
    private static final int REQUEST_CODE = 100;
    private static MediaProjection sMediaProjection;
    private static String STORE_DIRECTORY;


    private static int IMAGES_PRODUCED;
    private int mDensity;
    private int mWidth;
    private int mHeight;
    private int mRotation;

    private Handler mHandler;
    private Display mDisplay;
    private OrientationChangeCallback mOrientationChangeCallback;
    private static final int VIRTUAL_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    private ImageReader mImageReader;
    private static final String SCREENCAP_NAME = "screencap";
    private VirtualDisplay mVirtualDisplay;

    public static ScreenActivity activity = null;

    @TargetApi(21)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_screen);
        PreyLogger.i("SCREEN ScreenActivity onCreate");
        mProjectionManager = (MediaProjectionManager) this.getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        activity = this;


    }

    @TargetApi(21)
    @Override
    public void onResume() {
        super.onResume();
        startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
        PreyLogger.i("SCREEN ScreenActivity onResume");
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        ;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        PreyLogger.i("surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


    @TargetApi(21)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        PreyLogger.i("SCREEN ScreenActivity onActivityResult requestCode:" + requestCode);
        if (requestCode == REQUEST_CODE) {

            sMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);


            // display metrics
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            mDensity = metrics.densityDpi;
            mDisplay = getWindowManager().getDefaultDisplay();

            // create virtual display depending on device width / height
            createVirtualDisplay();

            // register orientation change callback
            mOrientationChangeCallback = new OrientationChangeCallback(getApplicationContext());
            if (mOrientationChangeCallback.canDetectOrientation()) {
                mOrientationChangeCallback.enable();
            }

            // register media projection stop callback
            sMediaProjection.registerCallback(new MediaProjectionStopCallback(), mHandler);

        }
    }

    @TargetApi(21)
    private class MediaProjectionStopCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            Log.e("ScreenCapture", "stopping projection.");

            if (mVirtualDisplay != null) mVirtualDisplay.release();
            if (mImageReader != null) mImageReader.setOnImageAvailableListener(null, null);
            if (mOrientationChangeCallback != null) mOrientationChangeCallback.disable();
            sMediaProjection.unregisterCallback(this);
            finish();
        }
    }


    @TargetApi(21)
    private void createVirtualDisplay() {
        PreyLogger.i("SCREEN ScreenActivity createVirtualDisplay:");
        IMAGES_PRODUCED = 0;
        // get width and height
        Point size = new Point();
        mDisplay.getSize(size);
        mWidth = size.x;
        mHeight = size.y;

        // start capture reader
        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
        mVirtualDisplay = sMediaProjection.createVirtualDisplay(SCREENCAP_NAME, mWidth, mHeight, mDensity, VIRTUAL_DISPLAY_FLAGS, mImageReader.getSurface(), null, mHandler);
        mImageReader.setOnImageAvailableListener(new ImageAvailableListener(), mHandler);
    }

    @TargetApi(21)
    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
        @Override
        public void onImageAvailable(ImageReader reader) {
            PreyLogger.i("SCREEN onImageAvailable ImageAvailableListener:");
            Image image = null;
            FileOutputStream fos = null;
            Bitmap bitmap = null;

            try {
                image = reader.acquireLatestImage();

                PreyLogger.i("SCREEN onImageAvailable image is null:" + (image == null));

                if (image != null) {
                    PreyLogger.i("SCREEN onImageAvailable image is 1");

                        Image.Plane[] planes = image.getPlanes();
                        ByteBuffer buffer = planes[0].getBuffer();







                        int pixelStride = planes[0].getPixelStride();
                        int rowStride = planes[0].getRowStride();
                        int rowPadding = rowStride - pixelStride * mWidth;

                        // create bitmap
                        bitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
                        bitmap.copyPixelsFromBuffer(buffer);
                    PreyLogger.i("SCREEN onImageAvailable image is 2");
                        //  buffer.get(bytes);
                        //    dataImagen=bytes;


                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    dataImagen =  stream.toByteArray();
                    bitmap.recycle();

                    PreyLogger.i("SCREEN onImageAvailable image is 3");
/*
                        // write bitmap to a file


                        File newFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "Amyscreen_1.png");
                        newFile.getParentFile().mkdirs();

                    PreyLogger.i("SCREEN onImageAvailable image is 3");
                        PreyLogger.i("SCREEN captured image " + Environment.DIRECTORY_DOWNLOADS + " fileOut: " + newFile.getPath());
                        //EntelLogger.i( "fileOut: " + fileOut);

                        fos = new FileOutputStream(newFile);

                    PreyLogger.i("SCREEN onImageAvailable image is 4");
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        PreyLogger.i("SCREEN captured image: " + IMAGES_PRODUCED);

                        InputStream input = new FileInputStream(newFile.getPath());
                        int byteReads;
                        ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
                        while ((byteReads = input.read()) != -1) {
                            output.write(byteReads);
                        }
                    PreyLogger.i("SCREEN onImageAvailable image is 5");
                        dataImagen = output.toByteArray();


                        PreyLogger.i("SCREEN bytes :" + bytes.length);


*/



                    PreyLogger.i("SCREEN onImageAvailable image is 6");
                }

            } catch (Exception e) {
                PreyLogger.e("error: " + e.getMessage(), e);
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException ioe) {

                    }
                }

                if (bitmap != null) {
                    bitmap.recycle();
                }

                if (image != null) {
                    image.close();
                }
            }

            sMediaProjection.stop();
        }
    }


    @TargetApi(21)
    private class OrientationChangeCallback extends OrientationEventListener {

        OrientationChangeCallback(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            final int rotation = mDisplay.getRotation();
            if (rotation != mRotation) {
                mRotation = rotation;
                try {
                    // clean up
                    if (mVirtualDisplay != null) mVirtualDisplay.release();
                    if (mImageReader != null) mImageReader.setOnImageAvailableListener(null, null);

                    // re-create virtual display depending on device width / height
                    createVirtualDisplay();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
 