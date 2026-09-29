package com.example.ui.plus.phone

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.cos
import kotlin.math.sin

data class SatelliteMessage(
    val id: String,
    val text: String,
    val timestamp: String,
    val isEmergency: Boolean = false,
    val status: String = "Transmitted"
)

@Composable
fun SatellitePhoneComponent(
    onInitiateSatCall: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isSatelliteEnabled by remember { mutableStateOf(true) }
    var isEmergencySosActive by remember { mutableStateOf(false) }
    var compassHeading by remember { mutableFloatStateOf(48f) }
    var satelliteAzimuth by remember { mutableFloatStateOf(52f) }
    var signalStrengthPercent by remember { mutableIntStateOf(92) }
    var connectedSatelliteName by remember { mutableStateOf("TS-SAT-4 (Civic LEO Orbit)") }
    var isPointingOptimal by remember { mutableStateOf(true) }
    
    // Emergency SOS dialog
    var showSosDialog by remember { mutableStateOf(false) }
    var sosSentSuccess by remember { mutableStateOf(false) }

    // Satellite SMS state
    var satMessageText by remember { mutableStateOf("") }
    var satMessages by remember {
        mutableStateOf(
            listOf(
                SatelliteMessage("1", "Uplink packet synced with Harbor Watchtower. All clear.", "11:20 AM"),
                SatelliteMessage("2", "Weather advisory relayed via LEO-4: Breakwater squall at 21:00.", "10:45 AM")
            )
        )
    }

    // Dial satellite call state
    var satCallDialText by remember { mutableStateOf("+SAT 8816-") }

    // Compass animation effect simulating real-time telemetry
    LaunchedEffect(Unit) {
        var angle = 45f
        while (isActive) {
            delay(1200L)
            angle = (angle + ((-2..2).random())).coerceIn(30f, 70f)
            compassHeading = angle
            val delta = kotlin.math.abs(compassHeading - satelliteAzimuth)
            isPointingOptimal = delta < 8f
            signalStrengthPercent = if (isPointingOptimal) (90..96).random() else (65..80).random()
        }
    }

    Box(modifier = modifier.fillMaxSize().background(DarkBg)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        // Satellite Uplink Header & Status Banner
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isSatelliteEnabled) Color(0xFF071926) else DarkSurface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSatelliteEnabled) NeonCyan.copy(alpha = 0.6f) else DarkBorder
                ),
                modifier = Modifier.fillMaxWidth().testTag("satellite_status_banner")
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
                                color = if (isSatelliteEnabled) NeonCyan.copy(alpha = 0.15f) else DarkSurfaceElevated,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.SatelliteAlt,
                                        contentDescription = null,
                                        tint = if (isSatelliteEnabled) NeonCyan else DarkTextMuted,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Satellite Direct Link",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (isPointingOptimal) Color(0xFF30D158) else WarmAmber
                                    ) {
                                        Text(
                                            text = if (isPointingOptimal) "LOCK" else "SEARCHING",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.Black,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = if (isSatelliteEnabled) connectedSatelliteName else "Transceiver Disabled",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSatelliteEnabled) NeonCyan else DarkTextMuted
                                )
                            }
                        }

                        Switch(
                            checked = isSatelliteEnabled,
                            onCheckedChange = { isSatelliteEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF003544),
                                checkedTrackColor = NeonCyan,
                                uncheckedThumbColor = DarkTextSecondary,
                                uncheckedTrackColor = DarkSurfaceElevated
                            ),
                            modifier = Modifier.testTag("toggle_satellite_switch")
                        )
                    }

                    if (isSatelliteEnabled) {
                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = DarkBorder)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TelemetryStatChip(label = "LINK SIGNAL", value = "$signalStrengthPercent%", color = Color(0xFF30D158))
                            TelemetryStatChip(label = "ORBIT ALT", value = "540 km", color = NeonCyan)
                            TelemetryStatChip(label = "DOPPLER", value = "+1.4 kHz", color = WarmAmber)
                            TelemetryStatChip(label = "LATENCY", value = "48 ms", color = Color.White)
                        }
                    }
                }
            }
        }

        // Sky Targeter & Satellite Compass Visualizer
        if (isSatelliteEnabled) {
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
                                text = "SKY ALIGNMENT COMPASS",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                color = NeonCyan
                            )
                            Text(
                                text = if (isPointingOptimal) "✓ Optimized Azimuth" else "Rotate device slightly East",
                                fontSize = 11.sp,
                                color = if (isPointingOptimal) Color(0xFF30D158) else WarmAmber
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Custom Sky Radar Canvas
                        Box(
                            modifier = Modifier
                                .size(180.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF050E17)),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val center = Offset(size.width / 2f, size.height / 2f)
                                val radius = size.width / 2f - 12f

                                // Orbital Range Rings
                                drawCircle(color = Color(0xFF132A3E), radius = radius, style = Stroke(width = 1.5f))
                                drawCircle(color = Color(0xFF132A3E), radius = radius * 0.65f, style = Stroke(width = 1.5f))
                                drawCircle(color = Color(0xFF132A3E), radius = radius * 0.35f, style = Stroke(width = 1.5f))

                                // Crosshairs
                                drawLine(Color(0xFF132A3E), Offset(center.x, 10f), Offset(center.x, size.height - 10f), strokeWidth = 1f)
                                drawLine(Color(0xFF132A3E), Offset(10f, center.y), Offset(size.width - 10f, center.y), strokeWidth = 1f)

                                // Satellite Position Target Dot
                                val satRad = Math.toRadians((satelliteAzimuth - 90).toDouble())
                                val satDistance = radius * 0.7f
                                val satX = center.x + (satDistance * cos(satRad)).toFloat()
                                val satY = center.y + (satDistance * sin(satRad)).toFloat()

                                drawCircle(color = NeonCyan.copy(alpha = 0.3f), radius = 18f, center = Offset(satX, satY))
                                drawCircle(color = NeonCyan, radius = 6f, center = Offset(satX, satY))

                                // Device Heading Vector Line
                                val headRad = Math.toRadians((compassHeading - 90).toDouble())
                                val headX = center.x + (radius * cos(headRad)).toFloat()
                                val headY = center.y + (radius * sin(headRad)).toFloat()

                                drawLine(
                                    color = if (isPointingOptimal) Color(0xFF30D158) else WarmAmber,
                                    start = center,
                                    end = Offset(headX, headY),
                                    strokeWidth = 3f
                                )
                                drawCircle(color = if (isPointingOptimal) Color(0xFF30D158) else WarmAmber, radius = 5f, center = Offset(headX, headY))
                            }

                            // Center Label
                            Text(
                                text = "${compassHeading.toInt()}°",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Aim device toward open sky. No cellular towers or Wi-Fi required.",
                            style = MaterialTheme.typography.labelSmall,
                            color = DarkTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Satellite Calling Quick Dial
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SATELLITE VOICE RELAY",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                color = NeonCyan
                            )
                            Surface(shape = RoundedCornerShape(4.dp), color = NeonCyan.copy(alpha = 0.2f)) {
                                Text(
                                    text = "ENCRYPTED 4.8 kbps",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonCyan,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = satCallDialText,
                            onValueChange = { satCallDialText = it },
                            label = { Text("Satellite Wire / Emergency Station #") },
                            trailingIcon = {
                                IconButton(
                                    onClick = { onInitiateSatCall(satCallDialText) },
                                    modifier = Modifier.testTag("sat_call_dial_btn")
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFF30D158),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.PhoneInTalk, contentDescription = "Call", tint = Color.White, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkSurfaceElevated,
                                unfocusedContainerColor = DarkSurfaceElevated,
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Emergency SOS Satellite Beacon
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF2B0E14),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, CoralRed.copy(alpha = 0.8f)),
                    modifier = Modifier.fillMaxWidth().testTag("satellite_sos_card")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = CoralRed.copy(alpha = 0.2f),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = CoralRed, modifier = Modifier.size(24.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Emergency SOS via Satellite",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "Broadcast GPS & distress coordinates to civic rescue dispatch",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DarkTextSecondary
                                )
                            }
                        }

                        Button(
                            onClick = { showSosDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = CoralRed, contentColor = Color.White),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("trigger_sos_button")
                        ) {
                            Text("SOS", fontWeight = FontWeight.Black)
                        }
                    }
                }
            }

            // Satellite SMS Text Dispatch
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SATELLITE PACKET DISPATCH (SMS)",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                color = NeonCyan
                            )
                            Text(text = "${satMessages.size} PACKETS", fontSize = 10.sp, color = DarkTextMuted)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        satMessages.forEach { msg ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurfaceElevated,
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = msg.text, style = MaterialTheme.typography.bodySmall, color = Color.White)
                                        Text(text = "${msg.timestamp} • ${msg.status}", fontSize = 10.sp, color = NeonCyan)
                                    }
                                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color(0xFF30D158), modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = satMessageText,
                                onValueChange = { satMessageText = it },
                                placeholder = { Text("Send short message over sat link...", fontSize = 12.sp, color = DarkTextMuted) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = DarkSurfaceElevated,
                                    unfocusedContainerColor = DarkSurfaceElevated,
                                    focusedBorderColor = NeonCyan,
                                    unfocusedBorderColor = DarkBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    if (satMessageText.isNotBlank()) {
                                        val newMsg = SatelliteMessage(
                                            id = System.currentTimeMillis().toString(),
                                            text = satMessageText,
                                            timestamp = "Just now"
                                        )
                                        satMessages = listOf(newMsg) + satMessages
                                        satMessageText = ""
                                    }
                                },
                                modifier = Modifier.testTag("send_sat_sms_btn")
                            ) {
                                Surface(shape = CircleShape, color = NeonCyan, modifier = Modifier.size(40.dp)) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color(0xFF003544), modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // SOS Broadcast Notification
            if (sosSentSuccess) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0C2B1D),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF30D158)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF30D158), modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("SOS Beacon Acknowledged", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                    Text("Civic Rescue packet confirmed by Satellite Relay 4", fontSize = 11.sp, color = Color(0xFF30D158))
                                }
                            }
                            IconButton(onClick = { sosSentSuccess = false }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = DarkTextMuted, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Emergency SOS Confirmation Modal
    if (showSosDialog) {
            AlertDialog(
                onDismissRequest = { showSosDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = CoralRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Broadcast Satellite SOS?", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "This will immediately transmit your emergency beacon to the Harbor Coast Guard and Civic Rescue Network via LEO satellite relay.",
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Surface(shape = RoundedCornerShape(8.dp), color = DarkSurfaceElevated, modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Telemetry Packet Preview:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = NeonCyan)
                                Text("• Coordinates: 45° 28' 14\" N, 122° 40' 52\" W", fontSize = 11.sp, color = DarkTextSecondary)
                                Text("• Distress Code: CIVIC_OFF_GRID_BEACON", fontSize = 11.sp, color = DarkTextSecondary)
                                Text("• Battery Reserve: 88%", fontSize = 11.sp, color = DarkTextSecondary)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showSosDialog = false
                            sosSentSuccess = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CoralRed, contentColor = Color.White)
                    ) {
                        Text("Transmit SOS Beacon", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSosDialog = false }) {
                        Text("Cancel", color = DarkTextSecondary)
                    }
                }
            )
        }
    }
}

@Composable
private fun TelemetryStatChip(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = DarkTextMuted)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Black, color = color)
    }
}
