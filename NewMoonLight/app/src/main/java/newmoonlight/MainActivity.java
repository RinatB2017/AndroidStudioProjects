package newmoonlight;

    import android.Manifest;
    import android.app.ProgressDialog;
    import android.bluetooth.BluetoothAdapter;
    import android.bluetooth.BluetoothDevice;
    import android.bluetooth.BluetoothSocket;
    import android.content.BroadcastReceiver;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.content.pm.ActivityInfo;
    import android.content.pm.PackageManager;
    import android.graphics.Bitmap;
    import android.graphics.Canvas;
    import android.graphics.Color;
    import android.graphics.Paint;
    import android.graphics.Point;
    import android.graphics.PorterDuff;
    import android.graphics.Rect;
    import android.graphics.drawable.ColorDrawable;
    import android.os.Build;
    import android.os.Bundle;
    import android.preference.PreferenceManager;
    import android.support.v4.app.ActivityCompat;
    import android.support.v4.content.ContextCompat;
    import android.support.v7.app.ActionBar;
    import android.support.v7.app.AppCompatActivity;
    import android.util.Log;
    import android.view.Display;
    import android.view.Menu;
    import android.view.MenuItem;
    import android.view.MotionEvent;
    import android.view.View;
    import android.view.Window;
    import android.view.WindowManager;
    import android.widget.Button;
    import android.widget.ImageView;
    import android.widget.LinearLayout;
    import android.widget.SeekBar;
    import android.widget.Space;
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

// если на смартфоне стоит Android 6.0, то надо поставить в свойствах app
// Flawors Target SDK Version API22
// иначе bluetooth не будет находить устройства

