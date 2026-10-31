package com.example.ui.components.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.data.model.MediaItemEntity
import com.example.data.model.MediaType
import com.example.ui.components.SleepTimerController
import com.example.ui.components.SleepTimerDialog
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber

/**
 * Global Media Player - Mode 1: Audio
 * Handles Music Streaming, Podcasts, and Device Storage Audio Files.
 */
@Composable
fun GlobalAudioPlayerMode(
    audioState: AudioState,
    onTogglePlayPause: () -> Unit,
    onClose: () -> Unit,
    onExpandFullPlayer: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedAudioSubTab by remember { mutableIntStateOf(0) } // 0: Now Playing, 1: Music Streaming, 2: Podcasts, 3: Device Files
    var isSleepTimerDialogOpen by remember { mutableStateOf(false) }
    var playbackSpeed by remember { mutableFloatStateOf(audioState.speed.coerceAtLeast(1.0f)) }
    var isQueueExpanded by remember { mutableStateOf(false) }

    // Sample Device Audio Files for device scanning demonstration
    val deviceAudioFiles = remember {
        listOf(
            DeviceAudioTrack("dev_1", "townsquare_field_recording_01.mp3", "Townsquare Field Ambient Mix", "Civic Media Labs", "Internal Storage/Music", 240, "MP3 • 320kbps", 9400000L),
            DeviceAudioTrack("dev_2", "retro_synthwave_nightdrive.flac", "Nightdrive (Modular Synth Jam)", "DJ Neon Pulse", "Internal Storage/Audio", 312, "FLAC Lossless", 28400000L),
            DeviceAudioTrack("dev_3", "town_hall_meeting_oct28.m4a", "Oct 28 Town Council Q&A Session", "Municipal Records", "Internal Storage/Recordings", 1840, "AAC Voice", 32000000L),
            DeviceAudioTrack("dev_4", "acoustic_patio_coffee.wav", "Sunday Acoustic Coffee Session", "Central Café Live", "Internal Storage/Downloads", 195, "WAV 24-bit 48kHz", 42000000L)
        )
    }

    if (isSleepTimerDialogOpen) {
        SleepTimerDialog(
            onDismiss = { isSleepTimerDialogOpen = false },
            onSetTimer = { mins, label ->
                SleepTimerController.startTimer(mins, label)
            }
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("global_audio_player_mode"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Mode Badge & Source Switchers
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(NeonCyan.copy(alpha = 0.15f))
                            .border(1.dp, NeonCyan, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "🎵 AUDIO HUB",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = NeonCyan
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (selectedAudioSubTab) {
                            0 -> "Now Playing"
                            1 -> "Music Streaming"
                            2 -> "Podcasts"
                            else -> "Device Storage Files"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.LightGray
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Sleep timer
                    IconButton(
                        onClick = { isSleepTimerDialogOpen = true },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("audio_mode_sleep_timer_btn")
                    ) {
                        Text(
                            text = if (SleepTimerController.isTimerActive) "🌙 ${SleepTimerController.remainingSeconds / 60}m" else "🌙",
                            fontSize = if (SleepTimerController.isTimerActive) 11.sp else 14.sp,
                            color = if (SleepTimerController.isTimerActive) WarmAmber else Color.LightGray,
                            fontWeight = if (SleepTimerController.isTimerActive) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    // Expand full player
                    IconButton(
                        onClick = onExpandFullPlayer,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInFull,
                            contentDescription = "Expand Full Audio Sheet",
                            tint = Color.LightGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Close
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Audio Player",
                            tint = Color.LightGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sub-category selector tabs (Now Playing, Music, Podcasts, Device Files)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    Pair(0, "▶ Now Playing"),
                    Pair(1, "🎶 Music"),
                    Pair(2, "🎙️ Podcasts"),
                    Pair(3, "📁 Device Files")
                ).forEach { (idx, label) ->
                    val isSelected = selectedAudioSubTab == idx
                    val bg = if (isSelected) NeonCyan else Color(0xFF1E293B)
                    val txt = if (isSelected) Color(0xFF00323D) else Color.White

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(bg)
                            .clickable { selectedAudioSubTab = idx }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = txt,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (selectedAudioSubTab) {
                0 -> {
                    // NOW PLAYING VIEW
                    val current = audioState.currentItem
                    val currentTitle = current?.title ?: "Townsquare Music Stream"
                    val currentArtist = current?.authorName ?: "Townsquare Broadcast"
                    val isRadio = current?.type == MediaType.RADIO_STATION.name

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Album Art / Visualizer Box
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF020617))
                                .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            val coverRes = if (isRadio) R.drawable.img_radio_live else R.drawable.img_podcast_cover
                            Image(
                                painter = painterResource(id = coverRes),
                                contentDescription = "Audio Art",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentTitle,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isRadio) "📻 ${current?.stationFrequency ?: "98.5 FM"}" else "🎙️ $currentArtist",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NeonCyan,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• ${playbackSpeed}x",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.LightGray
                                )
                            }

                            // Dynamic Live Audio Spectrum / Waveform
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(14.dp)
                                    .padding(top = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                verticalAlignment = Alignment.Bottom
                            ) {
                                audioState.waveformHeights.forEach { wave ->
                                    val barHeight = if (audioState.isPlaying) (wave * 14).dp.coerceAtLeast(3.dp) else 3.dp
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(barHeight)
                                            .clip(RoundedCornerShape(1.dp))
                                            .background(if (audioState.isPlaying) NeonCyan else Color.Gray)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Play/Pause Main Button
                        IconButton(
                            onClick = onTogglePlayPause,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(NeonCyan)
                                .testTag("audio_mode_play_pause_btn")
                        ) {
                            Icon(
                                imageVector = if (audioState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (audioState.isPlaying) "Pause" else "Play",
                                tint = Color(0xFF003442),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Secondary controls: Speed changer & Queue toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf(0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speedOption ->
                                val active = playbackSpeed == speedOption
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (active) NeonCyan.copy(alpha = 0.25f) else Color(0xFF1E293B))
                                        .border(1.dp, if (active) NeonCyan else Color.Transparent, RoundedCornerShape(6.dp))
                                        .clickable { playbackSpeed = speedOption }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${speedOption}x",
                                        fontSize = 11.sp,
                                        fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                                        color = if (active) NeonCyan else Color.LightGray
                                    )
                                }
                            }
                        }

                        TextButton(
                            onClick = { isQueueExpanded = !isQueueExpanded },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(
                                imageVector = if (isQueueExpanded) Icons.Default.ExpandLess else Icons.Default.QueueMusic,
                                contentDescription = "Toggle Queue",
                                tint = NeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Queue (4)", color = NeonCyan, fontSize = 11.sp)
                        }
                    }

                    AnimatedVisibility(visible = isQueueExpanded) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF131D2E))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "UPCOMING IN PLAYLIST",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            listOf(
                                "1. Downtown Soundscapes • DJ Neon Pulse",
                                "2. Morning Editorial Dispatch #418 • Press Wire",
                                "3. The Tech Beacon (Ep. 42) • Mayor Alistair",
                                "4. Midnight Lofi Jazz Stream • Vinyl Lounge"
                            ).forEach { track ->
                                Text(
                                    text = track,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.LightGray,
                                    modifier = Modifier.padding(vertical = 3.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                1 -> {
                    // MUSIC STREAMING TAB
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Featured Townsquare Music Streams",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = NeonCyan
                        )
                        listOf(
                            Pair("Neo-Wave Cyber Jazz", "Downtown Chill Lounge • 320kbps"),
                            Pair("Acoustic Town Hall Sessions", "Live Singer-Songwriter Ensemble • Lossless"),
                            Pair("Midnight Lo-Fi Civic Beats", "Townsquare Creative Commons • Stereo")
                        ).forEach { (streamTitle, streamSub) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF1E293B))
                                    .clickable { onTogglePlayPause() }
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.MusicNote,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(streamTitle, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                        Text(streamSub, color = Color.Gray, fontSize = 11.sp)
                                    }
                                }
                                Icon(
                                    imageVector = Icons.Default.PlayCircle,
                                    contentDescription = "Play Stream",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }

                2 -> {
                    // PODCASTS TAB
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Top On-Demand Podcasts & Chapter Scrubbing",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = NeonCyan
                        )
                        listOf(
                            Triple("Townsquare Voices Ep. 84", "45 mins", "Urban planning & the new civic hub"),
                            Triple("Tech Pioneers Dispatch", "28 mins", "Decentralized mesh networks in modern cities"),
                            Triple("Heritage & Historic Echoes", "52 mins", "Remembering the Great Steam Rail of 1920")
                        ).forEach { (podTitle, podDur, podDesc) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF1E293B))
                                    .clickable { onTogglePlayPause() }
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Icon(
                                        imageVector = Icons.Default.Podcasts,
                                        contentDescription = null,
                                        tint = WarmAmber,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(podTitle, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                        Text("$podDur • $podDesc", color = Color.LightGray, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                                Icon(
                                    imageVector = Icons.Default.PlayCircle,
                                    contentDescription = "Play Episode",
                                    tint = WarmAmber,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }

                3 -> {
                    // DEVICE FILES TAB
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Device Storage Audio Tracks",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = NeonCyan
                            )
                            Button(
                                onClick = { /* Device scan trigger */ },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp), tint = NeonCyan)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Scan /app/Music", fontSize = 10.sp, color = NeonCyan)
                            }
                        }

                        deviceAudioFiles.forEach { track ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF1E293B))
                                    .clickable { onTogglePlayPause() }
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Icon(
                                        imageVector = Icons.Default.Folder,
                                        contentDescription = null,
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(track.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                        Text("${track.fileName} • ${track.format}", color = Color.Gray, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                                Icon(
                                    imageVector = Icons.Default.PlayCircle,
                                    contentDescription = "Play Local File",
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
