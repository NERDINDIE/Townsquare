package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.MediaChannelEntity
import com.example.ui.components.MorningBriefCard
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    composeTestRule.setContent {
      MyApplicationTheme {
        MorningBriefCard(
          channels = listOf(
            MediaChannelEntity(
              id = "chan_1",
              name = "Horizon Tech",
              category = "Technology",
              description = "Tech horizons",
              iconEmoji = "⚡",
              morningBriefHighlight = "Daily tech highlights ready.",
              bannerColorHex = 0xFF00D2FF,
              isFollowed = true
            )
          ),
          onOpenFullBrief = {},
          onPlayAudioBrief = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
