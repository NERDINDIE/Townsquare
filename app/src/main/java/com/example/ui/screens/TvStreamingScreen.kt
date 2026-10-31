package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FiberDvr
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.TvChannelEntity
import com.example.data.model.TvDvrRecordItem
import com.example.data.model.TvOnDemandItem
import com.example.data.model.TvScheduleItem
import com.example.data.model.TvSeriesItem
import com.example.ui.components.LiveBroadcastCallInDialog
import com.example.ui.components.TvBroadcastPlayer
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber
import com.example.ui.viewmodel.TvChatMessage
import kotlinx.coroutines.delay

/**
 * Modern Streaming Service TV Hub:
 * - Streaming Hero Billboard with dynamic episode countdowns
 * - Horizontal Category Rails: Live Now, Series Premieres, Originals Vault, Infinite DVR
 * - 7-Day Interactive EPG Timeline Grid with Program Booking & Infinite DVR Recording
 * - Digital Remote Quick-Tune Bar & Broadcast Player integration
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvStreamingScreen(
    channels: List<TvChannelEntity>,
    activeChannelId: String,
    isPlaying: Boolean,
    isMuted: Boolean,
    isFullscreen: Boolean,
    isCaptionsEnabled: Boolean,
    streamQuality: String,
    selectedTab: String,
    scanlineFxEnabled: Boolean,
    chatMessages: List<TvChatMessage> = emptyList(),
    onSendChatMessage: (String) -> Unit = {},
    onSelectChannel: (String) -> Unit,
    onTuneChannelNumber: (Int) -> Unit,
    onNextChannel: () -> Unit,
    onPrevChannel: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleFullscreen: () -> Unit,
    onToggleCaptions: () -> Unit,
    onSelectQuality: (String) -> Unit,
    onSetTab: (String) -> Unit,
    onToggleScanlineFx: () -> Unit,
    onToggleFavorite: (TvChannelEntity) -> Unit,
    onToggleReminder: (TvChannelEntity) -> Unit,
    onToggleRecording: (TvChannelEntity) -> Unit,
    onShareBroadcast: (TvChannelEntity) -> Unit,
    tvWatchlist: List<com.example.data.model.TvWatchlistItem> = emptyList(),
    onToggleWatchlist: (showTitle: String, channelNumber: Int, channelName: String, durationMinutes: Int, category: String, timeSlot: String, dayOfWeek: Int, synopsis: String) -> Boolean = { _, _, _, _, _, _, _, _ -> false },
    onOpenProfileWatchlist: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val activeChannel = channels.find { it.id == activeChannelId } ?: channels.firstOrNull()
    var isCallInDialogOpen by remember { mutableStateOf(false) }

    // Live clock ticker for series countdown timers
    var currentEpochMs by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            currentEpochMs = System.currentTimeMillis()
        }
    }

    // Interactive Program Detail Modal state
    var selectedProgramForDetail by remember { mutableStateOf<TvScheduleItem?>(null) }
    var selectedChannelForDetail by remember { mutableStateOf<TvChannelEntity?>(null) }

    // Infinite DVR Recordings state
    var dvrLibrary by remember {
        mutableStateOf(getInitialDvrLibrary())
    }

    // Reminders set state for schedule items
    var scheduledReminderIds by remember { mutableStateOf(setOf("prog_ch5_thu_2000", "prog_ch3_sat_1845")) }

    // Call-In Modal
    if (isCallInDialogOpen && activeChannel != null) {
        LiveBroadcastCallInDialog(
            broadcastTitle = activeChannel.name + " - " + activeChannel.tagline,
            channelOrStation = "Channel ${activeChannel.channelNumber}: ${activeChannel.name}",
            onDismiss = { isCallInDialogOpen = false }
        )
    }

    // Program Details Dialog
    if (selectedProgramForDetail != null) {
        val prog = selectedProgramForDetail!!
        val channel = selectedChannelForDetail ?: channels.find { it.channelNumber == prog.channelNumber }
        val isRecorded = dvrLibrary.any { it.programTitle == prog.title }
        val isReminderSet = scheduledReminderIds.contains(prog.id)

        Dialog(
            onDismissRequest = {
                selectedProgramForDetail = null
                selectedChannelForDetail = null
            }
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = DarkSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = NeonCyan.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "CH ${prog.channelNumber} • ${channel?.name ?: "Townsquare TV"}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = NeonCyan,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                selectedProgramForDetail = null
                                selectedChannelForDetail = null
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = prog.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "⏱ ${prog.timeSlot} (${prog.durationMinutes}m)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "•",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = prog.category,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = WarmAmber
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = prog.synopsis,
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Record for Infinite Viewing
                        FilledTonalButton(
                            onClick = {
                                if (isRecorded) {
                                    dvrLibrary = dvrLibrary.filterNot { it.programTitle == prog.title }
                                } else {
                                    dvrLibrary = dvrLibrary + TvDvrRecordItem(
                                        id = "rec_${System.currentTimeMillis()}",
                                        programTitle = prog.title,
                                        channelName = channel?.name ?: "Townsquare TV",
                                        channelNumber = prog.channelNumber,
                                        category = prog.category,
                                        recordedDate = "Recorded Now",
                                        durationMinutes = prog.durationMinutes,
                                        synopsis = prog.synopsis
                                    )
                                }
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = if (isRecorded) Color(0xFFFF3B30).copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (isRecorded) Icons.Default.Check else Icons.Default.FiberDvr,
                                contentDescription = "Record",
                                tint = if (isRecorded) Color(0xFFFF3B30) else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isRecorded) "In DVR" else "Record DVR",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isRecorded) Color(0xFFFF3B30) else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Schedule Alert
                        FilledTonalButton(
                            onClick = {
                                scheduledReminderIds = if (isReminderSet) {
                                    scheduledReminderIds - prog.id
                                } else {
                                    scheduledReminderIds + prog.id
                                }
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = if (isReminderSet) NeonCyan.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (isReminderSet) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                contentDescription = "Alert",
                                tint = if (isReminderSet) NeonCyan else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isReminderSet) "Alert On" else "Set Alert",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isReminderSet) NeonCyan else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Bookmark to Watchlist & Global Inbox Alert
                    val isBookmarked = tvWatchlist.any { it.showTitle == prog.title }
                    FilledTonalButton(
                        onClick = {
                            onToggleWatchlist(
                                prog.title,
                                prog.channelNumber,
                                channel?.name ?: "Townsquare TV",
                                prog.durationMinutes,
                                prog.category,
                                prog.timeSlot,
                                prog.dayOfWeek,
                                prog.synopsis
                            )
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (isBookmarked) NeonCyan.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_modal_bookmark_watchlist")
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Watchlist",
                            tint = if (isBookmarked) NeonCyan else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBookmarked) "In Watchlist (Inbox Alerts Active) ✓" else "🔖 Bookmark to Watchlist & Inbox Alert",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isBookmarked) NeonCyan else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tune Now button
                    Button(
                        onClick = {
                            channel?.let { onSelectChannel(it.id) }
                            selectedProgramForDetail = null
                            selectedChannelForDetail = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Watch",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Tune Channel ${prog.channelNumber} Live",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }

    // Fullscreen Landscape Dialog Mode
    if (isFullscreen && activeChannel != null) {
        Dialog(
            onDismissRequest = onToggleFullscreen,
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                TvBroadcastPlayer(
                    channel = activeChannel,
                    isPlaying = isPlaying,
                    isMuted = isMuted,
                    isCaptionsEnabled = isCaptionsEnabled,
                    streamQuality = streamQuality,
                    scanlineFxEnabled = scanlineFxEnabled,
                    chatMessages = chatMessages,
                    onSendChatMessage = onSendChatMessage,
                    isFullscreen = true,
                    onTogglePlayPause = onTogglePlayPause,
                    onToggleMute = onToggleMute,
                    onToggleCaptions = onToggleCaptions,
                    onToggleFullscreen = onToggleFullscreen,
                    onSelectQuality = onSelectQuality,
                    onToggleScanlineFx = onToggleScanlineFx,
                    onToggleRecording = { onToggleRecording(activeChannel) },
                    onNextChannel = onNextChannel,
                    onPrevChannel = onPrevChannel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .testTag("tv_streaming_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // 1. Station Branding Bar
        item {
            TvModernStreamingHeader(
                watchlistCount = tvWatchlist.size,
                onOpenWatchlist = onOpenProfileWatchlist
            )
        }

        // 2. Main Live Broadcast Player (Collapsible / expandable)
        item {
            if (activeChannel != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    TvBroadcastPlayer(
                        channel = activeChannel,
                        isPlaying = isPlaying,
                        isMuted = isMuted,
                        isCaptionsEnabled = isCaptionsEnabled,
                        streamQuality = streamQuality,
                        scanlineFxEnabled = scanlineFxEnabled,
                        chatMessages = chatMessages,
                        onSendChatMessage = onSendChatMessage,
                        isFullscreen = false,
                        onTogglePlayPause = onTogglePlayPause,
                        onToggleMute = onToggleMute,
                        onToggleCaptions = onToggleCaptions,
                        onToggleFullscreen = onToggleFullscreen,
                        onSelectQuality = onSelectQuality,
                        onToggleScanlineFx = onToggleScanlineFx,
                        onNextChannel = onNextChannel,
                        onPrevChannel = onPrevChannel,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Active Channel Status & Quick Details
                    ActiveChannelDetailCard(
                        channel = activeChannel,
                        isWatchlisted = tvWatchlist.any { it.showTitle == activeChannel.currentShowTitle },
                        onToggleWatchlist = {
                            onToggleWatchlist(
                                activeChannel.currentShowTitle,
                                activeChannel.channelNumber,
                                activeChannel.name,
                                60,
                                activeChannel.currentShowCategory,
                                activeChannel.currentShowTime,
                                0,
                                activeChannel.currentShowSynopsis
                            )
                        },
                        onToggleFavorite = { onToggleFavorite(activeChannel) },
                        onToggleReminder = { onToggleReminder(activeChannel) },
                        onToggleRecording = { onToggleRecording(activeChannel) },
                        onOpenCallIn = { isCallInDialogOpen = true },
                        onShare = { onShareBroadcast(activeChannel) },
                        onNextChannel = onNextChannel,
                        onPrevChannel = onPrevChannel
                    )
                }
            }
        }

        // 3. Quick Channel Tuner Row (Buttons 1 to 6)
        item {
            QuickChannelTunerBar(
                channels = channels,
                activeChannelNumber = activeChannel?.channelNumber ?: 1,
                onTuneChannel = onTuneChannelNumber
            )
        }

        // 4. Modern Navigation Tabs
        item {
            TvModernSectionTabs(
                selectedTab = selectedTab,
                onSelectTab = onSetTab
            )
        }

        // 5. Section Content based on selected tab
        when (selectedTab) {
            "LIVE_CHANNELS", "HOME" -> {
                // Modern Streaming Home: Hero Banner + Rails
                item {
                    val featuredSeries = getTownsquareSeriesCatalog()
                    TvStreamingHeroBanner(
                        seriesList = featuredSeries,
                        currentEpochMs = currentEpochMs,
                        onWatchSeries = { s ->
                            val ch = channels.find { it.channelNumber == s.channelNumber }
                            ch?.let { onSelectChannel(it.id) }
                        },
                        onScheduleReminder = { s ->
                            scheduledReminderIds = scheduledReminderIds + s.id
                        }
                    )
                }

                // Horizontal Rail 1: Live Now Across Channels
                item {
                    TvLiveChannelsRail(
                        channels = channels,
                        activeChannelId = activeChannelId,
                        onSelectChannel = onSelectChannel
                    )
                }

                // Horizontal Rail 2: Series Premieres & Countdowns
                item {
                    TvSeriesCountdownRail(
                        seriesList = getTownsquareSeriesCatalog(),
                        currentEpochMs = currentEpochMs,
                        scheduledReminderIds = scheduledReminderIds,
                        onToggleReminder = { series ->
                            scheduledReminderIds = if (scheduledReminderIds.contains(series.id)) {
                                scheduledReminderIds - series.id
                            } else {
                                scheduledReminderIds + series.id
                            }
                        },
                        onTuneSeries = { series ->
                            val ch = channels.find { it.channelNumber == series.channelNumber }
                            ch?.let { onSelectChannel(it.id) }
                        }
                    )
                }

                // Horizontal Rail 3: Townsquare Originals & Documentaries
                item {
                    TvOriginalsVaultRail(
                        items = getSampleOnDemandVault(),
                        channels = channels,
                        onSelectChannel = onSelectChannel
                    )
                }

                // Horizontal Rail 4: Infinite DVR Library
                item {
                    TvDvrLibraryRail(
                        records = dvrLibrary,
                        onPlayDvr = { rec ->
                            val ch = channels.find { it.channelNumber == rec.channelNumber }
                            ch?.let { onSelectChannel(it.id) }
                        },
                        onDeleteDvr = { rec ->
                            dvrLibrary = dvrLibrary.filterNot { it.id == rec.id }
                        }
                    )
                }
            }

            "TV_GUIDE" -> {
                // 7-Day Interactive EPG Timeline Grid
                item {
                    Tv7DayEpgGuideSection(
                        channels = channels,
                        activeChannelId = activeChannelId,
                        currentEpochMs = currentEpochMs,
                        dvrLibrary = dvrLibrary,
                        scheduledReminderIds = scheduledReminderIds,
                        tvWatchlist = tvWatchlist,
                        onSelectChannel = onSelectChannel,
                        onSelectProgram = { prog, ch ->
                            selectedProgramForDetail = prog
                            selectedChannelForDetail = ch
                        }
                    )
                }
            }

            "ON_DEMAND" -> {
                item {
                    TvOriginalsVaultRail(
                        items = getSampleOnDemandVault(),
                        channels = channels,
                        onSelectChannel = onSelectChannel
                    )
                }
            }

            "RECORDINGS" -> {
                item {
                    TvDvrLibraryFullSection(
                        records = dvrLibrary,
                        channels = channels,
                        onPlay = { rec ->
                            val ch = channels.find { it.channelNumber == rec.channelNumber }
                            ch?.let { onSelectChannel(it.id) }
                        },
                        onDelete = { rec ->
                            dvrLibrary = dvrLibrary.filterNot { it.id == rec.id }
                        }
                    )
                }
            }

            "CABLE_TIERS" -> {
                item {
                    TvCableSubscriptionManager()
                }
            }

            "WATCH_PARTY" -> {
                item {
                    TvWatchPartyView(activeChannelName = activeChannel?.name ?: "Townsquare Central TV")
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Modern Streaming Top Bar
// -------------------------------------------------------------

@Composable
private fun TvModernStreamingHeader(
    watchlistCount: Int = 0,
    onOpenWatchlist: () -> Unit = {}
) {
    Surface(
        color = DarkSurface,
        tonalElevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "📺",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Townsquare Stream",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.4.sp
                        ),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = NeonCyan.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "MAX",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 9.sp
                            ),
                            color = NeonCyan,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "6 Live 4K Channels • 7-Day Interactive EPG • DVR Vault",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Quick Watchlist in Profile button
                FilledTonalButton(
                    onClick = onOpenWatchlist,
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = NeonCyan.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(28.dp).testTag("btn_tv_header_watchlist")
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "Watchlist in Profile",
                        tint = NeonCyan,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Watchlist ($watchlistCount)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = NeonCyan
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Clean On-Air Indicator
                Surface(
                    color = Color(0xFF00E676).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFF00E676), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "LIVE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = Color(0xFF00E676)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Modern Streaming Hero Banner with Premiere Countdown
// -------------------------------------------------------------

@Composable
private fun TvStreamingHeroBanner(
    seriesList: List<TvSeriesItem>,
    currentEpochMs: Long,
    onWatchSeries: (TvSeriesItem) -> Unit,
    onScheduleReminder: (TvSeriesItem) -> Unit
) {
    var selectedIndex by remember { mutableStateOf(0) }
    val featured = seriesList.getOrNull(selectedIndex) ?: seriesList.firstOrNull() ?: return

    val remainingMs = (featured.countdownTargetEpochMs - currentEpochMs).coerceAtLeast(0L)
    val hours = remainingMs / 3600000L
    val minutes = (remainingMs % 3600000L) / 60000L
    val seconds = (remainingMs % 60000L) / 1000L
    val countdownText = String.format("%02dh %02dm %02ds", hours, minutes, seconds)

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(1.dp, DarkBorder, RoundedCornerShape(20.dp))
            .testTag("tv_hero_banner")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1E2230),
                            Color(0xFF0D0F16)
                        )
                    )
                )
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Top Tag Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            color = Color(0xFFFF3B30),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "FEATURED PREMIERE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 9.sp
                                ),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }

                        Surface(
                            color = NeonCyan.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "CH ${featured.channelNumber} • ${featured.category}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                ),
                                color = NeonCyan,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Series Carousel switcher pills
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        seriesList.forEachIndexed { idx, _ ->
                            Box(
                                modifier = Modifier
                                    .size(if (idx == selectedIndex) 16.dp else 6.dp, 6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (idx == selectedIndex) NeonCyan else Color.White.copy(alpha = 0.3f))
                                    .clickable { selectedIndex = idx }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = featured.bannerEmoji,
                        fontSize = 32.sp,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Column {
                        Text(
                            text = featured.seriesTitle,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.2.sp
                            ),
                            color = Color.White
                        )
                        Text(
                            text = "${featured.seasonEpisode} • ${featured.episodeTitle}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = NeonCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = featured.synopsis,
                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                    color = Color.White.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Live Countdown Box
                Surface(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "Countdown",
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "LIVE COUNTDOWN TO EPISODE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = featured.airTimeDisplay,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = Color.White
                                )
                            }
                        }

                        // Digital ticking clock
                        Surface(
                            color = NeonCyan.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = countdownText,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.sp
                                ),
                                color = NeonCyan,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Hero CTA Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onWatchSeries(featured) },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Watch",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Watch Stream",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.Black
                        )
                    }

                    FilledTonalButton(
                        onClick = { onScheduleReminder(featured) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Set Alert",
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Set Alert",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Horizontal Category Rails
// -------------------------------------------------------------

@Composable
private fun TvLiveChannelsRail(
    channels: List<TvChannelEntity>,
    activeChannelId: String,
    onSelectChannel: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFFFF3B30), CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "On Air Now Across Townsquare",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
            Text(
                text = "${channels.size} Channels Live",
                style = MaterialTheme.typography.labelSmall,
                color = NeonCyan
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(channels, key = { "live_rail_${it.id}" }) { ch ->
                val isSelected = (ch.id == activeChannelId)
                val chColor = Color(ch.themeColorHex)

                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .width(220.dp)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) chColor else DarkBorder,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { onSelectChannel(ch.id) }
                        .testTag("tv_live_card_${ch.channelNumber}")
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = chColor.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "CH ${ch.channelNumber} • ${ch.callsign}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                    color = chColor,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Surface(
                                color = Color(0xFFFF3B30).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Box(modifier = Modifier.size(4.dp).background(Color(0xFFFF3B30), CircleShape))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "LIVE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Black
                                        ),
                                        color = Color(0xFFFF3B30)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = ch.iconEmoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = ch.name,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = ch.currentShowTitle,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = "${ch.currentShowTime} • ${ch.category}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { ch.currentShowProgress },
                            color = chColor,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "👥 ${(ch.liveViewersCount / 1000.0).let { String.format("%.1fK", it) }}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = if (isSelected) "Playing" else "Tune ▶",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) chColor else NeonCyan
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TvSeriesCountdownRail(
    seriesList: List<TvSeriesItem>,
    currentEpochMs: Long,
    scheduledReminderIds: Set<String>,
    onToggleReminder: (TvSeriesItem) -> Unit,
    onTuneSeries: (TvSeriesItem) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "⏳", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Series Premieres & Countdowns",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
            Text(
                text = "Live Tickers",
                style = MaterialTheme.typography.labelSmall,
                color = WarmAmber
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(seriesList, key = { "series_rail_${it.id}" }) { s ->
                val remainingMs = (s.countdownTargetEpochMs - currentEpochMs).coerceAtLeast(0L)
                val hours = remainingMs / 3600000L
                val minutes = (remainingMs % 3600000L) / 60000L
                val seconds = (remainingMs % 60000L) / 1000L
                val timeClock = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                val isReminderSet = scheduledReminderIds.contains(s.id)

                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .width(240.dp)
                        .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                        .clickable { onTuneSeries(s) }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = WarmAmber.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "CH ${s.channelNumber} • ${s.category}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = WarmAmber,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            IconButton(
                                onClick = { onToggleReminder(s) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = if (isReminderSet) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                    contentDescription = "Alert",
                                    tint = if (isReminderSet) NeonCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = s.bannerEmoji, fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = s.seriesTitle,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${s.seasonEpisode}: ${s.episodeTitle}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = NeonCyan,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = s.synopsis,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Countdown Pill
                        Surface(
                            color = Color.Black.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().border(0.5.dp, WarmAmber.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "STARTS IN",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = timeClock,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = WarmAmber
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TvOriginalsVaultRail(
    items: List<TvOnDemandItem>,
    channels: List<TvChannelEntity>,
    onSelectChannel: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "📼", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Townsquare Originals & Vault",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
            Text(
                text = "On Demand",
                style = MaterialTheme.typography.labelSmall,
                color = NeonCyan
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items, key = { "vod_${it.id}" }) { item ->
                val ch = channels.find { it.channelNumber == item.channelNumber }
                val color = Color(ch?.themeColorHex ?: 0xFF00D2FF)

                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .width(220.dp)
                        .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                        .clickable { ch?.let { onSelectChannel(it.id) } }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = color.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "CH ${item.channelNumber}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = color,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = item.duration,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, lineHeight = 14.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.viewsCount,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            FilledTonalButton(
                                onClick = { ch?.let { onSelectChannel(it.id) } },
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(26.dp)
                            ) {
                                Text(
                                    text = "Watch",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = NeonCyan
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TvDvrLibraryRail(
    records: List<TvDvrRecordItem>,
    onPlayDvr: (TvDvrRecordItem) -> Unit,
    onDeleteDvr: (TvDvrRecordItem) -> Unit
) {
    if (records.isEmpty()) return

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.FiberDvr,
                    contentDescription = "DVR",
                    tint = Color(0xFFFF3B30),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "My Recorded DVR Library",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
            Text(
                text = "${records.size} Infinite Passes",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFFF3B30)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(records, key = { "dvr_${it.id}" }) { rec ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .width(220.dp)
                        .border(1.dp, Color(0xFFFF3B30).copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .clickable { onPlayDvr(rec) }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = Color(0xFFFF3B30).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "CH ${rec.channelNumber} DVR",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black
                                    ),
                                    color = Color(0xFFFF3B30),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }

                            IconButton(
                                onClick = { onDeleteDvr(rec) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = rec.programTitle,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = "${rec.channelName} • ${rec.durationMinutes}m",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Infinite Viewing Pass • ${rec.fileSizeMb}MB",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = NeonCyan,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 7-Day Interactive EPG Timeline Guide
// -------------------------------------------------------------

@Composable
private fun Tv7DayEpgGuideSection(
    channels: List<TvChannelEntity>,
    activeChannelId: String,
    currentEpochMs: Long,
    dvrLibrary: List<TvDvrRecordItem>,
    scheduledReminderIds: Set<String>,
    tvWatchlist: List<com.example.data.model.TvWatchlistItem> = emptyList(),
    onSelectChannel: (String) -> Unit,
    onSelectProgram: (TvScheduleItem, TvChannelEntity) -> Unit
) {
    var selectedDayIndex by remember { mutableStateOf(0) } // 0..6
    val daysOfWeek = listOf("Mon Oct 26", "Tue Oct 27", "Wed Oct 28", "Thu Oct 29", "Fri Oct 30", "Sat Oct 31", "Sun Nov 01")

    val verticalScroll = rememberScrollState()
    val horizontalScroll = rememberScrollState()

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "7-Day Interactive TV Guide (EPG)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = "Tap any programme to schedule alerts or record for infinite viewing.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 7-Day Selector Strip
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(daysOfWeek.indices.toList()) { dayIdx ->
                val isSelected = (selectedDayIndex == dayIdx)
                val label = daysOfWeek[dayIdx]

                FilterChip(
                    selected = isSelected,
                    onClick = { selectedDayIndex = dayIdx },
                    label = {
                        Text(
                            text = if (dayIdx == 0) "Today ($label)" else label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonCyan.copy(alpha = 0.2f),
                        selectedLabelColor = NeonCyan,
                        containerColor = DarkSurface,
                        labelColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // EPG Timeline Grid Frame
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(440.dp)
                .background(DarkSurface, RoundedCornerShape(14.dp))
                .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                .clip(RoundedCornerShape(14.dp))
        ) {
            Row(modifier = Modifier.verticalScroll(verticalScroll)) {
                // Fixed Left Channel Headers
                Column(
                    modifier = Modifier
                        .width(115.dp)
                        .background(Color(0xFF10121A))
                ) {
                    // Header Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .background(DarkSurface)
                            .border(0.5.dp, DarkBorder),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "CHANNELS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp,
                                letterSpacing = 0.6.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Channel items
                    channels.forEach { ch ->
                        val isTuned = (ch.id == activeChannelId)
                        val chColor = Color(ch.themeColorHex)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(85.dp)
                                .border(0.5.dp, DarkBorder)
                                .background(if (isTuned) chColor.copy(alpha = 0.12f) else Color.Transparent)
                                .clickable { onSelectChannel(ch.id) }
                                .padding(8.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = ch.iconEmoji, fontSize = 15.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "CH ${ch.channelNumber}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                        color = chColor
                                    )
                                }
                                Text(
                                    text = ch.name,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = ch.callsign,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Scrollable Schedule Timeline Grid
                Box {
                    Column(modifier = Modifier.horizontalScroll(horizontalScroll)) {
                        // Hour Header Track (06:00 to 24:00)
                        Row(modifier = Modifier.height(42.dp)) {
                            for (hour in 6..24) {
                                Box(
                                    modifier = Modifier
                                        .width(240.dp) // 1 hr = 240dp (4dp/min)
                                        .fillMaxHeight()
                                        .background(DarkSurface)
                                        .border(0.5.dp, DarkBorder),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        text = String.format("%02d:00", hour),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }

                        // Program Schedule Rows per Channel for selected day
                        channels.forEach { ch ->
                            Row(
                                modifier = Modifier
                                    .height(85.dp)
                                    .border(0.5.dp, DarkBorder)
                            ) {
                                val schedule = getSampleScheduleForChannelAndDay(ch.channelNumber, selectedDayIndex)

                                if (schedule.isNotEmpty()) {
                                    val firstStart = parseStartTimeMinutes(schedule.first().timeSlot)
                                    val offsetMins = maxOf(firstStart - 360, 0)
                                    if (offsetMins > 0) {
                                        Spacer(modifier = Modifier.width((offsetMins * 4).dp))
                                    }
                                }

                                schedule.forEach { prog ->
                                    val width = (prog.durationMinutes * 4).dp
                                    val isLive = (prog.isLiveNow && selectedDayIndex == 0)
                                    val isRecorded = dvrLibrary.any { it.programTitle == prog.title }
                                    val isReminder = scheduledReminderIds.contains(prog.id)
                                    val isWatchlisted = tvWatchlist.any { it.showTitle == prog.title }

                                    Box(
                                        modifier = Modifier
                                            .width(width)
                                            .fillMaxHeight()
                                            .border(0.5.dp, DarkBorder)
                                            .background(
                                                when {
                                                    isLive -> Color(ch.themeColorHex).copy(alpha = 0.22f)
                                                    isRecorded -> Color(0xFFFF3B30).copy(alpha = 0.15f)
                                                    isWatchlisted -> NeonCyan.copy(alpha = 0.16f)
                                                    isReminder -> NeonCyan.copy(alpha = 0.12f)
                                                    else -> Color.Transparent
                                                }
                                            )
                                            .clickable { onSelectProgram(prog, ch) }
                                            .padding(6.dp)
                                    ) {
                                        Column {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = prog.title,
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.sp
                                                    ),
                                                    color = if (isLive) Color(ch.themeColorHex) else Color.White,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier.weight(1f, fill = false)
                                                )

                                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                                    if (isWatchlisted) {
                                                        Text("🔖", fontSize = 9.sp)
                                                    }
                                                    if (isRecorded) {
                                                        Text("⏺️", fontSize = 9.sp)
                                                    }
                                                    if (isReminder) {
                                                        Text("⏰", fontSize = 9.sp)
                                                    }
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(2.dp))

                                            Text(
                                                text = "${prog.timeSlot} • ${prog.category}",
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )

                                            if (isLive) {
                                                Spacer(modifier = Modifier.height(3.dp))
                                                Surface(
                                                    color = Color(0xFFFF3B30),
                                                    shape = RoundedCornerShape(3.dp)
                                                ) {
                                                    Text(
                                                        text = "ON AIR",
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            fontSize = 8.sp,
                                                            fontWeight = FontWeight.Black
                                                        ),
                                                        color = Color.White,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Current Time Needle (Red line at 19:30 simulated)
                    if (selectedDayIndex == 0) {
                        val currentTimeMins = 19 * 60 + 30
                        val indicatorOffset = (currentTimeMins - 360) * 4
                        if (indicatorOffset > 0) {
                            Box(
                                modifier = Modifier
                                    .padding(start = indicatorOffset.dp)
                                    .width(2.dp)
                                    .fillMaxHeight()
                                    .background(Color(0xFFFF3B30))
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TvDvrLibraryFullSection(
    records: List<TvDvrRecordItem>,
    channels: List<TvChannelEntity>,
    onPlay: (TvDvrRecordItem) -> Unit,
    onDelete: (TvDvrRecordItem) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Infinite DVR Library",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = "Broadcasts recorded from Townsquare television for permanent offline replay.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                color = Color(0xFFFF3B30).copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "${records.size} Saved",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFFFF3B30),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (records.isEmpty()) {
            Surface(
                color = DarkSurface,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.FiberDvr,
                        contentDescription = "DVR",
                        tint = Color(0xFFFF3B30),
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No Recorded Episodes Yet",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Tap the ⏺ Record button on any broadcast or 7-Day EPG program to store it in your infinite DVR.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        } else {
            records.forEach { rec ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .border(1.dp, Color(0xFFFF3B30).copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color(0xFFFF3B30).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.FiberDvr,
                                    contentDescription = "DVR",
                                    tint = Color(0xFFFF3B30),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "CH ${rec.channelNumber} • ${rec.channelName}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = NeonCyan
                            )
                            Text(
                                text = rec.programTitle,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "${rec.recordedDate} • ${rec.durationMinutes}m • ${rec.fileSizeMb}MB HD",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilledTonalButton(
                                onClick = { onPlay(rec) },
                                colors = ButtonDefaults.filledTonalButtonColors(containerColor = NeonCyan),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text(
                                    text = "Play",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.Black
                                )
                            }

                            IconButton(onClick = { onDelete(rec) }) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Channel Details & Remote Bar
// -------------------------------------------------------------

@Composable
private fun ActiveChannelDetailCard(
    channel: TvChannelEntity,
    isWatchlisted: Boolean = false,
    onToggleWatchlist: () -> Unit = {},
    onToggleFavorite: () -> Unit,
    onToggleReminder: () -> Unit,
    onToggleRecording: () -> Unit,
    onOpenCallIn: () -> Unit = {},
    onShare: () -> Unit,
    onNextChannel: () -> Unit,
    onPrevChannel: () -> Unit
) {
    val channelColor = Color(channel.themeColorHex)

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Channel Name & Category Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = channelColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.border(1.dp, channelColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            text = "CH ${channel.channelNumber}",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                            color = channelColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = channel.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = channel.tagline,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Channel Surfing Arrows
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onPrevChannel, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Channel",
                            tint = NeonCyan
                        )
                    }
                    IconButton(onClick = onNextChannel, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Channel",
                            tint = NeonCyan
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Pills Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Watchlist Bookmark
                FilledTonalButton(
                    onClick = onToggleWatchlist,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (isWatchlisted) NeonCyan.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp).testTag("btn_channel_watchlist")
                ) {
                    Icon(
                        imageVector = if (isWatchlisted) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Watchlist",
                        tint = if (isWatchlisted) NeonCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isWatchlisted) "Saved" else "Watchlist",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isWatchlisted) NeonCyan else MaterialTheme.colorScheme.onSurface
                    )
                }

                // Favorite
                FilledTonalButton(
                    onClick = onToggleFavorite,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (channel.isFavorite) Color(0xFFFFD600).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        imageVector = if (channel.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (channel.isFavorite) Color(0xFFFFD600) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (channel.isFavorite) "Saved" else "Fav",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (channel.isFavorite) Color(0xFFFFD600) else MaterialTheme.colorScheme.onSurface
                    )
                }

                // Reminder
                FilledTonalButton(
                    onClick = onToggleReminder,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (channel.isReminderSet) NeonCyan.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        imageVector = if (channel.isReminderSet) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                        contentDescription = "Reminder",
                        tint = if (channel.isReminderSet) NeonCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (channel.isReminderSet) "Alert On" else "Alert",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (channel.isReminderSet) NeonCyan else MaterialTheme.colorScheme.onSurface
                    )
                }

                // Record / DVR
                FilledTonalButton(
                    onClick = onToggleRecording,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (channel.isRecording) Color(0xFFFF3B30).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FiberDvr,
                        contentDescription = "Record",
                        tint = if (channel.isRecording) Color(0xFFFF3B30) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (channel.isRecording) "In DVR" else "Record",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (channel.isRecording) Color(0xFFFF3B30) else MaterialTheme.colorScheme.onSurface
                    )
                }

                // Call-In
                FilledTonalButton(
                    onClick = onOpenCallIn,
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = NeonCyan.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp).testTag("tv_live_call_in_button")
                ) {
                    Text("📞", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Call-In", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NeonCyan)
                }

                // Share
                IconButton(onClick = onShare, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Stream",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickChannelTunerBar(
    channels: List<TvChannelEntity>,
    activeChannelNumber: Int,
    onTuneChannel: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "DIGITAL REMOTE CHANNELS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    fontSize = 10.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Channels 1–6",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = NeonCyan
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            channels.forEach { ch ->
                val isSelected = (ch.channelNumber == activeChannelNumber)
                val chColor = Color(ch.themeColorHex)

                Surface(
                    color = if (isSelected) chColor else DarkSurface,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) chColor else DarkBorder,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { onTuneChannel(ch.channelNumber) }
                        .testTag("tuner_button_ch${ch.channelNumber}")
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "CH ${ch.channelNumber}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            ),
                            color = if (isSelected) Color.Black else Color.White
                        )
                        Text(
                            text = ch.iconEmoji,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TvModernSectionTabs(
    selectedTab: String,
    onSelectTab: (String) -> Unit
) {
    val tabs = listOf(
        Pair("LIVE_CHANNELS", "🌟 Streaming Home"),
        Pair("TV_GUIDE", "📅 7-Day EPG Guide"),
        Pair("RECORDINGS", "🔴 DVR Vault"),
        Pair("ON_DEMAND", "📼 Originals Vault"),
        Pair("CABLE_TIERS", "📡 Cable Tiers"),
        Pair("WATCH_PARTY", "🎉 Watch Party")
    )
    val selectedIndex = tabs.indexOfFirst { it.first == selectedTab }.coerceAtLeast(0)

    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        containerColor = DarkSurface,
        contentColor = NeonCyan,
        edgePadding = 16.dp,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                color = NeonCyan
            )
        },
        divider = {}
    ) {
        tabs.forEachIndexed { _, (tabKey, label) ->
            val isSelected = (selectedTab == tabKey)
            Tab(
                selected = isSelected,
                onClick = { onSelectTab(tabKey) },
                text = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (isSelected) NeonCyan else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }
    }
}

// -------------------------------------------------------------
// Sample Series, Weekly Schedule & DVR Data
// -------------------------------------------------------------

private fun getTownsquareSeriesCatalog(): List<TvSeriesItem> {
    val now = System.currentTimeMillis()
    return listOf(
        TvSeriesItem(
            id = "series_clockmaker",
            seriesTitle = "The Clockmaker of Saint-Germain",
            episodeTitle = "The Pendulum Cipher",
            seasonEpisode = "S1:E4",
            channelNumber = 5,
            category = "Period Mystery",
            synopsis = "Master horologist Laurent uncovers secret diplomatic telegraph lines concealed inside the pendulum cylinder.",
            countdownTargetEpochMs = now + 4_200_000L, // ~1h 10m
            airTimeDisplay = "Tonight 20:00",
            bannerEmoji = "🕰️"
        ),
        TvSeriesItem(
            id = "series_grandmetro",
            seriesTitle = "Grand Metro Cup: Semifinal",
            episodeTitle = "Riverside FC vs. Highland United",
            seasonEpisode = "Live Match",
            channelNumber = 4,
            category = "Live Football",
            synopsis = "Electric semifinal clash with high stakes for the annual civic trophy.",
            countdownTargetEpochMs = now + 6_300_000L, // ~1h 45m
            airTimeDisplay = "Tonight 19:15",
            bannerEmoji = "⚽"
        ),
        TvSeriesItem(
            id = "series_quantum",
            seriesTitle = "Quantum Frontiers",
            episodeTitle = "Spectrographic Mapping of Exoplanets",
            seasonEpisode = "S2:E7",
            channelNumber = 3,
            category = "Astrophysics",
            synopsis = "Dr. Aris Thorne reveals deep space atmospheric spectra from the new orbital array.",
            countdownTargetEpochMs = now + 14_800_000L, // ~4h 8m
            airTimeDisplay = "Tonight 18:45",
            bannerEmoji = "🌌"
        ),
        TvSeriesItem(
            id = "series_townsquare_live",
            seriesTitle = "Townsquare Tonight",
            episodeTitle = "The Subterranean Grid Overhaul",
            seasonEpisode = "Daily Live",
            channelNumber = 1,
            category = "Investigative",
            synopsis = "Anchors Marcus Cole and Evelyn Vance examine the city's green energy grid transformation.",
            countdownTargetEpochMs = now + 1_800_000L, // ~30 mins
            airTimeDisplay = "Tonight 19:00",
            bannerEmoji = "⚡"
        )
    )
}

private fun getInitialDvrLibrary(): List<TvDvrRecordItem> {
    return listOf(
        TvDvrRecordItem(
            id = "dvr_1",
            programTitle = "The Clockmaker of Saint-Germain • Ep. 3",
            channelName = "Fifth Programme Drama",
            channelNumber = 5,
            category = "Period Mystery",
            recordedDate = "Yesterday 20:00",
            durationMinutes = 75,
            synopsis = "Laurent decodes the first telegraph transcript found behind the pendulum face.",
            fileSizeMb = 512
        ),
        TvDvrRecordItem(
            id = "dvr_2",
            programTitle = "Philharmonic Live: Brahms Symphony No. 4",
            channelName = "Second Programme Arts",
            channelNumber = 2,
            category = "Classical Music",
            recordedDate = "2 days ago",
            durationMinutes = 90,
            synopsis = "Maestro Julian Rossi conducts the Municipal Symphony Orchestra in 4K UHD Dolby 5.1.",
            fileSizeMb = 640
        ),
        TvDvrRecordItem(
            id = "dvr_3",
            programTitle = "Deep Oceans: Kermadec Trench Explorers",
            channelName = "Third Programme Discovery",
            channelNumber = 3,
            category = "Marine Science",
            recordedDate = "Last weekend",
            durationMinutes = 135,
            synopsis = "Submersible bathyscaphe expedition mapping deep oceanic trenches.",
            fileSizeMb = 820
        )
    )
}

private fun getSampleScheduleForChannelAndDay(channelNumber: Int, dayOfWeek: Int): List<TvScheduleItem> {
    // 7-day varied timetable
    return when (channelNumber) {
        1 -> listOf(
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "06:00 - 09:00", title = "Townsquare Breakfast Live", category = "Morning News", durationMinutes = 180, synopsis = "Early morning bulletins, weather radar, transit reports."),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "09:00 - 12:00", title = if (dayOfWeek % 2 == 0) "The Civic Morning Hour" else "Municipal Council Briefing", category = "Public Affairs", durationMinutes = 180, synopsis = "Council hearings and civic interviews."),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "12:00 - 13:00", title = "Midday Journal at One", category = "News", durationMinutes = 60, synopsis = "Headlines, financial markets."),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "13:00 - 17:00", title = "City Inquiry & Documentary", category = "Documentary", durationMinutes = 240, synopsis = "Investigative reports on urban history."),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "17:00 - 19:00", title = "Metropolitan Evening Report", category = "Live News", durationMinutes = 120, synopsis = "Regional commute and sports roundup."),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "19:00 - 20:00", title = "Townsquare Tonight: 19:00 Primetime", category = "Live News", durationMinutes = 60, synopsis = "Anchors Evelyn Vance and Marcus Cole report on key city milestones.", isLiveNow = (dayOfWeek == 0), isPrimeTime = true),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "20:00 - 21:00", title = "Civic Forum: Urban Architecture", category = "Current Affairs", durationMinutes = 60, synopsis = "Panel debate on city planning.", isPrimeTime = true),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "21:00 - 22:30", title = "Nightly Investigative Dossier", category = "Investigative", durationMinutes = 90, synopsis = "Renewable grid expansion analysis."),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "22:30 - 00:00", title = "Late City Edition", category = "News Round-Up", durationMinutes = 90, synopsis = "Late breaking dispatches.")
        )
        2 -> listOf(
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "10:00 - 12:00", title = "Morning Masterpiece Recital", category = "Classical", durationMinutes = 120, synopsis = "Chamber music from the Conservatory."),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "12:00 - 14:30", title = "Artisan Workshops: Ceramic & Glass", category = "Craft Arts", durationMinutes = 150, synopsis = "Master craftspeople documentary."),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "14:30 - 17:00", title = "World Cinema Archive", category = "Cinema", durationMinutes = 150, synopsis = "Restored 1950s European film prints."),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "17:00 - 19:30", title = "Shakespeare in the Square", category = "Stage Drama", durationMinutes = 150, synopsis = "The Tempest recorded live."),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "19:30 - 21:00", title = "Philharmonic Live: Brahms Symphony No. 4", category = "Live Orchestral", durationMinutes = 90, synopsis = "Maestro Rossi leads the Municipal Symphony.", isLiveNow = (dayOfWeek == 0), isPrimeTime = true),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "21:00 - 22:30", title = "Indie Screen: The Lost Artisans", category = "Documentary", durationMinutes = 90, synopsis = "Glassmakers of Murano."),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "22:30 - 00:00", title = "Midnight Jazz & Poetry Lounge", category = "Music", durationMinutes = 90, synopsis = "Underground vault acoustic jazz.")
        )
        3 -> listOf(
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "08:00 - 11:00", title = "Planetary Biomes & Flora", category = "Nature", durationMinutes = 180, synopsis = "Botanical expeditions in cloud forests."),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "11:00 - 14:00", title = "The Quantum Horizon", category = "Physics", durationMinutes = 180, synopsis = "Superconducting alloys and qubits."),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "14:00 - 16:30", title = "Architects of the Future", category = "Engineering", durationMinutes = 150, synopsis = "Zero carbon passive timber buildings."),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "16:30 - 18:45", title = "Deep Oceans: Trench Explorers", category = "Marine Science", durationMinutes = 135, synopsis = "Submersible bathyscaphe expedition."),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "18:45 - 20:15", title = "Quantum Frontiers: Spectrographic Mapping", category = "Astrophysics", durationMinutes = 90, synopsis = "Dr. Thorne reveals exoplanet spectra.", isLiveNow = (dayOfWeek == 0), isPrimeTime = true),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "20:15 - 21:15", title = "Wild Earth: Ancient Redwood Canopy", category = "Nature", durationMinutes = 60, synopsis = "Pacific coastal forest arboreal study."),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "21:15 - 23:00", title = "University Masterclass: Topology", category = "Mathematics", durationMinutes = 105, synopsis = "Lecture by Fields Medalist.")
        )
        4 -> listOf(
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "07:00 - 10:00", title = "Morning Running & Cycling Circuit", category = "Athletics", durationMinutes = 180, synopsis = "Autumn Half Marathon qualifiers."),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "10:00 - 13:00", title = "Rowing Regatta: Riverside Masters", category = "Water Sports", durationMinutes = 180, synopsis = "Eight-oar sprint duels on Grand River."),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "13:00 - 16:00", title = "Metropolitan Tennis Championships", category = "Tennis", durationMinutes = 180, synopsis = "Clay Open quarterfinal matches."),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "16:00 - 19:15", title = "Matchday Live: Pre-Game Warmups", category = "Sports Preview", durationMinutes = 195, synopsis = "Locker room interviews and tactics."),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "19:15 - 21:00", title = "Grand Metro Cup: Semifinal", category = "Live Football", durationMinutes = 105, synopsis = "Riverside FC vs Highland United clash.", isLiveNow = (dayOfWeek == 0), isPrimeTime = true),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "21:00 - 22:00", title = "Post-Match Verdict & Highlights", category = "Analysis", durationMinutes = 60, synopsis = "VAR breakdowns and player talks."),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "22:00 - 00:00", title = "Alpine Adventure Chronicle", category = "Outdoors", durationMinutes = 120, synopsis = "Mountaineering in the Karakoram.")
        )
        5 -> listOf(
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "09:00 - 12:00", title = "Vintage Detective: Inspector Roy", category = "Crime Classic", durationMinutes = 180, synopsis = "1960s black and white detective serialized mystery."),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "12:00 - 15:00", title = "Period Manor: The Gilded Estate", category = "Period Drama", durationMinutes = 180, synopsis = "Country dynasty saga."),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "15:00 - 18:00", title = "Playwrights Guild: Original One-Acts", category = "Theatre", durationMinutes = 180, synopsis = "Townsquare Theatre contemporary scripts."),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "18:00 - 20:00", title = "The French Postmaster", category = "Drama Serial", durationMinutes = 120, synopsis = "Post-war rural mystery."),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "20:00 - 21:15", title = "The Clockmaker of Saint-Germain: Pendulum", category = "Period Mystery", durationMinutes = 75, synopsis = "Laurent uncovers hidden telegraph lines.", isLiveNow = (dayOfWeek == 0), isPrimeTime = true),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "21:15 - 23:00", title = "Classic Noir: Midnight on Pont Neuf", category = "Noir Cinema", durationMinutes = 105, synopsis = "4K scan of 1949 film noir classic."),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "23:00 - 01:00", title = "Midnight Supernatural Tales", category = "Mystery Anthology", durationMinutes = 120, synopsis = "Hearthside ghost stories.")
        )
        6 -> listOf(
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "10:00 - 13:00", title = "Animation Canvas: Indie Showcase", category = "Animation", durationMinutes = 180, synopsis = "Award-winning stop motion shorts."),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "13:00 - 16:00", title = "Future Beats & Lo-Fi Lounge", category = "Electronic Music", durationMinutes = 180, synopsis = "Instrumental beats with visual art."),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "16:00 - 19:00", title = "Game Dev & Digital Craft Live", category = "Tech Culture", durationMinutes = 180, synopsis = "Indie game jam shader sessions."),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "19:00 - 22:00", title = "Neon Soundstage: Modular Synth Sessions", category = "Live Electronic", durationMinutes = 180, synopsis = "DJ Kora & Pixel Collective ambient set.", isLiveNow = (dayOfWeek == 0), isPrimeTime = true),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "22:00 - 23:30", title = "Indie Animation Winners", category = "Animation", durationMinutes = 90, synopsis = "Short films exploring surrealist cities."),
            TvScheduleItem(dayOfWeek = dayOfWeek, timeSlot = "23:30 - 02:00", title = "Afterhours Club Live: Warehouse Set", category = "DJ Mix", durationMinutes = 150, synopsis = "Underground house broadcast.")
        )
        else -> emptyList()
    }
}

private fun getSampleOnDemandVault(): List<TvOnDemandItem> {
    return listOf(
        TvOnDemandItem(
            id = "vod_1",
            channelNumber = 1,
            title = "The Mayor's Annual Civic Address & Blueprint 2030",
            episodeInfo = "Special Broadcast • S2026 E1",
            category = "Civic Affairs",
            duration = "48 min",
            description = "Keynote speech outlining green spaces, transit expansion, and public housing.",
            viewsCount = "24K views",
            airDate = "Yesterday"
        ),
        TvOnDemandItem(
            id = "vod_2",
            channelNumber = 2,
            title = "Beethoven's 9th Symphony: Ode to Joy Gala Concert",
            episodeInfo = "Live at Grand Concert Hall",
            category = "Symphony",
            duration = "1h 14m",
            description = "Featuring 120-member chorus and Municipal Symphony Orchestra under Maestro Rossi.",
            viewsCount = "51K views",
            airDate = "2 days ago"
        ),
        TvOnDemandItem(
            id = "vod_3",
            channelNumber = 3,
            title = "The James Webb Deep Field: First Galaxies Decoded",
            episodeInfo = "Cosmos Series • Ep. 8",
            category = "Astrophysics",
            duration = "52 min",
            description = "Spectroscopic analysis of proto-galaxies formed after the Big Bang.",
            viewsCount = "38K views",
            airDate = "3 days ago"
        ),
        TvOnDemandItem(
            id = "vod_4",
            channelNumber = 4,
            title = "Metropolitan Derby Highlights: 90th Minute Winner",
            episodeInfo = "Cup Quarterfinal Review",
            category = "Live Sports",
            duration = "35 min",
            description = "Complete extended match recap with commentary and multi-angle 4K replays.",
            viewsCount = "89K views",
            airDate = "Last weekend"
        ),
        TvOnDemandItem(
            id = "vod_5",
            channelNumber = 5,
            title = "The Clockmaker of Saint-Germain • Ep. 1: The Blueprint",
            episodeInfo = "Season 1 Premiere",
            category = "Period Mystery",
            duration = "58 min",
            description = "Master horologist Laurent acquires an antique clock concealing encrypted letters.",
            viewsCount = "67K views",
            airDate = "Last week"
        ),
        TvOnDemandItem(
            id = "vod_6",
            channelNumber = 6,
            title = "Modular Synth Summit 2026: Live 8-Hour Ambient Set",
            episodeInfo = "Festival Broadcast",
            category = "Electronic Beats",
            duration = "2h 10m",
            description = "12 electronic synthesizer producers jam continuous polyrhythmic ambient textures.",
            viewsCount = "43K views",
            airDate = "4 days ago"
        )
    )
}

private fun parseStartTimeMinutes(timeStr: String): Int {
    return try {
        val parts = timeStr.split(" - ")
        val startParts = parts[0].split(":")
        val h = startParts[0].toInt()
        val m = startParts[1].toInt()
        h * 60 + m
    } catch(e: Exception) {
        6 * 60
    }
}
