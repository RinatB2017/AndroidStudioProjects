package com.example.boss.bluetooth_newmoonlight;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//public class MainActivity extends AppCompatActivity
//        implements View.OnTouchListener, SeekBar.OnSeekBarChangeListener, View.OnClickListener {
public class MainActivity extends AppCompatActivity
    implements View.OnTouchListener, SeekBar.OnSeekBarChangeListener {

    private final static int REQUEST_ENABLE_BT = 1;
    final String LOG_TAG = "myLogs";

    private Paint mPaint;
    Canvas c;
    LinearLayout linLayout;
    LinearLayout.LayoutParams layoutParams;
    ImageView main_view;
    TextView log;

    TextView x0;
    TextView y0;

    Bitmap bitmap;

    //Button btn_scan;

    SeekBar sb_hot;
    SeekBar sb_cold;

    TextView tv_hot;
    TextView tv_cold;

    //static final int WIDTH  = 798;
    //static final int HEIGHT = 784;
    static final int WIDTH  = 768;
    static final int HEIGHT = 768;

    static final float center_x = WIDTH / 2.0f;
    static final float center_y = HEIGHT / 2.0f;

    static final float center_r = WIDTH / 14.0f;

    static final float led_r = WIDTH / 17.0f;
    static final float min_r = center_r + led_r + 10.0f;
    static final float max_r = WIDTH / 2.0f - 20.0f;
    static final float min_angle = -30.0f;
    static final float max_angle = 330.0f;
    static final int inc_r = (int)((max_r - min_r) / 2.4f);
    static float temp_x = 0;
    static float temp_y = 0;

    static int background = Color.WHITE;
    static int color_off  = Color.GRAY;
    static int color_on   = Color.rgb(0xD3,0xD3,0xD3);
    //Color.rgb(0x51,0x81,0xB8)

    //---
    BluetoothAdapter bluetooth;
    BluetoothSocket btSocket;
    private BroadcastReceiver discoveryDevicesReceiver;
    private BroadcastReceiver discoveryFinishedReceiver;
    private final List<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();
    private ProgressDialog progressDialog;

    //private static final UUID MY_UUID = UUID.fromString("50001101-0000-1000-8000-00809F9B34FB");
    private static final UUID MY_UUID = UUID.fromString("00000001-0001-0001-0001-000000000001");
    private static final String DEVICE_NAME = "HC-05";
    private static InputStream inputStream;
    private static OutputStream outputStream;

    BluetoothDevice r_device;
    BluetoothSocket tmp = null;
    BluetoothSocket mmSocket = null;
    InputStream tmpIn = null;
    OutputStream tmpOut = null;

    //---
    byte[][] leds = new byte[6][6];
    /*
    {
        for (int y=0; y<6; y++)
        {
            for(int x=0; x<6; x++)
            {
                leds[x][y] = 0;
            }
        }
    }
    */
    int[][] leds_arr = {
            {0x2112, 0x2255, 0x4223, 0x4314, 0x5415, 0x2001},
            {0x4151, 0x3245, 0x5213, 0x5334, 0x0510, 0x4011},
            {0x3102, 0x2535, 0x0333, 0x0424, 0x4400, 0x3050}};
    //---

    ModBus modbus;
    //---
    ArrayList<LED> points;
    //---------------------------------------------------------------------------------------------
    private class LED {
        float   center_x;
        float   center_y;
        float   radius;
        int     color;
        int     address;
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    //---------------------------------------------------------------------------------------------
    void setting_0() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        //btn_scan.setTextColor(Color.WHITE);
        //btn_scan.setBackgroundColor(Color.DKGRAY);
        //btn_scan.setPadding(15,15,15,15);

        background = Color.WHITE;
        color_off  = Color.GRAY;
        color_on   = Color.rgb(0xD3,0xD3,0xD3);

        log.setTextColor(Color.BLACK);

        linLayout.setBackgroundColor(background);
        x0.setTextColor(Color.WHITE);
        y0.setTextColor(Color.WHITE);
        tv_cold.setTextColor(Color.WHITE);
        tv_hot.setTextColor(Color.WHITE);

        sb_cold.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        sb_cold.getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

        sb_hot.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        sb_hot.getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
    }
    //---------------------------------------------------------------------------------------------
    void setting_1() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        //btn_scan.setTextColor(Color.WHITE);
        //btn_scan.setBackgroundColor(Color.DKGRAY);
        //btn_scan.setPadding(15,15,15,15);

        background = Color.WHITE;
        color_off  = Color.GRAY;
        color_on   = Color.RED;

        log.setTextColor(Color.WHITE);

        linLayout.setBackgroundColor(background);
        x0.setTextColor(Color.WHITE);
        y0.setTextColor(Color.WHITE);
        tv_cold.setTextColor(Color.WHITE);
        tv_hot.setTextColor(Color.WHITE);

        sb_cold.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        sb_cold.getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

        sb_hot.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        sb_hot.getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
    }
    //---------------------------------------------------------------------------------------------
    void setting_2() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));

        //btn_scan.setTextColor(Color.WHITE);
        //btn_scan.setBackgroundColor(Color.DKGRAY);
        //btn_scan.setPadding(15,15,15,15);

        background = Color.WHITE;
        color_off  = Color.GRAY;
        color_on   = Color.BLUE;

        log.setTextColor(Color.WHITE);

        linLayout.setBackgroundColor(background);
        x0.setTextColor(Color.WHITE);
        y0.setTextColor(Color.WHITE);
        tv_cold.setTextColor(Color.WHITE);
        tv_hot.setTextColor(Color.WHITE);

        sb_cold.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        sb_cold.getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

        sb_hot.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        sb_hot.getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.action_settings_scan:
                scan();
                break;

            case R.id.action_settings_0:
                setting_0();
                break;

            case R.id.action_settings_1:
                setting_1();
                break;

            case R.id.action_settings_2:
                setting_2();
                break;

            case R.id.action_settings_exit:
                this.finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO временный костыль
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        create_widgets();

        create_bluetooth();
    }
    //---------------------------------------------------------------------------------------------
    /*
    @Override
    public void onClick(View v) {
        if(v == btn_scan) {
            scan();
        }
    }
    */
    //---------------------------------------------------------------------------------------------
    public void create_bluetooth()
    {
        bluetooth = BluetoothAdapter.getDefaultAdapter();

        block_interface(true);

        modbus = new ModBus();

        if(bluetooth == null)
        {
            log.append("Bluetooth модуль не найден\n");
            return;
        }
        if(bluetooth.isEnabled() == false)
        {
            // Bluetooth выключен. Предложим пользователю включить его.
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
    //---------------------------------------------------------------------------------------------
    public void create_widgets()
    {
        // создание LinearLayout
        linLayout = new LinearLayout(this);
        layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 0, 10, 0);

        // установим вертикальную ориентацию
        linLayout.setOrientation(LinearLayout.VERTICAL);
        // создаем LayoutParams
        // LinearLayout.LayoutParams linLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        // устанавливаем linLayout как корневой элемент экрана
        // setContentView(linLayout, linLayoutParam);
        setContentView(linLayout);

        //LinearLayout.LayoutParams stretch = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);

        bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        //bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.imgpsh_fullsize).copy(Bitmap.Config.ARGB_8888, true);
        Log.d(LOG_TAG, "w=" + bitmap.getWidth() +" h=" + bitmap.getHeight());

        //---
        mPaint = new Paint();
        mPaint.setStrokeWidth(5);
        //---
        c = new Canvas(bitmap);
        //---
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        int screenHeight = displaymetrics.heightPixels;
        Log.d(LOG_TAG, "screenWidth=" + screenWidth + " screenHeight=" + screenHeight);
        //---
        points = new ArrayList<LED>();
        new_draw_field();
        //---

        main_view = new ImageView(this);
        main_view.setImageBitmap(bitmap);
        main_view.setOnTouchListener(this);

        //---
        TableLayout table = new TableLayout(this);
        table.setColumnStretchable(1, true);

        TableRow row0 = new TableRow(this);
        TableRow row1 = new TableRow(this);

        x0 = new TextView(this);
        y0 = new TextView(this);

        x0.setText("Cold");
        y0.setText("Hot");

        sb_cold = new SeekBar(this);
        sb_hot = new SeekBar(this);

        sb_cold.setMax(100);
        sb_hot.setMax(100);

        sb_hot.setOnSeekBarChangeListener(this);
        sb_cold.setOnSeekBarChangeListener(this);

        tv_cold = new TextView(this);
        tv_hot = new TextView(this);

        tv_cold.setText("0");
        tv_hot.setText("0");

        row0.addView(x0);
        row0.addView(sb_cold);
        row0.addView(tv_cold);

        row1.addView(y0);
        row1.addView(sb_hot);
        row1.addView(tv_hot);

        table.addView(row0);
        table.addView(row1);
        //---
        /*
        btn_scan = new Button(this);
        btn_scan.setText("SCAN");
        btn_scan.setOnClickListener(this);
        */
        //---
        log = new TextView(this);
        ScrollView sv = new ScrollView(this);
        sv.addView(log);
        //---
        //linLayout.addView(btn_scan, layoutParams);
        linLayout.addView(main_view, layoutParams);
        linLayout.addView(table, layoutParams);
        linLayout.addView(sv, layoutParams);
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public boolean onTouch(View view, MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            LED led;
            int color = color_off;
            for (int index = 0; index < points.size(); index++) {
                led = points.get(index);
                double xx = (x - led.center_x) * (x - led.center_x);
                double yy = (y - led.center_y) * (y - led.center_y);
                float line = (float) Math.sqrt(xx + yy);
                if (line < led.radius) {
                    if(led.color == color_off) {
                        led.color = color_on;
                    }
                    else {
                        led.color = color_off;
                    }
                    color = led.color;

                    points.set(index, led);
                    mPaint.setColor(led.color);
                    mPaint.setStyle(Paint.Style.FILL);
                    //mPaint.setStyle(Paint.Style.STROKE);    //???

                    if(index == 0) {
                        //говнокод рулит, а план горит :)
                        for (int n = 0; n < points.size(); n++) {
                            led = points.get(n);
                            led.color = color;
                            points.set(n, led);
                            mPaint.setColor(color);
                            c.drawCircle(led.center_x,
                                    led.center_y,
                                    led.radius,
                                    mPaint);
                        }
                        main_view.setImageBitmap(bitmap);
                        return true;
                    }
                    c.drawCircle(led.center_x,
                            led.center_y,
                            led.radius,
                            mPaint);
                    main_view.setImageBitmap(bitmap);

                    return true;
                }
                else {
                    Log.d(LOG_TAG, "line = " + line);
                    Log.d(LOG_TAG, "led.radius = " + led.radius);
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
        send_command();
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //log.append("onStartTrackingTouch\n");
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if(seekBar == sb_hot)
        {
            tv_hot.setText("" + seekBar.getProgress());
        }
        if(seekBar == sb_cold)
        {
            tv_cold.setText("" + seekBar.getProgress());
        }
        send_command();
    }
    //---------------------------------------------------------------------------------------------
    public void send_command()
    {
        check_button();

        ModBus modbus = new ModBus();
        modbus.set_address(0);
        modbus.set_command(1);
        modbus.set_data(get_data());

        send_modbus_data(modbus.get_string());
    }
    //---------------------------------------------------------------------------------------------
    private void new_draw_field()
    {
        mPaint.setColor(color_off);
        mPaint.setStyle(Paint.Style.FILL);
        //mPaint.setStyle(Paint.Style.STROKE);    //???
        //mPaint.setStrokeWidth(BORDER_WIDTH);

        c.drawCircle(center_x,
                center_y,
                center_r,
                mPaint);

        LED led = new LED();

        led.center_x = center_x;
        led.center_y = center_y;
        led.radius = center_r;
        led.color = color_off;
        led.address = 0;
        points.add(led);

        float angle = min_angle;
        int x = 5;
        /*
        рисуем по часовой стрелке
        */
        while(angle < max_angle)
        {
            for(int n=0; n<3; n++)
            {
                calc_line(center_x,
                        center_y,
                        angle,
                        inc_r * (n + 1));
                c.drawCircle(temp_x,
                        temp_y,
                        led_r,
                        mPaint);

                LED s_led = new LED();
                s_led.center_x = temp_x;
                s_led.center_y = temp_y;
                s_led.radius = led_r;
                s_led.color = color_off;
                s_led.address = leds_arr[n][x];
                points.add(s_led);
            }
            angle += 60.0f;
            x--;
        }
    }
    //---------------------------------------------------------------------------------------------
    private void calc_line(float x,
                           float y,
                           float angle,
                           float radius)
    {
        float A = radius;
        float B = (float)Math.cos(Math.toRadians(angle)) * A;
        float C = (float)Math.sin(Math.toRadians(angle)) * A;

        temp_x = x + B;
        temp_y = y + C;
    }
    //---------------------------------------------------------------------------------------------
    private void block_interface(boolean state)
    {
        //btn_scan.setEnabled(!state);
    }
    //---------------------------------------------------------------------------------------------
    public boolean scan()
    {
        if(bluetooth == null)
        {
            log.append("Bluetooth модуль не найден\n");
            return false;
        }
        if(bluetooth.isEnabled() == false)
        {
            log.append("Bluetooth выключен\n");
            block_interface(false);
            return false;
        }

        discoveredDevices.clear();

        if (discoveryDevicesReceiver == null) {
            discoveryDevicesReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();

                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                        if (!discoveredDevices.contains(device))
                        {
                            if(device.getName().equals(DEVICE_NAME))
                            {
                                log.append("[" + DEVICE_NAME + "] FOUND\n");
                                boolean ok = connect_remote_device(device.getAddress());
                                block_interface(!ok);
                            }
                            discoveredDevices.add(device);
                        }
                    }
                }
            };
        }

        if (discoveryFinishedReceiver == null) {
            discoveryFinishedReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    log.append("Scan end!\n");
                    if (progressDialog != null) progressDialog.dismiss();
                    unregisterReceiver(discoveryFinishedReceiver);
                    unregisterReceiver(discoveryDevicesReceiver);

                    block_interface(true);
                }
            };
        }

        registerReceiver(discoveryDevicesReceiver,  new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(discoveryFinishedReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

        progressDialog = ProgressDialog.show(this, "Поиск устройств", "Подождите...");

        bluetooth.startDiscovery();
        log.append("Scan begin...\n");
        return true;
    }
    //---------------------------------------------------------------------------------------------
    public boolean connect_remote_device(String MAC_address)
    {
        if(MAC_address.isEmpty())
        {
            log.append("MAC_address is empty!\n");
            return false;
        }

        r_device = bluetooth.getRemoteDevice(MAC_address);
        //---
        try {
            tmp = r_device.createRfcommSocketToServiceRecord(MY_UUID);
            Method m = r_device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
            tmp = (BluetoothSocket) m.invoke(r_device, 1);
        } catch (IOException e) {
            log.append("create ERROR: " +e.getMessage() + "\n");
            return false;
        } catch (NoSuchMethodException e)
        {
            log.append("create ERROR: " +e.getMessage() + "\n");
            return false;
        } catch (IllegalAccessException e)
        {
            log.append("create ERROR: " +e.getMessage() + "\n");
            return false;
        } catch (InvocationTargetException e)
        {
            log.append("create ERROR: " +e.getMessage() + "\n");
            return false;
        }
        //---
        mmSocket = tmp;
        try {
            mmSocket.connect();
        } catch (IOException e) {
            try {
                mmSocket = (BluetoothSocket) r_device.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(r_device,1);
                mmSocket.connect();
            }
            catch (Exception e2) {
                log.append("Stream ERROR: Couldn't establish Bluetooth connection!\n");
                return false;
            }
        }
        //---
        try {
            tmpIn  = mmSocket.getInputStream();
            tmpOut = mmSocket.getOutputStream();
        } catch (IOException e) {
            log.append("Stream ERROR: " +e.getMessage() + "\n");
            return false;
        }
        //---
        inputStream = tmpIn;
        outputStream = tmpOut;
        log.append("OK\n");
        return true;
    }
    //---------------------------------------------------------------------------------------------
    public boolean send_modbus_data(String message)
    {
        byte[] buffer = new byte[128];  // buffer store for the stream
        int bytes = 0; // bytes returned from read()
        int bytesAvailableCount = 0;

        if(outputStream == null)
        {
            log.append("outputStream not created!\n");
            return false;
        }
        try {
            outputStream.write(message.getBytes());
            do {
                bytesAvailableCount = inputStream.available();
                if(bytesAvailableCount > 0) {
                    bytes = inputStream.read(buffer);
                }
            } while(bytesAvailableCount > 0);
        } catch (IOException e) {
            log.append("send_data ERROR: " + e.getMessage() + "\n");
            return false;
        }
        return true;
    }
    //---------------------------------------------------------------------------------------------
    public ByteArrayOutputStream get_data()
    {
        ByteArrayOutputStream data;
        data = new ByteArrayOutputStream();
        for (int y=0; y<6; y++)
        {
            for(int x=0; x<6; x++)
            {
                data.write(leds[x][y]);
            }
        }
        return data;
    }
    //---------------------------------------------------------------------------------------------
    public void set_led_data(int value)
    {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) ((value >> 8) & 0xFF);
        bytes[1] = (byte) (value & 0xFF);

        byte a = (byte) ((bytes[0] >> 4) & 0x0F);
        byte b = (byte) (bytes[0] & 0x0F);
        byte c = (byte) ((bytes[1] >> 4) & 0x0F);
        byte d = (byte) (bytes[1] & 0x0F);

        int value_hot = sb_hot.getProgress();
        int value_cold = sb_cold.getProgress();

        leds[a][b] = (byte)value_cold;
        leds[c][d] = (byte)value_hot;
    }
    //---------------------------------------------------------------------------------------------
    public void check_button() {
        LED led;
        for (int n = 0; n < points.size(); n++) {
            led = points.get(n);
            if(led.color == color_on) {
                set_led_data(led.address);
            }
        }
    }
    //---------------------------------------------------------------------------------------------
}
