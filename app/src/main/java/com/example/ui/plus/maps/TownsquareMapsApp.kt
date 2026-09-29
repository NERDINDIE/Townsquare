package com.example.ui.plus.maps

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.plus.model.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TownsquareMapsApp(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTopTab by remember { mutableIntStateOf(0) } // 0: Interactive Map, 1: Travel Magazine, 2: Itinerary
    val mapSpots = remember { TownsquarePlusSeed.generateInitialMapSpots() }
    var selectedCategory by remember { mutableStateOf<SpotCategory?>(null) }
    var activeSpotId by remember { mutableStateOf(mapSpots.first().id) }
    
    // Magazine issues
    var magazineArticles by remember { mutableStateOf(TownsquarePlusSeed.generateTravelMagazineIssues()) }
    var activeMagazineArticle by remember { mutableStateOf(magazineArticles.first()) }
    var isGeneratorOpen by remember { mutableStateOf(false) }

    // Itinerary items
    var itineraryStops by remember {
        mutableStateOf(
            listOf(
                ItineraryStop(timeSlot = "09:00 AM", spotName = "Lantern Lane Espresso & Roasters", note = "Morning pour-over and reading the fresh daily dispatch."),
                ItineraryStop(timeSlot = "11:30 AM", spotName = "Grand Clocktower & Archives", note = "Explore historical town printing press collection."),
                ItineraryStop(timeSlot = "02:00 PM", spotName = "Market Hall & Guild Arcade", note = "Sample smoked canal trout and browse artisan ceramics."),
                ItineraryStop(timeSlot = "05:45 PM", spotName = "Harbor Maritime Pavilion & Pier", note = "Sunset stroll along the renovated Pier 14 boardwalk.")
            )
        )
    }

    val activeSpot = mapSpots.find { it.id == activeSpotId } ?: mapSpots.first()

    Box(modifier = modifier.fillMaxSize().background(DarkBg)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Surface(
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("maps_back_button")) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = NeonCyan
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = CircleShape,
                            color = MintTeal.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Map,
                                    contentDescription = null,
                                    tint = MintTeal,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Townsquare Maps & Magazine",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MintTeal
                                ) {
                                    Text(
                                        text = "PLUS",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF003544),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Interactive Cartography & Curated Travel Dispatches",
                                style = MaterialTheme.typography.labelSmall,
                                color = DarkTextSecondary
                            )
                        }
                    }

                    if (selectedTopTab == 1) {
                        Button(
                            onClick = { isGeneratorOpen = true },
                            colors = ButtonDefaults.buttonColors(containerColor = WarmAmber, contentColor = Color(0xFF261800)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("generate_magazine_button")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Personalize", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Tabs Selector
            TabRow(
                selectedTabIndex = selectedTopTab,
                containerColor = DarkSurfaceVariant,
                contentColor = NeonCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTopTab]),
                        color = NeonCyan
                    )
                }
            ) {
                Tab(
                    selected = selectedTopTab == 0,
                    onClick = { selectedTopTab = 0 },
                    icon = { Icon(Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    text = { Text("Interactive Map", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTopTab == 1,
                    onClick = { selectedTopTab = 1 },
                    icon = { Icon(Icons.Default.AutoStories, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    text = { Text("Travel Magazine", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTopTab == 2,
                    onClick = { selectedTopTab = 2 },
                    icon = { Icon(Icons.Default.Route, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    text = { Text("My Itinerary (${itineraryStops.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }

            // Tab Content
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTopTab) {
                    0 -> InteractiveMapView(
                        spots = mapSpots.filter { selectedCategory == null || it.category == selectedCategory },
                        allSpots = mapSpots,
                        selectedCategory = selectedCategory,
                        onSelectCategory = { selectedCategory = if (selectedCategory == it) null else it },
                        activeSpot = activeSpot,
                        onSelectSpot = { activeSpotId = it.id },
                        onAddToItinerary = { spot ->
                            val newStop = ItineraryStop(
                                timeSlot = "Flexible",
                                spotName = spot.name,
                                note = spot.description.take(70) + "..."
                            )
                            itineraryStops = itineraryStops + newStop
                            selectedTopTab = 2
                        }
                    )
                    1 -> TravelMagazineView(
                        articles = magazineArticles,
                        currentArticle = activeMagazineArticle,
                        onSelectArticle = { activeMagazineArticle = it },
                        onOpenGenerator = { isGeneratorOpen = true }
                    )
                    2 -> ItineraryPlannerView(
                        stops = itineraryStops,
                        onToggleComplete = { stopId ->
                            itineraryStops = itineraryStops.map {
                                if (it.id == stopId) it.copy(isCompleted = !it.isCompleted) else it
                            }
                        },
                        onDeleteStop = { stopId ->
                            itineraryStops = itineraryStops.filterNot { it.id == stopId }
                        },
                        onAddCustomStop = { name, time, note ->
                            val newStop = ItineraryStop(timeSlot = time, spotName = name, note = note)
                            itineraryStops = itineraryStops + newStop
                        }
                    )
                }
            }
        }

        // Personalized Magazine Generator Modal
        if (isGeneratorOpen) {
            PersonalizedMagazineGeneratorDialog(
                onDismiss = { isGeneratorOpen = false },
                onGenerate = { travelPersona, pace, theme ->
                    val customArticle = TravelMagazineArticle(
                        issueTitle = "Personalized Edition • $travelPersona",
                        editionNumber = "Custom Issue #${(100..999).random()}",
                        title = "$theme: Curated $travelPersona Route",
                        subtitle = "A customized $pace journey woven from Townsquare's real-time civic cartography.",
                        author = "Personalized Travel Concierge & AI Editorial Desk",
                        readTimeMinutes = 6,
                        heroImageUrl = "https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?auto=format&fit=crop&w=800&q=80",
                        contentParagraphs = listOf(
                            "Designed specifically for your preference as $travelPersona, this itinerary balances quiet contemplation with sensory delight. Beginning at first light, our route meanders past the old stone embankments before the city stirs.",
                            "Your chosen pace ($pace) allows for lingering over single-origin filter roasts and perusing vintage letterpress folios without watching the clock.",
                            "As afternoon shifts into twilight, follow the canal trail to our recommended sunset overlook for a quiet moment of reflection over the water."
                        ),
                        highlights = listOf(
                            "Top Recommendation: Start at Foundry Antiquarian Books for rare printing artifacts",
                            "Pacing Tip: Allow 45 minutes for peaceful courtyard espresso at Lantern Lane",
                            "Evening Grand Finale: Harbor Maritime Pier boardwalk at twilight"
                        ),
                        recommendedStopNames = listOf(
                            "Lantern Lane Espresso & Roasters",
                            "Foundry Antiquarian Books & Kiosk",
                            "Harbor Maritime Pavilion & Pier"
                        )
                    )
                    magazineArticles = listOf(customArticle) + magazineArticles
                    activeMagazineArticle = customArticle
                    isGeneratorOpen = false
                    selectedTopTab = 1
                }
            )
        }
    }
}

// -------------------------------------------------------------
// 1. INTERACTIVE MAP VIEW
// -------------------------------------------------------------
@Composable
private fun InteractiveMapView(
    spots: List<MapLocationSpot>,
    allSpots: List<MapLocationSpot>,
    selectedCategory: SpotCategory?,
    onSelectCategory: (SpotCategory) -> Unit,
    activeSpot: MapLocationSpot,
    onSelectSpot: (MapLocationSpot) -> Unit,
    onAddToItinerary: (MapLocationSpot) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Category Filter Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(SpotCategory.entries) { cat ->
                val isSelected = selectedCategory == cat
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) NeonCyan else DarkSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) NeonCyan else DarkBorder),
                    modifier = Modifier.clickable { onSelectCategory(cat) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = cat.emoji, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = cat.title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color(0xFF003544) else Color.White
                        )
                    }
                }
            }
        }

        // Map Canvas with Custom Vector Render & Location Pins
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFF0A121E))
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(spots) {
                        detectTapGestures { offset ->
                            // Find nearest spot to tap
                            val tappedSpot = spots.minByOrNull { spot ->
                                val spotX = spot.longitudeOffset * size.width
                                val spotY = spot.latitudeOffset * size.height
                                val dx = spotX - offset.x
                                val dy = spotY - offset.y
                                dx * dx + dy * dy
                            }
                            if (tappedSpot != null) {
                                onSelectSpot(tappedSpot)
                            }
                        }
                    }
            ) {
                val canvasW = size.width
                val canvasH = size.height

                // Draw Waterway / River & Harbor
                val riverPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(0f, canvasH * 0.7f)
                    cubicTo(
                        canvasW * 0.35f, canvasH * 0.65f,
                        canvasW * 0.65f, canvasH * 0.85f,
                        canvasW, canvasH * 0.75f
                    )
                    lineTo(canvasW, canvasH)
                    lineTo(0f, canvasH)
                    close()
                }
                drawPath(riverPath, color = Color(0xFF0B253B))

                // Draw Grid Roads
                val gridColor = Color(0xFF162338)
                for (i in 1..8) {
                    val y = (canvasH / 9f) * i
                    drawLine(gridColor, Offset(0f, y), Offset(canvasW, y), strokeWidth = 1.5f)
                }
                for (j in 1..6) {
                    val x = (canvasW / 7f) * j
                    drawLine(gridColor, Offset(x, 0f), Offset(x, canvasH), strokeWidth = 1.5f)
                }

                // Draw Main Arterial Boulevards
                drawLine(
                    color = Color(0xFF1E3555),
                    start = Offset(0f, canvasH * 0.38f),
                    end = Offset(canvasW, canvasH * 0.42f),
                    strokeWidth = 6f
                )
                drawLine(
                    color = Color(0xFF1E3555),
                    start = Offset(canvasW * 0.45f, 0f),
                    end = Offset(canvasW * 0.52f, canvasH),
                    strokeWidth = 6f
                )
            }

            // Overlay Interactive Pins
            spots.forEach { spot ->
                val isSelected = spot.id == activeSpot.id
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val pinX = maxWidth * spot.longitudeOffset - 20.dp
                        val pinY = maxHeight * spot.latitudeOffset - 24.dp

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .offset(x = pinX, y = pinY)
                                .clickable { onSelectSpot(spot) }
                                .testTag("map_pin_${spot.name}")
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) WarmAmber else DarkSurfaceElevated,
                                border = androidx.compose.foundation.BorderStroke(
                                    2.dp,
                                    if (isSelected) Color.White else NeonCyan
                                ),
                                shadowElevation = if (isSelected) 8.dp else 2.dp,
                                modifier = Modifier.size(if (isSelected) 40.dp else 32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = spot.category.emoji,
                                        fontSize = if (isSelected) 18.sp else 14.sp
                                    )
                                }
                            }
                            if (isSelected) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color.Black.copy(alpha = 0.85f),
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    Text(
                                        text = spot.name,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                        color = WarmAmber,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Map HUD overlay (Compass & Active Count)
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DarkSurface.copy(alpha = 0.9f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Navigation, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("GRID N", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                    }
                }
            }
        }

        // Bottom Selected Spot Card
        Surface(
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    AsyncImage(
                        model = activeSpot.photoUrl,
                        contentDescription = activeSpot.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkBorder)
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = activeSpot.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = WarmAmber, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${activeSpot.rating}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                            }
                        }

                        Text(
                            text = "${activeSpot.category.title} • ${activeSpot.address}",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonCyan
                        )

                        Text(
                            text = activeSpot.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkTextSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (activeSpot.audioGuideDuration != null) {
                        OutlinedButton(
                            onClick = { /* audio guide simulation */ },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Headphones, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(activeSpot.audioGuideDuration!!, color = NeonCyan, fontSize = 12.sp)
                        }
                    }

                    Button(
                        onClick = { onAddToItinerary(activeSpot) },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).testTag("add_spot_to_itinerary_btn")
                    ) {
                        Icon(Icons.Default.AddLocationAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add to Itinerary", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. TRAVEL MAGAZINE VIEW
// -------------------------------------------------------------
@Composable
private fun TravelMagazineView(
    articles: List<TravelMagazineArticle>,
    currentArticle: TravelMagazineArticle,
    onSelectArticle: (TravelMagazineArticle) -> Unit,
    onOpenGenerator: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Issue Header Pill
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, WarmAmber.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = currentArticle.issueTitle.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                            color = WarmAmber
                        )
                        Text(
                            text = currentArticle.editionNumber,
                            style = MaterialTheme.typography.labelSmall,
                            color = DarkTextSecondary
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = WarmAmber.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "${currentArticle.readTimeMinutes} MIN DISPATCH",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarmAmber,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Magazine Cover Hero
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                AsyncImage(
                    model = currentArticle.heroImageUrl,
                    contentDescription = currentArticle.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xFF0A0E17).copy(alpha = 0.95f)),
                                startY = 100f
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = currentArticle.title,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "By ${currentArticle.author}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonCyan
                    )
                }
            }
        }

        // Article Subtitle & Pullquote
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "\"${currentArticle.subtitle}\"",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold, lineHeight = 24.sp),
                        color = WarmAmber
                    )
                }
            }
        }

        // Article Body Paragraphs
        items(currentArticle.contentParagraphs) { para ->
            Text(
                text = para,
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp),
                color = Color.White
            )
        }

        // Curated Highlights Checklist
        if (currentArticle.highlights.isNotEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = DarkSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MintTeal.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = MintTeal, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "LOCAL SECRETS & HIGHLIGHTS",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                color = MintTeal
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        currentArticle.highlights.forEach { h ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text("• ", color = MintTeal, fontWeight = FontWeight.Bold)
                                Text(text = h, style = MaterialTheme.typography.bodySmall, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Recommended Stops Pills
        if (currentArticle.recommendedStopNames.isNotEmpty()) {
            item {
                Text(
                    text = "FEATURED IN THIS ISSUE",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                    color = DarkTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(currentArticle.recommendedStopNames) { stop ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Place, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = stop, fontSize = 12.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Switch to other issues
        item {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = DarkBorder)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "MORE MAGAZINE ISSUES",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = DarkTextSecondary
            )
        }

        items(articles.filter { it.id != currentArticle.id }) { other ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectArticle(other) }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = other.heroImageUrl,
                        contentDescription = other.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = other.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                        Text(text = other.issueTitle, fontSize = 11.sp, color = NeonCyan)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = DarkTextMuted)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. ITINERARY PLANNER VIEW
// -------------------------------------------------------------
@Composable
private fun ItineraryPlannerView(
    stops: List<ItineraryStop>,
    onToggleComplete: (String) -> Unit,
    onDeleteStop: (String) -> Unit,
    onAddCustomStop: (name: String, time: String, note: String) -> Unit
) {
    var isAddStopOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "PERSONALIZED DAY PLAN",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                    color = NeonCyan
                )
                val completed = stops.count { it.isCompleted }
                Text(
                    text = "$completed of ${stops.size} stops visited",
                    style = MaterialTheme.typography.labelSmall,
                    color = DarkTextSecondary
                )
            }

            Button(
                onClick = { isAddStopOpen = true },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Stop", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (stops.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Route, contentDescription = null, tint = DarkTextMuted, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Your itinerary is empty.", color = DarkTextSecondary)
                    Text("Browse the map to add curated locations!", color = DarkTextMuted, fontSize = 12.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(stops, key = { it.id }) { stop ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (stop.isCompleted) DarkSurfaceVariant.copy(alpha = 0.6f) else DarkSurface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (stop.isCompleted) MintTeal.copy(alpha = 0.5f) else DarkBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { onToggleComplete(stop.id) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = if (stop.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = "Toggle Complete",
                                        tint = if (stop.isCompleted) MintTeal else DarkTextMuted
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = NeonCyan.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = stop.timeSlot,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NeonCyan,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = stop.spotName,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            textDecoration = if (stop.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                                        ),
                                        color = if (stop.isCompleted) DarkTextSecondary else Color.White
                                    )
                                    Text(
                                        text = stop.note,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = DarkTextMuted,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            IconButton(
                                onClick = { onDeleteStop(stop.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Remove", tint = CoralRed, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        // Add Stop Dialog
        if (isAddStopOpen) {
            var name by remember { mutableStateOf("") }
            var time by remember { mutableStateOf("02:00 PM") }
            var note by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { isAddStopOpen = false },
                title = { Text("Add Custom Itinerary Stop", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Stop / Destination Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = time,
                            onValueChange = { time = it },
                            label = { Text("Time Slot") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            label = { Text("Notes & Objectives") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onAddCustomStop(name, time, note)
                                isAddStopOpen = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544))
                    ) {
                        Text("Add to Route", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isAddStopOpen = false }) {
                        Text("Cancel", color = DarkTextSecondary)
                    }
                }
            )
        }
    }
}

// -------------------------------------------------------------
// 4. PERSONALIZED TRAVEL MAGAZINE GENERATOR DIALOG
// -------------------------------------------------------------
@Composable
private fun PersonalizedMagazineGeneratorDialog(
    onDismiss: () -> Unit,
    onGenerate: (persona: String, pace: String, theme: String) -> Unit
) {
    val personas = listOf(
        "The Historical Flâneur",
        "Artisan Coffee & Craft Seeker",
        "Architecture & Sunset Chaser",
        "Gastronomic & Flea Explorer"
    )
    val paces = listOf(
        "Brisk (Half-Day)",
        "Leisurely (Full Day)",
        "Weekend Retreat"
    )
    val themes = listOf(
        "Cobblestone Spires & Printing Lore",
        "Canal Waters & Hidden Courtyards",
        "Artisan Guilds & Vintage Waves"
    )

    var selectedPersona by remember { mutableStateOf(personas[0]) }
    var selectedPace by remember { mutableStateOf(paces[1]) }
    var selectedTheme by remember { mutableStateOf(themes[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoStories, contentDescription = null, tint = WarmAmber)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate Personalized Magazine", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Select your traveler persona and narrative pace. Townsquare will synthesize an exclusive 3-page illustrated dispatch:",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkTextSecondary
                )

                // Persona selector
                Text("TRAVELER PERSONA", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = WarmAmber)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    personas.forEach { p ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedPersona == p) WarmAmber.copy(alpha = 0.2f) else DarkSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedPersona == p) WarmAmber else DarkBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPersona = p }
                        ) {
                            Text(
                                text = p,
                                fontSize = 12.sp,
                                fontWeight = if (selectedPersona == p) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedPersona == p) WarmAmber else Color.White,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }

                // Pace selector
                Text("EXPLORATION PACE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NeonCyan)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    paces.forEach { pace ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedPace == pace) NeonCyan.copy(alpha = 0.2f) else DarkSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedPace == pace) NeonCyan else DarkBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedPace = pace }
                        ) {
                            Text(
                                text = pace,
                                fontSize = 11.sp,
                                fontWeight = if (selectedPace == pace) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedPace == pace) NeonCyan else Color.White,
                                modifier = Modifier.padding(8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onGenerate(selectedPersona, selectedPace, selectedTheme) },
                colors = ButtonDefaults.buttonColors(containerColor = WarmAmber, contentColor = Color(0xFF261800)),
                modifier = Modifier.testTag("confirm_generate_magazine_btn")
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Generate Issue", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = DarkTextSecondary)
            }
        }
    )
}
