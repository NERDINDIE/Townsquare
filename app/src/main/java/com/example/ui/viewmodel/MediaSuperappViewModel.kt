package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioPlayerManager
import com.example.audio.AudioState
import com.example.audio.NarrationStyle
import com.example.audio.VoiceNarrationManager
import com.example.audio.VoiceNarrationState
import com.example.data.local.AppDatabase
import com.example.data.local.AppSettingsManager
import com.example.data.model.AppSettings
import com.example.data.model.AudioStreamingQuality
import com.example.data.model.ClaimVerdictCategory
import com.example.data.model.ComicStripItem
import com.example.data.model.FactCheckClaim
import com.example.data.model.FactCheckReport
import com.example.data.model.FacsimileBroadsheet
import com.example.data.model.FacsimileBroadsheetRepository
import com.example.data.model.JournalEditionEntity
import com.example.data.model.LetterToEditor
import com.example.data.model.LiveNewsblogEntity
import com.example.data.model.LocalBulletinEntity
import com.example.data.model.MediaChannelEntity
import com.example.data.model.MediaItemEntity
import com.example.data.model.MediaSpaceEntity
import com.example.data.model.MediaType
import com.example.data.model.MisinformationType
import com.example.data.model.NotepadDraftEntity
import com.example.data.model.PartnerApplication
import com.example.data.model.PartnerPublication
import com.example.data.model.PartnerSyndicateRepository
import com.example.data.model.ReadingFontSize
import com.example.data.model.RetailKioskEntity
import com.example.data.model.TvChannelEntity
import com.example.data.model.UpcomingEditionEntity
import com.example.data.model.VisualPostEntity
import com.example.data.model.VoiceNarrationPreset
import com.example.data.model.InboxActionType
import com.example.data.model.InboxCategory
import com.example.data.model.InboxNotificationItem
import com.example.data.model.InboxSeedHelper
import com.example.data.repository.MediaRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TvChatMessage(
    val id: String,
    val sender: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val color: Long = 0xFF00D2FF
)

class MediaSuperappViewModel(application: Application) : AndroidViewModel(application) {

    private val _tvChatMessages = MutableStateFlow<Map<String, List<TvChatMessage>>>(emptyMap())
    val tvChatMessages: StateFlow<Map<String, List<TvChatMessage>>> = _tvChatMessages.asStateFlow()

    fun sendTvChatMessage(channelId: String, sender: String, message: String) {
        val currentMessages = _tvChatMessages.value[channelId] ?: emptyList()
        val newMessage = TvChatMessage(
            id = System.currentTimeMillis().toString(),
            sender = sender,
            message = message
        )
        _tvChatMessages.value = _tvChatMessages.value + (channelId to (currentMessages + newMessage))
    }

    private fun simulateIncomingChat(channelId: String) {
        viewModelScope.launch {
            val names = listOf("Alex", "Sam", "Chris", "Devin", "Taylor", "Jordan")
            val texts = listOf(
                "This broadcast is amazing!",
                "I agree, the visuals are stunning.",
                "Can't wait for the next segment.",
                "Anyone know what time it ends?",
                "TCTV is always the best quality.",
                "The scanline effect is a nice touch."
            )
            while (true) {
                delay((3000..8000).random().toLong())
                if (isTvPlaying.value && activeTvChannelId.value == channelId) {
                    sendTvChatMessage(channelId, names.random(), texts.random())
                }
            }
        }
    }

    private val database = AppDatabase.getInstance(application)
    val repository = MediaRepository(database.mediaDao())
    val audioPlayer = AudioPlayerManager.getInstance(application)
    val voiceNarrator = VoiceNarrationManager(application)
    val settingsManager = AppSettingsManager.getInstance(application)

    // Theme state
    private val _currentTheme = MutableStateFlow(com.example.ui.theme.PresetThemes[0])
    val currentTheme: StateFlow<com.example.ui.theme.ThemeConfig> = _currentTheme.asStateFlow()

    private val _isDynamicTheme = MutableStateFlow(false)
    val isDynamicTheme: StateFlow<Boolean> = _isDynamicTheme.asStateFlow()

    init {
        viewModelScope.launch {
            repository.checkAndSeedDatabase()
            // Start simulation for some channels
            listOf("tctv_ch1", "tctv_ch2", "tctv_ch3").forEach {
                simulateIncomingChat(it)
            }
        }
        monitorDynamicTheme()
    }

    val audioState: StateFlow<AudioState> = audioPlayer.audioState
    val voiceNarrationState: StateFlow<VoiceNarrationState> = voiceNarrator.state
    val narrationState: StateFlow<VoiceNarrationState> = voiceNarrator.state
    val appSettings: StateFlow<AppSettings> = settingsManager.settings

    // Raw sources from DB
    val allItems: StateFlow<List<MediaItemEntity>> = repository.allMediaItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allChannels: StateFlow<List<MediaChannelEntity>> = repository.allChannels
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val followedChannels: StateFlow<List<MediaChannelEntity>> = repository.followedChannels
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userSpaces: StateFlow<List<MediaSpaceEntity>> = repository.userSpaces
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSpaces: StateFlow<List<MediaSpaceEntity>> = repository.allSpaces
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userCreatedItems: StateFlow<List<MediaItemEntity>> = repository.userCreatedItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarkedItems: StateFlow<List<MediaItemEntity>> = repository.bookmarkedItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val offlineSavedItems: StateFlow<List<MediaItemEntity>> = repository.offlineSavedItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Offline Mode switch
    private val _isOfflineMode = MutableStateFlow(false)
    val isOfflineMode: StateFlow<Boolean> = _isOfflineMode.asStateFlow()

    val allJournalEditions: StateFlow<List<JournalEditionEntity>> = repository.allJournalEditions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val archivedJournalEditions: StateFlow<List<JournalEditionEntity>> = repository.archivedJournalEditions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUpcomingEditions: StateFlow<List<UpcomingEditionEntity>> = repository.allUpcomingEditions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRetailKiosks: StateFlow<List<RetailKioskEntity>> = repository.allRetailKiosks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBulletins: StateFlow<List<LocalBulletinEntity>> = repository.allBulletins
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTvChannels: StateFlow<List<TvChannelEntity>> = repository.allTvChannels
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 24/7 Live Newsblog State
    val allNewsblogEntries: StateFlow<List<LiveNewsblogEntity>> = repository.allNewsblogEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedNewsblogCategory = MutableStateFlow("ALL")
    val selectedNewsblogCategory: StateFlow<String> = _selectedNewsblogCategory.asStateFlow()

    private val _newsblogAudioPlayingId = MutableStateFlow<Long?>(null)
    val newsblogAudioPlayingId: StateFlow<Long?> = _newsblogAudioPlayingId.asStateFlow()

    private val _isSimulatingNewsblogUpdate = MutableStateFlow(false)
    val isSimulatingNewsblogUpdate: StateFlow<Boolean> = _isSimulatingNewsblogUpdate.asStateFlow()

