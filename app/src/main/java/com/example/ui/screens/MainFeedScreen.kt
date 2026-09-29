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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
                    // Townsquare Plus Superapp Suite Button
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = WarmAmber.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WarmAmber),
                        modifier = Modifier
                            .clickable { onOpenTownsquarePlus() }
                            .testTag("feed_open_plus_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = "Townsquare Plus",
                                tint = WarmAmber,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "PLUS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = WarmAmber
                            )
                        }
                    }

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

                    // Offline Mode Toggle
                    IconButton(
                        onClick = onToggleOfflineMode,
                        modifier = Modifier.testTag("toggle_offline_mode_button")
                    ) {
                        Icon(
                            imageVector = if (isOfflineMode) Icons.Default.CloudDone else Icons.Default.CloudOff,
                            contentDescription = if (isOfflineMode) "Offline Mode Active" else "Live Mode",
                            tint = if (isOfflineMode) NeonCyan else MaterialTheme.colorScheme.onBackground
                        )
                    }

                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier.testTag("feed_refresh_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Feed",
                            tint = if (isRefreshing) NeonCyan else MaterialTheme.colorScheme.onBackground
                        )
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
                "Civic News: New Promenade opening tomorrow morning",
                "Weather Alert: Offshore squall expected at 9 PM",
                "Marketplace: Vintage audio equipment auction ending soon",
                "Community: Old Town clocktower renovation begins"
            )
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
                // 1. Morning Editorial Briefing Highlight
                item {
                    MorningBriefCard(
                        channels = channels,
                        onOpenFullBrief = onOpenFullBrief,
                        onPlayAudioBrief = onPlayAudioBrief,
                        modifier = Modifier.testTag("feed_morning_brief_card")
                    )
                }

                // Townsquare Plus Superapp Showcase Banner
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF131A26),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WarmAmber.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenTownsquarePlus() }
                            .testTag("feed_townsquare_plus_card")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = WarmAmber
                                    ) {
                                        Text(
                                            text = "SUPERAPP SUITE",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 9.sp),
                                            color = Color(0xFF261800),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Townsquare Plus",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = "Open Suite →",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = WarmAmber
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Modular apps for modern civic life: Phone dialer & voicemail, Mailbox client, Interactive maps with personalized travel magazine, and Marketplace.",
                                style = MaterialTheme.typography.bodySmall,
                                color = DarkTextSecondary
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // 4 Mini-app pill shortcuts
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = DarkSurface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "📱 Phone",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF30D158),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = DarkSurface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "✉️ Mail",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = WarmAmber,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = DarkSurface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "🗺️ Maps",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MintTeal,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = DarkSurface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "🛍️ Market",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = RadiantPurple,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

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
    }
}
