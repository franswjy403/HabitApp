package com.dicoding.habitapp.ui.list

import android.content.ComponentName
import androidx.test.InstrumentationRegistry
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.dicoding.habitapp.R
import com.dicoding.habitapp.ui.add.AddHabitActivity

@RunWith(AndroidJUnit4::class)
class HabitActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(HabitListActivity::class.java)

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun cleanup() {
        Intents.release()
    }

    @Test
    fun testAddHabitButton() {
        Espresso.onView(withId(R.id.fab)).perform(ViewActions.click())

        Intents.intended(IntentMatchers.anyIntent())

        val expectedComponent = ComponentName(InstrumentationRegistry.getTargetContext(), AddHabitActivity::class.java)
        Intents.intended(IntentMatchers.hasComponent(expectedComponent))
    }
}