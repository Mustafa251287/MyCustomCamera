package com.camera.mycustomcamera;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Properties;

public class SplashActivity extends AppCompatActivity {
Properties p;
File f;
static String propertyfilename="properties.txt";
static String IMG_TAKEN="imagestaken";
static String IMG_NOT_UPLOADED="imagesnotuploaded";
static String WEB_SERVER_URL="https://anawesomedeveloper.000webhostapp.com/MyCustomCamera/upload.php";
static String CHAR_SET="UTF-8";
static String USERNAME="username";
String[] permissions=new String[]{Manifest.permission.CAMERA};
TextView hidden;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initnavstatusbar();
         hidden=findViewById(R.id.hidtv);
          f=new File(getExternalCacheDir(),propertyfilename);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M&& Build.VERSION.SDK_INT<=Build.VERSION_CODES.Q) {
            if(checkSelfPermission(permissions[0])!= PackageManager.PERMISSION_GRANTED){
                requestPermissions(permissions,100);

            }
        } else if(Build.VERSION.SDK_INT==Build.VERSION_CODES.R) {
            ActivityResultLauncher<String> cameraPermission=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean o) {
               if(o){
                   t("Permission Granted");
                   try {
                       startpath();
                   } catch (IOException e) {
                       t(e.toString());
                   }
               } else {
                   t("Permission denied");
                   exitpath();
               }
                }
            });
            cameraPermission.launch(Manifest.permission.CAMERA);



        } else if(Build.VERSION.SDK_INT>Build.VERSION_CODES.R&&Build.VERSION.SDK_INT<Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            ActivityResultLauncher<String> cameraPermission=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean o) {
                    if(o){
                        t("Permission Granted");
                        try {
                            startpath();
                        } catch (IOException e) {
                            t(e.toString());
                        }
                    } else {
                        t("Permission denied");
                        exitpath();
                    }
                }
            });
            cameraPermission.launch(Manifest.permission.CAMERA);

            }
        }
        // #########



    @Override
    public void onRequestPermissionsResult(int requestcode, @NonNull String[] per,@NonNull int[] res){
        super.onRequestPermissionsResult(requestcode,per,res);

        if(requestcode==100){
            if(res[0]==PackageManager.PERMISSION_GRANTED){
                t("Permission gotted");
                try {
                    startpath();
                } catch (IOException e) {
                    t(e.toString());
                }
            } else {
                t("Permission Denied");
                exitpath();


            }
        } else if(requestcode==102){
            if(res[0]==PackageManager.PERMISSION_GRANTED){
                t("Permission gotted");
            } else {
                t("Permission declined");
            }
        }
        }



    public boolean checkconnection(){
        ConnectivityManager cm=(ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo=cm.getActiveNetworkInfo();


        if(netinfo!=null &&netinfo.isConnected()){
            return isinternetavailable();
        }
        return false;
    }


    public boolean isinternetavailable(){
        try {
            InetAddress address=InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch(UnknownHostException e){
            return false;
        }
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


    public void t(final String s){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SplashActivity.this, s, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void exitpath(){
        hidden.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable(){
            public void run(){
             finish();
            }
        },3000);
    }


    public void startpath() throws IOException {

        if(f.exists() && !f.isDirectory()){
            p=loadproperties(f);
             String s=p.getProperty(USERNAME);

         if(s!=null){
             new Handler().postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     startActivity(new Intent(SplashActivity.this,CameraActivity.class));
                     finish();
                 }
             },3000);

         } else {
             new Handler().postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     startActivity(new Intent(SplashActivity.this,MainActivity.class));
                     finish();
                 }
             },3000);

         }


        } else {
            try {
                f.createNewFile();
                p=loadproperties(f);
                p.setProperty(IMG_TAKEN,"0");
                saveproperties(p,f);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SplashActivity.this,MainActivity.class));
                        finish();
                    }
                },3000);

            } catch(Exception e){
                t(e.toString());
            }
        }

    }

    public void initnavstatusbar(){
        Window win=getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        win.setStatusBarColor(Color.parseColor("#0c1022"));
        win.setNavigationBarColor(Color.parseColor("#0c1022"));
    }

}