package boss.armor;

import java.io.ByteArrayOutputStream;

//---------------------------------------------------------------------------------------------
/**
 * Created by tux on 31.03.16.
 */
//---------------------------------------------------------------------------------------------
public class ModBus {
    int address;
    int command;
    int cnt_data;
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
    public boolean set_data(ByteArrayOutputStream data)
    {
        if(data.size() == 0)  return false;
        if(data.size() > 255) return false;
        ba = data;
        cnt_data = data.size();
        return true;
    }
    //---------------------------------------------------------------------------------------------
    String get_string()
    {
        StringBuilder str = new StringBuilder();

        str.append(':');
        str.append(String.format("%02X", address));
        str.append(String.format("%02X", command));
        str.append(String.format("%02X", cnt_data));
        for(int n=0; n<ba.size(); n++)
        {
            str.append(String.format("%02X", ba.toByteArray()[n]));
        }
        str.append('\n');

        return str.toString();
    }
    //---------------------------------------------------------------------------------------------
}
