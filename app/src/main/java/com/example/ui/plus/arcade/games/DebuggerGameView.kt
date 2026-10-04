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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

data class DebuggerQuestion(
    val id: String,
    val title: String,
    val language: String,
    val domain: String,
    val faultyCode: String,
    val faultyLineNum: Int,
    val errorMessage: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val linterHint: String,
    val testFailureOutput: String
)

object DebuggerQuestionsBank {
    val questions = listOf(
        DebuggerQuestion(
            id = "q1",
            title = "The Frozen Citizen Counter",
            language = "KOTLIN / COMPOSE",
            domain = "State Management",
            faultyCode = """
@Composable
fun CitizenCounter() {
    var count = 0 // Line 3
    Button(onClick = { count++ }) {
        Text("Citizens Registered: ${'$'}count")
    }
}
            """.trimIndent(),
            faultyLineNum = 3,
            errorMessage = "StateMutationIgnoredException: UI does not recompose on button click. count resets to 0 on every render.",
            options = listOf(
                "var count by remember { mutableIntStateOf(0) }",
                "val count = remember { 0 }",
                "var count: Int = mutableListOf(0)",
                "@Volatile var count = 0"
            ),
            correctIndex = 0,
            explanation = "Local variables in Composables are re-initialized on every recomposition. Wrapping it with `remember { mutableIntStateOf(0) }` instructs Compose to preserve state and trigger recomposition on writes.",
            linterHint = "Check line 3: Variable mutation is ignored because it is not backed by Compose Snapshot State.",
            testFailureOutput = "ASSERTION FAILED: expected text 'Citizens Registered: 1', actual text 'Citizens Registered: 0' after click."
        ),
        DebuggerQuestion(
            id = "q2",
            title = "Civic Portal SQL Breach",
            language = "KOTLIN / SQL",
            domain = "Database Security",
            faultyCode = """
fun searchRecords(userInput: String): List<Citizen> {
    val query = "SELECT * FROM citizens WHERE name = '" + userInput + "'" // Line 2
    return database.rawQuery(query, null)
}
            """.trimIndent(),
            faultyLineNum = 2,
            errorMessage = "SecurityVulnerabilityWarning: Unsanitized string concatenation detected in raw SQL statement! Arbitrary payload injection risk.",
            options = listOf(
                "val query = \"SELECT * FROM citizens WHERE name = \" + userInput.trim()",
                "database.rawQuery(\"SELECT * FROM citizens WHERE name = ?\", arrayOf(userInput))",
                "val query = userInput.replace(\"'\", \"\\'\")",
                "database.rawQuery(\"SELECT ALL FROM citizens MATCH \" + userInput, null)"
            ),
            correctIndex = 1,
            explanation = "Parameterized queries with `?` placeholders ensure that database engines treat the user input strictly as literal data, preventing malicious code injection.",
            linterHint = "Line 2: SQL query is constructed via raw string concatenation. Use parameterized selection arguments.",
            testFailureOutput = "SECURITY AUDIT FAILED: Input \"' OR 1=1; --\" bypassed access controls and exposed all citizen records."
        ),
        DebuggerQuestion(
            id = "q3",
            title = "The Edition Circulation Overflow",
            language = "KOTLIN",
            domain = "Collections & Iteration",
            faultyCode = """
fun computeTotalCirculation(districtSales: List<Int>): Int {
    var total = 0
    for (i in 0..districtSales.size) { // Line 3
        total += districtSales[i]
    }
    return total
}
            """.trimIndent(),
            faultyLineNum = 3,
            errorMessage = "java.lang.IndexOutOfBoundsException: Index 6 out of bounds for length 6",
            options = listOf(
                "for (i in 1..districtSales.size)",
                "for (i in districtSales.size downTo 0)",
                "for (i in 0 until districtSales.size)",
                "for (i in 0..districtSales.size + 1)"
            ),
            correctIndex = 2,
            explanation = "In Kotlin, `..` represents an inclusive range (0 through size). To iterate zero-indexed collections, use `until` (exclusive of size) or `districtSales.sum()`.",
            linterHint = "Line 3: Range operator `..` includes the upper bound, causing index out of bounds on the final iteration.",
            testFailureOutput = "RUNTIME CRASH: IndexOutOfBoundsException: districtSales.get(6) when size is 6."
        ),
        DebuggerQuestion(
            id = "q4",
            title = "The Vanishing Morning Dispatch",
            language = "KOTLIN / COROUTINES",
            domain = "Concurrency",
            faultyCode = """
suspend fun loadMorningEdition(): EditionData {
    var data: EditionData? = null
    CoroutineScope(Dispatchers.IO).launch {
        data = fetchFromPressNetwork() // Line 4
    }
    return data!! // Line 6
}
            """.trimIndent(),
            faultyLineNum = 6,
            errorMessage = "NullPointerException: Parameter specified as non-null is null: method returned null before background fetch completed.",
            options = listOf(
                "withContext(Dispatchers.IO) { fetchFromPressNetwork() }",
                "runBlocking { delay(200); return data!! }",
                "GlobalScope.launch { data = fetchFromPressNetwork() }",
                "Thread.sleep(1000); return data!!"
            ),
            correctIndex = 0,
            explanation = "`launch` fires an asynchronous coroutine concurrently without waiting. Use `withContext(Dispatchers.IO)` to suspend until the result is computed and return it directly.",
            linterHint = "Line 6: Returning non-null data from an asynchronous launch block without awaiting execution completion.",
            testFailureOutput = "COROUTINE TEST FAILED: loadMorningEdition() threw NullPointerException at line 6 immediately."
        ),
        DebuggerQuestion(
            id = "q5",
            title = "The Shared Packet Leak",
            language = "PYTHON",
            domain = "Language Quirk",
            faultyCode = """
def dispatch_emergency_relay(node_id, packet_queue=[]): # Line 1
    packet_queue.append(node_id)
    return packet_queue
# Second call with dispatch_emergency_relay(202) returns [101, 202]!
            """.trimIndent(),
            faultyLineNum = 1,
            errorMessage = "StateLeakAnomaly: Default mutable list instance is created once at function definition time and shared across all calls.",
            options = listOf(
                "def dispatch_emergency_relay(node_id, packet_queue=list): packet_queue = packet_queue()",
                "def dispatch_emergency_relay(node_id, static packet_queue=[])",
                "def dispatch_emergency_relay(node_id, packet_queue=()): packet_queue = list(packet_queue)",
                "def dispatch_emergency_relay(node_id, packet_queue=None): if packet_queue is None: packet_queue = []"
            ),
            correctIndex = 3,
            explanation = "In Python, default arguments are evaluated once when the function is defined. Using `packet_queue=None` and creating a fresh list inside prevents cross-call mutation.",
            linterHint = "Line 1: Dangerous default value [] (mutable default argument) in function definition.",
            testFailureOutput = "FAIL: test_isolated_calls: expected [202], but got [101, 202] due to shared mutable default."
        ),
        DebuggerQuestion(
            id = "q6",
            title = "The Mystery Zero Identity",
            language = "JAVASCRIPT",
            domain = "Type Coercion",
            faultyCode = """
function verifyCitizenBadge(badgeCode) {
    if (badgeCode == 0) { // Line 2
        return "GUEST_VISITOR";
    }
    return "VERIFIED_RESIDENT";
}
// verifyCitizenBadge("") evaluated to "GUEST_VISITOR" unexpectedly!
            """.trimIndent(),
            faultyLineNum = 2,
            errorMessage = "SemanticTypeError: Empty string '' evaluates to 0 under loose comparison operator ==.",
            options = listOf(
                "if (badgeCode === 0)",
                "if (badgeCode = 0)",
                "if (typeof badgeCode == Number)",
                "if (Boolean(badgeCode) == false)"
            ),
            correctIndex = 0,
            explanation = "JavaScript's `==` coerces types (`\"\" == 0` is true). Strict equality `===` checks both value and type without coercion.",
            linterHint = "Line 2: Expected '===' and instead saw '==' (eqeqeq warning).",
            testFailureOutput = "ASSERT_EQUAL FAILED: verifyCitizenBadge(\"\") expected 'VERIFIED_RESIDENT', got 'GUEST_VISITOR'."
        ),
        DebuggerQuestion(
            id = "q7",
            title = "The Infinite Subway Loop",
            language = "KOTLIN",
            domain = "Graph Algorithms",
            faultyCode = """
fun countSubwayNodes(node: StationNode): Int {
    var count = 1
    for (neighbor in node.connectedStations) {
        count += countSubwayNodes(neighbor) // Line 4
    }
    return count
}
            """.trimIndent(),
            faultyLineNum = 4,
            errorMessage = "StackOverflowError: Circular reference in cyclic graph causes infinite recursive calling.",
            options = listOf(
                "if (node.connectedStations.isEmpty()) return 0",
                "fun count(node: StationNode, visited: MutableSet<String> = mutableSetOf()) and skip if visited.contains(node.id)",
                "for (neighbor in node.connectedStations.take(2))",
                "return node.connectedStations.size * 2"
            ),
            correctIndex = 1,
            explanation = "Subway maps are cyclic graphs with bidirectional tracks. Without passing and checking a `visited` set, the recursion bounces back and forth infinitely.",
            linterHint = "Line 4: Recursive call lacks termination condition or visited-cycle guard in cyclic network graph.",
            testFailureOutput = "FATAL: java.lang.StackOverflowError at line 4 (recursion depth exceeded 10,000 frames)."
        ),
        DebuggerQuestion(
            id = "q8",
            title = "The Radio Loop of Doom",
            language = "KOTLIN / COMPOSE",
            domain = "Side Effects",
            faultyCode = """
@Composable
fun LiveRadioTicker(stationId: String) {
    var volume by remember { mutableFloatStateOf(0.8f) }
    LaunchedEffect(volume) { // Line 4
        volume = (volume * 1.02f).coerceAtMost(1f) // Line 5
        broadcastAudioTelemetry(stationId, volume)
    }
}
            """.trimIndent(),
            faultyLineNum = 4,
            errorMessage = "RecompositionLoopException: LaunchedEffect restarts endlessly because its key (volume) is modified inside its own lambda body.",
            options = listOf(
                "SideEffect { volume += 0.1f }",
                "LaunchedEffect(Unit) { while(true) volume *= 1.02f }",
                "LaunchedEffect(stationId) { broadcastAudioTelemetry(stationId, volume) }",
                "rememberCoroutineScope().launch { volume = 1f }"
            ),
            correctIndex = 2,
            explanation = "A `LaunchedEffect` cancels and restarts whenever any of its keys change. Modifying `volume` inside `LaunchedEffect(volume)` creates an infinite restart loop.",
            linterHint = "Line 4: Key parameter 'volume' is mutated inside LaunchedEffect body, triggering recursive execution.",
            testFailureOutput = "STRESS TEST CRASH: 4,200 recompositions in 500ms. CPU usage at 100%."
        )
    )
}

