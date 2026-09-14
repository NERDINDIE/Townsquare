package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.NeonCyan
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AIVoiceBuilderDialog(
    isOpen: Boolean,
    onClose: () -> Unit
) {
    if (!isOpen) return

    var recordingState by remember { mutableIntStateOf(0) } // 0: Idle, 1: Recording, 2: Training, 3: Complete
    var progress by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AI Voice Builder",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onClose) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Train a custom neural voice model by reading the prompt below. The model will be used for narrating articles.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Prompt Box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "\"In the heart of the digital plaza, the airwaves crackle with civic energy. Information flows like water through the concrete veins of the city, bringing news, stories, and connections to every screen and speaker.\"",
                        style = MaterialTheme.typography.bodyMedium.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Progress / Status
                when (recordingState) {
                    0 -> Text("Ready to record", color = NeonCyan)
                    1 -> Text("Recording... Please read the text aloud.", color = Color(0xFFFF3B30))
                    2 -> {
                        Text("Training Neural Voice Model...", color = NeonCyan)
                        Spacer(modifier = Modifier.height(16.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth(),
                            color = NeonCyan,
                            trackColor = DarkSurfaceElevated
                        )
                    }
                    3 -> Text("Training Complete! Your voice model is ready.", color = Color(0xFF30D158))
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Action Button
                if (recordingState < 2) {
                    Button(
                        onClick = {
                            if (recordingState == 0) {
                                recordingState = 1 // Start recording
                            } else {
                                recordingState = 2 // Stop recording, start training
                                scope.launch {
                                    while (progress < 1f) {
                                        delay(50)
                                        progress += 0.02f
                                    }
                                    recordingState = 3 // Complete
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (recordingState == 0) NeonCyan else Color(0xFFFF3B30),
                            contentColor = if (recordingState == 0) Color(0xFF003544) else Color.White
                        ),
                        shape = CircleShape,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            imageVector = if (recordingState == 0) Icons.Default.Mic else Icons.Default.Stop,
                            contentDescription = if (recordingState == 0) "Record" else "Stop",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                } else if (recordingState == 3) {
                    Button(
                        onClick = onClose,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544))
                    ) {
                        Text("Finish", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
