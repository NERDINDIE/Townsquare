package com.example.ui.plus.arcade.games

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

enum class ExtraGamePhase {
    NEWSROOM_DESK, // Gathering breaking leads & beating rivals
    HEADLINE_CRAFTING, // Crafting the front-page edition
    PRESS_RUNNING, // Printing press animation
    EDITION_RESULTS // Circulation sales & citizen reactions
}

data class BreakingNewsLead(
    val id: String,
    val source: String, // "POLICE WIRE", "HARBOR DISPATCH", "CIVIC HALL", "STREET TIP"
    val topic: String,
    val rawTip: String,
    val verifiedTruth: String,
    val rivalSpeedSec: Int, // seconds before rival publishes
    val potentialReaders: Int,
    val leadPhotoEmoji: String,
    var status: LeadStatus = LeadStatus.UNASSIGNED,
    var timeRemainingSec: Int = rivalSpeedSec,
    var hasPhoto: Boolean = false,
    var isFactChecked: Boolean = false
)

enum class LeadStatus {
    UNASSIGNED, INVESTIGATING, READY_TO_PRINT, SCOOPED_BY_RIVAL, PUBLISHED
}

data class HeadlineOption(
    val text: String,
    val style: String, // "FACTUAL_PUNCHY", "CLICKBAIT_RUMOR", "DULL_BUREAUCRATIC"
    val readerMultiplier: Float,
    val trustChange: Int,
    val verdictComment: String
)

data class NewspaperEditionStory(
    val storyId: String,
    val title: String,
    val headlineOptions: List<HeadlineOption>,
    val verifiedFacts: String,
    val photoEmoji: String
)

object ExtraNewspaperSeeds {
    fun getInitialLeads(): List<BreakingNewsLead> = listOf(
        BreakingNewsLead(
            id = "lead_tramway",
            source = "HARBOR DISPATCH",
            topic = "Canal Bridge Power Grid",
            rawTip = "Sparks exploding along the Canal Tramway! Rumors say a rogue lightning bolt fried the whole transit substation.",
            verifiedTruth = "Saltwater corrosion degraded transformer coils at Pier 4; electric crews safely rerouted power with zero injuries.",
            rivalSpeedSec = 22,
            potentialReaders = 12500,
            leadPhotoEmoji = "🚊"
        ),
        BreakingNewsLead(
            id = "lead_soba",
            source = "STREET TIP",
            topic = "Yanaka Culinary Legend",
            rawTip = "Chef Tanaka from Yanaka Soba spotted carrying a locked titanium briefcase into the old noodle flour mill!",
            verifiedTruth = "Tanaka is donating his family's 90-year-old sourdough buckwheat yeast culture to the civic food archive.",
            rivalSpeedSec = 28,
            potentialReaders = 8400,
            leadPhotoEmoji = "🍜"
        ),
        BreakingNewsLead(
            id = "lead_skyscraper",
            source = "CIVIC HALL",
            topic = "Midnight Council Vote",
            rawTip = "City Planning Committee holding a closed-door midnight hearing on rezoning the historic cobblestone plaza for a 50-story neon spire.",
            verifiedTruth = "Council voted 6-1 against the tower, preserving the historic preservation zone and funding a cobblestone pedestrian park.",
            rivalSpeedSec = 25,
            potentialReaders = 16800,
            leadPhotoEmoji = "🏛️"
        ),
        BreakingNewsLead(
            id = "lead_meteor",
            source = "OBSERVATORY WIRE",
            topic = "Unexplained Night Lights",
            rawTip = "Bright turquoise fireball streaking across the bay! Citizens claim an alien satellite fell into the water.",
            verifiedTruth = "Rare Perseid iron-nickel meteorite burned up harmlessly at 80km altitude, creating a luminescent green plasma trail.",
            rivalSpeedSec = 30,
            potentialReaders = 21000,
            leadPhotoEmoji = "🌠"
        ),
        BreakingNewsLead(
            id = "lead_museum",
            source = "POLICE WIRE",
            topic = "Print Museum Burglary",
            rawTip = "Alarms wailing at the Museum of Typography! Master Gutenberg lead type drawer missing from display case.",
            verifiedTruth = "Curator accidentally misplaced the case in the archive vault for cleaning; no theft occurred.",
            rivalSpeedSec = 20,
            potentialReaders = 9200,
            leadPhotoEmoji = "📰"
        )
    )

