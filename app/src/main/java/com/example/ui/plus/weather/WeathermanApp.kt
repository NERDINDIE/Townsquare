package com.example.ui.plus.weather

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.plus.weather.model.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeathermanApp(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    BackHandler { onBack() }

    val initialEpisodes = remember { WeathermanSeed.getInitialEpisodes() }
    var episodes by remember { mutableStateOf(initialEpisodes) }
    var currentEpisode by remember { mutableStateOf(episodes.first()) }

    var isPlaying by remember { mutableStateOf(false) }
    var playbackProgress by remember { mutableFloatStateOf(0f) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var activeTab by remember { mutableIntStateOf(0) } // 0: Now Playing & Forecast, 1: Show Notes & Transcript, 2: Episodes Library
    var isGeneratorOpen by remember { mutableStateOf(false) }

    // Ambient sound toggles
    var ambientRain by remember { mutableStateOf(false) }
    var ambientWind by remember { mutableStateOf(false) }
    var ambientVinyl by remember { mutableStateOf(true) }

    // Android TextToSpeech Setup
    var ttsInstance by remember { mutableStateOf<TextToSpeech?>(null) }
    var isTtsReady by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        var tts: TextToSpeech? = null
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.ENGLISH
                ttsInstance = tts
                isTtsReady = true
            }
        }

        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    // Playback logic with TTS
    fun playCurrentEpisode() {
        val tts = ttsInstance
        if (tts != null && isTtsReady) {
            tts.stop()
            tts.setPitch(currentEpisode.host.speechPitch)
            tts.setSpeechRate(currentEpisode.host.speechRate * playbackSpeed)
            tts.speak(currentEpisode.audioScript, TextToSpeech.QUEUE_FLUSH, null, "weatherman_utterance")
            isPlaying = true
        } else {
            isPlaying = true
        }
    }

    fun pauseCurrentEpisode() {
        ttsInstance?.stop()
        isPlaying = false
    }

    // Simulated progress timer while playing
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isPlaying && playbackProgress < 1f) {
                delay(300L)
                playbackProgress = (playbackProgress + 0.005f * playbackSpeed).coerceAtMost(1f)
                if (playbackProgress >= 1f) {
                    isPlaying = false
                    playbackProgress = 0f
                }
            }
        }
    }

    val hostColor = remember(currentEpisode.host.badgeColorHex) { Color(currentEpisode.host.badgeColorHex) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("weatherman_app_screen"),
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Weatherman", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black), color = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = WarmAmber.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, WarmAmber.copy(alpha = 0.4f))
                            ) {
                                Text("PODCAST PROTOTYPE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = WarmAmber, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        Text("Personalized Audio Forecasts & Atmospheric Radio", style = MaterialTheme.typography.labelSmall, color = NeonCyan)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("weatherman_back_btn")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { isGeneratorOpen = true }, modifier = Modifier.testTag("weatherman_generate_btn")) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Generate Custom Podcast", tint = NeonCyan, modifier = Modifier.size(24.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurfaceElevated)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // TAB ROW
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = DarkSurfaceElevated,
                contentColor = NeonCyan,
                indicator = { tabPositions ->
                    if (activeTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]), color = NeonCyan)
                    }
                }
            ) {
                Tab(selected = activeTab == 0, onClick = { activeTab = 0 }, text = { Text("🎙️ Now Playing") })
                Tab(selected = activeTab == 1, onClick = { activeTab = 1 }, text = { Text("📜 Show Notes") })
                Tab(selected = activeTab == 2, onClick = { activeTab = 2 }, text = { Text("📻 Archives (${episodes.size})") })
            }

            when (activeTab) {
                0 -> {
                    // MAIN NOW PLAYING & TELEMETRY SCREEN
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // 1. HERO PODCAST AUDIO PLAYER CARD
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(22.dp),
                                color = DarkSurface,
                                border = BorderStroke(1.dp, hostColor.copy(alpha = 0.45f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("weatherman_player_card")
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    hostColor.copy(alpha = 0.12f),
                                                    DarkSurface
                                                )
                                            )
                                        )
                                        .padding(20.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        // Host Persona Avatar & Badge
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Surface(
                                                    shape = CircleShape,
                                                    color = hostColor.copy(alpha = 0.2f),
                                                    border = BorderStroke(1.dp, hostColor.copy(alpha = 0.6f)),
                                                    modifier = Modifier.size(46.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Text(currentEpisode.host.avatarEmoji, fontSize = 22.sp)
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(currentEpisode.host.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                    Text(currentEpisode.host.subtitle, fontSize = 10.sp, color = DarkTextSecondary)
                                                }
                                            }

                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = DarkSurfaceElevated
                                            ) {
                                                Text(
                                                    text = "EP #${currentEpisode.episodeNumber}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = hostColor,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(14.dp))

                                        // Episode Title & Location
                                        Text(
                                            text = currentEpisode.title,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "📍 ${currentEpisode.location} • ${currentEpisode.dateString}",
                                            fontSize = 11.sp,
                                            color = NeonCyan
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // EQUALIZER SOUND WAVEFORM
                                        AnimatedEqualizerWaveform(isPlaying = isPlaying, accentColor = hostColor)

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // SCRUBBER / PROGRESS SLIDER
                                        Slider(
                                            value = playbackProgress,
                                            onValueChange = { playbackProgress = it },
                                            colors = SliderDefaults.colors(
                                                thumbColor = hostColor,
                                                activeTrackColor = hostColor,
                                                inactiveTrackColor = DarkBorder
                                            ),
                                            modifier = Modifier.fillMaxWidth().height(24.dp)
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            val currentSecs = (playbackProgress * currentEpisode.durationSeconds).toInt()
                                            val remainingSecs = currentEpisode.durationSeconds - currentSecs
                                            Text(String.format("%02d:%02d", currentSecs / 60, currentSecs % 60), fontSize = 10.sp, color = DarkTextSecondary)
                                            Text("-${String.format("%02d:%02d", remainingSecs / 60, remainingSecs % 60)}", fontSize = 10.sp, color = DarkTextSecondary)
                                        }

                                        Spacer(modifier = Modifier.height(14.dp))

                                        // PLAYER CONTROLS (Skip -15s, Play/Pause, Skip +15s, Speed)
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceEvenly,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Speed toggle
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = DarkSurfaceElevated,
                                                modifier = Modifier.clickable {
                                                    playbackSpeed = when (playbackSpeed) {
                                                        0.8f -> 1.0f
                                                        1.0f -> 1.25f
                                                        1.25f -> 1.5f
                                                        else -> 0.8f
                                                    }
                                                    if (isPlaying) playCurrentEpisode()
                                                }
                                            ) {
                                                Text(
                                                    text = "${playbackSpeed}x",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                                )
                                            }

                                            // Skip -15s
                                            IconButton(
                                                onClick = {
                                                    playbackProgress = (playbackProgress - 0.15f).coerceAtLeast(0f)
                                                }
                                            ) {
                                                Icon(Icons.Default.Replay10, contentDescription = "Rewind 10s", tint = Color.White, modifier = Modifier.size(28.dp))
                                            }

                                            // Main Play / Pause Button
                                            Surface(
                                                shape = CircleShape,
                                                color = hostColor,
                                                modifier = Modifier
                                                    .size(60.dp)
                                                    .clickable {
                                                        if (isPlaying) pauseCurrentEpisode() else playCurrentEpisode()
                                                    }
                                                    .testTag("weatherman_play_pause_btn")
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                        contentDescription = if (isPlaying) "Pause" else "Play",
                                                        tint = Color(0xFF003544),
                                                        modifier = Modifier.size(34.dp)
                                                    )
                                                }
                                            }

                                            // Skip +15s
                                            IconButton(
                                                onClick = {
                                                    playbackProgress = (playbackProgress + 0.15f).coerceAtMost(1f)
                                                }
                                            ) {
                                                Icon(Icons.Default.Forward10, contentDescription = "Forward 10s", tint = Color.White, modifier = Modifier.size(28.dp))
                                            }

                                            // Regenerate / Voice reload
                                            IconButton(
                                                onClick = {
                                                    playCurrentEpisode()
                                                    Toast.makeText(context, "Voice narration synced with ${currentEpisode.host.title} 🎙️", Toast.LENGTH_SHORT).show()
                                                }
                                            ) {
                                                Icon(Icons.Default.VolumeUp, contentDescription = "Audio Engine", tint = NeonCyan, modifier = Modifier.size(24.dp))
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // Catchphrase Banner
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = DarkSurfaceElevated,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                                Text("💬", fontSize = 13.sp)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "\"${currentEpisode.host.catchphrase}\"",
                                                    fontSize = 11.sp,
                                                    fontFamily = FontFamily.Serif,
                                                    color = DarkTextMuted,
                                                    maxLines = 2,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 2. QUICK HOST PERSONA SWITCHER
                        item {
                            Text(
                                text = "SELECT PODCAST HOST PERSONA",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                color = NeonCyan
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(HostPersona.entries) { persona ->
                                    val isSelected = currentEpisode.host == persona
                                    val personaColor = remember(persona.badgeColorHex) { Color(persona.badgeColorHex) }
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) personaColor.copy(alpha = 0.2f) else DarkSurface,
                                        border = BorderStroke(1.dp, if (isSelected) personaColor else DarkBorder),
                                        modifier = Modifier.clickable {
                                            val generated = WeathermanSeed.generatePersonalizedEpisode(
                                                city = currentEpisode.location.split("•").first().trim(),
                                                persona = persona,
                                                commuteMode = "Canal Promenade Walk",
                                                outdoorActivity = "Morning Coffee Patio"
                                            )
                                            currentEpisode = generated
                                            playbackProgress = 0f
                                            if (isPlaying) playCurrentEpisode()
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(persona.avatarEmoji, fontSize = 14.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = persona.title,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) personaColor else Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 3. METEOROLOGICAL TELEMETRY DASHBOARD
                        item {
                            Text(
                                text = "ATMOSPHERIC TELEMETRY",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                color = WarmAmber
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = DarkSurface,
                                border = BorderStroke(1.dp, DarkBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    // Row 1: Temp & Condition
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(currentEpisode.conditionEmoji, fontSize = 32.sp)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(currentEpisode.temperature, fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                                                Text(currentEpisode.condition, fontSize = 11.sp, color = DarkTextSecondary)
                                            }
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFF1B2333),
                                            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f))
                                        ) {
                                            Text(
                                                text = currentEpisode.airQualityIndex,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = NeonCyan,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 12.dp))

                                    // Grid of 4 sensor readings
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        TelemetryPill(label = "BAROMETER", value = currentEpisode.barometer, icon = Icons.Default.Speed)
                                        TelemetryPill(label = "WIND", value = currentEpisode.windSpeed, icon = Icons.Default.Air)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        TelemetryPill(label = "HUMIDITY", value = currentEpisode.humidity, icon = Icons.Default.WaterDrop)
                                        TelemetryPill(label = "RAIN CHANCE", value = currentEpisode.precipitationChance, icon = Icons.Default.CloudQueue)
                                    }

                                    HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 12.dp))

                                    // Umbrella & Attire Advice
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("UMBRELLA INDEX", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = WarmAmber)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(currentEpisode.umbrellaRating, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                        Column(modifier = Modifier.weight(1.2f)) {
                                            Text("ATTIRE RECOMMENDATION", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(currentEpisode.attireAdvice, fontSize = 11.sp, color = DarkTextMuted, lineHeight = 15.sp)
                                        }
                                    }
                                }
                            }
                        }

                        // 4. AMBIENT SOUNDSCAPE GENERATOR TOGGLES
                        item {
                            Text(
                                text = "ATMOSPHERIC SOUNDSCAPE SYNTHESIS",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                color = NeonCyan
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = ambientVinyl,
                                    onClick = { ambientVinyl = !ambientVinyl },
                                    label = { Text("📻 Vinyl Static", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = WarmAmber.copy(alpha = 0.2f), selectedLabelColor = WarmAmber)
                                )
                                FilterChip(
                                    selected = ambientRain,
                                    onClick = { ambientRain = !ambientRain },
                                    label = { Text("🌧️ Rain Glass", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = NeonCyan.copy(alpha = 0.2f), selectedLabelColor = NeonCyan)
                                )
                                FilterChip(
                                    selected = ambientWind,
                                    onClick = { ambientWind = !ambientWind },
                                    label = { Text("🍃 Wind Chime", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF70E000).copy(alpha = 0.2f), selectedLabelColor = Color(0xFF70E000))
                                )
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(60.dp))
                        }
                    }
                }

                1 -> {
                    // SHOW NOTES & TRANSCRIPT
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text("BROADCAST SHOW SCRIPT & TRANSCRIPT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan, letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = DarkSurface,
                                border = BorderStroke(1.dp, DarkBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(currentEpisode.title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                        Surface(shape = RoundedCornerShape(6.dp), color = hostColor.copy(alpha = 0.2f)) {
                                            Text(currentEpisode.host.title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = hostColor, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }

                                    HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 10.dp))

                                    Text(
                                        text = currentEpisode.audioScript,
                                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                                        fontFamily = FontFamily.Serif,
                                        color = Color.White.copy(alpha = 0.95f)
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Button(
                                        onClick = { playCurrentEpisode() },
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Speak Transcript Aloud with TTS 🎙️", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // EPISODES ARCHIVE
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(episodes, key = { it.id }) { ep ->
                            val isCurrent = ep.id == currentEpisode.id
                            val epHostColor = remember(ep.host.badgeColorHex) { Color(ep.host.badgeColorHex) }

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isCurrent) DarkSurfaceElevated else DarkSurface,
                                border = BorderStroke(1.dp, if (isCurrent) NeonCyan else DarkBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        currentEpisode = ep
                                        playbackProgress = 0f
                                        playCurrentEpisode()
                                        activeTab = 0
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                        Text(ep.conditionEmoji, fontSize = 24.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(ep.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                            Text("EP #${ep.episodeNumber} • ${ep.host.title} • ${ep.temperature}", fontSize = 10.sp, color = DarkTextSecondary)
                                        }
                                    }

                                    Surface(
                                        shape = CircleShape,
                                        color = if (isCurrent && isPlaying) NeonCyan else epHostColor.copy(alpha = 0.2f),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = if (isCurrent && isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                tint = if (isCurrent && isPlaying) Color(0xFF003544) else epHostColor,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // GENERATE CUSTOM PODCAST DIALOG
    if (isGeneratorOpen) {
        GenerateWeatherPodcastDialog(
            onDismiss = { isGeneratorOpen = false },
            onGenerate = { newEp ->
                episodes = listOf(newEp) + episodes
                currentEpisode = newEp
                playbackProgress = 0f
                isGeneratorOpen = false
                activeTab = 0
                playCurrentEpisode()
                Toast.makeText(context, "Weather podcast synthesized for ${newEp.location}! 🎙️", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

// ==========================================
// EQUALIZER & TELEMETRY SUBCOMPONENTS
// ==========================================

@Composable
fun AnimatedEqualizerWaveform(isPlaying: Boolean, accentColor: Color) {
    val barCount = 28
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(DarkSurfaceElevated, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until barCount) {
            val animHeight by infiniteTransition.animateFloat(
                initialValue = 0.15f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 350 + (i * 35) % 400, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bar_$i"
            )

            val currentHeight = if (isPlaying) (animHeight * 32).dp.coerceAtLeast(4.dp) else (4 + (i % 3) * 3).dp

            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(currentHeight)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accentColor.copy(alpha = if (isPlaying) 0.85f else 0.35f))
            )
        }
    }
}

@Composable
fun TelemetryPill(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = DarkSurfaceElevated,
        modifier = Modifier.width(155.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(label, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = DarkTextSecondary)
                Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

// ==========================================
// GENERATE CUSTOM PODCAST DIALOG
// ==========================================

@Composable
fun GenerateWeatherPodcastDialog(
    onDismiss: () -> Unit,
    onGenerate: (WeatherPodcastEpisode) -> Unit
) {
    var cityInput by remember { mutableStateOf("Townsquare Promenade") }
    var selectedPersona by remember { mutableStateOf(HostPersona.VINTAGE_BROADCASTER) }
    var commuteMode by remember { mutableStateOf("Bicycle along the canal") }
    var outdoorActivity by remember { mutableStateOf("Morning Espresso Patio") }

    val commuteOptions = listOf(
        "Bicycle along the canal",
        "Pedestrian walking commute",
        "Vintage Electric Tram",
        "Ferry across the bay",
        "Motorcar / Roadster"
    )

    val outdoorOptions = listOf(
        "Morning Espresso Patio",
        "Photography walk in Old Quarter",
        "Canal jogging workout",
        "Rooftop dining with friends",
        "Botanical garden walk"
    )

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(20.dp),
            color = DarkBg,
            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Synthesize Weather Podcast", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                        Text("Personalized meteorology & lifestyle dispatch", fontSize = 11.sp, color = NeonCyan)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 12.dp))

                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Location
                    item {
                        Text("1. YOUR CITY OR DISTRICT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = cityInput,
                            onValueChange = { cityInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan, unfocusedBorderColor = DarkBorder)
                        )
                    }

                    // Host persona
                    item {
                        Text("2. SELECT HOST PERSONA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                        Spacer(modifier = Modifier.height(6.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            HostPersona.entries.forEach { persona ->
                                val isSelected = selectedPersona == persona
                                val pColor = remember(persona.badgeColorHex) { Color(persona.badgeColorHex) }
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) pColor.copy(alpha = 0.2f) else DarkSurface,
                                    border = BorderStroke(1.dp, if (isSelected) pColor else DarkBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedPersona = persona }
                                ) {
                                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text(persona.avatarEmoji, fontSize = 20.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(persona.title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                                            Text(persona.subtitle, fontSize = 10.sp, color = DarkTextSecondary)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Commute style
                    item {
                        Text("3. COMMUTE METHOD", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(commuteOptions) { opt ->
                                val isSelected = commuteMode == opt
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { commuteMode = opt },
                                    label = { Text(opt, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = NeonCyan.copy(alpha = 0.2f), selectedLabelColor = NeonCyan)
                                )
                            }
                        }
                    }

                    // Planned outdoor activity
                    item {
                        Text("4. OUTDOOR LIFESTYLE ACTIVITY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = WarmAmber)
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(outdoorOptions) { opt ->
                                val isSelected = outdoorActivity == opt
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { outdoorActivity = opt },
                                    label = { Text(opt, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = WarmAmber.copy(alpha = 0.2f), selectedLabelColor = WarmAmber)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        val newEp = WeathermanSeed.generatePersonalizedEpisode(
                            city = if (cityInput.isNotBlank()) cityInput else "Townsquare",
                            persona = selectedPersona,
                            commuteMode = commuteMode,
                            outdoorActivity = outdoorActivity
                        )
                        onGenerate(newEp)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Synthesize & Play Episode 🎙️", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}
