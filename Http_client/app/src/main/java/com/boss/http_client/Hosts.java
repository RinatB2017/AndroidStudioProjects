package com.boss.http_client;

import java.io.Serializable;
import java.util.ArrayList;

public class Hosts implements Serializable {

    ArrayList<Data> l_data;

    public Hosts() {
        l_data = new ArrayList<>();
    }

    public boolean add_data(String ip, int port, int pwm_value) {
        Data data = new Data();
        data.ip = ip;
        data.port = port;
        data.pwm_value = pwm_value;

        l_data.add(data);
        return true;
    }

    public boolean clean_all() {
        l_data.clear();
        return true;
    }

    public int get_count() {
        return l_data.size();
    }

    public boolean set_data(int index, Data data) {
        if(index < 0) {
            return false;
        }
        if(index > (l_data.size() - 1)) {
            return false;
        }
        l_data.set(index, data);
        return true;
    }

    public Data get_data(int index) {
        if(index < 0) {
            return null;
        }
        if(index > (l_data.size() - 1)) {
            return null;
        }

        Data data = (Data) l_data.get(index);
        if(data == null) {
            return null;
        }

        return data;
    }
}
