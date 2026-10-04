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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

data class CableTier(
    val id: String,
    val name: String,
    val price: String,
    val description: String,
    val channelsCount: Int,
    val badge: String,
    val color: Color,
    val includedChannels: List<String>,
    val isSubscribed: Boolean = false
)

data class WatchPartyMember(
    val id: String,
    val name: String,
    val emoji: String,
    val isHost: Boolean = false,
    val isSpeaking: Boolean = false
)

data class WatchPartyReaction(
    val emoji: String,
    val senderName: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Composable
fun TvCableSubscriptionManager(
    modifier: Modifier = Modifier
) {
    var tiers by remember {
        mutableStateOf(
            listOf(
                CableTier(
                    id = "tier_basic",
                    name = "Townsquare Civic Freeview",
                    price = "FREE (Included)",
                    description = "Public service municipal dispatches, weather radar, and community council access.",
                    channelsCount = 6,
                    badge = "STANDARD",
                    color = NeonCyan,
                    includedChannels = listOf("Townsquare News 24", "Civic Weather Radar", "Public Access 1", "Council Live"),
                    isSubscribed = true
                ),
                CableTier(
                    id = "tier_cinema",
                    name = "Silver Screen & CineMax Pack",
                    price = "$8.99 / mo",
                    description = "24/7 remastered indie cinema, film festival retrospectives, and classic Hollywood vault.",
                    channelsCount = 14,
                    badge = "POPULAR",
                    color = Color(0xFFF43F5E),
                    includedChannels = listOf("Silver Screen Classics", "Indie Cinema Vault", "Midnight Theatre HD", "Documentary Hub"),
                    isSubscribed = true
                ),
                CableTier(
                    id = "tier_sports",
                    name = "Metro Sports & Speed Tier",
                    price = "$11.99 / mo",
                    description = "Live cycling tours, municipal regattas, motorsport grand prix, and sports analytics.",
                    channelsCount = 12,
                    badge = "4K HDR",
                    color = WarmAmber,
                    includedChannels = listOf("Metro Sports 1", "Tyres Racing TV", "Civic Velodrome HD", "Extreme Outdoor"),
                    isSubscribed = false
                ),
                CableTier(
                    id = "tier_all_access",
                    name = "All-Access Platinum Pass",
                    price = "$19.99 / mo",
                    description = "Unlimited access to all cable tiers, multi-room cloud DVR, 4K UHD bitrate, and Watch Party host perks.",
                    channelsCount = 48,
                    badge = "ULTIMATE",
                    color = Color(0xFFA855F7),
                    includedChannels = listOf("All 48 Cable Channels", "VIP Watch Party Rooms", "Unlimited Cloud DVR", "Ad-Free Vault"),
                    isSubscribed = false
                )
            )
        )
    }

    var confirmationMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("tv_cable_subscription_manager"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Cable Header Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(NeonCyan.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.LiveTv, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Cable TV Subscription Manager",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Townsquare Optical Fiber Network • 48 Channels Available",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextSecondary
                    )
                }
            }
        }

        if (confirmationMessage != null) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF064E3B),
                border = BorderStroke(1.dp, Color(0xFF10B981)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF34D399))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(confirmationMessage!!, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    IconButton(onClick = { confirmationMessage = null }, modifier = Modifier.size(20.dp)) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // Tiers list
        tiers.forEach { tier ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkSurface,
                border = BorderStroke(
                    if (tier.isSubscribed) 1.5.dp else 1.dp,
                    if (tier.isSubscribed) tier.color else DarkBorder
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(tier.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = tier.color.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, tier.color)
                            ) {
                                Text(
                                    text = tier.badge,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = tier.color,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = tier.price,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black),
                            color = if (tier.isSubscribed) NeonCyan else Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(tier.description, style = MaterialTheme.typography.bodySmall, color = DarkTextSecondary)

                    Spacer(modifier = Modifier.height(10.dp))

                    // Included channels tags
                    Text("Included Channels:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkTextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        tier.includedChannels.take(3).forEach { ch ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = DarkSurfaceVariant,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Text(ch, fontSize = 10.sp, color = Color(0xFFE2E8F0), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Subscribe / Unsubscribe Button
                    Button(
                        onClick = {
                            val newSub = !tier.isSubscribed
                            tiers = tiers.map { if (it.id == tier.id) it.copy(isSubscribed = newSub) else it }
                            confirmationMessage = if (newSub) "Successfully subscribed to ${tier.name}!" else "Cancelled subscription to ${tier.name}."
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (tier.isSubscribed) Color(0xFF1E293B) else tier.color,
                            contentColor = if (tier.isSubscribed) Color.White else Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = if (tier.isSubscribed) Icons.Default.Check else Icons.Default.AddShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (tier.isSubscribed) "Active Subscription (Tap to Modify)" else "Subscribe for ${tier.price}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TvWatchPartyView(
    activeChannelName: String,
    modifier: Modifier = Modifier
) {
    val members = remember {
        mutableStateOf(
            listOf(
                WatchPartyMember("m1", "You (Citizen Host)", "👑", isHost = true, isSpeaking = true),
                WatchPartyMember("m2", "Sarah Vance", "🚇", isSpeaking = false),
                WatchPartyMember("m3", "Marcus Chen", "📰", isSpeaking = true),
                WatchPartyMember("m4", "Elena Rostova", "🏺", isSpeaking = false)
            )
        )
    }

    var floatingReactions by remember { mutableStateOf(listOf<WatchPartyReaction>()) }
    var chatInput by remember { mutableStateOf("") }
    var messages by remember {
        mutableStateOf(
            listOf(
                Pair("Sarah Vance", "Line 3 construction looks incredible on this 4K broadcast!"),
                Pair("Marcus Chen", "Running this segment on tomorrow's front page for sure."),
                Pair("Elena Rostova", "Notice the vintage stone masonry around the archway?")
            )
        )
    }

    var isMicOn by remember { mutableStateOf(true) }

    fun addReaction(emoji: String) {
        val r = WatchPartyReaction(emoji = emoji, senderName = "You")
        floatingReactions = floatingReactions + r
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("tv_watch_party_view"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Watch Party Lobby Header
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1E1B4B),
            border = BorderStroke(1.5.dp, Color(0xFF6366F1)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🎉 Townsquare Watch Party", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black), color = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF22C55E)) {
                            Text("SYNCED", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.Black, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                        }
                    }

                    Text("Room #TOWN-4491", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Color(0xFFA5B4FC), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text("Watching: $activeChannelName • Latency: 8ms", fontSize = 12.sp, color = Color(0xFFC7D2FE))

                Spacer(modifier = Modifier.height(12.dp))

                // Party Members Avatars Row
                Text("Room Citizens (${members.value.size}):", fontSize = 11.sp, color = DarkTextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(members.value) { member ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF312E81),
                            border = BorderStroke(1.dp, if (member.isSpeaking) Color(0xFF22C55E) else Color(0xFF4338CA))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(member.emoji, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Column {
                                    Text(member.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text(if (member.isSpeaking) "Speaking..." else "Listening", fontSize = 9.sp, color = if (member.isSpeaking) Color(0xFF4ADE80) else Color(0xFF94A3B8))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Voice Mic Toggle & Room Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = isMicOn,
                        onClick = { isMicOn = !isMicOn },
                        label = { Text(if (isMicOn) "Mic: Live" else "Mic: Muted", fontSize = 11.sp) },
                        leadingIcon = {
                            Icon(if (isMicOn) Icons.Default.Mic else Icons.Default.MicOff, contentDescription = null, modifier = Modifier.size(14.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF22C55E).copy(alpha = 0.2f),
                            selectedLabelColor = Color(0xFF22C55E)
                        )
                    )

                    Button(
                        onClick = { /* Invite citizen */ },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Invite Citizen", fontSize = 11.sp)
                    }
                }
            }
        }

        // Live Emoji Reaction Bar
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = DarkSurfaceVariant,
            border = BorderStroke(1.dp, DarkBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("React:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkTextSecondary)
                listOf("❤️", "👏", "🔥", "😂", "😮", "🎉").forEach { emoji ->
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                            .clickable { addReaction(emoji) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(emoji, fontSize = 18.sp)
                    }
                }
            }
        }

        // Watch Party Chat Feed
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = DarkSurface,
            border = BorderStroke(1.dp, DarkBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Party Chat", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    messages.forEach { (sender, text) ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text("$sender: ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                            Text(text, fontSize = 11.sp, color = Color(0xFFE2E8F0))
                        }
                    }

                    // Floating reactions stream
                    floatingReactions.takeLast(3).forEach { r ->
                        Text("✨ ${r.senderName} sent ${r.emoji}", fontSize = 10.sp, color = WarmAmber, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = chatInput,
                        onValueChange = { chatInput = it },
                        placeholder = { Text("Chat with watch party...", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = {
                            if (chatInput.isNotBlank()) {
                                messages = messages + Pair("You", chatInput.trim())
                                chatInput = ""
                            }
                        }
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = NeonCyan)
                    }
                }
            }
        }
    }
}
