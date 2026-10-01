package com.example.ui.plus.extensions

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// Classic Symbian S60 Blue-Grey Palette
private val SymbianNavy = Color(0xFF1B2A4A)
private val SymbianDeepBlue = Color(0xFF0F1B33)
private val SymbianCardBg = Color(0xFF22365D)
private val SymbianHighlight = Color(0xFF00D2FF)
private val SymbianGold = Color(0xFFFFD54F)
private val SymbianTextWhite = Color(0xFFF0F4F8)
private val SymbianTextMuted = Color(0xFF94A3B8)

data class S60AppIcon(
    val title: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun SymbianOSSkin(
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))) }
    var activeViewMode by remember { mutableIntStateOf(0) } // 0: Active Standby, 1: 3x4 App Grid
    var isAudioPlaying by remember { mutableStateOf(true) }

    val s60Apps = listOf(
        S60AppIcon("Dispatches", Icons.Default.Newspaper, Color(0xFF00D2FF)),
        S60AppIcon("Radio FM", Icons.Default.Radio, Color(0xFFFF9F1C)),
        S60AppIcon("Messages", Icons.Default.Mail, Color(0xFF30D158)),
        S60AppIcon("Gallery", Icons.Default.PhotoLibrary, Color(0xFF9D4EDD)),
        S60AppIcon("Maps & GPS", Icons.Default.Map, Color(0xFF00E5FF)),
        S60AppIcon("Marketplace", Icons.Default.ShoppingCart, Color(0xFFFF5252)),
        S60AppIcon("Organiser", Icons.Default.CalendarToday, Color(0xFFFFB300)),
        S60AppIcon("Clock/Alarm", Icons.Default.AccessTime, Color(0xFF64B5F6)),
        S60AppIcon("Recordings", Icons.Default.Mic, Color(0xFFFF4081)),
        S60AppIcon("Settings", Icons.Default.Settings, Color(0xFF9E9E9E)),
        S60AppIcon("Civic Pass", Icons.Default.Badge, Color(0xFF2EC4B6)),
        S60AppIcon("Web Carrier", Icons.Default.Public, Color(0xFF4FC3F7))
    )

    LaunchedEffect(Unit) {
        while (isActive) {
            delay(1000L)
            currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        SymbianNavy,
                        SymbianDeepBlue,
                        Color(0xFF070C18)
                    )
                )
            )
            .testTag("symbian_os_skin")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // --- 1. S60 OPERATOR & STATUS BAR ---
            Surface(
                color = Color(0xFF13203A),
                border = BorderStroke(1.dp, Color(0xFF263A63)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: 7 Signal Bars + 3G Icon
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.Bottom) {
                            listOf(4, 6, 8, 10, 12, 14, 16).forEach { h ->
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(h.dp)
                                        .background(SymbianHighlight)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "3G", fontSize = 10.sp, fontWeight = FontWeight.Black, color = SymbianHighlight)
                    }

                    // Center: Operator & Profile
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "TOWNSQUARE 3G",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = Color.White
                        )
                        Text(
                            text = "General • Wednesday",
                            fontSize = 9.sp,
                            color = SymbianTextMuted
                        )
                    }

                    // Right: Clock + Battery 7 Bars
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = currentTime,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.Bottom) {
                            listOf(16, 14, 12, 10, 8, 6, 4).reversed().forEach { h ->
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(h.dp)
                                        .background(Color(0xFF30D158))
                                )
                            }
                        }
                    }
                }
            }

            // --- 2. S60 ACTIVE STANDBY / TAB SELECTOR ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F1A30))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (activeViewMode == 0) SymbianHighlight else Color.Transparent,
                    modifier = Modifier.clickable { activeViewMode = 0 }
                ) {
                    Text(
                        text = "📱 Active Standby",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (activeViewMode == 0) Color(0xFF002733) else SymbianTextMuted,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (activeViewMode == 1) SymbianHighlight else Color.Transparent,
                    modifier = Modifier.clickable { activeViewMode = 1 }
                ) {
                    Text(
                        text = "☵ 3x4 App Grid",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (activeViewMode == 1) Color(0xFF002733) else SymbianTextMuted,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            // --- 3. MAIN S60 CONTENT AREA ---
            Box(modifier = Modifier.weight(1f).padding(8.dp)) {
                if (activeViewMode == 0) {
                    // MODE 0: S60 ACTIVE STANDBY
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Radio / Music Player Standby Widget
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SymbianCardBg,
                            border = BorderStroke(1.dp, Color(0xFF3B568C)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = SymbianHighlight.copy(alpha = 0.2f),
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = SymbianHighlight, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(text = "Music Player: Sonic FM 98.4", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                                        Text(text = "Now Playing: 'Autumn Mist Beats'", fontSize = 10.sp, color = SymbianTextMuted)
                                    }
                                }
                                TextButton(onClick = { isAudioPlaying = !isAudioPlaying }) {
                                    Text(if (isAudioPlaying) "PAUSE" else "PLAY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SymbianHighlight)
                                }
                            }
                        }

                        // Calendar Entries Widget
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SymbianCardBg,
                            border = BorderStroke(1.dp, Color(0xFF3B568C)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = SymbianGold, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Today's Agenda & Civic Duties", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SymbianGold)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = "• 10:00 AM: Pier 14 Promenade Opening live wire dispatch", fontSize = 11.sp, color = Color.White)
                                Text(text = "• 02:30 PM: Radio transmitter relay 7-B telemetry check", fontSize = 11.sp, color = SymbianTextMuted)
                            }
                        }

                        // Dispatches / Inbox Summary Widget
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SymbianCardBg,
                            border = BorderStroke(1.dp, Color(0xFF3B568C)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF30D158), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "3 Unread Broadcast Dispatches", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF30D158))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Mayoralty Press: Embargoed Waterfront grand release...", fontSize = 11.sp, color = Color.White)
                                Text(text = "Syndicate Market: 40 independent sellers registered...", fontSize = 10.sp, color = SymbianTextMuted)
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Directional Keypad Hint
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF0F1A30),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "▲ ▼ S60 Scroll Keys Active • Press Select to Open", fontSize = 10.sp, color = SymbianTextMuted)
                            }
                        }
                    }
                } else {
                    // MODE 1: 3x4 NOKIA APPLICATION GRID
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(s60Apps) { app ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SymbianCardBg,
                                border = BorderStroke(1.dp, Color(0xFF3B568C)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1.1f)
                                    .clickable { /* S60 App trigger */ }
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize().padding(6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = app.color.copy(alpha = 0.2f),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(imageVector = app.icon, contentDescription = null, tint = app.color, modifier = Modifier.size(20.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = app.title,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- 4. S60 CLASSIC BOTTOM SOFTKEYS ---
            Surface(
                color = Color(0xFF13203A),
                border = BorderStroke(1.dp, Color(0xFF263A63)),
                modifier = Modifier.fillMaxWidth().height(42.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Options",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = SymbianHighlight,
                        modifier = Modifier.clickable { activeViewMode = if (activeViewMode == 0) 1 else 0 }
                    )

                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF263A63),
                        modifier = Modifier.size(16.dp)
                    ) {}

                    Text(
                        text = "Exit",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = SymbianHighlight,
                        modifier = Modifier.clickable { activeViewMode = 0 }
                    )
                }
            }
        }
    }
}
