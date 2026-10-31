package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.TvChannelEntity
import com.example.data.model.TvScheduleItem
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber

/**
 * Foldable Global Live TV Player
 * Transitions between a floating mini Picture-in-Picture card at the bottom of the screen
 * and an immersive fullscreen view with full TV remote keypad and per-channel EPG schedule.
 */
@Composable
fun FoldableTvPlayerView(
    channel: TvChannelEntity,
    allChannels: List<TvChannelEntity>,
    isPlaying: Boolean,
    isMuted: Boolean,
    isCaptionsEnabled: Boolean,
    streamQuality: String,
    scanlineFxEnabled: Boolean,
    onTogglePlayPause: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleCaptions: () -> Unit,
    onToggleScanlineFx: () -> Unit,
    onSelectQuality: (String) -> Unit,
    onNextChannel: () -> Unit,
    onPrevChannel: () -> Unit,
    onSelectChannel: (String) -> Unit,
    onClose: () -> Unit,
    onRecordShow: (TvScheduleItem) -> Unit = {},
    onScheduleAlert: (TvScheduleItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isExpandedFullscreen by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "live_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val channelColor = Color(channel.themeColorHex)

    // Fullscreen View Modal with Remote & EPG
    if (isExpandedFullscreen) {
        Dialog(
            onDismissRequest = { isExpandedFullscreen = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("fullscreen_tv_player_dialog"),
                color = Color(0xFF090D16)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                ) {
                    // Top Modal Header Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.7f))
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Fold back to mini player button
                        FilledTonalButton(
                            onClick = { isExpandedFullscreen = false },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color.White.copy(alpha = 0.12f),
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("btn_fold_tv_player")
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Fold to Mini Player",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Fold Mini", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // Channel Callsign & Viewers
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFFFF3B30).copy(alpha = pulseAlpha), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${channel.callsign} • CH ${channel.channelNumber}",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = channelColor
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            val viewersK = channel.liveViewersCount / 1000.0
                            Text(
                                text = "👥 ${viewersK}K",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = Color.LightGray
                            )
                        }

                        // Close Stream Button
                        IconButton(
                            onClick = {
                                isExpandedFullscreen = false
                                onClose()
                            },
                            modifier = Modifier.size(36.dp).testTag("btn_close_tv_fullscreen")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close Broadcast", tint = Color.White)
                        }
                    }

                    // Main 16:9 Video Broadcast Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        TvBroadcastPlayer(
                            channel = channel,
                            isPlaying = isPlaying,
                            isMuted = isMuted,
                            isCaptionsEnabled = isCaptionsEnabled,
                            streamQuality = streamQuality,
                            scanlineFxEnabled = scanlineFxEnabled,
                            onTogglePlayPause = onTogglePlayPause,
                            onToggleMute = onToggleMute,
                            onToggleCaptions = onToggleCaptions,
                            onToggleFullscreen = { isExpandedFullscreen = false },
                            onToggleScanlineFx = onToggleScanlineFx,
                            onSelectQuality = onSelectQuality,
                            onNextChannel = onNextChannel,
                            onPrevChannel = onPrevChannel,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Scrollable Remote Keypad and Live Channel EPG Drawer
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Section 1: Channel Remote Keypad
                        item {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = DarkSurfaceElevated,
                                border = BorderStroke(1.dp, DarkBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "📺 DIGITAL REMOTE CONTROL",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 1.sp
                                            ),
                                            color = NeonCyan
                                        )
                                        Text(
                                            text = "Tap button to tune",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.Gray
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Keypad 6 channels in 2 rows of 3
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        val rows = allChannels.chunked(3)
                                        rows.forEach { rowChannels ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                rowChannels.forEach { ch ->
                                                    val isSelected = ch.id == channel.id
                                                    val chTheme = Color(ch.themeColorHex)
                                                    Surface(
                                                        shape = RoundedCornerShape(10.dp),
                                                        color = if (isSelected) chTheme.copy(alpha = 0.25f) else Color(0xFF131B2B),
                                                        border = BorderStroke(1.dp, if (isSelected) chTheme else Color(0xFF202C44)),
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .clickable { onSelectChannel(ch.id) }
                                                            .testTag("remote_keypad_ch${ch.channelNumber}")
                                                    ) {
                                                        Column(
                                                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                                            horizontalAlignment = Alignment.CenterHorizontally
                                                        ) {
                                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                                Text(
                                                                    text = "${ch.channelNumber}",
                                                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                                                    color = if (isSelected) chTheme else Color.White
                                                                )
                                                                Spacer(modifier = Modifier.width(4.dp))
                                                                Text(ch.iconEmoji, fontSize = 12.sp)
                                                            }
                                                            Text(
                                                                text = ch.name,
                                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                                color = Color.LightGray,
                                                                maxLines = 1,
                                                                overflow = TextOverflow.Ellipsis
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Section 2: Channel Live EPG Schedule Drawer
                        item {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = DarkSurfaceElevated,
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
                                            Text(
                                                text = "📅 ${channel.name} LIVE EPG",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    letterSpacing = 1.sp
                                                ),
                                                color = WarmAmber
                                            )
                                        }
                                        Text(
                                            text = "Today's Broadcasts",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.Gray
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Schedule items for this channel
                                    val sampleSchedules = remember(channel.channelNumber) {
                                        getSampleChannelSchedule(channel.channelNumber)
                                    }

                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        sampleSchedules.forEach { item ->
                                            ScheduleItemRow(
                                                schedule = item,
                                                channelColor = channelColor,
                                                onRecord = { onRecordShow(item) },
                                                onAlert = { onScheduleAlert(item) }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        item { Spacer(modifier = Modifier.height(24.dp)) }
                    }
                }
            }
        }
    }

    // Folded State: Compact Mini Video Player Card
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = DarkSurface,
        border = BorderStroke(1.dp, channelColor.copy(alpha = 0.4f)),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clickable { isExpandedFullscreen = true }
            .testTag("mini_tv_player_card")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Mini 16:9 Video Canvas Preview
            Box(
                modifier = Modifier
                    .width(84.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black)
            ) {
                // Mini animated canvas representation
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(
                        brush = Brush.linearGradient(
                            listOf(channelColor.copy(alpha = 0.5f), Color(0xFF0F172A))
                        )
                    )
                }

                // Mini LIVE Pill
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(3.dp)
                        .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .background(
                                if (isPlaying) Color(0xFFFF3B30).copy(alpha = pulseAlpha) else Color.Gray,
                                CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = if (isPlaying) "LIVE" else "PAUSE",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Expand icon overlay on tap preview
                Icon(
                    imageVector = Icons.Default.Fullscreen,
                    contentDescription = "Expand Fullscreen",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.BottomEnd)
                        .padding(2.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Center: Channel & Show Info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${channel.callsign} • CH ${channel.channelNumber}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = channelColor
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "• ${channel.currentShowTime}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = Color.Gray
                    )
                }
                Text(
                    text = channel.currentShowTitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    ),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Right: Surfing Controls & Expand/Close
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Prev Channel
                IconButton(
                    onClick = onPrevChannel,
                    modifier = Modifier.size(32.dp).testTag("mini_tv_prev_ch")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Channel",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Play / Pause
                IconButton(
                    onClick = onTogglePlayPause,
                    modifier = Modifier.size(36.dp).testTag("mini_tv_play_pause")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Next Channel
                IconButton(
                    onClick = onNextChannel,
                    modifier = Modifier.size(32.dp).testTag("mini_tv_next_ch")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next Channel",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Expand Fullscreen
                IconButton(
                    onClick = { isExpandedFullscreen = true },
                    modifier = Modifier.size(32.dp).testTag("mini_tv_expand")
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInFull,
                        contentDescription = "Expand Fullscreen Remote",
                        tint = WarmAmber,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Stop Broadcast
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(28.dp).testTag("mini_tv_close")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Stop Broadcast",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ScheduleItemRow(
    schedule: TvScheduleItem,
    channelColor: Color,
    onRecord: () -> Unit,
    onAlert: () -> Unit
) {
    var isRecorded by remember { mutableStateOf(schedule.isRecorded) }
    var isReminder by remember { mutableStateOf(schedule.isReminderSet) }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (schedule.isLiveNow) channelColor.copy(alpha = 0.12f) else Color(0xFF101726),
        border = BorderStroke(1.dp, if (schedule.isLiveNow) channelColor.copy(alpha = 0.5f) else Color(0xFF1E293B)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = schedule.timeSlot,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        ),
                        color = if (schedule.isLiveNow) channelColor else NeonCyan
                    )
                    if (schedule.isLiveNow) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFFF3B30).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "ON AIR",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFF3B30),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = schedule.category,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = Color.Gray
                    )
                }

                Text(
                    text = schedule.title,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // Record Button for infinite viewing
                IconButton(
                    onClick = {
                        isRecorded = !isRecorded
                        onRecord()
                    },
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = if (isRecorded) Icons.Default.CheckCircle else Icons.Default.FiberDvr,
                        contentDescription = "Record for infinite viewing",
                        tint = if (isRecorded) Color(0xFF00E676) else Color.LightGray,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Alert Reminder
                IconButton(
                    onClick = {
                        isReminder = !isReminder
                        onAlert()
                    },
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = if (isReminder) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                        contentDescription = "Schedule alert",
                        tint = if (isReminder) WarmAmber else Color.LightGray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

private fun getSampleChannelSchedule(channelNumber: Int): List<TvScheduleItem> {
    return when (channelNumber) {
        1 -> listOf(
            TvScheduleItem(channelNumber = 1, timeSlot = "18:00 - 19:00", title = "Townsquare Press Hour", category = "News", durationMinutes = 60, synopsis = "Local and regional headlines.", isLiveNow = false),
            TvScheduleItem(channelNumber = 1, timeSlot = "19:00 - 20:00", title = "Evening News Desk: Metro Today", category = "Current Affairs", durationMinutes = 60, synopsis = "In-depth investigative reports.", isLiveNow = true, isPrimeTime = true),
            TvScheduleItem(channelNumber = 1, timeSlot = "20:00 - 21:00", title = "Civic Town Hall: Infrastructure Q&A", category = "Civics", durationMinutes = 60, synopsis = "Mayor and city engineers respond to questions.", isPrimeTime = true),
            TvScheduleItem(channelNumber = 1, timeSlot = "21:00 - 22:30", title = "Global Satellite Wire Roundup", category = "World News", durationMinutes = 90, synopsis = "Foreign correspondents analysis.", isPrimeTime = true)
        )
        2 -> listOf(
            TvScheduleItem(channelNumber = 2, timeSlot = "17:00 - 18:30", title = "Symphony Hall: Beethoven's Ninth", category = "Classical", durationMinutes = 90, synopsis = "Metropolitan Orchestra performance.", isLiveNow = false),
            TvScheduleItem(channelNumber = 2, timeSlot = "18:30 - 20:30", title = "Townsquare Philharmonic Live", category = "Live Concert", durationMinutes = 120, synopsis = "Live from the Central Auditorium.", isLiveNow = true, isPrimeTime = true),
            TvScheduleItem(channelNumber = 2, timeSlot = "20:30 - 22:00", title = "Jazz Vault: Blue Note Sessions", category = "Jazz", durationMinutes = 90, synopsis = "Rare remastered recordings.", isPrimeTime = true)
        )
        3 -> listOf(
            TvScheduleItem(channelNumber = 3, timeSlot = "17:30 - 19:00", title = "Cosmos: The James Webb Archive", category = "Space", durationMinutes = 90, synopsis = "Deep infrared vistas.", isLiveNow = false),
            TvScheduleItem(channelNumber = 3, timeSlot = "19:00 - 20:30", title = "Quantum Horizons & Particle Physics", category = "Science", durationMinutes = 90, synopsis = "Subatomic explorations.", isLiveNow = true, isPrimeTime = true),
            TvScheduleItem(channelNumber = 3, timeSlot = "20:30 - 22:00", title = "Marine Biology: Abyssal Depths", category = "Nature", durationMinutes = 90, synopsis = "Bioluminescent creatures.", isPrimeTime = true)
        )
        4 -> listOf(
            TvScheduleItem(channelNumber = 4, timeSlot = "16:00 - 18:00", title = "Championship Athletics Recap", category = "Athletics", durationMinutes = 120, synopsis = "Track and field results.", isLiveNow = false),
            TvScheduleItem(channelNumber = 4, timeSlot = "18:00 - 20:00", title = "Metro Football Derby Live", category = "Football", durationMinutes = 120, synopsis = "Rivalry match under floodlights.", isLiveNow = true, isPrimeTime = true),
            TvScheduleItem(channelNumber = 4, timeSlot = "20:00 - 22:00", title = "Post-Game Analysis & Highlights", category = "Sports Panel", durationMinutes = 120, synopsis = "Pundits debate tactics.", isPrimeTime = true)
        )
        5 -> listOf(
            TvScheduleItem(channelNumber = 5, timeSlot = "17:00 - 18:30", title = "Cinema Classics: Noir Shadows", category = "Classic Cinema", durationMinutes = 90, synopsis = "1940s detective intrigue.", isLiveNow = false),
            TvScheduleItem(channelNumber = 5, timeSlot = "18:30 - 20:00", title = "The French Postmaster (Series)", category = "Drama", durationMinutes = 90, synopsis = "Provencal mystery unfolds.", isLiveNow = true, isPrimeTime = true),
            TvScheduleItem(channelNumber = 5, timeSlot = "20:00 - 22:00", title = "Original Stage Theatre Live", category = "Playhouse", durationMinutes = 120, synopsis = "Live theatre broadcast.", isPrimeTime = true)
        )
        else -> listOf(
            TvScheduleItem(channelNumber = 6, timeSlot = "17:00 - 19:00", title = "Chiptune & Indie Game Showcase", category = "Gaming", durationMinutes = 120, synopsis = "Indie developers live.", isLiveNow = false),
            TvScheduleItem(channelNumber = 6, timeSlot = "19:00 - 21:00", title = "Synthwave Soundstage: DJ Neon", category = "Electronic Music", durationMinutes = 120, synopsis = "Retrowave beats and visuals.", isLiveNow = true, isPrimeTime = true),
            TvScheduleItem(channelNumber = 6, timeSlot = "21:00 - 23:00", title = "Indie Animation Underground", category = "Animation", durationMinutes = 120, synopsis = "Experimental short films.", isPrimeTime = true)
        )
    }
}
