package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.example.data.model.LocalBulletinEntity
import com.example.data.model.MediaChannelEntity
import com.example.data.model.MediaItemEntity
import com.example.data.model.MediaType
import com.example.ui.components.Headline
import com.example.ui.components.LocalBulletinsSection
import com.example.ui.components.MediaCardItem
import com.example.ui.components.MorningBriefCard
import com.example.ui.components.NewsTicker
import com.example.ui.components.TownsquareTopBar
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainFeedScreen(
    items: List<MediaItemEntity>,
    channels: List<MediaChannelEntity>,
    bulletins: List<LocalBulletinEntity> = emptyList(),
    selectedBulletinCategory: String? = null,
    selectedChannelFilter: String?,
    selectedTypeFilter: MediaType?,
    searchQuery: String,
    audioState: AudioState,
    isRefreshing: Boolean = false,
    refreshStatusText: String? = null,
    isOfflineMode: Boolean = false,
    onToggleOfflineMode: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onSelectBulletinCategory: (String?) -> Unit = {},
    onOpenReportBulletin: () -> Unit = {},
    onToggleBulletinUpvote: (LocalBulletinEntity) -> Unit = {},
    onDeleteBulletin: (Long) -> Unit = {},
    onShareBulletin: (LocalBulletinEntity) -> Unit = {},
    onSelectChannelFilter: (String?) -> Unit,
    onSelectTypeFilter: (MediaType?) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    isForYouFeed: Boolean = false,
    onToggleForYouFeed: (Boolean) -> Unit = {},
    onOpenReader: (MediaItemEntity) -> Unit,
    onPlayAudio: (MediaItemEntity) -> Unit,
    onToggleLike: (MediaItemEntity) -> Unit,
    onToggleBookmark: (MediaItemEntity) -> Unit,
    onVoiceNarrate: ((MediaItemEntity) -> Unit)? = null,
    onShare: ((MediaItemEntity) -> Unit)? = null,
    onToggleSavedOffline: ((MediaItemEntity) -> Unit)? = null,
    onReportContent: ((MediaItemEntity) -> Unit)? = null,
    onOpenFullBrief: () -> Unit,
    onPlayAudioBrief: () -> Unit,
    onOpenSettings: () -> Unit = {},
    onSelectChannel: ((String) -> Unit)? = null,
    onNavigateToTv: (() -> Unit)? = null,
    onOpenSidebar: () -> Unit = {},
    onOpenWeather: () -> Unit = {},
    onOpenInbox: () -> Unit = {},
    onOpenEmergencyHub: () -> Unit = {},
    onOpenBroadsheetCover: () -> Unit = {},
    onOpenTownsquarePlus: () -> Unit = {},
    unreadInboxCount: Int = 0,
    modifier: Modifier = Modifier
) {
    var searchVisible by remember { mutableStateOf(false) }
    var selectedHeadline by remember { mutableStateOf<Headline?>(null) }
    val sheetState = rememberModalBottomSheetState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Header
        TownsquareTopBar(
            title = "Townsquare",
            subtitle = "Civic & Editorial Media Hub",
            onOpenSidebar = onOpenSidebar,
            actions = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Inbox Button with Unread Badge
                    IconButton(
                        onClick = onOpenInbox,
                        modifier = Modifier.testTag("open_inbox_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadInboxCount > 0) {
                                    Badge(
                                        containerColor = NeonCyan,
                                        contentColor = Color(0xFF003544)
                                    ) {
                                        Text(
                                            text = "$unreadInboxCount",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mail,
                                contentDescription = "Inbox",
                                tint = if (unreadInboxCount > 0) NeonCyan else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    IconButton(
                        onClick = { searchVisible = !searchVisible },
                        modifier = Modifier.testTag("toggle_search_button")
                    ) {
                        Icon(
                            imageVector = if (searchVisible) Icons.Default.Clear else Icons.Default.Search,
                            contentDescription = "Search",
                            tint = if (searchVisible) NeonCyan else MaterialTheme.colorScheme.onBackground
                        )
                    }

                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier.testTag("toggle_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        )

        // Offline Mode Banner
        if (isOfflineMode) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF00222B),
                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudDone, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Offline Mode Active", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = NeonCyan)
                            Text("Showing saved articles, publications & bookmarks", style = MaterialTheme.typography.bodySmall, color = Color.LightGray, fontSize = 11.sp)
                        }
                    }
                    TextButton(onClick = onToggleOfflineMode) {
                        Text("Go Live", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NeonCyan)
                    }
                }
            }
        }


        // Search bar (collapsible)
        if (searchVisible) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search stories, channels, podcasts, radio stations...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .testTag("feed_search_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = DarkBorder
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        NewsTicker(
            headlines = listOf(
                Headline("Civic News", "New Promenade opening tomorrow morning", "Civic Press"),
                Headline("Weather Alert", "Offshore squall expected at 9 PM", "Harbor Station"),
                Headline("Marketplace", "Vintage audio equipment auction ending soon", "Townsquare Market"),
                Headline("Community", "Old Town clocktower renovation begins", "Town Council")
            ),
            onHeadlineClick = { selectedHeadline = it }
        )

        // Curation Feed Toggles
        androidx.compose.material3.TabRow(
            selectedTabIndex = if (isForYouFeed) 1 else 0,
            containerColor = Color.Transparent,
            contentColor = NeonCyan,
            indicator = { tabPositions ->
                androidx.compose.material3.TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[if (isForYouFeed) 1 else 0]),
                    color = NeonCyan
                )
            },
            divider = { androidx.compose.material3.HorizontalDivider(color = DarkBorder) }
        ) {
            androidx.compose.material3.Tab(
                selected = !isForYouFeed,
                onClick = { onToggleForYouFeed(false) },
                text = { Text("Latest Dispatch", fontWeight = if (!isForYouFeed) FontWeight.Bold else FontWeight.Medium) }
            )
            androidx.compose.material3.Tab(
                selected = isForYouFeed,
                onClick = { onToggleForYouFeed(true) },
                text = { Text("For You", fontWeight = if (isForYouFeed) FontWeight.Bold else FontWeight.Medium) }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Main Feed with Pull-To-Refresh Box
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .testTag("feed_pull_to_refresh_box")
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 2. Local Community Bulletins & Public Notices
                if (bulletins.isNotEmpty()) {
                    item {
                        LocalBulletinsSection(
                            bulletins = bulletins,
                            selectedCategory = selectedBulletinCategory,
                            onSelectCategory = onSelectBulletinCategory,
                            onOpenReportDialog = onOpenReportBulletin,
                            onToggleUpvote = onToggleBulletinUpvote,
                            onDeleteBulletin = onDeleteBulletin,
                            onShareBulletin = onShareBulletin,
                            modifier = Modifier.testTag("feed_local_bulletins_section")
                        )
                    }
                }

                // 3. Featured Syndicated Outlets & Channels Carousel
                if (channels.isNotEmpty()) {
                    item {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "📡 Syndicated Media Outlets",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                                Text(
                                    text = "${channels.size} active",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeonCyan
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(channels) { ch ->
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        border = BorderStroke(1.dp, DarkBorder),
                                        modifier = Modifier
                                            .clickable { onSelectChannel?.invoke(ch.id) }
                                            .testTag("channel_chip_${ch.id}")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = ch.iconEmoji, fontSize = 18.sp)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = ch.name,
                                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = ch.category,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // --- THE TICKET: CULTURAL EVENT LISTINGS SECTION ---
                item {
                    val ticketEvents = items.filter { it.channelId == "channel_the_ticket" }
                    if (ticketEvents.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "🎟️ The Ticket • Cultural Events",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                                Text(
                                    text = "Book Seats",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeonCyan
                                )
                            }
                            
                            var bookedEventTitle by remember { mutableStateOf<String?>(null) }
                            var followedArtists by remember { mutableStateOf(setOf<String>()) }
                            
                            if (bookedEventTitle != null) {
                                androidx.compose.material3.AlertDialog(
                                    onDismissRequest = { bookedEventTitle = null },
                                    title = { Text("🎟️ Ticket Confirmed!", color = NeonCyan, fontWeight = FontWeight.Bold) },
                                    text = {
                                        Text(
                                            text = "Your seat at '$bookedEventTitle' has been cryptographically secured.\n\nWe have saved your ticket to your secure civil documents under 'The Ticket'.",
                                            color = Color.LightGray
                                        )
                                    },
                                    confirmButton = {
                                        androidx.compose.material3.Button(
                                            onClick = { bookedEventTitle = null },
                                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = NeonCyan)
                                        ) {
                                            Text("Awesome", color = Color.Black)
                                        }
                                    }
                                )
                            }
                            
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(ticketEvents) { event ->
                                    Card(
                                        modifier = Modifier
                                            .width(280.dp)
                                            .testTag("ticket_event_card_${event.id}"),
                                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                                        border = BorderStroke(1.dp, DarkBorder)
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = Color(0xFFFF5252).copy(alpha = 0.15f)
                                            ) {
                                                Text(
                                                    text = event.issueEdition.uppercase(),
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFFF5252),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            
                                            Spacer(modifier = Modifier.height(8.dp))
                                            
                                            Text(
                                                text = event.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = Color.White,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = event.subtitle,
                                                fontSize = 11.sp,
                                                color = Color.Gray,
                                                maxLines = 1
                                            )
                                            
                                            Spacer(modifier = Modifier.height(6.dp))
                                            
                                            Text(
                                                text = event.bodyText,
                                                fontSize = 11.sp,
                                                color = Color.LightGray,
                                                maxLines = 2,
                                                lineHeight = 15.sp
                                            )
                                            
                                            Spacer(modifier = Modifier.height(10.dp))
                                            
                                            // Featured artist follow row
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.Stars,
                                                        contentDescription = null,
                                                        tint = NeonCyan,
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = event.authorName,
                                                        fontSize = 11.sp,
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                
                                                val isFollowed = followedArtists.contains(event.authorName)
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = if (isFollowed) Color(0xFF30D158).copy(alpha = 0.2f) else NeonCyan.copy(alpha = 0.15f),
                                                    border = BorderStroke(1.dp, if (isFollowed) Color(0xFF30D158) else NeonCyan),
                                                    modifier = Modifier.clickable {
                                                        followedArtists = if (isFollowed) {
                                                            followedArtists - event.authorName
                                                        } else {
                                                            followedArtists + event.authorName
                                                        }
                                                    }
                                                ) {
                                                    Text(
                                                        text = if (isFollowed) "Following ✓" else "+ Follow",
                                                        fontSize = 10.sp,
                                                        color = if (isFollowed) Color(0xFF30D158) else NeonCyan,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                            
                                            Spacer(modifier = Modifier.height(10.dp))
                                            
                                            androidx.compose.material3.Button(
                                                onClick = { bookedEventTitle = event.title },
                                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                                                modifier = Modifier.fillMaxWidth().height(32.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text("Get Tickets / RSVP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. Media Format Filter Tabs
                item {
                    val mediaFormats = listOf(null to "All Formats") + MediaType.entries.map { it to it.badge }
                    ScrollableTabRow(
                        selectedTabIndex = mediaFormats.indexOfFirst { it.first == selectedTypeFilter }.coerceAtLeast(0),
                        edgePadding = 0.dp,
                        containerColor = Color.Transparent,
                        contentColor = NeonCyan,
                        indicator = { tabPositions ->
                            val index = mediaFormats.indexOfFirst { it.first == selectedTypeFilter }.coerceAtLeast(0)
                            if (index < tabPositions.size) {
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                                    color = NeonCyan
                                )
                            }
                        },
                        divider = {}
                    ) {
                        mediaFormats.forEach { (type, label) ->
                            val isSelected = selectedTypeFilter == type
                            Tab(
                                selected = isSelected,
                                onClick = { onSelectTypeFilter(type) },
                                text = {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (isSelected) NeonCyan else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                modifier = Modifier.testTag("media_tab_${type?.name ?: "ALL"}")
                            )
                        }
                    }
                }

                // 5. Feed Items
                if (items.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "📡", fontSize = 36.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No Media Found",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Try clearing search or picking another channel filter.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(items, key = { it.id }) { item ->
                        MediaCardItem(
                            item = item,
                            audioState = audioState,
                            onOpenReader = onOpenReader,
                            onPlayAudio = onPlayAudio,
                            onToggleLike = onToggleLike,
                            onToggleBookmark = onToggleBookmark,
                            onVoiceNarrate = onVoiceNarrate,
                            onShare = onShare,
                            onToggleSavedOffline = onToggleSavedOffline,
                            onReportContent = onReportContent
                        )
                    }
                }

                // Bottom spacer so content is not cut off by mini player & nav
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
        
        if (selectedHeadline != null) {
            ModalBottomSheet(
                onDismissRequest = { selectedHeadline = null },
                sheetState = sheetState
            ) {
                Column(modifier = Modifier.padding(16.dp).padding(bottom = 32.dp)) {
                    Text(selectedHeadline!!.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Source: ${selectedHeadline!!.source}", style = MaterialTheme.typography.labelSmall, color = NeonCyan)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(selectedHeadline!!.text, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
