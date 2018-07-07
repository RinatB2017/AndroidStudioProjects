package boss.armor;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
        implements SeekBar.OnSeekBarChangeListener
{
    private static final int RECORD_REQUEST_CODE = 101;
    private final static int REQUEST_ENABLE_BT = 1;
    final String LOG_TAG = "States";

    TextView tv_log;

    Button btn_1;
    Button btn_2;
    Button btn_3;
    Button btn_4;
    Button btn_5;

    Button btn_get_param;
    Button btn_set_param;

    BluetoothAdapter bluetooth;
    private BroadcastReceiver discoveryDevicesReceiver;
    private BroadcastReceiver discoveryFinishedReceiver;
    private final List<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();
    private ProgressDialog progressDialog;

    private static final UUID MY_UUID = UUID.fromString("00000001-0001-0001-0001-000000000001");
    private static InputStream inputStream;
    private static OutputStream outputStream;

    private static final int CMD_GET_PARAM = 10;
    private static final int CMD_SET_PARAM = 11;
    private static final int CMD_GET_MODE  = 20;
    private static final int CMD_SET_MODE  = 21;

    private static final int MODE_01 = 1;
    private static final int MODE_02 = 2;
    private static final int MODE_03 = 3;
    private static final int MODE_04 = 4;
    private static final int MODE_05 = 5;

    BluetoothDevice r_device;
    BluetoothSocket tmp = null;
    BluetoothSocket mmSocket = null;
    InputStream tmpIn = null;
    OutputStream tmpOut = null;

    ModBus modbus;

    SeekBar sb_delay_ms;
    SeekBar sb_brightness;

    SeekBar sb_delay_N_ms;
    SeekBar sb_delay_K_ms;

    TextView tv_delay_ms;
    TextView tv_brightness;

    TextView tv_delay_N_ms;
    TextView tv_delay_K_ms;

    Spinner spinner_mode;

    int addr;
    int mode;
    int brightness;
    int delay_N_ms;
    int delay_K_ms;
    int delay_ms;

    int spinner_mode_position;

    //---------------------------------------------------------------------------------------------
    public void logging(String text) {
        Log.i(LOG_TAG, text);
        tv_log.append(text + "\n");
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
        if(seekBar == sb_delay_ms)
        {
            tv_delay_ms.setText(String.valueOf(seekBar.getProgress()));
            set_delay_ms(seekBar.getProgress());
        }
        if(seekBar == sb_brightness)
        {
            tv_brightness.setText(String.valueOf(seekBar.getProgress()));
            set_brightness(seekBar.getProgress());
        }
        if(seekBar == sb_delay_N_ms)
        {
            tv_delay_N_ms.setText(String.valueOf(seekBar.getProgress()));
            set_delay_N_ms(seekBar.getProgress());
        }
        if(seekBar == sb_delay_K_ms)
        {
            tv_delay_K_ms.setText(String.valueOf(seekBar.getProgress()));
            set_delay_K_ms(seekBar.getProgress());
        }
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void onProgressChanged(SeekBar seekBar,
                                  int progress,
                                  boolean fromUser) {
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
    public boolean device_connect()
    {
        logging("connect");
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
    public void device_disconnect()
    {
        logging("disconnect");

        if(mmSocket.isConnected()) {
            try {
                mmSocket.close();
                logging("Соединение разорвано");
                block_interface(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.action_settings_scan:
                if(device_connect())
                {
                    block_interface(false);
                }
                break;

            case R.id.action_settings_disconnect:
                device_disconnect();
                block_interface(true);
                break;

            case R.id.action_settings_options:
                Intent intent = new Intent(this, OptionsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if(intent != null) {
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
        logging("OK");
    }
    //---------------------------------------------------------------------------------------------
    private void block_interface(boolean state)
    {
        sb_delay_ms.setEnabled(!state);
        sb_brightness.setEnabled(!state);

        sb_delay_N_ms.setEnabled(!state);
        sb_delay_K_ms.setEnabled(!state);

        btn_1.setEnabled(!state);
        btn_2.setEnabled(!state);
        btn_3.setEnabled(!state);
        btn_4.setEnabled(!state);
        btn_5.setEnabled(!state);

        btn_get_param.setEnabled(!state);
        btn_set_param.setEnabled(!state);

        spinner_mode.setEnabled(!state);
    }
    //---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_log = (TextView)findViewById(R.id.logView);
        tv_log.setTextColor(Color.BLACK);
        //tv_log.setTextColor(Color.WHITE);

        sb_delay_ms = (SeekBar)findViewById(R.id.sb_delay_ms);
        sb_brightness = (SeekBar)findViewById(R.id.sb_brightness);

        sb_delay_N_ms = (SeekBar)findViewById(R.id.sb_delay_N_ms);
        sb_delay_K_ms = (SeekBar)findViewById(R.id.sb_delay_K_ms);

        tv_delay_ms = (TextView)findViewById(R.id.tv_delay_ms);
        tv_brightness = (TextView)findViewById(R.id.tv_brightness);

        tv_delay_N_ms = (TextView)findViewById(R.id.tv_delay_N_ms);
        tv_delay_K_ms = (TextView)findViewById(R.id.tv_delay_K_ms);

        sb_delay_ms.setOnSeekBarChangeListener(this);
        sb_brightness.setOnSeekBarChangeListener(this);
        sb_delay_N_ms.setOnSeekBarChangeListener(this);
        sb_delay_K_ms.setOnSeekBarChangeListener(this);

        //---
        String[] data = {"MODE 1", "MODE 2", "MODE 3", "MODE 4", "MODE 5"};
        // адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_mode = (Spinner)findViewById(R.id.spinner_mode);
        spinner_mode.setAdapter(adapter);

        // устанавливаем обработчик нажатия
        spinner_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                   @Override
                                                   public void onItemSelected(AdapterView<?> parent, View view,
                                                                              int position, long id) {
                                                       spinner_mode_position = position + 1;
                                                   }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //---

        sb_delay_ms.setMax(1000);
        sb_brightness.setMax(150);
        sb_delay_N_ms.setMax(0xFFFF);
        sb_delay_K_ms.setMax(0xFFFF);

        btn_1 = (Button)findViewById(R.id.btn_cmd_1);
        btn_2 = (Button)findViewById(R.id.btn_cmd_2);
        btn_3 = (Button)findViewById(R.id.btn_cmd_3);
        btn_4 = (Button)findViewById(R.id.btn_cmd_4);
        btn_5 = (Button)findViewById(R.id.btn_cmd_5);

        btn_get_param = (Button)findViewById(R.id.btn_get_param);
        btn_set_param = (Button)findViewById(R.id.btn_set_param);

        //TODO временный костыль
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, RECORD_REQUEST_CODE);
        create_bluetooth();

        block_interface(true);
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
                    logging("Получено " + bytes + " байтов");
                    show_answer(buffer);
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
        uint8_t     mode;
        uint8_t     brightness;
        uint16_t    delay_N_ms;
        uint16_t    delay_K_ms;
        uint16_t    delay_ms;
    } p_data_t;
     */
    public void show_answer(byte[] buffer)
    {
        // :AABBCC\n
        if(buffer.length < (4 * 2 +2))   //sizeof(P_HEADER)
        {
            logging("answer too small");
            return;
        }

        //byte begin = buffer[0];
        int mode  = get_uint8_t(convert_ascii_to_byte(buffer[1]),
                                convert_ascii_to_byte(buffer[2]));
        int cmd   = get_uint8_t(convert_ascii_to_byte(buffer[3]),
                                convert_ascii_to_byte(buffer[4]));
        int len   = get_uint16_t(convert_ascii_to_byte(buffer[5]),
                                 convert_ascii_to_byte(buffer[6]),
                                 convert_ascii_to_byte(buffer[7]),
                                 convert_ascii_to_byte(buffer[8]));

        //char begin_s = (char)begin;

        //logging(String.valueOf(begin_s));
        logging("mode = " + String.valueOf(mode));
        logging("cmd = " + String.valueOf(cmd));
        logging("len = " + String.valueOf(len));

        logging("answer is OK");
    }
    //---------------------------------------------------------------------------------------------
    public byte convert_ascii_to_byte(byte ascii_data)
    {
        byte res = 0;
        switch (ascii_data)
        {
            case '0':   res = 0x00;    break;
            case '1':   res = 0x01;    break;
            case '2':   res = 0x02;    break;
            case '3':   res = 0x03;    break;
            case '4':   res = 0x04;    break;
            case '5':   res = 0x05;    break;
            case '6':   res = 0x06;    break;
            case '7':   res = 0x07;    break;
            case '8':   res = 0x08;    break;
            case '9':   res = 0x09;    break;
            case 'A':   res = 0x0A;    break;
            case 'B':   res = 0x0B;    break;
            case 'C':   res = 0x0C;    break;
            case 'D':   res = 0x0D;    break;
            case 'E':   res = 0x0E;    break;
            case 'F':   res = 0x0F;    break;
        }
        return res;
    }
    //---------------------------------------------------------------------------------------------
    public int get_uint16_t(byte a, byte b, byte c, byte d)
    {
        byte hi = (byte) ((a << 4) | b);
        byte lo = (byte) ((c << 4) | d);
        return (lo << 8) | hi;
    }
    //---------------------------------------------------------------------------------------------
    public int get_uint8_t(byte a, byte b)
    {
        return (a << 4) | b;
    }
    //---------------------------------------------------------------------------------------------
    public int get_addr()
    {
        return addr;
    }
    //---------------------------------------------------------------------------------------------
    public int get_mode()
    {
        return mode;
    }
    //---------------------------------------------------------------------------------------------
    public int get_spinner_mode_position()
    {
        return spinner_mode_position;
    }
    //---------------------------------------------------------------------------------------------
    public int get_brightness()
    {
        return brightness;
    }
    //---------------------------------------------------------------------------------------------
    public int get_delay_N_ms()
    {
        return delay_N_ms;
    }
    //---------------------------------------------------------------------------------------------
    public int get_delay_K_ms()
    {
        return delay_K_ms;
    }
    //---------------------------------------------------------------------------------------------
    public int get_delay_ms()
    {
        return delay_ms;
    }
    //---------------------------------------------------------------------------------------------
    public void set_mode(int value)
    {
        mode = value;
    }
    //---------------------------------------------------------------------------------------------
    public void set_brightness(int value)
    {
        brightness = value;
    }
    //---------------------------------------------------------------------------------------------
    public void set_delay_N_ms(int value)
    {
        delay_N_ms = value;
    }
    //---------------------------------------------------------------------------------------------
    public void set_delay_K_ms(int value)
    {
        delay_K_ms = value;
    }
    //---------------------------------------------------------------------------------------------
    public void set_delay_ms(int value)
    {
        delay_ms = value;
    }
    //---------------------------------------------------------------------------------------------
    public void cmd_get_param()
    {
        //logging("set_param");

        ModBus modbus = new ModBus();
        modbus.add_begin_simvol();
        modbus.add_uint8_t(get_addr());
        modbus.add_uint8_t(CMD_GET_PARAM);
        modbus.add_uint16_t(1);
        modbus.add_uint8_t(get_spinner_mode_position());
        modbus.add_end_simvol();

        //logging(modbus.get_string());

        boolean ok = send_modbus_data(modbus.get_string());
        if(!ok) {
            ok = send_modbus_data(modbus.get_string());
            if(ok) {
                logging("Данные переданы.");
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
    public void cmd_set_param()
    {
        //logging("set_param");

        ModBus modbus = new ModBus();
        modbus.add_begin_simvol();
        modbus.add_uint8_t(get_addr());
        modbus.add_uint8_t(CMD_SET_PARAM);
        modbus.add_uint16_t(8); //sizeof struct P_DATA
        modbus.add_uint8_t(get_spinner_mode_position());
        modbus.add_uint8_t(get_brightness());
        modbus.add_uint16_t(get_delay_N_ms());
        modbus.add_uint16_t(get_delay_K_ms());
        modbus.add_uint16_t(get_delay_ms());
        modbus.add_end_simvol();

        //logging(modbus.get_string());

        boolean ok = send_modbus_data(modbus.get_string());
        if(!ok) {
            ok = send_modbus_data(modbus.get_string());
            if(ok) {
                logging("Данные переданы.");
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
    public void cmd_get_mode()
    {
        //logging("get_mode");

        ModBus modbus = new ModBus();
        modbus.add_begin_simvol();
        modbus.add_uint8_t(get_addr());
        modbus.add_uint8_t(CMD_GET_MODE);
        modbus.add_uint16_t(0); //данных нет
        modbus.add_end_simvol();

        //logging(modbus.get_string());

        boolean ok = send_modbus_data(modbus.get_string());
        if(!ok) {
            ok = send_modbus_data(modbus.get_string());
            if(ok) {
                logging("Данные переданы.");
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
    public void cmd_set_mode()
    {
        //logging("set_mode");

        ModBus modbus = new ModBus();
        modbus.add_begin_simvol();
        modbus.add_uint8_t(get_addr());
        modbus.add_uint8_t(CMD_SET_MODE);
        modbus.add_uint16_t(1);
        modbus.add_uint8_t(get_mode());
        modbus.add_end_simvol();

        //logging(modbus.get_string());

        boolean ok = send_modbus_data(modbus.get_string());
        if(!ok) {
            ok = send_modbus_data(modbus.get_string());
            if(ok) {
                logging("Данные переданы.");
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
    public void command_1(View view) {
        logging("CMD 1");
        set_mode(MODE_01);
        cmd_set_mode();
    }
    //---------------------------------------------------------------------------------------------
    public void command_2(View view) {
        logging("CMD 2");
        set_mode(MODE_02);
        cmd_set_mode();
    }
    //---------------------------------------------------------------------------------------------
    public void command_3(View view) {
        logging("CMD 3");
        set_mode(MODE_03);
        cmd_set_mode();
    }
    //---------------------------------------------------------------------------------------------
    public void command_4(View view) {
        logging("CMD 4");
        set_mode(MODE_04);
        cmd_set_mode();
    }
    //---------------------------------------------------------------------------------------------
    public void command_5(View view) {
        logging("CMD 5");
        set_mode(MODE_05);
        cmd_set_mode();
    }
    //---------------------------------------------------------------------------------------------
    public void command_get(View view) {
        logging("GET");
        cmd_get_param();
    }
    //---------------------------------------------------------------------------------------------
    public void command_set(View view) {
        logging("SET");
        cmd_set_param();
    }
    //---------------------------------------------------------------------------------------------
}
