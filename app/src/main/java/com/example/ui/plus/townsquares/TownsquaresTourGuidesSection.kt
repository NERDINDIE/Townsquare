package com.example.ui.plus.townsquares

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*
import kotlinx.coroutines.delay

data class TourStop(
    val order: Int,
    val name: String,
    val district: String,
    val category: String,
    val durationMinutes: Int,
    val curatorNotes: String,
    val audioSnippetTitle: String,
    val localProTip: String,
    val addressOrLocation: String,
    val photoEmoji: String
)

data class DetailedTourGuide(
    val id: String,
    val cityId: String,
    val cityName: String,
    val title: String,
    val category: String, // "Art & Architecture", "Coffee & Vinyl", "Historic Walking", "Night Food Crawl", "Hidden Alleyways"
    val themeSubtitle: String,
    val durationHours: Double,
    val distanceKm: Double,
    val pacing: String, // "Leisurely", "Moderate", "Active Explorer"
    val authorName: String,
    val authorRole: String,
    val authorAvatar: String,
    val rating: Double,
    val reviewsCount: Int,
    val badgeEmoji: String,
    val stops: List<TourStop>,
    val highlights: List<String>,
    val recommendedStartingTime: String,
    val audioDurationMinutes: Int,
    var isSavedOffline: Boolean = false
)

