package com.camera.mycustomcamera;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.camera.mycustomcamera.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {
String assetfile="file:///android_asset/";
WebView web;
File f;
    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        f=new File(getExternalCacheDir(),SplashActivity.propertyfilename);

       initnavstatusbar();
       web=findViewById(R.id.webview);
       initwebview(web.getSettings());
       web.setWebViewClient(new mywebviewClient());
       web.setWebChromeClient(new WebChromeClient(){
           @Override
           public boolean onConsoleMessage(ConsoleMessage con){
               t(con.message());
               return true;
           }
       });

       web.loadUrl(assetfile+"index.html");
        web.addJavascriptInterface(new myClass(),"jsinterface");
    }

    public void initnavstatusbar(){
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0c1022")));
        Window win=getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        win.setStatusBarColor(Color.parseColor("#0c1022"));
        win.setNavigationBarColor(Color.parseColor("#0c1022"));
    }

     public void initwebview(WebSettings ws){
        ws.setJavaScriptEnabled(true);
        ws.getAllowFileAccess();
        ws.setDomStorageEnabled(true);
        ws.setAllowContentAccess(true);
        ws.setAllowFileAccessFromFileURLs(true);
        ws.setAllowUniversalAccessFromFileURLs(true);
        ws.setLoadWithOverviewMode(true);
        ws.setUseWideViewPort(true);
        ws.setBuiltInZoomControls(true);
        ws.setSupportZoom(true);

     }

     @JavascriptInterface
     public void loginsuccess(){
     t("Login Successfully");
     }


    private class mywebviewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView web,String url){
            return false;
        }
    }

    public class myClass {

        public myClass(){

        }

        @JavascriptInterface
        public String login(String u) throws IOException {
            Properties p=loadproperties(f);
            p.setProperty(SplashActivity.USERNAME,u);
            t("Login Successful");
            saveproperties(p,f);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this,CameraActivity.class));
                    finish();
                }
            },3000);

return "true";
        }
    }

    public void t(final String s){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
            }
        });
    }



    Properties loadproperties(File f) throws IOException {
        Properties p=new Properties();
        FileInputStream fi=new FileInputStream(f);
        p.load(fi);
        fi.close();
        return p;
    }


    void saveproperties(Properties p,File f)throws IOException{
        FileOutputStream fout=new FileOutputStream(f);
        p.store(fout,"Properties");
        fout.close();
        p=null;
    }
}