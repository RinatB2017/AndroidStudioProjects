package com.boss.test_flashlight;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

// https://stackoverflow.com/questions/27420594/android-5-camera2-use-only-flash

public class MainActivity extends AppCompatActivity {
    private static final int RECORD_REQUEST_CODE = 101;
    private CameraManager cameraManager;
    private CameraCharacteristics cameraCharacteristics;

    private CameraDevice mCameraDevice;
    private CameraCaptureSession mSession;

    private CaptureRequest.Builder mBuilder;

    private Button on;
    private Button off;

    final String TAG = "States";
    final String log_name = "LOG";
    TextView logView;

    //---------------------------------------------------------------------------------------------
    void logging(String text) {
        logView.append(text + "\n");
        Log.v(TAG, text);
    }
    //---------------------------------------------------------------------------------------------
    void clean_log() {
        logView.setText("");
    }
    //---------------------------------------------------------------------------------------------
    void load_log() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        logView.append(sp.getString(log_name, ""));

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.putString(log_name, "");
        editor.apply();
    }
    //---------------------------------------------------------------------------------------------
    void save_log() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.putString(log_name, logView.getText().toString());
        editor.apply();
    }

    protected void requestPermission(String permissionType, int requestCode) {
        int permission = ContextCompat.checkSelfPermission(this,
                permissionType);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{permissionType}, requestCode
            );
        }
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        on = (Button) findViewById(R.id.on);
        off = (Button) findViewById(R.id.off);

        logView = (TextView)findViewById(R.id.logView);
        load_log();

        requestPermission(Manifest.permission.CAMERA, RECORD_REQUEST_CODE);

        initCamera();
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onResume() {
        super.onResume();

        //initCamera();
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onStop() {
        super.onStop();

        save_log();
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.action_settings_clean_log:
                logView.setText("");
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //---------------------------------------------------------------------------------------------
    public void click(View v) {
        switch (v.getId()) {
            case R.id.on:
                turnOnFlashLight();
                break;
            case R.id.off:
                turnOffFlashLight();
                break;
        }
    }
    //---------------------------------------------------------------------------------------------
    private void initCamera() {
        logging("initCamera");
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] id = cameraManager.getCameraIdList();
            if (id != null && id.length > 0) {
                cameraCharacteristics = cameraManager.getCameraCharacteristics(id[0]);
                boolean isFlash = cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                if (isFlash) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        logging("checkSelfPermission return FALSE");
                        return;
                    }
                    cameraManager.openCamera(id[0], new MyCameraDeviceStateCallback(), null);
                }
            }
        }
        catch (CameraAccessException e)
        {
            logging("initCamera: " + e.getMessage());
            e.printStackTrace();
        }
    }
    //---------------------------------------------------------------------------------------------
    class MyCameraDeviceStateCallback extends CameraDevice.StateCallback
    {
        @Override
        public void onOpened(CameraDevice camera)
        {
            mCameraDevice = camera;
            // get builder
            try
            {
                mBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                List<Surface> list = new ArrayList<Surface>();
                SurfaceTexture mSurfaceTexture = new SurfaceTexture(1);
                Size size = getSmallestSize(mCameraDevice.getId());
                mSurfaceTexture.setDefaultBufferSize(size.getWidth(), size.getHeight());
                Surface mSurface = new Surface(mSurfaceTexture);
                list.add(mSurface);
                mBuilder.addTarget(mSurface);
                camera.createCaptureSession(list, new MyCameraCaptureSessionStateCallback(), null);
            }
            catch (CameraAccessException e)
            {
                logging("MyCameraDeviceStateCallback: " + e.getMessage());
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(CameraDevice camera)
        {

        }

        @Override
        public void onError(CameraDevice camera, int error)
        {

        }
    }
    //---------------------------------------------------------------------------------------------
    private Size getSmallestSize(String cameraId) throws CameraAccessException
    {
        Size[] outputSizes = cameraManager.getCameraCharacteristics(cameraId).get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(SurfaceTexture.class);
        if (outputSizes == null || outputSizes.length == 0)
        {
            throw new IllegalStateException("Camera " + cameraId + "doesn't support any outputSize.");
        }
        Size chosen = outputSizes[0];
        for (Size s : outputSizes)
        {
            if (chosen.getWidth() >= s.getWidth() && chosen.getHeight() >= s.getHeight())
            {
                chosen = s;
            }
        }
        return chosen;
    }
    //---------------------------------------------------------------------------------------------
    class MyCameraCaptureSessionStateCallback extends CameraCaptureSession.StateCallback
    {
        @Override
        public void onConfigured(CameraCaptureSession session)
        {
            mSession = session;
            try
            {
                mSession.setRepeatingRequest(mBuilder.build(), null, null);
            }
            catch (CameraAccessException e)
            {
                logging("MyCameraCaptureSessionStateCallback: " + e.getMessage());
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session)
        {

        }
    }
    //---------------------------------------------------------------------------------------------
    public void turnOnFlashLight()
    {
        try
        {
            mBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
            mSession.setRepeatingRequest(mBuilder.build(), null, null);
        }
        catch (CameraAccessException e) {
            logging("(ameraAccessException) turnOffFlashLight: " + e.getMessage() + " Reason " + String.valueOf(e.getReason()));
            e.printStackTrace();
        }
        catch (Exception e)
        {
            logging("(Exception) turnOnFlashLight: " + e.getMessage());
            e.printStackTrace();
        }
    }
    //---------------------------------------------------------------------------------------------
    public void turnOffFlashLight()
    {
        try
        {
            mBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF);
            mSession.setRepeatingRequest(mBuilder.build(), null, null);
        }
        catch (CameraAccessException e) {
            logging("(CameraAccessException) turnOffFlashLight: " + e.getMessage() + " Reason " + String.valueOf(e.getReason()));
            e.printStackTrace();
        }
        catch (Exception e)
        {
            logging("(Exception) turnOffFlashLight: " + e.getMessage());
            e.printStackTrace();
        }
    }
    //---------------------------------------------------------------------------------------------
    private void close()
    {
        if (mCameraDevice == null || mSession == null)
        {
            return;
        }
        mSession.close();
        mCameraDevice.close();
        mCameraDevice = null;
        mSession = null;
    }
    //---------------------------------------------------------------------------------------------
}
