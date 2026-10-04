package com.example.ui.components.settings

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class SuperappActivity(
    val id: String,
    val type: String, // READING, AUDIO, TV, COMMERCE, SEARCH
    val title: String,
    val timestamp: String,
    val iconEmoji: String
)

data class OfflineDownloadItem(
    val id: String,
    val name: String,
    val type: String,
    val sizeMb: Int,
    val downloadDate: String,
    val iconEmoji: String
)

@Composable
fun HistoryDownloadsSettings(
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableIntStateOf(0) } // 0: Superapp Activity History, 1: Downloads & Storage
    var isHistoryLoggingEnabled by remember { mutableStateOf(true) }
    var selectedActivityFilter by remember { mutableStateOf("ALL") }
    var confirmClearHistoryDialog by remember { mutableStateOf(false) }
    var confirmClearDownloadsDialog by remember { mutableStateOf(false) }

    var activities by remember {
        mutableStateOf(
            listOf(
                SuperappActivity("act_1", "READING", "Waterfront Promenade Ribbon Cutting (Broadsheet)", "Today 10:15", "📰"),
                SuperappActivity("act_2", "AUDIO", "Weatherman Episode #8 - Melancholic Autumn Commute", "Today 08:30", "🎙️"),
                SuperappActivity("act_3", "TV", "Townsquare Central TV: Morning Civic News 24", "Yesterday 20:00", "📺"),
                SuperappActivity("act_4", "COMMERCE", "Artisan Roasted Single-Origin Coffee (Marketplace)", "2 days ago", "🛍️"),
                SuperappActivity("act_5", "SEARCH", "Searched 'Line 3 transit schedule'", "3 days ago", "🔍"),
                SuperappActivity("act_6", "READING", "The Curator: Secret Antique Glassware of Old Town", "3 days ago", "🏺")
            )
        )
    }

    var downloads by remember {
        mutableStateOf(
            listOf(
                OfflineDownloadItem("dl_1", "Oct 2026 Daily Broadsheet (Full Issue)", "PDF Document", 32, "2026-10-02", "📰"),
                OfflineDownloadItem("dl_2", "Weatherman Audio Podcast Series (Episodes 1-4)", "Audio MP3", 64, "2026-10-01", "🎙️"),
                OfflineDownloadItem("dl_3", "Silver Screen Classics: The Velvet Hour (Remastered)", "Video MP4", 240, "2026-09-30", "🎬"),
                OfflineDownloadItem("dl_4", "Tokyo & Paris Offline Transit Vector Maps", "Map Data", 76, "2026-09-28", "🗺️")
            )
        )
    }

    val totalDownloadedMb = downloads.sumOf { it.sizeMb }
    val filteredActivities = activities.filter {
        selectedActivityFilter == "ALL" || it.type == selectedActivityFilter
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = DarkSurfaceElevated,
        border = BorderStroke(1.dp, DarkBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("history_downloads_settings_panel")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(NeonCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.History, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("History & Downloads Vault", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        Text("Manage browsing activity & offline files", style = MaterialTheme.typography.bodySmall, color = DarkTextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sub-tabs: Activity vs Downloads
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (activeTab == 0) NeonCyan else Color(0xFF1E293B),
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { activeTab = 0 }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Activity Log (${activities.size})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeTab == 0) Color.Black else Color.White
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (activeTab == 1) WarmAmber else Color(0xFF1E293B),
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { activeTab = 1 }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Offline Downloads (${totalDownloadedMb}MB)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeTab == 1) Color.Black else Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // TAB 0: ACTIVITY LOG
            if (activeTab == 0) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Pause switch & Clear button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Activity Logging", fontSize = 12.sp, color = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Switch(
                                checked = isHistoryLoggingEnabled,
                                onCheckedChange = { isHistoryLoggingEnabled = it },
                                modifier = Modifier.height(20.dp),
                                colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan)
                            )
                        }

                        if (activities.isNotEmpty()) {
                            TextButton(
                                onClick = { confirmClearHistoryDialog = true },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("Clear History", color = CoralRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Activity filters
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(listOf("ALL", "READING", "AUDIO", "TV", "COMMERCE", "SEARCH")) { filter ->
                            val isSelected = selectedActivityFilter == filter
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) NeonCyan.copy(alpha = 0.25f) else Color(0xFF1E293B),
                                border = BorderStroke(1.dp, if (isSelected) NeonCyan else Color.Transparent),
                                modifier = Modifier.clickable { selectedActivityFilter = filter }
                            ) {
                                Text(
                                    text = filter,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) NeonCyan else Color.LightGray,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Activity entries
                    if (filteredActivities.isEmpty()) {
                        Text("No activity items in this filter.", fontSize = 12.sp, color = DarkTextMuted, modifier = Modifier.padding(vertical = 8.dp))
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            filteredActivities.forEach { act ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFF161F2E),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(act.iconEmoji, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(act.title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1)
                                            Text("${act.type} • ${act.timestamp}", fontSize = 10.sp, color = DarkTextSecondary)
                                        }
                                        IconButton(
                                            onClick = { activities = activities.filter { it.id != act.id } },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // TAB 1: DOWNLOADS & OFFLINE VAULT
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Storage usage meter
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF161F2E),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Offline Storage Consumption", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("${totalDownloadedMb} MB / 1,024 MB", fontSize = 11.sp, color = WarmAmber, fontFamily = FontFamily.Monospace)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { (totalDownloadedMb / 1024f).coerceIn(0f, 1f) },
                                modifier = Modifier.fillMaxWidth().height(6.dp),
                                color = WarmAmber,
                                trackColor = Color(0xFF1E293B)
                            )
                        }
                    }

                    // Downloads action row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Cached Content (${downloads.size} files)", fontSize = 11.sp, color = DarkTextSecondary)
                        if (downloads.isNotEmpty()) {
                            TextButton(
                                onClick = { confirmClearDownloadsDialog = true },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("Delete All", color = CoralRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Downloads list
                    if (downloads.isEmpty()) {
                        Text("No offline downloads stored.", fontSize = 12.sp, color = DarkTextMuted, modifier = Modifier.padding(vertical = 8.dp))
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            downloads.forEach { item ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFF161F2E),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(item.iconEmoji, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(item.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1)
                                            Text("${item.type} • ${item.sizeMb} MB • Saved ${item.downloadDate}", fontSize = 10.sp, color = DarkTextSecondary)
                                        }
                                        IconButton(
                                            onClick = { downloads = downloads.filter { it.id != item.id } },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.DeleteOutline, contentDescription = "Remove", tint = CoralRed, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Confirm Clear History Dialog
    if (confirmClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { confirmClearHistoryDialog = false },
            containerColor = Color(0xFF1E293B),
            title = { Text("Clear All Activity History?", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text("This will permanently erase all reading, audio, TV, and commerce activity timestamps from this device.", color = DarkTextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        activities = emptyList()
                        confirmClearHistoryDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralRed)
                ) {
                    Text("Clear All", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmClearHistoryDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    // Confirm Clear Downloads Dialog
    if (confirmClearDownloadsDialog) {
        AlertDialog(
            onDismissRequest = { confirmClearDownloadsDialog = false },
            containerColor = Color(0xFF1E293B),
            title = { Text("Delete All Offline Downloads?", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text("This will free up ${totalDownloadedMb} MB of local storage space. You can re-download content at any time.", color = DarkTextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        downloads = emptyList()
                        confirmClearDownloadsDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralRed)
                ) {
                    Text("Delete All Files", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmClearDownloadsDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}
