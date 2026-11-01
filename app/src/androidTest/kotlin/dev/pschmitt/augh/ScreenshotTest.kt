package dev.pschmitt.augh

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
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

    @get:Rule val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun captureStoreScreenshots() {
        Screengrab.setDefaultScreenshotStrategy(UiAutomatorScreenshotStrategy())
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        // The first wait is flaky on cold CI emulators (tablet profiles especially):
        // OS readiness can precede the app's first Compose frame. Give startup more time than the
        // later waits, which run against an already-warm process.
        check(device.wait(Until.hasObject(By.text("Present")), 60_000))
        Screengrab.screenshot("01_editor")

        composeRule.onNodeWithText("Present").performClick()
        check(device.wait(Until.hasObject(By.desc("Exit present")), 15_000))
        // Let the entry zoom/fade transition (AUG-77) settle before capturing.
        Thread.sleep(600)
        Screengrab.screenshot("02_present")

        composeRule.onNodeWithContentDescription("Exit present").performClick()
        check(device.wait(Until.hasObject(By.text("Present")), 15_000))

        // About sits behind Settings (MainActivity.kt onAbout), but its own back button drops
        // straight to the editor - showSettings is cleared on the way in, not just showAbout.
        composeRule.onNodeWithContentDescription("Settings").performClick()
        check(device.wait(Until.hasObject(By.text("About")), 15_000))

        // Compose exposes off-screen text to UiAutomator without scrolling it into view. Use the
        // Compose semantics tree to scroll the settings column and click its About row.
        composeRule.onNodeWithText("About").performScrollTo().performClick()
        check(device.wait(Until.hasObject(By.desc("AUGH! app logo")), 15_000))
        Screengrab.screenshot("03_about")

        composeRule.onNodeWithContentDescription("Back").performClick()
        check(device.wait(Until.hasObject(By.text("Present")), 15_000))
    }
}
