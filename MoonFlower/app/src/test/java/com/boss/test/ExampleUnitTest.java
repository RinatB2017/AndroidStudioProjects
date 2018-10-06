package com.boss.test;

import com.boss.moonflower.LED_points;
import com.boss.moonflower.MainActivity;
import com.boss.moonflower.Memory;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test_LED_points() {
        LED_points lp = new LED_points();

        assertEquals(lp.size(), 19);
    }
}