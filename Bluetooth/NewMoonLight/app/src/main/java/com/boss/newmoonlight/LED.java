package com.boss.newmoonlight;

import java.io.Serializable;

public class LED implements Serializable {
    int number;             // порядковый номер светодиода
    int address;            // адрес светодиода
    float center_x;         // координаты центра
    float center_y;         // координаты центра
    float radius;           // радиус
    int color_border_on;    // цвет бордюра светодиода (ON)
    int color_border_off;   // цвет бордюра светодиода (OFF)
    boolean is_active;      // сейчас активен для изменения

    int hot_color;          // уровень "теплого" цвета
    int cold_color;         // уровень "холодного" цвета

    //TODO
    boolean draw_text;      // нужен текст?
    int color_text;         // цвет текста
    String text;            // текст, выводимый на светодиод
}
