package com.example.ui.plus.arcade

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.TownsquareTopBar
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.sqrt

// Game Enums
enum class ArcadeGame {
    NONE, SPACE_INVADERS, BLOCK_BREAKER
}

// Data Classes for Game States
data class SpaceInvader(var x: Float, var y: Float, val scoreValue: Int, val width: Float = 35f, val height: Float = 25f, var isAlive: Boolean = true)
data class Bullet(var x: Float, var y: Float, val isPlayerBullet: Boolean, var isActive: Boolean = true)
data class BricksBlock(val x: Float, val y: Float, val width: Float, val height: Float, val color: Color, var hitsLeft: Int)

data class ArcadeAchievement(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val targetScore: Int,
    var isUnlocked: Boolean = false
)

data class LeaderboardEntry(
    val name: String,
    val handle: String,
    val score: Int,
    val date: String,
    val isCurrentUser: Boolean = false
)

@Composable
fun TownsquareArcadeApp(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeGame by remember { mutableStateOf(ArcadeGame.NONE) }
    var highestSpaceScore by remember { mutableIntStateOf(0) }
    var highestBlockScore by remember { mutableIntStateOf(0) }

    // Arcade Achievements State
    var achievements by remember {
        mutableStateOf(
            listOf(
                ArcadeAchievement("cadet", "Arcade Cadet", "Launch your first retro game session.", "👾", 1, false),
                ArcadeAchievement("survivor", "Invasion Survivor", "Score 100+ points in Space Invaders.", "🛸", 100, false),
                ArcadeAchievement("demolisher", "Brick Demolisher", "Score 120+ points in Block Breaker.", "🧱", 120, false),
                ArcadeAchievement("duo", "Duo Gamer", "Play both Space Invaders and Block Breaker.", "🕹️", 2, false),
                ArcadeAchievement("legend", "Local Legend", "Get in the Top 3 Local High Scores.", "🏆", 350, false)
            )
        )
    }

    // Leaderboard entries (Static + User best)
    val userBest = maxOf(highestSpaceScore, highestBlockScore)
    val leaderboard = remember(userBest) {
        val baseList = listOf(
            LeaderboardEntry("Elena Vance", "@elenavance", 480, "2026-09-28"),
            LeaderboardEntry("Alex Chen", "@alexchen", 360, "2026-09-29"),
            LeaderboardEntry("Aria Chen", "@ariachen", 240, "2026-09-27"),
            LeaderboardEntry("Julian Frost", "@julian_frost", 150, "2026-09-29"),
            LeaderboardEntry("Sora DJ", "@djsora", 80, "2026-09-28")
        )

        val userEntry = LeaderboardEntry("You (Citizen)", "@citizen", userBest, "Today", isCurrentUser = true)
        val combined = (baseList + userEntry).sortedByDescending { it.score }
        combined
    }

    // Achievement Unlock Popup Toast
    var showUnlockToast by remember { mutableStateOf<ArcadeAchievement?>(null) }
    LaunchedEffect(showUnlockToast) {
        if (showUnlockToast != null) {
            delay(3000)
            showUnlockToast = null
        }
    }

    // Function to unlock achievement
    val triggerUnlock: (String) -> Unit = { id ->
        achievements = achievements.map { ach ->
            if (ach.id == id && !ach.isUnlocked) {
                showUnlockToast = ach
                ach.copy(isUnlocked = true)
            } else ach
        }
    }

    // Intercept Back button inside Arcade
    BackHandler {
        if (activeGame != ArcadeGame.NONE) {
            activeGame = ArcadeGame.NONE
        } else {
            onBack()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070B14)) // Dark Sci-Fi gaming color
            .testTag("arcade_root")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (activeGame == ArcadeGame.NONE) {
                // Main Game Lobby
                TownsquareTopBar(
                    title = "Townsquare Arcade",
                    subtitle = "Modular Gaming Hub • Retro 8-Bit Edition",
                    onOpenSidebar = {} // Left button is back
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onBack,
                        colors = ButtonDefaults.textButtonColors(contentColor = NeonCyan)
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Exit Arcade")
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF33FF33).copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF33FF33))
                    ) {
                        Text(
                            text = "BEST SCORE: $userBest PTS",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF33FF33),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Title Banner
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.Black,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF33FF33).copy(alpha = 0.3f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                Color(0xFF0F1E10),
                                                Color(0xFF050A05)
                                            )
                                        )
                                    )
                                    .padding(16.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "TOWNSQUARE ARCADE CABINET",
                                        style = TextStyle(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 18.sp,
                                            color = Color(0xFF33FF33),
                                            textAlign = TextAlign.Center
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Wired into the municipal server network. Top scores secure neighborhood fame.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.LightGray,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    // Games Section
                    item {
                        Text(
                            text = "AVAILABLE CABINET GAMES",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = NeonCyan
                        )
                    }

                    // GAME 1: Space Invaders
                    item {
                        GameCabinetCard(
                            title = "SPACE INVADERS",
                            description = "Defend the Townsquare perimeter against incoming alien ships! Retro laser combat, scrolling stars, and defensive grids.",
                            imageEmoji = "🚀",
                            neonColor = NeonCyan,
                            highScore = highestSpaceScore,
                            onPlay = {
                                activeGame = ArcadeGame.SPACE_INVADERS
                                triggerUnlock("cadet")
                                if (highestBlockScore > 0) triggerUnlock("duo")
                            }
                        )
                    }

                    // GAME 2: Block Breaker
                    item {
                        GameCabinetCard(
                            title = "BLOCK BREAKER",
                            description = "Slide the platform and bounce the core to destroy rows of municipal firewalls! Classic physics and multi-stage blocks.",
                            imageEmoji = "🧱",
                            neonColor = WarmAmber,
                            highScore = highestBlockScore,
                            onPlay = {
                                activeGame = ArcadeGame.BLOCK_BREAKER
                                triggerUnlock("cadet")
                                if (highestSpaceScore > 0) triggerUnlock("duo")
                            }
                        )
                    }

                    // Stats & Achievements & Leaderboard split Row
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Column 1: Local Highscores
                            Column(modifier = Modifier.weight(1.2f)) {
                                Text(
                                    text = "LOCAL LEADERBOARD",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                    color = NeonCyan,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = DarkSurfaceElevated,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        leaderboard.take(4).forEachIndexed { index, entry ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Surface(
                                                        shape = RoundedCornerShape(4.dp),
                                                        color = when (index) {
                                                            0 -> Color(0xFFFFD700).copy(alpha = 0.2f)
                                                            1 -> Color(0xFFC0C0C0).copy(alpha = 0.2f)
                                                            2 -> Color(0xFFCD7F32).copy(alpha = 0.2f)
                                                            else -> Color.Gray.copy(alpha = 0.1f)
                                                        },
                                                        border = androidx.compose.foundation.BorderStroke(
                                                            1.dp,
                                                            when (index) {
                                                                0 -> Color(0xFFFFD700)
                                                                1 -> Color(0xFFC0C0C0)
                                                                2 -> Color(0xFFCD7F32)
                                                                else -> Color.Gray
                                                            }
                                                        ),
                                                        modifier = Modifier.size(20.dp)
                                                    ) {
                                                        Box(contentAlignment = Alignment.Center) {
                                                            Text(
                                                                text = "${index + 1}",
                                                                fontSize = 11.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color.White
                                                            )
                                                        }
                                                    }

                                                    Spacer(modifier = Modifier.width(8.dp))

                                                    Column {
                                                        Text(
                                                            text = entry.name,
                                                            fontSize = 12.sp,
                                                            fontWeight = if (entry.isCurrentUser) FontWeight.Bold else FontWeight.Normal,
                                                            color = if (entry.isCurrentUser) NeonCyan else Color.White
                                                        )
                                                        Text(
                                                            text = entry.handle,
                                                            fontSize = 10.sp,
                                                            color = Color.Gray
                                                        )
                                                    }
                                                }

                                                Text(
                                                    text = "${entry.score} PTS",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = FontFamily.Monospace,
                                                    color = if (index < 3) Color(0xFF33FF33) else Color.LightGray
                                                )
                                            }

                                            if (index < 3) {
                                                HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 4.dp))
                                            }
                                        }
                                    }
                                }
                            }

                            // Column 2: Achievements Tracker
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "ACHIEVEMENTS",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                    color = NeonCyan,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = DarkSurfaceElevated,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        achievements.forEach { ach ->
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = if (ach.isUnlocked) Color(0xFF33FF33).copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.1f),
                                                    border = androidx.compose.foundation.BorderStroke(
                                                        1.dp,
                                                        if (ach.isUnlocked) Color(0xFF33FF33) else Color.Gray.copy(alpha = 0.4f)
                                                    ),
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Text(text = ach.iconEmoji, fontSize = 14.sp)
                                                    }
                                                }

                                                Spacer(modifier = Modifier.width(8.dp))

                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = ach.title,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (ach.isUnlocked) Color.White else Color.Gray
                                                    )
                                                    Text(
                                                        text = ach.description,
                                                        fontSize = 9.sp,
                                                        color = if (ach.isUnlocked) Color.LightGray else Color.Gray,
                                                        lineHeight = 11.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            } else {
                // ACTIVE RETRO GAME CABINET LAYOUT
                Box(modifier = Modifier.fillMaxSize()) {
                    when (activeGame) {
                        ArcadeGame.SPACE_INVADERS -> {
                            SpaceInvadersCabinet(
                                onGameOver = { finalScore ->
                                    if (finalScore > highestSpaceScore) {
                                        highestSpaceScore = finalScore
                                    }
                                    if (finalScore >= 100) {
                                        triggerUnlock("survivor")
                                    }
                                    if (finalScore >= 360) {
                                        triggerUnlock("legend")
                                    }
                                    activeGame = ArcadeGame.NONE
                                },
                                onExit = { activeGame = ArcadeGame.NONE }
                            )
                        }
                        ArcadeGame.BLOCK_BREAKER -> {
                            BlockBreakerCabinet(
                                onGameOver = { finalScore ->
                                    if (finalScore > highestBlockScore) {
                                        highestBlockScore = finalScore
                                    }
                                    if (finalScore >= 120) {
                                        triggerUnlock("demolisher")
                                    }
                                    if (finalScore >= 360) {
                                        triggerUnlock("legend")
                                    }
                                    activeGame = ArcadeGame.NONE
                                },
                                onExit = { activeGame = ArcadeGame.NONE }
                            )
                        }
                        else -> {}
                    }
                }
            }
        }

        // Achievement Unlocked Notification Toast
        AnimatedVisibility(
            visible = showUnlockToast != null,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp)
        ) {
            showUnlockToast?.let { ach ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF0F1E10),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF33FF33)),
                    shadowElevation = 8.dp,
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🏆", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "ACHIEVEMENT UNLOCKED!",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                color = Color(0xFF33FF33)
                            )
                            Text(
                                text = ach.title,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            Text(
                                text = ach.description,
                                color = Color.LightGray,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ------------------- CABINET COMPONENT CARDS -------------------

@Composable
fun GameCabinetCard(
    title: String,
    description: String,
    imageEmoji: String,
    neonColor: Color,
    highScore: Int,
    onPlay: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = DarkSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = neonColor.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, neonColor),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = imageEmoji, fontSize = 28.sp)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 4.dp),
                    lineHeight = 15.sp
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color.Black,
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, neonColor.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "HIGH SCORE: $highScore PTS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = neonColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = onPlay,
                colors = ButtonDefaults.buttonColors(containerColor = neonColor),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text(
                    text = "INSERT COIN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

// ------------------- SPACE INVADERS CABINET -------------------

@Composable
fun SpaceInvadersCabinet(
    onGameOver: (Int) -> Unit,
    onExit: () -> Unit
) {
    var playerX by remember { mutableFloatStateOf(180f) }
    var score by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(3) }
    var isRunning by remember { mutableStateOf(true) }

    // State collections
    val bullets = remember { mutableStateListOf<Bullet>() }
    val invaders = remember { mutableStateListOf<SpaceInvader>() }
    var invadersDirection by remember { mutableFloatStateOf(1f) } // 1 for right, -1 for left

    val maxGameWidth = 360f
    val maxGameHeight = 400f

    // Seed/Reset invaders
    LaunchedEffect(Unit) {
        invaders.clear()
        bullets.clear()
        for (row in 0 until 3) {
            for (col in 0 until 5) {
                invaders.add(
                    SpaceInvader(
                        x = 40f + col * 55f,
                        y = 40f + row * 40f,
                        scoreValue = (3 - row) * 10
                    )
                )
            }
        }
    }

    // Main Game Loop using Coroutines
    LaunchedEffect(isRunning) {
        if (!isRunning) return@LaunchedEffect
        while (isActive && isRunning) {
            delay(40) // ~25 FPS loop

            // 1. Move bullets
            bullets.forEach { b ->
                if (b.isPlayerBullet) {
                    b.y -= 12f
                    if (b.y < 0) b.isActive = false
                } else {
                    b.y += 8f
                    if (b.y > maxGameHeight) b.isActive = false
                }
            }
            // Remove dead bullets
            bullets.removeAll { !it.isActive }

            // 2. Move invaders
            var reachedWall = false
            invaders.filter { it.isAlive }.forEach { inv ->
                inv.x += invadersDirection * 2f
                if (inv.x > maxGameWidth - 40f || inv.x < 10f) {
                    reachedWall = true
                }
            }

            if (reachedWall) {
                invadersDirection = -invadersDirection
                invaders.filter { it.isAlive }.forEach { inv ->
                    inv.y += 12f
                    // If invaders reach player height, game over
                    if (inv.y >= maxGameHeight - 50f) {
                        isRunning = false
                    }
                }
            }

            // 3. Collision detection: Player Bullet hit Invader
            bullets.filter { it.isPlayerBullet && it.isActive }.forEach { b ->
                invaders.filter { it.isAlive }.forEach { inv ->
                    if (b.x >= inv.x && b.x <= inv.x + inv.width &&
                        b.y >= inv.y && b.y <= inv.y + inv.height
                    ) {
                        b.isActive = false
                        inv.isAlive = false
                        score += inv.scoreValue
                    }
                }
            }

            // 4. Invader shooting at player randomly
            val activeInvaders = invaders.filter { it.isAlive }
            if (activeInvaders.isEmpty()) {
                // Clean level cleared! Re-spawn faster
                isRunning = false
                onGameOver(score + 100) // bonus score for winning
                return@LaunchedEffect
            }

            if (Math.random() < 0.03 && activeInvaders.isNotEmpty()) {
                val shooter = activeInvaders[(Math.random() * activeInvaders.size).toInt()]
                bullets.add(Bullet(x = shooter.x + shooter.width / 2, y = shooter.y + shooter.height, isPlayerBullet = false))
            }

            // 5. Enemy Bullet hit Player
            bullets.filter { !it.isPlayerBullet && it.isActive }.forEach { b ->
                if (b.x >= playerX - 15f && b.x <= playerX + 15f &&
                    b.y >= maxGameHeight - 35f && b.y <= maxGameHeight - 15f
                ) {
                    b.isActive = false
                    lives--
                    if (lives <= 0) {
                        isRunning = false
                    }
                }
            }
        }

        // Game officially finished
        onGameOver(score)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Cabinet Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onExit) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Exit Game", tint = Color.White)
            }

            Text(
                text = "SPACE INVADERS",
                style = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = NeonCyan, fontSize = 16.sp)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Favorite, contentDescription = "Lives", tint = Color.Red, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "x$lives", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Play Screen Surface (Strict Canvas Box Aspect)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .border(2.dp, NeonCyan, RoundedCornerShape(8.dp)),
            color = Color.Black,
            shape = RoundedCornerShape(8.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Drawing Stars
                for (i in 0..15) {
                    val starX = (Math.sin(i.toDouble() + score) * 0.5 + 0.5) * size.width
                    val starY = ((i * 30 + score * 2) % size.height).toFloat()
                    drawCircle(Color.Gray.copy(alpha = 0.3f), radius = 1.5f, center = Offset(starX.toFloat(), starY))
                }

                // Drawing Invaders
                invaders.filter { it.isAlive }.forEach { inv ->
                    // Normalize to canvas sizes
                    val scaleX = size.width / maxGameWidth
                    val scaleY = size.height / maxGameHeight

                    drawRect(
                        color = when (inv.scoreValue) {
                            30 -> Color(0xFFFF5252) // Top tier
                            20 -> Color(0xFF33FF33) // Mid tier
                            else -> NeonCyan       // Low tier
                        },
                        topLeft = Offset(inv.x * scaleX, inv.y * scaleY),
                        size = Size(inv.width * scaleX, inv.height * scaleY)
                    )

                    // Draw pixel antenna
                    drawLine(
                        color = Color.White,
                        start = Offset((inv.x + inv.width / 2) * scaleX, inv.y * scaleY),
                        end = Offset((inv.x + inv.width / 2) * scaleX, (inv.y - 4f) * scaleY),
                        strokeWidth = 2f
                    )
                }

                // Drawing Bullets
                bullets.forEach { b ->
                    val scaleX = size.width / maxGameWidth
                    val scaleY = size.height / maxGameHeight
                    drawRect(
                        color = if (b.isPlayerBullet) Color(0xFF33FF33) else Color(0xFFFF9F1C),
                        topLeft = Offset((b.x - 2f) * scaleX, b.y * scaleY),
                        size = Size(4f * scaleX, 10f * scaleY)
                    )
                }

                // Drawing Player Spaceship
                val scaleX = size.width / maxGameWidth
                val scaleY = size.height / maxGameHeight
                val shipWidth = 32f
                val shipHeight = 16f

                // Draw main body
                drawRect(
                    color = NeonCyan,
                    topLeft = Offset((playerX - shipWidth / 2) * scaleX, (maxGameHeight - 30f) * scaleY),
                    size = Size(shipWidth * scaleX, shipHeight * scaleY)
                )
                // Draw cockpit top
                drawRect(
                    color = Color.White,
                    topLeft = Offset((playerX - 6f) * scaleX, (maxGameHeight - 38f) * scaleY),
                    size = Size(12f * scaleX, 8f * scaleY)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // On-Screen Score Display
        Text(
            text = "SCORE: $score PTS",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            color = Color(0xFF33FF33),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Controller Console Controls (Retro Arcade style)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Directional D-PAD
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { if (playerX > 30f) playerX -= 25f },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(54.dp)
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Left", tint = NeonCyan)
                }

                Button(
                    onClick = { if (playerX < maxGameWidth - 30f) playerX += 25f },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(54.dp)
                ) {
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Right", tint = NeonCyan)
                }
            }

            // RED FIRE TRIGGER BUTTON
            Button(
                onClick = {
                    if (bullets.count { it.isPlayerBullet } < 3) {
                        bullets.add(Bullet(x = playerX, y = maxGameHeight - 40f, isPlayerBullet = true))
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3B30)),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                modifier = Modifier
                    .width(100.dp)
                    .height(54.dp)
                    .testTag("arcade_fire_button")
            ) {
                Text(text = "FIRE", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
            }
        }
    }
}

