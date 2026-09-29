package com.example.ui.plus.phone

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.cos
import kotlin.math.sin

enum class MastheadStatus(val label: String, val color: Color) {
    OPERATIONAL("Operational 100%", Color(0xFF30D158)),
    REDUCED_CAPACITY("Reduced Power / Solar Backup", WarmAmber),
    MAINTENANCE("Scheduled Calibration", NeonCyan),
    OFFLINE("Standby Reserve", CoralRed)
}

data class SignalMasthead(
    val id: String,
    val name: String,
    val location: String,
    val distanceMiles: Double,
    val azimuthDegrees: Float,
    val status: MastheadStatus,
    val bands: String,
    val heightMeters: Int,
    val txPowerWatts: Int,
    val isCurrentRelay: Boolean = false
)

@Composable
fun SignalMastheadComponent(
    modifier: Modifier = Modifier
) {
    var mastheads by remember {
        mutableStateOf(
            listOf(
                SignalMasthead(
                    id = "mast-1",
                    name = "Civic Clocktower Masthead #4",
                    location = "1 Civic Square • Old Town",
                    distanceMiles = 0.3,
                    azimuthDegrees = 32f,
                    status = MastheadStatus.OPERATIONAL,
                    bands = "LTE B71 (600MHz) • 5G n77 (3.7GHz)",
                    heightMeters = 42,
                    txPowerWatts = 160,
                    isCurrentRelay = true
                ),
                SignalMasthead(
                    id = "mast-2",
                    name = "Harbor Breakwater Tower Relay",
                    location = "Pier 14 • Maritime Basin",
                    distanceMiles = 0.9,
                    azimuthDegrees = 118f,
                    status = MastheadStatus.REDUCED_CAPACITY,
                    bands = "LTE B2/B66 • Microwave Link 5.8GHz",
                    heightMeters = 35,
                    txPowerWatts = 120,
                    isCurrentRelay = false
                ),
                SignalMasthead(
                    id = "mast-3",
                    name = "North Ridge Monopole",
                    location = "North Hill Crest Overlook",
                    distanceMiles = 1.7,
                    azimuthDegrees = 345f,
                    status = MastheadStatus.OPERATIONAL,
                    bands = "5G Ultra Wideband n260 mmWave",
                    heightMeters = 55,
                    txPowerWatts = 200,
                    isCurrentRelay = false
                ),
                SignalMasthead(
                    id = "mast-4",
                    name = "Canal Basin Microcell Node",
                    location = "Riverside Way Lock Gate",
                    distanceMiles = 0.5,
                    azimuthDegrees = 210f,
                    status = MastheadStatus.MAINTENANCE,
                    bands = "LTE B48 CBRS Private Wire",
                    heightMeters = 18,
                    txPowerWatts = 40,
                    isCurrentRelay = false
                )
            )
        )
    }

    var selectedMastheadId by remember { mutableStateOf(mastheads.first().id) }
    val currentConnected = mastheads.find { it.isCurrentRelay } ?: mastheads.first()

    // Real-time RF diagnostics state
    var rsrpDbm by remember { mutableIntStateOf(-76) }
    var rsrqDb by remember { mutableIntStateOf(-8) }
    var sinrDb by remember { mutableIntStateOf(24) }
    var pingMs by remember { mutableIntStateOf(14) }
    var isRunningPingTest by remember { mutableStateOf(false) }

    // Handover notification
    var handoverNotice by remember { mutableStateOf<String?>(null) }

    // Live RF jitter effect
    LaunchedEffect(Unit) {
        while (isActive) {
            delay(1500L)
            rsrpDbm = (-82..-74).random()
            rsrqDb = (-11..-7).random()
            sinrDb = (21..27).random()
        }
    }

    fun switchRelay(targetId: String) {
        mastheads = mastheads.map { it.copy(isCurrentRelay = it.id == targetId) }
        val target = mastheads.find { it.id == targetId }
        handoverNotice = "Relay handover successful: Bound to ${target?.name}"
    }

    fun runDiagnostics() {
        isRunningPingTest = true
    }

    LaunchedEffect(isRunningPingTest) {
        if (isRunningPingTest) {
            delay(1200L)
            pingMs = (11..16).random()
            isRunningPingTest = false
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Nearest Masthead Primary Telemetry Card
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0C1B2A),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonCyan.copy(alpha = 0.7f)),
                modifier = Modifier.fillMaxWidth().testTag("primary_masthead_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = NeonCyan.copy(alpha = 0.2f),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.CellTower,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = currentConnected.name,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFF30D158)
                                    ) {
                                        Text(
                                            text = "ACTIVE RELAY",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.Black,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "${currentConnected.location} • ${currentConnected.distanceMiles} mi away (${currentConnected.azimuthDegrees.toInt()}° NNE)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeonCyan
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = DarkBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    // RF Health Stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        RfMetricColumn(label = "RSRP SIGNAL", value = "$rsrpDbm dBm", status = "Excellent", color = Color(0xFF30D158))
                        RfMetricColumn(label = "RSRQ QUALITY", value = "$rsrqDb dB", status = "Clean SNR", color = NeonCyan)
                        RfMetricColumn(label = "SINR NOISE", value = "+$sinrDb dB", status = "High Gain", color = WarmAmber)
                        RfMetricColumn(label = "PING LATENCY", value = "$pingMs ms", status = if (isRunningPingTest) "Testing..." else "Normal", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Bands: ${currentConnected.bands}",
                            style = MaterialTheme.typography.labelSmall,
                            color = DarkTextSecondary
                        )

                        TextButton(
                            onClick = { runDiagnostics() },
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isRunningPingTest) "Testing..." else "Run Ping Test", fontSize = 11.sp, color = NeonCyan)
                        }
                    }
                }
            }
        }

        // Radar Directional Compass
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MASTHEAD AZIMUTH RADAR",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = NeonCyan
                        )
                        Text(text = "4 Mastheads in Range", fontSize = 11.sp, color = DarkTextSecondary)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF081422)),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val radius = size.width / 2f - 10f

                            // Concentric distance ranges
                            drawCircle(Color(0xFF132B40), radius = radius, style = Stroke(1.5f))
                            drawCircle(Color(0xFF132B40), radius = radius * 0.65f, style = Stroke(1.5f))
                            drawCircle(Color(0xFF132B40), radius = radius * 0.35f, style = Stroke(1.5f))

                            // Crosshair lines
                            drawLine(Color(0xFF132B40), Offset(center.x, 8f), Offset(center.x, size.height - 8f), strokeWidth = 1f)
                            drawLine(Color(0xFF132B40), Offset(8f, center.y), Offset(size.width - 8f, center.y), strokeWidth = 1f)

                            // Plot each masthead on radar
                            mastheads.forEach { mast ->
                                val angleRad = Math.toRadians((mast.azimuthDegrees - 90).toDouble())
                                val distScale = (mast.distanceMiles / 2.0).coerceIn(0.2, 0.9)
                                val x = center.x + (radius * distScale * cos(angleRad)).toFloat()
                                val y = center.y + (radius * distScale * sin(angleRad)).toFloat()

                                val dotColor = if (mast.isCurrentRelay) Color(0xFF30D158) else mast.status.color
                                drawCircle(dotColor, radius = if (mast.isCurrentRelay) 7f else 4.5f, center = Offset(x, y))

                                if (mast.isCurrentRelay) {
                                    drawCircle(Color(0xFF30D158).copy(alpha = 0.3f), radius = 16f, center = Offset(x, y))
                                }
                            }
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("DEVICE", fontSize = 9.sp, fontWeight = FontWeight.Black, color = NeonCyan)
                            Text("GRID", fontSize = 8.sp, color = DarkTextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Green beacon indicates your active cell tower connection. Tap any tower below to inspect or switch.",
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkTextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        // Handover Notice Toast
        if (handoverNotice != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF0F3223),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF30D158)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = handoverNotice!!, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        IconButton(onClick = { handoverNotice = null }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = DarkTextMuted, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Nearby Mastheads List
        item {
            Text(
                text = "SURROUNDING TELECOM MASTHEADS",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = DarkTextSecondary
            )
        }

        items(mastheads, key = { it.id }) { mast ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (mast.isCurrentRelay) Color(0xFF0D2235) else DarkSurface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (mast.isCurrentRelay) NeonCyan else DarkBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedMastheadId = mast.id }
                    .testTag("masthead_item_${mast.id}")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = mast.status.color.copy(alpha = 0.15f),
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Sensors,
                                        contentDescription = null,
                                        tint = mast.status.color,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = mast.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "${mast.location} • ${mast.distanceMiles} mi (${mast.azimuthDegrees.toInt()}°)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DarkTextSecondary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = mast.status.color.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = mast.status.label,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = mast.status.color,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Specs: ${mast.heightMeters}m height • ${mast.txPowerWatts}W EIRP • ${mast.bands}", fontSize = 11.sp, color = DarkTextMuted)

                    if (!mast.isCurrentRelay) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            Button(
                                onClick = { switchRelay(mast.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("switch_relay_btn_${mast.id}")
                            ) {
                                Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Handover to this Masthead", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RfMetricColumn(label: String, value: String, status: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = DarkTextMuted)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Black, color = color)
        Text(text = status, fontSize = 9.sp, color = DarkTextSecondary)
    }
}
