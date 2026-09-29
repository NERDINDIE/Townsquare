package com.example.ui.plus.extensions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class MetroDetail {
    NONE, NEWS, WEATHER, MAIL, ARCADE, AUDIO, STATE, PHONE, MARKETPLACE
}

@Composable
fun MetroWin8Skin(modifier: Modifier = Modifier) {
    var activeDetail by remember { mutableStateOf(MetroDetail.NONE) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)) // Sleek dark slate backdrop
            .testTag("metro_win8_skin_root")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Metro Hub Header Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Start",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Thin,
                        fontFamily = FontFamily.SansSerif,
                        color = Color.White
                    )
                    Text(
                        text = "TOWNSQUARE METRO DASHBOARD",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8),
                        letterSpacing = 1.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.White.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "TOWNSQUARE OS v8.1",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.LightGray,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Metro Start Grid of Interactive Tiles
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                // 1. News Tile (Wide Rectangle, 2 columns span)
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                    MetroTile(
                        title = "News",
                        summary = "New Promenade opening tomorrow morning! (Civic Press)",
                        badge = "LATEST DISPATCH",
                        color = Color(0xFFE11D48), // Rose Red
                        icon = Icons.Default.Newspaper,
                        onClick = { activeDetail = MetroDetail.NEWS }
                    )
                }

                // 2. Weather Tile (Medium Square, 1 column span)
                item {
                    MetroTile(
                        title = "Weather",
                        summary = "18°C, Cloudy",
                        badge = "HARBOR MET",
                        color = Color(0xFF0284C7), // Sky Blue
                        icon = Icons.Default.Cloud,
                        onClick = { activeDetail = MetroDetail.WEATHER }
                    )
                }

                // 3. Mailbox Tile (Medium Square, 1 column span)
                item {
                    MetroTile(
                        title = "Mail",
                        summary = "3 unread messages",
                        badge = "INBOX",
                        color = Color(0xFFD97706), // Orange
                        icon = Icons.Default.Mail,
                        onClick = { activeDetail = MetroDetail.MAIL }
                    )
                }

                // 4. State Identity Tile (Medium Square, 1 column span)
                item {
                    MetroTile(
                        title = "Civil Ledger",
                        summary = "ID Verification Secure",
                        badge = "BIOMETRIC",
                        color = Color(0xFF0D9488), // Teal
                        icon = Icons.Default.AccountBalance,
                        onClick = { activeDetail = MetroDetail.STATE }
                    )
                }

                // 5. Phone Link Tile (Medium Square, 1 column span)
                item {
                    MetroTile(
                        title = "Communications",
                        summary = "Satellite Linked",
                        badge = "AI SHIELD ON",
                        color = Color(0xFF16A34A), // Forest Green
                        icon = Icons.Default.Phone,
                        onClick = { activeDetail = MetroDetail.PHONE }
                    )
                }

                // 6. Arcade Gaming Tile (Wide Rectangle, 2 columns span)
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                    MetroTile(
                        title = "Arcade Gaming",
                        summary = "Townsquare Space Invaders retro tournament is active! Reset scoreboard.",
                        badge = "CABINET",
                        color = Color(0xFF7C3AED), // Purple
                        icon = Icons.Default.Gamepad,
                        onClick = { activeDetail = MetroDetail.ARCADE }
                    )
                }

                // 7. Unified Audio Tile (Medium Square, 1 column span)
                item {
                    MetroTile(
                        title = "Audio Hub",
                        summary = "Neon Horizon Beats",
                        badge = "STREAMING",
                        color = Color(0xFF0891B2), // Cyan
                        icon = Icons.Default.GraphicEq,
                        onClick = { activeDetail = MetroDetail.AUDIO }
                    )
                }

                // 8. Marketplace Tile (Medium Square, 1 column span)
                item {
                    MetroTile(
                        title = "Marketplace",
                        summary = "Vintage audio auction",
                        badge = "LISTINGS",
                        color = Color(0xFF4F46E5), // Indigo
                        icon = Icons.Default.Storefront,
                        onClick = { activeDetail = MetroDetail.MARKETPLACE }
                    )
                }
            }
        }

        // Expanded Start Screen Overlays
        if (activeDetail != MetroDetail.NONE) {
            MetroFullscreenOverlay(
                detailType = activeDetail,
                onDismiss = { activeDetail = MetroDetail.NONE }
            )
        }
    }
}

