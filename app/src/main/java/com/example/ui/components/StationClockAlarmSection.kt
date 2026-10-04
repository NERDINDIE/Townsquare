package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class StationAlarm(
    val id: String,
    val timeText: String,
    val label: String,
    val daysText: String,
    val isEnabled: Boolean,
    val soundTone: String = "Westminster Station Chimes",
    val snoozeMinutes: Int = 10
)

@Composable
fun StationClockAlarmSection(
    modifier: Modifier = Modifier
) {
    var alarms by remember {
        mutableStateOf(
            listOf(
                StationAlarm("a1", "06:30 AM", "Morning Broadsheet Dispatch Press Run", "Mon, Tue, Wed, Thu, Fri", true, "Westminster Station Chimes", 10),
                StationAlarm("a2", "08:15 AM", "Line 3 Metro Express Departure", "Mon, Tue, Wed, Thu, Fri, Sat", true, "Vintage Steam Whistle", 5),
                StationAlarm("a3", "12:00 PM", "Civic Center Midday Bell", "Everyday", true, "Civic Council Carillon", 15),
                StationAlarm("a4", "18:00 PM", "Evening Television Broadcast Hour", "Mon, Tue, Wed, Thu, Fri", false, "Broadsheet Brass Horn", 10)
            )
        )
    }

    var isAddDialogOpen by remember { mutableStateOf(false) }
    var ringingAlarm by remember { mutableStateOf<StationAlarm?>(null) }
    var newTimeText by remember { mutableStateOf("07:00 AM") }
    var newLabel by remember { mutableStateOf("") }
    var newSoundTone by remember { mutableStateOf("Westminster Station Chimes") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(14.dp)
            .testTag("station_clock_alarm_section"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Next Alarm Banner
        val nextActiveAlarm = alarms.firstOrNull { it.isEnabled }
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(WarmAmber.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Alarm, contentDescription = null, tint = WarmAmber, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (nextActiveAlarm != null) "Next: ${nextActiveAlarm.timeText}" else "No Active Alarms",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = if (nextActiveAlarm != null) nextActiveAlarm.label else "Set a chime for transit or broadcast editions",
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkTextSecondary
                        )
                    }
                }

                if (nextActiveAlarm != null) {
                    FilledTonalButton(
                        onClick = { ringingAlarm = nextActiveAlarm },
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = WarmAmber.copy(alpha = 0.2f), contentColor = WarmAmber),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Test Ring", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Action Row: Add New Alarm
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Configured Station Alarms (${alarms.size})",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Button(
                onClick = { isAddDialogOpen = true },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544)),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.AddAlarm, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Alarm", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Alarms List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(alarms, key = { it.id }) { alarm ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, if (alarm.isEnabled) NeonCyan.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = alarm.timeText,
                                    fontSize = 24.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    color = if (alarm.isEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = alarm.soundTone,
                                        fontSize = 9.sp,
                                        color = NeonCyan,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = alarm.label,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = if (alarm.isEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "Repeats: ${alarm.daysText} • Snooze: ${alarm.snoozeMinutes}m",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = alarm.isEnabled,
                            onCheckedChange = { isChecked ->
                                alarms = alarms.map { if (it.id == alarm.id) it.copy(isEnabled = isChecked) else it }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF003544),
                                checkedTrackColor = NeonCyan
                            )
                        )
                    }
                }
            }
        }

        // Active Ringing Alarm Dialog
        if (ringingAlarm != null) {
            val ring = ringingAlarm!!
            AlertDialog(
                onDismissRequest = { ringingAlarm = null },
                containerColor = Color(0xFF1E1B4B),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = WarmAmber, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("⏰ Station Alarm Ringing!", color = Color.White, fontWeight = FontWeight.Black)
                    }
                },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(ring.timeText, fontSize = 38.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = NeonCyan)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(ring.label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Tone: ${ring.soundTone}", fontSize = 11.sp, color = DarkTextSecondary)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { ringingAlarm = null },
                        colors = ButtonDefaults.buttonColors(containerColor = CoralRed)
                    ) {
                        Text("Dismiss Alarm", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { ringingAlarm = null }
                    ) {
                        Text("Snooze (${ring.snoozeMinutes}m)", color = NeonCyan)
                    }
                }
            )
        }

        // Add Alarm Dialog
        if (isAddDialogOpen) {
            AlertDialog(
                onDismissRequest = { isAddDialogOpen = false },
                title = { Text("Set New Station Alarm", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = newTimeText,
                            onValueChange = { newTimeText = it },
                            label = { Text("Alarm Time (e.g. 07:45 AM)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newLabel,
                            onValueChange = { newLabel = it },
                            label = { Text("Alarm Label / Event") },
                            placeholder = { Text("e.g. Waterfront Tram Departure") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text("Sound Chime Tone:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        listOf("Westminster Station Chimes", "Vintage Steam Whistle", "Civic Council Carillon", "Digital Synth Chime").forEach { tone ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { newSoundTone = tone }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = newSoundTone == tone,
                                    onClick = { newSoundTone = tone }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(tone, fontSize = 12.sp)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val newAlarm = StationAlarm(
                                id = "alarm_${System.currentTimeMillis()}",
                                timeText = newTimeText.ifBlank { "07:00 AM" },
                                label = newLabel.ifBlank { "Custom Station Alarm" },
                                daysText = "Mon-Sun",
                                isEnabled = true,
                                soundTone = newSoundTone
                            )
                            alarms = listOf(newAlarm) + alarms
                            isAddDialogOpen = false
                            newLabel = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF003544))
                    ) {
                        Text("Save Alarm", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isAddDialogOpen = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
