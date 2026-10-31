package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.AudioState
import com.example.audio.NarrationStyle
import com.example.audio.VoiceNarrationState
import com.example.data.model.MediaType
import com.example.data.model.TvChannelEntity
import com.example.data.model.TvScheduleItem
import com.example.ui.components.player.*
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber
import com.example.ui.viewmodel.TvChatMessage

/**
 * Consolidated Townsquare Global Media Player
 * Refactored into 5 distinct mode states with dedicated component files:
 * 1. Audio: Music streaming, podcasts, device audio files (GlobalAudioPlayerMode.kt)
 * 2. Video: Device video files, video hosting platforms (GlobalVideoPlayerMode.kt)
 * 3. Image: Image editing, filters, and high-res viewing (GlobalImageViewerMode.kt)
 * 4. TV: Live TV broadcasts & livestreams with chatting (GlobalTvPlayerMode.kt)
 * 5. Radio: Radio tuner dial, presets, live recording & subtitles (GlobalRadioPlayerMode.kt)
 */
@Composable
fun GlobalMediaPlayer(
    audioState: AudioState,
    narrationState: VoiceNarrationState,
    onExpandFullPlayer: () -> Unit,
    onTogglePlayPauseAudio: () -> Unit,
    onStopAudio: () -> Unit,
    onTogglePlayPauseNarration: () -> Unit,
    onStopNarration: () -> Unit,
    onNextParagraph: () -> Unit,
    onPreviousParagraph: () -> Unit,
    onSelectNarrationStyle: (NarrationStyle) -> Unit,
    onSetNarrationSpeed: (Float) -> Unit,
    activeTvChannel: TvChannelEntity? = null,
    allTvChannels: List<TvChannelEntity> = emptyList(),
    isTvPlaying: Boolean = false,
    isTvMuted: Boolean = false,
    isTvCaptionsEnabled: Boolean = true,
    tvStreamQuality: String = "4K UHD 60FPS",
    tvScanlineFxEnabled: Boolean = false,
    onToggleTvPlayPause: () -> Unit = {},
    onToggleTvMute: () -> Unit = {},
    onToggleTvCaptions: () -> Unit = {},
    onToggleTvScanlineFx: () -> Unit = {},
    onSelectTvQuality: (String) -> Unit = {},
    onNextTvChannel: () -> Unit = {},
    onPrevTvChannel: () -> Unit = {},
    onSelectTvChannel: (String) -> Unit = {},
    onStopTv: () -> Unit = {},
    onRecordEpisode: (TvScheduleItem) -> Unit = {},
    onScheduleReminder: (TvScheduleItem) -> Unit = {},
    chatMessages: List<TvChatMessage> = emptyList(),
    onSendChatMessage: (String) -> Unit = {},
    initialMode: GlobalMediaMode? = null,
    modifier: Modifier = Modifier
) {
    // Current Media Player Mode State
    var currentMode by remember {
        mutableStateOf(
            initialMode ?: when {
                activeTvChannel != null && isTvPlaying -> GlobalMediaMode.TV
                audioState.currentItem != null -> GlobalMediaMode.AUDIO
                else -> GlobalMediaMode.AUDIO
            }
        )
    }

    // Auto-sync mode when TV or Audio starts playing
    LaunchedEffect(isTvPlaying, activeTvChannel) {
        if (isTvPlaying && activeTvChannel != null) {
            currentMode = GlobalMediaMode.TV
        }
    }

    LaunchedEffect(audioState.currentItem, audioState.isPlaying) {
        if (audioState.isPlaying && audioState.currentItem != null && !isTvPlaying) {
            currentMode = GlobalMediaMode.AUDIO
        }
    }

    var isPlayerVisible by remember { mutableStateOf(true) }
    var isExpandedMode by remember { mutableStateOf(false) }

    if (!isPlayerVisible) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("global_media_player_root")
    ) {
        // Voice Narration Special Overlay (if currently active)
        if (narrationState.isNarrating) {
            GlobalVoiceNarrationPlayerView(
                state = narrationState,
                onTogglePlayPause = onTogglePlayPauseNarration,
                onStop = onStopNarration,
                onNextParagraph = onNextParagraph,
                onPreviousParagraph = onPreviousParagraph,
                onSelectStyle = onSelectNarrationStyle,
                onSetSpeed = onSetNarrationSpeed
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0B132B),
            border = BorderStroke(1.dp, currentMode.accentColor.copy(alpha = 0.5f)),
            shadowElevation = 10.dp
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                // 5-MODE NAVIGATION SWITCHER BAR
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    GlobalMediaMode.values().forEach { mode ->
                        val isSelected = currentMode == mode
                        val bgColor = if (isSelected) mode.accentColor.copy(alpha = 0.25f) else Color(0xFF1E293B)
                        val borderColor = if (isSelected) mode.accentColor else Color.Transparent
                        val contentColor = if (isSelected) mode.accentColor else Color.LightGray

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(bgColor)
                                .border(1.dp, borderColor, RoundedCornerShape(10.dp))
                                .clickable {
                                    currentMode = mode
                                    isExpandedMode = true
                                }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(mode.iconEmoji, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = mode.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = contentColor
                                )
                            }
                        }
                    }
                }

                // MINI SUMMARY BAR (When not expanded into full mode view)
                if (!isExpandedMode) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF162032))
                            .clickable { isExpandedMode = true }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            // Mode Icon Badge
                            Surface(
                                shape = CircleShape,
                                color = currentMode.accentColor.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, currentMode.accentColor),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(currentMode.iconEmoji, fontSize = 16.sp)
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                val titleText = when (currentMode) {
                                    GlobalMediaMode.AUDIO -> audioState.currentItem?.title ?: "Townsquare Audio Stream"
                                    GlobalMediaMode.VIDEO -> "Townsquare 4K Video Showcase"
                                    GlobalMediaMode.IMAGE -> "Townsquare Visual Wire & Editor"
                                    GlobalMediaMode.TV -> activeTvChannel?.currentShowTitle ?: "Townsquare Central Live TV"
                                    GlobalMediaMode.RADIO -> "101.4 FM • Townsquare Central Live"
                                }

                                val subText = when (currentMode) {
                                    GlobalMediaMode.AUDIO -> "🎙️ ${audioState.currentItem?.authorName ?: "Music & Podcasts"}"
                                    GlobalMediaMode.VIDEO -> "🎬 1080p FHD • Townsquare Tube"
                                    GlobalMediaMode.IMAGE -> "🖼️ Photojournalism & Creative Studio"
                                    GlobalMediaMode.TV -> "📺 ${activeTvChannel?.name ?: "Channel 1"}"
                                    GlobalMediaMode.RADIO -> "📻 Stereo FM • Live Broadcast"
                                }

                                Text(
                                    text = titleText,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = subText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = currentMode.accentColor,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Right Action Buttons: Play/Pause, Expand, Close
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    when (currentMode) {
                                        GlobalMediaMode.AUDIO -> onTogglePlayPauseAudio()
                                        GlobalMediaMode.TV -> onToggleTvPlayPause()
                                        else -> {}
                                    }
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(currentMode.accentColor)
                            ) {
                                val isCurrentlyPlaying = when (currentMode) {
                                    GlobalMediaMode.AUDIO -> audioState.isPlaying
                                    GlobalMediaMode.TV -> isTvPlaying
                                    else -> true
                                }
                                Icon(
                                    imageVector = if (isCurrentlyPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Toggle Play",
                                    tint = Color(0xFF0F172A),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            IconButton(
                                onClick = { isExpandedMode = true },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ExpandLess,
                                    contentDescription = "Expand Mode Player",
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    onStopAudio()
                                    onStopTv()
                                    isPlayerVisible = false
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss Player",
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // EXPANDED MODE VIEW - DEDICATED COMPONENT FILES
                AnimatedVisibility(
                    visible = isExpandedMode,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Collapse bar handle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isExpandedMode = false }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = "Minimize to bar",
                                tint = currentMode.accentColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Minimize to Player Bar",
                                fontSize = 11.sp,
                                color = currentMode.accentColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        when (currentMode) {
                            GlobalMediaMode.AUDIO -> {
                                GlobalAudioPlayerMode(
                                    audioState = audioState,
                                    onTogglePlayPause = onTogglePlayPauseAudio,
                                    onClose = { isExpandedMode = false },
                                    onExpandFullPlayer = onExpandFullPlayer
                                )
                            }
                            GlobalMediaMode.VIDEO -> {
                                GlobalVideoPlayerMode(
                                    onClose = { isExpandedMode = false }
                                )
                            }
                            GlobalMediaMode.IMAGE -> {
                                GlobalImageViewerMode(
                                    onClose = { isExpandedMode = false }
                                )
                            }
                            GlobalMediaMode.TV -> {
                                GlobalTvPlayerMode(
                                    activeChannel = activeTvChannel,
                                    allChannels = allTvChannels,
                                    isPlaying = isTvPlaying,
                                    isMuted = isTvMuted,
                                    isCaptionsEnabled = isTvCaptionsEnabled,
                                    streamQuality = tvStreamQuality,
                                    scanlineFxEnabled = tvScanlineFxEnabled,
                                    chatMessages = chatMessages,
                                    onSendMessage = onSendChatMessage,
                                    onTogglePlayPause = onToggleTvPlayPause,
                                    onToggleMute = onToggleTvMute,
                                    onToggleCaptions = onToggleTvCaptions,
                                    onToggleScanlineFx = onToggleTvScanlineFx,
                                    onSelectQuality = onSelectTvQuality,
                                    onNextChannel = onNextTvChannel,
                                    onPrevChannel = onPrevTvChannel,
                                    onSelectChannel = onSelectTvChannel,
                                    onClose = { isExpandedMode = false }
                                )
                            }
                            GlobalMediaMode.RADIO -> {
                                GlobalRadioPlayerMode(
                                    onClose = { isExpandedMode = false }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Dedicated Voice Narration Player View
 */
@Composable
private fun GlobalVoiceNarrationPlayerView(
    state: VoiceNarrationState,
    onTogglePlayPause: () -> Unit,
    onStop: () -> Unit,
    onNextParagraph: () -> Unit,
    onPreviousParagraph: () -> Unit,
    onSelectStyle: (NarrationStyle) -> Unit,
    onSetSpeed: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .testTag("global_media_player_narration"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131924)),
        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(state.style.emoji, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "AI Narration • ${state.style.title}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = NeonCyan
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onPreviousParagraph, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.SkipPrevious, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onTogglePlayPause, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = if (state.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onNextParagraph, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.SkipNext, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onStop, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Text(
                text = state.currentItemTitle.ifBlank { "Townsquare Dispatch" },
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
