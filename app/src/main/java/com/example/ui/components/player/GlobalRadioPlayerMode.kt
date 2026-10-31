package com.example.ui.components.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber
import kotlinx.coroutines.delay

/**
 * Global Media Player - Mode 5: Radio
 * High-fidelity Radio Tuner Dial (FM / AM), Saved Station Presets,
 * Live Broadcast Tape Recording, and Real-Time Live Subtitles / AI Transcription.
 */
@Composable
fun GlobalRadioPlayerMode(
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedBand by remember { mutableStateOf(RadioBand.FM) }
    var currentFrequencyMhz by remember { mutableFloatStateOf(101.4f) }
    var currentFrequencyKhz by remember { mutableIntStateOf(1080) }
    var isRadioPlaying by remember { mutableStateOf(true) }
    var isSubtitlesEnabled by remember { mutableStateOf(true) }

    // Live Recording State
    var isRecordingLive by remember { mutableStateOf(false) }
    var recordingDurationSeconds by remember { mutableIntStateOf(0) }
    var isRecordingsDrawerOpen by remember { mutableStateOf(false) }

    // Live Subtitles stream
    var currentSubtitleIndex by remember { mutableIntStateOf(0) }
    val subtitleStream = remember {
        listOf(
            "🎙️ Host: \"Good morning Townsquare, you're tuned to 101.4 FM live transmission.\"",
            "🎵 [Station Jingle: 'Your Voice, Your Community, Townsquare Radio']",
            "🎙️ Host: \"Coming up after the break: local produce prices, transit rail updates, and ambient jazz.\"",
            "🎙️ Reporter: \"The farmers market on North Pavilion is officially underway with 30 local vendors.\"",
            "🎙️ Host: \"Stay tuned for the midday acoustic broadcast streaming directly over the airwaves.\""
        )
    }

    // Ticker timer for live subtitles
    LaunchedEffect(isRadioPlaying, isSubtitlesEnabled) {
        while (isRadioPlaying && isSubtitlesEnabled) {
            delay(4000L)
            currentSubtitleIndex = (currentSubtitleIndex + 1) % subtitleStream.size
        }
    }

    // Timer for active recording
    LaunchedEffect(isRecordingLive) {
        while (isRecordingLive) {
            delay(1000L)
            recordingDurationSeconds++
        }
    }

    // Saved Presets
    var savedPresets by remember {
        mutableStateOf(
            listOf(
                RadioPresetStation("p1", "WCTN", 101.4f, RadioBand.FM, "Townsquare Central Live", "News & Civic Affairs", isFavorite = true),
                RadioPresetStation("p2", "WJAZ", 94.7f, RadioBand.FM, "Metropolitan Jazz & Soul", "Jazz, Blues & Vinyl", isFavorite = true),
                RadioPresetStation("p3", "WCLX", 89.1f, RadioBand.FM, "Classical Symphony Hall", "Classical Orchestral"),
                RadioPresetStation("p4", "WNEO", 106.5f, RadioBand.FM, "Neo-Wave Modular Synth", "Electronic & Synthwave"),
                RadioPresetStation("p5", "KHAM", 720f, RadioBand.AM, "Amateur Ham & Packet Radio", "Weather & DX Logs"),
                RadioPresetStation("p6", "KTAL", 1080f, RadioBand.AM, "Civic Open Talk & Town Hall", "Public Debate & Talk")
            )
        )
    }

    // Saved tape recordings
    var recordedSessions by remember {
        mutableStateOf(
            listOf(
                RadioRecordingSession(
                    stationCallsign = "WCTN-FM",
                    stationFrequency = "101.4 FM",
                    recordingTitle = "Morning News & Traffic Special",
                    durationSeconds = 184,
                    fileSizeMb = 4.2f
                ),
                RadioRecordingSession(
                    stationCallsign = "WJAZ-FM",
                    stationFrequency = "94.7 FM",
                    recordingTitle = "Late Night Saxophone Solo Stream",
                    durationSeconds = 312,
                    fileSizeMb = 7.1f
                )
            )
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("global_radio_player_mode"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(1.dp, WarmAmber.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Badge & Band Switcher & Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(WarmAmber.copy(alpha = 0.2f))
                            .border(1.dp, WarmAmber, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "📻 RADIO TUNER",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = WarmAmber
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // FM / AM Band Switcher
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF1E293B))
                            .padding(2.dp)
                    ) {
                        listOf(RadioBand.FM, RadioBand.AM).forEach { band ->
                            val isSel = selectedBand == band
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isSel) WarmAmber else Color.Transparent)
                                    .clickable { selectedBand = band }
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = band.name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color(0xFF381E00) else Color.LightGray
                                )
                            }
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Saved tapes library button
                    IconButton(
                        onClick = { isRecordingsDrawerOpen = !isRecordingsDrawerOpen },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LibraryMusic,
                            contentDescription = "Saved Tapes",
                            tint = if (isRecordingsDrawerOpen) WarmAmber else Color.LightGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Subtitles toggle
                    IconButton(
                        onClick = { isSubtitlesEnabled = !isSubtitlesEnabled },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isSubtitlesEnabled) Icons.Default.Subtitles else Icons.Default.SubtitlesOff,
                            contentDescription = "Toggle Subtitles",
                            tint = if (isSubtitlesEnabled) Color.Green else Color.LightGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Close
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Radio Player",
                            tint = Color.LightGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // TUNER DIAL DISPLAY
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF030712)),
                border = BorderStroke(1.dp, Color(0xFF374151))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Top stats: Frequency readout & Signal strength
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            val freqString = if (selectedBand == RadioBand.FM) {
                                String.format("%.1f MHz FM", currentFrequencyMhz)
                            } else {
                                "$currentFrequencyKhz kHz AM"
                            }
                            Text(
                                text = freqString,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = WarmAmber
                            )
                            Text(
                                text = "STATION: Townsquare Central • STEREO LOCK 98%",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.LightGray
                            )
                        }

                        // Play/Pause button
                        IconButton(
                            onClick = { isRadioPlaying = !isRadioPlaying },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(WarmAmber)
                        ) {
                            Icon(
                                imageVector = if (isRadioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = Color(0xFF381E00),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // FREQUENCY DIAL SCALE & NEEDLE
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF111827))
                            .border(1.dp, Color(0xFF1F2937), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Background ruler marks
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            if (selectedBand == RadioBand.FM) {
                                listOf(88, 92, 96, 100, 104, 108).forEach { freqMark ->
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("$freqMark", fontSize = 9.sp, color = Color.Gray, fontFamily = FontFamily.Monospace)
                                        Box(
                                            modifier = Modifier
                                                .width(1.5.dp)
                                                .height(16.dp)
                                                .background(Color.Gray)
                                        )
                                    }
                                }
                            } else {
                                listOf(540, 700, 900, 1100, 1400, 1700).forEach { freqMark ->
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("$freqMark", fontSize = 9.sp, color = Color.Gray, fontFamily = FontFamily.Monospace)
                                        Box(
                                            modifier = Modifier
                                                .width(1.5.dp)
                                                .height(16.dp)
                                                .background(Color.Gray)
                                        )
                                    }
                                }
                            }
                        }

                        // Interactive dial slider overlay
                        val dialProgress = if (selectedBand == RadioBand.FM) {
                            ((currentFrequencyMhz - 87.5f) / (108.0f - 87.5f)).coerceIn(0f, 1f)
                        } else {
                            ((currentFrequencyKhz - 530f) / (1710f - 530f)).coerceIn(0f, 1f)
                        }

                        Slider(
                            value = dialProgress,
                            onValueChange = { frac ->
                                if (selectedBand == RadioBand.FM) {
                                    currentFrequencyMhz = 87.5f + (frac * (108.0f - 87.5f))
                                } else {
                                    currentFrequencyKhz = (530f + (frac * (1710f - 530f))).toInt()
                                }
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = WarmAmber,
                                activeTrackColor = WarmAmber.copy(alpha = 0.5f),
                                inactiveTrackColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Fine-tuning buttons & Save Station Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(
                                onClick = {
                                    if (selectedBand == RadioBand.FM) {
                                        currentFrequencyMhz = (currentFrequencyMhz - 0.1f).coerceAtLeast(87.5f)
                                    } else {
                                        currentFrequencyKhz = (currentFrequencyKhz - 10).coerceAtLeast(530)
                                    }
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("-0.1 Fine", fontSize = 10.sp, color = WarmAmber)
                            }

                            OutlinedButton(
                                onClick = {
                                    if (selectedBand == RadioBand.FM) {
                                        currentFrequencyMhz = (currentFrequencyMhz + 0.1f).coerceAtMost(108.0f)
                                    } else {
                                        currentFrequencyKhz = (currentFrequencyKhz + 10).coerceAtMost(1710)
                                    }
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("+0.1 Fine", fontSize = 10.sp, color = WarmAmber)
                            }
                        }

                        // Bookmark / Save current frequency
                        Button(
                            onClick = {
                                val newPreset = RadioPresetStation(
                                    id = "p_${System.currentTimeMillis()}",
                                    callsign = "WNEW",
                                    frequencyMhz = currentFrequencyMhz,
                                    band = selectedBand,
                                    stationName = "User Saved Frequency",
                                    genre = "Custom Preset",
                                    isFavorite = true
                                )
                                savedPresets = savedPresets + newPreset
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(Icons.Default.Bookmark, contentDescription = null, tint = WarmAmber, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Save Preset", fontSize = 10.sp, color = WarmAmber)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // LIVE SUBTITLES & AI TRANSCRIPTION TICKER
            if (isSubtitlesEnabled) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF020617))
                        .border(1.dp, Color(0xFF1F2937), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "LIVE CC",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Green,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFF052E16))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = subtitleStream[currentSubtitleIndex],
                            fontSize = 11.sp,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // LIVE RECORDING TAPE REEL & ACTION
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E293B))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Pulsing Red REC indicator
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(if (isRecordingLive) Color.Red else Color.Gray)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (isRecordingLive) "RECORDING BROADCAST LIVE" else "Live Radio Tape Recorder",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isRecordingLive) Color(0xFFFF6B6B) else Color.White
                        )
                        Text(
                            text = if (isRecordingLive) {
                                val m = recordingDurationSeconds / 60
                                val s = recordingDurationSeconds % 60
                                String.format("REC %02d:%02d • AAC 192kbps Stereo", m, s)
                            } else {
                                "Record radio segment directly to device tape archive"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.LightGray
                        )
                    }
                }

                Button(
                    onClick = {
                        if (isRecordingLive) {
                            // Stop & save
                            val newSession = RadioRecordingSession(
                                stationCallsign = "WCTN-FM",
                                stationFrequency = String.format("%.1f FM", currentFrequencyMhz),
                                recordingTitle = "Broadcast Clip ${recordedSessions.size + 1}",
                                durationSeconds = recordingDurationSeconds.coerceAtLeast(1),
                                fileSizeMb = (recordingDurationSeconds * 0.024f)
                            )
                            recordedSessions = listOf(newSession) + recordedSessions
                            isRecordingLive = false
                            recordingDurationSeconds = 0
                        } else {
                            // Start
                            isRecordingLive = true
                            recordingDurationSeconds = 0
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRecordingLive) Color.Red else Color(0xFF0F172A)
                    ),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = if (isRecordingLive) Icons.Default.Stop else Icons.Default.FiberManualRecord,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (isRecordingLive) Color.White else Color.Red
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isRecordingLive) "Stop & Save" else "Record ⏺️",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // SAVED RECORDINGS DRAWER
            AnimatedVisibility(visible = isRecordingsDrawerOpen) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF030712))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "💾 Saved Radio Cassette Tapes (${recordedSessions.size})",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = WarmAmber
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    recordedSessions.forEach { tape ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(tape.recordingTitle, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text("${tape.stationFrequency} • ${tape.durationSeconds}s • ${tape.fileSizeMb} MB", color = Color.Gray, fontSize = 10.sp)
                            }
                            IconButton(onClick = {}, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.PlayCircle, contentDescription = "Play Tape", tint = WarmAmber, modifier = Modifier.size(22.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // STATION PRESETS HORIZONTAL ROW (P1 - P6)
            Text(
                text = "Preset Stations (1-Tap Tune)",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = WarmAmber
            )
            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(savedPresets) { preset ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E293B))
                            .clickable {
                                selectedBand = preset.band
                                currentFrequencyMhz = preset.frequencyMhz
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(preset.callsign, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (preset.band == RadioBand.FM) "${preset.frequencyMhz}M" else "${preset.frequencyMhz.toInt()}k",
                                    color = WarmAmber,
                                    fontSize = 10.sp
                                )
                            }
                            Text(preset.genre, color = Color.Gray, fontSize = 9.sp, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}
