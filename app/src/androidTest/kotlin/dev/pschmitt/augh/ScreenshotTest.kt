package dev.pschmitt.augh

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
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
 * Captures Play Store listing screenshots (en-US only, see fastlane/Screengrabfile) by driving the
 * same edit -> present -> about journey as [MainActivitySmokeTest].
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

        // The first wait is flaky on cold CI emulators (tablet profiles especially):
        // OS readiness can precede the app's first Compose frame. Give startup more time than the
        // later waits, which run against an already-warm process.
        check(device.wait(Until.hasObject(By.text("Present")), 60_000))
        Screengrab.screenshot("01_editor")

        device.findObject(By.text("Present")).click()
        check(device.wait(Until.hasObject(By.desc("Exit present")), 15_000))
        // Let the entry zoom/fade transition (AUG-77) settle before capturing.
        Thread.sleep(600)
        Screengrab.screenshot("02_present")

        device.findObject(By.desc("Exit present")).click()
        check(device.wait(Until.hasObject(By.text("Present")), 15_000))

        // About sits behind Settings (MainActivity.kt onAbout), but its own back button drops
        // straight to the editor - showSettings is cleared on the way in, not just showAbout.
        device.findObject(By.desc("Settings")).click()
        check(device.wait(Until.hasObject(By.text("About")), 15_000))

        // Compose exposes off-screen text to UiAutomator without scrolling it into view. Scroll
        // until the About row has visible bounds, then tap those bounds instead of the off-screen
        // node coordinates.
        val about = device.findObject(By.text("About"))
        var aboutBounds = about.visibleBounds
        var scrollAttempts = 0
        while (aboutBounds.isEmpty && scrollAttempts < 4) {
            device.swipe(
                device.displayWidth / 2,
                device.displayHeight * 3 / 4,
                device.displayWidth / 2,
                device.displayHeight / 4,
                20,
            )
            aboutBounds = about.visibleBounds
            scrollAttempts++
        }
        check(!aboutBounds.isEmpty)
        device.click(aboutBounds.centerX(), aboutBounds.centerY())
        check(device.wait(Until.hasObject(By.desc("AUGH! app logo")), 15_000))
        Screengrab.screenshot("03_about")

        device.findObject(By.desc("Back")).click()
        check(device.wait(Until.hasObject(By.text("Present")), 15_000))
    }
}
