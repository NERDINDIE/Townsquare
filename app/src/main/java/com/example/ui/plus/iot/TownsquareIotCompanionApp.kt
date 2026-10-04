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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.cos
import kotlin.math.sin

enum class IotDeviceEnvironment {
    SMARTWATCH, EINK_HOME_DISPLAY, CAR_DASHBOARD, MP3_PLAYER, REMOTE_COMPUTER, AUTOMOTIVE_CLUSTER
}

enum class WatchFaceStyle {
    CLASSIC_CHRONO, RETRO_TERMINAL, NEON_CYBERPUNK
}

enum class WatchTileApp {
    WATCH_FACE, WRIST_NEWS, AUDIO_CONTROLLER, VOICE_MEMO, HEALTH_SENSORS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TownsquareIotCompanionApp(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeEnvironment by remember { mutableStateOf(IotDeviceEnvironment.SMARTWATCH) }

    // Smartwatch State
    var watchFaceStyle by remember { mutableStateOf(WatchFaceStyle.CLASSIC_CHRONO) }
    var activeWatchTile by remember { mutableStateOf(WatchTileApp.WATCH_FACE) }
    var isAmbientAod by remember { mutableStateOf(false) }
    var isWatchAudioPlaying by remember { mutableStateOf(true) }
    var watchVolume by remember { mutableFloatStateOf(0.75f) }
    var isRecordingVoice by remember { mutableStateOf(false) }
    var voiceRecordDuration by remember { mutableIntStateOf(0) }
    var liveTime by remember { mutableStateOf(LocalDateTime.now()) }

    // Simulated Sensor Readings
    var simulatedHeartRate by remember { mutableIntStateOf(74) }
    var simulatedSteps by remember { mutableIntStateOf(8420) }

    LaunchedEffect(Unit) {
        while (isActive) {
            liveTime = LocalDateTime.now()
            delay(1000L)
        }
    }

    LaunchedEffect(isRecordingVoice) {
        if (isRecordingVoice) {
            voiceRecordDuration = 0
            while (isRecordingVoice) {
                delay(1000L)
                voiceRecordDuration += 1
            }
        }
    }

    Box(modifier = modifier.fillMaxSize().background(DarkBg)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Surface(
                color = DarkSurface,
                border = BorderStroke(1.dp, DarkBorder),
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
                        IconButton(onClick = onBack, modifier = Modifier.testTag("iot_back_button")) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NeonCyan)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = CircleShape,
                            color = NeonCyan.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Watch, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "IoT & Multi-Device Companion",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(4.dp), color = MintTeal) {
                                    Text(
                                        text = "SIMULATOR",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF003544),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Simulate Townsquare on Smartwatches, E-Ink & Connected IoT",
                                style = MaterialTheme.typography.labelSmall,
                                color = DarkTextSecondary
                            )
                        }
                    }
                }
            }

            // Environment Selector Tabs (Scrollable for rich IoT device companion suite)
            ScrollableTabRow(
                selectedTabIndex = activeEnvironment.ordinal,
                containerColor = DarkSurfaceVariant,
                contentColor = NeonCyan,
                edgePadding = 8.dp
            ) {
                Tab(
                    selected = activeEnvironment == IotDeviceEnvironment.CAR_DASHBOARD,
                    onClick = { activeEnvironment = IotDeviceEnvironment.CAR_DASHBOARD },
                    icon = { Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    text = { Text("🚗 Car Dashboard", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = activeEnvironment == IotDeviceEnvironment.MP3_PLAYER,
                    onClick = { activeEnvironment = IotDeviceEnvironment.MP3_PLAYER },
                    icon = { Icon(Icons.Default.Headphones, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    text = { Text("🎵 MP3 Audio Player", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = activeEnvironment == IotDeviceEnvironment.REMOTE_COMPUTER,
                    onClick = { activeEnvironment = IotDeviceEnvironment.REMOTE_COMPUTER },
                    icon = { Icon(Icons.Default.Terminal, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    text = { Text("🖥️ Remote Workstation", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = activeEnvironment == IotDeviceEnvironment.SMARTWATCH,
                    onClick = { activeEnvironment = IotDeviceEnvironment.SMARTWATCH },
                    icon = { Icon(Icons.Default.Watch, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    text = { Text("⌚ Smartwatch OS", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = activeEnvironment == IotDeviceEnvironment.EINK_HOME_DISPLAY,
                    onClick = { activeEnvironment = IotDeviceEnvironment.EINK_HOME_DISPLAY },
                    icon = { Icon(Icons.Default.Tv, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    text = { Text("🏡 E-Ink Bedside Hub", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = activeEnvironment == IotDeviceEnvironment.AUTOMOTIVE_CLUSTER,
                    onClick = { activeEnvironment = IotDeviceEnvironment.AUTOMOTIVE_CLUSTER },
                    icon = { Icon(Icons.Default.DirectionsCar, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    text = { Text("🚘 In-Car Cluster", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }

            // Environment Views
            Box(modifier = Modifier.weight(1f)) {
                when (activeEnvironment) {
                    IotDeviceEnvironment.CAR_DASHBOARD -> IotCarDashboardView(liveTime = liveTime)
                    IotDeviceEnvironment.MP3_PLAYER -> IotMp3PlayerView()
                    IotDeviceEnvironment.REMOTE_COMPUTER -> IotRemoteComputerView()
                    IotDeviceEnvironment.SMARTWATCH -> SmartwatchSimulationView(
                        watchFaceStyle = watchFaceStyle,
                        activeTile = activeWatchTile,
                        isAmbient = isAmbientAod,
                        liveTime = liveTime,
                        heartRate = simulatedHeartRate,
                        steps = simulatedSteps,
                        isAudioPlaying = isWatchAudioPlaying,
                        volume = watchVolume,
                        isRecordingVoice = isRecordingVoice,
                        recordDuration = voiceRecordDuration,
                        onSelectWatchFace = { watchFaceStyle = it },
                        onSelectTile = { activeWatchTile = it },
                        onToggleAmbient = { isAmbientAod = !isAmbientAod },
                        onToggleAudioPlay = { isWatchAudioPlaying = !isWatchAudioPlaying },
                        onUpdateVolume = { watchVolume = it },
                        onToggleVoiceRecord = { isRecordingVoice = !isRecordingVoice }
                    )
                    IotDeviceEnvironment.EINK_HOME_DISPLAY -> EInkHomeDisplayView(liveTime = liveTime)
                    IotDeviceEnvironment.AUTOMOTIVE_CLUSTER -> AutomotiveClusterView(liveTime = liveTime)
                }
            }
        }
    }
}

// =========================================================================
// 1. SMARTWATCH SIMULATION VIEW
// =========================================================================
@Composable
private fun SmartwatchSimulationView(
    watchFaceStyle: WatchFaceStyle,
    activeTile: WatchTileApp,
    isAmbient: Boolean,
    liveTime: LocalDateTime,
    heartRate: Int,
    steps: Int,
    isAudioPlaying: Boolean,
    volume: Float,
    isRecordingVoice: Boolean,
    recordDuration: Int,
    onSelectWatchFace: (WatchFaceStyle) -> Unit,
    onSelectTile: (WatchTileApp) -> Unit,
    onToggleAmbient: () -> Unit,
    onToggleAudioPlay: () -> Unit,
    onUpdateVolume: (Float) -> Unit,
    onToggleVoiceRecord: () -> Unit
) {
    Column(
        modifier = Modifier
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
                when (activeTile) {
                    WatchTileApp.WATCH_FACE -> {
                        when (watchFaceStyle) {
                            WatchFaceStyle.CLASSIC_CHRONO -> ClassicChronoWatchFace(liveTime, heartRate, steps, isAmbient)
                            WatchFaceStyle.RETRO_TERMINAL -> RetroTerminalWatchFace(liveTime, steps, isAmbient)
                            WatchFaceStyle.NEON_CYBERPUNK -> NeonCyberpunkWatchFace(liveTime, heartRate, steps, isAmbient)
                        }
                    }
                    WatchTileApp.WRIST_NEWS -> WristNewsTile()
                    WatchTileApp.AUDIO_CONTROLLER -> WristAudioTile(isAudioPlaying, volume, onToggleAudioPlay, onUpdateVolume)
                    WatchTileApp.VOICE_MEMO -> WristVoiceMemoTile(isRecordingVoice, recordDuration, onToggleVoiceRecord)
                    WatchTileApp.HEALTH_SENSORS -> WristHealthTile(heartRate, steps)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Watch Tile Carousel Controls
        Text("WRIST TILES & COMPLICATIONS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NeonCyan)
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
                val isSel = activeTile == tile
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSel) NeonCyan else DarkSurfaceElevated,
                    border = BorderStroke(1.dp, if (isSel) NeonCyan else DarkBorder),
                    modifier = Modifier.clickable { onSelectTile(tile) }
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
        if (activeTile == WatchTileApp.WATCH_FACE) {
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
                        Text("WATCH FACE STYLES", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = WarmAmber)
                        // AOD Toggle
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Always-On AOD", fontSize = 11.sp, color = DarkTextSecondary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Switch(
                                checked = isAmbient,
                                onCheckedChange = { onToggleAmbient() },
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
                                modifier = Modifier.weight(1f).clickable { onSelectWatchFace(style) }
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

// =========================================================================
// 2. E-INK BEDIDE HUB VIEW
// =========================================================================
@Composable
private fun EInkHomeDisplayView(liveTime: LocalDateTime) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // E-Ink Frame
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF7F5EE),
            border = BorderStroke(3.dp, Color(0xFF4A463B)),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TOWNSQUARE E-INK DAILY BULLETIN",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFF1A1A1A)
                    )
                    Text(
                        text = liveTime.format(DateTimeFormatter.ofPattern("EEE MMM dd • HH:mm")),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Color(0xFF333333)
                    )
                }

                HorizontalDivider(color = Color(0xFF2B2B2B), thickness = 2.dp, modifier = Modifier.padding(vertical = 8.dp))

                Text(
                    text = "CIVIC CLEAN ENERGY ORDINANCE PASSES UNANIMOUSLY",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Commercial rooftops to begin solar conversions next quarter as transit corridors expand pedestrian lanes.",
                    fontFamily = FontFamily.Serif,
                    fontSize = 13.sp,
                    color = Color(0xFF222222)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("🏡 INDOOR CLIMATE", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF444444))
                        Text("70°F • 45% Humidity", fontFamily = FontFamily.Monospace, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                    Column {
                        Text("⛅ OUTDOOR RADAR", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF444444))
                        Text("72°F • Gentle Breeze", fontFamily = FontFamily.Monospace, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }
    }
}

// =========================================================================
// 3. IN-CAR AUTOMOTIVE CLUSTER VIEW
// =========================================================================
@Composable
private fun AutomotiveClusterView(liveTime: LocalDateTime) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF070C14),
            border = BorderStroke(2.dp, NeonCyan.copy(alpha = 0.7f)),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("TOWNSQUARE AUTO CLUSTER", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                    }
                    Text(liveTime.format(DateTimeFormatter.ofPattern("hh:mm a")), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("45", fontSize = 48.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Text("MPH SPEED", fontSize = 11.sp, color = DarkTextSecondary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("88.5 FM", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = WarmAmber)
                        Text("Sonic Frequencies", fontSize = 11.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF0F1A28),
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Navigation, contentDescription = null, tint = MintTeal)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Next Stop: Azure Palapa Coastal Cove", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                            Text("3.4 miles • Clear Traffic on Coastal Way", fontSize = 11.sp, color = DarkTextSecondary)
                        }
                    }
                }
            }
        }
    }
}
