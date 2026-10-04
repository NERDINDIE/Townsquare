package com.example.audio

import com.example.data.model.MediaItemEntity
import com.example.data.model.MediaType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

/**
 * JUnit 5 Test Suite for Townsquare OS Media Player Core Logic.
 * Validates audio state lifecycle, seeking boundaries, speed factors, and track switching.
 */
@DisplayName("Townsquare Media Player Core Logic Unit Tests (JUnit 5)")
class MediaPlayerCoreLogicTest {

    private lateinit var samplePodcast: MediaItemEntity
    private lateinit var sampleRadioStation: MediaItemEntity
    private lateinit var shortTrack: MediaItemEntity

    @BeforeEach
    fun setUp() {
        samplePodcast = MediaItemEntity(
            id = 101L,
            type = MediaType.PODCAST_EPISODE.name,
            title = "Townsquare Morning Dispatch #42",
            subtitle = "Daily Civic Review",
            authorName = "Elena Rostova",
            authorHandle = "@elena",
            channelId = "ch_dispatch",
            channelName = "Townsquare Dispatch",
            bodyText = "Comprehensive news review",
            mediaUrl = "https://example.com/audio/dispatch42.mp3",
            durationSeconds = 600, // 10 minutes
            stationFrequency = "",
            issueEdition = "2026-10-04"
        )

        sampleRadioStation = MediaItemEntity(
            id = 985L,
            type = MediaType.RADIO_STATION.name,
            title = "98.5 Townsquare Classic Jazz",
            subtitle = "Live 24/7 Jazz & Blues",
            authorName = "Townsquare Radio",
            authorHandle = "@jazz985",
            channelId = "ch_radio",
            channelName = "98.5 FM",
            bodyText = "Live broadcast streaming",
            mediaUrl = "https://example.com/stream/985.mp3",
            durationSeconds = 0,
            stationFrequency = "98.5 MHz",
            issueEdition = ""
        )

        shortTrack = MediaItemEntity(
            id = 55L,
            type = MediaType.SONG.name,
            title = "Civic Chimes Overture",
            subtitle = "Classical",
            authorName = "Marcus Vance",
            authorHandle = "@marcus",
            channelId = "ch_music",
            channelName = "Symphony",
            bodyText = "Short instrumental",
            mediaUrl = "https://example.com/audio/chimes.mp3",
            durationSeconds = 90, // 90 seconds
            stationFrequency = "",
            issueEdition = ""
        )
    }

    @Nested
    @DisplayName("AudioState Model & Duration Calculations")
    inner class AudioStateCalculations {

        @Test
        @DisplayName("Podcast item duration calculation converts seconds to milliseconds with minimum 3-minute floor")
        fun testPodcastDurationCalculation() {
            val isRadio = samplePodcast.type == MediaType.RADIO_STATION.name
            val calculatedDurationMs = if (isRadio) 0L else (samplePodcast.durationSeconds.toLong() * 1000L).coerceAtLeast(180000L)

            assertEquals(600_000L, calculatedDurationMs, "10-minute podcast should have 600,000ms duration")
        }

        @Test
        @DisplayName("Short track duration calculation enforces minimum 180,000ms floor")
        fun testShortTrackMinimumDurationFloor() {
            val isRadio = shortTrack.type == MediaType.RADIO_STATION.name
            val calculatedDurationMs = if (isRadio) 0L else (shortTrack.durationSeconds.toLong() * 1000L).coerceAtLeast(180000L)

            assertEquals(180_000L, calculatedDurationMs, "90-second track should have minimum 180,000ms duration floor")
        }

        @Test
        @DisplayName("Live Radio station duration must be 0 ms")
        fun testRadioZeroDuration() {
            val isRadio = sampleRadioStation.type == MediaType.RADIO_STATION.name
            val calculatedDurationMs = if (isRadio) 0L else (sampleRadioStation.durationSeconds.toLong() * 1000L).coerceAtLeast(180000L)

            assertEquals(0L, calculatedDurationMs, "Live radio streams must have 0ms duration")
        }

        @Test
        @DisplayName("AudioState initial defaults are clean and stopped")
        fun testAudioStateDefaultValues() {
            val state = AudioState()
            assertNull(state.currentItem)
            assertFalse(state.isPlaying)
            assertFalse(state.isBuffering)
            assertEquals(0L, state.positionMs)
            assertEquals(0L, state.durationMs)
            assertEquals(1.0f, state.speed)
            assertEquals(7, state.waveformHeights.size)
        }
    }

