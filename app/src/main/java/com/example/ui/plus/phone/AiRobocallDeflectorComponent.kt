package com.example.ui.plus.phone

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

enum class DeflectionStrategy(val label: String, val emoji: String) {
    RAMBLING_STORY("Endless Recipes & Nostalgia", "🍲"),
    BUREAUCRACY("Form 27B-6 & Infinite Holds", "🗄️"),
    TECH_PARANOIA("Conspiracy & Microwave Debugging", "📡"),
    CLUELESS_BAKER("Sourdough Distractions", "🥖")
}

data class DeflectorPersona(
    val id: String,
    val name: String,
    val tagline: String,
    val avatarEmoji: String,
    val strategy: DeflectionStrategy,
    val avgTimeWastedMins: Int,
    val sampleQuote: String,
    val frustrationRating: Int, // 1 to 5
    val voiceSpeed: Float = 0.85f,
    val isCustom: Boolean = false
)

data class DialogueTurn(
    val speaker: String,
    val text: String,
    val isPersona: Boolean,
    val timestampOffsetSeconds: Int
)

@Composable
fun AiRobocallDeflectorComponent(
    modifier: Modifier = Modifier
) {
    var personas by remember {
        mutableStateOf(
            listOf(
                DeflectorPersona(
                    id = "p-1",
                    name = "Grandpa Murray",
                    tagline = "Hard-of-hearing elderly storyteller with a potato-leek soup obsession",
                    avatarEmoji = "👴",
                    strategy = DeflectionStrategy.RAMBLING_STORY,
                    avgTimeWastedMins = 16,
                    sampleQuote = "Speak up sonny! Now, did you say you're calling about the leeks? Back in 1954 we had to wash the leeks three times...",
                    frustrationRating = 5
                ),
                DeflectorPersona(
                    id = "p-2",
                    name = "Agent Henderson (Auditor)",
                    tagline = "Municipal compliance clerk who demands sub-annex verification forms and leaves callers on elevator holds",
                    avatarEmoji = "📋",
                    strategy = DeflectionStrategy.BUREAUCRACY,
                    avgTimeWastedMins = 22,
                    sampleQuote = "Please remain on line while I retrieve Civic Regulation Subsection 94-D. Playing soothing waiting chimes now...",
                    frustrationRating = 5
                ),
                DeflectorPersona(
                    id = "p-3",
                    name = "Barnaby the Sourdough Baker",
                    tagline = "Constantly drops measuring cups, talks to his parrot, and asks scammers for yeast advice",
                    avatarEmoji = "🥖",
                    strategy = DeflectionStrategy.CLUELESS_BAKER,
                    avgTimeWastedMins = 12,
                    sampleQuote = "Hold on one second, my starter is overflowing! Polly, stop biting the spoon! Now, what did you say about my car warranty?",
                    frustrationRating = 4
                ),
                DeflectorPersona(
                    id = "p-4",
                    name = "Cipher (Paranoid Tinkerer)",
                    tagline = "Convinced the call is a secret test from the electric company and reads back endless binary code",
                    avatarEmoji = "🤖",
                    strategy = DeflectionStrategy.TECH_PARANOIA,
                    avgTimeWastedMins = 18,
                    sampleQuote = "Wait, if you're from the warranty bureau, tell me: why is my toaster emitting 433 MHz pulses? Let me read you the hex dump...",
                    frustrationRating = 5
                )
            )
        )
    }

    var selectedPersonaId by remember { mutableStateOf(personas.first().id) }
    val activePersona = personas.find { it.id == selectedPersonaId } ?: personas.first()

    // Live Deflection Simulator State
    var isSimulationActive by remember { mutableStateOf(false) }
    var simulationSeconds by remember { mutableIntStateOf(0) }
    var frustrationPercent by remember { mutableIntStateOf(15) }
    var simulationTurns by remember { mutableStateOf<List<DialogueTurn>>(emptyList()) }
    var isCallTerminatedByRageQuit by remember { mutableStateOf(false) }

    // Persona Builder Dialog State
    var isCreatePersonaOpen by remember { mutableStateOf(false) }

    // Live simulation conversation runner
    LaunchedEffect(isSimulationActive) {
        if (isSimulationActive) {
            simulationSeconds = 0
            frustrationPercent = 15
            isCallTerminatedByRageQuit = false

            val scriptedTurns = when (activePersona.strategy) {
                DeflectionStrategy.RAMBLING_STORY -> listOf(
                    DialogueTurn("Scammer", "Hello! This is Officer David calling regarding urgent federal tax refund action.", false, 2),
                    DialogueTurn(activePersona.name, "Hello? Sonny? Is that you, Arthur? Did you bring the parsnips for the broth?", true, 6),
                    DialogueTurn("Scammer", "No sir, this is regarding your legal file. You must confirm your social security digits.", false, 11),
                    DialogueTurn(activePersona.name, "Social digits? Ah, you mean my high school bowling score! In 1952 I bowled a 174. Or was it 176? Let me find my glasses...", true, 18),
                    DialogueTurn("Scammer", "Sir! Please listen carefully. This is not bowling. Are you the homeowner?", false, 25),
                    DialogueTurn(activePersona.name, "Of course I have potatoes! But you must simmer them with bay leaves. Three bay leaves, never four. Four ruins the salt balance.", true, 34),
                    DialogueTurn("Scammer", "SIR! Stop talking about soup! I am federal officer! Can you hear me?!", false, 42),
                    DialogueTurn(activePersona.name, "No need to shout dear boy! My ears aren't what they used to be since the steam train depot closed down...", true, 50),
                    DialogueTurn("Scammer", "*Groans in agony* ...I cannot take this anymore! *HANGS UP*", false, 58)
                )
                DeflectionStrategy.BUREAUCRACY -> listOf(
                    DialogueTurn("Scammer", "Good day, I am calling from the Credit Protection Bureau with a pre-approved relief fund.", false, 2),
                    DialogueTurn(activePersona.name, "Thank you for reaching the Municipal Intake Desk. Please state your Operator Dispatch License number and Form 27B-6 compliance token.", true, 7),
                    DialogueTurn("Scammer", "Uh, what? No, I am calling you to lower your credit interest rate today.", false, 14),
                    DialogueTurn(activePersona.name, "Unregistered commercial broadcast detected. Placing you on hold while we cross-reference your telephony routing with Subsection 14-B...", true, 22),
                    DialogueTurn("Scammer", "Wait! Don't put me on hold! Hello?!", false, 28),
                    DialogueTurn(activePersona.name, "[Elevator Jazz Music Playing ♫ ~ ♬ ~ ♫] 'Your call is approximately #84 in line. Average wait time: 47 minutes.'", true, 36),
                    DialogueTurn("Scammer", "Hello?! Are you there?! Hello?!", false, 45),
                    DialogueTurn(activePersona.name, "Auditor Henderson speaking. I see your Form 27B-6 lacks the official embossed copper seal. Would you like to file an appeal in triplicate?", true, 53),
                    DialogueTurn("Scammer", "*Slams receiver in frustration* *CLICK*", false, 60)
                )
                else -> listOf(
                    DialogueTurn("Scammer", "Congratulations! You have won an all-inclusive cruise voucher to the Bahamas!", false, 2),
                    DialogueTurn(activePersona.name, "Oh goodness! Can I bring my sourdough starter on the boat? It needs feeding every four hours at 78 degrees!", true, 7),
                    DialogueTurn("Scammer", "Yes yes, whatever. Just give me your billing postal code to claim the voucher.", false, 14),
                    DialogueTurn(activePersona.name, "Hold on, Polly the parrot just spilled the whole wheat flour on the phone! Polly, bad bird! What did you say about Bahamas flour?", true, 22),
                    DialogueTurn("Scammer", "Ma'am! I don't care about your bird! Just read the numbers on your card!", false, 30),
                    DialogueTurn(activePersona.name, "Well now that's just impolite. Polly was raised in a church choir! I'm putting you on speaker so Polly can sing to you.", true, 40),
                    DialogueTurn("Scammer", "*Unintelligible screaming* *HANGS UP*", false, 48)
                )
            }

            var turnIndex = 0
            while (isActive && isSimulationActive) {
                delay(1000L)
                simulationSeconds++
                frustrationPercent = (frustrationPercent + 2).coerceAtMost(100)

                if (turnIndex < scriptedTurns.size && simulationSeconds >= scriptedTurns[turnIndex].timestampOffsetSeconds) {
                    simulationTurns = simulationTurns + scriptedTurns[turnIndex]
                    if (turnIndex == scriptedTurns.size - 1) {
                        isCallTerminatedByRageQuit = true
                    }
                    turnIndex++
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize().background(DarkBg)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        // AI Deflector Master Status Banner
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1E1408),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, WarmAmber.copy(alpha = 0.7f)),
                modifier = Modifier.fillMaxWidth().testTag("ai_deflector_banner")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = WarmAmber.copy(alpha = 0.2f),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = "🤖", fontSize = 22.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "AI Robocall Time-Waster",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = WarmAmber
                                    ) {
                                        Text(
                                            text = "ACTIVE DEFENSE",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF261800),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Interceptors deployed to exhaust scammer call centers",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = WarmAmber.copy(alpha = 0.85f)
                                )
                            }
                        }

                        Button(
                            onClick = { isCreatePersonaOpen = true },
                            colors = ButtonDefaults.buttonColors(containerColor = WarmAmber, contentColor = Color(0xFF261800)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("create_custom_persona_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Persona", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = DarkBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Hall of Fame Stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DeflectionStatChip(label = "SCAMMERS DEFLECTED", value = "47 Calls", color = Color(0xFF30D158))
                        DeflectionStatChip(label = "TIME WASTED", value = "4h 38m", color = WarmAmber)
                        DeflectionStatChip(label = "RAGE-QUITS", value = "92%", color = CoralRed)
                        DeflectionStatChip(label = "AVG RUNTIME", value = "17m", color = NeonCyan)
                    }
                }
            }
        }

        // Live Simulated Robocall Deflection Demo
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isSimulationActive) Color(0xFF0F1E2E) else DarkSurface,
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (isSimulationActive) NeonCyan else DarkBorder
                ),
                modifier = Modifier.fillMaxWidth().testTag("deflection_live_simulator")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isSimulationActive) Color(0xFF30D158) else DarkTextMuted,
                                    modifier = Modifier.size(8.dp)
                                ) {}
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isSimulationActive) "LIVE DEFLECTION IN PROGRESS" else "DEFLECTION SIMULATOR",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                    color = if (isSimulationActive) NeonCyan else DarkTextSecondary
                                )
                            }
                            Text(
                                text = "Target: Suspicious Auto Warranty Scammer (+1 800-555-0199)",
                                style = MaterialTheme.typography.labelSmall,
                                color = DarkTextMuted
                            )
                        }

                        Button(
                            onClick = {
                                if (isSimulationActive) {
                                    isSimulationActive = false
                                } else {
                                    simulationTurns = emptyList()
                                    isSimulationActive = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSimulationActive) CoralRed else NeonCyan,
                                contentColor = if (isSimulationActive) Color.White else Color(0xFF003544)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("toggle_deflection_sim_btn")
                        ) {
                            Text(if (isSimulationActive) "Stop" else "Deploy Persona", fontWeight = FontWeight.Bold)
                        }
                    }

                    if (isSimulationActive) {
                        Spacer(modifier = Modifier.height(14.dp))

                        // Active Stopwatch & Frustration Meter
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Timer, contentDescription = null, tint = WarmAmber, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Wasted: ${String.format("%02d:%02d", simulationSeconds / 60, simulationSeconds % 60)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = WarmAmber
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Frustration: $frustrationPercent%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (frustrationPercent > 80) CoralRed else NeonCyan
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { frustrationPercent / 100f },
                            color = if (frustrationPercent > 80) CoralRed else NeonCyan,
                            trackColor = DarkBorder,
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp))
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Live Dialogue Speech Bubbles
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            simulationTurns.forEach { turn ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = if (turn.isPersona) Arrangement.End else Arrangement.Start
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (turn.isPersona) Color(0xFF132B3E) else Color(0xFF26191D),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (turn.isPersona) NeonCyan.copy(alpha = 0.5f) else CoralRed.copy(alpha = 0.4f)
                                        ),
                                        modifier = Modifier.fillMaxWidth(0.9f)
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text(
                                                text = turn.speaker,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (turn.isPersona) NeonCyan else CoralRed
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = turn.text,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (isCallTerminatedByRageQuit) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF0F3223),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF30D158)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Celebration, contentDescription = null, tint = Color(0xFF30D158), modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Scammer Rage-Quit Achieved! +50 Deflection XP",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Persona Selection Roster
        item {
            Text(
                text = "ACTIVE DEFLECTOR PERSONAS",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = DarkTextSecondary
            )
        }

        items(personas, key = { it.id }) { persona ->
            val isSelected = persona.id == activePersona.id
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (isSelected) Color(0xFF1E170C) else DarkSurface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) WarmAmber else DarkBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedPersonaId = persona.id }
                    .testTag("persona_card_${persona.id}")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = WarmAmber.copy(alpha = 0.15f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = persona.avatarEmoji, fontSize = 20.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = persona.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    if (isSelected) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = WarmAmber
                                        ) {
                                            Text(
                                                text = "ACTIVE",
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFF261800),
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = persona.strategy.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = WarmAmber
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = NeonCyan.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Avg ${persona.avgTimeWastedMins}m Wasted",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = persona.tagline, style = MaterialTheme.typography.bodySmall, color = DarkTextSecondary)

                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DarkSurfaceElevated,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "\"${persona.sampleQuote}\"",
                            fontSize = 11.sp,
                            color = LightTextSecondary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }

    // Custom Persona Builder Dialog
    if (isCreatePersonaOpen) {
            var newName by remember { mutableStateOf("") }
            var newTagline by remember { mutableStateOf("") }
            var newQuote by remember { mutableStateOf("") }
            var selectedStrategy by remember { mutableStateOf(DeflectionStrategy.RAMBLING_STORY) }

            AlertDialog(
                onDismissRequest = { isCreatePersonaOpen = false },
                title = { Text("Build Custom AI Deflector Persona", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = newName,
                            onValueChange = { newName = it },
                            label = { Text("Persona Name (e.g. Aunt Gertrude)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newTagline,
                            onValueChange = { newTagline = it },
                            label = { Text("Personality Archetype") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newQuote,
                            onValueChange = { newQuote = it },
                            label = { Text("Opening Deflection Phrase") },
                            placeholder = { Text("Who is this? Is this about the cat grooming appointment?") },
                            minLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newName.isNotBlank()) {
                                val created = DeflectorPersona(
                                    id = "p-${System.currentTimeMillis()}",
                                    name = newName,
                                    tagline = newTagline.ifBlank { "Custom community deflection agent" },
                                    avatarEmoji = "🎭",
                                    strategy = selectedStrategy,
                                    avgTimeWastedMins = 15,
                                    sampleQuote = newQuote.ifBlank { "Hold on, let me turn my hearing horn around..." },
                                    frustrationRating = 5,
                                    isCustom = true
                                )
                                personas = personas + created
                                selectedPersonaId = created.id
                                isCreatePersonaOpen = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WarmAmber, contentColor = Color(0xFF261800))
                    ) {
                        Text("Save & Activate", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isCreatePersonaOpen = false }) {
                        Text("Cancel", color = DarkTextSecondary)
                    }
                }
            )
        }
    }
}

@Composable
private fun DeflectionStatChip(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = DarkTextMuted)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Black, color = color)
    }
}
