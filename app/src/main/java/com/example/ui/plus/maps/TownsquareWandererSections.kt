package com.example.ui.plus.maps

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.plus.model.MapLocationSpot
import com.example.ui.plus.model.SpotCategory
import com.example.ui.theme.*

// =========================================================================
// 1. PALAPA VACATIONING SECTION
// =========================================================================
@Composable
fun PalapaVacationingView(
    spots: List<MapLocationSpot>,
    onAddToItinerary: (MapLocationSpot) -> Unit,
    modifier: Modifier = Modifier
) {
    val palapaSpots = remember(spots) { spots.filter { it.category == SpotCategory.PALAPA } }
    var selectedFilter by remember { mutableStateOf("ALL") }
    var reservedSpotName by remember { mutableStateOf<String?>(null) }
    var playingAudioSpotId by remember { mutableStateOf<String?>(null) }

    val filteredSpots = remember(palapaSpots, selectedFilter) {
        when (selectedFilter) {
            "BEACHFRONT" -> palapaSpots.filter { it.tags.any { t -> t.contains("Beachfront", ignoreCase = true) } }
            "OVERWATER" -> palapaSpots.filter { it.tags.any { t -> t.contains("Overwater", ignoreCase = true) || t.contains("Lagoon", ignoreCase = true) } }
            "SNORKEL" -> palapaSpots.filter { it.tags.any { t -> t.contains("Snorkel", ignoreCase = true) } }
            "SUNSET" -> palapaSpots.filter { it.tags.any { t -> t.contains("Sunset", ignoreCase = true) } }
            else -> palapaSpots
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Hero Card
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF003544),
                border = BorderStroke(1.dp, MintTeal.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MintTeal.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🏖️", fontSize = 18.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Palapa Vacationing & Cabana Resorts",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Thatched seaside lounges, lagoon daybeds & tropical retreats",
                                style = MaterialTheme.typography.labelSmall,
                                color = MintTeal
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Live Beach & Tide Telemetry
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0A222D),
                        border = BorderStroke(1.dp, MintTeal.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🌊 Water Temp", fontSize = 10.sp, color = DarkTextSecondary)
                                Text("78°F", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MintTeal)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🌅 Sunset", fontSize = 10.sp, color = DarkTextSecondary)
                                Text("6:48 PM", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WarmAmber)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🌴 Sea Breeze", fontSize = 10.sp, color = DarkTextSecondary)
                                Text("8 kts Gentle", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("☀️ UV Index", fontSize = 10.sp, color = DarkTextSecondary)
                                Text("6 Mod", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CoralRed)
                            }
                        }
                    }
                }
            }
        }

        // Filter Pills
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val filters = listOf(
                    "ALL" to "🌴 All Palapas (${palapaSpots.size})",
                    "BEACHFRONT" to "🏖️ Beachfront",
                    "OVERWATER" to "🛖 Overwater Cabanas",
                    "SNORKEL" to "🤿 Snorkeling Reefs",
                    "SUNSET" to "🍹 Sunset Loungers"
                )
                items(filters) { (key, label) ->
                    val isSelected = selectedFilter == key
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) MintTeal else DarkSurfaceElevated,
                        border = BorderStroke(1.dp, if (isSelected) MintTeal else DarkBorder),
                        modifier = Modifier.clickable { selectedFilter = key }
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color(0xFF003544) else Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Spot Cards
        items(filteredSpots, key = { it.id }) { spot ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkSurfaceElevated,
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // Image & Badges
                    Box(modifier = Modifier.fillMaxWidth().height(170.dp)) {
                        AsyncImage(
                            model = spot.photoUrl,
                            contentDescription = spot.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                                    )
                                )
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                                .align(Alignment.TopStart),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MintTeal
                            ) {
                                Text(
                                    text = "PALAPA RETREAT",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF003544),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.Black.copy(alpha = 0.7f),
                                border = BorderStroke(1.dp, WarmAmber)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = WarmAmber, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "${spot.rating} (${spot.reviewsCount})",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                                .align(Alignment.BottomStart)
                        ) {
                            Text(
                                text = spot.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = spot.address, fontSize = 11.sp, color = DarkTextSecondary)
                            }
                        }
                    }

                    // Details
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = spot.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFD1D5DB)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Tags
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(spot.tags) { tag ->
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = DarkSurfaceVariant,
                                    border = BorderStroke(1.dp, DarkBorder)
                                ) {
                                    Text(
                                        text = "#$tag",
                                        fontSize = 10.sp,
                                        color = MintTeal,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Audio guide preview button
                            spot.audioGuideDuration?.let { duration ->
                                val isPlaying = playingAudioSpotId == spot.id
                                OutlinedButton(
                                    onClick = {
                                        playingAudioSpotId = if (isPlaying) null else spot.id
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, if (isPlaying) MintTeal else DarkBorder),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.VolumeUp else Icons.Default.Headphones,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = if (isPlaying) MintTeal else Color.White
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isPlaying) "Playing..." else duration,
                                        fontSize = 10.sp,
                                        color = if (isPlaying) MintTeal else Color.White
                                    )
                                }
                            }

                            // Day Pass Reservation
                            Button(
                                onClick = { reservedSpotName = spot.name },
                                colors = ButtonDefaults.buttonColors(containerColor = MintTeal, contentColor = Color(0xFF003544)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1.2f)
                            ) {
                                Icon(Icons.Default.BeachAccess, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Reserve Palapa", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            // Add to Itinerary
                            IconButton(
                                onClick = { onAddToItinerary(spot) },
                                modifier = Modifier.background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
                            ) {
                                Icon(Icons.Default.AddLocationAlt, contentDescription = "Add to Route", tint = NeonCyan)
                            }
                        }
                    }
                }
            }
        }
    }

    // Reservation Confirmation Dialog
    reservedSpotName?.let { spotName ->
        AlertDialog(
            onDismissRequest = { reservedSpotName = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MintTeal)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Palapa Day Pass Reserved!", color = Color.White)
                }
            },
            text = {
                Text(
                    text = "Your private beachfront palapa lounger at $spotName has been confirmed for today! Present your digital Townsquare Pass upon arrival for complimentary towel service and chilled coconut water.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = { reservedSpotName = null },
                    colors = ButtonDefaults.buttonColors(containerColor = MintTeal, contentColor = Color(0xFF003544))
                ) {
                    Text("Enjoy Your Vacation", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

// =========================================================================
// 2. REST AREA & TOURIST TRAPS SECTION
// =========================================================================
@Composable
fun RestAreaTrapsView(
    spots: List<MapLocationSpot>,
    onAddToItinerary: (MapLocationSpot) -> Unit,
    modifier: Modifier = Modifier
) {
    val restSpots = remember(spots) { spots.filter { it.category == SpotCategory.REST_AREA } }
    var selectedFilter by remember { mutableStateOf("ALL") }
    var stampedTrapId by remember { mutableStateOf<String?>(null) }
    var passportStamps by remember { mutableStateOf(setOf<String>()) }

    val filteredSpots = remember(restSpots, selectedFilter) {
        when (selectedFilter) {
            "DINER" -> restSpots.filter { it.tags.any { t -> t.contains("Diner", ignoreCase = true) } }
            "EV" -> restSpots.filter { it.tags.any { t -> t.contains("EV", ignoreCase = true) || t.contains("Rest Stop", ignoreCase = true) } }
            "TRAP" -> restSpots.filter { it.tags.any { t -> t.contains("Tourist Trap", ignoreCase = true) || t.contains("Curio", ignoreCase = true) } }
            else -> restSpots
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Hero Card
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF261800),
                border = BorderStroke(1.dp, WarmAmber.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = WarmAmber.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("⛽", fontSize = 18.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Highway Rest Areas & Tourist Traps",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "24/7 Neon diners, EV superchargers & quirky roadside oddities",
                                style = MaterialTheme.typography.labelSmall,
                                color = WarmAmber
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Highway Travel Telemetry
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF191104),
                        border = BorderStroke(1.dp, WarmAmber.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("⚡ EV Chargers", fontSize = 10.sp, color = DarkTextSecondary)
                                Text("8 Available", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MintTeal)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("☕ Trucker Coffee", fontSize = 10.sp, color = DarkTextSecondary)
                                Text("Fresh Brewed", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = WarmAmber)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🛣️ Road Condition", fontSize = 10.sp, color = DarkTextSecondary)
                                Text("Open & Clear", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🏷️ Passport Stamps", fontSize = 10.sp, color = DarkTextSecondary)
                                Text("${passportStamps.size} Collected", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CoralRed)
                            }
                        }
                    }
                }
            }
        }

        // Filter Pills
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val filters = listOf(
                    "ALL" to "⛽ All Stops (${restSpots.size})",
                    "DINER" to "🍳 24/7 Neon Diners",
                    "EV" to "⚡ EV Superchargers",
                    "TRAP" to "🦘 Roadside Oddities & Traps"
                )
                items(filters) { (key, label) ->
                    val isSelected = selectedFilter == key
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) WarmAmber else DarkSurfaceElevated,
                        border = BorderStroke(1.dp, if (isSelected) WarmAmber else DarkBorder),
                        modifier = Modifier.clickable { selectedFilter = key }
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color(0xFF261800) else Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Spot Cards
        items(filteredSpots, key = { it.id }) { spot ->
            val isStamped = passportStamps.contains(spot.id)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkSurfaceElevated,
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // Image & Badges
                    Box(modifier = Modifier.fillMaxWidth().height(170.dp)) {
                        AsyncImage(
                            model = spot.photoUrl,
                            contentDescription = spot.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                                    )
                                )
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                                .align(Alignment.TopStart),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = WarmAmber
                            ) {
                                Text(
                                    text = if (spot.tags.any { it.contains("Trap", ignoreCase = true) }) "TOURIST TRAP" else "HIGHWAY REST STOP",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF261800),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            if (isStamped) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = CoralRed
                                ) {
                                    Text(
                                        text = "PASSPORT STAMPED ✓",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                                .align(Alignment.BottomStart)
                        ) {
                            Text(
                                text = spot.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocalGasStation, contentDescription = null, tint = WarmAmber, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = spot.address, fontSize = 11.sp, color = DarkTextSecondary)
                            }
                        }
                    }

                    // Details
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = spot.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFD1D5DB)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Tags
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(spot.tags) { tag ->
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = DarkSurfaceVariant,
                                    border = BorderStroke(1.dp, DarkBorder)
                                ) {
                                    Text(
                                        text = "#$tag",
                                        fontSize = 10.sp,
                                        color = WarmAmber,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Stamp Roadside Passport Button
                            Button(
                                onClick = {
                                    passportStamps = passportStamps + spot.id
                                    stampedTrapId = spot.name
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isStamped) DarkSurfaceVariant else WarmAmber,
                                    contentColor = if (isStamped) Color.White else Color(0xFF261800)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1.4f)
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isStamped) "Stamped in Passport" else "Stamp Passport", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            // Add to Itinerary
                            Button(
                                onClick = { onAddToItinerary(spot) },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1.2f)
                            ) {
                                Icon(Icons.Default.AddLocationAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add to Route", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Stamp Confirmation Dialog
    stampedTrapId?.let { trapName ->
        AlertDialog(
            onDismissRequest = { stampedTrapId = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Stars, contentDescription = null, tint = WarmAmber)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Roadside Passport Stamped!", color = Color.White)
                }
            },
            text = {
                Text(
                    text = "Congratulations! You earned the official Townsquare Wanderer Roadside Stamp for visiting '$trapName'. Collect all highway oddity stamps to unlock the Master Roadtripper badge.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = { stampedTrapId = null },
                    colors = ButtonDefaults.buttonColors(containerColor = WarmAmber, contentColor = Color(0xFF261800))
                ) {
                    Text("Keep Exploring", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
