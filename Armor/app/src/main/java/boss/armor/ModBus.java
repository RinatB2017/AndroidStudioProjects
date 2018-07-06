package boss.armor;

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
}
