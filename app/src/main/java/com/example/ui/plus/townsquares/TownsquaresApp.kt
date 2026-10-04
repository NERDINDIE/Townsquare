package com.example.ui.plus.townsquares

import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.plus.townsquares.model.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TownsquaresApp(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    BackHandler { onBack() }

    val cities = remember { TownsquaresSeed.getInitialCities() }
    var posts by remember { mutableStateOf(TownsquaresSeed.getInitialPosts()) }
    val transitRoutes = remember { TownsquaresSeed.getInitialTransitRoutes() }

    var selectedCityId by remember { mutableStateOf<String?>("tokyo") } // null = All Cities
    var selectedTab by remember { mutableIntStateOf(0) } // 0: City Feed, 1: Tour Guides, 2: Transit Routes, 3: Saved Stories, 4: World Hubs
    var isCreatePostOpen by remember { mutableStateOf(false) }
    var activeCommentPost by remember { mutableStateOf<CityExperiencePost?>(null) }
    var postCommentsMap by remember {
        mutableStateOf(
            mutableMapOf<String, List<PostComment>>(
                "post_tokyo_1" to listOf(
                    PostComment(authorName = "Hana Mori", authorHandle = "@hana_m", avatarEmoji = "🌸", text = "The soba shop owner is Mr. Tanaka! Ask for his seasonal yuzu soba."),
                    PostComment(authorName = "Dave Miller", authorHandle = "@davem", avatarEmoji = "📸", text = "Yanaka is magical on autumn afternoons. Great capture!")
                )
            )
        )
    }

    // Transit Filters
    var selectedTransitMode by remember { mutableStateOf<TransitMode?>(null) }

    val filteredPosts = remember(posts, selectedCityId, selectedTab) {
        when (selectedTab) {
            0 -> {
                if (selectedCityId == null) posts
                else posts.filter { it.cityId == selectedCityId }
            }
            3 -> posts.filter { it.isBookmarked }
            else -> posts
        }
    }

    val filteredTransit = remember(transitRoutes, selectedCityId, selectedTransitMode) {
        transitRoutes.filter { route ->
            val cityMatches = selectedCityId == null || route.cityId == selectedCityId
            val modeMatches = selectedTransitMode == null || route.mode == selectedTransitMode
            cityMatches && modeMatches
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("townsquares_app_screen"),
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Townsquares",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = WarmAmber.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, WarmAmber.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = "PROTOTYPE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WarmAmber,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "World Cities Social Feeds & Transit Networks",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonCyan
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("townsquares_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { isCreatePostOpen = true },
                        modifier = Modifier.testTag("townsquares_create_post_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Post Experience",
                            tint = NeonCyan,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurfaceElevated)
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { isCreatePostOpen = true },
                    containerColor = NeonCyan,
                    contentColor = Color(0xFF003544),
                    modifier = Modifier.testTag("townsquares_fab_post")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Post Experience", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. WORLD CITIES HORIZONTAL SELECTOR
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    val isAllSelected = selectedCityId == null
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isAllSelected) NeonCyan else DarkSurfaceElevated,
                        border = BorderStroke(1.dp, if (isAllSelected) NeonCyan else DarkBorder),
                        modifier = Modifier
                            .clickable { selectedCityId = null }
                            .testTag("city_chip_all")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🌍", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "All Cities",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAllSelected) Color(0xFF003544) else Color.White
                            )
                        }
                    }
                }

                items(cities, key = { it.id }) { city ->
                    val isSelected = selectedCityId == city.id
                    val cityColor = remember(city.accentColorHex) { Color(city.accentColorHex) }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) cityColor else DarkSurfaceElevated,
                        border = BorderStroke(1.dp, if (isSelected) cityColor else DarkBorder),
                        modifier = Modifier
                            .clickable { selectedCityId = city.id }
                            .testTag("city_chip_${city.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(city.flagEmoji, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = city.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color(0xFF070B12) else Color.White
                                )
                                Text(
                                    text = "${city.currentTemp} • ${city.localTime}",
                                    fontSize = 10.sp,
                                    color = if (isSelected) Color(0xFF070B12).copy(alpha = 0.8f) else DarkTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // 2. MAIN TABS ROW
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkSurfaceElevated,
                contentColor = NeonCyan,
                edgePadding = 8.dp,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = NeonCyan
                        )
                    }
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🏙️ City Feed")
                            if (filteredPosts.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("(${filteredPosts.size})", fontSize = 11.sp, color = NeonCyan)
                            }
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🧭 Tour Guides")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("NEW", fontSize = 9.sp, fontWeight = FontWeight.Black, color = WarmAmber)
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🚇 Transit Routes")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("(${filteredTransit.size})", fontSize = 11.sp, color = WarmAmber)
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("🔖 Bookmarks") }
                )
                Tab(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    text = { Text("🌐 World Hubs") }
                )
            }

            // 3. TAB CONTENT
            when (selectedTab) {
                0, 3 -> {
                    // CITY SOCIAL EXPERIENCES FEED
                    if (filteredPosts.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🏙️", fontSize = 48.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = if (selectedTab == 3) "No bookmarked stories yet" else "No posts for this city yet",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Be the first to share your traveler memories!",
                                    color = DarkTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(filteredPosts, key = { it.id }) { post ->
                                CityExperiencePostCard(
                                    post = post,
                                    onToggleLike = {
                                        posts = posts.map { p ->
                                            if (p.id == post.id) {
                                                val newLiked = !p.isLiked
                                                p.copy(
                                                    isLiked = newLiked,
                                                    likesCount = if (newLiked) p.likesCount + 1 else (p.likesCount - 1).coerceAtLeast(0)
                                                )
                                            } else p
                                        }
                                    },
                                    onToggleBookmark = {
                                        posts = posts.map { p ->
                                            if (p.id == post.id) p.copy(isBookmarked = !p.isBookmarked) else p
                                        }
                                        Toast.makeText(
                                            context,
                                            if (!post.isBookmarked) "Saved to bookmarks 🔖" else "Removed from bookmarks",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    onOpenComments = {
                                        activeCommentPost = post
                                    }
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    }
                }

                1 -> {
                    // DETAILED TOUR GUIDES & WALKING ITINERARIES
                    TownsquaresTourGuidesSection(
                        selectedCityId = selectedCityId,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                2 -> {
                    // TRANSIT ROUTES & METRO EXPLORER
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Transit Mode Filter Row
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                FilterChip(
                                    selected = selectedTransitMode == null,
                                    onClick = { selectedTransitMode = null },
                                    label = { Text("All Modes", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NeonCyan.copy(alpha = 0.2f),
                                        selectedLabelColor = NeonCyan
                                    )
                                )
                            }
                            items(TransitMode.entries) { mode ->
                                val isSelected = selectedTransitMode == mode
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedTransitMode = if (isSelected) null else mode },
                                    label = { Text("${mode.emoji} ${mode.label}", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = WarmAmber.copy(alpha = 0.2f),
                                        selectedLabelColor = WarmAmber
                                    )
                                )
                            }
                        }

                        if (filteredTransit.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No transit routes found for this filter", color = DarkTextSecondary)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                items(filteredTransit, key = { it.id }) { route ->
                                    CityTransitRouteCard(route = route)
                                }
                                item {
                                    Spacer(modifier = Modifier.height(80.dp))
                                }
                            }
                        }
                    }
                }

                4 -> {
                    // WORLD CITY HUBS DIRECTORY
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(cities, key = { it.id }) { city ->
                            val cityColor = remember(city.accentColorHex) { Color(city.accentColorHex) }
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = DarkSurface,
                                border = BorderStroke(1.dp, DarkBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedCityId = city.id
                                        selectedTab = 0
                                    }
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(city.flagEmoji, fontSize = 28.sp)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(text = city.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                Text(text = "${city.country} • ${city.timezone}", fontSize = 11.sp, color = DarkTextSecondary)
                                            }
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = cityColor.copy(alpha = 0.15f),
                                            border = BorderStroke(1.dp, cityColor.copy(alpha = 0.4f))
                                        ) {
                                            Text(
                                                text = "${city.weatherEmoji} ${city.currentTemp}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = cityColor,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(text = city.skylineVibe, fontSize = 12.sp, color = DarkTextMuted)
                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Key Districts
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        items(city.popularDistricts) { district ->
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = DarkSurfaceElevated
                                            ) {
                                                Text(text = "#$district", fontSize = 10.sp, color = NeonCyan, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${city.activePostsCount} community traveler posts",
                                            fontSize = 11.sp,
                                            color = WarmAmber
                                        )
                                        Text(
                                            text = "Explore City Feed →",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NeonCyan
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // CREATE POST MODAL DIALOG
    if (isCreatePostOpen) {
        CreateCityPostDialog(
            cities = cities,
            defaultCityId = selectedCityId ?: "tokyo",
            onDismiss = { isCreatePostOpen = false },
            onSubmit = { newPost ->
                posts = listOf(newPost) + posts
                isCreatePostOpen = false
                selectedCityId = newPost.cityId
                selectedTab = 0
                Toast.makeText(context, "Experience posted to ${newPost.cityName}! ✨", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // COMMENTS MODAL DIALOG
    activeCommentPost?.let { post ->
        val comments = postCommentsMap[post.id] ?: emptyList()
        PostCommentsDialog(
            post = post,
            comments = comments,
            onDismiss = { activeCommentPost = null },
            onAddComment = { newText ->
                val newComment = PostComment(
                    authorName = "You (Citizen Traveler)",
                    authorHandle = "@you_citizen",
                    avatarEmoji = "🎒",
                    text = newText
                )
                val updated = comments + newComment
                val newMap = postCommentsMap.toMutableMap()
                newMap[post.id] = updated
                postCommentsMap = newMap
                posts = posts.map { p ->
                    if (p.id == post.id) p.copy(commentsCount = p.commentsCount + 1) else p
                }
            }
        )
    }
}

// ==========================================
// SUBCOMPONENTS: POST CARD & TRANSIT CARD
// ==========================================

@Composable
fun CityExperiencePostCard(
    post: CityExperiencePost,
    onToggleLike: () -> Unit,
    onToggleBookmark: () -> Unit,
    onOpenComments: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = DarkSurface,
        border = BorderStroke(1.dp, DarkBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("city_post_card_${post.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Author & City Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = DarkSurfaceElevated,
                        border = BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = post.authorAvatarEmoji, fontSize = 20.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = post.authorName,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = post.authorHandle,
                                fontSize = 11.sp,
                                color = DarkTextSecondary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("•", color = DarkTextSecondary, fontSize = 10.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = post.timestamp,
                                fontSize = 11.sp,
                                color = DarkTextSecondary
                            )
                        }
                    }
                }

                // City & District Tag
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = NeonCyan.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📍 ${post.district}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Post Title & Story
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = post.highlightEmoji, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = post.storyText,
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 19.sp),
                color = DarkTextMuted
            )

            // Traveler Tip Box
            if (post.travelerTip.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF1B2333),
                    border = BorderStroke(1.dp, WarmAmber.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Top) {
                        Text("💡", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "LOCAL TRAVELER TIP",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = WarmAmber,
                                letterSpacing = 0.8.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = post.travelerTip,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tags
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                item {
                    Surface(shape = RoundedCornerShape(6.dp), color = DarkSurfaceElevated) {
                        Text(text = "⏰ ${post.bestTimeOfDay}", fontSize = 10.sp, color = WarmAmber, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
                items(post.categoryTags) { tag ->
                    Surface(shape = RoundedCornerShape(6.dp), color = DarkSurfaceElevated) {
                        Text(text = tag, fontSize = 10.sp, color = NeonCyan, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = DarkBorder)
            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons Row (Likes, Comments, Bookmark, Share)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Like button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onToggleLike() }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (post.isLiked) CoralRed else DarkTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = post.likesCount.toString(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (post.isLiked) CoralRed else DarkTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Comment button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onOpenComments() }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = "Comments",
                            tint = DarkTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = post.commentsCount.toString(),
                            fontSize = 12.sp,
                            color = DarkTextSecondary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onToggleBookmark, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = if (post.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (post.isBookmarked) NeonCyan else DarkTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CityTransitRouteCard(route: CityTransitRoute) {
    val routeColor = remember(route.colorHex) { Color(route.colorHex) }
    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = DarkSurface,
        border = BorderStroke(1.dp, routeColor.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("transit_route_card_${route.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Route Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Line Code Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = routeColor,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = route.lineCode,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = route.lineName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "${route.mode.emoji} ${route.mode.label} • ${route.cityName}",
                            fontSize = 11.sp,
                            color = DarkTextSecondary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = routeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = route.scenicRating,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Origin -> Destination Tracker
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurfaceElevated,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("ORIGIN", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = routeColor)
                        Text(route.startStation, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1)
                    }
                    Text(" ➔ ", color = routeColor, fontWeight = FontWeight.Black, fontSize = 14.sp)
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                        Text("TERMINUS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = routeColor)
                        Text(route.endStation, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stats row (Frequency, Travel time)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = DarkTextSecondary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Freq: ${route.frequencyMinutes}", fontSize = 11.sp, color = DarkTextSecondary)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Speed, contentDescription = null, tint = DarkTextSecondary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Time: ${route.travelTimeMinutes}", fontSize = 11.sp, color = DarkTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Insider Traveler Tip
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF131F2E),
                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Top) {
                    Text("💡", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("TRANSIT INSIDER TIP", fontSize = 9.sp, fontWeight = FontWeight.Black, color = NeonCyan)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(route.insiderTip, fontSize = 11.sp, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Toggle Station Stops
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExpanded) "Hide Key Stations" else "View Key Stations (${route.keyStops.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = routeColor
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = routeColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text("KEY STOPS ALONG ROUTE:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DarkTextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    route.keyStops.forEachIndexed { index, stop ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = routeColor,
                                modifier = Modifier.size(8.dp)
                            ) {}
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${index + 1}. $stop",
                                fontSize = 11.sp,
                                color = Color.White
                            )
                        }
                    }

                    if (route.transfersAvailable.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("TRANSFERS:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DarkTextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(route.transfersAvailable) { transfer ->
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = DarkSurfaceElevated
                                ) {
                                    Text(
                                        text = "⚡ $transfer",
                                        fontSize = 10.sp,
                                        color = WarmAmber,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// CREATE CITY EXPERIENCE POST DIALOG
// ==========================================

@Composable
fun CreateCityPostDialog(
    cities: List<WorldCity>,
    defaultCityId: String,
    onDismiss: () -> Unit,
    onSubmit: (CityExperiencePost) -> Unit
) {
    var selectedCity by remember { mutableStateOf(cities.find { it.id == defaultCityId } ?: cities.first()) }
    var districtInput by remember { mutableStateOf(selectedCity.popularDistricts.firstOrNull() ?: "") }
    var titleInput by remember { mutableStateOf("") }
    var storyInput by remember { mutableStateOf("") }
    var tipInput by remember { mutableStateOf("") }
    var bestTimeInput by remember { mutableStateOf("Twilight") }
    var tagsInput by remember { mutableStateOf("#CityWalk #HiddenGem #LocalCulture") }
    var emojiInput by remember { mutableStateOf("✨") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.90f),
            shape = RoundedCornerShape(20.dp),
            color = DarkBg,
            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Post City Experience", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        Text("Share memories, cafes & hidden spots", fontSize = 11.sp, color = NeonCyan)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // City selector
                    item {
                        Text("1. SELECT WORLD CITY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(cities) { city ->
                                val isSelected = selectedCity.id == city.id
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) NeonCyan else DarkSurface,
                                    border = BorderStroke(1.dp, if (isSelected) NeonCyan else DarkBorder),
                                    modifier = Modifier.clickable {
                                        selectedCity = city
                                        districtInput = city.popularDistricts.firstOrNull() ?: ""
                                    }
                                ) {
                                    Text(
                                        text = "${city.flagEmoji} ${city.name}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color(0xFF003544) else Color.White,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    // District input
                    item {
                        Text("2. NEIGHBORHOOD / DISTRICT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = districtInput,
                            onValueChange = { districtInput = it },
                            placeholder = { Text("e.g. Shibuya, Montmartre, Greenwich Village...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan, unfocusedBorderColor = DarkBorder)
                        )
                    }

                    // Title input
                    item {
                        Text("3. EXPERIENCE HEADLINE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = titleInput,
                            onValueChange = { titleInput = it },
                            placeholder = { Text("e.g. Rainy Soba Bar behind the Temple...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan, unfocusedBorderColor = DarkBorder)
                        )
                    }

                    // Story narrative
                    item {
                        Text("4. YOUR EXPERIENCE STORY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = storyInput,
                            onValueChange = { storyInput = it },
                            placeholder = { Text("Describe the sights, sounds, smells, atmosphere, and what made it memorable...") },
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            maxLines = 6,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan, unfocusedBorderColor = DarkBorder)
                        )
                    }

                    // Traveler Tip
                    item {
                        Text("5. INSIDER TIP FOR TRAVELERS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = WarmAmber)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = tipInput,
                            onValueChange = { tipInput = it },
                            placeholder = { Text("e.g. Visit at sunset, bring cash, take exit 4 from metro...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = WarmAmber, unfocusedBorderColor = DarkBorder)
                        )
                    }

                    // Best time of day & Tags
                    item {
                        Text("6. BEST TIME & TAGS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = bestTimeInput,
                                onValueChange = { bestTimeInput = it },
                                label = { Text("Best Time") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan, unfocusedBorderColor = DarkBorder)
                            )
                            OutlinedTextField(
                                value = emojiInput,
                                onValueChange = { emojiInput = it },
                                label = { Text("Vibe Icon") },
                                modifier = Modifier.width(80.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan, unfocusedBorderColor = DarkBorder)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = tagsInput,
                            onValueChange = { tagsInput = it },
                            label = { Text("Tags (#tag #tag)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan, unfocusedBorderColor = DarkBorder)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Submit Button
                Button(
                    onClick = {
                        if (titleInput.isBlank() || storyInput.isBlank()) {
                            return@Button
                        }
                        val parsedTags = tagsInput.split(" ").filter { it.isNotBlank() }
                        val post = CityExperiencePost(
                            authorName = "You (Citizen Traveler)",
                            authorHandle = "@you_citizen",
                            authorAvatarEmoji = "🎒",
                            cityId = selectedCity.id,
                            cityName = selectedCity.name,
                            country = selectedCity.country,
                            district = if (districtInput.isNotBlank()) districtInput else selectedCity.name,
                            title = titleInput,
                            storyText = storyInput,
                            travelerTip = tipInput,
                            bestTimeOfDay = bestTimeInput,
                            categoryTags = if (parsedTags.isNotEmpty()) parsedTags else listOf("#CityExperience"),
                            timestamp = "Just now",
                            highlightEmoji = emojiInput
                        )
                        onSubmit(post)
                    },
                    enabled = titleInput.isNotBlank() && storyInput.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Publish City Story ✨", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

// ==========================================
// COMMENTS DIALOG
// ==========================================

@Composable
fun PostCommentsDialog(
    post: CityExperiencePost,
    comments: List<PostComment>,
    onDismiss: () -> Unit,
    onAddComment: (String) -> Unit
) {
    var newCommentText by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.75f),
            shape = RoundedCornerShape(18.dp),
            color = DarkBg,
            border = BorderStroke(1.dp, DarkBorder)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Traveler Comments", fontWeight = FontWeight.Bold, color = Color.White)
                        Text(post.title, fontSize = 11.sp, color = NeonCyan, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 10.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (comments.isEmpty()) {
                        item {
                            Text("No comments yet. Share your experience or ask a question!", fontSize = 12.sp, color = DarkTextSecondary)
                        }
                    }
                    items(comments, key = { it.id }) { comment ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Top) {
                                Text(comment.avatarEmoji, fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(comment.authorName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(comment.timestamp, fontSize = 10.sp, color = DarkTextSecondary)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(comment.text, fontSize = 12.sp, color = DarkTextMuted)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newCommentText,
                        onValueChange = { newCommentText = it },
                        placeholder = { Text("Write a comment or tip...", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan, unfocusedBorderColor = DarkBorder)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (newCommentText.isNotBlank()) {
                                onAddComment(newCommentText)
                                newCommentText = ""
                            }
                        },
                        enabled = newCommentText.isNotBlank(),
                        colors = IconButtonDefaults.iconButtonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544))
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
