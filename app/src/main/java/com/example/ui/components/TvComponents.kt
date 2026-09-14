package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.ClosedCaptionDisabled
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Hd
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TvChannelEntity
import com.example.ui.theme.DarkBg
import com.example.ui.theme.NeonCyan
import kotlin.math.sin

import com.example.ui.viewmodel.TvChatMessage

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.ClosedCaptionDisabled
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Hd
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.ChatBubbleOutline


@Composable
fun TvBroadcastPlayer(
    channel: TvChannelEntity,
    isPlaying: Boolean,
    isMuted: Boolean,
    isCaptionsEnabled: Boolean,
    streamQuality: String,
    scanlineFxEnabled: Boolean,
    chatMessages: List<TvChatMessage> = emptyList(),
    onSendChatMessage: (String) -> Unit = {},
    onSelectQuality: (String) -> Unit = {},
    isFullscreen: Boolean = false,
    onTogglePlayPause: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleCaptions: () -> Unit,
    onToggleFullscreen: () -> Unit,
    onToggleScanlineFx: () -> Unit,
    onToggleRecording: (() -> Unit)? = null,
    onNextChannel: () -> Unit,
    onPrevChannel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showQualityDialog by remember { mutableStateOf(false) }
    var showChatOverlay by remember { mutableStateOf(false) }
    var chatInput by remember { mutableStateOf("") }

    val infiniteTransition = rememberInfiniteTransition(label = "broadcast_anim")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val channelColor = Color(channel.themeColorHex)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(if (isFullscreen) Modifier.fillMaxSize() else Modifier.aspectRatio(16f / 9f))
            .clip(if (isFullscreen) RoundedCornerShape(0.dp) else RoundedCornerShape(16.dp))
            .background(DarkBg)
            .border(
                width = if (isFullscreen) 0.dp else 1.dp,
                color = channelColor.copy(alpha = 0.3f),
                shape = if (isFullscreen) RoundedCornerShape(0.dp) else RoundedCornerShape(16.dp)
            )
            .testTag("tv_broadcast_player_${channel.id}")
    ) {
        // 1. Dynamic Program Canvas Shader
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (isPlaying) {
                when (channel.channelNumber) {
                    1 -> drawNewsroomShader(channelColor, waveOffset)
                    2 -> drawSymphonyShader(channelColor, waveOffset)
                    3 -> drawScienceSpaceShader(channelColor, waveOffset)
                    4 -> drawSportsFieldShader(channelColor, waveOffset)
                    5 -> drawCinemaDramaShader(channelColor, waveOffset)
                    6 -> drawSynthWaveShader(channelColor, waveOffset)
                    else -> drawNewsroomShader(channelColor, waveOffset)
                }
            } else {
                drawOffAirTestCard(size.width, size.height, channel.name)
            }

            // Vintage CRT Scanline Effect Overlay
            if (scanlineFxEnabled) {
                val step = 4f
                for (y in 0 until size.height.toInt() step step.toInt()) {
                    drawLine(
                        color = Color.Black.copy(alpha = 0.25f),
                        start = Offset(0f, y.toFloat()),
                        end = Offset(size.width, y.toFloat()),
                        strokeWidth = 1f
                    )
                }
            }
        }


        // Chat Overlay
        if (showChatOverlay) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(280.dp)
                    .align(Alignment.CenterEnd)
                    .background(Color.Black.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                    Text("Live Chat", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    androidx.compose.foundation.lazy.LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Bottom,
                        reverseLayout = false
                    ) {
                        items(chatMessages) { msg ->
                            Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                Text(
                                    text = "${msg.sender}: ",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(msg.color)
                                )
                                Text(
                                    text = msg.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.LightGray
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        androidx.compose.material3.TextField(
                            value = chatInput,
                            onValueChange = { chatInput = it },
                            placeholder = { Text("Say something...", fontSize = 11.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            textStyle = MaterialTheme.typography.bodySmall,
                            colors = androidx.compose.material3.TextFieldDefaults.colors(
                                focusedContainerColor = Color.DarkGray.copy(alpha = 0.5f),
                                unfocusedContainerColor = Color.DarkGray.copy(alpha = 0.3f),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(24.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (chatInput.isNotBlank()) {
                                    onSendChatMessage(chatInput)
                                    chatInput = ""
                                }
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = NeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2. Top Watermark & Status Overlay

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Live Status & Callsign Bug
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            if (isPlaying) Color(0xFFFF3B30).copy(alpha = pulseAlpha) else Color.Gray,
                            CircleShape
                        )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isPlaying) "LIVE" else "PAUSED",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ),
                    color = if (isPlaying) Color.White else Color.LightGray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${channel.callsign} • CH ${channel.channelNumber}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = channelColor
                )
            }

            // Resolution & Stream Quality Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.clickable { showQualityDialog = true }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Hd,
                            contentDescription = "Stream Quality",
                            tint = NeonCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = streamQuality,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = Color.White
                        )
                    }
                }

                // Scanline Filter Button
                Surface(
                    color = if (scanlineFxEnabled) channelColor.copy(alpha = 0.25f) else Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.clickable { onToggleScanlineFx() }
                ) {
                    Text(
                        text = "CRT FX",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = if (scanlineFxEnabled) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (scanlineFxEnabled) channelColor else Color.LightGray,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // 3. Lower Third Live Graphic Bar
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
        ) {
            // Live Subtitles/Captions
            if (isCaptionsEnabled && isPlaying) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Text(
                            text = getSampleSubtitles(channel.channelNumber),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = Color(0xFFFFF9A6),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Lower-third news/show info strap
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f),
                                Color.Black.copy(alpha = 0.95f)
                            )
                        )
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Show title and on-air presenter
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = channel.name.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.sp
                                ),
                                color = channelColor
                            )
                            Text(
                                text = " • ${channel.currentShowTime}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = Color.LightGray
                            )
                        }
                        Text(
                            text = channel.currentShowTitle,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            ),
                            color = Color.White,
                            maxLines = 1
                        )
                    }

                    // Interactive Player Bar Actions
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        // Play/Pause
                        IconButton(
                            onClick = onTogglePlayPause,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause broadcast" else "Play broadcast",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Mute/Unmute
                        IconButton(
                            onClick = onToggleMute,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                                contentDescription = if (isMuted) "Unmute" else "Mute",
                                tint = if (isMuted) Color(0xFFFF5252) else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Closed Captions (CC)
                        IconButton(
                            onClick = onToggleCaptions,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isCaptionsEnabled) Icons.Default.ClosedCaption else Icons.Default.ClosedCaptionDisabled,
                                contentDescription = "Closed Captions",
                                tint = if (isCaptionsEnabled) NeonCyan else Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }


                        // Chat Toggle
                        IconButton(
                            onClick = { showChatOverlay = !showChatOverlay },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (showChatOverlay) Icons.Filled.ChatBubble else Icons.Outlined.ChatBubbleOutline,
                                contentDescription = "Toggle Chat",
                                tint = if (showChatOverlay) NeonCyan else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Record Button
                        if (onToggleRecording != null) {
                            IconButton(onClick = onToggleRecording, modifier = Modifier.size(36.dp)) {
                                Icon(
                                    imageVector = Icons.Filled.RadioButtonChecked,
                                    contentDescription = "Record",
                                    tint = if (channel.isRecording) Color.Red else Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Fullscreen

                        IconButton(
                            onClick = onToggleFullscreen,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                contentDescription = "Toggle Fullscreen",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Quality Selector Dialog
    if (showQualityDialog) {
        val qualities = listOf("4K UHD 60FPS", "1080p 60FPS", "720p HD", "Auto (Data Saver)")
        AlertDialog(
            onDismissRequest = { showQualityDialog = false },
            title = {
                Text(
                    text = "Broadcast Stream Quality",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column {
                    qualities.forEach { quality ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectQuality(quality)
                                    showQualityDialog = false
                                }
                                .padding(vertical = 6.dp)
                        ) {
                            RadioButton(
                                selected = (streamQuality == quality),
                                onClick = {
                                    onSelectQuality(quality)
                                    showQualityDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = NeonCyan)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = quality,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showQualityDialog = false }) {
                    Text("Close", color = NeonCyan)
                }
            }
        )
    }
}

// -------------------------------------------------------------
// Canvas Shader Renderers for the 6 Programmes
// -------------------------------------------------------------

private fun DrawScope.drawNewsroomShader(accent: Color, offset: Float) {
    val bg = Brush.verticalGradient(
        listOf(Color(0xFF041122), Color(0xFF071F3D), Color(0xFF020914))
    )
    drawRect(brush = bg)

    // Studio globe and broadcast grid lines
    val cx = size.width * 0.75f
    val cy = size.height * 0.45f
    val r = size.height * 0.38f

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(accent.copy(alpha = 0.35f), Color.Transparent),
            center = Offset(cx, cy),
            radius = r * 1.5f
        ),
        radius = r * 1.5f,
        center = Offset(cx, cy)
    )

    // Studio Iso grid lines
    for (i in 0..6) {
        val y = size.height * 0.15f + i * (size.height * 0.12f)
        drawLine(
            color = accent.copy(alpha = 0.18f),
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 1.5f
        )
    }

    // Dynamic audio spectrum readout
    val bars = 24
    val barW = (size.width * 0.4f) / bars
    for (i in 0 until bars) {
        val heightFactor = (sin(Math.toRadians((offset * 2 + i * 20).toDouble())).toFloat() + 1.2f) * 0.35f
        val bh = size.height * 0.25f * heightFactor
        drawRect(
            color = accent.copy(alpha = 0.6f + (i % 2) * 0.3f),
            topLeft = Offset(size.width * 0.08f + i * barW, size.height * 0.65f - bh),
            size = androidx.compose.ui.geometry.Size(barW - 2f, bh)
        )
    }
}

private fun DrawScope.drawSymphonyShader(accent: Color, offset: Float) {
    val bg = Brush.radialGradient(
        colors = listOf(Color(0xFF2C0B30), Color(0xFF140317), Color(0xFF08010A)),
        center = Offset(size.width * 0.5f, size.height * 0.5f)
    )
    drawRect(brush = bg)

    // Musical sine wave harmonics
    val path = Path()
    val midY = size.height * 0.5f
    path.moveTo(0f, midY)

    val step = 10f
    var x = 0f
    while (x <= size.width) {
        val rad = Math.toRadians((offset * 1.5 + x * 0.8).toDouble())
        val y = midY + (sin(rad) * (size.height * 0.25f)).toFloat()
        path.lineTo(x, y)
        x += step
    }

    drawPath(
        path = path,
        color = accent.copy(alpha = 0.7f),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.5f)
    )

    // Secondary harmonic wave
    val path2 = Path()
    path2.moveTo(0f, midY)
    x = 0f
    while (x <= size.width) {
        val rad = Math.toRadians((offset * 2.2 + x * 1.4).toDouble())
        val y = midY + (sin(rad) * (size.height * 0.15f)).toFloat()
        path2.lineTo(x, y)
        x += step
    }
    drawPath(
        path = path2,
        color = Color(0xFFFFD166).copy(alpha = 0.5f),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
    )
}

private fun DrawScope.drawScienceSpaceShader(accent: Color, offset: Float) {
    val bg = Brush.verticalGradient(
        listOf(Color(0xFF031A12), Color(0xFF010D09), Color(0xFF000503))
    )
    drawRect(brush = bg)

    // Orbit rings
    val cx = size.width * 0.5f
    val cy = size.height * 0.5f

    for (i in 1..4) {
        val radius = i * (size.height * 0.18f)
        drawCircle(
            color = accent.copy(alpha = 0.2f),
            radius = radius,
            center = Offset(cx, cy),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.2f)
        )
    }

    // Exoplanet sphere
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(accent, Color(0xFF004D40), Color.Black),
            center = Offset(cx - 15f, cy - 15f),
            radius = size.height * 0.18f
        ),
        radius = size.height * 0.18f,
        center = Offset(cx, cy)
    )

    // Spectrograph readout lines
    for (i in 0..12) {
        val y = size.height * 0.2f + i * (size.height * 0.05f)
        val w = (sin(Math.toRadians((offset * 3 + i * 35).toDouble())).toFloat() + 1.2f) * (size.width * 0.15f)
        drawLine(
            color = accent.copy(alpha = 0.5f),
            start = Offset(size.width * 0.05f, y),
            end = Offset(size.width * 0.05f + w, y),
            strokeWidth = 2f
        )
    }
}

