package com.boss.armor;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    private static final int RECORD_REQUEST_CODE = 101;
    final String LOG_TAG = "States";

    TextView tv_log;

    Button btn_1;
    Button btn_2;
    Button btn_3;
    Button btn_4;
    Button btn_5;

    Button btn_get_param;
    Button btn_set_param;

    private static final int CMD_GET_PARAM = 10;
    private static final int CMD_SET_PARAM = 11;
    private static final int CMD_SET_MODE = 21;

    private static final int MODE_01 = 1;
    private static final int MODE_02 = 2;
    private static final int MODE_03 = 3;
    private static final int MODE_04 = 4;
    private static final int MODE_05 = 5;

    private static final int MAX_BRIGHTNESS = 150; // 150 выше моргает
    private static final int MIN_BRIGHTNESS = 0;   //
    private static final int DEFAULT_BRIGHTNESS = 10;

    private static final int MAX_DELAY_MS = 1000;
    private static final int MIN_DELAY_MS = 0;
    private static final int DEFAULT_DELAY_MS = 100;

    private static final int MAX_DELAY_N_MS = 3000;
    private static final int MIN_DELAY_N_MS = 0;
    private static final int DEFAULT_DELAY_N_MS = 100;

    private static final int MAX_DELAY_K_MS = 3000;
    private static final int MIN_DELAY_K_MS = 0;
    private static final int DEFAULT_DELAY_K_MS = 100;

    ModBus modbus;

    Handler h_print;

    Bluetooth bt;

    SeekBar sb_delay_ms;
    SeekBar sb_brightness;

    SeekBar sb_delay_N_ms;
    SeekBar sb_delay_K_ms;

    SeekBar sb_color_R;
    SeekBar sb_color_G;
    SeekBar sb_color_B;

    SeekBar sb_background_R;
    SeekBar sb_background_G;
    SeekBar sb_background_B;

    SeekBar sb_width_light;
    SeekBar sb_width_pause;

    TextView tv_delay_ms;
    TextView tv_brightness;

    TextView tv_delay_N_ms;
    TextView tv_delay_K_ms;

    TextView tv_color_R;
    TextView tv_color_G;
    TextView tv_color_B;

    TextView tv_background_R;
    TextView tv_background_G;
    TextView tv_background_B;

    TextView tv_width_light;
    TextView tv_width_pause;

    int addr = 0;
    int mode = MODE_01;

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
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar == sb_delay_ms) {
            tv_delay_ms.setText(String.valueOf(seekBar.getProgress()));
            set_delay_ms(seekBar.getProgress());
        }
        if (seekBar == sb_brightness) {
            tv_brightness.setText(String.valueOf(seekBar.getProgress()));
            set_brightness(seekBar.getProgress());
        }
        if (seekBar == sb_delay_N_ms) {
            tv_delay_N_ms.setText(String.valueOf(seekBar.getProgress()));
            set_delay_N_ms(seekBar.getProgress());
        }
        if (seekBar == sb_delay_K_ms) {
            tv_delay_K_ms.setText(String.valueOf(seekBar.getProgress()));
            set_delay_K_ms(seekBar.getProgress());
        }
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public void onProgressChanged(SeekBar seekBar,
                                  int progress,
                                  boolean fromUser) {
        if (seekBar == sb_delay_ms) {
            tv_delay_ms.setText(String.valueOf(seekBar.getProgress()));
            set_delay_ms(seekBar.getProgress());
        }
        if (seekBar == sb_brightness) {
            tv_brightness.setText(String.valueOf(seekBar.getProgress()));
            set_brightness(seekBar.getProgress());
        }
        if (seekBar == sb_delay_N_ms) {
            tv_delay_N_ms.setText(String.valueOf(seekBar.getProgress()));
            set_delay_N_ms(seekBar.getProgress());
        }
        if (seekBar == sb_delay_K_ms) {
            tv_delay_K_ms.setText(String.valueOf(seekBar.getProgress()));
            set_delay_K_ms(seekBar.getProgress());
        }
        if (seekBar == sb_color_R) {
            tv_color_R.setText(String.valueOf(seekBar.getProgress()));
            set_color_R(seekBar.getProgress());
        }
        if (seekBar == sb_color_G) {
            tv_color_G.setText(String.valueOf(seekBar.getProgress()));
            set_color_G(seekBar.getProgress());
        }
        if (seekBar == sb_color_B) {
            tv_color_B.setText(String.valueOf(seekBar.getProgress()));
            set_color_B(seekBar.getProgress());
        }
        if (seekBar == sb_background_R) {
            tv_background_R.setText(String.valueOf(seekBar.getProgress()));
            set_background_R(seekBar.getProgress());
        }
        if (seekBar == sb_background_G) {
            tv_background_G.setText(String.valueOf(seekBar.getProgress()));
            set_background_G(seekBar.getProgress());
        }
        if (seekBar == sb_background_B) {
            tv_background_B.setText(String.valueOf(seekBar.getProgress()));
            set_background_B(seekBar.getProgress());
        }
        if (seekBar == sb_width_light) {
            tv_width_light.setText(String.valueOf(seekBar.getProgress()));
            set_width_light(seekBar.getProgress());
        }
        if (seekBar == sb_width_pause) {
            tv_width_pause.setText(String.valueOf(seekBar.getProgress()));
            set_width_pause(seekBar.getProgress());
        }
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

            case R.id.action_clear_log:
                tv_log.setText("");
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //---------------------------------------------------------------------------------------------
    public void block_interface(boolean state) {
        sb_delay_ms.setEnabled(!state);
        sb_brightness.setEnabled(!state);

        sb_delay_N_ms.setEnabled(!state);
        sb_delay_K_ms.setEnabled(!state);

        sb_color_R.setEnabled(!state);
        sb_color_G.setEnabled(!state);
        sb_color_B.setEnabled(!state);

        sb_background_R.setEnabled(!state);
        sb_background_G.setEnabled(!state);
        sb_background_B.setEnabled(!state);

        sb_width_light.setEnabled(!state);
        sb_width_pause.setEnabled(!state);

        btn_1.setEnabled(!state);
        btn_2.setEnabled(!state);
        btn_3.setEnabled(!state);
        btn_4.setEnabled(!state);
        btn_5.setEnabled(!state);

        btn_get_param.setEnabled(!state);
        btn_set_param.setEnabled(!state);
    }

    //---------------------------------------------------------------------------------------------
    private void init_log() {
        tv_log = (TextView) findViewById(R.id.logView);
        tv_log.setTextColor(Color.BLACK);
        //tv_log.setTextColor(Color.WHITE);

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
    private void init_tabs() {
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);

        tabHost.setup();

        TabHost.TabSpec tabSpec;

        tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setContent(R.id.tab_buttons);
        tabSpec.setIndicator(getString(R.string.text_buttons));
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setContent(R.id.tab_test);
        tabSpec.setIndicator(getString(R.string.text_options));
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag3");
        tabSpec.setContent(R.id.tab_log);
        tabSpec.setIndicator(getString(R.string.text_log));
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTab(0);
    }

    //---------------------------------------------------------------------------------------------
    void init_widgets() {
        sb_delay_ms = (SeekBar) findViewById(R.id.sb_delay_ms);
        sb_brightness = (SeekBar) findViewById(R.id.sb_brightness);

        sb_delay_N_ms = (SeekBar) findViewById(R.id.sb_delay_N_ms);
        sb_delay_K_ms = (SeekBar) findViewById(R.id.sb_delay_K_ms);

        tv_delay_ms = (TextView) findViewById(R.id.tv_delay_ms);
        tv_brightness = (TextView) findViewById(R.id.tv_brightness);

        tv_delay_N_ms = (TextView) findViewById(R.id.tv_delay_N_ms);
        tv_delay_K_ms = (TextView) findViewById(R.id.tv_delay_K_ms);

        tv_color_R = (TextView) findViewById(R.id.tv_color_R);
        tv_color_G = (TextView) findViewById(R.id.tv_color_G);
        tv_color_B = (TextView) findViewById(R.id.tv_color_B);

        tv_background_R = (TextView) findViewById(R.id.tv_background_R);
        tv_background_G = (TextView) findViewById(R.id.tv_background_G);
        tv_background_B = (TextView) findViewById(R.id.tv_background_B);

        tv_width_light = (TextView) findViewById(R.id.tv_width_light);
        tv_width_pause = (TextView) findViewById(R.id.tv_width_pause);

        sb_color_R = (SeekBar) findViewById(R.id.sb_color_R);
        sb_color_G = (SeekBar) findViewById(R.id.sb_color_G);
        sb_color_B = (SeekBar) findViewById(R.id.sb_color_B);

        sb_background_R = (SeekBar) findViewById(R.id.sb_background_R);
        sb_background_G = (SeekBar) findViewById(R.id.sb_background_G);
        sb_background_B = (SeekBar) findViewById(R.id.sb_background_B);

        sb_width_light = (SeekBar) findViewById(R.id.sb_width_light);
        sb_width_pause = (SeekBar) findViewById(R.id.sb_width_pause);

        sb_delay_ms.setOnSeekBarChangeListener(this);
        sb_brightness.setOnSeekBarChangeListener(this);
        sb_delay_N_ms.setOnSeekBarChangeListener(this);
        sb_delay_K_ms.setOnSeekBarChangeListener(this);

        sb_color_R.setOnSeekBarChangeListener(this);
        sb_color_G.setOnSeekBarChangeListener(this);
        sb_color_B.setOnSeekBarChangeListener(this);

        sb_background_R.setOnSeekBarChangeListener(this);
        sb_background_G.setOnSeekBarChangeListener(this);
        sb_background_B.setOnSeekBarChangeListener(this);

        sb_width_light.setOnSeekBarChangeListener(this);
        sb_width_pause.setOnSeekBarChangeListener(this);

        sb_brightness.setMax(MAX_BRIGHTNESS);
        sb_delay_N_ms.setMax(MAX_DELAY_N_MS);
        sb_delay_K_ms.setMax(MAX_DELAY_K_MS);
        sb_delay_ms.setMax(MAX_DELAY_MS);
        sb_color_R.setMax(255);
        sb_color_G.setMax(255);
        sb_color_B.setMax(255);
        sb_background_R.setMax(255);
        sb_background_G.setMax(255);
        sb_background_B.setMax(255);
        sb_width_light.setMax(255);
        sb_width_pause.setMax(255);

        btn_1 = (Button) findViewById(R.id.btn_cmd_1);
        btn_2 = (Button) findViewById(R.id.btn_cmd_2);
        btn_3 = (Button) findViewById(R.id.btn_cmd_3);
        btn_4 = (Button) findViewById(R.id.btn_cmd_4);
        btn_5 = (Button) findViewById(R.id.btn_cmd_5);

        btn_get_param = (Button) findViewById(R.id.btn_get_param);
        btn_set_param = (Button) findViewById(R.id.btn_set_param);
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("log", tv_log.getText().toString());
        super.onSaveInstanceState(savedInstanceState);
    }

    //---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init_log();

        if (savedInstanceState != null) {
            String temp = savedInstanceState.getString("log");
            if (temp != null) {
                if (!temp.isEmpty()) {
                    tv_log.setText(temp);
                }
            }
        } else {
            Bundle bundle = new Bundle();
            getIntent().putExtras(bundle);
        }

        init_widgets();
        init_tabs();

        requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, RECORD_REQUEST_CODE);

        modbus = new ModBus();

        bt = new Bluetooth(MainActivity.this, tv_log);

        block_interface(true);
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onStart()
    {
        super.onStart();
        bt.device_connect();
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onRestart()
    {
        super.onRestart();
        bt.device_connect();
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onResume()
    {
        super.onResume();
        //logging("onResume()");
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onPause()
    {
        super.onPause();
        bt.device_disconnect();
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onStop()
    {
        super.onStop();
        //logging("onStop()");
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        //logging("onDestroy()");
    }

    //---------------------------------------------------------------------------------------------
    /*
    typedef struct P_HEADER
    {
        uint8_t   addr;
        uint8_t   cmd;
        uint16_t  len;
        uint8_t   data[];
    } p_header_t;

    typedef struct P_DATA
    {
        uint8_t     brightness;
        uint16_t    delay_N_ms;
        uint16_t    delay_K_ms;
        uint16_t    delay_ms;

        //added 23.07.18
        uint8_t   color_R;
        uint8_t   color_G;
        uint8_t   color_B;

        uint8_t   background_R;
        uint8_t   background_G;
        uint8_t   background_B;

        uint8_t   width_light;
        uint8_t   width_pause;
    } p_data_t;
    15 байт
     */
    public void show_answer(byte[] buffer) {
        // :AABBCC\n
        if (buffer.length < (4 * 2 + 2))   //sizeof(P_HEADER)
        {
            send_log("answer too small");
            return;
        }

        send_log("show_answer");

        //byte begin = buffer[0];
        int address = 1;    // пропускаеи ':'
        int p_addr = get_uint8_t(modbus.convert_ascii_to_byte(buffer[address++]),
                modbus.convert_ascii_to_byte(buffer[address++]));
        int p_cmd = get_uint8_t(modbus.convert_ascii_to_byte(buffer[address++]),
                modbus.convert_ascii_to_byte(buffer[address++]));
        int p_len = get_uint16_t(modbus.convert_ascii_to_byte(buffer[address++]),
                modbus.convert_ascii_to_byte(buffer[address++]),
                modbus.convert_ascii_to_byte(buffer[address++]),
                modbus.convert_ascii_to_byte(buffer[address++]));

        //logging(String.valueOf(begin_s));
        send_log("addr = " + String.valueOf(p_addr));
        send_log("cmd = " + String.valueOf(p_cmd));
        send_log("len = " + String.valueOf(p_len));

        if (p_cmd == CMD_GET_PARAM || p_cmd == CMD_SET_MODE) {
            int brightness = get_uint8_t(modbus.convert_ascii_to_byte(buffer[address++]),
                    modbus.convert_ascii_to_byte(buffer[address++]));
            int delay_N_ms = get_uint16_t(modbus.convert_ascii_to_byte(buffer[address++]),
                    modbus.convert_ascii_to_byte(buffer[address++]),
                    modbus.convert_ascii_to_byte(buffer[address++]),
                    modbus.convert_ascii_to_byte(buffer[address++]));
            int delay_K_ms = get_uint16_t(modbus.convert_ascii_to_byte(buffer[address++]),
                    modbus.convert_ascii_to_byte(buffer[address++]),
                    modbus.convert_ascii_to_byte(buffer[address++]),
                    modbus.convert_ascii_to_byte(buffer[address++]));
            int delay_ms = get_uint16_t(modbus.convert_ascii_to_byte(buffer[address++]),
                    modbus.convert_ascii_to_byte(buffer[address++]),
                    modbus.convert_ascii_to_byte(buffer[address++]),
                    modbus.convert_ascii_to_byte(buffer[address++]));

            //added 23.07.18
            int color_R = get_uint8_t(modbus.convert_ascii_to_byte(buffer[address++]),
                    modbus.convert_ascii_to_byte(buffer[address++]));
            int color_G = get_uint8_t(modbus.convert_ascii_to_byte(buffer[address++]),
                    modbus.convert_ascii_to_byte(buffer[address++]));
            int color_B = get_uint8_t(modbus.convert_ascii_to_byte(buffer[address++]),
                    modbus.convert_ascii_to_byte(buffer[address++]));

            int background_R = get_uint8_t(modbus.convert_ascii_to_byte(buffer[address++]),
                    modbus.convert_ascii_to_byte(buffer[address++]));
            int background_G = get_uint8_t(modbus.convert_ascii_to_byte(buffer[address++]),
                    modbus.convert_ascii_to_byte(buffer[address++]));
            int background_B = get_uint8_t(modbus.convert_ascii_to_byte(buffer[address++]),
                    modbus.convert_ascii_to_byte(buffer[address++]));

            int width_light = get_uint8_t(modbus.convert_ascii_to_byte(buffer[address++]),
                    modbus.convert_ascii_to_byte(buffer[address++]));
            int width_pause = get_uint8_t(modbus.convert_ascii_to_byte(buffer[address++]),
                    modbus.convert_ascii_to_byte(buffer[address++]));

            set_brightness(brightness);
            set_delay_N_ms(delay_N_ms);
            set_delay_K_ms(delay_K_ms);
            set_delay_ms(delay_ms);

            set_color_R(color_R);
            set_color_G(color_G);
            set_color_B(color_B);

            set_background_R(background_R);
            set_background_G(background_G);
            set_background_B(background_B);

            set_width_light(width_light);
            set_width_pause(width_pause);

            send_log("mode       = " + String.valueOf(mode));
            send_log("brightness = " + String.valueOf(brightness));
            send_log("delay_N_ms = " + String.valueOf(delay_N_ms));
            send_log("delay_K_ms = " + String.valueOf(delay_K_ms));
            send_log("delay_ms   = " + String.valueOf(delay_ms));

            send_log("color_R   = " + String.valueOf(color_R));
            send_log("color_G   = " + String.valueOf(color_G));
            send_log("color_B   = " + String.valueOf(color_B));

            send_log("background_R   = " + String.valueOf(background_R));
            send_log("background_G   = " + String.valueOf(background_G));
            send_log("background_B   = " + String.valueOf(background_B));

            send_log("width_light   = " + String.valueOf(width_light));
            send_log("width_pause   = " + String.valueOf(width_pause));
        }

        send_log("answer is OK");
    }

    //---------------------------------------------------------------------------------------------
    public int get_uint16_t(byte a, byte b, byte c, byte d) {
        int hi = (int) ((a << 4) | b);
        int lo = (int) ((c << 4) | d);
        return (int) ((lo << 8) | hi);
    }

    //---------------------------------------------------------------------------------------------
    public int get_uint8_t(byte a, byte b) {
        return (a << 4) | b;
    }

    //---------------------------------------------------------------------------------------------
    public int get_addr() {
        return addr;
    }

    //---------------------------------------------------------------------------------------------
    public int get_mode() {
        return mode;
    }

    //---------------------------------------------------------------------------------------------
    public int get_brightness() {
        return sb_brightness.getProgress();
    }

    //---------------------------------------------------------------------------------------------
    public int get_delay_N_ms() {
        return sb_delay_N_ms.getProgress();
    }

    //---------------------------------------------------------------------------------------------
    public int get_delay_K_ms() {
        return sb_delay_K_ms.getProgress();
    }

    //---------------------------------------------------------------------------------------------
    public int get_delay_ms() {
        return sb_delay_ms.getProgress();
    }

    //---------------------------------------------------------------------------------------------
    public int get_color_R() {
        return sb_color_R.getProgress();
    }

    //---------------------------------------------------------------------------------------------
    public int get_color_G() {
        return sb_color_G.getProgress();
    }

    //---------------------------------------------------------------------------------------------
    public int get_color_B() {
        return sb_color_B.getProgress();
    }

    //---------------------------------------------------------------------------------------------
    public int get_background_R() {
        return sb_background_R.getProgress();
    }

    //---------------------------------------------------------------------------------------------
    public int get_background_G() {
        return sb_background_G.getProgress();
    }

    //---------------------------------------------------------------------------------------------
    public int get_background_B() {
        return sb_background_B.getProgress();
    }

    //---------------------------------------------------------------------------------------------
    public int get_width_light() {
        return sb_width_light.getProgress();
    }

    //---------------------------------------------------------------------------------------------
    public int get_width_pause() {
        return sb_width_pause.getProgress();
    }

    //---------------------------------------------------------------------------------------------
    public void set_mode(int value) {
        mode = value;
    }

    //---------------------------------------------------------------------------------------------
    public void set_brightness(int value) {
        sb_brightness.setProgress(value);
    }

    //---------------------------------------------------------------------------------------------
    public void set_delay_N_ms(int value) {
        sb_delay_N_ms.setProgress(value);
    }

    //---------------------------------------------------------------------------------------------
    public void set_delay_K_ms(int value) {
        sb_delay_K_ms.setProgress(value);
    }

    //---------------------------------------------------------------------------------------------
    public void set_color_R(int value) {
        sb_color_R.setProgress(value);
    }

    //---------------------------------------------------------------------------------------------
    public void set_color_G(int value) {
        sb_color_G.setProgress(value);
    }

    //---------------------------------------------------------------------------------------------
    public void set_color_B(int value) {
        sb_color_B.setProgress(value);
    }

    //---------------------------------------------------------------------------------------------
    public void set_background_R(int value) {
        sb_background_R.setProgress(value);
    }

    //---------------------------------------------------------------------------------------------
    public void set_background_G(int value) {
        sb_background_G.setProgress(value);
    }

    //---------------------------------------------------------------------------------------------
    public void set_background_B(int value) {
        sb_background_B.setProgress(value);
    }

    //---------------------------------------------------------------------------------------------
    public void set_width_light(int value) {
        sb_width_light.setProgress(value);
    }

    //---------------------------------------------------------------------------------------------
    public void set_width_pause(int value) {
        sb_width_pause.setProgress(value);
    }

    //---------------------------------------------------------------------------------------------
    public void set_delay_ms(int value) {
        sb_delay_ms.setProgress(value);
    }

    //---------------------------------------------------------------------------------------------
    public boolean send_modbus_data(String message) {
        return bt.send_data(message);
    }

    //---------------------------------------------------------------------------------------------
    public void cmd_get_param() {
        //logging("set_param");

        ModBus modbus = new ModBus();
        modbus.add_begin_simvol();
        modbus.add_uint8_t(get_addr());
        modbus.add_uint8_t(CMD_GET_PARAM);
        modbus.add_uint16_t(0);
        modbus.add_end_simvol();

        boolean ok = send_modbus_data(modbus.get_string());
        if (ok) {
            send_log(getString(R.string.send_is_ok));
            //show_messagebox_info(getString(R.string.send_is_ok));
            Toast.makeText(getBaseContext(), getString(R.string.send_is_ok), Toast.LENGTH_LONG).show();
            show_answer(bt.get_result());
        } else {
            send_log(getString(R.string.send_is_fail));
            show_messagebox_alert(getString(R.string.send_is_fail));
        }
    }

    //---------------------------------------------------------------------------------------------
    public void cmd_set_param() {
        //send_log("set_param");

        ModBus modbus = new ModBus();
        modbus.add_begin_simvol();
        modbus.add_uint8_t(get_addr());
        modbus.add_uint8_t(CMD_SET_PARAM);
        modbus.add_uint16_t(15); //sizeof struct P_DATA
        modbus.add_uint8_t(get_brightness());
        modbus.add_uint16_t(get_delay_N_ms());
        modbus.add_uint16_t(get_delay_K_ms());
        modbus.add_uint16_t(get_delay_ms());

        modbus.add_uint8_t(get_color_R());
        modbus.add_uint8_t(get_color_G());
        modbus.add_uint8_t(get_color_B());
        modbus.add_uint8_t(get_background_R());
        modbus.add_uint8_t(get_background_G());
        modbus.add_uint8_t(get_background_B());
        modbus.add_uint8_t(get_width_light());
        modbus.add_uint8_t(get_width_pause());

        modbus.add_end_simvol();

        boolean ok = send_modbus_data(modbus.get_string());
        if (ok) {
            send_log(getString(R.string.send_is_ok));
            //show_messagebox_info(getString(R.string.send_is_ok));
            Toast.makeText(getBaseContext(), getString(R.string.send_is_ok), Toast.LENGTH_LONG).show();
        } else {
            send_log(getString(R.string.send_is_fail));
            show_messagebox_alert(getString(R.string.send_is_fail));
        }
    }

    //---------------------------------------------------------------------------------------------
    void show_messagebox_info(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle("Информация");
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // You don't have to do anything here if you just
                // want it dismissed when clicked
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    //---------------------------------------------------------------------------------------------
    void show_messagebox_alert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle("Ошибка");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // You don't have to do anything here if you just
                // want it dismissed when clicked
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    //---------------------------------------------------------------------------------------------
    public void cmd_set_mode() {
        send_log("set_mode: " + String.valueOf(get_mode()));

        ModBus modbus = new ModBus();
        modbus.add_begin_simvol();
        modbus.add_uint8_t(get_addr());
        modbus.add_uint8_t(CMD_SET_MODE);
        modbus.add_uint16_t(1);
        modbus.add_uint8_t(get_mode());
        modbus.add_end_simvol();

        boolean ok = send_modbus_data(modbus.get_string());
        if (ok) {
            send_log(getString(R.string.send_is_ok));
            //show_messagebox_info(getString(R.string.send_is_ok));
            Toast.makeText(getBaseContext(), getString(R.string.send_is_ok), Toast.LENGTH_LONG).show();
        } else {
            send_log(getString(R.string.send_is_fail));
            show_messagebox_alert(getString(R.string.send_is_fail));
        }
    }

    //---------------------------------------------------------------------------------------------
    public void command_1(View view) {
        send_log("CMD 1");
        set_mode(MODE_01);
        cmd_set_mode();
    }

    //---------------------------------------------------------------------------------------------
    public void command_2(View view) {
        send_log("CMD 2");
        set_mode(MODE_02);
        cmd_set_mode();
    }

    //---------------------------------------------------------------------------------------------
    public void command_3(View view) {
        send_log("CMD 3");
        set_mode(MODE_03);
        cmd_set_mode();
    }

    //---------------------------------------------------------------------------------------------
    public void command_4(View view) {
        send_log("CMD 4");
        set_mode(MODE_04);
        cmd_set_mode();
    }

    //---------------------------------------------------------------------------------------------
    public void command_5(View view) {
        send_log("CMD 5");
        set_mode(MODE_05);
        cmd_set_mode();
    }

    //---------------------------------------------------------------------------------------------
    public void command_get(View view) {
        send_log("GET");
        cmd_get_param();
    }

    //---------------------------------------------------------------------------------------------
    public void command_set(View view) {
        send_log("SET");
        cmd_set_param();
    }
    //---------------------------------------------------------------------------------------------
}
