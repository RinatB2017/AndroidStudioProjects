package com.boss.for_testing;

import java.io.ByteArrayOutputStream;

public class ConvertBytes {
    ByteArrayOutputStream ba;

    public ConvertBytes() {
        ba = new ByteArrayOutputStream();
    }

    public String to_sting(byte hi, byte lo) {
        ba.write(hi << 4 | lo);
        return String.format("%02X", ba.toByteArray()[0]);
    }

    private byte convert(byte value) {
        byte res = 0;

        switch (value) {
            case '0': res = 0x00; break;
            case '1': res = 0x01; break;
            case '2': res = 0x02; break;
            case '3': res = 0x03; break;
            case '4': res = 0x04; break;
            case '5': res = 0x05; break;
            case '6': res = 0x06; break;
            case '7': res = 0x07; break;
            case '8': res = 0x08; break;
            case '9': res = 0x09; break;
            case 'A': res = 0x0A; break;
            case 'B': res = 0x0B; break;
            case 'C': res = 0x0C; break;
            case 'D': res = 0x0D; break;
            case 'E': res = 0x0E; break;
            case 'F': res = 0x0F; break;

            case 'a': res = 0x0A; break;
            case 'b': res = 0x0B; break;
            case 'c': res = 0x0C; break;
            case 'd': res = 0x0D; break;
            case 'e': res = 0x0E; break;
            case 'f': res = 0x0F; break;
        }
        return res;
    }

    public byte to_byte(String value) {
        byte hi = value.getBytes()[0];
        byte lo = value.getBytes()[1];

        byte hi_res = convert(hi);
        byte lo_res = convert(lo);

        int res = (hi_res << 4) | lo_res;

        return (byte)res;
    }
}
