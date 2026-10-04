package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*
import kotlinx.coroutines.delay

enum class CallInStatus {
    FORM, QUEUED, ON_AIR, DISCONNECTED
}

@Composable
fun LiveBroadcastCallInDialog(
    broadcastTitle: String,
    channelOrStation: String,
    onDismiss: () -> Unit
) {
    var callStatus by remember { mutableStateOf(CallInStatus.FORM) }
    var callerName by remember { mutableStateOf("Citizen Alex") }
    var topicSummary by remember { mutableStateOf("Feedback on the Canal Tramway restoration") }
    var queuePosition by remember { mutableIntStateOf(2) }
    var isMicMuted by remember { mutableStateOf(false) }
    var onAirDurationSec by remember { mutableIntStateOf(0) }
    var applauseCount by remember { mutableIntStateOf(42) }
    var hostSpeechText by remember {
        mutableStateOf("Host Dave: \"Welcome to the studio! We're glad to have you on air. Tell us what you think!\"")
    }

    // Queue progression simulation
    LaunchedEffect(callStatus) {
        if (callStatus == CallInStatus.QUEUED) {
            delay(3000)
            queuePosition = 1
            delay(3000)
            callStatus = CallInStatus.ON_AIR
        } else if (callStatus == CallInStatus.ON_AIR) {
            while (callStatus == CallInStatus.ON_AIR) {
                delay(1000)
                onAirDurationSec += 1
                if (onAirDurationSec == 6) {
                    hostSpeechText = "Host Dave: \"That's a fantastic observation! Many listeners in the chat agree with you completely.\""
                }
            }
        }
    }

    Dialog(
        onDismissRequest = {
            if (callStatus == CallInStatus.ON_AIR) {
                callStatus = CallInStatus.DISCONNECTED
            } else {
                onDismiss()
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .testTag("live_call_in_dialog"),
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFF0F1722),
            border = BorderStroke(2.dp, if (callStatus == CallInStatus.ON_AIR) CoralRed else NeonCyan)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (callStatus == CallInStatus.ON_AIR) CoralRed else Color(0xFF1E2E40)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = when (callStatus) {
                                    CallInStatus.ON_AIR -> "🔴 ON AIR • LIVE TRANSMISSION"
                                    CallInStatus.QUEUED -> "⏳ SCREENING QUEUE"
                                    CallInStatus.DISCONNECTED -> "⏹️ CALL COMPLETED"
                                    else -> "📞 STUDIO CALL-IN LINE"
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(broadcastTitle, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White, textAlign = TextAlign.Center)
                Text(channelOrStation, fontSize = 11.sp, color = NeonCyan)

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = DarkBorder)
                Spacer(modifier = Modifier.height(14.dp))

                when (callStatus) {
                    CallInStatus.FORM -> {
                        // Caller Request Form
                        Text("Connect your microphone to speak with the broadcast host.", fontSize = 12.sp, color = DarkTextSecondary)
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = callerName,
                            onValueChange = { callerName = it },
                            label = { Text("Your On-Air Caller Name / Handle") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = topicSummary,
                            onValueChange = { topicSummary = it },
                            label = { Text("What topic or question would you like to discuss?") },
                            placeholder = { Text("e.g. My thoughts on the transit timetable...") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { callStatus = CallInStatus.QUEUED },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(44.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Request to Join Live On-Air Queue", fontWeight = FontWeight.Bold)
                        }
                    }

                    CallInStatus.QUEUED -> {
                        // Screening Queue
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = NeonCyan, modifier = Modifier.size(44.dp))
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Position #$queuePosition in Producer Queue",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = WarmAmber
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Producer is screening audio levels. Please stay on the line; you will be patched into the studio automatically!",
                                fontSize = 11.sp,
                                color = Color.LightGray,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedButton(
                                onClick = { callStatus = CallInStatus.FORM },
                                border = BorderStroke(1.dp, CoralRed),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Leave Call Queue", color = CoralRed, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    CallInStatus.ON_AIR -> {
                        // On Air Audio Waveform & Host Chat
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = String.format("ON AIR: %02d:%02d", onAirDurationSec / 60, onAirDurationSec % 60),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = CoralRed
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Animated Microphone Waveform Canvas
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(55.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val barCount = 28
                                    val barWidth = size.width / (barCount * 1.5f)
                                    val centerY = size.height / 2f
                                    for (i in 0 until barCount) {
                                        val waveFactor = if (isMicMuted) 0.1f else (0.3f + 0.7f * ((i * 7 + onAirDurationSec * 15) % 10) / 10f)
                                        val barHeight = size.height * 0.75f * waveFactor
                                        val x = i * (barWidth * 1.5f) + barWidth
                                        drawLine(
                                            color = if (isMicMuted) Color.Gray else CoralRed,
                                            start = Offset(x, centerY - barHeight / 2),
                                            end = Offset(x, centerY + barHeight / 2),
                                            strokeWidth = barWidth
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Host Speech Bubble
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF1B283A),
                                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Top) {
                                    Text("🎙️", fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(hostSpeechText, fontSize = 11.sp, color = Color.White, lineHeight = 15.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Mic Mute & Clapping Reactions
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { isMicMuted = !isMicMuted },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isMicMuted) CoralRed else Color(0xFF1E293B),
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (isMicMuted) "Mic Muted" else "Mute Mic", fontSize = 11.sp)
                                }

                                Button(
                                    onClick = { applauseCount += 1 },
                                    colors = ButtonDefaults.buttonColors(containerColor = WarmAmber.copy(alpha = 0.2f), contentColor = WarmAmber),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("👏 $applauseCount Claps", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { callStatus = CallInStatus.DISCONNECTED },
                                colors = ButtonDefaults.buttonColors(containerColor = CoralRed, contentColor = Color.White),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().height(40.dp)
                            ) {
                                Icon(Icons.Default.CallEnd, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("End Call / Hang Up", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    CallInStatus.DISCONNECTED -> {
                        // Thank you screen
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🎉", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Call Completed!", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                            Text(
                                text = "Thank you for participating on air with the Townsquare broadcasting community.",
                                fontSize = 11.sp,
                                color = DarkTextSecondary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = onDismiss,
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Back to Broadcast", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
