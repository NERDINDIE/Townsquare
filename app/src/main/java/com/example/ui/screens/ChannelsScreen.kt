package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MediaChannelEntity
import com.example.ui.components.TownsquareTopBar
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber

@Composable
fun ChannelsScreen(
    channels: List<MediaChannelEntity>,
    onToggleFollowChannel: (MediaChannelEntity) -> Unit,
    onSelectChannel: (String) -> Unit,
    onOpenSidebar: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var showOnlyFollowed by remember { mutableStateOf(false) }

    // Derive available categories dynamically from channels
    val categories = remember(channels) {
        val set = linkedSetOf("All")
        channels.forEach { set.add(it.category) }
        set.toList()
    }

    // Filter channels according to search, category, and followed toggle
    val filteredChannels = remember(channels, searchQuery, selectedCategoryFilter, showOnlyFollowed) {
        channels.filter { channel ->
            val matchesCategory = selectedCategoryFilter == "All" || channel.category.equals(selectedCategoryFilter, ignoreCase = true)
            val matchesFollowed = !showOnlyFollowed || channel.isFollowed
            val matchesSearch = searchQuery.isBlank() ||
                    channel.name.contains(searchQuery, ignoreCase = true) ||
                    channel.description.contains(searchQuery, ignoreCase = true) ||
                    channel.category.contains(searchQuery, ignoreCase = true) ||
                    channel.morningBriefHighlight.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesFollowed && matchesSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("channels_screen")
    ) {
        TownsquareTopBar(
            title = "Discovery Hub",
            subtitle = "Explore channels, local broadsheets & audio frequencies",
            onOpenSidebar = onOpenSidebar
        )

        // SEARCH & QUICK FILTER BAR
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("channels_search_input"),
                placeholder = {
                    Text(
                        "Search 22+ channels, cinema, stonks, broadsheet...",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = DarkBorder,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // CATEGORY FILTER CHIPS
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = showOnlyFollowed,
                        onClick = { showOnlyFollowed = !showOnlyFollowed },
                        label = {
                            Text(
                                if (showOnlyFollowed) "★ Following Only" else "★ Following",
                                fontSize = 11.sp,
                                fontWeight = if (showOnlyFollowed) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonCyan.copy(alpha = 0.2f),
                            selectedLabelColor = NeonCyan
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = showOnlyFollowed,
                            borderColor = if (showOnlyFollowed) NeonCyan else DarkBorder
                        )
                    )
                }

                items(categories) { cat ->
                    val isSelected = selectedCategoryFilter == cat && !showOnlyFollowed
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedCategoryFilter = cat
                            showOnlyFollowed = false
                        },
                        label = {
                            Text(
                                text = cat,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonCyan,
                            selectedLabelColor = Color(0xFF003544)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) NeonCyan else DarkBorder
                        )
                    )
                }
            }
        }

        // RESULTS HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredChannels.size} CHANNELS FOUND",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (selectedCategoryFilter != "All" || showOnlyFollowed || searchQuery.isNotBlank()) {
                Text(
                    text = "Reset Filters",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = NeonCyan,
                    modifier = Modifier.clickable {
                        searchQuery = ""
                        selectedCategoryFilter = "All"
                        showOnlyFollowed = false
                    }
                )
            }
        }

        // CHANNELS LIST
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(filteredChannels, key = { it.id }) { channel ->
                val channelAccent = remember(channel.bannerColorHex) { Color(channel.bannerColorHex) }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectChannel(channel.id) }
                        .testTag("channel_item_card_${channel.id}"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(
                        1.dp,
                        if (channel.isFollowed) channelAccent.copy(alpha = 0.5f) else DarkBorder
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = channelAccent.copy(alpha = 0.18f),
                                    border = BorderStroke(1.dp, channelAccent.copy(alpha = 0.4f)),
                                    modifier = Modifier.size(46.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(text = channel.iconEmoji, fontSize = 22.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = channel.name,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = channel.category,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                            color = channelAccent
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${channel.followersCount / 1000}k followers",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            // Follow / Following button
                            if (channel.isFollowed) {
                                Button(
                                    onClick = { onToggleFollowChannel(channel) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = NeonCyan.copy(alpha = 0.15f),
                                        contentColor = NeonCyan
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("unfollow_channel_${channel.id}")
                                ) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Following", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                }
                            } else {
                                Button(
                                    onClick = { onToggleFollowChannel(channel) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = NeonCyan,
                                        contentColor = Color(0xFF003544)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("follow_channel_${channel.id}")
                                ) {
                                    Text(text = "+ Follow", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = channel.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Morning brief snippet box
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WbSunny,
                                    contentDescription = null,
                                    tint = WarmAmber,
                                    modifier = Modifier.size(16.dp).padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "TODAY'S MORNING BRIEF HIGHLIGHT",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.8.sp
                                        ),
                                        color = WarmAmber
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = channel.morningBriefHighlight,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Tap to view channel hint
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "View Channel Feed & Broadcasts",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = channelAccent
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = channelAccent,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
