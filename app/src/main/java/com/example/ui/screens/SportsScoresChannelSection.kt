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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

enum class SportType(val label: String, val emoji: String) {
    ALL("All Sports", "🏅"),
    FOOTBALL("Football / Soccer", "⚽"),
    BASKETBALL("Basketball", "🏀"),
    TENNIS("Tennis", "🎾"),
    BASEBALL("Baseball", "⚾"),
    MOTORSPORT("Motorsport", "🏎️")
}

data class MatchEvent(
    val minute: String,
    val description: String,
    val type: String, // "GOAL", "CARD", "SUB", "POINT", "PITSTOP"
    val teamName: String
)

data class LiveSportMatch(
    val id: String,
    val sportType: SportType,
    val league: String,
    val homeTeam: String,
    val homeEmoji: String,
    val homeScore: Int,
    val awayTeam: String,
    val awayEmoji: String,
    val awayScore: Int,
    val matchStatus: String, // "LIVE 78'", "LIVE Q4 2:15", "HALFTIME", "FINAL", "TODAY 19:30"
    val isLive: Boolean,
    val venue: String,
    val possessionHomePercent: Int,
    val shotsHome: Int,
    val shotsAway: Int,
    val keyEvents: List<MatchEvent>,
    var isAlertsEnabled: Boolean = false
)

object SportsSeedData {
    fun getInitialMatches(): List<LiveSportMatch> = listOf(
        LiveSportMatch(
            id = "match_fb_1",
            sportType = SportType.FOOTBALL,
            league = "Townsquare Premier Cup",
            homeTeam = "Canal Harbor FC",
            homeEmoji = "⚓",
            homeScore = 2,
            awayTeam = "Metropolis Athletic",
            awayEmoji = "⚡",
            awayScore = 1,
            matchStatus = "LIVE 78'",
            isLive = true,
            venue = "Harbor Basin Stadium (Cap: 28,500)",
            possessionHomePercent = 56,
            shotsHome = 12,
            shotsAway = 7,
            keyEvents = listOf(
                MatchEvent("76'", "GOAL! Tanaka curls a stunning 22-yard strike into top corner!", "GOAL", "Canal Harbor FC"),
                MatchEvent("64'", "Yellow Card issued to Vance for sliding foul.", "CARD", "Metropolis Athletic"),
                MatchEvent("38'", "GOAL! Equalizer by Rodriguez on a rebound header.", "GOAL", "Metropolis Athletic"),
                MatchEvent("14'", "GOAL! Early opener from penalty spot by Captain Chen.", "GOAL", "Canal Harbor FC")
            ),
            isAlertsEnabled = true
        ),
        LiveSportMatch(
            id = "match_bb_1",
            sportType = SportType.BASKETBALL,
            league = "Civic Basketball League",
            homeTeam = "West District Vipers",
            homeEmoji = "🐍",
            homeScore = 104,
            awayTeam = "North Valley Hawks",
            awayEmoji = "🦅",
            awayScore = 99,
            matchStatus = "LIVE Q4 0:48",
            isLive = true,
            venue = "Civic Arena Center Court",
            possessionHomePercent = 51,
            shotsHome = 38,
            shotsAway = 36,
            keyEvents = listOf(
                MatchEvent("Q4 0:52", "3-POINTER! Marcus Chen hits clutch corner step-back!", "POINT", "West District Vipers"),
                MatchEvent("Q4 1:20", "Fastbreak slam dunk by Jackson!", "POINT", "North Valley Hawks"),
                MatchEvent("Q3 4:10", "Technical foul assessed to bench.", "CARD", "North Valley Hawks")
            )
        ),
        LiveSportMatch(
            id = "match_tn_1",
            sportType = SportType.TENNIS,
            league = "Grand Bay Tennis Masters",
            homeTeam = "Aoi Takahashi (JPN)",
            homeEmoji = "🇯🇵",
            homeScore = 2,
            awayTeam = "Matteo Rossi (ITA)",
            awayEmoji = "🇮🇹",
            awayScore = 1,
            matchStatus = "SET 3 • TIEBREAK (6-6)",
            isLive = true,
            venue = "Seaside Clay Courts - Court Central",
            possessionHomePercent = 50,
            shotsHome = 34,
            shotsAway = 31,
            keyEvents = listOf(
                MatchEvent("Set 3", "Ace! 198 km/h down the T-line.", "POINT", "Aoi Takahashi"),
                MatchEvent("Set 2", "Rossi breaks back with a forehand winner down the line.", "POINT", "Matteo Rossi")
            )
        ),
        LiveSportMatch(
            id = "match_ms_1",
            sportType = SportType.MOTORSPORT,
            league = "Townsquare Grand Prix",
            homeTeam = "Scuderia Canal (Verstappen/Lin)",
            homeEmoji = "🏎️",
            homeScore = 1,
            awayTeam = "Silver Arrow Racing",
            awayEmoji = "🏁",
            awayScore = 2,
            matchStatus = "LAP 46/58 • SAFETY CAR",
            isLive = true,
            venue = "Townsquare Waterfront Street Circuit",
            possessionHomePercent = 50,
            shotsHome = 1,
            shotsAway = 2,
            keyEvents = listOf(
                MatchEvent("Lap 44", "Pitstop: Under-cut pitstop in 2.1s!", "PITSTOP", "Scuderia Canal"),
                MatchEvent("Lap 39", "Full Course Yellow: Debris cleared on Turn 7 chicane.", "CARD", "Race Control")
            )
        ),
        LiveSportMatch(
            id = "match_bs_1",
            sportType = SportType.BASEBALL,
            league = "Metro Diamond Classic",
            homeTeam = "Canal Mariners",
            homeEmoji = "⚓",
            homeScore = 6,
            awayTeam = "Valley Tigers",
            awayEmoji = "🐅",
            awayScore = 4,
            matchStatus = "FINAL",
            isLive = false,
            venue = "Veterans Memorial Ballpark",
            possessionHomePercent = 54,
            shotsHome = 9,
            shotsAway = 6,
            keyEvents = listOf(
                MatchEvent("Bottom 8th", "2-Run Homer over left field wall by Delgado!", "GOAL", "Canal Mariners")
            )
        )
    )
}