    fun getEditionStories(): List<NewspaperEditionStory> = listOf(
        NewspaperEditionStory(
            storyId = "lead_skyscraper",
            title = "The Midnight Council Preservation Battle",
            photoEmoji = "🏛️",
            verifiedFacts = "City council overwhelmingly rejected the mega-tower development, voting 6-1 to protect the historic cobblestone district.",
            headlineOptions = listOf(
                HeadlineOption(
                    text = "HISTORIC PLAZA SAVED! COUNCIL REJECTS 50-STORY SPIRE IN 6-1 LANDMARK VOTE",
                    style = "FACTUAL_PUNCHY",
                    readerMultiplier = 1.45f,
                    trustChange = +15,
                    verdictComment = "Sensational, accurate journalism! The edition flew off newsstands across the downtown core."
                ),
                HeadlineOption(
                    text = "SECRET DEALS EXPOSED! MAYOR FLEES IN TERROR AS CITIZENS MOB CITY HALL OVER TOWER!",
                    style = "CLICKBAIT_RUMOR",
                    readerMultiplier = 1.6f,
                    trustChange = -25,
                    verdictComment = "Raked in short-term tabloid sales, but readers were outraged by the misleading conspiracy headlines."
                ),
                HeadlineOption(
                    text = "MUNICIPAL PLANNING SUB-COMMITTEE RESOLUTION 402 SECTION B PASSED",
                    style = "DULL_BUREAUCRATIC",
                    readerMultiplier = 0.5f,
                    trustChange = 0,
                    verdictComment = "Completely unreadable headline! Commuters walked right past the newsstand without a second glance."
                )
            )
        ),
        NewspaperEditionStory(
            storyId = "lead_meteor",
            title = "The Turquoise Night Sky Fireball",
            photoEmoji = "🌠",
            verifiedFacts = "Perseid iron meteor illuminated the harbor with a natural green aurora plasma trail.",
            headlineOptions = listOf(
                HeadlineOption(
                    text = "EMERALD FIREBALL DAZZLES BAY: RARE METEOR STREAKS ACROSS HARBOR SKY",
                    style = "FACTUAL_PUNCHY",
                    readerMultiplier = 1.5f,
                    trustChange = +18,
                    verdictComment = "Spectacular front page! Science societies and citizens kept souvenir copies."
                ),
                HeadlineOption(
                    text = "UFO CRASH IN THE HARBOR! MILITARY COVER-UP IN PROGRESS AS WE SPEAK!",
                    style = "CLICKBAIT_RUMOR",
                    readerMultiplier = 1.7f,
                    trustChange = -30,
                    verdictComment = "Tabloid frenzy! Panic in the fish market followed by widespread reader distrust."
                ),
                HeadlineOption(
                    text = "ASTRONOMICAL METEORIC EVENT OBSERVED OVER LOCAL COASTAL MERIDIAN",
                    style = "DULL_BUREAUCRATIC",
                    readerMultiplier = 0.6f,
                    trustChange = +2,
                    verdictComment = "Dry academic phrasing. Failed to convey the wonder of the nighttime event."
                )
            )
        )
    )
}