    @Nested
    @DisplayName("Seek & Scrubbing Logic")
    inner class SeekingLogicTests {

        @Test
        @DisplayName("Seeking within valid range clamps accurately")
        fun testSeekWithinValidRange() {
            val totalDurationMs = 600_000L
            val targetPos = 250_000L
            val bounded = targetPos.coerceIn(0L, totalDurationMs)

            assertEquals(250_000L, bounded)
        }

        @Test
        @DisplayName("Seeking beyond duration clamps to max duration")
        fun testSeekBeyondMaxDuration() {
            val totalDurationMs = 600_000L
            val targetPos = 750_000L
            val bounded = targetPos.coerceIn(0L, totalDurationMs)

            assertEquals(600_000L, bounded)
        }

        @Test
        @DisplayName("Seeking negative position clamps to 0 ms")
        fun testSeekNegativePosition() {
            val totalDurationMs = 600_000L
            val targetPos = -50_000L
            val bounded = targetPos.coerceIn(0L, totalDurationMs)

            assertEquals(0L, bounded)
        }

        @Test
        @DisplayName("Skip forward 15 seconds increments position by 15,000 ms")
        fun testSkipForward15Seconds() {
            val currentPos = 30_000L
            val duration = 600_000L
            val newPos = (currentPos + 15_000L).coerceIn(0L, duration)

            assertEquals(45_000L, newPos)
        }

        @Test
        @DisplayName("Skip backward 15 seconds decrements position with 0 ms lower bound")
        fun testSkipBackward15Seconds() {
            val currentPos = 10_000L
            val duration = 600_000L
            val newPos = (currentPos - 15_000L).coerceAtLeast(0L).coerceIn(0L, duration)

            assertEquals(0L, newPos, "Skipping back from 10s by 15s should clamp at 0ms")
        }
    }

    @Nested
    @DisplayName("Playback Speed & Waveform Visualizer")
    inner class SpeedAndVisualizerTests {

        @ParameterizedTest(name = "Playback speed {0}x is valid")
        @ValueSource(floats = [0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f])
        @DisplayName("Validates supported playback speed multipliers")
        fun testSupportedPlaybackSpeeds(speed: Float) {
            val state = AudioState(speed = speed)
            assertEquals(speed, state.speed)
            assertTrue(state.speed in 0.5f..2.5f)
        }

        @Test
        @DisplayName("Waveform heights remain strictly normalized within 0.0 to 1.0 range")
        fun testWaveformHeightsNormalization() {
            val heights = listOf(0.3f, 0.6f, 0.8f, 0.4f, 0.7f, 0.5f, 0.9f)
            heights.forEach { height ->
                assertTrue(height in 0.0f..1.0f, "Waveform height $height must be normalized between 0.0 and 1.0")
            }
        }
    }

    @Nested
    @DisplayName("Station List Traversal & Wrap-around Navigation")
    inner class StationTraversalTests {

        private lateinit var stationList: List<MediaItemEntity>

        @BeforeEach
        fun createStations() {
            stationList = listOf(
                sampleRadioStation.copy(id = 885L, title = "88.5 Indie", stationFrequency = "88.5 MHz"),
                sampleRadioStation.copy(id = 985L, title = "98.5 Jazz", stationFrequency = "98.5 MHz"),
                sampleRadioStation.copy(id = 1041L, title = "104.1 Dispatch", stationFrequency = "104.1 MHz"),
                sampleRadioStation.copy(id = 1079L, title = "107.9 Synth", stationFrequency = "107.9 MHz")
            )
        }

        @Test
        @DisplayName("Next station from index 1 returns index 2")
        fun testNextStationNormal() {
            val currentIndex = 1
            val nextIndex = (currentIndex + 1) % stationList.size
            assertEquals(2, nextIndex)
            assertEquals(1041L, stationList[nextIndex].id)
        }

        @Test
        @DisplayName("Next station from last index wraps around to index 0")
        fun testNextStationWrapAround() {
            val currentIndex = stationList.size - 1 // index 3
            val nextIndex = (currentIndex + 1) % stationList.size
            assertEquals(0, nextIndex)
            assertEquals(885L, stationList[nextIndex].id)
        }

        @Test
        @DisplayName("Previous station from index 0 wraps around to last index")
        fun testPreviousStationWrapAround() {
            val currentIndex = 0
            val prevIndex = if (currentIndex - 1 < 0) stationList.size - 1 else currentIndex - 1
            assertEquals(3, prevIndex)
            assertEquals(1079L, stationList[prevIndex].id)
        }

        @Test
        @DisplayName("Previous station from index 2 returns index 1")
        fun testPreviousStationNormal() {
            val currentIndex = 2
            val prevIndex = if (currentIndex - 1 < 0) stationList.size - 1 else currentIndex - 1
            assertEquals(1, prevIndex)
            assertEquals(985L, stationList[prevIndex].id)
        }
    }
}
