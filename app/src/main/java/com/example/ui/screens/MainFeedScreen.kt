package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Add
import com.example.ui.components.AirwavesBrandIcon
import com.example.ui.components.AirwavesBrandIconType
import com.example.ui.components.GlobalSandwichMenu
import com.example.ui.components.SandwichMenuItem
import com.example.ui.screens.discovery.TownsquarePeepsView
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioState
import com.example.data.model.LocalBulletinEntity
import com.example.data.model.MediaChannelEntity
import com.example.data.model.MediaItemEntity
import com.example.data.model.MediaType
import com.example.ui.components.Headline
import com.example.ui.components.LocalBulletinsSection
import com.example.ui.components.MediaCardItem
import com.example.ui.components.MorningBriefCard
import com.example.ui.components.NewsTicker
import com.example.ui.components.TownsquareTopBar
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainFeedScreen(
    items: List<MediaItemEntity>,
    channels: List<MediaChannelEntity>,
    bulletins: List<LocalBulletinEntity> = emptyList(),
    selectedBulletinCategory: String? = null,
    selectedChannelFilter: String?,
    selectedTypeFilter: MediaType?,
    searchQuery: String,
    audioState: AudioState,
    isRefreshing: Boolean = false,
    refreshStatusText: String? = null,
    isOfflineMode: Boolean = false,
    onToggleOfflineMode: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onSelectBulletinCategory: (String?) -> Unit = {},
    onOpenReportBulletin: () -> Unit = {},
    onToggleBulletinUpvote: (LocalBulletinEntity) -> Unit = {},
    onDeleteBulletin: (Long) -> Unit = {},
    onShareBulletin: (LocalBulletinEntity) -> Unit = {},
    onSelectChannelFilter: (String?) -> Unit,
    onSelectTypeFilter: (MediaType?) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    isForYouFeed: Boolean = false,
    onToggleForYouFeed: (Boolean) -> Unit = {},
    onOpenReader: (MediaItemEntity) -> Unit,
    onPlayAudio: (MediaItemEntity) -> Unit,
    onToggleLike: (MediaItemEntity) -> Unit,
    onToggleBookmark: (MediaItemEntity) -> Unit,
    onVoiceNarrate: ((MediaItemEntity) -> Unit)? = null,
    onShare: ((MediaItemEntity) -> Unit)? = null,
    onToggleSavedOffline: ((MediaItemEntity) -> Unit)? = null,
    onReportContent: ((MediaItemEntity) -> Unit)? = null,
    onOpenFullBrief: () -> Unit,
    onPlayAudioBrief: () -> Unit,
    onOpenProfile: () -> Unit = {},
    onOpenSettings: () -> Unit = onOpenProfile,
    onSelectChannel: ((String) -> Unit)? = null,
    onNavigateToTv: (() -> Unit)? = null,
    onOpenSidebar: () -> Unit = {},
    onOpenWeather: () -> Unit = {},
    onOpenInbox: () -> Unit = {},
    onOpenEmergencyHub: () -> Unit = {},
    onOpenBroadsheetCover: () -> Unit = {},
    onOpenTownsquarePlus: () -> Unit = {},
    unreadInboxCount: Int = 0,
    modifier: Modifier = Modifier
) {
    var searchVisible by remember { mutableStateOf(false) }
    var selectedHeadline by remember { mutableStateOf<Headline?>(null) }
    val sheetState = rememberModalBottomSheetState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Header
        TownsquareTopBar(
            title = "Townsquare",
            subtitle = "Civic & Editorial Media Hub",
            onOpenSidebar = onOpenSidebar,
            actions = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Inbox Button with Unread Badge
                    IconButton(
                        onClick = onOpenInbox,
                        modifier = Modifier.testTag("open_inbox_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadInboxCount > 0) {
                                    Badge(
                                        containerColor = NeonCyan,
                                        contentColor = Color(0xFF003544)
                                    ) {
                                        Text(
                                            text = "$unreadInboxCount",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        ) {
                            AirwavesBrandIcon(
                                iconType = AirwavesBrandIconType.UNIFIED_INBOX,
                                size = 26.dp,
                                iconColor = if (unreadInboxCount > 0) NeonCyan else MaterialTheme.colorScheme.onBackground,
                                circleColor = Color.Transparent,
                                borderColor = if (unreadInboxCount > 0) NeonCyan.copy(alpha = 0.5f) else Color.Transparent,
                                contentDescription = "Inbox"
                            )
                        }
                    }

                    IconButton(
                        onClick = { searchVisible = !searchVisible },
                        modifier = Modifier.testTag("toggle_search_button")
                    ) {
                        Icon(
                            imageVector = if (searchVisible) Icons.Default.Clear else Icons.Default.Search,
                            contentDescription = "Search",
                            tint = if (searchVisible) NeonCyan else MaterialTheme.colorScheme.onBackground
                        )
                    }

                    IconButton(
                        onClick = onOpenProfile,
                        modifier = Modifier.testTag("main_profile_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        )

        // Offline Mode Banner
        if (isOfflineMode) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 2.dp),
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF00222B),
                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudDone, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text("Offline Mode", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NeonCyan)
                            Text("Saved publications & bookmarks", style = MaterialTheme.typography.bodySmall, color = Color.LightGray, fontSize = 10.sp)
                        }
                    }
                    TextButton(onClick = onToggleOfflineMode) {
                        Text("Go Live", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NeonCyan)
                    }
                }
            }
        }


        // Network state check: if device is offline (or user toggled offline mode), Teletext is on by default
        val isInternetConnected = com.example.util.rememberIsConnectedToInternet()
        val isDeviceOffline = !isInternetConnected || isOfflineMode

        // Homepage Sandwich Menu for Feed Modes (0 = Main Civic Feed, 1 = Peeps, 2 = Pocket Reader, 3 = Offline Teletext)
        var isHomeMenuOpen by remember { mutableStateOf(false) }
        var selectedHomeMode by remember { mutableIntStateOf(if (isDeviceOffline) 3 else 0) }

        // Automatically switch to Teletext mode when internet is disconnected or offline mode is toggled
        androidx.compose.runtime.LaunchedEffect(isDeviceOffline) {
            if (isDeviceOffline) {
                selectedHomeMode = 3
            }
        }

        // Search bar (collapsible with Stories, Song Search, Browser View, and Brainrot/Slop Detector)
        var searchCategoryTab by remember { mutableIntStateOf(0) } // 0 = Stories & Feeds, 1 = Song Search, 2 = Browser View Tabs
        var recognizedListening by remember { mutableStateOf(false) }
        var identifiedLiveTrack by remember { mutableStateOf<com.example.ui.plus.waves.RecognizedSong?>(null) }
        var isSlopDetectorOpen by remember { mutableStateOf(false) }
        var isFactCheckCenterOpen by remember { mutableStateOf(false) }

        // Brainrot & Slop Detector Dialog triggerable directly from the Search view
        com.example.ui.components.browser.BrainrotSlopDetectorDialog(
            isOpen = isSlopDetectorOpen,
            initialUrlOrTitle = searchQuery,
            onDismissRequest = { isSlopDetectorOpen = false }
        )

        // AI Fact-Checking Center Dialog triggerable directly from the Search view
        com.example.ui.components.FactCheckingHubDialog(
            isOpen = isFactCheckCenterOpen,
            onClose = { isFactCheckCenterOpen = false }
        )

        // In-Search Browser Tabs View State
        var searchBrowserTabs by remember {
            mutableStateOf(
                listOf(
                    com.example.ui.components.WebTabItem(id = "tab_1", url = "https://news.google.com", title = "Townsquare Press Wire"),
                    com.example.ui.components.WebTabItem(id = "tab_2", url = "https://wikipedia.org", title = "Civic Wikipedia"),
                    com.example.ui.components.WebTabItem(id = "tab_3", url = "bbs://townsquare.local", title = "Townsquare BBS Terminal 📟")
                )
            )
        }
        var activeSearchTabId by remember { mutableStateOf("tab_1") }
        var searchBrowserUrlInput by remember { mutableStateOf("https://news.google.com") }

        if (searchVisible) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 2.dp)
            ) {
                // Search Category Switcher Chips (Feeds, Song Search, Browser View, Brainrot Detector)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (searchCategoryTab == 0) NeonCyan else Color(0xFF131D2E),
                        border = BorderStroke(1.dp, if (searchCategoryTab == 0) NeonCyan else DarkBorder),
                        modifier = Modifier
                            .clickable { searchCategoryTab = 0 }
                            .testTag("search_tab_all_feeds")
                    ) {
                        Text(
                            text = "🌐 Stories & Feeds",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (searchCategoryTab == 0) Color(0xFF003544) else Color.LightGray,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (searchCategoryTab == 1) NeonCyan else Color(0xFF131D2E),
                        border = BorderStroke(1.dp, if (searchCategoryTab == 1) NeonCyan else DarkBorder),
                        modifier = Modifier
                            .clickable { searchCategoryTab = 1 }
                            .testTag("search_tab_song_search")
                    ) {
                        Text(
                            text = "🎵 Song Search",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (searchCategoryTab == 1) Color(0xFF003544) else Color.LightGray,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (searchCategoryTab == 2) NeonCyan else Color(0xFF131D2E),
                        border = BorderStroke(1.dp, if (searchCategoryTab == 2) NeonCyan else DarkBorder),
                        modifier = Modifier
                            .clickable { searchCategoryTab = 2 }
                            .testTag("search_tab_browser_view")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "💻 Browser Tabs (${searchBrowserTabs.size})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (searchCategoryTab == 2) Color(0xFF003544) else Color.LightGray
                            )
                        }
                    }

                    // Brainrot / Slop Detector Quick Scan Chip
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFEF4444).copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, Color(0xFFEF4444)),
                        modifier = Modifier
                            .clickable { isSlopDetectorOpen = true }
                            .testTag("search_trigger_brainrot_detector")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("🛡️ Slop Detector", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFCA5A5))
                        }
                    }

                    // AI Fact-Checking Center Chip (Moved from Tools drawer)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF30D158).copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, Color(0xFF30D158)),
                        modifier = Modifier
                            .clickable { isFactCheckCenterOpen = true }
                            .testTag("search_trigger_fact_check_center")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("✅ AI Fact Check", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF86EFAC))
                        }
                    }

                    if (searchCategoryTab == 1) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (recognizedListening) WarmAmber else Color(0xFF1B2838),
                            border = BorderStroke(1.dp, if (recognizedListening) WarmAmber else NeonCyan.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .clickable {
                                    recognizedListening = true
                                    identifiedLiveTrack = com.example.ui.plus.waves.SongDetectionEngine.getRadioLiveDetectedSong("88.5 MHz")
                                }
                                .testTag("search_identify_live_airwave_song")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (recognizedListening) "🎧 Listening..." else "🎧 Identify Live",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (recognizedListening) Color.Black else NeonCyan
                                )
                            }
                        }
                    }
                }

                if (searchCategoryTab != 2) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = {
                            Text(
                                if (searchCategoryTab == 1) "Search song title, artist, genre, lyrics snippet..." else "Search stories, podcasts, radio...",
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (searchCategoryTab == 1) Icons.Default.MusicNote else Icons.Default.Search,
                                contentDescription = null,
                                tint = NeonCyan
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { onSearchQueryChange("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("feed_search_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = DarkBorder
                        )
                    )
                }

                // If in Song Search mode, show live matching song tracks with Play buttons
                if (searchCategoryTab == 1) {
                    val matchingSongs = remember(searchQuery) {
                        com.example.ui.plus.waves.SongDetectionEngine.searchSongs(searchQuery)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .background(Color(0xFF0F172A), RoundedCornerShape(10.dp))
                            .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "AIRWAVE CATALOG & FM BROADCAST TRACKS (${matchingSongs.size})",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            )
                            if (identifiedLiveTrack != null) {
                                Text(
                                    text = "LIVE DETECTED ✨",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WarmAmber
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        matchingSongs.take(4).forEach { song ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1E293B),
                                border = BorderStroke(1.dp, if (identifiedLiveTrack?.id == song.id) WarmAmber else Color(0xFF334155)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clickable { onPlayAudio(song.toMediaItemEntity()) }
                                    .testTag("song_result_${song.id}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFF0B132B),
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(song.albumArtEmoji, fontSize = 16.sp)
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = song.title,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Text(
                                                text = "${song.artist} • ${song.genre} • ${song.bitrate}",
                                                fontSize = 10.sp,
                                                color = DarkTextSecondary
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { onPlayAudio(song.toMediaItemEntity()) },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .testTag("play_song_${song.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Play Song",
                                            tint = NeonCyan
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // If in Browser Tabs View mode, show tabs bar and live WebView directly inside search
                if (searchCategoryTab == 2) {
                    val activeTab = searchBrowserTabs.find { it.id == activeSearchTabId } ?: searchBrowserTabs.firstOrNull()

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .background(Color(0xFF0F172A), RoundedCornerShape(12.dp))
                            .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(8.dp)
                            .testTag("search_browser_view_container")
                    ) {
                        // Browser Tab Strip
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            searchBrowserTabs.forEach { tab ->
                                val isActive = tab.id == activeSearchTabId
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isActive) Color(0xFF1E293B) else Color(0xFF0B132B),
                                    border = BorderStroke(1.dp, if (isActive) NeonCyan else Color(0xFF334155)),
                                    modifier = Modifier
                                        .clickable {
                                            activeSearchTabId = tab.id
                                            searchBrowserUrlInput = tab.url
                                        }
                                        .testTag("search_browser_tab_${tab.id}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "🌐", fontSize = 11.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = tab.title.take(18),
                                            fontSize = 11.sp,
                                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isActive) Color.White else Color.LightGray
                                        )
                                        if (searchBrowserTabs.size > 1) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Close tab",
                                                tint = Color.Gray,
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .clickable {
                                                        val filtered = searchBrowserTabs.filterNot { it.id == tab.id }
                                                        searchBrowserTabs = filtered
                                                        if (activeSearchTabId == tab.id) {
                                                            activeSearchTabId = filtered.first().id
                                                            searchBrowserUrlInput = filtered.first().url
                                                        }
                                                    }
                                            )
                                        }
                                    }
                                }
                            }

                            // Add Tab Button
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF1E293B),
                                border = BorderStroke(1.dp, Color(0xFF334155)),
                                modifier = Modifier
                                    .size(28.dp)
                                    .clickable {
                                        val newId = "tab_${System.currentTimeMillis()}"
                                        val newTab = com.example.ui.components.WebTabItem(
                                            id = newId,
                                            url = "https://news.google.com",
                                            title = "New Tab"
                                        )
                                        searchBrowserTabs = searchBrowserTabs + newTab
                                        activeSearchTabId = newId
                                        searchBrowserUrlInput = newTab.url
                                    }
                                    .testTag("search_browser_add_tab")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = "New Tab", tint = NeonCyan, modifier = Modifier.size(14.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // URL Address Bar & Slop Audit Quick Action
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedTextField(
                                value = searchBrowserUrlInput,
                                onValueChange = { searchBrowserUrlInput = it },
                                placeholder = { Text("https:// or search web address", fontSize = 12.sp) },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("search_browser_url_input"),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonCyan,
                                    unfocusedBorderColor = Color(0xFF334155),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Button(
                                onClick = {
                                    var formatted = searchBrowserUrlInput.trim()
                                    if (!formatted.startsWith("http://") && !formatted.startsWith("https://") && !formatted.startsWith("bbs://")) {
                                        formatted = if (formatted.contains(".") && !formatted.contains(" ")) "https://$formatted" else "https://www.google.com/search?q=${java.net.URLEncoder.encode(formatted, "UTF-8")}"
                                    }
                                    searchBrowserUrlInput = formatted
                                    searchBrowserTabs = searchBrowserTabs.map {
                                        if (it.id == activeSearchTabId) it.copy(url = formatted, title = formatted.take(24)) else it
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.height(44.dp)
                            ) {
                                Text("Go", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            // Quick Slop Audit for current URL
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFEF4444).copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, Color(0xFFEF4444)),
                                modifier = Modifier
                                    .height(44.dp)
                                    .clickable {
                                        isSlopDetectorOpen = true
                                    }
                                    .testTag("search_browser_audit_slop_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🛡️ Audit", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFCA5A5))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // WebView Content Window directly inside the Search button drawer/sheet
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(380.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            color = Color(0xFF020617),
                            border = BorderStroke(1.dp, Color(0xFF1E293B))
                        ) {
                            if (activeTab != null) {
                                if (activeTab.url.startsWith("bbs://")) {
                                    com.example.ui.screens.TownsquareBbsTerminalView(
                                        onNavigateUrl = { newUrl ->
                                            searchBrowserUrlInput = newUrl
                                            searchBrowserTabs = searchBrowserTabs.map {
                                                if (it.id == activeTab.id) it.copy(url = newUrl) else it
                                            }
                                        }
                                    )
                                } else {
                                    key(activeTab.id, activeTab.url) {
                                        androidx.compose.ui.viewinterop.AndroidView(
                                            factory = { ctx ->
                                                android.webkit.WebView(ctx).apply {
                                                    setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null)
                                                    settings.apply {
                                                        javaScriptEnabled = true
                                                        domStorageEnabled = true
                                                        loadWithOverviewMode = true
                                                        useWideViewPort = true
                                                        cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
                                                    }
                                                    webViewClient = object : android.webkit.WebViewClient() {
                                                        override fun onPageFinished(view: android.webkit.WebView?, url: String?) {
                                                            super.onPageFinished(view, url)
                                                            url?.let { finishedUrl ->
                                                                val pageTitle = view?.title ?: "Web Page"
                                                                searchBrowserUrlInput = finishedUrl
                                                                searchBrowserTabs = searchBrowserTabs.map {
                                                                    if (it.id == activeTab.id) it.copy(url = finishedUrl, title = pageTitle) else it
                                                                }
                                                            }
                                                        }
                                                    }
                                                    loadUrl(activeTab.url)
                                                }
                                            },
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        NewsTicker(
            headlines = listOf(
                Headline("Civic News", "New Promenade opening tomorrow morning", "Civic Press"),
                Headline("Weather Alert", "Offshore squall expected at 9 PM", "Harbor Station"),
                Headline("Marketplace", "Vintage audio equipment auction ending soon", "Townsquare Market"),
                Headline("Community", "Old Town clocktower renovation begins", "Town Council")
            ),
            onHeadlineClick = { selectedHeadline = it }
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .clickable { isHomeMenuOpen = !isHomeMenuOpen }
                .testTag("homepage_sandwich_menu_toggle"),
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF131D2E),
            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        when (selectedHomeMode) {
                            1 -> "🐥"
                            2 -> "📖"
                            3 -> "📟"
                            else -> "📰"
                        },
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = when (selectedHomeMode) {
                                1 -> "Mode: Townsquare Peeps Stream"
                                2 -> "Mode: Pocket Reader Vault"
                                3 -> "Mode: Offline Teletext Airwave Decoder"
                                else -> "Mode: Main Civic Feed"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = when (selectedHomeMode) {
                                1 -> "Short-form verified news & audio bites"
                                2 -> "Offline reading list & synchronized RSS vault"
                                3 -> if (isDeviceOffline) "Active (No Internet Connection - Broadcast Feed)" else "Airwave broadcast & VBI signal reception"
                                else -> "Latest stories, bulletins & outlets"
                            },
                            fontSize = 10.sp,
                            color = if (selectedHomeMode == 3 && isDeviceOffline) WarmAmber else DarkTextSecondary
                        )
                    }
                }
                Icon(
                    imageVector = if (isHomeMenuOpen) Icons.Default.Close else Icons.Default.Menu,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        GlobalSandwichMenu(
            isOpen = isHomeMenuOpen,
            title = "HOMEPAGE DISCOVERY SUITE",
            items = listOf(
                SandwichMenuItem(
                    id = "feed",
                    title = "Main Civic Feed",
                    description = "Latest stories, bulletins & outlets",
                    iconEmoji = "📰",
                    isSelected = selectedHomeMode == 0
                ),
                SandwichMenuItem(
                    id = "peeps",
                    title = "Townsquare Peeps",
                    description = "Short-form verified news & audio bites",
                    iconEmoji = "🐥",
                    isSelected = selectedHomeMode == 1
                ),
                SandwichMenuItem(
                    id = "pocket",
                    title = "Pocket Reader",
                    description = "Offline reading list & longform vault",
                    iconEmoji = "📖",
                    isSelected = selectedHomeMode == 2
                ),
                SandwichMenuItem(
                    id = "teletext",
                    title = "Offline Teletext",
                    description = "Broadcast airwave decoder (Default when offline)",
                    iconEmoji = "📟",
                    brandIconType = AirwavesBrandIconType.OFFLINE,
                    isSelected = selectedHomeMode == 3
                )
            ),
            onSelectItem = { item ->
                selectedHomeMode = when (item.id) {
                    "feed" -> 0
                    "peeps" -> 1
                    "pocket" -> 2
                    "teletext" -> 3
                    else -> 0
                }
                isHomeMenuOpen = false
            },
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        if (selectedHomeMode == 1) {
            TownsquarePeepsView(modifier = Modifier.fillMaxSize())
        } else if (selectedHomeMode == 2) {
            com.example.ui.plus.pocket.TownsquarePocketApp(
                onBack = { selectedHomeMode = 0 },
                modifier = Modifier.fillMaxSize()
            )
        } else if (selectedHomeMode == 3) {
            com.example.ui.components.TeletextScreenContent(
                isDeviceOffline = isDeviceOffline,
                onClose = { selectedHomeMode = 0 }
            )
        } else {
            // Curation Feed Toggles
            androidx.compose.material3.TabRow(
                selectedTabIndex = if (isForYouFeed) 1 else 0,
                containerColor = Color.Transparent,
                contentColor = NeonCyan,
                indicator = { tabPositions ->
                    androidx.compose.material3.TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[if (isForYouFeed) 1 else 0]),
                        color = NeonCyan
                    )
                },
                divider = { androidx.compose.material3.HorizontalDivider(color = DarkBorder) }
            ) {
                androidx.compose.material3.Tab(
                    selected = !isForYouFeed,
                    onClick = { onToggleForYouFeed(false) },
                    text = { Text("Latest", fontWeight = if (!isForYouFeed) FontWeight.Bold else FontWeight.Medium, fontSize = 12.sp) }
                )
                androidx.compose.material3.Tab(
                    selected = isForYouFeed,
                    onClick = { onToggleForYouFeed(true) },
                    text = { Text("For You", fontWeight = if (isForYouFeed) FontWeight.Bold else FontWeight.Medium, fontSize = 12.sp) }
                )
            }
            Spacer(modifier = Modifier.height(4.dp))

            // Main Feed with Pull-To-Refresh Box
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("feed_pull_to_refresh_box")
            ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 2. Local Community Bulletins & Public Notices
                if (bulletins.isNotEmpty()) {
                    item {
                        LocalBulletinsSection(
                            bulletins = bulletins,
                            selectedCategory = selectedBulletinCategory,
                            onSelectCategory = onSelectBulletinCategory,
                            onOpenReportDialog = onOpenReportBulletin,
                            onToggleUpvote = onToggleBulletinUpvote,
                            onDeleteBulletin = onDeleteBulletin,
                            onShareBulletin = onShareBulletin,
                            modifier = Modifier.testTag("feed_local_bulletins_section")
                        )
                    }
                }

                // 3. Featured Syndicated Outlets & Channels Carousel
                if (channels.isNotEmpty()) {
                    item {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "📡 Syndicated Media Outlets",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                                Text(
                                    text = "${channels.size} active",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeonCyan
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(channels) { ch ->
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        border = BorderStroke(1.dp, DarkBorder),
                                        modifier = Modifier
                                            .clickable { onSelectChannel?.invoke(ch.id) }
                                            .testTag("channel_chip_${ch.id}")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = ch.iconEmoji, fontSize = 18.sp)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = ch.name,
                                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = ch.category,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // --- THE TICKET: CULTURAL EVENT LISTINGS SECTION ---
                item {
                    val ticketEvents = items.filter { it.channelId == "channel_the_ticket" }
                    if (ticketEvents.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "🎟️ The Ticket • Cultural Events",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                                Text(
                                    text = "Book Seats",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeonCyan
                                )
                            }
                            
                            var bookedEventTitle by remember { mutableStateOf<String?>(null) }
                            var followedArtists by remember { mutableStateOf(setOf<String>()) }
                            
                            if (bookedEventTitle != null) {
                                androidx.compose.material3.AlertDialog(
                                    onDismissRequest = { bookedEventTitle = null },
                                    title = { Text("🎟️ Ticket Confirmed!", color = NeonCyan, fontWeight = FontWeight.Bold) },
                                    text = {
                                        Text(
                                            text = "Your seat at '$bookedEventTitle' has been cryptographically secured.\n\nWe have saved your ticket to your secure civil documents under 'The Ticket'.",
                                            color = Color.LightGray
                                        )
                                    },
                                    confirmButton = {
                                        androidx.compose.material3.Button(
                                            onClick = { bookedEventTitle = null },
                                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = NeonCyan)
                                        ) {
                                            Text("Awesome", color = Color.Black)
                                        }
                                    }
                                )
                            }
                            
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(ticketEvents) { event ->
                                    Card(
                                        modifier = Modifier
                                            .width(280.dp)
                                            .testTag("ticket_event_card_${event.id}"),
                                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                                        border = BorderStroke(1.dp, DarkBorder)
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = Color(0xFFFF5252).copy(alpha = 0.15f)
                                            ) {
                                                Text(
                                                    text = event.issueEdition.uppercase(),
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFFF5252),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            
                                            Spacer(modifier = Modifier.height(8.dp))
                                            
                                            Text(
                                                text = event.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = Color.White,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = event.subtitle,
                                                fontSize = 11.sp,
                                                color = Color.Gray,
                                                maxLines = 1
                                            )
                                            
                                            Spacer(modifier = Modifier.height(6.dp))
                                            
                                            Text(
                                                text = event.bodyText,
                                                fontSize = 11.sp,
                                                color = Color.LightGray,
                                                maxLines = 2,
                                                lineHeight = 15.sp
                                            )
                                            
                                            Spacer(modifier = Modifier.height(10.dp))
                                            
                                            // Featured artist follow row
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.Stars,
                                                        contentDescription = null,
                                                        tint = NeonCyan,
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = event.authorName,
                                                        fontSize = 11.sp,
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                
                                                val isFollowed = followedArtists.contains(event.authorName)
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = if (isFollowed) Color(0xFF30D158).copy(alpha = 0.2f) else NeonCyan.copy(alpha = 0.15f),
                                                    border = BorderStroke(1.dp, if (isFollowed) Color(0xFF30D158) else NeonCyan),
                                                    modifier = Modifier.clickable {
                                                        followedArtists = if (isFollowed) {
                                                            followedArtists - event.authorName
                                                        } else {
                                                            followedArtists + event.authorName
                                                        }
                                                    }
                                                ) {
                                                    Text(
                                                        text = if (isFollowed) "Following ✓" else "+ Follow",
                                                        fontSize = 10.sp,
                                                        color = if (isFollowed) Color(0xFF30D158) else NeonCyan,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                            
                                            Spacer(modifier = Modifier.height(10.dp))
                                            
                                            androidx.compose.material3.Button(
                                                onClick = { bookedEventTitle = event.title },
                                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                                                modifier = Modifier.fillMaxWidth().height(32.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text("Get Tickets / RSVP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. Media Format Filter Tabs
                item {
                    val mediaFormats = listOf(null to "All Formats") + MediaType.entries.map { it to it.badge }
                    ScrollableTabRow(
                        selectedTabIndex = mediaFormats.indexOfFirst { it.first == selectedTypeFilter }.coerceAtLeast(0),
                        edgePadding = 0.dp,
                        containerColor = Color.Transparent,
                        contentColor = NeonCyan,
                        indicator = { tabPositions ->
                            val index = mediaFormats.indexOfFirst { it.first == selectedTypeFilter }.coerceAtLeast(0)
                            if (index < tabPositions.size) {
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                                    color = NeonCyan
                                )
                            }
                        },
                        divider = {}
                    ) {
                        mediaFormats.forEach { (type, label) ->
                            val isSelected = selectedTypeFilter == type
                            Tab(
                                selected = isSelected,
                                onClick = { onSelectTypeFilter(type) },
                                text = {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (isSelected) NeonCyan else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                modifier = Modifier.testTag("media_tab_${type?.name ?: "ALL"}")
                            )
                        }
                    }
                }

                // 5. Feed Items
                if (items.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "📡", fontSize = 36.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No Media Found",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Try clearing search or picking another channel filter.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(items, key = { it.id }) { item ->
                        MediaCardItem(
                            item = item,
                            audioState = audioState,
                            onOpenReader = onOpenReader,
                            onPlayAudio = onPlayAudio,
                            onToggleLike = onToggleLike,
                            onToggleBookmark = onToggleBookmark,
                            onVoiceNarrate = onVoiceNarrate,
                            onShare = onShare,
                            onToggleSavedOffline = onToggleSavedOffline,
                            onReportContent = onReportContent
                        )
                    }
                }

                // Bottom spacer so content is not cut off by mini player & nav
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
      }
        
        if (selectedHeadline != null) {
            ModalBottomSheet(
                onDismissRequest = { selectedHeadline = null },
                sheetState = sheetState
            ) {
                Column(modifier = Modifier.padding(16.dp).padding(bottom = 32.dp)) {
                    Text(selectedHeadline!!.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Source: ${selectedHeadline!!.source}", style = MaterialTheme.typography.labelSmall, color = NeonCyan)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(selectedHeadline!!.text, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
