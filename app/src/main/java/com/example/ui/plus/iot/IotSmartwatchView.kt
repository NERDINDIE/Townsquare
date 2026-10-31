package com.example.ui.plus.iot

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.cos
import kotlin.math.sin

enum class WatchFaceStyle {
    CLASSIC_CHRONO, RETRO_TERMINAL, NEON_CYBERPUNK
}

enum class WatchTileApp {
    WATCH_FACE, WRIST_NEWS, AUDIO_CONTROLLER, VOICE_MEMO, HEALTH_SENSORS
}

/**
 * Isolated Smartwatch OS Simulation View Component
 */
@Composable
fun IotSmartwatchView(
    liveTime: LocalDateTime,
    modifier: Modifier = Modifier
) {
    var watchFaceStyle by remember { mutableStateOf(WatchFaceStyle.CLASSIC_CHRONO) }
    var activeWatchTile by remember { mutableStateOf(WatchTileApp.WATCH_FACE) }
    var isAmbientAod by remember { mutableStateOf(false) }
    var isWatchAudioPlaying by remember { mutableStateOf(true) }
    var watchVolume by remember { mutableFloatStateOf(0.75f) }
    var isRecordingVoice by remember { mutableStateOf(false) }
    var voiceRecordDuration by remember { mutableIntStateOf(0) }
    var simulatedHeartRate by remember { mutableIntStateOf(74) }
    var simulatedSteps by remember { mutableIntStateOf(8420) }

    LaunchedEffect(isRecordingVoice) {
        if (isRecordingVoice) {
            voiceRecordDuration = 0
            while (isRecordingVoice) {
                kotlinx.coroutines.delay(1000L)
                voiceRecordDuration += 1
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Physical Watch Hardware Frame (Titanium Bezel + AMOLED screen)
        Box(
            modifier = Modifier
                .size(310.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF2C3440), Color(0xFF151922), Color(0xFF0A0D14))
                    )
                )
                .padding(14.dp)
                .clip(CircleShape)
                .background(Color.Black)
                .testTag("smartwatch_screen_canvas"),
            contentAlignment = Alignment.Center
        ) {
            // Watch AMOLED Dial Screen Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                when (activeWatchTile) {
                    WatchTileApp.WATCH_FACE -> {
                        when (watchFaceStyle) {
                            WatchFaceStyle.CLASSIC_CHRONO -> ClassicChronoWatchFace(liveTime, simulatedHeartRate, simulatedSteps, isAmbientAod)
                            WatchFaceStyle.RETRO_TERMINAL -> RetroTerminalWatchFace(liveTime, simulatedSteps, isAmbientAod)
                            WatchFaceStyle.NEON_CYBERPUNK -> NeonCyberpunkWatchFace(liveTime, simulatedHeartRate, simulatedSteps, isAmbientAod)
                        }
                    }
                    WatchTileApp.WRIST_NEWS -> WristNewsTile()
                    WatchTileApp.AUDIO_CONTROLLER -> WristAudioTile(
                        isPlaying = isWatchAudioPlaying,
                        volume = watchVolume,
                        onTogglePlay = { isWatchAudioPlaying = !isWatchAudioPlaying },
                        onUpdateVolume = { watchVolume = it }
                    )
                    WatchTileApp.VOICE_MEMO -> WristVoiceMemoTile(
                        isRecording = isRecordingVoice,
                        durationSec = voiceRecordDuration,
                        onToggleRecord = { isRecordingVoice = !isRecordingVoice }
                    )
                    WatchTileApp.HEALTH_SENSORS -> WristHealthTile(simulatedHeartRate, simulatedSteps)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Watch Tile Carousel Controls
        Text(
            text = "WRIST TILES & COMPLICATIONS",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = NeonCyan
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val tiles = listOf(
                WatchTileApp.WATCH_FACE to "⌚ Dial Face",
                WatchTileApp.WRIST_NEWS to "📰 Town Dispatch",
                WatchTileApp.AUDIO_CONTROLLER to "📻 Audio Player",
                WatchTileApp.VOICE_MEMO to "🎙️ Wrist Memo",
                WatchTileApp.HEALTH_SENSORS to "💓 Pulse & Steps"
            )
            items(tiles) { (tile, label) ->
                val isSel = activeWatchTile == tile
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSel) NeonCyan else DarkSurfaceElevated,
                    border = BorderStroke(1.dp, if (isSel) NeonCyan else DarkBorder),
                    modifier = Modifier.clickable { activeWatchTile = tile }
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSel) Color(0xFF003544) else Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Watch Face Style Selector (When on Dial Face)
        if (activeWatchTile == WatchTileApp.WATCH_FACE) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = DarkSurfaceElevated,
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "WATCH FACE STYLES",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = WarmAmber
                        )
                        // AOD Toggle
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Always-On AOD", fontSize = 11.sp, color = DarkTextSecondary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Switch(
                                checked = isAmbientAod,
                                onCheckedChange = { isAmbientAod = !isAmbientAod },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = NeonCyan)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            WatchFaceStyle.CLASSIC_CHRONO to "Pilot Chrono",
                            WatchFaceStyle.RETRO_TERMINAL to "CRT Terminal",
                            WatchFaceStyle.NEON_CYBERPUNK to "Neon Pulse"
                        ).forEach { (style, label) ->
                            val isSel = watchFaceStyle == style
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) WarmAmber else DarkSurfaceVariant,
                                border = BorderStroke(1.dp, if (isSel) WarmAmber else DarkBorder),
                                modifier = Modifier.weight(1f).clickable { watchFaceStyle = style }
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) Color(0xFF261800) else Color.White,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// Watch Faces
// -------------------------------------------------------------------------
@Composable
private fun ClassicChronoWatchFace(liveTime: LocalDateTime, heartRate: Int, steps: Int, isAmbient: Boolean) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width / 2

            // Hour tick marks
            for (i in 0 until 12) {
                val angle = i * 30.0 - 90.0
                val start = center + Offset((radius * 0.85 * cos(Math.toRadians(angle))).toFloat(), (radius * 0.85 * sin(Math.toRadians(angle))).toFloat())
                val end = center + Offset((radius * 0.96 * cos(Math.toRadians(angle))).toFloat(), (radius * 0.96 * sin(Math.toRadians(angle))).toFloat())
                drawLine(color = if (i % 3 == 0) Color.White else Color.Gray, start = start, end = end, strokeWidth = if (i % 3 == 0) 3f else 1.5f)
            }

            val hourAngle = (liveTime.hour % 12 + liveTime.minute / 60f) * 30f - 90f
            val minuteAngle = liveTime.minute * 6f - 90f
            val secondAngle = liveTime.second * 6f - 90f

            // Hour Hand
            drawLine(
                color = Color.White,
                start = center,
                end = center + Offset((radius * 0.5 * cos(Math.toRadians(hourAngle.toDouble()))).toFloat(), (radius * 0.5 * sin(Math.toRadians(hourAngle.toDouble()))).toFloat()),
                strokeWidth = 4.dp.toPx()
            )
            // Minute Hand
            drawLine(
                color = NeonCyan,
                start = center,
                end = center + Offset((radius * 0.75 * cos(Math.toRadians(minuteAngle.toDouble()))).toFloat(), (radius * 0.75 * sin(Math.toRadians(minuteAngle.toDouble()))).toFloat()),
                strokeWidth = 3.dp.toPx()
            )
            if (!isAmbient) {
                // Second Hand
                drawLine(
                    color = CoralRed,
                    start = center,
                    end = center + Offset((radius * 0.85 * cos(Math.toRadians(secondAngle.toDouble()))).toFloat(), (radius * 0.85 * sin(Math.toRadians(secondAngle.toDouble()))).toFloat()),
                    strokeWidth = 1.5.dp.toPx()
                )
                drawCircle(color = CoralRed, radius = 3.dp.toPx(), center = center)
            }
        }

        // Digital Complications
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.offset(y = (-45).dp)) {
            Text("TOWNSQUARE", fontSize = 8.sp, fontWeight = FontWeight.Black, color = NeonCyan, letterSpacing = 1.sp)
            Text("72°F ⛅", fontSize = 10.sp, color = Color.White)
        }

        Row(
            modifier = Modifier.fillMaxWidth().offset(y = 45.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text("💓 $heartRate", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CoralRed)
            Text("👣 $steps", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MintTeal)
        }
    }
}

