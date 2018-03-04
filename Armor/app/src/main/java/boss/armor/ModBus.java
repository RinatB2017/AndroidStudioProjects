package boss.armor;

import java.io.ByteArrayOutputStream;

//---------------------------------------------------------------------------------------------
/**
 * Created by tux on 31.03.16.
 */
//---------------------------------------------------------------------------------------------
public class ModBus {
    int command;
    ByteArrayOutputStream ba;
    //---------------------------------------------------------------------------------------------
    public ModBus()
    {
        this.command = 0;
        ba = new ByteArrayOutputStream();
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
        return true;
    }
    //---------------------------------------------------------------------------------------------
    String get_string()
    {
        StringBuilder str = new StringBuilder();

        str.append(':');
        str.append(String.format("%02X", command));
        for(int n=0; n<ba.size(); n++)
        {
            str.append(String.format("%02X", ba.toByteArray()[n]));
        }
        str.append('\n');

        return str.toString();
    }
    //---------------------------------------------------------------------------------------------
}
