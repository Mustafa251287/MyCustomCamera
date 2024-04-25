package com.camera.mycustomcamera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class CameraActivity extends AppCompatActivity {
    public TextureView texture;
    public static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    String cameraid;
    CameraDevice cameradevice;
    CameraCaptureSession camerasession;
    CaptureRequest capturerequest;
    CaptureRequest.Builder capturerequestbuilder;
    Size imagedimen;
    File myfile;
    ImageReader imagereader;
    Handler backgroundhandler;
    HandlerThread handlerthread;
    static int camera_request = 200;
    public boolean flashsupport;
    int frontcam = 1;
    int rearcam = 0;
    int camid = rearcam;
    File propfile;
    TextureView.SurfaceTextureListener texturelistener;
    public CameraDevice.StateCallback statecallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        initnavstatusbar();
        //gettransparentdisplay();
        texture = (TextureView) findViewById(R.id.texture);
        assert texture != null;
        propfile = new File(getExternalCacheDir(), SplashActivity.propertyfilename);
        uploadremainingphotos();
        texturelistener = new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
                opencamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {

            }
        };
        texture.setSurfaceTextureListener(texturelistener);

        statecallback = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice camera) {
                cameradevice = camera;
                createCameraPreview();
            }

            @Override
            public void onDisconnected(@NonNull CameraDevice cameraDevice) {
                cameradevice.close();
            }

            @Override
            public void onError(@NonNull CameraDevice cameraDevice, int i) {
                cameradevice.close();
                cameradevice = null;
                t("Camera Error occured " + i);
            }
        };


    }

    public void startBackgroundThread() {
        handlerthread = new HandlerThread("Camera Background");
        handlerthread.start();
        backgroundhandler = new Handler(handlerthread.getLooper());
    }


    public void stopBackgroundThread() {
        handlerthread.quitSafely();
        try {
            handlerthread.join();
            handlerthread = null;
            backgroundhandler = null;
        } catch (Exception e) {
            t(e.toString());
        }
    }

    public void takePicture() {
        if (cameradevice == null) {
            t("Camera Device is null");
            return;
        }

        CameraManager cameramanager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics cameraCharacteristics = cameramanager.getCameraCharacteristics(cameradevice.getId());
            Size[] jpegsizes = null;

            if (cameraCharacteristics != null) {
                jpegsizes = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }
            int height =500;
            int width =500;

            if (jpegsizes != null && 0 < jpegsizes.length) {
                int test=jpegsizes.length;
                width = jpegsizes[test-1].getWidth();
                height = jpegsizes[test-1].getHeight();
            }

            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(texture.getSurfaceTexture()));
            final CaptureRequest.Builder capturebuilder = cameradevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            capturebuilder.addTarget(reader.getSurface());
            capturebuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            //Orientation
            int sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            int test = sensorOrientation;

            capturebuilder.set(CaptureRequest.JPEG_ORIENTATION, test);

            ImageReader.OnImageAvailableListener readerlistener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader iReader){
                    Image image = null;
                    try {
                        image = iReader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);
                    } catch (Exception e) {
                        t(e.toString());
                    } finally {
                        if (image != null) {
                            image.close();
                        }
                    }
                }

                private void save(byte[] bytes) {
                    OutputStream output = null;
                    try {
                        Properties p = loadproperties(propfile);
                        int img = Integer.parseInt(p.getProperty(SplashActivity.IMG_TAKEN));
                        img = img + 1;
                        p.setProperty(SplashActivity.IMG_TAKEN, img + "");
                        File file = null;
                        file = new File(getExternalCacheDir(), "pics" + img + ".jpg");
                        output = new FileOutputStream(file);
                        output.write(bytes);
                        output.close();
                        saveproperties(p, propfile);

                    } catch (Exception e) {
                        t(e.toString());
                    } finally {
                        if (null != output) {
                            output = null;
                        }
                    }
                }
            };
            reader.setOnImageAvailableListener(readerlistener, backgroundhandler);

            final CameraCaptureSession.CaptureCallback capturelistener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    try {
                        t("Successfully Saved Image");
                        Properties p = loadproperties(propfile);
                       if (checkconnection()) {
                            int imgtaken = Integer.parseInt(p.getProperty(SplashActivity.IMG_TAKEN));
                            String user = p.getProperty(SplashActivity.USERNAME).toString();
                            saveproperties(p, propfile);
                            File f = new File(getExternalCacheDir(), "pics" + imgtaken + ".jpg");
                            String resp = sendphototoserver(user, f);
                            t("Sending photo to server " + resp);

                        } else {
                            int imgtaken = Integer.parseInt(p.getProperty(SplashActivity.IMG_TAKEN));
                            String s = "pics" + imgtaken + ".jpg";

                            if (p.getProperty(SplashActivity.IMG_NOT_UPLOADED) == null) {
                                p.setProperty(SplashActivity.IMG_NOT_UPLOADED, s);
                            } else {
                                String n = p.getProperty(SplashActivity.IMG_NOT_UPLOADED);
                                n = n + ";" + s;
                                p.setProperty(SplashActivity.IMG_NOT_UPLOADED, n);
                            }


                        }

                        saveproperties(p, propfile);
                    } catch (Exception e) {
                        t(e.toString());
                    }
                    createCameraPreview();

                }
            };
            cameradevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    try {
                        cameraCaptureSession.capture(capturebuilder.build(), capturelistener, backgroundhandler);
                    } catch (CameraAccessException e) {
                        t(e.toString());
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                }
            }, backgroundhandler);
        } catch (Exception e) {
            t(e.toString());
        }

    }

    public void createCameraPreview() {
        try {
            SurfaceTexture surfacetexture = texture.getSurfaceTexture();
            assert surfacetexture != null;
            surfacetexture.setDefaultBufferSize(imagedimen.getWidth(), imagedimen.getHeight());
            Surface surface = new Surface(surfacetexture);
            capturerequestbuilder = cameradevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            capturerequestbuilder.addTarget(surface);

            cameradevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    if (null == cameradevice) {
                        return;
                    }
                    camerasession = cameraCaptureSession;
                    updatepreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    t("Cofiguration failed");
                }
            }, null);
        } catch (Exception e) {
            t(e.toString());
        }
    }

    public void click(View v) {
        takePicture();
    }


    public void opencamera() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraid = cameraManager.getCameraIdList()[camid];
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraid);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imagedimen = map.getOutputSizes(SurfaceTexture.class)[0];
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            cameraManager.openCamera(cameraid, statecallback, null);

        }catch(Exception e){
            t(e.toString());
        }
}