@Composable
private fun RetroTerminalWatchFace(liveTime: LocalDateTime, steps: Int, isAmbient: Boolean) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("SYS_TICK // WEAR", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = Color(0xFF33FF33))
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = liveTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
            fontFamily = FontFamily.Monospace,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = if (isAmbient) Color(0xFF1E821E) else Color(0xFF33FF33)
        )
        Text(
            text = liveTime.format(DateTimeFormatter.ofPattern("EEE MMM dd")),
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("STEP: $steps | BAT: 84%", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = Color(0xFF88CC88))
    }
}

@Composable
private fun NeonCyberpunkWatchFace(liveTime: LocalDateTime, heartRate: Int, steps: Int, isAmbient: Boolean) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = liveTime.format(DateTimeFormatter.ofPattern("hh:mm")),
            fontSize = 38.sp,
            fontWeight = FontWeight.Black,
            color = if (isAmbient) Color.White.copy(alpha = 0.6f) else NeonCyan
        )
        Text(
            text = liveTime.format(DateTimeFormatter.ofPattern("a • EEEE, MMM dd")),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = WarmAmber
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(shape = RoundedCornerShape(4.dp), color = CoralRed.copy(alpha = 0.2f)) {
                Text("❤️ $heartRate BPM", fontSize = 10.sp, color = CoralRed, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
            }
            Surface(shape = RoundedCornerShape(4.dp), color = MintTeal.copy(alpha = 0.2f)) {
                Text("⚡ ${steps / 1000}k STEPS", fontSize = 10.sp, color = MintTeal, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
            }
        }
    }
}