private fun DrawScope.drawSportsFieldShader(accent: Color, offset: Float) {
    val bg = Brush.verticalGradient(
        listOf(Color(0xFF1E3A1E), Color(0xFF0D210D), Color(0xFF050F05))
    )
    drawRect(brush = bg)

    // Stadium Floodlights glare
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFFF3B0).copy(alpha = 0.6f), Color.Transparent),
            center = Offset(size.width * 0.2f, size.height * 0.1f),
            radius = size.height * 0.5f
        ),
        radius = size.height * 0.5f,
        center = Offset(size.width * 0.2f, size.height * 0.1f)
    )

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFFF3B0).copy(alpha = 0.6f), Color.Transparent),
            center = Offset(size.width * 0.8f, size.height * 0.1f),
            radius = size.height * 0.5f
        ),
        radius = size.height * 0.5f,
        center = Offset(size.width * 0.8f, size.height * 0.1f)
    )

    // Pitch penalty box lines
    val pitchColor = Color.White.copy(alpha = 0.35f)
    drawLine(
        color = pitchColor,
        start = Offset(0f, size.height * 0.6f),
        end = Offset(size.width, size.height * 0.6f),
        strokeWidth = 3f
    )
    drawCircle(
        color = pitchColor,
        radius = size.height * 0.22f,
        center = Offset(size.width * 0.5f, size.height * 0.6f),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f)
    )
}