    val filteredNewsblogEntries: StateFlow<List<LiveNewsblogEntity>> = combine(
        allNewsblogEntries,
        _selectedNewsblogCategory
    ) { entries, category ->
        if (category == "ALL") entries
        else entries.filter { it.categoryTag.equals(category, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Multimedia Notepad (Journal Drafts) State
    val allNotepadDrafts: StateFlow<List<NotepadDraftEntity>> = repository.allNotepadDrafts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeNotepadDraft = MutableStateFlow<NotepadDraftEntity?>(null)
    val activeNotepadDraft: StateFlow<NotepadDraftEntity?> = _activeNotepadDraft.asStateFlow()

    private val _selectedDraftFilter = MutableStateFlow("ALL")
    val selectedDraftFilter: StateFlow<String> = _selectedDraftFilter.asStateFlow()

    val filteredNotepadDrafts: StateFlow<List<NotepadDraftEntity>> = combine(
        allNotepadDrafts,
        _selectedDraftFilter
    ) { drafts, filter ->
        when (filter) {
            "ALL" -> drafts
            "STARRED" -> drafts.filter { it.isStarred }
            else -> drafts.filter { it.draftType.equals(filter, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Visual & Photojournalism Feed State
    val allVisualPosts: StateFlow<List<VisualPostEntity>> = repository.allVisualPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedVisualCategory = MutableStateFlow("ALL")
    val selectedVisualCategory: StateFlow<String> = _selectedVisualCategory.asStateFlow()

    private val _activeLightboxPost = MutableStateFlow<VisualPostEntity?>(null)
    val activeLightboxPost: StateFlow<VisualPostEntity?> = _activeLightboxPost.asStateFlow()

    val filteredVisualPosts: StateFlow<List<VisualPostEntity>> = combine(
        allVisualPosts,
        _selectedVisualCategory
    ) { posts, category ->
        if (category == "ALL") posts
        else posts.filter { it.category.contains(category, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Sidebar drawer visibility
    private val _isSidebarOpen = MutableStateFlow(false)
    val isSidebarOpen: StateFlow<Boolean> = _isSidebarOpen.asStateFlow()

    fun selectTheme(theme: com.example.ui.theme.ThemeConfig) {
        _isDynamicTheme.value = false
        _currentTheme.value = theme
    }

    fun setDynamicThemeEnabled(enabled: Boolean) {
        _isDynamicTheme.value = enabled
        if (enabled) {
            val hour = java.time.LocalDateTime.now().hour
            _currentTheme.value = getDynamicThemeForHour(hour)
        }
    }

    private fun monitorDynamicTheme() {
        viewModelScope.launch {
            while (true) {
                if (_isDynamicTheme.value) {
                    val hour = java.time.LocalDateTime.now().hour
                    val matchedTheme = getDynamicThemeForHour(hour)
                    if (_currentTheme.value.id != matchedTheme.id) {
                        _currentTheme.value = matchedTheme
                    }
                }
                delay(5000) // Check every 5 seconds for immediate update on manual clock triggers
            }
        }
    }

    fun getDynamicThemeForHour(hour: Int): com.example.ui.theme.ThemeConfig {
        return when (hour) {
            in 22..23, in 0..2 -> com.example.ui.theme.PresetThemes.firstOrNull { it.id == "late_night" } ?: com.example.ui.theme.PresetThemes[0]
            in 3..6 -> com.example.ui.theme.PresetThemes.firstOrNull { it.id == "dawn" } ?: com.example.ui.theme.PresetThemes[0]
            in 7..11 -> com.example.ui.theme.PresetThemes.firstOrNull { it.id == "morning" } ?: com.example.ui.theme.PresetThemes[0]
            12 -> com.example.ui.theme.PresetThemes.firstOrNull { it.id == "noon" } ?: com.example.ui.theme.PresetThemes[0]
            in 13..15 -> com.example.ui.theme.PresetThemes.firstOrNull { it.id == "afternoon" } ?: com.example.ui.theme.PresetThemes[0]
            in 16..19 -> com.example.ui.theme.PresetThemes.firstOrNull { it.id == "evening" } ?: com.example.ui.theme.PresetThemes[0]
            else -> com.example.ui.theme.PresetThemes.firstOrNull { it.id == "night" } ?: com.example.ui.theme.PresetThemes[0]
        }
    }

    fun applyCustomTheme(
        name: String,
        isDark: Boolean,
        primary: androidx.compose.ui.graphics.Color,
        secondary: androidx.compose.ui.graphics.Color,
        background: androidx.compose.ui.graphics.Color,
        surface: androidx.compose.ui.graphics.Color
    ) {
        val customTheme = com.example.ui.theme.ThemeConfig(
            id = "custom_theme",
            name = name.ifBlank { "Custom Theme" },
            isDark = isDark,
            primary = primary,
            secondary = secondary,
            background = background,
            surface = surface
        )
        _isDynamicTheme.value = false
        _currentTheme.value = customTheme
    }

    // Profile state
    private val _selectedProfileType = MutableStateFlow<String?>(null)
    val selectedProfileType: StateFlow<String?> = _selectedProfileType.asStateFlow()

    fun selectProfile(type: String?) {
        _selectedProfileType.value = type
    }

    // Fact Checker state
    private val _activeFactCheckReport = MutableStateFlow<FactCheckReport?>(null)
    val activeFactCheckReport: StateFlow<FactCheckReport?> = _activeFactCheckReport.asStateFlow()

    // Letters to the Editor state
    private val _lettersToEditor = MutableStateFlow<List<LetterToEditor>>(
        listOf(
            LetterToEditor(
                id = "letter_1",
                recipientId = "ch_metro",
                recipientName = "Metro Editorial Staff",
                recipientType = "CHANNEL",
                senderName = "Eleanor Vance",
                senderEmail = "eleanor.vance@townsquare.org",
                subject = "Clarification on Civic Center Transit Initiative Budget",
                messageBody = "Dear Editors,\n\nI appreciated the thorough coverage on the downtown transit expansion in Volume 14. However, the estimated completion timeline in paragraph 4 appears to reflect the 2024 initial scope rather than the revised Q3 2026 phase II schedule.\n\nThank you for keeping our community informed,\nEleanor",
                categoryTag = "Correction Request",
                timestampFormatted = "Today at 09:14 AM",
                status = "Editor Replied",
                editorReply = "Thank you Eleanor! We have audited the claim and issued an updated fact-checking correction badge on the article dispatch.",
                editorReplyTimestamp = "Today at 10:30 AM",
                isStarred = true
            ),
            LetterToEditor(
                id = "letter_2",
                recipientId = "sp_civic",
                recipientName = "Civic Journalists Guild",
                recipientType = "SPACE",
                senderName = "Marcus Thorne",
                senderEmail = "m.thorne@pressguild.org",
                subject = "Op-Ed Submission: The Future of Hyperlocal Radio Dial Streaming",
                messageBody = "To the Guild Moderator,\n\nSubmitting an op-ed draft proposing community-owned low-power FM radio repeaters synchronized with digital superapp streams. Would love to have this reviewed for the upcoming weekend edition.\n\nBest regards,\nMarcus Thorne",
                categoryTag = "Op-Ed Submission",
                timestampFormatted = "Yesterday at 04:45 PM",
                status = "Sent to Desk",
                isStarred = false
            )
        )
    )
    val lettersToEditor: StateFlow<List<LetterToEditor>> = _lettersToEditor.asStateFlow()

    private val _activeLetterRecipient = MutableStateFlow<Pair<String, String>?>(null) // Pair(id, name)
    val activeLetterRecipient: StateFlow<Pair<String, String>?> = _activeLetterRecipient.asStateFlow()

    private val _isLettersMailboxOpen = MutableStateFlow(false)
    val isLettersMailboxOpen: StateFlow<Boolean> = _isLettersMailboxOpen.asStateFlow()

    // Sunday Funnies & Memes state
    private val _selectedFunniesCategory = MutableStateFlow("All Funnies")
    val selectedFunniesCategory: StateFlow<String> = _selectedFunniesCategory.asStateFlow()

    private val _sundayFunniesComics = MutableStateFlow<List<ComicStripItem>>(
        listOf(
            ComicStripItem(
                id = "comic_1",
                title = "The Morning Coffee Deadline",
                syndicateSeries = "The Townsquare Pressroom",
                cartoonistName = "Artie Quill",
                publishDateFormatted = "Sunday Edition",
                imageUrl = "https://picsum.photos/seed/comic_coffee/800/480",
                caption = "“I don't print the news until the coffee machine finishes its third editorial cycle.”",
                issueNumber = 482,
                laughsCount = 142,
                spotOnCount = 89,
                classicCount = 64,
                tags = listOf("Pressroom", "Coffee", "Deadlines")
            ),
            ComicStripItem(
                id = "comic_2",
                title = "Fact-Checking the Weather Forecast",
                syndicateSeries = "Press & Pixel",
                cartoonistName = "Scribble Sam",
                publishDateFormatted = "Sunday Special",
                imageUrl = "https://picsum.photos/seed/comic_weather/800/480",
                caption = "“The algorithm predicts 100% chance of rain, but the editor's umbrella says 40%.”",
                issueNumber = 219,
                laughsCount = 98,
                spotOnCount = 134,
                classicCount = 42,
                tags = listOf("Weather", "Algorithms", "Cartoons")
            ),
            ComicStripItem(
                id = "comic_3",
                title = "When the Radio Tuner Hits 88.5 MHz",
                syndicateSeries = "Civic Memes Digest",
                cartoonistName = "MemeMaster99",
                publishDateFormatted = "Community Submission",
                imageUrl = "https://picsum.photos/seed/comic_radio/800/480",
                caption = "Nobody: \nMe listening to analog static on the Townsquare media tuner at 2 AM:",
                issueNumber = 104,
                laughsCount = 215,
                spotOnCount = 176,
                classicCount = 92,
                isUserSubmittedMeme = true,
                tags = listOf("Memes", "Radio", "Civic")
            ),
            ComicStripItem(
                id = "comic_4",
                title = "The Great Broadsheet Fold",
                syndicateSeries = "Metro Minutes Cartoons",
                cartoonistName = "Pen & Ink Co.",
                publishDateFormatted = "Classic Syndicate",
                imageUrl = "https://picsum.photos/seed/comic_newspaper/800/480",
                caption = "“It's not just a newspaper—it's a wind shield, a coffee coaster, and a masterpiece.”",
                issueNumber = 350,
                laughsCount = 110,
                spotOnCount = 94,
                classicCount = 120,
                tags = listOf("Broadsheet", "Print", "Humor")
            )
        )
    )

    val filteredSundayFunnies: StateFlow<List<ComicStripItem>> = combine(
        _sundayFunniesComics,
        _selectedFunniesCategory
    ) { list, cat ->
        when (cat) {
            "Syndicated Strips" -> list.filter { !it.isUserSubmittedMeme }
            "Civic Memes" -> list.filter { it.isUserSubmittedMeme }
            "Top Voted" -> list.sortedByDescending { it.laughsCount + it.spotOnCount }
            else -> list
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeTvChannelId = MutableStateFlow<String>("tctv_ch1")
    val activeTvChannelId: StateFlow<String> = _activeTvChannelId.asStateFlow()

    private val _isTvPlaying = MutableStateFlow(true)
    val isTvPlaying: StateFlow<Boolean> = _isTvPlaying.asStateFlow()

    private val _isTvMuted = MutableStateFlow(false)
    val isTvMuted: StateFlow<Boolean> = _isTvMuted.asStateFlow()

    private val _isTvFullscreen = MutableStateFlow(false)
    val isTvFullscreen: StateFlow<Boolean> = _isTvFullscreen.asStateFlow()

    private val _isTvCaptionsEnabled = MutableStateFlow(true)
    val isTvCaptionsEnabled: StateFlow<Boolean> = _isTvCaptionsEnabled.asStateFlow()

    private val _tvStreamQuality = MutableStateFlow("4K UHD 60FPS")
    val tvStreamQuality: StateFlow<String> = _tvStreamQuality.asStateFlow()

    private val _tvSelectedTab = MutableStateFlow("LIVE_CHANNELS") // LIVE_CHANNELS, TV_GUIDE, ON_DEMAND, RECORDINGS
    val tvSelectedTab: StateFlow<String> = _tvSelectedTab.asStateFlow()

    private val _tvScanlineFxEnabled = MutableStateFlow(false)
    val tvScanlineFxEnabled: StateFlow<Boolean> = _tvScanlineFxEnabled.asStateFlow()

    private val _selectedBulletinCategory = MutableStateFlow<String?>(null)
    val selectedBulletinCategory: StateFlow<String?> = _selectedBulletinCategory.asStateFlow()

    private val _isReportBulletinDialogVisible = MutableStateFlow(false)
    val isReportBulletinDialogVisible: StateFlow<Boolean> = _isReportBulletinDialogVisible.asStateFlow()

    // Partner Syndicate Network state
    private val _partnerPublications = MutableStateFlow<List<PartnerPublication>>(
        PartnerSyndicateRepository.initialPartnerPublications
    )
    val partnerPublications: StateFlow<List<PartnerPublication>> = _partnerPublications.asStateFlow()

    private val _partnerApplications = MutableStateFlow<List<PartnerApplication>>(emptyList())
    val partnerApplications: StateFlow<List<PartnerApplication>> = _partnerApplications.asStateFlow()

    private val _isPartnerApplicationOpen = MutableStateFlow(false)
    val isPartnerApplicationOpen: StateFlow<Boolean> = _isPartnerApplicationOpen.asStateFlow()

    // Retro Hourly Facsimile Broadsheet state
    private val _isFacsimileOpen = MutableStateFlow(false)
    val isFacsimileOpen: StateFlow<Boolean> = _isFacsimileOpen.asStateFlow()

    private val _activeFacsimileDispatch = MutableStateFlow<FacsimileBroadsheet?>(null)
    val activeFacsimileDispatch: StateFlow<FacsimileBroadsheet?> = _activeFacsimileDispatch.asStateFlow()

    // Pull-to-refresh state for main feed
    private val _isRefreshingFeed = MutableStateFlow(false)
    val isRefreshingFeed: StateFlow<Boolean> = _isRefreshingFeed.asStateFlow()

    private val _refreshStatusText = MutableStateFlow<String?>("Editorial wire connected • Pull to refresh")
    val refreshStatusText: StateFlow<String?> = _refreshStatusText.asStateFlow()

    // Filters and UI states
    private val _selectedChannelFilter = MutableStateFlow<String?>(null)
    val selectedChannelFilter: StateFlow<String?> = _selectedChannelFilter.asStateFlow()

    private val _selectedMediaTypeFilter = MutableStateFlow<MediaType?>(null)
    val selectedMediaTypeFilter: StateFlow<MediaType?> = _selectedMediaTypeFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _activeReadingItem = MutableStateFlow<MediaItemEntity?>(null)
    val activeReadingItem: StateFlow<MediaItemEntity?> = _activeReadingItem.asStateFlow()

    // Flipbook reader state
    private val _activeFlipbookItem = MutableStateFlow<MediaItemEntity?>(null)
    val activeFlipbookItem: StateFlow<MediaItemEntity?> = _activeFlipbookItem.asStateFlow()

    private val _activeFlipbookJournal = MutableStateFlow<JournalEditionEntity?>(null)
    val activeFlipbookJournal: StateFlow<JournalEditionEntity?> = _activeFlipbookJournal.asStateFlow()

    // Newsstand filter state: "ALL", "NEWSPAPER", "MAGAZINE", "MY_PRESS"
    private val _newsstandFilter = MutableStateFlow("ALL")
    val newsstandFilter: StateFlow<String> = _newsstandFilter.asStateFlow()

    private val _isMorningBriefOpen = MutableStateFlow(false)
    val isMorningBriefOpen: StateFlow<Boolean> = _isMorningBriefOpen.asStateFlow()

    private val _isCreateContentOpen = MutableStateFlow(false)
    val isCreateContentOpen: StateFlow<Boolean> = _isCreateContentOpen.asStateFlow()

    private val _isCreateSpaceOpen = MutableStateFlow(false)
    val isCreateSpaceOpen: StateFlow<Boolean> = _isCreateSpaceOpen.asStateFlow()

    private val _isFullPlayerOpen = MutableStateFlow(false)
    val isFullPlayerOpen: StateFlow<Boolean> = _isFullPlayerOpen.asStateFlow()

    // Inbox Notifications State
    private val _inboxItems = MutableStateFlow<List<InboxNotificationItem>>(InboxSeedHelper.getInitialInboxItems())
    val inboxItems: StateFlow<List<InboxNotificationItem>> = _inboxItems.asStateFlow()

    val unreadInboxCount: StateFlow<Int> = _inboxItems.map { list ->
        list.count { !it.isRead }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 3)

    fun markInboxItemAsRead(id: String) {
        _inboxItems.value = _inboxItems.value.map { item ->
            if (item.id == id) item.copy(isRead = true) else item
        }
    }

    fun markAllInboxAsRead() {
        _inboxItems.value = _inboxItems.value.map { it.copy(isRead = true) }
    }

    fun deleteInboxItem(id: String) {
        _inboxItems.value = _inboxItems.value.filterNot { it.id == id }
    }

    fun clearAllInbox() {
        _inboxItems.value = emptyList()
    }

    // --- MODERATION STATE ---
    private val _reportedContents = MutableStateFlow<List<com.example.ui.screens.ReportedContent>>(emptyList())
    val reportedContents: StateFlow<List<com.example.ui.screens.ReportedContent>> = _reportedContents.asStateFlow()

    fun reportContent(targetId: String, title: String, author: String, authorId: String, reason: String, type: String) {
        val newReport = com.example.ui.screens.ReportedContent(
            id = System.currentTimeMillis().toString(),
            targetTitle = title,
            targetAuthor = author,
            authorId = authorId,
            reason = reason,
            timestamp = System.currentTimeMillis(),
            itemType = type
        )
        _reportedContents.value = _reportedContents.value + newReport
    }

    fun dismissReport(reportId: String) {
        _reportedContents.value = _reportedContents.value.filterNot { it.id == reportId }
    }

    fun removeReportedContent(reportId: String) {
        // Mock remove logic
        _reportedContents.value = _reportedContents.value.filterNot { it.id == reportId }
    }

    fun suspendUser(authorId: String) {
        // Mock suspend logic
        _reportedContents.value = _reportedContents.value.filterNot { it.authorId == authorId }
    }

    // --- MONETIZATION STATE ---
    private val _isAdsEnabled = MutableStateFlow(false)
    val isAdsEnabled: StateFlow<Boolean> = _isAdsEnabled.asStateFlow()

    fun toggleAdsEnabled(enabled: Boolean) {
        _isAdsEnabled.value = enabled
    }

    // --- PERSONALIZED CURATION ENGINE ---
    private val _isForYouFeed = MutableStateFlow(false)
    val isForYouFeed: StateFlow<Boolean> = _isForYouFeed.asStateFlow()

    fun toggleForYouFeed(enabled: Boolean) {
        _isForYouFeed.value = enabled
    }

    // Combined filtered feed items
    val filteredFeedItems: StateFlow<List<MediaItemEntity>> = combine(
        allItems,
        _selectedChannelFilter,
        _selectedMediaTypeFilter,
        _searchQuery,
        _isOfflineMode
    ) { items, channelFilter, typeFilter, query, isOffline ->
        Triple(items, channelFilter, typeFilter) to Pair(query, isOffline)
    }.combine(_isForYouFeed) { state, isForYou ->
        val items = state.first.first
        val channelFilter = state.first.second
        val typeFilter = state.first.third
        val query = state.second.first
        val isOffline = state.second.second

        val candidateItems = if (isOffline) {
            items.filter { it.isSavedOffline || it.isBookmarked }
        } else {
            items
        }
        
        var filteredList = candidateItems.filter { item ->
            val matchesChannel = channelFilter == null || item.channelId == channelFilter
            val matchesType = typeFilter == null || item.type == typeFilter.name
            val matchesQuery = query.isBlank() ||
                item.title.contains(query, ignoreCase = true) ||
                item.bodyText.contains(query, ignoreCase = true) ||
                item.authorName.contains(query, ignoreCase = true) ||
                item.tags.contains(query, ignoreCase = true) ||
                item.channelName.contains(query, ignoreCase = true)
            matchesChannel && matchesType && matchesQuery
        }

        if (isForYou && channelFilter == null && query.isBlank()) {
            // Mock personalized curation algorithm
            // Boost items that are liked, bookmarked, or match a specific tier based on type
            filteredList = filteredList.sortedByDescending { item ->
                var score = 0
                if (item.isLiked) score += 50
                if (item.isBookmarked) score += 100
                score += item.likesCount
                // Tiered approach
                score += when (item.type) {
                    MediaType.PODCAST_EPISODE.name -> 30
                    MediaType.RADIO_STATION.name -> 25
                    MediaType.NEWSPAPER_MAGAZINE.name -> 20
                    MediaType.NEWSLETTER.name -> 15
                    else -> 5 // Social posts
                }
                score
            }
        }
        filteredList
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setChannelFilter(channelId: String?) {
        _selectedChannelFilter.value = if (_selectedChannelFilter.value == channelId) null else channelId
    }

    fun setStreamQuality(quality: String) {
        _tvStreamQuality.value = quality
    }

    fun setMediaTypeFilter(type: MediaType?) {
        _selectedMediaTypeFilter.value = if (_selectedMediaTypeFilter.value == type) null else type
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleLike(item: MediaItemEntity) {
        viewModelScope.launch {
            repository.toggleLike(item)
        }
    }

    fun toggleBookmark(item: MediaItemEntity) {
        viewModelScope.launch {
            repository.toggleBookmark(item)
        }
    }

    fun toggleFollowChannel(channel: MediaChannelEntity) {
        viewModelScope.launch {
            repository.toggleFollowChannel(channel)
        }
    }

    fun openReader(item: MediaItemEntity) {
        _activeReadingItem.value = item
    }

    fun closeReader() {
        _activeReadingItem.value = null
    }

    fun openMorningBrief() {
        _isMorningBriefOpen.value = true
    }

    fun closeMorningBrief() {
        _isMorningBriefOpen.value = false
    }

    fun openCreateContent() {
        _isCreateContentOpen.value = true
    }

    fun closeCreateContent() {
        _isCreateContentOpen.value = false
    }

    fun openCreateSpace() {
        _isCreateSpaceOpen.value = true
    }

    fun closeCreateSpace() {
        _isCreateSpaceOpen.value = false
    }

    fun openFullPlayer() {
        _isFullPlayerOpen.value = true
    }

    fun closeFullPlayer() {
        _isFullPlayerOpen.value = false
    }

    fun playMorningBriefAudio() {
        val briefPodcast = allItems.value.firstOrNull { it.type == MediaType.PODCAST_EPISODE.name }
            ?: allItems.value.firstOrNull { it.type == MediaType.RADIO_STATION.name }
        if (briefPodcast != null) {
            audioPlayer.play(briefPodcast)
        }
    }

    fun playAudio(item: MediaItemEntity) {
        audioPlayer.play(item)
    }

    fun togglePlayPause() {
        audioPlayer.togglePlayPause()
    }

    fun seekAudio(posMs: Long) {
        audioPlayer.seekTo(posMs)
    }

    fun setAudioSpeed(speed: Float) {
        audioPlayer.setSpeed(speed)
    }

    fun skipAudioForward() {
        audioPlayer.skipForward15()
    }

    fun skipAudioBackward() {
        audioPlayer.skipBackward15()
    }

    fun stopAudio() {
        audioPlayer.stop()
    }

    fun playRadioStation(station: MediaItemEntity) {
        audioPlayer.playRadioDirectly(station)
    }

    fun seekNextRadioStation(stations: List<MediaItemEntity>) {
        audioPlayer.seekNextStation(stations)
    }

    fun seekPreviousRadioStation(stations: List<MediaItemEntity>) {
        audioPlayer.seekPreviousStation(stations)
    }

    fun createNewContent(
        type: MediaType,
        title: String,
        subtitle: String,
        body: String,
        space: MediaSpaceEntity?,
        channel: MediaChannelEntity?,
        tags: String,
        readTimeMinutes: Int = 4,
        durationSeconds: Int = 180,
        frequency: String = ""
    ) {
        viewModelScope.launch {
            val newItem = MediaItemEntity(
                type = type.name,
                title = title.trim(),
                subtitle = subtitle.trim(),
                authorName = "Alex Chen",
                authorHandle = "@alexchen",
                channelId = channel?.id ?: "channel_tech",
                channelName = channel?.name ?: "⚡ Horizon Tech",
                spaceId = space?.id,
                spaceTitle = space?.title,
                bodyText = body.trim(),
                imageResName = when (type) {
                    MediaType.NEWSPAPER_MAGAZINE -> "img_magazine_cover"
                    MediaType.PODCAST_EPISODE -> "img_podcast_cover"
                    MediaType.RADIO_STATION -> "img_radio_live"
                    MediaType.NEWSLETTER -> "img_morning_brief"
                    MediaType.SOCIAL_POST -> ""
                    MediaType.SONG -> "img_radio_live"
                    MediaType.PLAYLIST -> "img_podcast_cover"
                },
                timestamp = System.currentTimeMillis(),
                readTimeMinutes = readTimeMinutes,
                durationSeconds = durationSeconds,
                likesCount = 1,
                commentsCount = 0,
                sharesCount = 0,
                isLiked = true,
                isUserCreated = true,
                tags = tags.trim(),
                stationFrequency = frequency.ifBlank { if (type == MediaType.RADIO_STATION) "101.4 FM Live" else "" },
                issueEdition = if (type == MediaType.NEWSLETTER) "Issue #${(1..99).random()}" else if (type == MediaType.NEWSPAPER_MAGAZINE) "Special Edition" else ""
            )
            repository.insertItem(newItem)
            _isCreateContentOpen.value = false
        }
    }

    fun createSpace(
        title: String,
        handle: String,
        description: String,
        category: String,
        accentColorHex: Long
    ) {
        viewModelScope.launch {
            repository.createSpace(
                title = title.trim(),
                handle = handle.trim(),
                description = description.trim(),
                category = category.trim(),
                accentColor = accentColorHex
            )
            _isCreateSpaceOpen.value = false
        }
    }

    // Flipbook controls
    fun openFlipbook(item: MediaItemEntity) {
        _activeFlipbookJournal.value = null
        _activeFlipbookItem.value = item
    }

    fun openFlipbook(journal: JournalEditionEntity) {
        _activeFlipbookItem.value = null
        _activeFlipbookJournal.value = journal
    }

    fun closeFlipbook() {
        _activeFlipbookItem.value = null
        _activeFlipbookJournal.value = null
    }

    // Newsstand filter controls
    fun setNewsstandFilter(filter: String) {
        _newsstandFilter.value = filter
    }

    // Journal Edition controls
    fun createJournalEdition(
        newspaperTitle: String,
        motto: String,
        volumeNumber: Int,
        issueNumber: Int,
        issueDate: String,
        templateStyle: String,
        bannerColorHex: Long,
        leadHeadline: String,
        leadSubheadline: String,
        leadArticleBody: String,
        leadAuthor: String,
        secondaryHeadline: String,
        secondaryArticleBody: String,
        editorialNotes: String,
        communityBulletin: String,
        onSuccess: (Long) -> Unit = {}
    ) {
        viewModelScope.launch {
            val edition = JournalEditionEntity(
                newspaperTitle = newspaperTitle.trim().ifEmpty { "The Townsquare Chronicle" },
                motto = motto.trim().ifEmpty { "The Voice of the Town" },
                volumeNumber = volumeNumber.coerceAtLeast(1),
                issueNumber = issueNumber.coerceAtLeast(1),
                issueDate = issueDate.trim().ifEmpty { "September 2026" },
                templateStyle = templateStyle,
                bannerColorHex = bannerColorHex,
                leadHeadline = leadHeadline.trim(),
                leadSubheadline = leadSubheadline.trim(),
                leadArticleBody = leadArticleBody.trim(),
                leadAuthor = leadAuthor.trim().ifEmpty { "Chief Editor" },
                secondaryHeadline = secondaryHeadline.trim(),
                secondaryArticleBody = secondaryArticleBody.trim(),
                editorialNotes = editorialNotes.trim(),
                communityBulletin = communityBulletin.trim(),
                circulationReads = (30..150).random(),
                isArchived = true
            )
            val newId = repository.createAndArchiveJournalEdition(edition)
            onSuccess(newId)
        }
    }

    fun deleteJournalEdition(id: Long) {
        viewModelScope.launch {
            repository.deleteJournalEdition(id)
        }
    }

    // Pull-to-refresh implementation
    fun refreshFeed(onComplete: (Int) -> Unit = {}) {
        viewModelScope.launch {
            _isRefreshingFeed.value = true
            _refreshStatusText.value = "Fetching fresh editorial dispatches from Press Wire..."
            try {
                // Simulate network latency for smooth user interaction
                delay(700)
                val newCount = repository.fetchFreshEditorialDispatches()
                _refreshStatusText.value = "Just updated • $newCount new dispatches hot off the press"
                onComplete(newCount)
            } catch (e: Exception) {
                _refreshStatusText.value = "Updated just now"
            } finally {
                _isRefreshingFeed.value = false
            }
        }
    }

    fun dismissRefreshStatus() {
        _refreshStatusText.value = null
    }

    // Calendar: Upcoming Editions
    fun toggleEditionReminder(edition: UpcomingEditionEntity) {
        viewModelScope.launch {
            repository.toggleEditionReminder(edition)
        }
    }

    fun toggleEditionSubscription(edition: UpcomingEditionEntity) {
        viewModelScope.launch {
            repository.toggleEditionSubscription(edition)
        }
    }

    fun announceUpcomingEdition(
        publicationTitle: String,
        editionType: String,
        volumeIssue: String,
        releaseDate: String,
        releaseDayLabel: String,
        releaseTime: String,
        coverHeadline: String,
        leadTeaser: String,
        editorNotes: String,
        bannerColorHex: Long = 0xFF00D2FF,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val edition = UpcomingEditionEntity(
                id = "up_custom_${System.currentTimeMillis()}",
                publicationTitle = publicationTitle.trim().ifEmpty { "Townsquare Gazette" },
                channelId = "channel_culture",
                editionType = editionType,
                volumeIssue = volumeIssue.trim().ifEmpty { "Vol. 1, Issue 1" },
                releaseDate = releaseDate.trim().ifEmpty { "2026-09-15" },
                releaseDayLabel = releaseDayLabel.trim().ifEmpty { "Upcoming Dispatch" },
                releaseTime = releaseTime.trim().ifEmpty { "07:00 AM" },
                coverHeadline = coverHeadline.trim().ifEmpty { "Upcoming Community Dispatch" },
                leadTeaser = leadTeaser.trim().ifEmpty { "Special edition preview." },
                editorNotes = editorNotes.trim().ifEmpty { "Published via Townsquare Press Guild." },
                bannerColorHex = bannerColorHex,
                isSubscribed = true,
                isReminderSet = true,
                specialSection = "Citizen Press Drop"
            )
            repository.announceUpcomingEdition(edition)
            onSuccess()
        }
    }

    // Map: Retail Kiosks & Press Stores
    fun reserveKioskCopy(kiosk: RetailKioskEntity, copies: Int = 1, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.reservePhysicalCopy(kiosk, copies)
            onSuccess()
        }
    }

    fun toggleKioskFavorite(kiosk: RetailKioskEntity) {
        viewModelScope.launch {
            repository.toggleKioskFavorite(kiosk)
        }
    }

    // Voice Narration
    fun startVoiceNarration(item: MediaItemEntity, style: NarrationStyle? = null) {
        if (audioPlayer.audioState.value.isPlaying) {
            audioPlayer.pause()
        }
        voiceNarrator.startNarration(item, style ?: when (item.type) {
            MediaType.NEWSPAPER_MAGAZINE.name -> NarrationStyle.LITERARY_MAGAZINE
            MediaType.NEWSLETTER.name -> NarrationStyle.PODCAST
            else -> if (item.tags.contains("breaking", ignoreCase = true)) NarrationStyle.BREAKING_NEWS else NarrationStyle.PODCAST
        })
    }

    fun startVoiceNarrationForJournal(journal: JournalEditionEntity, style: NarrationStyle? = null) {
        if (audioPlayer.audioState.value.isPlaying) {
            audioPlayer.pause()
        }
        voiceNarrator.startNarrationForJournal(journal, style ?: NarrationStyle.LITERARY_MAGAZINE)
    }

    fun startVoiceNarrationText(title: String, text: String, author: String = "Townsquare Press", style: NarrationStyle = NarrationStyle.BREAKING_NEWS) {
        if (audioPlayer.audioState.value.isPlaying) {
            audioPlayer.pause()
        }
        voiceNarrator.startNarrationText(title, text, author, style)
    }

    fun toggleVoiceNarrationPlayPause() {
        voiceNarrator.togglePlayPause()
    }

    fun stopVoiceNarration() {
        voiceNarrator.stop()
    }

    fun nextNarrationParagraph() {
        voiceNarrator.nextParagraph()
    }

    fun previousNarrationParagraph() {
        voiceNarrator.previousParagraph()
    }

    fun setNarrationStyle(style: NarrationStyle) {
        voiceNarrator.setStyle(style)
    }

    fun setNarrationSpeed(speed: Float) {
        voiceNarrator.setSpeed(speed)
    }

    // Offline Mode & Saved Content
    fun toggleOfflineMode() {
        _isOfflineMode.value = !_isOfflineMode.value
    }

    fun setOfflineMode(enabled: Boolean) {
        _isOfflineMode.value = enabled
    }

    fun toggleSavedOffline(item: MediaItemEntity) {
        viewModelScope.launch {
            repository.toggleSavedOffline(item)
        }
    }

    // Sharing tracker
    fun recordShare(item: MediaItemEntity) {
        viewModelScope.launch {
            repository.incrementShare(item.id)
        }
    }

    // App Settings Actions
    fun updateAudioQuality(quality: AudioStreamingQuality) {
        settingsManager.setAudioQuality(quality)
    }

    fun updateReadingFontSize(size: ReadingFontSize) {
        settingsManager.setReadingFontSize(size)
    }

    fun updateDialHapticFeedback(enabled: Boolean) {
        settingsManager.setDialHapticFeedback(enabled)
    }

    fun updateAutoTuneLastStation(enabled: Boolean) {
        settingsManager.setAutoTuneLastStation(enabled)
    }

    fun updateAutoCacheMorningEdition(enabled: Boolean) {
        settingsManager.setAutoCacheMorningEdition(enabled)
    }

    fun updateBreakingNewsAlerts(enabled: Boolean) {
        settingsManager.setBreakingNewsAlerts(enabled)
    }

    fun updatePrintKioskAlerts(enabled: Boolean) {
        settingsManager.setPrintKioskAlerts(enabled)
    }

    fun updateVoiceNarrationPreset(preset: VoiceNarrationPreset) {
        settingsManager.setVoiceNarrationPreset(preset)
    }

    fun updateVoiceNarrationSpeed(speed: Float) {
        settingsManager.setVoiceNarrationSpeed(speed)
        voiceNarrator.setSpeed(speed)
    }

    fun updateCommunityRadius(miles: Int) {
        settingsManager.setCommunityRadius(miles)
    }

    fun updateMorningDispatchTime(time: String) {
        settingsManager.setMorningDispatchTime(time)
    }

    fun updateHighContrastDisplay(enabled: Boolean) {
        settingsManager.setHighContrastDisplay(enabled)
    }

    fun clearMediaCache() {
        settingsManager.clearMediaCache()
    }

    fun resetSettingsToDefaults() {
        settingsManager.resetToDefaults()
    }

    // Local News Bulletins methods
    fun selectBulletinCategory(category: String?) {
        _selectedBulletinCategory.value = category
    }

    fun setReportBulletinDialogVisible(visible: Boolean) {
        _isReportBulletinDialogVisible.value = visible
    }

    fun reportLocalBulletin(
        category: String,
        title: String,
        description: String,
        locationName: String,
        urgencyLevel: String,
        iconEmoji: String,
        reporterName: String = "Citizen Reporter",
        reporterHandle: String = "@town_reporter",
        onSuccess: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            repository.reportBulletin(
                category = category,
                title = title,
                description = description,
                locationName = locationName,
                urgencyLevel = urgencyLevel,
                iconEmoji = iconEmoji,
                reporterName = reporterName.ifBlank { "Citizen Reporter" },
                reporterHandle = reporterHandle.ifBlank { "@town_reporter" }
            )
            onSuccess?.invoke()
        }
    }

    fun toggleBulletinUpvote(bulletin: LocalBulletinEntity) {
        viewModelScope.launch {
            repository.toggleBulletinUpvote(bulletin)
        }
    }

    fun deleteBulletin(id: Long) {
        viewModelScope.launch {
            repository.deleteBulletin(id)
        }
    }

    // TV & Streaming Roster Controls
    fun selectTvChannel(channelId: String) {
        _activeTvChannelId.value = channelId
    }

    fun tuneChannelByNumber(channelNumber: Int) {
        val channels = allTvChannels.value
        channels.find { it.channelNumber == channelNumber }?.let {
            _activeTvChannelId.value = it.id
        }
    }

    fun nextTvChannel() {
        val channels = allTvChannels.value
        if (channels.isEmpty()) return
        val currentIndex = channels.indexOfFirst { it.id == _activeTvChannelId.value }
        val nextIndex = if (currentIndex < 0 || currentIndex == channels.lastIndex) 0 else currentIndex + 1
        _activeTvChannelId.value = channels[nextIndex].id
    }

    fun previousTvChannel() {
        val channels = allTvChannels.value
        if (channels.isEmpty()) return
        val currentIndex = channels.indexOfFirst { it.id == _activeTvChannelId.value }
        val prevIndex = if (currentIndex <= 0) channels.lastIndex else currentIndex - 1
        _activeTvChannelId.value = channels[prevIndex].id
    }

    fun toggleTvPlayPause() {
        _isTvPlaying.value = !_isTvPlaying.value
    }

    fun toggleTvMute() {
        _isTvMuted.value = !_isTvMuted.value
    }

    fun toggleTvFullscreen() {
        _isTvFullscreen.value = !_isTvFullscreen.value
    }

    fun setTvFullscreen(isFullscreen: Boolean) {
        _isTvFullscreen.value = isFullscreen
    }

    fun toggleTvCaptions() {
        _isTvCaptionsEnabled.value = !_isTvCaptionsEnabled.value
    }

    fun setTvStreamQuality(quality: String) {
        _tvStreamQuality.value = quality
    }

    fun setTvTab(tab: String) {
        _tvSelectedTab.value = tab
    }

    fun toggleTvScanlineFx() {
        _tvScanlineFxEnabled.value = !_tvScanlineFxEnabled.value
    }

    fun toggleTvFavorite(channel: TvChannelEntity) {
        viewModelScope.launch {
            repository.toggleTvFavorite(channel)
        }
    }

    fun toggleTvReminder(channel: TvChannelEntity) {
        viewModelScope.launch {
            repository.toggleTvReminder(channel)
        }
    }

    fun toggleTvRecording(channel: TvChannelEntity) {
        viewModelScope.launch {
            repository.toggleTvRecording(channel)
        }
    }

    // 24/7 Live Newsblog Actions
    fun selectNewsblogCategory(category: String) {
        _selectedNewsblogCategory.value = category
    }

    fun toggleNewsblogLike(entry: LiveNewsblogEntity) {
        viewModelScope.launch {
            repository.toggleNewsblogLike(entry)
        }
    }

    fun toggleNewsblogBookmark(entry: LiveNewsblogEntity) {
        viewModelScope.launch {
            repository.toggleNewsblogBookmark(entry)
        }
    }

    fun addNewsblogEntry(
        headline: String,
        body: String,
        authorName: String,
        authorRole: String,
        categoryTag: String,
        urgencyLevel: String,
        location: String,
        keyTakeaway: String,
        quote: String,
        quoteSpeaker: String,
        onSuccess: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            repository.addNewsblogEntry(
                headline = headline,
                body = body,
                authorName = authorName,
                authorRole = authorRole,
                categoryTag = categoryTag,
                urgencyLevel = urgencyLevel,
                location = location,
                keyTakeaway = keyTakeaway,
                quote = quote,
                quoteSpeaker = quoteSpeaker
            )
            onSuccess?.invoke()
        }
    }

    fun simulateLiveNewsblogTick(onDispatched: ((LiveNewsblogEntity) -> Unit)? = null) {
        viewModelScope.launch {
            _isSimulatingNewsblogUpdate.value = true
            delay(600)
            val newEntry = repository.simulateIncomingLiveNewsblogDispatch()
            _isSimulatingNewsblogUpdate.value = false
            onDispatched?.invoke(newEntry)
        }
    }

    fun playNewsblogAudio(entry: LiveNewsblogEntity) {
        if (_newsblogAudioPlayingId.value == entry.id) {
            _newsblogAudioPlayingId.value = null
            voiceNarrator.stop()
        } else {
            _newsblogAudioPlayingId.value = entry.id
            val fullText = listOf(
                entry.headline,
                "Dispatched by ${entry.authorName}, ${entry.authorRole} at ${entry.timestampFormatted}.",
                entry.body,
                if (entry.keyTakeaway.isNotEmpty()) "Key takeaway: ${entry.keyTakeaway}" else "",
                if (entry.quote.isNotEmpty()) "Quote: ${entry.quote} by ${entry.quoteSpeaker}" else ""
            ).filter { it.isNotBlank() }.joinToString("\n\n")

            voiceNarrator.startNarrationText(
                title = entry.headline,
                text = fullText,
                author = entry.authorName,
                style = NarrationStyle.BREAKING_NEWS
            )
        }
    }

    fun stopNewsblogAudio() {
        _newsblogAudioPlayingId.value = null
        voiceNarrator.stop()
    }

    // Multimedia Notepad (Journal Drafts) Actions
    fun selectDraftForEdit(draft: NotepadDraftEntity?) {
        _activeNotepadDraft.value = draft
    }

    fun setDraftFilter(filter: String) {
        _selectedDraftFilter.value = filter
    }

    fun saveNotepadDraft(draft: NotepadDraftEntity, onSuccess: ((Long) -> Unit)? = null) {
        viewModelScope.launch {
            val id = repository.saveOrUpdateNotepadDraft(draft)
            _activeNotepadDraft.value = null
            onSuccess?.invoke(id)
        }
    }

    fun toggleNotepadDraftStarred(draft: NotepadDraftEntity) {
        viewModelScope.launch {
            repository.toggleNotepadDraftStarred(draft)
        }
    }

    fun deleteNotepadDraft(id: Long) {
        viewModelScope.launch {
            repository.deleteNotepadDraft(id)
            if (_activeNotepadDraft.value?.id == id) {
                _activeNotepadDraft.value = null
            }
        }
    }

    fun convertDraftToJournalEdition(draft: NotepadDraftEntity, onConverted: ((Long) -> Unit)? = null) {
        viewModelScope.launch {
            val newJournalId = repository.convertDraftToJournalEdition(draft)
            onConverted?.invoke(newJournalId)
        }
    }

    // Visual & Photojournalism Feed Actions
    fun selectVisualCategory(category: String) {
        _selectedVisualCategory.value = category
    }

    fun toggleVisualPostLike(post: VisualPostEntity) {
        viewModelScope.launch {
            repository.toggleVisualPostLike(post)
        }
    }

    fun toggleVisualPostBookmark(post: VisualPostEntity) {
        viewModelScope.launch {
            repository.toggleVisualPostBookmark(post)
        }
    }

    fun openVisualLightbox(post: VisualPostEntity) {
        _activeLightboxPost.value = post
    }

    fun closeVisualLightbox() {
        _activeLightboxPost.value = null
    }

    fun addVisualPost(
        title: String,
        caption: String,
        photographerName: String,
        category: String,
        imageResUrl: String,
        locationTaken: String,
        cameraMeta: String,
        storyContext: String,
        onSuccess: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            repository.addVisualPost(
                title = title,
                caption = caption,
                photographerName = photographerName,
                category = category,
                imageResUrl = imageResUrl,
                locationTaken = locationTaken,
                cameraMeta = cameraMeta,
                storyContext = storyContext
            )
            onSuccess?.invoke()
        }
    }

    // Sidebar Drawer Actions
    fun openSidebar() {
        _isSidebarOpen.value = true
    }

    fun closeSidebar() {
        _isSidebarOpen.value = false
    }

    fun toggleSidebar() {
        _isSidebarOpen.value = !_isSidebarOpen.value
    }

    // Fact Check logic
    fun runFactCheckForMediaItem(item: MediaItemEntity) {
        val claims = listOf(
            FactCheckClaim(
                id = "c1",
                claimText = "City Council approved $45M budget expansion for electric bus fleet in Q3.",
                isFactual = true,
                confidenceScore = 98,
                sourceCitation = "City Council Minutes 2026-08, Ordinance #402",
                verdictSummary = "Verified by official municipal public record.",
                detailedExplanation = "Resolution passed on August 14 with 7-2 vote."
            ),
            FactCheckClaim(
                id = "c2",
                claimText = "Ridership increased by 450% within 30 days of fareless weekend trial.",
                isFactual = false,
                misinformationType = MisinformationType.EXAGGERATED_CLAIM,
                confidenceScore = 94,
                sourceCitation = "Regional Transit Authority Passenger Data 2026",
                verdictSummary = "The actual measured ridership growth was 45%, not 450%.",
                detailedExplanation = "Initial news reports miscalculated the baseline percentage.",
                correctionOrContext = "Ridership rose from 12,000 to 17,400 daily passengers (+45%)."
            ),
            FactCheckClaim(
                id = "c3",
                claimText = "Old central depot facility was built in 1892 during the railway expansion.",
                isFactual = false,
                misinformationType = MisinformationType.OUTDATED_STATISTIC,
                confidenceScore = 91,
                sourceCitation = "State Historical Preservation Registry",
                verdictSummary = "Depot construction began in 1904 following the 1902 municipal fire.",
                detailedExplanation = "1892 refers to the original wooden shed predecessor.",
                correctionOrContext = "Built in 1904, renovated in 1978 and 2022."
            ),
            FactCheckClaim(
                id = "c4",
                claimText = "All solar arrays installed on transit roofs generate 2.4 MegaWatts of peak power.",
                isFactual = true,
                confidenceScore = 96,
                sourceCitation = "Department of Energy Microgrid Telemetry",
                verdictSummary = "Confirmed via real-time grid meters.",
                detailedExplanation = "Grid telemetric data verifies sustained peak output during noon hours."
            )
        )

        val factualCount = claims.count { it.isFactual }
        val misinfoCount = claims.count { !it.isFactual }
        val total = claims.size
        val acc = (factualCount * 100) / total

        _activeFactCheckReport.value = FactCheckReport(
            targetId = item.id.toString(),
            targetTitle = item.title,
            targetAuthor = item.authorName,
            checkedAtFormatted = "Today at 02:37 PM",
            totalClaimsCount = total,
            factualClaimsCount = factualCount,
            misinformationClaimsCount = misinfoCount,
            accuracyPercentage = acc,
            claims = claims
        )
    }

    fun runFactCheckForNewsblog(entry: LiveNewsblogEntity) {
        val claims = listOf(
            FactCheckClaim(
                id = "nc1",
                claimText = entry.headline,
                isFactual = true,
                confidenceScore = 99,
                sourceCitation = "Wire Service Dispatch #8821",
                verdictSummary = "Verified breaking dispatch from accredited press bureau.",
                detailedExplanation = "Directly witnessed and verified by 2 on-scene reporters."
            ),
            FactCheckClaim(
                id = "nc2",
                claimText = if (entry.keyTakeaway.isNotBlank()) entry.keyTakeaway else "Location dispatches confirmed by emergency broadcast network.",
                isFactual = true,
                confidenceScore = 95,
                sourceCitation = "Civic Emergency Management Log",
                verdictSummary = "Key takeaway confirmed.",
                detailedExplanation = "Matches official bulletin release."
            ),
            FactCheckClaim(
                id = "nc3",
                claimText = "Initial social media claims indicated total grid blackout across 5 counties.",
                isFactual = false,
                misinformationType = MisinformationType.MISLEADING_CONTEXT,
                confidenceScore = 92,
                sourceCitation = "Regional Power Utility Grid Status Report",
                verdictSummary = "Blackout was isolated to 2 substations in Sector 4.",
                detailedExplanation = "Unverified online rumors exaggerated geographic scale.",
                correctionOrContext = "Power restored within 42 minutes to 98% of customers."
            )
        )

        val factualCount = claims.count { it.isFactual }
        val misinfoCount = claims.count { !it.isFactual }
        val total = claims.size
        val acc = (factualCount * 100) / total

        _activeFactCheckReport.value = FactCheckReport(
            targetId = entry.id.toString(),
            targetTitle = entry.headline,
            targetAuthor = entry.authorName,
            checkedAtFormatted = entry.timestampFormatted,
            totalClaimsCount = total,
            factualClaimsCount = factualCount,
            misinformationClaimsCount = misinfoCount,
            accuracyPercentage = acc,
            claims = claims
        )
    }

    fun closeFactCheckReport() {
        _activeFactCheckReport.value = null
    }

    // Letters to Editor methods
    fun openSendLetterDialog(recipientId: String, recipientName: String) {
        _activeLetterRecipient.value = Pair(recipientId, recipientName)
    }

    fun closeSendLetterDialog() {
        _activeLetterRecipient.value = null
    }

    fun openLettersMailbox() {
        _isLettersMailboxOpen.value = true
    }

    fun closeLettersMailbox() {
        _isLettersMailboxOpen.value = false
    }

    fun sendLetterToEditor(
        recipientId: String,
        recipientName: String,
        recipientType: String,
        senderName: String,
        senderEmail: String,
        subject: String,
        messageBody: String,
        categoryTag: String,
        onSuccess: (() -> Unit)? = null
    ) {
        val newLetter = LetterToEditor(
            id = "letter_${System.currentTimeMillis()}",
            recipientId = recipientId,
            recipientName = recipientName,
            recipientType = recipientType,
            senderName = senderName,
            senderEmail = senderEmail,
            subject = subject,
            messageBody = messageBody,
            categoryTag = categoryTag,
            timestampFormatted = "Just Now",
            status = "Sent to Desk",
            editorReply = "Received by $recipientName desk! The editorial board will review your dispatch.",
            editorReplyTimestamp = "Moments Ago"
        )
        _lettersToEditor.value = listOf(newLetter) + _lettersToEditor.value
        _activeLetterRecipient.value = null
        onSuccess?.invoke()
    }

    fun toggleLetterStarred(letterId: String) {
        _lettersToEditor.value = _lettersToEditor.value.map {
            if (it.id == letterId) it.copy(isStarred = !it.isStarred) else it
        }
    }

    // Sunday Funnies methods
    fun selectFunniesCategory(category: String) {
        _selectedFunniesCategory.value = category
    }

    fun toggleFunniesLike(id: String) {
        _sundayFunniesComics.value = _sundayFunniesComics.value.map {
            if (it.id == id) {
                val newLiked = !it.isLiked
                it.copy(
                    isLiked = newLiked,
                    laughsCount = if (newLiked) it.laughsCount + 1 else it.laughsCount - 1
                )
            } else it
        }
    }

    fun toggleFunniesBookmark(id: String) {
        _sundayFunniesComics.value = _sundayFunniesComics.value.map {
            if (it.id == id) it.copy(isBookmarked = !it.isBookmarked) else it
        }
    }

    fun addFunniesReaction(id: String, reactionType: String) {
        _sundayFunniesComics.value = _sundayFunniesComics.value.map { comic ->
            if (comic.id == id) {
                when (reactionType) {
                    "LAUGH" -> comic.copy(laughsCount = comic.laughsCount + 1)
                    "SPOT_ON" -> comic.copy(spotOnCount = comic.spotOnCount + 1)
                    "CLASSIC" -> comic.copy(classicCount = comic.classicCount + 1)
                    else -> comic
                }
            } else comic
        }
    }

    fun addComicOrMeme(
        title: String,
        series: String,
        cartoonist: String,
        url: String,
        caption: String,
        isMeme: Boolean,
        onSuccess: (() -> Unit)? = null
    ) {
        val newComic = ComicStripItem(
            id = "comic_${System.currentTimeMillis()}",
            title = title,
            syndicateSeries = series,
            cartoonistName = cartoonist,
            publishDateFormatted = "Just Submitted",
            imageUrl = url,
            caption = caption,
            issueNumber = (100..999).random(),
            isUserSubmittedMeme = isMeme
        )
        _sundayFunniesComics.value = listOf(newComic) + _sundayFunniesComics.value
        onSuccess?.invoke()
    }

    // AI-Powered Feature Toggles (Settings)
    fun updateEnableAiFeatures(enabled: Boolean) {
        settingsManager.setEnableAiFeatures(enabled)
    }

    fun updateEnableAiFactChecking(enabled: Boolean) {
        settingsManager.setEnableAiFactChecking(enabled)
    }

    fun updateEnableAiVoiceNarration(enabled: Boolean) {
        settingsManager.setEnableAiVoiceNarration(enabled)
    }

    fun updateEnableAiSmartSummaries(enabled: Boolean) {
        settingsManager.setEnableAiSmartSummaries(enabled)
    }

    // Skin Extension Settings & Custom Extensions Studio
    private val _customExtensions = MutableStateFlow<List<com.example.data.model.CustomExtensionManifest>>(
        listOf(
            com.example.data.model.CustomExtensionManifest(
                id = "cyber_pulse_2099",
                name = "CyberPulse 2099",
                author = "NeonStudio",
                version = "1.2",
                description = "Futuristic retrowave interface with neon cyan accents, wire audio ticker, and CRT scanlines.",
                primaryColorHex = "#00D2FF",
                secondaryColorHex = "#FF007F",
                backgroundColorHex = "#080B10",
                surfaceColorHex = "#121926",
                fontStyle = "MONOSPACE",
                layoutType = "TERMINAL",
                enableSoundFx = true,
                enableCrtScanlines = true,
                enableTopTicker = true
            )
        )
    )
    val customExtensions: StateFlow<List<com.example.data.model.CustomExtensionManifest>> = _customExtensions.asStateFlow()

    fun addCustomExtension(extension: com.example.data.model.CustomExtensionManifest) {
        _customExtensions.value = _customExtensions.value + extension
    }

    fun removeCustomExtension(id: String) {
        _customExtensions.value = _customExtensions.value.filterNot { it.id == id }
    }

    fun updateActiveAppSkinId(skinId: String?) {
        settingsManager.setActiveAppSkinId(skinId)
    }

    fun updateActiveWelcomeSkinId(skinId: String) {
        settingsManager.setActiveWelcomeSkinId(skinId)
    }

    fun updateOverrideBaseAppInterface(override: Boolean) {
        settingsManager.setOverrideBaseAppInterface(override)
    }

    fun updateEnableRetroTerminalMode(enabled: Boolean) {
        settingsManager.setEnableRetroTerminalMode(enabled)
    }

    fun updateEnableKeitai3GOverlay(enabled: Boolean) {
        settingsManager.setEnableKeitai3GOverlay(enabled)
    }

    fun updateEnableManuscriptParchmentTheme(enabled: Boolean) {
        settingsManager.setEnableManuscriptParchmentTheme(enabled)
    }

    fun updateEnableMetroTilesView(enabled: Boolean) {
        settingsManager.setEnableMetroTilesView(enabled)
    }

    fun updateEnableGeekLiveTickerHeader(enabled: Boolean) {
        settingsManager.setEnableGeekLiveTickerHeader(enabled)
    }

    // Partner Syndicate Methods
    fun openPartnerApplicationDialog() {
        _isPartnerApplicationOpen.value = true
    }

    fun closePartnerApplicationDialog() {
        _isPartnerApplicationOpen.value = false
    }

    fun submitPartnerApplication(
        publicationName: String,
        applicantName: String,
        contactEmail: String,
        mediumType: String,
        region: String,
        circulation: String,
        editorialCharter: String,
        sampleUrl: String,
        onSuccess: (() -> Unit)? = null
    ) {
        val application = PartnerApplication(
            id = "app_${System.currentTimeMillis()}",
            publicationName = publicationName,
            applicantName = applicantName,
            contactEmail = contactEmail,
            mediumType = mediumType,
            region = region,
            circulation = circulation,
            pitchAndCharter = editorialCharter,
            sampleUrlOrRss = sampleUrl,
            submittedAt = "Today at 08:00 AM",
            status = "UNDER_EDITORIAL_REVIEW"
        )
        _partnerApplications.value = listOf(application) + _partnerApplications.value
        onSuccess?.invoke()
    }

    // Facsimile Broadsheet Methods
    fun openFacsimileDialog(dispatch: FacsimileBroadsheet? = null) {
        _activeFacsimileDispatch.value = dispatch ?: FacsimileBroadsheetRepository.getEditionForHour(8)
        _isFacsimileOpen.value = true
    }

    fun closeFacsimileDialog() {
        _isFacsimileOpen.value = false
        _activeFacsimileDispatch.value = null
    }

    fun saveFacsimileSlipToDrafts(dispatch: FacsimileBroadsheet, onSaved: () -> Unit) {
        viewModelScope.launch {
            val draft = NotepadDraftEntity(
                title = "📠 [FACSIMILE] ${dispatch.headline}",
                bodyText = "========================================\n" +
                        "TOWNSQUARE HOURLY FACSIMILE WIRE SLIP\n" +
                        "${dispatch.editionCode} • ${dispatch.timestampFormatted}\n" +
                        "CARRIER: ${dispatch.carrierSignalKhz}\n" +
                        "BARO: ${dispatch.barometer} • TEMP: ${dispatch.temperature}\n" +
                        "========================================\n\n" +
                        "${dispatch.headline}\n" +
                        "${dispatch.subheadline}\n\n" +
                        "BULLETIN ITEMS:\n" +
                        dispatch.items.joinToString("\n") { "• [${it.timeTag}] ${it.category}: ${it.content}" } +
                        "\n\n${dispatch.editorialNote}\n" +
                        "[TELEX VERIFIED • TOWNSQUARE DISPATCH DESK]",
                draftType = "FIELD_REPORT"
            )
            repository.saveOrUpdateNotepadDraft(draft)
            onSaved()
        }
    }

    fun refreshData(onRefreshed: () -> Unit = {}) {
        viewModelScope.launch {
            delay(500L)
            onRefreshed()
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceNarrator.shutdown()
        audioPlayer.stop()
    }
}
