package com.example.ui.components.media

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
fun AudioRestorationStudioDialog(
    item: PhysicalMediaItem,
    onDismiss: () -> Unit,
    onRestorationApplied: () -> Unit,
    modifier: Modifier = Modifier
) {
    var deClickLevel by remember { mutableFloatStateOf(80f) }
    var deHissLevel by remember { mutableFloatStateOf(75f) }
    var wowFlutterCorrection by remember { mutableFloatStateOf(65f) }
    var spectralReconstruction by remember { mutableStateOf(true) }
    var hiddenVocalUnmasking by remember { mutableStateOf(true) }

    var isProcessing by remember { mutableStateOf(false) }
    var isRestoredPreview by remember { mutableStateOf(true) }
    var restorationProgress by remember { mutableFloatStateOf(0f) }

    val isVinyl = item.format == PhysicalMediaFormat.VINYL
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.testTag("audio_restoration_studio_dialog")
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
                                Text("✨", fontSize = 18.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Audio Restoration Studio",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = if (isVinyl) "Vinyl De-Scratch & Spectral Unmasking" else "Tape De-Hiss & Flutter Stabilizer",
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

                // Media Header Card
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
                                text = "Damaged Source • ${item.creatorOrArtist}",
                                style = MaterialTheme.typography.bodySmall,
                                color = WarmAmber
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Real-time Spectrogram & A/B Bypass View
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
                                text = if (isRestoredPreview) "RESTORED HD SPECTROGRAM ⚡" else "RAW DAMAGED AUDIO ⚠️",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isRestoredPreview) NeonCyan else Color.Red
                            )

                            // A/B Bypass Toggle
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

                        // Simulated Spectrogram Canvas
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0F172A))
                        ) {
                            val barWidth = size.width / 30f
                            for (i in 0 until 30) {
                                val heightMultiplier = if (isRestoredPreview) {
                                    (0.4f + kotlin.math.sin(i.toFloat() * 0.5f) * 0.4f).coerceIn(0.2f, 0.95f)
                                } else {
                                    (0.15f + (i % 5) * 0.15f).coerceIn(0.1f, 0.7f)
                                }
                                val color = if (isRestoredPreview) NeonCyan.copy(alpha = 0.8f) else Color.Red.copy(alpha = 0.5f)
                                drawRect(
                                    color = color,
                                    topLeft = androidx.compose.ui.geometry.Offset(i * barWidth + 2f, size.height * (1f - heightMultiplier)),
                                    size = androidx.compose.ui.geometry.Size(barWidth - 4f, size.height * heightMultiplier)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Restoration Sliders & Filters
                if (isVinyl) {
                    Text("Vinyl Scratch & Pop Suppressor (${deClickLevel.toInt()}%)", fontSize = 11.sp, color = Color.White, modifier = Modifier.align(Alignment.Start))
                    Slider(
                        value = deClickLevel,
                        onValueChange = { deClickLevel = it },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                    )
                } else {
                    Text("Tape Hiss & Background Hum Eliminator (${deHissLevel.toInt()}%)", fontSize = 11.sp, color = Color.White, modifier = Modifier.align(Alignment.Start))
                    Slider(
                        value = deHissLevel,
                        onValueChange = { deHissLevel = it },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text("Wow & Flutter Tape Speed Stabilizer (${wowFlutterCorrection.toInt()}%)", fontSize = 11.sp, color = Color.White, modifier = Modifier.align(Alignment.Start))
                    Slider(
                        value = wowFlutterCorrection,
                        onValueChange = { wowFlutterCorrection = it },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(thumbColor = WarmAmber, activeTrackColor = WarmAmber)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Advanced AI Unmasking Toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Harmonic Spectral Reconstruction", fontSize = 11.sp, color = Color.LightGray)
                    Switch(
                        checked = spectralReconstruction,
                        onCheckedChange = { spectralReconstruction = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Unmask Buried Vocals & Instruments", fontSize = 11.sp, color = Color.LightGray)
                    Switch(
                        checked = hiddenVocalUnmasking,
                        onCheckedChange = { hiddenVocalUnmasking = it },
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
                    Text("Restoring audio frequencies & removing noise... ${(restorationProgress * 100).toInt()}%", fontSize = 11.sp, color = NeonCyan)
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
                        Text("Apply Master HD Restoration", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
