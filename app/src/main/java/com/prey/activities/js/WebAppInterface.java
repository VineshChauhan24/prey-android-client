package com.prey.activities.js;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.prey.PreyConfig;
import com.prey.PreyLogger;
import com.prey.PreyStatus;
import com.prey.R;
import com.prey.activities.CheckPasswordActivity;
import com.prey.activities.DeviceReadyActivity;
import com.prey.activities.PreyConfigurationActivity;
import com.prey.events.Event;
import com.prey.events.manager.EventManagerRunner;
import com.prey.exceptions.PreyException;
import com.prey.net.PreyWebServices;

/**
 * Created by oso on 30-10-17.
 */

public class WebAppInterface {

    Context mContext;

    int wrongPasswordIntents = 0;

    /** Instantiate the interface and set the context */
    public WebAppInterface(Context c ) {
        mContext = c;

    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }


    @JavascriptInterface
    public void signIn(String pass) {
        PreyLogger.i("signIn:"+pass);

        PreyConfig.getPreyConfig(mContext).setApiKey("2iu23oxyc1ha");
        PreyConfig.getPreyConfig(mContext).setDeviceId("4250b6");



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            new CheckPassword().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,pass);
        else
            new CheckPassword().execute(pass);

    }
    @JavascriptInterface
    public void panel() {

        String url = "https://panel.preyproject.com/login?email=oaliaga@gmail.com";
        PreyLogger.i("panel:"+url);

        Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
        mContext.startActivity(browserIntent);

    }



    public class CheckPassword extends AsyncTask<String, Void, Void> {

        ProgressDialog progressDialog = null;
        boolean isPasswordOk = false;
        String error = null;


        @Override
        protected void onPreExecute() {
            try {
                progressDialog = new ProgressDialog(mContext);
                progressDialog.setMessage(mContext.getText(R.string.password_checking_dialog).toString());
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.show();
            } catch (Exception e) {

            }
        }

        @Override
        protected Void doInBackground(String... password) {
            try {
                String apikey = PreyConfig.getPreyConfig(mContext).getApiKey();
                PreyLogger.d("apikey:"+apikey+" password[0]:"+password[0]);
                isPasswordOk = PreyWebServices.getInstance().checkPassword(mContext, apikey, password[0]);
                if(isPasswordOk) {
                    PreyConfig.getPreyConfig(mContext).setTimePasswordOk();
                    PreyWebServices.getInstance().sendEvent(mContext, PreyConfig.ANDROID_LOGIN_SETTINGS);
                } else {
                    PreyWebServices.getInstance().sendEvent(mContext, PreyConfig.ANDROID_FAILED_LOGIN_SETTINGS);
                }
            } catch (PreyException e) {
                error = e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            try {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            } catch (Exception e) {
            }
            if (error != null)
                Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();
            else if (!isPasswordOk) {

                wrongPasswordIntents++;
                if (wrongPasswordIntents == 3) {
                    Toast.makeText(mContext, R.string.password_intents_exceed, Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(mContext, R.string.password_wrong, Toast.LENGTH_SHORT).show();
                }

            } else {
                Intent intent = new Intent(mContext, PreyConfigurationActivity.class);
                PreyStatus.getInstance().setPreyConfigurationActivityResume(true);
                mContext.startActivity(intent);

                new Thread(new EventManagerRunner(mContext, new Event(Event.APPLICATION_OPENED))).start();
            }
        }

    }
}
