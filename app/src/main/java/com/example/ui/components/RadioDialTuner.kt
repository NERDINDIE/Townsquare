package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioState
import com.example.data.model.MediaItemEntity
import com.example.ui.theme.CoralRed
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.MintTeal
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

// Helper to extract frequency value from station
fun parseStationFrequency(station: MediaItemEntity): Float {
    val regex = """(\d+\.?\d*)""".toRegex()
    val match = regex.find(station.stationFrequency) ?: regex.find(station.title)
    return match?.value?.toFloatOrNull() ?: 98.5f
}

/**
 * Hi-Fi Analog/Digital Radio Dial Tuner with frequency ruler, seek controls,
 * live RDS broadcast telemetry, and station presets.
 */
@Composable
fun RadioDialTuner(
    stations: List<MediaItemEntity>,
    audioState: AudioState,
    onPlayStation: (MediaItemEntity) -> Unit,
    onTogglePlayPause: () -> Unit,
    onOpenAndroidAutoMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    // Radio band limits (FM: 87.5 to 108.0 MHz)
    val minFreq = 87.5f
    val maxFreq = 108.0f

    // Current tuned frequency
    var currentFreq by remember { mutableFloatStateOf(98.5f) }
    val freqAnimatable = remember { Animatable(98.5f) }

    // Pair each station with its parsed frequency
    val parsedStations = remember(stations) {
        stations.map { station -> station to parseStationFrequency(station) }
            .sortedBy { it.second }
    }

    // Sync frequency with active playing item if it's a radio station
    LaunchedEffect(audioState.currentItem) {
        val playingItem = audioState.currentItem
        if (playingItem != null && stations.any { it.id == playingItem.id }) {
            val f = parseStationFrequency(playingItem)
            if (abs(f - currentFreq) > 0.1f) {
                currentFreq = f
                freqAnimatable.snapTo(f)
            }
        }
    }

    // Keep currentFreq in sync with animation
    LaunchedEffect(freqAnimatable.value) {
        currentFreq = (freqAnimatable.value * 10f).roundToInt() / 10f
    }

    // Identify closest station within capture window
    val nearestStationWithFreq = remember(currentFreq, parsedStations) {
        parsedStations.minByOrNull { abs(it.second - currentFreq) }
    }

    val isLockedOn = nearestStationWithFreq != null && abs(nearestStationWithFreq.second - currentFreq) <= 0.15f
    val lockedStation = if (isLockedOn) nearestStationWithFreq?.first else null

    // Signal strength calculation (0 to 100%)
    val signalPercent = remember(currentFreq, nearestStationWithFreq) {
        if (nearestStationWithFreq == null) 5
        else {
            val delta = abs(nearestStationWithFreq.second - currentFreq)
            when {
                delta <= 0.05f -> 99
                delta <= 0.15f -> 85
                delta <= 0.35f -> 62
                delta <= 0.60f -> 35
                delta <= 0.90f -> 18
                else -> 6
            }
        }
    }

    val isCurrentlyStreaming = lockedStation != null &&
            audioState.isPlaying &&
            audioState.currentItem?.id == lockedStation.id

    // Seek helper functions
    fun seekToNextStation() {
        if (parsedStations.isEmpty()) return
        val next = parsedStations.firstOrNull { it.second > currentFreq + 0.15f }
            ?: parsedStations.first() // Wrap to first
        coroutineScope.launch {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            freqAnimatable.animateTo(
                targetValue = next.second,
                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
            )
            onPlayStation(next.first)
        }
    }

    fun seekToPrevStation() {
        if (parsedStations.isEmpty()) return
        val prev = parsedStations.lastOrNull { it.second < currentFreq - 0.15f }
            ?: parsedStations.last() // Wrap to last
        coroutineScope.launch {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            freqAnimatable.animateTo(
                targetValue = prev.second,
                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
            )
            onPlayStation(prev.first)
        }
    }

    fun stepFrequency(delta: Float) {
        val target = (currentFreq + delta).coerceIn(minFreq, maxFreq)
        coroutineScope.launch {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            freqAnimatable.snapTo(target)
        }
    }

    fun tuneDirectlyTo(station: MediaItemEntity, freq: Float) {
        coroutineScope.launch {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            freqAnimatable.animateTo(
                targetValue = freq,
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            )
            onPlayStation(station)
        }
    }

    // Pulsing indicator for live audio
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("radio_dial_tuner_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, DarkBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Bar: Tuner Title, Stereo Badge & Android Auto Launch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(NeonCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Radio,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "ANALOG & DIGITAL FM TUNER",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = NeonCyan
                        )
                        Text(
                            text = "Seek Local Stations • 87.5 - 108.0 MHz",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Android Auto in-car button
                FilledTonalButton(
                    onClick = onOpenAndroidAutoMode,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MintTeal.copy(alpha = 0.15f),
                        contentColor = MintTeal
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("android_auto_launch_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = "Android Auto",
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Android Auto",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // RECEIVER DISPLAY WINDOW (Illuminated LCD/VFD)
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF070B12),
                                Color(0xFF0F1522)
                            )
                        )
                    )
                    .border(1.dp, if (isLockedOn) NeonCyan.copy(alpha = 0.4f) else DarkBorder, RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Column {
                    // Top Telemetry Row: Stereo / Tuned / Signal
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Stereo and Locked Indicators
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isLockedOn) NeonCyan else Color.DarkGray
                                    )
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "STEREO",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = if (isLockedOn) NeonCyan else Color.Gray
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isLockedOn) WarmAmber else Color.DarkGray
                                    )
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "LOCKED",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = if (isLockedOn) WarmAmber else Color.Gray
                            )
                        }

                        // Signal Strength Meter
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "SIGNAL $signalPercent%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = if (signalPercent > 60) NeonCyan else WarmAmber
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            // 5-segment LED signal meter
                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                (1..5).forEach { segment ->
                                    val active = signalPercent >= (segment * 20 - 10)
                                    Box(
                                        modifier = Modifier
                                            .width(4.dp)
                                            .height((6 + segment * 2).dp)
                                            .clip(RoundedCornerShape(1.dp))
                                            .background(
                                                if (active) {
                                                    if (segment == 5) CoralRed else if (segment >= 3) NeonCyan else WarmAmber
                                                } else Color(0xFF202938)
                                            )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Center Row: Big Glowing Frequency & Live Status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = String.format("%.1f", currentFreq),
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = (-1).sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = if (isLockedOn) NeonCyan else WarmAmber
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column(modifier = Modifier.padding(bottom = 6.dp)) {
                                Text(
                                    text = "MHz",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = NeonCyan
                                )
                                Text(
                                    text = "FM BROADCAST",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Live audio spectrum or Tune Action
                        if (lockedStation != null) {
                            if (isCurrentlyStreaming) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CoralRed.copy(alpha = 0.15f * pulseAlpha))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.GraphicEq,
                                        contentDescription = null,
                                        tint = CoralRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "ON AIR",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = CoralRed
                                        )
                                    )
                                }
                            } else {
                                Button(
                                    onClick = { onPlayStation(lockedStation) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = NeonCyan,
                                        contentColor = DarkBg
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                        horizontal = 12.dp,
                                        vertical = 6.dp
                                    ),
                                    modifier = Modifier.testTag("listen_live_dial_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "TUNE IN",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = "STATIC / SEEKING",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                ),
                                color = WarmAmber.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // RDS Text Telemetry (Station Name, Live Program, or Frequency Guide)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF070B12))
                            .border(BorderStroke(0.5.dp, DarkBorder), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "RDS:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                ),
                                color = WarmAmber
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (lockedStation != null) {
                                    "${lockedStation.title.uppercase()} • ${lockedStation.subtitle}"
                                } else {
                                    "SCANNING LOCAL SPECTRUM • TAP 'SEEK' TO SNAP TO NEAREST STATION"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                ),
                                color = if (lockedStation != null) NeonCyan else Color.LightGray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // ANALOG FREQUENCY SCALE RULER (Interactive Canvas)
            // ==========================================
            Text(
                text = "DRAG TO SCRUB FREQUENCY BAND",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    fontSize = 9.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0A0F1A))
                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                    .pointerInput(parsedStations, currentFreq) {
                        detectHorizontalDragGestures { change, dragAmount ->
                            change.consume()
                            // Drag sensitivity: 1px = ~0.04 MHz
                            val deltaFreq = -dragAmount * 0.035f
                            val newFreq = (currentFreq + deltaFreq).coerceIn(minFreq, maxFreq)
                            currentFreq = (newFreq * 10f).roundToInt() / 10f
                            coroutineScope.launch {
                                freqAnimatable.snapTo(currentFreq)
                            }
                            // Trigger light haptic on whole 0.2 ticks
                            if ((currentFreq * 10).toInt() % 2 == 0) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        }
                    }
                    .pointerInput(parsedStations) {
                        detectTapGestures { offset ->
                            // Tap on ruler calculates relative frequency
                            val width = size.width
                            val clickRatio = (offset.x / width).coerceIn(0f, 1f)
                            // Show roughly +/- 3.0 MHz window around current
                            val targetFreq = (currentFreq + (clickRatio - 0.5f) * 6.0f).coerceIn(minFreq, maxFreq)
                            val rounded = (targetFreq * 10f).roundToInt() / 10f
                            coroutineScope.launch {
                                freqAnimatable.animateTo(
                                    targetValue = rounded,
                                    animationSpec = tween(200)
                                )
                            }
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRadioRuler(
                        currentFreq = currentFreq,
                        minFreq = minFreq,
                        maxFreq = maxFreq,
                        stations = parsedStations
                    )
                }

                // Center Tuning Needle Glass Overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .width(2.dp)
                        .height(68.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    CoralRed.copy(alpha = 0.4f),
                                    CoralRed,
                                    CoralRed,
                                    CoralRed.copy(alpha = 0.4f)
                                )
                            )
                        )
                )

                // Needle Jewel Indicator at top
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 2.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(CoralRed)
                        .border(1.dp, Color.White, CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // TUNING CONTROLS: SEEK ◀ / Fine Tune / SEEK ▶
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Seek Down
                Button(
                    onClick = { seekToPrevStation() },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkSurfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = BorderStroke(1.dp, DarkBorder),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("radio_seek_prev_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.FastRewind,
                        contentDescription = "Seek Previous",
                        tint = WarmAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SEEK ◀",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                // Fine Tune -0.1 & +0.1
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { stepFrequency(-0.1f) },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("fine_tune_minus_button")
                    ) {
                        Text(
                            text = "-0.1",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace)
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )

                    OutlinedButton(
                        onClick = { stepFrequency(+0.1f) },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("fine_tune_plus_button")
                    ) {
                        Text(
                            text = "+0.1",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace)
                        )
                    }
                }

                // Seek Up
                Button(
                    onClick = { seekToNextStation() },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkSurfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = BorderStroke(1.dp, DarkBorder),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("radio_seek_next_button")
                ) {
                    Text(
                        text = "SEEK ▶",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = "Seek Next",
                        tint = NeonCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // PRESETS BANK: P1 .. P7 (One-Touch Tuning)
            // ==========================================
            Text(
                text = "LOCAL STATION PRESETS (ONE-TAP TUNE)",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    fontSize = 9.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                parsedStations.take(7).forEachIndexed { index, (station, freq) ->
                    val isPresetSelected = abs(currentFreq - freq) <= 0.15f
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isPresetSelected) NeonCyan.copy(alpha = 0.2f) else DarkSurfaceVariant,
                        border = BorderStroke(
                            1.dp,
                            if (isPresetSelected) NeonCyan else DarkBorder
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { tuneDirectlyTo(station, freq) }
                            .testTag("preset_${index + 1}")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "P${index + 1}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                ),
                                color = if (isPresetSelected) NeonCyan else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = String.format("%.1f", freq),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = if (isPresetSelected) NeonCyan else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Custom Canvas drawing for the analog frequency ruler scale with ticks, MHz labels,
 * and station beacons.
 */
private fun DrawScope.drawRadioRuler(
    currentFreq: Float,
    minFreq: Float,
    maxFreq: Float,
    stations: List<Pair<MediaItemEntity, Float>>
) {
    val canvasWidth = size.width
    val canvasHeight = size.height
    val centerX = canvasWidth / 2f

    // Pixels per MHz: e.g. 1 MHz = 90 dp/px
    val pxPerMhz = canvasWidth / 5.5f

    // Frequency window visible
    val visibleSpanMhz = canvasWidth / pxPerMhz
    val startVisibleFreq = (currentFreq - visibleSpanMhz / 2f).coerceAtLeast(minFreq - 1f)
    val endVisibleFreq = (currentFreq + visibleSpanMhz / 2f).coerceAtMost(maxFreq + 1f)

    // Draw baseline
    val baselineY = canvasHeight - 18f
    drawLine(
        color = Color(0xFF26334D),
        start = Offset(0f, baselineY),
        end = Offset(canvasWidth, baselineY),
        strokeWidth = 2f
    )

    // Draw ticks every 0.1 MHz
    var f = (startVisibleFreq * 10).toInt() / 10f
    while (f <= endVisibleFreq) {
        val x = centerX + (f - currentFreq) * pxPerMhz
        if (x in 0f..canvasWidth) {
            val isWholeMhz = ((f * 10).roundToInt() % 10) == 0
            val isHalfMhz = ((f * 10).roundToInt() % 5) == 0 && !isWholeMhz

            val tickHeight = when {
                isWholeMhz -> 28f
                isHalfMhz -> 18f
                else -> 10f
            }

            val tickColor = when {
                isWholeMhz -> Color(0xFFE2E8F0)
                isHalfMhz -> Color(0xFF94A3B8)
                else -> Color(0xFF334155)
            }

            drawLine(
                color = tickColor,
                start = Offset(x, baselineY),
                end = Offset(x, baselineY - tickHeight),
                strokeWidth = if (isWholeMhz) 2.5f else 1.5f,
                cap = StrokeCap.Round
            )

            // Draw Frequency numeric labels on even MHz numbers (e.g. 88, 90, 92, 94, 96, 98, 100...)
            if (isWholeMhz && (f.toInt() % 2 == 0) && f in minFreq..maxFreq) {
                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.parseColor("#00D2FF")
                        textSize = 28f
                        textAlign = android.graphics.Paint.Align.CENTER
                        typeface = android.graphics.Typeface.MONOSPACE
                        isAntiAlias = true
                        isFakeBoldText = true
                    }
                    drawText(f.toInt().toString(), x, baselineY - tickHeight - 8f, paint)
                }
            }
        }
        f = ((f + 0.1f) * 10).roundToInt() / 10f
    }

    // Draw station markers/beacons along ruler
    stations.forEach { (_, stationFreq) ->
        val x = centerX + (stationFreq - currentFreq) * pxPerMhz
        if (x in 0f..canvasWidth) {
            val isClose = abs(stationFreq - currentFreq) <= 0.15f

            // Beacon glowing line down
            drawLine(
                color = if (isClose) Color(0xFFFF9F1C) else Color(0xFF00D2FF).copy(alpha = 0.6f),
                start = Offset(x, baselineY + 4f),
                end = Offset(x, baselineY - 32f),
                strokeWidth = if (isClose) 3f else 1.5f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
            )

            // Beacon dot
            drawCircle(
                color = if (isClose) Color(0xFFFF9F1C) else Color(0xFF00D2FF),
                radius = if (isClose) 6f else 4f,
                center = Offset(x, baselineY - 36f)
            )
        }
    }
}
