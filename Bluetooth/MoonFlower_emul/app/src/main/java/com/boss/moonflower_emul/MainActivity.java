package com.boss.moonflower_emul;

import android.Manifest;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

// если на смартфоне стоит Android 6.0, то надо поставить в свойствах app
// Flawors Target SDK Version API22
// иначе bluetooth не будет находить устройства

public class MainActivity extends ListActivity {

    public final static String UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private final static int REQUEST_ENABLE_BT = 1;

    private static final int RECORD_REQUEST_CODE = 101;
    final String LOG_TAG = "States";

    private static final int MAX_SCREEN_X = 6;
    private static final int MAX_SCREEN_Y = 6;

    static String hex_str;
    static byte[] data;
    static byte[][] data_arr;

    TextView tv_log;

    private Paint mPaint;
    Canvas c_bitmap;
    ImageView main_view;

    Bitmap bitmap;

    boolean flag_is_enable = false;

    //---
    LED_points points;
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

    byte[][] leds = new byte[MAX_SCREEN_X][MAX_SCREEN_Y];
    int[][] leds_arr = {
            {0x2112, 0x2255, 0x4223, 0x4314, 0x5415, 0x2001},
            {0x4151, 0x3245, 0x5213, 0x5334, 0x0510, 0x4011},
            {0x3102, 0x2535, 0x0333, 0x0424, 0x4400, 0x3050}};

    int[][] x_buf = {
            {0x00, 0x01, 0x02, 0x03, 0x04, 0x05},
            {0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B},
            {0x0C, 0x0D, 0x0E, 0x0F, 0x10, 0x11},
            {0x12, 0x13, 0x14, 0x15, 0x16, 0x17},
            {0x18, 0x19, 0x1A, 0x1B, 0x1C, 0x1D},
            {0x1E, 0x1F, 0x20, 0x21, 0x22, 0x23}
    };
    //---

    ModBus modbus;

    Handler h_print;

    private BluetoothAdapter bluetoothAdapter;
    private ServerThread serverThread;

    private final List<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();
    private ArrayAdapter<BluetoothDevice> listAdapter;

