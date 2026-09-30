package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber

@Composable
fun LegalNoticeGuidelinesDialog(
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).testTag("legal_notice_dialog"),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Gavel,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Legal Notice & Guidelines",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "Townsquare Civic Media Charter v2.4",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Section Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = DarkSurfaceElevated,
                    contentColor = NeonCyan
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("⚖️ Legal Notice", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("📰 Editorial Charter", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("🛡️ Guidelines", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    when (selectedTab) {
                        0 -> LegalNoticeContent()
                        1 -> EditorialCharterContent()
                        2 -> CommunityGuidelinesContent()
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("I Acknowledge & Agree", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun LegalNoticeContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            SectionCard("1. Publisher Ownership & Operating Identity") {
                Text(
                    text = "Townsquare Media Group is a decentralized civic broadcasting and publishing network. All dispatches, audio streams, facsimile transmissions, and live wire tickers are published under open editorial syndication licenses.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }
        }

        item {
            SectionCard("2. Copyright & Wire Syndication Rights") {
                Text(
                    text = "Content published across Townsquare channels remains the intellectual property of respective creators and syndicates. Wire feeds, teletext pages, and local bulletins may be quoted with proper attribution under the Civic Syndication Agreement.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }
        }

        item {
            SectionCard("3. Liability & Financial Disclaimers") {
                Text(
                    text = "Marketplace transactions, foreign exchange (FX) rates, stock tickers, and merchant listings are provided for informational and civic trade purposes. Townsquare is not liable for private party transactions or financial decisions based on wire data.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }
        }
    }
}

@Composable
private fun EditorialCharterContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            SectionCard("1. Code of Journalistic Integrity") {
                Text(
                    text = "Townsquare reporters and channel owners adhere to fair, objective, and verified reporting. All breaking newswire updates undergo multi-source verification before primary dispatch.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }
        }

        item {
            SectionCard("2. Artificial Intelligence Transparency") {
                Text(
                    text = "AI-assisted summaries, smart fact-checking, and synthetic voice narrations are explicitly tagged with AI verification badges. Automated audits do not replace human editorial judgment.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }
        }

        item {
            SectionCard("3. Fact-Checking & Correction Policy") {
                Text(
                    text = "Any factual errors in broadsheets or live newsblogs are promptly corrected with a transparent editorial changelog in the AI Fact-Checking Center.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }
        }
    }
}

@Composable
private fun CommunityGuidelinesContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            SectionCard("1. Respectful Civic Discourse") {
                Text(
                    text = "Community posts, fandom hubs, and letters to the editor must remain respectful. Hate speech, harassment, impersonation, and explicit spam are strictly prohibited.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }
        }

        item {
            SectionCard("2. Safe Marketplace Trading") {
                Text(
                    text = "When buying, selling, or offering items in the Marketplace & Catalogs, members must provide accurate descriptions. Prohibited items include illegal contraband and hazardous goods.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }
        }

        item {
            SectionCard("3. Content Reporting & Moderation") {
                Text(
                    text = "Users can flag inappropriate dispatches or marketplace items directly to the Civic Moderation Board for instant review.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = DarkSurface,
        border = BorderStroke(1.dp, DarkBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = WarmAmber
            )
            Spacer(modifier = Modifier.height(4.dp))
            content()
        }
    }
}