private fun DrawScope.drawCinemaDramaShader(accent: Color, offset: Float) {
    val bg = Brush.verticalGradient(
        listOf(Color(0xFF2B0909), Color(0xFF150404), Color(0xFF080101))
    )
    drawRect(brush = bg)

    // 2.39:1 Cinema letterbox bars top & bottom
    val letterboxHeight = size.height * 0.08f
    drawRect(
        color = Color.Black,
        topLeft = Offset(0f, 0f),
        size = androidx.compose.ui.geometry.Size(size.width, letterboxHeight)
    )
    drawRect(
        color = Color.Black,
        topLeft = Offset(0f, size.height - letterboxHeight),
        size = androidx.compose.ui.geometry.Size(size.width, letterboxHeight)
    )

    // Film projector beam
    val path = Path()
    path.moveTo(size.width * 0.5f, 0f)
    path.lineTo(size.width * 0.1f, size.height)
    path.lineTo(size.width * 0.9f, size.height)
    path.close()

    drawPath(
        path = path,
        brush = Brush.verticalGradient(
            listOf(accent.copy(alpha = 0.35f), Color.Transparent)
        )
    )
}

private fun DrawScope.drawSynthWaveShader(accent: Color, offset: Float) {
    val bg = Brush.verticalGradient(
        listOf(Color(0xFF261D00), Color(0xFF151000), Color(0xFF080600))
    )
    drawRect(brush = bg)

    // Retro perspective grid
    val horizonY = size.height * 0.45f
    val cols = 10
    val wStep = size.width / cols

    for (i in 0..cols) {
        val startX = i * wStep
        val targetX = size.width * 0.5f + (startX - size.width * 0.5f) * 2.8f
        drawLine(
            color = accent.copy(alpha = 0.4f),
            start = Offset(startX, horizonY),
            end = Offset(targetX, size.height),
            strokeWidth = 1.5f
        )
    }

    // Horizontal moving grid lines
    val lines = 8
    for (i in 1..lines) {
        val progress = ((offset * 0.8f + i * 20f) % 100f) / 100f
        val y = horizonY + (progress * progress) * (size.height - horizonY)
        drawLine(
            color = accent.copy(alpha = 0.5f * progress),
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 1.5f
        )
    }

    // Neon Sun
    drawCircle(
        brush = Brush.verticalGradient(
            listOf(Color(0xFFFFD600), Color(0xFFFF5252))
        ),
        radius = size.height * 0.22f,
        center = Offset(size.width * 0.5f, horizonY - 10f)
    )
}