object TownsquareTourGuideSeeds {
    fun getInitialGuides(): List<DetailedTourGuide> = listOf(
        DetailedTourGuide(
            id = "tour_tokyo_yanaka",
            cityId = "tokyo",
            cityName = "Tokyo",
            title = "Yanaka Nostalgia: Retro Shitamachi Kissaten & Cedar Temples",
            category = "Historic Walking",
            themeSubtitle = "A serene escape into pre-war Tokyo's atmospheric alleyways and artisan roasteries",
            durationHours = 3.5,
            distanceKm = 3.8,
            pacing = "Leisurely",
            authorName = "Hana Mori",
            authorRole = "Local Preservation Historian & Flâneur",
            authorAvatar = "🌸",
            rating = 4.98,
            reviewsCount = 142,
            badgeEmoji = "🏮",
            recommendedStartingTime = "09:30 AM",
            audioDurationMinutes = 28,
            isSavedOffline = true,
            highlights = listOf("Pre-war Wooden Kissaten", "Nezu Shrine Torii Tunnel", "Yanaka Cemetery Sakura Canopy", "Artisan Yuzu Soba"),
            stops = listOf(
                TourStop(
                    order = 1,
                    name = "Kayaba Coffee (1938)",
                    district = "Yanaka Shitamachi",
                    category = "Culinary Gem",
                    durationMinutes = 45,
                    curatorNotes = "Famed Machiya townhouse cafe operating since 1938. Second floor features traditional tatami seating beneath exposed wooden beams.",
                    audioSnippetTitle = "Chapter 1: The Scent of Roasted Sumiyaki & Showa Mornings",
                    localProTip = "Order the legendary fluffy Egg Toast with bitter matcha syrup.",
                    addressOrLocation = "6-1-29 Yanaka, Taito-ku",
                    photoEmoji = "☕"
                ),
                TourStop(
                    order = 2,
                    name = "Nezu-jinja Shrine Tunnel",
                    district = "Nezu Border",
                    category = "Historical Landmark",
                    durationMinutes = 40,
                    curatorNotes = "Founded nearly 1,900 years ago; survived WW2 air raids completely intact with original Edo-era lacquered shrine pavilions.",
                    audioSnippetTitle = "Chapter 2: The Red Torii Spine of Old Bunkyo",
                    localProTip = "Walk slowly through the vermilion miniature torii path; look for the turtle koi pond on the western bank.",
                    addressOrLocation = "1-28-9 Nezu, Bunkyo-ku",
                    photoEmoji = "⛩️"
                ),
                TourStop(
                    order = 3,
                    name = "Asakura Choso Museum of Sculpture",
                    district = "Yanaka Ginza Hill",
                    category = "Art & Architecture",
                    durationMinutes = 50,
                    curatorNotes = "Former studio and residence of master sculptor Fumio Asakura, blending Western studio skylights with a classical sukiya-style water courtyard.",
                    audioSnippetTitle = "Chapter 3: Granite, Water Lilies & Bronze Cats",
                    localProTip = "Remove shoes at the entrance and climb to the rooftop garden for panoramic district views.",
                    addressOrLocation = "7-18-10 Yanaka, Taito-ku",
                    photoEmoji = "🏛️"
                ),
                TourStop(
                    order = 4,
                    name = "Yanaka Ginza Sunset Steps (Yuyake Dandan)",
                    district = "Yanaka Heights",
                    category = "Scenic Viewpoint",
                    durationMinutes = 45,
                    curatorNotes = "Iconic staircase descending into the shopping street, famous for golden hour silhouettes and wandering street cats.",
                    audioSnippetTitle = "Chapter 4: The Golden Steps of the Cat Quarter",
                    localProTip = "Grab warm freshly-fried menchi-katsu at the butcher shop at the foot of the stairs.",
                    addressOrLocation = "3-13-1 Nishi-Nippori, Arakawa-ku",
                    photoEmoji = "🌇"
                )
            )
        ),
        DetailedTourGuide(
            id = "tour_paris_passages",
            cityId = "paris",
            cityName = "Paris",
            title = "Passages Couverts: Glass Arcades & Antiquarian Book Vaults",
            category = "Art & Architecture",
            themeSubtitle = "Trace the footsteps of Baudelaire and Walter Benjamin through 19th-century iron-and-glass galleries",
            durationHours = 2.8,
            distanceKm = 2.9,
            pacing = "Leisurely",
            authorName = "Luc Delacroix",
            authorRole = "Architectural Essayist & Antiquarian",
            authorAvatar = "🎩",
            rating = 4.96,
            reviewsCount = 98,
            badgeEmoji = "⚜️",
            recommendedStartingTime = "14:00 PM",
            audioDurationMinutes = 24,
            highlights = listOf("Passage des Panoramas (1799)", "Galerie Vivienne Mosaic Floors", "Old Engravings & Postcards", "Hidden Wine Cave"),
            stops = listOf(
                TourStop(
                    order = 1,
                    name = "Passage des Panoramas",
                    district = "2nd Arrondissement",
                    category = "Hidden Alleyways",
                    durationMinutes = 40,
                    curatorNotes = "Built in 1799, this is Paris's oldest covered arcade, pioneer of early gas lighting and philately stamp collectors.",
                    audioSnippetTitle = "Chapter 1: The Gaslit Dawn of the Flâneur",
                    localProTip = "Inspect the vintage typography on the engraver storefronts halfway down the arcade.",
                    addressOrLocation = "11 Boulevard Montmartre",
                    photoEmoji = "🏮"
                ),
                TourStop(
                    order = 2,
                    name = "Galerie Vivienne",
                    district = "Palais-Royal",
                    category = "Art & Architecture",
                    durationMinutes = 50,
                    curatorNotes = "Designed in 1823 with neoclassical rotunda, signed mosaic floors by Giandomenico Facchina and high glass canopies.",
                    audioSnippetTitle = "Chapter 2: Marble Tesserae & the Rotunda",
                    localProTip = "Visit Librairie Jousseaume, an original 1826 bookstore packed floor to ceiling with antique first editions.",
                    addressOrLocation = "4 Rue des Petits-Champs",
                    photoEmoji = "📚"
                ),
                TourStop(
                    order = 3,
                    name = "Passage Jouffroy & Wax Cabinet",
                    district = "Grands Boulevards",
                    category = "Historical Landmark",
                    durationMinutes = 45,
                    curatorNotes = "First arcade constructed entirely with metal and glass framework in 1845, featuring vintage walking cane shops.",
                    audioSnippetTitle = "Chapter 3: Iron Stanchions & Curio Windows",
                    localProTip = "Look up at the geometric glass roof when sunlight streams through late in the afternoon.",
                    addressOrLocation = "10-12 Boulevard Montmartre",
                    photoEmoji = "✨"
                )
            )
        ),
        DetailedTourGuide(
            id = "tour_london_vinyl",
            cityId = "london",
            cityName = "London",
            title = "Soho to Barbican: Subterranean Vinyl, Brutalist Slabs & Jazz Vaults",
            category = "Coffee & Vinyl",
            themeSubtitle = "An audio-centric walking route linking subterranean vinyl dens with post-war concrete sanctuaries",
            durationHours = 4.0,
            distanceKm = 4.6,
            pacing = "Moderate",
            authorName = "Maya Patel",
            authorRole = "Sound Archivist & Radio Host",
            authorAvatar = "🎧",
            rating = 4.93,
            reviewsCount = 115,
            badgeEmoji = "🎵",
            recommendedStartingTime = "11:00 AM",
            audioDurationMinutes = 35,
            highlights = listOf("Berwick Street Record Vaults", "Brutalist Highwalks of Barbican", "Foyer Conservatory", "Subterranean Jazz Crypt"),
            stops = listOf(
                TourStop(
                    order = 1,
                    name = "Sounds of the Universe & Berwick St",
                    district = "Soho",
                    category = "Coffee & Vinyl",
                    durationMinutes = 55,
                    curatorNotes = "Flagship home of Soul Jazz Records with unmatched collections of reggae, funk, Afrobeat, and vintage 7-inch singles.",
                    audioSnippetTitle = "Chapter 1: The 45 RPM Soul of Berwick Street",
                    localProTip = "Ask the basement clerk to browse the newly arrived Brazilian imports box.",
                    addressOrLocation = "7 Broadwick St, Soho",
                    photoEmoji = "💽"
                ),
                TourStop(
                    order = 2,
                    name = "Barbican Highwalks & Concrete Water Garden",
                    district = "City of London",
                    category = "Art & Architecture",
                    durationMinutes = 70,
                    curatorNotes = "Chamberlin, Powell and Bon's post-war utopian housing complex. Bush-hammered concrete columns hover over serene lily ponds.",
                    audioSnippetTitle = "Chapter 2: The Brutalist Sanctuary Above the City",
                    localProTip = "Take the pedestrian ramp to St Giles Cripplegate, a medieval church cradled by modernist monoliths.",
                    addressOrLocation = "Silk Street, Barbican",
                    photoEmoji = "🏢"
                ),
                TourStop(
                    order = 3,
                    name = "Barbican Conservatory & Glass Canopy",
                    district = "Barbican Upper Tier",
                    category = "Scenic Viewpoint",
                    durationMinutes = 40,
                    curatorNotes = "Massive hidden greenhouse containing over 1,500 species of tropical plants and koi pools intertwined with raw concrete balconies.",
                    audioSnippetTitle = "Chapter 3: Ferns, Moss & Concrete Ribs",
                    localProTip = "Check for quiet seats on the upper cactus platform.",
                    addressOrLocation = "Level 3, Barbican Centre",
                    photoEmoji = "🌿"
                )
            )
        ),
        DetailedTourGuide(
            id = "tour_newyork_dumbo",
            cityId = "new_york",
            cityName = "New York",
            title = "Waterfront Cobblestones & High Line Industrial Skylines",
            category = "Scenic Viewpoint",
            themeSubtitle = "From historic brick warehouses of Brooklyn across the river to elevated railway gardens",
            durationHours = 3.2,
            distanceKm = 4.2,
            pacing = "Active Explorer",
            authorName = "Marcus Chen",
            authorRole = "Urban Planner & Waterfront Geographer",
            authorAvatar = "🌉",
            rating = 4.97,
            reviewsCount = 184,
            badgeEmoji = "🗽",
            recommendedStartingTime = "08:00 AM",
            audioDurationMinutes = 26,
            highlights = listOf("Manhattan Bridge Arch View", "Brooklyn Bridge Pedestrian Timber Boardwalk", "Chelsea Elevated Railway Viaduct", "Hudson River Sunset"),
            stops = listOf(
                TourStop(
                    order = 1,
                    name = "Washington & Water Street Overlook",
                    district = "DUMBO",
                    category = "Scenic Viewpoint",
                    durationMinutes = 30,
                    curatorNotes = "Classic cinematic framing where the red-brick warehouses perfectly frame the blue steel stanchion of the Manhattan Bridge.",
                    audioSnippetTitle = "Chapter 1: The Belgian Blocks of the Industrial Port",
                    localProTip = "Arrive before 08:30 AM to photograph the bridge tower without morning tour crowds.",
                    addressOrLocation = "Washington St & Water St, Brooklyn",
                    photoEmoji = "📸"
                ),
                TourStop(
                    order = 2,
                    name = "Brooklyn Bridge Boardwalk Crossing",
                    district = "East River",
                    category = "Historical Landmark",
                    durationMinutes = 55,
                    curatorNotes = "John A. Roebling's 1883 engineering masterpiece with limestone Gothic arches and diagonal suspension cable webs.",
                    audioSnippetTitle = "Chapter 2: Granite Caissons & the River Winds",
                    localProTip = "Pause at the western granite tower pier to gaze up through the wire harp at Lower Manhattan.",
                    addressOrLocation = "Brooklyn Bridge Promenade",
                    photoEmoji = "🌉"
                ),
                TourStop(
                    order = 3,
                    name = "The High Line Rail Garden (Gansevoort to 23rd)",
                    district = "Meatpacking & Chelsea",
                    category = "Art & Architecture",
                    durationMinutes = 65,
                    curatorNotes = "Piet Oudolf-designed naturalistic prairie grasses and perennial wildflowers sprouting between historic freight train tracks.",
                    audioSnippetTitle = "Chapter 3: Wild Grasses on the Viaduct",
                    localProTip = "Sit in the 10th Avenue sunken amphitheater to watch yellow cabs pass below beneath the glass floor.",
                    addressOrLocation = "Gansevoort St & Washington St",
                    photoEmoji = "🌱"
                )
            )
        ),
        DetailedTourGuide(
            id = "tour_taipei_night",
            cityId = "taipei",
            cityName = "Taipei",
            title = "Midnight Steamer Baskets & Neon Temple Courtyards",
            category = "Night Food Crawl",
            themeSubtitle = "Follow the scent of scallion pancakes and incense through night bazaars and ancient halls",
            durationHours = 3.0,
            distanceKm = 3.2,
            pacing = "Leisurely",
            authorName = "Wei-Ting Lin",
            authorRole = "Taipei Culinary Chronicler",
            authorAvatar = "🥟",
            rating = 4.99,
            reviewsCount = 210,
            badgeEmoji = "🏮",
            recommendedStartingTime = "18:30 PM",
            audioDurationMinutes = 22,
            highlights = listOf("Bangka Lungshan Temple Incense", "Herb Alley (Xichang St)", "Huaxi Night Market Pepper Buns", "Herbal Tea Stalls"),
            stops = listOf(
                TourStop(
                    order = 1,
                    name = "Bangka Lungshan Temple at Twilight",
                    district = "Wanhua",
                    category = "Historical Landmark",
                    durationMinutes = 45,
                    curatorNotes = "Built in 1738; features elaborate bronze dragon pillars, gold leaf ceilings, and chanting rituals during sunset.",
                    audioSnippetTitle = "Chapter 1: The Incense Glow of Old Wanhua",
                    localProTip = "Observe the wooden moon blocks (jiaobei) cast by worshipers seeking guidance at the main altar.",
                    addressOrLocation = "No. 211 Guangzhou St, Wanhua",
                    photoEmoji = "🛕"
                ),
                TourStop(
                    order = 2,
                    name = "Qingcao Herb Lane",
                    district = "Wanhua Lane",
                    category = "Hidden Alleyways",
                    durationMinutes = 30,
                    curatorNotes = "A sheltered pedestrian corridor stacked high with fragrant dried medicinal roots, mint, and fresh bitter melon vines.",
                    audioSnippetTitle = "Chapter 2: The Bitter Sweet Tonics of Herb Lane",
                    localProTip = "Order a glass of chilled medicinal herbal tea (qingcao cha) with minimal sweetness.",
                    addressOrLocation = "Lane 224, Xichang St",
                    photoEmoji = "🌿"
                ),
                TourStop(
                    order = 3,
                    name = "Huaxi Street Night Market Gourmet Stalls",
                    district = "Guangzhou Bazaar",
                    category = "Culinary Gem",
                    durationMinutes = 60,
                    curatorNotes = "Traditional night market renowned for clay oven pepper pork buns (hujiao bing) baked on hot charcoal cylinder walls.",
                    audioSnippetTitle = "Chapter 3: Charcoal Clay Ovens & Crispy Crusts",
                    localProTip = "Let the pepper bun cool for two minutes before taking a bite to avoid the hot bubbling broth!",
                    addressOrLocation = "Huaxi St & Guangzhou St",
                    photoEmoji = "🥟"
                )
            )
        )
    )
}

