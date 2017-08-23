package com.test;

import org.junit.Test;

import static org.junit.Assert.*;

public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test_memory() throws Exception {
        Memory mem = new Memory(null);
        assertEquals(true,  mem.get_boolean_value(5));
        assertEquals(true,  mem.get_boolean_value(-5));
        assertEquals(false, mem.get_boolean_value(0));
    }
}
