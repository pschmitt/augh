package dev.pschmitt.aughhhh

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivitySmokeTest {
    @get:Rule val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun editPresentAndBackJourneyWorks() {
        composeRule.onNodeWithText("Your sign, your rules").assertIsDisplayed()
        composeRule.onNodeWithText("Present full screen").performClick()
        composeRule.onNodeWithText("Exit present").assertIsDisplayed()
        composeRule.onNodeWithText("Exit present").performClick()
        composeRule.onNodeWithText("Your sign, your rules").assertIsDisplayed()
    }
}
