package com.example.ui.components.player

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioState
import com.example.data.model.MediaItemEntity
import com.example.ui.plus.waves.SongDetectionEngine
import com.example.ui.theme.*

@Composable
fun RadioDialPlayerSkin(
    radioStations: List<MediaItemEntity>,
    audioState: AudioState,
    onPlayStation: (MediaItemEntity) -> Unit,
    onTogglePlayPause: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFrequency by remember { mutableFloatStateOf(101.4f) }
    val haptic = LocalHapticFeedback.current

    val currentStation = radioStations.minByOrNull {
        kotlin.math.abs((it.stationFrequency.replace(" MHz", "").toFloatOrNull() ?: 100f) - selectedFrequency)
    } ?: radioStations.firstOrNull()

    val liveSong = SongDetectionEngine.getRadioLiveDetectedSong(currentStation?.stationFrequency ?: "101.4 MHz")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF0F172A))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📻", fontSize = 22.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Townsquare Waves • Radio Dial Skin",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "FM Stereo Tuner & Automated Song Recognition",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonCyan
                    )
                }
            }

            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
        }

        // Authentic Vintage Radio Dial Faceplate
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF141A26),
            border = BorderStroke(1.5.dp, WarmAmber.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                // Frequency Display & Signal Strength
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF22C55E),
                            modifier = Modifier.size(10.dp)
                        ) {}
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("STEREO LOCK", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF4ADE80))
                    }

                    Text(
                        text = "%.1f MHz".format(selectedFrequency),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = WarmAmber,
                        fontFamily = FontFamily.Monospace
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SignalCellularAlt, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("99.8 dB", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Frequency Tuning Ruler Slider
                Slider(
                    value = selectedFrequency,
                    onValueChange = { freq ->
                        selectedFrequency = freq
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    },
                    valueRange = 88.0f..108.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = WarmAmber,
                        activeTrackColor = WarmAmber,
                        inactiveTrackColor = Color(0xFF1E293B)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("radio_dial_slider")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf(88.0f, 92.0f, 96.0f, 100.0f, 104.0f, 108.0f).forEach { freq ->
                        Text("%.0f".format(freq), fontSize = 10.sp, color = Color(0xFF94A3B8))
                    }
                }
            }
        }

        // Live Song Recognition Banner (TV News Graphics style)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFDC2626),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(liveSong.albumArtEmoji, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "LIVE SONG: ${liveSong.title}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            maxLines = 1
                        )
                        Text(
                            text = "Artist: ${liveSong.artist} • Station: ${currentStation?.title ?: "Townsquare Central"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Yellow,
                            maxLines = 1
                        )
                    }
                }

                IconButton(
                    onClick = {
                        if (currentStation != null) {
                            onPlayStation(currentStation)
                        } else {
                            onTogglePlayPause()
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(
                        imageVector = if (audioState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = Color(0xFF0F172A)
                    )
                }
            }
        }

        // Preset Stations Row
        Text("PRESET STATIONS", fontSize = 11.sp, fontWeight = FontWeight.Black, color = NeonCyan)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(radioStations) { station ->
                val freqVal = station.stationFrequency.replace(" MHz", "").toFloatOrNull() ?: 100f
                val isSelected = kotlin.math.abs(freqVal - selectedFrequency) < 1.0f
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) WarmAmber else Color(0xFF1E293B),
                    modifier = Modifier
                        .clickable {
                            selectedFrequency = freqVal
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onPlayStation(station)
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = station.stationFrequency,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color(0xFF261800) else Color.White
                        )
                        Text(
                            text = station.title.take(10),
                            fontSize = 9.sp,
                            color = if (isSelected) Color(0xFF261800) else Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }
    }
}
