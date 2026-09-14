package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.MediaDao
import com.example.data.model.JournalEditionEntity
import com.example.data.model.MediaChannelEntity
import com.example.data.model.MediaItemEntity
import com.example.data.model.MediaSpaceEntity
import com.example.data.model.MediaType
import com.example.ui.components.buildFlipbookPages
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: MediaDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.mediaDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun readStringFromContext() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Townsquare", appName)
    }

    @Test
    fun testInsertAndRetrieveMediaItem() = runBlocking {
        val item = MediaItemEntity(
            type = MediaType.SOCIAL_POST.name,
            title = "Test Post",
            subtitle = "Sub",
            authorName = "Alex Chen",
            authorHandle = "@alexchen",
            channelId = "channel_tech",
            channelName = "Horizon Tech",
            bodyText = "Testing the unified media feed item.",
            imageResName = "",
            timestamp = System.currentTimeMillis(),
            likesCount = 5,
            isUserCreated = true
        )
        dao.insertMediaItem(item)

        val items = dao.getAllMediaItems().first()
        assertEquals(1, items.size)
        assertEquals("Test Post", items[0].title)
        assertTrue(items[0].isUserCreated)
    }

    @Test
    fun testThematicChannelsAndMorningBrief() = runBlocking {
        val channel = MediaChannelEntity(
            id = "tech_chan",
            name = "Horizon Tech",
            category = "Technology",
            description = "AI and computing horizons",
            iconEmoji = "⚡",
            morningBriefHighlight = "Breakthroughs in edge models announced today.",
            bannerColorHex = 0xFF00D2FF,
            isFollowed = true
        )
        dao.insertChannels(listOf(channel))

        val followed = dao.getFollowedChannels().first()
        assertEquals(1, followed.size)
        assertEquals("Horizon Tech", followed[0].name)
        assertEquals("Breakthroughs in edge models announced today.", followed[0].morningBriefHighlight)
    }

    @Test
    fun testUserMediaSpaceCreation() = runBlocking {
        val space = MediaSpaceEntity(
            title = "Sunset Frequencies",
            handle = "@sunset_audio",
            description = "Evening ambient broadcasts",
            category = "Audio & Ambient",
            subscriberCount = 1200,
            isOwner = true
        )
        dao.insertSpace(space)

        val spaces = dao.getUserSpaces().first()
        assertEquals(1, spaces.size)
        assertEquals("Sunset Frequencies", spaces[0].title)
        assertTrue(spaces[0].isOwner)
    }

    @Test
    fun testJournalEditionArchiveAndRetrieval() = runBlocking {
        val edition = JournalEditionEntity(
            newspaperTitle = "The Civic Post",
            motto = "Truth & Community First",
            volumeNumber = 1,
            issueNumber = 1,
            issueDate = "September 2026",
            templateStyle = "CLASSIC_BROADSHEET",
            bannerColorHex = 0xFFD4A373,
            leadHeadline = "Historic Community Market Expansion",
            leadSubheadline = "Civic planners vote in favor of expanded pedestrian square.",
            leadArticleBody = "The town gathered today to inaugurate the new central pavilion.",
            leadAuthor = "Jane Doe",
            secondaryHeadline = "Local Merchant Spotlight",
            secondaryArticleBody = "Local bakers and bookbinders share seasonal offerings.",
            editorialNotes = "Our gratitude to all the neighbors.",
            communityBulletin = "Farmer Market • Library Hour",
            isArchived = true
        )
        val id = dao.insertJournalEdition(edition)
        assertTrue(id > 0)

        val archived = dao.getArchivedJournalEditions().first()
        assertEquals(1, archived.size)
        assertEquals("The Civic Post", archived[0].newspaperTitle)
        assertEquals("Historic Community Market Expansion", archived[0].leadHeadline)
    }

    @Test
    fun testFlipbookPagesBuilding() {
        val edition = JournalEditionEntity(
            newspaperTitle = "The Daily Townsquare",
            motto = "Voice of the People",
            volumeNumber = 2,
            issueNumber = 5,
            issueDate = "Morning Dispatch",
            leadHeadline = "Autonomous Electric Tramways Open",
            leadSubheadline = "Quiet and zero-emission transit links districts.",
            leadArticleBody = "Transit officials cut the ribbon this morning on the 10-mile loop.\n\nThe system runs smoothly.",
            leadAuthor = "Alex Chen"
        )
        val pages = buildFlipbookPages(null, edition)
        assertEquals(4, pages.size)
        assertEquals("The Daily Townsquare", pages[0].mastheadTitle)
        assertEquals("Autonomous Electric Tramways Open", pages[0].headline)
        assertEquals(1, pages[0].pageNumber)
    }

    @Test
    fun testRadioStationFrequencyParsingAndSeeking() {
        val station1 = MediaItemEntity(
            id = 101L,
            type = MediaType.RADIO_STATION.name,
            title = "WCRB 88.5 Classical",
            subtitle = "Orchestral Masterworks",
            authorName = "Elena",
            channelId = "ch_sonic",
            channelName = "Sonic Waveform",
            bodyText = "Live broadcast stream.",
            stationFrequency = "88.5 MHz FM"
        )
        val station2 = MediaItemEntity(
            id = 102L,
            type = MediaType.RADIO_STATION.name,
            title = "K-Pulse 98.5 FM",
            subtitle = "Downtown Lo-Fi",
            authorName = "Sora",
            channelId = "ch_sonic",
            channelName = "Sonic Waveform",
            bodyText = "Live broadcast stream.",
            stationFrequency = "98.5 MHz FM"
        )
        val station3 = MediaItemEntity(
            id = 103L,
            type = MediaType.RADIO_STATION.name,
            title = "Apex Electronic 107.9",
            subtitle = "Sub-bass Pulse",
            authorName = "Vector Node",
            channelId = "ch_sonic",
            channelName = "Sonic Waveform",
            bodyText = "Live broadcast stream.",
            stationFrequency = "107.9 MHz FM"
        )

        val f1 = com.example.ui.components.parseStationFrequency(station1)
        val f2 = com.example.ui.components.parseStationFrequency(station2)
        val f3 = com.example.ui.components.parseStationFrequency(station3)

        assertEquals(88.5f, f1, 0.01f)
        assertEquals(98.5f, f2, 0.01f)
        assertEquals(107.9f, f3, 0.01f)

        val stations = listOf(station1, station2, station3)
        val context = ApplicationProvider.getApplicationContext<Context>()
        val player = com.example.audio.AudioPlayerManager.getInstance(context)

        player.playRadioDirectly(station1)
        assertEquals(station1.id, player.audioState.value.currentItem?.id)
        assertTrue(player.audioState.value.isPlaying)

        player.seekNextStation(stations)
        assertEquals(station2.id, player.audioState.value.currentItem?.id)

        player.seekNextStation(stations)
        assertEquals(station3.id, player.audioState.value.currentItem?.id)

        // Wrap around to first station
        player.seekNextStation(stations)
        assertEquals(station1.id, player.audioState.value.currentItem?.id)

        // Seek previous (wraps around to last station)
        player.seekPreviousStation(stations)
        assertEquals(station3.id, player.audioState.value.currentItem?.id)

        player.stop()
    }
}