@Composable
fun DebuggerCabinet(
    onGameOver: (Int) -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val questions = remember { DebuggerQuestionsBank.questions.shuffled() }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(3) }
    var secondsLeft by remember { mutableIntStateOf(30) }
    var isTimerRunning by remember { mutableStateOf(true) }

    // Gameshow Lifelines
    var lifeline5050Used by remember { mutableStateOf(false) }
    var lifelineLinterUsed by remember { mutableStateOf(false) }
    var lifelineTestUsed by remember { mutableStateOf(false) }

    // Active Lifeline modal / state
    var activeLifelineModal by remember { mutableStateOf<String?>(null) } // "LINTER", "TEST"
    var eliminatedOptionIndices by remember { mutableStateOf<Set<Int>>(emptySet()) }

    // Question State
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var isAnswerSubmitted by remember { mutableStateOf(false) }
    var isGameFinished by remember { mutableStateOf(false) }

    val currentQ = questions.getOrNull(currentQuestionIndex) ?: questions.first()

    // Host dialogue lines
    val hostDialogue = remember(currentQuestionIndex, isAnswerSubmitted, selectedOptionIndex) {
        if (!isAnswerSubmitted) {
            when (streak) {
                0 -> "Host Syntax-Bot: \"Welcome contestants to DEBUGGER! Spot the faulty code line before the server locks up!\""
                1 -> "Host Syntax-Bot: \"One bug down! The compiler temperature is stabilizing. Keep up the rhythm!\""
                2 -> "Host Syntax-Bot: \"Double combo! The production logs are looking sparkling clean!\""
                else -> "Host Syntax-Bot: \"UNSTOPPABLE STREAK! The municipal servers are honoring your commit hashes!\""
            }
        } else {
            if (selectedOptionIndex == currentQ.correctIndex) {
                "Host Syntax-Bot: \"🎉 BINGO! Patch compiled and verified with zero compiler warnings!\""
            } else {
                "Host Syntax-Bot: \"🚨 SYNTAX EXPLOSION! That change triggered a runtime fault in staging!\""
            }
        }
    }

    // Timer effect
    LaunchedEffect(currentQuestionIndex, isTimerRunning, isAnswerSubmitted) {
        secondsLeft = 30
        while (isTimerRunning && !isAnswerSubmitted && secondsLeft > 0) {
            delay(1000)
            secondsLeft -= 1
        }
        if (secondsLeft <= 0 && !isAnswerSubmitted) {
            // Time out: counts as wrong answer
            isAnswerSubmitted = true
            selectedOptionIndex = -1
            streak = 0
            lives -= 1
            if (lives <= 0) {
                isGameFinished = true
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070B14))
            .testTag("debugger_game_cabinet")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Arcade Header
            Surface(
                color = Color(0xFF0F1E2E),
                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
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
                            Icon(Icons.Default.ArrowBack, contentDescription = "Exit", tint = NeonCyan)
                        }
                        Text(
                            text = "⚡ DEBUGGER: THE CODE QUIZSHOW",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = NeonCyan
                        )
                    }

                    // Score & Streak
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF1E3A20),
                            border = BorderStroke(1.dp, Color(0xFF33FF33))
                        ) {
                            Text(
                                text = "$score PTS",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF33FF33),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        // Lives
                        Row {
                            repeat(3) { i ->
                                Text(
                                    text = if (i < lives) "❤️" else "🖤",
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            if (isGameFinished || lives <= 0 || currentQuestionIndex >= questions.size) {
                // GAME OVER / VICTORY SCREEN
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF0E1A29),
                        border = BorderStroke(2.dp, if (lives > 0) Color(0xFF33FF33) else CoralRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (lives > 0) "🏆 CODEBASE MERGED!" else "💥 PRODUCTION DOWN!",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                color = if (lives > 0) Color(0xFF33FF33) else CoralRed
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (lives > 0)
                                    "Sensational debugging! You fixed the municipal bugs and passed CI/CD with flying colors."
                                else
                                    "Out of lives! A runtime null-pointer slipped into production.",
                                textAlign = TextAlign.Center,
                                color = Color.LightGray,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.Black.copy(alpha = 0.5f),
                                border = BorderStroke(1.dp, DarkBorder)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("FINAL GAMESHOW SCORE", fontSize = 11.sp, color = DarkTextSecondary)
                                    Text(
                                        text = "$score PTS",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 28.sp,
                                        color = NeonCyan
                                    )
                                    Text(
                                        text = "Highest Streak: $streak",
                                        fontSize = 12.sp,
                                        color = WarmAmber
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Button(
                                    onClick = {
                                        score = 0
                                        lives = 3
                                        streak = 0
                                        currentQuestionIndex = 0
                                        isGameFinished = false
                                        isAnswerSubmitted = false
                                        selectedOptionIndex = null
                                        eliminatedOptionIndices = emptySet()
                                        lifeline5050Used = false
                                        lifelineLinterUsed = false
                                        lifelineTestUsed = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Play Again", fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { onGameOver(score) },
                                    border = BorderStroke(1.dp, Color.White),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Exit Cabinet", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            } else {
                // ACTIVE QUESTION GAMEPLAY
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Host Dialogue Banner
                    item {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF16253B),
                            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🤖", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = hostDialogue,
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    // Question Header & Lifelines
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "ROUND ${currentQuestionIndex + 1}/${questions.size} • ${currentQ.domain.uppercase()}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = NeonCyan
                                )
                                Text(
                                    text = currentQ.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            // Timer countdown pill
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (secondsLeft <= 8) CoralRed.copy(alpha = 0.2f) else WarmAmber.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, if (secondsLeft <= 8) CoralRed else WarmAmber)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = if (secondsLeft <= 8) CoralRed else WarmAmber,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${secondsLeft}s",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp,
                                        color = if (secondsLeft <= 8) CoralRed else WarmAmber
                                    )
                                }
                            }
                        }
                    }

                    // Lifeline Buttons Bar
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // 50/50 Lifeline
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (lifeline5050Used) Color(0xFF1E293B) else Color(0xFF23354C),
                                border = BorderStroke(1.dp, if (lifeline5050Used) Color.DarkGray else NeonCyan),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable(enabled = !lifeline5050Used && !isAnswerSubmitted) {
                                        lifeline5050Used = true
                                        val wrongIndices = currentQ.options.indices.filter { it != currentQ.correctIndex }.shuffled()
                                        eliminatedOptionIndices = wrongIndices.take(2).toSet()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("✂️", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (lifeline5050Used) "50/50 Used" else "50/50 Filter",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (lifeline5050Used) Color.Gray else Color.White
                                    )
                                }
                            }

                            // Ask Linter Lifeline
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (lifelineLinterUsed) Color(0xFF1E293B) else Color(0xFF23354C),
                                border = BorderStroke(1.dp, if (lifelineLinterUsed) Color.DarkGray else WarmAmber),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable(enabled = !lifelineLinterUsed && !isAnswerSubmitted) {
                                        lifelineLinterUsed = true
                                        activeLifelineModal = "LINTER"
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🤖", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (lifelineLinterUsed) "Linter Read" else "Ask Linter",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (lifelineLinterUsed) Color.Gray else Color.White
                                    )
                                }
                            }

                            // Run Test Suite Lifeline
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (lifelineTestUsed) Color(0xFF1E293B) else Color(0xFF23354C),
                                border = BorderStroke(1.dp, if (lifelineTestUsed) Color.DarkGray else Color(0xFF33FF33)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable(enabled = !lifelineTestUsed && !isAnswerSubmitted) {
                                        lifelineTestUsed = true
                                        activeLifelineModal = "TEST"
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🧪", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (lifelineTestUsed) "Tests Run" else "Run Tests",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (lifelineTestUsed) Color.Gray else Color.White
                                    )
                                }
                            }
                        }
                    }

                    // Code Editor Display Box (Retro CRT IDE)
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF03070E),
                            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                // IDE Tab bar
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("🔴 🟡 🟢", fontSize = 10.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "FaultySnippet.${currentQ.language.split(" ").first().lowercase()}",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp,
                                            color = Color.LightGray
                                        )
                                    }
                                    Text(
                                        text = currentQ.language,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = WarmAmber
                                    )
                                }

                                HorizontalDivider(color = DarkBorder)
                                Spacer(modifier = Modifier.height(8.dp))

                                // Code Lines with numbers
                                val lines = currentQ.faultyCode.lines()
                                lines.forEachIndexed { idx, lineText ->
                                    val lineNum = idx + 1
                                    val isFaulty = lineNum == currentQ.faultyLineNum

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(if (isFaulty) CoralRed.copy(alpha = 0.25f) else Color.Transparent)
                                            .padding(vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = String.format("%2d ", lineNum),
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp,
                                            color = if (isFaulty) CoralRed else Color(0xFF4A5568)
                                        )
                                        Text(
                                            text = lineText,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp,
                                            color = if (isFaulty) Color(0xFFFF8888) else Color(0xFF81C784)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Compiler Error Bar
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = CoralRed.copy(alpha = 0.15f),
                                    border = BorderStroke(1.dp, CoralRed.copy(alpha = 0.4f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text("❌", fontSize = 12.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = currentQ.errorMessage,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp,
                                            color = Color(0xFFFFB4B4),
                                            lineHeight = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Fix Options List
                    item {
                        Text(
                            text = "SELECT THE BUG FIX PATCH:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = NeonCyan
                        )
                    }

                    items(currentQ.options.indices.toList()) { optIdx ->
                        val isEliminated = eliminatedOptionIndices.contains(optIdx)
                        val optText = currentQ.options[optIdx]
                        val isCorrect = optIdx == currentQ.correctIndex
                        val isSelected = selectedOptionIndex == optIdx

                        val cardBg = when {
                            isEliminated -> Color(0xFF131B26).copy(alpha = 0.4f)
                            isAnswerSubmitted && isCorrect -> Color(0xFF1B4D2E)
                            isAnswerSubmitted && isSelected && !isCorrect -> Color(0xFF4D1B1B)
                            isSelected -> NeonCyan.copy(alpha = 0.2f)
                            else -> Color(0xFF0F1E2E)
                        }

                        val borderCol = when {
                            isEliminated -> Color.Transparent
                            isAnswerSubmitted && isCorrect -> Color(0xFF33FF33)
                            isAnswerSubmitted && isSelected && !isCorrect -> CoralRed
                            isSelected -> NeonCyan
                            else -> DarkBorder
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = cardBg,
                            border = BorderStroke(1.dp, borderCol),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !isAnswerSubmitted && !isEliminated) {
                                    selectedOptionIndex = optIdx
                                }
                                .testTag("debugger_option_$optIdx")
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isSelected) NeonCyan else Color(0xFF1E293B),
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = ('A' + optIdx).toString(),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color(0xFF003544) else Color.White
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Text(
                                    text = if (isEliminated) "[ ELIMINATED BY 50/50 ]" else optText,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isEliminated) Color.Gray else Color.White,
                                    modifier = Modifier.weight(1f)
                                )

                                if (isAnswerSubmitted && isCorrect) {
                                    Text("✅", fontSize = 16.sp)
                                } else if (isAnswerSubmitted && isSelected && !isCorrect) {
                                    Text("❌", fontSize = 16.sp)
                                }
                            }
                        }
                    }

                    // Action Button / Next Question
                    item {
                        if (!isAnswerSubmitted) {
                            Button(
                                onClick = {
                                    if (selectedOptionIndex != null) {
                                        isAnswerSubmitted = true
                                        val correct = selectedOptionIndex == currentQ.correctIndex
                                        if (correct) {
                                            val timeBonus = secondsLeft * 5
                                            val streakMultiplier = (streak + 1).coerceAtMost(4)
                                            score += (100 + timeBonus) * streakMultiplier
                                            streak += 1
                                        } else {
                                            streak = 0
                                            lives -= 1
                                        }
                                    }
                                },
                                enabled = selectedOptionIndex != null,
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("debugger_submit_button")
                            ) {
                                Text("COMPILE & TEST PATCH", fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                            }
                        } else {
                            Column {
                                // Detailed explanation
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFF132238),
                                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "EXPLANATION & WHY IT WORKS:",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = WarmAmber
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = currentQ.explanation,
                                            fontSize = 11.sp,
                                            color = Color.LightGray,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = {
                                        if (lives <= 0 || currentQuestionIndex + 1 >= questions.size) {
                                            isGameFinished = true
                                        } else {
                                            currentQuestionIndex += 1
                                            selectedOptionIndex = null
                                            isAnswerSubmitted = false
                                            eliminatedOptionIndices = emptySet()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (lives <= 0) CoralRed else Color(0xFF33FF33),
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp)
                                        .testTag("debugger_next_button")
                                ) {
                                    Text(
                                        text = if (lives <= 0 || currentQuestionIndex + 1 >= questions.size) "VIEW FINAL RESULTS" else "NEXT BUG PUZZLE ➔",
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }

    // Modal Dialog for Lifeline Information (Linter or Test Suite)
    if (activeLifelineModal != null) {
        AlertDialog(
            onDismissRequest = { activeLifelineModal = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(if (activeLifelineModal == "LINTER") "🤖 Linter Analysis Output" else "🧪 Test Suite Console Trace")
                }
            },
            text = {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black,
                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = if (activeLifelineModal == "LINTER") currentQ.linterHint else currentQ.testFailureOutput,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = if (activeLifelineModal == "LINTER") WarmAmber else Color(0xFFFF8888),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { activeLifelineModal = null }) {
                    Text("Return to Debugger", color = NeonCyan, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color(0xFF0F1E2E)
        )
    }
}
