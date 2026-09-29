package com.example.data.repository

import com.example.data.local.MediaDao
import com.example.data.model.JournalEditionEntity
import com.example.data.model.LiveNewsblogEntity
import com.example.data.model.LocalBulletinEntity
import com.example.data.model.MediaChannelEntity
import com.example.data.model.MediaItemEntity
import com.example.data.model.MediaSpaceEntity
import com.example.data.model.MediaType
import com.example.data.model.NotepadDraftEntity
import com.example.data.model.RetailKioskEntity
import com.example.data.model.TvChannelEntity
import com.example.data.model.UpcomingEditionEntity
import com.example.data.model.VisualPostEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class MediaRepository(private val mediaDao: MediaDao) {

    val allMediaItems: Flow<List<MediaItemEntity>> = mediaDao.getAllMediaItems()
    val allChannels: Flow<List<MediaChannelEntity>> = mediaDao.getAllChannels()
    val followedChannels: Flow<List<MediaChannelEntity>> = mediaDao.getFollowedChannels()
    val allSpaces: Flow<List<MediaSpaceEntity>> = mediaDao.getAllSpaces()
    val userSpaces: Flow<List<MediaSpaceEntity>> = mediaDao.getUserSpaces()
    val userCreatedItems: Flow<List<MediaItemEntity>> = mediaDao.getUserCreatedItems()
    val bookmarkedItems: Flow<List<MediaItemEntity>> = mediaDao.getBookmarkedItems()
    val offlineSavedItems: Flow<List<MediaItemEntity>> = mediaDao.getOfflineSavedItems()
    val allJournalEditions: Flow<List<JournalEditionEntity>> = mediaDao.getAllJournalEditions()
    val archivedJournalEditions: Flow<List<JournalEditionEntity>> = mediaDao.getArchivedJournalEditions()
    val allUpcomingEditions: Flow<List<UpcomingEditionEntity>> = mediaDao.getAllUpcomingEditions()
    val allRetailKiosks: Flow<List<RetailKioskEntity>> = mediaDao.getAllRetailKiosks()
    val allBulletins: Flow<List<LocalBulletinEntity>> = mediaDao.getAllBulletins()
    val allTvChannels: Flow<List<TvChannelEntity>> = mediaDao.getAllTvChannels()
    val allNewsblogEntries: Flow<List<LiveNewsblogEntity>> = mediaDao.getAllNewsblogEntries()
    val allNotepadDrafts: Flow<List<NotepadDraftEntity>> = mediaDao.getAllNotepadDrafts()
    val allVisualPosts: Flow<List<VisualPostEntity>> = mediaDao.getAllVisualPosts()

    // 24/7 Live Newsblog Methods
    fun getNewsblogEntriesByCategory(category: String): Flow<List<LiveNewsblogEntity>> {
        return mediaDao.getNewsblogEntriesByCategory(category)
    }

    suspend fun toggleNewsblogLike(entry: LiveNewsblogEntity) {
        val newLiked = !entry.isLiked
        val newCount = if (newLiked) entry.likesCount + 1 else (entry.likesCount - 1).coerceAtLeast(0)
        mediaDao.updateNewsblogLike(entry.id, newLiked, newCount)
    }

    suspend fun toggleNewsblogBookmark(entry: LiveNewsblogEntity) {
        mediaDao.updateNewsblogBookmark(entry.id, !entry.isBookmarked)
    }

    suspend fun addNewsblogEntry(
        headline: String,
        body: String,
        authorName: String,
        authorRole: String,
        categoryTag: String,
        urgencyLevel: String,
        location: String,
        keyTakeaway: String,
        quote: String,
        quoteSpeaker: String
    ): Long {
        val entry = LiveNewsblogEntity(
            headline = headline,
            body = body,
            authorName = authorName,
            authorRole = authorRole,
            categoryTag = categoryTag,
            urgencyLevel = urgencyLevel,
            timestampFormatted = "Just now",
            timestampMillis = System.currentTimeMillis(),
            isPinned = false,
            keyTakeaway = keyTakeaway,
            quote = quote,
            quoteSpeaker = quoteSpeaker,
            location = location,
            likesCount = 1,
            isLiked = false,
            isBookmarked = false
        )
        return mediaDao.insertNewsblogEntry(entry)
    }

    suspend fun simulateIncomingLiveNewsblogDispatch(): LiveNewsblogEntity {
        val now = System.currentTimeMillis()
        val headlines = listOf(
            Triple(
                "⚡ LIVE DISPATCH: Solar Grid Output Hits Record 94% of Municipal Daytime Power",
                "Clean Energy Control Center confirms solar and micro-hydro generation supplied almost the entire civic district throughout peak afternoon operations.",
                "Dr. Neil Thorne"
            ),
            Triple(
                "🔴 BREAKING: Waterfront Promenade Night Market Extends Opening Hours to 2 AM",
                "Due to warm autumn temperatures and bustling crowds, municipal authorities approved 30 additional artisan food carts and acoustic busking stages along Pier 7.",
                "Aria Chen"
            ),
            Triple(
                "🚇 TRANSIT UPDATE: Line 1 Express Shuttle Adds Extra Carriages for Evening Concert Gala",
                "Light Rail Dispatchers deployed 4 extra double-decker articulated electric trainsets between Central Station and Symphony Hall with 4-minute headways.",
                "Marcus O'Reilly"
            )
        )
        val pick = headlines.random()
        val entry = LiveNewsblogEntity(
            headline = pick.first,
            body = pick.second,
            authorName = pick.third,
            authorRole = "Live Wire Desk",
            categoryTag = "BREAKING",
            urgencyLevel = "HIGH",
            timestampFormatted = "Just now",
            timestampMillis = now,
            isPinned = false,
            keyTakeaway = "Dispatched automatically from Townsquare Civic Wire monitoring stations.",
            quote = "Real-time updates continuously streamed to citizen readers.",
            quoteSpeaker = "Townsquare News Desk",
            location = "City Core Network",
            likesCount = (12..45).random(),
            isLiked = false,
            isBookmarked = false,
            verifiedSourcesCount = 3
        )
        val id = mediaDao.insertNewsblogEntry(entry)
        return entry.copy(id = id)
    }

    // Multimedia Notepad Drafts
    suspend fun saveOrUpdateNotepadDraft(draft: NotepadDraftEntity): Long {
        val updatedDraft = draft.copy(updatedTimestamp = System.currentTimeMillis())
        return if (draft.id == 0L) {
            mediaDao.insertNotepadDraft(updatedDraft)
        } else {
            mediaDao.updateNotepadDraft(updatedDraft)
            draft.id
        }
    }

    suspend fun toggleNotepadDraftStarred(draft: NotepadDraftEntity) {
        mediaDao.updateNotepadDraftStarred(draft.id, !draft.isStarred)
    }

    suspend fun deleteNotepadDraft(id: Long) {
        mediaDao.deleteNotepadDraft(id)
    }

    suspend fun convertDraftToJournalEdition(draft: NotepadDraftEntity): Long {
        val combinedBody = buildString {
            append(draft.bodyText)
            if (draft.audioTranscript.isNotEmpty()) {
                append("\n\n[Recorded Audio Voice Note]:\n")
                append(draft.audioTranscript)
            }
            if (draft.quoteAttribution.isNotEmpty()) {
                append("\n\nNotable Excerpt:\n")
                append(draft.quoteAttribution)
            }
            if (draft.checklistItems.isNotEmpty()) {
                append("\n\nInvestigation Checklist Completed:\n")
                draft.checklistItems.lines().filter { it.isNotBlank() }.forEach {
                    append("• ").append(it.trim()).append("\n")
                }
            }
        }

        val journalEdition = JournalEditionEntity(
            newspaperTitle = "The Townsquare Chronicle",
            motto = "Personal Edition • Published from Multimedia Notepad",
            volumeNumber = 1,
            issueNumber = (3..12).random(),
            issueDate = "Friday, Sept 11, 2026",
            templateStyle = "CLASSIC_BROADSHEET",
            bannerColorHex = draft.accentColorHex,
            leadHeadline = draft.title.ifEmpty { "Personal Journal Record" },
            leadSubheadline = draft.attachedPhotoCaption.ifEmpty { "Transcribed idea and multimedia notes preserved into print." },
            leadArticleBody = combinedBody,
            leadAuthor = "Reporter @personal",
            secondaryHeadline = "Draft Field Log & Observations",
            secondaryArticleBody = "Original tags: ${draft.tags}. Prepared with Townsquare Multimedia Journal Notepad.",
            editorialNotes = "Transferred seamlessly from draft idea holder into permanent local broadsheet archive.",
            circulationReads = 1
        )
        val newJournalId = mediaDao.insertJournalEdition(journalEdition)
        mediaDao.markDraftConverted(draft.id)
        return newJournalId
    }

    // Visual Posts Methods
    fun getVisualPostsByCategory(category: String): Flow<List<VisualPostEntity>> {
        return mediaDao.getVisualPostsByCategory(category)
    }

    suspend fun toggleVisualPostLike(post: VisualPostEntity) {
        val newLiked = !post.isLiked
        val newCount = if (newLiked) post.likesCount + 1 else (post.likesCount - 1).coerceAtLeast(0)
        mediaDao.updateVisualPostLike(post.id, newLiked, newCount)
    }

    suspend fun toggleVisualPostBookmark(post: VisualPostEntity) {
        mediaDao.updateVisualPostBookmark(post.id, !post.isBookmarked)
    }

    suspend fun addVisualPost(
        title: String,
        caption: String,
        photographerName: String,
        category: String,
        imageResUrl: String,
        locationTaken: String,
        cameraMeta: String,
        storyContext: String
    ): Long {
        val post = VisualPostEntity(
            title = title,
            caption = caption,
            photographerName = photographerName,
            category = category,
            imageResUrl = imageResUrl,
            locationTaken = locationTaken,
            cameraMeta = cameraMeta,
            storyContext = storyContext,
            likesCount = 1,
            isLiked = false,
            isBookmarked = false,
            timestampFormatted = "Just now",
            timestampMillis = System.currentTimeMillis()
        )
        return mediaDao.insertVisualPost(post)
    }

    suspend fun toggleTvFavorite(channel: TvChannelEntity) {
        mediaDao.updateTvChannelFavorite(channel.id, !channel.isFavorite)
    }

    suspend fun toggleTvReminder(channel: TvChannelEntity) {
        mediaDao.updateTvChannelReminder(channel.id, !channel.isReminderSet)
    }

    suspend fun toggleTvRecording(channel: TvChannelEntity) {
        mediaDao.updateTvChannelRecording(channel.id, !channel.isRecording)
    }

    fun getBulletinsByCategory(category: String): Flow<List<LocalBulletinEntity>> {
        return mediaDao.getBulletinsByCategory(category)
    }

    suspend fun reportBulletin(
        category: String,
        title: String,
        description: String,
        locationName: String,
        urgencyLevel: String,
        iconEmoji: String,
        reporterName: String = "Citizen Reporter",
        reporterHandle: String = "@town_reporter"
    ): Long {
        val bulletin = LocalBulletinEntity(
            category = category,
            title = title,
            description = description,
            locationName = locationName,
            reporterName = reporterName,
            reporterHandle = reporterHandle,
            timestamp = System.currentTimeMillis(),
            upvotesCount = 1,
            isUpvoted = false,
            urgencyLevel = urgencyLevel,
            iconEmoji = iconEmoji,
            status = "ACTIVE",
            isUserSubmitted = true
        )
        return mediaDao.insertBulletin(bulletin)
    }

    suspend fun toggleBulletinUpvote(bulletin: LocalBulletinEntity) {
        val newUpvoted = !bulletin.isUpvoted
        val newCount = if (newUpvoted) bulletin.upvotesCount + 1 else (bulletin.upvotesCount - 1).coerceAtLeast(0)
        mediaDao.updateBulletinUpvote(bulletin.id, newUpvoted, newCount)
    }

    suspend fun deleteBulletin(id: Long) {
        mediaDao.deleteBulletin(id)
    }

    fun getItemsByType(type: MediaType): Flow<List<MediaItemEntity>> {
        return mediaDao.getMediaItemsByType(type.name)
    }

    fun getItemsByChannel(channelId: String): Flow<List<MediaItemEntity>> {
        return mediaDao.getMediaItemsByChannel(channelId)
    }

    fun getItemsBySpace(spaceId: Long): Flow<List<MediaItemEntity>> {
        return mediaDao.getMediaItemsBySpace(spaceId)
    }

    suspend fun insertItem(item: MediaItemEntity): Long {
        return mediaDao.insertMediaItem(item)
    }

    suspend fun toggleLike(item: MediaItemEntity) {
        val newLiked = !item.isLiked
        val newCount = if (newLiked) item.likesCount + 1 else (item.likesCount - 1).coerceAtLeast(0)
        mediaDao.updateLike(item.id, newLiked, newCount)
    }

    suspend fun toggleBookmark(item: MediaItemEntity) {
        mediaDao.updateBookmark(item.id, !item.isBookmarked)
    }

    suspend fun toggleSavedOffline(item: MediaItemEntity) {
        mediaDao.updateSavedOffline(item.id, !item.isSavedOffline)
    }

    suspend fun incrementShare(itemId: Long) {
        mediaDao.incrementShareCount(itemId)
    }

    suspend fun toggleFollowChannel(channel: MediaChannelEntity) {
        mediaDao.updateChannelFollow(channel.id, !channel.isFollowed)
    }

    suspend fun createSpace(title: String, handle: String, description: String, category: String, accentColor: Long): Long {
        val space = MediaSpaceEntity(
            title = title,
            handle = if (handle.startsWith("@")) handle else "@$handle",
            description = description,
            category = category,
            subscriberCount = 1,
            accentColorHex = accentColor,
            isOwner = true,
            createdTimestamp = System.currentTimeMillis()
        )
        return mediaDao.insertSpace(space)
    }

    suspend fun createAndArchiveJournalEdition(edition: JournalEditionEntity): Long {
        val editionId = mediaDao.insertJournalEdition(edition)
        // Also add to media_items as NEWSPAPER_MAGAZINE so it appears on the Newsstand & Feed!
        val mediaItem = MediaItemEntity(
            type = MediaType.NEWSPAPER_MAGAZINE.name,
            title = edition.leadHeadline,
            subtitle = edition.leadSubheadline.ifEmpty { "Published in ${edition.newspaperTitle} • ${edition.issueDate}" },
            authorName = "${edition.newspaperTitle} (${edition.leadAuthor})",
            authorHandle = "@press_${edition.newspaperTitle.lowercase().replace(" ", "_")}",
            channelId = "channel_culture",
            channelName = "🎙️ Culture & Voices",
            bodyText = """${edition.leadArticleBody}

[EDITORIAL DESK NOTES]
${edition.editorialNotes.ifEmpty { "An independent edition published via Townsquare Journal Press." }}

[TOWN ALMANAC & BULLETINS]
${edition.communityBulletin.ifEmpty { "Daily local dispatch, community exchange, and weather notices." }}""",
            readTimeMinutes = 6,
            isUserCreated = true,
            issueEdition = "Vol. ${edition.volumeNumber}, Issue ${edition.issueNumber} • ${edition.issueDate}",
            tags = "#newspaper #broadsheet #townsquare #${edition.templateStyle.lowercase()}"
        )
        mediaDao.insertMediaItem(mediaItem)
        return editionId
    }

    suspend fun deleteJournalEdition(id: Long) {
        mediaDao.deleteJournalEdition(id)
    }

    suspend fun toggleEditionReminder(edition: UpcomingEditionEntity) {
        mediaDao.updateEditionReminder(edition.id, !edition.isReminderSet)
    }

    suspend fun toggleEditionSubscription(edition: UpcomingEditionEntity) {
        mediaDao.updateEditionSubscription(edition.id, !edition.isSubscribed)
    }

    suspend fun announceUpcomingEdition(edition: UpcomingEditionEntity): Long {
        return mediaDao.insertUpcomingEdition(edition)
    }

    suspend fun reservePhysicalCopy(kiosk: RetailKioskEntity, copiesToReserve: Int = 1) {
        val newAvailable = (kiosk.availableCopies - copiesToReserve).coerceAtLeast(0)
        val newReserved = kiosk.reservedCopiesCount + copiesToReserve
        mediaDao.updateKioskReservation(kiosk.id, newReserved, newAvailable)
    }

    suspend fun toggleKioskFavorite(kiosk: RetailKioskEntity) {
        mediaDao.updateKioskFavorite(kiosk.id, !kiosk.isFavorite)
    }

    // Pull-to-refresh: fetch fresh breaking dispatches
    suspend fun fetchFreshEditorialDispatches(): Int {
        val now = System.currentTimeMillis()
        val freshDispatches = listOf(
            MediaItemEntity(
                type = MediaType.NEWSLETTER.name,
                title = "⚡ BREAKING: Autonomous Clean Tram Fleet Exceeds Passenger Goal by 140%",
                subtitle = "Department of Transit confirms zero emissions across the entire central loop in week one.",
                authorName = "Elena Vance",
                authorHandle = "@elenavance",
                channelId = "channel_tech",
                channelName = "⚡ Horizon Tech",
                bodyText = """TOWNSQUARE METROPOLITAN TRANSIT — City transit commissioners announced historic patronage figures this morning as the autonomous electric tram system completed its inaugural 7-day operational cycle.

Over 120,000 passenger journeys were logged across the East-West cultural corridor and North Harbor lines, with zero interruptions, zero carbon emissions, and a 99.8% on-time departure average.

"The public appetite for whisper-quiet, pedestrian-first urban travel has proven overwhelming," noted Transit Director Marcus O'Reilly. "We are already evaluating extending the southern branch toward University Heights." """,
                readTimeMinutes = 3,
                durationSeconds = 160,
                mediaUrl = "https://audio.townsquare.local/dispatch_tram_surge.mp3",
                likesCount = 38,
                timestamp = now,
                tags = "#breaking #transit #sustainability #urbanism #tech"
            ),
            MediaItemEntity(
                type = MediaType.NEWSPAPER_MAGAZINE.name,
                title = "Townsquare Morning Gazette: Fresh Print Edition Off The Presses",
                subtitle = "Front page special on civic architecture, urban farming milestones, and neighborhood artisan profiles.",
                authorName = "Townsquare Editorial Board",
                authorHandle = "@townsquare_press",
                channelId = "channel_culture",
                channelName = "🎙️ Culture & Voices",
                bodyText = """The daily morning edition has just been printed at the Central Press Guild.

Highlights in today's fresh broadsheet:
• Urban rooftop hydroponic yields cross 20 metric tons for civic food co-ops.
• Historic Preservation Society allocates grants to restore 12 neighborhood corner kiosks.
• Symphony in the Park schedules autumn twilight performance series.

Available now digitally in the Townsquare Newsstand Flipbook and across all 6 local street kiosks.""",
                readTimeMinutes = 4,
                likesCount = 52,
                timestamp = now - 60000,
                issueEdition = "Morning Extra • September 11, 2026",
                tags = "#newspaper #broadsheet #morning #townsquare #kiosk"
            ),
            MediaItemEntity(
                type = MediaType.NEWSLETTER.name,
                title = "The Civic Spark: Weekly Neighborhood Initiatives & Micro-Grants",
                subtitle = "Seven community projects funded this week, from tool-lending libraries to rain-garden swales.",
                authorName = "Maya Lin",
                authorHandle = "@mayalin_civic",
                channelId = "channel_culture",
                channelName = "🎙️ Culture & Voices",
                bodyText = """Every Friday, The Civic Spark curates the grassroots micro-projects transforming our public spaces block by block.

This week's highlights:
1. Tool-Lending Library: Opens Saturday at 4th St Community Center with over 400 gardening, woodworking, and repair tools free to borrow.
2. Rain Gardens & Bioswales: 15 street corners planted with native wildflowers to naturally filter urban runoff.
3. Open Chess Tables: Marble boards installed under the sycamore trees on Civic Plaza.""",
                readTimeMinutes = 3,
                likesCount = 29,
                timestamp = now - 120000,
                tags = "#newsletter #civic #community #grassroots"
            )
        )
        mediaDao.insertMediaItems(freshDispatches)
        return freshDispatches.size
    }

    suspend fun checkAndSeedDatabase() {
        val count = mediaDao.getMediaItemsCount()
        if (count == 0) {
            seedDatabase()
        }
        if (mediaDao.getUpcomingEditionsCount() == 0) {
            seedUpcomingEditions()
        }
        if (mediaDao.getRetailKiosksCount() == 0) {
            seedRetailKiosks()
        }
        if (mediaDao.getBulletinsCount() == 0) {
            seedBulletins()
        }
        if (mediaDao.getTvChannelsCount() == 0) {
            seedTvChannels()
        }
        if (mediaDao.getNewsblogCount() == 0) {
            mediaDao.insertNewsblogEntries(MediaSeedHelper.getInitialNewsblogEntries())
        }
        if (mediaDao.getNotepadDraftsCount() == 0) {
            MediaSeedHelper.getInitialNotepadDrafts().forEach {
                mediaDao.insertNotepadDraft(it)
            }
        }
        if (mediaDao.getVisualPostsCount() == 0) {
            mediaDao.insertVisualPosts(MediaSeedHelper.getInitialVisualPosts())
        }
    }

    private suspend fun seedDatabase() {
        val channels = listOf(
            MediaChannelEntity(
                id = "channel_tech",
                name = "⚡ Horizon Tech",
                description = "Quantum computing, ambient AI, and next-generation engineering paradigms.",
                category = "Technology",
                bannerColorHex = 0xFF00D2FF,
                isFollowed = true,
                followersCount = 48200,
                morningBriefHighlight = "Breakthrough solid-state battery architecture achieves 800-mile range with 10-minute ultra-fast charging.",
                iconEmoji = "⚡"
            ),
            MediaChannelEntity(
                id = "channel_morning_espresso",
                name = "☕ Morning Espresso",
                description = "Essential daily briefing across world news, markets, and cultural milestones.",
                category = "Daily Digest",
                bannerColorHex = 0xFFFF9F1C,
                isFollowed = true,
                followersCount = 92400,
                morningBriefHighlight = "Global central banks hold rates steady as tech sector productivity reaches a 5-year high.",
                iconEmoji = "☕"
            ),
            MediaChannelEntity(
                id = "channel_global",
                name = "🌐 Global Dispatch",
                description = "Geopolitics, sustainability, and transformative international developments.",
                category = "World Affairs",
                bannerColorHex = 0xFF2EC4B6,
                isFollowed = true,
                followersCount = 31500,
                morningBriefHighlight = "High-speed trans-continental clean rail initiative launches cross-border service today.",
                iconEmoji = "🌐"
            ),
            MediaChannelEntity(
                id = "channel_culture",
                name = "🎙️ Culture & Voices",
                description = "Long-form journalism, avant-garde design, literature, and independent audio.",
                category = "Arts & Culture",
                bannerColorHex = 0xFFE71D36,
                isFollowed = true,
                followersCount = 26800,
                morningBriefHighlight = "Indie audio creators pioneer open spatial sound networks across 40 metropolitan hubs.",
                iconEmoji = "🎙️"
            ),
            MediaChannelEntity(
                id = "channel_market",
                name = "📈 Markets & Future",
                description = "Fintech, venture ecosystems, emerging economies, and algorithmic commerce.",
                category = "Finance",
                bannerColorHex = 0xFF70E000,
                isFollowed = false,
                followersCount = 19400,
                morningBriefHighlight = "Clean tech investment funds hit historic $1.2T allocation milestone worldwide.",
                iconEmoji = "📈"
            ),
            MediaChannelEntity(
                id = "channel_sonic",
                name = "📻 Sonic Waveform",
                description = "Broadcast radio transmissions, generative music streams, and live station frequencies.",
                category = "Radio & Audio",
                bannerColorHex = 0xFF9D4EDD,
                isFollowed = true,
                followersCount = 37100,
                morningBriefHighlight = "Live atmospheric radio stations report record listener engagement for morning focus blocks.",
                iconEmoji = "📻"
            ),
            MediaChannelEntity(
                id = "channel_the_ticket",
                name = "🎟️ The Ticket",
                description = "Your local gateway to cultural events, theater, live gigs, and followable neighborhood artists.",
                category = "Arts & Culture",
                bannerColorHex = 0xFFFF5252,
                isFollowed = true,
                followersCount = 15800,
                morningBriefHighlight = "Symphony under the stars tickets are now live in the superapp feed. Get yours today!",
                iconEmoji = "🎟️"
            ),
            MediaChannelEntity(
                id = "channel_arcade",
                name = "🎮 Arcade Gaming",
                description = "Local competitive high-scores, retro cabinet tournaments, indie games, and interactive speedrun chronicles.",
                category = "Gaming & Tech",
                bannerColorHex = 0xFF33FF33,
                isFollowed = true,
                followersCount = 8450,
                morningBriefHighlight = "The new Townsquare Space Game cabinet tournament is officially live in the Plus app!",
                iconEmoji = "🎮"
            )
        )
        mediaDao.insertChannels(channels)

        val userSpaces = listOf(
            MediaSpaceEntity(
                id = 1,
                title = "Alex's Tech Dispatch",
                handle = "@alexchen",
                description = "Dispatches on decentralized media systems, edge computing, and human curation.",
                category = "Tech & Future",
                subscriberCount = 1420,
                accentColorHex = 0xFF00D2FF,
                isOwner = true,
                createdTimestamp = System.currentTimeMillis() - 86400000L * 14
            ),
            MediaSpaceEntity(
                id = 2,
                title = "Sunset Frequencies",
                handle = "@sunset_audio",
                description = "Curated ambient soundscapes, micro-radio shows, and late night sonic thoughts.",
                category = "Audio & Radio",
                subscriberCount = 3890,
                accentColorHex = 0xFFFF9F1C,
                isOwner = true,
                createdTimestamp = System.currentTimeMillis() - 86400000L * 30
            ),
            MediaSpaceEntity(
                id = 3,
                title = "The Metropolitan Review",
                handle = "@metropolitancurator",
                description = "Architecture, civic life, and modern urban design essays.",
                category = "Magazine & Design",
                subscriberCount = 12400,
                accentColorHex = 0xFF2EC4B6,
                isOwner = false,
                createdTimestamp = System.currentTimeMillis() - 86400000L * 60
            )
        )
        mediaDao.insertSpaces(userSpaces)

        val now = System.currentTimeMillis()
        val items = listOf(
            // --- SONGS ---
            MediaItemEntity(
                type = MediaType.SONG.name,
                title = "Neon Horizon Beats",
                subtitle = "Late Night Synthesizer Jam • 85 BPM",
                authorName = "DJ Sora",
                authorHandle = "@djsora",
                channelId = "channel_sonic",
                channelName = "📻 Sonic Waveform",
                bodyText = "Unwind with retro-futuristic synthwave chords, analog tape delays, and a steady rhythmic groove designed for deep design sessions.",
                durationSeconds = 195,
                mediaUrl = "https://audio.townsquare.local/neon_horizon.mp3",
                timestamp = now - 600000L,
                likesCount = 84,
                isBookmarked = false,
                tags = "#synthwave #lofi #beats #focus"
            ),
            MediaItemEntity(
                type = MediaType.SONG.name,
                title = "Autumn Wind Sonata",
                subtitle = "Acoustic Grand Piano & Ambient Strings",
                authorName = "Maestro Julian Frost",
                authorHandle = "@julian_frost",
                channelId = "channel_sonic",
                channelName = "📻 Sonic Waveform",
                bodyText = "A warm, slow-tempo acoustic masterpiece capturing the early morning fog of Townsquare's botanical gardens.",
                durationSeconds = 240,
                mediaUrl = "https://audio.townsquare.local/autumn_wind.mp3",
                timestamp = now - 900000L,
                likesCount = 112,
                isBookmarked = true,
                tags = "#piano #classical #ambient #calm"
            ),

            // --- PLAYLISTS ---
            MediaItemEntity(
                type = MediaType.PLAYLIST.name,
                title = "Lo-Fi Workday Focus",
                subtitle = "Generative vinyl beats compiled by Sonic Guild",
                authorName = "Townsquare Curators",
                authorHandle = "@townsquare_curators",
                channelId = "channel_sonic",
                channelName = "📻 Sonic Waveform",
                bodyText = "A 45-minute continuous flow of warm, atmospheric grooves, jazz loops, and ambient street hums to keep your mind sharp.",
                durationSeconds = 2700,
                mediaUrl = "https://audio.townsquare.local/lofi_focus.mp3",
                timestamp = now - 1200000L,
                likesCount = 248,
                isBookmarked = true,
                tags = "#lofi #playlist #focus #study"
            ),
            MediaItemEntity(
                type = MediaType.PLAYLIST.name,
                title = "Symphony Under the Stars Special",
                subtitle = "Historical live recordings from the Glass Pavilion",
                authorName = "Philharmonic Orchestra",
                authorHandle = "@philharmonic",
                channelId = "channel_sonic",
                channelName = "📻 Sonic Waveform",
                bodyText = "Revisit past performances of Debussy, Glass, and Chopin recorded live during the annual twilight series.",
                durationSeconds = 3600,
                mediaUrl = "https://audio.townsquare.local/symphony_special.mp3",
                timestamp = now - 1800000L,
                likesCount = 315,
                isBookmarked = false,
                tags = "#symphony #playlist #classical #live"
            ),

            // --- THE TICKET EVENTS ---
            MediaItemEntity(
                type = MediaType.NEWSLETTER.name,
                title = "🎭 Shakespeare in the Courtyard: Hamlet",
                subtitle = "Augustine Theater Guild • Open Air Stage",
                authorName = "Aria Chen",
                authorHandle = "@ariachen",
                channelId = "channel_the_ticket",
                channelName = "🎟️ The Ticket",
                bodyText = "Join the Townsquare Augustine Theater Guild for a modern-dress, open-air production of Shakespeare's classic tragedy under the historical stone arches.",
                timestamp = now + 86400000L * 3,
                issueEdition = "Date: Friday, Oct 2, 2026 • 20:00",
                likesCount = 84,
                isBookmarked = false,
                tags = "#ticket #event #theater #hamlet #arts"
            ),
            MediaItemEntity(
                type = MediaType.NEWSLETTER.name,
                title = "🎻 Symphony in the Park: Twilight Concert",
                subtitle = "Townsquare Philharmonic • Botanical Glasshouse",
                authorName = "Maestro Julian Frost",
                authorHandle = "@julian_frost",
                channelId = "channel_the_ticket",
                channelName = "🎟️ The Ticket",
                bodyText = "The full Philharmonic string ensemble performs classical masterworks by Debussy and Chopin at sunset. Admission is free, blanket rentals available.",
                timestamp = now + 86400000L * 5,
                issueEdition = "Date: Sunday, Oct 4, 2026 • 18:30",
                likesCount = 142,
                isBookmarked = true,
                tags = "#ticket #event #orchestra #classical #music"
            ),
            MediaItemEntity(
                type = MediaType.NEWSLETTER.name,
                title = "🎨 Modern Art Vernissage & Gallery Stroll",
                subtitle = "East Arts Quarter • Collective Guild",
                authorName = "Elena Vance",
                authorHandle = "@elenavance",
                channelId = "channel_the_ticket",
                channelName = "🎟️ The Ticket",
                bodyText = "Discover new physical canvases, digital print layouts, and hand-molded clay structures from 15 independent local artists.",
                timestamp = now + 86400000L * 7,
                issueEdition = "Date: Tuesday, Oct 6, 2026 • 19:00",
                likesCount = 96,
                isBookmarked = false,
                tags = "#ticket #event #art #vernissage #stroll"
            ),
            MediaItemEntity(
                type = MediaType.NEWSPAPER_MAGAZINE.name,
                title = "👾 Pixel Odyssey: The Dawn of Local Indie Game Design",
                subtitle = "Spotlight on Retro-Modern Wave Fronts • Vol. 12",
                authorName = "Leo Vance",
                authorHandle = "@leovance_dev",
                channelId = "channel_arcade",
                channelName = "🎮 Arcade Gaming",
                bodyText = "How a small group of municipal developers constructed a full, responsive retro game library. Read about the physics calculations, dynamic pixel grids, and local game engines running directly on-device.",
                timestamp = now - 3600000L * 4,
                likesCount = 189,
                isBookmarked = false,
                tags = "#gaming #arcade #indiedev #pixels"
            ),
            MediaItemEntity(
                type = MediaType.SOCIAL_POST.name,
                title = "",
                subtitle = "Townsquare Retro Cup Is Active!",
                authorName = "Arcade Guild",
                authorHandle = "@arcade_guild",
                channelId = "channel_arcade",
                channelName = "🎮 Arcade Gaming",
                bodyText = "🚨 TOURNAMENT ALERT: The Townsquare Space Invaders and Block Breaker leaderboard has officially reset! Launch the 'Arcade' app in Townsquare Plus to test your skills, secure achievements, and see if you can top the local high-score board. May the highest FPS win!",
                timestamp = now - 3600000L,
                likesCount = 312,
                commentsCount = 45,
                isLiked = true,
                tags = "#arcade #superapp #leaderboard #tournament"
            ),

            // 1. Social Post
            MediaItemEntity(
                type = MediaType.SOCIAL_POST.name,
                title = "",
                subtitle = "",
                authorName = "Alex Chen",
                authorHandle = "@alexchen",
                channelId = "channel_tech",
                channelName = "⚡ Horizon Tech",
                spaceId = 1,
                spaceTitle = "Alex's Tech Dispatch",
                bodyText = "The true shift in media isn't AI replacing human journalists — it's readers wanting verified, opinionated human curation alongside instant audio briefs. The future belongs to integrated superapps that don't force you between reading a magazine and listening to a live station.",
                timestamp = now - 1800000L, // 30 min ago
                likesCount = 142,
                commentsCount = 28,
                sharesCount = 19,
                isLiked = true,
                isUserCreated = true,
                tags = "#future #media #journalism"
            ),

            // 2. Magazine Article
            MediaItemEntity(
                type = MediaType.NEWSPAPER_MAGAZINE.name,
                title = "The Architecture of Tomorrow: Living in Carbon-Negative Cities",
                subtitle = "How computational timber and solar biosurfaces are transforming vertical metropolis design.",
                authorName = "Elena Vance",
                authorHandle = "@elenavance",
                channelId = "channel_tech",
                channelName = "⚡ Horizon Tech",
                spaceTitle = "Horizon Editorial Press",
                bodyText = """Urban centers produce nearly 70% of global greenhouse emissions. But a quiet revolution is taking place across Oslo, Tokyo, and Singapore: architects are abandoning raw concrete for structural cross-laminated timber (CLT) treated with mineralizing silicates.

These new towers don't just reduce carbon during construction; they breathe. Integrated bio-reactive facades harbor phototropic algae panels that scrub airborne particulate matter while generating low-voltage electricity for building common areas.

"We no longer look at buildings as static physical containers," explains chief architect Dr. Marcus Holt. "They are metabolic organisms that filter air, cycle rainwater, and produce net positive power for their surrounding neighborhoods."

As zoning laws modernize across North America and Europe, computational architecture algorithms optimize structural geometries to withstand extreme weather while maximizing natural illumination. The 21st-century skyline is emerging as a living, resilient canopy.""",
                imageResName = "img_magazine_cover",
                timestamp = now - 3600000L * 2,
                readTimeMinutes = 6,
                likesCount = 520,
                commentsCount = 64,
                sharesCount = 112,
                isBookmarked = true,
                issueEdition = "Horizon Mag • Autumn Issue 44",
                tags = "#architecture #sustainability #future"
            ),

            // 3. Newsletter Edition
            MediaItemEntity(
                type = MediaType.NEWSLETTER.name,
                title = "The Cognitive OS: Why Human Curation Outperforms Pure Algorithms",
                subtitle = "Dispatch #72 • The Death of the Infinite Scroll and the Rebirth of Intentional Media.",
                authorName = "Dr. Maya Lin",
                authorHandle = "@mayalin",
                channelId = "channel_morning_espresso",
                channelName = "☕ Morning Espresso",
                spaceTitle = "SubStack Vanguard",
                bodyText = """Welcome to Friday's deep dive. 

Over the last five years, recommendation feeds optimized for pure engagement time pushed culture toward sensationalist micro-content. Yet survey after survey shows that readers feel deeply fatigued. 

What readers crave is intentionality:
1. Finite editions: The satisfaction of reaching the end of an issue.
2. Thematic context: Understanding how today's headline fits into the 5-year horizon.
3. Multi-modal synthesis: Switching effortlessly from reading an in-depth policy brief to hearing the author discuss it in a 3-minute voice memo.

When you start your day with a curated morning brief, your cortisol levels drop compared to frantic algorithmic social feeds. The superapp model proves that when you respect the reader's attention span, loyalty compounds.""",
                imageResName = "img_morning_brief",
                timestamp = now - 3600000L * 4,
                readTimeMinutes = 4,
                likesCount = 388,
                commentsCount = 42,
                sharesCount = 78,
                issueEdition = "Vol. 72 • The Vanguard Letter",
                tags = "#curation #psychology #tech"
            ),

            // 4. Live Radio Station
            MediaItemEntity(
                type = MediaType.RADIO_STATION.name,
                title = "K-Pulse 98.5 FM Live Broadcast",
                subtitle = "Downtown Lo-Fi, Deep Ambient & Late Morning Beats",
                authorName = "DJ Sora & Pulse Studio",
                authorHandle = "@kpulse985",
                channelId = "channel_sonic",
                channelName = "📻 Sonic Waveform",
                spaceId = 2,
                spaceTitle = "Sunset Frequencies",
                bodyText = "Broadcasting live from the studio soundstage with uninterrupted analog vinyl warmth, mellow acoustic grooves, and ambient electronica engineered for deep focus.",
                mediaUrl = "https://stream.live.vc/kpulse",
                imageResName = "img_radio_live",
                timestamp = now - 600000L,
                durationSeconds = 0,
                likesCount = 1890,
                commentsCount = 115,
                sharesCount = 340,
                stationFrequency = "98.5 MHz FM",
                issueEdition = "Live 24/7 Studio Stream",
                tags = "#lofi #ambient #focus #radio"
            ),

            // 5. Podcast Episode
            MediaItemEntity(
                type = MediaType.PODCAST_EPISODE.name,
                title = "Episode 142: Can Machines Understand the Subtlety of Metaphor?",
                subtitle = "A fascinating conversation with linguists and cognitive scientists on computational poetry.",
                authorName = "Sarah Jenkins",
                authorHandle = "@sarahjenkins",
                channelId = "channel_culture",
                channelName = "🎙️ Culture & Voices",
                spaceTitle = "The Deep Dive Daily",
                bodyText = """In this episode, we unpack whether large language models genuinely grasp humor, sarcasm, and poignant poetic metaphors—or if they are simply executing probabilistic token associations. 

Featuring guest Dr. Julian Thorne, professor of Cognitive Linguistics at Oxford University, we explore human emotion, the history of oral storytelling, and why listening to spoken voice creates a unique parasocial intimacy that text alone cannot replicate.""",
                mediaUrl = "https://podcast.omnimedia.internal/ep142.mp3",
                imageResName = "img_podcast_cover",
                timestamp = now - 3600000L * 7,
                readTimeMinutes = 24,
                durationSeconds = 1440,
                likesCount = 894,
                commentsCount = 93,
                sharesCount = 205,
                issueEdition = "The Deep Dive Daily • Ep. 142",
                tags = "#podcast #linguistics #ai #philosophy"
            ),

            // 6. Social Post
            MediaItemEntity(
                type = MediaType.SOCIAL_POST.name,
                title = "",
                subtitle = "",
                authorName = "Claire Moreau",
                authorHandle = "@clairem",
                channelId = "channel_global",
                channelName = "🌐 Global Dispatch",
                bodyText = "Riding the new trans-Alpine rail connection between Lyon and Milan. Quiet electric carriages, floor-to-ceiling windows, and local espresso served at your seat. Truly the golden era of European train travel.",
                timestamp = now - 3600000L * 5,
                likesCount = 312,
                commentsCount = 45,
                sharesCount = 38,
                tags = "#travel #europe #railways"
            ),

            // 7. Live Radio Station 2
            MediaItemEntity(
                type = MediaType.RADIO_STATION.name,
                title = "Global News Wire 91.1 Live",
                subtitle = "24/7 Diplomatic & Economic Live Dispatch",
                authorName = "World News Network",
                authorHandle = "@worldnewswire",
                channelId = "channel_global",
                channelName = "🌐 Global Dispatch",
                bodyText = "Continuous live updates, breaking global market headlines, foreign bureau correspondents, and hourly international weather bulletins.",
                mediaUrl = "https://stream.worldwire.internal/live",
                imageResName = "img_radio_live",
                timestamp = now - 900000L,
                stationFrequency = "91.1 MHz FM",
                issueEdition = "International News Service",
                likesCount = 1420,
                commentsCount = 88,
                sharesCount = 210,
                tags = "#news #international #radio"
            ),

            // Live Radio Station 3 (88.5 FM)
            MediaItemEntity(
                type = MediaType.RADIO_STATION.name,
                title = "WCRB 88.5 Classical & Symphonic",
                subtitle = "Concert Hall Acoustics, Orchestral Masterworks & Choral Streams",
                authorName = "Maestro Elena Rostova",
                authorHandle = "@wcrb_classical",
                channelId = "channel_sonic",
                channelName = "📻 Sonic Waveform",
                spaceId = 2,
                spaceTitle = "Sunset Frequencies",
                bodyText = "Broadcasting live from Symphony Hall featuring acoustic purity, 24-bit studio fidelity, chamber music, and historical concert recordings.",
                mediaUrl = "https://stream.symphony.internal/885",
                imageResName = "img_radio_live",
                timestamp = now - 1200000L,
                stationFrequency = "88.5 MHz FM",
                issueEdition = "Symphonic Broadcast Series",
                likesCount = 980,
                commentsCount = 45,
                sharesCount = 160,
                tags = "#classical #orchestra #acoustic #radio"
            ),

            // Live Radio Station 4 (94.3 FM)
            MediaItemEntity(
                type = MediaType.RADIO_STATION.name,
                title = "Civic Voice 94.3 Community FM",
                subtitle = "Town Hall Open Mic, City Council Audio & Neighborhood Dispatches",
                authorName = "Marcus Vance",
                authorHandle = "@civicvoice943",
                channelId = "channel_culture",
                channelName = "🎙️ Culture & Voices",
                bodyText = "Community-powered non-commercial airwaves bringing live city council floor hearings, local ballot analyses, and neighborhood grassroots dispatches.",
                mediaUrl = "https://stream.civic.internal/943",
                imageResName = "img_radio_live",
                timestamp = now - 1500000L,
                stationFrequency = "94.3 MHz FM",
                issueEdition = "Municipal Public Broadcast",
                likesCount = 1120,
                commentsCount = 74,
                sharesCount = 195,
                tags = "#civic #community #democracy #radio"
            ),

            // Live Radio Station 5 (101.3 FM)
            MediaItemEntity(
                type = MediaType.RADIO_STATION.name,
                title = "Metro Jazz 101.3 Blue Note Sessions",
                subtitle = "Late Night Hard Bop, Cool Jazz & Improvisational Fusion",
                authorName = "Ronnie Hayes",
                authorHandle = "@metrojazz1013",
                channelId = "channel_sonic",
                channelName = "📻 Sonic Waveform",
                spaceId = 2,
                spaceTitle = "Sunset Frequencies",
                bodyText = "Warm analog recordings from legendary jazz cellars, vintage vinyl master pressings, upright bass solos, and rare tenor sax recordings.",
                mediaUrl = "https://stream.jazz.internal/1013",
                imageResName = "img_radio_live",
                timestamp = now - 800000L,
                stationFrequency = "101.3 MHz FM",
                issueEdition = "Midnight Blue Note Hour",
                likesCount = 1650,
                commentsCount = 98,
                sharesCount = 310,
                tags = "#jazz #vinyl #acoustic #radio"
            ),

            // Live Radio Station 6 (104.7 FM)
            MediaItemEntity(
                type = MediaType.RADIO_STATION.name,
                title = "Echo Indie Wave 104.7 College Radio",
                subtitle = "Underground Lo-Fi, Bedroom Pop & Post-Rock Broadcast",
                authorName = "Zoe & Liam",
                authorHandle = "@echo1047",
                channelId = "channel_culture",
                channelName = "🎙️ Culture & Voices",
                bodyText = "Independent student and local artist showcases, cassette tape premieres, fuzz guitar jams, and uncut underground demos.",
                mediaUrl = "https://stream.echo.internal/1047",
                imageResName = "img_radio_live",
                timestamp = now - 650000L,
                stationFrequency = "104.7 MHz FM",
                issueEdition = "College Radio Independent",
                likesCount = 1340,
                commentsCount = 82,
                sharesCount = 280,
                tags = "#indie #alternative #collegeradio #radio"
            ),

            // Live Radio Station 7 (107.9 FM)
            MediaItemEntity(
                type = MediaType.RADIO_STATION.name,
                title = "Apex Electronic 107.9 Sub-Bass Pulse",
                subtitle = "Deep House, Modular Synthesizer Drones & Ambient Techno",
                authorName = "Vector Node",
                authorHandle = "@apex1079",
                channelId = "channel_sonic",
                channelName = "📻 Sonic Waveform",
                spaceId = 2,
                spaceTitle = "Sunset Frequencies",
                bodyText = "Continuous progressive soundscapes engineered with analog modular synths, deep polyrhythms, and hypnotic ambient textures.",
                mediaUrl = "https://stream.apex.internal/1079",
                imageResName = "img_radio_live",
                timestamp = now - 500000L,
                stationFrequency = "107.9 MHz FM",
                issueEdition = "Sub-Bass Club & Studio",
                likesCount = 2210,
                commentsCount = 142,
                sharesCount = 490,
                tags = "#electronic #ambient #synth #techno #radio"
            ),

            // 8. Podcast Episode 2
            MediaItemEntity(
                type = MediaType.PODCAST_EPISODE.name,
                title = "The Morning 3-Minute Spark: Market Momentum & Tech Frontiers",
                subtitle = "Your fast, high-density audio summary to start your morning routine.",
                authorName = "Omni Editorial Desk",
                authorHandle = "@omnidesk",
                channelId = "channel_morning_espresso",
                channelName = "☕ Morning Espresso",
                bodyText = "A rapid 3-minute executive briefing designed for your morning commute or breakfast coffee: top index movements, overnight tech announcements, and what to watch before opening bell.",
                imageResName = "img_morning_brief",
                timestamp = now - 3600000L * 1,
                readTimeMinutes = 3,
                durationSeconds = 180,
                likesCount = 2140,
                commentsCount = 120,
                sharesCount = 415,
                issueEdition = "Daily Brief Series • Sept 9",
                tags = "#morningbrief #news #finance"
            ),

            // 9. Newspaper Article
            MediaItemEntity(
                type = MediaType.NEWSPAPER_MAGAZINE.name,
                title = "The Quantum Leap: Commercial Room-Temperature Superconductors Enter Testing",
                subtitle = "Experimental laboratory validation hints at lossless grid transmission and compact MRI machines.",
                authorName = "Jonathan Ward",
                authorHandle = "@jward_physics",
                channelId = "channel_tech",
                channelName = "⚡ Horizon Tech",
                bodyText = """In an unprecedented multi-university verification study, researchers have replicated zero-resistance conductivity at near-ambient pressure using a synthesized nitrogen-doped lutetium hydride matrix.

If commercially manufacturable, the implications are staggering:
- Lossless power transmission grids saving gigawatt-hours annually
- High-speed magnetic levitation trains operating with minimal cooling
- Portable high-resolution medical diagnostics accessible in rural clinics

While scaled manufacturing presents significant material crystallization hurdles, industrial consortia have already committed $4B to fabrication testbeds.""",
                imageResName = "img_magazine_cover",
                timestamp = now - 3600000L * 8,
                readTimeMinutes = 5,
                likesCount = 740,
                commentsCount = 89,
                sharesCount = 180,
                issueEdition = "Daily Science Wire • Issue 892",
                tags = "#physics #quantum #engineering"
            ),

            // 10. Flagship Morning Broadsheet
            MediaItemEntity(
                type = MediaType.NEWSPAPER_MAGAZINE.name,
                title = "The Townsquare Chronicle: Civic Microgrids and Urban Greenways Transform Central District",
                subtitle = "Historic town square completes its five-year ecological transition, delivering zero-emission pedestrian avenues and municipal energy independence.",
                authorName = "Townsquare Press Guild",
                authorHandle = "@townsquare_chronicle",
                channelId = "channel_morning_espresso",
                channelName = "☕ Morning Espresso",
                spaceTitle = "The Civic Press Guild",
                bodyText = """TOWNSQUARE — Under clear autumn skies this morning, the Townsquare Municipal Council and civic engineering guild officially switched on the district's distributed solar microgrid and bio-filtration canopy.

The project, which broke ground three years ago, connects over 140 historic storefronts, independent libraries, cooperative cafes, and residential brownstones to an intelligent local battery reserve network.

"This is what real civic sovereignty looks like," announced Mayor Lillian Cruz during the morning dedication ceremony at the central fountain plaza. "We generate our own clean electricity, store it locally, and guarantee uninterrupted public lighting, clean transport, and community heat even during harsh winter storms."

Beyond power generation, the newly unveiled greenway ribbons across 4.2 miles of former parking corridors, replacing asphalt with permeable flagstone pavers, rain gardens, and native shaded oak trees. Early traffic studies report a 60% increase in morning pedestrian footfall, directly benefiting independent merchants, bakeries, and local craft studios.

As morning broadsheets hit the newsstands, town residents gathered at outdoor cafes with fresh espresso, reading through the printed schedules of upcoming harvest festivals and open community forums.""",
                imageResName = "img_magazine_cover",
                timestamp = now - 3600000L * 1,
                readTimeMinutes = 7,
                likesCount = 1840,
                commentsCount = 142,
                sharesCount = 380,
                isBookmarked = true,
                issueEdition = "Vol. 142 • Morning City Edition",
                tags = "#townsquare #broadsheet #civic #cleanenergy"
            ),

            // 11. Glossy Architecture & Living Periodical
            MediaItemEntity(
                type = MediaType.NEWSPAPER_MAGAZINE.name,
                title = "Architectural Review: Biophilic Sanctuaries & Living Timber Towers",
                subtitle = "Why the modern city is trading sterile glass boxes for sculpted cross-laminated timber and cascading rooftop gardens.",
                authorName = "Julian Thorne & Design Bureau",
                authorHandle = "@arch_review",
                channelId = "channel_culture",
                channelName = "🎙️ Culture & Voices",
                bodyText = """Across global design studios, a fundamental philosophical shift has taken hold. For nearly a century, urban architecture prioritized industrial sterility—smooth curtain-wall glass, cold chrome, and reflective titanium that isolated dwellers from their natural surroundings.

Today, visionary designers are embracing biophilic structural timber. Buildings are engineered not as impenetrable fortresses against nature, but as porous vertical ecosystems. 

In the heart of the capital, the newly completed 18-story Cedar Spire integrates over 4,000 square meters of vertical foliage, providing microclimate cooling, air purification, and soothing natural acoustics. Internal studies show building occupants report a 34% drop in mental fatigue and significant boosts in creative problem-solving.

"When you touch a warm timber column or gaze through daylight filtered by living wisteria vines, your biology responds immediately," reflects lead architect Maya Lin. "We are bringing the wisdom of ancient forests into the heart of the modern town." """,
                imageResName = "img_magazine_cover",
                timestamp = now - 3600000L * 3,
                readTimeMinutes = 8,
                likesCount = 920,
                commentsCount = 76,
                sharesCount = 210,
                issueEdition = "Autumn Monograph • Vol. 38",
                tags = "#magazine #architecture #biophilic #design"
            ),

            // 12. Weekend Investigative Gazette
            MediaItemEntity(
                type = MediaType.NEWSPAPER_MAGAZINE.name,
                title = "The Metropolitan Dispatch: High-Speed Electric Rail Unites 14 Regional Town Squares",
                subtitle = "First commercial runs of the Regional Maglev link rural agricultural valleys with metropolitan cultural districts in under 28 minutes.",
                authorName = "Marcus Vance",
                authorHandle = "@marcusvance",
                channelId = "channel_global",
                channelName = "🌐 Global Dispatch",
                bodyText = """METROPOLIS — The morning 7:15 express departure from Valley Crossing arrived at Townsquare Central Station precisely twenty-seven minutes later, completing the maiden voyage of the region's clean magnetic transit corridor.

Passengers disembarking onto the open-air wooden concourse praised the whisper-quiet ride, panoramic mountain viewports, and on-board regional breakfast dining. 

Commuters who previously endured two-hour highway gridlocks now comfortably read their morning newspapers, draft articles, and sip coffee as countryside hills glide smoothly past at 160 miles per hour.

Urban economists predict the corridor will unlock unprecedented economic revitalization for historic market towns, allowing artisans, farmers, and tech specialists to live in tranquil rural hamlets while seamlessly participating in cosmopolitan commerce.""",
                imageResName = "img_magazine_cover",
                timestamp = now - 3600000L * 6,
                readTimeMinutes = 6,
                likesCount = 1130,
                commentsCount = 95,
                sharesCount = 290,
                issueEdition = "Weekend Gazette • Issue 512",
                tags = "#newspaper #rail #metro #dispatch"
            )
        )
        mediaDao.insertMediaItems(items)

        // Seed initial Journal Editions if empty
        if (mediaDao.getJournalEditionsCount() == 0) {
            val initialJournalEditions = listOf(
                JournalEditionEntity(
                    newspaperTitle = "The Townsquare Chronicle",
                    motto = "The Independent Voice of the Town • Est. 2026",
                    volumeNumber = 1,
                    issueNumber = 1,
                    issueDate = "Thursday, September 10, 2026",
                    templateStyle = "CLASSIC_BROADSHEET",
                    bannerColorHex = 0xFFD4A373,
                    leadHeadline = "Townsquare Opens New Digital Press Guild and Community Newsstand",
                    leadSubheadline = "Civic square inaugurates an open printing hall where every citizen can compose, archive, and print personal broadsheets.",
                    leadArticleBody = """TOWNSQUARE — In a celebrated step for civic storytelling, the Townsquare Press Guild was officially chartered today. Designed to empower local journalists, neighborhood storytellers, and citizen chroniclers, the new facility bridges centuries-old broadsheet traditions with cutting-edge digital flipbook publishing.

From morning market roundups to investigative neighborhood profiles, readers can now browse freshly minted daily editions right off the virtual newsstand shelf, or step into the press room to draft their own front-page issues.

"Local journalism is the lifeblood of an enlightened community," stated Guild master Arthur Finch. "When citizens document their own history, culture thrives." """,
                    leadAuthor = "Arthur Finch, Editor",
                    secondaryHeadline = "Weekly Farmers Market Sets All-Time Organic Harvest Record",
                    secondaryArticleBody = "More than 40 regional growers showcased heirloom produce, honeycombs, and sourdough batches under the solar pavilion, drawing visitors from across three counties.",
                    editorialNotes = "A warm welcome to our inaugural volume! In these pages, we commit to honesty, thoughtful inquiry, and celebrating the artisans who make Townsquare remarkable.",
                    communityBulletin = "Town Library Book Sale: Saturday 10 AM • Harvest Dance at the Gazebo: Friday 7 PM • Weather: High 74°F, gentle breeze.",
                    circulationReads = 248,
                    isArchived = true
                ),
                JournalEditionEntity(
                    newspaperTitle = "The Evening Gazette",
                    motto = "Twilight Dispatches, Arts & Neighborhood Voices",
                    volumeNumber = 1,
                    issueNumber = 2,
                    issueDate = "Friday, September 11, 2026",
                    templateStyle = "MODERN_GAZETTE",
                    bannerColorHex = 0xFF00D2FF,
                    leadHeadline = "Night Stargazing Pavilion Unveils Deep-Space Optics for Public Viewing",
                    leadSubheadline = "Community observatory offers breathtaking views of Saturn's rings and the Orion Nebula every Friday twilight.",
                    leadArticleBody = """HILLSIDE PARK — Under pristine dark skies free from urban light haze, over three hundred town residents climbed Observatory Ridge for the first public night of the new Townsquare Optical Pavilion.

Equipped with dual 24-inch apochromatic reflectors donated by local astronomy patrons, visitors marvelled at razor-sharp details of lunar craters and swirling Jovian storm bands.

"Seeing the Cassini division with your own eyes changes how you feel about our place in the cosmos," whispered young attendee Maya Santos.""",
                    leadAuthor = "Clara Bennett, Science Bureau",
                    secondaryHeadline = "Bicycle Cooperative Repairs 200 Vintage Cruisers for Free Town Commute",
                    secondaryArticleBody = "Volunteer mechanics spent the week restoring vintage steel bicycles, now available across five town docking stations for free public use.",
                    editorialNotes = "As night descends on Townsquare, we reflect on shared wonders—from distant stars to neighboring smiles across the market square.",
                    communityBulletin = "Observatory open Fridays 8 PM - Midnight • Solar Eclipse Workshop next Tuesday • Wind: 4 mph from the West.",
                    circulationReads = 184,
                    isArchived = true
                )
            )
            mediaDao.insertJournalEditions(initialJournalEditions)
        }
    }

    private suspend fun seedUpcomingEditions() {
        val editions = listOf(
            UpcomingEditionEntity(
                id = "up_townsquare_tomorrow",
                publicationTitle = "The Daily Townsquare",
                channelId = "channel_culture",
                editionType = "BROADSHEET",
                volumeIssue = "Vol. 14, Issue 249",
                releaseDate = "2026-09-11",
                releaseDayLabel = "Tomorrow • Friday",
                releaseTime = "06:00 AM",
                coverHeadline = "Tomorrow's Lead: 100% Clean Energy Grid Reaches Full Civic Deployment",
                leadTeaser = "Comprehensive investigation into the magnetic levitation and autonomous zero-emission tram system opening across all town quarters.",
                editorNotes = "Includes special 4-page pullout map of new transit links and civic plaza connections.",
                bannerColorHex = 0xFF00D2FF,
                isSubscribed = true,
                isReminderSet = true,
                specialSection = "Morning Broadsheet Drop"
            ),
            UpcomingEditionEntity(
                id = "up_espresso_sat",
                publicationTitle = "Morning Espresso Weekend",
                channelId = "channel_morning_espresso",
                editionType = "BROADSHEET",
                volumeIssue = "Weekend Edition #312",
                releaseDate = "2026-09-12",
                releaseDayLabel = "Saturday • Sept 12",
                releaseTime = "06:30 AM",
                coverHeadline = "Global Markets: Decentralized Clean Energy Bonds Oversubscribed 3x",
                leadTeaser = "Weekend market wrap, cultural recommendations, and an origin spotlight on regenerative highland coffee farms.",
                editorNotes = "Weekend cultural supplement & weekly economic charts.",
                bannerColorHex = 0xFFFF9F1C,
                isSubscribed = true,
                isReminderSet = false,
                specialSection = "Weekend Financial Dispatch"
            ),
            UpcomingEditionEntity(
                id = "up_chronicle_sunday",
                publicationTitle = "Metropolitan Sunday Chronicle",
                channelId = "channel_culture",
                editionType = "WEEKEND_SPECIAL",
                volumeIssue = "Vol. 48, Sunday Edition",
                releaseDate = "2026-09-13",
                releaseDayLabel = "This Sunday • Sept 13",
                releaseTime = "07:00 AM",
                coverHeadline = "The Artisan Guilds Reclaiming Historic Riverfront Warehouses",
                leadTeaser = "How woodcrafters, letterpress printers, and ceramicists transformed industrial ruins into a vibrant creative district.",
                editorNotes = "Featuring photo-essays by Clara Bennett and weekly heritage crossword puzzle.",
                bannerColorHex = 0xFFE71D36,
                isSubscribed = true,
                isReminderSet = false,
                specialSection = "Sunday Broadsheet Edition"
            ),
            UpcomingEditionEntity(
                id = "up_horizon_fall",
                publicationTitle = "Horizon Tech Quarterly",
                channelId = "channel_tech",
                editionType = "MAGAZINE",
                volumeIssue = "Fall 2026 • Issue 38",
                releaseDate = "2026-09-15",
                releaseDayLabel = "Next Tuesday • Sept 15",
                releaseTime = "08:30 AM",
                coverHeadline = "Beyond Silicon: Photonic Computing & Ambient Neural Hardware",
                leadTeaser = "Laboratory results confirm 1,000x efficiency gains in room-temperature optical signal processors.",
                editorNotes = "Deep technical report with architectural schematics and engineer roundtables.",
                bannerColorHex = 0xFF9D4EDD,
                isSubscribed = true,
                isReminderSet = false,
                specialSection = "Quarterly Journal Review"
            ),
            UpcomingEditionEntity(
                id = "up_atelier_monthly",
                publicationTitle = "Atelier & Design Journal",
                channelId = "channel_culture",
                editionType = "MAGAZINE",
                volumeIssue = "Issue 52 • Architecture Focus",
                releaseDate = "2026-09-18",
                releaseDayLabel = "Friday • Sept 18",
                releaseTime = "09:00 AM",
                coverHeadline = "Biophilic Architecture: Buildings That Breathe With Forests",
                leadTeaser = "Exploration of sustainable mass-timber structures and living mycelium insulation systems across modern dwellings.",
                editorNotes = "Full color spreads, architectural drafting plans, and interviews with visionary urbanists.",
                bannerColorHex = 0xFF2EC4B6,
                isSubscribed = true,
                isReminderSet = false,
                specialSection = "Monthly Design & Living"
            ),
            UpcomingEditionEntity(
                id = "up_global_report",
                publicationTitle = "Global Dispatch Monthly Report",
                channelId = "channel_global",
                editionType = "INVESTIGATION",
                volumeIssue = "Vol. 9, September",
                releaseDate = "2026-09-22",
                releaseDayLabel = "Tuesday • Sept 22",
                releaseTime = "07:00 AM",
                coverHeadline = "The Global Watershed Restoration Treaty: One Year Later",
                leadTeaser = "Field reports from river basins across three continents tracking unprecedented ecological recovery.",
                editorNotes = "Special collaborative investigative dispatch with interactive data appendices.",
                bannerColorHex = 0xFF00D2FF,
                isSubscribed = true,
                isReminderSet = false,
                specialSection = "Global Investigation"
            )
        )
        mediaDao.insertUpcomingEditions(editions)
    }

    private suspend fun seedRetailKiosks() {
        val kiosks = listOf(
            RetailKioskEntity(
                id = "kiosk_historic_townsquare",
                name = "Historic Townsquare Kiosk",
                type = "Heritage Kiosk (Est. 1912)",
                address = "Civic Plaza, North Arcade #1",
                district = "Civic Center",
                distanceMiles = 0.2f,
                walkingMinutes = 3,
                openingHours = "Open • 6:00 AM – 9:00 PM",
                isOpenNow = true,
                mapX = 0.48f,
                mapY = 0.42f,
                phoneNumber = "(555) 234-5678",
                carriedPublicationTitles = "The Daily Townsquare, Metropolitan Sunday Chronicle, Atelier & Design Journal, Morning Espresso Weekend",
                stockStatus = "Morning Drop Fresh (18 copies in stock)",
                availableCopies = 18,
                isFavorite = true
            ),
            RetailKioskEntity(
                id = "kiosk_grand_central",
                name = "Grand Central News & Periodicals",
                type = "Transit Newsstand",
                address = "420 Transit Blvd, Metro Concourse A",
                district = "Midtown Transit",
                distanceMiles = 0.5f,
                walkingMinutes = 7,
                openingHours = "Open 24/7",
                isOpenNow = true,
                mapX = 0.72f,
                mapY = 0.28f,
                phoneNumber = "(555) 345-6789",
                carriedPublicationTitles = "The Daily Townsquare, Horizon Tech Quarterly, Global Dispatch Monthly Report, Morning Espresso Weekend",
                stockStatus = "Fully Stocked (34 copies in stock)",
                availableCopies = 34
            ),
            RetailKioskEntity(
                id = "kiosk_waterfront",
                name = "Waterfront Promenade Press Stand",
                type = "Open-Air Press Pavilion",
                address = "Pier 7 Maritime Boardwalk",
                district = "Harbor Promenade",
                distanceMiles = 0.8f,
                walkingMinutes = 12,
                openingHours = "Open • 7:00 AM – 8:00 PM",
                isOpenNow = true,
                mapX = 0.82f,
                mapY = 0.68f,
                phoneNumber = "(555) 456-7890",
                carriedPublicationTitles = "Metropolitan Sunday Chronicle, The Daily Townsquare, Atelier & Design Journal",
                stockStatus = "Limited Stock (4 copies remaining)",
                availableCopies = 4
            ),
            RetailKioskEntity(
                id = "kiosk_atelier_books",
                name = "Atelier Heritage Bookshop & Periodicals",
                type = "Art & Press Bookshop",
                address = "14 Bauhaus Way, Arts Quarter",
                district = "Arts Quarter",
                distanceMiles = 1.1f,
                walkingMinutes = 16,
                openingHours = "Open • 9:00 AM – 8:00 PM",
                isOpenNow = true,
                mapX = 0.22f,
                mapY = 0.24f,
                phoneNumber = "(555) 567-8901",
                carriedPublicationTitles = "Atelier & Design Journal, Horizon Tech Quarterly, The Daily Townsquare",
                stockStatus = "Special Archive Editions Available (12 copies in stock)",
                availableCopies = 12
            ),
            RetailKioskEntity(
                id = "kiosk_midtown_corner",
                name = "Midtown Broadsheets & Corner Stand",
                type = "Corner Newsstand",
                address = "Corner of 5th Ave & 28th St",
                district = "Midtown Commercial",
                distanceMiles = 1.4f,
                walkingMinutes = 20,
                openingHours = "Open • 5:30 AM – 8:30 PM",
                isOpenNow = true,
                mapX = 0.55f,
                mapY = 0.76f,
                phoneNumber = "(555) 678-9012",
                carriedPublicationTitles = "The Daily Townsquare, Morning Espresso Weekend, Metropolitan Sunday Chronicle",
                stockStatus = "Restocked at 2:00 PM (15 copies in stock)",
                availableCopies = 15
            ),
            RetailKioskEntity(
                id = "kiosk_university",
                name = "University Heights Student Commons Press",
                type = "Campus Periodicals Kiosk",
                address = "100 Academic Row, Quadrangle",
                district = "University Campus",
                distanceMiles = 1.7f,
                walkingMinutes = 24,
                openingHours = "Open • 7:30 AM – 10:00 PM",
                isOpenNow = true,
                mapX = 0.18f,
                mapY = 0.65f,
                phoneNumber = "(555) 789-0123",
                carriedPublicationTitles = "Horizon Tech Quarterly, The Daily Townsquare, Global Dispatch Monthly Report",
                stockStatus = "Campus Discount Active (20 copies in stock)",
                availableCopies = 20
            )
        )
        mediaDao.insertRetailKiosks(kiosks)
    }

    private suspend fun seedBulletins() {
        val now = System.currentTimeMillis()
        val bulletins = listOf(
            LocalBulletinEntity(
                category = "TRAFFIC",
                title = "Westbound Riverfront Parkway Lane Closure for Bridge Check",
                description = "Single lane reduction near Pier 3 due to routine structural survey. 10-15 minute delays expected. Use 2nd Ave as alternate bypass.",
                locationName = "Riverfront Pkwy & 3rd Ave",
                reporterName = "Transit Officer Dan",
                reporterHandle = "@metro_patrol",
                timestamp = now - 180000,
                upvotesCount = 42,
                isUpvoted = false,
                urgencyLevel = "MODERATE",
                iconEmoji = "🚦",
                status = "ACTIVE"
            ),
            LocalBulletinEntity(
                category = "WEATHER",
                title = "Sudden Flash Rainstorm & Gust Warnings Across East Basin",
                description = "Fast-moving storm front carrying brief heavy downpours and 25 mph wind gusts. Expected to clear within 40 minutes.",
                locationName = "East Hills & Highland Ave",
                reporterName = "Civic Weather Watch",
                reporterHandle = "@east_weather",
                timestamp = now - 420000,
                upvotesCount = 68,
                isUpvoted = false,
                urgencyLevel = "MODERATE",
                iconEmoji = "⛈️",
                status = "ACTIVE"
            ),
            LocalBulletinEntity(
                category = "EVENT",
                title = "Historic Quad Farmers & Artisan Market Open Today",
                description = "40+ local organic growers, heritage bakers, sourdough specialists, and print artisans set up until 3:00 PM. Live acoustic band playing at noon.",
                locationName = "Civic Market Commons Quad",
                reporterName = "Clara Bennett",
                reporterHandle = "@market_clara",
                timestamp = now - 900000,
                upvotesCount = 115,
                isUpvoted = false,
                urgencyLevel = "INFO",
                iconEmoji = "🎪",
                status = "ACTIVE"
            ),
            LocalBulletinEntity(
                category = "ALERT",
                title = "Emergency Water Main Repair Work in Progress",
                description = "Municipal utility crew on site repairing main line valve. Low water pressure reported on 8th Ave between Elm and Pine until 2:00 PM.",
                locationName = "8th Ave & Pine Street",
                reporterName = "Public Utilities Dept",
                reporterHandle = "@town_utilities",
                timestamp = now - 1500000,
                upvotesCount = 53,
                isUpvoted = false,
                urgencyLevel = "URGENT",
                iconEmoji = "⚠️",
                status = "ACTIVE"
            ),
            LocalBulletinEntity(
                category = "TRAFFIC",
                title = "Civic Center Autonomous Tram Line 1 Operating Smoothly",
                description = "All electric shuttles on 4-minute headways. Passenger boarding kiosks clear and contactless tap-in stations active.",
                locationName = "Civic Center Central Loop",
                reporterName = "Marcus Vance",
                reporterHandle = "@marcus_v",
                timestamp = now - 2400000,
                upvotesCount = 31,
                isUpvoted = false,
                urgencyLevel = "INFO",
                iconEmoji = "🚊",
                status = "VERIFIED"
            ),
            LocalBulletinEntity(
                category = "EVENT",
                title = "Twilight Outdoor Acoustic Jazz Session Tonight at 7:00 PM",
                description = "Free community admission in the Riverfront Pavilion. Bring picnic blankets. Espresso and artisanal cider by the Local Roasters Guild.",
                locationName = "Riverfront Bandstand Park",
                reporterName = "Arts Council",
                reporterHandle = "@arts_council",
                timestamp = now - 3600000,
                upvotesCount = 94,
                isUpvoted = false,
                urgencyLevel = "INFO",
                iconEmoji = "🎷",
                status = "ACTIVE"
            ),
            LocalBulletinEntity(
                category = "WEATHER",
                title = "Pristine Air Quality Index (AQI 16) Valley-wide",
                description = "Crisp autumn morning with crystal-clear visibility and light northwest breeze (6 mph). Ideal conditions for outdoor cycling and running.",
                locationName = "Valley Basin Wide",
                reporterName = "EcoMonitor Station",
                reporterHandle = "@air_quality",
                timestamp = now - 5400000,
                upvotesCount = 27,
                isUpvoted = false,
                urgencyLevel = "INFO",
                iconEmoji = "🌤️",
                status = "VERIFIED"
            ),
            LocalBulletinEntity(
                category = "COMMUNITY",
                title = "Friendly Lost Golden Retriever 'Barnaby' Reunited Safely",
                description = "Thanks to quick neighbor reports on the bulletin board, Barnaby has been reunited with his family near North Meadow!",
                locationName = "North Meadow Gazebo",
                reporterName = "Citizen Patrol",
                reporterHandle = "@civic_patrol",
                timestamp = now - 7200000,
                upvotesCount = 142,
                isUpvoted = false,
                urgencyLevel = "INFO",
                iconEmoji = "🐕",
                status = "RESOLVED"
            )
        )
        mediaDao.insertBulletins(bulletins)
    }

    private suspend fun seedTvChannels() {
        val tvChannels = listOf(
            TvChannelEntity(
                id = "tctv_ch1",
                channelNumber = 1,
                name = "First Programme",
                networkTitle = "Townsquare Central Television",
                callsign = "TCTV-1",
                tagline = "The Voice of the City • Flagship News, Prime Current Affairs & Live Civic Debates",
                category = "News & Current Affairs",
                themeColorHex = 0xFF00D2FF,
                iconEmoji = "📺",
                resolutionBadge = "4K UHD HDR • 60 FPS",
                audioFormat = "Dolby 5.1 Surround",
                signalStrength = "100% Broadcast (MUX-A / Tower Alpha)",
                currentShowTitle = "Townsquare Tonight: 19:00 Primetime Journal",
                currentShowCategory = "Live News & Investigative",
                currentShowTime = "19:00 - 20:00",
                currentShowSynopsis = "Anchors Evelyn Vance and Marcus Cole report on the grand launch of the Central Magnetic Transit line, tonight's City Hall vote on renewable solar grid expansion, and an exclusive roundtable with community architects.",
                currentShowProgress = 0.58f,
                hostPresenter = "Evelyn Vance & Marcus Cole",
                liveViewersCount = 31450,
                isFavorite = true,
                nextShowTitle = "Civic Forum: Metropolitan Urban Development Debate",
                nextShowTime = "20:00 - 21:00"
            ),
            TvChannelEntity(
                id = "tctv_ch2",
                channelNumber = 2,
                name = "Second Programme",
                networkTitle = "Townsquare Central Television",
                callsign = "TCTV-2",
                tagline = "Arts, Stage & Symphony • World Documentaries & Cinema Classics",
                category = "Culture, Arts & Symphony",
                themeColorHex = 0xFFE040FB,
                iconEmoji = "🎭",
                resolutionBadge = "4K Cinema 24p • Master Audio",
                audioFormat = "Dolby Atmos 7.1",
                signalStrength = "98% Broadcast (MUX-B / Tower Alpha)",
                currentShowTitle = "Philharmonic Live: Brahms Symphony No. 4 in E Minor",
                currentShowCategory = "Live Orchestral Gala",
                currentShowTime = "19:30 - 21:00",
                currentShowSynopsis = "Live direct from the Townsquare Grand Concert Hall, Maestro Julian Rossi leads the Municipal Symphony Orchestra in an expressive interpretation of Johannes Brahms' Opus 98.",
                currentShowProgress = 0.42f,
                hostPresenter = "Maestro Julian Rossi & Claire Dupond",
                liveViewersCount = 18600,
                isFavorite = false,
                nextShowTitle = "Indie Masterpiece: The Lost Glassblowers of Murano",
                nextShowTime = "21:00 - 22:30"
            ),
            TvChannelEntity(
                id = "tctv_ch3",
                channelNumber = 3,
                name = "Third Programme",
                networkTitle = "Townsquare Central Television",
                callsign = "TCTV-3",
                tagline = "Inquiry, Exploration & Science • Planetary Biomes & Higher Lectures",
                category = "Science, Tech & Nature",
                themeColorHex = 0xFF00E676,
                iconEmoji = "🔬",
                resolutionBadge = "1080p 60 FPS • High Dynamic",
                audioFormat = "Studio Master Stereo",
                signalStrength = "99% Broadcast (MUX-C / Tower Beta)",
                currentShowTitle = "Quantum Frontiers: Spectrographic Mapping of Exoplanets",
                currentShowCategory = "Astrophysics & Space",
                currentShowTime = "18:45 - 20:15",
                currentShowSynopsis = "Astrophysicist Dr. Aris Thorne demonstrates new atmospheric spectra from deep space orbital arrays detecting water vapor and oxygen isotopes in nearby exoplanetary solar systems.",
                currentShowProgress = 0.68f,
                hostPresenter = "Dr. Aris Thorne & Prof. Elena Ramos",
                liveViewersCount = 14200,
                isFavorite = false,
                nextShowTitle = "Wild Earth: Secrets of the Ancient Redwood Canopy",
                nextShowTime = "20:15 - 21:15"
            ),
            TvChannelEntity(
                id = "tctv_ch4",
                channelNumber = 4,
                name = "Fourth Programme",
                networkTitle = "Townsquare Central Television",
                callsign = "TCTV-4",
                tagline = "Live Arena Sports, Local Athletics • Marathons & Championship Leagues",
                category = "Sports & Live Stadium",
                themeColorHex = 0xFFFF9100,
                iconEmoji = "⚽",
                resolutionBadge = "4K 120 FPS • High Speed Motion",
                audioFormat = "Stadium Spatial 5.1",
                signalStrength = "100% Broadcast (MUX-D / Tower Beta)",
                currentShowTitle = "Grand Metro Cup: Riverside FC vs. Highland United",
                currentShowCategory = "Live Football Semifinal",
                currentShowTime = "19:15 - 21:00",
                currentShowSynopsis = "Intense second-half action at Civic Stadium as Riverside FC holds a narrow 2-1 lead with 20 minutes remaining on the clock. Commentary from veteran sporting analysts.",
                currentShowProgress = 0.74f,
                hostPresenter = "Leo Sterling & Coach Tara Wells",
                liveViewersCount = 46800,
                isFavorite = true,
                nextShowTitle = "Post-Match Verdict & Night League Highlights",
                nextShowTime = "21:00 - 22:00"
            ),
            TvChannelEntity(
                id = "tctv_ch5",
                channelNumber = 5,
                name = "Fifth Programme",
                networkTitle = "Townsquare Central Television",
                callsign = "TCTV-5",
                tagline = "Masterpiece Drama, Period Serializations & Vintage Theatre Archive",
                category = "Drama Series & Cinema",
                themeColorHex = 0xFFFF5252,
                iconEmoji = "🎬",
                resolutionBadge = "4K Cinematic • Dolby Vision",
                audioFormat = "Dolby Atmos 5.1",
                signalStrength = "97% Broadcast (MUX-E / Tower Gamma)",
                currentShowTitle = "The Clockmaker of Saint-Germain • Ep. 4: The Pendulum",
                currentShowCategory = "Period Mystery Serial",
                currentShowTime = "20:00 - 21:15",
                currentShowSynopsis = "In 1888 Paris, master horologist Laurent uncovers an ornate clockwork cylinder concealing coded diplomatic cables routed beneath the Seine river embankments.",
                currentShowProgress = 0.22f,
                hostPresenter = "TCTV Drama Guild Ensemble",
                liveViewersCount = 24900,
                isFavorite = false,
                nextShowTitle = "Classic Vault: Midnight on the Pont Neuf (1949 Remastered)",
                nextShowTime = "21:15 - 23:00"
            ),
            TvChannelEntity(
                id = "tctv_ch6",
                channelNumber = 6,
                name = "Sixth Programme",
                networkTitle = "Townsquare Central Television",
                callsign = "TCTV-6",
                tagline = "Youth Culture, Electronic Music • Animation, Street Art & Festival Live",
                category = "Youth, Music & Animation",
                themeColorHex = 0xFFFFD600,
                iconEmoji = "⚡",
                resolutionBadge = "1080p 60 FPS • Ultra-Low Latency",
                audioFormat = "Hi-Res Lossless 96kHz",
                signalStrength = "100% Broadcast (MUX-F / Tower Gamma)",
                currentShowTitle = "Neon Soundstage: Live Modular Synth Sessions & Visuals",
                currentShowCategory = "Electronic Music Live",
                currentShowTime = "19:00 - 22:00",
                currentShowSynopsis = "Sound artists DJ Kora and the Pixel Collective perform an improvisational generative ambient and electronica set with real-time reactive optical projection mapping.",
                currentShowProgress = 0.38f,
                hostPresenter = "DJ Kora & The Pixel Collective",
                liveViewersCount = 21300,
                isFavorite = false,
                nextShowTitle = "Indie Animation Showcase: Tokyo & Bristol Festival Winners",
                nextShowTime = "22:00 - 23:30"
            ),
            TvChannelEntity(
                id = "space_ch_alex",
                channelNumber = 7,
                name = "Alex's Tech Stream",
                networkTitle = "Alex's Tech Dispatch",
                callsign = "SPACE-ALEX",
                tagline = "Live Coding, Architecture Reviews & Tech Deep Dives",
                category = "User Space Live",
                themeColorHex = 0xFF00D2FF,
                iconEmoji = "💻",
                currentShowTitle = "Live Coding: Building the Future Media Superapp",
                currentShowCategory = "Programming Live",
                currentShowTime = "19:00 - 21:00",
                currentShowSynopsis = "Alex is live coding a new modular UI system for civic engagement apps using Jetpack Compose and Room.",
                hostPresenter = "Alex Chen",
                liveViewersCount = 1420,
                isFavorite = true,
                nextShowTitle = "AMA: Career in Computational Architecture",
                nextShowTime = "21:00 - 22:00"
            ),
            TvChannelEntity(
                id = "space_ch_sunset",
                channelNumber = 8,
                name = "Sunset Live Beats",
                networkTitle = "Sunset Frequencies",
                callsign = "SPACE-SUNSET",
                tagline = "24/7 Ambient Soundscapes & Visualizer Chill",
                category = "User Space Live",
                themeColorHex = 0xFFFF9F1C,
                iconEmoji = "🌇",
                currentShowTitle = "Golden Hour Ambient Session",
                currentShowCategory = "Ambient Music",
                currentShowTime = "18:00 - 20:00",
                currentShowSynopsis = "Relaxing ambient textures and field recordings from the waterfront as the sun sets over the city.",
                hostPresenter = "Sunset AI Curator",
                liveViewersCount = 3890,
                isFavorite = false,
                nextShowTitle = "Midnight Echoes: Deep Focus Dub",
                nextShowTime = "20:00 - 22:00"
            )
        )
        mediaDao.insertTvChannels(tvChannels)
    }
}