private fun DrawScope.drawOffAirTestCard(w: Float, h: Float, stationName: String) {
    // SMPTE Color Bars
    val colors = listOf(
        Color(0xFFEBEBEB), Color(0xFFEBEB00), Color(0xFF00EBEB), Color(0xFF00EB00),
        Color(0xFFEB00EB), Color(0xFFEB0000), Color(0xFF0000EB), Color(0xFF141414)
    )
    val barW = w / colors.size
    colors.forEachIndexed { index, color ->
        drawRect(
            color = color,
            topLeft = Offset(index * barW, 0f),
            size = androidx.compose.ui.geometry.Size(barW, h * 0.75f)
        )
    }

    // Bottom base bars
    drawRect(
        color = Color(0xFF002244),
        topLeft = Offset(0f, h * 0.75f),
        size = androidx.compose.ui.geometry.Size(w * 0.3f, h * 0.25f)
    )
    drawRect(
        color = Color.White,
        topLeft = Offset(w * 0.3f, h * 0.75f),
        size = androidx.compose.ui.geometry.Size(w * 0.4f, h * 0.25f)
    )
    drawRect(
        color = Color(0xFF220033),
        topLeft = Offset(w * 0.7f, h * 0.75f),
        size = androidx.compose.ui.geometry.Size(w * 0.3f, h * 0.25f)
    )
}

private fun getSampleSubtitles(channelNumber: Int): String {
    return when (channelNumber) {
        1 -> "[Evelyn Vance] ...the Central Magnetic Transit system opens all 4 urban districts tomorrow at 06:00."
        2 -> "[Maestro Rossi] ...and now Johannes Brahms' Opus 98 Allegro non troppo with the full orchestral winds..."
        3 -> "[Dr. Thorne] ...spectrographic data reveals atmospheric water signatures in the outer planetary ring."
        4 -> "[Leo Sterling] ...tremendous save by the Riverside goalkeeper at the 74th minute mark! 2-1 on the board!"
        5 -> "[Laurent] ...this pendulum wasn't made to measure seconds. It's decoding the telegraph wires..."
        6 -> "[DJ Kora] ...dropping into the 124 BPM generative synthesizer live set across Townsquare..."
        else -> "[TCTV Broadcast] ...broadcasting in ultra high definition on Townsquare Central Television..."
    }
}