    //---------------------------------------------------------------------------------------------
    public void draw_led(int num) {
        if (bitmap == null) {
            send_log("bitmap == null");
            return;
        }

        if (c_bitmap == null) {
            send_log("c_bitmap == null");
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

        LinearLayout main_layout = (LinearLayout) findViewById(R.id.main_layout);
        main_layout.setBackgroundColor(background);

        LinearLayout tab_main = (LinearLayout) findViewById(R.id.tab_main);
        tab_main.setBackgroundColor(background);

        LinearLayout tab_log = (LinearLayout) findViewById(R.id.tab_log);
        tab_log.setBackgroundColor(background);
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
            //tabHost.getTabWidget().getChildAt(i).setLayoutParams(layoutParams);
            TextView textView = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);
            textView.setTextColor(Color.WHITE);
        }
    }

    //---------------------------------------------------------------------------------------------
    void init_widgets() {
        main_view = (ImageView) findViewById(R.id.main_view);
    }

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

        // Always call the superclass so it can save the view hierarchy state
        savedInstanceState.putInt("current_tab", tabHost.getCurrentTab());

        super.onSaveInstanceState(savedInstanceState);
    }

    //----------------------------------------------------------------------------------------
    /*
    F_01 *packet = (F_01 *)buf_modbus;
    uint8_t addr = packet->body.header.addr;
    uint8_t cmd = packet->body.header.cmd;
    uint8_t len = packet->body.header.len;

    for(int y=0; y<MAX_SCREEN_Y; y++)
    {
        {
        for(int x=0; x<MAX_SCREEN_X; x++)
          set(x, y, packet->body.data_t.leds[x][y]);
        }
    }
    */

    private int get_led_value(int address)
    {
        int c = address >> 12 & 0xF;
        int d = address >> 8 & 0xF;
        int a = address >> 4 & 0xF;
        int b = address & 0xF;

        return data_arr[a][b] << 8 | data_arr[c][d];
    }

    private void set_led(int index_led, int value) {
        LED led = points.get(index_led);
        led.hot_color  = (value >> 8) & 0xFF;
        led.cold_color = value & 0xFF;
        points.set(index_led, led);
    }

    private boolean analize(String message) {
        send_log("analize");
        if(message.length() < 2) {
            send_log("message too short");
            return false;
        }
        send_log(message);
        hex_str = message.substring(1, message.length() - 1);
        if((hex_str.length() % 2) != 0) {
            send_log("error len " + hex_str.length());
            return false;
        }

        if(hex_str.length() != 39 * 2) {
            send_log("ERROR: hex_str.length() = " + String.valueOf(hex_str.length()));
            return false;
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int addr = 6;
        for(int n=addr; n<hex_str.length(); n+=2) {
            String str = hex_str.substring(n, n+2);
            int value = Integer.parseInt(str, 16);

            output.write((byte)value);
        }
        data = output.toByteArray();

        int index = 0;
        data_arr = new byte[MAX_SCREEN_X][MAX_SCREEN_Y];
        for(int y=0; y<MAX_SCREEN_Y; y++) {
            for(int x=0; x<MAX_SCREEN_X; x++) {
                data_arr[x][y] = data[index];
                index++;
            }
        }

        int led_15 = get_led_value(0x2112);
        int led_16 = get_led_value(0x4151);
        int led_17 = get_led_value(0x3102);

        int led_12 = get_led_value(0x2255);
        int led_13 = get_led_value(0x3245);
        int led_14 = get_led_value(0x2535);

        int led_09 = get_led_value(0x4223);
        int led_10 = get_led_value(0x5213);
        int led_11 = get_led_value(0x0333);

        int led_06 = get_led_value(0x4314);
        int led_07 = get_led_value(0x5334);
        int led_08 = get_led_value(0x0424);

        int led_03 = get_led_value(0x5415);
        int led_04 = get_led_value(0x0510);
        int led_05 = get_led_value(0x4400);

        int led_00 = get_led_value(0x2001);
        int led_01 = get_led_value(0x4011);
        int led_02 = get_led_value(0x3050);

        set_led(0,  led_00);
        set_led(1,  led_01);
        set_led(2,  led_02);
        set_led(3,  led_03);
        set_led(4,  led_04);
        set_led(5,  led_05);
        set_led(6,  led_06);
        set_led(7,  led_07);
        set_led(8,  led_08);
        set_led(9,  led_09);
        set_led(10, led_10);
        set_led(11, led_11);
        set_led(12, led_12);
        set_led(13, led_13);
        set_led(14, led_14);
        set_led(15, led_15);
        set_led(16, led_16);
        set_led(17, led_17);

//        LED led = points.get(0);
//        led.hot_color  = led_00 >> 8 & 0xFF;
//        led.cold_color = led_00 & 0xFF;
//        points.set(0, led);
//
//        points.set(0, points.get(0));

        redraw_all_buttons();   //FIXME
        //send_log("cnt " + cnt);
        return true;
    }

    //----------------------------------------------------------------------------------------
    private final CommunicatorService communicatorService = new CommunicatorService() {
        @Override
        public Communicator createCommunicatorThread(final BluetoothSocket socket) {
            return new CommunicatorImpl(socket, new CommunicatorImpl.CommunicationListener() {
                @Override
                public void onMessage(final String message) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //send_log(message);

                            //TODO hex
                            analize(message);

                            // отвечаем эхом
                            try {
                                OutputStream o_stream = socket.getOutputStream();
                                o_stream.write(message.getBytes());
                            } catch (IOException e) {
                                send_log("Stream ERROR: " +e.getMessage() + " \n");
                            }
                        }
                    });
                }
            });
        }
    };
    //---------------------------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init_log();

        modbus = new ModBus();

        create_bluetooth();

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

    //----------------------------------------------------------------------------------------
    public void makeDiscoverable(View view) {
        Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        i.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(i);
    }

    //----------------------------------------------------------------------------------------
    public void create_bluetooth() {
        send_log("create_bluetooth");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, RECORD_REQUEST_CODE);

        listAdapter = new ArrayAdapter<BluetoothDevice>(getBaseContext(), android.R.layout.simple_list_item_1, discoveredDevices) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final BluetoothDevice device = getItem(position);
                ((TextView) view.findViewById(android.R.id.text1)).setText(device.getName());
                return view;
            }
        };
        setListAdapter(listAdapter);

        if(bluetoothAdapter == null) {
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            // Bluetooth выключен. Предложим пользователю включить его.
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
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
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onRestart() {
        super.onRestart();
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onResume() {
        super.onResume();

        if(bluetoothAdapter != null) {
            serverThread = new ServerThread(communicatorService);
            serverThread.start();

            discoveredDevices.clear();
            listAdapter.notifyDataSetChanged();
        }
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onPause() {
        super.onPause();
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
        //---
    }

    //---------------------------------------------------------------------------------------------
    public void redraw_all_buttons() {
        send_log("redraw_all_buttons");
        for (int n = 0; n < points.size(); n++) {
            draw_led(n);
        }
        redraw();
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

        points.get(0).text = "0";
        points.get(0).draw_text = true;

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

                s_led.text = String.valueOf(s_led.number);
                s_led.draw_text = true;

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
        for (int y = 0; y < MAX_SCREEN_Y; y++) {
            for (int x = 0; x < MAX_SCREEN_X; x++) {
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
}
