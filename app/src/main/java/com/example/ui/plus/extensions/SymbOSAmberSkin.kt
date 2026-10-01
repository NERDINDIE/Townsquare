package com.example.ui.plus.extensions

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// Amber Phosphor Palette
private val AmberBright = Color(0xFFFFB000)
private val AmberMedium = Color(0xFFFF9100)
private val AmberDark = Color(0xFF7A4500)
private val AmberSurface = Color(0xFF1E1300)
private val AmberBg = Color(0xFF0C0700)

data class SymbOSWindow(
    val id: String,
    val title: String,
    val icon: ImageVector,
    var isMinimized: Boolean = false,
    var isMaximized: Boolean = false
)

@Composable
fun SymbOSAmberSkin(
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))) }
    var activeWindowId by remember { mutableStateOf("news_wire") }
    var isStartMenuOpen by remember { mutableStateOf(false) }

    // Simulated News Wire Posts in SymbOS
    val amberDispatches = listOf(
        Pair("DISP_101", "Pier 14 Solar Kiosk network operational on 1200 baud packet radio."),
        Pair("DISP_102", "Harbor Barometer reports incoming squall; floodgates secured."),
        Pair("DISP_103", "Old Quarter Autumn Arts & Vinyl Exchange catalogue indexed.")
    )

    // SymbOS Audio Station State
    var isAudioPlaying by remember { mutableStateOf(true) }
    var currentStation by remember { mutableStateOf("91.3 FM Retro CRT Waves") }

    LaunchedEffect(Unit) {
        while (isActive) {
            delay(1000L)
            currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AmberBg)
            .testTag("symbos_amber_skin")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // --- 1. TOP SYMBOS TITLEBAR / DESKTOP HEADER ---
            Surface(
                color = AmberSurface,
                border = BorderStroke(1.dp, AmberMedium),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(2.dp),
                            color = AmberMedium,
                            modifier = Modifier.size(16.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "S", fontSize = 10.sp, fontWeight = FontWeight.Black, color = AmberBg)
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SymbOS v3.1 [AMBER CRT EDITION]",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberBright
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Z80: 4.0MHz • RAM: 512KB",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = AmberMedium
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = currentTime,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberBright
                        )
                    }
                }
            }

            // --- 2. DESKTOP WORKSPACE WITH RETRO WINDOWS ---
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Desktop Quick Icon Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SymbOSDesktopIcon(label = "NewsWire", icon = Icons.Default.Newspaper, isSelected = activeWindowId == "news_wire") {
                            activeWindowId = "news_wire"
                        }
                        SymbOSDesktopIcon(label = "RadioPl", icon = Icons.Default.Radio, isSelected = activeWindowId == "audio_player") {
                            activeWindowId = "audio_player"
                        }
                        SymbOSDesktopIcon(label = "FileMgr", icon = Icons.Default.Folder, isSelected = activeWindowId == "file_mgr") {
                            activeWindowId = "file_mgr"
                        }
                        SymbOSDesktopIcon(label = "Control", icon = Icons.Default.Settings, isSelected = activeWindowId == "control") {
                            activeWindowId = "control"
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Window 1: Townsquare News Wire v3.1
                    SymbOSWindowCard(
                        title = "Townsquare NewsWire Terminal (A:DISPATCH.TXT)",
                        isActive = activeWindowId == "news_wire",
                        onFocus = { activeWindowId = "news_wire" },
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            item {
                                Text(
                                    text = "=== TOWNSQUARE CIVIC TELEMETRY NODE #04 ===",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberBright
                                )
                                Text(
                                    text = "Packet broadcast synchronized. Amber monochrome CRT buffer active.",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = AmberMedium
                                )
                                HorizontalDivider(color = AmberDark, modifier = Modifier.padding(vertical = 4.dp))
                            }

                            items(amberDispatches) { (code, text) ->
                                Surface(
                                    shape = RoundedCornerShape(2.dp),
                                    color = AmberSurface,
                                    border = BorderStroke(1.dp, AmberDark),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(6.dp)) {
                                        Text(text = "[$code] SYS_NOTIFY", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AmberMedium)
                                        Text(text = text, fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = AmberBright)
                                    }
                                }
                            }
                        }
                    }

                    // Window 2: SymbOS Sound Master Audio Player
                    SymbOSWindowCard(
                        title = "SymbOS Sound Master 1.2 [FM-CHIP AY-3-8910]",
                        isActive = activeWindowId == "audio_player",
                        onFocus = { activeWindowId = "audio_player" },
                        modifier = Modifier.fillMaxWidth().height(140.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "CURRENT TRACK: $currentStation", fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AmberBright)
                                    Text(text = "STATUS: ${if (isAudioPlaying) "PLAYING (STEREO)" else "PAUSED"}", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = AmberMedium)
                                }
                                Button(
                                    onClick = { isAudioPlaying = !isAudioPlaying },
                                    colors = ButtonDefaults.buttonColors(containerColor = AmberMedium, contentColor = AmberBg),
                                    shape = RoundedCornerShape(2.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text(if (isAudioPlaying) "PAUSE" else "PLAY", fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Simulated Sound Waveform Visualizer in Amber
                            Row(
                                modifier = Modifier.fillMaxWidth().height(26.dp),
                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                                verticalAlignment = Alignment.Bottom
                            ) {
                                val heights = listOf(8, 14, 22, 18, 26, 12, 20, 16, 24, 10, 19, 25, 14, 22, 9, 15, 26, 12, 18, 23)
                                heights.forEach { h ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(if (isAudioPlaying) h.dp else 4.dp)
                                            .background(AmberBright)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- 3. BOTTOM SYMBOS TASKBAR ---
            Surface(
                color = AmberSurface,
                border = BorderStroke(1.dp, AmberMedium),
                modifier = Modifier.fillMaxWidth().height(36.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Start Button
                        Surface(
                            shape = RoundedCornerShape(2.dp),
                            color = if (isStartMenuOpen) AmberBright else AmberMedium,
                            modifier = Modifier
                                .clickable { isStartMenuOpen = !isStartMenuOpen }
                                .padding(vertical = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "START", fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Black, color = AmberBg)
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Active Window Taskbar Button
                        Surface(
                            shape = RoundedCornerShape(2.dp),
                            color = AmberDark,
                            border = BorderStroke(1.dp, AmberMedium)
                        ) {
                            Text(
                                text = " [NewsWire] ",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = AmberBright,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "SYM-SHELL READY",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = AmberMedium
                    )
                }
            }
        }

        // Start Menu Popup
        if (isStartMenuOpen) {
            Surface(
                shape = RoundedCornerShape(2.dp),
                color = AmberSurface,
                border = BorderStroke(2.dp, AmberMedium),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 6.dp, bottom = 40.dp)
                    .width(180.dp)
            ) {
                Column(modifier = Modifier.padding(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "SYMBOS MENU", fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AmberMedium)
                    HorizontalDivider(color = AmberDark)
                    SymbOSMenuItem("1. NewsWire") { activeWindowId = "news_wire"; isStartMenuOpen = false }
                    SymbOSMenuItem("2. Sound Master") { activeWindowId = "audio_player"; isStartMenuOpen = false }
                    SymbOSMenuItem("3. Civic Kiosks") { isStartMenuOpen = false }
                    SymbOSMenuItem("4. System Shell") { isStartMenuOpen = false }
                }
            }
        }
    }
}

@Composable
private fun SymbOSDesktopIcon(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = if (isSelected) AmberDark else AmberSurface,
            border = BorderStroke(1.dp, if (isSelected) AmberBright else AmberDark),
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = AmberBright, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AmberBright)
    }
}

@Composable
private fun SymbOSWindowCard(
    title: String,
    isActive: Boolean,
    onFocus: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(2.dp),
        color = AmberBg,
        border = BorderStroke(1.dp, if (isActive) AmberBright else AmberDark),
        modifier = modifier.clickable { onFocus() }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Window Titlebar
            Surface(
                color = if (isActive) AmberMedium else AmberDark,
                modifier = Modifier.fillMaxWidth().height(22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberBg,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "[_]", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AmberBg)
                        Text(text = "[X]", fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AmberBg)
                    }
                }
            }

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                content()
            }
        }
    }
}

@Composable
private fun SymbOSMenuItem(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Text(
            text = text,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = AmberBright,
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 6.dp)
        )
    }
}
