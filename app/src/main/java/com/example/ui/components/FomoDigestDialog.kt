package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class DigestItem(
    val id: String,
    val category: String, // "Breaking News", "Community Bulletin", "Live Radio", "Media Channels"
    val title: String,
    val snippet: String,
    val source: String,
    val timestampAgo: String,
    val priorityScore: Int // 1-100
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FomoDigestDialog(
    onDismiss: () -> Unit,
    onNavigateToTab: (String) -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedTimeframe by remember { mutableStateOf("Last 4 Hours") }
    var isReadingAudio by remember { mutableStateOf(false) }

    val timeframeOptions = listOf("Last 1 Hour", "Last 4 Hours", "Since Yesterday", "This Weekend")

    val digestList = remember(selectedTimeframe) {
        listOf(
            DigestItem(
                id = "DIG-1",
                category = "Breaking News",
                title = "Town Council Approves 2026 Civic Infrastructure Expansion",
                snippet = "With an 8-1 vote, the council pledged $4.2M towards public fiber internet and downtown solar shade pavilions.",
                source = "Townsquare Gazette",
                timestampAgo = "1 hour ago",
                priorityScore = 98
            ),
            DigestItem(
                id = "DIG-2",
                category = "Live Radio",
                title = "Station 98.5 FM: Special Broadcast on Local Arts Festival",
                snippet = "DJ Maya interviewed local sculptors and premiered 3 indie tracks from regional musicians.",
                source = "Townsquare Radio 98.5 FM",
                timestampAgo = "2 hours ago",
                priorityScore = 91
            ),
            DigestItem(
                id = "DIG-3",
                category = "Community Bulletin",
                title = "Farmer's Market Relocated to Central Park Plaza",
                snippet = "Due to main street repaving, Saturday's organic produce market will open at Central Plaza at 8:00 AM.",
                source = "Civic Board",
                timestampAgo = "3 hours ago",
                priorityScore = 87
            ),
            DigestItem(
                id = "DIG-4",
                category = "Media Channels",
                title = "The Curator: Modern Architecture in Small Town Spaces",
                snippet = "A visual retrospective on adaptive reuse of historical brick buildings for community hubs.",
                source = "The Curator Channel",
                timestampAgo = "3.5 hours ago",
                priorityScore = 82
            )
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.88f)
                .padding(10.dp)
                .testTag("fomo_digest_dialog"),
            colors = CardDefaults.cardColors(containerColor = DarkBg),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header Banner
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(WarmAmber.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "FOMO Digest",
                                tint = WarmAmber,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "WHILE YOU WERE AWAY",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = WarmAmber
                                ) {
                                    Text(
                                        text = "AI RECAP",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = DarkBg,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Catch up on top highlights missed during your absence",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Timeframe Chips Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    timeframeOptions.forEach { tf ->
                        val isSelected = selectedTimeframe == tf
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedTimeframe = tf },
                            label = { Text(tf, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonCyan,
                                selectedLabelColor = DarkBg,
                                containerColor = DarkCardBg,
                                labelColor = Color.LightGray
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // AI Executive Briefing Summary Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCardBg),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WarmAmber.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⚡ EXECUTIVE RECAP SUMMARY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = WarmAmber,
                                    letterSpacing = 1.sp
                                )
                            )

                            IconButton(
                                onClick = {
                                    isReadingAudio = !isReadingAudio
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = if (isReadingAudio) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                                    contentDescription = "Read Aloud",
                                    tint = if (isReadingAudio) NeonCyan else Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "While away for $selectedTimeframe, 4 major events occurred: Town Council approved fiber internet funding ($4.2M), Central Park market was relocated due to paving, Station 98.5 FM aired an arts special, and 'The Curator' released a new architectural retrospective.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.LightGray,
                                lineHeight = 18.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("4", fontWeight = FontWeight.Bold, color = NeonCyan)
                                Text("Top Stories", fontSize = 10.sp, color = Color.Gray)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("18", fontWeight = FontWeight.Bold, color = WarmAmber)
                                Text("New Posts", fontSize = 10.sp, color = Color.Gray)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("2", fontWeight = FontWeight.Bold, color = MintTeal)
                                Text("Radio Airs", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "HIGHLIGHTS DIGEST",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Digest Cards
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(digestList) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onDismiss()
                                    if (item.category == "Live Radio") {
                                        onNavigateToTab("Radio")
                                    } else {
                                        onNavigateToTab("Gazette")
                                    }
                                },
                            colors = CardDefaults.cardColors(containerColor = DarkCardBg),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = when (item.category) {
                                            "Breaking News" -> CoralRed.copy(alpha = 0.2f)
                                            "Live Radio" -> NeonCyan.copy(alpha = 0.2f)
                                            "Community Bulletin" -> WarmAmber.copy(alpha = 0.2f)
                                            else -> MintTeal.copy(alpha = 0.2f)
                                        }
                                    ) {
                                        Text(
                                            text = item.category.uppercase(),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (item.category) {
                                                "Breaking News" -> CoralRed
                                                "Live Radio" -> NeonCyan
                                                "Community Bulletin" -> WarmAmber
                                                else -> MintTeal
                                            },
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Text(
                                        text = item.timestampAgo,
                                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontSize = 11.sp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = item.snippet,
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray)
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Source: ${item.source}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            color = Color.Gray,
                                            fontSize = 10.sp
                                        )
                                    )

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Relevance ${item.priorityScore}%",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = NeonCyan,
                                                fontSize = 10.sp
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            Icons.Default.ArrowForward,
                                            contentDescription = null,
                                            tint = NeonCyan,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("dismiss_fomo_digest_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = DarkBg),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.DoneAll, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("MARK DIGEST AS READ & CONTINUE", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