// ------------------- BLOCK BREAKER CABINET -------------------

@Composable
fun BlockBreakerCabinet(
    onGameOver: (Int) -> Unit,
    onExit: () -> Unit
) {
    var paddleX by remember { mutableFloatStateOf(180f) }
    val paddleWidth = 60f
    val paddleHeight = 10f

    var ballX by remember { mutableFloatStateOf(180f) }
    var ballY by remember { mutableFloatStateOf(250f) }
    var ballVx by remember { mutableFloatStateOf(4f) }
    var ballVy by remember { mutableFloatStateOf(-5f) }
    val ballRadius = 6f

    var score by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(3) }
    var isRunning by remember { mutableStateOf(true) }

    val blocks = remember { mutableStateListOf<BricksBlock>() }

    val maxGameWidth = 360f
    val maxGameHeight = 400f

    // Initial blocks spawning
    LaunchedEffect(Unit) {
        blocks.clear()
        val colors = listOf(Color(0xFFFF3B30), Color(0xFFFFCC00), Color(0xFF34C759), Color(0xFF007AFF))
        for (row in 0 until 4) {
            for (col in 0 until 6) {
                blocks.add(
                    BricksBlock(
                        x = 15f + col * 55f,
                        y = 35f + row * 22f,
                        width = 50f,
                        height = 16f,
                        color = colors[row],
                        hitsLeft = 1
                    )
                )
            }
        }
    }

    // Engine loop
    LaunchedEffect(isRunning) {
        if (!isRunning) return@LaunchedEffect
        while (isActive && isRunning) {
            delay(40) // ~25 FPS

            // Move ball
            ballX += ballVx
            ballY += ballVy

            // Wall collisions (Left / Right)
            if (ballX <= ballRadius) {
                ballX = ballRadius
                ballVx = -ballVx
            } else if (ballX >= maxGameWidth - ballRadius) {
                ballX = maxGameWidth - ballRadius
                ballVx = -ballVx
            }

            // Ceiling collision
            if (ballY <= ballRadius) {
                ballY = ballRadius
                ballVy = -ballVy
            }

            // Bottom border - lose life
            if (ballY >= maxGameHeight) {
                lives--
                if (lives <= 0) {
                    isRunning = false
                } else {
                    // Reset ball position
                    ballX = 180f
                    ballY = 250f
                    ballVx = if (Math.random() > 0.5) 4f else -4f
                    ballVy = -5f
                }
            }

            // Paddle collision
            if (ballY >= maxGameHeight - 40f && ballY <= maxGameHeight - 30f) {
                if (ballX >= paddleX - paddleWidth / 2 && ballX <= paddleX + paddleWidth / 2) {
                    ballVy = -ballVy
                    // Calculate bounce angle offset depending on where ball hits paddle
                    val hitPoint = (ballX - paddleX) / (paddleWidth / 2)
                    ballVx = hitPoint * 5f
                    score += 5 // bounce bonus points!
                }
            }

            // Block collision
            var blockDestroyed = false
            blocks.filter { it.hitsLeft > 0 }.forEach { block ->
                if (ballX + ballRadius >= block.x && ballX - ballRadius <= block.x + block.width &&
                    ballY + ballRadius >= block.y && ballY - ballRadius <= block.y + block.height
                ) {
                    ballVy = -ballVy
                    block.hitsLeft--
                    score += 15
                    blockDestroyed = true
                }
            }

            if (blocks.none { it.hitsLeft > 0 }) {
                // All blocks demolished - WIN!
                isRunning = false
                onGameOver(score + 150)
                return@LaunchedEffect
            }
        }

        onGameOver(score)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onExit) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Exit Game", tint = Color.White)
            }

            Text(
                text = "BLOCK BREAKER",
                style = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = WarmAmber, fontSize = 16.sp)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Favorite, contentDescription = "Lives", tint = Color.Red, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "x$lives", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Interactive Canvas Box
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .border(2.dp, WarmAmber, RoundedCornerShape(8.dp))
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val newX = paddleX + dragAmount.x
                        if (newX >= paddleWidth / 2 && newX <= maxGameWidth - paddleWidth / 2) {
                            paddleX = newX
                        }
                    }
                },
            color = Color.Black,
            shape = RoundedCornerShape(8.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val scaleX = size.width / maxGameWidth
                val scaleY = size.height / maxGameHeight

                // Draw blocks
                blocks.filter { it.hitsLeft > 0 }.forEach { block ->
                    drawRect(
                        color = block.color,
                        topLeft = Offset(block.x * scaleX, block.y * scaleY),
                        size = Size(block.width * scaleX, block.height * scaleY)
                    )
                    // Inner block glow
                    drawRect(
                        color = Color.White.copy(alpha = 0.3f),
                        topLeft = Offset((block.x + 2f) * scaleX, (block.y + 2f) * scaleY),
                        size = Size((block.width - 4f) * scaleX, (block.height - 4f) * scaleY)
                    )
                }

                // Draw ball
                drawCircle(
                    color = Color.White,
                    radius = ballRadius * scaleX,
                    center = Offset(ballX * scaleX, ballY * scaleY)
                )

                // Draw Paddle
                drawRect(
                    color = WarmAmber,
                    topLeft = Offset((paddleX - paddleWidth / 2) * scaleX, (maxGameHeight - 40f) * scaleY),
                    size = Size(paddleWidth * scaleX, paddleHeight * scaleY)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "SCORE: $score PTS",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            color = WarmAmber,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Interactive control buttons / sliders for ease of use in touch emulators
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { if (paddleX > paddleWidth / 2) paddleX -= 30f },
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, WarmAmber),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(54.dp)
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Paddle Left", tint = WarmAmber)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "DRAG / TAP TO SLIDE",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = { if (paddleX < maxGameWidth - paddleWidth / 2) paddleX += 30f },
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, WarmAmber),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(54.dp)
            ) {
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Paddle Right", tint = WarmAmber)
            }
        }
    }
}
