package com.example.ui.plus.waves

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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MediaItemEntity
import com.example.ui.theme.*

@Composable
fun WavesGridCatalog(
    songs: List<MediaItemEntity>,
    playlists: List<MediaItemEntity>,
    podcasts: List<MediaItemEntity>,
    onPlayAudio: (MediaItemEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("Trending Albums") }
    val haptic = LocalHapticFeedback.current

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Category Pills
        val categories = listOf("Trending Albums", "Curated Playlists", "On-Demand Podcasts")
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategory == cat
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) NeonCyan else Color(0xFF1E293B),
                    modifier = Modifier
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedCategory = cat
                        }
                        .testTag("waves_cat_${cat.take(3)}")
                ) {
                    Text(
                        text = cat,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (isSelected) Color(0xFF003544) else Color.White,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // Grid / Row content depending on category
        when (selectedCategory) {
            "Trending Albums" -> {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    songs.forEach { song ->
                        WaveCatalogCard(
                            title = song.title,
                            subtitle = song.subtitle.ifEmpty { "By ${song.authorName}" },
                            emoji = "💿",
                            tag = "ALBUM STREAM",
                            onPlay = { onPlayAudio(song) }
                        )
                    }
                }
            }
            "Curated Playlists" -> {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    playlists.forEach { playlist ->
                        WaveCatalogCard(
                            title = playlist.title,
                            subtitle = playlist.subtitle.ifEmpty { "Curated Playlist" },
                            emoji = "🎧",
                            tag = "PLAYLIST",
                            onPlay = { onPlayAudio(playlist) }
                        )
                    }
                }
            }
            else -> {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    podcasts.forEach { podcast ->
                        val detectedSongs = SongDetectionEngine.getPodcastDetectedSongs(podcast.id.toString())
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF131D30)),
                            border = BorderStroke(1.dp, Color(0xFF1E293B)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Text("🎙️", fontSize = 24.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = podcast.title,
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                color = Color.White,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = "Host: ${podcast.authorName} • ${podcast.durationSeconds / 60} mins",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF94A3B8)
                                            )
                                        }
                                    }

                                    FilledTonalButton(
                                        onClick = { onPlayAudio(podcast) },
                                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = WarmAmber, contentColor = Color(0xFF261800)),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("Play", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "AUTOMATICALLY DETECTED SONGS IN EPISODE (TIMESTAMPS):",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 9.sp),
                                    color = NeonCyan
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                detectedSongs.forEach { ds ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = NeonCyan.copy(alpha = 0.2f)
                                            ) {
                                                Text(
                                                    text = ds.timestampText,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = NeonCyan,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "${ds.title} — ${ds.artist}",
                                                fontSize = 12.sp,
                                                color = Color.White
                                            )
                                        }
                                        Text(ds.albumArtEmoji, fontSize = 14.sp)
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

@Composable
private fun WaveCatalogCard(
    title: String,
    subtitle: String,
    emoji: String,
    tag: String,
    onPlay: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF131D30),
        border = BorderStroke(1.dp, Color(0xFF1E293B)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onPlay()
            }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = NeonCyan.copy(alpha = 0.15f),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(emoji, fontSize = 20.sp)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF1E293B),
                        modifier = Modifier.padding(bottom = 2.dp)
                    ) {
                        Text(
                            text = tag,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        maxLines = 1
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8),
                        maxLines = 1
                    )
                }
            }

            IconButton(onClick = onPlay) {
                Icon(Icons.Default.PlayCircle, contentDescription = "Play", tint = NeonCyan, modifier = Modifier.size(32.dp))
            }
        }
    }
}
