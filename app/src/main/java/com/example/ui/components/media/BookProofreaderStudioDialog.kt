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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PhysicalMediaItem
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookProofreaderStudioDialog(
    item: PhysicalMediaItem,
    onDismiss: () -> Unit,
    onProofreadSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    var contrastEnhancer by remember { mutableFloatStateOf(85f) }
    var correctArchaicSpelling by remember { mutableStateOf(true) }
    var enableGlossaryFootnotes by remember { mutableStateOf(true) }

    var isProofreading by remember { mutableStateOf(false) }
    var proofreadProgress by remember { mutableFloatStateOf(0f) }

    val coroutineScope = rememberCoroutineScope()

    val rawScannedText = "In the year 1794, the fower of youth ſhall not faile, though the darkneſse of the night covereth the city..."
    val proofreadText = if (correctArchaicSpelling) {
        "In the year 1794, the power of youth shall not fail, though the darkness of the night covers the city..."
    } else {
        rawScannedText
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.testTag("book_proofreader_studio_dialog")
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
                                Text("🔍", fontSize = 18.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Manuscript Proofreader",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "OCR Restoration & Archaic Spelling Corrector",
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

                // Split Comparison View (Scanned Faded vs Proofread Digital)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Left: Scanned Faded Manuscript
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF241E15)),
                        border = BorderStroke(1.dp, WarmAmber.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "RAW MANUSCRIPT SCAN",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = WarmAmber
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = rawScannedText,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Serif,
                                color = Color(0xFFD4C3A3),
                                lineHeight = 16.sp
                            )
                        }
                    }

                    // Right: Proofread & Restored Text
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF132238)),
                        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "PROOFREAD DIGITAL TEXT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = proofreadText,
                                fontSize = 11.sp,
                                color = Color.White,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Restoration Controls
                Text("Faded Ink & Contrast Enhancer (${contrastEnhancer.toInt()}%)", fontSize = 11.sp, color = Color.White, modifier = Modifier.align(Alignment.Start))
                Slider(
                    value = contrastEnhancer,
                    onValueChange = { contrastEnhancer = it },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Archaic Long-S (ſ) & Typo Auto-Corrector", fontSize = 11.sp, color = Color.LightGray)
                    Switch(
                        checked = correctArchaicSpelling,
                        onCheckedChange = { correctArchaicSpelling = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Historical Glossary & Footnote Explainer", fontSize = 11.sp, color = Color.LightGray)
                    Switch(
                        checked = enableGlossaryFootnotes,
                        onCheckedChange = { enableGlossaryFootnotes = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan)
                    )
                }

                if (enableGlossaryFootnotes) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF162032),
                        border = BorderStroke(1.dp, WarmAmber.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("💡", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Footnote 1: 'fower' -> corrected to 'power' (18th century printer typeface long 's' misread).",
                                fontSize = 10.sp,
                                color = WarmAmber
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isProofreading) {
                    LinearProgressIndicator(
                        progress = { proofreadProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = NeonCyan,
                        trackColor = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Proofreading pages & building annotated replica... ${(proofreadProgress * 100).toInt()}%", fontSize = 11.sp, color = NeonCyan)
                } else {
                    Button(
                        onClick = {
                            isProofreading = true
                            coroutineScope.launch {
                                for (i in 1..10) {
                                    delay(100L)
                                    proofreadProgress = i / 10f
                                }
                                isProofreading = false
                                onProofreadSaved()
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Proofread Replica to Vault", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
