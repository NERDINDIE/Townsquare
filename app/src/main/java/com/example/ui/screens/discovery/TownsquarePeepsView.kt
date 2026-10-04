package com.example.ui.screens.discovery

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

data class PeepItem(
    val id: String,
    val authorName: String,
    val authorHandle: String,
    val authorAvatar: String,
    val authorRole: String,
    val isVerified: Boolean = true,
    val content: String,
    val tag: String, // "#Breaking", "#TransitAlert", "#WeatherWarning", "#CultureDrop", "#CivicNotice"
    val timestamp: String,
    val audioSnippetDurationSec: Int = 0,
    val factCheckedSourceCount: Int = 2,
    var likesCount: Int = 0,
    var repeepsCount: Int = 0,
    var isLiked: Boolean = false,
    var isRepeeped: Boolean = false,
    var isBookmarked: Boolean = false
)

object TownsquarePeepsSeedData {
    fun getInitialPeeps(): List<PeepItem> = listOf(
        PeepItem(
            id = "peep_1",
            authorName = "Canal Transit Control",
            authorHandle = "@metro_dispatch",
            authorAvatar = "🚇",
            authorRole = "Municipal Transit Authority",
            content = "🚨 TRANSIT UPDATE: Line 3 Electric Tramway resumes full service across Canal Bridge following routine coil maintenance. All 8 heritage trams operating on standard 6-minute headway.",
            tag = "#TransitAlert",
            timestamp = "4m ago",
            audioSnippetDurationSec = 8,
            factCheckedSourceCount = 3,
            likesCount = 284,
            repeepsCount = 92
        ),
        PeepItem(
            id = "peep_2",
            authorName = "Meteorological Station",
            authorHandle = "@weatherman_hub",
            authorAvatar = "🌦️",
            authorRole = "Chief Weather Desk",
            content = "☀️ Crisp autumn golden hour expected from 16:30. Gentle coastal breeze at 8 knots, low humidity (42%), ideal conditions for outdoor reading and harbor terrace strolls.",
            tag = "#WeatherWarning",
            timestamp = "18m ago",
            audioSnippetDurationSec = 6,
            factCheckedSourceCount = 2,
            likesCount = 412,
            repeepsCount = 54
        ),
        PeepItem(
            id = "peep_3",
            authorName = "Civic Hall Press Office",
            authorHandle = "@civic_press",
            authorAvatar = "🏛️",
            authorRole = "Official Government Communications",
            content = "📢 Council unanimously approved Phase II of the Historic Cobblestone Pedestrian Zone. Zero-interest micro-grants now open for artisan workshops and independent bookstands.",
            tag = "#CivicNotice",
            timestamp = "35m ago",
            audioSnippetDurationSec = 10,
            factCheckedSourceCount = 4,
            likesCount = 590,
            repeepsCount = 138
        ),
        PeepItem(
            id = "peep_4",
            authorName = "Square Arts Foundation",
            authorHandle = "@square_arts",
            authorAvatar = "🎨",
            authorRole = "Cultural Heritage Trust",
            content = "✨ TONIGHT: Master Gutenberg typesetting exhibition opens at 19:00 in the Central Printing Hall. Archival broadsheets from 1924 on display with live hand-press demonstrations.",
            tag = "#CultureDrop",
            timestamp = "1h ago",
            audioSnippetDurationSec = 5,
            factCheckedSourceCount = 2,
            likesCount = 340,
            repeepsCount = 76
        ),
        PeepItem(
            id = "peep_5",
            authorName = "Port Harbor Emergency Safety",
            authorHandle = "@harbor_safety",
            authorAvatar = "🚨",
            authorRole = "Harbor Police & Coast Guard",
            content = "⚓ Maritime notice: Pier 4 navigation beacon green light restored. Small recreational sailboats and rowboats cleared for evening basin cruising.",
            tag = "#Breaking",
            timestamp = "2h ago",
            audioSnippetDurationSec = 7,
            factCheckedSourceCount = 3,
            likesCount = 195,
            repeepsCount = 28
        )
    )
}

