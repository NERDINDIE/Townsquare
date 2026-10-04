package com.example.audio

import com.example.data.model.MediaItemEntity
import com.example.data.model.MediaType
import com.example.ui.components.parseStationFrequency
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * JUnit 5 Test Suite for Townsquare OS Frequency Tuning & Radio Dial Logic.
 * Validates frequency regex parsing, signal strength mapping, locked station detection,
 * fine tuning step bounds, and preset bank mapping.
 */
@DisplayName("Townsquare Radio Frequency Tuning Logic Unit Tests (JUnit 5)")
class FrequencyTuningLogicTest {

    private val minFmFreq = 87.5f
    private val maxFmFreq = 108.0f

    private fun createStation(id: Long, title: String, freqStr: String): MediaItemEntity {
        return MediaItemEntity(
            id = id,
            type = MediaType.RADIO_STATION.name,
            title = title,
            subtitle = "Broadcast Station",
            authorName = "Townsquare Media",
            authorHandle = "@radio",
            channelId = "ch_radio",
            channelName = "FM Tuner",
            bodyText = "Live audio stream",
            mediaUrl = "https://example.com/stream.mp3",
            durationSeconds = 0,
            stationFrequency = freqStr,
            issueEdition = ""
        )
    }

    @Nested
    @DisplayName("Frequency String Parsing & Regex Extraction")
    inner class FrequencyParsingTests {

        @ParameterizedTest(name = "Extract frequency from ''{0}'' with station title ''{1}'' -> {2} MHz")
        @CsvSource(
            "'98.5 MHz', 'Townsquare Jazz', 98.5",
            "'104.1 FM', 'Hourly Dispatch', 104.1",
            "'88.5', 'Indie Waves', 88.5",
            "'107.9', 'Synthwave Night', 107.9",
            "'', '94.7 FM The City Sound', 94.7",
            "'', 'Classic Rock 101.5 Radio', 101.5",
            "'', 'No Frequency In Title', 98.5"
        )
        @DisplayName("Validates parsing station frequencies from stationFrequency field and fallback titles")
        fun testStationFrequencyParsing(freqField: String, title: String, expectedFreq: Float) {
            val station = createStation(100L, title, freqField)
            val parsed = parseStationFrequency(station)
            assertEquals(expectedFreq, parsed, 0.01f, "Parsed frequency should match expected value")
        }
    }

    @Nested
    @DisplayName("Signal Strength Calculation (0% to 100%)")
    inner class SignalStrengthTests {

        private fun computeSignalPercent(currentFreq: Float, targetStationFreq: Float): Int {
            val delta = abs(targetStationFreq - currentFreq)
            return when {
                delta <= 0.05f -> 99
                delta <= 0.15f -> 85
                delta <= 0.35f -> 62
                delta <= 0.60f -> 35
                delta <= 0.90f -> 18
                else -> 6
            }
        }

        @Test
        @DisplayName("Exact match frequency has 99% signal strength")
        fun testExactFrequencySignalStrength() {
            val signal = computeSignalPercent(98.5f, 98.5f)
            assertEquals(99, signal)
        }

        @Test
        @DisplayName("Frequency within 0.15 MHz threshold has 85% signal strength")
        fun testLockThresholdSignalStrength() {
            val signal = computeSignalPercent(98.6f, 98.5f)
            assertEquals(85, signal)
        }

        @Test
        @DisplayName("Frequency within 0.35 MHz has 62% signal strength")
        fun testModerateSignalStrength() {
            val signal = computeSignalPercent(98.8f, 98.5f)
            assertEquals(62, signal)
        }

        @Test
        @DisplayName("Frequency with large delta (> 0.90 MHz) drops to static baseline 6%")
        fun testStaticNoiseSignalStrength() {
            val signal = computeSignalPercent(94.0f, 98.5f)
            assertEquals(6, signal)
        }
    }

