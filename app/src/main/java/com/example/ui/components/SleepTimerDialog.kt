package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

object SleepTimerController {
    var remainingSeconds by mutableIntStateOf(0)
    var isTimerActive by mutableStateOf(false)
    var timerPresetLabel by mutableStateOf("Off")

    fun startTimer(minutes: Int, label: String) {
        remainingSeconds = minutes * 60
        isTimerActive = minutes > 0
        timerPresetLabel = label
    }

    fun cancelTimer() {
        remainingSeconds = 0
        isTimerActive = false
        timerPresetLabel = "Off"
    }
}

@Composable
fun SleepTimerHost(
    onPauseAudio: () -> Unit
) {
    LaunchedEffect(SleepTimerController.isTimerActive, SleepTimerController.remainingSeconds) {
        while (SleepTimerController.isTimerActive && SleepTimerController.remainingSeconds > 0) {
            delay(1000)
            SleepTimerController.remainingSeconds -= 1
        }
        if (SleepTimerController.isTimerActive && SleepTimerController.remainingSeconds <= 0) {
            SleepTimerController.isTimerActive = false
            SleepTimerController.timerPresetLabel = "Off"
            onPauseAudio()
        }
    }
}

@Composable
fun SleepTimerDialog(
    onDismiss: () -> Unit,
    onSetTimer: (minutes: Int, label: String) -> Unit
) {
    val presets = listOf(
        Pair(5, "5 minutes"),
        Pair(15, "15 minutes"),
        Pair(30, "30 minutes"),
        Pair(45, "45 minutes"),
        Pair(60, "1 hour"),
        Pair(90, "1.5 hours")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🌙", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Media Sleep Timer", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    Text("Smoothly stop radio and audio playback", fontSize = 11.sp, color = NeonCyan)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (SleepTimerController.isTimerActive) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1E2E3E),
                        border = BorderStroke(1.dp, NeonCyan),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("ACTIVE SLEEP TIMER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                                val mins = SleepTimerController.remainingSeconds / 60
                                val secs = SleepTimerController.remainingSeconds % 60
                                Text(
                                    text = String.format("%02d:%02d remaining", mins, secs),
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                            }
                            Button(
                                onClick = {
                                    SleepTimerController.cancelTimer()
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CoralRed, contentColor = Color.White),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("Cancel", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text("Choose when to pause audio:", fontSize = 12.sp, color = DarkTextSecondary)

                presets.forEach { (mins, label) ->
                    val isCurrent = SleepTimerController.isTimerActive && SleepTimerController.timerPresetLabel == label
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isCurrent) NeonCyan.copy(alpha = 0.2f) else Color(0xFF1E293B),
                        border = BorderStroke(1.dp, if (isCurrent) NeonCyan else Color.Transparent),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSetTimer(mins, label)
                                onDismiss()
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(label, color = if (isCurrent) NeonCyan else Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            if (isCurrent) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color.LightGray)
            }
        },
        containerColor = DarkSurfaceElevated,
        modifier = Modifier.testTag("sleep_timer_dialog")
    )
}