@Composable
fun TownsquaresTourGuidesSection(
    selectedCityId: String?,
    modifier: Modifier = Modifier
) {
    var tourGuides by remember { mutableStateOf(TownsquareTourGuideSeeds.getInitialGuides()) }
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var activeGuideDetail by remember { mutableStateOf<DetailedTourGuide?>(null) }
    var visitedStopIds by remember { mutableStateOf(mutableSetOf<String>()) }
    var isAudioPlaying by remember { mutableStateOf(false) }
    var activeAudioStopIndex by remember { mutableIntStateOf(0) }
    var audioProgressSec by remember { mutableIntStateOf(14) }

    // Audio playback simulation
    LaunchedEffect(isAudioPlaying) {
        while (isAudioPlaying) {
            delay(1000)
            audioProgressSec = (audioProgressSec + 1) % 180
        }
    }

    val categories = listOf("All", "Historic Walking", "Art & Architecture", "Coffee & Vinyl", "Scenic Viewpoint", "Night Food Crawl")

    val filteredGuides = remember(tourGuides, selectedCityId, selectedCategory, searchQuery) {
        tourGuides.filter { guide ->
            val cityMatches = selectedCityId == null || guide.cityId == selectedCityId
            val categoryMatches = selectedCategory == "All" || guide.category == selectedCategory
            val queryMatches = searchQuery.isBlank() ||
                guide.title.contains(searchQuery, ignoreCase = true) ||
                guide.themeSubtitle.contains(searchQuery, ignoreCase = true) ||
                guide.cityName.contains(searchQuery, ignoreCase = true) ||
                guide.stops.any { it.name.contains(searchQuery, ignoreCase = true) || it.district.contains(searchQuery, ignoreCase = true) }
            cityMatches && categoryMatches && queryMatches
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .testTag("townsquares_tour_guides_section")
    ) {
        // Hero Header Banner
        Surface(
            color = DarkSurfaceElevated,
            border = BorderStroke(1.dp, DarkBorder),
            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🧭", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (selectedCityId == null) "Detailed Townsquare Tour Guides" else "${filteredGuides.firstOrNull()?.cityName ?: "City"} Curated Walking Guides",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Self-guided walking itineraries, stop-by-stop audio commentary & insider local tips",
                            fontSize = 11.sp,
                            color = DarkTextSecondary
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = NeonCyan.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "${filteredGuides.size} Guides",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search tour by district, architect, or coffee roaster...", fontSize = 12.sp, color = DarkTextMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = DarkTextSecondary, modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = DarkTextSecondary, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkBg,
                        unfocusedContainerColor = DarkBg,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tour_guides_search_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Category Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) NeonCyan else DarkSurface,
                            border = BorderStroke(1.dp, if (isSelected) NeonCyan else DarkBorder),
                            modifier = Modifier
                                .clickable { selectedCategory = cat }
                                .testTag("tour_category_chip_$cat")
                        ) {
                            Text(
                                text = cat,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color(0xFF003544) else Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tour Guides List
        if (filteredGuides.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🗺️", fontSize = 44.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("No tour guides match your filter", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Try choosing 'All Cities' or resetting your search term.", color = DarkTextSecondary, fontSize = 12.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredGuides, key = { it.id }) { guide ->
                    TourGuideCard(
                        guide = guide,
                        onOpenDetail = { activeGuideDetail = guide },
                        onToggleOffline = {
                            tourGuides = tourGuides.map {
                                if (it.id == guide.id) it.copy(isSavedOffline = !it.isSavedOffline) else it
                            }
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }

    // Modal Sheet / Dialog for Detailed Tour Itinerary
    if (activeGuideDetail != null) {
        val guide = activeGuideDetail!!
        Dialog(
            onDismissRequest = { activeGuideDetail = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DarkBg)
                    .testTag("tour_guide_detail_dialog"),
                color = DarkBg
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header Bar
                    Surface(color = DarkSurfaceElevated, border = BorderStroke(1.dp, DarkBorder)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { activeGuideDetail = null }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Close", tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${guide.badgeEmoji} ${guide.cityName} Walking Guide",
                                    fontSize = 11.sp,
                                    color = NeonCyan,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = guide.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            IconButton(
                                onClick = {
                                    tourGuides = tourGuides.map {
                                        if (it.id == guide.id) it.copy(isSavedOffline = !it.isSavedOffline) else it
                                    }
                                    activeGuideDetail = activeGuideDetail?.copy(isSavedOffline = !(activeGuideDetail?.isSavedOffline ?: false))
                                }
                            ) {
                                Icon(
                                    imageVector = if (guide.isSavedOffline) Icons.Default.CloudDone else Icons.Default.CloudDownload,
                                    contentDescription = "Save Offline",
                                    tint = if (guide.isSavedOffline) NeonCyan else Color.White
                                )
                            }
                        }
                    }

                    // Content Scroll
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                            // Overview Box
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = DarkSurfaceElevated,
                                border = BorderStroke(1.dp, DarkBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(text = guide.title, fontSize = 17.sp, fontWeight = FontWeight.Black, color = Color.White)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = guide.themeSubtitle, fontSize = 12.sp, color = DarkTextSecondary)

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Metric badges
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        MetricItem(icon = "⏱️", label = "Duration", value = "${guide.durationHours} hrs")
                                        MetricItem(icon = "🚶", label = "Distance", value = "${guide.distanceKm} km")
                                        MetricItem(icon = "🏃", label = "Pacing", value = guide.pacing)
                                        MetricItem(icon = "⭐", label = "Rating", value = "${guide.rating} (${guide.reviewsCount})")
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    HorizontalDivider(color = DarkBorder)
                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Author Info
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = guide.authorAvatar, fontSize = 22.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(text = "Curated by ${guide.authorName}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            Text(text = guide.authorRole, fontSize = 10.sp, color = NeonCyan)
                                        }
                                        Spacer(modifier = Modifier.weight(1f))
                                        Text("Starts ${guide.recommendedStartingTime}", fontSize = 10.sp, color = WarmAmber, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Audio Guide Player Card
                        item {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFF0F2338),
                                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("🎧", fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Audio Narration & Field Recordings", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            val currentStop = guide.stops.getOrNull(activeAudioStopIndex) ?: guide.stops.first()
                                            Text(currentStop.audioSnippetTitle, fontSize = 11.sp, color = NeonCyan, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                        FilledIconButton(
                                            onClick = { isAudioPlaying = !isAudioPlaying },
                                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                contentDescription = if (isAudioPlaying) "Pause" else "Play",
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    LinearProgressIndicator(
                                        progress = { (audioProgressSec % 60) / 60f },
                                        modifier = Modifier.fillMaxWidth().height(4.dp),
                                        color = NeonCyan,
                                        trackColor = DarkSurfaceElevated
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("0:${String.format("%02d", audioProgressSec % 60)}", fontSize = 10.sp, color = DarkTextSecondary)
                                        Text("Stop ${activeAudioStopIndex + 1} of ${guide.stops.size} • ${guide.audioDurationMinutes}m total", fontSize = 10.sp, color = DarkTextSecondary)
                                    }
                                }
                            }
                        }

                        item {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("📍 Tour Stops & Itinerary", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                val visitedCount = guide.stops.count { visitedStopIds.contains("${guide.id}_${it.order}") }
                                Text(
                                    text = "$visitedCount/${guide.stops.size} Completed",
                                    fontSize = 11.sp,
                                    color = if (visitedCount == guide.stops.size) Color(0xFF4CAF50) else WarmAmber,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Stop by stop list
                        itemsIndexed(guide.stops) { index, stop ->
                            val stopKey = "${guide.id}_${stop.order}"
                            val isVisited = visitedStopIds.contains(stopKey)

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isVisited) DarkSurface.copy(alpha = 0.7f) else DarkSurfaceElevated,
                                border = BorderStroke(1.dp, if (activeAudioStopIndex == index) NeonCyan else DarkBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("tour_stop_item_${stop.order}")
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.Top) {
                                        Surface(
                                            shape = CircleShape,
                                            color = if (isVisited) Color(0xFF2E7D32) else if (activeAudioStopIndex == index) NeonCyan else Color(0xFF1E293B),
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                if (isVisited) {
                                                    Icon(Icons.Default.Check, contentDescription = "Completed", tint = Color.White, modifier = Modifier.size(16.dp))
                                                } else {
                                                    Text(
                                                        text = "${stop.order}",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp,
                                                        color = if (activeAudioStopIndex == index) Color(0xFF003544) else Color.White
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(stop.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(stop.photoEmoji, fontSize = 13.sp)
                                            }
                                            Text(
                                                text = "${stop.district} • ${stop.durationMinutes} mins • ${stop.category}",
                                                fontSize = 10.sp,
                                                color = NeonCyan
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                val newSet = visitedStopIds.toMutableSet()
                                                if (isVisited) newSet.remove(stopKey) else newSet.add(stopKey)
                                                visitedStopIds = newSet
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isVisited) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                                contentDescription = "Mark visited",
                                                tint = if (isVisited) Color(0xFF4CAF50) else DarkTextSecondary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(stop.curatorNotes, fontSize = 11.sp, color = DarkTextSecondary, lineHeight = 16.sp)

                                    Spacer(modifier = Modifier.height(8.dp))
                                    // Local Pro Tip
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = WarmAmber.copy(alpha = 0.12f),
                                        border = BorderStroke(1.dp, WarmAmber.copy(alpha = 0.35f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("💡", fontSize = 12.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = stop.localProTip,
                                                fontSize = 10.sp,
                                                color = WarmAmber,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "📍 ${stop.addressOrLocation}",
                                            fontSize = 10.sp,
                                            color = DarkTextMuted,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        TextButton(
                                            onClick = {
                                                activeAudioStopIndex = index
                                                isAudioPlaying = true
                                            },
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Play Audio", fontSize = 10.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(30.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TourGuideCard(
    guide: DetailedTourGuide,
    onOpenDetail: () -> Unit,
    onToggleOffline: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = DarkSurfaceElevated,
        border = BorderStroke(1.dp, DarkBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenDetail() }
            .testTag("tour_guide_card_${guide.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(guide.badgeEmoji, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${guide.cityName.uppercase()} • ${guide.category.uppercase()}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = NeonCyan
                    )
                    Text(
                        text = guide.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
                IconButton(onClick = onToggleOffline, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = if (guide.isSavedOffline) Icons.Default.CloudDone else Icons.Default.CloudDownload,
                        contentDescription = "Save Offline",
                        tint = if (guide.isSavedOffline) NeonCyan else DarkTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = guide.themeSubtitle,
                fontSize = 11.sp,
                color = DarkTextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Highlight chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(guide.highlights) { tag ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF1E293B)
                    ) {
                        Text(
                            text = tag,
                            fontSize = 10.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = DarkBorder)
            Spacer(modifier = Modifier.height(8.dp))

            // Footer stats & Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⏱️ ${guide.durationHours}h  •  🚶 ${guide.distanceKm}km  •  ⭐ ${guide.rating}",
                    fontSize = 11.sp,
                    color = Color.LightGray,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = onOpenDetail,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Explore Tour (${guide.stops.size} Stops)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MetricItem(icon: String, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(label, fontSize = 9.sp, color = DarkTextSecondary)
    }
}
