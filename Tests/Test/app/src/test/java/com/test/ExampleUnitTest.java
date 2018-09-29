package com.test;

import org.junit.Test;

import static org.junit.Assert.*;

public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test_Memory() throws Exception {
        Memory mem = new Memory(null);
        assertEquals(true,  mem.get_boolean_value(5));
        assertEquals(true,  mem.get_boolean_value(-5));
        assertEquals(false, mem.get_boolean_value(0));
    }

    @Test
    public void test_ImageAdapter() throws Exception {
        ImageAdapter ia = new ImageAdapter();
        assertEquals(0, ia.getCount());
        assertEquals(0, ia.getItem(10));
    }

    @Test
    public void test_Test_class() throws Exception {
        Test_class tc = new Test_class();
        assertEquals(0, tc.get_count());

        float a = 5.0f;
        float b = 0.0f;
        assertEquals(5.0f, tc.calc(a, b), 0.1f);
    }
}
