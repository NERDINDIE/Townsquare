package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.TextButton
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Feed
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DynamicFeed
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Stream
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import com.example.ui.components.BrowserMultitaskingBar
import com.example.ui.components.BrowserWorkspaceTab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.MediaChannelEntity
import com.example.data.model.MediaItemEntity
import com.example.ui.AppTab
import com.example.ui.NavDestination
import com.example.ui.components.ArticleReaderDialog
import com.example.ui.components.CreateContentDialog
import com.example.ui.components.CreateSpaceDialog
import com.example.ui.components.FlipbookReaderDialog
import com.example.ui.components.FullAudioPlayerSheet
import com.example.ui.components.FullMorningBriefDialog
import com.example.ui.components.GlobalMediaPlayer
import com.example.ui.components.PartnerApplicationDialog
import com.example.ui.components.TeletextDialog
import com.example.ui.components.FacsimileBroadsheetDialog
import com.example.ui.components.LocalWeatherForecastDialog
import com.example.ui.components.ReportBulletinDialog
import com.example.ui.components.TownsquareInboxDialog
import com.example.ui.components.AIVoiceBuilderDialog
import com.example.ui.components.EmergencyAlertsHubDialog
import com.example.ui.components.TownsquareSidebarDrawer
import com.example.ui.components.TownsquareTopBar
import com.example.ui.components.MultitaskingBrowserDialog
import com.example.ui.components.WebTabItem
import com.example.ui.screens.BrowserScreen
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Person
import com.example.ui.screens.OpeningBroadsheetFlow
import com.example.ui.screens.BroadsheetOpeningScreen
import com.example.ui.screens.AudioHubScreen
import com.example.ui.screens.EngagementDashboardScreen
import com.example.ui.screens.ChannelProfileScreen
import com.example.ui.screens.ChannelsScreen
import com.example.ui.screens.JournalScreen
import com.example.ui.screens.MainFeedScreen
import com.example.ui.screens.NewsblogScreen
import com.example.ui.screens.NewsstandScreen
import com.example.ui.screens.PartnerPublicationsScreen
import com.example.ui.screens.ProfileSpacesScreen
import com.example.ui.screens.ProfileSwitcherScreen
import com.example.ui.screens.TvStreamingScreen
import com.example.ui.screens.VisualGalleryScreen
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TownsquareTheme
import com.example.ui.theme.WarmAmber
import com.example.ui.viewmodel.MediaSuperappViewModel
import com.example.ui.plus.extensions.*
import com.example.util.ShareHelper
import kotlinx.coroutines.launch

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding

class MainActivity : ComponentActivity() {
    companion object {
        var isTvPlaying: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MediaSuperappViewModel = viewModel()
            val themeConfig by viewModel.currentTheme.collectAsState()
            TownsquareTheme(themeConfig = themeConfig) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TownsquareApp(viewModel = viewModel)
                }
            }
        }
    }

    override fun onUserLeaveHint() {
        if (isTvPlaying) {
            enterPictureInPictureMode(android.app.PictureInPictureParams.Builder().build())
        }
        super.onUserLeaveHint()
    }
}

