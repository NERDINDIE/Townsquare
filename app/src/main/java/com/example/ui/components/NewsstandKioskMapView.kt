package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.TurnRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RetailKioskEntity
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber
import kotlin.math.sqrt

@Composable
fun NewsstandKioskMapView(
    retailKiosks: List<RetailKioskEntity>,
    followedPublications: Set<String>,
    onReserveCopy: (RetailKioskEntity, Int, () -> Unit) -> Unit,
    onToggleFavorite: (RetailKioskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    var selectedKiosk by remember { mutableStateOf<RetailKioskEntity?>(retailKiosks.firstOrNull()) }
    var filterFollowedOnly by remember { mutableStateOf(false) }
    var filterInStockOnly by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showReservationDialog by remember { mutableStateOf<RetailKioskEntity?>(null) }
    var showRouteDialog by remember { mutableStateOf<RetailKioskEntity?>(null) }
    var reservationSuccessMessage by remember { mutableStateOf<String?>(null) }

    // Map Pan and Zoom State
    var panOffsetX by remember { mutableFloatStateOf(0f) }
    var panOffsetY by remember { mutableFloatStateOf(0f) }
    var zoomScale by remember { mutableFloatStateOf(1.0f) }

    // User location on the town map
    val userMapPos = Offset(0.48f, 0.44f)

    // Filter kiosks
    val filteredKiosks = remember(retailKiosks, filterFollowedOnly, filterInStockOnly, searchQuery, followedPublications) {
        val q = searchQuery.trim().lowercase()
        retailKiosks.filter { kiosk ->
            val carriesFollowed = followedPublications.isEmpty() || followedPublications.any { pub ->
                kiosk.carriedPublicationTitles.contains(pub, ignoreCase = true)
            }
            val matchesFollowed = !filterFollowedOnly || carriesFollowed
            val matchesStock = !filterInStockOnly || kiosk.availableCopies > 0
            val matchesQuery = q.isEmpty() || kiosk.name.lowercase().contains(q) ||
                    kiosk.district.lowercase().contains(q) ||
                    kiosk.carriedPublicationTitles.lowercase().contains(q)
            matchesFollowed && matchesStock && matchesQuery
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("newsstand_kiosk_map_view"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Map Masthead
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161C26)),
                border = BorderStroke(1.dp, Color(0xFF263346))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = WarmAmber.copy(alpha = 0.15f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Store,
                                        contentDescription = null,
                                        tint = WarmAmber,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Town Kiosks & Press Map",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "Physical stores carrying your followed broadsheets & magazines",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1F2B3E),
                            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "📍 Civic Plaza",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = NeonCyan,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Search box
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("kiosk_search_input"),
                        placeholder = { Text("Search kiosks, districts, or publication titles...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan) },
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Filter Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = filterFollowedOnly,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                filterFollowedOnly = !filterFollowedOnly
                            },
                            label = {
                                Text("⭐ My Followed Prints", style = MaterialTheme.typography.labelSmall)
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = WarmAmber,
                                selectedLabelColor = Color(0xFF003544)
                            ),
                            modifier = Modifier.testTag("filter_kiosk_followed")
                        )

                        FilterChip(
                            selected = filterInStockOnly,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                filterInStockOnly = !filterInStockOnly
                            },
                            label = {
                                Text("🟢 In Stock Only", style = MaterialTheme.typography.labelSmall)
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonCyan,
                                selectedLabelColor = Color(0xFF003544)
                            ),
                            modifier = Modifier.testTag("filter_kiosk_instock")
                        )
                    }
                }
            }
        }

        // Interactive Map Canvas Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .testTag("interactive_city_map"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F141C)),
                border = BorderStroke(1.dp, Color(0xFF263346))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Custom Canvas City Map
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    panOffsetX = (panOffsetX + dragAmount.x).coerceIn(-180f, 180f)
                                    panOffsetY = (panOffsetY + dragAmount.y).coerceIn(-180f, 180f)
                                }
                            }
                            .pointerInput(filteredKiosks, zoomScale, panOffsetX, panOffsetY) {
                                detectTapGestures { tapOffset ->
                                    // Hit-test kiosk markers
                                    val w = size.width
                                    val h = size.height
                                    val centerX = w / 2f + panOffsetX
                                    val centerY = h / 2f + panOffsetY

                                    for (kiosk in filteredKiosks) {
                                        val kx = centerX + (kiosk.mapX - 0.5f) * w * 0.9f * zoomScale
                                        val ky = centerY + (kiosk.mapY - 0.5f) * h * 0.9f * zoomScale
                                        val dist = sqrt((tapOffset.x - kx) * (tapOffset.x - kx) + (tapOffset.y - ky) * (tapOffset.y - ky))
                                        if (dist < 40f) {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            selectedKiosk = kiosk
                                            break
                                        }
                                    }
                                }
                            }
                    ) {
                        val w = size.width
                        val h = size.height
                        val centerX = w / 2f + panOffsetX
                        val centerY = h / 2f + panOffsetY

                        // 1. Draw Town River
                        val riverPath = Path().apply {
                            val rStart = Offset(centerX - w * 0.55f * zoomScale, centerY + h * 0.45f * zoomScale)
                            moveTo(rStart.x, rStart.y)
                            cubicTo(
                                centerX - w * 0.2f * zoomScale, centerY + h * 0.15f * zoomScale,
                                centerX + w * 0.1f * zoomScale, centerY + h * 0.4f * zoomScale,
                                centerX + w * 0.55f * zoomScale, centerY + h * 0.1f * zoomScale
                            )
                        }
                        drawPath(
                            path = riverPath,
                            color = Color(0xFF0F364E),
                            style = Stroke(width = 28f * zoomScale)
                        )

                        // 2. Draw District Green Parks
                        drawCircle(
                            color = Color(0xFF143026),
                            radius = 45f * zoomScale,
                            center = Offset(centerX - w * 0.25f * zoomScale, centerY - h * 0.22f * zoomScale)
                        )
                        drawCircle(
                            color = Color(0xFF143026),
                            radius = 55f * zoomScale,
                            center = Offset(centerX, centerY - 10f)
                        )

                        // 3. Draw Street Grid Lines
                        val gridPaint = Color(0xFF1C2534)
                        val gridStroke = Stroke(width = 2.5f * zoomScale)
                        for (i in -3..3) {
                            val y = centerY + i * 50f * zoomScale
                            drawLine(gridPaint, Offset(0f, y), Offset(w, y), strokeWidth = 2f)
                            val x = centerX + i * 65f * zoomScale
                            drawLine(gridPaint, Offset(x, 0f), Offset(x, h), strokeWidth = 2f)
                        }

                        // 4. Draw Transit Metro Line
                        val metroPath = Path().apply {
                            moveTo(centerX - w * 0.45f * zoomScale, centerY + h * 0.25f * zoomScale)
                            lineTo(centerX, centerY - 10f)
                            lineTo(centerX + w * 0.42f * zoomScale, centerY - h * 0.25f * zoomScale)
                        }
                        drawPath(
                            path = metroPath,
                            color = Color(0xFFFF9F1C).copy(alpha = 0.4f),
                            style = Stroke(width = 3.5f * zoomScale, pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f))
                        )

                        // 5. Draw Walking Route if kiosk is selected
                        selectedKiosk?.let { kiosk ->
                            val ux = centerX + (userMapPos.x - 0.5f) * w * 0.9f * zoomScale
                            val uy = centerY + (userMapPos.y - 0.5f) * h * 0.9f * zoomScale
                            val kx = centerX + (kiosk.mapX - 0.5f) * w * 0.9f * zoomScale
                            val ky = centerY + (kiosk.mapY - 0.5f) * h * 0.9f * zoomScale

                            val routePath = Path().apply {
                                moveTo(ux, uy)
                                lineTo((ux + kx) / 2f, uy)
                                lineTo(kx, ky)
                            }
                            drawPath(
                                path = routePath,
                                color = NeonCyan,
                                style = Stroke(
                                    width = 3.5f * zoomScale,
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)
                                )
                            )
                        }

                        // 6. Draw User Location Pin
                        val ux = centerX + (userMapPos.x - 0.5f) * w * 0.9f * zoomScale
                        val uy = centerY + (userMapPos.y - 0.5f) * h * 0.9f * zoomScale
                        drawCircle(
                            color = NeonCyan.copy(alpha = 0.25f),
                            radius = 20f * zoomScale,
                            center = Offset(ux, uy)
                        )
                        drawCircle(
                            color = NeonCyan,
                            radius = 8f * zoomScale,
                            center = Offset(ux, uy)
                        )

                        // 7. Draw Kiosk Pins
                        for (kiosk in filteredKiosks) {
                            val kx = centerX + (kiosk.mapX - 0.5f) * w * 0.9f * zoomScale
                            val ky = centerY + (kiosk.mapY - 0.5f) * h * 0.9f * zoomScale
                            val isSelected = selectedKiosk?.id == kiosk.id
                            val carriesFollowed = followedPublications.any { kiosk.carriedPublicationTitles.contains(it, ignoreCase = true) }

                            // Glow halo if selected
                            if (isSelected) {
                                drawCircle(
                                    color = if (carriesFollowed) WarmAmber.copy(alpha = 0.45f) else NeonCyan.copy(alpha = 0.45f),
                                    radius = 24f * zoomScale,
                                    center = Offset(kx, ky)
                                )
                            }

                            // Pin body
                            val pinColor = when {
                                carriesFollowed -> WarmAmber
                                kiosk.availableCopies >= 10 -> Color(0xFF10B981)
                                else -> Color(0xFFEAB308)
                            }
                            drawCircle(
                                color = pinColor,
                                radius = (if (isSelected) 14f else 10f) * zoomScale,
                                center = Offset(kx, ky)
                            )
                            drawCircle(
                                color = Color(0xFF0F141C),
                                radius = (if (isSelected) 6f else 4f) * zoomScale,
                                center = Offset(kx, ky)
                            )
                        }
                    }

                    // Map Overlay: Compass and Recenter / Zoom Controls
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF1E2634),
                            border = BorderStroke(1.dp, Color(0xFF334155)),
                            modifier = Modifier.size(34.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    panOffsetX = 0f
                                    panOffsetY = 0f
                                    zoomScale = 1.0f
                                },
                                modifier = Modifier.testTag("map_recenter_button")
                            ) {
                                Icon(Icons.Default.MyLocation, contentDescription = "Recenter", tint = NeonCyan, modifier = Modifier.size(18.dp))
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF1E2634),
                            border = BorderStroke(1.dp, Color(0xFF334155)),
                            modifier = Modifier.size(34.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    zoomScale = (zoomScale + 0.25f).coerceAtMost(2.0f)
                                },
                                modifier = Modifier.testTag("map_zoom_in_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF1E2634),
                            border = BorderStroke(1.dp, Color(0xFF334155)),
                            modifier = Modifier.size(34.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    zoomScale = (zoomScale - 0.25f).coerceAtLeast(0.75f)
                                },
                                modifier = Modifier.testTag("map_zoom_out_button")
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    // Bottom Map Legend
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xCC0F141C),
                        border = BorderStroke(1.dp, Color(0xFF263346)),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(WarmAmber))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Followed", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = Color.White)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF10B981)))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("In Stock", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = Color.White)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(NeonCyan))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("You", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Reservation Success Banner
        reservationSuccessMessage?.let { msg ->
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF0F382E),
                    border = BorderStroke(1.dp, Color(0xFF10B981)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = msg, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        }
                        Text(
                            text = "Dismiss",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF10B981),
                            modifier = Modifier.clickable { reservationSuccessMessage = null }
                        )
                    }
                }
            }
        }

        // Selected Kiosk Detail Card
        selectedKiosk?.let { kiosk ->
            item {
                SelectedKioskDetailCard(
                    kiosk = kiosk,
                    followedPublications = followedPublications,
                    onReserveClick = { showReservationDialog = kiosk },
                    onRouteClick = { showRouteDialog = kiosk },
                    onToggleFavorite = { onToggleFavorite(kiosk) }
                )
            }
        }

        // All Kiosks Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RETAIL KIOSKS IN TOWNSQUARE (${filteredKiosks.size})",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Tap pin or card to inspect stock",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonCyan
                )
            }
        }

        // Kiosks horizontal or vertical listing
        items(filteredKiosks, key = { it.id }) { kiosk ->
            KioskListItemCard(
                kiosk = kiosk,
                isSelected = selectedKiosk?.id == kiosk.id,
                followedPublications = followedPublications,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    selectedKiosk = kiosk
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Copy Reservation Sheet / Dialog
    showReservationDialog?.let { kiosk ->
        AlertDialog(
            onDismissRequest = { showReservationDialog = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎟️", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reserve Copy at Kiosk", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Reserve a physical print edition at ${kiosk.name}.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF18202D),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "AVAILABLE TITLES AT STAND:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = WarmAmber
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            kiosk.carriedPublicationTitles.split(",").forEach { title ->
                                val trimmed = title.trim()
                                val isFollowed = followedPublications.any { trimmed.contains(it, ignoreCase = true) }
                                Row(
                                    modifier = Modifier.padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isFollowed) "⭐ $trimmed" else "• $trimmed",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = if (isFollowed) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (isFollowed) WarmAmber else Color.White
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "A copy will be set aside under your name until 8:00 PM today. Free reservation.",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonCyan
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onReserveCopy(kiosk, 1) {
                            reservationSuccessMessage = "1 print copy held at ${kiosk.name}! Reservation token #TQ-${(1000..9999).random()}"
                            showReservationDialog = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                    modifier = Modifier.testTag("btn_confirm_reserve")
                ) {
                    Text("Confirm Hold")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReservationDialog = null }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    // Walking Route Guidance Dialog
    showRouteDialog?.let { kiosk ->
        AlertDialog(
            onDismissRequest = { showRouteDialog = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🚶", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Walking Route to Kiosk", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "${kiosk.distanceMiles} miles • ${kiosk.walkingMinutes} min walk from Civic Plaza",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = NeonCyan
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = kiosk.address, style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(12.dp))

                    val steps = listOf(
                        "1. Head north from Civic Plaza Fountain toward North Arcade.",
                        "2. Continue along the pedestrian corridor past Town Hall (300 ft).",
                        "3. Cross 4th Avenue at the designated green zebra crosswalk.",
                        "4. Arrive at ${kiosk.name} on your right."
                    )
                    steps.forEach { step ->
                        Text(
                            text = step,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFD1D5DB),
                            modifier = Modifier.padding(vertical = 3.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showRouteDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544))
                ) {
                    Text("Got It")
                }
            }
        )
    }
}

@Composable
fun SelectedKioskDetailCard(
    kiosk: RetailKioskEntity,
    followedPublications: Set<String>,
    onReserveClick: () -> Unit,
    onRouteClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("selected_kiosk_card_${kiosk.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF171D27)),
        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Title, Favorite, Type
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = kiosk.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                    Text(
                        text = "${kiosk.type} • ${kiosk.district}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonCyan
                    )
                }

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.testTag("btn_favorite_kiosk_${kiosk.id}")
                ) {
                    Icon(
                        imageVector = if (kiosk.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (kiosk.isFavorite) WarmAmber else Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Address, Distance, Hours
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF0F2634)
                ) {
                    Text(
                        text = "🚶 ${kiosk.distanceMiles} mi (${kiosk.walkingMinutes} min)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = NeonCyan,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF122C20)
                ) {
                    Text(
                        text = kiosk.openingHours,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF10B981),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "📍 ${kiosk.address}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Publications carried with followed status
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF1E2634),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "PRINT TITLES AVAILABLE HERE:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val titles = kiosk.carriedPublicationTitles.split(",").map { it.trim() }
                    titles.forEach { title ->
                        val isFollowed = followedPublications.any { title.contains(it, ignoreCase = true) }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isFollowed) "⭐ $title" else "• $title",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = if (isFollowed) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isFollowed) WarmAmber else Color.White
                                )
                                if (isFollowed) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = WarmAmber.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "Followed",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                            color = WarmAmber,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "In Stock",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = Color(0xFF10B981)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = kiosk.stockStatus,
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons: Reserve Copy & Walking Route
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onRouteClick,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_kiosk_route"),
                    border = BorderStroke(1.dp, Color(0xFF2E3D52))
                ) {
                    Icon(Icons.Default.DirectionsWalk, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Walking Route", style = MaterialTheme.typography.labelMedium)
                }

                Button(
                    onClick = onReserveClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_reserve_kiosk_copy")
                ) {
                    Text("Reserve Copy", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}

@Composable
fun KioskListItemCard(
    kiosk: RetailKioskEntity,
    isSelected: Boolean,
    followedPublications: Set<String>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val carriesFollowed = followedPublications.any { kiosk.carriedPublicationTitles.contains(it, ignoreCase = true) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("kiosk_list_item_${kiosk.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF1B2332) else Color(0xFF151922)
        ),
        border = BorderStroke(
            1.dp,
            if (isSelected) NeonCyan else Color(0xFF222B3A)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (carriesFollowed) WarmAmber.copy(alpha = 0.2f) else Color(0xFF1F2838),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Newspaper,
                        contentDescription = null,
                        tint = if (carriesFollowed) WarmAmber else NeonCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = kiosk.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    if (carriesFollowed) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "⭐", fontSize = 12.sp)
                    }
                }
                Text(
                    text = "${kiosk.district} • ${kiosk.distanceMiles} mi (${kiosk.walkingMinutes} min walk)",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonCyan
                )
                Text(
                    text = kiosk.stockStatus,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = Color.LightGray,
                    maxLines = 1
                )
            }

            if (kiosk.isFavorite) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = WarmAmber,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
