package com.example.ui.components.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Global Media Player - Mode 2: Video
 * Supports Device Storage Video Files, Video Hosting Platforms (Townsquare Tube, YouTube, Vimeo),
 * and custom Web Video Streams with resolution, subtitle, and aspect ratio controls.
 */
@Composable
fun GlobalVideoPlayerMode(
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedVideoCategory by remember { mutableIntStateOf(0) } // 0: Video Platforms, 1: Device Video Files, 2: Stream URL Link
    var isPlaying by remember { mutableStateOf(true) }
    var currentProgressSeconds by remember { mutableIntStateOf(164) }
    var totalDurationSeconds by remember { mutableIntStateOf(645) }
    var selectedQuality by remember { mutableStateOf("1080p FHD") }
    var selectedAspectRatio by remember { mutableStateOf("16:9") }
    var isSubtitlesEnabled by remember { mutableStateOf(true) }
    var selectedSubtitleLang by remember { mutableStateOf("English [CC]") }
    var isFullscreenSimulated by remember { mutableStateOf(false) }
    var customStreamUrl by remember { mutableStateOf("https://video.townsquare.org/live/council_stream_hd.m3u8") }

    // Sample video catalog across platforms & device storage
    val videoCatalog = remember {
        listOf(
            VideoPlayerItem(
                id = "vid_1",
                title = "Townsquare 2026 Future Architecture Drone Reel",
                creatorName = "Civic Urban Studios",
                platformSource = VideoPlatformSource.TOWNSQUARE_TUBE,
                videoUrl = "https://townsquare.org/tube/drone_4k.mp4",
                durationSeconds = 480,
                resolutionBadge = "4K 60FPS",
                viewsCount = "24.5K views",
                description = "Breathtaking 4K aerial photogrammetry of the new Townsquare central pavilion and renewable solar canopy."
            ),
            VideoPlayerItem(
                id = "vid_2",
                title = "Local Farmers Market & Harvest Documentary",
                creatorName = "Townsquare Documentaries",
                platformSource = VideoPlatformSource.YOUTUBE,
                videoUrl = "https://youtube.com/watch?v=townsquare_harvest",
                durationSeconds = 720,
                resolutionBadge = "1080p HD",
                viewsCount = "12.1K views",
                description = "Exploring 50 years of agricultural history in the valley and our Saturday community bazaar."
            ),
            VideoPlayerItem(
                id = "vid_3",
                title = "Emergency Town Hall: Autumn Storm Preparedness",
                creatorName = "Public Safety Bureau",
                platformSource = VideoPlatformSource.DEVICE_STORAGE,
                videoUrl = "/storage/emulated/0/DCIM/TownCouncil_StormPrep.mp4",
                durationSeconds = 1250,
                resolutionBadge = "1080p FHD",
                viewsCount = "Saved on Device",
                description = "Local municipal briefing on emergency flood barriers and neighborhood watch dispatch."
            ),
            VideoPlayerItem(
                id = "vid_4",
                title = "Artisan Glassblowing in the Cultural District",
                creatorName = "Vimeo Curated Arts",
                platformSource = VideoPlatformSource.VIMEO,
                videoUrl = "https://vimeo.com/channels/townsquare_glass",
                durationSeconds = 390,
                resolutionBadge = "4K UHD",
                viewsCount = "8.9K views",
                description = "A visual meditation on handcrafted blown glassware created inside the Old Brick Foundry."
            )
        )
    }

    var activeVideo by remember { mutableStateOf(videoCatalog[0]) }

    // Live video playback timer simulation
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(1000L)
            currentProgressSeconds = if (currentProgressSeconds >= totalDurationSeconds) 0 else currentProgressSeconds + 1
        }
    }

    val purpleAccent = Color(0xFF8B5CF6)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("global_video_player_mode"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(1.dp, purpleAccent.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Badge & Action Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(purpleAccent.copy(alpha = 0.2f))
                            .border(1.dp, purpleAccent, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "🎬 VIDEO PLAYER",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = purpleAccent
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${activeVideo.platformSource.badgeEmoji} ${activeVideo.platformSource.label}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.LightGray
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Fullscreen simulator button
                    IconButton(
                        onClick = { isFullscreenSimulated = !isFullscreenSimulated },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isFullscreenSimulated) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                            contentDescription = "Toggle Fullscreen",
                            tint = Color.LightGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Close
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Video Player",
                            tint = Color.LightGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // VIDEO VIEWPORT CANVAS
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isFullscreenSimulated) 280.dp else 190.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF020617))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
                    .clickable { isPlaying = !isPlaying },
                contentAlignment = Alignment.Center
            ) {
                // Background visual simulation
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF1E1B4B), Color(0xFF0F172A), Color(0xFF000000))
                            )
                        )
                )

                // Aspect Ratio indicator watermark
                Text(
                    text = "${activeVideo.resolutionBadge} • $selectedAspectRatio",
                    color = Color.White.copy(alpha = 0.2f),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                // Center Play/Pause Overlay indicator
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, purpleAccent),
                    modifier = Modifier.size(54.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = purpleAccent,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Subtitle Line Overlay
                if (isSubtitlesEnabled) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 38.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.75f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "[CC] \"...and the autonomous community tramline connects directly to Townsquare Plaza.\"",
                            color = Color.Yellow,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Bottom Timeline Scrubber & Bar
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f)))
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    val progressFloat = (currentProgressSeconds.toFloat() / totalDurationSeconds.toFloat()).coerceIn(0f, 1f)

                    Slider(
                        value = progressFloat,
                        onValueChange = { newProg ->
                            currentProgressSeconds = (newProg * totalDurationSeconds).toInt()
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = purpleAccent,
                            activeTrackColor = purpleAccent,
                            inactiveTrackColor = Color.DarkGray
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val curMins = currentProgressSeconds / 60
                        val curSecs = currentProgressSeconds % 60
                        val totMins = totalDurationSeconds / 60
                        val totSecs = totalDurationSeconds % 60

                        Text(
                            text = String.format("%02d:%02d / %02d:%02d", curMins, curSecs, totMins, totSecs),
                            color = Color.LightGray,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = selectedQuality,
                                color = purpleAccent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isSubtitlesEnabled) "CC [ON]" else "CC [OFF]",
                                color = if (isSubtitlesEnabled) Color.Green else Color.Gray,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { isSubtitlesEnabled = !isSubtitlesEnabled }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Video Title & Meta
            Text(
                text = activeVideo.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "By ${activeVideo.creatorName} • ${activeVideo.viewsCount}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Category Selectors (Video Platforms, Device Storage, Stream URL)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    Pair(0, "🌐 Platforms"),
                    Pair(1, "💾 Device Files"),
                    Pair(2, "🔗 Web Stream")
                ).forEach { (idx, label) ->
                    val isSelected = selectedVideoCategory == idx
                    val bg = if (isSelected) purpleAccent else Color(0xFF1E293B)
                    val txt = if (isSelected) Color.White else Color.LightGray

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(bg)
                            .clickable { selectedVideoCategory = idx }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = txt
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            when (selectedVideoCategory) {
                0 -> {
                    // Video Platforms list
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        videoCatalog.filter { it.platformSource != VideoPlatformSource.DEVICE_STORAGE }.forEach { item ->
                            val isCurrent = activeVideo.id == item.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isCurrent) Color(0xFF2E1065) else Color(0xFF1E293B))
                                    .border(1.dp, if (isCurrent) purpleAccent else Color.Transparent, RoundedCornerShape(10.dp))
                                    .clickable {
                                        activeVideo = item
                                        totalDurationSeconds = item.durationSeconds
                                        currentProgressSeconds = 0
                                        isPlaying = true
                                    }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Text(item.platformSource.badgeEmoji, fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(item.title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text("${item.platformSource.label} • ${item.resolutionBadge}", color = Color.LightGray, fontSize = 10.sp)
                                    }
                                }
                                Icon(
                                    imageVector = if (isCurrent && isPlaying) Icons.Default.GraphicEq else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = purpleAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                1 -> {
                    // Device Files list
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        videoCatalog.filter { it.platformSource == VideoPlatformSource.DEVICE_STORAGE }.forEach { item ->
                            val isCurrent = activeVideo.id == item.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isCurrent) Color(0xFF2E1065) else Color(0xFF1E293B))
                                    .clickable {
                                        activeVideo = item
                                        totalDurationSeconds = item.durationSeconds
                                        currentProgressSeconds = 0
                                        isPlaying = true
                                    }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Icon(Icons.Default.VideoFile, contentDescription = null, tint = purpleAccent, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(item.title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        Text("Local File: ${item.videoUrl}", color = Color.LightGray, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                                Icon(Icons.Default.PlayCircle, contentDescription = null, tint = purpleAccent, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }

                2 -> {
                    // Custom Web Stream URL input
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = customStreamUrl,
                            onValueChange = { customStreamUrl = it },
                            label = { Text("Web Stream or Video Link (HLS / MP4 / YouTube)") },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = MaterialTheme.typography.bodySmall.copy(color = Color.White),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = purpleAccent,
                                unfocusedBorderColor = Color.DarkGray
                            ),
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                activeVideo = VideoPlayerItem(
                                    id = "custom_${System.currentTimeMillis()}",
                                    title = "Custom Web Stream Feed",
                                    creatorName = "Remote Host",
                                    platformSource = VideoPlatformSource.CUSTOM_WEB,
                                    videoUrl = customStreamUrl,
                                    durationSeconds = 900,
                                    resolutionBadge = "1080p Stream",
                                    viewsCount = "Live Stream"
                                )
                                currentProgressSeconds = 0
                                isPlaying = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = purpleAccent)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Stream Custom Video", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Aspect Ratio & Quality Switchers
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Ratio:", fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    listOf("16:9", "4:3", "21:9").forEach { ratio ->
                        val active = selectedAspectRatio == ratio
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (active) purpleAccent.copy(alpha = 0.25f) else Color(0xFF1E293B))
                                .clickable { selectedAspectRatio = ratio }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(ratio, fontSize = 10.sp, color = if (active) purpleAccent else Color.LightGray)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Quality:", fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    listOf("4K", "1080p", "720p").forEach { qual ->
                        val active = selectedQuality.contains(qual)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (active) purpleAccent.copy(alpha = 0.25f) else Color(0xFF1E293B))
                                .clickable { selectedQuality = "$qual FHD" }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(qual, fontSize = 10.sp, color = if (active) purpleAccent else Color.LightGray)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
            }
        }
    }
}