@Composable
fun TownsquarePeepsView(
    modifier: Modifier = Modifier
) {
    var peeps by remember { mutableStateOf(TownsquarePeepsSeedData.getInitialPeeps()) }
    var selectedTagFilter by remember { mutableStateOf("All") }
    var isComposeDialogOpen by remember { mutableStateOf(false) }
    var playingAudioPeepId by remember { mutableStateOf<String?>(null) }
    var audioProgressSec by remember { mutableIntStateOf(0) }

    // Audio snippet simulation
    LaunchedEffect(playingAudioPeepId) {
        if (playingAudioPeepId != null) {
            audioProgressSec = 0
            while (playingAudioPeepId != null && audioProgressSec < 8) {
                delay(1000)
                audioProgressSec += 1
            }
            playingAudioPeepId = null
        }
    }

    // New Peep Compose State
    var composeText by remember { mutableStateOf("") }
    var composeTag by remember { mutableStateOf("#CivicNotice") }
    var composeAuthorRole by remember { mutableStateOf("Verified Citizen Reporter") }

    val tags = listOf("All", "#Breaking", "#TransitAlert", "#WeatherWarning", "#CultureDrop", "#CivicNotice")

    val filteredPeeps = remember(peeps, selectedTagFilter) {
        if (selectedTagFilter == "All") peeps
        else peeps.filter { it.tag.equals(selectedTagFilter, ignoreCase = true) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .testTag("townsquare_peeps_view")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Info & Tag Filter Chips
            Surface(
                color = DarkSurface,
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🐥", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Townsquare Peeps", fontWeight = FontWeight.Black, fontSize = 15.sp, color = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.Default.Verified, contentDescription = "Verified Stream", tint = NeonCyan, modifier = Modifier.size(16.dp))
                                }
                                Text("Short-form, real-time verified civic news bites & audio dispatches", fontSize = 11.sp, color = DarkTextSecondary)
                            }
                        }

                        Button(
                            onClick = { isComposeDialogOpen = true },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Peep", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tags filter
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(tags) { tag ->
                            val isSel = selectedTagFilter == tag
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSel) NeonCyan else Color(0xFF1E293B),
                                border = BorderStroke(1.dp, if (isSel) NeonCyan else DarkBorder),
                                modifier = Modifier.clickable { selectedTagFilter = tag }
                            ) {
                                Text(
                                    text = tag,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color(0xFF003544) else Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Peeps Stream List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredPeeps, key = { it.id }) { peep ->
                    val isAudioPlaying = playingAudioPeepId == peep.id

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = DarkSurfaceElevated,
                        border = BorderStroke(1.dp, if (peep.tag == "#Breaking") CoralRed.copy(alpha = 0.5f) else DarkBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("peep_card_${peep.id}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Header Row: Avatar, Author, Verified Badge, Timestamp
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFF1E293B),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(peep.authorAvatar, fontSize = 20.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(peep.authorName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(Icons.Default.Verified, contentDescription = "Verified", tint = NeonCyan, modifier = Modifier.size(14.dp))
                                        }
                                        Text("${peep.authorHandle} • ${peep.authorRole}", fontSize = 10.sp, color = DarkTextSecondary)
                                    }
                                }

                                Text(peep.timestamp, fontSize = 10.sp, color = DarkTextMuted)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Tag Pill
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = when (peep.tag) {
                                    "#Breaking" -> CoralRed.copy(alpha = 0.2f)
                                    "#TransitAlert" -> NeonCyan.copy(alpha = 0.2f)
                                    "#WeatherWarning" -> WarmAmber.copy(alpha = 0.2f)
                                    else -> Color(0xFF2C3E50)
                                }
                            ) {
                                Text(
                                    text = peep.tag,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (peep.tag) {
                                        "#Breaking" -> CoralRed
                                        "#TransitAlert" -> NeonCyan
                                        "#WeatherWarning" -> WarmAmber
                                        else -> Color.LightGray
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Content Body
                            Text(
                                text = peep.content,
                                fontSize = 12.sp,
                                color = Color(0xFFE2E8F0),
                                lineHeight = 17.sp
                            )

                            // Audio Byte Player Bar (if available)
                            if (peep.audioSnippetDurationSec > 0) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF0F1E2E),
                                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.3f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(
                                            onClick = {
                                                playingAudioPeepId = if (isAudioPlaying) null else peep.id
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                contentDescription = "Play Audio Byte",
                                                tint = NeonCyan,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("🎙️ Official Audio Byte (${peep.audioSnippetDurationSec}s)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            if (isAudioPlaying) {
                                                LinearProgressIndicator(
                                                    progress = { audioProgressSec / 8f },
                                                    modifier = Modifier.fillMaxWidth().height(3.dp),
                                                    color = NeonCyan,
                                                    trackColor = Color.DarkGray
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = DarkBorder)
                            Spacer(modifier = Modifier.height(6.dp))

                            // Action Buttons & Fact Check Shield
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Like button
                                    TextButton(
                                        onClick = {
                                            peeps = peeps.map {
                                                if (it.id == peep.id) {
                                                    val newLiked = !it.isLiked
                                                    it.copy(isLiked = newLiked, likesCount = if (newLiked) it.likesCount + 1 else it.likesCount - 1)
                                                } else it
                                            }
                                        },
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (peep.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = null,
                                            tint = if (peep.isLiked) CoralRed else Color.LightGray,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("${peep.likesCount}", fontSize = 11.sp, color = if (peep.isLiked) CoralRed else Color.LightGray)
                                    }

                                    // Re-Peep / Boost
                                    TextButton(
                                        onClick = {
                                            peeps = peeps.map {
                                                if (it.id == peep.id) {
                                                    val newRep = !it.isRepeeped
                                                    it.copy(isRepeeped = newRep, repeepsCount = if (newRep) it.repeepsCount + 1 else it.repeepsCount - 1)
                                                } else it
                                            }
                                        },
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Repeat,
                                            contentDescription = null,
                                            tint = if (peep.isRepeeped) Color(0xFF4CAF50) else Color.LightGray,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("${peep.repeepsCount}", fontSize = 11.sp, color = if (peep.isRepeeped) Color(0xFF4CAF50) else Color.LightGray)
                                    }
                                }

                                // Fact Check Shield
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Security, contentDescription = "Fact Checked", tint = Color(0xFF4CAF50), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Verified by ${peep.factCheckedSourceCount} desks", fontSize = 10.sp, color = Color(0xFFA5D6A7))
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        }
    }

    // Compose Peep Dialog
    if (isComposeDialogOpen) {
        AlertDialog(
            onDismissRequest = { isComposeDialogOpen = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🐥", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Dispatch a Verified Peep", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Share a verified micro-dispatch to the municipal news stream (Max 280 chars).", fontSize = 11.sp, color = DarkTextSecondary)

                    OutlinedTextField(
                        value = composeText,
                        onValueChange = { if (it.length <= 280) composeText = it },
                        label = { Text("What is happening in Townsquare?") },
                        placeholder = { Text("e.g. Canal water level normal; street musicians performing at the pavilion...") },
                        modifier = Modifier.fillMaxWidth().height(110.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${280 - composeText.length} chars left", fontSize = 10.sp, color = DarkTextSecondary)
                        Text(composeTag, fontSize = 10.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                    }

                    // Select Tag
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(listOf("#CivicNotice", "#Breaking", "#TransitAlert", "#WeatherWarning", "#CultureDrop")) { tag ->
                            val isSel = composeTag == tag
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSel) NeonCyan else Color(0xFF1E293B),
                                modifier = Modifier.clickable { composeTag = tag }
                            ) {
                                Text(tag, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isSel) Color.Black else Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (composeText.isNotBlank()) {
                            val newPeep = PeepItem(
                                id = "peep_${System.currentTimeMillis()}",
                                authorName = "Citizen Dispatcher",
                                authorHandle = "@citizen_live",
                                authorAvatar = "👤",
                                authorRole = composeAuthorRole,
                                content = composeText,
                                tag = composeTag,
                                timestamp = "Just now",
                                audioSnippetDurationSec = 6,
                                factCheckedSourceCount = 2,
                                likesCount = 1
                            )
                            peeps = listOf(newPeep) + peeps
                            composeText = ""
                            isComposeDialogOpen = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544))
                ) {
                    Text("Dispatch Peep", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { isComposeDialogOpen = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }
}
