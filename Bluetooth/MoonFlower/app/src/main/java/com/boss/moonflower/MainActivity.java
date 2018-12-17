package com.boss.moonflower;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

// если на смартфоне стоит Android 6.0, то надо поставить в свойствах app
// Flawors Target SDK Version API22
// иначе bluetooth не будет находить устройства

public class MainActivity extends AppCompatActivity
        implements View.OnTouchListener, SeekBar.OnSeekBarChangeListener {

    private static final int RECORD_REQUEST_CODE = 101;
    final String LOG_TAG = "States";

    TextView tv_log;

    private Paint mPaint;
    Canvas c_bitmap;
    ImageView main_view;

    TextView x0;
    TextView y0;

    Bitmap bitmap;

    boolean flag_is_enable = false;
    SeekBar sb_hot;
    SeekBar sb_cold;

    //---
    LED_points points;

    TextView tv_hot;
    TextView tv_cold;
    Button btn_power_on;
    Button btn_power_off;
    //---

    TabHost tabHost;

    float center_x;
    float center_y;

    float center_r;

    float led_r;
    float min_r;
    float max_r;
    float min_angle;
    float max_angle;
    int inc_r;
    float temp_x;
    float temp_y;

    int background = Color.BLACK;
    int color_border_on = Color.GREEN;
    int color_border_off = Color.GRAY;
    int text_color = Color.WHITE;

    int DEFAULT_HOT_COLOR = 10;
    int DEFAULT_COLD_COLOR = 10;

    byte[][] leds = new byte[6][6];
    int[][] leds_arr = {
            {0x2112, 0x2255, 0x4223, 0x4314, 0x5415, 0x2001},
            {0x4151, 0x3245, 0x5213, 0x5334, 0x0510, 0x4011},
            {0x3102, 0x2535, 0x0333, 0x0424, 0x4400, 0x3050}};
    //---

    ModBus modbus;

    Bluetooth bt;

    Handler h_print;

    //---------------------------------------------------------------------------------------------
    public void draw_led(int num) {
        if (bitmap == null) {
            return;
        }

        if (c_bitmap == null) {
            return;
        }

        LED led = points.get(num);
        if (led.is_active)
            mPaint.setColor(led.color_border_on);
        else
            mPaint.setColor(led.color_border_off);

        mPaint.setStyle(Paint.Style.STROKE);
        //mPaint.setAntiAlias(true);

        c_bitmap.drawCircle(led.center_x,
                led.center_y,
                led.radius,
                mPaint);


        //---
        final RectF circle = new RectF();
        circle.set(led.center_x - led.radius,
                led.center_y - led.radius,
                led.center_x + led.radius,
                led.center_y + led.radius);

        if(num != 0) {  //центр без полукругов
            // hot
            mPaint.setColor(Color.rgb(led.hot_color, 0, 0));
            mPaint.setStyle(Paint.Style.FILL);
            c_bitmap.drawArc(circle,
                    90, 180,
                    true,
                    mPaint);
            //---

            // cold
            mPaint.setColor(Color.rgb(0, 0, led.cold_color));
            mPaint.setStyle(Paint.Style.FILL);
            c_bitmap.drawArc(circle,
                    270, 180,
                    true,
                    mPaint);
            //sweepAngle - на сколько градусов рисуем от startAngle
            //---
        }

        //FIXME исправить позже
        if (points.get(num).draw_text) {
            int fontSize = (int) (led.radius * 1.37 + 0.5); //58; //80;

            String text = points.get(num).text;
            mPaint.setColor(points.get(num).color_text);

            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setTextSize(fontSize);

            Paint fontPaint;
            fontPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            fontPaint.setTextSize(fontSize);
            float width = fontPaint.measureText(text);

            Rect bounds = new Rect();
            fontPaint.getTextBounds(text, 0, 1, bounds);

            float height = bounds.height();

            c_bitmap.drawText(text,
                    points.get(num).center_x - width / 2,
                    points.get(num).center_y + height / 2,
                    mPaint);
        }
    }

    //---------------------------------------------------------------------------------------------
    public void redraw() {
        main_view.setImageBitmap(bitmap);
    }

    //---------------------------------------------------------------------------------------------
    public void send_log(String text) {
        if (text == null) {
            return;
        }
        Message msg = new Message();
        msg.obj = text;
        h_print.sendMessage(msg);
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //---------------------------------------------------------------------------------------------
    void set_setting() {
        for (int n = 0; n < points.size(); n++) {
            points.get(n).color_border_on = color_border_on;
            points.get(n).color_border_off = color_border_off;
            points.get(n).color_text = text_color;

            draw_led(n);
        }
        redraw();

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setBackgroundDrawable(new ColorDrawable(background));
        }

        LinearLayout main_layout = (LinearLayout) findViewById(R.id.main_layout);
        main_layout.setBackgroundColor(background);

        LinearLayout tab_main = (LinearLayout) findViewById(R.id.tab_main);
        tab_main.setBackgroundColor(background);

        LinearLayout tab_log = (LinearLayout) findViewById(R.id.tab_log);
        tab_log.setBackgroundColor(background);

        LinearLayout tab_buttons = (LinearLayout) findViewById(R.id.tab_buttons);
        tab_buttons.setBackgroundColor(background);

        x0.setTextColor(text_color);
        y0.setTextColor(text_color);
        tv_cold.setTextColor(text_color);
        tv_hot.setTextColor(text_color);

        //заремарено из-за 4.0.4
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            sb_cold.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            sb_cold.getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

            sb_hot.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            sb_hot.getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        }
    }

    //---------------------------------------------------------------------------------------------
    void setting_RED() {
        background = Color.RED;
        color_border_on = Color.GREEN;
        color_border_off = Color.GRAY;

        set_setting();
    }

    //---------------------------------------------------------------------------------------------
    void setting_GREEN() {
        background = Color.GREEN;
        color_border_on = Color.YELLOW;
        color_border_off = Color.GRAY;

        set_setting();
    }

    //---------------------------------------------------------------------------------------------
    void setting_BLUE() {
        background = Color.BLUE;
        color_border_on = Color.GREEN;
        color_border_off = Color.GRAY;

        set_setting();
    }

    //---------------------------------------------------------------------------------------------
    void setting_GRAY() {
        background = Color.BLACK;
        color_border_on = Color.GREEN;
        color_border_off = Color.GRAY;

        set_setting();
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings_scan:
                bt.device_connect();
                break;

            case R.id.action_settings_disconnect:
                bt.device_disconnect();
                break;

            case R.id.action_settings_options:
                Intent intent = new Intent(this, OptionsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (intent != null) {
                    startActivity(intent);
                }
                break;

            case R.id.clear_log:
                tv_log.setText("");
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //---------------------------------------------------------------------------------------------
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
    void max_screen() {
        // займем весь экран
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION           // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN                // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    //---------------------------------------------------------------------------------------------
    void init_log() {
        tv_log = (TextView) findViewById(R.id.logView);
        tv_log.setTextColor(Color.WHITE);

        h_print = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String text = (String) msg.obj;
                Log.i(LOG_TAG, text);
                tv_log.append(text + "\n");
            }
        };
    }

    //---------------------------------------------------------------------------------------------
    void init_tabs() {
        tabHost = (TabHost) findViewById(R.id.tabHost);

        tabHost.setup();

        TabHost.TabSpec tabSpec;

        tabSpec = tabHost.newTabSpec("tab_main");
        tabSpec.setContent(R.id.tab_main);
        tabSpec.setIndicator(getString(R.string.main));
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tab_log");
        tabSpec.setContent(R.id.tab_log);
        tabSpec.setIndicator(getString(R.string.log));
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTab(0);
        //---

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;

        TabWidget tabWidget = tabHost.getTabWidget();
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            tabHost.getTabWidget().getChildAt(i).setLayoutParams(layoutParams);
            TextView textView = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);
            textView.setTextColor(Color.WHITE);
        }
    }

    //---------------------------------------------------------------------------------------------
    void init_widgets() {
        x0 = (TextView) findViewById(R.id.x0);
        y0 = (TextView) findViewById(R.id.y0);

        main_view = (ImageView) findViewById(R.id.main_view);

        sb_cold = (SeekBar) findViewById(R.id.sb_cold);
        sb_hot = (SeekBar) findViewById(R.id.sb_hot);

        sb_hot.setOnSeekBarChangeListener(this);
        sb_cold.setOnSeekBarChangeListener(this);

        tv_cold = (TextView) findViewById(R.id.tv_cold);
        tv_hot = (TextView) findViewById(R.id.tv_hot);

        btn_power_on = (Button) findViewById(R.id.btn_power_on);
        btn_power_off = (Button) findViewById(R.id.btn_power_off);

        btn_power_on.setOnClickListener(btn_power_apply_on);
        btn_power_off.setOnClickListener(btn_power_apply_off);
    }

    //---------------------------------------------------------------------------------------------
    /*
    for (int index = 0; index < points.size(); index++) {
        savedInstanceState.putInt("led_" + String.valueOf(index) + "_number", points.get(index).number);
        savedInstanceState.putInt("led_" + String.valueOf(index) + "_address", points.get(index).address);                     // адрес светодиода
        savedInstanceState.putFloat("led_" + String.valueOf(index) + "_center_x", points.get(index).center_x);                 // координаты центра
        savedInstanceState.putFloat("led_" + String.valueOf(index) + "_center_y", points.get(index).center_y);                 // координаты центра
        savedInstanceState.putFloat("led_" + String.valueOf(index) + "_radius", points.get(index).radius);                     // радиус
        savedInstanceState.putInt("led_" + String.valueOf(index) + "_color_border_on", points.get(index).color_border_on);     // цвет бордюра светодиода (ON)
        savedInstanceState.putInt("led_" + String.valueOf(index) + "_color_border_off", points.get(index).color_border_off);   // цвет бордюра светодиода (OFF)
        savedInstanceState.putBoolean("led_" + String.valueOf(index) + "_is_active", points.get(index).is_active);             // сейчас активен для изменения
        savedInstanceState.putInt("led_" + String.valueOf(index) + "_hot_color", points.get(index).hot_color);                 // уровень "теплого" цвета
        savedInstanceState.putInt("led_" + String.valueOf(index) + "_cold_color", points.get(index).cold_color);               // уровень "холодного" цвета
        savedInstanceState.putBoolean("led_" + String.valueOf(index) + "_draw_text", points.get(index).draw_text);             // нужен текст?
        savedInstanceState.putInt("led_" + String.valueOf(index) + "_color_text", points.get(index).color_text);               // цвет текста
        savedInstanceState.putString("led_" + String.valueOf(index) + "_text", points.get(index).text);                        // текст, выводимый на светодиод
    }
    */
    //---------------------------------------------------------------------------------------------
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (points == null) {
            send_log("points is null");
        } else {
            int s = points.size();
            if (s == 19) {
                send_log("points size: " + s);
                savedInstanceState.putSerializable("led_points", points);
            }
        }

        //send_log("onSaveInstanceState");

        // Always call the superclass so it can save the view hierarchy state
        savedInstanceState.putInt("current_tab", tabHost.getCurrentTab());

        super.onSaveInstanceState(savedInstanceState);
    }

    //---------------------------------------------------------------------------------------------
    /*
    points = new ArrayList<LED>();
    points = new LED_points();
    for (int index = 0; index < 19; index++) {
        LED led = new LED();
        led.number = savedInstanceState.getInt("led_" + String.valueOf(index) + "_number");
        led.address = savedInstanceState.getInt("led_" + String.valueOf(index) + "_address");                     // адрес светодиода
        led.center_x = savedInstanceState.getFloat("led_" + String.valueOf(index) + "_center_x");                 // координаты центра
        led.center_y = savedInstanceState.getFloat("led_" + String.valueOf(index) + "_center_y");                 // координаты центра
        led.radius = savedInstanceState.getFloat("led_" + String.valueOf(index) + "_radius");                     // радиус
        led.color_border_on = savedInstanceState.getInt("led_" + String.valueOf(index) + "_color_border_on");     // цвет бордюра светодиода (ON)
        led.color_border_off = savedInstanceState.getInt("led_" + String.valueOf(index) + "_color_border_off");   // цвет бордюра светодиода (OFF)
        led.is_active = savedInstanceState.getBoolean("led_" + String.valueOf(index) + "_is_active");             // сейчас активен для изменения
        led.hot_color = savedInstanceState.getInt("led_" + String.valueOf(index) + "_hot_color");                 // уровень "теплого" цвета
        led.cold_color = savedInstanceState.getInt("led_" + String.valueOf(index) + "_cold_color");               // уровень "холодного" цвета
        led.draw_text = savedInstanceState.getBoolean("led_" + String.valueOf(index) + "_draw_text");             // нужен текст?
        led.color_text = savedInstanceState.getInt("led_" + String.valueOf(index) + "_color_text");               // цвет текста
        led.text = savedInstanceState.getString("led_" + String.valueOf(index) + "_text");                        // текст, выводимый на светодиод

        //points.add(led);  //TODO
        points.set(index, led);
    }
    */
    //---------------------------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init_log();

        modbus = new ModBus();
        bt = new Bluetooth(MainActivity.this, tv_log);
        requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, RECORD_REQUEST_CODE);

        mPaint = new Paint();

        if (savedInstanceState != null) {
            points = (LED_points) savedInstanceState.getSerializable("led_points");
        } else {
            points = new LED_points();

            Bundle bundle = new Bundle();
            getIntent().putExtras(bundle);
        }
        //---

        init_widgets();

        init_tabs();

        if (savedInstanceState != null) {
            int current_tab = savedInstanceState.getInt("current_tab");
            tabHost.setCurrentTab(current_tab);
        }
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        create_widgets();
        //setting_RED();
        //setting_GREEN();
        //setting_BLUE();
        setting_GRAY();
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onStart() {
        super.onStart();
        bt.device_connect();
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onRestart() {
        super.onRestart();
        bt.device_connect();
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onResume() {
        super.onResume();
        //send_log("onResume()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onPause() {
        super.onPause();
        bt.device_disconnect();
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onStop() {
        super.onStop();
        //send_log("onStop()");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //send_log("onDestroy()");
    }

    //---------------------------------------------------------------------------------------------
    public void create_widgets() {
        Display display = getWindowManager().getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        //send_log("p.x " + p.x);
        //send_log("p.y " + p.y);

        tabHost.measure(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        int s_tabHost = tabHost.getMeasuredHeight();
        //send_log("measuredHeight " + s_tabHost);

        int c_view = tabHost.getTabContentView().getHeight();

        int size = 0;

        int rotate = getWindowManager().getDefaultDisplay().getRotation();
        switch (rotate) {
            case Surface.ROTATION_0:
            case Surface.ROTATION_180:
                // ORIENTATION_PORTRAIT
                size = p.x;
                break;

            case Surface.ROTATION_90:
            case Surface.ROTATION_270:
                // ORIENTATION_LANDSCAPE
                size = c_view;
                break;
        }

        //send_log("size " + size);

        //TODO bitmap
        bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        center_x = bitmap.getWidth() / 2.0f;
        center_y = bitmap.getHeight() / 2.0f;

        center_r = bitmap.getWidth() / 14.0f;

        led_r = bitmap.getWidth() / 17.0f;
        min_r = center_r + led_r + 10.0f;
        max_r = bitmap.getWidth() / 2.0f - 20.0f;
        inc_r = (int) ((max_r - min_r) / 2.4f);
        //---

        //---
        mPaint.setStrokeWidth(5);
        //---
        c_bitmap = new Canvas(bitmap);

        //---
        new_draw_field();
        //---

        //---
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
        main_view.setLayoutParams(layoutParams);
        main_view.setOnTouchListener(this);
        //---
    }

    //---------------------------------------------------------------------------------------------
    public void redraw_all_buttons() {
        for (int n = 0; n < points.size(); n++) {
            draw_led(n);
        }
        redraw();
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public boolean onTouch(View view, MotionEvent event) {

        if (flag_is_enable == false) {
            return false;
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();// - diff_x;
            float y = event.getY();

            Log.i(LOG_TAG, "x=" + x + " y=" + y);

            LED led;
            for (int index = 0; index < points.size(); index++) {
                led = points.get(index);
                double xx = (x - led.center_x) * (x - led.center_x);
                double yy = (y - led.center_y) * (y - led.center_y);
                float line = (float) Math.sqrt(xx + yy);
                if (line < led.radius) {
                    led.is_active = !led.is_active;

                    if (index == 0) {
                        for (int n = 0; n < points.size(); n++) {
                            points.get(n).is_active = led.is_active;
                        }
                        redraw_all_buttons();
                        //говнокод рулит, а план горит :)
                        return true;
                    }
                    //save_led(index);    //TODO

                    points.set(index, led);
                    if (led.is_active)
                        mPaint.setColor(color_border_on);
                    else
                        mPaint.setColor(color_border_off);

                    mPaint.setStyle(Paint.Style.STROKE);
                    //mPaint.setAntiAlias(true);
                    c_bitmap.drawCircle(led.center_x,
                            led.center_y,
                            led.radius - 1,
                            mPaint);

                    //FIXME исправить позже
                    if (points.get(index).draw_text) {
                        int fontSize = 80;

                        String text = points.get(index).text;
                        mPaint.setColor(points.get(index).color_text);

                        mPaint.setStyle(Paint.Style.FILL);
                        mPaint.setTextSize(fontSize);

                        Paint fontPaint;
                        fontPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                        fontPaint.setTextSize(fontSize);
                        float width = fontPaint.measureText(text);

                        Rect bounds = new Rect();
                        fontPaint.getTextBounds(text, 0, 1, bounds);

                        float height = bounds.height();

                        c_bitmap.drawText(text,
                                points.get(index).center_x - width / 2,
                                points.get(index).center_y + height / 2,
                                mPaint);
                    }
                    //---
                    redraw();
                    //---
                    return true;
                }
            }
        }
        return false;
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public void onProgressChanged(SeekBar seekBar,
                                  int progress,
                                  boolean fromUser) {
        if (seekBar == sb_hot) {
            tv_hot.setText(String.valueOf(seekBar.getProgress()));
            if (points != null) {
                for (int n = 0; n < points.size(); n++) {
                    if (points.get(n).is_active) {
                        points.get(n).hot_color = progress * 255 / 100;
                        draw_led(n);
                    }
                }
                redraw();
                send_command();
            }
        }
        if (seekBar == sb_cold) {
            tv_cold.setText(String.valueOf(seekBar.getProgress()));
            if (points != null) {
                for (int n = 0; n < points.size(); n++) {
                    if (points.get(n).is_active) {
                        points.get(n).cold_color = progress * 255 / 100;
                        draw_led(n);
                    }
                }
                redraw();
                send_command();
            }
        }
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //log.append("onStartTrackingTouch");
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //log.append("onStopTrackingTouch");
    }

    //---------------------------------------------------------------------------------------------
    public void send_command() {
        Runnable runnable = new Runnable() {
            public void run() {
                check_button();

                ModBus modbus = new ModBus();
                modbus.set_address(0);
                modbus.set_command(1);
                modbus.set_data(get_data());

                boolean ok = send_modbus_data(modbus.get_string());
                if (ok) {
                    send_log("Данные переданы.");
                } else {
                    send_log("Ошибка соединения.");
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    //---------------------------------------------------------------------------------------------
    public void send_power_off() {
        Runnable runnable = new Runnable() {
            public void run() {
                check_button();

                ModBus modbus = new ModBus();
                modbus.set_address(0);
                modbus.set_command(1);

                ByteArrayOutputStream data;
                data = new ByteArrayOutputStream();
                for (int y = 0; y < 6; y++) {
                    for (int x = 0; x < 6; x++) {
                        data.write(0);
                    }
                }
                modbus.set_data(data);

                boolean ok = send_modbus_data(modbus.get_string());
                if (ok) {
                    send_log("Данные переданы.");
                } else {
                    send_log("Ошибка соединения.");
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    //---------------------------------------------------------------------------------------------
    public boolean send_modbus_data(String message) {
        return bt.send_data(message);
    }

    //---------------------------------------------------------------------------------------------
    private void new_draw_field() {

        points.get(0).number = 0;
        points.get(0).center_x = center_x;
        points.get(0).center_y = center_y;
        points.get(0).radius = center_r;
        points.get(0).hot_color = DEFAULT_HOT_COLOR;
        points.get(0).cold_color = DEFAULT_COLD_COLOR;
        points.get(0).address = 0;

        min_angle = -30.0f;
        max_angle = 330.0f;

        float angle = min_angle;
        int x = 5;
        /*
        рисуем по часовой стрелке
        */
        int number = 1; //центр уже нарисовали ранее

//        calc_line(center_x,
//                center_y,
//                45.0f,
//                1000);
//        mPaint.setColor(Color.GREEN);
//        c_bitmap.drawLine(center_x, center_y, temp_x, temp_y, mPaint);

        while (angle < max_angle) {
            for (int n = 0; n < 3; n++) {
                calc_line(center_x,
                        center_y,
                        angle,
                        inc_r * (n + 1));
                c_bitmap.drawCircle(temp_x,
                        temp_y,
                        led_r,
                        mPaint);

                LED s_led = new LED();
                s_led.number = number;
                s_led.center_x = temp_x;
                s_led.center_y = temp_y;
                s_led.radius = led_r;
                s_led.address = leds_arr[n][x];

                //FIXME исправить позже

                /*
                s_led.text = String.valueOf(s_led.number);
                s_led.draw_text = true;
                */

                if (n == 2) {
                    if (angle == 270) s_led.text = "1";
                    if (angle == -30) s_led.text = "2";
                    if (angle == 30)  s_led.text = "3";
                    if (angle == 90)  s_led.text = "4";
                    if (angle == 150) s_led.text = "5";
                    if (angle == 210) s_led.text = "6";
                    s_led.draw_text = true;
                } else {
                    s_led.draw_text = false;
                }
                //---

                points.get(number).number = s_led.number;
                points.get(number).address = s_led.address;
                points.get(number).center_x = s_led.center_x;
                points.get(number).center_y = s_led.center_y;
                points.get(number).radius = s_led.radius;
                points.get(number).color_border_on = s_led.color_border_on;
                points.get(number).color_border_off = s_led.color_border_off;
                points.get(number).draw_text = s_led.draw_text;
                points.get(number).color_text = s_led.color_text;
                points.get(number).text = s_led.text;

                number++;
            }
            angle += 60.0f;
            x--;
        }
        redraw();
    }

    //---------------------------------------------------------------------------------------------
    private void calc_line(float x,
                           float y,
                           float angle,
                           float radius) {
        float A = radius;
        float B = (float) Math.cos(Math.toRadians(angle)) * A;
        float C = (float) Math.sin(Math.toRadians(angle)) * A;

        temp_x = x + B;
        temp_y = y + C;
    }

    //---------------------------------------------------------------------------------------------
    public void block_interface(boolean state) {
        flag_is_enable = !state;
        if(sb_cold != null) sb_cold.setEnabled(!state);
        if(sb_hot != null)  sb_hot.setEnabled(!state);
        if(btn_power_on != null)  btn_power_on.setEnabled(!state);
        if(btn_power_off != null) btn_power_off.setEnabled(!state);
    }

    //---------------------------------------------------------------------------------------------
    public boolean analize_answer(byte[] data) throws UnsupportedEncodingException {
        String str = new String(data, "UTF-8"); // for UTF-8 encoding
        Log.i(LOG_TAG, "data " + str);
        Log.i(LOG_TAG, "analize_answer: read " + str.length() + " bytes"); //TODO проверка приема после передачи
        return true;
    }

    //---------------------------------------------------------------------------------------------
    public ByteArrayOutputStream get_data() {
        ByteArrayOutputStream data;
        data = new ByteArrayOutputStream();
        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 6; x++) {
                data.write(leds[x][y]);
            }
        }
        return data;
    }

    //---------------------------------------------------------------------------------------------
    public void set_led_data(int value, int index) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) ((value >> 8) & 0xFF);
        bytes[1] = (byte) (value & 0xFF);

        byte a = (byte) ((bytes[0] >> 4) & 0x0F);
        byte b = (byte) (bytes[0] & 0x0F);
        byte c = (byte) ((bytes[1] >> 4) & 0x0F);
        byte d = (byte) (bytes[1] & 0x0F);

        int value_hot = points.get(index).hot_color;
        int value_cold = points.get(index).cold_color;

        leds[a][b] = (byte) value_cold;
        leds[c][d] = (byte) value_hot;
    }

    //---------------------------------------------------------------------------------------------
    public void set_led_data(int value, int value_hot, int value_cold) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) ((value >> 8) & 0xFF);
        bytes[1] = (byte) (value & 0xFF);

        byte a = (byte) ((bytes[0] >> 4) & 0x0F);
        byte b = (byte) (bytes[0] & 0x0F);
        byte c = (byte) ((bytes[1] >> 4) & 0x0F);
        byte d = (byte) (bytes[1] & 0x0F);

        leds[a][b] = (byte) value_cold;
        leds[c][d] = (byte) value_hot;
    }

    //---------------------------------------------------------------------------------------------
    public void check_button() {
        for (int n = 0; n < points.size(); n++) {
            set_led_data(points.get(n).address, n);
        }
    }

    //---------------------------------------------------------------------------------------------
    void power_on() {
        //send_log("ON");
        send_command();
    }

    //---------------------------------------------------------------------------------------------
    void power_off() {
        //send_log("OFF");
        send_power_off();
    }

    //---------------------------------------------------------------------------------------------
    View.OnClickListener btn_power_apply_on = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //send_log("OK");
            power_on();
        }
    };

    //---------------------------------------------------------------------------------------------
    View.OnClickListener btn_power_apply_off = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //send_log("OK");
            power_off();
        }
    };

    //---------------------------------------------------------------------------------------------
    void test() {
        Runnable runnable = new Runnable() {
            public void run() {
                ModBus modbus = new ModBus();
                modbus.set_address(0);
                modbus.set_command(1);

                ByteArrayOutputStream data;
                data = new ByteArrayOutputStream();
                for(int n=0; n<points.size(); n++)
                {
                    set_led_data(points.get(n).address, 0, 0);
                }
                for(int n=0; n<points.size(); n++)
                {
                    if(points.get(n).number == 1) {
                        set_led_data(points.get(n).address, 0xFF, 0xFF);
                    }
                    if(points.get(n).number == 2) {
                        set_led_data(points.get(n).address, 0xFF, 0xFF);
                    }
                    if(points.get(n).number == 3) {
                        set_led_data(points.get(n).address, 0xFF, 0xFF);
                    }
                    if(points.get(n).number == 4) {
                        set_led_data(points.get(n).address, 0xFF, 0xFF);
                    }
                    if(points.get(n).number == 5) {
                        set_led_data(points.get(n).address, 0xFF, 0xFF);
                    }
                    if(points.get(n).number == 6) {
                        set_led_data(points.get(n).address, 0xFF, 0xFF);
                    }
                }
                modbus.set_data(get_data());

                boolean ok = send_modbus_data(modbus.get_string());
                if (ok) {
                    send_log("Данные переданы.");
                } else {
                    send_log("Ошибка соединения.");
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    //---------------------------------------------------------------------------------------------
}