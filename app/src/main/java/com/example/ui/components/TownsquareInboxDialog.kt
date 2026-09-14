package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.InboxActionType
import com.example.data.model.InboxCategory
import com.example.data.model.InboxNotificationItem
import com.example.data.model.MediaChannelEntity
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TownsquareInboxDialog(
    isOpen: Boolean,
    onClose: () -> Unit,
    items: List<InboxNotificationItem>,
    channels: List<MediaChannelEntity>,
    onMarkAsRead: (String) -> Unit,
    onMarkAllAsRead: () -> Unit,
    onDeleteNotification: (String) -> Unit,
    onClearAll: () -> Unit,
    onOpenFullBrief: () -> Unit,
    onPlayAudioBrief: () -> Unit,
    onOpenWeather: () -> Unit,
    onOpenBulletin: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (!isOpen) return

    var selectedCategory by remember { mutableStateOf(InboxCategory.ALL) }
    val unreadCount = remember(items) { items.count { !it.isRead } }

    val filteredItems = remember(items, selectedCategory) {
        if (selectedCategory == InboxCategory.ALL) items
        else items.filter { it.category == selectedCategory }
    }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxSize()
                .testTag("townsquare_inbox_dialog"),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = NeonCyan.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Mail,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Townsquare Inbox",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                if (unreadCount > 0) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        color = NeonCyan,
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(
                                            text = "$unreadCount new",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            ),
                                            color = Color(0xFF003544),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "Briefs, Dispatches & Editorial Notices",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (unreadCount > 0) {
                            IconButton(
                                onClick = onMarkAllAsRead,
                                modifier = Modifier.testTag("inbox_mark_all_read_btn")
                            ) {
                                Icon(
                                    Icons.Default.DoneAll,
                                    contentDescription = "Mark all as read",
                                    tint = NeonCyan
                                )
                            }
                        }

                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.testTag("inbox_close_btn")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }

                // Category Filter Bar
                ScrollableTabRow(
                    selectedTabIndex = selectedCategory.ordinal,
                    edgePadding = 12.dp,
                    containerColor = Color(0xFF0B111E),
                    contentColor = NeonCyan,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedCategory.ordinal]),
                            color = NeonCyan
                        )
                    }
                ) {
                    InboxCategory.values().forEach { category ->
                        val count = if (category == InboxCategory.ALL) items.size
                        else items.count { it.category == category }

                        Tab(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            text = {
                                Text(
                                    text = "${category.emoji} ${category.label} ($count)",
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedCategory == category) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                // Main Inbox Feed
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(4.dp)) }

                    // Highlight Hero: Embed Morning Brief in Briefs or All tab if present
                    if (selectedCategory == InboxCategory.ALL || selectedCategory == InboxCategory.BRIEFS) {
                        item {
                            Text(
                                text = "TODAY'S MORNING BRIEFING",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                ),
                                color = WarmAmber,
                                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                            )
                            MorningBriefCard(
                                channels = channels,
                                onOpenFullBrief = onOpenFullBrief,
                                onPlayAudioBrief = onPlayAudioBrief
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }

                    if (filteredItems.isEmpty()) {
                        item {
                            EmptyInboxState(category = selectedCategory)
                        }
                    } else {
                        item {
                            Text(
                                text = if (selectedCategory == InboxCategory.ALL) "ALL RECENT NOTIFICATIONS & BULLETINS"
                                else "${selectedCategory.label.uppercase()} NOTIFICATIONS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                ),
                                color = Color(0xFF94A3B8),
                                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                            )
                        }

                        items(filteredItems, key = { it.id }) { item ->
                            InboxNotificationCard(
                                item = item,
                                onMarkAsRead = { onMarkAsRead(item.id) },
                                onDelete = { onDeleteNotification(item.id) },
                                onActionClick = {
                                    onMarkAsRead(item.id)
                                    when (item.actionType) {
                                        InboxActionType.OPEN_BRIEF -> onOpenFullBrief()
                                        InboxActionType.PLAY_BRIEF_AUDIO -> onPlayAudioBrief()
                                        InboxActionType.OPEN_WEATHER -> onOpenWeather()
                                        InboxActionType.OPEN_BULLETIN -> item.targetId?.let { onOpenBulletin(it) } ?: onOpenFullBrief()
                                        InboxActionType.OPEN_ARTICLE -> onOpenFullBrief()
                                        InboxActionType.NONE -> {}
                                    }
                                }
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(40.dp)) }
                }
            }
        }
    }
}

@Composable
private fun InboxNotificationCard(
    item: InboxNotificationItem,
    onMarkAsRead: () -> Unit,
    onDelete: () -> Unit,
    onActionClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onActionClick() }
            .testTag("inbox_item_${item.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (!item.isRead) Color(0xFF131D30) else Color(0xFF0D1424)
        ),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            1.dp,
            if (!item.isRead) Color(item.accentColorHex).copy(alpha = 0.5f) else Color(0xFF1E293B)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(item.accentColorHex).copy(alpha = 0.2f),
                        shape = CircleShape,
                        modifier = Modifier.size(26.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = item.badgeEmoji, fontSize = 13.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.sourceLabel,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(item.accentColorHex)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${item.timeAgo}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = Color.Gray
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!item.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(NeonCyan)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.DeleteOutline,
                            contentDescription = "Delete notification",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = if (!item.isRead) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (!item.isRead) Color.White else Color(0xFFCBD5E1)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.snippet,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF94A3B8),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            if (item.actionType != InboxActionType.NONE) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Surface(
                        color = Color(item.accentColorHex).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(item.accentColorHex).copy(alpha = 0.4f)),
                        modifier = Modifier.clickable { onActionClick() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val actionLabel = when (item.actionType) {
                                InboxActionType.OPEN_BRIEF -> "Read Brief"
                                InboxActionType.PLAY_BRIEF_AUDIO -> "Listen (3 min)"
                                InboxActionType.OPEN_WEATHER -> "View Weather"
                                InboxActionType.OPEN_BULLETIN -> "View Dispatch"
                                InboxActionType.OPEN_ARTICLE -> "Open Story"
                                InboxActionType.NONE -> "View"
                            }
                            Text(
                                text = actionLabel,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(item.accentColorHex)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = null,
                                tint = Color(item.accentColorHex),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyInboxState(category: InboxCategory) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF101726),
        border = BorderStroke(1.dp, Color(0xFF1E293B))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = NeonCyan.copy(alpha = 0.15f),
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "📬", fontSize = 28.sp)
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "All Caught Up!",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "No unread ${category.label.lowercase()} notifications right now. New dispatches and wire alerts will appear here.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF94A3B8),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
