package com.example.ui.plus.iot

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.cos
import kotlin.math.sin

enum class DriveMode { ECO, COMFORT, SPORT, CIVIC_CRUISE }

@Composable
fun IotCarDashboardView(
    liveTime: LocalDateTime,
    modifier: Modifier = Modifier
) {
    var speed by remember { mutableIntStateOf(54) }
    var rpm by remember { mutableFloatStateOf(2.4f) }
    var gear by remember { mutableStateOf("D") }
    var driveMode by remember { mutableStateOf(DriveMode.COMFORT) }
    var batteryPercentage by remember { mutableIntStateOf(78) }
    var isCruiseControlOn by remember { mutableStateOf(true) }
    var cabinTemp by remember { mutableFloatStateOf(71.5f) }
    var fanSpeed by remember { mutableIntStateOf(3) }
    var isAcOn by remember { mutableStateOf(true) }
    var isHazardOn by remember { mutableStateOf(false) }
    var isHeadlightsOn by remember { mutableStateOf(true) }
    var isMediaPlaying by remember { mutableStateOf(true) }

    // Subtle dynamic speed fluctuation
    LaunchedEffect(Unit) {
        while (true) {
            delay(1500)
            if (isCruiseControlOn) {
                speed = 52 + (Math.random() * 5).toInt()
                rpm = 2.1f + (Math.random() * 0.4f).toFloat()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070A0F))
            .padding(16.dp)
            .testTag("iot_car_dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Cockpit Status Bar
        Surface(
            color = Color(0xFF0F172A),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Townsquare AutoOS · EV-900 Cruiser",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "⚡ $batteryPercentage% (312 mi)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF22C55E)
                    )
                    Text(
                        text = liveTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                    Text(
                        text = "68°F Clear",
                        fontSize = 12.sp,
                        color = DarkTextSecondary
                    )
                }
            }
        }

        // Instrument Cluster Main Canvas (Speedometer + Tachometer + Gear)
        Surface(
            color = Color(0xFF0B111E),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.5.dp, Color(0xFF1E293B)),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.3f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Gauge: RPM
                Box(
                    modifier = Modifier.size(130.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawArc(
                            color = Color(0xFF1E293B),
                            startAngle = 135f,
                            sweepAngle = 270f,
                            useCenter = false,
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = if (rpm > 5f) CoralRed else WarmAmber,
                            startAngle = 135f,
                            sweepAngle = (rpm / 8f) * 270f,
                            useCenter = false,
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = String.format("%.1f", rpm),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "x1000 RPM",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarmAmber
                        )
                    }
                }

                // Center Display: Speed & Gear & Navigation Prompt
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    // Gear Selector Strip
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("P", "R", "N", "D", "S").forEach { g ->
                            val isSelected = gear == g
                            Text(
                                text = g,
                                fontSize = if (isSelected) 18.sp else 13.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                                color = if (isSelected) NeonCyan else Color(0xFF475569),
                                modifier = Modifier
                                    .clickable { gear = g }
                                    .padding(4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Big Speed Digits
                    Text(
                        text = "$speed",
                        fontSize = 58.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        lineHeight = 60.sp
                    )
                    Text(
                        text = "MPH",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkTextSecondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // GPS Turn by turn prompt
                    Surface(
                        color = Color(0xFF0284C7).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF0284C7))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Navigation, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Turn Right onto Grand Blvd · 450 ft", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // Right Gauge: Battery Power & Regeneration
                Box(
                    modifier = Modifier.size(130.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawArc(
                            color = Color(0xFF1E293B),
                            startAngle = 135f,
                            sweepAngle = 270f,
                            useCenter = false,
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = Color(0xFF22C55E),
                            startAngle = 135f,
                            sweepAngle = (batteryPercentage / 100f) * 270f,
                            useCenter = false,
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$batteryPercentage%",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "EV BATTERY",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF22C55E)
                        )
                    }
                }
            }
        }

        // Infotainment & Drive Controls Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Left Card: Vehicle Controls & ADAS
            Surface(
                color = Color(0xFF0F172A),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFF1E293B)),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Vehicle Dynamics & ADAS",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )

                    // Drive mode selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        DriveMode.values().forEach { mode ->
                            val isSelected = driveMode == mode
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSelected) NeonCyan.copy(alpha = 0.2f) else Color(0xFF1E293B),
                                border = BorderStroke(1.dp, if (isSelected) NeonCyan else Color.Transparent),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { driveMode = mode }
                            ) {
                                Text(
                                    text = mode.name.take(4),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) NeonCyan else Color(0xFF94A3B8),
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    // Radar Cruise & Headlights Toggles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FilterChip(
                            selected = isCruiseControlOn,
                            onClick = { isCruiseControlOn = !isCruiseControlOn },
                            label = { Text("Radar Cruise 55mph", fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonCyan.copy(alpha = 0.2f),
                                selectedLabelColor = NeonCyan
                            )
                        )
                        FilterChip(
                            selected = isHeadlightsOn,
                            onClick = { isHeadlightsOn = !isHeadlightsOn },
                            label = { Text("Auto Lights", fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = WarmAmber.copy(alpha = 0.2f),
                                selectedLabelColor = WarmAmber
                            )
                        )
                    }

                    // Tire Pressure TPM Status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("TPMS: FL 34 | FR 34 PSI", fontSize = 9.sp, color = DarkTextSecondary)
                        Text("RL 34 | RR 34 PSI", fontSize = 9.sp, color = DarkTextSecondary)
                    }
                }
            }

            // Right Card: 2-Zone Climate & In-Car Townsquare Radio
            Surface(
                color = Color(0xFF0F172A),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFF1E293B)),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Dual-Zone Climate & Cabin Audio",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )

                    // Climate controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { cabinTemp -= 0.5f }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Remove, contentDescription = null, tint = NeonCyan)
                            }
                            Text(
                                text = String.format("%.1f°F", cabinTemp),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            IconButton(onClick = { cabinTemp += 0.5f }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = CoralRed)
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Fan: $fanSpeed", fontSize = 11.sp, color = DarkTextSecondary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Switch(
                                checked = isAcOn,
                                onCheckedChange = { isAcOn = it },
                                modifier = Modifier.height(20.dp),
                                colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan)
                            )
                        }
                    }

                    // In-Car Audio Strip
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Radio, contentDescription = null, tint = WarmAmber, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text("City Soundwaves 98.4 FM", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("Townsquare Harbor Jazz Dispatch", fontSize = 9.sp, color = DarkTextSecondary)
                                }
                            }
                            IconButton(
                                onClick = { isMediaPlaying = !isMediaPlaying },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (isMediaPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = NeonCyan,
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
