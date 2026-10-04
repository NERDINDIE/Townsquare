package com.example.ui.plus.pocket

import androidx.activity.compose.BackHandler
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

data class PocketArticle(
    val id: String,
    val title: String,
    val excerpt: String,
    val fullContent: String,
    val sourceName: String,
    val feedUrl: String,
    val author: String,
    val publishDate: String,
    val readTimeMinutes: Int,
    val category: String,
    val iconEmoji: String,
    var isSavedOffline: Boolean = false,
    var isFavorite: Boolean = false,
    var isRead: Boolean = false
)

object PocketSeedData {
    fun getInitialArticles(): List<PocketArticle> = listOf(
        PocketArticle(
            id = "pocket_1",
            title = "The Quiet Geometry of Shitamachi Pocket Parks and Cedar Shrines",
            excerpt = "How micro-urbanism and public benches tucked between residential alleys foster deep community connection.",
            fullContent = """Between the narrow alleyways of Yanaka and Nezu, public life thrives in spaces no larger than a tennis court. Known as 'pocket parks' (poketto kouen), these tiny green enclaves feature weathered wooden benches, weeping willow planters, and community water spigots.

Unlike sprawling metropolitan plazas that demand grand spectacle, pocket parks prioritize domestic intimacy. Older residents gather at 07:00 AM for radio calisthenics, followed by thermos tea exchanges. Commuters pause beneath wisteria trellises to read broadsheet dispatches before catching the morning Yamanote line.

Urban architects argue that these micro-sanctuaries provide essential psychological breathing room in hyper-dense neighborhoods. By preserving small municipal parcels rather than selling them to mega-condominium developers, cities preserve their walkable human scale.""",
            sourceName = "The Architectural Flâneur",
            feedUrl = "https://archflaneur.org/feed.xml",
            author = "Luc Delacroix",
            publishDate = "Today, 08:30 AM",
            readTimeMinutes = 4,
            category = "Architecture",
            iconEmoji = "🏛️",
            isFavorite = true,
            isSavedOffline = true
        ),
        PocketArticle(
            id = "pocket_2",
            title = "The Return of Analog Clickwheels and Tactile Computing",
            excerpt = "Why digital fatigue is driving music lovers back to standalone MP3 players and physical knobs.",
            fullContent = """In an era of relentless algorithmic notifications and endless push alerts, a quiet counter-revolution is gaining momentum: the resurgence of dedicated, offline media devices.

From vintage clickwheel MP3 players to mechanical volume potentiometers on car dashboards, users are rediscovering the joy of sensory feedback. 'When you scroll through a physical clickwheel, your fingers develop spatial memory,' says industrial designer Maya Patel. 'You know exactly how many clicks separate track one from track ten without glancing down.'

Furthermore, dedicated audio appliances eliminate distraction. When your music player cannot receive email notifications or social feed vibrations, listening to an album returns to being an immersive, sacred ritual.""",
            sourceName = "Tactile Hardware Journal",
            feedUrl = "https://tactilehardware.dev/rss",
            author = "Maya Patel",
            publishDate = "Yesterday, 14:15 PM",
            readTimeMinutes = 5,
            category = "Tech & Gadgets",
            iconEmoji = "💽",
            isFavorite = true
        ),
        PocketArticle(
            id = "pocket_3",
            title = "Canal Basin Pedestrianization Proposal Passes Public Consultation",
            excerpt = "Historic waterfront wharf to transition into electric tramways and open-air reading terraces.",
            fullContent = """Following three months of spirited town hall debates, the Townsquare Civic Planning Board has officially approved the Phase II Canal Basin Revitalization Plan.

The landmark initiative will permanently pedestrianize 2.4 kilometers of historic dockside cobblestones, replacing commercial delivery truck lanes with silent electric streetcar tracks, shaded reading pavilions, and bicycle priority lanes.

'This ensures our grandchildren will walk alongside the water without the roar of diesel exhaust,' stated Planning Commissioner Marcus Chen. Construction begins early next spring, with local artisans invited to design handcrafted stone bollards and public water fountains.""",
            sourceName = "Townsquare Civic Gazette",
            feedUrl = "https://civicgazette.townsquare/feed",
            author = "Marcus Chen",
            publishDate = "2 days ago",
            readTimeMinutes = 3,
            category = "Civic News",
            iconEmoji = "⚓",
            isSavedOffline = true
        ),
        PocketArticle(
            id = "pocket_4",
            title = "How Independent Community FM Stations Keep Neighborhoods Connected",
            excerpt = "Low-power radio transmitters provide vital local news, school bulletins, and jazz recordings.",
            fullContent = """Perched on the rooftop of the Central Library, a humble 100-watt FM dipole antenna broadcasts 24 hours a day to a three-mile radius. Welcome to Townsquare Community Radio (94.2 FM).

In emergencies when cellular networks buckle under storm surges, radio remains the most resilient communication protocol on Earth. With a simple pair of AA batteries, citizens tune into live transit updates, boil-water notices, and local choir concerts.

'Radio is personal,' remarks station manager Dave Tanaka. 'Someone is speaking directly into your kitchen while you brew coffee. There are no tracking cookies or surveillance algorithms—just acoustic waves vibrating through the air.'""",
            sourceName = "Acoustic Waves Weekly",
            feedUrl = "https://acousticwaves.org/rss",
            author = "Dave Tanaka",
            publishDate = "3 days ago",
            readTimeMinutes = 6,
            category = "Culture & Media",
            iconEmoji = "📻"
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TownsquarePocketApp(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var articles by remember { mutableStateOf(PocketSeedData.getInitialArticles()) }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedArticleForReading by remember { mutableStateOf<PocketArticle?>(null) }
    var isAddFeedDialogOpen by remember { mutableStateOf(false) }
    var filterOnlyFavorites by remember { mutableStateOf(false) }
    var filterOnlyOffline by remember { mutableStateOf(false) }

    // Add feed dialog state
    var newFeedUrl by remember { mutableStateOf("") }
    var newFeedTitle by remember { mutableStateOf("") }
    var newFeedCategory by remember { mutableStateOf("Tech & Gadgets") }

    val categories = listOf("All", "Architecture", "Tech & Gadgets", "Civic News", "Culture & Media")

    val filteredArticles = remember(articles, selectedCategory, filterOnlyFavorites, filterOnlyOffline) {
        articles.filter { a ->
            val matchesCat = selectedCategory == "All" || a.category == selectedCategory
            val matchesFav = !filterOnlyFavorites || a.isFavorite
            val matchesOff = !filterOnlyOffline || a.isSavedOffline
            matchesCat && matchesFav && matchesOff
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .testTag("townsquare_pocket_app"),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📥", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Townsquare Pocket", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White)
                            Text("Distraction-Free RSS Feed & Read-Later Vault", fontSize = 11.sp, color = NeonCyan)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { isAddFeedDialogOpen = true }) {
                        Icon(Icons.Default.RssFeed, contentDescription = "Add RSS Feed", tint = WarmAmber)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurfaceElevated)
            )
        },
        containerColor = DarkBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Category & Filters Bar
            Surface(
                color = DarkSurface,
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 10.dp, horizontal = 14.dp)) {
                    // Quick Filter Badges (Favorites & Offline)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = filterOnlyFavorites,
                            onClick = { filterOnlyFavorites = !filterOnlyFavorites },
                            label = { Text("⭐ Starred (${articles.count { it.isFavorite }})", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = WarmAmber.copy(alpha = 0.2f),
                                selectedLabelColor = WarmAmber
                            )
                        )
                        FilterChip(
                            selected = filterOnlyOffline,
                            onClick = { filterOnlyOffline = !filterOnlyOffline },
                            label = { Text("💾 Offline Vault (${articles.count { it.isSavedOffline }})", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonCyan.copy(alpha = 0.2f),
                                selectedLabelColor = NeonCyan
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Topic Categories
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(categories) { cat ->
                            val isSel = selectedCategory == cat
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSel) NeonCyan else Color(0xFF1E293B),
                                border = BorderStroke(1.dp, if (isSel) NeonCyan else DarkBorder),
                                modifier = Modifier.clickable { selectedCategory = cat }
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color(0xFF003544) else Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Article List
            if (filteredArticles.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📭", fontSize = 44.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No articles in this filter", fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Add new feeds or reset filters to explore stories.", color = DarkTextSecondary, fontSize = 12.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredArticles, key = { it.id }) { article ->
                        PocketArticleCard(
                            article = article,
                            onOpen = { selectedArticleForReading = article },
                            onToggleFavorite = {
                                articles = articles.map { if (it.id == article.id) it.copy(isFavorite = !it.isFavorite) else it }
                            },
                            onToggleOffline = {
                                articles = articles.map { if (it.id == article.id) it.copy(isSavedOffline = !it.isSavedOffline) else it }
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }

    // Modal Distraction-Free Article Reader View
    if (selectedArticleForReading != null) {
        val article = selectedArticleForReading!!
        PocketArticleReaderModal(
            article = article,
            onClose = { selectedArticleForReading = null },
            onToggleFavorite = {
                articles = articles.map { if (it.id == article.id) it.copy(isFavorite = !it.isFavorite) else it }
                selectedArticleForReading = selectedArticleForReading?.copy(isFavorite = !(selectedArticleForReading?.isFavorite ?: false))
            },
            onToggleOffline = {
                articles = articles.map { if (it.id == article.id) it.copy(isSavedOffline = !it.isSavedOffline) else it }
                selectedArticleForReading = selectedArticleForReading?.copy(isSavedOffline = !(selectedArticleForReading?.isSavedOffline ?: false))
            }
        )
    }

    // Add Feed Dialog
    if (isAddFeedDialogOpen) {
        AlertDialog(
            onDismissRequest = { isAddFeedDialogOpen = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📡", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Subscribe to RSS / Atom Feed", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Enter feed XML/RSS address to syndicate articles into Townsquare Pocket.", fontSize = 12.sp, color = DarkTextSecondary)
                    OutlinedTextField(
                        value = newFeedUrl,
                        onValueChange = { newFeedUrl = it },
                        label = { Text("Feed URL") },
                        placeholder = { Text("https://example.com/feed.xml") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newFeedTitle,
                        onValueChange = { newFeedTitle = it },
                        label = { Text("Publication Name (Optional)") },
                        placeholder = { Text("e.g. City Pulse Wire") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newFeedUrl.isNotBlank()) {
                            val newArticle = PocketArticle(
                                id = "custom_${System.currentTimeMillis()}",
                                title = "New Edition: Recent Dispatches from ${newFeedTitle.ifBlank { "Subscribed RSS Feed" }}",
                                excerpt = "Synchronized articles and syndication updates pulled directly from $newFeedUrl.",
                                fullContent = "This feed has been successfully added to your Townsquare Pocket stream. New dispatches from $newFeedUrl will be cached and parsed automatically for offline reading.",
                                sourceName = newFeedTitle.ifBlank { "Custom RSS Feed" },
                                feedUrl = newFeedUrl,
                                author = "Syndicated Wire",
                                publishDate = "Just now",
                                readTimeMinutes = 2,
                                category = newFeedCategory,
                                iconEmoji = "📡",
                                isSavedOffline = true
                            )
                            articles = listOf(newArticle) + articles
                            newFeedUrl = ""
                            newFeedTitle = ""
                            isAddFeedDialogOpen = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544))
                ) {
                    Text("Add Feed", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { isAddFeedDialogOpen = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }
}

@Composable
fun PocketArticleCard(
    article: PocketArticle,
    onOpen: () -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleOffline: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = DarkSurfaceElevated,
        border = BorderStroke(1.dp, DarkBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() }
            .testTag("pocket_article_${article.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Source & Category
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(article.iconEmoji, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = article.sourceName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onToggleFavorite, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = if (article.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Favorite",
                            tint = if (article.isFavorite) WarmAmber else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onToggleOffline, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = if (article.isSavedOffline) Icons.Default.CloudDone else Icons.Default.CloudDownload,
                            contentDescription = "Save Offline",
                            tint = if (article.isSavedOffline) NeonCyan else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = article.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = article.excerpt,
                fontSize = 11.sp,
                color = DarkTextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = DarkBorder)
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${article.publishDate} • By ${article.author}",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
                Text(
                    text = "⏱️ ${article.readTimeMinutes}m read",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = WarmAmber
                )
            }
        }
    }
}

@Composable
fun PocketArticleReaderModal(
    article: PocketArticle,
    onClose: () -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleOffline: () -> Unit
) {
    var fontSizeSp by remember { mutableFloatStateOf(15f) }
    var isNarratingAudio by remember { mutableStateOf(false) }

    LaunchedEffect(isNarratingAudio) {
        while (isNarratingAudio) {
            delay(1000)
        }
    }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF0D1117)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Reader Top Bar
                Surface(color = DarkSurfaceElevated, border = BorderStroke(1.dp, DarkBorder)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onClose) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Close", tint = Color.White)
                            }
                            Text("Reader View", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Text size adjust
                            IconButton(onClick = { fontSizeSp = (fontSizeSp - 1f).coerceAtLeast(12f) }) {
                                Text("A-", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            IconButton(onClick = { fontSizeSp = (fontSizeSp + 1f).coerceAtMost(22f) }) {
                                Text("A+", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }

                            // Text-To-Speech Play
                            IconButton(onClick = { isNarratingAudio = !isNarratingAudio }) {
                                Icon(
                                    imageVector = if (isNarratingAudio) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                                    contentDescription = "Audio Reader",
                                    tint = if (isNarratingAudio) NeonCyan else Color.White
                                )
                            }

                            // Favorite
                            IconButton(onClick = onToggleFavorite) {
                                Icon(
                                    imageVector = if (article.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = null,
                                    tint = if (article.isFavorite) WarmAmber else Color.White
                                )
                            }
                        }
                    }
                }

                // Audio reading ticker banner if active
                if (isNarratingAudio) {
                    Surface(color = NeonCyan.copy(alpha = 0.15f), border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f))) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🔊", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Townsquare Voice Narrator is reading article aloud...", fontSize = 11.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Scrollable Article Body
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = NeonCyan.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "${article.iconEmoji} ${article.sourceName.uppercase()}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = NeonCyan,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = article.title,
                            fontFamily = FontFamily.Serif,
                            fontSize = (fontSizeSp + 6).sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            lineHeight = (fontSizeSp + 10).sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Published ${article.publishDate} • By ${article.author} • ${article.readTimeMinutes} min read",
                            fontSize = 11.sp,
                            color = DarkTextSecondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = DarkBorder)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Article paragraphs
                        Text(
                            text = article.fullContent,
                            fontFamily = FontFamily.Serif,
                            fontSize = fontSizeSp.sp,
                            color = Color(0xFFE2E8F0),
                            lineHeight = (fontSizeSp * 1.55f).sp
                        )

                        Spacer(modifier = Modifier.height(30.dp))
                        HorizontalDivider(color = DarkBorder)
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Feed: ${article.feedUrl}", fontSize = 10.sp, color = Color.Gray, fontFamily = FontFamily.Monospace)
                            Button(
                                onClick = onToggleOffline,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B), contentColor = NeonCyan),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(if (article.isSavedOffline) Icons.Default.CloudDone else Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (article.isSavedOffline) "Saved Offline" else "Save Offline", fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(50.dp))
                    }
                }
            }
        }
    }
}
