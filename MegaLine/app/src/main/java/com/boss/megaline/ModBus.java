package com.boss.megaline;

import java.io.ByteArrayOutputStream;

//---------------------------------------------------------------------------------------------
/**
 * Created by tux on 31.03.16.
 */
//---------------------------------------------------------------------------------------------
public class ModBus {
    int address = 0;
    int command = 0;
    int cnt_data = 0;

    int len_line = 0;
    int len_pause = 0;
    int delay_ms = 0;

    int color_R = 0;
    int color_G = 0;
    int color_B = 0;

    ByteArrayOutputStream ba;
    //---------------------------------------------------------------------------------------------
    public ModBus()
    {
        this.address = 0;
        this.command = 0;
        this.cnt_data = 0;
        ba = new ByteArrayOutputStream();
    }
    //---------------------------------------------------------------------------------------------
    public boolean set_address(int address)
    {
        if(address < 0) return false;
        if(address > 0xFF) return false;
        this.address = address;
        return true;
    }
    //---------------------------------------------------------------------------------------------
    public boolean set_command(int cmd)
    {
        if(cmd < 0) return false;
        if(cmd > 0xFF) return false;
        this.command = cmd;
        return true;
    }
    //---------------------------------------------------------------------------------------------
    public boolean set_len_line(int value) {
        len_line = value;
        return true;
    }
    //---------------------------------------------------------------------------------------------
    public boolean set_len_pause(int value) {
        len_pause = value;
        return true;
    }
    //---------------------------------------------------------------------------------------------
    public boolean set_delay_ms(int value) {
        delay_ms = value;
        return true;
    }
    //---------------------------------------------------------------------------------------------
    public boolean set_color_R(int value) {
        color_R = value;
        return true;
    }
    //---------------------------------------------------------------------------------------------
    public boolean set_color_G(int value) {
        color_G = value;
        return true;
    }
    //---------------------------------------------------------------------------------------------
    public boolean set_color_B(int value) {
        color_B = value;
        return true;
    }
    //---------------------------------------------------------------------------------------------
    private int convert(int value) {
        byte data0 = (byte) value;
        byte data1 = (byte) (value >> 8);

        return (data0 << 8) + data1;
    }
    //---------------------------------------------------------------------------------------------
    String get_string()
    {
        StringBuilder str = new StringBuilder();

        cnt_data = 9;

        str.append(':');
        str.append(String.format("%02X", address));
        str.append(String.format("%02X", command));
        str.append(String.format("%02X", cnt_data));

        str.append(String.format("%04X", convert(len_line)));
        str.append(String.format("%04X", convert(len_pause)));
        str.append(String.format("%04X", convert(delay_ms)));

        str.append(String.format("%02X", color_R));
        str.append(String.format("%02X", color_G));
        str.append(String.format("%02X", color_B));

        str.append('\n');

        return str.toString();
    }
    //---------------------------------------------------------------------------------------------
}
