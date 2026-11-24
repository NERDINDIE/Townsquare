package com.example.audio

import com.example.data.model.MediaItemEntity
import com.example.data.model.MediaType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SimultaneousEqEngineTest {

    @Test
    fun `detects optimal curves across music radio podcast and audiobook`() {
        val songItem = MediaItemEntity(
            id = 1L,
            type = MediaType.SONG.name,
            title = "Neo-Wave Cyber Synth Session",
            subtitle = "Electronic",
            authorName = "DJ Neon",
            channelId = "ch_music",
            channelName = "Music",
            bodyText = "Electronic session",
            tags = "synth electronic"
        )
        val radioItem = MediaItemEntity(
            id = 2L,
            type = MediaType.RADIO_STATION.name,
            title = "101.4 FM Townsquare Broadcast",
            subtitle = "Live Radio",
            authorName = "FM Desk",
            channelId = "ch_radio",
            channelName = "Radio",
            bodyText = "FM Broadcast",
            stationFrequency = "101.4 MHz"
        )
        val podcastItem = MediaItemEntity(
            id = 3L,
            type = MediaType.PODCAST_EPISODE.name,
            title = "Civic Builders Weekly",
            subtitle = "Deep conversation",
            authorName = "Alex Chen",
            channelId = "ch_pod",
            channelName = "Podcasts",
            bodyText = "Episode 42",
            tags = "podcast"
        )
        val audiobookItem = MediaItemEntity(
            id = 4L,
            type = MediaType.PODCAST_EPISODE.name,
            title = "The Architecture of Cities (Audiobook Chapter 1)",
            subtitle = "Audiobook Narrative",
            authorName = "Elena Vance",
            channelId = "ch_book",
            channelName = "Audiobooks",
            bodyText = "Chapter 1",
            tags = "audiobook"
        )

        val base = SimultaneousEqEngine.defaultState()

        val songState = SimultaneousEqEngine.computeState(base, item = songItem)
        assertEquals(EqContentCategory.MUSIC, songState.effectiveCategory)
        assertEquals("Wide Harmonic Electronic", songState.activeSubProfileLabel)

        val radioState = SimultaneousEqEngine.computeState(base, item = radioItem)
        assertEquals(EqContentCategory.RADIO, radioState.effectiveCategory)
        assertEquals(-16.0f, radioState.targetLufs, 0.01f)

        val podcastState = SimultaneousEqEngine.computeState(base, item = podcastItem)
        assertEquals(EqContentCategory.PODCAST, podcastState.effectiveCategory)
        // Podcasts cut sub-rumble below 64Hz and boost vocal presence @ 4kHz
        assertTrue(podcastState.contentCurveDb[0] < 0f)
        assertTrue(podcastState.contentCurveDb[5] > 2f)

        val bookState = SimultaneousEqEngine.computeState(base, item = audiobookItem)
        assertEquals(EqContentCategory.AUDIOBOOK, bookState.effectiveCategory)
        assertEquals(-14.0f, bookState.targetLufs, 0.01f)
    }

    @Test
    fun `simultaneously applies inverse gear compensation and keeps net eq within safe bounds`() {
        val bassHeavySeed = SimultaneousEqState(
            selectedGearProfile = OutputGearProfile.BASS_HEAVY_CANS,
            gearSignatureDb = OutputGearProfile.BASS_HEAVY_CANS.nativeColorationDb,
            isGearCompensationEnabled = true,
            autoHeadroomEnabled = true
        )
        val musicItem = MediaItemEntity(
            id = 10L,
            type = MediaType.SONG.name,
            title = "Sunday Acoustic Coffee",
            subtitle = "Acoustic",
            authorName = "Artist",
            channelId = "c",
            channelName = "Music",
            bodyText = "Acoustic",
            tags = "acoustic"
        )

        val computed = SimultaneousEqEngine.computeState(bassHeavySeed, item = musicItem)

        // Bass-heavy cans have +7.0dB @ 64Hz native coloration -> inverse compensation must be negative
        assertTrue(computed.gearCompensationDb[1] < -5f)
        // Net simultaneous curve sums content + inverse compensation
        assertTrue(computed.netSimultaneousDb[1] < computed.contentCurveDb[1])
        // All 8 bands remain clamped safely inside [-12, +12]
        assertTrue(computed.netSimultaneousDb.all { it in SimultaneousEqEngine.MIN_DB..SimultaneousEqEngine.MAX_DB })
    }

    @Test
    fun `bypass compare zeroes net output while preserving user gear and category selections`() {
        val configured = SimultaneousEqState(
            selectedGearProfile = OutputGearProfile.BRIGHT_V_IEM,
            gearSignatureDb = OutputGearProfile.BRIGHT_V_IEM.nativeColorationDb,
            categoryOverride = EqContentCategory.PODCAST,
            isBypassCompare = true
        )

        val bypassed = SimultaneousEqEngine.computeState(configured)
        assertEquals(List(8) { 0f }, bypassed.netSimultaneousDb)
        assertEquals(0f, bypassed.preampGainDb, 0.01f)
        assertEquals(OutputGearProfile.BRIGHT_V_IEM, bypassed.selectedGearProfile)
        assertEquals(EqContentCategory.PODCAST, bypassed.effectiveCategory)
    }
}
