package com.boss.armor;

import java.io.ByteArrayOutputStream;

//---------------------------------------------------------------------------------------------
/**
 * Created by tux on 31.03.16.
 */
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
 */
//---------------------------------------------------------------------------------------------
public class ModBus {
    StringBuilder res_str;
    //---------------------------------------------------------------------------------------------
    public ModBus()
    {
        res_str = new StringBuilder();
    }
    //---------------------------------------------------------------------------------------------
    public void add_begin_simvol()
    {
        res_str.append(':');
    }
    //---------------------------------------------------------------------------------------------
    public void add_end_simvol()
    {
        res_str.append('\n');
    }
    //---------------------------------------------------------------------------------------------
    public void add_uint8_t(int value)
    {
        res_str.append(String.format("%02X", value));
    }
    //---------------------------------------------------------------------------------------------
    public void add_uint16_t(int value)
    {
        int hi = (value >> 8) & 0xFF;
        int lo = value & 0xFF;
        res_str.append(String.format("%02X", lo));
        res_str.append(String.format("%02X", hi));
    }
    //---------------------------------------------------------------------------------------------
    String get_string()
    {
        return res_str.toString();
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
}
