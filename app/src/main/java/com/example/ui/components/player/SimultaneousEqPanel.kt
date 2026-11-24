package com.example.ui.components.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.EqContentCategory
import com.example.audio.OutputGearProfile
import com.example.audio.SimultaneousEqEngine
import com.example.audio.SimultaneousEqState
import com.example.ui.theme.CoralRed
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.RadiantPurple
import com.example.ui.theme.WarmAmber
import kotlin.math.roundToInt

/**
 * Grouped callbacks for controlling the Simultaneous Equalizer cleanly across player surfaces.
 */
data class SimultaneousEqCallbacks(
    val onToggleEq: () -> Unit = {},
    val onToggleBypassCompare: () -> Unit = {},
    val onSetAutoContentAdapt: (Boolean) -> Unit = {},
    val onSetGearCompensationEnabled: (Boolean) -> Unit = {},
    val onSetAutoHeadroom: (Boolean) -> Unit = {},
    val onSetCompensationIntensity: (Float) -> Unit = {},
    val onSelectContentCategory: (EqContentCategory?) -> Unit = {},
    val onSelectOutputGear: (OutputGearProfile) -> Unit = {},
    val onDetectOutputGear: () -> Unit = {},
    val onUpdateGearZone: (bassDb: Float?, midDb: Float?, trebleDb: Float?) -> Unit = { _, _, _ -> },
    val onUpdateManualTrimBand: (bandIdx: Int, trimDb: Float) -> Unit = { _, _ -> },
    val onResetManualTrim: () -> Unit = {},
    val onResetGearCompensation: () -> Unit = {}
)

private val EmeraldEqAccent = Color(0xFF34D399)