@Composable
fun MetroTile(
    title: String,
    summary: String,
    badge: String,
    color: Color,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(0.dp), // Win8 Metro Tiles are strictly rectangular flat
        color = color,
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable { onClick() }
    ) {
        Box(modifier = Modifier.padding(10.dp)) {
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(24.dp)
                    )

                    Surface(
                        shape = RoundedCornerShape(0.dp),
                        color = Color.White.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = FontFamily.SansSerif
                    )
                    Text(
                        text = summary,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MetroFullscreenOverlay(
    detailType: MetroDetail,
    onDismiss: () -> Unit
) {
    val overlayColor = when (detailType) {
        MetroDetail.NEWS -> Color(0xFFE11D48)
        MetroDetail.WEATHER -> Color(0xFF0284C7)
        MetroDetail.MAIL -> Color(0xFFD97706)
        MetroDetail.ARCADE -> Color(0xFF7C3AED)
        MetroDetail.AUDIO -> Color(0xFF0891B2)
        MetroDetail.STATE -> Color(0xFF0D9488)
        MetroDetail.PHONE -> Color(0xFF16A34A)
        MetroDetail.MARKETPLACE -> Color(0xFF4F46E5)
        else -> Color(0xFF0F172A)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = overlayColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss Metro",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Text(
                    text = "TOWNSQUARE METRO PANEL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.7f),
                    letterSpacing = 1.sp
                )
            }

            // Overlay Content based on type
            when (detailType) {
                MetroDetail.NEWS -> {
                    Text("News Feed Hub", fontSize = 28.sp, fontWeight = FontWeight.Light, color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    MetroDetailItem(title = "Civic News: New Promenade opening tomorrow", desc = "The municipal boulevard will open a pedestrian boardwalk featuring local crafts.")
                    MetroDetailItem(title = "Weather Alert: Offshore squall expected", desc = "High-speed winds are forecast at Harbor station. Secure outdoor equipment.")
                    MetroDetailItem(title = "Community: Old Town clocktower renovation", desc = "Local architects have partnered with the Arts Quarter to repair historical masonry.")
                }
                MetroDetail.WEATHER -> {
                    Text("Weather Telemetry", fontSize = 28.sp, fontWeight = FontWeight.Light, color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    MetroDetailItem(title = "Temperature: 18°C", desc = "Windchill feels like 16.5°C.")
                    MetroDetailItem(title = "Humidity: 78%", desc = "Precipitation probability at 60% tonight.")
                    MetroDetailItem(title = "Wind Vectors: 24.5 Knots NE", desc = "High-pressure squall developing offshore.")
                }
                MetroDetail.MAIL -> {
                    Text("Secure Municipal Mail", fontSize = 28.sp, fontWeight = FontWeight.Light, color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    MetroDetailItem(title = "Mayor's Office", desc = "Invitation to the pedestrian Promenade ceremony.")
                    MetroDetailItem(title = "Water Utility Bureau", desc = "Your electronic ledger statement for October is ready.")
                    MetroDetailItem(title = "Arcade Guild Leaderboard", desc = "You've been challenged by @elenavance to space duel.")
                }
                MetroDetail.ARCADE -> {
                    Text("Cabinet Arcade Stats", fontSize = 28.sp, fontWeight = FontWeight.Light, color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    MetroDetailItem(title = "Space Invaders Best: 100 PTS", desc = "Rank 3 on local leaderboard.")
                    MetroDetailItem(title = "Block Breaker Best: 120 PTS", desc = "Rank 2 on local leaderboard.")
                    MetroDetailItem(title = "Achievements Unlocked", desc = "Unlocked 2 of 5 badges (Cadet, Duo).")
                }
                MetroDetail.AUDIO -> {
                    Text("Unified Audio Streams", fontSize = 28.sp, fontWeight = FontWeight.Light, color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    MetroDetailItem(title = "Active Stream: Neon Horizon Beats", desc = "Late Night Synthesizer Jam • 85 BPM")
                    MetroDetailItem(title = "Playback Status: Active", desc = "Hardware node audio streaming is running smoothly.")
                    MetroDetailItem(title = "Next Track Queue: Autumn Wind Sonata", desc = "Acoustic Grand Piano & Ambient Strings")
                }
                MetroDetail.STATE -> {
                    Text("Civil State Ledger", fontSize = 28.sp, fontWeight = FontWeight.Light, color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    MetroDetailItem(title = "Ledger Profile", desc = "ID checks verify active biometric signature.")
                    MetroDetailItem(title = "Pending Items", desc = "1 bill to pay ($85.00 Electric).")
                    MetroDetailItem(title = "Voting Ballot Check", desc = "Cast your vote in the live mayoralty race.")
                }
                MetroDetail.PHONE -> {
                    Text("Phone Link Controls", fontSize = 28.sp, fontWeight = FontWeight.Light, color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    MetroDetailItem(title = "Visual Voicemail", desc = "AI spam protection filter is active.")
                    MetroDetailItem(title = "Satellite Telemetry", desc = "Link active. Link Quality: 92% (High)")
                    MetroDetailItem(title = "Masthead Link", desc = "Nearest tower Link verified at 1.4km.")
                }
                MetroDetail.MARKETPLACE -> {
                    Text("Marketplace Listings", fontSize = 28.sp, fontWeight = FontWeight.Light, color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    MetroDetailItem(title = "Active Offer: Vintage Synthesizer", desc = "Offer pending for $320.00 from @djsora.")
                    MetroDetailItem(title = "Active Listing: Leather Jacket", desc = "Listed for $150.00. 12 interested citizens.")
                    MetroDetailItem(title = "Locker Delivery Node", desc = "Pedestrian Locker #4 is reserved for collection.")
                }
                else -> {}
            }
        }
    }
}

@Composable
fun MetroDetailItem(title: String, desc: String) {
    Surface(
        shape = RoundedCornerShape(0.dp),
        color = Color.White.copy(alpha = 0.15f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = desc,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.85f),
                lineHeight = 16.sp
            )
        }
    }
}
