package com.example.ui.plus.camera

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

enum class ArHudMode {
    CIVIC_GEO_AR,
    OBJECT_SCANNER,
    THERMAL_NIGHT_HUD
}

enum class CameraFilterMode {
    NATURAL,
    CYBER_CYAN,
    BROADSHEET_BW,
    INFRARED_HEAT
}

data class GeoArPin(
    val name: String,
    val category: String,
    val distanceMeters: Int,
    val direction: String,
    val emoji: String,
    val offsetXPercent: Float,
    val offsetYPercent: Float
)

data class ScannedObjectTarget(
    val id: String,
    val label: String,
    val confidencePercent: Int,
    val category: String,
    val actionText: String,
    val rectXPercent: Float,
    val rectYPercent: Float,
    val rectWPercent: Float,
    val rectHPercent: Float
)

@Composable
fun TownsquareArCameraApp(
    onBack: () -> Unit,
    onNavigateToMarketplace: () -> Unit = {},
    onNavigateToMailbox: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var hudMode by remember { mutableStateOf(ArHudMode.CIVIC_GEO_AR) }
    var filterMode by remember { mutableStateOf(CameraFilterMode.NATURAL) }
    var zoomLevel by remember { mutableFloatStateOf(1f) }
    var isFlashOn by remember { mutableStateOf(false) }
    var headingDegrees by remember { mutableIntStateOf(142) }
    var altitudeMeters by remember { mutableIntStateOf(34) }
    var capturedPhotoDialog by remember { mutableStateOf(false) }
    var actionMessage by remember { mutableStateOf<String?>(null) }

    // Dynamic compass & gyro drift simulation
    LaunchedEffect(Unit) {
        while (true) {
            delay(1200)
            headingDegrees = (headingDegrees + (-1..1).random() + 360) % 360
        }
    }

    val geoPins = remember {
        listOf(
            GeoArPin("Town Hall & Civic Council", "Civic Landmark", 240, "NW", "🏛️", 0.28f, 0.32f),
            GeoArPin("Grand Central Terminal (Line 3)", "Transit Hub", 580, "N", "🚇", 0.65f, 0.40f),
            GeoArPin("Old Town Artisan Bazaar", "Marketplace", 110, "W", "🛍️", 0.20f, 0.60f),
            GeoArPin("Harbor Waterfront Promenade", "Recreation", 920, "NE", "🌊", 0.78f, 0.25f)
        )
    }

    val scannedObjects = remember {
        listOf(
            ScannedObjectTarget("obj_1", "Townsquare Broadsheet (Oct Edition)", 98, "Media Dispatch", "Open Article", 0.18f, 0.28f, 0.35f, 0.25f),
            ScannedObjectTarget("obj_2", "Line 3 Contactless Metro Card", 94, "Transit NFC", "Check Balance", 0.60f, 0.52f, 0.28f, 0.20f),
            ScannedObjectTarget("obj_3", "Artisan Roasted Coffee Beans", 89, "Local Goods", "Find in Market", 0.22f, 0.62f, 0.30f, 0.22f)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("townsquare_ar_camera_screen")
    ) {
        // Simulated Camera Viewfinder with filter effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    when (filterMode) {
                        CameraFilterMode.NATURAL -> Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF0B0F17)))
                        CameraFilterMode.CYBER_CYAN -> Brush.verticalGradient(listOf(Color(0xFF002A36), Color(0xFF073642), Color(0xFF001E26)))
                        CameraFilterMode.BROADSHEET_BW -> Brush.verticalGradient(listOf(Color(0xFF222222), Color(0xFF333333), Color(0xFF1A1A1A)))
                        CameraFilterMode.INFRARED_HEAT -> Brush.verticalGradient(listOf(Color(0xFF3B0764), Color(0xFF831843), Color(0xFF1E1B4B)))
                    }
                )
        ) {
            // Viewfinder Grid Overlay & Crosshairs
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val lineColor = when (hudMode) {
                    ArHudMode.CIVIC_GEO_AR -> NeonCyan.copy(alpha = 0.25f)
                    ArHudMode.OBJECT_SCANNER -> WarmAmber.copy(alpha = 0.25f)
                    ArHudMode.THERMAL_NIGHT_HUD -> Color(0xFF22C55E).copy(alpha = 0.35f)
                }

                // Rule of Thirds Lines
                drawLine(lineColor, Offset(w / 3f, 0f), Offset(w / 3f, h), strokeWidth = 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f)))
                drawLine(lineColor, Offset(2f * w / 3f, 0f), Offset(2f * w / 3f, h), strokeWidth = 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f)))
                drawLine(lineColor, Offset(0f, h / 3f), Offset(w, h / 3f), strokeWidth = 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f)))
                drawLine(lineColor, Offset(0f, 2f * h / 3f), Offset(w, 2f * h / 3f), strokeWidth = 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f)))

                // Center Reticle
                val center = Offset(w / 2f, h / 2f)
                drawCircle(color = lineColor, radius = 24.dp.toPx(), center = center, style = Stroke(width = 1.5.dp.toPx()))
                drawLine(lineColor, Offset(center.x - 36.dp.toPx(), center.y), Offset(center.x + 36.dp.toPx(), center.y), strokeWidth = 1.5.dp.toPx())
                drawLine(lineColor, Offset(center.x, center.y - 36.dp.toPx()), Offset(center.x, center.y + 36.dp.toPx()), strokeWidth = 1.5.dp.toPx())
            }

            // HUD OVERLAY: Mode 1 - CIVIC GEO-AR PINS
            if (hudMode == ArHudMode.CIVIC_GEO_AR) {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val boxW = maxWidth
                    val boxH = maxHeight
                    geoPins.forEach { pin ->
                        val leftOffset = boxW * pin.offsetXPercent
                        val topOffset = boxH * pin.offsetYPercent

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF0F172A).copy(alpha = 0.85f),
                            border = BorderStroke(1.5.dp, NeonCyan),
                            modifier = Modifier
                                .offset(x = leftOffset, y = topOffset)
                                .clickable {
                                    actionMessage = "Locked onto ${pin.name} (${pin.distanceMeters}m)"
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(pin.emoji, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(pin.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("${pin.distanceMeters}m ${pin.direction} • ${pin.category}", fontSize = 9.sp, color = NeonCyan)
                                }
                            }
                        }
                    }
                }
            }

            // HUD OVERLAY: Mode 2 - OBJECT & QR/BARCODE SCANNER
            if (hudMode == ArHudMode.OBJECT_SCANNER) {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val boxW = maxWidth
                    val boxH = maxHeight
                    scannedObjects.forEach { target ->
                        val leftOffset = boxW * target.rectXPercent
                        val topOffset = boxH * target.rectYPercent
                        val cardW = boxW * target.rectWPercent
                        val cardH = boxH * target.rectHPercent

                        Box(
                            modifier = Modifier
                                .offset(x = leftOffset, y = topOffset)
                                .size(width = cardW, height = cardH)
                                .border(BorderStroke(2.dp, WarmAmber), RoundedCornerShape(8.dp))
                                .padding(4.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.Black.copy(alpha = 0.8f),
                                modifier = Modifier.align(Alignment.TopStart)
                            ) {
                                Column(modifier = Modifier.padding(4.dp)) {
                                    Text("[${target.confidencePercent}% MATCH]", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF22C55E), fontFamily = FontFamily.Monospace)
                                    Text(target.label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = target.actionText,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = WarmAmber,
                                        modifier = Modifier.clickable {
                                            if (target.actionText == "Find in Market") onNavigateToMarketplace()
                                            else actionMessage = "Selected: ${target.label}"
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // HUD OVERLAY: Mode 3 - THERMAL NIGHT HUD TELEMETRY
            if (hudMode == ArHudMode.THERMAL_NIGHT_HUD) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 72.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("FLIR IR GAIN: +18dB", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF22C55E), fontWeight = FontWeight.Bold)
                        Text("FOV: 84° HORIZON", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF22C55E), fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("TEMP REF: 21.4°C AMBIENT", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF22C55E))
                        Text("LUX SENSOR: 0.42 LX", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF22C55E))
                    }
                }
            }
        }

        // TOP HUD BAR: Back, Mode Switcher, Compass & Telemetry
        Surface(
            color = Color.Black.copy(alpha = 0.65f),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("camera_back_button")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }

                    // Compass Heading Tape
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0F172A).copy(alpha = 0.8f),
                        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Explore, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "HDG: $headingDegrees° NW • ALT: ${altitudeMeters}m",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Flashlight toggle
                    IconButton(onClick = { isFlashOn = !isFlashOn }) {
                        Icon(
                            imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = "Torch",
                            tint = if (isFlashOn) WarmAmber else Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // AR Mode Pills Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    listOf(
                        Triple(ArHudMode.CIVIC_GEO_AR, "🏛️ Civic Geo-AR", NeonCyan),
                        Triple(ArHudMode.OBJECT_SCANNER, "🔍 Object Vision", WarmAmber),
                        Triple(ArHudMode.THERMAL_NIGHT_HUD, "🌙 Night HUD", Color(0xFF22C55E))
                    ).forEach { (mode, label, color) ->
                        val isSelected = hudMode == mode
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) color.copy(alpha = 0.25f) else Color.Transparent,
                            border = BorderStroke(1.dp, if (isSelected) color else Color.Gray.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .clickable { hudMode = mode }
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) color else Color.LightGray,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }

        // Action Toast Notification Banner
        if (actionMessage != null) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurfaceVariant,
                border = BorderStroke(1.dp, NeonCyan),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = actionMessage!!, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { actionMessage = null }, modifier = Modifier.size(20.dp)) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // BOTTOM HUD CONTROLS: Zoom, Filters, Shutter & App Integrations
        Surface(
            color = Color.Black.copy(alpha = 0.75f),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Zoom Pills (0.5x, 1x, 2x, 5x)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(0.5f, 1.0f, 2.0f, 5.0f).forEach { z ->
                        val isSelected = zoomLevel == z
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) NeonCyan else Color(0xFF1E293B),
                            modifier = Modifier
                                .size(32.dp)
                                .clickable { zoomLevel = z }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${z}x",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Row: Filter Switcher, Big Shutter Button, Media Dispatch Integrations
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Filter Mode Switcher
                    IconButton(
                        onClick = {
                            val modes = CameraFilterMode.values()
                            val next = modes[(filterMode.ordinal + 1) % modes.size]
                            filterMode = next
                        }
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF1E293B),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.FilterList, contentDescription = "Filters", tint = WarmAmber, modifier = Modifier.size(22.dp))
                            }
                        }
                    }

                    // MAIN SHUTTER BUTTON
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(4.dp)
                            .clickable {
                                capturedPhotoDialog = true
                            }
                            .testTag("camera_shutter_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    when (hudMode) {
                                        ArHudMode.CIVIC_GEO_AR -> NeonCyan
                                        ArHudMode.OBJECT_SCANNER -> WarmAmber
                                        ArHudMode.THERMAL_NIGHT_HUD -> Color(0xFF22C55E)
                                    }
                                )
                        )
                    }

                    // Connected App: Attach to Mailbox
                    IconButton(
                        onClick = {
                            onNavigateToMailbox()
                        }
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF1E293B),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Mail, contentDescription = "Mailbox Dispatch", tint = NeonCyan, modifier = Modifier.size(22.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tap Shutter to capture AR annotated dispatch photo",
                    fontSize = 11.sp,
                    color = DarkTextSecondary
                )
            }
        }

        // Captured Photo Dialog / Interconnection Sheet
        if (capturedPhotoDialog) {
            AlertDialog(
                onDismissRequest = { capturedPhotoDialog = false },
                containerColor = DarkSurface,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = NeonCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AR Dispatch Captured!", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Snapshot taken with ${hudMode.name} spatial telemetry overlay.",
                            fontSize = 13.sp,
                            color = DarkTextSecondary
                        )

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DarkSurfaceVariant,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Metadata: HDG $headingDegrees° • ALT ${altitudeMeters}m • Filter: ${filterMode.name}", fontSize = 10.sp, color = NeonCyan, fontFamily = FontFamily.Monospace)
                                Text("Location: Civic Center Plaza / Line 3 Junction", fontSize = 10.sp, color = DarkTextSecondary)
                            }
                        }

                        Text("Where would you like to send this photo?", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)

                        FilledTonalButton(
                            onClick = {
                                capturedPhotoDialog = false
                                onNavigateToMailbox()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = NeonCyan.copy(alpha = 0.2f), contentColor = NeonCyan)
                        ) {
                            Icon(Icons.Default.Mail, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Send to Editor's Mailbox")
                        }

                        FilledTonalButton(
                            onClick = {
                                capturedPhotoDialog = false
                                onNavigateToMarketplace()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = WarmAmber.copy(alpha = 0.2f), contentColor = WarmAmber)
                        ) {
                            Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Search in Townsquare Marketplace")
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { capturedPhotoDialog = false }) {
                        Text("Done", color = NeonCyan)
                    }
                }
            )
        }
    }
}