@Composable
fun SimultaneousEqCompactBadge(
    eqState: SimultaneousEqState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeColor = when {
        !eqState.isEnabled -> Color(0xFF64748B)
        eqState.isBypassCompare -> CoralRed
        else -> EmeraldEqAccent
    }
    val statusLabel = when {
        !eqState.isEnabled -> "EQ OFF"
        eqState.isBypassCompare -> "BYPASS RAW"
        else -> "EQ: ${eqState.effectiveCategory.label.uppercase()} • ${eqState.selectedGearProfile.shortLabel}"
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = activeColor.copy(alpha = 0.16f),
        border = BorderStroke(1.dp, activeColor.copy(alpha = 0.65f)),
        modifier = modifier
            .defaultMinSize(minHeight = 36.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .testTag("simultaneous_eq_compact_badge")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = Icons.Default.GraphicEq,
                contentDescription = "Open Simultaneous Equalizer",
                tint = activeColor,
                modifier = Modifier.size(15.dp)
            )
            Text(
                text = statusLabel,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = activeColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimultaneousEqModalSheet(
    eqState: SimultaneousEqState,
    callbacks: SimultaneousEqCallbacks,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF0A101D),
        modifier = Modifier.testTag("simultaneous_eq_modal_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = EmeraldEqAccent.copy(alpha = 0.18f),
                        border = BorderStroke(1.dp, EmeraldEqAccent),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Equalizer,
                                contentDescription = null,
                                tint = EmeraldEqAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Simultaneous Adaptive EQ",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "Content-Matched + Inverse Gear Acoustic Compensation",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonCyan
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("close_simultaneous_eq_sheet")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Simultaneous EQ",
                        tint = Color.LightGray
                    )
                }
            }

            SimultaneousEqStudioCard(
                eqState = eqState,
                callbacks = callbacks,
                initiallyExpandTrim = false
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun SimultaneousEqStudioCard(
    eqState: SimultaneousEqState,
    callbacks: SimultaneousEqCallbacks,
    initiallyExpandTrim: Boolean = false,
    modifier: Modifier = Modifier
) {
    var showFineTrim by remember { mutableStateOf(initiallyExpandTrim) }
    var showGearCalibration by remember { mutableStateOf(true) }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF0F172A),
        border = BorderStroke(
            width = 1.dp,
            color = when {
                !eqState.isEnabled -> Color(0xFF334155)
                eqState.isBypassCompare -> CoralRed.copy(alpha = 0.7f)
                else -> EmeraldEqAccent.copy(alpha = 0.55f)
            }
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("simultaneous_eq_studio_card")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Top Controls: Power, A/B Bypass Compare, Headroom LUFS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${eqState.effectiveCategory.emoji} ${eqState.effectiveCategory.label.uppercase()} PROFILE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonCyan
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (eqState.categoryOverride == null) EmeraldEqAccent.copy(alpha = 0.18f) else WarmAmber.copy(alpha = 0.18f)
                        ) {
                            Text(
                                text = if (eqState.categoryOverride == null) "AUTO" else "LOCKED",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (eqState.categoryOverride == null) EmeraldEqAccent else WarmAmber,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = eqState.activeSubProfileLabel,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // A/B Bypass Compare Button (minimum 48dp height wrapper via OutlinedButton)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (eqState.isBypassCompare) CoralRed.copy(alpha = 0.25f) else Color(0xFF1E293B),
                        border = BorderStroke(1.dp, if (eqState.isBypassCompare) CoralRed else Color(0xFF334155)),
                        modifier = Modifier
                            .defaultMinSize(minWidth = 48.dp, minHeight = 40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { callbacks.onToggleBypassCompare() }
                            .testTag("eq_bypass_compare_button")
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = if (eqState.isBypassCompare) "A/B: RAW" else "A/B: EQ ON",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (eqState.isBypassCompare) CoralRed else Color.White
                            )
                        }
                    }

                    Switch(
                        checked = eqState.isEnabled,
                        onCheckedChange = { callbacks.onToggleEq() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF003544),
                            checkedTrackColor = EmeraldEqAccent,
                            uncheckedThumbColor = Color.LightGray,
                            uncheckedTrackColor = Color(0xFF1E293B)
                        ),
                        modifier = Modifier.testTag("eq_master_switch")
                    )
                }
            }

            // Rationale & Telemetry Strip
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF090F1C),
                border = BorderStroke(1.dp, Color(0xFF1E293B))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = eqState.optimizationReason,
                        fontSize = 11.sp,
                        color = Color(0xFFCBD5E1),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Preamp: ${formatSignedDb(eqState.preampGainDb)}",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = WarmAmber
                        )
                        Text(
                            text = "Target: ${eqState.targetLufs} LUFS",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = NeonCyan
                        )
                        Text(
                            text = if (eqState.hardwareEqAttached) "HW AudioFX (${eqState.hardwareBandCount}B)" else "DSP Curve Active",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = EmeraldEqAccent
                        )
                    }
                }
            }

            // Triple-Layer Simultaneous Curve Visualizer Canvas
            SimultaneousEqCurveCanvas(eqState = eqState)

            // Curve Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendDot(color = NeonCyan, label = "Content Ideal")
                LegendDot(color = WarmAmber, label = "Gear Inverse")
                LegendDot(color = EmeraldEqAccent, label = "Simultaneous Net")
            }

            // Content Category Selector Chips (Auto vs Manual Test/Override)
            Text(
                text = "LAYER 1 • CONTENT-ADAPTIVE TARGET",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = NeonCyan
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val isAuto = eqState.categoryOverride == null
                EqFilterPill(
                    label = "✨ Auto",
                    selected = isAuto,
                    accentColor = EmeraldEqAccent,
                    onClick = { callbacks.onSelectContentCategory(null) },
                    testTag = "eq_cat_auto"
                )
                EqContentCategory.entries.forEach { category ->
                    val selected = eqState.categoryOverride == category ||
                        (isAuto && eqState.detectedCategory == category)
                    EqFilterPill(
                        label = "${category.emoji} ${category.label}",
                        selected = selected,
                        accentColor = if (eqState.categoryOverride == category) WarmAmber else NeonCyan,
                        onClick = {
                            if (eqState.categoryOverride == category) {
                                callbacks.onSelectContentCategory(null)
                            } else {
                                callbacks.onSelectContentCategory(category)
                            }
                        },
                        testTag = "eq_cat_${category.name.lowercase()}"
                    )
                }
            }

            // Layer 2: Gear Acoustic Compensation ("Regardless of how your gear is tuned")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "LAYER 2 • OUTPUT GEAR COMPENSATION",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = WarmAmber
                    )
                    Text(
                        text = eqState.detectedDeviceName,
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = { callbacks.onDetectOutputGear() },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, WarmAmber.copy(alpha = 0.7f)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .defaultMinSize(minHeight = 36.dp)
                            .testTag("eq_scan_gear_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = "Scan Output Gear",
                            tint = WarmAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Scan Gear", fontSize = 11.sp, color = WarmAmber, fontWeight = FontWeight.Bold)
                    }

                    IconButton(
                        onClick = { showGearCalibration = !showGearCalibration },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = if (showGearCalibration) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle Gear Calibration",
                            tint = Color.LightGray
                        )
                    }
                }
            }

            // Gear Profile Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutputGearProfile.entries.forEach { profile ->
                    val selected = eqState.selectedGearProfile == profile
                    EqFilterPill(
                        label = "${profile.emoji} ${profile.shortLabel}",
                        selected = selected,
                        accentColor = WarmAmber,
                        onClick = { callbacks.onSelectOutputGear(profile) },
                        testTag = "eq_gear_${profile.name.lowercase()}"
                    )
                }
            }

            AnimatedVisibility(visible = showGearCalibration) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF162033),
                    border = BorderStroke(1.dp, Color(0xFF28354D))
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = eqState.selectedGearProfile.summary,
                                fontSize = 11.sp,
                                color = Color(0xFFE2E8F0),
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(
                                onClick = { callbacks.onResetGearCompensation() },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.defaultMinSize(minHeight = 36.dp)
                            ) {
                                Text("Flatten", fontSize = 11.sp, color = WarmAmber)
                            }
                        }

                        // Compensation Intensity Slider + Anti-Clipping Headroom toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Inverse Strength: ${(eqState.compensationIntensity * 100).roundToInt()}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = WarmAmber
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = eqState.autoHeadroomEnabled,
                                    onCheckedChange = { callbacks.onSetAutoHeadroom(it) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = EmeraldEqAccent,
                                        checkmarkColor = Color(0xFF003544)
                                    )
                                )
                                Text(
                                    text = "Anti-Clip",
                                    fontSize = 11.sp,
                                    color = Color.LightGray
                                )
                            }
                        }
                        Slider(
                            value = eqState.compensationIntensity,
                            onValueChange = { callbacks.onSetCompensationIntensity(it) },
                            valueRange = 0f..1.25f,
                            colors = SliderDefaults.colors(
                                thumbColor = WarmAmber,
                                activeTrackColor = WarmAmber,
                                inactiveTrackColor = Color(0xFF0F172A)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("eq_compensation_intensity_slider")
                        )

                        // Quick Hardware Coloration Calibration ("How does your gear natively sound?")
                        Text(
                            text = "GEAR COLORATION CALIBRATION (WE APPLY THE EXACT INVERSE):",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                        val bassBias = ((eqState.gearSignatureDb.getOrElse(0) { 0f } + eqState.gearSignatureDb.getOrElse(1) { 0f }) / 2f)
                        val midBias = eqState.gearSignatureDb.getOrElse(4) { 0f }
                        val trebleBias = eqState.gearSignatureDb.getOrElse(6) { 0f }

                        GearZoneSliderRow(
                            label = "Gear Bass",
                            valueDb = bassBias,
                            accentColor = WarmAmber,
                            onValueChange = { callbacks.onUpdateGearZone(it, null, null) }
                        )
                        GearZoneSliderRow(
                            label = "Gear Mids",
                            valueDb = midBias,
                            accentColor = RadiantPurple,
                            onValueChange = { callbacks.onUpdateGearZone(null, it, null) }
                        )
                        GearZoneSliderRow(
                            label = "Gear Treble",
                            valueDb = trebleBias,
                            accentColor = NeonCyan,
                            onValueChange = { callbacks.onUpdateGearZone(null, null, it) }
                        )
                    }
                }
            }

            // Expandable 8-Band Fine Listener Trim
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { showFineTrim = !showFineTrim }
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎛️ Fine 8-Band Listener Trim",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Icon(
                    imageVector = if (showFineTrim) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Toggle Fine 8-Band Trim",
                    tint = NeonCyan
                )
            }

            AnimatedVisibility(visible = showFineTrim) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Fine trim blends simultaneously on top of Content + Gear Inverse",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8),
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(
                            onClick = { callbacks.onResetManualTrim() },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.defaultMinSize(minHeight = 36.dp)
                        ) {
                            Text("Reset Trim", fontSize = 11.sp, color = NeonCyan)
                        }
                    }

                    SimultaneousEqEngine.BAND_LABELS.forEachIndexed { idx, bandLabel ->
                        val trimVal = eqState.userFineTrimDb.getOrElse(idx) { 0f }
                        val netVal = eqState.netSimultaneousDb.getOrElse(idx) { 0f }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = bandLabel,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color.LightGray,
                                modifier = Modifier.width(44.dp)
                            )
                            Slider(
                                value = trimVal,
                                onValueChange = { callbacks.onUpdateManualTrimBand(idx, it) },
                                valueRange = -6f..6f,
                                colors = SliderDefaults.colors(
                                    thumbColor = EmeraldEqAccent,
                                    activeTrackColor = EmeraldEqAccent,
                                    inactiveTrackColor = Color(0xFF1E293B)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(28.dp)
                            )
                            Text(
                                text = "Net ${formatSignedDb(netVal)}",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = EmeraldEqAccent,
                                modifier = Modifier.width(68.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SimultaneousEqCurveCanvas(eqState: SimultaneousEqState) {
    val animatedContent = eqState.contentCurveDb.mapIndexed { i, target ->
        val animated by animateFloatAsState(
            targetValue = target,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
            label = "eq_content_$i"
        )
        animated
    }
    val animatedGearComp = eqState.gearCompensationDb.mapIndexed { i, target ->
        val animated by animateFloatAsState(
            targetValue = target,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
            label = "eq_gear_$i"
        )
        animated
    }
    val animatedNet = eqState.netSimultaneousDb.mapIndexed { i, target ->
        val animated by animateFloatAsState(
            targetValue = target,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
            label = "eq_net_$i"
        )
        animated
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF060B14))
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(132.dp)
                .testTag("simultaneous_eq_curve_canvas")
        ) {
            val w = size.width
            val h = size.height
            val padTop = 10f
            val padBottom = 10f
            val usableH = (h - padTop - padBottom).coerceAtLeast(10f)
            val zeroY = padTop + usableH / 2f

            fun dbToY(db: Float): Float {
                val norm = (db.coerceIn(-12f, 12f) + 12f) / 24f // 0 at -12, 1 at +12
                return padTop + usableH * (1f - norm)
            }

            // Grid lines: +6dB, 0dB, -6dB
            drawLine(
                color = Color(0xFF1E293B),
                start = Offset(0f, dbToY(6f)),
                end = Offset(w, dbToY(6f)),
                strokeWidth = 1f
            )
            drawLine(
                color = Color(0xFF334155),
                start = Offset(0f, zeroY),
                end = Offset(w, zeroY),
                strokeWidth = 1.6f
            )
            drawLine(
                color = Color(0xFF1E293B),
                start = Offset(0f, dbToY(-6f)),
                end = Offset(w, dbToY(-6f)),
                strokeWidth = 1f
            )

            val bandCount = SimultaneousEqEngine.BAND_LABELS.size
            val stepX = if (bandCount > 1) w / (bandCount - 1) else w

            // Vertical frequency guide lines
            for (i in 0 until bandCount) {
                val x = i * stepX
                drawLine(
                    color = Color(0xFF111C2E),
                    start = Offset(x, padTop),
                    end = Offset(x, h - padBottom),
                    strokeWidth = 1f
                )
            }

            fun buildSmoothPath(values: List<Float>): Path {
                val path = Path()
                values.forEachIndexed { idx, v ->
                    val x = idx * stepX
                    val y = dbToY(v)
                    if (idx == 0) {
                        path.moveTo(x, y)
                    } else {
                        val prevX = (idx - 1) * stepX
                        val prevY = dbToY(values[idx - 1])
                        val midX = (prevX + x) / 2f
                        path.cubicTo(midX, prevY, midX, y, x, y)
                    }
                }
                return path
            }

            // 1. Content Optimal Curve (Dashed NeonCyan)
            if (eqState.isEnabled) {
                val contentPath = buildSmoothPath(animatedContent)
                drawPath(
                    path = contentPath,
                    color = NeonCyan.copy(alpha = 0.72f),
                    style = Stroke(
                        width = 2.6f,
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                    )
                )
            }

            // 2. Inverse Gear Compensation Curve (WarmAmber)
            if (eqState.isEnabled && eqState.isGearCompensationEnabled) {
                val gearPath = buildSmoothPath(animatedGearComp)
                drawPath(
                    path = gearPath,
                    color = WarmAmber.copy(alpha = 0.78f),
                    style = Stroke(width = 2.6f, cap = StrokeCap.Round)
                )
            }

            // 3. Net Simultaneous Output Curve (Gradient Fill + Solid Stroke + Nodes)
            val netPath = buildSmoothPath(animatedNet)
            val fillPath = Path().apply {
                addPath(netPath)
                lineTo(w, zeroY)
                lineTo(0f, zeroY)
                close()
            }
            val primaryCurveColor = if (!eqState.isEnabled || eqState.isBypassCompare) CoralRed else EmeraldEqAccent
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        primaryCurveColor.copy(alpha = 0.28f),
                        Color.Transparent
                    )
                )
            )
            drawPath(
                path = netPath,
                color = primaryCurveColor,
                style = Stroke(width = 4.2f, cap = StrokeCap.Round)
            )

            animatedNet.forEachIndexed { idx, v ->
                val cx = idx * stepX
                val cy = dbToY(v)
                drawCircle(
                    color = Color(0xFF060B14),
                    radius = 6f,
                    center = Offset(cx, cy)
                )
                drawCircle(
                    color = primaryCurveColor,
                    radius = 4f,
                    center = Offset(cx, cy)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SimultaneousEqEngine.BAND_LABELS.forEachIndexed { idx, label ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = label,
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = formatSignedDb(eqState.netSimultaneousDb.getOrElse(idx) { 0f }),
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (!eqState.isEnabled || eqState.isBypassCompare) CoralRed else EmeraldEqAccent
                    )
                }
            }
        }
    }
}

