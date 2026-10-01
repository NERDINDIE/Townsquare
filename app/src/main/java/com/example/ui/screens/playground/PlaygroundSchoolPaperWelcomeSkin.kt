package com.example.ui.screens.playground

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// ==========================================
// 1. PLAYGROUND OPENING ANIMATION
// ==========================================

@Composable
fun PlaygroundOpeningAnimation(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var animationPhase by remember { mutableIntStateOf(0) }

    val scaleAnim = remember { Animatable(0.3f) }
    val rotateAnim = remember { Animatable(-15f) }
    val alphaAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alphaAnim.animateTo(1f, animationSpec = tween(400))
        scaleAnim.animateTo(
            1.15f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
        scaleAnim.animateTo(1.0f, animationSpec = tween(200))
        rotateAnim.animateTo(0f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        
        delay(1200L)
        animationPhase = 1
        delay(600L)
        onFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFEE55),
                        Color(0xFFFF9900),
                        Color(0xFFFF5252)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(24.dp)
                .scale(scaleAnim.value)
                .rotate(rotateAnim.value)
        ) {
            // Cheerful School Mascot Badge
            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 12.dp,
                modifier = Modifier.size(110.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "🎒", fontSize = 56.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Playful Title Banner
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF1E293B),
                border = BorderStroke(3.dp, Color.White),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🎈 PLAYGROUND 🎈",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFFD54F),
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "OAK CREEK ELEMENTARY CIVIC EDITION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White.copy(alpha = 0.9f)
            ) {
                Text(
                    text = "⭐ Learning • Discovery • Fun • Friendship ⭐",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD84315),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }
    }
}

// ==========================================
// 2. SCHOOL PAPER BROADSHEET WELCOME SCREEN
// ==========================================

@Composable
fun PlaygroundSchoolPaperWelcomeScreen(
    onEnterPlayground: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFFDF5)) // Warm newsprint cream
            .verticalScroll(scrollState)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("playground_school_paper_screen")
    ) {
        // --- 1. NEWSPAPER MASTHEAD ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFF8E7))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "VOL. 14 • NO. 4", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5D4037))
                Text(text = "OAK CREEK ELEMENTARY SCHOOL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD84315))
                Text(text = "FREE STUDENT COPY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5D4037))
            }

            HorizontalDivider(color = Color(0xFF5D4037), thickness = 2.dp, modifier = Modifier.padding(vertical = 6.dp))

            Text(
                text = "THE OAK CREEK BUGLE",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 1.sp
                ),
                color = Color(0xFF2E1C12),
                textAlign = TextAlign.Center
            )

            Text(
                text = "📰 The Official Student Gazette & Recess Dispatch • Edited by Students, for Students!",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF795548),
                textAlign = TextAlign.Center
            )

            HorizontalDivider(color = Color(0xFF5D4037), thickness = 1.dp, modifier = Modifier.padding(vertical = 6.dp))

            // Recess & Weather Ticker
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFFFECB3),
                border = BorderStroke(1.dp, Color(0xFFFFB300)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "☀️ Recess Forecast:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "72°F & Sunny • Perfect for Monkey Bars!", fontSize = 11.sp, color = Color(0xFF3E2723))
                    }
                    Text(text = "🔔 Bell: 10:15 AM", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFBF360C))
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- 2. FRONT PAGE HEADLINE STORY ---
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE0D7C6)),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFFF5722)
                    ) {
                        Text(
                            text = "🏆 LEAD HEADLINE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "🚀 4th Grade Robotics Squad Wins District Championship!",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Serif
                        ),
                        color = Color(0xFF1F1610)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "By Leo Vance, Junior Science Reporter • Photography by Room 204",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8D6E63)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "The Oak Creek 'Robo-Otters' built a miniature solar rover that navigated the obstacle course in just 42 seconds! The rover featured an eco-sensor that counts fallen autumn leaves. Principal Henderson cheered from the front row as the team took home the giant blue ribbon.",
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                        color = Color(0xFF3E2723)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- 3. TWO-COLUMN STORIES ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Left Column: Art Room Mural
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE0D7C6)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(text = "🎨 ART ROOM NEWS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8E24AA))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "100 Butterflies Mural Completed",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF2E1C12),
                            fontFamily = FontFamily.Serif
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Each student in 2nd and 4th grade painted a unique watercolor butterfly in the library corridor.",
                            fontSize = 11.sp,
                            color = Color(0xFF5D4037)
                        )
                    }
                }

                // Right Column: Cafeteria Special
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE0D7C6)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(text = "🥪 TODAY'S LUNCH", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Crispy Tacos & Watermelon",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF2E1C12),
                            fontFamily = FontFamily.Serif
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Served with fresh guacamole, corn salsa, and chocolate milk carton.",
                            fontSize = 11.sp,
                            color = Color(0xFF5D4037)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- 4. RIDDLE & COMICS FUN CORNER ---
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFE8F5E9),
                border = BorderStroke(1.dp, Color(0xFFA5D6A7)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🧩", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "RIDDLE OF THE DAY:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20)
                        )
                        Text(
                            text = "\"What has hands but cannot clap?\"",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E1C12)
                        )
                        Text(
                            text = "Answer: A Clock! ⏰",
                            fontSize = 10.sp,
                            color = Color(0xFF388E3C)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- 5. ENTER PLAYGROUND CTA BUTTON ---
            Button(
                onClick = onEnterPlayground,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800),
                    contentColor = Color(0xFF3E2723)
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("enter_playground_broadsheet_btn")
            ) {
                Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = Color(0xFF3E2723))
                Spacer(modifier = Modifier.width(8.dp))
                Text("🚀 Enter My Playground", fontSize = 16.sp, fontWeight = FontWeight.Black)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ==========================================
