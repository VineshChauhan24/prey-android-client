package com.prey.activities;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.prey.R;
import com.prey.activities.js.WebAppInterface;

/**
 * Created by oso on 26-10-17.
 */

public class WebViewDemo extends Activity {






    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        WebView webView = (WebView) findViewById(R.id.install_browser);



        webView.getSettings().setJavaScriptEnabled(true);


        webView.loadUrl("file:///android_asset/html/index.html");

        webView.addJavascriptInterface(new WebAppInterface(this), "Android");

    }




}
