package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioState
import com.example.data.model.MediaItemEntity
import com.example.data.model.MediaType
import com.example.ui.components.AndroidAutoDrivingDialog
import com.example.ui.components.LiveBroadcastCallInDialog
import com.example.ui.components.MediaCardItem
import com.example.ui.components.RadioDialTuner
import com.example.ui.components.TownsquareTopBar
import com.example.ui.theme.MintTeal
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber

@Composable
fun AudioHubScreen(
    items: List<MediaItemEntity>,
    audioState: AudioState,
    onPlayAudio: (MediaItemEntity) -> Unit,
    onOpenReader: (MediaItemEntity) -> Unit,
    onToggleLike: (MediaItemEntity) -> Unit,
    onToggleBookmark: (MediaItemEntity) -> Unit,
    onTogglePlayPause: () -> Unit = {},
    onSeekNextStation: () -> Unit = {},
    onSeekPrevStation: () -> Unit = {},
    onOpenSidebar: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedAudioTab by remember { mutableIntStateOf(0) } // 0: Radio, 1: Podcasts, 2: Songs, 3: Playlists
    var showAndroidAutoDialog by remember { mutableStateOf(false) }
    var isRadioCallInOpen by remember { mutableStateOf(false) }

    val radioStations = items.filter { it.type == MediaType.RADIO_STATION.name }
    val podcasts = items.filter { it.type == MediaType.PODCAST_EPISODE.name }
    val songs = items.filter { it.type == MediaType.SONG.name }
    val playlists = items.filter { it.type == MediaType.PLAYLIST.name }

    if (isRadioCallInOpen) {
        val currentStationName = audioState.currentItem?.title ?: radioStations.firstOrNull()?.title ?: "Townsquare Central Radio"
        LiveBroadcastCallInDialog(
            broadcastTitle = "Live Radio Talkback - $currentStationName",
            channelOrStation = "FM Broadcast Studio Link",
            onDismiss = { isRadioCallInOpen = false }
        )
    }

    if (showAndroidAutoDialog) {
        AndroidAutoDrivingDialog(
            stations = radioStations,
            audioState = audioState,
            onPlayStation = onPlayAudio,
            onTogglePlayPause = onTogglePlayPause,
            onSeekNext = onSeekNextStation,
            onSeekPrev = onSeekPrevStation,
            onDismiss = { showAndroidAutoDialog = false }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("audio_hub_screen")
    ) {
        // Header
        TownsquareTopBar(
            title = "Radio & Podcasts",
            subtitle = "Live analog & digital stations and on-demand audio series",
            onOpenSidebar = onOpenSidebar,
            actions = {
                if (audioState.isPlaying) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Streaming",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = NeonCyan
                        )
                    }
                }
            }
        )

        // Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedAudioTab,
            containerColor = Color.Transparent,
            contentColor = NeonCyan,
            indicator = { tabPositions ->
                if (selectedAudioTab < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedAudioTab]),
                        color = NeonCyan
                    )
                }
            },
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Tab(
                selected = selectedAudioTab == 0,
                onClick = { selectedAudioTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Radio, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Radio (${radioStations.size})",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (selectedAudioTab == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                },
                modifier = Modifier.testTag("radio_tab")
            )

            Tab(
                selected = selectedAudioTab == 1,
                onClick = { selectedAudioTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Headphones, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Podcasts (${podcasts.size})",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (selectedAudioTab == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                },
                modifier = Modifier.testTag("podcast_tab")
            )

            Tab(
                selected = selectedAudioTab == 2,
                onClick = { selectedAudioTab = 2 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.MusicNote, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Songs (${songs.size})",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (selectedAudioTab == 2) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                },
                modifier = Modifier.testTag("songs_tab")
            )

            Tab(
                selected = selectedAudioTab == 3,
                onClick = { selectedAudioTab = 3 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.QueueMusic, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Playlists (${playlists.size})",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (selectedAudioTab == 3) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                },
                modifier = Modifier.testTag("playlists_tab")
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Content List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (selectedAudioTab == 0) {
                // Radio Dial Tuner with Frequency Ruler & Seek Controls
                item(key = "radio_dial_tuner_item") {
                    RadioDialTuner(
                        stations = radioStations,
                        audioState = audioState,
                        onPlayStation = onPlayAudio,
                        onTogglePlayPause = onTogglePlayPause,
                        onOpenAndroidAutoMode = { showAndroidAutoDialog = true }
                    )
                }

                // Section divider / header
                item(key = "radio_stations_header") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LOCAL FM BROADCAST CHANNELS (${radioStations.size})",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilledTonalButton(
                                onClick = { isRadioCallInOpen = true },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = NeonCyan.copy(alpha = 0.15f),
                                    contentColor = NeonCyan
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp).testTag("radio_call_in_button")
                            ) {
                                Text("📞 Studio Call-In", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Station Cards
                items(radioStations, key = { it.id }) { item ->
                    MediaCardItem(
                        item = item,
                        audioState = audioState,
                        onOpenReader = onOpenReader,
                        onPlayAudio = onPlayAudio,
                        onToggleLike = onToggleLike,
                        onToggleBookmark = onToggleBookmark
                    )
                }
            } else if (selectedAudioTab == 1) {
                items(podcasts, key = { it.id }) { item ->
                    MediaCardItem(
                        item = item,
                        audioState = audioState,
                        onOpenReader = onOpenReader,
                        onPlayAudio = onPlayAudio,
                        onToggleLike = onToggleLike,
                        onToggleBookmark = onToggleBookmark
                    )
                }
            } else if (selectedAudioTab == 2) {
                items(songs, key = { it.id }) { item ->
                    MediaCardItem(
                        item = item,
                        audioState = audioState,
                        onOpenReader = onOpenReader,
                        onPlayAudio = onPlayAudio,
                        onToggleLike = onToggleLike,
                        onToggleBookmark = onToggleBookmark
                    )
                }
            } else {
                items(playlists, key = { it.id }) { item ->
                    MediaCardItem(
                        item = item,
                        audioState = audioState,
                        onOpenReader = onOpenReader,
                        onPlayAudio = onPlayAudio,
                        onToggleLike = onToggleLike,
                        onToggleBookmark = onToggleBookmark
                    )
                }
            }

            item(key = "bottom_spacer") {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
