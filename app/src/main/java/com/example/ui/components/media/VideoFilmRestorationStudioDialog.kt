package com.example.ui.components.media

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PhysicalMediaFormat
import com.example.data.model.PhysicalMediaItem
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoFilmRestorationStudioDialog(
    item: PhysicalMediaItem,
    onDismiss: () -> Unit,
    onRestorationApplied: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isVhs = item.format == PhysicalMediaFormat.VHS_TAPE || item.format == PhysicalMediaFormat.DISC_MOVIE
    val isFilmReel = item.format == PhysicalMediaFormat.MOVIE_REEL

    var trackingStabilizer by remember { mutableFloatStateOf(85f) }
    var colorBleedCorrector by remember { mutableFloatStateOf(75f) }
    var dustScratchRemoval by remember { mutableFloatStateOf(90f) }
    var frameFlickerStabilizer by remember { mutableFloatStateOf(80f) }

    var enable4kUpscaling by remember { mutableStateOf(true) }
    var enable60fpsInterpolation by remember { mutableStateOf(true) }

    var isProcessing by remember { mutableStateOf(false) }
    var isRestoredPreview by remember { mutableStateOf(true) }
    var restorationProgress by remember { mutableFloatStateOf(0f) }

    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.testTag("video_film_restoration_studio_dialog")
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.8f)),
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .heightIn(max = 620.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = NeonCyan.copy(alpha = 0.2f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(if (isVhs) "📼" else "🎞️", fontSize = 18.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isVhs) "VHS Video Restoration" else "35mm Theatre Film Restorer",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = if (isVhs) "Tracking Stabilizer & Scanline De-Interlacer" else "Optical Scratch Removal & Technicolor Remaster",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonCyan
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Item Header Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF162032)),
                    border = BorderStroke(1.dp, Color(0xFF1E293B))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item.format.iconEmoji, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "${item.format.label} • ${item.creatorOrArtist}",
                                style = MaterialTheme.typography.bodySmall,
                                color = WarmAmber
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Video Frame Before/After View
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF070C14)),
                    border = BorderStroke(1.dp, if (isRestoredPreview) NeonCyan else Color.Red.copy(alpha = 0.6f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isRestoredPreview) "4K RESTORED FRAME ⚡ (60 FPS)" else "DEGRADED RAW FRAME ⚠️",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isRestoredPreview) NeonCyan else Color.Red
                            )

                            // Bypass Toggle
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("A/B Bypass", fontSize = 10.sp, color = Color.LightGray)
                                Spacer(modifier = Modifier.width(4.dp))
                                Switch(
                                    checked = isRestoredPreview,
                                    onCheckedChange = { isRestoredPreview = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Video Frame Box
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF0F172A),
                            border = BorderStroke(1.dp, Color(0xFF334155)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = if (isRestoredPreview) "🎬 4K Remastered Technicolor Stream" else "📼 VHS Tracking Jitter & Noise Artifacts",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isRestoredPreview) Color.White else WarmAmber
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (isRestoredPreview) "Color Bleed: Cleaned • Gate Flicker: 0% • Upscaled to 2160p" else "Color Bleed: +24% • Noise Floor: -12dB • Vertical Sync Loss",
                                        fontSize = 10.sp,
                                        color = Color.LightGray
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Specific Sliders
                if (isVhs) {
                    Text("VHS Tracking Jitter & Head Sync Stabilizer (${trackingStabilizer.toInt()}%)", fontSize = 11.sp, color = Color.White, modifier = Modifier.align(Alignment.Start))
                    Slider(
                        value = trackingStabilizer,
                        onValueChange = { trackingStabilizer = it },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text("Color Bleed & Tape Dropout Corrector (${colorBleedCorrector.toInt()}%)", fontSize = 11.sp, color = Color.White, modifier = Modifier.align(Alignment.Start))
                    Slider(
                        value = colorBleedCorrector,
                        onValueChange = { colorBleedCorrector = it },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(thumbColor = WarmAmber, activeTrackColor = WarmAmber)
                    )
                } else {
                    Text("Film Scratch & Optical Dust Spec Remover (${dustScratchRemoval.toInt()}%)", fontSize = 11.sp, color = Color.White, modifier = Modifier.align(Alignment.Start))
                    Slider(
                        value = dustScratchRemoval,
                        onValueChange = { dustScratchRemoval = it },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text("Projector Gate Flicker & Speed Stabilizer (${frameFlickerStabilizer.toInt()}%)", fontSize = 11.sp, color = Color.White, modifier = Modifier.align(Alignment.Start))
                    Slider(
                        value = frameFlickerStabilizer,
                        onValueChange = { frameFlickerStabilizer = it },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(thumbColor = WarmAmber, activeTrackColor = WarmAmber)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Switches
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("AI 4K Ultra HD Super-Resolution Upscaling", fontSize = 11.sp, color = Color.LightGray)
                    Switch(
                        checked = enable4kUpscaling,
                        onCheckedChange = { enable4kUpscaling = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Motion Interpolation to 60 FPS", fontSize = 11.sp, color = Color.LightGray)
                    Switch(
                        checked = enable60fpsInterpolation,
                        onCheckedChange = { enable60fpsInterpolation = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isProcessing) {
                    LinearProgressIndicator(
                        progress = { restorationProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = NeonCyan,
                        trackColor = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Restoring video frames & cleaning film artifacts... ${(restorationProgress * 100).toInt()}%", fontSize = 11.sp, color = NeonCyan)
                } else {
                    Button(
                        onClick = {
                            isProcessing = true
                            coroutineScope.launch {
                                for (i in 1..10) {
                                    delay(120L)
                                    restorationProgress = i / 10f
                                }
                                isProcessing = false
                                onRestorationApplied()
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Apply Master 4K Video Restoration", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