@Composable
private fun GearZoneSliderRow(
    label: String,
    valueDb: Float,
    accentColor: Color,
    onValueChange: (Float) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.White,
            modifier = Modifier.width(74.dp)
        )
        Slider(
            value = valueDb.coerceIn(-10f, 10f),
            onValueChange = onValueChange,
            valueRange = -10f..10f,
            colors = SliderDefaults.colors(
                thumbColor = accentColor,
                activeTrackColor = accentColor,
                inactiveTrackColor = Color(0xFF0F172A)
            ),
            modifier = Modifier
                .weight(1f)
                .height(28.dp)
        )
        Text(
            text = formatSignedDb(valueDb),
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            color = accentColor,
            modifier = Modifier.width(50.dp)
        )
    }
}

@Composable
private fun EqFilterPill(
    label: String,
    selected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (selected) accentColor.copy(alpha = 0.22f) else Color(0xFF1E293B),
        border = BorderStroke(1.dp, if (selected) accentColor else Color(0xFF334155)),
        modifier = Modifier
            .defaultMinSize(minHeight = 36.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) accentColor else Color(0xFFCBD5E1),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color(0xFFCBD5E1)
        )
    }
}

private fun formatSignedDb(db: Float): String {
    val rounded = (db * 10f).roundToInt() / 10f
    return if (rounded > 0f) "+${rounded}dB" else "${rounded}dB"
}