public class MainActivity extends AppCompatActivity
        implements View.OnTouchListener, SeekBar.OnSeekBarChangeListener {

    private static final int RECORD_REQUEST_CODE = 101;
    private final static int REQUEST_ENABLE_BT = 1;
    final String LOG_TAG = "States";

    TextView tv_log;

    private Paint mPaint;
    Canvas c;
    LinearLayout linLayout;
    LinearLayout.LayoutParams layoutParams;
    ImageView main_view;

    TextView x0;
    TextView y0;

    Bitmap bitmap;

    boolean flag_is_enable = false;
    SeekBar sb_hot;
    SeekBar sb_cold;

    TextView tv_hot;
    TextView tv_cold;

    static int WIDTH  = 768;
    static int HEIGHT = 768;

    static float center_x = WIDTH / 2.0f;
    static float center_y = HEIGHT / 2.0f;

    static float center_r = WIDTH / 14.0f;

    static float led_r = WIDTH / 17.0f;
    static float min_r = center_r + led_r + 10.0f;
    static float max_r = WIDTH / 2.0f - 20.0f;
    static float min_angle = -30.0f;
    static float max_angle = 330.0f;
    static int inc_r = (int)((max_r - min_r) / 2.4f);
    static float temp_x = 0;
    static float temp_y = 0;

    static int background = Color.BLACK;
    static int color_border = Color.RED;
    static int color_off  = Color.GRAY;
    static int color_on   = Color.RED;
    static int text_color   = Color.WHITE;

    //---
    BluetoothAdapter bluetooth;
    private BroadcastReceiver discoveryDevicesReceiver;
    private BroadcastReceiver discoveryFinishedReceiver;
    private final List<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();
    private ProgressDialog progressDialog;

    private static final UUID MY_UUID = UUID.fromString("00000001-0001-0001-0001-000000000001");
    //private static final String DEVICE_NAME = "20:15:10:19:62:52";
    //private static final String DEVICE_NAME = "HC-05";
    private static InputStream inputStream;
    private static OutputStream outputStream;

    BluetoothDevice r_device;
    BluetoothSocket tmp = null;
    BluetoothSocket mmSocket = null;
    InputStream tmpIn = null;
    OutputStream tmpOut = null;

    //---
    byte[][] leds = new byte[6][6];
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
        int     color_border;
        int     color_center_on;
        int     color_center_off;
        boolean is_active;
        int     address;

        //TODO
        String text;
        boolean draw_text;
    }
    //---------------------------------------------------------------------------------------------
    public void logging(String text) {
        Log.i(LOG_TAG, text);
        tv_log.setText(text);
    }
    //---------------------------------------------------------------------------------------------
    void load_states() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sb_hot.setProgress(sp.getInt("sb_hot", 0));
        sb_cold.setProgress(sp.getInt("sb_cold", 0));

        tv_hot.setText(String.valueOf(sb_hot.getProgress()));
        tv_cold.setText(String.valueOf(sb_cold.getProgress()));

        flag_is_enable = sp.getBoolean("flag_is_enable", false);

        for (int n = 0; n < points.size(); n++) {
            LED led = new LED();
            led.color_border = sp.getInt("color_border_" + String.valueOf(n), color_border);
            led.color_center_on = sp.getInt("color_center_on_" + String.valueOf(n), color_on);
            led.color_center_off = sp.getInt("color_center_off_" + String.valueOf(n), color_off);
            led.is_active = sp.getBoolean("is_active_" + String.valueOf(n), false);

            points.set(n, led);
        }
    }
    //---------------------------------------------------------------------------------------------
    void save_states() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.putInt("sb_hot", sb_hot.getProgress());
        editor.putInt("sb_cold", sb_cold.getProgress());
        editor.putBoolean("flag_is_enable", flag_is_enable);

        for (int n = 0; n < points.size(); n++) {
            editor.putInt("color_border_" + String.valueOf(n), points.get(n).color_border);
            editor.putInt("color_center_on_" + String.valueOf(n), points.get(n).color_center_on);
            editor.putInt("color_center_off_" + String.valueOf(n), points.get(n).color_center_off);
            editor.putBoolean("is_active_" + String.valueOf(n), points.get(n).is_active);
        }
        editor.apply();
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    //---------------------------------------------------------------------------------------------
    void setting_RED() {
        background = Color.BLACK;
        color_border = Color.RED;
        color_off  = Color.GRAY;
        color_on   = Color.RED;

        for (int n = 0; n < points.size(); n++) {
            points.get(n).color_border = color_border;
            points.get(n).color_center_off = color_off;
            points.get(n).color_center_on = color_on;
            points.get(n).is_active = false;

            mPaint.setColor(color_border);
            mPaint.setStyle(Paint.Style.STROKE);
            c.drawCircle(points.get(n).center_x,
                    points.get(n).center_y,
                    points.get(n).radius,
                    mPaint);

            mPaint.setColor(color_off);
            mPaint.setStyle(Paint.Style.FILL);
            c.drawCircle(points.get(n).center_x,
                    points.get(n).center_y,
                    points.get(n).radius-1,
                    mPaint);
        }

        ActionBar bar = getSupportActionBar();
        if(bar != null) {
            bar.setBackgroundDrawable(new ColorDrawable(background));
        }

        linLayout.setBackgroundColor(background);
        x0.setTextColor(text_color);
        y0.setTextColor(text_color);
        tv_cold.setTextColor(text_color);
        tv_hot.setTextColor(text_color);

        sb_cold.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        sb_cold.getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

        sb_hot.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        sb_hot.getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
    }
    //---------------------------------------------------------------------------------------------
    void setting_GREEN() {
        background = Color.BLACK;
        color_border = Color.GREEN;
        color_off  = Color.GRAY;
        color_on   = Color.GREEN;

        for (int n = 0; n < points.size(); n++) {
            points.get(n).color_border = color_border;
            points.get(n).color_center_off = color_off;
            points.get(n).color_center_on = color_on;
            points.get(n).is_active = false;

            mPaint.setColor(color_border);
            mPaint.setStyle(Paint.Style.STROKE);
            c.drawCircle(points.get(n).center_x,
                    points.get(n).center_y,
                    points.get(n).radius,
                    mPaint);

            mPaint.setColor(color_off);
            mPaint.setStyle(Paint.Style.FILL);
            c.drawCircle(points.get(n).center_x,
                    points.get(n).center_y,
                    points.get(n).radius-1,
                    mPaint);
        }

        ActionBar bar = getSupportActionBar();
        if(bar != null) {
            bar.setBackgroundDrawable(new ColorDrawable(background));
        }

        linLayout.setBackgroundColor(background);
        x0.setTextColor(text_color);
        y0.setTextColor(text_color);
        tv_cold.setTextColor(text_color);
        tv_hot.setTextColor(text_color);

        sb_cold.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        sb_cold.getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

        sb_hot.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        sb_hot.getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
    }
    //---------------------------------------------------------------------------------------------
    void setting_BLUE() {
        background = Color.BLACK;
        color_border = Color.BLUE;
        color_off  = Color.GRAY;
        color_on   = Color.BLUE;

        for (int n = 0; n < points.size(); n++) {
            points.get(n).color_border = color_border;
            points.get(n).color_center_off = color_off;
            points.get(n).color_center_on = color_on;
            points.get(n).is_active = false;

            mPaint.setColor(color_border);
            mPaint.setStyle(Paint.Style.STROKE);
            c.drawCircle(points.get(n).center_x,
                    points.get(n).center_y,
                    points.get(n).radius,
                    mPaint);

            mPaint.setColor(color_off);
            mPaint.setStyle(Paint.Style.FILL);
            c.drawCircle(points.get(n).center_x,
                    points.get(n).center_y,
                    points.get(n).radius-1,
                    mPaint);
        }

        ActionBar bar = getSupportActionBar();
        if(bar != null) {
            bar.setBackgroundDrawable(new ColorDrawable(background));
        }

        linLayout.setBackgroundColor(background);
        x0.setTextColor(text_color);
        y0.setTextColor(text_color);
        tv_cold.setTextColor(text_color);
        tv_hot.setTextColor(text_color);

        sb_cold.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        sb_cold.getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

        sb_hot.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        sb_hot.getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
    }
    //---------------------------------------------------------------------------------------------
    void setting_GRAY() {
        background   = Color.BLACK;
        color_border = Color.WHITE;
        color_off    = Color.BLACK;
        color_on     = Color.WHITE;

        for (int n = 0; n < points.size(); n++) {
            points.get(n).color_border = color_border;
            points.get(n).color_center_off = color_off;
            points.get(n).color_center_on = color_on;
            points.get(n).is_active = false;

            mPaint.setColor(color_border);
            mPaint.setStyle(Paint.Style.STROKE);
            c.drawCircle(points.get(n).center_x,
                    points.get(n).center_y,
                    points.get(n).radius,
                    mPaint);

            mPaint.setColor(color_off);
            mPaint.setStyle(Paint.Style.FILL);
            c.drawCircle(points.get(n).center_x,
                    points.get(n).center_y,
                    points.get(n).radius-1,
                    mPaint);

            //FIXME надо исправить
            if(points.get(n).draw_text) {
                int fontSize = 80;

                String text = points.get(n).text;
                mPaint.setColor(Color.WHITE);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setAntiAlias(true);
                mPaint.setTextSize(fontSize);

                Paint fontPaint;
                fontPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                fontPaint.setTextSize(fontSize);
                float width = fontPaint.measureText(text);

                Rect bounds = new Rect();
                fontPaint.getTextBounds(text, 0, 1, bounds);

                float height = bounds.height();

                c.drawText(text,
                        points.get(n).center_x - width / 2,
                        points.get(n).center_y + height / 2,
                        mPaint);
            }
            //---
        }

        ActionBar bar = getSupportActionBar();
        if(bar != null) {
            bar.setBackgroundDrawable(new ColorDrawable(background));
        }

        linLayout.setBackgroundColor(background);
        x0.setTextColor(text_color);
        y0.setTextColor(text_color);
        tv_cold.setTextColor(text_color);
        tv_hot.setTextColor(text_color);

        //заремарено из-за 4.0.4
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            sb_cold.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            sb_cold.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

            sb_hot.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            sb_hot.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        }
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.action_settings_scan:
                scan();
                break;

            case R.id.action_settings_disconnect:
                if(mmSocket.isConnected()) {
                    try {
                        mmSocket.close();
                        logging("Соединение разорвано");
                        block_interface(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case R.id.action_settings_options:
                Intent intent = new Intent(this, OptionsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if(intent != null) {
                    startActivity(intent);
                }
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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tv_log = new TextView(this);
        tv_log.setTextColor(Color.WHITE);

        //max_screen();

        //TODO временный костыль
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        create_widgets();
        create_bluetooth();
        setting_GRAY();

        requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, RECORD_REQUEST_CODE);

        //TODO
        scan();

        //load_states();
    }
    //---------------------------------------------------------------------------------------------
    public void create_bluetooth()
    {
        bluetooth = BluetoothAdapter.getDefaultAdapter();

        block_interface(true);

        modbus = new ModBus();

        if(bluetooth == null)
        {
            logging("Bluetooth модуль не найден");
            return;
        }
        if(!bluetooth.isEnabled())
        {
            // Bluetooth выключен. Предложим пользователю включить его.
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
    //---------------------------------------------------------------------------------------------
    public void create_widgets()
    {
        //---
        Display display = getWindowManager().getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        WIDTH = p.x;
        HEIGHT = p.x;

        center_x = WIDTH / 2.0f;
        center_y = HEIGHT / 2.0f;

        center_r = WIDTH / 14.0f;

        led_r = WIDTH / 17.0f;
        min_r = center_r + led_r + 10.0f;
        max_r = WIDTH / 2.0f - 20.0f;
        inc_r = (int)((max_r - min_r) / 2.4f);
        //---

        // создание LinearLayout
        linLayout = new LinearLayout(this);
        layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 0, 10, 0);

        linLayout.setOrientation(LinearLayout.VERTICAL);
        setContentView(linLayout);

        bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        //logging("w=" + bitmap.getWidth() +" h=" + bitmap.getHeight());

        //---
        mPaint = new Paint();
        mPaint.setStrokeWidth(5);
        //---
        c = new Canvas(bitmap);
        //---
        points = new ArrayList<LED>();
        new_draw_field();
        //---

        main_view = new ImageView(this);
        main_view.setImageBitmap(bitmap);
        main_view.setOnTouchListener(this);

        linLayout.addView(main_view, layoutParams);

        //FIXME исправить позже
        Space space = new Space(getApplicationContext());
        final LinearLayout.LayoutParams spacerLp = new LinearLayout.LayoutParams(0, 0, 1f);
        linLayout.addView(space, spacerLp);

        linLayout.addView(add_sb_table(), layoutParams);

        linLayout.addView(add_log());
    }
    //---------------------------------------------------------------------------------------------
    TextView add_log() {
        return tv_log;
    }
    //---------------------------------------------------------------------------------------------
    TableLayout add_sb_table() {
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

        return table;
    }
    //---------------------------------------------------------------------------------------------
    LinearLayout add_btn() {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.weight = 1;

        LinearLayout btn_layout = new LinearLayout(this);
        btn_layout.setOrientation(LinearLayout.HORIZONTAL);

        Button btn_apply = new Button(this);
        Button btn_cancel = new Button(this);

        //-------------------------------------------------------------------
        //TODO
        View.OnClickListener apply = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOG_TAG, "apply");

                //boolean ok = connect_remote_device("00:14:02:10:09:04");
                boolean ok = connect_remote_device(BluetoothName.get_mac(getApplicationContext()));
                if(ok)
                    logging("Соединение установлено");
                else
                    logging("Соединение не удалось");
            }
        };
        //-------------------------------------------------------------------
        View.OnClickListener cancel = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOG_TAG, "cancel");
            }
        };

        btn_apply.setOnClickListener(apply);
        btn_cancel.setOnClickListener(cancel);

        btn_apply.setText("Применить");
        btn_cancel.setText("Отменить");

        btn_apply.setLayoutParams(p);
        btn_cancel.setLayoutParams(p);

        btn_layout.addView(btn_apply);
        btn_layout.addView(btn_cancel);

        return btn_layout;
    }
    //---------------------------------------------------------------------------------------------
    public void redraw_all_buttons(boolean is_filled) {
        LED led;
        for (int n = 0; n < points.size(); n++) {
            led = points.get(n);
            led.is_active = is_filled;
            points.set(n, led);
            if(is_filled)
                mPaint.setColor(led.color_center_on);
            else
                mPaint.setColor(led.color_center_off);

            mPaint.setStyle(Paint.Style.FILL);
            c.drawCircle(led.center_x,
                    led.center_y,
                    led.radius,
                    mPaint);

            //FIXME исправить позже
            if(points.get(n).draw_text) {
                int fontSize = 80;

                String text = points.get(n).text;
                if(led.is_active)
                    mPaint.setColor(Color.BLACK);
                else
                    mPaint.setColor(Color.WHITE);

                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setAntiAlias(true);
                mPaint.setTextSize(fontSize);

                Paint fontPaint;
                fontPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                fontPaint.setTextSize(fontSize);
                float width = fontPaint.measureText(text);

                Rect bounds = new Rect();
                fontPaint.getTextBounds(text, 0, 1, bounds);

                float height = bounds.height();

                c.drawText(text,
                        points.get(n).center_x - width / 2,
                        points.get(n).center_y + height / 2,
                        mPaint);
            }
            //---
        }
        main_view.setImageBitmap(bitmap);
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public boolean onTouch(View view, MotionEvent event) {

        if(flag_is_enable == false) {
            return false;
        }
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            LED led;
            for (int index = 0; index < points.size(); index++) {
                led = points.get(index);
                double xx = (x - led.center_x) * (x - led.center_x);
                double yy = (y - led.center_y) * (y - led.center_y);
                float line = (float) Math.sqrt(xx + yy);
                if (line < led.radius) {
                    led.is_active = !led.is_active;

                    if(index == 0) {
                        redraw_all_buttons(led.is_active);
                        //говнокод рулит, а план горит :)
                        return true;
                    }
                    points.set(index, led);
                    if(led.is_active) {
                        mPaint.setColor(color_on);
                        mPaint.setStyle(Paint.Style.FILL);
                        c.drawCircle(led.center_x,
                                led.center_y,
                                led.radius-1,
                                mPaint);
                    }
                    else {
                        mPaint.setColor(color_off);
                        mPaint.setStyle(Paint.Style.FILL);
                        c.drawCircle(led.center_x,
                                led.center_y,
                                led.radius-1,
                                mPaint);
                    }
                    main_view.setImageBitmap(bitmap);
                    //FIXME исправить позже
                    if(points.get(index).draw_text) {
                        int fontSize = 80;

                        String text = points.get(index).text;
                        if(led.is_active)
                            mPaint.setColor(Color.BLACK);
                        else
                            mPaint.setColor(Color.WHITE);

                        mPaint.setStyle(Paint.Style.FILL);
                        mPaint.setAntiAlias(true);
                        mPaint.setTextSize(fontSize);

                        Paint fontPaint;
                        fontPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                        fontPaint.setTextSize(fontSize);
                        float width = fontPaint.measureText(text);

                        Rect bounds = new Rect();
                        fontPaint.getTextBounds(text, 0, 1, bounds);

                        float height = bounds.height();

                        c.drawText(text,
                                points.get(index).center_x - width / 2,
                                points.get(index).center_y + height / 2,
                                mPaint);
                    }
                    //---
                    return true;
                }
                else {
                    //logging("line = " + line);
                    //logging("led.radius = " + led.radius);
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
        //log.append("onStartTrackingTouch");
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if(seekBar == sb_hot)
        {
            tv_hot.setText(String.valueOf(seekBar.getProgress()));
        }
        if(seekBar == sb_cold)
        {
            tv_cold.setText(String.valueOf(seekBar.getProgress()));
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

        boolean ok = send_modbus_data(modbus.get_string());
        if(!ok) {
            ok = scan();
            if(ok) {
                ok = send_modbus_data(modbus.get_string());
                if(ok) {
                    logging("Данные переданы.");
                }
                else {
                    logging("Ошибка соединения.");
                }
            }
            else {
                logging("Ошибка соединения.");
            }
        }
        else {
            logging("Данные переданы.");
        }
    }
    //---------------------------------------------------------------------------------------------
    private void new_draw_field()
    {
        mPaint.setColor(color_off);
        mPaint.setStyle(Paint.Style.FILL);

        c.drawCircle(center_x,
                center_y,
                center_r,
                mPaint);

        LED led = new LED();

        led.center_x = center_x;
        led.center_y = center_y;
        led.radius = center_r;
        led.color_center_off = color_off;
        led.color_center_on  = color_on;
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
                s_led.color_border = color_border;
                s_led.color_center_off = color_off;
                s_led.color_center_on  = color_on;
                s_led.address = leds_arr[n][x];

                //FIXME исправить позже
                if(n == 2) {
                    if(angle == 270) s_led.text = "1";
                    if(angle == -30) s_led.text = "2";
                    if(angle ==  30) s_led.text = "3";
                    if(angle ==  90) s_led.text = "4";
                    if(angle == 150) s_led.text = "5";
                    if(angle == 210) s_led.text = "6";
                    s_led.draw_text = true;
                }
                else {
                    s_led.draw_text = false;
                }
                //---

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
        flag_is_enable = !state;
        sb_cold.setEnabled(!state);
        sb_hot.setEnabled(!state);
    }
    //---------------------------------------------------------------------------------------------
    public boolean scan()
    {
        if(bluetooth == null)
        {
            logging("Bluetooth модуль не найден");
            return false;
        }
        if(!bluetooth.isEnabled())
        {
            logging("Bluetooth выключен");
            block_interface(true);
            return false;
        }

        boolean ok = connect_remote_device(BluetoothName.get_mac(getApplicationContext()));
        if(ok)
            logging("Соединение установлено");
        else
            logging("Соединение не удалось");
        block_interface(!ok);

        return ok;
    }
    //---------------------------------------------------------------------------------------------
    public boolean connect_remote_device(String MAC_address)
    {
        if(MAC_address.isEmpty())
        {
            logging("MAC_address is empty!");
            return false;
        }

        r_device = bluetooth.getRemoteDevice(MAC_address);
        //---
        try {
            tmp = r_device.createRfcommSocketToServiceRecord(MY_UUID);
            Method m = r_device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
            tmp = (BluetoothSocket) m.invoke(r_device, 1);
        } catch (IOException e) {
            logging("create ERROR: " + e.getMessage());
            return false;
        } catch (NoSuchMethodException e)
        {
            logging("create ERROR: " + e.getMessage());
            return false;
        } catch (IllegalAccessException e)
        {
            logging("create ERROR: " + e.getMessage());
            return false;
        } catch (InvocationTargetException e)
        {
            logging("create ERROR: " + e.getMessage());
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
                logging("Stream ERROR: Couldn't establish Bluetooth connection!");
                return false;
            }
        }
        //---
        try {
            tmpIn  = mmSocket.getInputStream();
            tmpOut = mmSocket.getOutputStream();
        } catch (IOException e) {
            logging("Stream ERROR: " + e.getMessage());
            return false;
        }
        //---
        inputStream = tmpIn;
        outputStream = tmpOut;
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
            logging("outputStream not created!");
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
            logging("send_data ERROR: " + e.getMessage());
            block_interface(true);
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Ошибка связи",
                    Toast.LENGTH_SHORT);
            toast.show();
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
            if(led.is_active) {
                set_led_data(led.address);
            }
        }
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onStop() {
        super.onStop();

        //save_states();
    }
    //---------------------------------------------------------------------------------------------
}
