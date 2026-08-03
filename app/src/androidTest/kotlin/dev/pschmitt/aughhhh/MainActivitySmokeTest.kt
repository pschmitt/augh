package dev.pschmitt.aughhhh

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivitySmokeTest {
    @get:Rule val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun editPresentAndBackJourneyWorks() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        check(device.wait(Until.hasObject(By.text("Your sign, your rules")), 5_000))
        device.findObject(By.text("Present")).click()
        check(device.wait(Until.hasObject(By.desc("Exit present")), 5_000))
        device.findObject(By.desc("Exit present")).click()
        check(device.wait(Until.hasObject(By.text("Your sign, your rules")), 5_000))
    }
}
