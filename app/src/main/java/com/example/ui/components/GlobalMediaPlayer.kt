package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
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
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber

/**
 * Consolidated Global Media Player
 * Handles audio playback (podcasts, radio, dispatches) and AI voice narration
 * within a single unified bar component.
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
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
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
        } else if (audioState.currentItem != null) {
            GlobalAudioPlayerView(
                audioState = audioState,
                onExpandFullPlayer = onExpandFullPlayer,
                onTogglePlayPause = onTogglePlayPauseAudio,
                onClose = onStopAudio
            )
        }
    }
}

@Composable
private fun GlobalAudioPlayerView(
    audioState: AudioState,
    onExpandFullPlayer: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val current = audioState.currentItem ?: return

    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .clickable { onExpandFullPlayer() }
                .testTag("global_media_player_audio"),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Thumbnail
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    val coverRes = if (current.type == MediaType.RADIO_STATION.name) {
                        R.drawable.img_radio_live
                    } else {
                        R.drawable.img_podcast_cover
                    }
                    Image(
                        painter = painterResource(id = coverRes),
                        contentDescription = "Cover",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Titles and dynamic visualizer
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = current.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (current.type == MediaType.RADIO_STATION.name)
                                "📻 ${current.stationFrequency.ifBlank { "Live Station" }}"
                            else
                                "🎙️ ${current.authorName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeonCyan,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (audioState.isPlaying) {
                            Spacer(modifier = Modifier.width(8.dp))
                            // Equalizer wave mini
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                verticalAlignment = Alignment.Bottom,
                                modifier = Modifier.height(12.dp)
                            ) {
                                audioState.waveformHeights.take(4).forEach { wave ->
                                    Box(
                                        modifier = Modifier
                                            .width(3.dp)
                                            .height((wave * 12).dp.coerceAtLeast(3.dp))
                                            .clip(RoundedCornerShape(1.dp))
                                            .background(NeonCyan)
                                    )
                                }
                            }
                        }
                    }
                }

                // Controls
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onTogglePlayPause,
                        modifier = Modifier
                            .size(38.dp)
                            .background(NeonCyan, CircleShape)
                            .testTag("global_audio_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (audioState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (audioState.isPlaying) "Pause" else "Play",
                            tint = Color(0xFF003544),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Player",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

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
    val haptic = LocalHapticFeedback.current

    AnimatedVisibility(
        visible = state.isNarrating,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .shadow(12.dp, RoundedCornerShape(16.dp))
                .testTag("global_media_player_narration"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF131924)
            ),
            border = BorderStroke(
                1.dp,
                when (state.style) {
                    NarrationStyle.BREAKING_NEWS -> Color(0xFFE53935)
                    NarrationStyle.PODCAST -> NeonCyan
                    NarrationStyle.LITERARY_MAGAZINE -> WarmAmber
                }.copy(alpha = 0.6f)
            )
        ) {
            Column {
                // Top Progress indicator
                val progress = if (state.totalParagraphs > 0) {
                    ((state.currentParagraphIndex + 1).toFloat() / state.totalParagraphs.toFloat()).coerceIn(0f, 1f)
                } else 0f

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = when (state.style) {
                        NarrationStyle.BREAKING_NEWS -> Color(0xFFE53935)
                        NarrationStyle.PODCAST -> NeonCyan
                        NarrationStyle.LITERARY_MAGAZINE -> WarmAmber
                    },
                    trackColor = Color(0xFF222B3D)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left: Style Avatar + Title & Progress
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = when (state.style) {
                                NarrationStyle.BREAKING_NEWS -> Color(0xFF4A1010)
                                NarrationStyle.PODCAST -> Color(0xFF003644)
                                NarrationStyle.LITERARY_MAGAZINE -> Color(0xFF3B2B11)
                            },
                            border = BorderStroke(
                                1.dp,
                                when (state.style) {
                                    NarrationStyle.BREAKING_NEWS -> Color(0xFFE53935)
                                    NarrationStyle.PODCAST -> NeonCyan
                                    NarrationStyle.LITERARY_MAGAZINE -> WarmAmber
                                }
                            ),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(state.style.emoji, fontSize = 16.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = state.style.title,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = when (state.style) {
                                        NarrationStyle.BREAKING_NEWS -> Color(0xFFFF6B6B)
                                        NarrationStyle.PODCAST -> NeonCyan
                                        NarrationStyle.LITERARY_MAGAZINE -> WarmAmber
                                    }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• Sec ${state.currentParagraphIndex + 1}/${state.totalParagraphs}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.LightGray
                                )
                            }

                            Text(
                                text = state.currentItemTitle.ifBlank { "Townsquare Dispatch" },
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            // Animated Waveform Mini
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                state.waveformHeights.forEach { h ->
                                    val barHeight = if (state.isPaused) 4.dp else (h * 12).coerceAtLeast(3f).dp
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(barHeight)
                                            .background(
                                                color = if (state.isPaused) Color.Gray else NeonCyan,
                                                shape = RoundedCornerShape(1.dp)
                                            )
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${state.speed}x speed",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    // Right: Media Controls
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onPreviousParagraph()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipPrevious,
                                contentDescription = "Previous Section",
                                tint = Color.LightGray,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Play/Pause
                        Surface(
                            shape = CircleShape,
                            color = NeonCyan,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onTogglePlayPause()
                                }
                                .testTag("voice_play_pause_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (state.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                    contentDescription = if (state.isPaused) "Resume Narration" else "Pause Narration",
                                    tint = Color(0xFF003442),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onNextParagraph()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Next Section",
                                tint = Color.LightGray,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onStop()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Player",
                                tint = Color.LightGray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