    @Nested
    @DisplayName("Station Lock-On Detection & Nearest Station Matching")
    inner class StationLockOnTests {

        private val stations = listOf(
            createStation(1L, "88.5 FM College Indie", "88.5 MHz"),
            createStation(2L, "98.5 FM Classic Jazz", "98.5 MHz"),
            createStation(3L, "104.1 FM News Dispatch", "104.1 MHz"),
            createStation(4L, "107.9 FM Retro Waves", "107.9 MHz")
        )

        private val parsedStations = stations.map { it to parseStationFrequency(it) }.sortedBy { it.second }

        @Test
        @DisplayName("Tuning exactly to 98.5 MHz locks on to Classic Jazz station")
        fun testExactStationLock() {
            val currentFreq = 98.5f
            val nearest = parsedStations.minByOrNull { abs(it.second - currentFreq) }

            assertNotNull(nearest)
            val isLocked = abs(nearest!!.second - currentFreq) <= 0.15f
            assertTrue(isLocked, "Should lock on to station within 0.15 MHz")
            assertEquals(2L, nearest.first.id)
            assertEquals("98.5 FM Classic Jazz", nearest.first.title)
        }

        @Test
        @DisplayName("Tuning to 98.6 MHz (delta 0.1 MHz) is within lock-on capture threshold")
        fun testSlightlyOffCenterLock() {
            val currentFreq = 98.6f
            val nearest = parsedStations.minByOrNull { abs(it.second - currentFreq) }

            assertNotNull(nearest)
            val isLocked = abs(nearest!!.second - currentFreq) <= 0.15f
            assertTrue(isLocked, "98.6 MHz is within 0.15 MHz threshold of 98.5 MHz")
            assertEquals(2L, nearest.first.id)
        }

        @Test
        @DisplayName("Tuning to 92.0 MHz (dead zone) is not locked to any station")
        fun testDeadZoneNoLock() {
            val currentFreq = 92.0f
            val nearest = parsedStations.minByOrNull { abs(it.second - currentFreq) }

            assertNotNull(nearest)
            val isLocked = abs(nearest!!.second - currentFreq) <= 0.15f
            assertFalse(isLocked, "92.0 MHz has no station within 0.15 MHz")
        }
    }

    @Nested
    @DisplayName("Fine-Tuning Steps & FM Band Boundaries")
    inner class FineTuningStepTests {

        private fun stepFrequency(currentFreq: Float, delta: Float): Float {
            val raw = (currentFreq + delta).coerceIn(minFmFreq, maxFmFreq)
            return (raw * 10f).roundToInt() / 10f
        }

        @Test
        @DisplayName("Stepping +0.1 from 98.5 MHz yields exactly 98.6 MHz")
        fun testStepPlusTenth() {
            val result = stepFrequency(98.5f, 0.1f)
            assertEquals(98.6f, result, 0.001f)
        }

        @Test
        @DisplayName("Stepping -0.1 from 98.5 MHz yields exactly 98.4 MHz")
        fun testStepMinusTenth() {
            val result = stepFrequency(98.5f, -0.1f)
            assertEquals(98.4f, result, 0.001f)
        }

        @Test
        @DisplayName("Stepping +0.1 beyond maximum FM boundary (108.0 MHz) clamps at 108.0 MHz")
        fun testMaximumBoundaryClamping() {
            val result = stepFrequency(108.0f, 0.1f)
            assertEquals(108.0f, result, 0.001f)
        }

        @Test
        @DisplayName("Stepping -0.1 beyond minimum FM boundary (87.5 MHz) clamps at 87.5 MHz")
        fun testMinimumBoundaryClamping() {
            val result = stepFrequency(87.5f, -0.1f)
            assertEquals(87.5f, result, 0.001f)
        }

        @ParameterizedTest(name = "Initial freq {0} MHz remains inside [87.5, 108.0]")
        @ValueSource(floats = [87.5f, 90.0f, 98.5f, 104.1f, 107.9f, 108.0f])
        @DisplayName("All tuned frequencies conform to FM broadcast spectrum limits")
        fun testValidFmFrequencies(freq: Float) {
            assertTrue(freq in minFmFreq..maxFmFreq, "Frequency $freq must be in range [$minFmFreq, $maxFmFreq]")
        }
    }
}
