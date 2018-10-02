package com.boss.moonflower;

import java.io.Serializable;
import java.util.ArrayList;

public class LED_points implements Serializable {

    private ArrayList<LED> points;
    private int max_led = 19;

    //---------------------------------------------------------------------------------------------
    public LED_points() {
        points = new ArrayList<LED>();
        for(int n=0; n<max_led; n++) {
            LED led = new LED();
            points.add(led);
        }
    }

    //---------------------------------------------------------------------------------------------
    public void set(int index, LED led) {
        if(index < 0) {
            return;
        }
        if(index > max_led) {
            return;
        }
        points.set(index, led);
    }

    //---------------------------------------------------------------------------------------------
    public LED get(int index) {
        if(index < 0) {
            return null;
        }
        if(index > max_led) {
            return null;
        }

        return points.get(index);
    }

    //---------------------------------------------------------------------------------------------
    public int size() {
        return max_led;
    }

}
