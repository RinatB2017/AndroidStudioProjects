package com.boss.cyberbiology;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

class World {
    public static World simulation;
    public int width;
    public int height;
    public Bot[][] matrix;    //Матрица мира
    public int generation;
    public int population;
    public int organic;

    Paint mPaint;
    Canvas g;

    public World() {
        simulation = this;
        mPaint = new Paint();
        g = new Canvas();
    }

    public void paint() {

        g.drawRect(49, 49, simulation.width * 4 + 1, simulation.height * 4 + 1, mPaint);

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
    }

}
