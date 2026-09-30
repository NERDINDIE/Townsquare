package com.example.ui.components

import android.widget.Toast
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.ui.theme.*

data class NewsletterIssue(
    val id: String,
    val publicationTitle: String,
    val channelEmoji: String,
    val issueNumber: String,
    val date: String,
    val headline: String,
    val author: String,
    val readTimeMinutes: Int,
    val leadParagraph: String,
    val fullContent: List<String>,
    val isSubscribed: Boolean = true,
    var isRead: Boolean = false,
    val audioUrl: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscribedNewslettersView(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var newsletterIssues by remember {
        mutableStateOf(
            listOf(
                NewsletterIssue(
                    id = "nl_1",
                    publicationTitle = "Horizon Tech Weekly Insider",
                    channelEmoji = "⚡",
                    issueNumber = "Issue #42",
                    date = "Today • 08:00 AM",
                    headline = "Solid-State Battery Breakthroughs & Quantum Edge Compilers",
                    author = "Dr. Maya Lin",
                    readTimeMinutes = 5,
                    leadParagraph = "Welcome to issue #42. This week, we examine how solid-state silicon anodes are redefining long-range transport, and why local edge compilation architectures are outperforming centralized server farms.",
                    fullContent = listOf(
                        "In our lead investigation, materials scientists in the Western District have validated a multi-layer ceramic electrolyte that resists dendrite formation even under 4C ultra-fast charging.",
                        "Meanwhile, regional civic datacenters are transitioning to asynchronous edge compilers, allowing devices to run verified privacy-preserving language models with zero telemetry latency.",
                        "Upcoming next week: Our exclusive teardown of 2026 photonic bus interfaces."
                    ),
                    isSubscribed = true,
                    isRead = false
                ),
                NewsletterIssue(
                    id = "nl_2",
                    publicationTitle = "Morning Espresso Civic Dispatch",
                    channelEmoji = "☕",
                    issueNumber = "Edition #118",
                    date = "Yesterday",
                    headline = "Pedestrian Transit Corridor & Municipal Budget Breakdown",
                    author = "Marcus Vance",
                    readTimeMinutes = 4,
                    leadParagraph = "Following the City Council's landmark vote, we break down what the 2030 pedestrianization ordinance means for local merchants, tram schedules, and harbor ferry links.",
                    fullContent = listOf(
                        "Starting next spring, the central four-avenue grid will convert exclusively to electric trams, bicycles, and pedestrian tree canopies.",
                        "Tax credits for commercial building rooftop solar installations will begin disbursement next month through the municipal Treasury desk.",
                        "Civic Forum Question: What are your recommendations for the newly planned market plaza?"
                    ),
                    isSubscribed = true,
                    isRead = true
                ),
                NewsletterIssue(
                    id = "nl_3",
                    publicationTitle = "Bookworm Literary Gazette",
                    channelEmoji = "📚",
                    issueNumber = "Issue #29",
                    date = "Sep 28, 2026",
                    headline = "The Tactile Beauty of Hand-Set Movable Lead Type",
                    author = "Julian Vance",
                    readTimeMinutes = 6,
                    leadParagraph = "A deep dive into why letterpress artisans and independent bookbinders are seeing unprecedented demand for physical, heirloom publications.",
                    fullContent = listOf(
                        "Visiting the Old Quarter guild shop, one is greeted by the intoxicating scent of linseed ink, rag cotton paper, and precision brass typesetting rules.",
                        "Linen bindings and debossed covers provide a physical anchor to reading that fleeting digital screens struggle to match."
                    ),
                    isSubscribed = true,
                    isRead = false
                ),
                NewsletterIssue(
                    id = "nl_4",
                    publicationTitle = "Retro & Vintage Weekly Digest",
                    channelEmoji = "📼",
                    issueNumber = "Issue #04",
                    date = "Sep 26, 2026",
                    headline = "Restoring 1950s Vacuum Tube Receivers & CRT Scanlines",
                    author = "Miles Holloway",
                    readTimeMinutes = 5,
                    leadParagraph = "Why the warm second-order harmonics of vacuum tube audio and 15kHz CRT display scanlines connect us deeper to media.",
                    fullContent = listOf(
                        "In this edition, master restorer Eleanor Finch walks through the restoration of a 1958 Philco Bakelite receiver.",
                        "Cassette tape trading circles report record attendance across three regional weekend swapmeets."
                    ),
                    isSubscribed = true,
                    isRead = true
                )
            )
        )
    }

    var selectedFilter by remember { mutableStateOf("ALL") } // "ALL", "UNREAD", "SUBSCRIBED"
    var activeReadingIssue by remember { mutableStateOf<NewsletterIssue?>(null) }

    val filteredIssues = remember(newsletterIssues, selectedFilter) {
        when (selectedFilter) {
            "UNREAD" -> newsletterIssues.filter { !it.isRead }
            "SUBSCRIBED" -> newsletterIssues.filter { it.isSubscribed }
            else -> newsletterIssues
        }
    }

    Column(modifier = modifier.fillMaxSize().background(DarkBg)) {
        // Filter Chips Bar
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F172A))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filters = listOf(
                "ALL" to "📬 All Editions (${newsletterIssues.size})",
                "UNREAD" to "🔔 Unread (${newsletterIssues.count { !it.isRead }})",
                "SUBSCRIBED" to "⭐ Subscribed (${newsletterIssues.count { it.isSubscribed }})"
            )
            items(filters) { (key, label) ->
                val isSelected = selectedFilter == key
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) NeonCyan else DarkSurfaceVariant,
                    border = BorderStroke(1.dp, if (isSelected) NeonCyan else DarkBorder),
                    modifier = Modifier.clickable { selectedFilter = key }
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color(0xFF003544) else Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Newsletter Issues List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(filteredIssues, key = { it.id }) { issue ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkSurface,
                    border = BorderStroke(1.dp, if (!issue.isRead) NeonCyan.copy(alpha = 0.5f) else DarkBorder),
                    modifier = Modifier.fillMaxWidth().clickable {
                        // Mark as read and open reader
                        newsletterIssues = newsletterIssues.map {
                            if (it.id == issue.id) it.copy(isRead = true) else it
                        }
                        activeReadingIssue = issue
                    }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Header Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = NeonCyan.copy(alpha = 0.15f),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(issue.channelEmoji, fontSize = 16.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = issue.publicationTitle,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${issue.issueNumber} • ${issue.date}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = DarkTextSecondary
                                    )
                                }
                            }

                            if (!issue.isRead) {
                                Surface(shape = RoundedCornerShape(4.dp), color = NeonCyan) {
                                    Text(
                                        text = "NEW ISSUE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF003544),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Headline & Lead
                        Text(
                            text = issue.headline,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = issue.leadParagraph,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFCBD5E1),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Actions Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "By ${issue.author} • ${issue.readTimeMinutes} min read",
                                fontSize = 11.sp,
                                color = DarkTextSecondary
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        Toast.makeText(context, "Playing audio narration for '${issue.headline}'", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, DarkBorder),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.Headphones, contentDescription = null, modifier = Modifier.size(14.dp), tint = NeonCyan)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Listen", fontSize = 11.sp, color = Color.White)
                                }

                                Button(
                                    onClick = {
                                        newsletterIssues = newsletterIssues.map {
                                            if (it.id == issue.id) it.copy(isRead = true) else it
                                        }
                                        activeReadingIssue = issue
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Read Issue", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Full Reading Modal Dialog
    activeReadingIssue?.let { issue ->
        Dialog(
            onDismissRequest = { activeReadingIssue = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("newsletter_reader_dialog"),
                color = Color(0xFF0B111E)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Reader Top Bar
                    Surface(
                        color = Color(0xFF0F172A),
                        border = BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(issue.channelEmoji, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(issue.publicationTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                                    Text("${issue.issueNumber} • ${issue.date}", fontSize = 11.sp, color = DarkTextSecondary)
                                }
                            }

                            IconButton(onClick = { activeReadingIssue = null }) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                            }
                        }
                    }

                    // Reader Content
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                text = issue.headline,
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                                color = Color.White
                            )
                        }

                        item {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurfaceElevated,
                                border = BorderStroke(1.dp, DarkBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Written by ${issue.author}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NeonCyan)
                                        Text("${issue.readTimeMinutes} min read • Verified Townsquare Press Edition", fontSize = 11.sp, color = DarkTextSecondary)
                                    }
                                    IconButton(
                                        onClick = {
                                            Toast.makeText(context, "Playing audio narration...", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Icon(Icons.Default.VolumeUp, contentDescription = null, tint = NeonCyan)
                                    }
                                }
                            }
                        }

                        item {
                            Text(
                                text = issue.leadParagraph,
                                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 26.sp, fontWeight = FontWeight.Medium),
                                color = Color(0xFFE2E8F0)
                            )
                        }

                        items(issue.fullContent) { paragraph ->
                            Text(
                                text = paragraph,
                                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp),
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }

                    // Footer
                    Surface(
                        color = Color(0xFF0F172A),
                        border = BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Subscribed to this publication", fontSize = 12.sp, color = MintTeal)
                            Button(
                                onClick = { activeReadingIssue = null },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Done", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