// -------------------------------------------------------------------------
// Watch App Tiles
// -------------------------------------------------------------------------
@Composable
private fun WristNewsTile() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(shape = RoundedCornerShape(4.dp), color = NeonCyan) {
            Text("TOWNSQUARE DISPATCH", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color(0xFF003544), modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Clean Transit Corridor & Solar Retrofit approved by Council 9-1.",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text("Swipe for next headline", fontSize = 9.sp, color = DarkTextSecondary)
    }
}

@Composable
private fun WristAudioTile(isPlaying: Boolean, volume: Float, onTogglePlay: () -> Unit, onUpdateVolume: (Float) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("88.5 FM • LIVE", fontSize = 9.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
        Text("Sonic Frequencies", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            IconButton(onClick = onTogglePlay, modifier = Modifier.size(36.dp).background(NeonCyan, CircleShape)) {
                Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF003544))
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text("Vol ${(volume * 100).toInt()}%", fontSize = 9.sp, color = DarkTextSecondary)
    }
}

@Composable
private fun WristVoiceMemoTile(isRecording: Boolean, durationSec: Int, onToggleRecord: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("WRIST VOICE MEMO", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = WarmAmber)
        Spacer(modifier = Modifier.height(8.dp))
        IconButton(
            onClick = onToggleRecord,
            modifier = Modifier.size(48.dp).background(if (isRecording) CoralRed else Color(0xFF1E293B), CircleShape)
        ) {
            Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(if (isRecording) "Recording 00:${durationSec.toString().padStart(2, '0')}" else "Tap mic to record", fontSize = 10.sp, color = Color.White)
    }
}

@Composable
private fun WristHealthTile(heartRate: Int, steps: Int) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("VITAL TELEMETRY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CoralRed)
        Spacer(modifier = Modifier.height(6.dp))
        Text("❤️ $heartRate BPM", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text("Cardio Resting: Good", fontSize = 9.sp, color = MintTeal)
        Spacer(modifier = Modifier.height(6.dp))
        Text("👣 $steps / 10,000", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
    }
}
