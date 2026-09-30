package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioState
import com.example.data.model.MediaItemEntity
import com.example.data.model.MediaSpaceEntity
import com.example.data.model.MediaType
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material.icons.filled.Mic
import com.example.ui.components.MediaCardItem
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber

@Composable
fun ProfileSpacesScreen(
    userSpaces: List<MediaSpaceEntity>,
    userCreatedItems: List<MediaItemEntity>,
    bookmarkedItems: List<MediaItemEntity>,
    audioState: AudioState,
    onOpenCreateSpace: () -> Unit,
    onOpenCreateContent: () -> Unit,
    onOpenReader: (MediaItemEntity) -> Unit,
    onPlayAudio: (MediaItemEntity) -> Unit,
    onToggleLike: (MediaItemEntity) -> Unit,
    onToggleBookmark: (MediaItemEntity) -> Unit,
    onVoiceNarrate: ((MediaItemEntity) -> Unit)? = null,
    onShare: ((MediaItemEntity) -> Unit)? = null,
    onToggleSavedOffline: ((MediaItemEntity) -> Unit)? = null,
    onOpenSettings: () -> Unit = {},
    onOpenVoiceBuilder: () -> Unit = {},
    onSelectSpace: (Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: All, 1: Posts, 2: Newsletters, 3: Articles, 4: Audio, 5: Bookmarks
    var mainProfileModule by remember { mutableIntStateOf(0) } // 0: Created Content Wall, 1: Personal Finance, 2: Health & Well-being, 3: State & Civic Pass

    val totalSubscribers = userSpaces.sumOf { it.subscriberCount }
    val totalPublished = userCreatedItems.size

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("profile_spaces_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile 3D ID Card
            item {
                Profile3DIDCard(
                    onOpenSettings = onOpenSettings,
                    subscriberCount = totalSubscribers,
                    publishedCount = totalPublished,
                    spacesCount = userSpaces.size
                )
            }
            
            // Train Voice Model Action
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = NeonCyan.copy(alpha = 0.1f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenVoiceBuilder() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = NeonCyan.copy(alpha = 0.2f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "AI Voice Builder",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = NeonCyan
                            )
                            Text(
                                text = "Train a custom voice model for narration",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Train Model",
                            tint = NeonCyan
                        )
                    }
                }
            }
            
            // Action buttons
            item {
                OutlinedButton(
                    onClick = onOpenCreateSpace,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth().testTag("profile_new_space_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Media Space")
                }
            }

            // "My Media Spaces" Showcase
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MY MEDIA SPACES (${userSpaces.size})",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.1.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "+ New Space",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = NeonCyan,
                            modifier = Modifier
                                .clickable { onOpenCreateSpace() }
                                .padding(4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(userSpaces, key = { it.id }) { space ->
                            Card(
                                modifier = Modifier
                                    .width(220.dp)
                                    .testTag("user_space_card_${space.id}")
                                    .clickable { onSelectSpace(space.id) },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    Color(space.accentColorHex).copy(alpha = 0.4f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = Color(space.accentColorHex).copy(alpha = 0.2f),
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Hub,
                                                    contentDescription = null,
                                                    tint = Color(space.accentColorHex),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(space.accentColorHex).copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = "${space.subscriberCount} subs",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(space.accentColorHex),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = space.title,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = space.handle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = space.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Unified Personal Profile Subapp Modules
            item {
                Column {
                    Text(
                        text = "UNIFIED PERSONAL PROFILE MODULES",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.1.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val profileModules = listOf(
                            Pair(0, "🖼️ Content Wall"),
                            Pair(1, "💳 Finance"),
                            Pair(2, "🫀 Health"),
                            Pair(3, "🏛️ State Pass")
                        )
                        profileModules.forEach { (modId, label) ->
                            val isSelected = mainProfileModule == modId
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) NeonCyan else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { mainProfileModule = modId }
                                    .testTag("profile_module_tab_$modId")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) Color(0xFF003544) else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (mainProfileModule == 1) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(680.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WarmAmber.copy(alpha = 0.5f))
                    ) {
                        com.example.ui.plus.finance.TownsquareFinanceApp(onBack = { mainProfileModule = 0 })
                    }
                }
            } else if (mainProfileModule == 2) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(680.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
                    ) {
                        com.example.ui.plus.health.TownsquareHealthApp(onBack = { mainProfileModule = 0 })
                    }
                }
            } else if (mainProfileModule == 3) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(680.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f))
                    ) {
                        com.example.ui.plus.state.TownsquareStateApp(onBack = { mainProfileModule = 0 })
                    }
                }
            } else {
                // Default: Published Works Content Wall
                item {
                    val tabLabels = listOf(
                        "All My Works (${userCreatedItems.size})",
                        "💬 Posts",
                        "✉️ Newsletters",
                        "📰 Articles",
                        "📻 Audio",
                        "🔖 Saved (${bookmarkedItems.size})"
                    )

                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 0.dp,
                    containerColor = Color.Transparent,
                    contentColor = NeonCyan,
                    indicator = { tabPositions ->
                        if (selectedTab < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = NeonCyan
                            )
                        }
                    },
                    divider = {}
                ) {
                    tabLabels.forEachIndexed { index, label ->
                        val isSelected = selectedTab == index
                        Tab(
                            selected = isSelected,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) NeonCyan else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            modifier = Modifier.testTag("profile_tab_$index")
                        )
                    }
                }
            }

            // Filtered Items List
            val displayedItems = when (selectedTab) {
                0 -> userCreatedItems
                1 -> userCreatedItems.filter { it.type == MediaType.SOCIAL_POST.name }
                2 -> userCreatedItems.filter { it.type == MediaType.NEWSLETTER.name }
                3 -> userCreatedItems.filter { it.type == MediaType.NEWSPAPER_MAGAZINE.name }
                4 -> userCreatedItems.filter { it.type == MediaType.RADIO_STATION.name || it.type == MediaType.PODCAST_EPISODE.name }
                5 -> bookmarkedItems
                else -> userCreatedItems
            }

            if (displayedItems.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "✍️", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (selectedTab == 5) "No Bookmarks Yet" else "No Content In This Section",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (selectedTab == 5) "Bookmark articles and newsletters to read later." else "Tap 'New Content' to publish your first piece!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(displayedItems, key = { it.id }) { item ->
                    MediaCardItem(
                        item = item,
                        audioState = audioState,
                        onOpenReader = onOpenReader,
                        onPlayAudio = onPlayAudio,
                        onToggleLike = onToggleLike,
                        onToggleBookmark = onToggleBookmark,
                        onVoiceNarrate = onVoiceNarrate,
                        onShare = onShare,
                        onToggleSavedOffline = onToggleSavedOffline
                    )
                }
            }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun Profile3DIDCard(
    onOpenSettings: () -> Unit,
    subscriberCount: Int,
    publishedCount: Int,
    spacesCount: Int,
    modifier: Modifier = Modifier
) {
    var rotateX by androidx.compose.runtime.mutableFloatStateOf(0f)
    var rotateY by androidx.compose.runtime.mutableFloatStateOf(0f)
    
    val animatedRotateX by animateFloatAsState(targetValue = rotateX, animationSpec = tween(150), label = "rotX")
    val animatedRotateY by animateFloatAsState(targetValue = rotateY, animationSpec = tween(150), label = "rotY")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .graphicsLayer {
                rotationX = animatedRotateX
                rotationY = animatedRotateY
                cameraDistance = 12f * density
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        rotateX = 0f
                        rotateY = 0f
                    },
                    onDragCancel = {
                        rotateX = 0f
                        rotateY = 0f
                    }
                ) { change, dragAmount ->
                    change.consume()
                    rotateX = (rotateX - dragAmount.y * 0.3f).coerceIn(-20f, 20f)
                    rotateY = (rotateY + dragAmount.x * 0.3f).coerceIn(-20f, 20f)
                }
            },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(18.dp).fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = NeonCyan,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "AC",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF003544)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Alex Chen",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "@alexchen",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeonCyan
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Independent curator",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                androidx.compose.material3.IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier.testTag("profile_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = spacesCount.toString(),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Spaces",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$subscriberCount",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = WarmAmber
                    )
                    Text(
                        text = "Subscribers",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = publishedCount.toString(),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = NeonCyan
                    )
                    Text(
                        text = "Creations",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
