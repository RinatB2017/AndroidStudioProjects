package com.example.boss.test_opengl;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

    private OpenGLView mOpenGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        // Go to the title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Set screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mOpenGLView = new OpenGLView(this);
        setContentView(mOpenGLView);
    }
}
