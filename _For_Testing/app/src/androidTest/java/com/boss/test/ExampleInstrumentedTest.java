package com.boss.test;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

//---
import androidx.test.rule.ActivityTestRule;
import android.widget.Button;

import com.boss.for_testing.MainActivity;
import com.boss.for_testing.R;

import org.junit.Before;
import org.junit.Rule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
//---

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    //---
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void onClick() throws Exception {
        onView(withId(R.id.btn_test)).perform(click());

        //onView(withId(R.id.textViewInfo)).check(matches(withText("Hello")));

        // можно поискать существующий UI-обхект с данными и проверить, есть ли ав нёи заданный текст
        //onView(withId(R.id.name)).check(matches(withText("name")));
    }
    //---

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.boss.for_testing", appContext.getPackageName());

        //---
        MainActivity activity = mActivityRule.getActivity();

        Button btn = (Button)activity.findViewById(R.id.btn_test);
        btn.setText("XXX");

        onView(withId(R.id.btn_test)).check(matches(withText("XXX")));
    }
}
