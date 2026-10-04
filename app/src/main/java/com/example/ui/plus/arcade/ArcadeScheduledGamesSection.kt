package com.example.ui.plus.arcade

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

data class ScheduledGameMatch(
    val id: String,
    val title: String,
    val gameType: String,
    val scheduledTime: String,
    val participantsCount: Int,
    val prizePool: String,
    val isLiveNow: Boolean = false,
    val casterName: String = "Caster Alex",
    val badge: String = "TOURNAMENT"
)

data class SavedDubbedBroadcast(
    val id: String,
    val matchTitle: String,
    val durationText: String,
    val recordedDate: String,
    val finalScore: String,
    val dubTrackTitle: String,
    val likesCount: Int = 24,
    val isPlaying: Boolean = false
)

@Composable
fun ArcadeScheduledGamesSection(
    modifier: Modifier = Modifier
) {
    var activeMatchSession by remember { mutableStateOf<ScheduledGameMatch?>(null) }
    var isDubbingActive by remember { mutableStateOf(false) }
    var micGain by remember { mutableFloatStateOf(0.85f) }
    var dubbingDurationSec by remember { mutableIntStateOf(0) }
    var soundFxPlayed by remember { mutableStateOf<String?>(null) }
    var saveSuccessMessage by remember { mutableStateOf<String?>(null) }
    var watchingOnDemandBroadcast by remember { mutableStateOf<SavedDubbedBroadcast?>(null) }
    var onDemandProgress by remember { mutableFloatStateOf(0.35f) }

    val scheduledMatches = remember {
        listOf(
            ScheduledGameMatch(
                id = "match_1",
                title = "Inter-District Space Invaders Cup",
                gameType = "SPACE_INVADERS",
                scheduledTime = "LIVE NOW",
                participantsCount = 128,
                prizePool = "2,500 Credits",
                isLiveNow = true,
                casterName = "Alex Chen & Citizen Chat",
                badge = "FEATURED LIVE"
            ),
            ScheduledGameMatch(
                id = "match_2",
                title = "Grand Block Breaker Speedrun Showdown",
                gameType = "BLOCK_BREAKER",
                scheduledTime = "Today 19:30",
                participantsCount = 64,
                prizePool = "1,200 Credits",
                isLiveNow = false,
                casterName = "Elena Vance",
                badge = "UPCOMING"
            ),
            ScheduledGameMatch(
                id = "match_3",
                title = "Chiptune Retro Marathon & Boss Rush",
                gameType = "ARCADE_SPECIAL",
                scheduledTime = "Saturday 14:00",
                participantsCount = 250,
                prizePool = "5,000 Credits",
                isLiveNow = false,
                casterName = "DJ Sora & Townsquare Guests",
                badge = "WEEKEND EVENT"
            )
        )
    }

    var savedBroadcasts by remember {
        mutableStateOf(
            listOf(
                SavedDubbedBroadcast(
                    id = "rec_1",
                    matchTitle = "Space Invaders Quarter-Finals: Downtown vs West Bay",
                    durationText = "14:22",
                    recordedDate = "2026-10-02",
                    finalScore = "1,480 - 1,220 pts",
                    dubTrackTitle = "Dubbed by Leo: 'Epic Shield Defense Commentary'",
                    likesCount = 42
                ),
                SavedDubbedBroadcast(
                    id = "rec_2",
                    matchTitle = "Block Breaker Sudden Death: Red Bricks Cleared",
                    durationText = "08:45",
                    recordedDate = "2026-09-29",
                    finalScore = "3,110 pts (Record)",
                    dubTrackTitle = "Caster Voiceover & Vintage Chiptune FX",
                    likesCount = 67
                )
            )
        )
    }

    // Dubbing timer
    LaunchedEffect(isDubbingActive) {
        while (isDubbingActive) {
            delay(1000)
            dubbingDurationSec++
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("arcade_scheduled_games_section"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Header
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFF180E29),
            border = BorderStroke(1.5.dp, Color(0xFFC084FC)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFA855F7).copy(alpha = 0.25f),
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🎮", fontSize = 24.sp)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Scheduled Interactive Arcade & Dubbing", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black), color = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF22C55E)) {
                            Text("LIVE ON-DEMAND", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color.Black, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                        }
                    }
                    Text("Participate in scheduled matches, dub gameplay live with caster commentary, and watch recorded broadcasts on-demand.", style = MaterialTheme.typography.bodySmall, color = DarkTextSecondary)
                }
            }
        }

        if (saveSuccessMessage != null) {
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
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(saveSuccessMessage!!, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    IconButton(onClick = { saveSuccessMessage = null }, modifier = Modifier.size(20.dp)) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // Upcoming Scheduled Matches List
        Text("Scheduled Matches & Live Tournaments:", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = DarkTextSecondary)

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            scheduledMatches.forEach { match ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = DarkSurface,
                    border = BorderStroke(
                        if (match.isLiveNow) 1.5.dp else 1.dp,
                        if (match.isLiveNow) CoralRed else DarkBorder
                    ),
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
                                    text = match.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (match.isLiveNow) CoralRed else Color(0xFF3B82F6)
                                ) {
                                    Text(
                                        text = match.scheduledTime,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = "🏆 ${match.prizePool}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = WarmAmber
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Caster: ${match.casterName} • ${match.participantsCount} Citizens Registered", fontSize = 11.sp, color = DarkTextSecondary)

                        Spacer(modifier = Modifier.height(10.dp))

                        // Join / Dub Match Button
                        Button(
                            onClick = {
                                activeMatchSession = match
                                isDubbingActive = false
                                dubbingDurationSec = 0
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (match.isLiveNow) CoralRed else Color(0xFF4F46E5),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (match.isLiveNow) "Join Live Gameplay & Open Dubbing Studio" else "Open Practice Session & Pre-Dub",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // SAVED ON-DEMAND BROADCASTS VAULT
        Text("Saved On-Demand Broadcasts (Watch Anytime):", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = DarkTextSecondary)

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            savedBroadcasts.forEach { rec ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = DarkSurface,
                    border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(rec.matchTitle, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.White, maxLines = 1)
                            }
                            Text(rec.durationText, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = NeonCyan)
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(rec.dubTrackTitle, fontSize = 11.sp, color = WarmAmber)
                        Text("Recorded on ${rec.recordedDate} • Final: ${rec.finalScore} • ❤️ ${rec.likesCount}", fontSize = 10.sp, color = DarkTextMuted)

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                watchingOnDemandBroadcast = rec
                                onDemandProgress = 0.1f
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7), contentColor = Color.White)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Watch On-Demand with Commentary", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Interactive Live Gameplay & Dubbing Studio Modal
    if (activeMatchSession != null) {
        val sess = activeMatchSession!!
        AlertDialog(
            onDismissRequest = { activeMatchSession = null },
            containerColor = Color(0xFF0F172A),
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🎙️ Live Dubbing Studio", color = Color.White, fontWeight = FontWeight.Black)
                    if (isDubbingActive) {
                        Surface(shape = RoundedCornerShape(4.dp), color = CoralRed) {
                            Text("REC ${dubbingDurationSec}s", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.White, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                        }
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Match: ${sess.title}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonCyan)

                    // Simulated live gameplay canvas preview
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF050B14),
                        border = BorderStroke(1.dp, Color(0xFF1E293B)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("👾 LIVE GAMEPLAY TELEMETRY 👾", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF22C55E), fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Score: 840 pts • Wave 4 • Lives: 3", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color.White)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(if (isDubbingActive) "🔴 Microphone active: Dubbing voice track..." else "Dubbing paused. Tap Record below.", fontSize = 10.sp, color = DarkTextSecondary)
                            }
                        }
                    }

                    // Dubbing mic control
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(if (isDubbingActive) Icons.Default.Mic else Icons.Default.MicOff, contentDescription = null, tint = if (isDubbingActive) CoralRed else Color.Gray)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Live Dub Caster Mic", fontSize = 12.sp, color = Color.White)
                        }

                        Switch(
                            checked = isDubbingActive,
                            onCheckedChange = { isDubbingActive = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = CoralRed)
                        )
                    }

                    // Caster Voice Sound FX Soundboard
                    Text("Caster Soundboard FX (Plays on Air):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkTextSecondary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("👏 Cheers", "🪙 Coin", "🎺 Fanfare", "🚨 Buzzer").forEach { fx ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1E293B),
                                border = BorderStroke(1.dp, Color(0xFF334155)),
                                modifier = Modifier
                                    .clickable { soundFxPlayed = fx }
                                    .padding(vertical = 2.dp)
                            ) {
                                Text(fx, fontSize = 10.sp, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp))
                            }
                        }
                    }
                    if (soundFxPlayed != null) {
                        Text("▶ Played sound effect: $soundFxPlayed", fontSize = 10.sp, color = WarmAmber)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newRecord = SavedDubbedBroadcast(
                            id = "rec_${System.currentTimeMillis()}",
                            matchTitle = sess.title,
                            durationText = "${dubbingDurationSec / 60}:${String.format("%02d", dubbingDurationSec % 60)}",
                            recordedDate = "Today",
                            finalScore = "980 pts",
                            dubTrackTitle = "Dubbed by You (Live Caster Track)"
                        )
                        savedBroadcasts = listOf(newRecord) + savedBroadcasts
                        saveSuccessMessage = "Broadcast saved to On-Demand Vault with dubbed commentary!"
                        activeMatchSession = null
                        isDubbingActive = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E), contentColor = Color.Black)
                ) {
                    Text("Save & Publish Broadcast", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { activeMatchSession = null }) {
                    Text("Close", color = Color.Gray)
                }
            }
        )
    }

    // On-Demand Broadcast Player Modal
    if (watchingOnDemandBroadcast != null) {
        val vod = watchingOnDemandBroadcast!!
        AlertDialog(
            onDismissRequest = { watchingOnDemandBroadcast = null },
            containerColor = Color(0xFF0F172A),
            title = {
                Text(vod.matchTitle, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.PlayCircle, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(40.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("PLAYING ON-DEMAND BROADCAST", fontSize = 10.sp, color = Color.White, fontFamily = FontFamily.Monospace)
                                Text("Dub Track: ${vod.dubTrackTitle}", fontSize = 10.sp, color = WarmAmber)
                            }
                        }
                    }

                    // Scrubber
                    LinearProgressIndicator(
                        progress = { onDemandProgress },
                        modifier = Modifier.fillMaxWidth().height(6.dp),
                        color = NeonCyan,
                        trackColor = Color(0xFF1E293B)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("03:15", fontSize = 10.sp, color = DarkTextSecondary)
                        Text(vod.durationText, fontSize = 10.sp, color = DarkTextSecondary)
                    }

                    Text("Final Match Score: ${vod.finalScore}", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                TextButton(onClick = { watchingOnDemandBroadcast = null }) {
                    Text("Done", color = NeonCyan)
                }
            }
        )
    }
}