@Composable
fun ExtraNewspaperCabinet(
    onGameOver: (Int) -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    var gamePhase by remember { mutableStateOf(ExtraGamePhase.NEWSROOM_DESK) }
    var leads by remember { mutableStateOf(ExtraNewspaperSeeds.getInitialLeads()) }
    var currentEditionNum by remember { mutableIntStateOf(1) }
    var totalCirculation by remember { mutableIntStateOf(14500) }
    var trustRating by remember { mutableIntStateOf(85) } // 0 - 100%
    var newsroomFunds by remember { mutableIntStateOf(2400) } // Cash for upgrades
    var score by remember { mutableIntStateOf(0) }

    // Selected lead for Headline Crafting
    var selectedEditionStory by remember { mutableStateOf(ExtraNewspaperSeeds.getEditionStories().first()) }
    var selectedHeadlineOption by remember { mutableStateOf<HeadlineOption?>(null) }
    var subheadNote by remember { mutableStateOf("By Our Senior Investigative Desk • Special Morning Dispatch") }

    // Press printing countdown animation
    var pressProgress by remember { mutableFloatStateOf(0f) }

    // Upgrades
    var hasRotaryPressUpgrade by remember { mutableStateOf(false) }
    var hasMotorbikeFleetUpgrade by remember { mutableStateOf(false) }
    var hasTeletypeUpgrade by remember { mutableStateOf(false) }

    // Rivals progress ticker
    LaunchedEffect(gamePhase) {
        if (gamePhase == ExtraGamePhase.NEWSROOM_DESK) {
            while (gamePhase == ExtraGamePhase.NEWSROOM_DESK) {
                delay(1000)
                leads = leads.map { lead ->
                    if (lead.status == LeadStatus.UNASSIGNED || lead.status == LeadStatus.INVESTIGATING) {
                        val newTime = lead.timeRemainingSec - 1
                        if (newTime <= 0 && lead.status == LeadStatus.UNASSIGNED) {
                            lead.copy(timeRemainingSec = 0, status = LeadStatus.SCOOPED_BY_RIVAL)
                        } else if (newTime <= 0 && lead.status == LeadStatus.INVESTIGATING) {
                            // Finished fact check!
                            lead.copy(timeRemainingSec = 0, status = LeadStatus.READY_TO_PRINT, isFactChecked = true)
                        } else {
                            lead.copy(timeRemainingSec = newTime)
                        }
                    } else lead
                }
            }
        } else if (gamePhase == ExtraGamePhase.PRESS_RUNNING) {
            pressProgress = 0f
            while (pressProgress < 1f) {
                delay(120)
                pressProgress += if (hasRotaryPressUpgrade) 0.12f else 0.07f
            }
            gamePhase = ExtraGamePhase.EDITION_RESULTS
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF111418))
            .testTag("extra_newspaper_game_cabinet")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Surface(
                color = Color(0xFF1E242B),
                border = BorderStroke(1.dp, WarmAmber.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onExit) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Exit", tint = WarmAmber)
                        }
                        Column {
                            Text(
                                text = "EXTRA! THE NEWSPAPER RUSH",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = WarmAmber
                            )
                            Text(
                                text = "Townsquare Daily Chronicle • Edition #$currentEditionNum",
                                fontSize = 10.sp,
                                color = Color.LightGray
                            )
                        }
                    }

                    // Newspaper Stats (Circulation, Trust, Funds)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF2B2010),
                            border = BorderStroke(1.dp, WarmAmber)
                        ) {
                            Text(
                                text = "📰 $totalCirculation READERS",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = WarmAmber,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (trustRating >= 70) Color(0xFF183318) else Color(0xFF3B1818),
                            border = BorderStroke(1.dp, if (trustRating >= 70) Color(0xFF4CAF50) else CoralRed)
                        ) {
                            Text(
                                text = "⭐ $trustRating% TRUST",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = if (trustRating >= 70) Color(0xFF4CAF50) else CoralRed,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // PHASE SWITCHER
            when (gamePhase) {
                ExtraGamePhase.NEWSROOM_DESK -> {
                    // 1. LIVE BREAKING WIRE & REPORTING RACE
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Newsroom Teletype Banner
                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF1A2129),
                                border = BorderStroke(1.dp, WarmAmber.copy(alpha = 0.4f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("📠", fontSize = 24.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("TELETYPE NEWS DESK: BEAT RIVAL DEADLINES", fontWeight = FontWeight.Black, fontSize = 13.sp, color = Color.White)
                                            Text("The Daily Echo & Tabloid Express are hunting these leads. Dispatch reporters before scoops expire!", fontSize = 11.sp, color = Color.LightGray)
                                        }
                                        Button(
                                            onClick = { gamePhase = ExtraGamePhase.HEADLINE_CRAFTING },
                                            colors = ButtonDefaults.buttonColors(containerColor = WarmAmber, contentColor = Color.Black),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                        ) {
                                            Text("Assemble Edition ➔", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    // Upgrades row
                                    Spacer(modifier = Modifier.height(10.dp))
                                    HorizontalDivider(color = Color.DarkGray)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        UpgradePill(
                                            title = "🛵 Motorbikes",
                                            isActive = hasMotorbikeFleetUpgrade,
                                            onBuy = {
                                                if (newsroomFunds >= 800) {
                                                    newsroomFunds -= 800
                                                    hasMotorbikeFleetUpgrade = true
                                                }
                                            }
                                        )
                                        UpgradePill(
                                            title = "⚙️ Rotary Press",
                                            isActive = hasRotaryPressUpgrade,
                                            onBuy = {
                                                if (newsroomFunds >= 1200) {
                                                    newsroomFunds -= 1200
                                                    hasRotaryPressUpgrade = true
                                                }
                                            }
                                        )
                                        UpgradePill(
                                            title = "⚡ Wire Teletype",
                                            isActive = hasTeletypeUpgrade,
                                            onBuy = {
                                                if (newsroomFunds >= 1000) {
                                                    newsroomFunds -= 1000
                                                    hasTeletypeUpgrade = true
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Text(
                                text = "INCOMING BREAKING NEWS LEADS (${leads.size})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = WarmAmber,
                                letterSpacing = 1.sp
                            )
                        }

                        // Leads list
                        items(leads, key = { it.id }) { lead ->
                            BreakingLeadCard(
                                lead = lead,
                                onSendReporter = {
                                    leads = leads.map { l ->
                                        if (l.id == lead.id) {
                                            val investigateSpeed = if (hasMotorbikeFleetUpgrade) 6 else 10
                                            l.copy(status = LeadStatus.INVESTIGATING, timeRemainingSec = investigateSpeed)
                                        } else l
                                    }
                                },
                                onAddPhoto = {
                                    leads = leads.map { l ->
                                        if (l.id == lead.id) l.copy(hasPhoto = true) else l
                                    }
                                },
                                onRushPrint = {
                                    // Rush without fact check: 50% chance of scandalous rumor
                                    leads = leads.map { l ->
                                        if (l.id == lead.id) l.copy(status = LeadStatus.PUBLISHED) else l
                                    }
                                    totalCirculation += (lead.potentialReaders * 0.8f).toInt()
                                    trustRating = (trustRating - 8).coerceAtLeast(10)
                                    score += 150
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(40.dp))
                        }
                    }
                }

                ExtraGamePhase.HEADLINE_CRAFTING -> {
                    // 2. HEADLINE CRAFTING & FRONT PAGE COMPOSER
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("📰 FRONT-PAGE HEADLINE DESK", fontWeight = FontWeight.Black, fontSize = 15.sp, color = WarmAmber)
                                    Text("Select the most impactful & truthful banner headline to win the city's trust!", fontSize = 11.sp, color = Color.LightGray)
                                }
                                OutlinedButton(
                                    onClick = { gamePhase = ExtraGamePhase.NEWSROOM_DESK },
                                    border = BorderStroke(1.dp, Color.Gray),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("Back to Leads", color = Color.White, fontSize = 10.sp)
                                }
                            }
                        }

                        // BroadSheet Preview Mockup
                        item {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF7F4EB), // Vintage newsprint paper color
                                border = BorderStroke(2.dp, Color(0xFF2C2416)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    // Newspaper Masthead
                                    Text(
                                        text = "The Townsquare Daily Chronicle",
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        color = Color(0xFF1A1A1A),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Text(
                                        text = "DAILY MORNING EDITION • WEATHER: CLEAR • PRICE: 25¢ • CIRCULATION LEADER",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 8.sp,
                                        color = Color(0xFF444444),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    HorizontalDivider(color = Color(0xFF1A1A1A), thickness = 2.dp, modifier = Modifier.padding(vertical = 4.dp))

                                    // Lead Headline
                                    Text(
                                        text = selectedHeadlineOption?.text ?: "[ SELECT A BANNER HEADLINE BELOW ]",
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp,
                                        lineHeight = 18.sp,
                                        color = if (selectedHeadlineOption == null) Color.Gray else Color.Black,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Text(
                                            text = selectedEditionStory.photoEmoji,
                                            fontSize = 38.sp,
                                            modifier = Modifier.padding(end = 10.dp)
                                        )
                                        Column {
                                            Text(
                                                text = subheadNote,
                                                fontSize = 10.sp,
                                                fontStyle = FontStyle.Italic,
                                                color = Color(0xFF333333)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = selectedEditionStory.verifiedFacts,
                                                fontSize = 10.sp,
                                                lineHeight = 14.sp,
                                                color = Color(0xFF222222)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Headline Options selector
                        item {
                            Text("CHOOSE YOUR FRONT-PAGE HEADLINE:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WarmAmber)
                        }

                        items(selectedEditionStory.headlineOptions) { option ->
                            val isSelected = selectedHeadlineOption == option

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) WarmAmber.copy(alpha = 0.2f) else Color(0xFF1E252E),
                                border = BorderStroke(1.dp, if (isSelected) WarmAmber else Color.DarkGray),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedHeadlineOption = option }
                                    .testTag("headline_option_${option.style}")
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = when (option.style) {
                                                "FACTUAL_PUNCHY" -> Color(0xFF1E3A20)
                                                "CLICKBAIT_RUMOR" -> Color(0xFF4A2020)
                                                else -> Color(0xFF2A2A2A)
                                            }
                                        ) {
                                            Text(
                                                text = when (option.style) {
                                                    "FACTUAL_PUNCHY" -> "⭐ ACCURATE & PUNCHY"
                                                    "CLICKBAIT_RUMOR" -> "⚠️ SENSATIONAL CLICKBAIT"
                                                    else -> "💤 DULL BUREAUCRATIC"
                                                },
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = when (option.style) {
                                                    "FACTUAL_PUNCHY" -> Color(0xFF4CAF50)
                                                    "CLICKBAIT_RUMOR" -> CoralRed
                                                    else -> Color.LightGray
                                                },
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        Text(
                                            text = "Hype: ${(option.readerMultiplier * 100).toInt()}% • Trust: ${if (option.trustChange >= 0) "+" else ""}${option.trustChange}%",
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = Color.LightGray
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = option.text,
                                        fontFamily = FontFamily.Serif,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        // RUN THE PRESSES BUTTON
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    if (selectedHeadlineOption != null) {
                                        gamePhase = ExtraGamePhase.PRESS_RUNNING
                                    }
                                },
                                enabled = selectedHeadlineOption != null,
                                colors = ButtonDefaults.buttonColors(containerColor = WarmAmber, contentColor = Color.Black),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("run_the_presses_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🖨️", fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("RUN THE PRESSES! (PRINT EDITION #$currentEditionNum)", fontWeight = FontWeight.Black, fontSize = 13.sp)
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(40.dp))
                        }
                    }
                }

                ExtraGamePhase.PRESS_RUNNING -> {
                    // 3. SATISFYING ROTARY PRINTING PRESS ANIMATION
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF161C24),
                            border = BorderStroke(2.dp, WarmAmber),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("📰 ⚙️ 🖨️", fontSize = 48.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "ROTARY CYLINDERS IN FULL MOTION!",
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = WarmAmber
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Printing 30,000 broadsheets • Folding and bundling for newsboys...",
                                    fontSize = 11.sp,
                                    color = Color.LightGray
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                LinearProgressIndicator(
                                    progress = { pressProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp)
                                        .clip(RoundedCornerShape(5.dp)),
                                    color = WarmAmber,
                                    trackColor = Color.Black
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "${(pressProgress * 100).toInt()}% BUNDLED",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                ExtraGamePhase.EDITION_RESULTS -> {
                    // 4. EDITION RESULTS & CITIZEN REACTIONS
                    val headline = selectedHeadlineOption ?: selectedEditionStory.headlineOptions.first()
                    val earnedReaders = (18000 * headline.readerMultiplier).toInt()
                    val editionRevenue = (earnedReaders * 0.12f).toInt()

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFF19232F),
                                border = BorderStroke(2.dp, if (headline.style == "FACTUAL_PUNCHY") Color(0xFF4CAF50) else WarmAmber),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(18.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("🎉 EXTRA! EXTRA! READ ALL ABOUT IT!", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Black, fontSize = 16.sp, color = WarmAmber)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Edition #$currentEditionNum Results Summary", fontSize = 11.sp, color = Color.LightGray)

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        ResultStatItem(label = "Copies Sold", value = "$earnedReaders", emoji = "🗞️")
                                        ResultStatItem(label = "Press Revenue", value = "$$editionRevenue", emoji = "💵")
                                        ResultStatItem(label = "Trust Change", value = "${if (headline.trustChange >= 0) "+" else ""}${headline.trustChange}%", emoji = "⭐")
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))
                                    HorizontalDivider(color = Color.DarkGray)
                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Editorial Critique
                                    Text(
                                        text = headline.verdictComment,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp,
                                        fontStyle = FontStyle.Italic,
                                        color = Color.White,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        // Citizen morning reactions quotes
                        item {
                            Text("COMMUNITY MORNING REACTIONS:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WarmAmber)
                        }

                        item {
                            CitizenQuoteCard(
                                citizen = "Elena Vance (Architectural Fellow)",
                                quote = if (headline.style == "FACTUAL_PUNCHY")
                                    "\"Finally, a paper that values facts over hysterics! The chronicle is the only sheet I buy at the station.\""
                                else
                                    "\"The headline was screaming nonsense! I bought it thinking aliens landed, only to find out it was just a rock.\"",
                                emoji = "☕"
                            )
                        }

                        item {
                            CitizenQuoteCard(
                                citizen = "Hana Mori (Preservation Historian)",
                                quote = if (headline.style == "FACTUAL_PUNCHY")
                                    "\"Exemplary reporting on the council vote. Thorough and inspiring for our neighborhood!\""
                                else
                                    "\"They printed sensational rumors before checking with the council archive. Disappointing.\"",
                                emoji = "🌸"
                            )
                        }

                        // Next Edition Button
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    totalCirculation += earnedReaders
                                    trustRating = (trustRating + headline.trustChange).coerceIn(10, 100)
                                    newsroomFunds += editionRevenue
                                    score += earnedReaders / 100 + (trustRating * 2)
                                    currentEditionNum += 1
                                    selectedHeadlineOption = null
                                    leads = ExtraNewspaperSeeds.getInitialLeads().shuffled()
                                    val nextStory = ExtraNewspaperSeeds.getEditionStories().find { it.storyId != selectedEditionStory.storyId }
                                        ?: ExtraNewspaperSeeds.getEditionStories().first()
                                    selectedEditionStory = nextStory
                                    gamePhase = ExtraGamePhase.NEWSROOM_DESK
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = WarmAmber, contentColor = Color.Black),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("next_edition_button")
                            ) {
                                Text("BEGIN TOMORROW'S NEWS CYCLE ➔", fontWeight = FontWeight.Black, fontSize = 12.sp)
                            }
                        }

                        item {
                            OutlinedButton(
                                onClick = { onGameOver(score) },
                                border = BorderStroke(1.dp, Color.White),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Retire to Newsroom Archives (Exit)", color = Color.White)
                            }
                            Spacer(modifier = Modifier.height(40.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BreakingLeadCard(
    lead: BreakingNewsLead,
    onSendReporter: () -> Unit,
    onAddPhoto: () -> Unit,
    onRushPrint: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1B222C),
        border = BorderStroke(
            1.dp,
            when (lead.status) {
                LeadStatus.SCOOPED_BY_RIVAL -> CoralRed.copy(alpha = 0.5f)
                LeadStatus.READY_TO_PRINT -> Color(0xFF4CAF50)
                LeadStatus.INVESTIGATING -> NeonCyan
                else -> Color.DarkGray
            }
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Source & Countdown Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF2A3644)
                ) {
                    Text(
                        text = "📡 ${lead.source}",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                if (lead.status == LeadStatus.SCOOPED_BY_RIVAL) {
                    Text("❌ SCOOPED BY THE DAILY ECHO", fontSize = 10.sp, fontWeight = FontWeight.Black, color = CoralRed)
                } else if (lead.status == LeadStatus.READY_TO_PRINT) {
                    Text("✅ 100% FACT CHECKED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                } else {
                    Text(
                        text = "⏱️ Rival Drops Scoop in: ${lead.timeRemainingSec}s",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (lead.timeRemainingSec <= 8) CoralRed else WarmAmber
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.Top) {
                Text(lead.leadPhotoEmoji, fontSize = 28.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(lead.topic, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(
                        text = if (lead.isFactChecked) lead.verifiedTruth else lead.rawTip,
                        fontSize = 11.sp,
                        color = if (lead.isFactChecked) Color(0xFFA5D6A7) else Color.LightGray,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            if (lead.status == LeadStatus.UNASSIGNED) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onSendReporter,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.weight(1f).height(32.dp)
                    ) {
                        Text("🔍 Fact-Check (Investigate)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onRushPrint,
                        border = BorderStroke(1.dp, CoralRed),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("⚡ Rush Print (Risky)", color = CoralRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else if (lead.status == LeadStatus.INVESTIGATING) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = NeonCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Investigative reporter interviewing sources... (${lead.timeRemainingSec}s)", fontSize = 11.sp, color = NeonCyan)
                }
            } else if (lead.status == LeadStatus.READY_TO_PRINT) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Ready for tonight's front page!", fontSize = 11.sp, color = Color(0xFFA5D6A7), fontWeight = FontWeight.Bold)
                    if (!lead.hasPhoto) {
                        TextButton(onClick = onAddPhoto, contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)) {
                            Text("📸 Attach Photo (+30% Readers)", fontSize = 10.sp, color = WarmAmber)
                        }
                    } else {
                        Text("📸 Photo Attached", fontSize = 10.sp, color = WarmAmber)
                    }
                }
            }
        }
    }
}

@Composable
fun UpgradePill(title: String, isActive: Boolean, onBuy: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (isActive) Color(0xFF1B3B20) else Color(0xFF232D38),
        border = BorderStroke(1.dp, if (isActive) Color(0xFF4CAF50) else Color.Gray),
        modifier = Modifier.clickable(enabled = !isActive) { onBuy() }
    ) {
        Text(
            text = if (isActive) "$title [OWNED]" else "$title (Buy)",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = if (isActive) Color(0xFF4CAF50) else Color.White,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun ResultStatItem(label: String, value: String, emoji: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 16.sp)
        Text(value, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color.White)
        Text(label, fontSize = 9.sp, color = Color.LightGray)
    }
}

@Composable
fun CitizenQuoteCard(citizen: String, quote: String, emoji: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF161E28),
        border = BorderStroke(1.dp, Color.DarkGray),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Text(emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(citizen, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WarmAmber)
                Spacer(modifier = Modifier.height(2.dp))
                Text(quote, fontSize = 11.sp, fontStyle = FontStyle.Italic, color = Color.LightGray, lineHeight = 15.sp)
            }
        }
    }
}
