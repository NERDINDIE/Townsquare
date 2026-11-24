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
import com.example.data.model.PhysicalMediaItem
import com.example.data.repository.PhysicalMediaRepository
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransformativeWorkStudioDialog(
    item: PhysicalMediaItem,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Low-effort filters (capped & penalized)
    var pitchShiftSemiTones by remember { mutableFloatStateOf(0f) }
    var useOneClickAiBypass by remember { mutableStateOf(false) }

    // High-effort genuine human creative work categories
    var structuralChopping by remember { mutableStateOf(false) }
    var originalVocalInstrument by remember { mutableStateOf(false) }
    var customBeatComposition by remember { mutableStateOf(false) }
    var customCommentaryAnalysis by remember { mutableStateOf(false) }

    // Calculation with Anti-Slop Safeguards
    val isSlopOnly = remember(pitchShiftSemiTones, useOneClickAiBypass, structuralChopping, originalVocalInstrument, customBeatComposition, customCommentaryAnalysis) {
        (pitchShiftSemiTones != 0f || useOneClickAiBypass) &&
                !structuralChopping && !originalVocalInstrument && !customBeatComposition && !customCommentaryAnalysis
    }

    val calculatedScore = remember(pitchShiftSemiTones, useOneClickAiBypass, structuralChopping, originalVocalInstrument, customBeatComposition, customCommentaryAnalysis) {
        var score = 0f

        // High-effort human creative work additions
        if (structuralChopping) score += 25f
        if (originalVocalInstrument) score += 30f
        if (customBeatComposition) score += 25f
        if (customCommentaryAnalysis) score += 25f

        // Capped low-effort additions
        val lowEffortPitchScore = (kotlin.math.abs(pitchShiftSemiTones) * 2f).coerceAtMost(10f)
        val lowEffortAiScore = if (useOneClickAiBypass) 15f else 0f

        score += lowEffortPitchScore
        score += lowEffortAiScore

        // Slop Cap: If no genuine human creative effort is added, max score is strictly 25%
        if (isSlopOnly) {
            score.coerceAtMost(25f)
        } else {
            score.coerceIn(0f, 100f)
        }
    }

    val isPassed = calculatedScore >= 50f && !isSlopOnly

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.testTag("transformative_work_studio_dialog")
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, if (isPassed) NeonCyan else WarmAmber),
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .heightIn(max = 600.dp)
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
                            color = if (isPassed) NeonCyan.copy(alpha = 0.2f) else WarmAmber.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🎨", fontSize = 18.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Transformative Studio",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Anti-Slop Genuine Effort Verifier",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isPassed) NeonCyan else WarmAmber
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Score meter
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSlopOnly) Color(0xFF2C1616) else Color(0xFF162032)
                    ),
                    border = BorderStroke(1.dp, if (isPassed) NeonCyan else WarmAmber)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Derivative Transformation & Creative Effort Score",
                            fontSize = 11.sp,
                            color = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${calculatedScore.toInt()}% Derived",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isPassed) NeonCyan else WarmAmber
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { calculatedScore / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = if (isPassed) NeonCyan else WarmAmber,
                            trackColor = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        if (isSlopOnly) {
                            Text(
                                text = "⚠️ Low-Effort Slop Warning: Simple pitch shifting or 1-click AI alone cannot pass the 50% threshold. You must add genuine human creative work below.",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = WarmAmber
                            )
                        } else if (isPassed) {
                            Text(
                                text = "✅ Threshold Passed (>50%). Genuine human creative work verified! Resharing unlocked.",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            )
                        } else {
                            Text(
                                text = "⚠️ Need >50% genuine human derivative effort to unlock public resharing.",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = WarmAmber
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 1: Genuine Human Creative Effort (Required)
                Text(
                    text = "GENUINE HUMAN CREATIVE EFFORT (REQUIRED FOR >50%)",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                    color = NeonCyan,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(6.dp))

                CreativeOptionToggle(
                    title = "Structural Chop & Splice Re-arrangement (+25%)",
                    description = "Manually chop verse/chorus structure and re-sequence sections",
                    checked = structuralChopping,
                    onCheckedChange = { structuralChopping = it }
                )

                Spacer(modifier = Modifier.height(6.dp))

                CreativeOptionToggle(
                    title = "Original Recorded Vocal / Instrument Layer (+30%)",
                    description = "Record new live guitar, synths, drums, or vocal performance over media",
                    checked = originalVocalInstrument,
                    onCheckedChange = { originalVocalInstrument = it }
                )

                Spacer(modifier = Modifier.height(6.dp))

                CreativeOptionToggle(
                    title = "Custom Counter-Melody / Beat Composition (+25%)",
                    description = "Write original basslines, beat patterns, or melody synth lines",
                    checked = customBeatComposition,
                    onCheckedChange = { customBeatComposition = it }
                )

                Spacer(modifier = Modifier.height(6.dp))

                CreativeOptionToggle(
                    title = "Critical Commentary & Spoken Review Track (+25%)",
                    description = "Record spoken educational commentary or editorial analysis layer",
                    checked = customCommentaryAnalysis,
                    onCheckedChange = { customCommentaryAnalysis = it }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Section 2: Low-Effort Filters & AI (Capped)
                Text(
                    text = "LOW-EFFORT FILTERS & AI (CAPPED - MAX 25% ALONE)",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                    color = WarmAmber,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text("Simple Pitch / Speed Shift (${pitchShiftSemiTones.toInt()} semitones) • Capped at +10%", fontSize = 11.sp, color = Color.LightGray, modifier = Modifier.align(Alignment.Start))
                Slider(
                    value = pitchShiftSemiTones,
                    onValueChange = { pitchShiftSemiTones = it },
                    valueRange = -6f..6f,
                    colors = SliderDefaults.colors(thumbColor = WarmAmber, activeTrackColor = WarmAmber)
                )

                CreativeOptionToggle(
                    title = "1-Click AI Auto-Mix (Capped at +15%)",
                    description = "Low-effort automated AI filter (Cannot cross threshold alone)",
                    checked = useOneClickAiBypass,
                    onCheckedChange = { useOneClickAiBypass = it },
                    accentColor = WarmAmber
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        PhysicalMediaRepository.applyDerivativeWork(item.id, calculatedScore)
                        onDismiss()
                    },
                    enabled = isPassed,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan,
                        disabledContainerColor = Color(0xFF1E293B)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = if (isPassed) Icons.Default.CheckCircle else Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (isPassed) Color.Black else Color.Gray
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isPassed) "Unlock Reshare (${calculatedScore.toInt()}% Genuine Work)" else "High-Effort Work Required to Unlock (${calculatedScore.toInt()}%)",
                        color = if (isPassed) Color.Black else Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun CreativeOptionToggle(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    accentColor: Color = NeonCyan
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF162032),
        border = BorderStroke(1.dp, if (checked) accentColor else Color(0xFF334155)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(checkedThumbColor = accentColor)
            )
        }
    }
}
