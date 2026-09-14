package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.TeletextAirwaveRepository
import com.example.data.model.TeletextLine
import com.example.data.model.TeletextPage
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TeletextDialog(
    isOpen: Boolean,
    isDeviceOffline: Boolean = true,
    onClose: () -> Unit
) {
    if (!isOpen) return

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .testTag("teletext_fullscreen_dialog"),
            color = Color.Black
        ) {
            TeletextScreenContent(
                isDeviceOffline = isDeviceOffline,
                onClose = onClose
            )
        }
    }
}

@Composable
fun TeletextScreenContent(
    isDeviceOffline: Boolean = true,
    onClose: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    var currentPageNumber by remember { mutableIntStateOf(100) }
    var inputPageBuffer by remember { mutableStateOf("") }
    var packetCount by remember { mutableIntStateOf(4892) }
    var showNumpad by remember { mutableStateOf(false) }

    val currentPage: TeletextPage = remember(currentPageNumber) {
        TeletextAirwaveRepository.pages[currentPageNumber]
            ?: TeletextAirwaveRepository.pages[100]!!
    }

    // Packet reception counter animation
    LaunchedEffect(Unit) {
        while (true) {
            delay(1200)
            packetCount += (12..48).random()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "scanline_anim")
    val scanlineY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanline"
    )

    val timeFormatted = remember {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        sdf.format(Date())
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("teletext_screen_container")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            // Top Teletext System Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0000AA))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "P${currentPageNumber}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.Yellow
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TOWNSQUARE CEEFAX",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = timeFormatted,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.Yellow
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(24.dp)
                            .testTag("teletext_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Exit Teletext",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Airwave & Satellite Telemetry Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                color = Color(0xFF111111),
                border = BorderStroke(1.dp, Color(0xFF00FF00))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFF00FF00), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isDeviceOffline) "OFFLINE AIRWAVE RX ACTIVE" else "AIRWAVE VBI RECEIVER",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = Color(0xFF00FF00)
                        )
                    }
                    Text(
                        text = "142.85 MHz • PKTS: $packetCount",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        ),
                        color = Color(0xFF00FFFF)
                    )
                }
            }

            // Automatic Offline Mode Notice
            if (isDeviceOffline) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 2.dp),
                    color = Color(0xFF330000),
                    border = BorderStroke(1.dp, Color(0xFFFF0000))
                ) {
                    Text(
                        text = "⚡ DEVICE OFFLINE: Gathering bulletins direct from satellite & FM subcarrier",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        ),
                        color = Color(0xFFFF5555),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            // Teletext CRT Screen Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.Black)
                    .border(1.dp, Color(0xFF333333))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    currentPage.lines.forEach { line ->
                        TeletextLineView(line)
                    }
                }
            }

            // Fasttext Color Navigation Buttons (Red, Green, Yellow, Cyan)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // RED
                FasttextKeyButton(
                    color = Color(0xFFFF0000),
                    textColor = Color.White,
                    label = "RED ${currentPage.redLabel}",
                    modifier = Modifier
                        .weight(1f)
                        .testTag("fasttext_red"),
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        currentPageNumber = currentPage.fasttextRed
                    }
                )

                // GREEN
                FasttextKeyButton(
                    color = Color(0xFF00FF00),
                    textColor = Color.Black,
                    label = "GRN ${currentPage.greenLabel}",
                    modifier = Modifier
                        .weight(1f)
                        .testTag("fasttext_green"),
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        currentPageNumber = currentPage.fasttextGreen
                    }
                )

                // YELLOW
                FasttextKeyButton(
                    color = Color(0xFFFFFF00),
                    textColor = Color.Black,
                    label = "YEL ${currentPage.yellowLabel}",
                    modifier = Modifier
                        .weight(1f)
                        .testTag("fasttext_yellow"),
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        currentPageNumber = currentPage.fasttextYellow
                    }
                )

                // CYAN
                FasttextKeyButton(
                    color = Color(0xFF00FFFF),
                    textColor = Color.Black,
                    label = "CYN ${currentPage.cyanLabel}",
                    modifier = Modifier
                        .weight(1f)
                        .testTag("fasttext_cyan"),
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        currentPageNumber = currentPage.fasttextCyan
                    }
                )
            }

            // Bottom Dialing and Navigation Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF141414))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Prev / Next Page Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            val allPages = TeletextAirwaveRepository.pages.keys.sorted()
                            val idx = allPages.indexOf(currentPageNumber)
                            currentPageNumber = if (idx > 0) allPages[idx - 1] else allPages.last()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF222222)),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("teletext_prev_page")
                    ) {
                        Text("◄ P-", color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            val allPages = TeletextAirwaveRepository.pages.keys.sorted()
                            val idx = allPages.indexOf(currentPageNumber)
                            currentPageNumber = if (idx >= 0 && idx < allPages.size - 1) allPages[idx + 1] else allPages.first()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF222222)),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("teletext_next_page")
                    ) {
                        Text("P+ ►", color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            currentPageNumber = 100
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF222222)),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("teletext_index_button")
                    ) {
                        Text("INDEX P100", color = Color.Yellow, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                    }
                }

                // Numpad Toggle Button
                Button(
                    onClick = { showNumpad = !showNumpad },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (showNumpad) Color(0xFF00FF00) else Color(0xFF333333)
                    ),
                    shape = RoundedCornerShape(4.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("teletext_numpad_toggle")
                ) {
                    Text(
                        text = if (showNumpad) "CLOSE PAD" else "DIAL PAGE [${if (inputPageBuffer.isEmpty()) "___" else inputPageBuffer}]",
                        color = if (showNumpad) Color.Black else Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Numeric Keypad Drawer
            AnimatedVisibility(visible = showNumpad) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1A1A1A))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val rows = listOf(
                        listOf("1", "2", "3"),
                        listOf("4", "5", "6"),
                        listOf("7", "8", "9"),
                        listOf("CLR", "0", "GO")
                    )

                    rows.forEach { rowKeys ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            rowKeys.forEach { key ->
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                        .clickable {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            when (key) {
                                                "CLR" -> inputPageBuffer = ""
                                                "GO" -> {
                                                    val num = inputPageBuffer.toIntOrNull()
                                                    if (num != null && TeletextAirwaveRepository.pages.containsKey(num)) {
                                                        currentPageNumber = num
                                                        inputPageBuffer = ""
                                                        showNumpad = false
                                                    } else {
                                                        inputPageBuffer = "ERR"
                                                    }
                                                }
                                                else -> {
                                                    if (inputPageBuffer.length < 3) {
                                                        inputPageBuffer += key
                                                        if (inputPageBuffer.length == 3) {
                                                            val num = inputPageBuffer.toIntOrNull()
                                                            if (num != null && TeletextAirwaveRepository.pages.containsKey(num)) {
                                                                currentPageNumber = num
                                                                inputPageBuffer = ""
                                                                showNumpad = false
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        },
                                    color = when (key) {
                                        "GO" -> Color(0xFF00AA00)
                                        "CLR" -> Color(0xFFAA0000)
                                        else -> Color(0xFF2C2C2C)
                                    },
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = key,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TeletextLineView(line: TeletextLine) {
    Text(
        text = line.text,
        style = MaterialTheme.typography.bodyMedium.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = if (line.isHeader || line.isDoubleHeight) FontWeight.ExtraBold else FontWeight.Bold,
            fontSize = if (line.isDoubleHeight) 17.sp else 13.sp,
            letterSpacing = 0.5.sp,
            lineHeight = if (line.isDoubleHeight) 22.sp else 18.sp
        ),
        color = Color(line.textColor)
    )
}

@Composable
private fun FasttextKeyButton(
    color: Color,
    textColor: Color,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(34.dp)
            .clickable { onClick() },
        color = color,
        shape = RoundedCornerShape(4.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 10.sp
                ),
                color = textColor
            )
        }
    }
}
