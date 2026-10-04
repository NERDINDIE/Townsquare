package com.example.ui.screens.discovery

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.ui.theme.*
import kotlinx.coroutines.delay

data class DiscoveryStory(
    val id: String,
    val authorHandle: String,
    val authorName: String,
    val avatarEmoji: String,
    val storyImageUrl: String,
    val caption: String,
    var isViewed: Boolean = false
)

data class DiscoveryPhotoItem(
    val id: String,
    val authorHandle: String,
    val authorName: String,
    val avatarEmoji: String,
    val location: String,
    val imageUrl: String,
    val caption: String,
    val tags: List<String>,
    var likesCount: Int,
    var commentsCount: Int,
    var isLiked: Boolean = false,
    var isBookmarked: Boolean = false,
    val timestamp: String = "2h ago"
)

object VisualDiscoverySeedData {
    fun getInitialStories(): List<DiscoveryStory> = listOf(
        DiscoveryStory("s1", "@yanaka_photo", "Hana Mori", "🌸", "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?auto=format&fit=crop&w=800&q=80", "Morning mist through the torii gates ⛩️"),
        DiscoveryStory("s2", "@night_market", "Wei-Ting Lin", "🏮", "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=800&q=80", "Steaming pepper buns at the night bazaar 🥟"),
        DiscoveryStory("s3", "@canal_roasters", "Luc Delacroix", "☕", "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?auto=format&fit=crop&w=800&q=80", "First pour of Ethiopian single origin beans!"),
        DiscoveryStory("s4", "@metro_tram", "Marcus Chen", "🚊", "https://images.unsplash.com/photo-1513635269975-59663e0ac1ad?auto=format&fit=crop&w=800&q=80", "Vintage green tram cruising over the canal bridge"),
        DiscoveryStory("s5", "@jazz_berlin", "Jonas Richter", "🎷", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=800&q=80", "Soundcheck in the subterranean cellar vault")
    )

    fun getInitialPhotos(): List<DiscoveryPhotoItem> = listOf(
        DiscoveryPhotoItem(
            id = "photo_1",
            authorHandle = "@claracinema",
            authorName = "Clara De Laurentiis",
            avatarEmoji = "🎬",
            location = "Grand Rivoli 1928 Theatre",
            imageUrl = "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=1200&q=80",
            caption = "35mm projector light cutting through the velvet haze of the upper balcony. Preserving the tactile soul of cinema.",
            tags = listOf("#cinema", "#analog", "#filmisnotdead", "#grandrivoli"),
            likesCount = 842,
            commentsCount = 38
        ),
        DiscoveryPhotoItem(
            id = "photo_2",
            authorHandle = "@yanaka_photo",
            authorName = "Hana Mori",
            avatarEmoji = "🌸",
            location = "Old Yanaka Shitamachi",
            imageUrl = "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?auto=format&fit=crop&w=1200&q=80",
            caption = "Autumn afternoon in the cat quarter. Wooden machiya roofs and tea steam rising into the amber light.",
            tags = listOf("#yanaka", "#tokyo", "#autumnlight", "#kissaten"),
            likesCount = 1290,
            commentsCount = 74
        ),
        DiscoveryPhotoItem(
            id = "photo_3",
            authorHandle = "@night_market",
            authorName = "Wei-Ting Lin",
            avatarEmoji = "🥟",
            location = "Canal Waterfront Night Bazaar",
            imageUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?auto=format&fit=crop&w=1200&q=80",
            caption = "Neon reflections dancing on the harbor waters. Fresh scallion skewers sizzling over hot binchotan charcoal.",
            tags = listOf("#nightmarket", "#streetfood", "#neonvibes"),
            likesCount = 956,
            commentsCount = 49
        ),
        DiscoveryPhotoItem(
            id = "photo_4",
            authorHandle = "@marcus_urban",
            authorName = "Marcus Chen",
            avatarEmoji = "🌉",
            location = "Canal Suspension Bridge Promenade",
            imageUrl = "https://images.unsplash.com/photo-1513635269975-59663e0ac1ad?auto=format&fit=crop&w=1200&q=80",
            caption = "The pedestrian timber boardwalk at dawn before the commuter rush. Crisp autumn breeze and silent waters.",
            tags = listOf("#bridges", "#urbanism", "#morningrun"),
            likesCount = 670,
            commentsCount = 22
        ),
        DiscoveryPhotoItem(
            id = "photo_5",
            authorHandle = "@vinyl_maya",
            authorName = "Maya Patel",
            avatarEmoji = "🎧",
            location = "Broadwick Record Crypt",
            imageUrl = "https://images.unsplash.com/photo-1539185441755-769473a23570?auto=format&fit=crop&w=1200&q=80",
            caption = "Fresh crate arrival: Japanese city pop 7-inches and 1970s dub reggae. Analog gold in the basement.",
            tags = listOf("#vinyl", "#recordstore", "#cratedigging"),
            likesCount = 1120,
            commentsCount = 58
        ),
        DiscoveryPhotoItem(
            id = "photo_6",
            authorHandle = "@luc_flaneur",
            authorName = "Luc Delacroix",
            avatarEmoji = "🏛️",
            location = "Galerie Vivienne Arcades",
            imageUrl = "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?auto=format&fit=crop&w=1200&q=80",
            caption = "Mosaic floors by Facchina and glass canopies illuminating antiquarian bookshops. Time standing still.",
            tags = listOf("#paris", "#architecture", "#arcades"),
            likesCount = 780,
            commentsCount = 31
        )
    )
}

@Composable
fun VisualPhotoDiscoveryView(
    modifier: Modifier = Modifier
) {
    var stories by remember { mutableStateOf(VisualDiscoverySeedData.getInitialStories()) }
    var photos by remember { mutableStateOf(VisualDiscoverySeedData.getInitialPhotos()) }
    var isGridView by remember { mutableStateOf(false) }
    var activeStory by remember { mutableStateOf<DiscoveryStory?>(null) }
    var activeLightboxPhoto by remember { mutableStateOf<DiscoveryPhotoItem?>(null) }
    var showHeartPopId by remember { mutableStateOf<String?>(null) }

    // Heart pop animation reset
    LaunchedEffect(showHeartPopId) {
        if (showHeartPopId != null) {
            delay(800)
            showHeartPopId = null
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .testTag("visual_photo_discovery_view")
    ) {
        // 1. Stories Avatar Row ("Stories from the Square")
        Surface(
            color = DarkSurface,
            border = BorderStroke(1.dp, DarkBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(vertical = 10.dp)) {
                Text(
                    text = "STORIES FROM THE SQUARE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = NeonCyan,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 8.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp)
                ) {
                    items(stories, key = { it.id }) { story ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                activeStory = story
                                stories = stories.map { if (it.id == story.id) it.copy(isViewed = true) else it }
                            }
                        ) {
                            // Story gradient border ring
                            Box(
                                modifier = Modifier
                                    .size(58.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (story.isViewed) Brush.linearGradient(listOf(Color.DarkGray, Color.Gray))
                                        else Brush.linearGradient(listOf(Color(0xFFFF007A), Color(0xFFFF8800), NeonCyan))
                                    )
                                    .padding(2.5.dp)
                                    .clip(CircleShape)
                                    .background(DarkBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(story.avatarEmoji, fontSize = 24.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = story.authorHandle,
                                fontSize = 10.sp,
                                color = if (story.isViewed) DarkTextSecondary else Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // 2. View Mode Toggle & Header Filter Bar
        Surface(
            color = DarkSurfaceElevated,
            border = BorderStroke(1.dp, DarkBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "EXPLORE PHOTO FEED",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { isGridView = false },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewStream,
                            contentDescription = "Feed View",
                            tint = if (!isGridView) NeonCyan else Color.Gray
                        )
                    }
                    IconButton(
                        onClick = { isGridView = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.GridView,
                            contentDescription = "Grid View",
                            tint = if (isGridView) NeonCyan else Color.Gray
                        )
                    }
                }
            }
        }

        // 3. Main Discovery Feed or Grid
        if (isGridView) {
            // 3x3 Instagram Style Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(photos, key = { it.id }) { photo ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable { activeLightboxPhoto = photo }
                    ) {
                        AsyncImage(
                            model = photo.imageUrl,
                            contentDescription = photo.caption,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Likes overlay badge
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color.Black.copy(alpha = 0.5f),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("❤️", fontSize = 9.sp)
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("${photo.likesCount}", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else {
            // Full Stream Feed Cards
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 60.dp)
            ) {
                items(photos, key = { it.id }) { photo ->
                    val isPoppingHeart = showHeartPopId == photo.id

                    Surface(
                        color = DarkSurface,
                        border = BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            // Author Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFF1E293B),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(photo.avatarEmoji, fontSize = 20.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(photo.authorName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                        Text("📍 ${photo.location}", fontSize = 10.sp, color = DarkTextSecondary)
                                    }
                                }

                                Text(photo.timestamp, fontSize = 10.sp, color = DarkTextSecondary)
                            }

                            // High-Res Image with Double-Tap to Like
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(320.dp)
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onDoubleTap = {
                                                showHeartPopId = photo.id
                                                if (!photo.isLiked) {
                                                    photos = photos.map {
                                                        if (it.id == photo.id) it.copy(isLiked = true, likesCount = it.likesCount + 1) else it
                                                    }
                                                }
                                            },
                                            onTap = { activeLightboxPhoto = photo }
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = photo.imageUrl,
                                    contentDescription = photo.caption,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                // Animated popping heart on double-tap
                                androidx.compose.animation.AnimatedVisibility(
                                    visible = isPoppingHeart,
                                    enter = scaleIn() + fadeIn(),
                                    exit = scaleOut() + fadeOut()
                                ) {
                                    Text("❤️", fontSize = 72.sp)
                                }
                            }

                            // Actions Row (Like, Comment, Bookmark, Share)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            photos = photos.map {
                                                if (it.id == photo.id) {
                                                    val newLiked = !it.isLiked
                                                    it.copy(
                                                        isLiked = newLiked,
                                                        likesCount = if (newLiked) it.likesCount + 1 else it.likesCount - 1
                                                    )
                                                } else it
                                            }
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (photo.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = "Like",
                                            tint = if (photo.isLiked) CoralRed else Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    IconButton(
                                        onClick = { activeLightboxPhoto = photo },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.ChatBubbleOutline, contentDescription = "Comments", tint = Color.White)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    IconButton(
                                        onClick = { /* Share */ },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        photos = photos.map {
                                            if (it.id == photo.id) it.copy(isBookmarked = !it.isBookmarked) else it
                                        }
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (photo.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = "Bookmark",
                                        tint = if (photo.isBookmarked) WarmAmber else Color.White
                                    )
                                }
                            }

                            // Likes Count & Caption
                            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)) {
                                Text(
                                    text = "${"%,d".format(photo.likesCount)} likes",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row {
                                    Text(
                                        text = "${photo.authorHandle} ",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = photo.caption,
                                        fontSize = 12.sp,
                                        color = Color(0xFFE2E8F0)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "View all ${photo.commentsCount} comments",
                                    fontSize = 11.sp,
                                    color = DarkTextSecondary,
                                    modifier = Modifier.clickable { activeLightboxPhoto = photo }
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
    }

    // Full Story Viewer Dialog
    if (activeStory != null) {
        val story = activeStory!!
        Dialog(
            onDismissRequest = { activeStory = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = story.storyImageUrl,
                        contentDescription = story.caption,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Story Top Bar & Progress Bar
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(14.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier.fillMaxWidth().height(3.dp),
                            color = Color.White,
                            trackColor = Color.Gray.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(story.avatarEmoji, fontSize = 22.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(story.authorHandle, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                            }
                            IconButton(onClick = { activeStory = null }) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                            }
                        }
                    }

                    // Bottom Caption
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .navigationBarsPadding()
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = story.caption,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
            }
        }
    }

    // Lightbox Modal
    if (activeLightboxPhoto != null) {
        val photo = activeLightboxPhoto!!
        Dialog(
            onDismissRequest = { activeLightboxPhoto = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF090D14)) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { activeLightboxPhoto = null }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Text("Photo Detail", fontWeight = FontWeight.Bold, color = Color.White)
                        IconButton(onClick = { /* share */ }) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                        }
                    }

                    AsyncImage(
                        model = photo.imageUrl,
                        contentDescription = photo.caption,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )

                    Surface(
                        color = DarkSurfaceElevated,
                        modifier = Modifier.fillMaxWidth().navigationBarsPadding()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(photo.avatarEmoji, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(photo.authorName, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                    Text("📍 ${photo.location} • ${photo.timestamp}", fontSize = 10.sp, color = DarkTextSecondary)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(photo.caption, color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
