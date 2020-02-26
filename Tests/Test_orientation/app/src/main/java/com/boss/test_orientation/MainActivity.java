package com.boss.test_orientation;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    LinearLayout linLayout;
    LinearLayout.LayoutParams layoutParams;

    TextView tv_X1;
    TextView tv_Y1;
    TextView tv_Z1;

    TextView tv_X2;
    TextView tv_Y2;
    TextView tv_Z2;

    Button btn_X_plus;
    Button btn_X_minus;

    Button btn_Y_plus;
    Button btn_Y_minus;

    Button btn_Z_plus;
    Button btn_Z_minus;

    GLSurfaceView glSurfaceView;
    OpenGLRenderer main_view;

    SensorManager sensorManager;
    Sensor sensorAccel;
    Sensor sensorMagnet;

    float X = 0;
    float Y = 0;
    float Z = 0;
    float delta = 5;

    StringBuilder sb = new StringBuilder();

    Timer timer;

    int rotation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // создание LinearLayout
        linLayout = new LinearLayout(this);
        layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 10, 10, 10);

        // установим вертикальную ориентацию
        linLayout.setOrientation(LinearLayout.VERTICAL);

        Bundle bundle1 = new Bundle();
        bundle1.putFloat("value_X", 0); //0
        bundle1.putFloat("value_Y", 0); //2
        bundle1.putFloat("value_Z", 4); //4

        main_view = new OpenGLRenderer(this, bundle1);
        main_view.set_angle(0, 0, 0);

        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(main_view);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagnet = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        init_widgets();
    }

    void init_widgets() {
        // создаем LayoutParams
        LinearLayout.LayoutParams linLayoutParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        LinearLayout.LayoutParams stretch = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);

        Button btn = new Button(this);
        btn.setText("test");

        View.OnClickListener oclBtnOk = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                X = 0;
                Y = 0;
                Z = 0;
                main_view.set_angle(X, Y, Z);
            }
        };

        //---
        View.OnClickListener btn_X_plus_click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                X = X + delta;
                main_view.set_angle(X, Y, Z);
            }
        };
        View.OnClickListener btn_Y_plus_click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Y = Y + delta;
                main_view.set_angle(X, Y, Z);
            }
        };
        View.OnClickListener btn_Z_plus_click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Z = Z + delta;
                main_view.set_angle(X, Y, Z);
            }
        };
        View.OnClickListener btn_X_minus_click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                X = X - delta;
                main_view.set_angle(X, Y, Z);
            }
        };
        View.OnClickListener btn_Y_minus_click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Y = Y - delta;
                main_view.set_angle(X, Y, Z);
            }
        };
        View.OnClickListener btn_Z_minus_click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Z = Z - delta;
                main_view.set_angle(X, Y, Z);
            }
        };
        //---
        // присвоим обработчик кнопке OK (btnOk)
        btn.setOnClickListener(oclBtnOk);

        //---
        tv_X1 = new TextView(this);
        tv_Y1 = new TextView(this);
        tv_Z1 = new TextView(this);

        tv_X2 = new TextView(this);
        tv_Y2 = new TextView(this);
        tv_Z2 = new TextView(this);

        tv_X1.setGravity(Gravity.CENTER);
        tv_Y1.setGravity(Gravity.CENTER);
        tv_Z1.setGravity(Gravity.CENTER);

        tv_X2.setGravity(Gravity.CENTER);
        tv_Y2.setGravity(Gravity.CENTER);
        tv_Z2.setGravity(Gravity.CENTER);

        tv_X1.setTextSize(24);
        tv_Y1.setTextSize(24);
        tv_Z1.setTextSize(24);

        tv_X2.setTextSize(24);
        tv_Y2.setTextSize(24);
        tv_Z2.setTextSize(24);

        TableRow row0 = new TableRow(this);
        TableRow row1 = new TableRow(this);

        row0.addView(tv_X1);
        row0.addView(tv_Y1);
        row0.addView(tv_Z1);

        row1.addView(tv_X2);
        row1.addView(tv_Y2);
        row1.addView(tv_Z2);

        TableLayout table = new TableLayout(this);
        table.setColumnStretchable(1, true);

        table.addView(row0);
        table.addView(row1);
        //---

        tv_X1.append("0");
        tv_Y1.append("0");
        tv_Z1.append("0");

        tv_X2.append("0");
        tv_Y2.append("0");
        tv_Z2.append("0");

        //---
        btn_X_plus = new Button(this);
        btn_Y_plus = new Button(this);
        btn_Z_plus = new Button(this);

        btn_X_minus = new Button(this);
        btn_Y_minus = new Button(this);
        btn_Z_minus = new Button(this);

        btn_X_plus.setOnClickListener(btn_X_plus_click);
        btn_Y_plus.setOnClickListener(btn_Y_plus_click);
        btn_Z_plus.setOnClickListener(btn_Z_plus_click);

        btn_X_minus.setOnClickListener(btn_X_minus_click);
        btn_Y_minus.setOnClickListener(btn_Y_minus_click);
        btn_Z_minus.setOnClickListener(btn_Z_minus_click);

        btn_X_plus.setText("X+");
        btn_Y_plus.setText("Y+");
        btn_Z_plus.setText("Z+");

        btn_X_minus.setText("X-");
        btn_Y_minus.setText("Y-");
        btn_Z_minus.setText("Z-");

        TableRow row_btn0 = new TableRow(this);
        TableRow row_btn1 = new TableRow(this);
        TableRow row_btn2 = new TableRow(this);

        row_btn0.addView(btn_X_plus);
        row_btn0.addView(btn_X_minus);

        row_btn1.addView(btn_Y_plus);
        row_btn1.addView(btn_Y_minus);

        row_btn2.addView(btn_Z_plus);
        row_btn2.addView(btn_Z_minus);

        TableLayout table2 = new TableLayout(this);

        table2.addView(row_btn0);
        table2.addView(row_btn1);
        table2.addView(row_btn2);
        //---

        linLayout.addView(table, layoutParams);
        linLayout.addView(glSurfaceView, stretch);
        linLayout.addView(table2, layoutParams);
        linLayout.addView(btn, layoutParams);

        // устанавливаем linLayout как корневой элемент экрана
        setContentView(linLayout, linLayoutParam);
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();

        sensorManager.unregisterListener(listener);
        timer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();

        sensorManager.registerListener(listener,    sensorAccel,    SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listener,    sensorMagnet,   SensorManager.SENSOR_DELAY_NORMAL);

        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getDeviceOrientation();
                        getActualDeviceOrientation();
                        showInfo();
                    }
                });
            }
        };
        timer.schedule(task, 0, 400);

        WindowManager windowManager = ((WindowManager) getSystemService(Context.WINDOW_SERVICE));
        Display display = windowManager.getDefaultDisplay();
        rotation = display.getRotation();
    }

    void showInfo() {
    }

    float[] r = new float[9];

    void getDeviceOrientation() {
        SensorManager.getRotationMatrix(r, null, valuesAccel, valuesMagnet);
        SensorManager.getOrientation(r, valuesResult);

        valuesResult[0] = (float) Math.toDegrees(valuesResult[0]);
        valuesResult[1] = (float) Math.toDegrees(valuesResult[1]);
        valuesResult[2] = (float) Math.toDegrees(valuesResult[2]);

        tv_X1.setText(String.format("%.1f", valuesResult[0]));
        tv_Y1.setText(String.format("%.1f", valuesResult[1]));
        tv_Z1.setText(String.format("%.1f", valuesResult[2]));

//        main_view.set_angle(
//                valuesResult[0],
//                valuesResult[1],
//                valuesResult[2]);
        return;
    }

    float[] inR = new float[9];
    float[] outR = new float[9];

    void getActualDeviceOrientation() {
        SensorManager.getRotationMatrix(inR, null, valuesAccel, valuesMagnet);
        int x_axis = SensorManager.AXIS_X;
        int y_axis = SensorManager.AXIS_Y;
        switch (rotation) {
            case (Surface.ROTATION_0): break;
            case (Surface.ROTATION_90):
                x_axis = SensorManager.AXIS_Y;
                y_axis = SensorManager.AXIS_MINUS_X;
                break;
            case (Surface.ROTATION_180):
                y_axis = SensorManager.AXIS_MINUS_Y;
                break;
            case (Surface.ROTATION_270):
                x_axis = SensorManager.AXIS_MINUS_Y;
                y_axis = SensorManager.AXIS_X;
                break;
            default: break;
        }
        SensorManager.remapCoordinateSystem(inR, x_axis, y_axis, outR);
        SensorManager.getOrientation(outR, valuesResult2);
        valuesResult2[0] = (float) Math.toDegrees(valuesResult2[0]);
        valuesResult2[1] = (float) Math.toDegrees(valuesResult2[1]);
        valuesResult2[2] = (float) Math.toDegrees(valuesResult2[2]);

        tv_X2.setText(String.format("%.1f", valuesResult2[0]));
        tv_Y2.setText(String.format("%.1f", valuesResult2[1]));
        tv_Z2.setText(String.format("%.1f", valuesResult2[2]));

//        main_view.set_angle(
//                valuesResult2[0] * -1,
//                valuesResult2[1] * -1,
//                valuesResult2[2] * -1);
        return;
    }

    float[] valuesAccel = new float[3];
    float[] valuesMagnet = new float[3];
    float[] valuesResult = new float[3];
    float[] valuesResult2 = new float[3];


    SensorEventListener listener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    for (int i=0; i < 3; i++){
                        valuesAccel[i] = event.values[i];
                    }
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    for (int i=0; i < 3; i++){
                        valuesMagnet[i] = event.values[i];
                    }
                    break;
            }
        }
    };

}