@Composable
fun SportsScoresChannelSection(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
    var matches by remember { mutableStateOf(SportsSeedData.getInitialMatches()) }
    var selectedSport by remember { mutableStateOf(SportType.ALL) }
    var expandedMatchId by remember { mutableStateOf<String?>("match_fb_1") }
    var isSimulatingScore by remember { mutableStateOf(false) }

    val filteredMatches = remember(matches, selectedSport) {
        if (selectedSport == SportType.ALL) matches
        else matches.filter { it.sportType == selectedSport }
    }

    // Live score simulation ticker
    LaunchedEffect(isSimulatingScore) {
        if (isSimulatingScore) {
            delay(1500)
            matches = matches.map { m ->
                if (m.isLive && m.sportType == SportType.FOOTBALL) {
                    val newEvent = MatchEvent("82'", "SHOT ON GOAL! Off the crossbar!", "POINT", m.homeTeam)
                    m.copy(shotsHome = m.shotsHome + 1, keyEvents = listOf(newEvent) + m.keyEvents)
                } else m
            }
            isSimulatingScore = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .testTag("sports_channel_section")
    ) {
        // Channel Top Header Banner
        Surface(
            color = Color(0xFF1F140E),
            border = BorderStroke(1.dp, WarmAmber.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🏆", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("STADIUM CENTRAL", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = CoralRed
                                ) {
                                    Text(
                                        text = "LIVE",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text("Real-Time Scores, Pitch Telemetry & Instant Match Commentary", fontSize = 11.sp, color = DarkTextSecondary)
                        }
                    }

                    IconButton(
                        onClick = { isSimulatingScore = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh scores",
                            tint = if (isSimulatingScore) NeonCyan else Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Sport Filter Pills
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(SportType.entries) { sport ->
                        val isSelected = selectedSport == sport
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) WarmAmber else Color(0xFF2C1E14),
                            border = BorderStroke(1.dp, if (isSelected) WarmAmber else Color(0xFF4A3222)),
                            modifier = Modifier.clickable { selectedSport = sport }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(sport.emoji, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = sport.label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Matches List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredMatches, key = { it.id }) { match ->
                val isExpanded = expandedMatchId == match.id
                LiveMatchCard(
                    match = match,
                    isExpanded = isExpanded,
                    onToggleExpand = {
                        expandedMatchId = if (isExpanded) null else match.id
                    },
                    onToggleAlerts = {
                        matches = matches.map { if (it.id == match.id) it.copy(isAlertsEnabled = !it.isAlertsEnabled) else it }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
fun LiveMatchCard(
    match: LiveSportMatch,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onToggleAlerts: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF141923),
        border = BorderStroke(1.dp, if (match.isLive) WarmAmber.copy(alpha = 0.5f) else DarkBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleExpand() }
            .testTag("live_match_card_${match.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: League & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(match.sportType.emoji, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = match.league.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        letterSpacing = 0.8.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (match.isLive) CoralRed.copy(alpha = 0.15f) else Color(0xFF1E293B),
                    border = BorderStroke(1.dp, if (match.isLive) CoralRed else Color.DarkGray)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (match.isLive) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(CoralRed)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = match.matchStatus,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = if (match.isLive) CoralRed else Color.LightGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Scoreboard Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Home Team
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(match.homeEmoji, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = match.homeTeam,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }

                // Scores Box
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, DarkBorder)
                ) {
                    Text(
                        text = "${match.homeScore}  :  ${match.awayScore}",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = if (match.isLive) WarmAmber else Color.White,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                // Away Team
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = match.awayTeam,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = Color.White,
                        textAlign = TextAlign.End
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(match.awayEmoji, fontSize = 24.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "📍 ${match.venue}",
                fontSize = 10.sp,
                color = DarkTextSecondary
            )

            // Expanded Match Telemetry & Play-by-play
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(color = DarkBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Match Stats (Possession bar, Shots)
                    Text("MATCH STATISTICS", fontSize = 10.sp, fontWeight = FontWeight.Black, color = NeonCyan)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${match.possessionHomePercent}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Possession", fontSize = 10.sp, color = DarkTextSecondary)
                        Text("${100 - match.possessionHomePercent}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    LinearProgressIndicator(
                        progress = { match.possessionHomePercent / 100f },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = WarmAmber,
                        trackColor = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Shots on Target: ${match.shotsHome}", fontSize = 10.sp, color = Color.LightGray)
                        Text("Shots on Target: ${match.shotsAway}", fontSize = 10.sp, color = Color.LightGray)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Play-by-play Key Events Timeline
                    Text("LIVE COMMENTARY & EVENTS", fontSize = 10.sp, fontWeight = FontWeight.Black, color = WarmAmber)
                    Spacer(modifier = Modifier.height(6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        match.keyEvents.forEach { ev ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF0F151F),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = ev.minute,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = WarmAmber
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = when (ev.type) {
                                            "GOAL" -> "⚽"
                                            "CARD" -> "🟨"
                                            "POINT" -> "🎯"
                                            "PITSTOP" -> "⛽"
                                            else -> "⏱️"
                                        },
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = ev.description,
                                        fontSize = 11.sp,
                                        color = Color.White,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Alerts Toggle Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = onToggleAlerts,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(
                                imageVector = if (match.isAlertsEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsNone,
                                contentDescription = null,
                                tint = if (match.isAlertsEnabled) WarmAmber else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (match.isAlertsEnabled) "Alerts Active (Pushing live goals)" else "Enable Match Notifications",
                                fontSize = 11.sp,
                                color = if (match.isAlertsEnabled) WarmAmber else Color.LightGray
                            )
                        }

                        Text("Tap to collapse ▲", fontSize = 10.sp, color = DarkTextSecondary)
                    }
                }
            }
        }
    }
}
