package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FiberDvr
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.TvChannelEntity
import com.example.data.model.TvOnDemandItem
import com.example.data.model.TvScheduleItem
import com.example.ui.components.TvBroadcastPlayer
import com.example.ui.components.LiveBroadcastCallInDialog
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonCyan

import com.example.ui.viewmodel.TvChatMessage

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
    modifier: Modifier = Modifier
) {
    val activeChannel = channels.find { it.id == activeChannelId } ?: channels.firstOrNull()
    var isCallInDialogOpen by remember { mutableStateOf(false) }

    if (isCallInDialogOpen && activeChannel != null) {
        LiveBroadcastCallInDialog(
            broadcastTitle = activeChannel.name + " - " + activeChannel.tagline,
            channelOrStation = "Channel ${activeChannel.channelNumber}: ${activeChannel.name}",
            onDismiss = { isCallInDialogOpen = false }
        )
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
            .background(MaterialTheme.colorScheme.background)
            .testTag("tv_streaming_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // 1. Station Header Bar
        item {
            TvStationHeader()
        }

        // 2. Main Live Broadcast Video Player
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

                    Spacer(modifier = Modifier.height(12.dp))

                    // Active Channel Status & Quick Details
                    ActiveChannelDetailCard(
                        channel = activeChannel,
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

        // 4. Mode Navigation Tabs
        item {
            TvSectionTabs(
                selectedTab = selectedTab,
                onSelectTab = onSetTab
            )
        }

        // 5. Section Content based on selected tab
        when (selectedTab) {
            "LIVE_CHANNELS" -> {
                item {
                    Text(
                        text = "Townsquare Central Television Roster (6 Channels)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
                    )
                }

                items(channels, key = { it.id }) { channel ->
                    TvChannelRosterCard(
                        channel = channel,
                        isSelected = (channel.id == activeChannelId),
                        onSelect = { onSelectChannel(channel.id) },
                        onToggleFavorite = { onToggleFavorite(channel) },
                        onToggleReminder = { onToggleReminder(channel) }
                    )
                }
            }

            "TV_GUIDE" -> {
                item {
                    val currentChannel = activeChannel ?: channels.firstOrNull()
                    if (currentChannel != null) {
                        EpgGuideSection(
                            channel = currentChannel,
                            channels = channels,
                            onSelectChannel = onSelectChannel,
                            onToggleReminder = onToggleReminder
                        )
                    }
                }
            }

            "ON_DEMAND" -> {
                item {
                    TvOnDemandVaultSection(
                        activeChannelNumber = activeChannel?.channelNumber ?: 1,
                        channels = channels,
                        onSelectChannel = onSelectChannel
                    )
                }
            }

            "RECORDINGS" -> {
                val recordedChannels = channels.filter { it.isRecording }
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "DVR Broadcast Library",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "High-definition recorded broadcasts and programmed series passes.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (recordedChannels.isEmpty()) {
                    item {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FiberDvr,
                                    contentDescription = "DVR",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No Active Broadcast Recordings",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Tap the ⏺️ Record button on any Townsquare Central Television programme to save it to your local DVR vault.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                } else {
                    items(recordedChannels, key = { "rec_${it.id}" }) { channel ->
                        RecordedBroadcastCard(
                            channel = channel,
                            onPlay = { onSelectChannel(channel.id) },
                            onDelete = { onToggleRecording(channel) }
                        )
                    }
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
// Sub-components
// -------------------------------------------------------------

@Composable
private fun TvStationHeader() {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
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
                        fontSize = 18.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Townsquare Central Television",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "TCTV Public Broadcast Network • 6 Live Channels",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = NeonCyan
                )
            }

            // MUX On-Air Signal Indicator
            Surface(
                color = Color(0xFF00E676).copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
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
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "MUX 100%",
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

@Composable
private fun ActiveChannelDetailCard(
    channel: TvChannelEntity,
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                            color = MaterialTheme.colorScheme.onSurface
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
                    IconButton(onClick = onPrevChannel, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Channel",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onNextChannel, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Channel",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Show Progress Bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "ON AIR: ${channel.currentShowTime}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = channelColor
                    )
                    Text(
                        text = "${(channel.currentShowProgress * 100).toInt()}% Elapsed",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { channel.currentShowProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = channelColor,
                    trackColor = channelColor.copy(alpha = 0.2f),
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Synopsis
            Text(
                text = channel.currentShowSynopsis,
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Presenter & Next Show info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎙️ ${channel.hostPresenter}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "👥 %,d Watching".format(channel.liveViewersCount),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = NeonCyan
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )

            // Interactive Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Favorite
                FilledTonalButton(
                    onClick = onToggleFavorite,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (channel.isFavorite) Color(0xFFFFD600).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = if (channel.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (channel.isFavorite) Color(0xFFFFD600) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (channel.isFavorite) "Favorited" else "Favorite",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (channel.isFavorite) Color(0xFFFFD600) else MaterialTheme.colorScheme.onSurface
                    )
                }

                // Schedule Reminder
                FilledTonalButton(
                    onClick = onToggleReminder,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (channel.isReminderSet) NeonCyan.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = if (channel.isReminderSet) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                        contentDescription = "Reminder",
                        tint = if (channel.isReminderSet) NeonCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (channel.isReminderSet) "Alert On" else "Reminder",
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
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RadioButtonChecked,
                        contentDescription = "Record",
                        tint = if (channel.isRecording) Color(0xFFFF3B30) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (channel.isRecording) "Recording" else "Record",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (channel.isRecording) Color(0xFFFF3B30) else MaterialTheme.colorScheme.onSurface
                    )
                }

                // Call-In to Live Studio
                FilledTonalButton(
                    onClick = onOpenCallIn,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = NeonCyan.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("tv_live_call_in_button")
                ) {
                    Text("📞", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Call-In",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Share
                IconButton(
                    onClick = onShare,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Stream",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
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
                text = "DIRECT TUNER",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    fontSize = 10.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Townsquare Central Television",
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
                    color = if (isSelected) chColor else MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) chColor else MaterialTheme.colorScheme.outlineVariant,
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
                            color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
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
private fun TvSectionTabs(
    selectedTab: String,
    onSelectTab: (String) -> Unit
) {
    val tabs = listOf(
        Pair("LIVE_CHANNELS", "📺 Live Roster"),
        Pair("TV_GUIDE", "📅 TV Guide"),
        Pair("ON_DEMAND", "📼 On-Demand"),
        Pair("RECORDINGS", "🔴 DVR Vault"),
        Pair("CABLE_TIERS", "📡 Cable Tiers"),
        Pair("WATCH_PARTY", "🎉 Watch Party")
    )
    val selectedIndex = tabs.indexOfFirst { it.first == selectedTab }.coerceAtLeast(0)

    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        containerColor = MaterialTheme.colorScheme.surface,
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
        tabs.forEachIndexed { index, (tabKey, label) ->
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

@Composable
private fun TvChannelRosterCard(
    channel: TvChannelEntity,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleReminder: () -> Unit
) {
    val channelColor = Color(channel.themeColorHex)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) channelColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) channelColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onSelect() }
            .testTag("tv_channel_card_${channel.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Channel Number Box
            Surface(
                color = channelColor.copy(alpha = 0.2f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .size(48.dp)
                    .border(1.dp, channelColor.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${channel.channelNumber}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = channelColor
                    )
                    Text(
                        text = channel.iconEmoji,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Channel Program & Details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = channel.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        color = channelColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = channel.callsign,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            ),
                            color = channelColor,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = "ON AIR: ${channel.currentShowTitle}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "${channel.currentShowTime} • ${channel.category}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Next: ${channel.nextShowTitle} (${channel.nextShowTime})",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = NeonCyan,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Quick Status Actions
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (isSelected) {
                    Surface(
                        color = channelColor,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "TUNED",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 9.sp
                            ),
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = onSelect,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
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
@Composable
 private fun EpgGuideSection(
    channel: TvChannelEntity,
    channels: List<TvChannelEntity>,
    onSelectChannel: (String) -> Unit,
    onToggleReminder: (TvChannelEntity) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Electronic Program Guide (EPG)",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Full broadcast timetable across Townsquare Central Television.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        val verticalScroll = rememberScrollState()
        val horizontalScroll = rememberScrollState()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                .border(1.dp, com.example.ui.theme.DarkBorder, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
        ) {
            Row(modifier = Modifier.verticalScroll(verticalScroll)) {
                // Fixed Channels Column
                Column(
                    modifier = Modifier
                        .width(110.dp)
                        .background(com.example.ui.theme.DarkBg)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(0.5.dp, com.example.ui.theme.DarkBorder),
                        contentAlignment = Alignment.Center
                    ) {
                         Text("CHANNEL", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }

                    channels.forEach { ch ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .border(0.5.dp, com.example.ui.theme.DarkBorder)
                                .clickable { onSelectChannel(ch.id) }
                                .padding(8.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Column {
                                Text(text = ch.iconEmoji, fontSize = 16.sp)
                                Text(
                                    text = "CH ${ch.channelNumber}",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(ch.themeColorHex)
                                )
                                Text(
                                    text = ch.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                // Horizontally Scrollable Timeline + Grid
                Box {
                    Column(modifier = Modifier.horizontalScroll(horizontalScroll)) {
                        // Timeline
                        Row(modifier = Modifier.height(40.dp)) {
                            for (hour in 6..24) {
                                Box(
                                    modifier = Modifier
                                        .width(240.dp) // 1 hour = 240dp (4dp / min)
                                        .fillMaxHeight()
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .border(0.5.dp, com.example.ui.theme.DarkBorder),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        text = String.format("%02d:00", hour),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }

                        // Program Rows
                        channels.forEach { ch ->
                            Row(modifier = Modifier.height(80.dp).border(0.5.dp, com.example.ui.theme.DarkBorder)) {
                                val schedule = getSampleScheduleForChannel(ch.channelNumber)
                                
                                if (schedule.isNotEmpty()) {
                                    // Calculate initial spacer if the first show doesn't start at 06:00
                                    val firstStart = parseStartTimeMinutes(schedule.first().timeSlot)
                                    val offsetMins = maxOf(firstStart - 360, 0)
                                    if (offsetMins > 0) {
                                        Spacer(modifier = Modifier.width((offsetMins * 4).dp))
                                    }
                                }

                                schedule.forEach { item ->
                                    val width = (item.durationMinutes * 4).dp
                                    Box(
                                        modifier = Modifier
                                            .width(width)
                                            .fillMaxHeight()
                                            .border(0.5.dp, com.example.ui.theme.DarkBorder)
                                            .background(if (item.isLiveNow) Color(ch.themeColorHex).copy(alpha = 0.2f) else Color.Transparent)
                                            .padding(4.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = item.title,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = if (item.isLiveNow) Color(ch.themeColorHex) else MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = item.timeSlot,
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            if (item.isLiveNow) {
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Surface(
                                                    color = Color.Red,
                                                    shape = RoundedCornerShape(2.dp)
                                                ) {
                                                    Text(
                                                        text = "LIVE",
                                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Black),
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

                    // Current Time Indicator (Red Line)
                    // Assuming current time is around 19:30 for simulation
                    val currentTimeMins = 19 * 60 + 30
                    val indicatorOffset = (currentTimeMins - 360) * 4
                    if (indicatorOffset > 0) {
                        Box(
                            modifier = Modifier
                                .padding(start = indicatorOffset.dp)
                                .width(2.dp)
                                .fillMaxHeight()
                                .background(Color.Red)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TvOnDemandVaultSection(
    activeChannelNumber: Int,
    channels: List<TvChannelEntity>,
    onSelectChannel: (String) -> Unit
) {
    val onDemandList = getSampleOnDemandVault()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "TCTV On-Demand & Catch-Up Vault",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Stream past episodes, symphony performances, and sporting broadcasts on demand.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        onDemandList.forEach { item ->
            val ch = channels.find { it.channelNumber == item.channelNumber }
            val color = Color(ch?.themeColorHex ?: 0xFF00D2FF)

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .clickable {
                        ch?.let { onSelectChannel(it.id) }
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = color.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .size(56.dp)
                            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = ch?.iconEmoji ?: "📺", fontSize = 22.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                color = color.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "CH ${item.channelNumber} • ${ch?.name ?: ""}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = color,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = item.airDate,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "${item.episodeInfo} • ${item.duration}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 16.sp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    IconButton(
                        onClick = { ch?.let { onSelectChannel(it.id) } },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LiveTv,
                            contentDescription = "Stream Episode",
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecordedBroadcastCard(
    channel: TvChannelEntity,
    onPlay: () -> Unit,
    onDelete: () -> Unit
) {
    val color = Color(channel.themeColorHex)
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color(0xFFFF3B30).copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(44.dp)
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
                    text = "${channel.name} (CH ${channel.channelNumber})",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                )
                Text(
                    text = channel.currentShowTitle,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Saved in 4K UHD • Recorded 19:00 Today",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                FilledTonalButton(
                    onClick = onPlay,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Play", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "Remove DVR",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Sample Timetable Helpers
// -------------------------------------------------------------

private fun getSampleScheduleForChannel(channelNumber: Int): List<TvScheduleItem> {
    return when (channelNumber) {
        1 -> listOf(
            TvScheduleItem("06:00 - 09:00", "Townsquare Breakfast Live", "Morning News", 180, "Early morning bulletins, weather radar, and commuter transit reports."),
            TvScheduleItem("09:00 - 12:00", "The Civic Morning Hour", "Public Affairs", 180, "Council interviews, neighborhood initiatives, and economic updates."),
            TvScheduleItem("12:00 - 13:00", "Midday Journal at One", "News", 60, "National and international headlines, financial market summaries."),
            TvScheduleItem("13:00 - 17:00", "City Inquiry & Documentary", "Documentary", 240, "In-depth investigative reports on urban architecture and civic history."),
            TvScheduleItem("17:00 - 19:00", "Metropolitan Evening Report", "Live News", 120, "Commute digest, regional headlines, and sports preview."),
            TvScheduleItem("19:00 - 20:00", "Townsquare Tonight: 19:00 Primetime Journal", "Live News", 60, "Anchors Evelyn Vance and Marcus Cole report on key city milestones.", isLiveNow = true, isPrimeTime = true),
            TvScheduleItem("20:00 - 21:00", "The Civic Forum: Urban Heritage Debate", "Current Affairs", 60, "Panel discussion on historic preservation vs modernization.", isPrimeTime = true),
            TvScheduleItem("21:00 - 22:30", "Nightly Investigative Dossier", "Investigative", 90, "Deep dive into regional renewable energy and grid expansion."),
            TvScheduleItem("22:30 - 00:00", "Late City Edition", "News Round-Up", 90, "Final dispatch of today's events, tomorrow's weather outlook.")
        )
        2 -> listOf(
            TvScheduleItem("10:00 - 12:00", "Morning Masterpiece Recital", "Classical Music", 120, "Chamber music from the Townsquare Conservatory of Music."),
            TvScheduleItem("12:00 - 14:30", "Artisan Workshops: Ceramic & Glass", "Craft Arts", 150, "Documentary spotlighting master glassblowers and ceramicists."),
            TvScheduleItem("14:30 - 17:00", "World Cinema Archive", "Cinema", 150, "Restored 1950s cinematic prints from the European film vault."),
            TvScheduleItem("17:00 - 19:30", "Shakespeare in the Square", "Stage Drama", 150, "Full theatrical performance of The Tempest recorded live."),
            TvScheduleItem("19:30 - 21:00", "Philharmonic Live: Brahms Symphony No. 4", "Live Orchestral", 90, "Maestro Julian Rossi leads the Municipal Symphony Orchestra.", isLiveNow = true, isPrimeTime = true),
            TvScheduleItem("21:00 - 22:30", "Indie Screen: The Lost Artisans of Murano", "Documentary", 90, "Exploration of centuries-old craft traditions in the Venetian lagoon.", isPrimeTime = true),
            TvScheduleItem("22:30 - 00:00", "Midnight Jazz & Poetry Lounge", "Music & Verse", 90, "Live acoustic jazz quartet from the Underground Vault.")
        )
        3 -> listOf(
            TvScheduleItem("08:00 - 11:00", "Planetary Biomes & Flora", "Nature", 180, "High-definition botanical exploration across tropical cloud forests."),
            TvScheduleItem("11:00 - 14:00", "The Quantum Horizon", "Physics & Tech", 180, "Breakdowns in quantum entanglement and superconducting alloys."),
            TvScheduleItem("14:00 - 16:30", "Architects of the Future", "Design & Engineering", 150, "Modern passive housing, mass timber skyscrapers, and zero-carbon grids."),
            TvScheduleItem("16:30 - 18:45", "Deep Oceans: Trench Explorers", "Marine Science", 135, "Submersible footage from the Mariana and Kermadec ocean trenches."),
            TvScheduleItem("18:45 - 20:15", "Quantum Frontiers: Spectrographic Mapping of Exoplanets", "Astrophysics", 90, "Dr. Aris Thorne reveals deep space atmospheric spectra.", isLiveNow = true, isPrimeTime = true),
            TvScheduleItem("20:15 - 21:15", "Wild Earth: Secrets of the Ancient Redwood Canopy", "Nature", 60, "Arboreal canopy ecology in the Pacific coastal rain forests.", isPrimeTime = true),
            TvScheduleItem("21:15 - 23:00", "University Masterclass: Theoretical Mathematics", "Higher Ed", 105, "Lecture by visiting Fields Medalist on topology and geometry.")
        )
        4 -> listOf(
            TvScheduleItem("07:00 - 10:00", "Morning Running & Cycling Circuit", "Athletics", 180, "Coverage of the Annual Autumn Half Marathon qualifiers."),
            TvScheduleItem("10:00 - 13:00", "Rowing Regatta: Riverside Masters", "Water Sports", 180, "Eight-oar sprint duels on the Grand River Canal course."),
            TvScheduleItem("13:00 - 16:00", "Metropolitan Tennis Championships", "Tennis", 180, "Center Court Quarterfinal matches from the Clay Open."),
            TvScheduleItem("16:00 - 19:15", "Matchday Live: Pre-Game Warmups", "Sports Preview", 195, "Locker room interviews, tactical whiteboards, and stadium fan cam."),
            TvScheduleItem("19:15 - 21:00", "Grand Metro Cup: Riverside FC vs. Highland United", "Live Football", 105, "Electric semifinal cup battle with 2-1 second-half tension.", isLiveNow = true, isPrimeTime = true),
            TvScheduleItem("21:00 - 22:00", "Post-Match Verdict & Night League Highlights", "Analysis", 60, "Detailed slow-motion VAR breakdowns and player interviews.", isPrimeTime = true),
            TvScheduleItem("22:00 - 00:00", "Extreme Alpine Adventure Chronicle", "Outdoors", 120, "High altitude mountaineering in the Karakoram range.")
        )
        5 -> listOf(
            TvScheduleItem("09:00 - 12:00", "Vintage Detective Theatre: Inspector Roy", "Crime Classic", 180, "1960s black and white detective serialized mystery."),
            TvScheduleItem("12:00 - 15:00", "Period Manor: The Gilded Estate", "Period Drama", 180, "Family dynasty saga set in the turn-of-the-century English countryside."),
            TvScheduleItem("15:00 - 18:00", "Playwrights Guild: Original One-Acts", "Original Theatre", 180, "Contemporary scripts performed by the Townsquare Theatre Troupe."),
            TvScheduleItem("18:00 - 20:00", "The French Postmaster • Episodes 1-3", "Drama Serial", 120, "Post-war rural postal service mystery in Provence."),
            TvScheduleItem("20:00 - 21:15", "The Clockmaker of Saint-Germain • Ep. 4: The Pendulum", "Period Mystery", 75, "Master horologist Laurent uncovers hidden diplomatic telegraph lines.", isLiveNow = true, isPrimeTime = true),
            TvScheduleItem("21:15 - 23:00", "Classic Vault: Midnight on the Pont Neuf (1949 Remastered)", "Noir Cinema", 105, "4K scan of the influential French film noir masterpiece.", isPrimeTime = true),
            TvScheduleItem("23:00 - 01:00", "Midnight Supernatural Tales", "Mystery Anthology", 120, "Eerie atmospheric ghost stories told by candlelit hearth.")
        )
        6 -> listOf(
            TvScheduleItem("10:00 - 13:00", "Animation Canvas: Indie Showcase", "Animation", 180, "Award-winning stop motion and hand-drawn animated shorts."),
            TvScheduleItem("13:00 - 16:00", "Future Beats & Lo-Fi Lounge", "Electronic Music", 180, "Chill instrumental beatmakers with generative animated backdrops."),
            TvScheduleItem("16:00 - 19:00", "Game Dev & Digital Craft Live", "Tech Culture", 180, "Indie studio game jams and shader development workshops."),
            TvScheduleItem("19:00 - 22:00", "Neon Soundstage: Live Modular Synth Sessions & Visuals", "Live Electronic", 180, "DJ Kora & Pixel Collective live ambient techno & projection mapping.", isLiveNow = true, isPrimeTime = true),
            TvScheduleItem("22:00 - 23:30", "Indie Animation Showcase: Tokyo & Bristol Festival Winners", "Animation", 90, "Short films exploring surrealist landscapes and cyberpunk cities.", isPrimeTime = true),
            TvScheduleItem("23:30 - 02:00", "Afterhours Club Live: The Warehouse Set", "DJ Mix Live", 150, "Underground deep house broadcast direct from the Industrial Pier.")
        )
        7 -> listOf(
            TvScheduleItem("10:00 - 13:00", "Morning Dev Session: Refactoring Legacy Code", "Coding", 180, "Deep dive into clean architecture principles."),
            TvScheduleItem("13:00 - 16:00", "Community Code Review", "Education", 180, "Reviewing pull requests from the Townsquare open source community."),
            TvScheduleItem("16:00 - 19:00", "System Design: Scaling to Millions", "Tech Talk", 180, "Architectural patterns for high-concurrency civic platforms."),
            TvScheduleItem("19:00 - 21:00", "Live Coding: Building the Future Media Superapp", "Programming Live", 120, "Alex is live coding a new modular UI system.", isLiveNow = true, isPrimeTime = true),
            TvScheduleItem("21:00 - 22:00", "AMA: Career in Computational Architecture", "Career", 60, "Ask me anything about tech careers.", isPrimeTime = true)
        )
        8 -> listOf(
            TvScheduleItem("08:00 - 12:00", "Morning Mist: Ambient Focus", "Ambient", 240, "Gentle morning textures."),
            TvScheduleItem("12:00 - 15:00", "Midday Solar Drift", "Music", 180, "Warm acoustic grooves."),
            TvScheduleItem("15:00 - 18:00", "Afternoon Rain: Field Recordings", "Nature", 180, "Soothing rain sounds from around the world."),
            TvScheduleItem("18:00 - 20:00", "Golden Hour Ambient Session", "Ambient Music", 120, "Relaxing textures as the sun sets.", isLiveNow = true, isPrimeTime = true),
            TvScheduleItem("20:00 - 22:00", "Midnight Echoes: Deep Focus Dub", "Music", 120, "Late night deep focus beats.", isPrimeTime = true)
        )
        else -> emptyList()
    }
}

private fun getSampleOnDemandVault(): List<TvOnDemandItem> {
    return listOf(
        TvOnDemandItem(
            id = "vod_1",
            channelNumber = 1,
            title = "The Mayor's Annual Civic Address & Urban Blueprint 2030",
            episodeInfo = "Special Broadcast • S2026 E1",
            category = "Civic Affairs",
            duration = "48 min",
            description = "Complete keynote speech outlining city green spaces, rapid transit expansion, and public housing grants.",
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
            description = "Monumental performance featuring 120-member chorus and the Municipal Symphony Orchestra under Maestro Rossi.",
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
            description = "Spectroscopic analysis of proto-galaxies formed 300 million years after the Big Bang.",
            viewsCount = "38K views",
            airDate = "3 days ago"
        ),
        TvOnDemandItem(
            id = "vod_4",
            channelNumber = 4,
            title = "Metropolitan Derby Highlights: 90th Minute Stoppage Winner",
            episodeInfo = "Cup Quarterfinal Review",
            category = "Live Sports",
            duration = "35 min",
            description = "Complete extended match recap with commentary from Leo Sterling and multi-angle 4K replays.",
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
            description = "Master horologist Laurent arrives in Paris and acquires an antique clock concealing encrypted letters.",
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
            description = "12 electronic synthesizer producers jam continuous polyrhythmic ambient textures with live laser visualizers.",
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