public void updatepreview(){
        if(cameradevice==null){
            t("Camera Device is null");
            return;
        }
        capturerequestbuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

        try{
            camerasession.setRepeatingRequest(capturerequestbuilder.build(),null,backgroundhandler);
        } catch(Exception e){
            t(e.toString());
        }

}

public void closeCamera(){
        if(null!=cameradevice){
            cameradevice.close();
            cameradevice=null;
        }

        if(null!=imagereader){
            imagereader.close();
            imagereader=null;
        }
}


@Override
protected void onPause(){
        stopBackgroundThread();
        super.onPause();
}

@Override
protected void onResume(){
        super.onResume();
        startBackgroundThread();
        if(texture.isAvailable()){
            opencamera();
        } else {
            texture.setSurfaceTextureListener(texturelistener);

        }
}

public void changecam(View v){
        closeCamera();
        if(camid==rearcam){
            camid=frontcam;
        } else {
            camid=rearcam;
        }
        opencamera();
}


    public void initnavstatusbar(){
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(70,70,70)));
        Window win=getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        win.setStatusBarColor(Color.rgb(70,70,70));
        win.setNavigationBarColor(Color.rgb(70,70,70));
    }

    public void gettransparentdisplay(){
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setWindowFlag(this,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    public void setWindowFlag(Activity act,final int bits,boolean on){
        Window win=act.getWindow();
        WindowManager.LayoutParams winparams=win.getAttributes();

        if(on){
            winparams.flags|=bits;
        } else {
            winparams.flags&=-bits;
        }
        win.setAttributes(winparams);
    }


    public void t(final String s){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CameraActivity.this, s, Toast.LENGTH_LONG).show();
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

public String sendphototoserver(String uname,File f){
  try {
      multi mul=new multi(SplashActivity.WEB_SERVER_URL,SplashActivity.CHAR_SET);
      mul.addFormField("num","1");
      mul.addFormField(SplashActivity.USERNAME,uname);
      mul.addFilePart("file",f);
      String res=mul.finish();
      return res;
  } catch(Exception e){
      t(e.toString());
      return e.toString();
  }
}


public void uploadremainingphotos(){
        new Thread(){
            public void run(){
                try {
                    Properties p=loadproperties(propfile);
                    String s=p.getProperty(SplashActivity.IMG_NOT_UPLOADED);

                    if(s!=null&&checkconnection()){
                        t("Uploading Images");
                        String[] sarr=s.split(";");
                        multi mul=new multi(SplashActivity.WEB_SERVER_URL,SplashActivity.CHAR_SET);
                        mul.addFormField("num",sarr.length+"");
                        mul.addFormField(SplashActivity.USERNAME,p.getProperty(SplashActivity.USERNAME));

                        for(int i=0;i<sarr.length;i++){
                            mul.addFilePart((i+1)+"",new File(getExternalCacheDir(),sarr[i]));
                        }
                        String res=mul.finish();
                        t("Uploading all Images "+res);
                        p.remove(SplashActivity.IMG_NOT_UPLOADED);
                    }
                    saveproperties(p,propfile);


                } catch(Exception e){
                    t(e.toString());
                }


            }
        }.start();
}

}