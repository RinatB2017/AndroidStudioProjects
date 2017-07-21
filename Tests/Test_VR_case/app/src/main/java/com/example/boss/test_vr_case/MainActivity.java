package com.example.boss.test_vr_case;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

// http://startandroid.ru/ru/uroki/vse-uroki-spiskom/408-urok-176-opengl-indeksy-tekstury-dlja-kuba.html

public class MainActivity extends Activity {

    private GLSurfaceView glSurfaceView_1;
    private GLSurfaceView glSurfaceView_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!supportES2()) {
            Toast.makeText(this, "OpenGl ES 2.0 is not supported", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        float delta = 0.0f;

        Bundle bundle1 = new Bundle();
        bundle1.putFloat("value_X", 3); //0
        bundle1.putFloat("value_Y", 2 + delta); //2
        bundle1.putFloat("value_Z", 7); //4

        OpenGLRenderer renderer_1 = new OpenGLRenderer(this, bundle1);

        Bundle bundle2 = new Bundle();
        bundle2.putFloat("value_X", 3); //0
        bundle2.putFloat("value_Y", 2 - delta); //2
        bundle2.putFloat("value_Z", 7); //4

        OpenGLRenderer renderer_2 = new OpenGLRenderer(this, bundle2);

        glSurfaceView_1 = new GLSurfaceView(this);
        glSurfaceView_1.setEGLContextClientVersion(2);
        glSurfaceView_1.setRenderer(renderer_1);

        glSurfaceView_2 = new GLSurfaceView(this);
        glSurfaceView_2.setEGLContextClientVersion(2);
        glSurfaceView_2.setRenderer(renderer_2);

        // создание LinearLayout
        LinearLayout linLayout = new LinearLayout(this);
        // установим вертикальную ориентацию
        linLayout.setOrientation(LinearLayout.VERTICAL);
        // создаем LayoutParams
        LinearLayout.LayoutParams linLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        // займем весь экран
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LOW_PROFILE );
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        //---

        // устанавливаем linLayout как корневой элемент экрана
        setContentView(linLayout, linLayoutParam);

        // если хотим, чтобы приложение постоянно имело портретную ориентацию
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        LinearLayout.LayoutParams stretch = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);

        linLayout.addView(glSurfaceView_1, stretch);
        linLayout.addView(glSurfaceView_2, stretch);
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView_1.onPause();
        glSurfaceView_2.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView_1.onResume();
        glSurfaceView_2.onResume();
    }

    private boolean supportES2() {
        ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        return (configurationInfo.reqGlEsVersion >= 0x20000);
    }

}
