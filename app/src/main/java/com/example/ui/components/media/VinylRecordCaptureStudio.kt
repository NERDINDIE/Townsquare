package com.example.ui.components.media

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DigitalFingerprint
import com.example.data.model.PhysicalMediaFormat
import com.example.data.model.PhysicalMediaItem
import com.example.data.model.ProofOfPurchase
import com.example.data.repository.PhysicalMediaRepository
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VinylRecordCaptureStudio(
    onDismiss: () -> Unit,
    onSavedToVault: (PhysicalMediaItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var vinylTitle by remember { mutableStateOf("") }
    var artistName by remember { mutableStateOf("") }
    var selectedRpm by remember { mutableStateOf("33 1/3 RPM") }
    var isRecording by remember { mutableStateOf(false) }
    var recordingTimeSeconds by remember { mutableIntStateOf(0) }
    var isAudioNormalized by remember { mutableStateOf(true) }
    var autoTrackSplit by remember { mutableStateOf(true) }
    var trackCount by remember { mutableIntStateOf(1) }

    val infiniteTransition = rememberInfiniteTransition(label = "VinylRotation")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (selectedRpm.contains("33")) 3000 else 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Rotation"
    )

    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (true) {
                delay(1000L)
                recordingTimeSeconds++
                if (recordingTimeSeconds % 180 == 0 && autoTrackSplit) {
                    trackCount++
                }
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.testTag("vinyl_capture_studio_dialog")
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, WarmAmber.copy(alpha = 0.6f)),
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
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
                            color = WarmAmber.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("💿", fontSize = 18.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Vinyl Capture Studio",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Direct Record Player Hardware Recording",
                                style = MaterialTheme.typography.labelSmall,
                                color = WarmAmber
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Spinning Record & Arm Animation
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF020617))
                        .border(2.dp, WarmAmber.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    // Record grooves
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(if (isRecording) rotationAngle else 0f)
                    ) {
                        val centerPx = size.width / 2
                        drawCircle(color = Color(0xFF1E293B), radius = centerPx * 0.9f, style = Stroke(width = 2f))
                        drawCircle(color = Color(0xFF334155), radius = centerPx * 0.7f, style = Stroke(width = 2f))
                        drawCircle(color = Color(0xFF1E293B), radius = centerPx * 0.5f, style = Stroke(width = 2f))
                        drawCircle(color = WarmAmber, radius = centerPx * 0.25f)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isRecording) "REC 🔴" else "READY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isRecording) Color.Red else NeonCyan
                        )
                        val formatSecs = "%02d:%02d".format(recordingTimeSeconds / 60, recordingTimeSeconds % 60)
                        Text(text = formatSecs, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Inputs
                OutlinedTextField(
                    value = vinylTitle,
                    onValueChange = { vinylTitle = it },
                    label = { Text("Vinyl Album Title") },
                    placeholder = { Text("e.g. Abbey Road Collector Vinyl") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WarmAmber,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedLabelColor = WarmAmber
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = artistName,
                    onValueChange = { artistName = it },
                    label = { Text("Artist / Band") },
                    placeholder = { Text("e.g. The Beatles") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WarmAmber,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedLabelColor = WarmAmber
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

    var isDenoiseActive by remember { mutableStateOf(true) }
    var noiseReductionDb by remember { mutableFloatStateOf(24f) }

    // Speed & Audio Options
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            selected = selectedRpm == "33 1/3 RPM",
            onClick = { selectedRpm = "33 1/3 RPM" },
            label = { Text("33 ⅓ RPM") }
        )
        FilterChip(
            selected = selectedRpm == "45 RPM",
            onClick = { selectedRpm = "45 RPM" },
            label = { Text("45 RPM") }
        )
        FilterChip(
            selected = isDenoiseActive,
            onClick = { isDenoiseActive = !isDenoiseActive },
            label = { Text("DSP Denoise") }
        )
    }

    if (isDenoiseActive) {
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Surface Noise Floor: -${noiseReductionDb.toInt()} dB",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan
            )
            Text(
                text = "Adaptive Spectral Subtraction ACTIVE",
                fontSize = 9.sp,
                color = WarmAmber
            )
        }
        Slider(
            value = noiseReductionDb,
            onValueChange = { noiseReductionDb = it },
            valueRange = 12f..36f,
            colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
        )
    }

                Spacer(modifier = Modifier.height(16.dp))

                // Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { isRecording = !isRecording },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRecording) Color(0xFFEF4444) else WarmAmber
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.RadioButtonChecked,
                            contentDescription = null,
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isRecording) "Stop Capture" else "Start Live Recording",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val newItem = PhysicalMediaItem(
                                title = vinylTitle.ifBlank { "Untitled Vinyl Record" },
                                creatorOrArtist = artistName.ifBlank { "Unknown Artist" },
                                format = PhysicalMediaFormat.VINYL,
                                description = "Direct Vinyl Record Player capture ($selectedRpm). Auto split: $trackCount tracks.",
                                tracksOrPages = (1..trackCount).map { "Track $it (Vinyl Rip)" },
                                totalDurationOrPages = "%02d:%02d".format(recordingTimeSeconds / 60, recordingTimeSeconds % 60),
                                isPrivate = true,
                                proofOfPurchase = ProofOfPurchase(
                                    isVerified = true,
                                    storeName = "Hardware Audio Capture"
                                ),
                                fingerprint = DigitalFingerprint(
                                    originalUploaderId = "usr_me",
                                    originalUploaderName = "My Vault",
                                    isDownloadedFromElsewhere = false
                                )
                            )
                            PhysicalMediaRepository.addItem(newItem)
                            onSavedToVault(newItem)
                            onDismiss()
                        },
                        enabled = recordingTimeSeconds > 0 || vinylTitle.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save to Vault", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