// 3. SPECIAL PLAYGROUND SIDEBAR DRAWER
// ==========================================

@Composable
fun PlaygroundSidebarDrawer(
    activeChildName: String = "Leo",
    schoolName: String = "Oak Creek Elementary",
    grade: String = "4th Grade",
    onClose: () -> Unit,
    onSwitchToParentProfile: () -> Unit,
    onOpenDoodlePad: () -> Unit = {},
    onOpenStoryCorner: () -> Unit = {},
    onOpenScienceLab: () -> Unit = {},
    onOpenBackpackHomework: () -> Unit = {},
    onOpenSchoolPaper: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .width(310.dp),
        color = Color(0xFF1A1F36), // Deep playful navy
        border = BorderStroke(1.dp, Color(0xFF313B63))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header with Student Profile Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFFB300),
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "🦁", fontSize = 24.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "$activeChildName's Cubby",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = Color.White
                        )
                        Text(
                            text = "$schoolName • $grade",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFFFCA28)
                        )
                    }
                }

                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Student Badges & Stars Counter
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF282F52),
                border = BorderStroke(1.dp, Color(0xFF3F4A7A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "⭐ 28", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFFFFD54F))
                        Text(text = "Gold Stars", fontSize = 10.sp, color = Color(0xFFB0B9D6))
                    }
                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0xFF3F4A7A)))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🏅 5", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFF4DD0E1))
                        Text(text = "Badges Earned", fontSize = 10.sp, color = Color(0xFFB0B9D6))
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "KIDS HUBS & ACTIVITIES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4DD0E1),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Playground Activity Links
            val kidsApps = listOf(
                Triple("📰 School Bugle Paper", "Read student articles & comics", onOpenSchoolPaper),
                Triple("🎨 Color & Doodle Pad", "Draw cartoons & freehand art", onOpenDoodlePad),
                Triple("📚 Storybooks & Fun Comics", "Oak Creek library collection", onOpenStoryCorner),
                Triple("🔬 Science & Nature Lab", "Mini experiments & leaf guides", onOpenScienceLab),
                Triple("🎒 Backpack & Homework", "Check reading logs & math cards", onOpenBackpackHomework)
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                kidsApps.forEach { (title, subtitle, onClick) ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF242A4A),
                        border = BorderStroke(1.dp, Color(0xFF38426E)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onClick()
                                onClose()
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                Text(text = subtitle, fontSize = 10.sp, color = Color(0xFFA5B0D1))
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFFFFCA28), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Switch to Parent / Adult Townsquare Mode
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF0F3A4A),
                border = BorderStroke(1.dp, Color(0xFF00D2FF).copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onClose()
                        onSwitchToParentProfile()
                    }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.SupervisorAccount, contentDescription = null, tint = Color(0xFF00D2FF), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = "Switch to Parent Profile", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                        Text(text = "Return to Townsquare Civic Hub", fontSize = 10.sp, color = Color(0xFF00D2FF))
                    }
                }
            }
        }
    }
}

enum class PlaygroundSplashStage {
    ANIMATION,
    SCHOOL_PAPER,
    ENTERED
}

@Composable
fun PlaygroundOpeningFlow(
    onEnterPlayground: () -> Unit,
    modifier: Modifier = Modifier
) {
    var stage by remember { mutableStateOf(PlaygroundSplashStage.ANIMATION) }

    when (stage) {
        PlaygroundSplashStage.ANIMATION -> {
            PlaygroundOpeningAnimation(
                onFinished = { stage = PlaygroundSplashStage.SCHOOL_PAPER }
            )
        }
        PlaygroundSplashStage.SCHOOL_PAPER -> {
            PlaygroundSchoolPaperWelcomeScreen(
                onEnterPlayground = {
                    stage = PlaygroundSplashStage.ENTERED
                    onEnterPlayground()
                }
            )
        }
        PlaygroundSplashStage.ENTERED -> {
            LaunchedEffect(Unit) {
                onEnterPlayground()
            }
        }
    }
}
