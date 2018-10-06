package com.boss.test;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.boss.moonflower.MainActivity;
import com.boss.moonflower.Memory;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.boss.moonflower", appContext.getPackageName());
    }

    @Test
    public void test_Memory() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        String str_name = "test_str";
        int check_value = 5;

        Memory m = new Memory(appContext);

        m.set_string(str_name, "test");
        assertEquals("test", m.get_string(str_name));

        assertEquals(true, m.set_int_value(str_name, check_value));
        assertEquals(check_value, m.get_int_value(str_name));

        m.set_boolean_value(str_name, true);
        assertEquals(true, m.get_boolean_value(str_name));
    }

}