@Composable
fun TownsquareApp(
    viewModel: MediaSuperappViewModel = viewModel()
) {
    val selectedProfileType by viewModel.selectedProfileType.collectAsState()

    if (selectedProfileType == null) {
        ProfileSwitcherScreen(onSelectProfile = { viewModel.selectProfile(it) })
        return
    }

    var currentNavIndex by remember(selectedProfileType) {
        mutableIntStateOf(
            if (selectedProfileType == "PLAYGROUND") NavDestination.PLAYGROUND else NavDestination.FEED
        )
    }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val tabTitles = listOf("Feed", "Browser", "Wire", "Gallery", "TV", "Newsstand", "Journal", "Audio", "Spaces", "Funnies", "Partners", "Discovery", "Community", "Playground")
    val tabIcons = listOf(
        Icons.AutoMirrored.Filled.Feed,
        Icons.Default.Public,
        Icons.Default.DynamicFeed,
        Icons.Default.Widgets,
        Icons.Default.LiveTv,
        Icons.Default.MenuBook,
        Icons.Default.HistoryEdu,
        Icons.Default.Radio,
        Icons.Default.Hub,
        Icons.Default.Tv,
        Icons.Default.Newspaper,
        Icons.Default.Explore,
        Icons.Default.Person,
        Icons.Default.Build
    )

    // State collected from ViewModel
    val feedItems by viewModel.filteredFeedItems.collectAsState()
    val allItems by viewModel.allItems.collectAsState()
    val allChannels by viewModel.allChannels.collectAsState()
    val userSpaces by viewModel.userSpaces.collectAsState()
    val userCreatedItems by viewModel.userCreatedItems.collectAsState()
    val bookmarkedItems by viewModel.bookmarkedItems.collectAsState()

    val journalEditions by viewModel.allJournalEditions.collectAsState()
    val upcomingEditions by viewModel.allUpcomingEditions.collectAsState()
    val retailKiosks by viewModel.allRetailKiosks.collectAsState()
    val bulletins by viewModel.allBulletins.collectAsState()
    val allTvChannels by viewModel.allTvChannels.collectAsState()
    val activeTvChannelId by viewModel.activeTvChannelId.collectAsState()
    val isTvPlaying by viewModel.isTvPlaying.collectAsState()
    
    androidx.compose.runtime.LaunchedEffect(isTvPlaying) {
        MainActivity.isTvPlaying = isTvPlaying
    }
    
    val isTvMuted by viewModel.isTvMuted.collectAsState()
    val isTvFullscreen by viewModel.isTvFullscreen.collectAsState()
    val isTvCaptionsEnabled by viewModel.isTvCaptionsEnabled.collectAsState()
    val tvStreamQuality by viewModel.tvStreamQuality.collectAsState()
    val tvSelectedTab by viewModel.tvSelectedTab.collectAsState()
    val tvScanlineFxEnabled by viewModel.tvScanlineFxEnabled.collectAsState()
    val tvChatMessages by viewModel.tvChatMessages.collectAsState()
    val selectedBulletinCategory by viewModel.selectedBulletinCategory.collectAsState()
    val isReportBulletinDialogVisible by viewModel.isReportBulletinDialogVisible.collectAsState()
    val isRefreshingFeed by viewModel.isRefreshingFeed.collectAsState()
    val refreshStatusText by viewModel.refreshStatusText.collectAsState()
    val activeFlipbookItem by viewModel.activeFlipbookItem.collectAsState()
    val activeFlipbookJournal by viewModel.activeFlipbookJournal.collectAsState()
    val newsstandFilter by viewModel.newsstandFilter.collectAsState()

    val isSidebarOpen by viewModel.isSidebarOpen.collectAsState()
    val newsblogEntries by viewModel.filteredNewsblogEntries.collectAsState()
    val selectedNewsblogCategory by viewModel.selectedNewsblogCategory.collectAsState()
    val newsblogAudioPlayingId by viewModel.newsblogAudioPlayingId.collectAsState()
    val isSimulatingNewsblogUpdate by viewModel.isSimulatingNewsblogUpdate.collectAsState()
    val visualPosts by viewModel.filteredVisualPosts.collectAsState()
    val selectedVisualCategory by viewModel.selectedVisualCategory.collectAsState()
    val activeLightboxPost by viewModel.activeLightboxPost.collectAsState()
    val notepadDrafts by viewModel.filteredNotepadDrafts.collectAsState()
    val selectedDraftFilter by viewModel.selectedDraftFilter.collectAsState()

    val activeFactCheckReport by viewModel.activeFactCheckReport.collectAsState()
    val lettersToEditor by viewModel.lettersToEditor.collectAsState()
    val activeLetterRecipient by viewModel.activeLetterRecipient.collectAsState()
    val isLettersMailboxOpen by viewModel.isLettersMailboxOpen.collectAsState()
    val sundayFunniesComics by viewModel.filteredSundayFunnies.collectAsState()
    val selectedFunniesCategory by viewModel.selectedFunniesCategory.collectAsState()

    val partnerPublications by viewModel.partnerPublications.collectAsState()
    val partnerApplications by viewModel.partnerApplications.collectAsState()
    val isPartnerApplicationOpen by viewModel.isPartnerApplicationOpen.collectAsState()
    val isFacsimileOpen by viewModel.isFacsimileOpen.collectAsState()

    val selectedChannelFilter by viewModel.selectedChannelFilter.collectAsState()
    val selectedMediaTypeFilter by viewModel.selectedMediaTypeFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val audioState by viewModel.audioState.collectAsState()
    val narrationState by viewModel.narrationState.collectAsState()
    val isOfflineMode by viewModel.isOfflineMode.collectAsState()
    val appSettings by viewModel.appSettings.collectAsState()
    val customExtensions by viewModel.customExtensions.collectAsState()
    val currentTheme by viewModel.currentTheme.collectAsState()
    val isDynamicTheme by viewModel.isDynamicTheme.collectAsState()
    val context = LocalContext.current

    var isTeletextOpen by remember { mutableStateOf(false) }
    var hasAutoTriggeredTeletextForOffline by remember { mutableStateOf(false) }

    var showOpeningFlow by rememberSaveable { mutableStateOf(true) }
    var isBroadsheetViewOpen by remember { mutableStateOf(false) }

    var isMultitaskingBrowserOpen by remember { mutableStateOf(false) }
    val initialTab = remember { WebTabItem(url = "https://news.google.com", title = "Townsquare Press Wire") }
    var browserTabs by remember { mutableStateOf(listOf(initialTab)) }
    var activeBrowserTabId by remember { mutableStateOf(initialTab.id) }

    if (showOpeningFlow) {
        OpeningBroadsheetFlow(
            feedItems = feedItems,
            journalEditions = journalEditions,
            bulletins = bulletins,
            onEnterHomepage = { showOpeningFlow = false }
        )
        return
    }

    androidx.compose.runtime.LaunchedEffect(isOfflineMode) {
        if (isOfflineMode && !hasAutoTriggeredTeletextForOffline) {
            isTeletextOpen = true
            hasAutoTriggeredTeletextForOffline = true
        } else if (!isOfflineMode) {
            hasAutoTriggeredTeletextForOffline = false
        }
    }

    // Dialog & sheet visibility states
    var isFullPlayerVisible by remember { mutableStateOf(false) }
    var isMorningBriefVisible by remember { mutableStateOf(false) }
    var isCreateContentVisible by remember { mutableStateOf(false) }
    var isCreateSpaceVisible by remember { mutableStateOf(false) }
    var isSettingsVisible by remember { mutableStateOf(false) }
    var sidebarInitialTab by remember { androidx.compose.runtime.mutableIntStateOf(0) }
    var activeChannelProfileId by remember { mutableStateOf<String?>(null) }
    val activeChannelProfile = remember(activeChannelProfileId, allChannels) {
        allChannels.find { it.id == activeChannelProfileId }
    }
    var activeMediaSpaceId by remember { mutableStateOf<Long?>(null) }
    val activeMediaSpace = remember(activeMediaSpaceId, userSpaces) {
        userSpaces.find { it.id == activeMediaSpaceId }
    }
    var activeReadingItem by remember { mutableStateOf<MediaItemEntity?>(null) }
    val inboxItems by viewModel.inboxItems.collectAsState()
    val unreadInboxCount by viewModel.unreadInboxCount.collectAsState()
    var isWeatherForecastOpen by remember { mutableStateOf(false) }
    var isInboxOpen by remember { mutableStateOf(false) }
    var isEmergencyHubOpen by remember { mutableStateOf(false) }
    var isVoiceBuilderOpen by remember { mutableStateOf(false) }
    var isBroadcastScheduleOpen by remember { mutableStateOf(false) }
    var isFactCheckHubOpen by remember { mutableStateOf(false) }
    var isDrivingModeOpen by remember { mutableStateOf(false) }
    var isModerationDashboardOpen by remember { mutableStateOf(false) }
    var isCreatorMonetizationOpen by remember { mutableStateOf(false) }
    var isLegalNoticeOpen by remember { mutableStateOf(false) }
    var reportedItemToReport by remember { mutableStateOf<MediaItemEntity?>(null) }

    val reportedContents by viewModel.reportedContents.collectAsState()
    val isAdsEnabled by viewModel.isAdsEnabled.collectAsState()
    val isForYouFeed by viewModel.isForYouFeed.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        val isSkinOverrideActive = appSettings.overrideBaseAppInterface && appSettings.activeAppSkinId != null
        if (isSkinOverrideActive) {
            val skinId = appSettings.activeAppSkinId
            Box(modifier = Modifier.fillMaxSize()) {
                when (skinId) {
                    "GEEK_LIVE" -> GeekLiveSkin(modifier = Modifier.fillMaxSize())
                    "FANDOM_TIMES" -> FandomTimesSkin(modifier = Modifier.fillMaxSize())
                    "CLI_DOS" -> CliDosSkin(modifier = Modifier.fillMaxSize())
                    "METRO_WIN8" -> MetroWin8Skin(modifier = Modifier.fillMaxSize())
                    "ANDROID_10" -> Android10Skin(modifier = Modifier.fillMaxSize())
                    else -> {
                        val customManifest = customExtensions.find { it.id == skinId }
                        if (customManifest != null) {
                            CustomExtensionLivePreview(
                                manifest = customManifest,
                                onClose = { viewModel.updateOverrideBaseAppInterface(false) }
                            )
                        } else {
                            GeekLiveSkin(modifier = Modifier.fillMaxSize())
                        }
                    }
                }

                // Top Floating Banner to Exit Skin Override or Open Settings
                Surface(
                    color = Color(0xEE0B132B),
                    border = BorderStroke(1.dp, NeonCyan),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚡ SKIN OVERRIDE ACTIVE: $skinId",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextButton(
                                onClick = {
                                    sidebarInitialTab = 2
                                    viewModel.openSidebar()
                                }
                            ) {
                                Text("SETTINGS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = WarmAmber)
                            }
                            IconButton(onClick = { viewModel.updateOverrideBaseAppInterface(false) }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Exit Skin Override",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {},
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    // Global Media Player
                    GlobalMediaPlayer(
                        audioState = audioState,
                        narrationState = narrationState,
                        onExpandFullPlayer = { isFullPlayerVisible = true },
                        onTogglePlayPauseAudio = { viewModel.togglePlayPause() },
                        onStopAudio = { viewModel.stopAudio() },
                        onTogglePlayPauseNarration = { viewModel.toggleVoiceNarrationPlayPause() },
                        onStopNarration = { viewModel.stopVoiceNarration() },
                        onNextParagraph = { viewModel.nextNarrationParagraph() },
                        onPreviousParagraph = { viewModel.previousNarrationParagraph() },
                        onSelectNarrationStyle = { viewModel.setNarrationStyle(it) },
                        onSetNarrationSpeed = { viewModel.setNarrationSpeed(it) }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentNavIndex) {
                    NavDestination.FEED -> {
                        MainFeedScreen(
                            items = feedItems,
                            channels = allChannels,
                            bulletins = bulletins,
                            selectedBulletinCategory = selectedBulletinCategory,
                            selectedChannelFilter = selectedChannelFilter,
                            selectedTypeFilter = selectedMediaTypeFilter,
                            searchQuery = searchQuery,
                            audioState = audioState,
                            isRefreshing = isRefreshingFeed,
                            refreshStatusText = refreshStatusText,
                            isOfflineMode = isOfflineMode,
                            onToggleOfflineMode = { viewModel.toggleOfflineMode() },
                            onRefresh = {
                                viewModel.refreshFeed { newCount ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar("⚡ $newCount new editorial dispatches received")
                                    }
                                }
                            },
                            onSelectBulletinCategory = { viewModel.selectBulletinCategory(it) },
                            onOpenReportBulletin = { viewModel.setReportBulletinDialogVisible(true) },
                            onToggleBulletinUpvote = { bulletin ->
                                viewModel.toggleBulletinUpvote(bulletin)
                                scope.launch {
                                    val action = if (bulletin.isUpvoted) "Removed helpful vote" else "Marked as helpful 👍"
                                    snackbarHostState.showSnackbar(action)
                                }
                            },
                            onDeleteBulletin = { bulletinId ->
                                viewModel.deleteBulletin(bulletinId)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Bulletin removed")
                                }
                            },
                            onShareBulletin = { bulletin ->
                                val shareText = "📢 [${bulletin.category}] ${bulletin.title}\n📍 ${bulletin.locationName}\n${bulletin.description}\n— via Townsquare Local Bulletins"
                                ShareHelper.sharePlainText(context, bulletin.title, shareText)
                            },
                            onSelectChannelFilter = { viewModel.setChannelFilter(it) },
                            onSelectTypeFilter = { viewModel.setMediaTypeFilter(it) },
                            onSearchQueryChange = { viewModel.setSearchQuery(it) },
                            isForYouFeed = isForYouFeed,
                            onToggleForYouFeed = { viewModel.toggleForYouFeed(it) },
                            onOpenReader = { activeReadingItem = it },
                            onPlayAudio = { viewModel.playAudio(it) },
                            onToggleLike = { viewModel.toggleLike(it) },
                            onToggleBookmark = { viewModel.toggleBookmark(it) },
                            onVoiceNarrate = { item -> viewModel.startVoiceNarration(item) },
                            onShare = { item ->
                                ShareHelper.shareMediaItem(context, item)
                                viewModel.recordShare(item)
                            },
                            onToggleSavedOffline = { viewModel.toggleSavedOffline(it) },
                            onReportContent = { reportedItemToReport = it },
                            onOpenFullBrief = { isMorningBriefVisible = true },
                            onPlayAudioBrief = { viewModel.playMorningBriefAudio() },
                            onOpenSettings = { isSettingsVisible = true },
                            onSelectChannel = { channelId ->
                                activeChannelProfileId = channelId
                            },
                            onNavigateToTv = { currentNavIndex = NavDestination.TV_STREAMING },
                            onOpenSidebar = { viewModel.openSidebar() },
                            onOpenWeather = { isWeatherForecastOpen = true },
                            onOpenInbox = { isInboxOpen = true },
                            onOpenEmergencyHub = { isEmergencyHubOpen = true },
                            onOpenBroadsheetCover = { isBroadsheetViewOpen = true },
                            onOpenTownsquarePlus = { currentNavIndex = NavDestination.TOWNSQUARE_PLUS },
                            unreadInboxCount = unreadInboxCount
                        )
                    }
                    NavDestination.NEWSBLOG -> {
                        NewsblogScreen(
                            entries = newsblogEntries,
                            selectedCategory = selectedNewsblogCategory,
                            playingAudioId = newsblogAudioPlayingId,
                            isSimulatingUpdate = isSimulatingNewsblogUpdate,
                            onSelectCategory = { viewModel.selectNewsblogCategory(it) },
                            onToggleLike = { viewModel.toggleNewsblogLike(it) },
                            onToggleBookmark = { viewModel.toggleNewsblogBookmark(it) },
                            onPlayAudio = { viewModel.playNewsblogAudio(it) },
                            onSimulateTick = { viewModel.simulateLiveNewsblogTick() },
                            onAddEntry = { headline, body, author, role, category, urgency, location, takeaway, quote, speaker ->
                                viewModel.addNewsblogEntry(
                                    headline = headline,
                                    body = body,
                                    authorName = author,
                                    authorRole = role,
                                    categoryTag = category,
                                    urgencyLevel = urgency,
                                    location = location,
                                    keyTakeaway = takeaway,
                                    quote = quote,
                                    quoteSpeaker = speaker
                                ) {
                                    scope.launch { snackbarHostState.showSnackbar("Dispatched breaking wire entry!") }
                                }
                            },
                            onOpenSidebar = { viewModel.openSidebar() },
                            onShare = { entry ->
                                ShareHelper.sharePlainText(context, entry.headline, "${entry.headline}\n\n${entry.body}\n— via Townsquare 24/7 Live Wire")
                            }
                        )
                    }
                    NavDestination.VISUAL_GALLERY -> {
                        VisualGalleryScreen(
                            posts = visualPosts,
                            selectedCategory = selectedVisualCategory,
                            activeLightboxPost = activeLightboxPost,
                            onSelectCategory = { viewModel.selectVisualCategory(it) },
                            onToggleLike = { viewModel.toggleVisualPostLike(it) },
                            onToggleBookmark = { viewModel.toggleVisualPostBookmark(it) },
                            onOpenLightbox = { viewModel.openVisualLightbox(it) },
                            onCloseLightbox = { viewModel.closeVisualLightbox() },
                            onAddPost = { title, caption, photographer, category, imageResUrl, location, cameraMeta, storyContext ->
                                viewModel.addVisualPost(
                                    title = title,
                                    caption = caption,
                                    photographerName = photographer,
                                    category = category,
                                    imageResUrl = imageResUrl,
                                    locationTaken = location,
                                    cameraMeta = cameraMeta,
                                    storyContext = storyContext
                                ) {
                                    scope.launch { snackbarHostState.showSnackbar("Visual story published to photojournalism feed!") }
                                }
                            },
                            onOpenSidebar = { viewModel.openSidebar() },
                            onShare = { post ->
                                ShareHelper.sharePlainText(context, post.title, "📷 ${post.title}\nBy ${post.photographerName}\n${post.caption}\n— via Townsquare Visual Gallery")
                            }
                        )
                    }
                    NavDestination.TV_STREAMING -> {
                        TvStreamingScreen(
                            channels = allTvChannels,
                            activeChannelId = activeTvChannelId,
                            isPlaying = isTvPlaying,
                            isMuted = isTvMuted,
                            isFullscreen = isTvFullscreen,
                            isCaptionsEnabled = isTvCaptionsEnabled,
                            streamQuality = tvStreamQuality,
                            selectedTab = tvSelectedTab,
                            scanlineFxEnabled = tvScanlineFxEnabled,
                            chatMessages = tvChatMessages[activeTvChannelId] ?: emptyList(),
                            onSendChatMessage = { msg -> viewModel.sendTvChatMessage(activeTvChannelId, "You", msg) },
                            onSelectChannel = { viewModel.selectTvChannel(it) },
                            onTuneChannelNumber = { viewModel.tuneChannelByNumber(it) },
                            onNextChannel = { viewModel.nextTvChannel() },
                            onPrevChannel = { viewModel.previousTvChannel() },
                            onTogglePlayPause = { viewModel.toggleTvPlayPause() },
                            onToggleMute = { viewModel.toggleTvMute() },
                            onToggleFullscreen = { viewModel.toggleTvFullscreen() },
                            onToggleCaptions = { viewModel.toggleTvCaptions() },
                            onSelectQuality = { viewModel.setTvStreamQuality(it) },
                            onSetTab = { viewModel.setTvTab(it) },
                            onToggleScanlineFx = { viewModel.toggleTvScanlineFx() },
                            onToggleFavorite = { channel ->
                                viewModel.toggleTvFavorite(channel)
                                scope.launch {
                                    val action = if (channel.isFavorite) "Removed favorite from" else "⭐ Added to favorites:"
                                    snackbarHostState.showSnackbar("$action ${channel.name}")
                                }
                            },
                            onToggleReminder = { channel ->
                                viewModel.toggleTvReminder(channel)
                                scope.launch {
                                    val action = if (channel.isReminderSet) "Alert removed for" else "🔔 Broadcast reminder set for"
                                    snackbarHostState.showSnackbar("$action ${channel.name}")
                                }
                            },
                            onToggleRecording = { channel ->
                                viewModel.toggleTvRecording(channel)
                                scope.launch {
                                    val action = if (channel.isRecording) "Removed recording for" else "⏺️ Recording ${channel.name} to local DVR"
                                    snackbarHostState.showSnackbar(action)
                                }
                            },
                            onShareBroadcast = { channel ->
                                ShareHelper.shareTvBroadcast(context, channel)
                            }
                        )
                    }
                    NavDestination.NEWSSTAND -> {
                        NewsstandScreen(
                            items = allItems,
                            journalEditions = journalEditions,
                            upcomingEditions = upcomingEditions,
                            retailKiosks = retailKiosks,
                            selectedFilter = newsstandFilter,
                            onFilterChange = { viewModel.setNewsstandFilter(it) },
                            onOpenFlipbookItem = { viewModel.openFlipbook(it) },
                            onOpenFlipbookJournal = { viewModel.openFlipbook(it) },
                            onToggleLike = { viewModel.toggleLike(it) },
                            onToggleBookmark = { viewModel.toggleBookmark(it) },
                            onToggleEditionReminder = { edition ->
                                viewModel.toggleEditionReminder(edition)
                                scope.launch {
                                    val action = if (edition.isReminderSet) "Notification disabled for" else "Notification set for"
                                    snackbarHostState.showSnackbar("$action ${edition.publicationTitle}")
                                }
                            },
                            onToggleEditionSubscription = { edition ->
                                viewModel.toggleEditionSubscription(edition)
                                scope.launch {
                                    val action = if (edition.isSubscribed) "Unfollowed" else "Now following"
                                    snackbarHostState.showSnackbar("$action ${edition.publicationTitle}")
                                }
                            },
                            onAnnounceEdition = { title, type, vol, date, label, time, head, teaser, notes, color, onDone ->
                                viewModel.announceUpcomingEdition(title, type, vol, date, label, time, head, teaser, notes, color) {
                                    onDone()
                                    scope.launch {
                                        snackbarHostState.showSnackbar("📢 Scheduled upcoming edition for $title")
                                    }
                                }
                            },
                            onReserveKioskCopy = { kiosk, copies, onDone ->
                                viewModel.reserveKioskCopy(kiosk, copies) {
                                    onDone()
                                    scope.launch {
                                        snackbarHostState.showSnackbar("🎟️ 1 print copy reserved at ${kiosk.name}")
                                    }
                                }
                            },
                            onToggleKioskFavorite = { kiosk ->
                                viewModel.toggleKioskFavorite(kiosk)
                            },
                            onNavigateToJournal = { currentNavIndex = NavDestination.JOURNAL }
                        )
                    }
                    NavDestination.JOURNAL -> {
                        JournalScreen(
                            journalEditions = journalEditions,
                            notepadDrafts = notepadDrafts,
                            notepadFilter = selectedDraftFilter,
                            onSelectNotepadFilter = { viewModel.setDraftFilter(it) },
                            onSaveNotepadDraft = { viewModel.saveNotepadDraft(it) },
                            onToggleNotepadDraftStar = { viewModel.toggleNotepadDraftStarred(it) },
                            onDeleteNotepadDraft = { viewModel.deleteNotepadDraft(it) },
                            onConvertDraftToJournal = { viewModel.convertDraftToJournalEdition(it) },
                            onOpenFlipbook = { viewModel.openFlipbook(it) },
                            onCreateEdition = { title, motto, vol, iss, date, tmpl, col, lHead, lSub, lBody, lAuth, sHead, sBody, edNotes, bull, onSuccess ->
                                viewModel.createJournalEdition(
                                    newspaperTitle = title,
                                    motto = motto,
                                    volumeNumber = vol,
                                    issueNumber = iss,
                                    issueDate = date,
                                    templateStyle = tmpl,
                                    bannerColorHex = col,
                                    leadHeadline = lHead,
                                    leadSubheadline = lSub,
                                    leadArticleBody = lBody,
                                    leadAuthor = lAuth,
                                    secondaryHeadline = sHead,
                                    secondaryArticleBody = sBody,
                                    editorialNotes = edNotes,
                                    communityBulletin = bull,
                                    onSuccess = onSuccess
                                )
                            },
                            onDeleteEdition = { viewModel.deleteJournalEdition(it) },
                            onOpenSidebar = { viewModel.openSidebar() }
                        )
                    }
                    NavDestination.AUDIO_HUB -> {
                        AudioHubScreen(
                            items = allItems,
                            audioState = audioState,
                            onPlayAudio = { viewModel.playAudio(it) },
                            onOpenReader = { activeReadingItem = it },
                            onToggleLike = { viewModel.toggleLike(it) },
                            onToggleBookmark = { viewModel.toggleBookmark(it) },
                            onTogglePlayPause = { viewModel.togglePlayPause() },
                            onSeekNextStation = {
                                viewModel.seekNextRadioStation(allItems.filter { it.type == com.example.data.model.MediaType.RADIO_STATION.name })
                            },
                            onSeekPrevStation = {
                                viewModel.seekPreviousRadioStation(allItems.filter { it.type == com.example.data.model.MediaType.RADIO_STATION.name })
                            },
                            onOpenSidebar = { viewModel.openSidebar() }
                        )
                    }
                    NavDestination.SPACES -> {
                        ProfileSpacesScreen(
                            userSpaces = userSpaces,
                            userCreatedItems = userCreatedItems,
                            bookmarkedItems = bookmarkedItems,
                            audioState = audioState,
                            onOpenCreateSpace = { isCreateSpaceVisible = true },
                            onOpenCreateContent = { isCreateContentVisible = true },
                            onOpenReader = { activeReadingItem = it },
                            onPlayAudio = { viewModel.playAudio(it) },
                            onToggleLike = { viewModel.toggleLike(it) },
                            onToggleBookmark = { viewModel.toggleBookmark(it) },
                            onVoiceNarrate = { item -> viewModel.startVoiceNarration(item) },
                            onShare = { item ->
                                ShareHelper.shareMediaItem(context, item)
                                viewModel.recordShare(item)
                            },
                            onToggleSavedOffline = { viewModel.toggleSavedOffline(it) },
                            onOpenSettings = { isSettingsVisible = true },
                            onOpenVoiceBuilder = { isVoiceBuilderOpen = true },
                            onSelectSpace = { spaceId -> activeMediaSpaceId = spaceId }
                        )
                    }
                    NavDestination.COMMUNITY -> {
                        com.example.ui.screens.CommunityScreen(
                            onOpenSidebar = { viewModel.openSidebar() },
                            onBack = { currentNavIndex = NavDestination.FEED }
                        )
                    }
                    NavDestination.FUNNIES -> {
                        com.example.ui.screens.SundayFunniesScreen(
                            comics = sundayFunniesComics,
                            selectedCategory = selectedFunniesCategory,
                            onSelectCategory = { viewModel.selectFunniesCategory(it) },
                            onToggleLike = { comicId -> viewModel.toggleFunniesLike(comicId) },
                            onToggleBookmark = { comicId -> viewModel.toggleFunniesBookmark(comicId) },
                            onAddReaction = { comicId, reaction -> viewModel.addFunniesReaction(comicId, reaction) },
                            onAddComicOrMeme = { title, series, artist, imgUrl, caption, isMeme ->
                                viewModel.addComicOrMeme(title, series, artist, imgUrl, caption, isMeme) {
                                    scope.launch { snackbarHostState.showSnackbar("Shared new cartoon/meme to Sunday Funnies! 🎉") }
                                }
                            },
                            onOpenSidebar = { viewModel.openSidebar() },
                            onShare = { comic ->
                                ShareHelper.sharePlainText(context, comic.title, "😄 [Sunday Funnies] ${comic.title} by ${comic.cartoonistName}\n${comic.imageUrl}\n— via Townsquare Syndicated Cartoons")
                            }
                        )
                    }
                    NavDestination.PARTNERS -> {
                        PartnerPublicationsScreen(
                            partners = partnerPublications,
                            submittedApplications = partnerApplications,
                            onOpenApplicationForm = { viewModel.openPartnerApplicationDialog() },
                            onOpenSidebar = { viewModel.openSidebar() }
                        )
                    }
                    NavDestination.DISCOVERY -> {
                        com.example.ui.screens.ChannelsScreen(
                            channels = allChannels,
                            onToggleFollowChannel = { viewModel.toggleFollowChannel(it) },
                            onSelectChannel = { activeChannelProfileId = it }
                        )
                    }
                    NavDestination.PLAYGROUND -> {
                        com.example.ui.screens.PlaygroundScreen(
                            onSwitchProfile = { target ->
                                if (target == "CHOOSE") {
                                    viewModel.selectProfile(null)
                                } else {
                                    viewModel.selectProfile(target)
                                    currentNavIndex = if (target == "PLAYGROUND") NavDestination.PLAYGROUND else NavDestination.FEED
                                }
                            },
                            onOpenSidebar = { viewModel.openSidebar() }
                        )
                    }
                    NavDestination.ENGAGEMENT_DASHBOARD -> {
                        EngagementDashboardScreen(viewModel)
                    }
                    NavDestination.BROWSER -> {
                        BrowserScreen(
                            tabs = browserTabs,
                            activeTabId = activeBrowserTabId,
                            onTabSelected = { activeBrowserTabId = it },
                            onNewTab = { initialUrl ->
                                val newTab = WebTabItem(
                                    url = initialUrl ?: "https://news.google.com",
                                    title = "New Tab"
                                )
                                browserTabs = browserTabs + newTab
                                activeBrowserTabId = newTab.id
                            },
                            onCloseTab = { tabId ->
                                val updatedTabs = browserTabs.filterNot { it.id == tabId }
                                if (updatedTabs.isNotEmpty()) {
                                    browserTabs = updatedTabs
                                    if (activeBrowserTabId == tabId) {
                                        activeBrowserTabId = updatedTabs.last().id
                                    }
                                } else {
                                    val fallbackTab = WebTabItem()
                                    browserTabs = listOf(fallbackTab)
                                    activeBrowserTabId = fallbackTab.id
                                }
                            },
                            onUpdateTab = { updatedTab ->
                                browserTabs = browserTabs.map { if (it.id == updatedTab.id) updatedTab else it }
                            },
                            onOpenSidebar = { viewModel.openSidebar() }
                        )
                    }
                    NavDestination.TOWNSQUARE_PLUS -> {
                        com.example.ui.plus.TownsquarePlusScreen(
                            onOpenSidebar = { viewModel.openSidebar() },
                            onBackToFeed = { currentNavIndex = NavDestination.FEED }
                        )
                    }
                }
            }
        }
    }

    // Townsquare Sidebar Drawer Overlay
    TownsquareSidebarDrawer(
        isOpen = isSidebarOpen,
        currentIndex = currentNavIndex,
        onSelectIndex = { selectedIdx ->
            currentNavIndex = selectedIdx
        },
        onClose = { viewModel.closeSidebar() },
        onOpenSettings = {
            sidebarInitialTab = 2
            viewModel.openSidebar()
        },
        onOpenCreateContent = { isCreateContentVisible = true },
        isOfflineMode = isOfflineMode,
        onToggleOfflineMode = { viewModel.toggleOfflineMode() },
        onOpenLettersMailbox = { viewModel.openLettersMailbox() },
        onOpenTeletext = { isTeletextOpen = true },
        onOpenFacsimile = { viewModel.openFacsimileDialog() },
        onOpenBroadsheetCover = { isBroadsheetViewOpen = true },
        onOpenMultitaskingBrowser = { currentNavIndex = NavDestination.BROWSER },
        onOpenEmergencyHub = { isEmergencyHubOpen = true },
        onOpenWeather = { isWeatherForecastOpen = true },
        onOpenInbox = { isInboxOpen = true },
        onOpenModeration = { isModerationDashboardOpen = true },
        onOpenMonetization = { isCreatorMonetizationOpen = true },
        onOpenBroadcastSchedule = { isBroadcastScheduleOpen = true },
        onOpenFactCheckHub = { isFactCheckHubOpen = true },
        onOpenVoiceBuilder = { isVoiceBuilderOpen = true },
        onOpenDrivingMode = { isDrivingModeOpen = true },
        onOpenProfileSwitcher = { viewModel.selectProfile(null) },
        unreadInboxCount = unreadInboxCount,
        draftsCount = notepadDrafts.size,
        settings = appSettings,
        currentTheme = currentTheme,
        isDynamicTheme = isDynamicTheme,
        onSelectTheme = { viewModel.selectTheme(it) },
        onSetDynamicThemeEnabled = { viewModel.setDynamicThemeEnabled(it) },
        onUpdateAudioQuality = { viewModel.updateAudioQuality(it) },
        onUpdateFontSize = { viewModel.updateReadingFontSize(it) },
        onUpdateDialHaptic = { viewModel.updateDialHapticFeedback(it) },
        onUpdateAutoTune = { viewModel.updateAutoTuneLastStation(it) },
        onUpdateAutoCache = { viewModel.updateAutoCacheMorningEdition(it) },
        onUpdateBreakingNewsAlerts = { viewModel.updateBreakingNewsAlerts(it) },
        onUpdatePrintKioskAlerts = { viewModel.updatePrintKioskAlerts(it) },
        onUpdateVoicePreset = { viewModel.updateVoiceNarrationPreset(it) },
        onUpdateVoiceSpeed = { viewModel.updateVoiceNarrationSpeed(it) },
        onUpdateCommunityRadius = { viewModel.updateCommunityRadius(it) },
        onUpdateMorningDispatchTime = { viewModel.updateMorningDispatchTime(it) },
        onUpdateHighContrast = { viewModel.updateHighContrastDisplay(it) },
        onClearMediaCache = {
            viewModel.clearMediaCache()
            scope.launch { snackbarHostState.showSnackbar("Media cache cleared") }
        },
        onResetToDefaults = {
            viewModel.resetSettingsToDefaults()
            scope.launch { snackbarHostState.showSnackbar("Settings reset to defaults") }
        },
        onUpdateEnableAiFeatures = { viewModel.updateEnableAiFeatures(it) },
        onUpdateEnableAiFactChecking = { viewModel.updateEnableAiFactChecking(it) },
        onUpdateEnableAiVoiceNarration = { viewModel.updateEnableAiVoiceNarration(it) },
        onUpdateEnableAiSmartSummaries = { viewModel.updateEnableAiSmartSummaries(it) },
        customExtensions = customExtensions,
        onUpdateActiveAppSkin = { viewModel.updateActiveAppSkinId(it) },
        onUpdateActiveWelcomeSkin = { viewModel.updateActiveWelcomeSkinId(it) },
        onUpdateOverrideBaseAppInterface = { viewModel.updateOverrideBaseAppInterface(it) },
        onUpdateRetroTerminalMode = { viewModel.updateEnableRetroTerminalMode(it) },
        onUpdateKeitai3GOverlay = { viewModel.updateEnableKeitai3GOverlay(it) },
        onUpdateManuscriptParchmentTheme = { viewModel.updateEnableManuscriptParchmentTheme(it) },
        onUpdateMetroTilesView = { viewModel.updateEnableMetroTilesView(it) },
        onUpdateGeekLiveTickerHeader = { viewModel.updateEnableGeekLiveTickerHeader(it) },
        onOpenLegalNotice = { isLegalNoticeOpen = true },
        initialTab = sidebarInitialTab
    )
    }

    // Modal Components & Overlays
    if (isLegalNoticeOpen) {
        com.example.ui.components.LegalNoticeGuidelinesDialog(
            onDismiss = { isLegalNoticeOpen = false }
        )
    }

    // 0a. Local Weather Forecast Dialog
    LocalWeatherForecastDialog(
        isOpen = isWeatherForecastOpen,
        onClose = { isWeatherForecastOpen = false }
    )

    // 0b. Townsquare Inbox Dialog
    TownsquareInboxDialog(
        isOpen = isInboxOpen,
        onClose = { isInboxOpen = false },
        items = inboxItems,
        channels = allChannels,
        onMarkAsRead = { viewModel.markInboxItemAsRead(it) },
        onMarkAllAsRead = { viewModel.markAllInboxAsRead() },
        onDeleteNotification = { viewModel.deleteInboxItem(it) },
        onClearAll = { viewModel.clearAllInbox() },
        onOpenFullBrief = {
            isInboxOpen = false
            isMorningBriefVisible = true
        },
        onPlayAudioBrief = {
            viewModel.playMorningBriefAudio()
        },
        onOpenWeather = {
            isInboxOpen = false
            isWeatherForecastOpen = true
        },
        onOpenBulletin = { bulletinId ->
            isInboxOpen = false
            currentNavIndex = NavDestination.FEED
        }
    )

    // 0c. Emergency Alerts Hub
    EmergencyAlertsHubDialog(
        isOpen = isEmergencyHubOpen,
        onClose = { isEmergencyHubOpen = false }
    )
    
    // 0d. AI Voice Builder
    AIVoiceBuilderDialog(
        isOpen = isVoiceBuilderOpen,
        onClose = { isVoiceBuilderOpen = false }
    )

    // 1. Full Audio Player Bottom Sheet
    if (isFullPlayerVisible && audioState.currentItem != null) {
        FullAudioPlayerSheet(
            audioState = audioState,
            onTogglePlayPause = { viewModel.togglePlayPause() },
            onSeek = { viewModel.seekAudio(it) },
            onSpeedChange = { viewModel.setAudioSpeed(it) },
            onSkipForward = { viewModel.skipAudioForward() },
            onSkipBackward = { viewModel.skipAudioBackward() },
            onDismiss = { isFullPlayerVisible = false }
        )
    }

    // 2. Full Morning Briefing Dialog
    if (isMorningBriefVisible) {
        FullMorningBriefDialog(
            channels = allChannels,
            onPlayAudioBrief = { viewModel.playMorningBriefAudio() },
            onDismiss = { isMorningBriefVisible = false }
        )
    }

    // 3. Editorial Article Reader Dialog
    activeReadingItem?.let { readingItem ->
        ArticleReaderDialog(
            item = readingItem,
            narrationState = narrationState,
            onStartNarration = { item, style -> viewModel.startVoiceNarration(item, style) },
            onPauseResumeNarration = { viewModel.toggleVoiceNarrationPlayPause() },
            onStopNarration = { viewModel.stopVoiceNarration() },
            onToggleLike = { viewModel.toggleLike(it) },
            onToggleBookmark = { viewModel.toggleBookmark(it) },
            onToggleSavedOffline = { viewModel.toggleSavedOffline(it) },
            onShare = { item ->
                ShareHelper.shareMediaItem(context, item)
                viewModel.recordShare(item)
            },
            onOpenFlipbook = { viewModel.openFlipbook(it) },
            onFactCheckClaims = { item -> viewModel.runFactCheckForMediaItem(item) },
            onSendLetterToEditor = { recipientId, recipientName -> viewModel.openSendLetterDialog(recipientId, recipientName) },
            showAiFactChecking = appSettings.enableAiFeatures && appSettings.enableAiFactChecking,
            showAiVoiceNarration = appSettings.enableAiFeatures && appSettings.enableAiVoiceNarration,
            onDismiss = { activeReadingItem = null }
        )
    }

    // 3b. Fact Check Audit Report Dialog
    activeFactCheckReport?.let { report ->
        com.example.ui.components.FactCheckerDialog(
            report = report,
            onClose = { viewModel.closeFactCheckReport() }
        )
    }

    // 3c. Send Letter to Editor Dialog
    activeLetterRecipient?.let { (recipientId, recipientName) ->
        com.example.ui.components.SendLetterToEditorDialog(
            recipientId = recipientId,
            recipientName = recipientName,
            recipientType = "CHANNEL",
            onClose = { viewModel.closeSendLetterDialog() },
            onSendLetter = { recId, recName, recType, sName, sEmail, subj, msg, cat ->
                viewModel.sendLetterToEditor(
                    recipientId = recId,
                    recipientName = recName,
                    recipientType = recType,
                    senderName = sName,
                    senderEmail = sEmail,
                    subject = subj,
                    messageBody = msg,
                    categoryTag = cat
                ) {
                    scope.launch { snackbarHostState.showSnackbar("📨 Letter sent to $recName!") }
                }
            }
        )
    }

    // 3d. Letters to Editor Mailbox Dialog
    if (isLettersMailboxOpen) {
        com.example.ui.components.LettersMailboxDialog(
            letters = lettersToEditor,
            onClose = { viewModel.closeLettersMailbox() },
            onToggleStar = { viewModel.toggleLetterStarred(it) }
        )
    }

    // 4. Physical E-Flipbook Reader Overlay Dialog
    if (activeFlipbookItem != null || activeFlipbookJournal != null) {
        FlipbookReaderDialog(
            mediaItem = activeFlipbookItem,
            journalEdition = activeFlipbookJournal,
            narrationState = narrationState,
            onStartNarration = { title, text, author, style ->
                viewModel.startVoiceNarrationText(title, text, author, style)
            },
            onStopNarration = { viewModel.stopVoiceNarration() },
            onShare = { title, content ->
                ShareHelper.shareText(context, title, content)
            },
            onDismiss = { viewModel.closeFlipbook() }
        )
    }

    // 5. Create & Publish Content Dialog
    if (isCreateContentVisible) {
        CreateContentDialog(
            userSpaces = userSpaces,
            channels = allChannels,
            onPublish = { type, title, subtitle, body, space, channel, tags, readTime, duration, freq ->
                viewModel.createNewContent(
                    type = type,
                    title = title,
                    subtitle = subtitle,
                    body = body,
                    space = space,
                    channel = channel,
                    tags = tags,
                    readTimeMinutes = readTime,
                    durationSeconds = duration,
                    frequency = freq
                )
                isCreateContentVisible = false
                scope.launch {
                    snackbarHostState.showSnackbar("Published to ${space?.title ?: "Media Space"}!")
                }
            },
            onDismiss = { isCreateContentVisible = false }
        )
    }

    // 6. Create Media Space Dialog
    if (isCreateSpaceVisible) {
        CreateSpaceDialog(
            onCreateSpace = { title, handle, description, category, colorHex ->
                viewModel.createSpace(title, handle, description, category, colorHex)
                isCreateSpaceVisible = false
                scope.launch {
                    snackbarHostState.showSnackbar("Media Space '$title' created!")
                }
            },
            onDismiss = { isCreateSpaceVisible = false }
        )
    }

    // 7. App Settings - Integrated into Sidebar
    if (isSettingsVisible) {
        androidx.compose.runtime.LaunchedEffect(Unit) {
            sidebarInitialTab = 2
            viewModel.openSidebar()
            isSettingsVisible = false
        }
    }

    // 8. Media Channel Profile Screen Dialog
    activeChannelProfile?.let { channelProfile ->
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { activeChannelProfileId = null },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            ChannelProfileScreen(
                channel = channelProfile,
                allItems = allItems,
                audioState = audioState,
                onPlayAudio = { viewModel.playAudio(it) },
                onOpenReader = { activeReadingItem = it },
                onToggleLike = { viewModel.toggleLike(it) },
                onToggleBookmark = { viewModel.toggleBookmark(it) },
                onToggleFollowChannel = { viewModel.toggleFollowChannel(it) },
                onVoiceNarrate = { item -> viewModel.startVoiceNarration(item) },
                onShare = { item ->
                    ShareHelper.shareMediaItem(context, item)
                    viewModel.recordShare(item)
                },
                onBack = { activeChannelProfileId = null }
            )
        }
    }

    activeMediaSpace?.let { space ->
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { activeMediaSpaceId = null },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            com.example.ui.screens.MediaSpaceProfileScreen(
                space = space,
                spaceItems = userCreatedItems.filter { it.spaceId == space.id },
                audioState = audioState,
                onPlayAudio = { viewModel.playAudio(it) },
                onOpenReader = { activeReadingItem = it },
                onToggleLike = { viewModel.toggleLike(it) },
                onToggleBookmark = { viewModel.toggleBookmark(it) },
                onVoiceNarrate = { item -> viewModel.startVoiceNarration(item) },
                onShare = { item ->
                    ShareHelper.shareMediaItem(context, item)
                    viewModel.recordShare(item)
                },
                onBack = { activeMediaSpaceId = null }
            )
        }
    }

    // 9. Report Local Bulletin Dialog
    if (isReportBulletinDialogVisible) {
        ReportBulletinDialog(
            onDismiss = { viewModel.setReportBulletinDialogVisible(false) },
            onSubmitBulletin = { category, title, description, locationName, urgencyLevel, iconEmoji, reporterName, reporterHandle ->
                viewModel.reportLocalBulletin(
                    category = category,
                    title = title,
                    description = description,
                    locationName = locationName,
                    urgencyLevel = urgencyLevel,
                    iconEmoji = iconEmoji,
                    reporterName = reporterName,
                    reporterHandle = reporterHandle,
                    onSuccess = {
                        viewModel.setReportBulletinDialogVisible(false)
                        scope.launch {
                            snackbarHostState.showSnackbar("🚨 Local bulletin posted to live feed")
                        }
                    }
                )
            }
        )
    }

    // 10. Airwave Teletext Offline & Satellite Terminal Dialog
    TeletextDialog(
        isOpen = isTeletextOpen,
        isDeviceOffline = isOfflineMode,
        onClose = { isTeletextOpen = false }
    )

    // 11. Retro Hourly Facsimile Broadsheet Dialog
    FacsimileBroadsheetDialog(
        isOpen = isFacsimileOpen,
        onClose = { viewModel.closeFacsimileDialog() },
        onTearSlip = { broadsheet ->
            scope.launch {
                snackbarHostState.showSnackbar("📠 Torn & saved facsimile dispatch: ${broadsheet.editionCode}")
            }
        }
    )

    // 12. Partner Syndicate Application Dialog
    if (isPartnerApplicationOpen) {
        PartnerApplicationDialog(
            onDismiss = { viewModel.closePartnerApplicationDialog() },
            onSubmitApplication = { pubName, applicant, email, medium, region, circ, charter, url ->
                viewModel.submitPartnerApplication(
                    publicationName = pubName,
                    applicantName = applicant,
                    contactEmail = email,
                    mediumType = medium,
                    region = region,
                    circulation = circ,
                    editorialCharter = charter,
                    sampleUrl = url
                ) {
                    scope.launch {
                        snackbarHostState.showSnackbar("🎉 Partner application submitted for $pubName!")
                    }
                }
            }
        )
    }

    // 13. Broadsheet Edition Overlay View
    if (isBroadsheetViewOpen) {
        Box(modifier = Modifier.fillMaxSize()) {
            BroadsheetOpeningScreen(
                feedItems = feedItems,
                journalEditions = journalEditions,
                bulletins = bulletins,
                onEnterHomepage = { isBroadsheetViewOpen = false }
            )
            IconButton(
                onClick = { isBroadsheetViewOpen = false },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.75f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Broadsheet",
                    tint = Color.White
                )
            }
        }
    }

    if (isModerationDashboardOpen) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { isModerationDashboardOpen = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            com.example.ui.screens.ModerationDashboardScreen(
                reportedItems = reportedContents,
                onBack = { isModerationDashboardOpen = false },
                onDismissReport = { viewModel.dismissReport(it) },
                onRemoveContent = { viewModel.removeReportedContent(it) },
                onSuspendUser = { viewModel.suspendUser(it) }
            )
        }
    }

    if (isCreatorMonetizationOpen) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { isCreatorMonetizationOpen = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            com.example.ui.screens.CreatorMonetizationScreen(
                isAdsEnabled = isAdsEnabled,
                onToggleAds = { viewModel.toggleAdsEnabled(it) },
                onBack = { isCreatorMonetizationOpen = false }
            )
        }
    }

    reportedItemToReport?.let { item ->
        com.example.ui.components.ReportContentDialog(
            itemTitle = item.title.ifBlank { item.bodyText.take(50) },
            onDismiss = { reportedItemToReport = null },
            onSubmit = { reason ->
                viewModel.reportContent(
                    targetId = item.id.toString(),
                    title = item.title.ifBlank { item.bodyText.take(50) },
                    author = item.authorName,
                    authorId = item.channelId,
                    reason = reason,
                    type = item.type
                )
                reportedItemToReport = null
                scope.launch {
                    snackbarHostState.showSnackbar("Content reported for review")
                }
            }
        )
    }

    if (isBroadcastScheduleOpen) {
        com.example.ui.components.BroadcastScheduleDialog(
            isOpen = isBroadcastScheduleOpen,
            onClose = { isBroadcastScheduleOpen = false },
            onTuneChannel = { channelNum ->
                viewModel.tuneChannelByNumber(channelNum)
                currentNavIndex = NavDestination.TV_STREAMING
            }
        )
    }

    if (isFactCheckHubOpen) {
        com.example.ui.components.FactCheckingHubDialog(
            isOpen = isFactCheckHubOpen,
            onClose = { isFactCheckHubOpen = false }
        )
    }

    if (isDrivingModeOpen) {
        val radioStations = remember(allItems) {
            allItems.filter { it.type == com.example.data.model.MediaType.RADIO_STATION.name }
        }
        com.example.ui.components.AndroidAutoDrivingDialog(
            stations = radioStations,
            audioState = audioState,
            onPlayStation = { viewModel.playAudio(it) },
            onTogglePlayPause = { viewModel.togglePlayPause() },
            onSeekNext = { viewModel.seekNextRadioStation(radioStations) },
            onSeekPrev = { viewModel.seekPreviousRadioStation(radioStations) },
            onDismiss = { isDrivingModeOpen = false }
        )
    }
}
