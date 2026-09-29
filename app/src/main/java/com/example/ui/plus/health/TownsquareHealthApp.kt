package com.example.ui.plus.health

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.TownsquareTopBar
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmAmber
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun TownsquareHealthApp(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Fitness States
    var stepsCount by remember { mutableIntStateOf(8450) }
    val stepsGoal = 10000
    var caloriesBurned by remember { mutableIntStateOf(342) }
    var activeMinutes by remember { mutableIntStateOf(45) }
    var waterCups by remember { mutableIntStateOf(5) }
    val waterGoal = 8

    // Heart Rate Simulator States
    var currentBpm by remember { mutableIntStateOf(72) }
    var isBeating by remember { mutableStateOf(false) }

    // Screen Time State
    val screenTimeMinutes = 135 // 2h 15m
    val mediaMinutes = 45
    val arcadeMinutes = 20
    val mailMinutes = 70

    // FOMO & Digital Well-being settings
    var digestNotifications by remember { mutableStateOf(false) }
    var feedBreakerActive by remember { mutableStateOf(true) }
    var sleepGrayscaleActive by remember { mutableStateOf(false) }

    // Breathing Assistant Overlay State
    var isBreathingActive by remember { mutableStateOf(false) }
    var breathingTimer by remember { mutableIntStateOf(60) }
    var breathPhase by remember { mutableStateOf("Inhale...") } // "Inhale", "Hold", "Exhale"
    var breathScale by remember { mutableFloatStateOf(1.0f) }

    // Heartbeat Pulse Simulator loop
    LaunchedEffect(Unit) {
        while (isActive) {
            delay(800) // Heart rate frequency (~75 bpm)
            isBeating = true
            currentBpm = (68..76).random()
            delay(150)
            isBeating = false
        }
    }

    // Breathing Assistant cycle loop
    LaunchedEffect(isBreathingActive) {
        if (!isBreathingActive) return@LaunchedEffect
        breathingTimer = 60
        while (isActive && isBreathingActive && breathingTimer > 0) {
            // 4-second inhale, 4-second hold, 4-second exhale cycle
            val cycleSec = (60 - breathingTimer) % 12
            when {
                cycleSec < 4 -> {
                    breathPhase = "Breathe In Slowly..."
                    breathScale = 1.0f + (cycleSec / 4.0f) * 0.8f // expands
                }
                cycleSec < 8 -> {
                    breathPhase = "Hold Breath..."
                    breathScale = 1.8f
                }
                else -> {
                    breathPhase = "Breathe Out Peacefully..."
                    breathScale = 1.8f - ((cycleSec - 8) / 4.0f) * 0.8f // contracts
                }
            }
            delay(1000)
            breathingTimer--
        }
        isBreathingActive = false
    }

    // Handle back button interception to exit mindfulness breathing first
    BackHandler {
        if (isBreathingActive) {
            isBreathingActive = false
        } else {
            onBack()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)) // Sleek dark wellbeing background
            .testTag("health_app_root")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // App Bar
            TownsquareTopBar(
                title = "Townsquare Health & Mind",
                subtitle = "Ecosystem Health • Mindful Screen Controls",
                onOpenSidebar = {} // uses back arrow navigation
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("health_back_button")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = NeonCyan
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Back to Plus Dashboard",
                    color = NeonCyan,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onBack() }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: Fitness Metrics Panel
                item {
                    Text(
                        text = "TODAY'S ACTIVITY",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = NeonCyan
                    )
                }

                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = DarkSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Step Ring Canvas
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.size(110.dp)
                                ) {
                                    val progressFraction = stepsCount.toFloat() / stepsGoal
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        // Background Track
                                        drawArc(
                                            color = Color.White.copy(alpha = 0.08f),
                                            startAngle = -90f,
                                            sweepAngle = 360f,
                                            useCenter = false,
                                            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                                        )
                                        // Neon Cyan steps arc
                                        drawArc(
                                            color = NeonCyan,
                                            startAngle = -90f,
                                            sweepAngle = 360f * progressFraction,
                                            useCenter = false,
                                            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.DirectionsWalk, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(24.dp))
                                        Text(
                                            text = "$stepsCount",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 18.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "/ $stepsGoal",
                                            fontSize = 10.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                // Secondary metrics
                                Column(
                                    modifier = Modifier.padding(start = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Calories Row
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(shape = CircleShape, color = Color(0xFFFF5252).copy(alpha = 0.15f), modifier = Modifier.size(32.dp)) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(18.dp))
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text("Calories Burned", fontSize = 11.sp, color = Color.Gray)
                                            Text("$caloriesBurned kcal", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                                        }
                                    }

                                    // Active Minutes Row
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(shape = CircleShape, color = Color(0xFF33FF33).copy(alpha = 0.15f), modifier = Modifier.size(32.dp)) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(Icons.Default.Timer, contentDescription = null, tint = Color(0xFF33FF33), modifier = Modifier.size(18.dp))
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text("Active Exercise", fontSize = 11.sp, color = Color.Gray)
                                            Text("$activeMinutes mins", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Step simulation log button
                            Button(
                                onClick = {
                                    stepsCount += 850
                                    caloriesBurned += 32
                                    activeMinutes += 5
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan.copy(alpha = 0.15f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().height(36.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Log Walk Session (+850 steps)", fontSize = 11.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Section 2: Water Intake Logger & Beating Heart Rate Simulator
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Water Intake (Left half)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = DarkSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                            modifier = Modifier.weight(1f).height(145.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Water Logger", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                    Text("$waterCups/$waterGoal cups", fontSize = 11.sp, color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
                                }

                                // Visual cups row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    for (i in 1..waterGoal) {
                                        Icon(
                                            imageVector = Icons.Default.LocalCafe,
                                            contentDescription = null,
                                            tint = if (i <= waterCups) Color(0xFF38BDF8) else Color.Gray.copy(alpha = 0.4f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Button(
                                        onClick = { if (waterCups > 0) waterCups-- },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray.copy(alpha = 0.15f)),
                                        contentPadding = PaddingValues(0.dp),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.weight(1f).height(30.dp)
                                    ) {
                                        Text("-", color = Color.White, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { if (waterCups < 12) waterCups++ },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8).copy(alpha = 0.15f)),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8)),
                                        contentPadding = PaddingValues(0.dp),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.weight(1f).height(30.dp)
                                    ) {
                                        Text("+", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Beating Heart Rate Simulator (Right half)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = DarkSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                            modifier = Modifier.weight(1f).height(145.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
                                Text("Heart Rate Monitor", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)

                                val heartScale = if (isBeating) 1.25f else 1.0f
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Pulse Beats",
                                    tint = Color(0xFFFF3B30),
                                    modifier = Modifier
                                        .size(36.dp * heartScale)
                                        .animateContentSize()
                                )

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$currentBpm BPM",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "Resting: 62 bpm",
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }

                // Section 3: Screen Time Counter Breakdown
                item {
                    Text(
                        text = "DIGITAL USAGE ANALYSIS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = NeonCyan,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = DarkSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Screen Time Counter", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                    Text("Calculated cross-app usage logs", fontSize = 11.sp, color = Color.Gray)
                                }

                                Text(
                                    text = "2h 15m",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp,
                                    color = WarmAmber
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Visual usage bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            ) {
                                Box(modifier = Modifier.weight(mediaMinutes.toFloat()).fillMaxHeight().background(Color(0xFFE11D48))) // Rose
                                Box(modifier = Modifier.weight(arcadeMinutes.toFloat()).fillMaxHeight().background(Color(0xFF33FF33))) // Green
                                Box(modifier = Modifier.weight(mailMinutes.toFloat()).fillMaxHeight().background(Color(0xFF38BDF8))) // Blue
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Breakdown Legends
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                UsageLegendRow(color = Color(0xFFE11D48), name = "Media Feed & Article Dispatches", duration = "45m")
                                UsageLegendRow(color = Color(0xFF33FF33), name = "Cabinet Arcade Gaming Session", duration = "20m")
                                UsageLegendRow(color = Color(0xFF38BDF8), name = "Secure Dispatch Correspondence (Mailbox)", duration = "1h 10m")
                            }
                        }
                    }
                }

                // Section 4: FOMO Wellbeing Shield Settings
                item {
                    Text(
                        text = "FOMO & ATTENTION SHIELDS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = NeonCyan,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = DarkSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            // 1. Digestive notification switch
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Digestive Notification Buffering", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                    Text("Groups system alerts into twice-a-day packages (8 AM & 8 PM) to shield focus.", fontSize = 11.sp, color = Color.Gray, lineHeight = 14.sp)
                                }
                                Switch(
                                    checked = digestNotifications,
                                    onCheckedChange = { digestNotifications = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF0F172A), checkedTrackColor = NeonCyan)
                                )
                            }

                            HorizontalDivider(color = DarkBorder)

                            // 2. Feed breaker switch
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Infinite Feed Breaker Shield", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                    Text("Blocks continuous scroll triggers after 15 minutes of uninterrupted activity.", fontSize = 11.sp, color = Color.Gray, lineHeight = 14.sp)
                                }
                                Switch(
                                    checked = feedBreakerActive,
                                    onCheckedChange = { feedBreakerActive = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF0F172A), checkedTrackColor = NeonCyan)
                                )
                            }

                            HorizontalDivider(color = DarkBorder)

                            // 3. Sleep grayscale mode
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Grayscale Sleep Shroud Mode", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                    Text("Converts application layouts to monochrome grayscale after 10:00 PM.", fontSize = 11.sp, color = Color.Gray, lineHeight = 14.sp)
                                }
                                Switch(
                                    checked = sleepGrayscaleActive,
                                    onCheckedChange = { sleepGrayscaleActive = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF0F172A), checkedTrackColor = NeonCyan)
                                )
                            }
                        }
                    }
                }

                // Section 5: Interactive Breathing Anchor Assistant
                item {
                    Text(
                        text = "MINDFUL BREATHING ANCHOR",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = NeonCyan,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = DarkSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Breathe with the Soundscapes",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Anchor your attention with a guided 60-second breathing balloon.",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = { isBreathingActive = true },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Waves, contentDescription = null, tint = Color(0xFF0F172A))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Start Guided Breath Anchor", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        // Full Screen Breathing Balloon Guide Overlay
        AnimatedVisibility(
            visible = isBreathingActive,
            enter = fadeIn() + expandIn(),
            exit = fadeOut() + shrinkOut()
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFF0F172A).copy(alpha = 0.96f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    // Close button
                    IconButton(
                        onClick = { isBreathingActive = false },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .testTag("close_breathing_overlay")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(28.dp))
                    }

                    // Main breathing balloon container
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "MINDFUL BREATHING SHIELD",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = NeonCyan
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Interactive Balloon bubble
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(240.dp)
                        ) {
                            // Expand/Contract bubble circle
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF0EA5E9).copy(alpha = 0.25f),
                                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF0EA5E9)),
                                modifier = Modifier
                                    .size(120.dp * breathScale)
                                    .animateContentSize()
                            ) {}

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = breathPhase,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$breathingTimer s",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonCyan,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            text = "Follow the expanding circle. Anchor your attention. Inhale for 4 seconds, Hold for 4, Exhale for 4.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(260.dp),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UsageLegendRow(color: Color, name: String, duration: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = CircleShape,
                color = color,
                modifier = Modifier.size(10.dp)
            ) {}
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = name, fontSize = 12.sp, color = Color.White)
        }
        Text(text = duration, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.LightGray)
    }
}
