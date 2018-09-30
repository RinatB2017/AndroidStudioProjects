package com.boss.cyberbiology;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.ImageView;

class World {
    //public static World simulation;
    public static int width;
    public static int height;
    public static Bot[][] matrix;    //Матрица мира
    public int generation;
    public int population;
    public int organic;
    private ImageView view;
    private Bitmap bitmap;

    Paint mPaint;
    Canvas g;

    public World(ImageView view, Bitmap bitmap, int w, int h) {
        this.view = view;
        this.bitmap = bitmap;
        this.width = w;
        this.height = h;

        mPaint = new Paint();
        g = new Canvas(bitmap);
        view.setImageBitmap(bitmap);

        matrix = new Bot[width][height];

        generateAdam();
        run();
    }

    public void paint() {

        /*
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        g.drawLine(0, 0, width, height, mPaint);
        */

        g.drawRect(49, 49, width * 4 + 1, height * 4 + 1, mPaint);

        population = 0;
        organic = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix[x][y] == null) {
                    mPaint.setColor(Color.WHITE);
                    g.drawRect(50 + x * 4, 50 + y * 4, 4, 4, mPaint);
                } else if ((matrix[x][y].alive == 1) || (matrix[x][y].alive == 2)) {
                    mPaint.setColor(Color.rgb(200.0f, 200.0f, 200.0f));
                    g.drawRect(50 + x * 4, 50 + y * 4, 4, 4, mPaint);
                    organic = organic + 1;
                } else if (matrix[x][y].alive == 3) {
                    mPaint.setColor(Color.BLACK);
                    g.drawRect(50 + x * 4, 50 + y * 4, 4, 4, mPaint);

//                    g.setColor(new Color(matrix[x][y].c_red, matrix[x][y].c_green, matrix[x][y].c_blue));
                    int green = (int) (matrix[x][y].c_green - ((matrix[x][y].c_green * matrix[x][y].health) / 2000));
                    if (green < 0) green = 0;
                    if (green > 255) green = 255;
                    int blue = (int) (matrix[x][y].c_blue * 0.8 - ((matrix[x][y].c_blue * matrix[x][y].mineral) / 2000));
                    mPaint.setColor(Color.rgb(matrix[x][y].c_red, green, blue));
//                    g.setColor(new Color(matrix[x][y].c_red, matrix[x][y].c_green, matrix[x][y].c_blue));
                    g.drawRect(50 + x * 4 + 1, 50 + y * 4 + 1, 3, 3, mPaint);
                    population = population + 1;
                }
            }
        }

        mPaint.setColor(Color.WHITE);
        g.drawRect(50, 30, 140, 16, mPaint);
        mPaint.setColor(Color.BLACK);
        g.drawText("Generation: " + String.valueOf(generation), 54, 44, mPaint);

        mPaint.setColor(Color.WHITE);
        g.drawRect(200, 30, 140, 16, mPaint);
        mPaint.setColor(Color.BLACK);
        g.drawText("Population: " + String.valueOf(population), 204, 44, mPaint);

        mPaint.setColor(Color.WHITE);
        g.drawRect(350, 30, 140, 16, mPaint);
        mPaint.setColor(Color.BLACK);
        g.drawText("Organic: " + String.valueOf(organic), 354, 44, mPaint);

        view.setImageBitmap(bitmap);
    }

    // делает паузу
    public void sleep() {
        try {
            int delay = 1000;
            Thread.sleep(delay);
        } catch (InterruptedException e) {
        }
    }

    // Основной цикл ------------------------------------------------------------------------
    public void run() {
        //пока не остановят симуляцию
        generation = 0;
        while (true) {
            //Обновляем матрицу мира
            for (int yw = 0; yw < height; yw++) {
                for (int xw = 0; xw < width; xw++) {
                    if (matrix[xw][yw] != null) {
                        matrix[xw][yw].step();      //Выполняем ход бота
                    }
                }
            }
            generation = generation + 1;
            if (generation % 10 == 0) {
                paint();        //отображаем текущее состояние симуляции на экран
            }
            //sleep();        //пауза между ходами
        }
    }

    public void generateAdam() {
        //==========  1  ==============
        // бот номер 1 - это уже реальный бот
        Bot bot = new Bot();

        bot.adr = 0;
        bot.x = width / 2;      // координаты бота
        bot.y = height / 2;
        bot.health = 990;       // энергия
        bot.mineral = 0;        // минералы
        bot.alive = 3;          // отмечаем, что бот живой
        bot.c_red = 170;        // задаем цвет бота
        bot.c_blue = 170;
        bot.c_green = 170;
        bot.direction = 5;      // направление
        bot.mprev = null;       // бот не входит в многоклеточные цепочки, поэтому ссылки
        bot.mnext = null;       // на предыдущего, следующего в многоклеточной цепочке пусты
        for (int i = 0; i < 64; i++) {        // заполняем геном командой 25 - фотосинтез
            bot.mind[i] = 25;
        }

        matrix[bot.x][bot.y] = bot;            // даём ссылку на бота в массиве world[]
    }
}
