package com.example.ui.components.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.example.data.model.TvChannelEntity
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber
import com.example.ui.viewmodel.TvChatMessage
import kotlinx.coroutines.delay

/**
 * Global Media Player - Mode 4: TV
 * Live TV Broadcasts and Livestreams with Live Interactive Chatting.
 */
@Composable
fun GlobalTvPlayerMode(
    activeChannel: TvChannelEntity?,
    allChannels: List<TvChannelEntity>,
    isPlaying: Boolean,
    isMuted: Boolean,
    isCaptionsEnabled: Boolean,
    streamQuality: String,
    scanlineFxEnabled: Boolean,
    chatMessages: List<TvChatMessage>,
    onSendMessage: (String) -> Unit,
    onTogglePlayPause: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleCaptions: () -> Unit,
    onToggleScanlineFx: () -> Unit,
    onSelectQuality: (String) -> Unit,
    onNextChannel: () -> Unit,
    onPrevChannel: () -> Unit,
    onSelectChannel: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val redTvAccent = Color(0xFFEF4444)
    var isChatDrawerOpen by remember { mutableStateOf(true) }
    var userChatDraft by remember { mutableStateOf("") }
    var localMessages by remember {
        mutableStateOf(
            if (chatMessages.isNotEmpty()) chatMessages
            else listOf(
                TvChatMessage(id = "c1", sender = "Alistair_Mayor", message = "Welcome to tonight's live broadcast!", color = 0xFFEF4444),
                TvChatMessage(id = "c2", sender = "Citizen_99", message = "The new Townsquare camera resolution is crystal clear!"),
                TvChatMessage(id = "c3", sender = "Elena_Press", message = "Live reporter crew standing by at the Pavilion 🎥"),
                TvChatMessage(id = "c4", sender = "RetroSynthFan", message = "Love the scanline CRT filter on Channel 3 🔥")
            )
        )
    }

    // Keep synced if parent updates
    LaunchedEffect(chatMessages) {
        if (chatMessages.isNotEmpty()) {
            localMessages = chatMessages
        }
    }

    val channel = activeChannel ?: allChannels.firstOrNull() ?: TvChannelEntity(
        id = "tctv_ch1",
        channelNumber = 1,
        name = "First Programme",
        callsign = "TCTV-1",
        tagline = "Townsquare Flagship News & Live Affairs",
        category = "News",
        themeColorHex = 0xFF00D2FF,
        iconEmoji = "🏛️",
        currentShowTitle = "Townsquare Central News at 7",
        currentShowCategory = "News",
        currentShowTime = "19:00 - 20:00",
        currentShowSynopsis = "Live municipal report from the central chamber.",
        hostPresenter = "Julian Vance",
        nextShowTitle = "Townsquare In-Depth",
        nextShowTime = "20:00"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("global_tv_player_mode"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(1.dp, redTvAccent.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Badge & Live Viewer Count & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(redTvAccent.copy(alpha = 0.2f))
                            .border(1.dp, redTvAccent, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(redTvAccent)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LIVE TV & CHAT",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = redTvAccent
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "👁️ ${channel.liveViewersCount / 1000.0}K watching",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.LightGray
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Chat drawer toggle
                    IconButton(
                        onClick = { isChatDrawerOpen = !isChatDrawerOpen },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isChatDrawerOpen) Icons.Default.ChatBubble else Icons.Default.ChatBubbleOutline,
                            contentDescription = "Toggle Chat",
                            tint = if (isChatDrawerOpen) redTvAccent else Color.LightGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Mute
                    IconButton(
                        onClick = onToggleMute,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = "Mute",
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
                            contentDescription = "Close TV Player",
                            tint = Color.LightGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // TV STREAM CANVAS
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF000000))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Background broadcast gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF1E1010), Color(0xFF0F0808), Color(0xFF000000))
                            )
                        )
                )

                // Scanline retro FX simulation
                if (scanlineFxEnabled) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    List(40) { if (it % 2 == 0) Color.Transparent else Color.Black.copy(alpha = 0.25f) }
                                )
                            )
                    )
                }

                // Station Callsign / Channel bug watermark in top-right
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(channel.iconEmoji, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${channel.callsign} • ${channel.channelNumber}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Center Play/Pause button
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, redTvAccent),
                    modifier = Modifier
                        .size(50.dp)
                        .clickable { onTogglePlayPause() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = redTvAccent,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                // Closed Captions Subtitle Overlay
                if (isCaptionsEnabled) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.8f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "[LIVE CC] Host: \"...bringing you uninterrupted live transmission across all municipal channels.\"",
                            color = Color.Yellow,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Channel Title & Quick Channel Switcher Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${channel.channelNumber}. ${channel.name} • ${channel.currentShowTitle}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Host: ${channel.hostPresenter} • ${channel.currentShowTime}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onPrevChannel, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.SkipPrevious, contentDescription = "Prev Channel", tint = Color.LightGray)
                    }
                    IconButton(onClick = onNextChannel, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.SkipNext, contentDescription = "Next Channel", tint = Color.LightGray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Channel Badges 1 to 6
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                allChannels.take(6).forEach { ch ->
                    val isCurrent = ch.id == channel.id
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isCurrent) redTvAccent else Color(0xFF1E293B))
                            .clickable { onSelectChannel(ch.id) }
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "CH ${ch.channelNumber}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCurrent) Color.White else Color.LightGray
                        )
                    }
                }
            }

            // LIVE CHAT PANEL
            AnimatedVisibility(visible = isChatDrawerOpen) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF020617))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💬 Live Community Stream Chat",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = redTvAccent
                        )
                        Text(
                            text = "${localMessages.size} messages",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Chat messages list
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(localMessages) { msg ->
                            val isHost = msg.sender.contains("Mayor") || msg.sender.contains("Host") || msg.color == 0xFFEF4444
                            Row(verticalAlignment = Alignment.Top) {
                                Text(
                                    text = "${msg.sender}:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isHost) redTvAccent else NeonCyan
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = msg.message,
                                    fontSize = 11.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Emoji quick reactions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("🔥", "👏", "❤️", "📺", "😮").forEach { emoji ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF1E293B))
                                    .clickable {
                                        val newMsg = TvChatMessage(
                                            id = "user_${System.currentTimeMillis()}",
                                            sender = "You",
                                            message = emoji
                                        )
                                        localMessages = localMessages + newMsg
                                        onSendMessage(emoji)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(emoji, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Chat input field
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = userChatDraft,
                            onValueChange = { userChatDraft = it },
                            placeholder = { Text("Send message to live broadcast...", fontSize = 11.sp, color = Color.Gray) },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            textStyle = MaterialTheme.typography.bodySmall.copy(color = Color.White),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = redTvAccent,
                                unfocusedBorderColor = Color(0xFF334155)
                            ),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = {
                                if (userChatDraft.isNotBlank()) {
                                    val newMsg = TvChatMessage(
                                        id = "user_${System.currentTimeMillis()}",
                                        sender = "You",
                                        message = userChatDraft
                                    )
                                    localMessages = localMessages + newMsg
                                    onSendMessage(userChatDraft)
                                    userChatDraft = ""
                                }
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(redTvAccent)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send Chat",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
