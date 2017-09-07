package com.boss.test_orientation;

import android.content.DialogInterface;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    LinearLayout linLayout;
    LinearLayout.LayoutParams layoutParams;

    GLSurfaceView glSurfaceView;
    OpenGLRenderer main_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // создаем LayoutParams
        LinearLayout.LayoutParams linLayoutParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        LinearLayout.LayoutParams stretch = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);

        // создание LinearLayout
        linLayout = new LinearLayout(this);
        layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 10, 10, 10);

        // установим вертикальную ориентацию
        linLayout.setOrientation(LinearLayout.VERTICAL);

        Bundle bundle1 = new Bundle();
        bundle1.putFloat("value_X", 3); //0
        bundle1.putFloat("value_Y", 2); //2
        bundle1.putFloat("value_Z", 7); //4

        main_view = new OpenGLRenderer(this, bundle1);

        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(main_view);

        Button btn = new Button(this);
        btn.setText("test");

        Button btn2 = new Button(this);
        btn2.setText("test2");

        // создаем обработчик нажатия
        View.OnClickListener oclBtnOk = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("States", "click");
            }
        };

        // присвоим обработчик кнопке OK (btnOk)
        btn.setOnClickListener(oclBtnOk);

        linLayout.addView(btn, layoutParams);
        linLayout.addView(glSurfaceView, stretch);
        linLayout.addView(btn2, layoutParams);

        // устанавливаем linLayout как корневой элемент экрана
        setContentView(linLayout, linLayoutParam);
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }
}
