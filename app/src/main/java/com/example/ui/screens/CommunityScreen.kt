package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CommunityCommentsDialog
import com.example.ui.components.CreateCommunityPostDialog
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber

data class FandomHub(
    val id: String,
    val name: String,
    val emoji: String,
    val description: String,
    var followerCount: Int,
    var isFollowed: Boolean = false,
    val categoryTag: String
)

data class CommunityPost(
    val id: String,
    val author: String,
    val category: String = "Local News",
    val fandomId: String? = null,
    val content: String,
    val timeAgo: String,
    var likes: Int,
    var comments: Int,
    var isLiked: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    onOpenSidebar: () -> Unit,
    onBack: () -> Unit = {}
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    // Fandom Hubs State
    var fandoms by remember {
        mutableStateOf(
            listOf(
                FandomHub("fandom_gaming", "Retro Gaming Guild", "🎮", "Classics, speedruns, arcade cabinets, and ROM hacks.", 8420, true, "Retro Gaming"),
                FandomHub("fandom_scifi", "Sci-Fi & Cyberpunk Alliance", "🚀", "Retrowave, futuristic lore, dystopian dispatches & space opera.", 12150, true, "Sci-Fi"),
                FandomHub("fandom_anime", "Anime & Cosplay Syndicate", "⛩️", "Otaku discussion, cosplay craft, manga reviews & fan art.", 15600, false, "Anime & Cosplay"),
                FandomHub("fandom_lit", "Victorian Literature Club", "📜", "Bookworm discussions, gothic horror, 19th-century classics.", 6240, true, "Literature"),
                FandomHub("fandom_film", "Classic Cinema & Film Noir", "🎬", "35mm film analysis, golden age cinema, director spotlights.", 4920, false, "Classic Cinema"),
                FandomHub("fandom_cartoons", "Syndicated Cartoons & Memes", "🦸", "Sunday funnies lore, comic strips, animated series & fan memes.", 9810, false, "Cartoons")
            )
        )
    }

    var posts by remember {
        mutableStateOf(
            listOf(
                CommunityPost("1", "Alice Smith", "Local News", null, "Just had an amazing time at the local farmer's market! 🍎🥕 Great produce from Oak Valley farms.", "2h ago", 14, 3),
                CommunityPost("2", "Gamer_Nostalgia", "Retro Gaming", "fandom_gaming", "Just finished a zero-damage speedrun of Chrono Trigger on CRT TV! 🎮 What is your favorite 16-bit RPG soundtrack?", "3h ago", 48, 12),
                CommunityPost("3", "CyberPioneer_99", "Sci-Fi", "fandom_scifi", "Analyzed the world-building in Blade Runner & Neuromancer. 🚀 How do you feel about retro-futuristic UI aesthetics?", "4h ago", 65, 18),
                CommunityPost("4", "Bookworm_Julia", "Literature", "fandom_lit", "Started re-reading Pride & Prejudice alongside 1984. The contrast between Victorian social satire and totalitarian dystopian wire reports is fascinating! 📜", "5h ago", 32, 7),
                CommunityPost("5", "Bob Johnson", "Discussion", null, "Does anyone know a reputable local plumber in the Riverside district? Need radiator servicing.", "6h ago", 4, 8),
                CommunityPost("6", "Cosplay_Craft_Anna", "Anime & Cosplay", "fandom_anime", "Finished sewing my retro mech armor cosplay for the upcoming Townsquare Expo! ⛩️ Full gallery drops tonight.", "7h ago", 89, 21)
            )
        )
    }

    var isCreatePostOpen by remember { mutableStateOf(false) }
    var activeCommentingPost by remember { mutableStateOf<CommunityPost?>(null) }
    var isSearchVisible by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ALL") }

    val categories = listOf("ALL", "Followed Fandoms", "Local News", "Retro Gaming", "Sci-Fi", "Anime & Cosplay", "Literature", "Classic Cinema", "Cartoons", "Discussion")

    val filteredPosts = remember(posts, searchQuery, selectedCategory, fandoms) {
        val followedFandomIds = fandoms.filter { it.isFollowed }.map { it.id }.toSet()
        posts.filter { post ->
            val matchesCategory = when (selectedCategory) {
                "ALL" -> true
                "Followed Fandoms" -> post.fandomId != null && followedFandomIds.contains(post.fandomId)
                else -> post.category.equals(selectedCategory, ignoreCase = true)
            }
            val matchesSearch = searchQuery.isBlank() ||
                    post.content.contains(searchQuery, ignoreCase = true) ||
                    post.author.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Community Wire & Fandom Hubs", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text("Civic Discussions & Fan Guilds", style = MaterialTheme.typography.labelSmall, color = NeonCyan)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onOpenSidebar, modifier = Modifier.testTag("community_sidebar_btn")) {
                        Icon(Icons.Default.Menu, contentDescription = "Open Sidebar")
                    }
                },
                actions = {
                    IconButton(onClick = { isSearchVisible = !isSearchVisible }) {
                        Icon(
                            imageVector = if (isSearchVisible) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isCreatePostOpen = true },
                containerColor = NeonCyan,
                contentColor = Color(0xFF003544),
                modifier = Modifier.testTag("new_community_post_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Post")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search field
            AnimatedVisibility(visible = isSearchVisible) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search community dispatches & fandoms...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // FOLLOWABLE FANDOM HUBS CAROUSEL
            Surface(
                color = DarkSurfaceElevated,
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("✨", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("FOLLOWABLE FANDOM HUBS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp), color = WarmAmber)
                        }
                        Text("${fandoms.count { it.isFollowed }} Followed", fontSize = 11.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(fandoms, key = { it.id }) { fandom ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (fandom.isFollowed) NeonCyan.copy(alpha = 0.15f) else DarkSurface,
                                border = BorderStroke(1.dp, if (fandom.isFollowed) NeonCyan else DarkBorder),
                                modifier = Modifier
                                    .width(200.dp)
                                    .clickable {
                                        selectedCategory = fandom.categoryTag
                                    }
                                    .testTag("fandom_card_${fandom.id}")
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(fandom.emoji, fontSize = 22.sp)
                                        Button(
                                            onClick = {
                                                fandoms = fandoms.map {
                                                    if (it.id == fandom.id) {
                                                        val nextFollowed = !it.isFollowed
                                                        it.copy(
                                                            isFollowed = nextFollowed,
                                                            followerCount = if (nextFollowed) it.followerCount + 1 else it.followerCount - 1
                                                        )
                                                    } else it
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (fandom.isFollowed) Color.DarkGray else NeonCyan,
                                                contentColor = if (fandom.isFollowed) Color.White else Color(0xFF003544)
                                            ),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text(if (fandom.isFollowed) "✓ Following" else "+ Follow", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(fandom.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White, maxLines = 1)
                                    Text("${fandom.followerCount} Fans • ${fandom.description}", fontSize = 10.sp, color = Color.LightGray, maxLines = 2, lineHeight = 13.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Category filter chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonCyan,
                            selectedLabelColor = Color(0xFF003544)
                        )
                    )
                }
            }

            // Posts list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("community_post_list"),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredPosts, key = { it.id }) { post ->
                    CommunityPostCard(
                        post = post,
                        fandom = fandoms.find { it.id == post.fandomId },
                        onToggleLike = {
                            posts = posts.map {
                                if (it.id == post.id) {
                                    it.copy(
                                        isLiked = !it.isLiked,
                                        likes = if (it.isLiked) it.likes - 1 else it.likes + 1
                                    )
                                } else it
                            }
                        },
                        onOpenComments = {
                            activeCommentingPost = post
                        },
                        onShare = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "💬 [${post.category}] ${post.author}: \"${post.content}\"\n— via Townsquare Community Fandom Wire")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Dispatch"))
                        }
                    )
                }
            }
        }
    }

    if (isCreatePostOpen) {
        CreateCommunityPostDialog(
            isOpen = isCreatePostOpen,
            fandoms = fandoms,
            onClose = { isCreatePostOpen = false },
            onSubmit = { author, category, fandomId, content ->
                val newPost = CommunityPost(
                    id = "post_${System.currentTimeMillis()}",
                    author = author,
                    category = category,
                    fandomId = fandomId,
                    content = content,
                    timeAgo = "Just now",
                    likes = 0,
                    comments = 0
                )
                posts = listOf(newPost) + posts
            }
        )
    }

    activeCommentingPost?.let { post ->
        CommunityCommentsDialog(
            isOpen = true,
            postAuthor = post.author,
            postText = post.content,
            onClose = { activeCommentingPost = null },
            onCommentAdded = {
                posts = posts.map {
                    if (it.id == post.id) it.copy(comments = it.comments + 1) else it
                }
            }
        )
    }
}

@Composable
fun CommunityPostCard(
    post: CommunityPost,
    fandom: FandomHub? = null,
    onToggleLike: () -> Unit,
    onOpenComments: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, DarkBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
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
                            Text(
                                text = post.author.first().toString(),
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan,
                                fontSize = 16.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = post.author,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = post.timeAgo,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (fandom != null) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = WarmAmber.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, WarmAmber.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "${fandom.emoji} ${fandom.name}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                color = WarmAmber,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = post.category,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = DarkBorder)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TextButton(onClick = onToggleLike) {
                    Icon(
                        imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.isLiked) Color(0xFFFF453A) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = post.likes.toString(),
                        color = if (post.isLiked) Color(0xFFFF453A) else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }

                TextButton(onClick = onOpenComments) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Comment",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = post.comments.toString(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }

                TextButton(onClick = onShare) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Share",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
