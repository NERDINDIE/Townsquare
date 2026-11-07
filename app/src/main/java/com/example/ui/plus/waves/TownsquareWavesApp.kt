package com.example.ui.plus.waves

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.ui.components.LiveBroadcastCallInDialog
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TownsquareWavesApp(
    items: List<MediaItemEntity>,
    audioState: AudioState,
    onPlayAudio: (MediaItemEntity) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val radioStations = remember(items) { items.filter { it.type == MediaType.RADIO_STATION.name } }
    val podcasts = remember(items) { items.filter { it.type == MediaType.PODCAST_EPISODE.name } }
    val songs = remember(items) { items.filter { it.type == MediaType.SONG.name } }
    val playlists = remember(items) { items.filter { it.type == MediaType.PLAYLIST.name } }

    var isRadioCallInOpen by remember { mutableStateOf(false) }

    if (isRadioCallInOpen) {
        val currentStationName = audioState.currentItem?.title ?: radioStations.firstOrNull()?.title ?: "Townsquare Waves Radio"
        LiveBroadcastCallInDialog(
            broadcastTitle = "Live Waves Talkback - $currentStationName",
            channelOrStation = "Waves FM Broadcast Studio Link",
            onDismiss = { isRadioCallInOpen = false }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B111E))
            .testTag("townsquare_waves_app")
    ) {
        // Top App Bar
        Surface(
            color = Color(0xFF0F172A),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("waves_back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = NeonCyan
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = CircleShape,
                        color = NeonCyan.copy(alpha = 0.2f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Townsquare Waves",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = NeonCyan
                            ) {
                                Text(
                                    text = "STREAM",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF003544),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "Live Radio, Podcasts & Automatic Song Recognition",
                            style = MaterialTheme.typography.labelSmall,
                            color = DarkTextSecondary
                        )
                    }
                }

                FilledTonalButton(
                    onClick = { isRadioCallInOpen = true },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = NeonCyan.copy(alpha = 0.2f),
                        contentColor = NeonCyan
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("waves_call_in_btn")
                ) {
                    Icon(Icons.Default.PhoneInTalk, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Call-In", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Main Stream Feed
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Radio Ticker with Song Recognition
            item {
                Text(
                    text = "LIVE BROADCASTS & SONG DETECTION",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                    color = NeonCyan
                )
                Spacer(modifier = Modifier.height(8.dp))
                WavesRadioTicker(
                    radioStations = radioStations,
                    onSelectStation = { onPlayAudio(it) },
                    onOpenRadioList = {
                        radioStations.firstOrNull()?.let { onPlayAudio(it) }
                    }
                )
            }

            // Catalog Grids & Podcast Song Lists
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "WAVES STREAMING CATALOG & PODCASTS",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                    color = WarmAmber
                )
                Spacer(modifier = Modifier.height(8.dp))
                WavesGridCatalog(
                    songs = songs,
                    playlists = playlists,
                    podcasts = podcasts,
                    onPlayAudio = onPlayAudio
                )
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}
