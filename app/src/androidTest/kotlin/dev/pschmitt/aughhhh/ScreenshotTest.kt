package dev.pschmitt.aughhhh

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import tools.fastlane.screengrab.Screengrab
import tools.fastlane.screengrab.UiAutomatorScreenshotStrategy
import tools.fastlane.screengrab.locale.LocaleTestRule

/**
 * Captures Play Store listing screenshots (en-US only, see fastlane/Screengrabfile) by driving
 * the same edit -> present journey as [MainActivitySmokeTest].
 */
@RunWith(AndroidJUnit4::class)
class ScreenshotTest {
    companion object {
        @get:ClassRule @JvmStatic val localeTestRule = LocaleTestRule()
    }

    @get:Rule val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun captureStoreScreenshots() {
        Screengrab.setDefaultScreenshotStrategy(UiAutomatorScreenshotStrategy())
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        check(device.wait(Until.hasObject(By.text("Present")), 5_000))
        Screengrab.screenshot("01_editor")

        device.findObject(By.text("Present")).click()
        check(device.wait(Until.hasObject(By.desc("Exit present")), 5_000))
        // Let the entry zoom/fade transition (AUG-77) settle before capturing.
        Thread.sleep(600)
        Screengrab.screenshot("02_present")

        device.findObject(By.desc("Exit present")).click()
        check(device.wait(Until.hasObject(By.text("Present")), 5_000))
    }
}
