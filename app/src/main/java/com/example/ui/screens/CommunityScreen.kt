package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.NeonCyan

data class CommunityPost(
    val id: String,
    val author: String,
    val category: String = "Local News",
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

    var posts by remember {
        mutableStateOf(
            listOf(
                CommunityPost("1", "Alice Smith", "Local News", "Just had an amazing time at the local farmer's market! 🍎🥕 Great produce from Oak Valley farms.", "2h ago", 14, 3),
                CommunityPost("2", "Bob Johnson", "Discussion", "Does anyone know a reputable local plumber in the Riverside district? Need radiator servicing.", "4h ago", 4, 8),
                CommunityPost("3", "Townsquare Update", "Civic Notice", "Join us for the municipal town hall session tomorrow at 6 PM at the Civic Auditorium. Streaming live on TCTV-4.", "5h ago", 52, 16),
                CommunityPost("4", "David K.", "Events", "Annual Autumn Book Swap & Vinyl Fair taking place this Saturday at Central Library Plaza!", "7h ago", 29, 6)
            )
        )
    }

    var isCreatePostOpen by remember { mutableStateOf(false) }
    var activeCommentingPost by remember { mutableStateOf<CommunityPost?>(null) }
    var isSearchVisible by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ALL") }

    val categories = listOf("ALL", "Local News", "Events", "Discussion", "Civic Notice")

    val filteredPosts = remember(posts, searchQuery, selectedCategory) {
        posts.filter { post ->
            val matchesCategory = selectedCategory == "ALL" || post.category.equals(selectedCategory, ignoreCase = true)
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
                        Text("Community Wire", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text("Hyperlocal Civic Discussions", style = MaterialTheme.typography.labelSmall, color = NeonCyan)
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
                        placeholder = { Text("Search community dispatches...") },
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
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredPosts, key = { it.id }) { post ->
                    CommunityPostCard(
                        post = post,
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
                                putExtra(Intent.EXTRA_TEXT, "💬 [${post.category}] ${post.author}: \"${post.content}\"\n— via Townsquare Community Wire")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Community Dispatch"))
                        }
                    )
                }
            }
        }
    }

    if (isCreatePostOpen) {
        CreateCommunityPostDialog(
            isOpen = isCreatePostOpen,
            onClose = { isCreatePostOpen = false },
            onSubmit = { author, category, content ->
                val newPost = CommunityPost(
                    id = "post_${System.currentTimeMillis()}",
                    author = author,
                    category = category,
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
    onToggleLike: () -> Unit,
    onOpenComments: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
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
