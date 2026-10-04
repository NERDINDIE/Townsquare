package com.example.ui.plus.iot

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

data class Mp3Track(
    val title: String,
    val artist: String,
    val album: String,
    val duration: String,
    val bitrate: String = "320 kbps MP3"
)

@Composable
fun IotMp3PlayerView(
    modifier: Modifier = Modifier
) {
    val tracks = remember {
        listOf(
            Mp3Track("Midnight Promenade Suite", "Townsquare Civic Philharmonic", "Harbor Serenades", "3:45", "FLAC 24-bit/96kHz"),
            Mp3Track("Neon Skywalk (Synthwave Rework)", "Kofi & The Soundwaves", "City Gridlock", "4:12", "320 kbps MP3"),
            Mp3Track("Vintage Broadsheet Waltz", "Elena Rostova Chamber Trio", "Curator Vault Vol. 1", "2:50", "320 kbps MP3"),
            Mp3Track("Subsea Hydrophone Frequencies", "Coastal Buoy Station #4", "Deep Water Fieldwork", "5:18", "FLAC Lossless"),
            Mp3Track("Bicycle Bell Rhapsody", "Downtown Cycle Collective", "Pedestrian Flow", "3:04", "320 kbps MP3")
        )
    }

    var currentTrackIndex by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(true) }
    var progressSeconds by remember { mutableIntStateOf(42) }
    var volume by remember { mutableFloatStateOf(0.75f) }
    var selectedEqPreset by remember { mutableStateOf("Bass Boost") }
    var isShuffle by remember { mutableStateOf(false) }
    var isRepeat by remember { mutableStateOf(true) }
    var showTracklist by remember { mutableStateOf(false) }

    // Dynamic playback ticker
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(1000)
            progressSeconds++
            if (progressSeconds > 225) progressSeconds = 0
        }
    }

    val currentTrack = tracks[currentTrackIndex]

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F1115))
            .padding(16.dp)
            .testTag("iot_mp3_player_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // MP3 Player Body Chassis (Retro Brushed Slate / Classic Portable Player)
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF1E232B),
            border = BorderStroke(2.dp, Color(0xFF333B47)),
            shadowElevation = 8.dp,
            modifier = Modifier
                .widthIn(max = 380.dp)
                .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Chassis Screws & Brand Tag
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF4A5568)))
                    Text(
                        text = "TOWNSQUARE AUDIO-DAP 128GB",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 1.sp
                    )
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF4A5568)))
                }

                // Monochrome / Backlit Retro LCD Display Screen
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF071C1E),
                    border = BorderStroke(2.dp, Color(0xFF00383F)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    if (showTracklist) {
                        // Tracklist Mode
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("MEMORY CARD PLAYLIST", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                                Text("TAP TO PLAY", fontSize = 9.sp, color = DarkTextSecondary)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                itemsIndexed(tracks) { index, t ->
                                    val isCur = index == currentTrackIndex
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                currentTrackIndex = index
                                                progressSeconds = 0
                                                showTracklist = false
                                            }
                                            .padding(vertical = 3.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "${index + 1}. ${t.title}",
                                            fontSize = 11.sp,
                                            fontWeight = if (isCur) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isCur) NeonCyan else Color(0xFF7DD3FC),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(t.duration, fontSize = 10.sp, color = Color(0xFF38BDF8))
                                    }
                                }
                            }
                        }
                    } else {
                        // Now Playing Screen
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Top metadata bar in LCD
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isPlaying) "▶ PLAY" else "❚❚ PAUSE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = NeonCyan
                                )
                                Text(
                                    text = "EQ: $selectedEqPreset",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = WarmAmber
                                )
                                Text(
                                    text = currentTrack.bitrate,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF34D399)
                                )
                            }

                            // Song title & Artist in LCD
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = currentTrack.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFE0F2FE),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${currentTrack.artist} • ${currentTrack.album}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF7DD3FC),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // Animated Real-Time Spectrum Visualizer in LCD
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(28.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                val heights = listOf(14, 22, 10, 26, 18, 12, 28, 20, 16, 24, 8, 22)
                                heights.forEachIndexed { i, h ->
                                    val dynamicH = if (isPlaying) {
                                        ((h + (progressSeconds * (i + 1) * 7) % 18).coerceIn(4, 28)).dp
                                    } else 4.dp
                                    Box(
                                        modifier = Modifier
                                            .width(5.dp)
                                            .height(dynamicH)
                                            .background(NeonCyan, RoundedCornerShape(2.dp))
                                    )
                                }
                            }

                            // Scrubber Timeline in LCD
                            Column {
                                LinearProgressIndicator(
                                    progress = { (progressSeconds % 225) / 225f },
                                    modifier = Modifier.fillMaxWidth().height(4.dp),
                                    color = NeonCyan,
                                    trackColor = Color(0xFF00383F)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    val m = progressSeconds / 60
                                    val s = progressSeconds % 60
                                    Text(String.format("%02d:%02d", m, s), fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = NeonCyan)
                                    Text(currentTrack.duration, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF7DD3FC))
                                }
                            }
                        }
                    }
                }

                // Tactile Controls / Classic Navigation Click-Wheel
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFF2D3748), Color(0xFF1A202C))
                            )
                        )
                        .testTag("mp3_click_wheel"),
                    contentAlignment = Alignment.Center
                ) {
                    // Wheel Outer Labels
                    // MENU (Top)
                    Text(
                        text = if (showTracklist) "PLAYER" else "MENU",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFCBD5E1),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 10.dp)
                            .clickable { showTracklist = !showTracklist }
                    )

                    // PREVIOUS (Left)
                    IconButton(
                        onClick = {
                            currentTrackIndex = if (currentTrackIndex > 0) currentTrackIndex - 1 else tracks.size - 1
                            progressSeconds = 0
                        },
                        modifier = Modifier.align(Alignment.CenterStart).padding(start = 6.dp)
                    ) {
                        Icon(Icons.Default.SkipPrevious, contentDescription = "Prev", tint = Color(0xFFCBD5E1), modifier = Modifier.size(24.dp))
                    }

                    // NEXT (Right)
                    IconButton(
                        onClick = {
                            currentTrackIndex = (currentTrackIndex + 1) % tracks.size
                            progressSeconds = 0
                        },
                        modifier = Modifier.align(Alignment.CenterEnd).padding(end = 6.dp)
                    ) {
                        Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = Color(0xFFCBD5E1), modifier = Modifier.size(24.dp))
                    }

                    // PLAY / PAUSE (Bottom)
                    IconButton(
                        onClick = { isPlaying = !isPlaying },
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 6.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = Color(0xFFCBD5E1),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Center Select Button
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF111827),
                        border = BorderStroke(1.5.dp, Color(0xFF374151)),
                        modifier = Modifier
                            .size(72.dp)
                            .clickable {
                                // Cycle EQ Preset
                                val eqList = listOf("Flat", "Bass Boost", "Jazz Acoustic", "Vocal Punch", "Rock")
                                val curIndex = eqList.indexOf(selectedEqPreset)
                                selectedEqPreset = eqList[(curIndex + 1) % eqList.size]
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "SELECT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }

                // Bottom Volume Slider & Toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(Icons.Default.VolumeDown, contentDescription = null, tint = DarkTextMuted, modifier = Modifier.size(18.dp))
                    Slider(
                        value = volume,
                        onValueChange = { volume = it },
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = NeonCyan,
                            activeTrackColor = NeonCyan,
                            inactiveTrackColor = Color(0xFF334155)
                        )
                    )
                    Icon(Icons.Default.VolumeUp, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
