package com.example.ui.components.media

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber
import kotlinx.coroutines.delay

enum class CassetteTapeType(val label: String, val eqUs: String) {
    TYPE_I("Type I Normal (Fe₂O₃)", "120 µs"),
    TYPE_II("Type II Chrome (CrO₂)", "70 µs"),
    TYPE_IV("Type IV Metal", "70 µs")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CassetteBoomboxCaptureStudio(
    onDismiss: () -> Unit,
    onSavedToVault: (PhysicalMediaItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var cassetteTitle by remember { mutableStateOf("") }
    var artistName by remember { mutableStateOf("") }
    var selectedTapeType by remember { mutableStateOf(CassetteTapeType.TYPE_II) }
    var dolbyNrEnabled by remember { mutableStateOf(true) }
    var tapeHeadAzimuth by remember { mutableFloatStateOf(0.95f) }

    var isRecording by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var recordingTimeSeconds by remember { mutableIntStateOf(0) }
    var trackCount by remember { mutableIntStateOf(1) }

    var pendingItemForVerification by remember { mutableStateOf<PhysicalMediaItem?>(null) }

    // Tape Reel rotation
    val infiniteTransition = rememberInfiniteTransition(label = "CassetteReelRotation")
    val reelRotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ReelAngle"
    )

    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (true) {
                delay(1000L)
                recordingTimeSeconds++
                if (recordingTimeSeconds % 210 == 0) {
                    trackCount++
                }
            }
        }
    }

    if (pendingItemForVerification != null) {
        UploadVerificationModal(
            pendingItem = pendingItemForVerification!!,
            onDismiss = { pendingItemForVerification = null },
            onUploadFinalized = { item ->
                onSavedToVault(item)
                pendingItemForVerification = null
                onDismiss()
            }
        )
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            modifier = modifier.testTag("cassette_boombox_studio_dialog")
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF0F172A),
                border = BorderStroke(1.dp, WarmAmber.copy(alpha = 0.8f)),
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
                                color = WarmAmber.copy(alpha = 0.2f),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("📻", fontSize = 18.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Cassette Boombox Capture Studio",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "Direct Tape Deck Magnetic Reel Recording",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = WarmAmber
                                )
                            }
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Cassette Tape Housing & Dual Reels Animation
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF162032)),
                        border = BorderStroke(2.dp, WarmAmber.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "CASSETTE TAPE DECK • SIDE A",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = WarmAmber
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Tape window with dual spinning spools
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF070C14),
                                border = BorderStroke(1.dp, Color(0xFF334155)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 24.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Left Reel
                                    ReelSpool(angle = if (isRecording || isPlaying) reelRotationAngle else 0f)

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = if (isRecording) "REC 🔴" else if (isPlaying) "PLAY ▶️" else "STOP ⏹",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isRecording) Color.Red else if (isPlaying) NeonCyan else Color.Gray
                                        )
                                        val counterText = "%02d:%02d".format(recordingTimeSeconds / 60, recordingTimeSeconds % 60)
                                        Text(
                                            text = counterText,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Side A • $trackCount Tracks",
                                            fontSize = 9.sp,
                                            color = WarmAmber
                                        )
                                    }

                                    // Right Reel
                                    ReelSpool(angle = if (isRecording || isPlaying) reelRotationAngle else 0f)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Title & Artist Inputs
                    OutlinedTextField(
                        value = cassetteTitle,
                        onValueChange = { cassetteTitle = it },
                        label = { Text("Cassette Tape Title") },
                        placeholder = { Text("e.g. Mixtape Vol. 1 (Chrome Edition)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WarmAmber,
                            unfocusedBorderColor = Color(0xFF334155)
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = artistName,
                        onValueChange = { artistName = it },
                        label = { Text("Artist / Curator Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WarmAmber,
                            unfocusedBorderColor = Color(0xFF334155)
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tape Bias Selector
                    Text("Tape Type & Equalization", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.align(Alignment.Start))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        CassetteTapeType.entries.forEach { tape ->
                            val isSel = selectedTapeType == tape
                            FilterChip(
                                selected = isSel,
                                onClick = { selectedTapeType = tape },
                                label = { Text(tape.label.take(12), fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = WarmAmber, selectedLabelColor = Color.Black)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    var isDspDenoiseActive by remember { mutableStateOf(true) }
                    var tapeNoiseFloorDb by remember { mutableFloatStateOf(28f) }

                    // Dolby Noise Reduction & Head Azimuth Align
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Dolby B/C NR + DSP Surface Denoise", fontSize = 11.sp, color = Color.LightGray)
                        Switch(
                            checked = dolbyNrEnabled,
                            onCheckedChange = { dolbyNrEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = WarmAmber)
                        )
                    }

                    if (dolbyNrEnabled) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Tape Hiss Suppression: -${tapeNoiseFloorDb.toInt()} dB", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                            Text("Real-time DSP ACTIVE", fontSize = 9.sp, color = WarmAmber)
                        }
                        Slider(
                            value = tapeNoiseFloorDb,
                            onValueChange = { tapeNoiseFloorDb = it },
                            valueRange = 12f..36f,
                            colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Mechanical Tape Transport Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = {
                                isRecording = !isRecording
                                isPlaying = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRecording) Color(0xFFEF4444) else WarmAmber
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.RadioButtonChecked,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isRecording) "Stop" else "REC Tape",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        Button(
                            onClick = {
                                isPlaying = !isPlaying
                                isRecording = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isPlaying) "Pause" else "Play Tape",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Finalize & Open Verification Modal
                    Button(
                        onClick = {
                            val tapeItem = PhysicalMediaItem(
                                title = cassetteTitle.ifBlank { "Untitled Cassette Tape" },
                                creatorOrArtist = artistName.ifBlank { "Cassette Curator" },
                                format = PhysicalMediaFormat.CASSETTE_TAPE,
                                description = "Direct Tape Deck Boombox recording (${selectedTapeType.label}). Dolby NR: $dolbyNrEnabled.",
                                tracksOrPages = (1..trackCount).map { "Side A Track $it" },
                                totalDurationOrPages = "%02d:%02d".format(recordingTimeSeconds / 60, recordingTimeSeconds % 60)
                            )
                            pendingItemForVerification = tapeItem
                        },
                        enabled = recordingTimeSeconds > 0 || cassetteTitle.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Attach Verification & Finalize Upload", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReelSpool(angle: Float) {
    Box(
        modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(Color(0xFF1E293B))
            .border(2.dp, WarmAmber, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .rotate(angle)
        ) {
            val centerPx = size.width / 2
            drawCircle(color = WarmAmber, radius = centerPx * 0.35f)
            // Reel spokes
            drawLine(color = Color.White, start = androidx.compose.ui.geometry.Offset(centerPx * 0.4f, centerPx), end = androidx.compose.ui.geometry.Offset(centerPx * 1.6f, centerPx), strokeWidth = 3f)
            drawLine(color = Color.White, start = androidx.compose.ui.geometry.Offset(centerPx, centerPx * 0.4f), end = androidx.compose.ui.geometry.Offset(centerPx, centerPx * 1.6f), strokeWidth = 3f)
        }
    }
}
